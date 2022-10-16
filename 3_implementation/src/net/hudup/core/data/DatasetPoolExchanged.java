/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.UUID;

import net.hudup.core.Util;

/**
 * This class is the exchanged data for dataset pool.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetPoolExchanged  implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * List of remote dataset pairs.
	 */
	public List<DatasetPairExchanged> dspList = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public DatasetPoolExchanged() {
		
	}

	
	/**
     * Clearing this dataset pool, which means that all dataset pairs are removed from this dataset pool.
	 * @param forced forced mode to unexport datasets.
	 */
	public void clear(boolean forced) {
		for (DatasetPairExchanged pair : dspList) {
			pair.clear(forced);
		}
		dspList.clear();
	}

	
	/**
	 * Reloading the pool.
	 */
	public void reload() {
		for (DatasetPairExchanged pair : dspList) {
			pair.reload();
		}
	}
	
	
	/**
	 * Filling missing UUIDs.
	 */
	public void fillMissingUUID() {
		for (DatasetPairExchanged pair : dspList) {
			if (pair.training != null && pair.trainingUUID == null)
				pair.trainingUUID = UUID.randomUUID();
			
			if (pair.testing != null && pair.testingUUID == null)
				pair.testingUUID = UUID.randomUUID();
			
			if (pair.whole != null && pair.wholeUUID == null)
				pair.wholeUUID = UUID.randomUUID();
		}
	}

	
	/**
	 * Exporting the this pool and returning a new exported pool.
	 * @param serverPort port to export. Using port 0 if not concerning registry or naming.
     * @param exclusive exclusive mode.
	 * @return exported pool.
	 */
	public DatasetPoolExchanged export(int serverPort, boolean exclusive) {
		List<DatasetPairExchanged> exporteDspList = Util.newList(dspList.size());
		
		for (DatasetPairExchanged pair : dspList) {
			if (pair == null) continue;
			
			DatasetRemote newTraining = DatasetUtil.exportAsWrapper(pair.training, serverPort, exclusive);
			DatasetRemote newTesting = DatasetUtil.exportAsWrapper(pair.testing, serverPort, exclusive);
			DatasetRemote newWhole = DatasetUtil.exportAsWrapper(pair.whole, serverPort, exclusive);
			DatasetPairExchanged newPair = new DatasetPairExchanged(newTraining, newTesting, newWhole);
			exporteDspList.add(newPair);
		}
		
		DatasetPoolExchanged wrapper = new DatasetPoolExchanged();
		wrapper.dspList = exporteDspList;
		return wrapper;
	}

	
	/**
	 * Unexporting inner datasets.
	 * @param forced forced mode to unexport datasets.
	 * @throws RemoteException if any error raises.
	 */
	public void unexport(boolean forced) {
		for (DatasetPairExchanged pair : dspList) {
			DatasetUtil.unexport(pair.training, forced);
			DatasetUtil.unexport(pair.testing, forced);
			DatasetUtil.unexport(pair.whole, forced);
		}
	}


	/**
	 * Finding dataset pair by training UUID;
	 * @param trainingUUID training UUID;
	 * @return dataset pair having specified training UUID;
	 */
	public DatasetPairExchanged findByTrainingUUID(UUID trainingUUID) {
		if (trainingUUID == null) return null;
		for (DatasetPairExchanged pair : dspList) {
			if (pair != null && pair.training != null && pair.trainingUUID != null && pair.trainingUUID.equals(trainingUUID))
				return pair; 
		}
		return null;
	}
	
	
	/**
	 * Finding dataset pair by testing UUID;
	 * @param testingUUID testing UUID;
	 * @return dataset pair having specified testing UUID;
	 */
	public DatasetPairExchanged findByTestingUUID(UUID testingUUID) {
		if (testingUUID == null) return null;
		for (DatasetPairExchanged pair : dspList) {
			if (pair != null && pair.testing != null && pair.testingUUID != null && pair.testingUUID.equals(testingUUID))
				return pair; 
		}
		return null;
	}


	/**
	 * Finding dataset pair by whole UUID;
	 * @param wholeUUID whole UUID;
	 * @return dataset pair having specified whole UUID;
	 */
	public DatasetPairExchanged findByWholeUUID(UUID wholeUUID) {
		if (wholeUUID == null) return null;
		for (DatasetPairExchanged pair : dspList) {
			if (pair != null && pair.whole != null && pair.wholeUUID != null && pair.wholeUUID.equals(wholeUUID))
				return pair; 
		}
		return null;
	}


	/**
	 * Checking whether containing only UUID (at clients).
	 * @return true if containing only UUID (at clients).
	 */
	public boolean containsOnlyUUID() {
		for (DatasetPairExchanged pair : dspList) {
			if (pair.training == null && pair.trainingUUID != null) return true;
			if (pair.testing == null && pair.testingUUID != null) return true;
			if (pair.whole == null && pair.wholeUUID != null) return true;
		}
		
		return false;
	}

	
	/**
	 * Converting exchanged dataset pool to normal dataset.
	 * @param referredPool referred pool.
	 * @return normal dataset.
	 */
	public DatasetPool toDatasetPool(DatasetPoolExchanged referredPool) {
		if (referredPool == null) referredPool = new DatasetPoolExchanged();
		
		DatasetPool pool = new DatasetPool();
		for (DatasetPairExchanged pair : dspList) {
			if (pair == null) continue;
			
			Dataset training = null;
			UUID trainingUUID = null;
			if (pair.training != null) {
				training = DatasetUtil.getMostInnerDataset2(pair.training);
				trainingUUID = training != null ? pair.trainingUUID : null;
			}
			else if (pair.trainingUUID != null) {
				DatasetPairExchanged newPair = referredPool.findByTrainingUUID(pair.trainingUUID);
				if (newPair != null) {
					training = DatasetUtil.getMostInnerDataset2(newPair.training);
					trainingUUID = training != null ? newPair.trainingUUID : null;
				}
			}
			
			Dataset testing = null;
			UUID testingUUID = null;
			if (pair.testing != null) {
				testing = DatasetUtil.getMostInnerDataset2(pair.testing);
				testingUUID = testing != null ? pair.testingUUID : null;
			}
			else if (pair.testingUUID != null) {
				DatasetPairExchanged newPair = referredPool.findByTestingUUID(pair.testingUUID);
				if (newPair != null) {
					testing = DatasetUtil.getMostInnerDataset2(newPair.testing);
					testingUUID = testing != null ? newPair.testingUUID : null;
				}
			}
			
			Dataset whole = null;
			UUID wholeUUID = null;
			if (pair.whole != null) {
				whole = DatasetUtil.getMostInnerDataset2(pair.whole);
				wholeUUID = whole != null ? pair.wholeUUID : null;
			}
			else if (pair.wholeUUID != null) {
				DatasetPairExchanged newPair = referredPool.findByWholeUUID(pair.wholeUUID);
				if (newPair != null) {
					whole = DatasetUtil.getMostInnerDataset2(newPair.whole);
					wholeUUID = whole != null ? newPair.wholeUUID : null;
				}
			}
			
			if (training != null || testing != null || whole != null) {
				DatasetPair newPair = new DatasetPair(training, testing, whole);
				newPair.trainingUUID = trainingUUID;
				newPair.testingUUID = testingUUID;
				newPair.wholeUUID = wholeUUID;
				pool.add(newPair);
			}
		}
		
		return pool;
	}

	
	/**
	 * Converting exchanged dataset pool to normal dataset in client.
	 * @return normal dataset in client.
	 */
	public DatasetPool toDatasetPoolClient() {
		DatasetPool pool = new DatasetPool();
		for (DatasetPairExchanged pair : dspList) {
			if (pair == null) continue;
			
			Dataset training = null;
			UUID trainingUUID = null;
			if (pair.training != null) {
				if (pair.training instanceof DatasetRemoteWrapper)
					training = (DatasetRemoteWrapper)pair.training;
				else
					training = Util.getPluginManager().wrap(pair.training, false);
				trainingUUID = training != null ? pair.trainingUUID : null;
			}
			
			Dataset testing = null;
			UUID testingUUID = null;
			if (pair.testing != null) {
				if (pair.testing instanceof DatasetRemoteWrapper)
					testing = (DatasetRemoteWrapper)pair.testing;
				else
					testing = Util.getPluginManager().wrap(pair.testing, false);
				testingUUID = testing != null ? pair.testingUUID : null;
			}
			
			Dataset whole = null;
			UUID wholeUUID = null;
			if (pair.whole != null) {
				if (pair.whole instanceof DatasetRemoteWrapper)
					whole = (DatasetRemoteWrapper)pair.whole;
				else
					whole = Util.getPluginManager().wrap(pair.whole, false);
				wholeUUID = whole != null ? pair.wholeUUID : null;
			}
			
			if (training != null || testing != null || whole != null) {
				DatasetPair newPair = new DatasetPair(training, testing, whole);
				newPair.trainingUUID = trainingUUID;
				newPair.testingUUID = testingUUID;
				newPair.wholeUUID = wholeUUID;
				pool.add(newPair);
			}
		}
		
		return pool;
	}

	
    /**
     * Adding client pool to this pool.
     * @param clientPool client pool.
     */
    public void syncWithClientPool(DatasetPoolExchanged clientPool) {
    	if (clientPool == null) return;
    	
		List<DatasetPairExchanged> removedList = Util.newList();
		for (DatasetPairExchanged pair : this.dspList) {
			if (!clientPool.containsPairUUID(pair)) removedList.add(pair);
		}
		for (DatasetPairExchanged pair : removedList) {
			this.dspList.remove(pair);
			pair.unexport(true);
		}

		for (DatasetPairExchanged pair : clientPool.dspList) {
			if (pair.training == null || pair.testing == null) continue;
			if (pair.wholeUUID != null && pair.whole == null) continue;
			
			if (!containsPairUUID(pair)) this.dspList.add(pair);
		}
		
    }
    
    
    /**
     * Checking whether containing the specified pair.
     * @param pair specified pair.
     * @return whether containing the specified pair.
     */
    private boolean containsPairUUID(DatasetPairExchanged pair) {
    	return pair != null && containsTrainingUUID(pair.trainingUUID) && containsTestingUUID(pair.testingUUID);
    }
    
    
	/**
	 * Checking whether containing training UUID.
	 * @param trainingUUID training UUID;
	 * @return whether containing training UUID.
	 */
	private boolean containsTrainingUUID(UUID trainingUUID) {
		if (trainingUUID == null) return false;
		for (DatasetPairExchanged pair : dspList) {
			if (pair != null && pair.trainingUUID != null && pair.trainingUUID.equals(trainingUUID))
				return true; 
		}
		return false;
	}
	
	
	/**
	 * Checking whether containing testing UUID.
	 * @param testingUUID testing UUID;
	 * @return whether containing testing UUID.
	 */
	private boolean containsTestingUUID(UUID testingUUID) {
		if (testingUUID == null) return false;
		for (DatasetPairExchanged pair : dspList) {
			if (pair != null && pair.testingUUID != null && pair.testingUUID.equals(testingUUID))
				return true; 
		}
		return false;
	}

	
}
