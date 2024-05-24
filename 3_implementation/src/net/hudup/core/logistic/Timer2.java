/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

/**
 * This class is extended version of timer.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class Timer2 extends AbstractRunner {

	
	/**
	 * Delay at starting time in milisecond.
	 */
	protected long delay = 0;

	
	/**
	 * Period to perform task in milisecond.
	 */
	protected long period = 0;

	
	/**
	 * Constructor with period.
	 * @param period period time in miliseconds.
	 */
	public Timer2(long period) {
		this(0, period);
	}

	
	/**
	 * Constructor with delay and period.
	 * @param delay delay time in miliseconds.
	 * @param period period time in miliseconds.
	 */
	public Timer2(long delay, long period) {
		super();
		this.delay = delay >= 0 ? delay : 0;
		this.period = period >= 0 ? period : 0;
	}

	
	@Override
	public void run() {
		if (thread != null && delay > 0) {
			synchronized (this) {
				try {
					wait(delay);
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
		}

		Thread current = Thread.currentThread();
		while (current == thread) {
			
			try {
				task();
			}
			catch (Throwable e) {LogUtil.trace(e);}

			synchronized (this) {
				try {
					if (thread != null && period > 0)
						wait(period);
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		
		synchronized (this) {
			thread = null;
			clear();
			
			notifyAll();
		}
	}

	
	@Override
	public synchronized boolean start() {
		if (isStarted()) return false;
		
		thread = new RunnerThread(this);
		try {
			if (priority == Priority.min)
				thread.setPriority(Thread.MIN_PRIORITY);
			else if (priority == Priority.normal)
				thread.setPriority(Thread.NORM_PRIORITY);
			else if (priority == Priority.max)
				thread.setPriority(Thread.MAX_PRIORITY);
		}
		catch (Exception e) {LogUtil.trace(e);}

		thread.start();
		
		return true;
	}


	@Override
	public synchronized boolean pause() {
		LogUtil.error("Timer2 does not support method pause()");
		return false;
	}
	
	
	@Override
	public synchronized boolean resume() {
		LogUtil.error("Timer2 does not support method resume()");
		return false;
	}
	
	
	@Override
	public synchronized boolean stop() {
		if (!isStarted()) return false;
		
		thread = null;
		
		notifyAll();
		
		try {
			wait();
		} 
		catch (Throwable e) {LogUtil.trace(e);}
		
		return true;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public synchronized boolean forceStop() {
		if (!isStarted()) return false;

		try {
			if (thread != null && !thread.isInterrupted()) thread.interrupt();
		}
		catch (Throwable e) {LogUtil.error("Calling thread interrupt() causes error " + e.getMessage());}
		try {
			if (thread != null && SystemUtil.getJavaVersion() <= 15) thread.stop();
		}
		catch (Throwable e) {LogUtil.error("Calling thread stop() in AbstractRunner#forceStop causes error " + e.getMessage());}

		thread = null;
		clear();
		
		try {
			notifyAll();
		}
		catch (Throwable e) {
			LogUtil.error("Calling notifyAll() in Timer2#forceStop causes error " + e.getMessage());
		}
		
		return true;
	}


}
