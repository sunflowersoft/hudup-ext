/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.logistic;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class is list of plain texts (strings). This class is mainly used for text selection.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class PlainList implements TextParsable, Serializable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Internal plain list (list of strings).
	 */
	protected List<String> list = Util.newList();
	
	
	/**
	 * Selected index.
	 */
	protected int selectedIndex = -1;
	
	
	/**
	 * Default constructor.
	 */
	public PlainList() {
		
	}

	
	/**
	 * Getting text at specified index.
	 * @param index specified index.
	 * @return string at specified index.
	 */
	public String get(int index) {
		return list.get(index);
	}
	
	
	/**
	 * Getting size of list.
	 * @return size of list.
	 */
	public int size() {
		return list.size();
	}
	
	
	/**
	 * Add specified text.
	 * @param element specified text.
	 */
	public void add(String element) {
		if (!list.contains(element))
			list.add(element);
		
		if (list.size() > 0 && selectedIndex == -1)
			selectedIndex = 0;
	}
	
	
	/**
	 * Adding all texts from other collection.
	 * @param collection other collection of texts.
	 */
	public void addAll(Collection<String> collection) {
		for (String element : collection)
			add(element);
	}
	
	
	/**
	 * Clearing this list.
	 */
	public void clear() {
		list.clear();
		selectedIndex = -1;
	}
	
	
	/**
	 * Getting selected index.
	 * @return selected index.
	 */
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	
	/**
	 * Getting selected text.
	 * @return selected text.
	 */
	public String getSelectedText() {
		if (selectedIndex == -1)
			return null;
		else
			return list.get(selectedIndex);
	}
	
	
	/**
	 * Setting selected text.
	 * @param text selected text.
	 */
	public void setSelectedText(String text) {
		int index = list.indexOf(text);
		if (index != -1)
			selectedIndex = index;
	}
	
	
	/**
	 * Setting selected index.
	 * @param selectedIndex selected index.
	 */
	public void setSelectedIndex(int selectedIndex) {
		if (selectedIndex >= 0 && selectedIndex < list.size())
			this.selectedIndex = selectedIndex;
	}
	
	
	/**
	 * Removing text at specified index.
	 * @param index specified index.
	 */
	public void remove(int index) {
		String selectedText = getSelectedText();
		list.remove(index);
		
		if (list.size() > 0) {
			
			if (selectedText != null)
				setSelectedText(selectedText);
		}
		else {
			selectedIndex = -1;
		}
		
		
	}
	
	
	/**
	 * Converting this list into array of texts.
	 * @return array of elements (array of texts).
	 */
	public String[] toArray() {
		return list.toArray(new String[] { });
	}
	
	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		list.clear();
		selectedIndex = -1;
		
		int index = spec.lastIndexOf(TextParserUtil.LINK_SEP);
		if (index == -1)
			return;
		
		List<String> newList = TextParserUtil.parseTextList(
				spec.substring(0, index), TextParserUtil.LINK_SEP);
		for (String el : newList) {
			list.add(TextParserUtil.decryptReservedChars(el));
		}
		
		selectedIndex = Integer.parseInt(spec.substring(index + 1));
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		List<String> newList = Util.newList();
		for (String element : list) {
			newList.add(TextParserUtil.encryptReservedChars(element));
		}
		
		String listText = TextParserUtil.toText(newList, TextParserUtil.LINK_SEP);
		listText = listText.replaceAll(" ", "");
		return listText + TextParserUtil.LINK_SEP + selectedIndex;
	}
	
	
	@Override
	public String toString() {
		return toText();
	}
	
	
}
