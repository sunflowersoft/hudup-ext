package net.hudup.alg.cf.bnet;

import java.awt.Component;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import elvira.Bnet;
import elvira.CaseListMem;
import elvira.Evidence;
import elvira.NodeList;
import elvira.database.DataBaseCases;
import elvira.learning.BICLearning;
import elvira.learning.K2Learning;
import elvira.learning.Learning;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.ValueTriple;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.DatasetUtil2;

/**
 * This class implements collaborative filtering based on Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class BnetCF extends BnetAbstractCF {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public BnetCF() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "bayesnet";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Bayesian network collaborative filtering algorithm";
	}


	@Override
	public KBase createKB() throws RemoteException {
		// TODO Auto-generated method stub
		return BnetKB.create(this);
	}


	/**
	 * Choosing Bayesian network to make estimation (recommendation).
	 * @param itemIds specified collection of item identifiers (item IDs).
	 * @return Bayesian network to make estimation (recommendation).
	 */
	protected Bnet chooseBnet(Collection<Integer> itemIds) {
		BnetKB bKb = (BnetKB)kb;
		return bKb.getBnetList().get(0);
	}

	
	@Override
	protected List<ValueTriple> bnetEstimate(RecommendParam param, Set<Integer> queryIds, double referredRatingValue, RatingFilter ratingFilter) {
		// TODO Auto-generated method stub
		Set<Integer> itemIdSet = Util.newSet();
		itemIdSet.addAll(param.ratingVector.fieldIds());
		itemIdSet.addAll(queryIds);
		
		Bnet bnet = this.chooseBnet(itemIdSet);
		List<ValueTriple> result = Util.newList();
		
		if (bnet == null)
			return result;
		
		double minRating = config.getMinRating();
		Evidence ev = BnetUtil.createItemEvidence(
				bnet.getNodeList(), 
				param.ratingVector, 
				minRating);
		
        result = BnetUtil.inference(
        		bnet, 
        		ev, 
        		queryIds, 
        		minRating,
        		referredRatingValue,
        		ratingFilter);
        
        if (result.size() == 0)
            result = BnetUtil.inference(
            	bnet, 
            	new Evidence(), 
            	queryIds,
            	minRating,
        		referredRatingValue,
        		ratingFilter);
        
        return result;
	}


	@Override
	protected Set<Integer> getItemIds() {
		// TODO Auto-generated method stub
		return ((BnetKB)kb).itemIds;
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig superConfig = super.createDefaultConfig();
		superConfig.put(BnetKB.K2_MAX_PARENTS, new Integer(BnetKB.K2_MAX_PARENTS_DEFAULT));
		superConfig.put(BnetKB.LEARNING_METHOD_FIELD, BnetKB.K2);
		
		DataConfig config = new DataConfig() {

			/**
			 * Default version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				// TODO Auto-generated method stub
				if (key.equals(BnetKB.LEARNING_METHOD_FIELD)) {
					String method = getAsString(BnetKB.LEARNING_METHOD_FIELD);
					method = method == null ? BnetKB.K2 : method;
					return (Serializable) JOptionPane.showInputDialog(
							comp, 
							"Please choose one learning method", 
							"Choosing learning method", 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							BnetKB.LEARNING_METHODS_SUPPORTED, 
							method);
				}
				else
					return super.userEdit(comp, key, defaultValue);
			}
			
		};
		
		config.putAll(superConfig);
		return config;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new BnetCF();
	}

	
}



/**
 * This is knowledge base for collaborative filtering algorithm based on Bayesian network.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
abstract class BnetKB extends KBaseAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * File extension of Bayesian network.
	 */
	public final static String BNET_FILEEXT = "elv";

	
	/**
	 * BIC learning algorithm.
	 */
	public final static String BIC = "bic";
	
	
	/**
	 * K2 learning algorithm.
	 */
	public final static String K2 = "k2";

	
	/**
	 * Bayesian network structure learning method.
	 */
	public final static String LEARNING_METHOD_FIELD = "learning_method";

	
	/**
	 * Supported Bayesian network structure learning methods.
	 */
	public final static String[] LEARNING_METHODS_SUPPORTED = {
		BIC,
		K2,
	};
	
	
	/**
	 * Maximum number of parents of given node.
	 */
	public final static String K2_MAX_PARENTS = "k2_max_parent";
	
	
	/**
	 * Default maximum number of parents of given node.
	 */
	public final static int K2_MAX_PARENTS_DEFAULT = 10;
	
	
	/**
	 * Bayesian network list.
	 */
	protected List<Bnet>  bnetList = Util.newList();

	
	/**
	 * List of item identifiers.
	 */
	protected Set<Integer> itemIds = Util.newSet();
	
	
	/**
	 * Default constructor.
	 */
	protected BnetKB() {
		
	}
	
	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		
		UriAdapter adapter = new UriAdapter(config);
		bnetList = loadBnet(adapter, config.getStoreUri(), getName());
		adapter.close();
		
		itemIds.clear();
		for (Bnet bnet : bnetList) {
			List<Integer> ids = BnetUtil.itemIdListOf(bnet.getNodeList());
			itemIds.addAll(ids);
		}
	}

	
	@Override
	public void learn(Dataset dataset, Alg alg) {
		// TODO Auto-generated method stub
		super.learn(dataset, alg);
		
		learnBnet(dataset);
		
	}


	/**
	 * Learning Bayesian network from specified dataset.
	 * @param dataset specified dataset.
	 */
	protected void learnBnet(Dataset dataset) {
		bnetList.clear();
		itemIds.clear();

		DataBaseCases dbc = createDataBaseCases(dataset);
		if (dbc == null) return;
		
		Learning bnetLearner = null;
		String method = config.getAsString(LEARNING_METHOD_FIELD);
		if (method.equals(BIC)) {
			bnetLearner = new BICLearning(dbc);
			bnetLearner.learning();
		}
		else {
			int maxParents = (int) (calcUserAverageRatingCount(dataset) + 0.5);
			maxParents = Math.max(maxParents, config.getAsInt(K2_MAX_PARENTS));
			bnetLearner = new K2Learning(dbc, maxParents);
			bnetLearner.learning();
		}
		
//		DELearning de = new DELearning(dbc, bnetLearner.getOutput());
//	    de.learning();
	    Bnet bnet = bnetLearner.getOutput();
		bnetList.add(bnet);
		
		List<Integer> ids = BnetUtil.itemIdListOf(bnet.getNodeList());
		itemIds.addAll(ids);
	}
	
	
	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		
		super.export(storeConfig);
		
		UriAdapter adapter = new UriAdapter(storeConfig);
		saveBnet(adapter, bnetList, storeConfig.getStoreUri(), getName());
		adapter.close();
	}

	
	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return bnetList.size() == 0;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		bnetList.clear();
		itemIds.clear();
	}


	/**
	 * Getting list of Bayesian networks.
	 * @return list of Bayesian networks.
	 */
	public List<Bnet> getBnetList() {
		return bnetList;
	}
	
	
	/**
	 * Creating knowledge base from collaborative filtering algorithm based on Bayesian network.
	 * @param cf collaborative filtering algorithm based on Bayesian network.
	 * @return knowledge base from collaborative filtering algorithm based on Bayesian network.
	 */
	public static BnetKB create(final BnetCF cf) {
		return new BnetKB() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public String getName() {
				// TODO Auto-generated method stub
				return cf.getName();
			}
			
		};
		
	}
	

	/**
	 * Getting list of Bayesian networks in specified store.
	 * @param adapter URI adapter.
	 * @param store specified store (directory) where to store Bayesian networks.
	 * @param prefixName prefix of names of files.
	 * @return list of Bayesian networks.
	 */
	protected static List<Bnet> loadBnet(UriAdapter adapter, xURI store, String prefixName) {
		
		List<Bnet> bnetList = Util.newList();
		List<xURI> uriList = BnetAbstractCF.getUriList(store, BNET_FILEEXT, prefixName, false);
		
		
		for(xURI uri : uriList) {
			Bnet bnet = BnetUtil.loadBnet(adapter, uri);
			if (bnet != null)
				bnetList.add(bnet);
		}
		
		return bnetList;
	}


	/**
	 * Saving list of Bayesian networks to specified store.
	 * @param bnetList list of Bayesian networks.
	 * @param store specified store (directory) where to store Bayesian networks.
	 * @param prefixName prefix of names of files.
	 */
	protected static void saveBnet(UriAdapter adapter, List<Bnet> bnetList, xURI store, String prefixName) {
		adapter.create(store, true);

		for (int i = 0; i < bnetList.size(); i++) {
			Bnet bnet = bnetList.get(i);
			String filename = prefixName + TextParserUtil.CONNECT_SEP + i + "." + BNET_FILEEXT;
			xURI uri = store.concat(filename);
			
			BnetUtil.saveBnet(adapter, bnet, uri);
		}
		adapter.close();
	}

	
	/**
	 * Creating database cases from datatset.
	 * @param dataset specified dataset.
	 * @return database cases created from specified dataset.
	 */
	protected static DataBaseCases createDataBaseCases(Dataset dataset) {
		int numberRatingsPerItem = dataset.getConfig().getNumberRatingsPerItem();
		int minRating = (int)dataset.getConfig().getMinRating();
		
		Fetcher<RatingVector> users = null;
		CaseListMem caseList = null;
		try {
			users = dataset.fetchUserRatings();
			Set<Integer> itemIds = Util.newSet();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user != null)
					itemIds.addAll(user.fieldIds(true));
			}
			users.reset();
			
			List<Integer> itemIdList = Util.newList();
			itemIdList.addAll(itemIds);
			itemIds.clear();
			NodeList nodeList = BnetUtil.createItemNodeList(
					itemIdList, 
					numberRatingsPerItem, 
					minRating);
			Vector<int[]> cases = Util.newVector();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				
				List<Double> valueList = user.toValueList(itemIdList);
				int[] aCase = new int[valueList.size()];
				for (int i = 0; i < aCase.length; i++) {
					double value = valueList.get(i);
					if (Util.isUsed(value)) {
						int realValue = (int)(value + 0.5);
						aCase[i] = DatasetUtil2.zeroBasedRatingValueOf(realValue, minRating);
					}
					else
						aCase[i] = -1; //Missing value.
				}
				
				cases.add(aCase);
			}

			caseList = new CaseListMem(nodeList);
			caseList.setCases(cases);
			itemIdList.clear();
		}
		catch (Exception e) {
			e.printStackTrace();
			caseList = null;
		}
		finally {
			if (users != null) {
				try {
					users.close();
				} catch (Throwable e) {e.printStackTrace();}
			}
		}

		if (caseList != null)
			return new DataBaseCases("Rating matrix database", caseList);
		else
			return null;
	}


	/**
	 * Calculating user average rating count.
	 * @param dataset given datatset.
	 * @return user average rating count.
	 */
	protected static double calcUserAverageRatingCount(Dataset dataset) {
		if (dataset == null) return 0;
		
		Fetcher<RatingVector> users = null;
		double userAverageRatingCount = 0;
		try {
			users = dataset.fetchUserRatings();
			int rateCount = 0;
			int n = 0;
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				int count = user.count(true);
				if (count == 0) continue;
				
				n++;
				
				rateCount += count;
			}
			
			if (n != 0)
				userAverageRatingCount = (double)rateCount / n;
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			userAverageRatingCount = 0;
		}
		finally {
			try {
				if (users != null)
					users.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return userAverageRatingCount;
	}
	
	
}


