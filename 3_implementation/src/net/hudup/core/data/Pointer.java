/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This is abstract class of pointer. There are two typical {@link Dataset} such as {@link Snapshot} and {@link Scanner}.
 * {@link Snapshot} or scanner is defined as an image of piece of {@link Dataset} and knowledge base ({@code KBase}) at certain time point.
 * This image is stored in share memory for fast access because it takes long time to access data and knowledge stored in hard disk.
 * The difference between {@link Snapshot} and {@link Scanner} that {@link Snapshot} copies whole piece of data into memory while scanner is merely a reference to such data piece.
 * {@link Snapshot} consumes much more memory but gives faster access and is more convenient.
 * {@link Snapshot} and {@link Scanner} are read-only objects because they provide only read operator.<br>
 * Another additional {@link Dataset} is {@link Pointer}.
 * {@link Pointer} does not contain any information nor provide any access means to dataset.
 * It only points to another {@link Snapshot}, {@link Scanner}, or {@code KBase}.<br>
 * Although you can create your own {@link Dataset}, {@link Dataset} is often retrieved from utility class parsers that implement interface {@code DatasetParser}.
 * {@link Snapshot} is retrieved from {@code SnapshotParser}.
 * {@link Scanner} is retrieved from {@code ScannerParser}. Both {@code SnapshotParser} and {@code ScannerParser} implement interface {@code DatasetParser}.
 * {@link Pointer} is retrieved from {@code Indicator}. {@code Indicator} is {@code DatasetParser} specified to create {@link Pointer}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Pointer extends Dataset {

	
}
