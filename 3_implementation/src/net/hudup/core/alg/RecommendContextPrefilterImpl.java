package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.NextUpdate;


/**
 * This class is default implementation of context pre-filter recommendation algorithm represented by interface {@link RecommendContextPrefilter}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@BaseClass //This class is not really a base class but the notation "BaseClass" makes it not registered in plug-in storage.
@NextUpdate
//@Deprecated //This class should be annotated as deprecated class instead of being annotated as base class so that it is easy to improve it.
public class RecommendContextPrefilterImpl extends CompositeRecommenderAbstract implements RecommendContextPrefilter {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This constant specifies the default internal recommendation algorithm.
	 */
	public static final String INNER_RECOMMENDER = "net.hudup.alg.cf.gfall.GreenFallCF";
			

	/**
	 * The original dataset.
	 */
	protected Dataset dataset = null;

	
	/**
	 * The context-aware dataset resulted from filtering the original dataset {@link #dataset}, based on contexts.
	 */
	protected Dataset filteredDataset = null;
	
	
	/**
	 * Default constructor.
	 */
	public RecommendContextPrefilterImpl() {
		super();
		
		Recommender recommender = null;
		try {
			recommender = (Recommender) Class.forName(INNER_RECOMMENDER).newInstance();
		}
		catch (Throwable e) {
			logger.info("Inner recommender " + INNER_RECOMMENDER + " initiated " + this.getClass() + " not exist");
			recommender = null;
		}
		
		if (recommender != null)
			setInnerRecommenders(new AlgList(recommender));
	}
	
	
	@Override
	public synchronized void setup(Dataset dataset, Object...params) throws RemoteException {
		// TODO Auto-generated method stub
		unsetup();
		
		this.dataset = dataset;
		
		if (params.length >= 1) {
			if (params[0] != null && params[0] instanceof ContextList) {
				filteredDataset = dataset.selectByContexts((ContextList)params[0] );
				((Recommender)getInnerRecommenders().get(0)).setup(filteredDataset);
			}
			else
				throw new RemoteException("Invalid parameter");
		}
		
	}

	
	@Override
	public void setup(Dataset dataset, ContextList contextList) throws RemoteException {
		if (dataset == null)
			throw new RemoteException("Invalid parameter");
		
		if (contextList == null)
			setup(dataset);
		else
			setup(dataset, new Object[] { contextList });
	}
	
	
	@Override
	public synchronized void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		super.unsetup();
		
		if (filteredDataset != null) {
			filteredDataset.clear();
		}
		filteredDataset = null;
		
		dataset = null;
	}


	@Override
	public Dataset getDataset() {
		// TODO Auto-generated method stub
		return dataset == null ? filteredDataset : dataset;
	}

	
	/**
	 * Preparing (setting up) the internal recommendation algorithm with regard to context-aware filtered dataset {@link #filteredDataset}.
	 * 
	 * @param param specified recommendation parameter.
	 * @throws Exception if any error raises.
	 */
	private void prepareInnerRecommender(RecommendParam param) throws Exception {
		if (this.filteredDataset != null)
			return;
		
		Recommender recommender = (Recommender)getInnerRecommenders().get(0);
		
		// If there is no contexts list, we recommend as normal situation
		if (param.contextList != null && param.contextList.size() > 0) {
			Dataset filteredDataset = dataset.selectByContexts(param.contextList);
			filteredDataset.setExclusive(true);
			recommender.setup(filteredDataset);
		}
		else if (recommender.getDataset() == null) {
			Dataset dataset = (Dataset) getDataset().clone();
			dataset.setExclusive(true);
			
			recommender.setup(dataset);
		}
	}
	
	
	@Override
	public synchronized RatingVector estimate(RecommendParam param, Set<Integer> queryIds) throws RemoteException {
		// TODO Auto-generated method stub
		
		try {
			prepareInnerRecommender(param);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		adjustParam(param);
		return ((Recommender)getInnerRecommenders().get(0)).estimate(param, queryIds);
	}

	
	@Override
	public synchronized RatingVector recommend(RecommendParam param, int maxRecommend) throws RemoteException {
		// TODO Auto-generated method stub

		try {
			prepareInnerRecommender(param);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		adjustParam(param);
		return ((Recommender)getInnerRecommenders().get(0)).recommend(param, maxRecommend);
	}

	
	/**
	 * The recommendation parameter is very important to method {@link #estimate(RecommendParam, Set)} and {@link #recommend(RecommendParam, int)}.
	 * Therefore, this method adjusts the specified recommendation parameter with regard to contexts.
	 * Note, recommendation parameter contains the internal context list.
	 * 
	 * @param param specified recommendation parameter. This is also input and output parameter.
	 */
	private void adjustParam(RecommendParam param) {
		if (param.ratingVector == null || param.contextList == null || param.contextList.size() == 0)
			return;
		
		// Removing item id that not relate to context 
		Set<Integer> fieldIds = Util.newSet();
		fieldIds.addAll(param.ratingVector.fieldIds(true));
		for (int fieldId : fieldIds) {
			Rating rating = param.ratingVector.get(fieldId);
			if (!param.contextList.canInferFrom(rating.contexts))
				param.ratingVector.remove(fieldId);
		}
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "context_prefilter";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Recommendation algorithm based on context prefilter";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new RecommendContextPrefilterImpl();
	}

	
}
