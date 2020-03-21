/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.Serializable;
import java.util.EventObject;

import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.logistic.Timestamp;

/**
 * This class represents an event of evaluator.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class EvaluatorEvent extends EventObject {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Type of evaluation event.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	public static enum Type {
		
		/**
		 * Evaluator is setting up. 
		 */
		start,
		
		/**
		 * Evaluator was paused. 
		 */
		pause,
		
		/**
		 * Evaluator was resumed. 
		 */
		resume,
		
		/**
		 * Evaluator was stopped. 
		 */
		stop,
		
		/**
		 * Evaluator was stopped. 
		 */
		force_stop,
		
		/**
		 * Updating pool. 
		 */
		update_pool,
	}
	
	
	/**
	 * Type of event.
	 */
	protected Type type = Type.start;

	
	/**
	 * Evaluation information as other result.
	 */
	protected EvaluateInfo otherResult = null;
	
	
	/**
	 * Pool result.
	 */
	protected DatasetPoolExchanged poolResult = null;

	
	/**
	 * Time stamp.
	 */
	protected Timestamp timestamp = null;
	
	
	/**
	 * Additional information.
	 */
	protected Serializable[] params = null;

	
	/**
	 * Constructor with specified evaluator and event type.
	 * @param evaluator reference to evaluator. This evaluator is invalid in remote call.
	 * @param type type of this event.
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type) {
		this(evaluator, type, null, null, null);
	}

	
	/**
	 * Constructor with specified evaluator and evaluation information. 
	 * @param evaluator specified evaluator. This evaluator is invalid in remote call.
	 * @param type specified type of evaluation event.
	 * @param otherResult evaluation information as other result.
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type, EvaluateInfo otherResult) {
		this(evaluator, type, otherResult, null, null);
	}

	
	/**
	 * Constructor with specified evaluator, evaluation information, and time stamp. 
	 * @param evaluator specified evaluator. This evaluator is invalid in remote call.
	 * @param type specified type of evaluation event.
	 * @param otherResult evaluation information as other result.
	 * @param poolResult specified pool result.
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type, EvaluateInfo otherResult, DatasetPoolExchanged poolResult) {
		this(evaluator, type, otherResult, poolResult, null);
	}
	
	
	/**
	 * Constructor with specified evaluator, evaluation information, and time stamp. 
	 * @param evaluator specified evaluator. This evaluator is invalid in remote call.
	 * @param type specified type of evaluation event.
	 * @param otherResult evaluation information as other result.
	 * @param poolResult specified pool result.
	 * @param timestamp times tamp.
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type, EvaluateInfo otherResult, DatasetPoolExchanged poolResult, Timestamp timestamp) {
		super(evaluator); //Test different hosts for RMI, evaluator wrapper can solve.
		this.type = type;
		this.otherResult = otherResult;
		this.poolResult = poolResult;
		this.timestamp = timestamp;
	}

	
	/**
	 * Getting evaluator. This method is invalid in remote call.
	 * @return {@link Evaluator} that fires this event.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Evaluator getEvaluator() {
		Object source = getSource();
		if (source instanceof Evaluator)
			return (Evaluator)source;
		else
			return null;
	}
	
	
	/**
	 * Getting type of this evaluation event.
	 * @return {@link Type} of this evaluation event.
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * Setting evaluation information as other result.
	 * @param otherResult evaluation information as other result.
	 */
	public void setOtherResult(EvaluateInfo otherResult) {
		this.otherResult = otherResult;
	}

	
	/**
	 * Getting evaluation information as other result.
	 * @return evaluation information as other result.
	 */
	public EvaluateInfo getOtherResult() {
		return otherResult;
	}

	
	/**
	 * Setting pool result.
	 * @param poolResult pool result.
	 */
	public void setPoolResult(DatasetPoolExchanged poolResult) {
		this.poolResult = poolResult;
	}

	
	/**
	 * Getting pool result.
	 * @return pool result.
	 */
	public DatasetPoolExchanged getPoolResult() {
		return poolResult;
	}

	
	/**
	 * Getting time stamp.
	 * @return time stamp.
	 */
	public Timestamp getTimestamp() {
		return timestamp;
	}
	
	
	/**
	 * Setting time stamp.
	 * @param timestamp specified time stamp.
	 */
	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	
	/**
	 * Getting additional parameter list.
	 * @return additional  parameter list.
	 */
	public Serializable[] getParams() {
		return params;
	}
	
	
	/**
	 * Setting additional parameter list.
	 * @param params additional parameter list.
	 */
	public void setParams(Serializable... params) {
		this.params = params;
	}


}
