/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This abstract class implements partially the {@link ProviderAssoc} interface.
 * It add the internal configuration {@link #config} for {@link ProviderAssoc}.
 * Note, {@link ProviderAssoc} assists the provider specified by interface {@code Provider} to performs read-write operations.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class ProviderAssocAbstract implements ProviderAssoc {

	
	/**
	 * The internal configuration {@link #config} for {@link ProviderAssoc}.
	 */
	protected DataConfig config = null;
		
	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	public ProviderAssocAbstract(DataConfig config) {
		this.config = config;
	}

	
	@Override
	public DataConfig getConfig() {
		return config;
	}
	

	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
