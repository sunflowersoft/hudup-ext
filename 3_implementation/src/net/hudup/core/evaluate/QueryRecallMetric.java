package net.hudup.core.evaluate;

import net.hudup.core.alg.Alg;


/**
 * This class implements recall metric for estimation on query ID.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class QueryRecallMetric extends DefaultMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public QueryRecallMetric() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Query recall";
	}

	
	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Hudup";
	}

	
	@Override
	public boolean recalc(Object... params) throws Exception {
		// TODO Auto-generated method stub
		if (params == null || params.length != 1 || !(params[0] instanceof FractionMetricValue))
			return false;

		FractionMetricValue fraction = (FractionMetricValue)params[0];
		if (!fraction.isUsed())
			return false;
		
		return recalc0(fraction);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Query recall";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new QueryRecallMetric();
	}


}
