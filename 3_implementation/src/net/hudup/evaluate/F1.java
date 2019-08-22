package net.hudup.evaluate;

import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.RatingVector;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMetricValue;
import net.hudup.core.evaluate.recommend.ClassificationAccuracy;
import net.hudup.core.logistic.NextUpdate;


/**
 * This class represents F1 metric.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class F1 extends ClassificationAccuracy {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public F1() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "F1.recommend";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Classification accuracy";
	}


	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "F1 for recommendation algorithm";
	}

	
	@Override
	protected MetricValue calc(RatingVector recommended, RatingVector vTesting, Dataset testing) {
		// TODO Auto-generated method stub
		
		if (vTesting == null || vTesting.size() == 0)
			return null;
		
		RatingVector Nr = extractRelevant(vTesting, true, testing);
		if (Nr == null || Nr.size() == 0)
			return null;
		
		RatingVector Ns = recommended;
		RatingVector Nrs = extractRelevant(recommended, true, testing);
		if (Nrs == null)
			return null;

		Set<Integer> fieldIds = Util.newSet();
		fieldIds.addAll(Nrs.fieldIds());
		for (int fieldId : fieldIds) {
			if (!Nr.isRated(fieldId))
				Nrs.remove(fieldId);
		}
		if (Nrs.size() == 0)
			return null;
		
		double precision = (double)Nrs.size() / (double)Ns.size();
		double recall = (double)Nrs.size() / (double)Nr.size();
		return new RealMetricValue(2*precision*recall / (precision+recall));
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new F1();
	}


}


/**
 * This class represents F1 metric.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate
@Deprecated
class F1Deprecated extends MetaMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public F1Deprecated() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Setting up F1 metric with precision metric and recall metric.
	 * @param precision precision metric.
	 * @param recall recall metric.
	 */
	public void setup(Precision precision, Recall recall) {
		// TODO Auto-generated method stub
		super.setup(new Object[] { precision, recall });
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "F1.recommend.deprecated";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Classification accuracy";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "F1 (deprecated) for recommendation algorithm";
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		
		if ( meta == null || meta.length < 2 || meta[0] == null || meta[1] == null || 
				(!(meta[0] instanceof Precision)) || 
				(!(meta[1] instanceof Recall)) )
			return false;
		
		Precision p = (Precision) meta[0];
		Recall r = (Recall) meta[1];
		if (!p.isValid() || !r.isValid() ||
				!p.getCurrentValue().isUsed() || !r.getCurrentValue().isUsed() ||
				!p.getAccumValue().isUsed() || !r.getAccumValue().isUsed()
			)
			return false;
		
		double pCurrentValue = MetricValue.extractRealValue(p.getCurrentValue());
		double rCurrentValue = MetricValue.extractRealValue(r.getCurrentValue());
		this.currentValue = new RealMetricValue(
				(2 * pCurrentValue * rCurrentValue) / 
				(pCurrentValue + rCurrentValue));
		
		double pAccumValue = MetricValue.extractRealValue(p.getAccumValue());
		double rAccumValue = MetricValue.extractRealValue(r.getAccumValue());
		this.accumValue = new RealMetricValue(
				(2 * pAccumValue * rAccumValue) / 
				(pAccumValue + rAccumValue));
		
		return true;
	}


	@Override
	protected boolean recalc0(MetricValue metricValue) throws Exception {
		// TODO Auto-generated method stub
		throw new Exception("Not implement this method");
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new F1Deprecated();
	}


}
