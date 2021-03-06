/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.ScannerImpl;

/**
 * This class implements scanner parser.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ScannerParserImpl extends ScannerParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ScannerParserImpl() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		
		config.setParser(this);
		return new ScannerImpl(config);
	}

	
	@Override
	public String getName() {
		return "scanner_parser";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Scanner parser";
	}


}
