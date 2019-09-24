/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;

/**
 * {@code DatasetParser} is one of main interface responsible for reading and parsing coarse data such as CSV file, Excel file, database table into {@link Dataset}.
 * The main method of {@code DatasetParser} is {@link #parse(DataConfig)} that receives the input as configuration of data source and returns the output as a {@code Dataset}.
 * For example, in order to parse <a href="http://grouplens.org/datasets/movielens">Movielens data</a>, programmers often create a MovielensParser which is an implementation of this interface {@link DatasetParser} and then,
 * define the method {@link #parse(DataConfig)} according to features of Movielens data.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface DatasetParser extends DatasetParserRemoteTask, Alg {


}
