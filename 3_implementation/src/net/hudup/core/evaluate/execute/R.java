package net.hudup.core.evaluate.execute;

import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.RealMetricValue;
import net.hudup.core.evaluate.RealTwoArrayMetricValue;
import net.hudup.core.evaluate.TwoArrayMetric;
import net.hudup.core.evaluate.TwoArrayMetricValue;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.Vector;

/**
 * This class represents Pearson correlation for evaluating dual regression EM algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class R extends TwoArrayMetric {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public R() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected TwoArrayMetricValue createMetricValue() {
		// TODO Auto-generated method stub
		return new PearsonTwoArrayMetricValue();
	}


	@Override
	protected MetricValue[] parseParams(Object... params) {
		// TODO Auto-generated method stub
		if (params == null || params.length < 2 || !(params[0] instanceof Number) || !(params[1] instanceof Number))
			return null;
		
		RealMetricValue value0 = new RealMetricValue(((Number)params[0]).doubleValue());
		RealMetricValue value1 = new RealMetricValue(((Number)params[1]).doubleValue());
		return new MetricValue[] {value0, value1};
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "R.execute";
	}
	
	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Correlation";
	}
	
	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Pearson correlation (R) for testing algorithms";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new R();
	}


	/**
	 * Pearson metric value.
	 * @author Loc Nguyen
	 * @version 1.0
	 *
	 */
	protected class PearsonTwoArrayMetricValue extends RealTwoArrayMetricValue {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		@Override
		public Object value() {
			// TODO Auto-generated method stub
			Vector v0 = toVector0();
			Vector v1 = toVector1();
			return v0.corr(v1);
		}

		@Override
		public Object clone() {
			// TODO Auto-generated method stub
			PearsonTwoArrayMetricValue pearsonValue = new PearsonTwoArrayMetricValue();
			pearsonValue.update(this.array0, this.array1);
			return pearsonValue;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			Object value = value();
			if (value == null)
				return "";
			else if (value instanceof Number)
				return MathUtil.format(((Number)value).doubleValue());
			else
				return value.toString();
		}
		
	}
	
	
}
