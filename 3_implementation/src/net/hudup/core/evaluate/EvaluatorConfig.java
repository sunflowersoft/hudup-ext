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
	 * Default file name of evaluator configuration.
	 */
	public final static String EVALCONFIG_FILENAME_PREFIX = "evalconfig";

	
	/**
	 * Default file name extension of evaluator configuration.
	 */
	public final static String EVALCONFIG_FILEEXT = "xml";

	
	/**
	 * Default path including directory and file name to store evaluator configuration.
	 * By default, it is &quot;&lt;directory to deploy Hudup framework&gt;/working/evalconfig.xml&quot;.
	 */
	public final static String EVALCONFIG_FILEPATH_DEFAULT = Constants.WORKING_DIRECTORY + "/" + EVALCONFIG_FILENAME_PREFIX + "." + EVALCONFIG_FILEEXT;

	
	/**
	 * This constant specifies key name of evaluator port.
	 */
	public final static String EVALUATOR_PORT_FIELD = changeCase("evaluator_port");

	
	/**
	 * By default, do not recommend all items.
	 */
	public final static boolean RECOMMEND_ALL_DEFAULT = false;

	
//	/**
//	 * By default, agent field is false. If agent mode is true, no GUI can close evaluator.
//	 */
//	public final static boolean EVALUATOR_AGENT_DEFAULT = false;

	
	/**
	 * This constant specifies result summary saving field. If true, only done evaluated results are saved.
	 */
	public final static String EVALUATOR_SAVE_RESULT_SUMMARY_FIELD = changeCase("save_result_summary");

	
	/**
	 * By default, result summary saving is false. If true, only done evaluated results are saved.
	 */
	public final static boolean EVALUATOR_SAVE_RESULT_SUMMARY_DEFAULT = false;

	
	/**
	 * This constant specifies reproduction field. If true, evaluator is reproduced.
	 */
	public final static String EVALUATOR_REPRODUCED_VERSION_FIELD = changeCase("reproduced_version");

	
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
		
		setRecommendAll(RECOMMEND_ALL_DEFAULT);
		setEvaluatorPort(Constants.DEFAULT_EVALUATOR_PORT);
		//setAgent(EVALUATOR_AGENT_DEFAULT);
		setSaveResultSummary(EVALUATOR_SAVE_RESULT_SUMMARY_DEFAULT);
		
		addReadOnly(EVALUATOR_REPRODUCED_VERSION_FIELD);
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
	
	
	/**
	 * Setting evaluator port by specified port.
	 * @param port specified port.
	 */
	public void setEvaluatorPort(int port) {
		put(EVALUATOR_PORT_FIELD, port);
	}
	
	
	/**
	 * Getting evaluator port.
	 * @return evaluator port
	 */
	public int getEvaluatorPort() {
		return getAsInt(EVALUATOR_PORT_FIELD);
	}

	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		EvaluatorConfig cfg = new EvaluatorConfig();
		cfg.putAll(this);
		
		return cfg;
	}


//	/**
//	 * Checking whether the evaluator is agent. If the evaluator is agent, no GUI can close evaluator. 
//	 * @return whether the evaluator is agent.
//	 */
//	public boolean isAgent() {
//		return getAsBoolean(DataConfig.AGENT_FIELD);
//	}
//	
//	
//	/**
//	 * Setting whether the evaluator is agent. If the evaluator is agent, no GUI can close evaluator. 
//	 * @param isAgent agent mode.
//	 */
//	public void setAgent(boolean isAgent) {
//		put(DataConfig.AGENT_FIELD, isAgent);
//	}


	/**
	 * Checking whether result summary saving mode is true. If true, only done evaluated results are saved. 
	 * @return whether result summary saving mode is true.
	 */
	public boolean isSaveResultSummary() {
		return getAsBoolean(EVALUATOR_SAVE_RESULT_SUMMARY_FIELD);
	}
	
	
	/**
	 * Setting whether result summary saving mode is true. If true, only done evaluated results are saved.
	 * @param saveResultSummary result summary saving mode is true.
	 */
	public void setSaveResultSummary(boolean saveResultSummary) {
		put(EVALUATOR_SAVE_RESULT_SUMMARY_FIELD, saveResultSummary);
	}

	
	/**
	 * Setting reproduced version.
	 * @param reproducedVersion reproduced version. If null, removing reproduced version.
	 */
	public void setReproducedVersion(String reproducedVersion) {
		if (reproducedVersion == null)
			remove(EVALUATOR_REPRODUCED_VERSION_FIELD);
		else
			put(EVALUATOR_REPRODUCED_VERSION_FIELD, reproducedVersion);
	}
	
	
	/**
	 * Checking whether the evaluator is reproduced.
	 * @return whether the evaluator is reproduced.
	 */
	public boolean isReproduced() {
		return containsKey(EVALUATOR_REPRODUCED_VERSION_FIELD);
	}
	
	
	/**
	 * Getting reproduced version.
	 * @return reproduced version.
	 */
	public String getReproducedVersion() {
		return getAsString(EVALUATOR_REPRODUCED_VERSION_FIELD);
	}
	
	
}
