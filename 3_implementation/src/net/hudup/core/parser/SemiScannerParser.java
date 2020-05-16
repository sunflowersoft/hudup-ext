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
import net.hudup.core.data.Dataset;
import net.hudup.core.data.SemiScanner;

/**
 * This class implements semi-scanner parser.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SemiScannerParser extends ScannerParserImpl {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SemiScannerParser() {
		super();
	}


	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		
		config.setParser(this);
		return new SemiScanner(config);
	}

	
	@Override
	public String getName() {
		return "semi_scanner_parser";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		return new DataConfig();
	}

	
	@Override
	public Alg newInstance() {
		return new SemiScannerParser();
	}
	
	
	
}
