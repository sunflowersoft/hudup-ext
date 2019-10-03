package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import net.hudup.core.logistic.ClipboardUtil;


/**
 * This is Java text field with more functions such as context menu.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class TextField extends JTextField {

	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public TextField() {
		// TODO Auto-generated constructor stub
		init();
	}

	
	/**
	 * Constructor with specified text.
	 * @param text specified text.
	 */
	public TextField(String text) {
		super(text);
		// TODO Auto-generated constructor stub
		init();
	}

	
	/**
	 * Constructor with specified columns.
	 * @param columns specified number of columns.
	 */
	public TextField(int columns) {
		super(columns);
		// TODO Auto-generated constructor stub
		init();
	}

	
	/**
	 * Constructor with specified text and columns.
	 * @param text specified text.
	 * @param columns specified columns.
	 */
	public TextField(String text, int columns) {
		super(text, columns);
		// TODO Auto-generated constructor stub
		init();
	}

	
	/**
	 * Constructor with specified document, text and columns.
	 * @param doc specified document.
	 * @param text specified text.
	 * @param columns specified columns.
	 */
	public TextField(Document doc, String text, int columns) {
		super(doc, text, columns);
		// TODO Auto-generated constructor stub
		init();
	}

	
	/**
	 * Initializing method.
	 */
	protected void init() {
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu == null) return;
					
					addToContextMenu(contextMenu);
					
					contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else {
				}
			}
			
		});
	}
	
	
	/**
	 * Creating context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu() {
		String text = getText();
		if (text == null || text.isEmpty()) return null;

		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miCopyDesc = UIUtil.makeMenuItem(null, "Copy", 
			new ActionListener() {
				
				public void actionPerformed(ActionEvent e) {
					ClipboardUtil.util.setText(getText());
				}
			});
		contextMenu.add(miCopyDesc);

		return contextMenu;
	}
	
	
	/**
	 * Adding menu item to specified context menu.
	 * @param contextMenu specified context menu.
	 */
	protected void addToContextMenu(JPopupMenu contextMenu) {
		
	}


	@Override
	public void setText(String t) {
		// TODO Auto-generated method stub
		super.setText(t);
		setCaretPosition(0);
	}
	
	
}
