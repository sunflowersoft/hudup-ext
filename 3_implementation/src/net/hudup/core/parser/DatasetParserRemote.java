/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemote;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Dataset;

/**
 * This interface establishes a remote parser.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface DatasetParserRemote extends DatasetParserRemoteTask, AlgRemote {

	
	@Override
	Dataset parse(DataConfig config) throws RemoteException;
	
	
	@Override
	boolean support(DataDriver driver) throws RemoteException;


}
