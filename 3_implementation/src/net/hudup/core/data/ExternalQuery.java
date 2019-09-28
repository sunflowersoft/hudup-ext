/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import net.hudup.core.alg.Alg;

/**
 * {@link Alg} is the most abstract interface for all algorithms.
 * {@link Alg} is one of the most important interfaces.
 * It declares the most basic methods in an algorithm.
 * The most important thing is that every {@link Alg} owns a configuration specified by {@link DataConfig} class.
 * <code>Alg</code> has five typical inherited interfaces as follows:
 * <ul>
 * <li>Recommendation algorithm {@code Recommender}.</li> 
 * <li>Context template schema manager {@code CTSManager}.</li> 
 * <li>Data set parser {@code DatasetParser}.</li>
 * <li>External query {@link ExternalQuery}.</li>
 * <li>Metric {@code Metric}.</li>
 * </ul>
 * Therefore, this class is also an algorithm and it is discovered and registered in plug-in storage by plug-in manager.
 * External query is mainly used to retrieve external information stored in outside database different from Hudup database.
 * In current implementation, external query retrieve (queries) external information about user and item specified by {@link ExternalUserInfo} and {@link ExternalItemInfo}, respectively.
 * Moreover, external query can fill in Hudup database by importing data from outside database.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface ExternalQuery extends ExternalQueryRemoteTask, Alg, AutoCloseable {

	
}
