/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.Serializable;
import java.io.Writer;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.RegisterTable.AlgFilter;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.NullAlg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.Connector;
import net.hudup.core.client.HudupRMIClassLoader;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Service;
import net.hudup.core.client.ServiceExt;
import net.hudup.core.client.ServiceLocal;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPairExchanged;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.DatasetPoolExchangedItem;
import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.data.DatasetRemoteWrapper;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Simplify;
import net.hudup.core.evaluate.EvaluateEvent.Type;
import net.hudup.core.evaluate.ui.AbstractEvaluateGUI;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.CounterTaskQueue;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.EventListenerList2;
import net.hudup.core.logistic.EventListenerList2.ListenerInfo;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.TaskQueue;
import net.hudup.core.logistic.Timer2;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.LoginDlg;

/**
 * {@code Evaluator} is one of main objects of Hudup framework, which is responsible for executing and evaluation algorithms according to built-in and user-defined metrics.
 * Such metrics implement by {@code Metric} interface. As an evaluator of any recommendation algorithm, {@code Evaluator} is the bridge between {@code Dataset} and {@code Recommender} and it has six roles:
 * <ol>
 * <li>
 * It is a loader which loads and configures {@code Dataset}.
 * </li>
 * <li>
 * It is an executor which calls methods {@code Recommender#estimate(...)} and {@code Recommender#recommend(...)}.
 * </li>
 * <li>
 * It is an analyzer which analyzes and translates the result of algorithm execution into the form of evaluation metrics. The execution result is output of method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)}.
 * Evaluation metric is represented by {@code Metric} interface. {@code Metrics} class manages a list of {@code Metric} (s).
 * </li>
 * <li>
 * It is a registry. If external applications require receiving result from {@code Evaluator}, they need to register with it.
 * Such applications must implement {@code EvaluatorListener} interface. In other words, {@code Evaluator} contains a list of {@code EvaluatorListener} (s).
 * </li>
 * <li>
 * Whenever it finishes a call of method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)}, it issues a so-called evaluation event and send back evaluation metrics to external applications after executing algorithm.
 * So it is also a provider. The evaluation event is wrapped by {@code EvaluatorEvent} class.
 * </li>
 * <li>
 * It works as a service which allows scientists to start, pause, resume, and stop the evaluation process via its methods {@code start()}, {@code pause()}, {@code resume()}, and {@code stop()}, respectively.
 * </li>
 * </ol>
 * {@code Evaluator} has four most important methods:
 * <ol>
 * <li>
 * Method {@code evaluate(...)} performs main tasks of {@code Evaluator}, which loads {@code Dataset} and activates method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} on such {@code Dataset}.
 * </li>
 * <li>
 * Method {@code analyze(...)} is responsible for analyzing the result returned by method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} so as to translate such result into evaluation metric.
 * Metrics are used to assess algorithms and they are discussed later. By default implementation, {@code analyze(...)} method will simply call {@code Metric#recalc(...)} method in order to calculate such metric itself.
 * </li>
 * <li>
 * Method {@code issue(...)} issues an evaluation event and sends back evaluation metrics to external applications. Method {@code issue(...)} is also named {@code fireEvaluatorEvent(...)}.
 * </li>
 * </ol>
 * If external applications want to receive metrics, they need to register with {@code Evaluator} by calling {@code Evaluator#addListener(...)} method. The evaluation process has five steps:
 * <ol>
 * <li>
 * {@code Evaluator} calls {@code Evaluator#evaluate(...)} method to load and feed {@code Dataset} to {@code Recommender}.
 * </li>
 * <li>
 * Method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} is executed by {@code Evaluator#evaluate(...)} method to perform recommendation task.
 * </li>
 * <li>
 * Method {@code Evaluator#analyze(...)} analyzes the result returned by method {@code Recommender#estimate(...)} or {@code Recommender#recommend(...)} and translates such result into {@code Metric}.
 * The {@code Metrics} class manages a list of {@code Metric} (s).
 * </li>
 * <li>
 * External applications that implement {@code EvaluatorListener} interface register with {@code Evaluator} by calling {@code Evaluator#addListener(...)} method.
 * </li>
 * <li>
 * Method {@code Evaluator#issue(...)} sends {@code Metrics} to external applications.
 * </li>
 * </ol>
 * 
 * It is associated with a friendly user interface in order to give facilities to users.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class EvaluatorAbstract extends AbstractRunner implements Evaluator {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default main unit.
	 */
	public final static String MAIN_UNIT_DEFAULT = DataConfig.RATING_UNIT;
	
	
	/**
	 * Flag to indicate whether the evaluator is agent. The agent evaluator runs on server.
	 */
	protected boolean isAgent = false;
	
	
	/**
	 * Configuration of this evaluator.
	 */
	protected EvaluatorConfig config = null;

	
	/**
	 * Holding a list of {@link EventListener} (s)
	 * 
	 */
    protected EventListenerList2 listenerList = new EventListenerList2();

    
    /**
     * Exported stub as remote evaluator. It must be serializable.
     */
    protected Evaluator exportedStub = null;

	
    /**
     * List of algorithms that are evaluated by this evaluator.
     */
    protected List<Alg> evAlgList = null;
    
    
    /**
     * Current evaluated algorithm.
     */
	protected Alg evAlg = null;

	
	/**
     * This {@code dataset pool} contains many training and testing datasets, which is fed to evaluator, which allows evaluator assesses algorithm on many testing datasets.
     */
    protected DatasetPool evPool = null;
    
    
	/**
     * The list of original metrics used to evaluate algorithms in {@link #evAlgList}.
     */
	protected NoneWrapperMetricList evMetricList = null;

	
    /**
     * Additional parameter for this evaluator.
     */
    protected Serializable evParameter = null;
    
    
    /**
     * Internal task queue.
     */
    protected CounterTaskQueue evTaskQueue = null;
    
    
    /**
     * Evaluation processor.
     */
    protected EvaluateProcessor evProcessor = null;

    
	/**
	 * Evaluation information.
	 */
	protected EvaluateInfoPersit evInfo = new EvaluateInfoPersit();
	
	
    /**
     * The list of metrics resulted from the evaluation process.
     */
	protected volatile Metrics result = null;
	
	
	/**
	 * Resulted dataset pool.
	 */
	protected DatasetPoolExchanged poolResult = null;

	
	/**
	 * Resulted algorithm register table.
	 */
	protected RegisterTable algRegResult = null;

	
	/**
	 * Information of resulted metrics.
	 */
	protected EvaluateInfo otherResult = new EvaluateInfo();
	
	
	/**
     * List of unsetup algorithms that are evaluated by this evaluator. This list is not important.
     */
    protected List<Alg> delayUnsetupAlgs = Util.newList();

    
    /**
     * Referred service.
     */
    protected Service referredService = null;
    
    
    /**
     * Timer to purging.
     */
    protected Timer2 purgeTimer = null;
    
    
	/**
	 * Default constructor.
	 */
	public EvaluatorAbstract() {
		try {
			String evalconfigPath = Constants.WORKING_DIRECTORY + "/" +
					(EvaluatorConfig.EVALCONFIG_FILENAME_PREFIX + getName()).replaceAll("\\s", "") + "." +
					EvaluatorConfig.EVALCONFIG_FILEEXT;
			config = new EvaluatorConfig(xURI.create(evalconfigPath));
		}
		catch (Exception e) {
			LogUtil.trace(e);
			config = new EvaluatorConfig(xURI.create(EvaluatorConfig.EVALCONFIG_FILEPATH_DEFAULT));
		}
		
		try {
			evMetricList = defaultMetrics();
			evMetricList.syncWithPlugin();
			evMetricList.sort();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		evProcessor = new EvaluateProcessor(this);
		evTaskQueue = new CounterTaskQueue(otherResult);
		evTaskQueue.setListenerList(listenerList); //Task queue uses the same listener list.
		
		//Normal evaluators (non-reproduced evaluators) always own purging listeners timer.
		purgeTimer = createPurgeListenersTimer(listenerList);
	}
	
	
	@Override
	public void stimulate() throws RemoteException {
		if (purgeTimer != null && !purgeTimer.isStarted())
			purgeTimer.start();
	}


	@Override
	public synchronized boolean remoteStart(List<Alg> algList, DatasetPoolExchanged pool, Timestamp timestamp, Serializable parameter) throws RemoteException {
		if (isStarted() || this.evAlgList != null || this.evPool != null) {
			LogUtil.error("Evaluator is running and so evaluation is not run");
			return false;
		}
		
		if (algList == null || algList.size() == 0 || pool == null || pool.dspList.size() == 0) {
			LogUtil.error("Empty algorithm list of empty dataset pool");
			return false;
		}

		//Initialized tasks. This code snippet can be removed.
		try {
			if (config.isDuplicateAlgorithm()) algList = AlgList.clone(algList);
		}
		catch (Throwable e) {}
		
		
		this.evTaskQueue.stop();

		this.evPool = updatePoolResult(pool);
		if (this.evPool == null) {
			LogUtil.error("Cannot create pool");
			return false;
		}
		
		try {
			clearDelayUnsetupAlgs(); //This code line is important.
		} catch (Throwable e) {LogUtil.trace(e);} 
		
		this.evAlgList = algList;
		this.algRegResult = this.evAlgList != null ? new RegisterTable(this.evAlgList) : null;
		
		this.result = null;
		this.evParameter = parameter;
		
		this.otherResult.reset();
		this.otherResult.algNames = Util.newList(this.evAlgList.size());
		for (Alg alg : this.evAlgList) {
			this.otherResult.algNames.add(alg.getName());
		}
		this.otherResult.algName = this.otherResult.algNames.get(0);
		
		if (!start()) {
			clear();
			return false;
		}
		
		this.evTaskQueue.start();
		this.stimulate();

		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.start,
				this.otherResult,
				this.poolResult,
				this.evInfo,
				timestamp),
				null);
		
		return true;
	}

	
	@Override
	public void run() {
		try {
			run0();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Actually, make evaluation process on algorithms with a dataset pool according to original (built-in) metrics.
	 */
	protected void run0() {
		otherResult.progressStep = 0;
		otherResult.progressTotal = 0;
		for (int i = 0; i < evPool.size(); i++) {
			Dataset testing = evPool.get(i).getTesting();
			Fetcher<Profile> fetcher = fetchTesting(testing);
			try {
				otherResult.progressTotal += fetcher.getMetadata().getSize();
				fetcher.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
		otherResult.progressTotal *= evAlgList.size();
		otherResult.startDate = System.currentTimeMillis();
		
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; current == thread && evAlgList != null && i < evAlgList.size(); i++) {
			evAlg = evAlgList.get(i);
			otherResult.algName = evAlg.getName();
			
			for (int j = 0; current == thread && evPool != null && j < evPool.size(); j++) {
				
				Fetcher<Profile> testingFetcher = null;
				try {
					DatasetPair dsPair = evPool.get(j);
					Dataset     training = dsPair.getTraining();
					Dataset     testing = dsPair.getTesting();
					int         datasetId = j + 1;
					xURI        datasetUri = testing.getConfig() != null ? testing.getConfig().getUriId() : null;
					
					otherResult.datasetId = datasetId;
					DatasetUtil.setDatasetId(training, datasetId);
					DatasetUtil.setDatasetId(testing, datasetId);
					
					// Adding default metrics to metric result. Pay attention to cloning metrics list.
					result.add( evAlg.getName(), datasetId, datasetUri, ((NoneWrapperMetricList)evMetricList.clone()).sort().list() );
					
					otherResult.inAlgSetup = true;
					if (evAlg instanceof AlgRemote) {
						((AlgRemote)evAlg).addSetupListener(this);
						SetupAlgEvent setupEvt = new SetupAlgEvent(evAlg, SetupAlgEvent.Type.doing, evAlg.getName(), datasetId, "fired");
						otherResult.statuses = extractSetupInfo(setupEvt);
						fireSetupAlgEvent(setupEvt);
					}
					
					long beginSetupTime = System.currentTimeMillis();
					// 
					setupAlg(evAlg, training);
					//
					long endSetupTime = System.currentTimeMillis();
					long setupElapsed = endSetupTime - beginSetupTime;
					Metrics setupMetrics = result.recalc(
							evAlg, 
							datasetId, 
							SetupTimeMetric.class, 
							new Object[] { setupElapsed / 1000.0f }
						); // calculating setup time metric
					fireEvaluateEvent(new EvaluateEvent(this, Type.doing, setupMetrics)); // firing setup time metric
					
					if (evAlg instanceof AlgRemote) {
						SetupAlgEvent setupEvt = new SetupAlgEvent(evAlg, SetupAlgEvent.Type.done, evAlg.getName(), datasetId, "fired");
						otherResult.statuses = extractSetupInfo(setupEvt);
						fireSetupAlgEvent(setupEvt);
						((AlgRemote)evAlg).removeSetupListener(this);
					}
					otherResult.inAlgSetup = false;
					
					//Auto enhancement after setting up algorithm.
					SystemUtil.enhanceAuto();

					testingFetcher = fetchTesting(testing);
					otherResult.vCurrentTotal = testingFetcher.getMetadata().getSize();
					otherResult.vCurrentCount = 0;
					int vExecutedCount = 0;
					while (current == thread && testingFetcher.next()) {
						otherResult.progressStep++;
						otherResult.vCurrentCount++;
						EvaluateProgressEvent progressEvt = new EvaluateProgressEvent(this, otherResult.progressTotal, otherResult.progressStep);
						progressEvt.setAlgName(evAlg.getName());
						progressEvt.setDatasetId(datasetId);
						progressEvt.setCurrentCount(otherResult.vCurrentCount);
						progressEvt.setCurrentTotal(otherResult.vCurrentTotal);
						progressEvt.setElapsedTime(otherResult.elapsedTime);
						otherResult.statuses = extractEvaluateProgressInfo(progressEvt);
						fireEvaluateProgressEvent(progressEvt);
						
						Profile testingProfile = testingFetcher.pick();
						if (testingProfile == null)
							continue;
						
						Profile param = prepareExecuteAlg(evAlg, testingProfile);
						//
						long beginRecommendTime = System.currentTimeMillis();
						Serializable executedResult = executeAlg(evAlg, param);
						long endRecommendTime = System.currentTimeMillis();
						//
						long recommendElapsed = endRecommendTime - beginRecommendTime;
						Metrics speedMetrics = result.recalc(
								evAlg, 
								datasetId, 
								SpeedMetric.class, 
								new Object[] { recommendElapsed / 1000.0f }
							); // calculating time speed metric
						fireEvaluateEvent(new EvaluateEvent(
								this, 
								Type.doing, 
								speedMetrics)); // firing time speed metric
						
						if (executedResult != null) { // successful recommendation
							Metrics executedMetrics = result.recalc(
									evAlg, 
									datasetId,
									new Object[] { executedResult, extractTestValue(evAlg, testingProfile) }
								); // calculating execution metric
							
							vExecutedCount++;
							
							fireEvaluateEvent(new EvaluateEvent(
									this, 
									Type.doing, 
									executedMetrics, 
									executedResult, 
									extractTestValue(evAlg, testingProfile))); // firing execution metric
						}
						
						
						synchronized (this) {
							while (paused) {
								notifyAll();
								wait();
							}
						}
						
					} // User id iterate
					
					Metrics hudupRecallMetrics = result.recalc(
							evAlg, 
							datasetId, 
							HudupRecallMetric.class, 
							new Object[] { new FractionMetricValue(vExecutedCount, otherResult.vCurrentTotal) }
						);
					fireEvaluateEvent(new EvaluateEvent(this, Type.doing, hudupRecallMetrics));
					
					Metrics doneOneMetrics = result.gets(evAlg.getName(), datasetId);
					fireEvaluateEvent(new EvaluateEvent(this, Type.done_one, doneOneMetrics));
					
				} // end try
				catch (Throwable e) {
					LogUtil.trace(e);
				}
				finally {
					try {
						if (testingFetcher != null)
							testingFetcher.close();
						testingFetcher = null;
					} catch (Throwable e) {LogUtil.trace(e);}
					
					try {
						unsetupAlgSupportDelay(evAlg);
					} catch (Throwable e) {LogUtil.trace(e);}
				}
				
				SystemUtil.enhanceAuto();

			} // dataset iterate
			
		} // algorithm iterate
		
		otherResult.endDate = System.currentTimeMillis();
		
		
		synchronized (this) {
			thread = null;
			paused = false;
			
			fireEvaluateEvent(new EvaluateEvent(this, Type.done, result));
			
			clear();

			notifyAll();
		}
		
	}
	
	
	/**
	 * Setting up specified algorithm based on training dataset and additional parameters.
	 * This method is always called by another method and so it is not synchronozed.
	 * @param alg specified algorithm.
	 * @param training training dataset.
	 */
	protected abstract void setupAlg(Alg alg, Dataset training);
	
	
	/**
	 * Unsetting up specified algorithm.
	 * This method is always called by another method and so it is not synchronized.
	 * @param alg specified algorithm.
	 */
	protected abstract void unsetupAlg(Alg alg);
	
	
	/**
	 * Unsetting up specified algorithm with support delaying.
	 * @param alg specified algorithm.
	 */
	protected void unsetupAlgSupportDelay(Alg alg) {
		DataConfig config = alg.getConfig();
		if (config == null) return;
		
		if (!config.getAsBoolean(DataConfig.DELAY_UNSETUP)) {
			try {
				unsetupAlg(alg);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		else {
			synchronized (delayUnsetupAlgs) {
				delayUnsetupAlgs.add(alg);
			}
		}
	}
	
	
	/**
	 * Clearing delay unsetting up algorithms.
	 */
	private void clearDelayUnsetupAlgs() {
		synchronized (delayUnsetupAlgs) {
			for (Alg alg : delayUnsetupAlgs) {
				try {
					unsetupAlg(alg);
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			
			delayUnsetupAlgs.clear();
		}
	}

	
	/**
	 * Clearing all evaluated results.
	 */
    private synchronized void clearResult() {
//    	if (result != null) result.clear(); //Keep for local recovery.
    	result = null;
    	
    	if (poolResult != null) poolResult.clear(true);
    	poolResult = null;
    	
    	if (algRegResult != null) {
    		algRegResult.unexportNonPluginAlgs();
    		algRegResult.clear();
    	}
    	algRegResult = null;
    	
    	otherResult.reset(); //Wrong when recover evaluation progress.
    }

    
    /**
	 * Fetching profiles from the specified testing dataset.
	 * @param testing specified training dataset.
	 * @return fetcher for retrieving profiles from the specified testing dataset as {@link Fetcher}.
	 */
	protected abstract Fetcher<Profile> fetchTesting(Dataset testing);
	
	
	@Override
	public DatasetPoolExchanged getDatasetPool() throws RemoteException {
		return poolResult;
	}


	/**
	 * Prepare to execute the specified algorithm.
	 * @param alg specified algorithm.
	 * @param testingProfile testing profile as coarse parameter.
	 * @return a returned profile as refined parameter for algorithm execution.
	 */
	protected abstract Profile prepareExecuteAlg(Alg alg, Profile testingProfile);
	
	
	/**
	 * Execute the specified algorithm.
	 * @param alg specified algorithm.
	 * @param param specified profile as parameter for algorithm execution.
	 * @return an object as result of algorithm execution.
	 */
	protected abstract Serializable executeAlg(Alg alg, Profile param);
	
	
	/**
	 * Extracting value from testing profile.
	 * @param alg specified algorithm.
	 * @param testingProfile testing profile.
	 * @return value from testing profile.
	 */
	protected abstract Serializable extractTestValue(Alg alg, Profile testingProfile);
	
	
	/**
	 * As usual, the GUI receives event but this set up event is issued by algorithm.
	 * Hence, this evaluator receives the set up event and then passes it to the GUI.
	 */
	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		fireSetupAlgEvent(evt.transferForRemote());
		otherResult.statuses = extractSetupInfo(evt);
	}
	
	
	@Override
	public String getClassName() throws RemoteException {
		return this.getClass().getName();
	}


	@Override
	public String getMainUnit() throws RemoteException {
		return MAIN_UNIT_DEFAULT;
	}
	
	
	@Override
	public Metrics getResult() throws RemoteException {
		return result;
	}

	
	@Override
	public EvaluateInfo getOtherResult() throws RemoteException {
		return otherResult;
	}


	@Override
	public EvaluateInfoPersit getInfo() throws RemoteException {
		return evInfo;
	}


	@Override
	public List<String> getMetricNameList() throws RemoteException {
		synchronized(evMetricList) {
			return this.evMetricList.nameList();
		}
	}

	
	/*
	 * Current implementation does not export metrics. Exporting normal algorithms only.
	 */
	@Override
	public synchronized void setMetricNameList(List<String> metricNameList) throws RemoteException {
		if (isStarted()) {
			LogUtil.error("Evaluator is started and so it is impossible to set up metric list");
			return;
		}
		
		RegisterTable metricReg = PluginStorage.getMetricReg();
		List<Metric> newMetricList = Util.newList();
		for (String metricName : metricNameList) {
			Metric registeredMetric = (Metric) metricReg.query(metricName);
			if (registeredMetric != null)
				newMetricList.add(registeredMetric); //This code line is important to remote setting.
		}
		
		synchronized(this.evMetricList) {
			this.evMetricList.clear();
			this.evMetricList.addAll(newMetricList);
			this.evMetricList.sort();
		}
	}
	
	
	@Deprecated
	@Override
	public RegisterTable extractNormalAlgFromPluginStorage0() throws RemoteException {
		List<Alg> algList = PluginStorage.getNormalAlgReg().getAlgList(new AlgFilter() {
			
			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean accept(Alg alg) {
				if (alg == null) return false;

				try {
					return acceptAlg(alg);
				} 
				catch (Throwable e) {
					LogUtil.trace(e);
					return false;
				}
			}
		});
		
		return new RegisterTable(algList);
	}
	
	
	/**
	 * Extracting algorithms from plug-in storage so that such algorithms are accepted by the specified evaluator.
	 * @param evaluator specified evaluator.
	 * @param connectInfo connection information.
	 * @return register table to store algorithms extracted from plug-in storage.
	 */
	public static RegisterTable extractNormalAlgFromPluginStorage(Evaluator evaluator, ConnectInfo connectInfo) {
		List<Alg> algList = PluginStorage.getNormalAlgReg().getAlgList(new AlgFilter() {
			
			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean accept(Alg alg) {
				return acceptAlg(evaluator, alg, connectInfo);
			}
			
		});
		
		return new RegisterTable(algList);
	}
	

	@Override
	public boolean acceptAlg(String algClassName) throws RemoteException {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Alg> algClass = (Class<? extends Alg>)Util.getPluginManager().loadClass(algClassName, false);
			return acceptAlg(algClass);
		} catch (Exception e) {LogUtil.trace(e);}
		
		return false;
	}


	@Override
	public boolean acceptAlg(Class<? extends Alg> algClass) throws RemoteException {
		try {
			return acceptAlg(algClass.getDeclaredConstructor().newInstance());
		} catch (Exception e) {LogUtil.trace(e);}
		
		return false;
	}

	
	/**
	 * Checking whether the specified algorithm is accepted by specified evaluator.
	 * @param evaluator specified evaluator.
	 * @param alg specified algorithm
	 * @param connectInfo connection information.
	 * @return whether the specified algorithm is accepted by specified evaluator.
	 */
	public static boolean acceptAlg(Evaluator evaluator, Alg alg, ConnectInfo connectInfo) {
		if (evaluator == null || alg == null) return false;
		
		if (connectInfo == null || connectInfo.bindUri == null) {
			try {
				return evaluator.acceptAlg(alg);
			} catch (Exception e) {LogUtil.trace(e);}
			return false;
		}

		//The evaluator is now remote.
		
		try {
			AlgDesc2 algDesc = evaluator.getEvaluatedAlgDesc(alg.getName());
			if (algDesc != null) return true;
		} catch (Exception e) {	}
		
		if (alg instanceof NullAlg) {
			try {
				String referredAlgClassName = ((NullAlg)alg).getReferredAlgClassName();
				if (referredAlgClassName == null)
					return false;
				else
					return evaluator.acceptAlg(referredAlgClassName);
			}
			catch (Exception e) {
				LogUtil.error("Evaluator does not accept null algorithm '" + alg.getName() + "' due to " + e.getMessage());
			}
		}
		
		//This code line is not redundant because the exclusive is set to false so as to prevent the algorithm from unexporting.
		alg = AlgDesc2.wrapNewInstance(alg, false);
		if (alg == null) return false;
		
		if ((connectInfo.checkPullMode()) || !(alg instanceof AlgRemote)) { //Currently, there is no algorithm that does not implement remote interface.
			try {
				return evaluator.acceptAlg(alg);
			}
			catch (Exception e) {
				LogUtil.error("Evaluator does not accept algorithm '" + alg.getName() + "' due to " + e.getMessage());
			}
			return false;
		}
		
		try {
			//This code line is not redundant because the remote evaluator can call some methods of the algorithm although such calling is invalid in pull mode.
			//As usual, the method evaluator#acceptAlg often checks the type of algorithm and so there is now no such calling.
			((AlgRemote)alg).export(0);
		}
		catch (Exception e) {
			LogUtil.trace(e); return false;
		}
		
		boolean accepted = true;
		try {
			accepted = evaluator.acceptAlg(
				Util.getPluginManager().wrap((AlgRemote)alg, false));
		} 
		catch (Throwable e) {
			LogUtil.error("Evaluator does not accept algorithm '" + alg.getName() + "' due to " + e.getMessage());
			accepted = false;
		}
		
		try {
			((AlgRemote)alg).unexport();
		}
		catch (Exception e) {LogUtil.trace(e);}

		return accepted;
	}
	
	
	@Deprecated
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		return new PluginStorageWrapper();
	}


    @Override
	public List<String> getPluginAlgNames(Class<? extends Alg> algClass) throws RemoteException {
    	RegisterTable algReg = PluginStorage.lookupTable(algClass);
    	if (algReg == null) return Util.newList();
    	
    	String tableName = PluginStorage.lookupTableName(algClass);
    	List<Alg> algList = algReg.getAlgList();
    	if (tableName != null && tableName.equals(PluginStorage.NORMAL_ALG)) {
        	List<String> algNames = Util.newList();
	    	for (Alg alg : algList) {
	    		try {
	    			if (acceptAlg(alg)) algNames.add(alg.getName());
	    		} catch (Throwable e) {LogUtil.trace(e);}
	    	}
	    	
			return algNames;
    	}
    	else
    		return AlgList.getAlgNameList(algList); 
	}


	@Override
	public AlgDesc2List getPluginAlgDescs(Class<? extends Alg> algClass) throws RemoteException {
    	RegisterTable algReg = PluginStorage.lookupTable(algClass);
		AlgDesc2List algDescs = new AlgDesc2List();
    	if (algReg == null) return algDescs;
		
    	List<Alg> algList = algReg.getAlgList();
    	String tableName = PluginStorage.lookupTableName(algClass);
    	for (Alg alg : algList) {
        	if (tableName != null && tableName.equals(PluginStorage.NORMAL_ALG)) {
	    		try {
	    			if (acceptAlg(alg))
	    				algDescs.add(alg);
	    		} catch (Throwable e) {LogUtil.trace(e);}
        	}
        	else
        		algDescs.add(alg);
    	}
    	
		return algDescs;
	}


	@Override
	public AlgDesc2 getPluginAlgDesc(Class<? extends Alg> algClass, String algName) throws RemoteException {
    	RegisterTable algReg = PluginStorage.lookupTable(algClass);
    	if (algReg == null) return null;
		Alg alg = algReg.query(algName);
		if (alg == null) return null;
		
		return new AlgDesc2(alg);
	}


	@Override
	public AlgDesc2 getPluginAlgDesc(String algClassName, String algName) throws RemoteException {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends Alg> algClass = (Class<? extends Alg>) Util.getPluginManager().loadClass(algClassName, false);
			return getPluginAlgDesc(algClass, algName);
		}
		catch (Throwable e) {
			LogUtil.error("Error when evaluator gets plug-in algorithm description, caused by " + e.getMessage());
		}
		return null;
	}


	@Override
	public AlgDesc2 getPluginNormalAlgDesc(String algName) throws RemoteException {
		Alg alg = PluginStorage.getNormalAlgReg().query(algName);
		if (alg == null) return null;
		
		return new AlgDesc2(alg);
	}


	@Override
	public Alg getPluginAlg(Class<? extends Alg> algClass, String algName, boolean remote) throws RemoteException {
		return getAlg(PluginStorage.lookupTable(algClass), algName, remote);
	}


	@Override
	public Alg getEvaluatedAlg(String algName, boolean remote) throws RemoteException {
		return getAlg(algRegResult, algName, remote);
	}


	@Override
	public AlgDesc2 getEvaluatedAlgDesc(String algName) throws RemoteException {
		if (algRegResult == null) return null;
		Alg alg = algRegResult.query(algName);
		if (alg == null) return null;

		return new AlgDesc2(alg);
	}


	@Override
	public String getEvaluatedAlgDescText(String algName) throws RemoteException {
		if (algRegResult == null) return null;
		Alg alg = algRegResult.query(algName);
		
		if (alg == null) 
			return null;
		else if (alg instanceof AlgRemote)
			return ((AlgRemote)alg).getDescription();
		else
			return null;
	}


	/**
	 * Retrieving algorithm from register table. Current version only exports normal algorithms.
	 * @param algReg register table.
	 * @param algName algorithm name.
	 * @param remote true if getting remotely.
	 * @return algorithm. It is exported if remote parameter is true.
	 */
	private Alg getAlg(RegisterTable algReg, String algName, boolean remote) {
		if (algReg == null) return null;
		
		Alg alg = algReg.query(algName);
		if (alg == null) return null;
		
		try {
			boolean isNormalAlg = PluginStorage.isNormalAlg(alg);
			
			if (!isNormalAlg)
				return alg;
			else if (!acceptAlg(alg))
				return null;
			else if (!remote)
				return alg;
			else if (alg instanceof AlgRemote) {
				AlgRemote remoteAlg = (AlgRemote)alg;
				synchronized (remoteAlg) {
					remoteAlg.export(config.getEvaluatorPort());
				}
				
				return Util.getPluginManager().wrap(remoteAlg, false);
			}
			else
				return alg;
		}
		catch (Exception e) {
			LogUtil.trace(e);
			return null;
		}
	}
	
	
	/**
	 * Updating exchanged pool result.
	 * @param pool specified exchanged pool.
	 * @return normal pool for evaluation.
	 * @throws RemoteException if any error raises.
	 */
	private synchronized DatasetPool updatePoolResult(DatasetPoolExchanged pool) throws RemoteException {
		//Loc Nguyen added: 2022.05.24
		if (evInfo.isRefPoolResult) {
			DatasetPool returnedPool = this.poolResult != null ? this.poolResult.toDatasetPool(null) : new DatasetPool();
			returnedPool.fillMissingUUID();
			return returnedPool;
		}
		
		if (pool == null) pool = new DatasetPoolExchanged();
		if (poolResult != null) {
			List<DatasetPairExchanged> tempDspList = Util.newList(poolResult.dspList.size());
			tempDspList.addAll(poolResult.dspList);
			
			for (DatasetPairExchanged pair1 : tempDspList) {
				boolean foundTraining = false;
				boolean foundTesting = false;
				boolean foundWhole = false;
				for (DatasetPairExchanged pair2 : pool.dspList) {
					if (pair1.trainingUUID != null && pair2.trainingUUID != null && pair1.trainingUUID.equals(pair2.trainingUUID))
						foundTraining = true;
					if (pair1.testingUUID != null && pair2.testingUUID != null && pair1.testingUUID.equals(pair2.testingUUID))
						foundTesting = true;
					if (pair1.wholeUUID != null && pair2.wholeUUID != null && pair1.wholeUUID.equals(pair2.wholeUUID))
						foundWhole = true;
					
					if (foundTraining && foundTesting && foundWhole)
						break;
				}
				
				boolean clearTraining = pair1.training == null;
				if ((!foundTraining) && (pair1.training != null)) {
					if (pair1.training instanceof DatasetRemoteWrapper) {
						((DatasetRemoteWrapper)pair1.training).forceClear();
						clearTraining = true;
					}
				}
				
				boolean clearTesting = pair1.testing == null;
				if ((!foundTesting) && (pair1.testing != null)) {
					if (pair1.testing instanceof DatasetRemoteWrapper) {
						((DatasetRemoteWrapper)pair1.testing).forceClear();
						clearTesting = true;
					}
				}
				
				boolean clearWhole = pair1.whole == null;
				if ((!foundWhole) && (pair1.whole != null)) {
					if (pair1.whole instanceof DatasetRemoteWrapper) {
						((DatasetRemoteWrapper)pair1.whole).forceClear();
						clearWhole = true;
					}
				}
				
				if (clearTraining && clearTesting && clearWhole)
					poolResult.dspList.remove(pair1);
			}
		}
		
		DatasetPool returnedPool = pool.toDatasetPool(this.poolResult);
		returnedPool.fillMissingUUID();
		if (this.poolResult != null) this.poolResult.unexport(true);
		this.poolResult = returnedPool.toDatasetPoolExchanged();
		this.poolResult.export(config.getEvaluatorPort(), false);
		
		return returnedPool;
	}
	
	
	@Override
	public synchronized boolean updatePool(DatasetPoolExchanged pool, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		if (isStarted() || this.evPool != null) {
			LogUtil.error("Evaluator is running and so it cannot update pool");
			return false;
		}
		
		DatasetPool returnedPool = updatePoolResult(pool);
		
		if (returnedPool != null) {
			fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.update_pool,
				this.otherResult,
				this.poolResult,
				this.evInfo,
				timestamp),
				localTargetListener);
			return true;
		}
		else
			return false;
	}


	@Override
	public synchronized boolean updatePoolWithoutClear(DatasetPoolExchanged pool, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		if (isStarted() || this.evPool != null) {
			LogUtil.error("Evaluator is running and so it cannot update pool");
			return false;
		}
		
		DatasetPool returnedPool = null;
		if (evInfo.isRefPoolResult) {
			//Returned pool here and in this implementation is not important.
			returnedPool = this.poolResult != null ? this.poolResult.toDatasetPool(null) : new DatasetPool();
			returnedPool.fillMissingUUID();
		}
		else {
			if (pool == null) pool = new DatasetPoolExchanged();
			returnedPool = pool.toDatasetPool(this.poolResult);
			returnedPool.fillMissingUUID();
			if (this.poolResult != null) this.poolResult.unexport(true);
			this.poolResult = returnedPool.toDatasetPoolExchanged();
			this.poolResult.export(config.getEvaluatorPort(), false);
		}
		
		fireEvaluatorEvent(new EvaluatorEvent(
			this, 
			EvaluatorEvent.Type.update_pool,
			this.otherResult,
			this.poolResult,
			this.evInfo,
			timestamp),
			localTargetListener);
		return true;
	}

	
	@Override
	public synchronized boolean reloadPool(EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		if (poolResult == null) return false;
		
		if (!evInfo.isRefPoolResult) {
			this.poolResult.reload();
			this.poolResult.fillMissingUUID();
			this.poolResult.export(config.getEvaluatorPort(), false);
		}
		
		fireEvaluatorEvent(new EvaluatorEvent(
			this, 
			EvaluatorEvent.Type.update_pool,
			this.otherResult,
			this.poolResult,
			this.evInfo,
			timestamp),
			localTargetListener);
		return true;
	}

	
	@Override
	public synchronized boolean refPool(boolean ref, DatasetPoolExchanged refPool, String refName, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		if (isStarted() || this.evPool != null) {
			LogUtil.error("Evaluator is running and so it cannot update pool");
			return false;
		}
		if (this.evInfo.isRefPoolResult == ref)
			return false;
		
		if (this.poolResult != null && !this.evInfo.isRefPoolResult) this.poolResult.unexport(true);
		this.poolResult = ref ? (refPool != null ? refPool : new DatasetPoolExchanged()) : new DatasetPoolExchanged();
		this.evInfo.isRefPoolResult = ref;
		this.evInfo.refPoolResultName = ref ? refName : null;
		
		fireEvaluatorEvent(new EvaluatorEvent(
			this, 
			ref ? EvaluatorEvent.Type.ref_pool : EvaluatorEvent.Type.unref_pool,
			this.otherResult,
			this.poolResult,
			this.evInfo,
			timestamp),
			localTargetListener);
		return true;
	}

	
	@Override
	public boolean refPool(boolean ref, DatasetPoolsService poolsService, String refName, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		if (isStarted() || this.evPool != null) {
			LogUtil.error("Evaluator is running and so it cannot update pool");
			return false;
		}
		if (this.evInfo.isRefPoolResult == ref)
			return false;
		
		if (poolsService == null) poolsService = getDatasetPoolsService();
		if (poolsService != null) {
			try {
				if (ref) {
					DatasetPoolExchangedItem item = refName != null ? poolsService.get(refName) : null;
					if (item != null) item.addClient(this);
				}
				else
					poolsService.removeClient(this, false);
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		
		if (this.poolResult != null && !this.evInfo.isRefPoolResult) this.poolResult.unexport(true);
		if (poolsService == null || refName == null || !ref)
			this.poolResult = new DatasetPoolExchanged();
		else {
			try {
				this.poolResult = poolsService.get(refName).getPool();
			}
			catch (Throwable e) {
				this.poolResult = new DatasetPoolExchanged();
				LogUtil.trace(e);
			}
		}
		this.evInfo.isRefPoolResult = ref;
		this.evInfo.refPoolResultName = ref ? refName : null;
		
		fireEvaluatorEvent(new EvaluatorEvent(
			this, 
			ref ? EvaluatorEvent.Type.ref_pool : EvaluatorEvent.Type.unref_pool,
			this.otherResult,
			this.poolResult,
			this.evInfo,
			timestamp),
			localTargetListener);
		return true;
	}


	@Override
	public boolean refPool(boolean ref, String refName, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return refPool(ref, getDatasetPoolsService(), refName, localTargetListener, timestamp);
	}


	/*
	 * This method only clears parameters.
	 */
	@Override
	protected void clear() {
		this.evAlgList = null;
		this.evAlg = null;
		this.evPool = null;
		this.evParameter = null;
		
		this.evTaskQueue.stop();
	}

	
    @Override
	protected void task() {
		LogUtil.info("Evaluator#task not used because overriding Evaluator#run");
	}

	
	@SuppressWarnings("static-access")
	@Override
	public synchronized boolean forceStop() {
		if (!super.forceStop())
			return false;

		try {
			Thread.currentThread().sleep(1000);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		if (evAlgList != null) {
			synchronized (delayUnsetupAlgs) {
				for (Alg alg : evAlgList) {
					for (Alg delayUnsetupAlg : delayUnsetupAlgs) {
						if (!alg.getName().equals(delayUnsetupAlg.getName())) {
							try {
								unsetupAlg(alg);
							} catch (Throwable e) {LogUtil.trace(e);}
						}
					}
				}
			}
		}
		
		otherResult.endDate = System.currentTimeMillis();
		
		thread = null;
		paused = false;

		fireEvaluateEvent(new EvaluateEvent(this, Type.done, result));
		
		clear();

		return true;
	}
	
	
	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		return config;
	}
	
	
	@Override
	public void setConfig(EvaluatorConfig config) throws RemoteException {
		if (config == null) return;
		this.config.putAll(config);
	}


	@Override
	public boolean isDelegate() throws RemoteException {
		return false;
	}


	@Override
	public synchronized void pluginChanged(PluginChangedEvent evt) throws RemoteException {
		if (remoteIsStarted()) {
			if (!stop()) return;
		}

		clearDelayUnsetupAlgs();
		clearResult();
		
		firePluginChangedEvent(evt);
	}


	@Override
	public boolean isIdle() throws RemoteException {
		return !remoteIsStarted();
	}


	@Override
	public int getPort() throws RemoteException {
		return config.getEvaluatorPort();
	}

	
	@Override
	public List<EventObject> doTask(UUID listenerID) throws RemoteException {
		return evTaskQueue.doTask(listenerID);
	}


	@Override
	public List<EventObject> doTask2(UUID listenerID) throws RemoteException {
		return evTaskQueue.doTask2(listenerID);
	}


	@Override
	public void addPluginChangedListener(PluginChangedListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(PluginChangedListener.class, listener);
		}
    }

	
	@Override
	public void addPluginChangedListener(UUID listenerID) throws RemoteException {
		evTaskQueue.addListener(listenerID);
	}


	@Override
    public void removePluginChangedListener(PluginChangedListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(PluginChangedListener.class, listener);
		}
    }
	
    
    @Override
	public void removePluginChangedListener(UUID listenerID) throws RemoteException {
    	evTaskQueue.removeListener(listenerID);
	}


	/**
     * Return an array of registered plug-in changed listeners.
     * @return array of registered plug-in changed listeners.
     */
    protected PluginChangedListener[] getPluginChangedListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(PluginChangedListener.class);
		}
    }

    
    /**
     * Dispatching plug-in changed event to registered plug-in changed listeners after plug-in storage was changed.
     * @param evt plug-in changed event is issued to registered plug-in changed listeners after plug-in storage was changed.
     */
    protected void firePluginChangedEvent(PluginChangedEvent evt) {
		evTaskQueue.addTask(evt);

		PluginChangedListener[] listeners = getPluginChangedListeners();
		for (PluginChangedListener listener : listeners) {
    		try {
    	    	if ((evt instanceof Simplify) && !(listener.classPathContains(evt.getClass().getName()))) {
	    			PluginChangedEvent simplifiedEvt = (PluginChangedEvent) ((Simplify)evt).simplify();
	    			if (simplifiedEvt != null) evt = simplifiedEvt;
    	    	}
    		} catch (Exception e) {LogUtil.trace(e);}

	    	try {
				listener.pluginChanged(evt);
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
    }

    
    @Override
	public void addEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluatorListener.class, listener);
		}
    }

    
	@Override
	public void addEvaluatorListener(UUID listenerID) throws RemoteException {
		evTaskQueue.addListener(listenerID);
	}

	
	@Override
    public void removeEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluatorListener.class, listener);
		}
    }
	
    
    @Override
	public void removeEvaluatorListener(UUID listenerID) throws RemoteException {
    	evTaskQueue.removeListener(listenerID);
	}


    /**
     * Return array of evaluator listeners for this evaluator.
     * @return array of evaluator listeners for this evaluator.
     */
    protected EvaluatorListener[] getEvaluatorListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluatorListener.class);
		}
    }

    
    /**
     * Firing (issuing) an event from this evaluator to all evaluator listeners. 
     * @param evt event from this evaluator.
     * @param localTargetListener local target listener.
     */
    protected void fireEvaluatorEvent(EvaluatorEvent evt, EvaluatorListener localTargetListener) {
		evTaskQueue.addTask(evt);

		EvaluatorListener[] listeners = getEvaluatorListeners();
		for (EvaluatorListener listener : listeners) {
    		try {
    	    	if ((evt instanceof Simplify) && !(listener.classPathContains(evt.getClass().getName()))) {
	    			EvaluatorEvent simplifiedEvt = (EvaluatorEvent) ((Simplify)evt).simplify();
	    			if (simplifiedEvt != null) evt = simplifiedEvt;
    	    	}
    		} catch (Exception e) {LogUtil.trace(e);}

	    	try {
				if (localTargetListener != null) {
					if (listener == localTargetListener)
						listener.receivedEvaluator(evt);
				}
				else
					listener.receivedEvaluator(evt);
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
    }

    
    @Override
	public void addEvaluateListener(EvaluateListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluateListener.class, listener);
		}
    }

    
	@Override
	public void addEvaluateListener(UUID listenerID) throws RemoteException {
		evTaskQueue.addListener(listenerID);
	}

	
	@Override
    public void removeEvaluateListener(EvaluateListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluateListener.class, listener);
		}
    }
	
    
    @Override
	public void removeEvaluateListener(UUID listenerID) throws RemoteException {
    	evTaskQueue.removeListener(listenerID);
	}


    /**
     * Return array of evaluation listeners for this evaluator.
     * @return array of evaluation listeners.
     */
    protected EvaluateListener[] getEvaluateListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluateListener.class);
		}
    }

    
    /**
     * Firing an evaluation event from this evaluator to all evaluation listeners. 
     * @param evt event from this evaluator.
     */
    protected void fireEvaluateEvent(EvaluateEvent evt) {
		evTaskQueue.addTask(evt);
    	
		EvaluateListener[] listeners = getEvaluateListeners();
		for (EvaluateListener listener : listeners) {
    		try {
    	    	if ((evt instanceof Simplify) && !(listener.classPathContains(evt.getClass().getName()))) {
	    			EvaluateEvent simplifiedEvt = (EvaluateEvent) ((Simplify)evt).simplify();
	    			if (simplifiedEvt != null) evt = simplifiedEvt;
    	    	}
    		} catch (Exception e) {LogUtil.trace(e);}

	    	if ((!config.isTiedSync()) && evTaskQueue.isRunning() && (!(listener instanceof AbstractEvaluateGUI))) {
	    		EvaluateEvent finalEvt = evt;
				evTaskQueue.addTask(new TaskQueue.Task() {
					@Override
					public void doTask() throws Exception {
						listener.receivedEvaluation(finalEvt);
					}
				});
			}
			else {
				try {
					listener.receivedEvaluation(evt);
				}
				catch (Exception e) {LogUtil.trace(e);}
			}
		}
	
		String evStorePath = config.getAsString(DataConfig.STORE_URI_FIELD);
		if (evStorePath != null && algRegResult != null) {
			boolean saveResultSummary = false;
			try {
				saveResultSummary = config.isSaveResultSummary();
			} catch (Throwable e) {LogUtil.trace(e);}
			
			evt.setMetrics(result); //Important code line, saving all metrics.
			evProcessor.saveEvaluateResult(evStorePath, evt, algRegResult, saveResultSummary);
		}
		
		
		//Backing up evaluation results.
		boolean backup = isBackup() || (evStorePath == null && listeners.length == 0);
		if (!backup) return;
		
		if (evt.getType() != Type.done && evt.getType() != Type.done_one)
			return;
		if (this.result == null || this.algRegResult == null)
			return;
		
		try {
			xURI backupDir = xURI.create(Constants.BACKUP_DIRECTORY);
			UriAdapter backupAdapter = new UriAdapter(backupDir);
			if (!backupAdapter.exists(backupDir)) backupAdapter.create(backupDir, true);
			xURI analyzeBackupFile = backupDir.concat("evaluator-analyze-backup-" + new Date().getTime() + "." + Constants.DEFAULT_EXT);
			
			MetricsUtil util = new MetricsUtil(this.result, algRegResult, this);
			Writer writer = backupAdapter.getWriter(analyzeBackupFile, false);
			writer.write(util.createPlainText());
			writer.close();
			backupAdapter.close();
		}
		catch (Throwable e) {LogUtil.trace(e);}
    }

    
    @Override
	public void addEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluateProgressListener.class, listener);
		}
    }

    
	@Override
	public void addEvaluateProgressListener(UUID listenerID) throws RemoteException {
		evTaskQueue.addListener(listenerID);
	}

	
    @Override
    public void removeEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluateProgressListener.class, listener);
		}
    }
	
    
    @Override
	public void removeEvaluateProgressListener(UUID listenerID) throws RemoteException {
    	evTaskQueue.removeListener(listenerID);
	}


    /**
     * Getting an array of evaluation progress listeners.
     * @return array of evaluation progress listeners.
     */
    protected EvaluateProgressListener[] getEvaluateProgressListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluateProgressListener.class);
		}
    }
    
    
    /**
     * Firing evaluation progress event.
     * @param evt the specified for evaluation progress.
     */
    protected void fireEvaluateProgressEvent(EvaluateProgressEvent evt) {
		evTaskQueue.addTask(evt);

    	EvaluateProgressListener[] listeners = getEvaluateProgressListeners();
		for (EvaluateProgressListener listener : listeners) {
    		try {
    	    	if ((evt instanceof Simplify) && !(listener.classPathContains(evt.getClass().getName()))) {
	    			EvaluateProgressEvent simplifiedEvt = (EvaluateProgressEvent) ((Simplify)evt).simplify();
	    			if (simplifiedEvt != null) evt = simplifiedEvt;
    	    	}
    		} catch (Exception e) {LogUtil.trace(e);}

	    	if ((!config.isTiedSync()) && evTaskQueue.isRunning() && (!(listener instanceof AbstractEvaluateGUI))) {
	    		EvaluateProgressEvent finalEvt = evt;
				evTaskQueue.addTask(new TaskQueue.Task() {
					@Override
					public void doTask() throws Exception {
						listener.receivedProgress(finalEvt);
					}
				});
			}
			else {
				try {
					listener.receivedProgress(evt);
				}
				catch (Exception e) {LogUtil.trace(e);}
			}
		}
    }


    @Override
	public void addSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(SetupAlgListener.class, listener);
		}
    }

    
	@Override
	public void addSetupAlgListener(UUID listenerID) throws RemoteException {
		evTaskQueue.addListener(listenerID);
	}

	
    @Override
    public void removeSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
    }
	
    
    @Override
	public void removeSetupAlgListener(UUID listenerID) throws RemoteException {
    	evTaskQueue.removeListener(listenerID);
	}


    /**
     * Getting an array of setup algorithm listeners.
     * @return array of setup algorithm listeners.
     */
    protected SetupAlgListener[] getSetupAlgListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(SetupAlgListener.class);
		}
    }
    
    
    /**
     * Firing setup algorithm event.
     * @param evt the specified for setup algorithm event.
     */
    protected void fireSetupAlgEvent(SetupAlgEvent evt) {
		evTaskQueue.addTask(evt);

    	SetupAlgListener[] listeners = getSetupAlgListeners();
		for (SetupAlgListener listener : listeners) {
    		try {
    	    	if ((evt instanceof Simplify) && !(listener.classPathContains(evt.getClass().getName()))) {
	    			SetupAlgEvent simplifiedEvt = (SetupAlgEvent) ((Simplify)evt).simplify();
	    			if (simplifiedEvt != null) evt = simplifiedEvt;
    	    	}
    		} catch (Exception e) {LogUtil.trace(e);}
	    	
			if ((!config.isTiedSync()) && evTaskQueue.isRunning() && (!(listener instanceof AbstractEvaluateGUI))) {
				final SetupAlgEvent finalEvt = evt;
				evTaskQueue.addTask(new TaskQueue.Task() {
					@Override
					public void doTask() throws Exception {
						listener.receivedSetup(finalEvt);
					}
				});
			}
			else {
				try {
					listener.receivedSetup(evt);
				}
				catch (Exception e) {LogUtil.trace(e);}
			}
		}
	
		String evStorePath = config.getAsString(DataConfig.STORE_URI_FIELD);
		if (evStorePath != null) {
			boolean saveResultSummary = false;
			try {
				saveResultSummary = config.isSaveResultSummary();
			} catch (Throwable e) {LogUtil.trace(e);}

			evProcessor.saveSetupResult(evStorePath, evt, saveResultSummary);
		}
		
		
		//Backing up evaluation results.
		boolean backup = isBackup() || (evStorePath == null && listeners.length == 0);
		if (!backup || evt.getType() != SetupAlgEvent.Type.done)
			return;
		try {
			String info = "========== Algorithm \"" + evt.getAlgName() + "\" ==========\n";
			info = info + evt.translate() + "\n\n\n\n";

			xURI backupDir = xURI.create(Constants.BACKUP_DIRECTORY);
			UriAdapter backupAdapter = new UriAdapter(backupDir);
			if (!backupAdapter.exists(backupDir)) backupAdapter.create(backupDir, true);
			xURI analyzeBackupFile = backupDir.concat(
					"evaluator-" + evt.getAlgName() + "-backup-" + new Date().getTime() +
					EvaluateProcessor.SETUP_DONE_FILE_EXTENSION);
			
			Writer writer = backupAdapter.getWriter(analyzeBackupFile, false);
			writer.write(info.toCharArray());
			writer.close();
			backupAdapter.close();
		}
		catch (Throwable e) {LogUtil.trace(e);}
    }

    
    @Override
	public void addElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
    	synchronized (listenerList) {
    		listenerList.add(CounterElapsedTimeListener.class, listener);
    	}
    }

    
    @Override
    public void removeElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
    	synchronized (listenerList) {
    		listenerList.remove(CounterElapsedTimeListener.class, listener);
    	}
    }


    @Override
	public boolean classPathContains(String className) throws RemoteException {
    	try {
    		Util.getPluginManager().loadClass(className, false);
    		return true;
    	} catch (Exception e) {}
    	
		return false;
	}


	@Override
	public String getEvaluateStorePath() throws RemoteException {
   		return config.getAsString(DataConfig.STORE_URI_FIELD);
	}

	
	@Override
	public void setEvaluateStorePath(String evStorePath) throws RemoteException {
    	if (evStorePath == null)
    		config.remove(DataConfig.STORE_URI_FIELD);
    	else
    		config.put(DataConfig.STORE_URI_FIELD, evStorePath);
	}


	@Override
	public Service getReferredService() throws RemoteException {
		return referredService;
	}


	@Override
	public void setReferredService(Service referredService) throws RemoteException {
		this.referredService = referredService; 
	}


	/**
     * Testing whether backing up evaluation results is necessary.
     * @return whether backing up evaluation results is necessary.
     */
    private boolean isBackup() {
		boolean backup = false;
		try {
			String bkText = Util.getHudupProperty("evaluator_backup");
			if (bkText != null && !bkText.isEmpty())
				backup = Boolean.parseBoolean(bkText);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			backup = false;
		}
		return backup;
    }
    
    
	@Override
	public String toString() {
    	String evaluatorName = "No name";
		try {
			evaluatorName = getName();
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		return DSUtil.shortenVerbalName(evaluatorName);
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exportedStub != null) return exportedStub;

		exportedStub = (Evaluator) NetUtil.RegistryRemote.export(this, serverPort);
		if (exportedStub != null)
			LogUtil.info("Evaluator served at port " + serverPort);
		else
			LogUtil.info("Evaluator failed to export");
		
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (exportedStub == null) return;
		
		boolean ret = NetUtil.RegistryRemote.unexport(this);
		exportedStub = null;
		if (ret)
			LogUtil.info("Evaluator unexported successfully");
		else
			LogUtil.info("Evaluator unexported failedly");
	}


	@Override
	public synchronized void forceUnexport() throws RemoteException {
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}
	
	
	@Override
	public boolean isAgent() throws RemoteException {
		return isAgent;
	}


	@Override
	public synchronized void setAgent(boolean isAgent) throws RemoteException {
		this.isAgent = isAgent;
	}


	@Override
	public boolean containsAgent() throws RemoteException {
		return isAgent();
	}

	
	/**
	 * Getting purging timer.
	 * @return purging timer.
	 */
	public Timer2 getPurgeTimer() {
		return purgeTimer;
	}
	
	
	/**
	 * Setting purging timer.
	 * @param purgeTimer purging timer.
	 */
	public void setPurgeTimer(Timer2 purgeTimer) {
		removePurgeTimer();
		if (purgeTimer != null) {
			this.purgeTimer = purgeTimer;
			try {
				stimulate();
			} catch (Exception e) {LogUtil.trace(e);}
		}
	}
	
	
	/**
	 * Removing purging timer.
	 */
	public void removePurgeTimer() {
		if (purgeTimer == null) return;
		
		purgeTimer.stop();
		purgeTimer = null;
	}
	
	
	/**
	 * Getting listener list.
	 * @return listener list.
	 */
	public EventListenerList2 getListenerList() {
		return listenerList;
	}
	
	
	@Override
	public String getVersionName() throws RemoteException {
		String name = getName();
		try {
			String reproducedVersion = config.getReproducedVersion();
			if (reproducedVersion != null && !reproducedVersion.isEmpty())
				name = EvaluatorAbstract.createVersionName(name, reproducedVersion);
		} catch (Exception e) {}
		
		return name;
	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


	@Override
	public synchronized void close() throws Exception {
		try {
			remoteStop();
		}
		catch (Throwable e) {LogUtil.trace(e);}

		try {
			clearDelayUnsetupAlgs();
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			if (poolResult != null && !evInfo.isRefPoolResult)
				poolResult.clear(true);
			poolResult = null;

			if (evPool != null)
				evPool.clear();
			evPool = null;
		}
		catch (Throwable e) {LogUtil.trace(e);}

		try {
	    	if (algRegResult != null) {
	    		algRegResult.unexportNonPluginAlgs();
	    		algRegResult.clear();
	    	}
	    	algRegResult = null;
		}
		catch (Throwable e) {LogUtil.trace(e);}

    	try {
			evProcessor.close();
		}
		catch (Throwable e) {LogUtil.trace(e);}

    	try {
    		if (purgeTimer != null) {
    			purgeTimer.stop();
    			purgeTimer = null;
    		}
    	}
		catch (Throwable e) {LogUtil.trace(e);}
    	
		try {
			config.save();
		} catch (Throwable e) {LogUtil.trace(e);}

		try {
			unexport();
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
			close();
		} catch (Throwable e) {LogUtil.errorNoLog("Finalize error: " + e.getMessage());}
	}

	
	@Override
	public synchronized boolean remoteStart(List<String> algNameList, DatasetPoolExchanged pool, ClassProcessor cp, DataConfig config, Timestamp timestamp, Serializable parameter) throws RemoteException {
		HudupRMIClassLoader cl = null;
		if (cp != null)
			cl = new HudupRMIClassLoader(getClass().getClassLoader(), (ClassProcessor)cp);

		RegisterTable algReg = PluginStorage.getNormalAlgReg();
		List<Alg> algList = Util.newList();
		for (String algName : algNameList) {
			AlgDesc algDesc = config != null ? config.getAsAlgDesc(algName) : null;
			Alg evAlg = null;
			
			if (PluginStorage.lookupNextUpdateList(Alg.class, algName) != -1)
				continue;
			else if (algReg.contains(algName))
				evAlg = algReg.query(algName);
			else if (algRegResult != null && algRegResult.contains(algName))
				evAlg = algRegResult.query(algName);
			else if (cl != null && config.containsKey(algName)) {
				try {
					Class<?> newAlgClass = cl.loadClass(algDesc.getAlgClassName());
					if (newAlgClass != null && Alg.class.isAssignableFrom(newAlgClass)) {
						evAlg = (Alg)newAlgClass.getDeclaredConstructor().newInstance();
					}
				}
				catch (Exception e) {
					LogUtil.error("remoteStart: Error to load class " + algDesc.getAlgClassName() + " from remote host/client" +
							" caused by " + e.getMessage());
					evAlg = null;
				}
				
			}
			
			if ((evAlg != null) && !(evAlg instanceof NullAlg) && acceptAlg(evAlg)) {
				if (algDesc != null)
					evAlg.getConfig().putAll(algDesc.getConfig());
				algList.add(evAlg);
			}
		}
		
		if (algList.size() == 0)
			return false;
		else
			return remoteStart(algList, pool, timestamp, parameter);
	}
	
	
	@Override
	public synchronized boolean remoteStart(Serializable... parameters) throws RemoteException {
		if (parameters == null || parameters.length < 2
				|| !(parameters[0] instanceof List<?>)
				|| !(parameters[1] instanceof DatasetPoolExchanged))
			return false;
		
		@SuppressWarnings("unchecked")
		List<String> algNameList = (List<String>)(parameters[0]);
		DatasetPoolExchanged pool = (DatasetPoolExchanged)(parameters[1]);
		
		ClassProcessor cp = null;
		if ((parameters.length > 2) && (parameters[2] instanceof ClassProcessor))
			cp = (ClassProcessor)parameters[2];
		
		DataConfig config = null;
		if ((parameters.length > 3) && (parameters[3] instanceof DataConfig))
			config = (DataConfig)parameters[3];
		
		Timestamp timestamp = null;
		if ((parameters.length > 4) && (parameters[4] instanceof Serializable))
			timestamp = (Timestamp)parameters[4];
		
		Serializable parameter = null;
		if ((parameters.length > 5) && (parameters[5] instanceof Serializable))
			parameter = (Serializable)parameters[5];
		
		return remoteStart(algNameList, pool, cp, config, timestamp, parameter);
	}


	@Override
	public synchronized boolean remotePause() throws RemoteException {
		if (otherResult.inAlgSetup && evAlg != null && evAlg instanceof AlgRemote) {
			if (!((AlgRemote)evAlg).learnPause()) {
				if (!pause()) return false;
			}
			else
				paused = true;
		}
		else {
			if (!pause()) return false;
		}

		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.pause),
				null); // firing paused event.
		
		evTaskQueue.pause();

		return true;
	}

	
	@Override
	public synchronized boolean remoteResume() throws RemoteException {
		if (otherResult.inAlgSetup && evAlg != null && evAlg instanceof AlgRemote) {
			if (!((AlgRemote)evAlg).learnResume()) {
				if (!resume()) return false;
			}
			else
				paused = false;
		}
		else {
			if (!resume()) return false;
		}

		evTaskQueue.resume();

		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.resume),
				null);
		
		return true;
	}

	
	@Override
	public synchronized boolean remoteStop() throws RemoteException {
		if (otherResult.inAlgSetup && evAlg != null && evAlg instanceof AlgRemote) {
			((AlgRemote)evAlg).learnStop();
		}
		
		if (!stop()) return false;

		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.stop),
				null);
		
		this.evTaskQueue.stop();
		
		return true;
	}

	
	@Override
	public synchronized void remoteStopAndClearResults(boolean clearPool) throws RemoteException {
		remoteStop();
		
		clearDelayUnsetupAlgs();

		DatasetPoolExchanged oldPool = poolResult;
		if (!clearPool) poolResult = null;
		clearResult();
		if (!clearPool) poolResult = oldPool;
		
		fireEvaluatorEvent(new EvaluatorEvent(
			this,
			EvaluatorEvent.Type.update_pool,
			this.otherResult,
			this.poolResult,
			this.evInfo,
			new Timestamp()),
			null);
	}


	@Override
	public boolean remoteForceStop() throws RemoteException {
		return forceStop();
	}

	
	@Override
	public boolean remoteIsStarted() throws RemoteException {
		return isStarted();
	}

	
	@Override
	public boolean remoteIsPaused() throws RemoteException {
		return isPaused();
	}

	
	@Override
	public boolean remoteIsRunning() throws RemoteException {
		return isRunning();
	}


	/**
	 * Extracting information from specified evaluation progress event.
	 * @param evt specified evaluation progress event.
	 * @return information from evaluation progress event as string array.
	 */
	public static String[] extractEvaluateProgressInfo(EvaluateProgressEvent evt) {
		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		String algName = evt.getAlgName();
		int datasetId = evt.getDatasetId();
		int vCurrentCount = evt.getCurrentCount();
		int vCurrentTotal = evt.getCurrentTotal();
		
		String status1 =
				I18nUtil.message("algorithm") + " '" + DSUtil.shortenVerbalName(algName) + "' " +
				I18nUtil.message("dataset") + " '" + datasetId + "': " + 
				vCurrentCount + "/" + vCurrentTotal;
		
		String status2 =
				I18nUtil.message("total") + ": " + progressStep + "/" + progressTotal;
		
		return new String[] {status1, status2};
	}
	
	
	/**
	 * Extracting information from specified setting up algorithm event.
	 * @param evt specified setting up algorithm event.
	 * @return information from specified setting up algorithm event.
	 */
	public static String[] extractSetupInfo(SetupAlgEvent evt) {
		String algName = evt.getAlgName();
		if (algName == null) return new String[] {""};

		String datasetIdOrName = evt.extractTrainingDatasetIdOrName();
		String status = "";
		if (evt.getType() == SetupAlgEvent.Type.doing) {
			status = I18nUtil.message("setting_up_algorithm") + " '" + DSUtil.shortenVerbalName(algName) + "'";
			if (datasetIdOrName != null)
				status += " " + I18nUtil.message("dataset") + " '" + DSUtil.shortenVerbalName(datasetIdOrName) + "'";
			
			if (evt.getProgressTotalEstimated() > 0)
				status += ": " + MathUtil.format((double)evt.getProgressStep() / evt.getProgressTotalEstimated() * 100.0) + "%";
			else
				status += ". " + I18nUtil.message("please_wait") + "...";
		}
		else if (evt.getType() == SetupAlgEvent.Type.done) {
			if (evt.getProgressTotalEstimated() > 0) {
				status = I18nUtil.message("setting_up_algorithm") + " '" + DSUtil.shortenVerbalName(algName) + "'";
				if (datasetIdOrName != null)
					status += " " + I18nUtil.message("dataset") + " '" + DSUtil.shortenVerbalName(datasetIdOrName) + "'";

				status += " done " + MathUtil.format((double)evt.getProgressStep() / evt.getProgressTotalEstimated() * 100.0) + "%";
			}
		}
		
		return new String[] {status};
	}
	
	
	/**
	 * Getting top-most plug-in changed listener with specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return top-most plug-in changed listener with specified evaluator.
	 */
	public static PluginChangedListener getTopMostPluginChangedListener(Evaluator evaluator) {
		return getTopMostPluginChangedListener(evaluator, null, null, false);
	}

	
	/**
	 * Getting top-most plug-in changed listener with specified evaluator, account, and password.
	 * @param evaluator specified evaluator.
	 * @param account specified account.
	 * @param password specified password.
	 * @return top-most plug-in changed listener with specified evaluator, account, and password.
	 */
	public static PluginChangedListener getTopMostPluginChangedListener(Evaluator evaluator, String account, String password) {
		return getTopMostPluginChangedListener(evaluator, account, password, false);
	}
	
	
	/**
	 * Getting top-most plug-in changed listener with specified evaluator.
	 * @param evaluator specified evaluator.
	 * @param loginIfNecessary flag to indicate whether show login dialog if necessary.
	 * @return top-most plug-in changed listener with specified evaluator.
	 */
	public static PluginChangedListener getTopMostPluginChangedListener(Evaluator evaluator, boolean loginIfNecessary) {
		return getTopMostPluginChangedListener(evaluator, null, null, loginIfNecessary);
	}
	
	
	/**
	 * Getting top-most plug-in changed listener with specified evaluator, account, and password.
	 * @param evaluator specified evaluator.
	 * @param account specified account.
	 * @param password specified password.
	 * @param loginIfNecessary flag to indicate whether show login dialog if necessary.
	 * @return top-most plug-in changed listener with specified evaluator, account, and password.
	 */
	private static PluginChangedListener getTopMostPluginChangedListener(Evaluator evaluator, String account, String password, boolean loginIfNecessary) {
		if (evaluator == null) return null;
		
		Service referredService = null;
		try {
			referredService = evaluator.getReferredService();
		} catch (Exception e) {LogUtil.trace(e);}
		
		if (referredService == null)
			return evaluator;
		else if (referredService instanceof ServiceExt) {
			PowerServer referredServer = null;
			if (referredService instanceof ServiceLocal)
				referredServer = ((ServiceLocal)referredService).getReferredServer();
			else {
				if (account == null || password == null) {
					if (loginIfNecessary) {
						LoginDlg dlgLogin = new LoginDlg(null, "Enter admin account and password \nto retrieve server");
						if (dlgLogin.wasLogin()) {
							account = dlgLogin.getUsername();
							password = dlgLogin.getPassword();
						}
					}
				}
				
				if (account != null && password != null) {
					try {
						referredServer = ((ServiceExt)referredService).getReferredServer(account, password);
					} catch (Exception e) {LogUtil.trace(e);}
				}
				else
					referredServer = null;
			}
			
			if (referredServer == null) {
				if (loginIfNecessary) {
					JOptionPane.showMessageDialog(null,
						"Cannot retrieve server but \n possible to retrieve service or evaluator", 
						"Cannot retrieve server", JOptionPane.INFORMATION_MESSAGE);
				}
				
				if (referredService instanceof PluginChangedListener)
					return (PluginChangedListener)referredService;
				else
					return evaluator;
			}
			else
				return referredServer;
		}
		else if (referredService instanceof PluginChangedListener)
			return (PluginChangedListener)referredService;
		else
			return evaluator;
	}
	
	
	/**
	 * Getting power server according to plug-in changed listeners.
	 * @param evaluator specified evaluator.
	 * @return power server;
	 */
	public static PowerServer getServer(Evaluator evaluator) {
		PluginChangedListener listener = EvaluatorAbstract.getTopMostPluginChangedListener(evaluator);
		return (listener != null) && (listener instanceof PowerServer) ? (PowerServer)listener : null;
	}

	
	/**
	 * Getting referred service by many possible ways.
	 * @return referred service.
	 */
	private Service getReferredServiceExt() {
		Service service = null;
		try {
			service = getReferredService();
		} catch (Throwable e) {LogUtil.trace(e);}
		if (service != null) return service;
		
		PowerServer server = EvaluatorAbstract.getServer(this);
		if (server == null) return null;
		try {
			return server.getService();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return null;
	}
	
	
	/**
	 * Getting referred service.
	 * @param evaluator specified evaluator.
	 * @param loginIfNecessary flag to indicate whether show login dialog if necessary.
	 * @return pool service.
	 */
	public static Service getReferredService(Evaluator evaluator, boolean loginIfNecessary) {
		if (evaluator == null)
			return null;
		else if (evaluator instanceof EvaluatorAbstract)
			return ((EvaluatorAbstract)evaluator).getReferredServiceExt();
		else {
			try {
				Service service = evaluator.getReferredService();
				if (service != null) return service;
			} catch (Throwable e) {LogUtil.trace(e);}
			
			if (loginIfNecessary) {
				try {
					Connector connector = Connector.connect();
					return connector.getService();
				} catch (Throwable e) {LogUtil.trace(e);}
				return null;
			}
			else
				return null;
		}
	}
	
	
	/**
	 * Getting pool service.
	 * @return pool service.
	 */
	private DatasetPoolsService getDatasetPoolsService() {
		Service service = getReferredServiceExt();
		if (service == null)
			return null;
		else if (service instanceof ServiceLocal)
			return ((ServiceLocal)service).getDatasetPoolsService();
		else
			return null;
	}
	
	
	/**
	 * Getting pool service given account and password.
	 * @param account account name.
	 * @param password account password.
	 * @return pool service.
	 */
	private DatasetPoolsService getDatasetPoolsService(String account, String password) {
		Service service = getReferredServiceExt();
		if (service == null)
			return null;
		else if (service instanceof ServiceLocal)
			return ((ServiceLocal)service).getDatasetPoolsService();
		else if (service instanceof ServiceExt) {
			try {
				return ((ServiceExt)service).getDatasetPoolsService(account, password);
			} catch (Throwable e) {LogUtil.trace(e);}
			return null;
		}
		else
			return null;
	}

	
	/**
	 * Getting pool service.
	 * @param evaluator specified evaluator.
	 * @param loginIfNecessary flag to indicate whether show login dialog if necessary.
	 * @return pool service.
	 */
	public static DatasetPoolsService getDatasetPoolsService(Evaluator evaluator, boolean loginIfNecessary) {
		if (evaluator == null) return null;
		
		DatasetPoolsService poolsService = null;
		if (evaluator instanceof EvaluatorAbstract) {
			poolsService = ((EvaluatorAbstract)evaluator).getDatasetPoolsService();
			if (poolsService == null && loginIfNecessary) {
				LoginDlg login = new LoginDlg(null, "Enter user name and password");
				if (login.wasLogin())
					poolsService = ((EvaluatorAbstract)evaluator).getDatasetPoolsService(login.getUsername(), login.getPassword());
			}
		}
		if (poolsService != null) return poolsService;
		if (!loginIfNecessary) return null;
		
		try {
			ConnectInfo connectInfo = null;
			Service service = evaluator.getReferredService();
			if (service == null) {
				Connector connector = Connector.connect();
				service = connector.getService();
				connectInfo = connector.getConnectInfo();
			}
			
			if (service == null || !(service instanceof ServiceExt))
				return null;
			else if (service instanceof ServiceLocal)
				poolsService = ((ServiceLocal)service).getDatasetPoolsService();
			else if (connectInfo == null) {
				LoginDlg login = new LoginDlg(null, "Enter user name and password");
				if (!login.wasLogin()) return null;
				poolsService = ((ServiceExt)service).getDatasetPoolsService(login.getUsername(), login.getPassword());
			}
			else
				poolsService = ((ServiceExt)service).getDatasetPoolsService(connectInfo.account.getName(), connectInfo.account.getPassword());
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return poolsService;
	}
	
	
	/**
	 * Checking whether the specified evaluator is wrapped evaluator.
	 * @param evaluator specified evaluator.
	 * @return whether the specified evaluator is wrapped evaluator.
	 */
	public static boolean isWrapper(Evaluator evaluator) {
		return (evaluator instanceof EvaluatorWrapperExt) || (evaluator instanceof EvaluatorWrapper);
	}
	
	
	/**
	 * Checking whether the specified evaluator is remote object.
	 * @param evaluator specified evaluator.
	 * @return whether the specified evaluator is remote object.
	 */
	public static boolean isRemote(Evaluator evaluator) {
		if (evaluator == null)
			return false;
		else if ((evaluator instanceof EvaluatorWrapper) || (evaluator instanceof EvaluatorWrapperExt)) {
			Evaluator remoteEvaluator = null;
			if (evaluator instanceof EvaluatorWrapper)
				remoteEvaluator = ((EvaluatorWrapper)evaluator).remoteEvaluator;
			else if (evaluator instanceof EvaluatorWrapperExt)
				remoteEvaluator = ((EvaluatorWrapperExt)evaluator).remoteEvaluator;

			return isRemote(remoteEvaluator);
		}
		else if (evaluator instanceof EvaluatorAbstract)
			return false;
		else
			return true;
	}

	
	/**
	 * Getting the most inner evaluator of the specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return the most inner evaluator of the specified evaluator.
	 */
	public static Evaluator getMostInner(Evaluator evaluator) {
		if (evaluator == null)
			return null;
		else if ((evaluator instanceof EvaluatorWrapper) || (evaluator instanceof EvaluatorWrapperExt)) {
			Evaluator remoteEvaluator = null;
			if (evaluator instanceof EvaluatorWrapper)
				remoteEvaluator = ((EvaluatorWrapper)evaluator).remoteEvaluator;
			else if (evaluator instanceof EvaluatorWrapperExt)
				remoteEvaluator = ((EvaluatorWrapperExt)evaluator).remoteEvaluator;
			
			return getMostInner(remoteEvaluator);
		}
		else
			return evaluator;
	}


	/**
	 * Checking whether the specified evaluator requires pull mode connection.
	 * @param evaluator specified evaluator.
	 * @return whether the specified evaluator requires pull mode connection.
	 */
	public static boolean isPullModeRequired(Evaluator evaluator) {
		try {
			if (!isRemote(evaluator)) return false;
			
			EvaluatorConfig config = evaluator.getConfig();
			return config.isPullModeRequired();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}

	
	/**
	 * Creating timer to purge listeners list.
	 * @param listenerList purged listeners list.
	 * @return timer to purge listeners list.
	 */
	public static Timer2 createPurgeListenersTimer(EventListenerList2 listenerList) {
		if (listenerList == null) return null;
		
		Timer2 timer = new Timer2(Constants.DEFAULT_LONG_TIMEOUT*1000) {
			
			@Override
			protected void task() {
				try {
					purgeListeners(listenerList);
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			
			@Override
			protected void clear() {}
			
		};
		timer.setPriority(Priority.min);
		
		return timer;
	}
	
	
	/**
	 * Purging listeners list.
	 * @param listenerList purged listeners list.
	 */
	public static void purgeListeners(EventListenerList2 listenerList) {
		if (listenerList == null) return;
		synchronized (listenerList) {
			listenerList.updateInfo();
			
			List<EventListener> listeners = listenerList.getListeners();
			List<EventListener> tempListeners = Util.newList(listeners.size());
			tempListeners.addAll(listeners);
			for (EventListener listener : tempListeners) {
				if (!listeners.contains(listener))
					continue;
				
				ListenerInfo info = listenerList.getInfo(listener);
				if (info != null && info.failedPingCount > 2) { //Removing clients that are unable to connect more than 2 times (more than 1 hour in average).
					listenerList.remove(listener);
					if (listeners.size() == 0) break;
				}
			}
		}
		
	}
	
	
	/**
	 * Getting algorithm description from evaluator.
	 * @param evaluator specified evaluator.
	 * @param alg specified algorithm.
	 * @return algorithm description from evaluator.
	 */
	public static AlgDesc2 getPluginAlgDesc(Evaluator evaluator, Alg alg) {
		if (evaluator == null || alg == null) return null;
		
		AlgDesc2 algDesc = null;
		Class<? extends Alg> algClass = null;
		String algName = null;
		try {
			algClass = alg.getClass();
			algName = alg.getName();
			algDesc = evaluator.getPluginAlgDesc(algClass, algName);
		}
		catch (RemoteException e) {
			algDesc = null;
			try {
				algDesc = evaluator.getPluginAlgDesc(algClass.getName(), algName);
			}
			catch (Exception e1) {
				LogUtil.error("Error when evaluator gets plug-in algorithm description, caused by " + e1.getMessage());
			}
		}
		catch (Exception e) {
			algDesc = null;
			LogUtil.error("Error when evaluator gets plug-in algorithm description, caused by " + e.getMessage());
		}
		
		return algDesc;
	}
	
	
	/**
	 * Create version name of evaluator.
	 * @param evaluatorName evaluator name.
	 * @param version version name.
	 * @return version name of evaluator.
	 */
	public static String createVersionName(String evaluatorName, String version) {
		if (evaluatorName == null)
			return null;
		else if (version == null || version.isEmpty())
			return evaluatorName;
		else
			return evaluatorName + "-" + version;
	}
	
	
	/**
	 * Extracting name from version name.
	 * @param evaluatorVersionName version name of evaluator.
	 * @return name of evaluator.
	 */
	public static String extractName(String evaluatorVersionName) {
		if (evaluatorVersionName == null) return null;
		int lastIdx = evaluatorVersionName.lastIndexOf("-");
		if (lastIdx < 0)
			return null;
		else
			return evaluatorVersionName.substring(0, lastIdx);
	}
	
	
	/**
	 * Extracting name from version name.
	 * @param evaluatorVersionName version name of evaluator.
	 * @return name of evaluator.
	 */
	public static String extractVersion(String evaluatorVersionName) {
		if (evaluatorVersionName == null) return null;
		int lastIdx = evaluatorVersionName.lastIndexOf("-");
		if (lastIdx < 0 || lastIdx == evaluatorVersionName.length() - 1)
			return null;
		else
			return evaluatorVersionName.substring(lastIdx + 1);
	}


	/**
	 * Getting status text of specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return status text of specified evaluator.
	 */
	public static String getStatusText(Evaluator evaluator) {
		try {
			if (!evaluator.remoteIsStarted())
				return "stopped";
			else if (evaluator.remoteIsRunning())
				return "running...";
			else
				return "paused";
		}
		catch (Exception e) {LogUtil.trace(e);}

		return "unknown";
	}

	
}
