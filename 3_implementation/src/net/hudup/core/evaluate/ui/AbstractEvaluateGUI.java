/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.ui;

import java.io.Serializable;
import java.rmi.Naming;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.util.Arrays;
import java.util.EventObject;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetAbstract;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPairExchanged;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.DatasetRemote;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Exportable;
import net.hudup.core.evaluate.EvaluateEvent;
import net.hudup.core.evaluate.EvaluateInfo;
import net.hudup.core.evaluate.EvaluateListener;
import net.hudup.core.evaluate.EvaluateProcessor;
import net.hudup.core.evaluate.EvaluateProgressEvent;
import net.hudup.core.evaluate.EvaluateProgressListener;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.EvaluatorWrapperExt;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.CounterElapsedTimeEvent;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.Timer;
import net.hudup.core.logistic.NetUtil.RegistryRemote;
import net.hudup.core.logistic.Timestamp;

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
	 * Identifier of this GUI.
	 */
	protected UUID id = null;
	
	
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
	 * Recovery of resulted metrics.
	 */
	protected Metrics recoveredResult = null;

	
	/**
	 * Processor to process evaluation results.
	 */
	protected EvaluateProcessor evProcessor = null;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = new ConnectInfo();
	
	
	/**
	 * RMI registry for exposing this GUI and associated non-agent evaluator.
	 */
	protected Registry registry = null;

	
	/**
	 * Exported stub of this GUI.
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
	protected Timestamp timestamp = null;
	
	
	/**
	 * Feeding task queue from server.
	 */
	protected AbstractRunner taskQueueFeedee = null;
	
	
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
	 * @param connectInfo connection information.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, ConnectInfo connectInfo) {
		this(evaluator, connectInfo, null, null);
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
	 * @param connectInfo connection information.
	 * @param referredGUIData referred GUI data.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, ConnectInfo connectInfo, EvaluateGUIData referredGUIData) {
		this(evaluator, connectInfo, referredGUIData, null);
	}
	
	
	/**
	 * Constructor with specified evaluator, bound URI, referred GUI data, and referred algorithm.
	 * @param evaluator specified evaluator.
	 * @param connectInfo connection information.
	 * @param referredGUIData referred GUI data.
	 * @param referredAlg referred algorithm.
	 */
	public AbstractEvaluateGUI(Evaluator evaluator, ConnectInfo connectInfo, EvaluateGUIData referredGUIData, Alg referredAlg) {
		this.id = UUID.randomUUID();
		this.evaluator = evaluator;
		connectInfo = connectInfo != null ? connectInfo : new ConnectInfo();
		this.connectInfo = connectInfo;
		
		try {
			String globalAddress = connectInfo.extractGlobalAddress();
			if (globalAddress != null) {
				System.setProperty("java.rmi.server.hostname", globalAddress);
				LogUtil.info("java.rmi.server.hostname=" + globalAddress);
			}
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			if (connectInfo.bindUri != null) { //Evaluator is remote
				if (connectInfo.namingUri == null) {
					this.exportedStub = NetUtil.RegistryRemote.export(this, connectInfo.bindUri.getPort()); //Exporting this GUI
					if (this.exportedStub != null)
						LogUtil.info("EVALUATOR GUI EXPORTED AT PORT " + connectInfo.bindUri.getPort());
					else
						LogUtil.info("Evaluator GUI failed to export");
				}
				else {
					RegistryRemote rr = NetUtil.RegistryRemote.registerExport(this, connectInfo.namingUri);
					if (rr != null) {
						exportedStub = rr.getStub();
						registry = rr.getRegistry();
						this.evaluator = new EvaluatorWrapperExt(evaluator, connectInfo.namingUri.getPort()) {

							/**
							 * Default serial version UID.
							 */
							private static final long serialVersionUID = 1L;
							
						};
						
						Naming.rebind(connectInfo.namingUri.toString(), this.evaluator);
						
						LogUtil.info("EVALUATOR AND EVALUATOR GUI EXPORTED AND NAMED AT PORT " + connectInfo.namingUri.getPort());
					}
					else {
						connectInfo.namingUri = null;
						LogUtil.info("Evaluator and evaluator GUI failed to export");
					}
				}
			}
			else { //Evaluator is local
				if (this.evaluator.isAgent()) { //Evaluator is agent.
					connectInfo.namingUri = null;
				}
				else if (connectInfo.namingUri != null) { //Exporting the evaluator.
					RegistryRemote rr = NetUtil.RegistryRemote.registerExport(this, connectInfo.namingUri);
					if (rr != null) {
						exportedStub = rr.getStub();
						registry = rr.getRegistry();
						
						this.evaluator.export(connectInfo.namingUri.getPort());
						this.evaluator.getConfig().setEvaluatorPort(connectInfo.namingUri.getPort());
						this.evaluator.setAgent(true); //It is OK to set this local evaluator to be agent here.
						Naming.rebind(connectInfo.namingUri.toString(), this.evaluator);
						
						LogUtil.info("EVALUATOR AND EVALUATOR GUI EXPORTED AND NAMED AT PORT " + connectInfo.namingUri.getPort());
					}
					else {
						connectInfo.namingUri = null;
						LogUtil.info("Evaluator and evaluator GUI failed to export");
					}
				}
			}
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		
		evProcessor = new EvaluateProcessor(this.evaluator);
		
		updatePluginFromEvaluator();

		if (referredAlg != null) {
			if (EvaluatorAbstract.acceptAlg(this.evaluator, referredAlg, connectInfo) && PluginStorage.getNormalAlgReg().contains(referredAlg.getName()))
				algRegTable = new RegisterTable(Arrays.asList(referredAlg));
		}
		else {
			algRegTable = EvaluatorAbstract.extractNormalAlgFromPluginStorage(this.evaluator, connectInfo);
		}
		if (algRegTable == null) algRegTable = new RegisterTable();

		guiData = referredGUIData != null ? referredGUIData : new EvaluateGUIData(); 
		guiData.wasRun = true;
		guiData.active = true;
		
		try {
			recoveredResult = result = this.evaluator.getResult(); //From server
		} catch (RemoteException e) {LogUtil.trace(e);}

		try {
			otherResult = this.evaluator.getOtherResult(); //From server
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
			DatasetPoolExchanged pool = this.evaluator.getDatasetPool();
			guiData.pool = pool != null ? pool.toDatasetPoolClient() : null;
		} catch (Throwable e) {LogUtil.trace(e);}
		if (guiData.pool == null) {
			if (oldPool != null)
				guiData.pool = oldPool;
			else
				guiData.pool = new DatasetPool();
		}
		
		if (connectInfo.bindUri == null) {
			String evStorePath = null;
			try {
				evStorePath = this.evaluator.getEvaluateStorePath();
			} catch (Throwable e) {LogUtil.trace(e);}
			
			if (guiData.txtRunSaveBrowse == null)
				guiData.txtRunSaveBrowse = evStorePath;
			else if (evStorePath != null && !evStorePath.equals(guiData.txtRunSaveBrowse))
				guiData.txtRunSaveBrowse = evStorePath;
		}

		if (connectInfo.checkPullMode()) {
			taskQueueFeedee = new Timer(0, connectInfo.accessPeriod) {
				
				@Override
				protected void task() {
					taskQueueFeed();
				}
				
				@Override
				protected void clear() {}
			};
			
			taskQueueFeedee.start();
		}
				
	}
	
	
	/**
	 * Feeding tasks from server.
	 * @return true if connecting to server is successful.
	 */
	protected synchronized boolean taskQueueFeed() {
		boolean connected = true;
		List<EventObject> evtList = Util.newList();
		try {
			evtList = evaluator.doTask(id);
		}
		catch (Throwable e) {connected = false; LogUtil.trace(e);}
		
		for (EventObject evt : evtList) {
			try {
				if (evt instanceof PluginChangedEvent) {
					pluginChanged((PluginChangedEvent)evt);
				}
				else if (evt instanceof EvaluatorEvent) {
					receivedEvaluator((EvaluatorEvent)evt);
				}
				else if (evt instanceof EvaluateEvent) {
					receivedEvaluation((EvaluateEvent)evt);
				}
				else if (evt instanceof EvaluateProgressEvent) {
					receivedProgress((EvaluateProgressEvent)evt);
				}
				else if (evt instanceof SetupAlgEvent) {
					receivedSetup((SetupAlgEvent)evt);
				}
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		
		try {
			EvaluateInfo otherResult = evaluator.getOtherResult();
			if (otherResult != null) {
				receivedElapsedTime(new CounterElapsedTimeEvent(new Counter(), otherResult.elapsedTime));
			}
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		return connected;
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
	 * Getting evaluated result.
	 * @return evaluated result.
	 */
	public Metrics getResult() {
		return result;
	}
	
	
	/**
	 * Getting recovered result.
	 * @return recovered result.
	 */
	public Metrics getRecoveredResult() {
		return recoveredResult;
	}

	
	/**
	 * Getting registered algorithm table.
	 * @return registered algorithm table.
	 */
	public RegisterTable getAlgRegTable() {
		return algRegTable;
	}
	
	
	/**
	 * Refreshing evaluated result. Please distinguish this method from method {@link #refresh()}
	 * when method {@link #refresh()} refreshes data pool and local GUI.
	 * This method will connect to server in pull mode.
	 */
	public synchronized void refreshResult() {
		if (connectInfo.checkPullMode()) {
			taskQueueFeed();
		}
		else {
			Metrics result = null;
			boolean success = true;
			try {
				result = evaluator.getResult();
			} catch (Exception ex) {success = false; LogUtil.trace(ex);}
	
			if (success) {
				if (result == null) {
					this.recoveredResult = this.result;
					this.result = result;
				}
				else
					this.recoveredResult = this.result = result;
			}
			
			updateMode();
		}
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
			if (evaluator.remoteIsPaused())
				ret = evaluator.remoteResume();
			else if (evaluator.remoteIsRunning()) {
				ret = evaluator.remotePause();
				
				try {
					this.recoveredResult = evaluator.getResult();
				} catch (Exception e) {LogUtil.trace(e);}
			}
			
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
			ret = evaluator.remoteStop();
			if (!ret) updateMode();
		}
		catch (Throwable e) {
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
			ret = evaluator.remoteForceStop();
			if (!ret) updateMode();
		}
		catch (Throwable e) {
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
		try {
			if (connectInfo.namingUri != null) {
				Naming.unbind(connectInfo.namingUri.toString());
				LogUtil.info("Evaluator unbound successfully");
			}
		}
		catch (Exception e) {
			LogUtil.info("Evaluator unbound failedly");
			LogUtil.trace(e);
		}
		
		
		boolean agent = false;
		boolean delegate = false;
		try {
			agent = evaluator.isAgent();
			delegate = evaluator.isDelegate();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		if (agent && connectInfo.namingUri == null) {
			if (delegate) {
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

			unsetupListeners(evaluator);
			
			try {
				evaluator.close(); //The close() method also unexports evaluator.
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
		
		result = null;
		
		evProcessor.clear();
		updateGUIData();
		guiData.active = false;
		
		try {
			algRegTable.unexportNonPluginAlgs();
		} catch (Exception e) {LogUtil.trace(e);}
		
		if (taskQueueFeedee != null) {
			taskQueueFeedee.stop();
			taskQueueFeedee = null;
		}

		if (exportedStub != null) {
			boolean ret = NetUtil.RegistryRemote.unexport(this);
			if (ret)
				LogUtil.info("Evaluator GUI unexported successfully");
			else
				LogUtil.info("Evaluator GUI unexported failedly");
			exportedStub = null;
		}
		
		if (registry != null) {
			boolean ret = NetUtil.RegistryRemote.unexport(registry);
			if (ret)
				LogUtil.info("Registry unexported successfully");
			else
				LogUtil.info("Registry unexported failedly");
			registry = null;
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
			LogUtil.trace(e);
			LogUtil.error("Error in setting metrics");
		}
	}
	
	
	@Override
	public boolean isIdle() throws RemoteException {
		try {
			return !evaluator.remoteIsStarted();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			return false;
		}
	}


	@Override
	public int getPort() throws RemoteException {
		if (connectInfo.bindUri != null) //Evaluator is remote
			return connectInfo.bindUri.getPort();
		else { //Evaluator is local
			try {
				return evaluator.getConfig().getEvaluatorPort();
			} catch (Throwable e) {LogUtil.trace(e);}
			
			return 0;
		}
	}


	/**
	 * Getting connection information.
	 * @return connection information.
	 */
	public ConnectInfo getConnectInfo() {
		return connectInfo;
	}
	
	
	@Override
	public byte[] getByteCode(String className) throws RemoteException {
		return ClassProcessor.getByteCode0(className);
	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


//	/**
//	 * Reconnecting to server. This method is only effective in pull mode.
//	 * @return true if reconnecting to server is success.
//	 */
//	public synchronized boolean reconnectServer() {
//		if (!connectInfo.isPullMode()) return false;
//		
//		unsetupListeners(evaluator);
//		return setupListeners(evaluator);
//	}
	
	
	/**
	 * Add this GUI as listeners to specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return true if unsetting up listeners successfully.
	 */
	protected synchronized boolean setupListeners(Evaluator evaluator) {
		try {
			if (connectInfo.checkPullMode()) {
				evaluator.addPluginChangedListener(id);
				evaluator.addEvaluatorListener(id);
				evaluator.addEvaluateListener(id);
				evaluator.addEvaluateProgressListener(id);
				evaluator.addSetupAlgListener(id);
			}
			else {
				evaluator.addPluginChangedListener(this);
				evaluator.addEvaluatorListener(this);
				evaluator.addEvaluateListener(this);
				evaluator.addEvaluateProgressListener(this);
				evaluator.addSetupAlgListener(this);
				evaluator.addElapsedTimeListener(this);
			}
			
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}
	
	
	/**
	 * Remove this GUI as listeners from specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return true if unsetting up listeners successfully.
	 */
	protected synchronized boolean unsetupListeners(Evaluator evaluator) {
		try {
			if (connectInfo.checkPullMode()) {
				evaluator.removePluginChangedListener(id);
				evaluator.removeEvaluatorListener(id);
				evaluator.removeEvaluateListener(id);
				evaluator.removeEvaluateProgressListener(id);
				evaluator.removeSetupAlgListener(id);
			}
			else {
				evaluator.removePluginChangedListener(this);
				evaluator.removeEvaluatorListener(this);
				evaluator.removeEvaluateListener(this);
				evaluator.removeEvaluateProgressListener(this);
				evaluator.removeSetupAlgListener(this);
				evaluator.removeElapsedTimeListener(this);
			}
			
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
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
		if (connectInfo.bindUri == null) return exchangedPool;
		
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
		if (remoteDataset == null || connectInfo.bindUri == null) return;
		if (DatasetUtil.getMostInnerDataset2(remoteDataset) == null)
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
				
				String globalAddress = connectInfo.extractGlobalAddress();
				String hostAddr = globalAddress != null ? globalAddress : Constants.hostAddress;
				config.put(DatasetAbstract.HOST_ADDR_FIELD, hostAddr);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Getting local dataset pool which contains unuploaded datasets. 
	 * @return local dataset pool which contains unuploaded datasets.
	 */
	protected DatasetPool getLocalDatasetPool() {
		DatasetPool newPool= new DatasetPool();
		for (int i = 0; i < guiData.pool.size(); i++) {
			DatasetPair pair = guiData.pool.get(i);
			boolean added = true;
			added = added && pair.getTrainingUUID() == null && pair.getTestingUUID() == null && pair.getWholeUUID() == null;
			added = added && pair.getTraining() != null && pair.getTesting() != null;
			
			if (added) newPool.add(pair);
		}
		
		return newPool;
	}
	
	
	/**
	 * Updating both plug-in storage, evaluated algorithms, and dataset pool from server.
	 */
	public synchronized void updateFromServer() {
		updatePluginFromEvaluator();
		updateAlgRegFromEvaluator();
		
		try {
			pluginChanged(new PluginChangedEvent(this));
		} catch (Exception e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * Updating plug-in storage from evaluator.
	 */
	protected void updatePluginFromEvaluator() {
		if (connectInfo.bindUri == null || evaluator == null) return;
		
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
			EvaluateInfo otherResult = evaluator.getOtherResult();
			algNames = otherResult != null ? otherResult.algNames : null;
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
		
		RegisterTable normalAlgReg = PluginStorage.getNormalAlgReg();
		for (String algName : algNames) {
			if (algRegTable.contains(algName)) {
				if (normalAlgReg.contains(algName))
					continue;
				else {
					Alg alg = algRegTable.query(algName);
					if (!AlgDesc2.canCallRemote(alg)) {
						algRegTable.unregister(algName);
						try {
							if (alg instanceof Exportable) ((Exportable)alg).unexport();
							alg = evaluator.getEvaluatedAlg(algName, connectInfo.bindUri != null);
							if (alg != null) algRegTable.register(alg);
						} catch (Exception e) {LogUtil.trace(e);}
					}
				}
			}
			else if (normalAlgReg.contains(algName))
				algRegTable.register(normalAlgReg.query(algName));
			else {
				try {
					Alg alg = evaluator.getEvaluatedAlg(algName, connectInfo.bindUri != null);
					if (alg != null) algRegTable.register(alg);
				}
				catch (Exception e) {LogUtil.trace(e);}
			}
		}
		
		if (connectInfo.bindUri == null) return;
		
		algNames = algRegTable.getAlgNames();
		for (String algName : algNames) {
			try {
				Alg alg = algRegTable.query(algName);
				AlgDesc algDesc = evaluator.getPluginAlgDesc(alg.getClass(), alg.getName());
				if (algDesc != null) alg.getConfig().putAll(algDesc.getConfig());
			} catch (Exception e) {LogUtil.error("Error when evaluator gets plug-in algorithm, caused by " + e.getMessage());}
		}
	}
	
	
}



