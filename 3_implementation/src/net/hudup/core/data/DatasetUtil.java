/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.parser.DatasetParser;

/**
 * This utility class, called {@code dataset utility}, provides utility methods processing on {@link Dataset} such as loading dataset and saving dataset.
 * In current implementation, {@code dataset utility} gives very few utility methods.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class DatasetUtil {
	
	
	/**
	 * Loading (reading, parsing) dataset from specified configuration.
	 * Actually, this method calls the method {@link DatasetParser#parse(DataConfig)} of dataset parser.
	 * Note, {@code DatasetParser} is one of main interface responsible for reading and parsing coarse data such as CSV file, Excel file, database table into {@link Dataset}.
	 * 
	 * @param config specified configuration. 
	 * @return {@link Dataset} loaded from specified configuration.
	 */
	public final static Dataset loadDataset(DataConfig config) {
		try {
			DatasetParser parser = config.getParser();
			if (parser == null)
				return null;
			
			if (config.getDataDriverName() != null)
				parser.getConfig().setDataDriverName(config.getDataDriverName());
			
			return parser.parse(config);
		}
		catch (Throwable e) {
			e.printStackTrace();
			
		}
		return null;
	}

	
	/**
	 * Getting rating user identifiers from dataset.
	 * @param dataset specified dataset.
	 * @return rating user identifiers from dataset.
	 */
	public final static Set<Integer> getRatingUserIds(Dataset dataset) {
		Fetcher<RatingVector> fetcher = dataset.fetchUserRatings();
		return getIds(fetcher, true);
	}
	
	
	/**
	 * Getting rated item identifiers from dataset.
	 * @param dataset specified dataset.
	 * @return rated item identifiers from dataset.
	 */
	public final static Set<Integer> getRatedItemIds(Dataset dataset) {
		Fetcher<RatingVector> fetcher = dataset.fetchItemRatings();
		return getIds(fetcher, true);
	}
	
	
	/**
	 * Getting identifiers from fetcher of rating vectors.
	 * @param fetcher fetcher of rating vectors.
	 * @param autoClose if true, fetcher is closed automatically.
	 * @return set of identifiers.
	 */
	private final static Set<Integer> getIds(Fetcher<RatingVector> fetcher, boolean autoClose) {
		Set<Integer> ids = Util.newSet();
		if (fetcher == null) return ids;
		
		try {
			while (fetcher.next()) {
				RatingVector vRating = fetcher.pick();
				if (vRating == null) continue;
				
				int count = vRating.count(true);
				if (count > 0) ids.add(vRating.id());
			}
		}
		catch (Throwable e) {e.printStackTrace();}
		
		if (autoClose) {
			try {
				fetcher.close();
			}
			catch (Throwable e) {e.printStackTrace();}
		}
		
		return ids;
	}
	
	
}
