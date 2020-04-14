/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.external;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.parser.SemiScannerParser;

/**
 * This class is parser to parse extended semi-scanner.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SemiScannerParserExt extends SemiScannerParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SemiScannerParserExt() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		
		config.setParser(this);
		return new SemiScannerExt(config);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "semi_scanner_ext_parser";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SemiScannerParserExt();
	}
	
	
}
