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
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.SystemUtil;
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
	 * Configuration of this evaluator.
	 */
	protected EvaluatorConfig config = null;

	
	/**
	 * Holding a list of {@link EventListener} (s)
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
     * List of algorithms that are evaluated by this evaluator.
     */
    protected List<Alg> algList = null;
    
    
    /**
     * List of unsetup algorithms that are evaluated by this evaluator. This list is not important.
     */
    protected List<Alg> delayUnsetupAlgs = Util.newList();

    
    /**
     * This {@code dataset pool} contains many training and testing datasets, which is fed to evaluator, which allows evaluator assesses algorithm on many testing datasets.
     */
    protected DatasetPool pool = null;
    
    
    /**
     * Additional parameter for this evaluator.
     */
    protected Serializable parameter = null;
    
    
	/**
     * The list of metrics resulted from the evaluation process.
     */
	protected volatile Metrics result = null;
	
	
	/**
	 * Information of resulted metrics.
	 */
	protected EvaluateInfo otherResult = new EvaluateInfo();
	
	
	/**
	 * Resulted dataset pool.
	 */
	protected DatasetPool poolResult = null;

	
	/**
     * The list of original metrics used to evaluate algorithms in {@link #algList}.
     */
	protected NoneWrapperMetricList metricList = null;

	
    /**
     * Exported stub as remote evaluator. It must be serializable.
     */
    protected Evaluator exportedStub = null;

	
    /**
     * Evaluation processor.
     */
    protected EvaluateProcessor evProcessor = new EvaluateProcessor();
    
    
	/**
	 * Internal counter.
	 */
	protected Counter counter = null;

	
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
			this.metricList.sort();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		this.counter = new Counter(otherResult);
	}
	
	
	/**
	 * Starting the evaluation process on specified algorithms with specified dataset pool.
	 * The original (built-in) metrics were discovered by Plug-in manager.
	 * 
	 * @param algList specified list of algorithms. It must be serializable in remote call.
	 * @param pool specified dataset pool containing many training datasets and testing datasets. It must be serializable in remote call.
	 * @param parameter additional parameter.
	 */
	public synchronized void evaluate(List<Alg> algList, DatasetPool pool, Serializable parameter) {
		if (isStarted() || this.algList != null || this.pool != null) {
			LogUtil.error("Evaluator is running and so evaluation is not run");
			return;
		}
		
		try {
			clearDelayUnsetupAlgs(); //This code line is important.
		} catch (Throwable e) {e.printStackTrace();} 
		
		this.algList = algList;
		this.pool = pool;
		this.parameter = parameter;
		this.result = null;
		
		this.otherResult.reset();
		if (algList != null) {
			this.otherResult.algNames = Util.newList(algList.size());
			for (Alg alg : algList) {
				this.otherResult.algNames.add(alg.getName());
			}
		}
		
		this.poolResult = pool;
		
		try {
			this.counter.stop();
			this.counter.start();
		} catch (Throwable e) {e.printStackTrace();}
		
		if ((parameter != null) && (parameter instanceof Timestamp) && ((Timestamp)parameter).isValid()) {
			fireEvaluatorEvent(new EvaluatorEvent(
				this, 
				Type.setup, 
				(Timestamp)parameter)); // firing setting up event with time stamp.
		}

		start();
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
		for (int i = 0; i < pool.size(); i++) {
			Dataset testing = pool.get(i).getTesting();
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
		otherResult.progressTotal *= algList.size();
		
		result = new Metrics();
		
		Thread current = Thread.currentThread();
		for (int i = 0; current == thread && algList != null && i < algList.size(); i++) {
			Alg alg = algList.get(i);
			otherResult.algName = alg.getName();
			
			for (int j = 0; current == thread && pool != null && j < pool.size(); j++) {
				
				Fetcher<Profile> testingFetcher = null;
				try {
					DatasetPair dsPair = pool.get(j);
					Dataset     training = dsPair.getTraining();
					Dataset     testing = dsPair.getTesting();
					int         datasetId = j + 1;
					xURI        datasetUri = testing.getConfig() != null ? testing.getConfig().getUriId() : null;
					otherResult.datasetId = datasetId;
					
					// Adding default metrics to metric result. Pay attention to cloning metrics list.
					result.add( alg.getName(), datasetId, datasetUri, ((NoneWrapperMetricList)metricList.clone()).sort().list() );
					
					otherResult.inSetup = true;
					if (alg instanceof AlgRemote) {
						((AlgRemote)alg).addSetupListener(this);
						SetupAlgEvent setupEvt = new SetupAlgEvent(new Integer(-1), SetupAlgEvent.Type.doing, alg, null, "not supported yet");
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
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, setupMetrics)); // firing setup time metric
					
					if (alg instanceof AlgRemote) {
						SetupAlgEvent setupEvt = new SetupAlgEvent(new Integer(1), SetupAlgEvent.Type.done, alg, null, "not supported yet");
						fireSetupAlgEvent(setupEvt);
						((AlgRemote)alg).removeSetupListener(this);
					}
					otherResult.inSetup = false;
					
					//Auto enhancement after setting up algorithm.
					SystemUtil.enhanceAuto();

					testingFetcher = fetchTesting(testing);
					otherResult.vCurrentTotal = testingFetcher.getMetadata().getSize();
					otherResult.vCurrentCount = 0;
					int vExecutedCount = 0;
					while (current == thread && testingFetcher.next()) {
						otherResult.progressStep++;
						otherResult.vCurrentCount++;
						EvaluatorProgressEvent progressEvt = new EvaluatorProgressEvent(this, otherResult.progressTotal, otherResult.progressStep);
						progressEvt.setAlgName(alg.getName());
						progressEvt.setDatasetId(datasetId);
						progressEvt.setCurrentCount(otherResult.vCurrentCount);
						progressEvt.setCurrentTotal(otherResult.vCurrentTotal);
						fireProgressEvent(progressEvt);
						
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
						fireEvaluatorEvent(new EvaluatorEvent(
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
							
							fireEvaluatorEvent(new EvaluatorEvent(
									this, 
									Type.doing, 
									executedMetrics, 
									executedResult, 
									extractTestValue(alg, testingProfile))); // firing execution metric
						}
						
						
						counter.pause();
						synchronized (this) {
							while (paused) {
								notifyAll();
								wait();
							}
						}
						counter.resume();
						
					} // User id iterate
					
					Metrics hudupRecallMetrics = result.recalc(
							alg, 
							datasetId, 
							HudupRecallMetric.class, 
							new Object[] { new FractionMetricValue(vExecutedCount, otherResult.vCurrentTotal) }
						);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.doing, hudupRecallMetrics));
					
					Metrics doneOneMetrics = result.gets(alg.getName(), datasetId);
					fireEvaluatorEvent(new EvaluatorEvent(this, Type.done_one, doneOneMetrics));
					
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
			
			fireEvaluatorEvent(new EvaluatorEvent(this, Type.done, result));
			
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
	
	
	@NextUpdate
	@Override
	public DatasetPool getDatasetPool() throws RemoteException {
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
	public List<Metric> getMetricList() throws RemoteException {
		synchronized(metricList) {
			return this.metricList.list();
		}
	}

	
	@Override
	public synchronized void setMetricList(List<Metric> metricList) throws RemoteException {
		if (isStarted()) {
			LogUtil.error("Evaluator is started and so it is impossible to set up metric list");
			return;
		}
		
		synchronized(metricList) {
			this.metricList.clear();
			this.metricList.addAll(metricList);
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
	protected void clear() {
		// TODO Auto-generated method stub
		this.algList = null;
		this.pool = null;
		this.parameter = null;
		
		try {
			this.counter.stop();
		} catch (Throwable e) {e.printStackTrace();}
	}

	
	@Override
	protected void task() {
		// TODO Auto-generated method stub
		LogUtil.info("Evaluator#task not used because overriding Evaluator#run");
	}

	
	@SuppressWarnings("static-access")
	@Override
	public synchronized void forceStop() {
		super.forceStop();
		
		try {
			Thread.currentThread().sleep(1000);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		fireEvaluatorEvent(new EvaluatorEvent(this, Type.done, result));
	}
	
	
	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		return config;
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
     * Return a {@link EvaluatorListener} list for this evaluator.
     * 
     * @return array of {@link EvaluatorListener} for this evaluator.
     * 
     */
    protected EvaluatorListener[] getEvaluatorListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluatorListener.class);
		}
    }

    
    /**
     * Firing (issuing) an event from this evaluator to all listeners. 
     * 
     * @param evt event from this evaluator.
     */
    protected void fireEvaluatorEvent(EvaluatorEvent evt) {
		EvaluatorListener[] listeners = getEvaluatorListeners();
		for (EvaluatorListener listener : listeners) {
			try {
				listener.receivedEvaluation(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		if (otherResult.evStorePath != null && algList != null) {
			boolean fastsave = false;
			try {
				fastsave = config.isSaveResultSummary();
			} catch (Throwable e) {e.printStackTrace();}
			
			evProcessor.saveEvaluateResult(otherResult.evStorePath, evt, algList, fastsave);
		}
		
		
		//Backing up evaluation results.
		boolean backup = isBackup() || (otherResult.evStorePath == null && listeners.length == 0);
		if (!backup) return;
		
		if (evt.getType() != Type.done && evt.getType() != Type.done_one)
			return;
		if (this.result == null || this.algList == null)
			return;
		
		try {
			xURI backupDir = xURI.create(Constants.BACKUP_DIRECTORY);
			UriAdapter backupAdapter = new UriAdapter(backupDir);
			if (!backupAdapter.exists(backupDir)) backupAdapter.create(backupDir, true);
			xURI analyzeBackupFile = backupDir.concat("evaluator-analyze-backup-" + new Date().getTime() + "." + Constants.DEFAULT_EXT);
			
			MetricsUtil util = new MetricsUtil(this.result, new RegisterTable(this.algList));
			Writer writer = backupAdapter.getWriter(analyzeBackupFile, false);
			writer.write(util.createPlainText());
			writer.close();
			backupAdapter.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
    }

    
    @Override
	public void addProgressListener(EvaluatorProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluatorProgressListener.class, listener);
		}
    }

    
    @Override
    public void removeProgressListener(EvaluatorProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluatorProgressListener.class, listener);
		}
    }
	
    
    /**
     * Getting an array of evaluation progress listeners.
     * @return array of {@link ProgressListener} (s).
     */
    protected EvaluatorProgressListener[] getProgressListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluatorProgressListener.class);
		}
    }
    
    
    /**
     * Firing {@link ProgressEvent}.
     * @param evt the specified for evaluation progress.
     */
    protected void fireProgressEvent(EvaluatorProgressEvent evt) {
    	if (!isStarted()) return;

    	EvaluatorProgressListener[] listeners = getProgressListeners();
		for (EvaluatorProgressListener listener : listeners) {
			try {
				listener.receivedProgress(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
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
    	if (!isStarted()) return;

    	SetupAlgListener[] listeners = getSetupAlgListeners();
		for (SetupAlgListener listener : listeners) {
			try {
				listener.receivedSetup(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
		if (otherResult.evStorePath != null) {
			boolean fastsave = false;
			try {
				fastsave = config.isSaveResultSummary();
			} catch (Throwable e) {e.printStackTrace();}

			evProcessor.saveSetupResult(otherResult.evStorePath, evt, evt.getAlg().getName(), fastsave);
		}
		
		
		//Backing up evaluation results.
		boolean backup = isBackup() || (otherResult.evStorePath == null && listeners.length == 0);
		if (!backup || evt.getType() != SetupAlgEvent.Type.done)
			return;
		try {
			String info = "========== Algorithm \"" + evt.getAlg().getName() + "\" ==========\n";
			info = info + evt.translate() + "\n\n\n\n";

			xURI backupDir = xURI.create(Constants.BACKUP_DIRECTORY);
			UriAdapter backupAdapter = new UriAdapter(backupDir);
			if (!backupAdapter.exists(backupDir)) backupAdapter.create(backupDir, true);
			xURI analyzeBackupFile = backupDir.concat(
					"evaluator-" + evt.getAlg().getName() + "-backup-" + new Date().getTime() +
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
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}
	
	
	@Override
	public boolean isAgent() throws RemoteException {
		// TODO Auto-generated method stub
		return config.isAgent();
	}


	@Override
	public void setAgent(boolean agent) throws RemoteException {
		// TODO Auto-generated method stub
		config.setAgent(agent);
	}


	@Override
	public synchronized void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			stop();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			clearDelayUnsetupAlgs();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		try {
			otherResult.evStorePath = null;
			evProcessor.clear();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			unexport();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
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
	public void remoteStart(Serializable... parameters) throws RemoteException {
		// TODO Auto-generated method stub
		if (parameters == null || parameters.length < 2
				|| !(parameters[0] instanceof List<?>)
				|| !(parameters[1] instanceof DatasetPool))
			return;
		
		@SuppressWarnings("unchecked")
		List<Alg> algList = (List<Alg>)(parameters[0]);
		DatasetPool pool = (DatasetPool)(parameters[1]);
		Serializable parameter = parameters.length > 2? parameters[2] : null;
		
		evaluate(algList, pool, parameter);
	}


	@Override
	public void remoteStart(List<Alg> algList, DatasetPool pool, Serializable parameter) throws RemoteException {
		// TODO Auto-generated method stub
		evaluate(algList, pool, parameter);
	}

	
	@Override
	public void remotePause() throws RemoteException {
		// TODO Auto-generated method stub
		pause();
	}

	
	@Override
	public void remoteResume() throws RemoteException {
		// TODO Auto-generated method stub
		resume();
	}

	
	@Override
	public void remoteStop() throws RemoteException {
		// TODO Auto-generated method stub
		stop();
	}

	
	@Override
	public void remoteForceStop() throws RemoteException {
		// TODO Auto-generated method stub
		forceStop();
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
