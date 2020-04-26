/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

/**
 * This class is timer.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class Timer extends AbstractRunner {

	
	/**
	 * Waiting flag for forcing to stop.
	 */
	protected volatile boolean wait = false;
	
	
	/**
	 * Delay at starting time.
	 */
	protected long delay = 0;

	
	/**
	 * Period to perform task.
	 */
	protected long period = 0;

	
	/**
	 * Constructor with period.
	 * @param period period time in miliseconds.
	 */
	public Timer(long period) {
		this(0, period);
	}

	
	/**
	 * Constructor with delay and period.
	 * @param delay delay time in miliseconds.
	 * @param period period time in miliseconds.
	 */
	public Timer(long delay, long period) {
		this.delay = delay >= 0 ? delay : 0;
		this.period = period >= 0 ? period : 0;
	}

	
	@Override
	public void run() {
		long startedTime = System.currentTimeMillis();
		while (wait && (System.currentTimeMillis() - startedTime < period)) { }

		Thread current = Thread.currentThread();
		while (current == thread) {
			
			try {
				startedTime = System.currentTimeMillis();
				task();
				
				while (wait && (System.currentTimeMillis() - startedTime < period)) { }
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}

			synchronized (this) {
				while (paused) {
					notifyAll();
					try {
						wait();
					}
					catch (Throwable e) {
						LogUtil.trace(e);
					}
				}
			}
		}
		
		synchronized (this) {
			thread = null;
			wait = false;
			paused = false;
			clear();
			
			notifyAll();
		}
	}

	
	@Override
	public synchronized boolean start() {
		if (isStarted()) return false;
		
		thread = new RunnerThread(this);
		wait = true;
		thread.start();
		
		return true;
	}

	
	@Override
	public synchronized boolean pause() {
		if (!isRunning()) return false;
		
		wait = false;
		paused  = true;
		
		try {
			wait();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return true;
	}
	
	
	@Override
	public synchronized boolean resume() {
		if (!isPaused()) return false;
		
		paused = false;
		wait = true;
		notifyAll();
		
		return true;
	}
	
	
	@Override
	public synchronized boolean stop() {
		if (!isStarted()) return false;
		
		thread = null;
		wait = false;
		
		if (paused) {
			paused = false;
			notifyAll();
		}
		
		try {
			wait();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return true;
	}
	
	
	@SuppressWarnings("deprecation")
	@Override
	public synchronized boolean forceStop() {
		if (!isStarted()) return false;

		try {
			if (thread != null)
				thread.stop();
		}
		catch (Throwable e) {
			LogUtil.error("Calling thread destroy() in Timer#forceStop causes error " + e.getMessage());
		}
		
		wait = false;
		thread = null;
		paused = false;
		clear();
		
		try {
			notifyAll();
		}
		catch (Throwable e) {
			LogUtil.error("Calling notifyAll() in Timer#forceStop causes error " + e.getMessage());
		}
		
		return true;
	}


}
