/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data;

import java.util.List;
import java.util.Random;

import javax.swing.event.EventListenerList;

import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Provider;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;

/**
 * Utility class for sampling dataset.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetSampler implements AutoCloseable {

	
	/**
	 * Suffix of training dataset unit.
	 */
	public final static String TRAINING_SUFFIX = ".base";
	
	
	/**
	 * Suffix of testing dataset unit.
	 */
	public final static String TESTING_SUFFIX = ".test";
	
	
	/**
	 * Suffix of missing dataset unit.
	 */
	public final static String MISSING_SUFFIX = ".miss";
	
	
	/**
	 * Source provider.
	 */
	protected Provider provider = null;
	
	
	/**
	 * Holding a list of listeners monitoring sampling process. 
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();
	
	
    /**
     * Constructor with specified configuration.
     * @param config specified source configuration.
     */
	public DatasetSampler(DataConfig config) {
		// TODO Auto-generated constructor stub
		this.provider = new ProviderImpl(config);
	}

	
	/**
	 * Splitting dataset into training dataset and testing dataset.
	 * @param srcUnit source unit.
	 * @param testRatio ratio of splitting. The folder number is inverse of the test ratio.
	 * @return list of configurations of training datasets and testing datasets.
	 * @throws Exception if any error raises.
	 */
	public List<DataConfig[]> split(String srcUnit, double testRatio) throws Exception {
		List<DataConfig[]> cfgList = Util.newList();
		if (testRatio <= 0 || testRatio > 1)
			return cfgList;
		
		int numFold = (int)(1.0 / testRatio);
		List<List<Integer>> foldList = Util.newList();
		for (int i = 0; i < numFold; i++) {
			List<Integer> list = Util.newList();
			foldList.add(list);
		}
		
		// Retrieving source content
		AttributeList attributes = provider.getProfileAttributes(srcUnit);
		Fetcher<Profile> fetcher = provider.getProfiles(srcUnit, null);
		List<Profile> content = Util.newList();
		while (fetcher.next()) {
			Profile profile = fetcher.pick();
			if (profile != null)
				content.add(profile);
		}
		fetcher.close();
		
		List<Pair> lineNumber = Util.newList();
		for (int i = 0; i < content.size(); i++)
			lineNumber.add(new Pair(i, 0));
		
		Random rnd = new Random();
		while (true) {
			for (int i = 0; i < numFold; i++) {
				int n = lineNumber.size();
				if (n > 0) {
					int k = rnd.nextInt(n);
					Pair pair = lineNumber.get(k);
					lineNumber.remove(k);
					
					List<Integer> list = foldList.get(i); 
					list.add(pair.key());
				}
			}
			
			if (lineNumber.size() == 0)
				break;
		}
		
		String[] foldUnits = new String[numFold * 2];
		for (int i = 0; i < numFold * 2; i += 2) {
			String baseUnit = srcUnit + "-" + testRatio + "-" + (i/2+1) + TRAINING_SUFFIX;
			foldUnits[i] = baseUnit;
			if(doesExist(baseUnit))
				provider.deleteUnitData(baseUnit);
			else
				provider.createUnit(baseUnit, attributes);
			
			String testUnit = srcUnit + "-" + testRatio + "-" + (i/2+1) + TESTING_SUFFIX;
			foldUnits[i + 1] = testUnit;
			if(doesExist(testUnit))
				provider.deleteUnitData(testUnit);
			else
				provider.createUnit(testUnit, attributes);
			
			cfgList.add(new DataConfig[] {
					makeConfigOfUnit(baseUnit),
					makeConfigOfUnit(testUnit),
				});
		}

		for (int i = 0; i < content.size(); i++) {
			Profile line = content.get(i);
			
			for (int j = 0; j < numFold; j++) {
				List<Integer> list = foldList.get(j);
				if (list.contains(i))
					provider.insertProfile(foldUnits[2*j + 1], line); // insert profile into test unit
				else
					provider.insertProfile(foldUnits[2*j], line); // insert profile into base unit
			}
			
			fireProgressEvent(new ProgressEvent(this, content.size(), i + 1, "Inserted: " + line));
		}
		
		return cfgList;
	}

	
	/**
	 * Splitting dataset into training dataset and testing dataset with specified folder number.
	 * @param srcUnit source unit.
	 * @param testRatio ratio of splitting.
	 * @param numFold folder number. If the folder number is zero, the method {@link #split(String, double)} is called instead.
	 * In other words, if the folder number is zero, the folder number is inverse of the test ratio.
	 * @return list of configurations of training datasets and testing datasets.
	 * @throws Exception if any error raises.
	 */
	public List<DataConfig[]> split(String srcUnit, double testRatio, int numFold) throws Exception {
		List<DataConfig[]> cfgList = Util.newList();
		if (testRatio <= 0 || testRatio > 1 || numFold < 0)
			return cfgList;
		if (numFold == 0)
			return split(srcUnit, testRatio);
		
		// Retrieving source content
		AttributeList attributes = provider.getProfileAttributes(srcUnit);
		Fetcher<Profile> fetcher = provider.getProfiles(srcUnit, null);
		List<Profile> content = Util.newList();
		while (fetcher.next()) {
			Profile profile = fetcher.pick();
			if (profile != null)
				content.add(profile);
		}
		fetcher.close();
		if (content.size() == 0) return cfgList;
		
		String[] foldUnits = new String[numFold * 2];
		for (int i = 0; i < numFold * 2; i += 2) {
			String baseUnit = srcUnit + "-" + testRatio + "-" + (i/2+1) + TRAINING_SUFFIX;
			foldUnits[i] = baseUnit;
			if(doesExist(baseUnit))
				provider.deleteUnitData(baseUnit);
			else
				provider.createUnit(baseUnit, attributes);
			
			String testUnit = srcUnit + "-" + testRatio + "-" + (i/2+1) + TESTING_SUFFIX;
			foldUnits[i + 1] = testUnit;
			if(doesExist(testUnit))
				provider.deleteUnitData(testUnit);
			else
				provider.createUnit(testUnit, attributes);
			
			cfgList.add(new DataConfig[] {
					makeConfigOfUnit(baseUnit),
					makeConfigOfUnit(testUnit),
				});
		}

		int eachTestSize = (int)(testRatio * content.size());
		int count = 0;
		for (int i = 0; i < numFold; i++) {
			List<Integer> lineNumber = Util.newList();
			for (int j = 0; j < content.size(); j++) {
				lineNumber.add(j);
			}
			
			Random rnd = new Random();
			for (int j = 0; j < eachTestSize; j++) {
				int k = rnd.nextInt(lineNumber.size());
				Profile line = content.get(lineNumber.get(k));
				lineNumber.remove(k);
				provider.insertProfile(foldUnits[2*i + 1], line); // insert profile into test unit
				
				count++;
				fireProgressEvent(new ProgressEvent(this, content.size()*numFold, count, "Inserted: " + line));
			}
			
			for (int j = 0; j < lineNumber.size(); j++) {
				Profile line = content.get(lineNumber.get(j));
				provider.insertProfile(foldUnits[2*i], line); // insert profile into base unit
				
				count++;
				fireProgressEvent(new ProgressEvent(this, content.size()*numFold, count, "Inserted: " + line));
			}
		}
		
		return cfgList;
	}

	
	/**
	 * Making sparse the unit (table) specified by the specified source configuration and source unit.
	 * The resulted sparse unit is stored in the storage of source unit.
	 * @param srcUnit source unit.
	 * @param sparseRatio ratio of making sparse.
	 * @param sparseIndices index list of columns to be made sparse.
	 * @return configuration of unit that is made sparse;
	 * @throws Exception if any error raises.
	 */
	public DataConfig makeSparse(String srcUnit, double sparseRatio, List<Integer> sparseIndices) throws Exception {
		DataConfig dstCfg = null;
		if (sparseRatio < 0 || sparseRatio > 1)
			return dstCfg;
		int n = sparseIndices.size(); 
		
		Fetcher<Profile> fetcher = provider.getProfiles(srcUnit, null);
		int m = fetcher.getMetadata().getSize();
		
		List<Integer> indices = Util.newList();
		int N = m*n;
		for (int i = 0; i < N; i++) {
			indices.add(i);
		}
		
		int K = Math.min((int)(sparseRatio * N), N);
		Random rnd = new Random();
		List<int[]> removedRowColumnList = Util.newList();
		for (int i = 0; i < K; i++) {
			int j = rnd.nextInt(indices.size());
			int index = indices.get(j);
			indices.remove(j);
			
			int row = index / n;
			int column = index % n;
			removedRowColumnList.add(new int[] {row, column});
		}
		
		String dstUnit = srcUnit + "-" + sparseRatio + MISSING_SUFFIX;
		AttributeList attributes = provider.getProfileAttributes(srcUnit);
		if(doesExist(dstUnit))
			provider.deleteUnitData(dstUnit);
		else
			provider.createUnit(dstUnit, attributes);
		dstCfg =  makeConfigOfUnit(dstUnit);
		int i = 0;
		while (fetcher.next()) {
			Profile profile = fetcher.pick();
			if (profile == null)
				continue;
			
			Profile sparseProfile = (Profile)profile.clone();
			for (int j = 0; j < attributes.size(); j++) {
				for (int k = 0; k < K; k++) {
					int[] rowColumn = removedRowColumnList.get(k); 
					int row = rowColumn[0];
					int column = sparseIndices.get(rowColumn[1]);
					
					if (i == row && j == column)
						sparseProfile.setMissing(j);
				}
			}
			provider.insertProfile(dstUnit, sparseProfile);
			
			i++;
			fireProgressEvent(new ProgressEvent(this, m, i, "Make sparse: " + sparseProfile));
		}
		fetcher.close();
		
		return dstCfg;
	}
	
	
	/**
	 * Checking whether the specified unit exists.
	 * @param unit the specified unit.
	 * @return whether the specified unit exists.
	 */
	public boolean doesExist(String unit) {
		UnitList unitList = provider.getUnitList();
		return unitList.contains(unit);
	}
	
	
	/**
	 * Getting provider.
	 * @return provider.
	 */
	public Provider getProvider() {
		return provider;
	}
	
	
	/**
	 * Create configuration for specified unit.
	 * @param unit specified unit;
	 * @return configuration for specified unit.
	 */
	protected DataConfig makeConfigOfUnit(String unit) {
		DataConfig config = (DataConfig) provider.getConfig().clone();
		if (config.getMainUnit() != null)
			config.put(config.getMainUnit(), unit);
		return config;
	}
	
	
	/**
	 * Add the specified listener to the end of listener list.
	 * @param listener specified listener.
	 */
	public void addProgressListener(ProgressListener listener) {
		synchronized (listenerList) {
			listenerList.add(ProgressListener.class, listener);
		}
    }

    
	/**
	 * Remove the specified progress listener from the listener list.
	 * @param listener specified progress listener.
	 */
    public void removeProgressListener(ProgressListener listener) {
		synchronized (listenerList) {
			listenerList.remove(ProgressListener.class, listener);
		}
    }
	
    
    /**
     * Return an array of progress listeners for this sampler.
     * @return an array of progress listeners for this sampler.
     */
    protected ProgressListener[] getProgressListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(ProgressListener.class);
		}
    }

    
    /**
     * Firing (issuing) an progress event from this sampler to all progress listeners. 
     * 
     * @param evt progress event from this sampler.
     */
    protected void fireProgressEvent(ProgressEvent evt) {
    	
		ProgressListener[] listeners = getProgressListeners();
		
		for (ProgressListener listener : listeners) {
			try {
				listener.receiveProgress(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
    }


	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			if (provider != null)
				provider.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		provider = null;
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
