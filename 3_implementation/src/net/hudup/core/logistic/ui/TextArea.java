/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import javax.swing.JTextArea;

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

		setEditable(false);
		setWrapStyleWord(true);
		setLineWrap(true);
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
			e.printStackTrace();
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
			e.printStackTrace();
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
