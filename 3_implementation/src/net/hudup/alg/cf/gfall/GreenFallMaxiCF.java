/**
 * 
 */
package net.hudup.alg.cf.gfall;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.NextUpdate;


/**
 * This class implements Green Fall algorithm with maximum improvement.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
public class GreenFallMaxiCF extends GreenFallCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public GreenFallMaxiCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		return "gfallmaxi";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Maximal Green Fall algorithm";
	}


	@Override
	protected FreqItemsetFinder createFreqItemsetFinder() {
		YRollerMaxi yrollermaxi = new YRollerMaxi();
		
		return yrollermaxi;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new GreenFallMaxiCF();
	}
	
	
}
