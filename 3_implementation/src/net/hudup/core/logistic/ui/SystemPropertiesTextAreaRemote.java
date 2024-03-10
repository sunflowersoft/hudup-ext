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
import net.hudup.core.client.ExtraService;
import net.hudup.core.data.PropList;
import net.hudup.core.logistic.LogUtil;

/**
 * Text area to show remotely system properties.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SystemPropertiesTextAreaRemote extends SystemPropertiesTextArea {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal power server
	 */
	protected ExtraService service;
	
	
	/**
	 * Constructor with specified service.
	 * @param service specified service.
	 */
	public SystemPropertiesTextAreaRemote(ExtraService service) {
		super();
		this.service = service;
		refresh();
	}

	
	@Override
	public void refresh() {
		PropList sysProps = new PropList();
		try {
			if (service != null) sysProps = service.getSystemProperties();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		StringBuffer buffer = new StringBuffer();
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
