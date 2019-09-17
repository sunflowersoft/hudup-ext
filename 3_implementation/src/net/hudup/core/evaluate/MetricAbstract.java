/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.AlgAbstract;

/**
 * This abstract class, called {@code abstract metric}, implements partially {@link Metric} interface.
 * It only stores a configuration and defines some methods related to such configuration (getting configuration, creating configuration, etc.) because metric is also an algorithm represented by {@code Alg} interface. However, most of metrics extends it.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class MetricAbstract extends AlgAbstract implements Metric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public MetricAbstract() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	@Override
	public void setup(Object... params) throws RemoteException {
		// TODO Auto-generated method stub
	}

	
}
