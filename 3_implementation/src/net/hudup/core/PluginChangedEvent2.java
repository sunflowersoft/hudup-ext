/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.util.List;

import net.hudup.core.alg.Alg;

/**
 * This class represents extended plug-in changed event.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class PluginChangedEvent2 extends PluginChangedEvent {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of removed algorithm.
	 */
	protected List<Alg> removedAlgList = Util.newList();
	
	
	/**
	 * Constructor with specified source.
	 * @param source specified source.
	 */
	public PluginChangedEvent2(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified source and list of removed algorithm.
	 * @param source specified source.
	 * @param removedAlgList list of removed algorithm.
	 */
	public PluginChangedEvent2(Object source, List<Alg> removedAlgList) {
		super(source);
		// TODO Auto-generated constructor stub
		
		if (removedAlgList != null)
			this.removedAlgList = removedAlgList;
	}

	
	/**
	 * Getting list of removed algorithm.
	 * @return list of removed algorithm.
	 */
	public List<Alg> getRemovedAlgList() {
		return removedAlgList;
	}
	
	
}
