package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.RatingVector;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * This abstract class implements basically recommendation algorithms.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public abstract class RecommenderAbstract extends AbstractAlg implements Recommender {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Exported flag.
	 */
	protected Boolean exported = false;

	
	/**
	 * The filter list contains of filters. Filter specifies tasks which be performed before any actual recommendation tasks.
	 * Concretely, two methods {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)} require filtering tasks specified by filters of this list.
	 * Suppose every item has types 1, 2, 3, an example of filtering task is to select only type-1 items for recommendation task, which means that the list of recommended items produced by the method {@link #recommend(RecommendParam, int)} later contains only type-1 items.
	 */
	protected RecommendFilterList filterList = new RecommendFilterList();
	
	
	/**
	 * Default constructor.
	 */
	public RecommenderAbstract() {
		super();
	}

	
	@Override
	public synchronized void unsetup() throws RemoteException {
		filterList.clear();
		Dataset dataset = getDataset();
		if (dataset != null && dataset.isExclusive())
			dataset.clear();
	}


	@Override
	public RecommendFilterList getFilterList() throws RemoteException {
		return filterList;
	}
	
	
	/**
	 * Pre-processing the specified recommendation parameter.
	 * For example, if this recommendation parameter only has user identifier (user ID) but it has no ratings then, this method fills in ratings by reading such ratings from framework database.
	 * @param param recommendation parameter. Please see {@link RecommendParam} for more details of this parameter. Return null if cannot pre-processing.
	 * @return new recommendation parameter that is processed from the specified recommendation parameter.
	 */
	protected RecommendParam recommendPreprocess(RecommendParam param) {
		if (param == null || param.ratingVector == null)
			return null;
		
		// Pay attention following lines
		Dataset dataset = getDataset(); //This is training dataset.
		int userId = param.ratingVector.id(); //If the user id is negative (< 0), param.ratingVector is not stored in database. 
		RatingVector vRating = dataset.getUserRating(userId);
		if (param.ratingVector.size() == 0) {
			if (vRating == null || vRating.size() == 0)
				return null;
			else
				param.ratingVector = vRating;
		}
		else if (vRating != null) {//Fixing date: 2019.07.13 by Loc Nguyen
			Set<Integer> itemIds = vRating.fieldIds(true);
			if (itemIds.size() > 0)
				param.ratingVector = (RatingVector)param.ratingVector.clone();
			for (int itemId : itemIds) {
				if (!param.ratingVector.contains(itemId))
					param.ratingVector.put(itemId, vRating.get(itemId));
			}
		}
		
		if (param.profile == null) {
			Profile profile = dataset.getUserProfile(param.ratingVector.id());
			param.profile = profile;
		}
		
		return param;
	}
	
	
//	/**
//	 * Getting minimum rating.
//	 * @return minimum rating.
//	 */
//	public double getMinRating() {
//		double minRatisng = getDataset().getConfig().getMinRating();
//		if (!Util.isUsed(minRating))
//			minRating = getConfig().getAsReal(DataConfig.MIN_RATING_FIELD);
//		return minRating; 
//	}
//
//	
//	/**
//	 * Getting maximum rating.
//	 * @return maximum rating.
//	 */
//	public double getMaxRating() {
//		double maxRating = getDataset().getConfig().getMaxRating();
//		if (!Util.isUsed(maxRating))
//			maxRating = getConfig().getAsReal(DataConfig.MAX_RATING_FIELD);
//		return maxRating; 
//	}
	

	@Override
	public synchronized Inspector getInspector() {
		// TODO Auto-generated method stub
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {e.printStackTrace();}
		
		return new DescriptionDlg(UIUtil.getFrameForComponent(null), "Inspector", desc);
	}

	
	@Override
	public String queryName() throws RemoteException {
		// TODO Auto-generated method stub
		return getName();
	}


	@Override
	public DataConfig queryConfig() throws RemoteException {
		// TODO Auto-generated method stub
		return getConfig();
	}


	@Override
	public synchronized void export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (exported) {
			if (!exported) {
				NetUtil.RegistryRemote.export(this, serverPort);
				exported = true;
			}
		}
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (exported) {
			if (exported) {
				NetUtil.RegistryRemote.unexport(this);
				exported = false;
			}
		}
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			unsetup();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		try {
			unexport();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}