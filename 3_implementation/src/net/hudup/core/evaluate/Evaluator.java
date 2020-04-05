/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.client.Service;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.Exportable;
import net.hudup.core.logistic.AgentSupport;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.RemoteRunner;
import net.hudup.core.logistic.Timestamp;

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
public interface Evaluator extends Remote, RemoteRunner, SetupAlgListener, PluginChangedListener, Exportable, AgentSupport, AutoCloseable {

	
	/**
	 * Starting the evaluation process on specified algorithms with specified dataset pool.
	 * @param algList specified list of algorithms.
	 * @param pool specified dataset pool containing many training datasets and testing datasets. It must be serializable in remote call.
	 * @param parameter additional parameter.
	 * @return true if successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteStart0(List<Alg> algList, DatasetPoolExchanged pool, Serializable parameter) throws RemoteException;

	
	/**
	 * Starting the evaluation process on specified algorithms with specified dataset pool.
	 * @param algNameList specified list of algorithm names.
	 * @param pool specified dataset pool containing many training datasets and testing datasets. It must be serializable in remote call.
	 * @param cp class processor. This parameter can be null.
	 * @param config configuration. This parameter can be null.
	 * @param parameter additional parameter.
	 * @return true if successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteStart(List<String> algNameList, DatasetPoolExchanged pool, ClassProcessor cp, DataConfig config, Serializable parameter) throws RemoteException;

	
	/**
	 * Evaluator forces to stop.
	 * @return true if successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteForceStop() throws RemoteException;

	
	/**
	 * Checking whether evaluator started.
	 * @return true if evaluator started.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteIsStarted() throws RemoteException;
	
	
	/**
	 * Checking whether evaluator paused.
	 * @return true if evaluator paused.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteIsPaused() throws RemoteException;

	
	/**
	 * Checking whether evaluator is running.
	 * @return true if evaluator is running.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteIsRunning() throws RemoteException;

	
	/**
	 * Returning name of this evaluator.
	 * @return name of this evaluator.
	 * @throws RemoteException if any error raises.
	 */
	String getName() throws RemoteException;
	
	
	/**
	 * Getting configuration of this evaluator.
	 * @return configuration of this evaluator.
	 * @throws RemoteException if any error raises.
	 */
	EvaluatorConfig getConfig() throws RemoteException;

	
	/**
	 * Setting configuration of this evaluator.
	 * @param config specified configuration. 
	 * @throws RemoteException if any error raises.
	 */
	void setConfig(EvaluatorConfig config) throws RemoteException;

	
	/**
	 * Checking whether this object is a wrapper of an evaluator.
	 * @return whether this object is a wrapper of an evaluator.
	 * @throws RemoteException if any error raises.
	 */
	boolean isWrapper() throws RemoteException;
	
	
	/**
	 * Checking whether the specified algorithm is accepted by this evaluator.
	 * @param alg specified algorithm.
	 * @return whether the specified algorithm is accepted by this evaluator.
	 * @throws RemoteException if any error raises.
	 */
	boolean acceptAlg(Alg alg) throws RemoteException;

	
	/**
	 * Defining the list of default metrics. This method is used only locally.
	 * @return the list of default metrics as {@link NoneWrapperMetricList}.
	 * @throws RemoteException if any error raises.
	 */
	NoneWrapperMetricList defaultMetrics() throws RemoteException;
	
	
	/**
	 * Getting main data unit for evaluation such as rating matrix, sample.
	 * @return main data unit for evaluation such as rating matrix, sample.
	 * @throws RemoteException if any error raises.
	 */
	String getMainUnit() throws RemoteException;

		
	/**
	 * Getting result of evaluation process as list of metrics.
	 * @return result of evaluation process as {@link Metrics}.
	 * @throws RemoteException if any error raises.
	 */
	Metrics getResult() throws RemoteException;
	
	
	/**
	 * Getting other result of evaluation process as the evaluation information.
	 * @return other result of evaluation process as the evaluation information.
	 * @throws RemoteException if any error raises.
	 */
	EvaluateInfo getOtherResult() throws RemoteException;

	
	/**
	 * Getting the metric name list resulted from the evaluation process.
	 * @return metric name list resulted from the evaluation process.
	 * @throws RemoteException if any error raises.
	 */
	List<String> getMetricNameList() throws RemoteException;

	
	/**
	 * Setting metric name list. Current implementation does not export metrics. Exporting normal algorithms only.
	 * @param metricNameList specified metric name list.
	 * @throws RemoteException if any error raises.
	 */
	void setMetricNameList(List<String> metricNameList) throws RemoteException;
	
	
	/**
	 * Extracting algorithms from plug-in storage so that such algorithms are accepted by this evaluator.
	 * This method is deprecated because of RMI class loading.
	 * @return register table to store algorithms extracted from plug-in storage.
	 * @throws RemoteException if any error raises.
	 */
	@Deprecated
	RegisterTable extractNormalAlgFromPluginStorage0() throws RemoteException;

	
//	/**
//	 * Clearing delay unsetting up algorithms.
//	 * @throws RemoteException if any error raises.
//	 */
//	void clearDelayUnsetupAlgs() throws RemoteException;

	
//	/**
//	 * Clearing all evaluated results.
//	 * @param timestamp time stamp.
//	 * @throws RemoteException if any error raises.
//	 */
//    void clearResult(Timestamp timestamp) throws RemoteException;

    
	/**
	 * Getting dataset pool. This method needs to be improved with large dataset pool and scanner.
	 * @return dataset pool.
	 * @throws RemoteException if any error raises.
	 */
	DatasetPoolExchanged getDatasetPool() throws RemoteException;

	
    /**
	 * Getting system plug-in storage. This method is currently not used.
	 * @return wrapper of system plug-in storage.
	 * @throws RemoteException if any error raises.
	 */
	@Deprecated
	PluginStorageWrapper getPluginStorage() throws RemoteException;
	
	
    /**
     * Getting name list of registered plug-in algorithms.
     * @param algClass specified algorithm class.
     * @return name list of registered plug-in algorithms.
     * @throws RemoteException if any error raises.
     */
    List<String> getPluginAlgNames(Class<? extends Alg> algClass) throws RemoteException;

    
    /**
     * Getting description list of registered plug-in algorithms.
     * @param algClass specified algorithm class.
     * @return description list of registered plug-in algorithms.
     * @throws RemoteException if any error raises.
     */
    AlgDesc2List getPluginAlgDescs(Class<? extends Alg> algClass) throws RemoteException;
    
    
    /**
     * Getting registered cloned plug-in algorithm.
     * @param algClass specified algorithm class.
     * @param algName algorithm name.
     * @param remote true if getting remotely.
     * @return registered plug-in algorithm. It is exported if remote parameter is true.
     * @throws RemoteException if any error raises.
     */
    Alg getPluginAlg(Class<? extends Alg> algClass, String algName, boolean remote) throws RemoteException;

    
    /**
     * Getting evaluated algorithm.
     * @param algName algorithm name.
     * @param remote true if getting remotely.
     * @return evaluated algorithm. It is exported if remote parameter is true.
     * @throws RemoteException if any error raises.
     */
    Alg getEvaluatedAlg(String algName, boolean remote) throws RemoteException;

    
    /**
     * Updating resulted dataset pool.
     * @param pool specified dataset pool.
     * @param localTargetListener local target listener. It can be null.
     * @param timestamp time stamp. It can be null.
     * @return true if successful.
     * @throws RemoteException if any error raises.
     */
    boolean updatePool(DatasetPoolExchanged pool, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException;
    
    
    /**
     * Reloading resulted dataset pool.
     * @param localTargetListener local target listener. It can be null.
     * @param timestamp time stamp. It can be null.
     * @return true if successful.
     * @throws RemoteException if any error raises.
     */
    boolean reloadPool(EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException;

    
	/**
	 * Adding the specified listener to the end of list of listeners, which means that such listener is registered.
	 * @param listener plug-in changed listener that is registered.
     * @throws RemoteException if any error raises.
	 */
	void addPluginChangedListener(PluginChangedListener listener) throws RemoteException;

    
	/**
	 * Remove the specified listener from the list of listener
	 * @param listener plug-in changed listener that is unregistered.
     * @throws RemoteException if any error raises.
	 */
    void removePluginChangedListener(PluginChangedListener listener) throws RemoteException;

    	
    /**
	 * Add the specified evaluator listener to the end of listener list.
	 * @param listener specified evaluator listener.
	 * @throws RemoteException if any error raises.
	 */
	void addEvaluatorListener(EvaluatorListener listener) throws RemoteException;
	
	
	/**
	 * Remove the specified evaluator listener from the listener list.
	 * @param listener specified evaluator listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeEvaluatorListener(EvaluatorListener listener) throws RemoteException;

    
    /**
	 * Add the specified evaluation listener to the end of listener list.
	 * @param listener specified {@link EvaluateListener}
	 * @throws RemoteException if any error raises.
	 */
	void addEvaluateListener(EvaluateListener listener) throws RemoteException;
	
	
	/**
	 * Remove the specified evaluation listener from the listener list.
	 * @param listener specified {@link EvaluateListener}.
	 * @throws RemoteException if any error raises.
	 */
    void removeEvaluateListener(EvaluateListener listener) throws RemoteException;
	

    /**
     * Adding the specified progress listener.
     * @param listener specified progress listener.
	 * @throws RemoteException if any error raises.
     */
	void addEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException;

    
	/**
     * Removing the specified progress listener.
	 * @param listener specified progress listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException;


    /**
     * Adding the specified setup algorithm listener.
     * @param listener specified setup algorithm listener.
	 * @throws RemoteException if any error raises.
     */
	void addSetupAlgListener(SetupAlgListener listener) throws RemoteException;

    
	/**
     * Removing the specified setup algorithm listener.
	 * @param listener specified progress algorithm listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeSetupAlgListener(SetupAlgListener listener) throws RemoteException;

    
	/**
	 * Adding elapsed time listener.
	 * @param listener elapsed time listener.
	 * @throws RemoteException if any error raises.
	 */
	void addElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException;

    
	/**
	 * Removing elapsed time listener.
	 * @param listener elapsed time listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException;

    
    /**
     * Getting evaluation store path.
     * @return evaluation store path.
     * @throws RemoteException if any error raises.
     */
	public String getEvaluateStorePath() throws RemoteException;
	
	
    /**
     * Setting evaluation store path. This method is only called locally because of different file systems in network.
     * @param evStorePath evaluation store path.
     * @throws RemoteException if any error raises.
     */
    void setEvaluateStorePath(String evStorePath) throws RemoteException;
    
    
    /**
     * Getting the internal referred service.
     * @return the internal referred service.
     * @throws RemoteException if any error raises.
     */
    Service getReferredService() throws RemoteException;
    
    
    /**
     * Setting the internal referred service.
     * @param referredService the internal referred service.
     * @throws RemoteException if any error raises.
     */
    void setReferredService(Service referredService) throws RemoteException;
    
    
//    /**
//     * Testing RMI call.
//     * @param o parameter.
//     * @return an object.
//     * @throws RemoteException if any error raises.
//     */
//    Object ping(Object o) throws RemoteException;
    
    
}
