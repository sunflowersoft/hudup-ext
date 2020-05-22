/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate;

import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.recommend.CorrelationAccuracy;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class implements normalized distance-based performance (NDPM) metric.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
public class NDPM extends CorrelationAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public NDPM() {
		super();
	}


	@Override
	public String getName() {
		return "NDPM.recommend";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		return "Correlation accuracy";
	}


	@Override
	public String getDescription() throws RemoteException {
		return "Normalized distance-based performance for recommendation algorithm";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		
		if (vTesting == null)
			return null;

		Set<Integer>  fieldIds = recommended.fieldIds(true);
		List<Integer> common = Util.newList();
		for (int fieldId : fieldIds) {
			if (vTesting.isRated(fieldId))
				common.add(fieldId);
		}
		if (common.size() < 2)
			return null;
		
		int Cminus = 0;
		int Cu = 0;
		int Ci = 0;
		for (int i = 0; i < common.size() - 1; i++) {
			int fieldId1 = common.get(i);
			double recommendedRating1 = recommended.get(fieldId1).value;
			double testingRating1 = vTesting.get(fieldId1).value;
			
			for (int j = i + 1; j < common.size(); j++) {
				int fieldId2 = common.get(j);
				double recommendedRating2 = recommended.get(fieldId2).value;
				double testingRating2 = vTesting.get(fieldId2).value;
				
				if ( 
						((recommendedRating1 >= recommendedRating2) && (testingRating1 < testingRating2)) ||
						((recommendedRating1 < recommendedRating2 ) && (testingRating1 >= testingRating2))
					) 
				{
					Cminus++;
				}
				
				if ( 
						(recommendedRating1 == recommendedRating2) && (testingRating1 != testingRating2)
					) 
				{
					Cu++;
				}
				
				if (testingRating1 != testingRating2)
					Ci++;
				
			} // for
			
			
			
		} // for
		
		
		if (Ci > 0)
			return new FractionMetricValue(2 * Cminus + Cu, 2 * Ci);
		else
			return null;
		
	}


}
