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
 * This class implements the iterable object for fetcher.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 * @param <T> data type.
 */
public class FetcherIterable<T> implements Iterable<T>, Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal fetcher.
	 */
	protected Fetcher<T> fetcher = null;
	
	
	/**
	 * Constructor with specified fetcher.
	 * @param fetcher specified fetcher.
	 */
	public FetcherIterable(Fetcher<T> fetcher) {
		this.fetcher = fetcher;
	}

	
	@Override
	public Iterator<T> iterator() {
		try {
			fetcher.reset();
		} catch (Throwable e) {LogUtil.trace(e);}
		return new FetchIterator<T>(fetcher);
	}

	
}
