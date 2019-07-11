package net.hudup.core.alg;

/**
 * This interface represents algorithms which support to cache in calculation or in doing some tasks.
 * Whether or not such algorithms support to cache depends on the method {@link #isCached()}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface SupportCacheAlg extends Alg {

	
	/**
	 * Supporting cache field name
	 */
	final static String SUPPORT_CACHE_FIELD = "alg_support_cache";

	
	/**
	 * The default setting of whether to support cache. By default cache mode is false because recommendation task should not run in cache mode.
	 * Cache is used to accelerate estimation process in which the user rating vector in recommendation parameter is made to be empty.
	 */
	final static boolean SUPPORT_CACHE_DEFAULT = false;
	
	
	/**
	 * Getting cached mode.
	 * @return true if cached mode is used.
	 */
	boolean isCached();
	
	
	/**
	 * Setting cached mode.
	 * @param cached cached mode.
	 */
	void setCached(boolean cached);


}
