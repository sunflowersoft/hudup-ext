/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;

/**
 * This class includes a bound URI and naming URI.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class BindNamingURI implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Bound URI. If bound URI is null, the associated is local and so it does not connect to remote object.
	 */
	public xURI bindUri = null;
	
	
	/**
	 * Naming URI. The naming URI is effective if bound URI is null. This naming URI is used to export the local object to be remote object.
	 */
	public xURI namingUri = null;
	
	
	/**
	 * Default constructor.
	 */
	public BindNamingURI() {
		
	}

	
	/**
	 * Constructor with bound URI and naming URI.
	 * @param bindUri bound URI.
	 * @param namingUri naming URI.
	 */
	public BindNamingURI(xURI bindUri, xURI namingUri) {
		this.bindUri = bindUri;
		this.namingUri = namingUri;
	}

	
	/**
	 * Creating bound and naming URI with bound URI.
	 * @param bindUri bound URI.
	 * @return bound and naming URI with bound URI.
	 */
	public static BindNamingURI createBindUri(xURI bindUri) {
		return new BindNamingURI(bindUri, null);
	}

	
	/**
	 * Creating bound and naming URI with naming URI.
	 * @param namingUri naming URI.
	 * @return bound and naming URI with naming URI.
	 */
	public static BindNamingURI createNamingUri(xURI namingUri) {
		return new BindNamingURI(null, namingUri);
	}
	
	
}
