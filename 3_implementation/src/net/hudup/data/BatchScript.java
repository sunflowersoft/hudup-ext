/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data;

import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.NullPointer;
import net.hudup.core.data.PropList;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;

/**
 * This utility class helps users to retrieve a dataset pool and a list of algorithm names from text batch script for evaluating many datasets and many algorithms later.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BatchScript implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Dataset pool.
	 */
	protected DatasetPool pool = new DatasetPool();
	
	
	/**
	 * List of algorithm names.
	 */
	protected List<String> algNameList = Util.newList();
	
	
	/**
	 * Reference to main unit, which is the key of main unit in configuration. It can be null.
	 */
	protected String mainUnit = null;
	
	
	/**
	 * Constructor with default pool, list of algorithm names, and main unit.
	 * @param pool specified dataset pool.
	 * @param algNameList specified list of algorithm names.
	 * @param mainUnit main unit  which is the key of main unit in configuration. It can be null.
	 */
	private BatchScript(DatasetPool pool, List<String> algNameList, String mainUnit) {
		this.pool = pool;
		this.algNameList = algNameList;
		this.mainUnit = mainUnit;
	}
	
	
	/**
	 * Saving this batch script.
	 * @param writer writer to save.
	 * @return whether save successfully.
	 */
	public boolean save(Writer writer) {
		PropList propList = new PropList();
		
		propList.put("algorithms", TextParserUtil.toText(algNameList, ","));
		
		
		List<String> trainings = Util.newList();
		List<String> testings = Util.newList();
		List<String> wholes = Util.newList();
		
		for (int i = 0; i < pool.size(); i++) {
			DatasetPair pair = pool.get(i);
			Dataset trainingSet = pair.getTraining();
			Dataset testingSet = pair.getTesting();
			
			if (trainingSet == null)
				continue;
			if (testingSet == null)
				continue;
			
			trainings.add(
					trainingSet.getConfig().getUriId().toString() + TextParserUtil.EXTRA_SEP +
					trainingSet.getConfig().getParser().getName()  + 
					(trainingSet.getConfig().getDataDriverName() != null ? TextParserUtil.EXTRA_SEP + trainingSet.getConfig().getDataDriverName() : "")
				);
			if (testingSet instanceof NullPointer)
				testings.add(NullPointer.NULL_POINTER);
			else
				testings.add(
						testingSet.getConfig().getUriId().toString() + TextParserUtil.EXTRA_SEP +
						testingSet.getConfig().getParser().getName() +
						(testingSet.getConfig().getDataDriverName() != null ? TextParserUtil.EXTRA_SEP + testingSet.getConfig().getDataDriverName() : "")
					);
			
			Dataset wholeSet = pair.getWhole();
			if (wholeSet != null)
				wholes.add(
					wholeSet.getConfig().getUriId().toString() + TextParserUtil.EXTRA_SEP +
					wholeSet.getConfig().getParser().getName() +
					(wholeSet.getConfig().getDataDriverName() != null ? TextParserUtil.EXTRA_SEP + wholeSet.getConfig().getDataDriverName() : "")
				);
		}
		
		if (trainings.size() == 0 || trainings.size() != testings.size())
			return false;
		if (wholes.size() != 0 && wholes.size() != trainings.size())
			return false;
		
		propList.put("trainingsets", TextParserUtil.toText(trainings, ","));
		propList.put("testingsets", TextParserUtil.toText(testings, ","));
		
		if (wholes.size() > 0)
			propList.put("wholesets", TextParserUtil.toText(wholes, ","));
		
		return propList.saveProperties(writer);
		
	}
	
	
	/**
	 * Parsing batch script to retrieve dataset pool and algorithm names. 
	 * @param reader reader to read.
	 * @param mainUnit main unit  which is the key of main unit in configuration. It can be null.
	 * @return {@link BatchScript} contains dataset pool and algorithm names.
	 */
	public static BatchScript parse(Reader reader, String mainUnit) {
		PropList propList = new PropList();
		propList.loadProperties(reader);
		
		if (!propList.containsKey("algorithms") ||
			!propList.containsKey("trainingsets") ||
			!propList.containsKey("testingsets")) {
			
			return null;
		}
		
		String algNames = propList.getAsString("algorithms");
		List<String> algNameList = TextParserUtil.split(algNames, ",", null);
//		if (algNameList.size() == 0) return null;
		
		DatasetPool pool = readPropList(propList, mainUnit);
		if (pool == null)
			return null;
		
		return new BatchScript(pool, algNameList, mainUnit);
	}
	
	
	/**
	 * Getting dataset pool.
	 * @return {@link DatasetPool}.
	 */
	public DatasetPool getPool() {
		return pool;
	}
	

	/**
	 * Getting list of algorithm names.
	 * @return list of algorithm names.
	 */
	public List<String> getAlgNameList() {
		return algNameList;
	}
	
	
	/**
	 * Getting list of algorithm names without duplication.
	 * @return list of algorithm names without duplication.
	 */
	public List<String> getAlgNameListNoDuplicate() {
		List<String> newAlgNameList = Util.newList();
		for (String algName : algNameList) {
			if (!newAlgNameList.contains(algName))
				newAlgNameList.add(algName);
		}
		
		return newAlgNameList;
	}

	
	/**
	 * Creating a batch script by assigning specified dataset pool and list of algorithm names.
	 * @param pool specified dataset pool.
	 * @param algNameList specified list of algorithm names.
	 * @param mainUnit main unit  which is the key of main unit in configuration. It can be null.
	 * @return {@link BatchScript} as new batch script.
	 */
	public static BatchScript assign(DatasetPool pool, List<String> algNameList, String mainUnit) {
		return new BatchScript(pool, algNameList, mainUnit);
	}
	

	/**
	 * Creating dataset pool from properties list.
	 * @param propList specified properties list.
	 * @param mainUnit main unit  which is the key of main unit in configuration. It can be null.
	 * @return {@link DatasetPool} from properties list.
	 */
    private static DatasetPool readPropList(PropList propList, String mainUnit) {
		
		if (!propList.containsKey("trainingsets") ||
			!propList.containsKey("testingsets")) {
			
			return null;
		}
		
		String trainings = propList.getAsString("trainingsets");
		List<String> trainingList = TextParserUtil.split(trainings, ",", null);
		if (trainingList.size() == 0)
			return null;
		
		String testings = propList.getAsString("testingsets");
		List<String> testingList = TextParserUtil.split(testings, ",", null);
		if (testingList.size() == 0 || testingList.size() != trainingList.size())
			return null;
		
		List<String> wholeList = Util.newList();
		if (propList.containsKey("wholesets")) {
			String wholes = propList.getAsString("wholesets");
			wholeList = TextParserUtil.split(wholes, ",", null);
			if (wholeList.size() == 0 || wholeList.size() != testingList.size())
				return null;
		}

		
		DatasetPool pool = new DatasetPool();
		
    	for (int i = 0; i < trainingList.size(); i++) {
    		
    		String training = trainingList.get(i);
    		String testing = testingList.get(i);
    		
    		Dataset trainingSet = null;
    		Dataset testingSet = null;
    		Dataset wholeSet = null;
    		
    		try {
    			List<String> trainingParts = TextParserUtil.split(training, TextParserUtil.EXTRA_SEP, null);
    			if (trainingParts.size() < 2)
    				continue;
    			
    			xURI trainingUri = xURI.create(trainingParts.get(0)); 
    			UriAdapter trainingAdapter = new UriAdapter(trainingUri);
    			DataConfig trainingCfg = trainingAdapter.makeFlatDataConfig(trainingUri, mainUnit);
    			trainingAdapter.close();
    			trainingCfg.setParser(trainingParts.get(1));
    			if (trainingParts.size() > 2)
    				trainingCfg.setDataDriverName(trainingParts.get(2));
	    		trainingSet = DatasetUtil.loadDataset(trainingCfg);
	    		if (trainingSet == null)
	    			continue;
	    		
    			List<String> testingParts = TextParserUtil.split(testing, TextParserUtil.EXTRA_SEP, null);
    			if (testingParts.size() == 0) 
    				continue;
    			else if (testingParts.size() == 1) {
    				if (testingParts.get(0).toLowerCase().equals(NullPointer.NULL_POINTER.toLowerCase()))
    					testingSet = new NullPointer();
    				else
    					continue;
    			}
    			else {
	    			xURI testingUri = xURI.create(testingParts.get(0)); 
	    			UriAdapter testingAdapter = new UriAdapter(testingUri);
	    			DataConfig testingCfg = testingAdapter.makeFlatDataConfig(testingUri, mainUnit);
	    			testingAdapter.close();
	    			testingCfg.setParser(testingParts.get(1));
	    			if (testingParts.size() > 2)
	    				testingCfg.setDataDriverName(testingParts.get(2));
		    		testingSet = DatasetUtil.loadDataset(testingCfg);
		    		if (testingSet == null)
		    			continue;
    			}
    			
	    		if (wholeList.size() > 0) {
	    			String whole = wholeList.get(i);
	    			
	    			List<String> wholeParts = TextParserUtil.split(whole, TextParserUtil.EXTRA_SEP, null);
	    			if (wholeParts.size() < 2)
	    				continue;
	        		
	    			xURI wholeUri = xURI.create(wholeParts.get(0)); 
	    			UriAdapter wholeAdapter = new UriAdapter(wholeUri);
	    			DataConfig wholeCfg = wholeAdapter.makeFlatDataConfig(wholeUri, mainUnit);
	    			wholeAdapter.close();
	    			DatasetPair pair = pool.findWholeSet(wholeCfg.getUriId());
	    			
	    			if (pair != null)
	    				wholeSet = pair.getWhole();
	    			else {
	        			wholeCfg.setParser(wholeParts.get(1));
	        			if (wholeParts.size() > 2)
	        				wholeCfg.setDataDriverName(wholeParts.get(2));
	    				wholeSet = DatasetUtil.loadDataset(wholeCfg);
	    			}
	    			
	    			
	    		} // if (wholeList.size() > 0)
	    		
	    		DatasetPair pair = new DatasetPair(
						trainingSet, 
				
						testingSet, 
				
						wholeSet == null ? null : wholeSet); 
		
	    		pool.add(pair);
	    		
	    		
    		}
    		catch (Throwable e) {
    			
    			LogUtil.trace(e);
    		}
    		
    	}
    	
    	if (pool.size() == 0)
    		return null;
    	
    	return pool;
    }
	
    
}
