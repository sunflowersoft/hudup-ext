package net.hudup.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.data.SemiScanner;


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
		// TODO Auto-generated constructor stub
	}


	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		
		config.setParser(this);
		return new SemiScanner(config);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "semi_scanner_parser";
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SemiScannerParser();
	}
	
	
	
}
