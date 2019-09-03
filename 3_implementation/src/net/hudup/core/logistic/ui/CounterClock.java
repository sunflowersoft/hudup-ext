package net.hudup.core.logistic.ui;

import javax.swing.JLabel;

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
public class CounterClock extends AbstractRunner {

	
	/**
	 * The text form of this counter clock &quot;hours: minutes: seconds&quot;.
	 */
	public static final String TIME_FORMAT = I18nUtil.message("time") + " %d:%d:%d";
	
	
	/**
	 * Time period in milisecond to update the counter.
	 */
	public static final long PERIOD = 1000; // 1 s
	
	
	/**
	 * The text pane to show the counter in text form &quot;hours: minutes: seconds&quot;.
	 */
	protected JLabel txtTime = null;
	
	
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
	 * Constructor with the specified text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 * @param txtTime specified text pane.
	 */
	public CounterClock(JLabel txtTime) {
		this();
		
		setTimeTextPane(txtTime);
	}
	
	
	/**
	 * Setting the text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 * @param txtTime text pane for showing the counter in text form &quot;hours: minutes: seconds&quot;.
	 */
	public void setTimeTextPane(JLabel txtTime) {
		this.txtTime = txtTime;
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
	 * Stopping this counter and clearing the text of elapsed time as empty text.
	 */
	public synchronized void stopAndClearText() {
		stop();
		clearText();
	}

	
	/**
	 * clearing the text of elapsed time as empty text.
	 */
	public synchronized void clearText() {
		txtTime.setText("");
	}
	
	
	/**
	 * Updating the time counter in text form &quot;hours: minutes: seconds&quot; by current system time.
	 */
	private void updateTime() {
		if (startedTime == 0)
			return;
		
		long currentTime = System.currentTimeMillis();
		long timeSum = (timeElapse + currentTime - startedTime) / 1000;
		long hours = timeSum / 3600;
		long minutes = (timeSum % 3600) / 60;
		long seconds = (timeSum % 3600) % 60;
		String text = String.format(TIME_FORMAT, hours, minutes, seconds);
		
		txtTime.setText(text);
	}
	
	
}
