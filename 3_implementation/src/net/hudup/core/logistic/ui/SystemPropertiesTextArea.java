/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.PropList;
import net.hudup.core.logistic.SystemUtil;

/**
 * Text area to show system properties.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class SystemPropertiesTextArea extends TextArea {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified rows and columns.
	 * 
	 * @param rows specified rows.
	 * @param columns specified columns.
	 */
	public SystemPropertiesTextArea(int rows, int columns) {
		super(rows, columns);
		
		init();
	}


	/**
	 * Default constructor.
	 */
	public SystemPropertiesTextArea() {
		super();
	}
	
	
	@Override
	protected void init() {
		super.init();
		setEditable(false);
		
		refresh();
	}
	
	
	/**
	 * Refreshing this text area.
	 */
	public void refresh() {
		SystemUtil.refreshSystemProperties();
		
		StringBuffer buffer = new StringBuffer();
		PropList sysProps = SystemUtil.getSystemProperties();
		List<String> keys = Util.newList();
		keys.addAll(sysProps.keySet());
		for (int i = 0; i < keys.size(); i++) {
			if (i > 0) buffer.append("\n\n");
			String key = keys.get(i);
			buffer.append(key + ": " + sysProps.getAsString(key));
		}
		
		setText(buffer.toString());
	}
	
	
}
