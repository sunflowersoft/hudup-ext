/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.rmi.RemoteException;

import net.hudup.core.evaluate.EvaluateInfo;

/**
 * This class is combination of counter and task queue.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class CounterTaskQueue extends TaskQueue {

	
	/**
	 * Associated evaluation information.
	 */
	protected EvaluateInfo assocEvaluateInfo = null;

	
    /**
	 * Elapsed time in miliseconds.
	 */
	protected long elapsedTime = 0;
	
	
	/**
	 * Starting time in miliseconds
	 */
	protected long startedTime = 0;

	
	/**
	 * Default constructor.
	 */
	public CounterTaskQueue() {
		this(null);
	}

	
	/**
	 * Constructor with associated evaluation information.
	 * @param assocEvaluateInfo associated evaluation information.
	 */
	public CounterTaskQueue(EvaluateInfo assocEvaluateInfo) {
		super();
		setAssocEvaluateInfo(assocEvaluateInfo);
	}


	/**
	 * Setting associated evaluation information.
	 * @param assocEvaluateInfo evaluation information.
	 */
	public synchronized void setAssocEvaluateInfo(EvaluateInfo assocEvaluateInfo) {
		if (assocEvaluateInfo != null)
			this.assocEvaluateInfo = assocEvaluateInfo;
	}

	
	@Override
	protected void task() {
		super.task();
		
		if (startedTime > 0) {
			long interval = System.currentTimeMillis() - startedTime;
			
			long newElapsedTime = elapsedTime + interval;
			if (assocEvaluateInfo != null) assocEvaluateInfo.elapsedTime = newElapsedTime;
			
			fireElapsedTimeEvent(new CounterElapsedTimeEvent(this, newElapsedTime));
		}
	}


	@Override
	protected void clear() {
		super.clear();
		
		elapsedTime = 0;
		startedTime = 0;
	}


	@Override
	public synchronized boolean start() {
		if (!super.start()) return false;
		
		this.elapsedTime = 0;
		this.startedTime = System.currentTimeMillis();

		return true;
	}


	@Override
	public synchronized boolean pause() {
		if (!super.pause()) return false;

		long currentTime = System.currentTimeMillis();
		elapsedTime = elapsedTime + currentTime - startedTime;
		startedTime = 0;
		
		return true;
	}


	@Override
	public synchronized boolean resume() {
		if (!super.resume()) return false;

		startedTime = System.currentTimeMillis();

		return true;
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
//		synchronized (listenerList) {
	    	CounterElapsedTimeListener[] listeners = getElapsedTimeListeners();
			for (CounterElapsedTimeListener listener : listeners) {
				try {
					listener.receivedElapsedTime(evt);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
//		}
    }

    
}
