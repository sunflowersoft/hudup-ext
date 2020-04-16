/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import net.hudup.core.alg.KBase;

/**
 * There are two typical {@link Dataset} such as {@link Snapshot} and {@link Scanner}.
 * {@code Snapshot} or scanner is defined as an image of piece of {@code Dataset} and knowledge base ({@link KBase}) at certain time point.
 * This image is stored in share memory for fast access because it takes long time to access data and knowledge stored in hard disk.
 * The difference between {@code Snapshot} and {@code Scanner} that {@code Snapshot} copies whole piece of data into memory while scanner is merely a reference to such data piece.
 * {@code Snapshot} consumes much more memory but gives faster access and is more convenient.
 * {@code Snapshot} and {@code Scanner} are read-only objects because they provide only read operator.<br>
 * Another additional {@code Dataset} is {@link Pointer}.
 * {@code Pointer} does not contain any information nor provide any access means to dataset.
 * It only points to another {@code Snapshot}, {@code Scanner}, or {@code KBase}.<br>
 * Although you can create your own {@code Dataset}, {@code Dataset} is often retrieved from utility class parsers that implement interface {@code DatasetParser}.
 * {@code Snapshot} is retrieved from {@code SnapshotParser}.
 * {@code Scanner} is retrieved from {@code ScannerParser}. Both {@code SnapshotParser} and {@code ScannerParser} implement interface {@code DatasetParser}.
 * {@code Pointer} is retrieved from {@code Indicator}. {@code Indicator} is {@code DatasetParser} specified to create {@code Pointer}.
 * <br><br>
 * Therefore, this class is called {@code KBase pointer}, which is also a pointer. It does not contains any information about knowledge base but it points to other {@code KBase}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface KBasePointer extends Pointer {

	
}
