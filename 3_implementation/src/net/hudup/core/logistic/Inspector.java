package net.hudup.core.logistic;

import java.io.Serializable;

import javax.swing.JDialog;

/**
 * This interface represents GUI which allows users to inspect an object.
 * It is often implemented as a dialog.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface Inspector extends Serializable {

	
	/**
	 * Inspecting an object. If the inspector is implemented as a dialog, this method is the showing method (see {@link JDialog#setVisible(boolean)}).
	 */
	void inspect();
	
	
}
