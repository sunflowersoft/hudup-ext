/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

/**
 * This class is a {@code runner} that implements partially the interface {@link Runner}.
 * Most necessary methods are implemented except that any completed class which extends this class must defines method {@link #task()} and method {@link #clear()}.
 * Method {@link #task()} defines actual works when runner is running.
 * Method {@link #clear()} clears all resources after runner run.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class AbstractRunner implements Runner {
	
	
	/**
	 * Built-in thread which is the base of this runner to run.
	 * This is also so a flag of whether this runner is starting.
	 */
	protected volatile RunnerThread thread = null;
	
	
	/**
	 * This variable is a flag of whether this runner is pause.
	 * Note, a runner paused if the variable {@link #thread} is not null and this variable is true.
	 */
	protected volatile boolean paused = false;

	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
		Thread current = Thread.currentThread();
		
		while (current == thread) {
			
			try {
				task();
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
			paused = false;
			clear();
			
			notifyAll();
		}
	}

	
	/**
	 * The actual tasks (works) which are performed when runner is running. This method should not be synchronized because it is called inside the loop of run method.
	 */
	protected abstract void task();
	
	
	/**
	 * Clearing all resources after runner run (stopped). This method should not be synchronized because it is called inside the loop of run method.
	 */
	protected abstract void clear();
	
	
	@Override
	public synchronized boolean start() {
		if (isStarted()) return false;
		
		thread = new RunnerThread(this);
		thread.start();
		
		return true;
	}

	
	@Override
	public synchronized boolean pause() {
		if (!isRunning()) return false;
		
		paused  = true;
		
		try {
			wait();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		return true;
	}
	
	
	/*
	 * It is possible that the method is not synchronized so that a active thread / application can resume this runner which was paused by other active thread / application.
	 * However, another risked can be issued when start/stop/pause can intervene right after the line code &quot;paused = false&quot;.
	 */
	@Override
	@NextUpdate
	public synchronized boolean resume() {
		if (!isPaused()) return false;
		
		paused = false;
		//Risked can be issued when start/stop/pause can intervene right here if the method is not synchronized.
		notifyAll();
		
		return true;
	}
	
	
	@Override
	public synchronized boolean stop() {
		if (!isStarted()) return false;
		
		thread = null;
		
		if (paused) {
			paused = false;
			notifyAll();
		}
		
		try {
			wait();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		return true;
	}
	
	
	/**
	 * Forcing this runner to stop immediately. This method stops the thread of this runner and so it is unsafe.
	 * Please see {@link Thread#stop()} for more details.
	 * @return true if forcing to stop successful.
	 */
	@SuppressWarnings("deprecation")
	@NextUpdate
	public synchronized boolean forceStop() {
		if (!isStarted()) return false;

		try {
			if (thread != null)
				thread.stop();
		}
		catch (Throwable e) {
			LogUtil.error("Calling thread destroy() in AbstractRunner#forceStop causes error " + e.getMessage());
		}
		
		thread = null;
		paused = false;
		clear();
		
		try {
			notifyAll();
		}
		catch (Throwable e) {
			LogUtil.error("Calling notifyAll() in Evaluator#forceStop causes error " + e.getMessage());
		}
		
		return true;
	}

	
	/*
	 * This method is not synchronized because thread and paused are volatile variables.
	 */
	@Override
	public boolean isStarted() {
		return thread != null;
	}
	
	
	/*
	 * This method is not synchronized because thread and paused are volatile variables.
	 */
	@Override
	public boolean isPaused() {
		return thread != null && paused;
	}
	
	
	/*
	 * This method is not synchronized because thread and paused are volatile variables.
	 */
	@Override
	public boolean isRunning() {
		return thread != null && !paused;
	}
	
	
}
