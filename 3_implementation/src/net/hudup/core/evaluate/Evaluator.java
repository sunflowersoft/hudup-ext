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

import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.Exportable;
import net.hudup.core.logistic.RemoteRunner;

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
public interface Evaluator extends Remote, RemoteRunner, SetupAlgListener, Exportable, AutoCloseable {

	
	/**
	 * Evaluator starts.
	 * @param algList specified list of algorithms. It must be serializable in remote call.
	 * @param pool specified dataset pool containing many training datasets and testing datasets. It must be serializable in remote call.
	 * @param parameter additional parameter.
	 * @throws RemoteException if any error raises.
	 */
	void remoteStart(List<Alg> algList, DatasetPool pool, Serializable parameter) throws RemoteException;

	
	/**
	 * Evaluator forces to stop.
	 * @throws RemoteException if any error raises.
	 */
	void remoteForceStop() throws RemoteException;

	
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
	 * Checking whether the specified algorithm is accepted by this evaluator.
	 * @param alg specified algorithm.
	 * @return whether the specified algorithm is accepted by this evaluator.
	 * @throws RemoteException if any error raises.
	 */
	boolean acceptAlg(Alg alg) throws RemoteException;

	
	/**
	 * Defining the list of default metrics.
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
	 * Getting the list of metrics resulted from the evaluation process.
	 * @return list of metrics resulted from the evaluation process.
	 * @throws RemoteException if any error raises.
	 */
	List<Metric> getMetricList() throws RemoteException;

	
	/**
	 * Setting metric list.
	 * @param metricList specified metric list.
	 * @throws RemoteException if any error raises.
	 */
	void setMetricList(List<Metric> metricList) throws RemoteException;
	
	
	/**
	 * Extracting algorithms from plug-in storage so that such algorithms are accepted by this evaluator.
	 * This method is deprecated because of RMI class loading.
	 * @return register table to store algorithms extracted from plug-in storage.
	 * @throws RemoteException if any error raises.
	 */
	@Deprecated
	RegisterTable extractAlgFromPluginStorage0() throws RemoteException;

	
	/**
	 * Clearing delay unsetting up algorithms.
	 * @throws RemoteException if any error raises.
	 */
	void clearDelayUnsetupAlgs() throws RemoteException;

	
	/**
	 * Getting system plug-in storage. This method is currently not used.
	 * @return wrapper of system plug-in storage.
	 * @throws RemoteException if any error raises.
	 */
	@Deprecated
	PluginStorageWrapper getPluginStorage() throws RemoteException;
	
	
	/**
	 * Add the specified listener to the end of listener list.
	 * @param listener specified {@link EvaluatorListener}
	 * @throws RemoteException if any error raises.
	 */
	void addEvaluatorListener(EvaluatorListener listener) throws RemoteException;
	
	
	/**
	 * Remove the specified listener from the listener list.
	 * @param listener specified {@link EvaluatorListener}.
	 * @throws RemoteException if any error raises.
	 */
    void removeEvaluatorListener(EvaluatorListener listener) throws RemoteException;
	

    /**
     * Adding the specified progress listener.
     * @param listener specified progress listener.
	 * @throws RemoteException if any error raises.
     */
	void addProgressListener(EvaluatorProgressListener listener) throws RemoteException;

    
	/**
     * Removing the specified progress listener.
	 * @param listener specified progress listener.
	 * @throws RemoteException if any error raises.
	 */
    void removeProgressListener(EvaluatorProgressListener listener) throws RemoteException;


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
     * Setting evaluation store path.
     * @param evStorePath evaluation store path.
     * @throws RemoteException if any error raises.
     */
    void setEvaluateStorePath(String evStorePath) throws RemoteException;
    
    
//    /**
//     * Testing RMI call.
//     * @param o parameter.
//     * @return an object.
//     * @throws RemoteException if any error raises.
//     */
//    Object ping(Object o) throws RemoteException;
    
    
}
