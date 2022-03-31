/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf.nb;

/**
 * This class lists measures which may be supported by the nearest neighbors collaborative filtering algorithm built in {@link NeighborCF}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Measure {
	
	
	/**
	 * Name of cosine measure.
	 */
	public static final String COSINE = "cosine";

	
	/**
	 * Name of Pearson measure.
	 */
	public static final String PEARSON = "pearson";

	
	/**
	 * Name of Jaccard measure.
	 */
	public static final String JACCARD = "jaccard";

	
	/**
	 * Name of MSD measure.
	 */
	public static final String MSD = "msd";

	
	/**
	 * Name of URP measure.
	 */
	public static final String URP = "urp";

	
	/**
	 * Name of Triangle measure.
	 */
	public static final String TRIANGLE = "triangle";

	
	/**
	 * Name of RPB measure.
	 */
	public static final String RPB = "rpb";
	
	
	/**
	 * SMCC measure.
	 */
	public static final String SMC = "smc";

	
	/**
	 * Absolute Difference of Ratings (ADR) measure.
	 */
	public static final String ADR = "adr";
	
	
	/**
	 * OS measure.
	 */
	public static final String OS = "os";
	
	
	/**
	 * IPWR measure.
	 */
	public static final String IPWR = "ipwr";
	
	
}
