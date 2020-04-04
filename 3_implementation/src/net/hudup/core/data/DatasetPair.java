/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.UUID;

import net.hudup.core.logistic.LogUtil;

/**
 * This contains a pair of dataset such as training dataset and testing dataset.
 * Note, training dataset is the dataset used to build up some model like knowledge database ({@code KBase}) whereas testing dataset is the dataset used to test or evaluate such model.
 * Both training dataset and testing dataset are extracted from a so-called whole dataset or entire dataset.
 * However it also contains the whole dataset (entire dataset) from which training dataset and testing dataset are extracted.
 * As a convention, it is called {@code dataset pair}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class DatasetPair implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Training dataset.
	 */
	protected Dataset training = null;
	
	
	/**
	 * Testing dataset.
	 */
	protected Dataset testing = null;
	
	
	/**
	 * Whole dataset (entire dataset) from which training dataset and testing dataset are extracted.
	 */
	protected Dataset whole = null;

	
	/**
	 * Training time stamp;
	 */
	protected UUID trainingUUID = null;

	
	/**
	 * Testing time stamp;
	 */
	protected UUID testingUUID = null;
	
	
	/**
	 * Whole time stamp;
	 */
	protected UUID wholeUUID = null;

	
	/**
	 * Constructor with specified training dataset, testing dataset, and whole (entire) dataset.
	 * @param training specified training dataset.
	 * @param testing specified testing dataset.
	 * @param whole specified whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public DatasetPair(Dataset training, Dataset testing, Dataset whole) {
		this.training = training;
		this.testing = testing;
		this.whole = whole;
	}
	
	
	/**
	 * Constructor with specified training dataset and testing dataset. The whole (entire) dataset is null.
	 * @param training specified training dataset.
	 * @param testing specified testing dataset.
	 */
	public DatasetPair(Dataset training, Dataset testing) {
		this (training, testing, null);
	}

	
	/**
	 * Get training dataset.
	 * @return training dataset.
	 */
	public Dataset getTraining() {
		return training;
	}
	

	/**
	 * Get training UUID.
	 * @return training UUID.
	 */
	public UUID getTrainingUUID() {
		return trainingUUID;
	}
	
	
	/**
	 * Setting training dataset.
	 * @param training specified training dataset.
	 */
	public void setTraining(Dataset training) {
		this.training = training;
	}
	
	
	/**
	 * Setting training UUID.
	 * @param trainingUUID specified training UUID.
	 */
	public void setTrainingUUID(UUID trainingUUID) {
		this.trainingUUID = trainingUUID;
	}
	
	
	/**
	 * Getting testing dataset.
	 * @return testing dataset.
	 */
	public Dataset getTesting() {
		return testing;
	}

	
	/**
	 * Get testing UUID.
	 * @return testing UUID.
	 */
	public UUID getTestingUUID() {
		return testingUUID;
	}
	
	
	/**
	 * Setting testing dataset.
	 * @param testing specified testing dataset.
	 */
	public void setTesting(Dataset testing) {
		this.testing = testing;
	}

	
	/**
	 * Setting testing UUID.
	 * @param testingUUID specified testing UUID.
	 */
	public void setTestingUUID(UUID testingUUID) {
		this.testingUUID = testingUUID;
	}
	
	
	/**
	 * Getting the whole (entire) dataset.
	 * @return whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public Dataset getWhole() {
		return whole;
	}
	
	
	/**
	 * Get whole UUID.
	 * @return whole UUID.
	 */
	public UUID getWholeUUID() {
		return wholeUUID;
	}
	
	
	/**
	 * Setting the whole (entire) dataset.
	 * @param whole specified whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public void setWhole(Dataset whole) {
		this.whole = whole;
	}

	
	/**
	 * Setting whole UUID.
	 * @param wholeUUID specified whole UUID.
	 */
	public void setWholeUUID(UUID wholeUUID) {
		this.wholeUUID = wholeUUID;
	}
	
	
	/**
	 * Testing whether this dataset pair is valid. The dataset pair is valid if its both training dataset and testing dataset are not null. 
	 * @return whether this dataset pair is valid.
	 */
	public boolean validate() {
		return training != null && testing != null;
	}
	
	
	/**
	 * Getting the total number of users in training dataset.
	 * @return the total number of users in training dataset
	 */
	public int getTrainingUserNumber() {
		return getUserNumber(training);
	}

	
	/**
	 * Getting the total number of users in testing dataset.
	 * @return the total number of users in testing dataset.
	 */
	public int getTestingUserNumber() {
		return getUserNumber(testing);
	}
	
	
	/**
	 * Getting the total number of users in the whole (entire) dataset.
	 * @return the total number of users in the whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public int getWholeUserNumber() {
		return getUserNumber(whole);
	}


	/**
	 * Getting the total number of items in training dataset.
	 * @return the total number of items in training dataset.
	 */
	public int getTrainingItemNumber() {
		return getItemNumber(training);
	}

	
	/**
	 * Getting the total number of items in testing dataset.
	 * @return the total number of items in testing dataset.
	 */
	public int getTestingItemNumber() {
		return getItemNumber(testing);
	}
	
	
	/**
	 * Getting the total number of items in the whole (entire) dataset.
	 * @return the total number of items in the whole (entire) dataset from which training dataset and testing dataset are extracted.
	 */
	public int getWholeItemNumber() {
		return getItemNumber(whole);
	}

	
	/**
	 * Getting total users in specified dataset.
	 * @param dataset specified dataset.
	 * @return total users in specified dataset.
	 */
	private static int getUserNumber(Dataset dataset) {
		if (dataset == null)
			return 0;
		
		try {
			return dataset.fetchUserIds().getMetadata().getSize();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		return 0;
	}
	
	
	/**
	 * Getting total items in specified dataset.
	 * @param dataset specified dataset.
	 * @return total items in specified dataset.
	 */
	private static int getItemNumber(Dataset dataset) {
		if (dataset == null)
			return 0;
		
		try {
			return dataset.fetchItemIds().getMetadata().getSize();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		return 0;
	}
	
	
	/**
	 * Clearing this dataset pair, which means that training dataset, testing dataset, and the whole (entire) dataset are cleared. 
	 */
	public void clear() {
		if (training != null)
			training.clear();
		training = null;
		trainingUUID = null;
		
		if (testing != null)
			testing.clear();
		testing = null;
		testingUUID = null;
		
		if (whole != null)
			whole.clear();
		whole = null;
		wholeUUID = null;
	}
	

	/**
	 * Reloading training dataset, testing dataset, and the whole (entire) dataset in this dataset pair.
	 */
	public void reload() {
		DataConfig config = null;
		if ( (training != null) && !(training instanceof NullPointer) ) {
			config = (DataConfig) training.getConfig().clone();
			if (!config.getAsBoolean(DatasetAbstract.ONLY_MEMORY_FIELD)) {
				training.clear();
				training = DatasetUtil.loadDataset(config);
			}
		}
		
		if ( (testing != null) && !(testing instanceof NullPointer) ) {
			config = (DataConfig) testing.getConfig().clone();
			if (!config.getAsBoolean(DatasetAbstract.ONLY_MEMORY_FIELD)) {
				testing.clear();
				testing = DatasetUtil.loadDataset(config);
			}
		}
		
		if ( (whole != null) && !(whole instanceof NullPointer) ) {
			config = (DataConfig) whole.getConfig().clone();
			if (!config.getAsBoolean(DatasetAbstract.ONLY_MEMORY_FIELD)) {
				whole.clear();
				whole = DatasetUtil.loadDataset(config);
			}
		}
		
	}

	
}
