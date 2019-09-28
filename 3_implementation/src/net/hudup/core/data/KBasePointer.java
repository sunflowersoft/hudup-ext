/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import net.hudup.core.PluginStorage;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.ModelBasedAlg;
import net.hudup.core.logistic.xURI;

/**
 * There are two typical {@link Dataset} such as {@link Snapshot} and {@link Scanner}.
 * {@link Snapshot} or scanner is defined as an image of piece of {@link Dataset} and knowledge base ({@link KBase}) at certain time point.
 * This image is stored in share memory for fast access because it takes long time to access data and knowledge stored in hard disk.
 * The difference between {@link Snapshot} and {@link Scanner} that {@link Snapshot} copies whole piece of data into memory while scanner is merely a reference to such data piece.
 * {@link Snapshot} consumes much more memory but gives faster access and is more convenient.
 * {@link Snapshot} and {@link Scanner} are read-only objects because they provide only read operator.<br>
 * Another additional {@link Dataset} is {@link Pointer}.
 * {@link Pointer} does not contain any information nor provide any access means to dataset.
 * It only points to another {@link Snapshot}, {@link Scanner}, or {@link KBase}.<br>
 * Although you can create your own {@link Dataset}, {@link Dataset} is often retrieved from utility class parsers that implement interface {@code DatasetParser}.
 * {@link Snapshot} is retrieved from {@code SnapshotParser}.
 * {@link Scanner} is retrieved from {@code ScannerParser}. Both {@code SnapshotParser} and {@code ScannerParser} implement interface {@code DatasetParser}.
 * {@link Pointer} is retrieved from {@code Indicator}. {@code Indicator} is {@code DatasetParser} specified to create {@link Pointer}.
 * <br><br>
 * Therefore, this class is called {@code KBase pointer}, which is also a pointer. It does not contains any information about knowledge base but it points to other {@link KBase}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class KBasePointer extends Pointer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public KBasePointer() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Create knowledge base from specified dataset.
	 * @param dataset specified dataset. It is often {@link KBasePointer}.
	 * @return knowledge base from dataset.
	 */
	public static KBase createKB(Dataset dataset) {
		DataConfig config = loadKBaseConfig(dataset);
		if (config == null) return null;
		String kbaseName = config.getAsString(KBase.KBASE_NAME);
		if (kbaseName == null) return null;
		
		KBase kbase = null;
		Alg alg = PluginStorage.getNormalAlgReg().query(kbaseName);
		if (alg != null && (alg instanceof ModelBasedAlg)) {
			try {
				kbase = ((ModelBasedAlg)alg).createKBase(dataset);
			}
			catch (Throwable e) {
				e.printStackTrace();
				kbase = null;
			}
		}
		
		try {
			if (kbase != null && kbase.isEmpty()) {
				kbase.close();
				kbase = null;
			}
		}
		catch (Throwable e) {e.printStackTrace(); kbase = null;}
		
		return kbase;
	}
	
	
	/**
	 * Loading configuration of knowledge base from specified dataset.
	 * @param dataset specified dataset. It is often {@link KBasePointer}.
	 * @return configuration of knowledge base from specified dataset.
	 */
	public static DataConfig loadKBaseConfig(Dataset dataset) {
		DataConfig config = (DataConfig) dataset.getConfig().clone();
		xURI storeUri = config.getStoreUri();
		if (storeUri == null) return null;
		xURI configUri = storeUri.concat(KBase.KBASE_CONFIG);
		config.load(configUri);
		
		return config;
	}
	
	
	
}
