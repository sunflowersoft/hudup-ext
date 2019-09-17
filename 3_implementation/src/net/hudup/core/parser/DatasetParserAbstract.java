/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import net.hudup.core.alg.AlgAbstract;

/**
 * This class implements partially the {@link DatasetParser} interface.
 * Its main feature is to add a configuration variable {@link #getConfig()} to normal parser.
 * Any complete dataset parser should extend this {@link DatasetParserAbstract}.
 * Note, {@link DatasetParser} is one of main interface responsible for reading and parsing coarse data such as CSV file, Excel file, database table into {@code Dataset}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class DatasetParserAbstract extends AlgAbstract implements DatasetParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public DatasetParserAbstract() {
		super();
	}


}
