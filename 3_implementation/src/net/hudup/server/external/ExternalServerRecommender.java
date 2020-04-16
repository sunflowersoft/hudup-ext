/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.external;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.CompositeRecommenderAbstract;
import net.hudup.core.alg.RecommendFilter;
import net.hudup.core.alg.RecommendFilterParam;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.cf.gfall.GreenFallCF;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.BaseClass;

/**
 * This class represents external server recommendation algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@BaseClass //This is not base class. The base class annotation is used for update this algorithm because it will be registered in plugin.
public class ExternalServerRecommender extends CompositeRecommenderAbstract {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExternalServerRecommender() {
		super();
		// TODO Auto-generated constructor stub
		
		Recommender recommender = new GreenFallCF();
		setInnerRecommenders(new AlgList(recommender));
	}

	
	@Override
	public void setup(Dataset dataset, Object...params) throws RemoteException {
		// TODO Auto-generated method stub
		super.setup(dataset, params);
		
		initFilters();
	}


	@Override
	public void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		filterList.clear();
	}


	/**
	 * Initializing filters
	 */
	protected void initFilters() {
		filterList.clear();
		
		RecommendFilter filter = new RecommendFilter() {
			
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;
			
			
			private double itemType = Constants.UNUSED;
			
			
			@Override
			public void prepare(RecommendParam param) {
				// TODO Auto-generated method stub
				if (param.extra == null || !(param.extra instanceof Number)) {
					itemType = ((Number) param.extra).doubleValue();
				}
				else {
					itemType = Constants.UNUSED;
				}
			}
			
			
			@Override
			public boolean filter(Dataset dataset, RecommendFilterParam param) {
				// TODO Auto-generated method stub
				if (!Util.isUsed(itemType) || itemType < 0)
					return true;
				
				Profile itemProfile = dataset.getItemProfile(param.itemId);
				if (itemProfile == null)
					return true;
				
				int itemType = itemProfile.getValueAsInt(DataConfig.ITEM_TYPE_FIELD);
				if (itemType == -1)
					return true;
				else
					return itemType == (int) this.itemType;
			}
		};
		
		filterList.add(filter);
	}

	
	@Override
	public RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		return ((Recommender)getInnerRecommenders().get(0)).estimate(param, queryIds);
	}

	
	@Override
	public RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub
		return ((Recommender)getInnerRecommenders().get(0)).recommend(param, maxRecommend);
	}

	
	@Override
	public Dataset getDataset() throws RemoteException {
		// TODO Auto-generated method stub
		AlgList innerRecommenders = getInnerRecommenders();
		if (innerRecommenders.size() > 0)
			return ((Recommender)getInnerRecommenders().get(0)).getDataset();
		else
			return null;
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "external_recommender";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Recommendation algorithm by calling other external algorithm";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ExternalServerRecommender();
	}

	
}
