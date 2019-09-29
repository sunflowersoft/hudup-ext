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
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;

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
	public void remoteStart(Serializable... parameters) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.remoteStart(parameters);
	}

	
	@Override
	public void remoteStart(List<Alg> algList, DatasetPool pool, Serializable parameter) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.remoteStart(algList, pool, parameter);
	}


	@Override
	public void remotePause() throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.remotePause();
	}

	
	@Override
	public void remoteResume() throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.remoteResume();
	}

	
	@Override
	public void remoteStop() throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.remoteStop();
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
				remoteEvaluator.close();
			} catch (Exception e) {e.printStackTrace();}
		}
		remoteEvaluator = null;
		
		unexport();
	}

	
	@Override
	public void remoteForceStop() throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.remoteForceStop();
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
	public List<Metric> getMetricList() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getMetricList();
	}

	
	@Override
	public void setMetricList(List<Metric> metricList) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.setMetricList(metricList);
	}

	
	@Override
	public RegisterTable extractAlgFromPluginStorage() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.extractAlgFromPluginStorage();
	}

	
	@Override
	public void clearDelayUnsetupAlgs() throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.clearDelayUnsetupAlgs();
	}


	@Deprecated
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getPluginStorage();
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
	public void addProgressListener(EvaluatorProgressListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.addProgressListener(listener);
	}

	
	@Override
	public void removeProgressListener(EvaluatorProgressListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.removeProgressListener(listener);
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
