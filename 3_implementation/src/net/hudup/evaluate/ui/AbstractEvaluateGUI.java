/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate.ui;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.evaluate.EvaluateProcessor;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.EvaluatorProgressListener;
import net.hudup.core.evaluate.EvaluateInfo;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.CounterClock;

/**
 * This abstract class represents an abstract GUI to allow users to interact with {@link EvaluatorAbstract}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AbstractEvaluateGUI extends JPanel implements EvaluatorListener, EvaluatorProgressListener, SetupAlgListener, PluginChangedListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Main evaluator.
	 */
	protected Evaluator evaluator = null;

	
	/**
	 * Metrics as result of evaluation.
	 */
	protected Metrics result = null;
	
	
	/**
	 * Evaluation information as other result of evaluation.
	 */
	protected EvaluateInfo otherResult = null;

	
	/**
	 * Processor to process evaluation results.
	 */
	protected EvaluateProcessor evProcessor = new EvaluateProcessor();
	
	
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
	 * Table of algorithms.
	 */
	protected RegisterTable algRegTable = null;

	
	/**
	 * Dataset pool.
	 */
	protected DatasetPool pool = null;
	

	/**
	 * Internal counter clock.
	 */
	protected CounterClock counterClock = null;
	
	
	/**
	 * Evaluator GUI data.
	 */
	protected EvaluateGUIData referredData = null;
	
	
	/**
	 * Constructor with local evaluator.
	 * @param evaluator local evaluator.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator) {
		this(evaluator, null, null);
	}

	
	/**
	 * Constructor with specified evaluator and bound URI.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, xURI bindUri) {
		this(evaluator, bindUri, null);
	}
	
	
	/**
	 * Constructor with specified evaluator, bound URI, and referred GUI data.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 * @param referredData addition referred GUI data.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, xURI bindUri, EvaluateGUIData referredData) {
		this.bindUri = bindUri;
		if (bindUri != null) { //Evaluator is remote
			this.exportedStub = NetUtil.RegistryRemote.export(this, bindUri.getPort());
			if (this.exportedStub != null)
				LogUtil.info("Evaluator GUI exported at port " + bindUri.getPort());
			else
				LogUtil.info("Evaluator GUI failed to exported");
		}
		else { //Evaluator is local
			try {
				EvaluatorConfig config = evaluator.getConfig();
				if (!config.isAutoself()) { //Evaluator is auto-self.
					int evaluatorPort = config.getEvaluatorPort();
					evaluatorPort = NetUtil.getPort(evaluatorPort, true);
					
					evaluator.export(evaluatorPort);
					
					config.setEvaluatorPort(evaluatorPort);
				}
			} catch (Throwable e) {e.printStackTrace();}
		}

		setupListeners(evaluator);
		
		this.evaluator = evaluator;
		
		try {
			this.result = evaluator.getResult();
		} catch (RemoteException e) {e.printStackTrace();}

		try {
			this.otherResult = evaluator.getOtherResult();
		} catch (RemoteException e) {e.printStackTrace();}
		if (this.otherResult == null) this.otherResult = new EvaluateInfo();

		try {
			this.algRegTable = EvaluatorAbstract.extractAlgFromPluginStorage(evaluator);
		} catch (Throwable e) {e.printStackTrace();}
		if (this.algRegTable == null) this.algRegTable = new RegisterTable();
		
		this.pool = referredData != null && referredData.pool != null ? referredData.pool : new DatasetPool ();

		this.counterClock = new CounterClock();
		this.counterClock.setTimeElapse(this.otherResult.timeElapse);

		this.referredData = referredData;
		if (referredData != null) {
			referredData.wasRun = true;
			referredData.active = true;
		}

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
	 * Dispose this GUI.
	 */
	public void dispose() {
		boolean agent = false;
		try {
			agent = this.evaluator.getConfig().isAutoself();
		} 
		catch (Throwable e) {
			e.printStackTrace();
			agent = false;
		}
		
		if (agent) {
			unsetupListeners(this.evaluator);
		}
		else {
			stop();
			clear();

			unsetupListeners(this.evaluator);
			
			try {
				this.evaluator.close(); //The close() method also unexports evaluator.
			}
			catch (Exception e) {e.printStackTrace();}
		}
		
		this.counterClock.stop();
		this.evProcessor.clear();
		if (this.referredData != null) {
			extractGUIData().fillTo(this.referredData);
			this.referredData.active = false;
			this.referredData = null;
		}
		
		if (this.exportedStub != null) {
			boolean ret = NetUtil.RegistryRemote.unexport(this);
			if (ret)
				LogUtil.info("Evaluator GUI unexported successfully");
			else
				LogUtil.info("Evaluator GUI unexported failedly");
			this.exportedStub = null;
		}
		
	}
	
	
//	/**
//	 * Switching the inside evaluator by specified evaluator. This method is deprecated.
//	 * @param newEvaluator new specified evaluator.
//	 */
//	@Deprecated
//	protected void switchEvaluator(Evaluator newEvaluator) {
//		stop();
//		unsetupListeners(this.evaluator);
//		setupListeners(newEvaluator);
//		
//		updateMode();
//	}
	
	
	/**
	 * Change list of metrics in evaluator.
	 */
	protected void metricsOption() {
		try {
			if (evaluator.remoteIsStarted()) {
				LogUtil.error("Evaluator started, it is impossible to set up metric list");
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
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error("Error in setting metrics");
		}
	}
	
	
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


//	@Override
//	public boolean isSupportImport() {
//		// TODO Auto-generated method stub
//		return this.bindUri == null;
//	}


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

	
	/**
	 * Extracting GUI data.
	 * @return extracted GUI data.
	 */
	public abstract EvaluateGUIData extractGUIData();

	
}



