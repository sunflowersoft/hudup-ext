/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
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
		init();
	}


	/**
	 * Constructor with specified document.
	 * @param doc specified document.
	 */
	public TextArea(Document doc) {
		super(doc);
		init();
	}


	/**
	 * Constructor with specified rows and columns.
	 * @param rows specified row.
	 * @param columns specified columns.
	 */
	public TextArea(int rows, int columns) {
		super(rows, columns);
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
		init();
	}


	/**
	 * Constructor with specified text.
	 * @param text specified text.
	 */
	public TextArea(String text) {
		super(text);
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
		super.setText(t);
		setCaretPosition(0);
	}
	
	
	/**
	 * Showing text area dialog.
	 * @param comp parent component.
	 * @param content shown content.
	 * @param editable editable flag.
	 */
	public static void showDlg(Component comp, Object content, boolean editable) {
		if (content == null) {
			JOptionPane.showMessageDialog(comp, "Null content", "Null content", JOptionPane.INFORMATION_MESSAGE);
			return;
		}

		JDialog dlgTextArea = new JDialog(UIUtil.getFrameForComponent(comp), true);
		dlgTextArea.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgTextArea.setSize(400, 400);
		dlgTextArea.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		dlgTextArea.setLayout(new BorderLayout());
		
		TextArea txtArea = null;
		if (content instanceof String)
			txtArea = new TextArea((String)content);
		else if (content instanceof Document)
			txtArea = new TextArea((Document)content);
		else
			txtArea = new TextArea(content.toString());
		txtArea.setEditable(editable);
		dlgTextArea.add(new JScrollPane(txtArea), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		dlgTextArea.add(footer, BorderLayout.SOUTH);

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dlgTextArea.dispose();
			}
		});
		footer.add(close);

		dlgTextArea.setVisible(true);
	}
	
	
}
