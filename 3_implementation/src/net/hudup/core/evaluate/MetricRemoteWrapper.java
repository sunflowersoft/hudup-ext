/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgRemoteWrapper;
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
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with remote metric and exclusive mode.
	 * @param remoteMetric remote metric.
	 * @param exclusive exclusive mode
	 */
	public MetricRemoteWrapper(MetricRemote remoteMetric, boolean exclusive) {
		super(remoteMetric, exclusive);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return ((MetricRemote)remoteAlg).getTypeName();
	}

	
	@Override
	public void setup(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		((MetricRemote)remoteAlg).setup(params);
	}

	
	@Override
	public MetricValue getCurrentValue() throws RemoteException {
		// TODO Auto-generated method stub
		return ((MetricRemote)remoteAlg).getCurrentValue();
	}

	
	@Override
	public MetricValue getAccumValue() throws RemoteException {
		// TODO Auto-generated method stub
		return ((MetricRemote)remoteAlg).getAccumValue();
	}

	
	@Override
	public boolean recalc(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
		return ((MetricRemote)remoteAlg).recalc(params);
	}

	
	@Override
	public void reset() throws RemoteException {
		// TODO Auto-generated method stub
		((MetricRemote)remoteAlg).reset();
	}

	
	@Override
	public boolean isValid() throws RemoteException {
		// TODO Auto-generated method stub
		return ((MetricRemote)remoteAlg).isValid();
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof MetricAbstract) {
			MetricAbstract newMetric = (MetricAbstract) ((MetricAbstract)remoteAlg).newInstance();
			return new MetricRemoteWrapper(newMetric, exclusive);
		}
		else {
			LogUtil.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}


}
