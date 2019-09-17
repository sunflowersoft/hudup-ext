/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
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
public interface DatasetParserRemote extends AlgRemote {
	
	
	/**
	 * This is the main method that receives the input as configuration of data source and returns the output as a {@link Dataset}.
	 * For example, in order to parse <a href="http://grouplens.org/datasets/movielens">Movielens data</a>, programmers often create a MovielensParser which is an implementation of this interface {@link DatasetParser} and then,
	 * define this method according to features of Movielens data.
	 * @param config Configuration of data source represented by {@link DataConfig}.
	 * @return {@link Dataset}
	 * @throws RemoteException if any error raises.
	 */
	Dataset parse(DataConfig config) throws RemoteException;
	
	
	/**
	 * Testing whether this parser supports the specified data driver.
	 * @param driver Specified data driver, represented by {@link DataDriver} class
	 * @return {@code true} if this parser supports the specified data driver; otherwise the returned is {@code false} 
	 * @throws RemoteException if any error raises.
	 */
	boolean support(DataDriver driver) throws RemoteException;

	
}
