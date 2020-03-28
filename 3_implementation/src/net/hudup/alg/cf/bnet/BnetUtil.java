/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.bnet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import elvira.Bnet;
import elvira.Configuration;
import elvira.Evidence;
import elvira.FiniteStates;
import elvira.Node;
import elvira.NodeList;
import elvira.inference.elimination.VariableElimination;
import elvira.potential.PotentialTable;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Pair;
import net.hudup.core.data.RatingTriple;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LineProcessor;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.DatasetUtil2;


/**
 * This is utility class for processing Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BnetUtil {

	
	/**
	 * Item name prefix.
	 */
	public final static String ITEM_PREFIX = DataConfig.ITEM_FIELD + TextParserUtil.CONNECT_SEP;
	
	
	/**
	 * Attribute name prefix.
	 */
	public final static String ATT_PREFIX = DataConfig.ATTRIBUTE_FIELD + TextParserUtil.CONNECT_SEP;
	
	
	/**
	 * Create item node name.
	 * @param itemId item identifier.
	 * @return item node name.
	 */
	public static String createItemNodeName(int itemId) {
		return ITEM_PREFIX + itemId;
	}

	
	/**
	 * Extracting item identifier from item node name.
	 * @param itemNodeName item node name.
	 * @return item identifier extracted from item node name.
	 */
	public static int extractItemId(String itemNodeName) {
		String sid = itemNodeName.substring(itemNodeName.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1);
		return Integer.parseInt(sid);
	}

	
	/**
	 * Checking whether is item from item node name.
	 * @param itemNodeName item node name.
	 * @return whether is item.
	 */
	public static boolean isItem(String itemNodeName) {
		return itemNodeName.startsWith(ITEM_PREFIX);
	}
	
	
	/**
	 * Create attribute node name.
	 * @param attId attribute identifier.
	 * @return attribute node name.
	 */
	public static String createAttNodeName(int attId) {
		return ATT_PREFIX + attId;
	}

	
	/**
	 * Extracting attribute identifier from attribute node name.
	 * @param attNodeName attribute node name.
	 * @return attribute node id.
	 */
	public static int extractAttId(String attNodeName) {
		String sid = attNodeName.substring(attNodeName.lastIndexOf(TextParserUtil.CONNECT_SEP) + 1);
		return Integer.parseInt(sid);
	}

	
	/**
	 * Checking whether is attribute from attribute node name.
	 * @param attNodeName attribute node name.
	 * @return whether is attribute.
	 */
	public static boolean isAtt(String attNodeName) {
		return attNodeName.startsWith(ATT_PREFIX);
	}

	
	/**
	 * Getting list of item id (s) from list of nodes.
	 * @param nodeList list of nodes.
	 * @return list of item id (s).
	 */
	public static List<Integer> itemIdListOf(NodeList nodeList) {
		
		List<Integer> itemIdList = Util.newList();
		
		for (int i = 0; i < nodeList.size(); i++) {
			Node node = nodeList.elementAt(i);
			String name = node.getName();
			
			int id = extractItemId(name);
			itemIdList.add(id);
		}
		
		return itemIdList;
	}

	
	/**
	 * Creating item node.
	 * @param itemId item identifier.
	 * @param numberRatingsPerItem number of ratings per item.
	 * @param minRating minimum rating value.
	 * @return {@link FiniteStates} as item node.
	 */
	public static FiniteStates createItemNode(
			int itemId, 
			int numberRatingsPerItem,
			int minRating) {
		int n = numberRatingsPerItem;
		String[] states = new String[n];
		int count = 0;
		for (int i = minRating; i < minRating + numberRatingsPerItem; i++) {
			states[count] = String.valueOf(DatasetUtil2.zeroBasedRatingValueOf(i, minRating));
			count ++;
		}
		
		return new FiniteStates(createItemNodeName(itemId), states);
	}

	
	/**
	 * Creating list of item nodes
	 * @param itemIdList list of item identifiers.
	 * @param numberRatingsPerItem number of ratings per item.
	 * @param minRating minimum rating value.
	 * @return {@link NodeList} as list of item nodes. 
	 */
	public static NodeList createItemNodeList(
			Collection<Integer> itemIdList,
			int numberRatingsPerItem,
			int minRating) {
	
		NodeList nodeList = new NodeList();
		
		for (int itemId : itemIdList) {
			Node node = createItemNode(
					itemId, 
					numberRatingsPerItem, 
					minRating);
			nodeList.insertNode(node);
		}
		
		return nodeList;
	}

	
	/**
	 * Creating item configuration.
	 * @param nodeList list of nodes.
	 * @param userRating user rating vector.
	 * @return item configuration.
	 */
	private static Configuration createItemConfiguration(
			NodeList nodeList, 
			RatingVector userRating, 
			double minRating) {
		Vector<FiniteStates> vars = new Vector<FiniteStates>();
		Vector<Integer> vals = new Vector<Integer>();
		
		List<RatingTriple> triples = RatingTriple.getUserRatings(userRating);
		for (RatingTriple triple : triples) {
			int itemId = triple.itemId();
			String nodeName = createItemNodeName(itemId);
			if (nodeList.getId(nodeName) == -1)
				continue;
			
			FiniteStates node = (FiniteStates)nodeList.getNode(nodeName);
			
			int value = DatasetUtil2.zeroBasedRatingValueOf(triple.getRating().value, minRating);
			String svalue = String.valueOf(value);
			
			String[] states = node.getStringStates();
			int found = -1;
			for (int i = 0; i < states.length; i++) {
				if (svalue.equals(states[i])) {
					found = i;
					break;
				}
			}
			
			if (found != -1) {
				vars.addElement(node);
				vals.add(value);
			}
		}
		
		return new Configuration(vars, vals);
	}

	
	/**
	 * Create item evidence.
	 * @param nodeList list of nodes.
	 * @param userRating user rating vector.
	 * @param minRating minimum rating value.
	 * @return item evidence.
	 */
	public static Evidence createItemEvidence(
			NodeList nodeList, 
			RatingVector userRating,
			double minRating) {
		Configuration conf = createItemConfiguration(nodeList, userRating, minRating);
		return new Evidence(conf);

//		Configuration conf = createItemConfiguration(nodeList, userRating, minRating);
//		Evidence evidence = new Evidence();
//		
//		Vector<?> vars = conf.getVariables();
//		Vector<?> vals = conf.getValues();
//		
//		for (int i = 0; i < vars.size(); i++) {
//			FiniteStates node = (FiniteStates)vars.get(i);
//			int value = (Integer)vals.get(i);
//			evidence.insert(node, value);
//		}
//		
//		return evidence;
	}


	/**
	 * Making inference on given Bayesian network and evidence.
	 * @param bnet given Bayesian network
	 * @param ev given evidence.
	 * @param queryItemIds list of queried item identifiers.
	 * @param minRating minimum rating.
	 * @param referredRatingValue referred rating value, often relevant value = (minimum value + maximum value)/2.
	 * @param ratingFilter rating filter.
	 * @return list of {@link ValueTriple} as reference result.
	 */
	public static List<ValueTriple> inference(
			Bnet bnet,
			Evidence ev,
			Set<Integer> queryItemIds,
			double minRating,
			double referredRatingValue,
			RatingFilter ratingFilter) {
		
		VariableElimination ve = new VariableElimination(bnet, ev);
        ve.obtainInterest();
        
        ve.propagate();
        bnet.setCompiledPotentialList(ve.getResults());
        //bnet.showResults(ve);
        
        return getInferenceResult(bnet, queryItemIds, minRating, referredRatingValue, ratingFilter);
	}
	
	
	/**
	 * Getting reference result.
	 * @param bnet given Bayesian network.
	 * @param queryItemIds list of queried item identifiers.
	 * @param minRating minimum rating.
	 * @param referredRatingValue referred rating value, often relevant value = (minimum value + maximum value)/2.
	 * @param ratingFilter rating filter.
	 * @return list of {@link ValueTriple} as reference result.
	 */
	private static List<ValueTriple> getInferenceResult(
			Bnet bnet, 
			Set<Integer> queryItemIds,
			double minRating,
			double referredRatingValue,
			RatingFilter ratingFilter) {
		
		List<ValueTriple> triples = Util.newList();

        Vector<?> results = bnet.getCompiledPotentialList();
        
        for (int i = 0 ; i < results.size() ; i++) {
        	PotentialTable pot = (PotentialTable)results.elementAt(i);
        	Vector<?> vars = pot.getVariables();
        	FiniteStates node = (FiniteStates)vars.elementAt(0);
            int itemId = BnetUtil.extractItemId(node.getName());
            
            if(!queryItemIds.contains(itemId))
            	continue;

    		double[] values = pot.getValues();
    		if (!DSUtil.assertNotNaN(values))
    			continue;
    		
        	int total = (int)FiniteStates.getSize(vars);
    		int maxIndex = -1;
    		double maxProp = -1;
        	for (int j = 0 ; j < total; j++) {
        		if (maxProp < values[j]) {
        			maxProp = values[j];
        			maxIndex = j;
        		}
        	}
        	
        	if (maxIndex == -1)
        		continue;
        	
    		String srating = node.getStates().elementAt(maxIndex).toString();
    		int value = Integer.parseInt(srating);
    		double rating = DatasetUtil2.realRatingValueOf(value, minRating);
        	ValueTriple triple = new ValueTriple(itemId, rating, maxProp);
        	
			if (!Util.isUsed(referredRatingValue) || ratingFilter == null)
	        	triples.add(triple);
			else if (ratingFilter.accept(rating, referredRatingValue))
	        	triples.add(triple);
        }
        
        return triples;
	}
	

	/**
	 * Making inference on given Bayesian network and evidence.
	 * @param bnet given Bayesian network.
	 * @param ev given evidence.
	 * @param queryItemList pair list of queried item identifiers.
	 * @param minRating minimum rating value.
	 * @return list of {@link ValueTriple} as reference result.
	 */
	public static List<ValueTriple> inference(
			Bnet bnet,
			Evidence ev,
			List<Pair> queryItemList,
			double minRating) {
		
		VariableElimination ve = new VariableElimination(bnet, ev);
        ve.obtainInterest();
        
        ve.propagate();
        bnet.setCompiledPotentialList(ve.getResults());
        //bnet.showResults(ve);
        
        return getInferenceResult(bnet, queryItemList, minRating);
	}

	
	/**
	 * Getting reference result.
	 * @param bnet given Bayesian network.
	 * @param queryItemList pair list of queried item identifiers.
	 * @param minRating minimum rating value.
	 * @return list of {@link ValueTriple} as reference result.
	 */
	private static List<ValueTriple> getInferenceResult(
			Bnet bnet, 
			List<Pair> queryItemList,
			double minRating) {
		
		List<ValueTriple> triples = Util.newList();

        Vector<?> results = bnet.getCompiledPotentialList();
        
        List<Integer> queryItemIdList = Pair.getKeyList(queryItemList);
        for (int i = 0 ; i < results.size() ; i++) {
        	PotentialTable pot = (PotentialTable)results.elementAt(i);
        	Vector<?> vars = pot.getVariables();
        	FiniteStates node = (FiniteStates)vars.elementAt(0);
            
        	int itemId = BnetUtil.extractItemId(node.getName());
        	int itemIndex = queryItemIdList.indexOf(itemId);
            if(itemIndex == -1)
            	continue;

    		double[] values = pot.getValues();
    		if (!DSUtil.assertNotNaN(values))
    			continue;
    		
    		double itemValue = queryItemList.get(itemIndex).value();
    		int index = DatasetUtil2.zeroBasedRatingValueOf(itemValue, minRating);
        	triples.add(new ValueTriple(itemId, itemValue, values[index]));
    				
        }
        
        return triples;
	}
	
	
	/**
	 * Loading Bayesian network.
	 * @param adapter URI adapter.
	 * @param bnet Bayesian network.
	 * @param uri store URI.
	 */
	public static void saveBnet(UriAdapter adapter, Bnet bnet, xURI uri) {
		try {
			Writer writer = adapter.getWriter(uri, false);
			bnet.save(new PrintWriter(writer));
			writer.close();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
	}
	
	
	
	/**
	 * Loading Bayesian network.
	 * @param adapter URI adapter.
	 * @param uri store URI.
	 * @return Bayesian network.
	 */
	public static Bnet loadBnet(UriAdapter adapter, xURI uri) {
		try {
			return new Bnet(uri.toURL());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		} 
		
		return null;
	}

	
	/**
	 * Creating mapping table for list of Bayesian networks.
	 * @param bnetList list of Bayesian networks.
	 * @return MT map of {@link BitSet} for list of Bayesian networks.
	 */
	public static Map<Integer, BitSet> createMT(List<Bnet> bnetList) {
		
		Set<Integer> totalItemIdSet = Util.newSet();
		
		for (Bnet bnet : bnetList) {
		    NodeList nodelList = bnet.getNodeList();
		    List<Integer> idList = BnetUtil.itemIdListOf(nodelList);
		    totalItemIdSet.addAll(idList);
		}
		
		Map<Integer, BitSet> MT = Util.newMap();
		for (int itemId : totalItemIdSet) {
			BitSet bs = new BitSet(bnetList.size());
			MT.put(itemId, bs);
		}
		
		for (int i = 0; i < bnetList.size(); i++) {
			
		    NodeList nodelList = bnetList.get(i).getNodeList();
		    
		    List<Integer> idList = BnetUtil.itemIdListOf(nodelList);
		    for (int id : idList) {
		    	BitSet bs = MT.get(id);
		    	bs.set(i);
		    }
		}
		
		return MT;
	}
	
	
	/**
	 * Loading mapping table.
	 * @param adapter URI adapter.
	 * @param store store URI.
	 * @param prefixName prefix name of mapping table file name.
	 * @return MT map of {@link BitSet}.
	 */
	public static Map<Integer, BitSet> loadMT(UriAdapter adapter, xURI store, String prefixName) {
		final Map<Integer, BitSet> MT = Util.newMap();
		
		prefixName = (prefixName == null ? "" : prefixName);
		xURI uri = store.concat(prefixName + TextParserUtil.CONNECT_SEP + "MT");
		
		BufferedReader reader = new BufferedReader(adapter.getReader(uri));
		DSUtil.lineProcess(reader, new LineProcessor() {
			
			@Override
			public void process(String line) {
				String[] comps = line.split("\\s");
				
				if (comps.length < 2)
					return;;
				
				int itemId = Integer.parseInt(comps[0]);
				BitSet bs = TextParserUtil.parseBitSet(comps[1]);
				MT.put(itemId, bs);
			}
		});
		
		try {
			reader.close();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		return MT;
	}
	
	
	/**
	 * Saving mapping table.
	 * @param adapter URI adapter.
	 * @param store store URI.
	 * @param MT mapping table.
	 * @param prefixName prefix name of mapping table file name.
	 */
	public static void saveMT(UriAdapter adapter, xURI store, Map<Integer, BitSet> MT, String prefixName) {
		adapter.create(store, true);
		
		prefixName = (prefixName == null ? "" : prefixName);
		xURI uri = store.concat(prefixName + TextParserUtil.CONNECT_SEP + "MT");

		if (adapter.exists(uri))
			adapter.delete(uri, null);
		
		Writer writer = null;
		try {
			writer = adapter.getWriter(uri, false);
			
			Set<Integer> itemIds = MT.keySet();
			for (int itemId : itemIds) {
				BitSet bs = MT.get(itemId);
				String line = "" + itemId + " " + TextParserUtil.toText(bs);
				
				writer.write(line + "\n");
			}
			
			writer.flush();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		finally {
			try {
				if (writer != null)
					writer.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
		
		
	} //saveMT
	
	
	
	/**
	 * Counting Bayesian networks containing at least an item identifier in specified list of item identifiers.
	 * @param MT mapping table
	 * @param bnetIdx Bayesian index.
	 * @param itemIds specified list of item identifiers.
	 * @return count for Bayesian networks containing at least an item identifier in specified list of item identifiers..
	 */
	public static int countForBnetIdx(Map<Integer, BitSet> MT, int bnetIdx, Collection<Integer> itemIds) {
		int count = 0;
		for (int itemId : itemIds) {
			if (!MT.containsKey(itemId))
				continue;
			
			BitSet bs = MT.get(itemId);
			if (bs.get(bnetIdx))
				count++;
		}
		
		return count;
	}
	
	
}
