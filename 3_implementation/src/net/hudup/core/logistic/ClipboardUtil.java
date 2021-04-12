/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;

/**
 * This class provides utility methods to process on clip-board such as getting text from clip-board and setting text to clip-board.
 *  
 * @author Loc Nguyen
 * @version 10.0
 */
public class ClipboardUtil implements ClipboardOwner {

	
	/**
	 * The system clip-board.
	 */
	protected Clipboard clipboard = null;
	
	
	/**
	 * The public utility variable for processing on clip-board.
	 */
	public final static ClipboardUtil util = new ClipboardUtil();
	
	
	/**
	 * Default constructor.
	 */
	private ClipboardUtil() {
		try {
			this.clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			this.clipboard = null;
		}
	}

	
	/**
	 * Setting (copying) the specified text data into clip-board.
	 * @param data specified text that is copied into clip-board.
	 */
	public void setText(String data) {
		if (clipboard == null || data == null)
			return;
		
		try {
			StringSelection stringSelection = new StringSelection(data);
		    clipboard.setContents(stringSelection, this);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Getting (retrieving) current text data from clip-board.
	 * @return current text data from clip-board.
	 */
	public String getText() {
		
		String result = "";
		if (clipboard == null)
			return result;
		
		try {
			Transferable contents = clipboard.getContents(null);
			
			if (contents == null || 
					!contents.isDataFlavorSupported(DataFlavor.stringFlavor))
				return "";
			
			result = (String)contents.getTransferData(DataFlavor.stringFlavor);
			
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			
		}
		
		return result;
	}
	
	
	@Override
	public void lostOwnership(Clipboard clipboard, Transferable contents) {
		
	}
	
	
}
