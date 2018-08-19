package net.hudup.core.evaluate;

import net.hudup.core.alg.Alg;


/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SetupTimeMetric extends TimeMetric {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * 
	 */
	public SetupTimeMetric() {
		// TODO Auto-generated constructor stub
		super();
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Setup time";
	}

	
	@Override
	public String getDesc() {
		// TODO Auto-generated method stub
		return "Setup time (in seconds)";
	}


	@Override
	public String getTypeName() {
		// TODO Auto-generated method stub
		return "Time";
	}


	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SetupTimeMetric();
	}


}
