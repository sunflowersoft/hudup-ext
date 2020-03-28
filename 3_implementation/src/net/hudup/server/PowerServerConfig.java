/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server;

import net.hudup.alg.cf.gfall.GreenFallCF;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Recommender;
import net.hudup.core.client.ServerConfig;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.parser.SemiScannerParser;
import net.hudup.parser.SnapshotParserImpl;

/**
 * This class represents configuration for power server.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PowerServerConfig extends ServerConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Server configuration path.
	 */
	public final static String serverConfig = Constants.WORKING_DIRECTORY + "/serverconfig.xml";
	
	
	/**
	 * Sample data name.
	 */
	public final static String  TEMPLATES_SAMPLE_DATA_NAME = "hudup_sample_data.zip";

	
	/**
	 * Sample data path.
	 */
	public final static String  TEMPLATES_SAMPLE_DATA_PATH = Constants.TEMPLATES_PACKAGE + TEMPLATES_SAMPLE_DATA_NAME;
	

	/**
	 * Default store path.
	 */
	public final static String  STORE_PATH_DEFAULT = Constants.DATABASE_DIRECTORY + "/file/" + TEMPLATES_SAMPLE_DATA_NAME;

	
	/**
	 * Server recommender field.
	 */
	public final static String SERVER_RECOMMENDER_FIELD = changeCase("server_recommender");

	
	/**
	 * Server parser.
	 */
	public final static String SERVER_PARSER_FIELD = changeCase("server_parser");
	
	
	/**
	 * Every new information (new entry) that is put into server configuration has a key.
	 * This constant specifies key name of periodical recommender learning.
	 * The field is used to prevent time consuming to learn internal recommender in some cases.
	 */
	public final static String PERIOD_LEARN_FIELD = changeCase("period_learn");

	
	/**
	 * By default, it is necessary to learn periodically internal recommender.
	 */
	public final static boolean PERIOD_LEARN_DEFAULT = true;

	
	/**
	 * Flag field to indicate empty dataset.
	 */
	public final static String DATASET_EMPTY_FIELD = changeCase("dataset_empty");

	
	/**
	 * By default, dataset is not empty.
	 */
	public final static boolean DATASET_EMPTY_DEFAULT = false;

	
	/**
	 * Default constructor.
	 */
	public PowerServerConfig() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Constructor with URI.
	 * @param uri specified URI.
	 */
	public PowerServerConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		// TODO Auto-generated method stub
		super.reset();
		try {
			xURI storeUri = xURI.create(STORE_PATH_DEFAULT);
			putDefaultUnitList(storeUri);
			setRecommender(new GreenFallCF());
			setParser(new SnapshotParserImpl());
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			putDefaultUnitList(xURI.create("jdbc:derby:hudup;create=true"));
			setRecommender(new GreenFallCF());
			setParser(new SemiScannerParser());
		}
		setPeriodLearn(PERIOD_LEARN_DEFAULT);
		setDatasetEmpty(DATASET_EMPTY_DEFAULT);
	}

	
	/**
	 * Getting recommender.
	 * @return server recommender {@link Recommender}.
	 */
	public Recommender getRecommender() {
		return (Recommender) get(SERVER_RECOMMENDER_FIELD);
	}
	
	
	/**
	 * Setting recommender.
	 * @param recommender Server recommender {@link Recommender}.
	 */
	public void setRecommender(Recommender recommender) {
		if (recommender != null)
			put(SERVER_RECOMMENDER_FIELD, recommender);
	}
	
	
	/**
	 * Setting flag to learn periodically recommender.
	 * @param flag flag to learn periodically recommender.
	 */
	public void setPeriodLearn(boolean flag) {
		put(PERIOD_LEARN_FIELD, flag);
	}
	
	
	/**
	 * Getting flag to learn periodically recommender.
	 * @return flag to learn periodically recommender.
	 */
	public boolean isPeriodLearn() {
		return getAsBoolean(PERIOD_LEARN_FIELD);
	}

	
	/**
	 * Setting flag to indicate empty dataset.
	 * @param flag flag to indicate empty dataset.
	 */
	public void setDatasetEmpty(boolean flag) {
		put(DATASET_EMPTY_FIELD, flag);
	}
	
	
	/**
	 * Getting flag to indicate empty dataset.
	 * @return flag to indicate empty dataset.
	 */
	public boolean isDatasetEmpty() {
		return getAsBoolean(DATASET_EMPTY_FIELD);
	}

	
	@Override
	protected String encrypt(HiddenText hidden) {
		// TODO Auto-generated method stub
		return Util.getCipher().encrypt(hidden.getText());
	}


	@Override
	protected HiddenText decrypt(String text) {
		// TODO Auto-generated method stub
		return new HiddenText(Util.getCipher().decrypt(text));
	}
	
	
	@Override
	public Object clone() {
		PowerServerConfig cfg = new PowerServerConfig();
		cfg.putAll(this);
		
		return cfg;
	}

	
}
