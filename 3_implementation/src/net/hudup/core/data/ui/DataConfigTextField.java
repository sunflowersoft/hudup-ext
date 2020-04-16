/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import javax.swing.JPopupMenu;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.ui.TagTextField;

/**
 * Text field to show data configuration.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DataConfigTextField extends TagTextField {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public DataConfigTextField() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected JPopupMenu createContextMenu() {
		if (getConfig() == null)
			return null;
		else
			return super.createContextMenu();
	}

	
	/**
	 * Setting configuration.
	 * @param config specified configuration.
	 */
	public void setConfig(DataConfig config) {
		
		tag = config;
		if (config == null)
			setText("");
		else
			setText(config.getUriId().toString());
	}
	
	
	/**
	 * Getting configuration.
	 * @return {@link DataConfig} as internal configuration.
	 */
	public DataConfig getConfig() {
		return (DataConfig)tag;
	}
	
	
}
