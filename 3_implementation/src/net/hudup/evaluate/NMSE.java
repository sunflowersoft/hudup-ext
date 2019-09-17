/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.recommend.PredictiveAccuracy;

/**
 * This class represents NMSE metric for recommendation algorithms.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NMSE extends PredictiveAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public NMSE() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "NMSE.recommend";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		// TODO Auto-generated method stub
		return "Predictive accuracy";
	}


	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Normalized Mean Squared Error for recommendation algorithm";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting,
			Dataset testing) {
		// TODO Auto-generated method stub
		
		if (vTesting == null)
			return null;

		double     mse = 0;
		DataConfig config = testing.getConfig(); 
		double     interval = config.getMaxRating() - config.getMinRating();
		int        n = 0;
		Set<Integer> fieldIds = recommended.fieldIds(true);
		
		for (int fieldId : fieldIds) {
			if (!vTesting.isRated(fieldId))
				continue;
			
			double dev = Math.abs(recommended.get(fieldId).value - vTesting.get(fieldId).value);
			double mae = dev / interval;
			
			mse += mae * mae;
			n++;
		}
		
		if (n > 0)
			return new FractionMetricValue(mse, n);
		else
			return null;
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new NMSE();
	}


}
