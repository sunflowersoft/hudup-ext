/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import net.hudup.core.data.Unit;
import net.hudup.core.logistic.ui.TagTextField;

/**
 * Text field to store a unit.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UnitTextField extends TagTextField {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public UnitTextField() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Setting unit.
	 * @param unit specified unit.
	 */
	public void setUnit(Unit unit) {
		
		tag = unit;
		if (unit == null)
			setText("");
		else
			setText(unit.toString());
	}
	
	
	/**
	 * Getting internal unit.
	 * @return the internal unit.
	 */
	public Unit getUnit() {
		return (Unit)tag;
	}


}
