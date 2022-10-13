/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
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
	 * Extracting name of specified dataset.
	 * @param dataset specified dataset.
	 * @return name of specified dataset.
	 */
	public static String extractDatasetName(Dataset dataset) {
		DataConfig config = dataset != null ? dataset.getConfig() : null;
		if (config != null && config.getMainUnit() != null) {
			String mainUnit = config.getMainUnit();
			return config.getAsString(mainUnit);
		}
		else
			return null;
	}
	
	
	/**
	 * Getting dataset identifier. Note that this identifier is often number, not URI identifier.
	 * This identifier is often assigned in evaluation process.
	 * @param dataset specified dataset.
	 * @return identifier of specified dataset.
	 */
	public static int getDatasetId(Dataset dataset) {
		if (dataset == null) return -1;
		
		DataConfig config = dataset.getConfig();
		if (config != null && config.containsKey(DatasetAbstract.DATASETID_FIELD))
			return config.getAsInt(DatasetAbstract.DATASETID_FIELD);
		else
			return -1;
	}
	
	
	/**
	 * Setting dataset identifier. Note that this identifier is often number, not URI identifier.
	 * This identifier is often assigned in evaluation process.
	 * @param dataset specified dataset.
	 * @param id identifier of specified dataset.
	 */
	public static void setDatasetId(Dataset dataset, int id) {
		if (dataset == null || id < 0) return;
		
		DataConfig config = dataset.getConfig();
		if (config != null)
			config.put(DatasetAbstract.DATASETID_FIELD, id);
	}
	
	
	/**
	 * Extracting identifier or name of specified dataset. If the identifier exists, it is returned. Otherwise, the name is returned.
	 * Note that the identifier is often number, not URI identifier. The identifier is often assigned in evaluation process.
	 * @param dataset specified dataset.
	 * @return identifier or name of specified dataset. Return null if there is nor identifier neither name.
	 */
	public static String extractDatasetIdOrName(Dataset dataset) {
		int id = getDatasetId(dataset);
		if (id >= 0)
			return "" + id;
		else
			return extractDatasetName(dataset);
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
	 * Getting the most inner dataset of the specified dataset.
	 * @param dataset specified dataset.
	 * @return the most inner dataset of the specified dataset.
	 */
	public static Dataset getMostInnerDataset(Dataset dataset) {
		if (dataset == null)
			return null;
		else if (dataset instanceof DatasetRemoteWrapper) {
			DatasetRemote remoteDataset = ((DatasetRemoteWrapper)dataset).getRemoteDataset();
			if (remoteDataset == null)
				return null;
			else if (remoteDataset instanceof Dataset)
				return getMostInnerDataset((Dataset)remoteDataset);
			else
				return null;
		}
		else
			return dataset;
	}


	/**
	 * Getting most inner dataset of remote dataset.
	 * @param remoteDataset remote dataset.
	 * @return most inner dataset of remote dataset.
	 */
	public static Dataset getMostInnerDataset2(DatasetRemote remoteDataset) {
		if (remoteDataset == null)
			return null;
		else if (remoteDataset instanceof DatasetRemoteWrapper)
			return getMostInnerDataset2(((DatasetRemoteWrapper)remoteDataset).getRemoteDataset());
		else if (remoteDataset instanceof Dataset)
			return (Dataset)remoteDataset;
		else
			return null;
	}


	/**
	 * Check whether to call the specified dataset remotely. There exists a reference to unexported object.
	 * @param dataset specified dataset.
	 * @return whether to call the specified dataset remotely.
	 */
	public static boolean canCallRemote(Dataset dataset) {
		if (dataset == null)
			return false;
		else if (dataset instanceof DatasetRemote) {
			DataConfig config = null;
			try {
				config = ((DatasetRemote)dataset).remoteGetConfig();
			} catch (Throwable e) {}
			return config != null;
		}
		else
			return dataset.getConfig() != null;
	}
	
	
	/**
	 * 
	 * Exporting remote dataset as wrapper.
	 * @param serverPort port to export. Using port 0 if not concerning registry or naming.
     * @param exclusive exclusive mode.
	 * @return exported remote dataset.
	 */
	public static DatasetRemoteWrapper exportAsWrapper(DatasetRemote remoteDataset, int serverPort, boolean exclusive) {
		if (remoteDataset == null) return null;
		
		DatasetRemoteWrapper newRemoteDataset = null;
		if (remoteDataset instanceof DatasetRemoteWrapper)
			newRemoteDataset = (DatasetRemoteWrapper)remoteDataset;
		else
			newRemoteDataset = Util.getPluginManager().wrap(remoteDataset, exclusive);
		
		try {
			((DatasetRemoteWrapper)newRemoteDataset).exportInside(serverPort);
		} catch (RemoteException e) {LogUtil.trace(e);}

		return newRemoteDataset;
	}
	
	
	/**
	 * Unexporting the specified remote dataset.
	 * @param remoteDataset specified remote dataset.
	 * @param forced forced mode to unexport datasets.
	 */
	public static void unexport(DatasetRemote remoteDataset, boolean forced) {
		try {
			if (remoteDataset == null)
				return;
			else if (remoteDataset instanceof DatasetRemoteWrapper) {
				if (forced)
					((DatasetRemoteWrapper)remoteDataset).forceUnexport();
				else
					((DatasetRemoteWrapper)remoteDataset).unexport();
			}
			else
				NetUtil.RegistryRemote.unexport(remoteDataset);

		} catch (Throwable e) {LogUtil.trace(e);}
	}
	
	
}
