/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
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
			LogUtil.trace(e);
			
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
		catch (Throwable e) {LogUtil.trace(e);}
		
		if (autoClose) {
			try {
				fetcher.close();
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		
		return ids;
	}
	
	
	/**
	 * Extracting text form of URI identifier of specified dataset.
	 * @param dataset specified dataset.
	 * @return text form of URI identifier of specified dataset.
	 */
	public static String extractUriIdText(Dataset dataset) {
		if (dataset == null)
			return "";
		else
			return extractUriIdText(dataset.getConfig());
	}
	
	
	/**
	 * Getting URI representative text form of URI identifier.
	 * @param config specified configuration.
	 * @return URI representative text form of URI identifier.
	 */
	public static String extractUriIdText(DataConfig config) {
		if (config == null) return NullPointer.NULL_POINTER;

		xURI uriId = config.getUriId();
		String uriIdText = uriId != null ? uriId.toString() : NullPointer.NULL_POINTER;
		if (uriId == null || Constants.hardwareAddress == null || Constants.hostAddress == null) {
			return uriIdText;
		}
		else {
			String hardwareAddress = config.getAsString(DatasetAbstract.HARDWARE_ADDR_FIELD);
			String hostAddress = config.getAsString(DatasetAbstract.HOST_ADDR_FIELD);

			if (hardwareAddress == null || hostAddress == null)
				return uriIdText;
			else if (!Constants.hardwareAddress.equals(hardwareAddress)) {
				String lastName = uriId.getLastName();
				String newUriIdText = "hdp://" + hostAddress + ":" + Constants.DEFAULT_SERVER_PORT + "/somewhere";
				newUriIdText = lastName != null && !lastName.isEmpty() ? newUriIdText + "/" + lastName : newUriIdText;
				return newUriIdText;
			}
			else
				return uriIdText;
		}
	}
	
	
	/**
	 * Checking whether the specified dataset is remote object.
	 * @param dataset specified dataset.
	 * @return whether the specified dataset is remote object.
	 */
	public static boolean isRemote(Dataset dataset) {
		if (dataset == null)
			return false;
		else if (dataset instanceof DatasetRemoteWrapper) {
			DatasetRemote remoteDataset = ((DatasetRemoteWrapper)dataset).getRemoteDataset();
			return ((remoteDataset != null) && !(remoteDataset instanceof Dataset));
		}
		else
			return false;
	}

	
	/**
	 * Getting most inner dataset of remote dataset.
	 * @param remoteDataset remote dataset.
	 * @return most inner dataset of remote dataset.
	 */
	public static Dataset getMostInnerDataset(DatasetRemote remoteDataset) {
		if (remoteDataset == null)
			return null;
		else if (remoteDataset instanceof DatasetRemoteWrapper)
			return getMostInnerDataset(((DatasetRemoteWrapper)remoteDataset).getRemoteDataset());
		else if (remoteDataset instanceof Dataset)
			return (Dataset)remoteDataset;
		else
			return null;
	}


	/**
	 * Getting the most inner dataset of the specified dataset.
	 * @param dataset specified dataset.
	 * @return the most inner dataset of the specified dataset.
	 */
	public static Dataset getMostInnerDataset2(Dataset dataset) {
		if (dataset == null)
			return null;
		else if (dataset instanceof DatasetRemoteWrapper) {
			DatasetRemote remoteDataset = ((DatasetRemoteWrapper)dataset).getRemoteDataset();
			if (remoteDataset == null)
				return null;
			else if (remoteDataset instanceof Dataset)
				return getMostInnerDataset2((Dataset)remoteDataset);
			else
				return null;
		}
		else
			return dataset;
	}


}
