/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Dataset;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is wrapper of remote dataset parser.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@BaseClass //This is not really base class but the base class annotation prevents the wrapper to be registered in plug-in storage.
public class DatasetParserRemoteWrapper extends AlgRemoteWrapper implements DatasetParser, DatasetParserRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Default constructor.
     */
    protected DatasetParserRemoteWrapper() {

    }

    
	/**
	 * Constructor with specified remote dataset parser.
	 * @param remoteDatasetParser remote dataset parser.
	 */
	public DatasetParserRemoteWrapper(DatasetParserRemote remoteDatasetParser) {
		super(remoteDatasetParser);
	}

	
	/**
	 * Constructor with specified remote dataset parser and exclusive mode.
	 * @param remoteDatasetParser remote dataset parser.
	 * @param exclusive exclusive mode.
	 */
	public DatasetParserRemoteWrapper(DatasetParserRemote remoteDatasetParser, boolean exclusive) {
		super(remoteDatasetParser, exclusive);
	}

	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		return ((DatasetParserRemote)remoteAlg).parse(config);
	}

	
	@Override
	public boolean support(DataDriver driver) throws RemoteException {
		return ((DatasetParserRemote)remoteAlg).support(driver);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {DatasetParserRemote.class.getName()};
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		if (remoteAlg instanceof DatasetParser)
			return ((DatasetParser)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("Wrapper of remote dataset parser does not support createDefaultConfig()");
			return null;
		}
	}


}
