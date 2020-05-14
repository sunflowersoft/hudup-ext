/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
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
	 * This enum specifies priority of thread.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public enum Priority {
		
		/**
		 * Default priority.
		 */
		default_value,
		
		/**
		 * Minimum priority.
		 */
		min,
		
		/**
		 * Normal priority.
		 */
		normal,
		
		/**
		 * Maximum priority.
		 */
		max,
		
	};
	
	
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

	
	/**
	 * Thread priority.
	 */
	protected Priority priority = Priority.default_value;
	
	
	/**
	 * Default constructor.
	 */
	public AbstractRunner() {
		this(Priority.default_value);
	}
	
	
	/**
	 * Constructor with specified priority.
	 * @param priority specified priority.
	 */
	public AbstractRunner(Priority priority) {
		super();
		this.priority = priority;
	}


	/**
	 * Setting thread priority.
	 * @param priority thread priority.
	 */
	public void setPriority(Priority priority) {
		this.priority = priority;
	}
	
	
	/**
	 * Getting thread priority.
	 * @return thread priority.
	 */
	public Priority getPriority() {
		return priority;
	}
	
	
	@Override
	public void run() {
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
		if (!isRunning()) return false;
		
		paused  = true;
		
		try {
			wait();
		} 
		catch (Throwable e) {
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
			LogUtil.error("Calling notifyAll() in AbstractRunner#forceStop causes error " + e.getMessage());
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
