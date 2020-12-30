/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import net.hudup.core.logistic.xURI;

/**
 * This interface represents a knowledge base that contains serialized nut called KBase nut.
 * You uses the nut to wrapper data structure, knowledge base, etc. without regarding how to store such data structure and knowledge base
 * because the nut is serialized and deserialized from file. The weak point of KBase node is not to interchange knowledge base among non-Java applications.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface KBaseSerializable extends KBase {
	
	
	/**
	 * File extension of knowledge base nut.
	 */
	public final static String KBASE_NUT_EXT = ".nut";

	
	/**
	 * Reading (deserializing) nut from knowledge base store.
	 * @param storeUri store URI.
	 * @return true if reading (deserializing) is successful. 
	 */
	boolean deserializeNut(xURI storeUri);

	
	/**
	 * Writing (serializing) nut to knowledge base store.
	 * @param storeUri store URI.
	 * @return true if writing (serializing) is successful. 
	 */
	boolean serializeNut(xURI storeUri);

	
}
