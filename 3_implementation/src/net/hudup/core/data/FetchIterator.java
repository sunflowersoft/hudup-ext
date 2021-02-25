/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Iterator;

import net.hudup.core.logistic.LogUtil;

/**
 * This class implements the iterator for fetcher.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 * @param <E> data type.
 */
public class FetchIterator<E> implements Iterator<E>, Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal fetcher.
	 */
	protected Fetcher<E> fetcher = null;
	
	
	/**
	 * Constructor with specified fetcher.
	 * @param fetcher specified fetcher.
	 */
	public FetchIterator(Fetcher<E> fetcher) {
		this.fetcher = fetcher;
	}

	
	@Override
	public boolean hasNext() {
		try {
			return fetcher.next();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}

	
	@Override
	public E next() {
		try {
			return fetcher.pick();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return null;
	}

	
}
