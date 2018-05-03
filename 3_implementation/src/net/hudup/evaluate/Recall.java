package net.hudup.evaluate;

import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.FractionMetricValue;
import net.hudup.core.evaluate.MetricValue;


/**
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
	 * 
	 */
	public Recall() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Recall.recommend";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Classification accuracy";
	}
	
	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Recall for recommendation algorithm";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		// TODO Auto-generated method stub
		
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
		
		return new FractionMetricValue(Nrs.size(), Nr.size());

	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new Recall();
	}


}
