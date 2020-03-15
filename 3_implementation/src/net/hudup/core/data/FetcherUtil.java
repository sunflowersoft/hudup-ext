/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.util.Collection;

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
			e.printStackTrace();
		}
		finally {
			try {
				if (autoClose)
					fetcher.close();
				else
					fetcher.reset();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
	}
	
	
	/**
	 * Fixing the problem of serializing memory fetcher.
	 * @param fetcher specified fetcher.
	 * @return the specified fetcher itself.
	 */
	public static <T> Fetcher<T> fixFetcherSerialized(Fetcher<T> fetcher) {
		if ((fetcher == null) || !(fetcher instanceof MemFetcher<?>))
			return fetcher;
		
		MemFetcher<T> memFetcher = (MemFetcher<T>)fetcher;
		if (memFetcher.iterator == null && memFetcher.data != null) {
			memFetcher.update(memFetcher.data);
		}
		
		return memFetcher;
	}
	
	
}
