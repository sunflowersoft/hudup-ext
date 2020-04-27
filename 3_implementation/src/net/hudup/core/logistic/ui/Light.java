/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

/**
 * This class represent a signal light.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Light extends JLabel {

	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public Light() {
		String alt = "Green light: connected";
		ImageIcon icon = UIUtil.getImageIcon("light-green-16x16.png", alt + " (IconArchive)");
		setIcon(icon);
		setToolTipText(alt);
	}

	
	/**
	 * Constructor with image and alternative text.
	 * @param image light image.
	 * @param alt alternative text.
	 */
	public Light(Icon image, String alt) {
		super(image);
		setToolTipText(alt);
	}

	
	/**
	 * Turning on/off the light.
	 * @param on true if turning on the light. 
	 */
	public void turn(boolean on) {
		setVisible(on);
	}
	
	
}
