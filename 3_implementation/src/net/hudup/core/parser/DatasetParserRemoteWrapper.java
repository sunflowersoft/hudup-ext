/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
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
	 * Constructor with specified remote dataset parser.
	 * @param remoteDatasetParser remote dataset parser.
	 */
	public DatasetParserRemoteWrapper(DatasetParserRemote remoteDatasetParser) {
		super(remoteDatasetParser);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified remote dataset parser and exclusive mode.
	 * @param remoteDatasetParser remote dataset parser.
	 * @param exclusive exclusive mode.
	 */
	public DatasetParserRemoteWrapper(DatasetParserRemote remoteDatasetParser, boolean exclusive) {
		super(remoteDatasetParser, exclusive);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		return ((DatasetParserRemote)remoteAlg).parse(config);
	}

	
	@Override
	public boolean support(DataDriver driver) throws RemoteException {
		// TODO Auto-generated method stub
		return ((DatasetParserRemote)remoteAlg).support(driver);
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof DatasetParserAbstract) {
			DatasetParserAbstract newDatasetParser = (DatasetParserAbstract) ((DatasetParserAbstract)remoteAlg).newInstance();
			return new DatasetParserRemoteWrapper(newDatasetParser, exclusive);
		}
		else {
			LogUtil.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}


}
