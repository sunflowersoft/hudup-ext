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
	 * Name of cosine + Jaccard measure.
	 */
	public static final String COSINEJ = "cosinej";

	
	/**
	 * Name of pseudo Cosine + Jaccard.
	 */
	public static final String COJ = "coj";

	
	/**
	 * Name of Pearson measure.
	 */
	public static final String PEARSON = "pearson";

	
	/**
	 * Name of Pearson + Jaccard measure.
	 */
	public static final String PEARSONJ = "pearsonj";

	
	/**
	 * Name of adjusted cosine measure.
	 */
	public static final String COD = "cod";

	
	/**
	 * Name of constrained Pearson correlation coefficient measure. It is also cosine normalized (CON) measure.
	 */
	public static final String CPC = "cpc";
	
	
	/**
	 * Name of weighted Pearson correlation coefficient measure.
	 */
	public static final String WPC = "wpc";
	
	
	/**
	 * Name of sigmoid Pearson correlation coefficient measure.
	 */
	public static final String SPC = "spc";

	
	/**
	 * Name of Jaccard measure.
	 */
	public static final String JACCARD = "jaccard";

	
	/**
	 * Name of Jaccard2 measure.
	 */
	public static final String JACCARD2 = "jaccard2";

	
	/**
	 * Name of Dice measure.
	 */
	public static final String DICE = "dice";

	
	/**
	 * Name of MSD measure.
	 */
	public static final String MSD = "msd";

	
	/**
	 * Name of MSD + Jaccard measure.
	 */
	public static final String MSDJ = "msdj";

	
	/**
	 * Name of URP measure.
	 */
	public static final String URP = "urp";

	
	/**
	 * Name of Triangle measure.
	 */
	public static final String TRIANGLE = "triangle";

	
	/**
	 * Name of TJM measure.
	 */
	public static final String TJM = "tjm";

	
}
