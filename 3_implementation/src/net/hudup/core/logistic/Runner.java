package net.hudup.core.logistic;


/**
 * This interface specifies that any object that implements it can start, pause, resume, and stop by itself.
 * Such object called {@code runner} like system thread.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Runner extends Runnable {
	
	
	/*
	 * This method will not be synchronized because it is not called directly. Method start() is called instead.
	 */
	@Override
	void run();

	
	/**
	 * Runner starts running.
	 * This method will be synchronized.
	 */
	public void start();

	
	/**
	 * Runner pauses.
	 * This method will be synchronized.
	 */
	void pause();
	
	
	/**
	 * Runner resumes after runner pauses.
	 * This method will be synchronized.
	 */
	public void resume();
	
	
	/**
	 * Runner stops. If runner wants to run again, runner must start again.
	 * This method will be synchronized.
	 */
	void stop();
	
	
	/**
	 * Testing whether runner started.
	 * @return whether runner started
	 */
	boolean isStarted();
	
	
	/**
	 * Testing whether runner paused. 
	 * @return whether runner paused.
	 */
	boolean isPaused();
	
	
	/**
	 * Testing whether runner is running.
	 * @return whether runner is running.
	 */
	boolean isRunning();


}
