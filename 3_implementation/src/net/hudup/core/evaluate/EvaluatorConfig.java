/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import net.hudup.core.Constants;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.SysConfig;
import net.hudup.core.logistic.xURI;

/**
 * This class specifies the configuration of an evaluator represented ({@code Evaluator}.
 * Recall that {@link EvaluatorAbstract} is one of main classes of Hudup framework, which is responsible for executing and evaluation algorithms according to built-in and user-defined metrics.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class EvaluatorConfig extends SysConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default path including directory and file name to store evaluator configuration.
	 * By default, it is &quot;&lt;directory to deploy Hudup framework&gt;/working/evalconfig.xml&quot;.
	 */
	public final static String evalConfig = Constants.WORKING_DIRECTORY + "/evalconfig.xml";

	
	/**
	 * By default, do not recommend all items.
	 */
	public final static boolean RECOMMEND_ALL_DEFAULT = false;

	
	/**
	 * Default constructor.
	 */
	public EvaluatorConfig() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified URI. Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet.
	 * @param uri URI pointing to file path of configuration.
	 */
	public EvaluatorConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		super.reset();
		
		put(DataConfig.RECOMMEND_ALL_FIELD, RECOMMEND_ALL_DEFAULT);
	}

	
	/**
	 * Checking whether to recommend all.
	 * @return whether to recommend all.
	 */
	public boolean isRecommendAll() {
		return getAsBoolean(DataConfig.RECOMMEND_ALL_FIELD);
	}
	
	
	/**
	 * Setting whether to recommend all.
	 * @param all true if recommending all.
	 */
	public void setRecommendAll(boolean all) {
		put(DataConfig.RECOMMEND_ALL_FIELD, all);
	}
	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		EvaluatorConfig cfg = new EvaluatorConfig();
		cfg.putAll(this);
		
		return cfg;
	}


}
