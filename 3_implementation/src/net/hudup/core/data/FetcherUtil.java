/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;

/**
 * This utility class provides utility methods to process on {@link Fetcher}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FetcherUtil {
	
	
	/**
	 * Filling the specified collection by elements from specified fetcher.
	 * 
	 * @param <E> type of elements retrieved from fetcher.
	 * @param collection specified collection which is filled in by elements from specified fetcher.
	 * @param fetcher specified fetcher which is iterated to retrieve its elements which are filled in the specified collection.
	 * @param autoClose if {@code true}, the fetcher is automatically closed after the filling task is finished.
	 */
	public static <E /*extends Serializable*/> void fillCollection(Collection<E> collection, Fetcher<E> fetcher, boolean autoClose) {
		try {
			while (fetcher.next()) {
				E el = fetcher.pick();
				if (el != null)
					collection.add(el);
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (autoClose)
					fetcher.close();
				else
					fetcher.reset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
	}
	
	
	/**
	 * Fixing the problem of serializing memory fetcher. This method is now deprecated because the problem of serialization is solved in {@link MemFetcher#next()}.
	 * @param <T> type of elements.
	 * @param fetcher specified fetcher.
	 * @return the specified fetcher itself.
	 */
	public static <T> Fetcher<T> fixFetcherSerialized(Fetcher<T> fetcher) {
		if ((fetcher == null) || !(fetcher instanceof MemFetcher<?>))
			return fetcher;
		
		MemFetcher<T> memFetcher = (MemFetcher<T>)fetcher;
		if ((memFetcher.data != null) && (!(memFetcher.data instanceof Serializable))) {
			List<T> newData = Util.newList(memFetcher.data.size());
			newData.addAll(memFetcher.data);
			memFetcher.update(newData);
		}
		
		return memFetcher;
	}
	
	
}
