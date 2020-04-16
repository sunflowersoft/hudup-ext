/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

/**
 * Text field whose tag is a particular object.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class TagTextField extends TextField {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Tag is a reference of a particular object.
	 */
	protected Object tag = null;
	
	
	/**
	 * Default constructor.
	 */
	public TagTextField() {
		super();
	}
	
	
	/**
	 * Getting tag object.
	 * @return tag specified tag object.
	 */
	public Object getTag() {
		return tag;
	}
	
	
	/**
	 * Setting tag object.
	 * @param tag specified tag object.
	 */
	public void setTag(Object tag) {
		this.tag = tag;
	}
	
	
	/**
	 * Setting text associated with a tag.
	 * @param text specified text.
	 * @param tag specified tag object.
	 */
	public void setText(String text, Object tag) {
		super.setText(text);
		this.tag = tag;
	}
	
	
	
}
