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
 * This class is implementation of interface {@link Pointer}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class KBasePointerImpl extends PointerAbstract implements KBasePointer, KBasePointerRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public KBasePointerImpl() {
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
