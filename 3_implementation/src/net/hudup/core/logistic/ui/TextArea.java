/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.text.Document;

import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;

/**
 * This class is text area to show content of an archive (file).
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class TextArea extends JTextArea {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public TextArea() {
		// TODO Auto-generated constructor stub
		super();
		init();
	}

	
	/**
	 * Constructor with specified document, text, rows, and columns.
	 * @param doc specified document.
	 * @param text specified text.
	 * @param rows specified row.
	 * @param columns specified columns.
	 */
	public TextArea(Document doc, String text, int rows, int columns) {
		super(doc, text, rows, columns);
		// TODO Auto-generated constructor stub
		init();
	}


	/**
	 * Constructor with specified document.
	 * @param doc specified document.
	 */
	public TextArea(Document doc) {
		super(doc);
		// TODO Auto-generated constructor stub
		init();
	}


	/**
	 * Constructor with specified rows and columns.
	 * @param rows specified row.
	 * @param columns specified columns.
	 */
	public TextArea(int rows, int columns) {
		super(rows, columns);
		// TODO Auto-generated constructor stub
		init();
	}


	/**
	 * Constructor with specified text, rows, and columns.
	 * @param text specified text.
	 * @param rows specified row.
	 * @param columns specified columns.
	 */
	public TextArea(String text, int rows, int columns) {
		super(text, rows, columns);
		// TODO Auto-generated constructor stub
		init();
	}


	/**
	 * Constructor with specified text.
	 * @param text specified text.
	 */
	public TextArea(String text) {
		super(text);
		// TODO Auto-generated constructor stub
		init();
	}


	/**
	 * Initializing method.
	 */
	protected void init() {
		setWrapStyleWord(true);
		setLineWrap(true);
		
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
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ClipboardUtil.util.setText(text);
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

	
	/**
	 * Setting text from specified reader.
	 * @param reader specified reader.
	 */
	public void setText(Reader reader) {
		StringBuffer textBuffer = new StringBuffer();
		try {
			char[] charBuffer = new char[4096];
			int read = 0;
			do {
				textBuffer.append(charBuffer, 0, read);
				read = reader.read(charBuffer);
			}
			while (read >= 0);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		setText(textBuffer.toString());
	}

	
	/**
	 * Setting text from specified input stream.
	 * @param in specified input stream.
	 */
	public void setText(InputStream in) {
		InputStreamReader reader = new InputStreamReader(in);
		setText(reader);
	}


	/**
	 * Setting text from archive (file).
	 * @param archiveUri URI of archive (file).
	 */
	public void setText(xURI archiveUri) {
		UriAdapter adapter = new UriAdapter(archiveUri);
		if (!adapter.exists(archiveUri) || !adapter.isArchive(archiveUri)) {
			adapter.close();
			return;
		}
		
		try {
			Reader reader = adapter.getReader(archiveUri);
			setText(reader);
			reader.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		adapter.close();
	}


	@Override
	public void setText(String t) {
		// TODO Auto-generated method stub
		super.setText(t);
		setCaretPosition(0);
	}
	
	
}
