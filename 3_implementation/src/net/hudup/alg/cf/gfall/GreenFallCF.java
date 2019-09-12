/**
 * 
 */
package net.hudup.alg.cf.gfall;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;



/**
 * This class implements the Green Fall algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class GreenFallCF extends FreqItemsetBasedCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public GreenFallCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
//		return "gfall vsaaadfaTEYYYYYYYYYYYYYYYYYYYYYYYYYYY fsgs hhdshs hdsghdsh hh gfall vsaaadfaTEYYYYYYYYYYYYYYYYYYYYYYYYYYY fsgs hhdshs hdsghdsh hh";
		return "gfall";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Green Fall algorithm";
	}


	@Override
	protected FreqItemsetFinder createFreqItemsetFinder() {
		YRoller yroller = new YRoller();
		
		return yroller;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new GreenFallCF();
	}


}




