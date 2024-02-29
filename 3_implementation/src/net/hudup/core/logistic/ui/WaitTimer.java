package net.hudup.core.logistic.ui;

import java.util.Timer;
import java.util.TimerTask;

import net.hudup.core.logistic.LogUtil;

/**
 * This class is timer for waiting dialog.
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
@Deprecated
public class WaitTimer extends Timer {

	
	/**
	 * The default period in mili-seconds.
	 */
	public static int PERIOD = 5000;
	
	
//	/**
//	 * Referred evaluator.
//	 */
//	protected Evaluator evaluator = null;
	
	
	/**
	 * Waiting dialog.
	 */
	protected WaitDialog dlgWait = null;
	
	
	/**
	 * Extra waiting mode. Even though this variable is false, waiting mode is still true if evaluator is running.
	 */
	protected volatile boolean waitMode = false;
	
	
	/**
	 * Default constructor with specified evaluator.
	 */
	public WaitTimer(/*Evaluator evaluator*/) {
//		this.evaluator = evaluator;
		this.dlgWait = new WaitDialog(null);
		
		schedule(
			new TimerTask() {
			
				@Override
				public void run() {
					try {
						dlgWait.stop();
						if (waitMode /*|| evaluator != null && evaluator.remoteIsRunning()*/)
							dlgWait.start();
					} 
					catch (Throwable e) {
						LogUtil.trace(e);
						dlgWait.stop();
					}
				}
			}, 
			0, 
			PERIOD);
	}

	
	/**
	 * Setting extra waiting mode.
	 * @param waitMode extra waiting mode.
	 */
	public void setWaitMode(boolean waitMode) {
		this.waitMode = waitMode;
	}


	@Override
	public void cancel() {
		super.cancel();
		
		if (dlgWait != null) dlgWait.stop();
	}
	
	
}
