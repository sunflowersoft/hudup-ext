package net.hudup.evaluate;

import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMeanMetricValue;


/**
 * ARHR metric for recommendation algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ARHR extends CorrelationAccuracy {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public ARHR() {
		super();
	}
	
	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting,
			Dataset testing) {
		// TODO Auto-generated method stub
		
		if (vTesting == null)
			return null;
		
		// List of testing items: T(u)
		RatingVector Nr = extractRelevant(vTesting, true, testing);
		if (Nr == null || Nr.size() == 0)
			return null;

		// List of recommended item: L(u)
		RatingVector Nrs = extractRelevant(recommended, true, testing);
		if (Nrs == null || Nrs.size() == 0)
			return new RealMeanMetricValue(0);
		
		// H(u) = L(u) ^ T(u)
		Set<Integer> commonFieldIds = Util.newSet();
		commonFieldIds.addAll(Nr.fieldIds());
		commonFieldIds.retainAll(Nrs.fieldIds());
		if (commonFieldIds.size() == 0)
			return new RealMeanMetricValue(0);

		List<Integer> NrsList = Util.newList();
		NrsList.addAll(Nrs.fieldIds());
		
		double arhr = 0;
		for (int fieldId : commonFieldIds) {
			arhr += 1.0 / (double) (NrsList.indexOf(fieldId) + 1);
		}
		
		return new RealMeanMetricValue(arhr / (double)Nrs.size());
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "ARHR for recommendation algorithm";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Correlation accuracy";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "ARHR.recommend";
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new ARHR();
	}


}
