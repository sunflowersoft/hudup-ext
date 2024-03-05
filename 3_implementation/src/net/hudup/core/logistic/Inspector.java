/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;

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
	
	
	/**
	 * Null inspector.
	 * @author Loc Nguyen
	 * @version 12.0
	 */
	static class NullInspector extends JDialog implements Inspector {

		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default constructor.
		 */
		public NullInspector() {
			super((Frame)null, "Null inspector", true);
			
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setSize(300, 200);
			setLocationRelativeTo(null);
			setLayout(new BorderLayout());
			
			JPanel body = new JPanel(new BorderLayout());
			add(body, BorderLayout.CENTER);
			
			JLabel info = new JLabel("Null inspector");
			info.setHorizontalAlignment(SwingConstants.CENTER);
			body.add(info, BorderLayout.CENTER);
			
			JPanel footer = new JPanel();
			add(footer, BorderLayout.SOUTH);

			JButton close = new JButton("Close");
			close.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
			footer.add(close);
		}
		
		@Override
		public void inspect() {
			setVisible(true);
		}
		
	}
	
}
