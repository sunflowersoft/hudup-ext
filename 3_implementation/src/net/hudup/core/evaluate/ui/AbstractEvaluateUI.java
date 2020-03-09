package net.hudup.core.evaluate.ui;

import java.io.IOException;
import java.io.Serializable;
import java.nio.channels.ByteChannel;
import java.rmi.Remote;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.EvaluatorProgressListener;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter.AdapterWriteChannel;
import net.hudup.core.logistic.xURI;

/**
 * This abstract class represents an abstract UI to allow users to interact with evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
@NextUpdate
@Deprecated
public abstract class AbstractEvaluateUI implements EvaluatorListener, EvaluatorProgressListener, SetupAlgListener, PluginChangedListener, Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
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
	public final static String METRICS_ANALYZE_EXCEL_FILE_NAME = "analyze.xls";
	
	
	/**
	 * Metrics analyzing Excel file name.
	 */
	public final static String METRICS_ANALYZE_EXCEL_FILE_NAME2 = "analyze.hdp";

	
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
	 * Internal counter.
	 */
	protected Counter counterClock = null;
	
	
	/**
	 * Remote bind URI.
	 */
	protected xURI bindUri = null;
	
	
	/**
	 * Exported stub (EvaluatorListener, EvaluatorProgressListener, SetupAlgListener).
	 */
	protected Remote exportedStub = null;
	
	
	/**
	 * Internationalization utility.
	 */
	protected I18nUtil i18n = null;
	

	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI.
	 */
	public AbstractEvaluateUI(Evaluator evaluator, xURI bindUri) {
		this.bindUri = bindUri;
		if (bindUri != null) { //Evaluator is remote
			this.exportedStub = NetUtil.RegistryRemote.export(this, bindUri.getPort());
			if (this.exportedStub != null)
				LogUtil.info("Evaluator UI exported at port " + bindUri.getPort());
			else
				LogUtil.info("Evaluator UI failed to exported");
		}

		setupListeners(evaluator);
		
		this.evaluator = evaluator;
		this.counterClock = new Counter();
	}
	
	
	/**
	 * Getting current algorithms.
	 * @return list of algorithms.
	 */
	protected abstract List<Alg> getCurrentAlgList();
	
	
	/**
	 * Getting current evaluator.
	 * @return current evaluator.
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
		try {
			if (evaluator.remoteIsPaused()) {
				evaluator.remoteResume();
				counterClock.resume();
				updateMode();
			}
			else if (evaluator.remoteIsRunning()) {
				evaluator.remotePause();
				counterClock.pause();
				updateMode();
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			updateMode();
		}
	}

	
	/**
	 * Stop evaluator in secure manner.
	 */
	protected void stop() {
		try {
			evaluator.remoteStop();
			counterClock.stop();
			updateMode();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			updateMode(); //Added date: 2019.08.12 by Loc Nguyen
		}
	}
	
	
	/**
	 * Force to stop evaluator in insecure manner.
	 */
	protected void forceStop() {
		try {
			evaluator.remoteForceStop();
			counterClock.stop();
		
			List<Alg> list = getCurrentAlgList();
			for (Alg alg : list) {
				if (alg instanceof Recommender)
					((Recommender)alg).unsetup();
			}
			
			updateMode();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			updateMode(); //Added date: 2019.08.12 by Loc Nguyen
		}
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
		try {
			stop();
			clear();
			closeIOChannels();
	
			unsetupListeners(this.evaluator);
			
			if (this.exportedStub != null) {
				boolean ret = NetUtil.RegistryRemote.unexport(this);
				if (ret)
					LogUtil.info("Evaluator UI unexported successfully");
				else
					LogUtil.info("Evaluator UI unexported failedly");
				this.exportedStub = null;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Change list of metrics in evaluator.
	 */
	protected abstract void metricsOption();
	
	
	@Override
	public boolean isIdle() {
		// TODO Auto-generated method stub
		try {
			return !evaluator.remoteIsStarted();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public int getPort() {
		if (bindUri != null) //Evaluator is remote
			return bindUri.getPort();
		else { //Evaluator is local
			try {
				return evaluator.getConfig().getEvaluatorPort();
			} catch (Throwable e) {e.printStackTrace();}
			
			return -1;
		}
	}


	/**
	 * Add this GUI as listeners to specified evaluator.
	 * @param evaluator specified evaluator.
	 */
	private void setupListeners(Evaluator evaluator) {
		try {
			evaluator.addEvaluatorListener(this);
			evaluator.addProgressListener(this);
			evaluator.addSetupAlgListener(this);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Remove this GUI as listeners from specified evaluator.
	 * @param evaluator specified evaluator.
	 */
	private void unsetupListeners(Evaluator evaluator) {
		try {
			evaluator.removeEvaluatorListener(this);
			evaluator.removeProgressListener(this);
			evaluator.removeSetupAlgListener(this);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}



