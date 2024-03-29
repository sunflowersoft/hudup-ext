/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf.mf;

import java.rmi.RemoteException;

import net.hudup.core.alg.KBase;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class is enhanced version of Singular Vector Decomposition (SVD+) algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
public class SvdGradientPlusCF extends SvdGradientCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SvdGradientPlusCF() {
		super();
	}

	
	@Override
	public KBase newKB() throws RemoteException {
		return SvdGradientPlusKB.create(this);
	}

	
	@Override
	public String getName() {
		return "svd_gradient_plus";
	}
	
	
	@Override
	public String getDescription() throws RemoteException {
		return "SVD++ algorithm";
	}


}



