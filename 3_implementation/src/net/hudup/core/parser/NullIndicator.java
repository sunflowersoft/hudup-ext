/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.NullPointer;
import net.hudup.core.data.Pointer;

/**
 * Null indicator parse null pointer {@link NullPointer}.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class NullIndicator extends Indicator {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public NullIndicator() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		Pointer pointer = new NullPointer();
		config.setParser(this);
		pointer.setConfig(config);
		return pointer;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "null_indicator";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Null indicator";
	}


	@Override
	public boolean support(DataDriver driver) throws RemoteException {
		// TODO Auto-generated method stub
		return true;
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new NullIndicator();
	}

	
}
