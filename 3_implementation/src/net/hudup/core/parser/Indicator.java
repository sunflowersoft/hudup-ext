/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import net.hudup.core.data.DataConfig;


/**
 * There are two typical {@code Dataset} such as {@code Snapshot} and {@code Scanner}.
 * {@code Snapshot} or {@code Scanner} is defined as an image of piece of {@code Dataset} and knowledge base ({@code KBase}) at certain time point.
 * The difference between {@code Snapshot} and {@code Scanner} that {@code Snapshot} copies whole piece of data into memory while scanner is merely a reference to such data piece.
 * Another additional {@code Dataset} is {@code Pointer}.
 * {@code Pointer} does not contain any information nor provide any access means to dataset.
 * It only points to another {@code Snapshot} or {@code Scanner}.<br>
 * Although you can create your own {@code Dataset}, {@code Dataset} is often retrieved from utility class parsers that implement interface {@link DatasetParser}.
 * {@code Snapshot} is retrieved from {@link SnapshotParser}.
 * {@code Scanner} is retrieved from {@link ScannerParser}. Both {@link SnapshotParser} and {@link ScannerParser} implement interface {@link DatasetParser}.
 * {@code Pointer} is retrieved from {@link Indicator}. {@link Indicator} is {@link DatasetParser} specified to create {@code Pointer}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class Indicator extends DatasetParserAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public Indicator() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}

	
}
