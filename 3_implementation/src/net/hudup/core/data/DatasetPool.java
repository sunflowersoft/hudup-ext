/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.xURI;

/**
 * This class contains a list of dataset pairs. Each dataset pair represented by {@link DatasetPair} class includes training dataset and testing dataset.
 * As a convention, this class is called {@code dataset pool}.
 * In general, {@code dataset pool} contains many training and testing datasets, which is fed to evaluator represented by {@code Evaluator}. In other words, it allows evaluator assesses algorithm on many testing datasets.
 * Moreover, dataset pool provides utility methods to process on such list of dataset pairs.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class DatasetPool implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * List of dataset pairs. Each dataset pair represented by {@link DatasetPair} class includes training dataset and testing dataset.
	 * Note, training dataset is the dataset used to build up some model like knowledge database ({@code KBase}) whereas testing dataset is the dataset used to test or evaluate such model.
	 * Both training dataset and testing dataset are extracted from a so-called whole dataset or entire dataset.
	 */
	protected List<DatasetPair> dspList = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public DatasetPool() {
		
	}
	

	/**
	 * Constructor with specified collection of of dataset pairs.
	 * @param dsPairs specified collection of of dataset pairs.
	 */
	public DatasetPool(Collection<DatasetPair> dsPairs) {
		fill(dsPairs);
	}

	
	/**
	 * Constructor with a specified dataset pair.
	 * @param dsPair specified dataset pair.
	 */
	public DatasetPool(DatasetPair dsPair) {
		fill(dsPair);
	}

	
	/**
	 * Constructor with one dapaset pair including specified training dataset and specified testing dataset.
	 * @param trainingSet specified training dataset.
	 * @param testingSet specified testing dataset.
	 */
	public DatasetPool(Dataset trainingSet, Dataset testingSet) {
		fill(trainingSet, testingSet, null);
	}
	
	
	/**
	 * Constructor with one dataset pair including specified training dataset, specified testing dataset, and specified entire dataset.
	 * @param trainingSet specified training dataset.
	 * @param testingSet specified testing dataset.
	 * @param wholeSet specified whole (entire) dataset from which training dataset and testing dataset
	 */
	public DatasetPool(Dataset trainingSet, Dataset testingSet, Dataset wholeSet) {
		fill(trainingSet, testingSet, wholeSet);
	}
	
	
    /**
     * Getting a dataset pair at specified index.
     * @param idx specified index.
     * @return {@link DatasetPair} at specified index.
     */
    public DatasetPair get(int idx) {
    	return dspList.get(idx);
    }
	
	
    /**
     * Getting the size of this dataset pool which is the number of dataset pairs.
     * @return size of this dataset pool.
     */
    public int size() {
    	return dspList.size();
    }
    
    
    /**
     * Clearing this dataset pool and then adding the specified dataset pair into this dataset pool.
     * @param dsPair specified dataset pair.
     */
    public void fill(DatasetPair dsPair) {
    	clear();
    	this.dspList.add(dsPair);
    }
    
    
    /**
     * Clearing this dataset pool and then adding a dataset pair (including specified training set, testing dataset, and whole dataset) into this dataset pool.
     * @param trainingSet specified training dataset.
     * @param testingSet specified testing dataset.
     * @param wholeSet specified whole (entire) dataset.
     */
    public void fill(Dataset trainingSet, Dataset testingSet, Dataset wholeSet) {
    	fill(new DatasetPair(trainingSet, testingSet, wholeSet));
    }
    

    /**
     * Clearing this dataset pool and then adding a dataset pair (including specified training set and testing dataset) into this dataset pool.
     * @param trainingSet specified training dataset.
     * @param testingSet specified testing dataset.
     */
    public void fill(Dataset trainingSet, Dataset testingSet) {
    	fill(new DatasetPair(trainingSet, testingSet, null));
    }

    
    /**
     * Clearing this dataset pool and then adding a collection of dataset pairs into this dataset pool.
     * @param dsPairs collection of dataset pairs.
     */
    public void fill(Collection<DatasetPair> dsPairs) {
    	clear();
    	this.dspList.addAll(dsPairs);
    }
    
    
    /**
     * Clearing this dataset pool and then adding other dataset pool into this dataset pool.
     * @param pool other dataset pool.
     */
    public void fill(DatasetPool pool) {
    	fill(pool.dspList);
    }
    
    
    /**
     * Adding a dataset pair into this dataset pool.
     * @param dsPair specified dataset pair.
     */
    public void add(DatasetPair dsPair) {
    	this.dspList.add(dsPair);
    }
    
    
    /**
     * Removing the specified dataset pair from this dataset pool.
     * @param dsPair the dataset pair that will be removed.
     */
    public void remove(DatasetPair dsPair) {
    	boolean ret = this.dspList.remove(dsPair);
    	if (ret && dsPair != null)
    		dsPair.clear();
    }
    
    
    /**
     * Removing the specified dataset pair from this dataset pool without clearing dataset.
     * @param dsPair the dataset pair that will be removed.
     */
    public void removeWithoutClear(DatasetPair dsPair) {
    	this.dspList.remove(dsPair);
    }

    
    /**
     * Removing the dataset pair at specified index.
     * @param idx index of {@link DatasetPair} which will be removed.
     */
    public void remove(int idx) {
    	DatasetPair pair = this.dspList.remove(idx);
    	if (pair != null)
    		pair.clear();
    }
    
    
    /**
     * Removing dataset pairs at indices.
     * @param idxes array of indices of dataset pairs which will be removed.
     */
    public void remove(int[] idxes) {
    	List<DatasetPair> list = Util.newList();
    	for (int idx : idxes) {
    		list.add(dspList.get(idx));
    	}
    	
    	for (DatasetPair pair : list) {
    		remove(pair);
    	}
    	
    }
    
    
    /**
     * Removing dataset pairs at indices without clearing datasets.
     * @param idxes array of indices of dataset pairs which will be removed.
     */
    public void removeWithoutClear(int[] idxes) {
    	List<DatasetPair> list = Util.newList();
    	for (int idx : idxes) {
    		list.add(dspList.get(idx));
    	}
    	
    	for (DatasetPair pair : list) {
    		removeWithoutClear(pair);
    	}
    	
    }
    
    
    /**
     * All dataset pairs of this pool are stored in the internal list {@link #dspList}.
     * This method moving dataset pairs of such list in specified range to specified position.
     * @param start start position of the specified range.
     * @param end end position of the specified range.
     * @param to specified position where dataset pairs in specified range are moved to.
     */
	public void moveRow(int start, int end, int to) {
		DSUtil.moveRow(dspList, start, end, to);	
		
	}

	
	/**
     * Clearing this dataset pool, which means that all dataset pairs are removed from this dataset pool.
     */
    public void clear() {
		for (int i = 0; i < dspList.size(); i++) {
			DatasetPair pair = dspList.get(i);
			pair.clear();
		}
		dspList.clear();
    }
    

    /**
     * Reloading this dataset pool, which means that reloading all training datasets, testing datasets, whole (entire) datasets in all dataset pairs.
     */
    public void reload() {
		for (int i = 0; i < dspList.size(); i++) {
			DatasetPair pair = dspList.get(i);
			pair.reload();
		}
    }
    
    
    /**
     * Getting the total number of users in training datasets in dataset pairs.
     * @return the total number of users in training datasets in dataset pairs.
     */
    public int getTotalTrainingUserNumber() {
    	int count = 0;
    	for (DatasetPair pair : dspList) {
			count += pair.getTrainingUserNumber();
    	}
    	
    	return count;
    }
    
    
    /**
     * Getting the total number of users in testing datasets in dataset pairs.
     * @return the total number of users in testing datasets in dataset pairs.
     */
    public int getTotalTestingUserNumber() {
    	int count = 0;
    	for (DatasetPair pair : dspList) {
			count += pair.getTestingUserNumber();
    	}
    	
    	return count;
    }

    
    /**
     * Getting the total number of users in whole (entire) datasets in dataset pairs.
     * @return the total number of users in whole (entire) datasets in dataset pairs.
     */
    public int getTotalWholeUserNumber() {
    	int count = 0;
    	for (DatasetPair pair : dspList) {
			count += pair.getWholeUserNumber();
    	}
    	
    	return count;
    }


    /**
     * Getting the total number of items in training datasets in dataset pairs.
     * @return the total number of items in training datasets in dataset pairs.
     */
    public int getTotalTrainingItemNumber() {
    	int count = 0;
    	for (DatasetPair pair : dspList) {
			count += pair.getTrainingItemNumber();
    	}
    	
    	return count;
    }
    
    
    /**
     * Getting the total number of items in testing datasets in dataset pairs.
     * @return the total number of items in testing datasets in dataset pairs.
     */
    public int getTotalTestingItemNumber() {
    	int count = 0;
    	for (DatasetPair pair : dspList) {
			count += pair.getTestingItemNumber();
    	}
    	
    	return count;
    }

    
    /**
     * Getting the total number of items in whole (entire) datasets in dataset pairs.
     * @return the total number of items in whole (entire) datasets in dataset pairs.
     */
    public int getTotalWholeItemNumber() {
    	int count = 0;
    	for (DatasetPair pair : dspList) {
			count += pair.getWholeItemNumber();
    	}
    	
    	return count;
    }

    
    /**
     * Every dataset (training dataset, testing dataset, or whole dataset) can has a URI pointing to where such dataset is stored.
     * Such URI is called identified URI (ID URI) of dataset.
     * This method looks up a dataset pair in this pool so that such dataset pair contains the training dataset specified by training ID URI and the testing dataset specified by testing ID URI.
     * Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet. URI in Hudup framework is represented by {@link xURI} class.
     * 
     * @param trainingId identified URI (ID URI) of training dataset.
     * @param testingId identified URI (ID URI) of testing dataset.
     * @return dataset pair contains the training dataset specified by training ID URI and the testing dataset specified by testing ID URI.
     */
    public DatasetPair findTrainingTesting(xURI trainingId, xURI testingId) {
    	if (trainingId == null || testingId == null)
    		return null;
    	
    	DatasetPair found = null;
		for (DatasetPair pair : dspList) {
			Dataset training = pair.getTraining();
			Dataset testing = pair.getTesting();
			if (training == null || testing == null) continue;
			
			DataConfig trainingCfg = training.getConfig();
			DataConfig testingCfg = testing.getConfig();
			
			if (trainingCfg != null && trainingCfg.getUriId().equals(trainingId) &&
				testingCfg != null && testingCfg.getUriId().equals(testingId)) {
				found = pair;
				break;
			}
		}
		
		return found;
    }
    
    
    /**
     * Every dataset (training dataset, testing dataset, or whole dataset) can has a URI pointing to where such dataset is stored.
     * Such URI is called identified URI (ID URI) of dataset.
     * This method looks up a dataset pair in this pool so that such dataset pair contains the training dataset specified by training ID URI.
     * Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet. URI in Hudup framework is represented by {@link xURI} class.
     * 
     * @param trainingId identified URI (ID URI) of training dataset.
     * @return dataset pair contains the training dataset specified by training ID URI.
     */
    public DatasetPair findTraining(xURI trainingId) {
    	if (trainingId == null)
    		return null;
    	
    	DatasetPair found = null;
		for (DatasetPair pair : dspList) {
			Dataset training = pair.getTraining();
			if (training == null) continue;
			
			DataConfig trainingCfg = training.getConfig();
			
			if (trainingCfg != null && trainingCfg.getUriId().equals(trainingId)) {
				found = pair;
				break;
			}
		}
		
		return found;
    }

    
    /**
     * Every dataset (training dataset, testing dataset, or whole dataset) can has a URI pointing to where such dataset is stored.
     * Such URI is called identified URI (ID URI) of dataset.
     * This method looks up a dataset pair in this pool so that such dataset pair contains the whole (entire) dataset specified by whole ID URI.
     * Note that URI, abbreviation of Uniform Resource Identifier, is the string of characters used to identify a resource on Internet. URI in Hudup framework is represented by {@link xURI} class.
     * 
     * @param wholeId identified URI (ID URI) of whole (entire) dataset. Note that whole (entire) dataset is the one from which training dataset and testing dataset are extracted.
     * @return dataset pair contains the whole (entire) dataset specified by whole ID URI.
     */
    public DatasetPair findWholeSet(xURI wholeId) {
    	if (wholeId == null)
    		return null;
    	
    	DatasetPair found = null;
		for (DatasetPair pair : dspList) {
			Dataset whole = pair.getWhole();
			if (whole == null)
				continue;
			
			DataConfig config = whole.getConfig();
			
			if (config != null && config.getUriId().equals(wholeId)) {
				found = pair;
				break;
			}
		}
		
		return found;
    }

    
    /**
     * Removing all pairs but not clearing datasets.
     */
    public void removeAllNoClearDatasets() {
    	dspList.clear();
    }

    
	/**
	 * Filling missing UUIDs.
	 */
	public void fillMissingUUID() {
		for (DatasetPair pair : dspList) {
			if (pair.training != null && pair.trainingUUID == null)
				pair.trainingUUID = UUID.randomUUID();
			
			if (pair.testing != null && pair.testingUUID == null)
				pair.testingUUID = UUID.randomUUID();
			
			if (pair.whole != null && pair.wholeUUID == null)
				pair.wholeUUID = UUID.randomUUID();
		}
	}

	
	/**
	 * Converting normal dataset pool to exchanged dataset.
	 * @return exchanged dataset pool.
	 */
	public DatasetPoolExchanged toDatasetExchangedPool() {
		List<DatasetPairExchanged> exchangeDspList = Util.newList();
		for (DatasetPair pair : dspList) {
			if (pair == null) continue;
			
			DatasetRemoteWrapper training = null;
			UUID trainingUUID = null;
			if (pair.training != null) {
				if (pair.training instanceof DatasetRemoteWrapper)
					training = (DatasetRemoteWrapper)pair.training;
				else if (pair.training instanceof DatasetRemote)
					training = Util.getPluginManager().wrap((DatasetRemote)pair.training, false);
				trainingUUID = training != null ? pair.trainingUUID : null;
			}
			
			DatasetRemoteWrapper testing = null;
			UUID testingUUID = null;
			if (pair.testing != null) {
				if (pair.testing instanceof DatasetRemoteWrapper)
					testing = (DatasetRemoteWrapper)pair.testing;
				else if (pair.testing instanceof DatasetRemote)
					testing = Util.getPluginManager().wrap((DatasetRemote)pair.testing, false);
				testingUUID = testing != null ? pair.testingUUID : null;
			}
			
			DatasetRemoteWrapper whole = null;
			UUID wholeUUID = null;
			if (pair.whole != null) {
				if (pair.whole instanceof DatasetRemoteWrapper)
					whole = (DatasetRemoteWrapper)pair.whole;
				else if (pair.whole instanceof DatasetRemote)
					whole = Util.getPluginManager().wrap((DatasetRemote)pair.whole, false);
				wholeUUID = whole != null ? pair.wholeUUID : null;
			}
			
			
			if (training != null || testing != null || whole != null) {
				DatasetPairExchanged newPair = new DatasetPairExchanged(training, testing, whole);
				newPair.trainingUUID = trainingUUID;
				newPair.testingUUID = testingUUID;
				newPair.wholeUUID = wholeUUID;
				exchangeDspList.add(newPair);
			}
		}
		
		DatasetPoolExchanged exchangedPool = new DatasetPoolExchanged();
		exchangedPool.dspList = exchangeDspList;
		return exchangedPool;
	}

	
	/**
	 * Converting normal dataset pool to exchanged dataset in client. This method is called by evaluator GUI.
	 * @return exchanged dataset pool in client.
	 */
	public DatasetPoolExchanged toDatasetExchangedPoolClient() {
		List<DatasetPairExchanged> exchangeDspList = Util.newList();
		for (DatasetPair pair : dspList) {
			if (pair == null) continue;

			DatasetRemote training = null;
			UUID trainingUUID = null;
			if (pair.training != null) {
				if (DatasetUtil.isRemote(pair.training)) {
					training = null;
					trainingUUID = pair.trainingUUID;
				}
				else if (pair.training instanceof DatasetRemote) { //AbstractDataset as usual.
					training = (DatasetRemote)pair.training;
					trainingUUID = pair.trainingUUID;
					trainingUUID = trainingUUID != null ? trainingUUID : UUID.randomUUID();
				}
			}
			
			DatasetRemote testing = null;
			UUID testingUUID = null;
			if (pair.testing != null) {
				if (DatasetUtil.isRemote(pair.testing)) {
					testing = null;
					testingUUID = pair.testingUUID;
				}
				else if (pair.testing instanceof DatasetRemote) { //AbstractDataset as usual.
					testing = (DatasetRemote)pair.testing;
					testingUUID = pair.testingUUID;
					testingUUID = testingUUID != null ? testingUUID : UUID.randomUUID();
				}
			}
			
			DatasetRemote whole = null;
			UUID wholeUUID = null;
			if (pair.whole != null) {
				if (DatasetUtil.isRemote(pair.whole)) {
					whole = null;
					wholeUUID = pair.wholeUUID;
				}
				else if (pair.whole instanceof DatasetRemote) { //AbstractDataset as usual.
					whole = (DatasetRemote)pair.whole;
					wholeUUID = pair.wholeUUID;
					wholeUUID = wholeUUID != null ? wholeUUID : UUID.randomUUID();
				}
			}
			
//			if (training != null || testing != null || whole != null) {
				DatasetPairExchanged newPair = new DatasetPairExchanged(training, testing, whole);
				newPair.trainingUUID = trainingUUID;
				newPair.testingUUID = testingUUID;
				newPair.wholeUUID = wholeUUID;
				exchangeDspList.add(newPair);
//			}
		}
		
		DatasetPoolExchanged exchangedPool = new DatasetPoolExchanged();
		exchangedPool.dspList = exchangeDspList;
		return exchangedPool;
	}
	
	
}
