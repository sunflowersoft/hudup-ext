package net.hudup.alg.cf.bnet;

import java.awt.Component;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import elvira.Bnet;
import elvira.Evidence;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.RecommendParam;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.RatingFilter;
import net.hudup.core.logistic.ValueTriple;

/**
 * This class implements collaborative filtering based on Bayesian network.
 * This algorithm is now stable but it runs too slowly because of Bayesian network inference mechanism with many nodes.
 * So it is put into update list. Whether to use it depends on users.
 * The attribute <code>reduced_ratio</code> ranges from 0 to 1.
 * The larger the attribute <code>reduced_ratio</code> is, the faster the inference mechanism is but the higher the accuracy is.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@NextUpdate
public class BnetCF extends BnetCFAbstract {

	
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
	public KBase newKB() throws RemoteException {
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
		if (bKb.getBnetList().size() == 0)
			return null;
		else
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
		superConfig.addInvisible(BnetKB.K2_MAX_PARENTS);
		
		superConfig.put(BnetKB.LEARNING_METHOD_FIELD, BnetKB.K2);
		superConfig.addInvisible(BnetKB.LEARNING_METHOD_FIELD);
		
		superConfig.put(BnetKB.REDUCED_RATIO_FIELD, BnetKB.REDUCED_RATIO_DEFAULT);

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



