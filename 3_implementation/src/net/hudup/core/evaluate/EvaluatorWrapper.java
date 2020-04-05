/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.Timestamp;

/**
 * This class is wrapper of remote evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0s
 *
 */
@BaseClass //This is not base class but the base class annotation is used to prevent reflection of evaluators.
public class EvaluatorWrapper implements Evaluator, Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Exported stub as remote evaluator. It must be serializable.
     */
    protected Evaluator exportedStub = null;

	
	/**
	 * Exclusive mode.
	 */
	protected boolean exclusive = true;
	
	
	/**
	 * Internal remote evaluator. It must be serializable.
	 */
	protected Evaluator remoteEvaluator = null;
	
	
	/**
	 * Constructor with specified remote evaluator.
	 * @param remoteEvaluator remote evaluator.
	 */
	public EvaluatorWrapper(Evaluator remoteEvaluator) {
		this(remoteEvaluator, true);
	}

	
	/**
	 * Constructor with specified remote evaluator and exclusive mode.
	 * @param remoteEvaluator remote evaluator.
	 * @param exclusive exclusive mode.
	 */
	public EvaluatorWrapper(Evaluator remoteEvaluator, boolean exclusive) {
		this.remoteEvaluator = remoteEvaluator;
		this.exclusive = exclusive;
	}

	
	@Override
	public boolean remoteStart0(List<Alg> algList, DatasetPoolExchanged pool, Serializable parameter) throws RemoteException {
		return remoteEvaluator.remoteStart0(algList, pool, parameter);
	}


	@Override
	public boolean remoteStart(List<String> algNameList, DatasetPoolExchanged pool, ClassProcessor cp, DataConfig config, Serializable parameter) throws RemoteException {
		return remoteEvaluator.remoteStart(algNameList, pool, cp, config, parameter);
	}


	@Override
	public boolean remoteStart(Serializable... parameters) throws RemoteException {
		return remoteEvaluator.remoteStart(parameters);
	}

	
	@Override
	public boolean remotePause() throws RemoteException {
		return remoteEvaluator.remotePause();
	}

	
	@Override
	public boolean remoteResume() throws RemoteException {
		return remoteEvaluator.remoteResume();
	}

	
	@Override
	public boolean remoteStop() throws RemoteException {
		return remoteEvaluator.remoteStop();
	}

	
	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		remoteEvaluator.receivedSetup(evt);
	}

	
	@Override
	public void close() throws Exception {
		if (exclusive && remoteEvaluator != null) {
			try {
				if (!remoteEvaluator.isAgent())
					remoteEvaluator.close();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		remoteEvaluator = null;
		
		unexport();
	}

	
	@Override
	public boolean remoteForceStop() throws RemoteException {
		return remoteEvaluator.remoteForceStop();
	}

	
	@Override
	public boolean remoteIsStarted() throws RemoteException {
		return remoteEvaluator.remoteIsStarted();
	}

	
	@Override
	public boolean remoteIsPaused() throws RemoteException {
		return remoteEvaluator.remoteIsPaused();
	}

	
	@Override
	public boolean remoteIsRunning() throws RemoteException {
		return remoteEvaluator.remoteIsRunning();
	}

	
	@Override
	public String getName() throws RemoteException {
		return remoteEvaluator.getName();
	}

	
	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		return remoteEvaluator.getConfig();
	}

	
	@Override
	public void setConfig(EvaluatorConfig config) throws RemoteException {
		remoteEvaluator.setConfig(config);
	}


	@Override
	public boolean isWrapper() throws RemoteException {
		return true;
	}


	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		return remoteEvaluator.acceptAlg(alg);
	}

	
	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		return remoteEvaluator.defaultMetrics();
	}

	
	@Override
	public String getMainUnit() throws RemoteException {
		return remoteEvaluator.getMainUnit();
	}

	
	@Override
	public Metrics getResult() throws RemoteException {
		return remoteEvaluator.getResult();
	}

	
	@Override
	public EvaluateInfo getOtherResult() throws RemoteException {
		return remoteEvaluator.getOtherResult();
	}


	@Override
	public List<String> getMetricNameList() throws RemoteException {
		return remoteEvaluator.getMetricNameList();
	}

	
	@Override
	public void setMetricNameList(List<String> metricNameList) throws RemoteException {
		remoteEvaluator.setMetricNameList(metricNameList);
	}

	
	@Deprecated
	@Override
	public RegisterTable extractNormalAlgFromPluginStorage0() throws RemoteException {
		return remoteEvaluator.extractNormalAlgFromPluginStorage0();
	}

	
//	@Override
//	public void clearDelayUnsetupAlgs() throws RemoteException {
//		remoteEvaluator.clearDelayUnsetupAlgs();
//	}
//
//
//	@Override
//	public void clearResult(Timestamp timestamp) throws RemoteException {
//		remoteEvaluator.clearResult(timestamp);
//	}


	@Override
	public DatasetPoolExchanged getDatasetPool() throws RemoteException {
		return remoteEvaluator.getDatasetPool();
	}


	@Override
	public boolean updatePool(DatasetPoolExchanged pool, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.updatePool(pool, localTargetListener, timestamp);
	}


	@Override
	public boolean reloadPool(EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.reloadPool(localTargetListener, timestamp);
	}


	@Deprecated
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		return remoteEvaluator.getPluginStorage();
	}

	
	@Override
	public List<String> getPluginAlgNames(Class<? extends Alg> algClass) throws RemoteException {
		return remoteEvaluator.getPluginAlgNames(algClass);
	}


	@Override
	public AlgDesc2List getPluginAlgDescs(Class<? extends Alg> algClass) throws RemoteException {
		return remoteEvaluator.getPluginAlgDescs(algClass);
	}


	@Override
	public Alg getPluginAlg(Class<? extends Alg> algClass, String algName, boolean remote) throws RemoteException {
		return remoteEvaluator.getPluginAlg(algClass, algName, remote);
	}


	@Override
	public Alg getEvaluatedAlg(String algName, boolean remote) throws RemoteException {
		return remoteEvaluator.getEvaluatedAlg(algName, remote);
	}


	@Override
	public void pluginChanged(PluginChangedEvent evt) throws RemoteException {
		remoteEvaluator.pluginChanged(evt);
	}


	@Override
	public boolean isIdle() throws RemoteException {
		return remoteEvaluator.isIdle();
	}


	@Override
	public int getPort() throws RemoteException {
		return remoteEvaluator.getPort();
	}


	@Override
	public void addPluginChangedListener(PluginChangedListener listener) throws RemoteException {
		remoteEvaluator.addPluginChangedListener(listener);
	}


	@Override
	public void removePluginChangedListener(PluginChangedListener listener) throws RemoteException {
		remoteEvaluator.removePluginChangedListener(listener);
	}


	@Override
	public void addEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		remoteEvaluator.addEvaluatorListener(listener);
	}

	
	@Override
	public void removeEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		remoteEvaluator.removeEvaluatorListener(listener);
	}

	
	@Override
	public void addEvaluateListener(EvaluateListener listener) throws RemoteException {
		remoteEvaluator.addEvaluateListener(listener);
	}

	
	@Override
	public void removeEvaluateListener(EvaluateListener listener) throws RemoteException {
		remoteEvaluator.removeEvaluateListener(listener);
	}

	
	@Override
	public void addEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		remoteEvaluator.addEvaluateProgressListener(listener);
	}

	
	@Override
	public void removeEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		remoteEvaluator.removeEvaluateProgressListener(listener);
	}

	
	@Override
	public void addSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		remoteEvaluator.addSetupAlgListener(listener);
	}

	
	@Override
	public void removeSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		remoteEvaluator.removeSetupAlgListener(listener);
	}

	
	@Override
	public void addElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		remoteEvaluator.addElapsedTimeListener(listener);
	}


	@Override
	public void removeElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		remoteEvaluator.removeElapsedTimeListener(listener);
	}


	@Override
	public String getEvaluateStorePath() throws RemoteException {
		return remoteEvaluator.getEvaluateStorePath();
	}


	@Override
	public void setEvaluateStorePath(String evStorePath) throws RemoteException {
		remoteEvaluator.setEvaluateStorePath(evStorePath);
	}


	@Override
	public Service getReferredService() throws RemoteException {
		return remoteEvaluator.getReferredService();
	}


	@Override
	public void setReferredService(Service referredService) throws RemoteException {
		remoteEvaluator.setReferredService(referredService);
	}


	@Override
	public String toString() {
    	String evaluatorName = "No name";
		try {
			evaluatorName = getName();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		return DSUtil.shortenVerbalName(evaluatorName);
	}

	
	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exportedStub != null) return exportedStub;

		exportedStub = (Evaluator) NetUtil.RegistryRemote.export(this, serverPort);
		if (exportedStub != null)
			LogUtil.info("Evaluator served at port " + serverPort);
		else
			LogUtil.info("Evaluator failed to export");
			
		return exportedStub;
	}

	
	@Override
	public synchronized void unexport() throws RemoteException {
		if (exclusive && remoteEvaluator != null) {
			try {
				if (!remoteEvaluator.isAgent())
					remoteEvaluator.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		remoteEvaluator = null;
		
		if (exportedStub == null) return;
		boolean ret = NetUtil.RegistryRemote.unexport(this);
		exportedStub = null;
		if (ret)
			LogUtil.info("Evaluator unexported successfully");
		else
			LogUtil.info("Evaluator unexported failedly");
	}


	@Override
	public synchronized void forceUnexport() throws RemoteException {
		if (remoteEvaluator != null) {
			try {
				if (!remoteEvaluator.isAgent())
					remoteEvaluator.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
		}
		remoteEvaluator = null;
		
		unexport();
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	public boolean isAgent() throws RemoteException {
		return remoteEvaluator.isAgent();
	}


	@Override
	public void setAgent(boolean agent) throws RemoteException {
		remoteEvaluator.setAgent(agent);
	}


	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}

	
}
