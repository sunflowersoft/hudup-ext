/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.alg.cf.bnet;

import java.awt.Frame;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.WindowConstants;

import elvira.Bnet;
import elvira.CaseListMem;
import elvira.NodeList;
import elvira.database.DataBaseCases;
import elvira.gui.ElviraDesktopPane;
import elvira.gui.NetworkFrame;
import elvira.learning.BICLearning;
import elvira.learning.K2Learning;
import elvira.learning.LPLearning;
import elvira.learning.Learning;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.data.DatasetUtil2;


/**
 * This is knowledge base for collaborative filtering algorithm based on Bayesian network.
 * The algorithm is now stable but it runs too slowly because of Bayesian network inference mechanism with many nodes.
 * So it is put into update list. Whether to use it depends on users.
 * The attribute <code>reduced_ratio</code> ranges from 0 to 1.
 * The larger the attribute <code>reduced_ratio</code> is, the faster the inference mechanism is but the higher the accuracy is.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class BnetKB extends KBaseAbstract {

	
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
	 * Field of maximum number of parents of given node.
	 */
	public final static String K2_MAX_PARENTS = "k2_max_parents";
	
	
	/**
	 * Default maximum number of parents of given node.
	 */
	public final static int K2_MAX_PARENTS_DEFAULT = 10;
	
	
	/**
	 * Field of reduced ratio.
	 */
	public final static String REDUCED_RATIO_FIELD = "reduced_ratio";
	
	
	/**
	 * Default reduced ratio.
	 */
	public final static double REDUCED_RATIO_DEFAULT = 0.5;
	
	
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

		double reducedRatio = config.getAsReal(REDUCED_RATIO_FIELD);
		reducedRatio = Math.max(reducedRatio, 0);
		reducedRatio = Math.min(reducedRatio, 1);
		DataBaseCases dbc = createDataBaseCases(dataset, reducedRatio);
		if (dbc == null) return;
		
		Learning bnetLearner = null;
		String method = config.getAsString(LEARNING_METHOD_FIELD);
		if (method.equals(BIC)) {
			bnetLearner = new BICLearning(dbc); //BIC learning not working. Fixing later.
			bnetLearner.learning();
		}
		else {
			int maxParents = (int) (calcUserAverageRatingCount(dataset) + 0.5);
			maxParents = Math.max(maxParents, config.getAsInt(K2_MAX_PARENTS));
			bnetLearner = new K2Learning(dbc, maxParents);
			bnetLearner.learning();
		}
		
		bnetLearner = new LPLearning(dbc, bnetLearner.getOutput());
		bnetLearner.learning();
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
	
	
	@NextUpdate
	@Override
	public Inspector getInspector() {
		// TODO Auto-generated method stub
		if (bnetList.size() == 0)
			return super.getInspector();
		else
			return super.getInspector();
	}


	/**
	 * Inspector for Bayesian network knowledge base.
	 * @author Loc Nguyen
	 * @version 12
	 */
	@NextUpdate
	public class BnetInspector extends JDialog implements Inspector {

		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor with specified Bayesian network.
		 * @param bnet specified Bayesian network.
		 */
		public BnetInspector(Bnet bnet) {
			// TODO Auto-generated constructor stub
			super((Frame)null, "Inspector for Bayesian network knowledge base", true);
			
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
			
			ElviraDesktopPane desktopPane = new ElviraDesktopPane();
			setContentPane(desktopPane);
			
			NetworkFrame networkFrame = new NetworkFrame();
			networkFrame.setVisible(true);
			desktopPane.add(networkFrame);
			networkFrame.getEditorPanel().setBayesNet(bnet);
		}

		@Override
		public void inspect() {
			// TODO Auto-generated method stub
			setVisible(true);
		}

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
		List<xURI> uriList = BnetCFAbstract.getUriList(store, BNET_FILEEXT, prefixName, false);
		
		
		for(xURI uri : uriList) {
			Bnet bnet = BnetUtil.loadBnet(adapter, uri);
			if (bnet != null)
				bnetList.add(bnet);
		}
		
		return bnetList;
	}


	/**
	 * Saving list of Bayesian networks to specified store.
	 * @param adapter URI adapter.
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
	 * @param reducedRatio reduced ratio.
	 * @return database cases created from specified dataset.
	 */
	protected static DataBaseCases createDataBaseCases(Dataset dataset, double reducedRatio) {
		int numberRatingsPerItem = dataset.getConfig().getNumberRatingsPerItem();
		int minRating = (int)dataset.getConfig().getMinRating();
		
		try {
			Fetcher<RatingVector> items = dataset.fetchItemRatings();
			Set<Integer> itemIds = Util.newSet();
			int itemReferredRatingCount = (int) (calcItemMaxRatingCount(dataset) * reducedRatio);
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null) continue;
				
				double count = item.count(true);
				if (count > 0 && count > itemReferredRatingCount)
					itemIds.add(item.id());
			}
			items.close();
			if (itemIds.size() == 0) return null;
			
			List<Integer> itemIdList = Util.newList();
			itemIdList.addAll(itemIds);
			itemIds.clear();
			NodeList nodeList = BnetUtil.createItemNodeList(
					itemIdList, 
					numberRatingsPerItem, 
					minRating);
			Vector<int[]> cases = Util.newVector();
			Fetcher<RatingVector> users = dataset.fetchUserRatings();
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				
				List<Double> valueList = user.toValueList(itemIdList);
				int[] aCase = new int[valueList.size()];
				for (int i = 0; i < aCase.length; i++) {
					double value = valueList.get(i);
					if (Util.isUsed(value))
						aCase[i] = DatasetUtil2.zeroBasedRatingValueOf(value, minRating);
					else
						aCase[i] = -1; //Missing value.
				}
				
				cases.add(aCase);
			}
			users.close();
			if (cases.size() == 0) return null;

			CaseListMem caseList = new CaseListMem(nodeList);
			caseList.setCases(cases);
			return new DataBaseCases("Rating matrix database", caseList);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}


	/**
	 * Calculating user average rating count.
	 * @param dataset given datatset.
	 * @return user average rating count.
	 */
	private static double calcUserAverageRatingCount(Dataset dataset) {
		if (dataset == null) return 0;
		Fetcher<RatingVector> users = dataset.fetchUserRatings();
		return calcAverageRatingCount(users, true);
	}
	
	
	/**
	 * Calculating item maximum rating count.
	 * @param dataset given datatset.
	 * @return item maximum rating count.
	 */
	private static int calcItemMaxRatingCount(Dataset dataset) {
		if (dataset == null) return 0;
		Fetcher<RatingVector> items = dataset.fetchItemRatings();
		return calcMaxRatingCount(items, true);
	}
	
	
	/**
	 * Calculating average rating count of fetcher.
	 * @param fetcher specified fetcher.
	 * @param autoCloseFetcher if true, fetcher is closed automatically.
	 * @return average rating count of fetcher.
	 */
	private static double calcAverageRatingCount(Fetcher<RatingVector> fetcher, boolean autoCloseFetcher) {
		if (fetcher == null) return 0;
		
		double averageRatingCount = 0;
		try {
			int rateCount = 0;
			int n = 0;
			while (fetcher.next()) {
				RatingVector vRating = fetcher.pick();
				if (vRating == null) continue;
				int count = vRating.count(true);
				if (count == 0) continue;
				
				n++;
				
				rateCount += count;
			}
			
			if (n != 0)
				averageRatingCount = (double)rateCount / n;
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			averageRatingCount = 0;
		}
		finally {
			try {
				if (autoCloseFetcher) fetcher.close();
			} 
			catch (Throwable e) {e.printStackTrace();}
		}
		
		return averageRatingCount;
	}


	/**
	 * Calculating maximum rating count of fetcher.
	 * @param fetcher specified fetcher.
	 * @param autoCloseFetcher if true, fetcher is closed automatically.
	 * @return maximum rating count of fetcher.
	 */
	private static int calcMaxRatingCount(Fetcher<RatingVector> fetcher, boolean autoCloseFetcher) {
		if (fetcher == null) return 0;
		
		int maxRatingCount = Integer.MIN_VALUE;
		try {
			while (fetcher.next()) {
				RatingVector vRating = fetcher.pick();
				if (vRating == null) continue;
				int count = vRating.count(true);
				if (count == 0) continue;
				
				maxRatingCount = Math.max(count, maxRatingCount);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			maxRatingCount = 0;
		}
		finally {
			try {
				if (autoCloseFetcher) fetcher.close();
			} 
			catch (Throwable e) {e.printStackTrace();}
		}
		
		return maxRatingCount;
	}


}


