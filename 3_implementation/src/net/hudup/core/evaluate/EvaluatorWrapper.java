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

import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NextUpdate;

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
	 * Defautl serial version UID.
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
		// TODO Auto-generated constructor stub
		this(remoteEvaluator, true);
	}

	
	/**
	 * Constructor with specified remote evaluator and exclusive mode.
	 * @param remoteEvaluator remote evaluator.
	 * @param exclusive exclusive mode.
	 */
	public EvaluatorWrapper(Evaluator remoteEvaluator, boolean exclusive) {
		// TODO Auto-generated constructor stub
		this.remoteEvaluator = remoteEvaluator;
		this.exclusive = exclusive;
	}

	
	@Override
	public boolean remoteStart(Serializable... parameters) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteStart(parameters);
	}

	
	@Override
	public boolean remoteStart(List<Alg> algList, DatasetPool pool, Serializable parameter) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteStart(algList, pool, parameter);
	}


	@Override
	public boolean remotePause() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remotePause();
	}

	
	@Override
	public boolean remoteResume() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteResume();
	}

	
	@Override
	public boolean remoteStop() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteStop();
	}

	
	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.receivedSetup(evt);
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (exclusive && remoteEvaluator != null) {
			try {
				if (!remoteEvaluator.isAgent())
					remoteEvaluator.close();
			} catch (Exception e) {e.printStackTrace();}
		}
		remoteEvaluator = null;
		
		unexport();
	}

	
	@Override
	public boolean remoteForceStop() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteForceStop();
	}

	
	@Override
	public boolean remoteIsStarted() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteIsStarted();
	}

	
	@Override
	public boolean remoteIsPaused() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteIsPaused();
	}

	
	@Override
	public boolean remoteIsRunning() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteIsRunning();
	}

	
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getName();
	}

	
	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getConfig();
	}

	
	@Override
	public boolean isWrapper() throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.acceptAlg(alg);
	}

	
	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.defaultMetrics();
	}

	
	@Override
	public String getMainUnit() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getMainUnit();
	}

	
	@Override
	public Metrics getResult() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getResult();
	}

	
	@Override
	public EvaluateInfo getOtherResult() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getOtherResult();
	}


	@Override
	public List<Metric> getMetricList() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getMetricList();
	}

	
	@Override
	public void setMetricList(List<Metric> metricList) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.setMetricList(metricList);
	}

	
	@Deprecated
	@Override
	public RegisterTable extractAlgFromPluginStorage0() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.extractAlgFromPluginStorage0();
	}

	
	@Override
	public void clearDelayUnsetupAlgs() throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.clearDelayUnsetupAlgs();
	}


//	@Override
//	public List<String> getAlgNames() throws RemoteException {
//		// TODO Auto-generated method stub
//		return remoteEvaluator.getAlgNames();
//	}

	
	@NextUpdate
	@Override
	public DatasetPool getDatasetPool() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getDatasetPool();
	}


	@Deprecated
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getPluginStorage();
	}

	
	@Override
	public List<String> getPluginAlgNames(Class<? extends Alg> algClass) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getPluginAlgNames(algClass);
	}


	@Override
	public AlgDesc2List getPluginAlgDescs(Class<? extends Alg> algClass) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getPluginAlgDescs(algClass);
	}


	@Override
	public Alg getPluginAlgCloned(Class<? extends Alg> algClass, String algName) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getPluginAlgCloned(algClass, algName);
	}


	@Override
	public void addEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.addEvaluatorListener(listener);
	}

	
	@Override
	public void removeEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.removeEvaluatorListener(listener);
	}

	
	@Override
	public void addEvaluateListener(EvaluateListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.addEvaluateListener(listener);
	}

	
	@Override
	public void removeEvaluateListener(EvaluateListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.removeEvaluateListener(listener);
	}

	
	@Override
	public void addEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.addEvaluateProgressListener(listener);
	}

	
	@Override
	public void removeEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.removeEvaluateProgressListener(listener);
	}

	
	@Override
	public void addSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.addSetupAlgListener(listener);
	}

	
	@Override
	public void removeSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.removeSetupAlgListener(listener);
	}

	
	@Override
	public void setEvaluateStorePath(String evStorePath) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.setEvaluateStorePath(evStorePath);
	}


	@Override
	public void addElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.addElapsedTimeListener(listener);
	}


	@Override
	public void removeElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.removeElapsedTimeListener(listener);
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
    	String evaluatorName = "No name";
		try {
			evaluatorName = getName();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return DSUtil.shortenVerbalName(evaluatorName);
	}

	
	@Override
	public Remote export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
		if (exportedStub != null) return exportedStub;

		exportedStub = (Evaluator) NetUtil.RegistryRemote.export(this, serverPort);
		if (exportedStub != null)
			LogUtil.info("Evaluator served at port " + serverPort);
		else
			LogUtil.info("Evaluator failed to export");
			
		return exportedStub;
	}

	
	@Override
	public void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteEvaluator != null) {
			try {
				if (!remoteEvaluator.isAgent())
					remoteEvaluator.unexport();
			} catch (Exception e) {e.printStackTrace();}
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
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	public boolean isAgent() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.isAgent();
	}


	@Override
	public void setAgent(boolean agent) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.setAgent(agent);
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
}
