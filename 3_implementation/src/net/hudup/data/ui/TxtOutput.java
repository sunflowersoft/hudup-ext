/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;

import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class is an extension of Java text field to show text.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class TxtOutput extends JTextArea {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public TxtOutput() {
		super();
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createOutputContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), 
								e.getX(), e.getY());
				}
			}
			
		});
		
	}

	
	/**
	 * Copying text output to clip-board.
	 */
	protected void copyOutputToClipboard() {
		
		ClipboardUtil.util.setText(getText());
	}
	
	
	/**
	 * Saving the text.
	 */
	protected void save() {
		UriAdapter adapter = new UriAdapter(); 
		xURI uri = adapter.chooseDefaultUri(this, false, null);
		adapter.close();
		
		if (uri == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Not saved URI", 
					"Not saved URI", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		adapter = new UriAdapter(uri); 
		boolean ret = adapter.saveText(uri, getText(), false);
		if (ret) {
			JOptionPane.showMessageDialog(
					this, 
					"URI saved successfully", 
					"URI saved successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		adapter.close();
	}

	
	/**
	 * Creating context menu.
	 * @return context menu as {@link JPopupMenu}.
	 */
	protected JPopupMenu createOutputContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miClipCopy = UIUtil.makeMenuItem((String)null, "Copy to clipboard", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					copyOutputToClipboard();
				}
			});
		contextMenu.add(miClipCopy);
		
		contextMenu.addSeparator();
		
		JMenuItem miSave = UIUtil.makeMenuItem((String)null, "Save", 
			new ActionListener() {
					
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
		contextMenu.add(miSave);

		contextMenu.addSeparator();

		JMenuItem miClear = UIUtil.makeMenuItem((String)null, "Clear", 
			new ActionListener() {
					
				public void actionPerformed(ActionEvent e) {
					clear();
				}
			});
		contextMenu.add(miClear);

		return contextMenu;
	}
	
	
	/**
	 * Clearing text.
	 */
	private void clear() {
		setText("");
	}
	
	
}
