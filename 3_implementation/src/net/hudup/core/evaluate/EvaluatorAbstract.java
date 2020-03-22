/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
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
import java.util.List;

import javax.swing.event.EventListenerList;

import net.hudup.core.Constants;
import net.hudup.core.PluginStorage;
import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.RegisterTable.AlgFilter;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPairExchanged;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.DatasetRemoteWrapper;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.EvaluateEvent.Type;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.TaskQueue;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;

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
    protected EventListenerList listenerList = new EventListenerList();

    
	/**
     * The list of original metrics used to evaluate algorithms in {@link #evAlgList}.
     */
	protected NoneWrapperMetricList metricList = null;

	
    /**
     * Exported stub as remote evaluator. It must be serializable.
     */
    protected Evaluator exportedStub = null;

	
    /**
     * List of algorithms that are evaluated by this evaluator.
     */
    protected List<Alg> evAlgList = null;
    
    
    /**
     * This {@code dataset pool} contains many training and testing datasets, which is fed to evaluator, which allows evaluator assesses algorithm on many testing datasets.
     */
    protected DatasetPool evPool = null;
    
    
    /**
     * Additional parameter for this evaluator.
     */
    protected Serializable evParameter = null;
    
    
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
     * Internal task queue.
     */
    protected TaskQueue taskQueue = new TaskQueue();
    
    
	/**
	 * Internal counter.
	 */
	protected Counter counter = null;

	
    /**
     * Evaluation processor.
     */
    protected EvaluateProcessor evProcessor = null;
    
    
	/**
	 * Default constructor.
	 */
	public EvaluatorAbstract() {
		try {
			String evalconfigPath = Constants.WORKING_DIRECTORY + "/" +
					(EvaluatorConfig.EVALCONFIG_FILENAME_PREFIX + getName()).replaceAll("\\s", "") + "." +
					EvaluatorConfig.EVALCONFIG_FILEEXT;
			this.config = new EvaluatorConfig(xURI.create(evalconfigPath));
		}
		catch (Exception e) {
			e.printStackTrace();
			this.config = new EvaluatorConfig(xURI.create(EvaluatorConfig.EVALCONFIG_FILEPATH_DEFAULT));
		}
		
		try {
			this.metricList = defaultMetrics();
			this.metricList.syncWithPlugin();
			this.metricList.sort();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		this.evProcessor = new EvaluateProcessor(this);
		this.counter = new Counter(otherResult);
	}
	
	
	@Override
	public synchronized boolean remoteStart0(List<Alg> algList, DatasetPoolExchanged pool, Serializable parameter) throws RemoteException {
		// TODO Auto-generated method stub
		if (isStarted() || this.evAlgList != null || this.evPool != null) {
			LogUtil.error("Evaluator is running and so evaluation is not run");
			return false;
		}
		
		if (algList == null || algList.size() == 0 || pool == null || pool.dspList.size() == 0) {
			LogUtil.error("Empty algorithm list of empty dataset pool");
			return false;
		}

		this.evPool = updatePoolResult(pool);
		if (this.evPool == null) {
			LogUtil.error("Cannot create pool");
			return false;
		}
		
		try {
			clearDelayUnsetupAlgs(); //This code line is important.
		} catch (Throwable e) {e.printStackTrace();} 
		
		this.evAlgList = algList;
		this.algRegResult = this.evAlgList != null ? new RegisterTable(this.evAlgList) : null;
		
		this.result = null;
		this.evParameter = parameter;
		
		String evStorePath = this.otherResult.evStorePath;
		this.otherResult.reset();
		this.otherResult.evStorePath = evStorePath;
		this.otherResult.algNames = Util.newList(this.evAlgList.size());
		for (Alg alg : this.evAlgList) {
			this.otherResult.algNames.add(alg.getName());
		}
		this.otherResult.algName = this.otherResult.algNames.get(0);
		
		if (!start()) {
			clear();
			return false;
		}
		
		this.counter.stop();
		this.counter.start();
		this.taskQueue.stop();
		this.taskQueue.start();

		Timestamp timestamp = null;
		if (parameter != null && parameter instanceof Timestamp)
			timestamp = (Timestamp)parameter;
		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.start,
				this.otherResult,
				this.poolResult,
				timestamp));
		
		return true;
	}

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			run0();
		}
		catch (Throwable e) {
			e.printStackTrace();
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		otherResult.progressTotal *= evAlgList.size();
		
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; current == thread && evAlgList != null && i < evAlgList.size(); i++) {
			Alg alg = evAlgList.get(i);
			otherResult.algName = alg.getName();
			
			for (int j = 0; current == thread && evPool != null && j < evPool.size(); j++) {
				
				Fetcher<Profile> testingFetcher = null;
				try {
					DatasetPair dsPair = evPool.get(j);
					Dataset     training = dsPair.getTraining();
					Dataset     testing = dsPair.getTesting();
					int         datasetId = j + 1;
					xURI        datasetUri = testing.getConfig() != null ? testing.getConfig().getUriId() : null;
					otherResult.datasetId = datasetId;
					
					// Adding default metrics to metric result. Pay attention to cloning metrics list.
					result.add( alg.getName(), datasetId, datasetUri, ((NoneWrapperMetricList)metricList.clone()).sort().list() );
					
					otherResult.inAlgSetup = true;
					if (alg instanceof AlgRemote) {
						((AlgRemote)alg).addSetupListener(this);
						SetupAlgEvent setupEvt = new SetupAlgEvent(alg, SetupAlgEvent.Type.doing, alg.getName(), null, "not supported yet");
						fireSetupAlgEvent(setupEvt);
					}
					
					long beginSetupTime = System.currentTimeMillis();
					// 
					setupAlg(alg, training);
					//
					long endSetupTime = System.currentTimeMillis();
					long setupElapsed = endSetupTime - beginSetupTime;
					Metrics setupMetrics = result.recalc(
							alg, 
							datasetId, 
							SetupTimeMetric.class, 
							new Object[] { setupElapsed / 1000.0f }
						); // calculating setup time metric
					fireEvaluateEvent(new EvaluateEvent(this, Type.doing, setupMetrics)); // firing setup time metric
					
					if (alg instanceof AlgRemote) {
						SetupAlgEvent setupEvt = new SetupAlgEvent(alg, SetupAlgEvent.Type.done, alg.getName(), null, "not supported yet");
						fireSetupAlgEvent(setupEvt);
						((AlgRemote)alg).removeSetupListener(this);
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
						progressEvt.setAlgName(alg.getName());
						progressEvt.setDatasetId(datasetId);
						progressEvt.setCurrentCount(otherResult.vCurrentCount);
						progressEvt.setCurrentTotal(otherResult.vCurrentTotal);
						fireEvaluateProgressEvent(progressEvt);
						
						Profile testingProfile = testingFetcher.pick();
						if (testingProfile == null)
							continue;
						
						Profile param = prepareExecuteAlg(alg, testingProfile);
						//
						long beginRecommendTime = System.currentTimeMillis();
						Serializable executedResult = executeAlg(alg, param);
						long endRecommendTime = System.currentTimeMillis();
						//
						long recommendElapsed = endRecommendTime - beginRecommendTime;
						Metrics speedMetrics = result.recalc(
								alg, 
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
									alg, 
									datasetId,
									new Object[] { executedResult, extractTestValue(alg, testingProfile) }
								); // calculating execution metric
							
							vExecutedCount++;
							
							fireEvaluateEvent(new EvaluateEvent(
									this, 
									Type.doing, 
									executedMetrics, 
									executedResult, 
									extractTestValue(alg, testingProfile))); // firing execution metric
						}
						
						
						synchronized (this) {
							while (paused) {
								notifyAll();
								wait();
							}
						}
						
					} // User id iterate
					
					Metrics hudupRecallMetrics = result.recalc(
							alg, 
							datasetId, 
							HudupRecallMetric.class, 
							new Object[] { new FractionMetricValue(vExecutedCount, otherResult.vCurrentTotal) }
						);
					fireEvaluateEvent(new EvaluateEvent(this, Type.doing, hudupRecallMetrics));
					
					Metrics doneOneMetrics = result.gets(alg.getName(), datasetId);
					fireEvaluateEvent(new EvaluateEvent(this, Type.done_one, doneOneMetrics));
					
				} // end try
				catch (Throwable e) {
					e.printStackTrace();
				}
				finally {
					try {
						if (testingFetcher != null)
							testingFetcher.close();
						testingFetcher = null;
					} catch (Throwable e) {e.printStackTrace();}
					
					try {
						unsetupAlgSupportDelay(alg);
					} catch (Throwable e) {e.printStackTrace();}
				}
				
				SystemUtil.enhanceAuto();

			} // dataset iterate
			
		} // algorithm iterate
		
		
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
			} catch (Throwable e) {e.printStackTrace();}
		}
		else {
			synchronized (delayUnsetupAlgs) {
				delayUnsetupAlgs.add(alg);
			}
		}
	}
	
	
	@Override
	public synchronized void clearDelayUnsetupAlgs() throws RemoteException {
		synchronized (delayUnsetupAlgs) {
			for (Alg alg : delayUnsetupAlgs) {
				try {
					unsetupAlg(alg);
				} catch (Throwable e) {e.printStackTrace();}
			}
			
			delayUnsetupAlgs.clear();
		}
	}

	
	/**
	 * Fetching profiles from the specified testing dataset.
	 * @param testing specified training dataset.
	 * @return fetcher for retrieving profiles from the specified testing dataset as {@link Fetcher}.
	 */
	protected abstract Fetcher<Profile> fetchTesting(Dataset testing);
	
	
	@Override
	public DatasetPoolExchanged getDatasetPool() throws RemoteException {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		fireSetupAlgEvent(evt);
	}
	
	
	@Override
	public String getMainUnit() throws RemoteException {
		return DataConfig.RATING_UNIT;
	}
	
	
	@Override
	public Metrics getResult() throws RemoteException {
		return result;
	}

	
	@Override
	public EvaluateInfo getOtherResult() throws RemoteException {
		// TODO Auto-generated method stub
		return otherResult;
	}


	@Override
	public List<String> getMetricNameList() throws RemoteException {
		synchronized(metricList) {
			return this.metricList.nameList();
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
		
		synchronized(this.metricList) {
			this.metricList.clear();
			this.metricList.addAll(newMetricList);
			this.metricList.sort();
		}
	}
	
	
	@Deprecated
	@Override
	public RegisterTable extractAlgFromPluginStorage0() throws RemoteException {
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
					// TODO Auto-generated catch block
					e.printStackTrace();
					return false;
				}
			}
		});
		
		return new RegisterTable(algList);
	}
	
	
	/**
	 * Extracting algorithms from plug-in storage so that such algorithms are accepted by the specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return register table to store algorithms extracted from plug-in storage.
	 */
	public static RegisterTable extractAlgFromPluginStorage(Evaluator evaluator) {
		List<Alg> algList = PluginStorage.getNormalAlgReg().getAlgList(new AlgFilter() {
			
			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean accept(Alg alg) {
				alg = AlgDesc2.wrapNewInstance(alg, false);
				if (alg == null) return false;
				
				try {
					return evaluator.acceptAlg(alg);
				} 
				catch (Throwable e) {
					e.printStackTrace();
					LogUtil.error("Evaluator does not accept algorithm '" + alg.getName() + "' due to " + e.getMessage());
					return false;
				}
			}
			
			
		});
		
		return new RegisterTable(algList);
	}
	
	
	@Deprecated
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		// TODO Auto-generated method stub
		return new PluginStorageWrapper();
	}


    @Override
	public List<String> getPluginAlgNames(Class<? extends Alg> algClass) throws RemoteException {
		// TODO Auto-generated method stub
    	RegisterTable algReg = PluginStorage.lookupTable(algClass);
    	List<String> algNames = Util.newList();
    	if (algReg == null) return algNames;
    	
    	List<Alg> algList = algReg.getAlgList();
    	for (Alg alg : algList) {
    		try {
    			if (acceptAlg(alg)) algNames.add(alg.getName());
    		} catch (Throwable e) {e.printStackTrace();}
    	}
    	
		return algNames;
	}


	@Override
	public AlgDesc2List getPluginAlgDescs(Class<? extends Alg> algClass) throws RemoteException {
		// TODO Auto-generated method stub
    	RegisterTable algReg = PluginStorage.lookupTable(algClass);
		AlgDesc2List algDescs = new AlgDesc2List();
    	if (algReg == null) return algDescs;
		
    	List<Alg> algList = algReg.getAlgList();
    	for (Alg alg : algList) {
    		try {
    			if (acceptAlg(alg))
    				algDescs.add(alg);
    		} catch (Throwable e) {e.printStackTrace();}
    	}
    	
		return algDescs;
	}


	@Override
	public Alg getPluginAlg(Class<? extends Alg> algClass, String algName, boolean remote) throws RemoteException {
		// TODO Auto-generated method stub
		return getAlg(PluginStorage.lookupTable(algClass), algName, remote);
	}


	@Override
	public Alg getEvaluatedAlg(String algName, boolean remote) throws RemoteException {
		// TODO Auto-generated method stub
		return getAlg(algRegResult, algName, remote);
	}


	/**
	 * Retrieving algorithm from register table.
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
			if (!acceptAlg(alg)) return null;
			
			if (!remote)
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
			e.printStackTrace();
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
		if (this.poolResult != null)
			this.poolResult.unexport(true);
		this.poolResult = returnedPool.toDatasetPoolExchanged();
		this.poolResult.export(config.getEvaluatorPort(), false);
		
		return returnedPool;
	}
	
	
	@Override
	public synchronized boolean updatePool(DatasetPoolExchanged pool, Timestamp timestamp) throws RemoteException {
		// TODO Auto-generated method stub
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
					timestamp));
			return true;
		}
		else
			return false;
	}


	@Override
	public boolean reloadPool() throws RemoteException {
		// TODO Auto-generated method stub
		if (poolResult == null) return false;
		
		this.poolResult.reload();
		this.poolResult.fillMissingUUID();
		this.poolResult.export(config.getEvaluatorPort(), false);
		
		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.update_pool,
				this.otherResult,
				this.poolResult));
		return true;
	}


	/*
	 * This method only clears parameters.
	 */
	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		this.evAlgList = null;
		this.evPool = null;
		this.evParameter = null;
		
		this.taskQueue.stop();
		this.counter.stop();
	}

	
	@Override
	protected void task() {
		// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (evAlgList != null) {
			synchronized (delayUnsetupAlgs) {
				for (Alg alg : evAlgList) {
					for (Alg delayUnsetupAlg : delayUnsetupAlgs) {
						if (!alg.getName().equals(delayUnsetupAlg.getName())) {
							try {
								unsetupAlg(alg);
							} catch (Throwable e) {e.printStackTrace();}
						}
					}
				}
			}
		}
		
		fireEvaluateEvent(new EvaluateEvent(this, Type.done, result));
		
		this.taskQueue.stop();
		this.counter.stop();
		
		return true;
	}
	
	
	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		return config;
	}
	
	
	@Override
	public void setConfig(EvaluatorConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		if (config == null) return;
		this.config.putAll(config);
	}


	@Override
	public boolean isWrapper() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void addEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluatorListener.class, listener);
		}
    }

    
	@Override
    public void removeEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluatorListener.class, listener);
		}
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
     */
    protected void fireEvaluatorEvent(EvaluatorEvent evt) {
		synchronized (listenerList) {
			
			EvaluatorListener[] listeners = getEvaluatorListeners();
			for (EvaluatorListener listener : listeners) {
				if (taskQueue.isRunning()) {
					taskQueue.addTask(new TaskQueue.Task() {
						
						@Override
						public void doTask() throws Exception {
							listener.receivedEvaluator(evt);
						}
						
					});
				}
				else {
					try {
						listener.receivedEvaluator(evt);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
    }

    
    @Override
	public void addEvaluateListener(EvaluateListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluateListener.class, listener);
		}
    }

    
	@Override
    public void removeEvaluateListener(EvaluateListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluateListener.class, listener);
		}
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
		synchronized (listenerList) {
			
			EvaluateListener[] listeners = getEvaluateListeners();
			for (EvaluateListener listener : listeners) {
				if (taskQueue.isRunning()) {
					taskQueue.addTask(new TaskQueue.Task() {
						
						@Override
						public void doTask() throws Exception {
							listener.receivedEvaluation(evt);
						}
						
					});
				}
				else {
					try {
						listener.receivedEvaluation(evt);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		
			if (otherResult.evStorePath != null && evAlgList != null) {
				boolean saveResultSummary = false;
				try {
					saveResultSummary = config.isSaveResultSummary();
				} catch (Throwable e) {e.printStackTrace();}
				
				evt.setMetrics(result); //Important code line, saving all metrics.
				evProcessor.saveEvaluateResult(otherResult.evStorePath, evt, evAlgList, saveResultSummary);
			}
			
			
			//Backing up evaluation results.
			boolean backup = isBackup() || (otherResult.evStorePath == null && listeners.length == 0);
			if (!backup) return;
			
			if (evt.getType() != Type.done && evt.getType() != Type.done_one)
				return;
			if (this.result == null || this.evAlgList == null)
				return;
			
			try {
				xURI backupDir = xURI.create(Constants.BACKUP_DIRECTORY);
				UriAdapter backupAdapter = new UriAdapter(backupDir);
				if (!backupAdapter.exists(backupDir)) backupAdapter.create(backupDir, true);
				xURI analyzeBackupFile = backupDir.concat("evaluator-analyze-backup-" + new Date().getTime() + "." + Constants.DEFAULT_EXT);
				
				MetricsUtil util = new MetricsUtil(this.result, new RegisterTable(this.evAlgList), this);
				Writer writer = backupAdapter.getWriter(analyzeBackupFile, false);
				writer.write(util.createPlainText());
				writer.close();
				backupAdapter.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
		}
    }

    
    @Override
	public void addEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluateProgressListener.class, listener);
		}
    }

    
    @Override
    public void removeEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluateProgressListener.class, listener);
		}
    }
	
    
    /**
     * Getting an array of evaluation progress listeners.
     * @return array of {@link ProgressListener} (s).
     */
    protected EvaluateProgressListener[] getEvaluateProgressListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluateProgressListener.class);
		}
    }
    
    
    /**
     * Firing {@link ProgressEvent}.
     * @param evt the specified for evaluation progress.
     */
    protected void fireEvaluateProgressEvent(EvaluateProgressEvent evt) {
		synchronized (listenerList) {
			
	    	EvaluateProgressListener[] listeners = getEvaluateProgressListeners();
			for (EvaluateProgressListener listener : listeners) {
				if (taskQueue.isRunning()) {
					taskQueue.addTask(new TaskQueue.Task() {
						
						@Override
						public void doTask() throws Exception {
							listener.receivedProgress(evt);
						}
						
					});
				}
				else {
					try {
						listener.receivedProgress(evt);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
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
    public void removeSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
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
		synchronized (listenerList) {
			
	    	SetupAlgListener[] listeners = getSetupAlgListeners();
			for (SetupAlgListener listener : listeners) {
				if (taskQueue.isRunning()) {
					taskQueue.addTask(new TaskQueue.Task() {
						
						@Override
						public void doTask() throws Exception {
							listener.receivedSetup(evt);
						}
						
					});
				}
				else {
					try {
						listener.receivedSetup(evt);
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		
			if (otherResult.evStorePath != null) {
				boolean saveResultSummary = false;
				try {
					saveResultSummary = config.isSaveResultSummary();
				} catch (Throwable e) {e.printStackTrace();}
	
				evProcessor.saveSetupResult(otherResult.evStorePath, evt, saveResultSummary);
			}
			
			
			//Backing up evaluation results.
			boolean backup = isBackup() || (otherResult.evStorePath == null && listeners.length == 0);
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
			catch (Throwable e) {
				e.printStackTrace();
			}
			
		}
    }

    
    @Override
	public void addElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		counter.addElapsedTimeListener(listener);
    }

    
    @Override
    public void removeElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		counter.removeElapsedTimeListener(listener);
    }

    
    @Override
	public void setEvaluateStorePath(String evStorePath) throws RemoteException {
		// TODO Auto-generated method stub
		this.otherResult.evStorePath = evStorePath;
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
			e.printStackTrace();
			backup = false;
		}
		return backup;
    }
    
    
	@Override
	public String toString() {
		// TODO Auto-generated method stub
    	String evaluatorName = "No name";
		try {
			evaluatorName = getName();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return DSUtil.shortenVerbalName(evaluatorName);
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}
	
	
	@Override
	public boolean isAgent() throws RemoteException {
		// TODO Auto-generated method stub
		return isAgent;
	}


	@Override
	public synchronized void setAgent(boolean isAgent) throws RemoteException {
		// TODO Auto-generated method stub
		this.isAgent = isAgent;
	}


	@Override
	public synchronized void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			stop();
		}
		catch (Throwable e) {e.printStackTrace();}

		try {
			clearDelayUnsetupAlgs();
		}
		catch (Throwable e) {e.printStackTrace();}
		
		try {
			if (poolResult != null)
				poolResult.clear(true);
			poolResult = null;

			if (evPool != null)
				evPool.clear();
			evPool = null;
		}
		catch (Throwable e) {e.printStackTrace();}

		try {
			otherResult.evStorePath = null;
			evProcessor.clear();
		}
		catch (Throwable e) {e.printStackTrace();}

		try {
			config.save();
		} catch (Throwable e) {e.printStackTrace();}

		try {
			unexport();
		}
		catch (Throwable e) {e.printStackTrace();}
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	@Override
	public synchronized boolean remoteStart(List<String> algNameList, DatasetPoolExchanged pool, Serializable parameter) throws RemoteException {
		RegisterTable algReg = PluginStorage.getNormalAlgReg();
		List<Alg> algList = Util.newList();
		for (String algName : algNameList) {
			Alg alg = algReg.query(algName);
			if (alg != null)
				algList.add(alg); //This code line is important to remote setting.
		}
		
		return remoteStart0(algList, pool, parameter);
	}
	
	
	@Override
	public synchronized boolean remoteStart(Serializable... parameters) throws RemoteException {
		// TODO Auto-generated method stub
		if (parameters == null || parameters.length < 2
				|| !(parameters[0] instanceof List<?>)
				|| !(parameters[1] instanceof DatasetPoolExchanged))
			return false;
		
		@SuppressWarnings("unchecked")
		List<String> algNameList = (List<String>)(parameters[0]);
		DatasetPoolExchanged pool = (DatasetPoolExchanged)(parameters[1]);
		Serializable parameter = parameters.length > 2? parameters[2] : null;
		
		return remoteStart(algNameList, pool, parameter);
	}


	@Override
	public synchronized boolean remotePause() throws RemoteException {
		// TODO Auto-generated method stub
		if (!pause()) return false;

		counter.pause();
		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.pause)); // firing paused event.
		
		return true;
	}

	
	@Override
	public synchronized boolean remoteResume() throws RemoteException {
		// TODO Auto-generated method stub
		if (!resume()) return false;

		counter.resume();
		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.resume)); // firing resume event.
		
		return true;
	}

	
	@Override
	public synchronized boolean remoteStop() throws RemoteException {
		// TODO Auto-generated method stub
		if (!stop()) return false;

		fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				EvaluatorEvent.Type.stop)); // firing stop event.
		
		this.taskQueue.stop();
		this.counter.stop();
		
		return true;
	}

	
	@Override
	public boolean remoteForceStop() throws RemoteException {
		// TODO Auto-generated method stub
		return forceStop();
	}

	
	@Override
	public boolean remoteIsStarted() throws RemoteException {
		// TODO Auto-generated method stub
		return isStarted();
	}

	
	@Override
	public boolean remoteIsPaused() throws RemoteException {
		// TODO Auto-generated method stub
		return isPaused();
	}

	
	@Override
	public boolean remoteIsRunning() throws RemoteException {
		// TODO Auto-generated method stub
		return isRunning();
	}


//	@Override
//	public Object ping(Object o) throws RemoteException {
//		// TODO Auto-generated method stub
//		return "Ping sucessful: " + o.toString() + " " + o.toString();
//	}
			

}
