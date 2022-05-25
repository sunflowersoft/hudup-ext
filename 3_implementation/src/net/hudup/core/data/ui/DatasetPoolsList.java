/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.util.Collections;
import java.util.Set;
import java.util.Vector;

import javax.swing.JList;

import net.hudup.core.Util;
import net.hudup.core.data.DatasetPoolExchangedItem;
import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is the list box of dataset pools.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetPoolsList extends JList<DatasetPoolExchangedItem> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Pools service.
	 */
	protected DatasetPoolsService poolsService = null;
	
	
	/**
	 * Constructor with pools service.
	 * @param poolsService pools service.
	 */
	public DatasetPoolsList(DatasetPoolsService poolsService) {
		super();
		this.poolsService = poolsService;
		update();
	}
	
	
	/**
	 * Updating list.
	 */
	public void update() {
		Vector<DatasetPoolExchangedItem> items = Util.newVector();
		if (poolsService == null) {
			setListData(items);
			return;
		}
		
		Set<String> names = Util.newSet();
		try {
			names = poolsService.names();
		} catch (Throwable e) {names = Util.newSet(); LogUtil.trace(e);}
		
		for (String name : names) {
			try {
				DatasetPoolExchangedItem pool = poolsService.get(name);
				if (pool != null) items.add(pool);
			} catch (Throwable e) {names = Util.newSet(); LogUtil.trace(e);}
		}
		
		Collections.sort(items);
		setListData(items);
	}
	
	
}



