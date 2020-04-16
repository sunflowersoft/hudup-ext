/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.external;

import java.awt.Component;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;

import javax.swing.JOptionPane;

import net.hudup.core.Util;
import net.hudup.core.alg.Recommender;
import net.hudup.core.data.CData;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.ui.ExternalConfigurator;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.DatasetParser;
import net.hudup.server.PowerServerConfig;

/**
 * This class is configuration of external server.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalServerConfig extends PowerServerConfig {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default server recommender.
	 */
	public final static Class<? extends Recommender> DEFAULT_SERVER_RECOMMENDER = ExternalServerRecommender.class;
	
	
	/**
	 * Default server parser.
	 */
	public final static Class<? extends DatasetParser> DEFAULT_SERVER_PARSER = SemiScannerParserExt.class;

	
	/**
	 * External query configuration.
	 */
	public static final String EXTERNAL_QUERY_CONFIG = DataConfig.changeCase("external_query_config");
	
	
	/**
	 * Default constructor.
	 */
	public ExternalServerConfig() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	/**
	 * Constructor with specified URI.
	 * @param uri specified URI.
	 */
	public ExternalServerConfig(xURI uri) {
		super(uri);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void reset() {
		super.reset();
		
		setRecommender(Util.newInstance(DEFAULT_SERVER_RECOMMENDER));
		setParser(Util.newInstance(DEFAULT_SERVER_PARSER));

		ExternalConfig externalConfig = new ExternalConfig();
		externalConfig.reset();
		setExternalConfig(externalConfig);
	}
	
	
	/**
	 * Getting external configuration.
	 * @return {@link ExternalConfig}
	 */
	public ExternalConfig getExternalConfig() {
		CData cdata = getAsCData(EXTERNAL_QUERY_CONFIG);
		if (cdata == null)
			return null;
		
		ExternalConfig externalConfig = new ExternalConfig();
		StringReader reader = new StringReader(cdata.getData());
		externalConfig.loadXml(reader);
		reader.close();
		
		return externalConfig;
	}
	
	
	/**
	 * Setting external configuration.
	 * @param externalConfig external configuration.
	 */
	public void setExternalConfig(ExternalConfig externalConfig) {
		if (externalConfig == null)
			return;
		
		try {
			StringWriter writer = new StringWriter();
			externalConfig.saveXml(writer);
			writer.flush();
			writer.close();
			
			String data = writer.getBuffer().toString();
			put(EXTERNAL_QUERY_CONFIG, new CData(data));
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}


	@Override
	public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
		// TODO Auto-generated method stub
		if (key.equals(EXTERNAL_QUERY_CONFIG)) {
			
			ExternalConfig defaultExternalConfig = getExternalConfig(); 
			ExternalConfigurator configurator = new ExternalConfigurator(
					comp, DataDriverList.get(), defaultExternalConfig);
			
			ExternalConfig externalConfig = configurator.getResult();
			if (externalConfig == null || externalConfig.size() == 0) {
				JOptionPane.showMessageDialog(
					comp, 
					"Not query external configuration", 
					"Not query external configuration", 
					JOptionPane.ERROR_MESSAGE);
				
				return null;
			}
			else
				return externalConfig;
			
		}
		else
			return super.userEdit(comp, key, defaultValue);
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		ExternalServerConfig cfg = new ExternalServerConfig();
		cfg.putAll(this);
		
		return cfg;
	}
	
	
}
