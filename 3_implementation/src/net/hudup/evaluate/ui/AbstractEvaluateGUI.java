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
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.PluginChangedListener;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.evaluate.EvaluateInfo;
import net.hudup.core.evaluate.EvaluateListener;
import net.hudup.core.evaluate.EvaluateProcessor;
import net.hudup.core.evaluate.EvaluateProgressListener;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;

/**
 * This abstract class represents an abstract GUI to allow users to interact with {@link EvaluatorAbstract}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AbstractEvaluateGUI extends JPanel implements EvaluatorListener, EvaluateListener, EvaluateProgressListener, SetupAlgListener, PluginChangedListener, CounterElapsedTimeListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Prefix of evaluated file name.
	 */
	public static final String EV_RESULT_FILENAME_PREFIX = "gui";
	
	
	/**
	 * Main evaluator.
	 */
	protected Evaluator evaluator = null;

	
	/**
	 * Metrics as result of evaluation.
	 */
	protected Metrics result = null;
	
	
	/**
	 * Evaluation information as other result.
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
	 * Table of algorithms.
	 */
	protected RegisterTable algRegTable = null;

	
	/**
	 * Evaluator GUI data.
	 */
	protected EvaluateGUIData guiData = null;
	
	
	/**
	 * Time stamp.
	 */
	protected volatile long timestamp = 0;
	
	
	/**
	 * Constructor with local evaluator.
	 * @param evaluator local evaluator.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator) {
		this(evaluator, null, null, null);
	}

	
	/**
	 * Constructor with specified evaluator and bound URI.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, xURI bindUri) {
		this(evaluator, bindUri, null, null);
	}
	
	
	/**
	 * Constructor with local evaluator and referred algorithm.
	 * @param evaluator local evaluator.
	 * @param referredAlg referred algorithm.
	 */
	protected AbstractEvaluateGUI(Evaluator evaluator, Alg referredAlg) {
		this(evaluator, null, null, referredAlg);
	}

	
	/**
	 * Constructor with local evaluator and referred GUI data.
	 * @param evaluator local evaluator.
	 * @param referredGUIData referred GUI data.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, EvaluateGUIData referredGUIData) {
		this(evaluator, null, referredGUIData, null);
	}

	
	/**
	 * Constructor with specified evaluator, bound URI, and referred GUI data.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 * @param referredGUIData referred GUI data.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, xURI bindUri, EvaluateGUIData referredGUIData) {
		this(evaluator, bindUri, referredGUIData, null);
	}
	
	
	/**
	 * Constructor with specified evaluator, bound URI, referred GUI data, and referred algorithm.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 * @param referredGUIData referred GUI data.
	 * @param referredAlg referred algorithm.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, xURI bindUri, EvaluateGUIData referredGUIData, Alg referredAlg) {
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
				if (!evaluator.isAgent()) { //Evaluator is not agent.
					EvaluatorConfig config = evaluator.getConfig();
					int evaluatorPort = config.getEvaluatorPort();
					evaluatorPort = NetUtil.getPort(evaluatorPort, true);
					
					evaluator.export(evaluatorPort);
					
					config.setEvaluatorPort(evaluatorPort);
				}
			} catch (Throwable e) {e.printStackTrace();}
		}

		setupListeners(evaluator);
		
		this.evaluator = evaluator;
		
		if (referredAlg != null) {
			try {
				if (evaluator.acceptAlg(referredAlg))
					this.algRegTable = new RegisterTable(Arrays.asList(referredAlg));
			} catch (Throwable e) {e.printStackTrace();}
		}
		else {
			try {
				algRegTable = EvaluatorAbstract.extractAlgFromPluginStorage(evaluator);
			} catch (Throwable e) {e.printStackTrace();}
		}
		if (algRegTable == null) algRegTable = new RegisterTable();

		initGUIData(referredGUIData);
	}
	
	
	/**
	 * Initializing the evaluator batch GUI.
	 * @param referredGUIData referred GUI data.
	 */
	protected synchronized void initGUIData(EvaluateGUIData referredGUIData) {
		guiData = referredGUIData != null ? referredGUIData : new EvaluateGUIData(); 
		guiData.wasRun = true;
		guiData.active = true;
		
		try {
			result = evaluator.getResult(); //From server
		} catch (RemoteException e) {e.printStackTrace();}

		try {
			otherResult = evaluator.getOtherResult(); //From server
		} catch (RemoteException e) {e.printStackTrace();}
		if (otherResult == null) otherResult = new EvaluateInfo();

		boolean loadFromServer = false;
		try {
			loadFromServer = evaluator.remoteIsStarted(); //From server
		}
		catch (RemoteException e) {
			e.printStackTrace();
			loadFromServer = false;
		}
		loadFromServer = loadFromServer || result != null;
		
		if (loadFromServer) {
			guiData.algName = otherResult.algName;
			guiData.algNames = otherResult.algNames;
			
			try {
				guiData.pool = evaluator.getDatasetPool();
			} catch (Throwable e) {e.printStackTrace();}
		}
		
		if (guiData.algNames == null || guiData.algNames.size() == 0)
			guiData.algNames = algRegTable.getAlgNames();
		
		guiData.pool = guiData.pool != null ? guiData.pool : new DatasetPool();
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
				//updateMode(); //Evaluator calls instead
			}
			else if (evaluator.remoteIsRunning()) {
				evaluator.remotePause();
				//updateMode(); //Evaluator calls instead
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
			//updateMode(); //Evaluator calls instead
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
			//updateMode(); //Evaluator calls instead
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
		boolean wrapper = false;
		EvaluatorConfig config = null;
		try {
			agent = evaluator.isAgent();
			wrapper = evaluator.isWrapper();
			config = evaluator.getConfig();
			config.setSaveAbility(bindUri == null); //Save only local configuration.
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		if (agent) {
			if (wrapper) {
				unsetupListeners(evaluator);
				
				try {
					evaluator.close(); //The close() method also unexports evaluator wrapper.
				}
				catch (Exception e) {e.printStackTrace();}
			}
			else
				unsetupListeners(evaluator);
		}
		else {
			stop();
			clear();

			unsetupListeners(evaluator);
			
			try {
				evaluator.close(); //The close() method also unexports evaluator.
			}
			catch (Exception e) {e.printStackTrace();}
		}
		
		config.save();
		this.evProcessor.clear();
		updateGUIData();
		guiData.active = false;
		
		if (exportedStub != null) {
			boolean ret = NetUtil.RegistryRemote.unexport(this);
			if (ret)
				LogUtil.info("Evaluator GUI unexported successfully");
			else
				LogUtil.info("Evaluator GUI unexported failedly");
			exportedStub = null;
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
			evaluator.addEvaluateListener(this);
			evaluator.addEvaluateProgressListener(this);
			evaluator.addSetupAlgListener(this);
			evaluator.addElapsedTimeListener(this);
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
			evaluator.removeEvaluateListener(this);
			evaluator.removeEvaluateProgressListener(this);
			evaluator.removeSetupAlgListener(this);
			evaluator.removeElapsedTimeListener(this);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Updating GUI data.
	 */
	protected abstract void updateGUIData();

	
}



