/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate;

import java.rmi.RemoteException;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.recommend.ClassificationAccuracy;

/**
 * This class represents Recall metric for recommendation algorithms.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Recall extends ClassificationAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public Recall() {
		super();
	}


	@Override
	public String getName() {
		return "Recall.recommend";
	}

	
	@Override
	public String getTypeName() throws RemoteException {
		return "Classification accuracy";
	}
	
	
	@Override
	public String getDescription() throws RemoteException {
		return "Recall for recommendation algorithm";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		
		if (vTesting == null || vTesting.size() == 0)
			return null;
		
		RatingVector Nr = extractRelevant(vTesting, true, testing);
		if (Nr == null || Nr.size() == 0)
			return null;
		
		RatingVector Nrs = extractRelevant(recommended, true, testing);
		if (Nrs == null || Nrs.size() == 0)
			return new FractionMetricValue(0, Nr.size());
		
		
		Set<Integer> fieldIds = Util.newSet();
		fieldIds.addAll(Nrs.fieldIds());
		for (int fieldId : fieldIds) {
			if (!Nr.isRated(fieldId))
				Nrs.remove(fieldId);
		}
		
		return new FractionMetricValue(Nrs.size(), Nr.size()); //This is more exact for overall dataset and can solve problem of zero vector.

	}


}
