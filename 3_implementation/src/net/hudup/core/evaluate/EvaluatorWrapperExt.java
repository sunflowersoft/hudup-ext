package net.hudup.core.evaluate;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.EventListener;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.data.Simplify;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.CounterElapsedTimeEvent;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.EventListenerList2;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.TaskQueue;
import net.hudup.core.logistic.TaskQueue.EventTask;
import net.hudup.core.logistic.Timer2;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;

/**
 * This class is extended wrapper of remote evaluator which is more independent from the internal remote evaluator than the wrapper {@link EvaluatorWrapper}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //This is not base class but the base class annotation is used to prevent reflection of evaluators.
public class EvaluatorWrapperExt implements Evaluator, EvaluatorListener, EvaluateListener, EvaluateProgressListener, CounterElapsedTimeListener {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Holding a list of {@link EventListener} (s)
	 * 
	 */
    protected EventListenerList2 listenerList = new EventListenerList2();

    
	/**
	 * Internal map of task lists. Each list is list of events of a listener ID.
	 */
	protected Map<UUID, EventTask> taskMap = Util.newMap();

	
    /**
	 * Remote evaluator.
	 * 
	 */
    protected Evaluator remoteEvaluator = null;

    
	/**
	 * Stub object of this evaluator which is serialized to client.
	 */
	protected Evaluator exportedStub = null;
	
	
    /**
     * Internal timer.
     */
    protected Timer2 purgeTimer = null;
    
    
	/**
	 * Flag to indicate whether the evaluator is agent.
	 */
	protected boolean isAgent = false;

	
	/**
	 * Constructor with remote evaluator and socket server.
	 * @param remoteEvaluator remote evaluator, always in non-exclusive mode.
	 * @param exportedPort port to export the wrapper.
	 */
	public EvaluatorWrapperExt(Evaluator remoteEvaluator, int exportedPort) {
		if (remoteEvaluator == null || exportedPort < 0) return;
		
		try {
			this.remoteEvaluator = remoteEvaluator;
			export(exportedPort);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		this.purgeTimer = EvaluatorAbstract.createPurgeListenersTimer(listenerList);
	}
	
	
	@Override
	public void stimulate() throws RemoteException {
		if (purgeTimer != null && !purgeTimer.isStarted())
			purgeTimer.start();
	}


	@Override
	public String getName() throws RemoteException {
		return remoteEvaluator.getName();
	}


	@Override
	public String getVersionName() throws RemoteException {
		return remoteEvaluator.getVersionName();
	}


	@Override
	public boolean acceptAlg(String algClassName) throws RemoteException {
		return remoteEvaluator.acceptAlg(algClassName);
	}


	@Override
	public boolean acceptAlg(Class<? extends Alg> algClass) throws RemoteException {
		return remoteEvaluator.acceptAlg(algClass);
	}


	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		return remoteEvaluator.acceptAlg(alg);
	}


	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		return remoteEvaluator.defaultMetrics();
	}


	@Override
	public String getClassName() throws RemoteException {
		return remoteEvaluator.getClassName();
	}


	@Override
	public String getMainUnit() throws RemoteException {
		return remoteEvaluator.getMainUnit();
	}
	
	
	@Override
	public Metrics getResult() throws RemoteException {
		return remoteEvaluator.getResult();
	}

	
	@Override
	public EvaluateInfo getOtherResult() throws RemoteException {
		return remoteEvaluator.getOtherResult();
	}


	@Override
	public EvaluateInfoPersit getInfo() throws RemoteException {
		return remoteEvaluator.getInfo();
	}


	@Override
	public List<String> getMetricNameList() throws RemoteException {
		return remoteEvaluator.getMetricNameList();
	}

	
	@Override
	public void setMetricNameList(List<String> metricNameList) throws RemoteException {
		remoteEvaluator.setMetricNameList(metricNameList);
	}
	
	
	@Deprecated
	@Override
	public RegisterTable extractNormalAlgFromPluginStorage0() throws RemoteException {
		return remoteEvaluator.extractNormalAlgFromPluginStorage0();
	}
	
	
	@NextUpdate
	@Override
	public DatasetPoolExchanged getDatasetPool() throws RemoteException {
		return remoteEvaluator.getDatasetPool();
	}

	
	@Deprecated
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		return remoteEvaluator.getPluginStorage();
	}


	@Override
	public List<String> getPluginAlgNames(Class<? extends Alg> algClass) throws RemoteException {
		return remoteEvaluator.getPluginAlgNames(algClass);
	}


	@Override
	public AlgDesc2List getPluginAlgDescs(Class<? extends Alg> algClass) throws RemoteException {
		return remoteEvaluator.getPluginAlgDescs(algClass);
	}


	@Override
	public AlgDesc2 getPluginAlgDesc(Class<? extends Alg> algClass, String algName) throws RemoteException {
		return remoteEvaluator.getPluginAlgDesc(algClass, algName);
	}


	@Override
	public AlgDesc2 getPluginAlgDesc(String algClassName, String algName) throws RemoteException {
		return remoteEvaluator.getPluginAlgDesc(algClassName, algName);
	}


	@Override
	public AlgDesc2 getPluginNormalAlgDesc(String algName) throws RemoteException {
		return remoteEvaluator.getPluginNormalAlgDesc(algName);
	}


	@Override
	public Alg getPluginAlg(Class<? extends Alg> algClass, String algName, boolean remote) throws RemoteException {
		return remoteEvaluator.getPluginAlg(algClass, algName, remote);
	}


	@Override
	public Alg getEvaluatedAlg(String algName, boolean remote) throws RemoteException {
		return remoteEvaluator.getEvaluatedAlg(algName, remote);
	}


	@Override
	public AlgDesc2 getEvaluatedAlgDesc(String algName) throws RemoteException {
		return remoteEvaluator.getEvaluatedAlgDesc(algName);
	}


	@Override
	public String getEvaluatedAlgDescText(String algName) throws RemoteException {
		return remoteEvaluator.getEvaluatedAlgDescText(algName);
	}


	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		return remoteEvaluator.getConfig();
	}
	
	
	@Override
	public void setConfig(EvaluatorConfig config) throws RemoteException {
		remoteEvaluator.setConfig(config);
	}


	@Override
	public boolean isDelegate() throws RemoteException {
		return true;
	}


	@Override
	public void pluginChanged(PluginChangedEvent evt) throws RemoteException {
		firePluginChangedEvent(evt);;
	}


	@Override
	public boolean isIdle() throws RemoteException {
		return remoteEvaluator.isIdle();
	}


	@Override
	public int getPort() throws RemoteException {
		return remoteEvaluator.getPort();
	}


	/**
	 * Adding listener via listener ID.
	 * @param listenerID listener ID.
	 * @return true if adding successful.
	 */
	private boolean addListener(UUID listenerID) {
		if (listenerID == null) return false;
		
		synchronized (taskMap) {
			if (taskMap.containsKey(listenerID)) 
				return true;
			else {
				EventTask task = new EventTask();
				taskMap.put(listenerID, task);
				return true;
			}
		}
	}

	
	/**
	 * Removing listener via listener ID. 
	 * @param listenerID listener ID. 
	 * @return true if removing is successful.
	 */
	private boolean removeListener(UUID listenerID) {
		if (listenerID == null) return false;
		
		synchronized (taskMap) {
			return taskMap.remove(listenerID) != null;
		}
	}

	
	/**
	 * Adding task to this queue by event object.
	 * @param evt event object.
	 * @return true if adding successfully.
	 */
	private boolean addTask(EventObject evt) {
		if (evt == null) return false;
		
		synchronized (taskMap) {
			Collection<EventTask> tasks = taskMap.values();
			for (EventTask task : tasks) {
				task.add(evt);
				if (task.size() > TaskQueue.MAX_NUMBER_EVENT_OBJECTS)
					task.remove(0);
			}
			
			return true;
		}
	}

	
	@Override
	public List<EventObject> doTask2(UUID listenerID) throws RemoteException {
		boolean classPathContains = true;
		try {
			Util.getPluginManager().loadClass(remoteEvaluator.getClassName(), false);
		}
		catch (Throwable e) {classPathContains = false;}

		List<EventObject> returnedEvtList = doTask(listenerID);
		if (!classPathContains)
			return returnedEvtList;
		else {
			List<EventObject> simplifiedEvtList = Util.newList(returnedEvtList.size());
			for (EventObject evt : returnedEvtList) {
				if (evt instanceof Simplify) {
		    		try {
		    			EventObject simplifiedEvt = (EventObject) ((Simplify)evt).simplify();
		    			if (simplifiedEvt != null) evt = simplifiedEvt;
		    		} catch (Exception e) {LogUtil.trace(e);}
				}
				simplifiedEvtList.add(evt);
			}
			return simplifiedEvtList;
		}
	}


	@Override
	public List<EventObject> doTask(UUID listenerID) throws RemoteException {
		List<EventObject> evtList = doTask0(listenerID);
		
		try {
			if (evtList.size() == 0 && !remoteIsRunning()) return evtList;
			EvaluateInfo otherResult = getOtherResult();
			if (otherResult != null)
				evtList.add(new CounterElapsedTimeEvent(this, otherResult.elapsedTime));
		} catch (Exception ex) {LogUtil.trace(ex);}

		return evtList;
	}


	/**
	 * Performing event task of specified listener.
	 * @param listenerID ID of specified listener.
	 * @return list of events as the event task.
	 */
	private List<EventObject> doTask0(UUID listenerID) {
		if (listenerID == null) return Util.newList();
		
		List<EventObject> returnedEvtList = Util.newList();
		synchronized (taskMap) {
			EventTask task = null;
			if (!taskMap.containsKey(listenerID)) return Util.newList();
			
			task = taskMap.get(listenerID);
			returnedEvtList = Util.newList(task.size());
			returnedEvtList.addAll(task.getEventList());
			task.clear();
			task.updateLastDone();
		}
		
		boolean classPathContains = true;
		try {
			Util.getPluginManager().loadClass(remoteEvaluator.getClassName(), false);
		}
		catch (Throwable e) {classPathContains = false;}
		
		if (classPathContains)
			return returnedEvtList;
		else {
			List<EventObject> simplifiedEvtList = Util.newList(returnedEvtList.size());
			for (EventObject evt : returnedEvtList) {
				if (evt instanceof Simplify) {
		    		try {
		    			EventObject simplifiedEvt = (EventObject) ((Simplify)evt).simplify();
		    			if (simplifiedEvt != null) evt = simplifiedEvt;
		    		} catch (Exception e) {LogUtil.trace(e);}
				}
				simplifiedEvtList.add(evt);
			}
			return simplifiedEvtList;
		}
	}
	
	
	@Override
	public void addPluginChangedListener(PluginChangedListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(PluginChangedListener.class, listener);
		}
    }

	
	@Override
	public void addPluginChangedListener(UUID listenerID) throws RemoteException {
		addListener(listenerID);
	}


	@Override
    public void removePluginChangedListener(PluginChangedListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(PluginChangedListener.class, listener);
		}
    }
	
    
    @Override
	public void removePluginChangedListener(UUID listenerID) throws RemoteException {
    	removeListener(listenerID);
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
    	addTask(evt);
    	
		PluginChangedListener[] listeners = getPluginChangedListeners();
		for (PluginChangedListener listener : listeners) {
			try {
				listener.pluginChanged(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
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
		addListener(listenerID);
	}


	@Override
    public void removeEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluatorListener.class, listener);
		}
    }

	
    @Override
	public void removeEvaluatorListener(UUID listenerID) throws RemoteException {
    	removeListener(listenerID);
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
    	addTask(evt);

		EvaluatorListener[] listeners = getEvaluatorListeners();
		for (EvaluatorListener listener : listeners) {
			try {
				listener.receivedEvaluator(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
    }

    
    @Override
	public void receivedEvaluator(EvaluatorEvent evt) throws RemoteException {
    	fireEvaluatorEvent(evt);
	}

    
    @Override
	public void addEvaluateListener(EvaluateListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluateListener.class, listener);
		}
    }

    
	@Override
	public void addEvaluateListener(UUID listenerID) throws RemoteException {
		addListener(listenerID);
	}


	@Override
    public void removeEvaluateListener(EvaluateListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluateListener.class, listener);
		}
    }
	
    
    @Override
	public void removeEvaluateListener(UUID listenerID) throws RemoteException {
    	removeListener(listenerID);
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
    	addTask(evt);

		EvaluateListener[] listeners = getEvaluateListeners();
		for (EvaluateListener listener : listeners) {
			try {
				listener.receivedEvaluation(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
    }

    
    @Override
	public void receivedEvaluation(EvaluateEvent evt) throws RemoteException {
    	fireEvaluateEvent(evt);
	}


	@Override
	public void addEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluateProgressListener.class, listener);
		}
    }

    
	@Override
	public void addEvaluateProgressListener(UUID listenerID) throws RemoteException {
		addListener(listenerID);
	}


    @Override
    public void removeEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluateProgressListener.class, listener);
		}
    }
	
    
    @Override
	public void removeEvaluateProgressListener(UUID listenerID) throws RemoteException {
    	removeListener(listenerID);
	}


    /**
     * Getting an array of evaluation progress listener.
     * @return array of {@link ProgressListener} (s).
     */
    protected EvaluateProgressListener[] getProgressListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluateProgressListener.class);
		}
    }
    
    
    /**
     * Firing {@link ProgressEvent}.
     * @param evt the specified for evaluation progress.
     */
    protected void fireProgressEvent(EvaluateProgressEvent evt) {
    	addTask(evt);

    	EvaluateProgressListener[] listeners = getProgressListeners();
		for (EvaluateProgressListener listener : listeners) {
			try {
				listener.receivedProgress(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
    }


    @Override
	public void receivedProgress(EvaluateProgressEvent evt) throws RemoteException {
		fireProgressEvent(evt);
	}


	@Override
	public void addSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(SetupAlgListener.class, listener);
		}
    }

    
	@Override
	public void addSetupAlgListener(UUID listenerID) throws RemoteException {
		addListener(listenerID);
	}


    @Override
    public void removeSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
    }
	
    
    @Override
	public void removeSetupAlgListener(UUID listenerID) throws RemoteException {
    	removeListener(listenerID);
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
    	addTask(evt);

    	SetupAlgListener[] listeners = getSetupAlgListeners();
		for (SetupAlgListener listener : listeners) {
			try {
				listener.receivedSetup(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
    }

    
	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		fireSetupAlgEvent(evt);
	}


	/**
	 * Adding elapsed time listener.
	 * @param listener elapsed time listener.
	 * @throws RemoteException if any error raises.
	 */
	public void addElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(CounterElapsedTimeListener.class, listener);
		}
    }

    
	/**
	 * Removing elapsed time listener.
	 * @param listener elapsed time listener.
	 * @throws RemoteException if any error raises.
	 */
    public void removeElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(CounterElapsedTimeListener.class, listener);
		}
    }
	
    
    /**
     * Getting an array of elapsed time listeners.
     * @return array of elapsed time listeners.
     */
    protected CounterElapsedTimeListener[] getElapsedTimeListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(CounterElapsedTimeListener.class);
		}
    }
    
    
    /**
     * Firing elapsed time event.
     * @param evt elapsed time event.
     */
    protected void fireElapsedTimeEvent(CounterElapsedTimeEvent evt) {
    	CounterElapsedTimeListener[] listeners = getElapsedTimeListeners();
		for (CounterElapsedTimeListener listener : listeners) {
			try {
				listener.receivedElapsedTime(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
    }

    
    @Override
	public void receivedElapsedTime(CounterElapsedTimeEvent evt) throws RemoteException {
    	fireElapsedTimeEvent(evt);
	}


	@Override
	public boolean classPathContains(String className) throws RemoteException {
		return remoteEvaluator.classPathContains(className);
	}


	@Override
	public synchronized boolean remoteStart(List<Alg> algList, DatasetPoolExchanged pool, Timestamp timestamp, Serializable parameter) throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		stimulate();
		
		return remoteEvaluator.remoteStart(algList, pool, timestamp, parameter);
	}

	
	@Override
	public synchronized boolean remoteStart(List<String> algNameList, DatasetPoolExchanged pool, ClassProcessor cp, DataConfig config, Timestamp timestamp, Serializable parameter)
			throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		stimulate();
		
		return remoteEvaluator.remoteStart(algNameList, pool, cp, config, timestamp, parameter);
	}


	@Override
	public synchronized boolean remoteStart(Serializable... parameters) throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		stimulate();
		
		return remoteEvaluator.remoteStart(parameters);
	}


	@Override
	public synchronized boolean remotePause() throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		
		return remoteEvaluator.remotePause();
	}

	
	@Override
	public synchronized boolean remoteResume() throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		
		return remoteEvaluator.remoteResume();
	}

	
	@Override
	public synchronized boolean remoteStop() throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		
		return remoteEvaluator.remoteStop();
	}

	
	@Override
	public synchronized boolean remoteForceStop() throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		
		return remoteEvaluator.remoteForceStop();
	}

	
	@Override
	public void remoteStopAndClearResults(boolean clearPool) throws RemoteException {
		remoteEvaluator.remoteStopAndClearResults(clearPool);
	}


	@Override
	public boolean remoteIsStarted() throws RemoteException {
		return remoteEvaluator.remoteIsStarted();
	}

	
	@Override
	public boolean remoteIsPaused() throws RemoteException {
		return remoteEvaluator.remoteIsPaused();
	}

	
	@Override
	public boolean remoteIsRunning() throws RemoteException {
		return remoteEvaluator.remoteIsRunning();
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exportedStub == null) {
			exportedStub = (Evaluator)NetUtil.RegistryRemote.export(this, serverPort);
			if (exportedStub != null)
				LogUtil.info("Delegator evaluator served at port " + serverPort);
			else
				LogUtil.info("Delegator evaluator failed to export");

			try {
				remoteEvaluator.addPluginChangedListener(this);
				remoteEvaluator.addEvaluatorListener(this);
				remoteEvaluator.addEvaluateListener(this);
				remoteEvaluator.addEvaluateProgressListener(this);
				remoteEvaluator.addSetupAlgListener(this);
				remoteEvaluator.addElapsedTimeListener(this);
			} catch (Exception e) {LogUtil.trace(e);}
		}
		
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (exportedStub != null) {
			if (!remoteEvaluator.containsAgent())
				this.remoteStop();
			
			try {
				remoteEvaluator.removePluginChangedListener(this);
				remoteEvaluator.removeEvaluatorListener(this);
				remoteEvaluator.removeEvaluateListener(this);
				remoteEvaluator.removeEvaluateProgressListener(this);
				remoteEvaluator.removeSetupAlgListener(this);
				remoteEvaluator.removeElapsedTimeListener(this);
				
				if (!remoteEvaluator.isAgent())
					remoteEvaluator.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
			
			boolean ret = NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
			if (ret)
				LogUtil.info("Delegator evaluator unexported successfully");
			else
				LogUtil.info("Delegator evaluator unexported failedly");
		}
	}


	@Override
	public synchronized void forceUnexport() throws RemoteException {
		unexport();
	}


	@Override
	public String getEvaluateStorePath() throws RemoteException {
		return remoteEvaluator.getEvaluateStorePath();
	}


	@Override
	public void setEvaluateStorePath(String evStorePath) throws RemoteException {
		remoteEvaluator.setEvaluateStorePath(evStorePath);
	}


	@Override
	public Service getReferredService() throws RemoteException {
		return remoteEvaluator.getReferredService();
	}


	@Override
	public void setReferredService(Service referredService) throws RemoteException {
		remoteEvaluator.setReferredService(referredService);
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
	public void setAgent(boolean agent) throws RemoteException {
		this.isAgent = agent;
	}


	@Override
	public boolean containsAgent() throws RemoteException {
		return remoteEvaluator.containsAgent();
	}


	@Override
	public boolean updatePool(DatasetPoolExchanged pool, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.updatePool(pool, localTargetListener, timestamp);
	}

	
	@Override
	public boolean reloadPool(EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.reloadPool(localTargetListener, timestamp);
	}


	@Override
	public boolean updatePoolWithoutClear(DatasetPoolExchanged pool, EvaluatorListener localTargetListener,
			Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.updatePoolWithoutClear(pool, localTargetListener, timestamp);
	}


	@Override
	public boolean refPool(boolean ref, DatasetPoolExchanged refPool, String refName, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.refPool(ref, refPool, refName, localTargetListener, timestamp);
	}


	@Override
	public boolean refPool(boolean ref, DatasetPoolsService poolsService, String refName, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.refPool(ref, poolsService, refName, localTargetListener, timestamp);
	}


	@Override
	public boolean refPool(boolean ref, String refName, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.refPool(ref, refName, localTargetListener, timestamp);
	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


	@Override
	public void close() throws Exception {
    	try {
    		if (purgeTimer != null) {
    			purgeTimer.stop();
    			purgeTimer = null;
    		}
    	}
		catch (Throwable e) {LogUtil.trace(e);}

    	try {
			unexport();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}


	@Override
	public String toString() {
    	String evaluatorName = "No name";
		try {
			evaluatorName = getName();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		return DSUtil.shortenVerbalName(evaluatorName);
	}

	
	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
			close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
			

}
