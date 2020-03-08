/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.io.Serializable;

import javax.swing.JLabel;

import net.hudup.core.evaluate.EvaluateInfo;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.I18nUtil;

/**
 * This class shows a time counter (counter clock) in text form &quot;hours: minutes: seconds&quot;.
 * Because it class is a runner by extending {@link AbstractRunner}, it updates automatically the time counter in text form &quot;hours: minutes: seconds&quot;.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class CounterClock extends AbstractRunner implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The text form of this counter clock &quot;hours: minutes: seconds&quot;.
	 */
	public static final String TIME_FORMAT = I18nUtil.message("time") + " %d:%d:%d";
	
	
	/**
	 * Time period in milisecond to update the counter.
	 */
	public static final long PERIOD = 1000; // 1 s
	
	
	/**
	 * Associated text pane to show the counter in text form &quot;hours: minutes: seconds&quot;.
	 */
	protected JLabel assocTxtTime = null;
	
	
	/**
	 * Associated evaluation information.
	 */
	protected EvaluateInfo assocEvaluateInfo = null;
	
	
	/**
	 * Elapsed time in miliseconds.
	 */
	protected long timeElapse = 0;
	
	
	/**
	 * Starting time in miliseconds
	 */
	protected long startedTime = 0;
	
	
	/**
	 * Default constructor.
	 */
	public CounterClock() {
		
	}
	
	
	/**
	 * Constructor with the associated text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 * @param assocTxtTime associated text pane.
	 */
	public CounterClock(JLabel assocTxtTime) {
		this();
		setAssocTimeTextPane(assocTxtTime);
	}
	
	
	/**
	 * Constructor with associated evaluation information.
	 * @param assocEvaluateInfo associated evaluation information.
	 */
	public CounterClock(EvaluateInfo assocEvaluateInfo) {
		this();
		setAssocEvaluateInfo(assocEvaluateInfo);
	}

	
	/**
	 * Setting associated text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 * @param assocTxtTime associated text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 */
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
		// TODO Auto-generated method stub
		if (startedTime == 0)
			return;
		
		long currentTime = System.currentTimeMillis();
		long interval = currentTime - startedTime;
		if (interval >= PERIOD)
			updateTime();
	}

	
	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public synchronized void start() {
		// TODO Auto-generated method stub
		super.start();
		timeElapse = 0;
		startedTime = System.currentTimeMillis();
	}

	
	@Override
	public synchronized void stop() {
		// TODO Auto-generated method stub
		super.stop();
		updateTime();
		timeElapse = 0;
		startedTime = 0;
	}

	
	@Override
	public synchronized void pause() {
		// TODO Auto-generated method stub
		super.pause();
		updateTime();
		
		long currentTime = System.currentTimeMillis();
		timeElapse = timeElapse + currentTime - startedTime;
		startedTime = 0;
	}

	
	@Override
	public synchronized void resume() {
		// TODO Auto-generated method stub
		super.resume();
		startedTime = System.currentTimeMillis();
		updateTime();
	}
	
	
	/**
	 * Stopping this counter and clearing associated information.
	 */
	public synchronized void stopAndClearAssoc() {
		stop();
		clearAssoc();
	}

	
	/**
	 * Clearing associated information.
	 */
	public synchronized void clearAssoc() {
		if (assocTxtTime != null) assocTxtTime.setText("");
		if (assocEvaluateInfo != null) assocEvaluateInfo.timeElapse = 0;
	}
	
	
	/**
	 * Updating the time counter in text form &quot;hours: minutes: seconds&quot; by current system time.
	 */
	private void updateTime() {
		if (startedTime == 0) return;
		
		long milis = getTimeElapse();
		String text = formatTime(milis);
		
		if (assocEvaluateInfo != null) assocEvaluateInfo.timeElapse = milis;
		if (assocTxtTime != null) assocTxtTime.setText(text);
	}
	
	
	/**
	 * Getting time elapse in miliseconds.
	 * @return time elapse in miliseconds.
	 */
	public synchronized long getTimeElapse() {
		if (startedTime == 0) return timeElapse;
		
		long currentTime = System.currentTimeMillis();
		return (timeElapse + currentTime - startedTime);
	}
	
	
	/**
	 * Setting time elapse in miliseconds.
	 * @param timeElapse time elapse in miliseconds.
	 */
	public synchronized void setTimeElapse(long timeElapse) {
		if (timeElapse >= 0) this.timeElapse = timeElapse;
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
	
	
}
