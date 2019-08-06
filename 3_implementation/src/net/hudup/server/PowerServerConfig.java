/**
 * 
 */
package net.hudup.server;

import java.net.URI;
import java.net.URL;

import net.hudup.alg.cf.gfall.GreenFallCF;
import net.hudup.core.Constants;
import net.hudup.core.alg.Recommender;
import net.hudup.core.client.ServerConfig;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.xURI;
import net.hudup.logistic.math.HudupCipher;
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
	 * Sample data path.
	 */
	public final static String  TEMPLATES_SAMPLE_DATA = Constants.TEMPLATES_PACKAGE + "hudup_sample_data.zip";
	

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
	public final static String RECOMMENDER_PERIOD_LEARN_FIELD = changeCase("recommender_period_learn");

	
	/**
	 * By default, it is necessary to learn periodically internal recommender.
	 */
	public final static boolean RECOMMENDER_PERIOD_LEARN_DEFAULT = true;

	
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
			URL storeUrl = getClass().getResource(TEMPLATES_SAMPLE_DATA);
			URI storeUri = storeUrl.toURI();
			putDefaultUnitList(xURI.create(storeUri));
			setRecommender(new GreenFallCF());
			setParser(new SnapshotParserImpl());
			setRecommenderPeriodLearn(RECOMMENDER_PERIOD_LEARN_DEFAULT);
		} 
		catch (Throwable e) {
			e.printStackTrace();
			putDefaultUnitList(xURI.create("jdbc:derby:hudup;create=true"));
			setRecommender(new GreenFallCF());
			setParser(new SemiScannerParser());
		}
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
	public void setRecommenderPeriodLearn(boolean flag) {
		put(RECOMMENDER_PERIOD_LEARN_FIELD, flag);
	}
	
	
	/**
	 * Getting flag to learn periodically recommender.
	 * @return flag to learn periodically recommender.
	 */
	public boolean isRecommenderPeriodLearn() {
		return getAsBoolean(RECOMMENDER_PERIOD_LEARN_FIELD);
	}

	
	@Override
	protected String encrypt(HiddenText hidden) {
		// TODO Auto-generated method stub
		HudupCipher cipher = new HudupCipher();
		return cipher.encrypt(hidden.getText());
	}


	@Override
	protected HiddenText decrypt(String text) {
		// TODO Auto-generated method stub
		HudupCipher cipher = new HudupCipher();
		return new HiddenText(cipher.decrypt(text));
	}
	
	
	@Override
	public Object clone() {
		PowerServerConfig cfg = new PowerServerConfig();
		cfg.putAll(this);
		
		return cfg;
	}

	
}
