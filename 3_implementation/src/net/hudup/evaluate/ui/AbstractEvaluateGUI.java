/**
 * 
 */
package net.hudup.evaluate.ui;

import java.io.IOException;
import java.nio.channels.ByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.log4j.Logger;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.EvaluatorProgressListener;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.UriAdapter.AdapterWriteChannel;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.CounterClock;


/**
 * This abstract class represents an abstract GUI to allow users to interact with {@link Evaluator}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AbstractEvaluateGUI extends JPanel implements EvaluatorListener, EvaluatorProgressListener, SetupAlgListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(AbstractEvaluateGUI.class);

	
	/**
	 * Evaluation file extension.
	 */
	protected final static String EVALUATION_FILE_EXTENSION = ".eval";
	
	
	/**
	 * Setting up doing mode file extension.
	 */
	protected final static String SETUP_DOING_FILE_EXTENSION = ".setup.doing";
	
	
	/**
	 * Setting up doing mode file extension.
	 */
	protected final static String SETUP_DONE_FILE_EXTENSION = ".setup.done";

	
	/**
	 * Metrics analyzing Excel file name.
	 */
	protected final static String METRICS_ANALYZE_EXCEL_FILE_NAME = "analyze.xls";
	
	
	/**
	 * Main evaluator.
	 */
	protected Evaluator evaluator = null;

	
	/**
	 * Metrics as result of evaluation.
	 */
	protected Metrics result = null;
	
	
	/**
	 * IO channels for IO writing evaluation results.
	 */
	protected Map<String, ByteChannel> ioChannels = Util.newMap();
	
	
	/**
	 * Internal counter clock.
	 */
	protected CounterClock counterClock = null;
	
	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified evaluator.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator) {
		setupListeners(evaluator);
		
		this.evaluator = evaluator;
		this.counterClock = new CounterClock();
	}
	
	
	/**
	 * Getting message by specified key.
	 * @param key specified key.
	 * @return message according to key.
	 */
	protected String getMessage(String key) {
		return I18nUtil.getMessage(evaluator.getConfig(), key);
		
	}
	
	
	/**
	 * Getting current algorithms.
	 * @return list of algorithms.
	 */
	protected abstract List<Alg> getCurrentAlgList();
	
	
	/**
	 * Responding to plug-ins changed. 
	 */
	public abstract void pluginChanged();
	
	
	/**
	 * Getting current evaluator.
	 * @return current {@link Evaluator}.
	 */
	public Evaluator getEvaluator() {
		return evaluator;
	}
	
	
	/**
	 * Refreshing GUI.
	 */
	protected abstract void refresh();
	
	
	/**
	 * Clearing text content in GUI.
	 */
	protected abstract void clear();
	
	
	/**
	 * Run evaluator.
	 */
	protected abstract void run();
	
	
	/**
	 * Pause/resume evaluator.
	 */
	protected void pauseResume() {
		if (evaluator.isPaused()) {
			evaluator.resume();
			counterClock.resume();
			updateMode();
		}
		else if (evaluator.isRunning()) {
			evaluator.pause();
			counterClock.pause();
			updateMode();
		}
		
	}

	
	/**
	 * Stop evaluator in secure manner.
	 */
	protected void stop() {
		evaluator.stop();
		counterClock.stop();
		updateMode();
	}
	
	
	/**
	 * Force to stop evaluator in insecure manner.
	 */
	protected void forceStop() {
		evaluator.forceStop();
		counterClock.stop();
		
		List<Alg> list = getCurrentAlgList();
		for (Alg alg : list) {
			if (alg instanceof Recommender)
				((Recommender)alg).unsetup();
		}
		
		updateMode();
	}
	
	
	/**
	 * Updating GUI according to mode of evaluator such as started, stopped, paused, resumed.
	 */
	protected abstract void updateMode();
	
	
	/**
	 * Close IO channels.
	 */
	protected void closeIOChannels() {
			
		Set<String> keys = ioChannels.keySet();
		for (String key : keys) {
			try {
				ioChannels.get(key).close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		ioChannels.clear();
		
	}
	
	
	/**
	 * Close IO channels by specified key.
	 * @param key specified key.
	 * @return whether close channel successfully.
	 */
	protected boolean closeIOChannel(String key) {
		if (!ioChannels.containsKey(key))
			return false;
		
		try {
			ByteChannel channel = ioChannels.get(key);
			channel.close();
			ioChannels.remove(key);
			
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	
	/**
	 * Getting channel from specified storage and key.
	 * @param store URI of specified storage.
	 * @param key specified key.
	 * @param append if true then the write channel allows appending writing.
	 * @return byte channel from specified storage and key.
	 */
	protected ByteChannel getIOChannel(xURI store, String key, boolean append) {
		if (ioChannels.containsKey(key))
			return ioChannels.get(key);
		
		xURI uri = store.concat(key);
		AdapterWriteChannel channel = new AdapterWriteChannel(uri, append);
		ioChannels.put(key, channel);
		return channel;
	}
	
	
	/**
	 * Dispose this GUI.
	 */
	public void dispose() {
		stop();
		clear();
		closeIOChannels();

		unsetupListeners(this.evaluator);
	}
	
	
	/**
	 * Switching the inside evaluator by specified evaluator.
	 * @param newEvaluator new specified evaluator.
	 */
	protected void switchEvaluator(Evaluator newEvaluator) {
		stop();
		unsetupListeners(this.evaluator);
		setupListeners(newEvaluator);
		
		updateMode();
	}
	
	
	/**
	 * Change list of metrics in evaluator.
	 */
	protected void metricsOption() {
		if (evaluator.isStarted()) {
			logger.error("Evaluator started, it is impossible to set up metric list");
			return;
		}
		
		MetricsOptionDlg dlg = new MetricsOptionDlg(this, evaluator.getMetricList());
		List<Metric> selectedMetricList = dlg.getSelectedMetricList();
		
		if (selectedMetricList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"No metrics option selected", 
					"No metrics option selected", 
					JOptionPane.WARNING_MESSAGE);
		}
		
		evaluator.setMetricList(selectedMetricList);
	}
	
	
	/**
	 * Add this GUI as listeners to specified evaluator.
	 * @param evaluator specified evaluator.
	 */
	private void setupListeners(Evaluator evaluator) {
		evaluator.addEvaluatorListener(this);
		evaluator.addProgressListener(this);
		evaluator.addSetupAlgListener(this);
	}
	
	
	/**
	 * Remove this GUI as listeners from specified evaluator.
	 * @param evaluator specified evaluator.
	 */
	private void unsetupListeners(Evaluator evaluator) {
		evaluator.removeEvaluatorListener(this);
		evaluator.removeProgressListener(this);
		evaluator.removeSetupAlgListener(this);
	}
	
	
}



