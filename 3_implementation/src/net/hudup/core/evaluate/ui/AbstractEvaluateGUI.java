/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.ui;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetAbstract;
import net.hudup.core.data.DatasetPairExchanged;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.DatasetRemote;
import net.hudup.core.data.DatasetUtil;
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
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.xURI;

/**
 * This abstract class represents an abstract GUI to allow users to interact with {@link EvaluatorAbstract}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AbstractEvaluateGUI extends JPanel implements EvaluatorListener, EvaluateListener, EvaluateProgressListener, SetupAlgListener, PluginChangedListener, CounterElapsedTimeListener, ClassProcessor, Serializable {

	
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
	protected EvaluateProcessor evProcessor = null;
	
	
	/**
	 * Remote bind URI. This is exactly bound URI of this GUI. If this bound URI is not null, the remote evaluator connects to this GUI vis this bound URI.
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
	
	
//	/**
//	 * Network class loader server.
//	 */
//	protected SocketClassLoaderServer cl = null;
	
	
	/**
	 * Time stamp.
	 */
	protected Timestamp timestamp = null;
	
	
//	/**
//	 * Waiting timer.
//	 */
//	protected WaitTimer waitTimer = null;
	
	
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
			} catch (Throwable e) {LogUtil.trace(e);}
		}

		setupListeners(evaluator);
		
		this.evaluator = evaluator;
		this.evProcessor = new EvaluateProcessor(evaluator);
		
		updatePluginFromEvaluator();

		if (referredAlg != null) {
			try {
				if (evaluator.acceptAlg(referredAlg) && PluginStorage.getNormalAlgReg().contains(referredAlg.getName()))
					algRegTable = new RegisterTable(Arrays.asList(referredAlg));
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		else {
			try {
				algRegTable = EvaluatorAbstract.extractNormalAlgFromPluginStorage(evaluator, bindUri);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		if (algRegTable == null) algRegTable = new RegisterTable();

		guiData = referredGUIData != null ? referredGUIData : new EvaluateGUIData(); 
		guiData.wasRun = true;
		guiData.active = true;
		
		try {
			result = evaluator.getResult(); //From server
		} catch (RemoteException e) {LogUtil.trace(e);}

		try {
			otherResult = evaluator.getOtherResult(); //From server
		} catch (RemoteException e) {LogUtil.trace(e);}
		if (otherResult == null) otherResult = new EvaluateInfo();

		if (otherResult.algName != null)
			guiData.algName = otherResult.algName;
		if (otherResult.algNames != null && otherResult.algNames.size() > 0)
			guiData.algNames = otherResult.algNames;
		if (guiData.algNames == null || guiData.algNames.size() == 0)
			guiData.algNames = algRegTable.getAlgNames();
		else
			updateAlgRegFromEvaluator(guiData.algNames);
		
		DatasetPool oldPool = guiData.pool; 
		try {
			DatasetPoolExchanged pool = evaluator.getDatasetPool();
			guiData.pool = pool != null ? pool.toDatasetPoolClient() : null;
		} catch (Throwable e) {LogUtil.trace(e);}
		if (guiData.pool == null) {
			if (oldPool != null)
				guiData.pool = oldPool;
			else
				guiData.pool = new DatasetPool();
		}
		
		if (bindUri == null) {
			String evStorePath = null;
			try {
				evStorePath = evaluator.getEvaluateStorePath();
			} catch (Throwable e) {LogUtil.trace(e);}
			
			if (guiData.txtRunSaveBrowse == null)
				guiData.txtRunSaveBrowse = evStorePath;
			else if (evStorePath != null && !evStorePath.equals(guiData.txtRunSaveBrowse))
				guiData.txtRunSaveBrowse = evStorePath;
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
			boolean ret = true;
//			synchronized (this) {
				if (evaluator.remoteIsPaused())
					ret = evaluator.remoteResume();
				else if (evaluator.remoteIsRunning())
					ret = evaluator.remotePause();
//				wait();
//			}
			if (!ret) updateMode();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			updateMode();
		}
	}

	
	/**
	 * Stop evaluator in secure manner.
	 */
	protected void stop() {
		try {
			boolean ret = true;
//			synchronized (this) {
				ret = evaluator.remoteStop();
//				wait();
//			}
			if (!ret) updateMode();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
			updateMode(); //Added date: 2019.08.12 by Loc Nguyen
		}
	}
	
	
	/**
	 * Force to stop evaluator in insecure manner.
	 */
	protected void forceStop() {
		try {
			boolean ret = true;
//			synchronized (this) {
				ret = evaluator.remoteForceStop();
//				wait();
//			}
			if (!ret) updateMode();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
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
		try {
			agent = evaluator.isAgent();
			wrapper = evaluator.isWrapper();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		if (agent) {
			if (wrapper) {
				unsetupListeners(evaluator);
				
				try {
					evaluator.close(); //The close() method also unexports evaluator wrapper.
				}
				catch (Exception e) {LogUtil.trace(e);}
			}
			else
				unsetupListeners(evaluator);
		}
		else {
			stop();
//			clear();

			unsetupListeners(evaluator);
			
			try {
				evaluator.close(); //The close() method also unexports evaluator.
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
		
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
	
	
	/**
	 * Change list of metrics in evaluator. Current implementation does not export metrics. Exporting normal algorithms only.
	 */
	protected void metricsOption() {
		try {
			if (evaluator.remoteIsStarted()) {
				LogUtil.error("Evaluator started, it is impossible to set up metric list");
				return;
			}

			List<String> metricNameList = evaluator.getMetricNameList();
			List<Metric> metricList = Util.newList();
			for (String metricName : metricNameList) {
				if (PluginStorage.getMetricReg().contains(metricName))
					metricList.add((Metric)PluginStorage.getMetricReg().query(metricName));
			}
			
			MetricsOptionDlg dlg = new MetricsOptionDlg(this, metricList);
			List<Metric> selectedMetricList = dlg.getSelectedMetricList();
			
			if (selectedMetricList.size() == 0) {
				JOptionPane.showMessageDialog(
						this, 
						"No metrics option selected", 
						"No metrics option selected", 
						JOptionPane.WARNING_MESSAGE);
			}
			
			evaluator.setMetricNameList(MetricsUtil.extractMetricNameList(selectedMetricList));
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
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
			LogUtil.trace(e);
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
			} catch (Throwable e) {LogUtil.trace(e);}
			
			return -1;
		}
	}


//	@Override
//	public void requireCleanupSomething() {
//		// TODO Auto-generated method stub
//		try {
//			if (bindUri == null)
//				evaluator.clearDelayUnsetupAlgs();
//		}
//		catch (Throwable e) {
//			LogUtil.trace(e);
//		}
//	}


	/**
	 * Getting bound URI.
	 * @return bound URI.
	 */
	public xURI getBindUri() {
		return bindUri;
	}
	
	
	@Override
	public byte[] getByteCode(String className) throws RemoteException {
		// TODO Auto-generated method stub
		return ClassProcessor.getByteCode0(className);
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
			LogUtil.trace(e);
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
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Updating GUI data.
	 */
	protected abstract void updateGUIData();

	
	/**
	 * Converting normal dataset pool to exchanged dataset in client. This method is called by evaluator GUI.
	 * @param pool normal dataset pool.
	 * @return exchanged dataset in client.
	 */
	protected DatasetPoolExchanged toDatasetPoolExchangedClient(DatasetPool pool) {
		if (pool == null) return null;
		
		DatasetPoolExchanged exchangedPool = pool.toDatasetPoolExchangedClient();
		if (bindUri == null) return exchangedPool;
		
		for (DatasetPairExchanged pair : exchangedPool.dspList) {
			setupDatasetExchanged(pair.training);
			setupDatasetExchanged(pair.testing);
			setupDatasetExchanged(pair.whole);
		}
		
		return exchangedPool;
	}
	
	
	/**
	 * Setting specified remote dataset.
	 * @param remoteDataset specified remote dataset.
	 */
	private void setupDatasetExchanged(DatasetRemote remoteDataset) {
		if (remoteDataset == null || bindUri == null) return;
		if (DatasetUtil.getMostInnerDataset(remoteDataset) == null)
			return;
		
		try {
			DataConfig config = remoteDataset.remoteGetConfig();
			if (config == null) return;
			
			if (!config.containsKey(DatasetAbstract.ONLY_MEMORY_FIELD))
				config.put(DatasetAbstract.ONLY_MEMORY_FIELD, true);
			
			if (Constants.hardwareAddress != null && Constants.hostAddress != null &&
					!config.containsKey(DatasetAbstract.HARDWARE_ADDR_FIELD) &&
					!config.containsKey(DatasetAbstract.HOST_ADDR_FIELD)) {
				config.put(DatasetAbstract.HARDWARE_ADDR_FIELD, Constants.hardwareAddress);
				config.put(DatasetAbstract.HOST_ADDR_FIELD, Constants.hostAddress);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
//	/**
//	 * Synchronizing plug-in storage with evaluator.
//	 */
//	protected void syncPluginWithEvaluator() {
//		if (bindUri == null || evaluator == null) return;
//		
//		//Current version only synchronize normal algorithm and metric plug-in
//		PluginStorage.syncWithEvaluator(evaluator, Alg.class, true);
//		PluginStorage.syncWithEvaluator(evaluator, Metric.class, true);
//	}
	
	
	/**
	 * Updating plug-in storage from evaluator.
	 */
	protected void updatePluginFromEvaluator() {
		if (bindUri == null || evaluator == null) return;
		
		//Current version only update normal algorithm and metric plug-in
		PluginStorage.updateFromEvaluator(evaluator, Alg.class, true);
		PluginStorage.updateFromEvaluator(evaluator, Metric.class, true);
	}

	
	/**
	 * Updating algorithm register table with evaluator.
	 * @return list of possible algorithm names.
	 */
	protected List<String> updateAlgRegFromEvaluator() {
		List<String> algNames = Util.newList();
		if (evaluator == null) return algNames;
		try {
			algNames = evaluator.getOtherResult().algNames;
		} catch (Exception e) {LogUtil.trace(e);}
		
		updateAlgRegFromEvaluator(algNames);
		
		return algNames;
	}
	
	
	/**
	 * Updating algorithm register table from evaluator and list of algorithm names.
	 * @param algNames list of algorithm names.
	 */
	protected void updateAlgRegFromEvaluator(List<String> algNames) {
		if (evaluator == null || algNames == null || algNames.size() == 0)
			return;
		
//		List<String> regAlgNames = algRegTable.getAlgNames();
//		for (String regAlgName : regAlgNames) {
//			Alg alg = algRegTable.query(regAlgName);
//			if (!PluginStorage.contains(alg)) {
//				try {
//					if (alg instanceof AlgRemote)
//						((AlgRemote)alg).unexport();
//				} catch (Exception e) {LogUtil.trace(e);}
//				
//				algRegTable.unregister(regAlgName);
//			}
//		}
		
		for (String algName : algNames) {
			if (algRegTable.contains(algName))
				continue;
			else if (PluginStorage.getNormalAlgReg().contains(algName))
				algRegTable.register(PluginStorage.getNormalAlgReg().query(algName));
			else {
				try {
					Alg alg = evaluator.getEvaluatedAlg(algName, bindUri != null);
					if (alg != null) algRegTable.register(alg);
				}
				catch (Exception e) {LogUtil.trace(e);}
			}
		}
	}
	
	
}



