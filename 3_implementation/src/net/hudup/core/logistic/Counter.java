/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;
import java.rmi.RemoteException;

import javax.swing.JLabel;

import net.hudup.core.evaluate.EvaluateInfo;

/**
 * This class shows a time counter in text form &quot;hours: minutes: seconds&quot;.
 * Because it class is a runner by extending {@link AbstractRunner}, it updates automatically the time counter in text form &quot;hours: minutes: seconds&quot;.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class Counter extends AbstractRunner implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The text form of this counter &quot;hours: minutes: seconds&quot;.
	 */
	public static final String TIME_FORMAT = I18nUtil.message("time") + " %d:%d:%d";
	
	
	/**
	 * Time period in seconds to update the counter.
	 */
	public static final long PERIOD = 1; // 1 s
	
	
	/**
	 * Associated text pane to show the counter in text form &quot;hours: minutes: seconds&quot;.
	 */
	@Deprecated
	protected JLabel assocTxtTime = null;
	
	
	/**
	 * Associated evaluation information.
	 */
	protected EvaluateInfo assocEvaluateInfo = null;
	
	
	/**
	 * Holding a list of event listener.
	 * 
	 */
    protected EventListenerList2 listenerList = new EventListenerList2();

    
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
	public Counter() {
		
	}
	
	
	/**
	 * Setting listener list. Using this method is careful.
	 * @param listenerList listener list.
	 */
	public synchronized void setListenerList(EventListenerList2 listenerList) {
		synchronized (this.listenerList) {
			if (listenerList != null) this.listenerList = listenerList;
		}
	}
	
	
	/**
	 * Constructor with the associated text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 * @param assocTxtTime associated text pane.
	 */
	@Deprecated
	public Counter(JLabel assocTxtTime) {
		this();
		setAssocTimeTextPane(assocTxtTime);
	}
	
	
	/**
	 * Constructor with associated evaluation information.
	 * @param assocEvaluateInfo associated evaluation information.
	 */
	public Counter(EvaluateInfo assocEvaluateInfo) {
		this();
		setAssocEvaluateInfo(assocEvaluateInfo);
	}

	
	/**
	 * Setting associated text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 * @param assocTxtTime associated text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 */
	@Deprecated
	public synchronized void setAssocTimeTextPane(JLabel assocTxtTime) {
		if (assocTxtTime != null)
			this.assocTxtTime = assocTxtTime;
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
		if (startedTime == 0) return;
		
		long interval = System.currentTimeMillis() - startedTime;
		if (interval < PERIOD*1000) return;
		
		long newElapsedTime = elapsedTime + interval;
		if (assocEvaluateInfo != null) assocEvaluateInfo.elapsedTime = newElapsedTime;
		if (assocTxtTime != null) assocTxtTime.setText(formatTime(newElapsedTime));
		
		fireElapsedTimeEvent(new CounterElapsedTimeEvent(this, newElapsedTime));
		
//		try {
//			Thread.sleep(Math.min(PERIOD*1000, 5000));
//		} catch (Exception e) {LogUtil.trace(e);}
	}

	
	@Override
	protected void clear() {
		
	}


	@Override
	public synchronized boolean start() {
		return start(0);
	}

	
	/**
	 * Starting counter with started time elapse.
	 * @param elapsedTime started time elapse.
	 * @return true if successful.
	 */
	public synchronized boolean start(long elapsedTime) {
		if (!super.start()) return false;

		this.elapsedTime = elapsedTime;
		this.startedTime = System.currentTimeMillis();

		return true;
	}

	
	@Override
	public synchronized boolean stop() {
		if (!super.stop()) return false;

		elapsedTime = 0;
		startedTime = 0;
		
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
	 * Stopping this counter and clearing associated information.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private synchronized void stopAndClearAssoc() {
		stop();
		clearAssoc();
	}

	
	/**
	 * Clearing associated information.
	 */
	@Deprecated
	private synchronized void clearAssoc() {
		if (assocTxtTime != null) assocTxtTime.setText("");
		if (assocEvaluateInfo != null) assocEvaluateInfo.elapsedTime = 0;
	}
	
	
	/**
	 * Getting elapsed time in miliseconds.
	 * @return elapsed time in miliseconds.
	 */
	public synchronized long getElapsedTime() {
		if (startedTime == 0) return elapsedTime;
		
		long currentTime = System.currentTimeMillis();
		return (elapsedTime + currentTime - startedTime);
	}
	
	
	/**
	 * Setting elapsed time in miliseconds.
	 * @param elapsedTime elapsed time in miliseconds.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private synchronized void setTimeElapse(long elapsedTime) {
		if (elapsedTime >= 0) this.elapsedTime = elapsedTime;
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

    
    /**
	 * Formatting miliseconds in date-time format.
	 * @param milis specified miliseconds. 
	 * @return date-time format text of specified miliseconds.
	 */
	public static String formatTime(long milis) {
		long timeSum = milis / 1000;
		long hours = timeSum / 3600;
		long minutes = (timeSum % 3600) / 60;
		long seconds = (timeSum % 3600) % 60;
		return String.format(TIME_FORMAT, hours, minutes, seconds);
	}
	
	
    /**
	 * Formatting miliseconds in time interval.
	 * @param milis specified miliseconds. 
	 * @return interval format text of specified miliseconds.
	 */
	public static String formatTimeInterval(long milis) {
		long timeSum = milis / 1000;
		long hours = timeSum / 3600;
		long minutes = (timeSum % 3600) / 60;
		long seconds = (timeSum % 3600) % 60;
		return hours + " hours " + minutes + " minutes " + seconds + " seconds";
	}


}
