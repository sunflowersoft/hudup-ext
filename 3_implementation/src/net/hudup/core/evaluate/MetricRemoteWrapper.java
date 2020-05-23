/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is wrapper of remote metric.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@BaseClass
public class MetricRemoteWrapper extends AlgRemoteWrapper implements Metric, MetricRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
    /**
	 * Constructor with remote metric.
	 * @param remoteMetric remote metric.
	 */
	public MetricRemoteWrapper(MetricRemote remoteMetric) {
		super(remoteMetric);
	}

	
	/**
	 * Constructor with remote metric and exclusive mode.
	 * @param remoteMetric remote metric.
	 * @param exclusive exclusive mode
	 */
	public MetricRemoteWrapper(MetricRemote remoteMetric, boolean exclusive) {
		super(remoteMetric, exclusive);
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		return ((MetricRemote)remoteAlg).getTypeName();
	}

	
	@Override
	public void setup(Object... params) throws RemoteException {
		((MetricRemote)remoteAlg).setup(params);
	}

	
	@Override
	public MetricValue getCurrentValue() throws RemoteException {
		return ((MetricRemote)remoteAlg).getCurrentValue();
	}

	
	@Override
	public MetricValue getAccumValue() throws RemoteException {
		return ((MetricRemote)remoteAlg).getAccumValue();
	}

	
	@Override
	public boolean recalc(Object... params) throws RemoteException {
		return ((MetricRemote)remoteAlg).recalc(params);
	}

	
	@Override
	public void reset() throws RemoteException {
		((MetricRemote)remoteAlg).reset();
	}

	
	@Override
	public boolean isValid() throws RemoteException {
		return ((MetricRemote)remoteAlg).isValid();
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {MetricRemote.class.getName()};
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		if (remoteAlg instanceof Metric)
			return ((Metric)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("Wrapper of remote metric does not support createDefaultConfig()");
			return null;
		}
	}


}
