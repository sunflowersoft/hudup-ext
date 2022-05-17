/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.util.EventObject;

/**
 * This class represents the service notice event.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ServiceNoticeEvent extends EventObject {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * This enum specified service notice event.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public enum Type {
		
		/**
		 * Adding reproduced evaluator.
		 */
		add_reproduced_evaluator,
		
		/**
		 * Remove reproduced evaluator.
		 */
		remove_reproduced_evaluator,
		
		/**
		 * Unknown type.
		 */
		unknown,
	}
	
	
	/**
	 * Type of service notice event.
	 */
	protected Type type = Type.unknown;
	
	
	/**
	 * Evaluator name.
	 */
	protected String evaluatorName = null;
	
	
	/**
	 * Evaluator version.
	 */
	protected String evaluatorVersion = null;
	
	
	/**
	 * Time stamp.
	 */
	protected long timestamp = 0;
	
	
	/**
	 * Constructor with evaluator and time stamp.
	 * @param source specified source.
	 * @param evaluatorName evaluator name.
	 * @param evaluatorVersion evaluator version.
	 * @param timestamp time stamp.
	 * @param type event type.
	 */
	public ServiceNoticeEvent(Object source, Type type, String evaluatorName, String evaluatorVersion, long timestamp) {
		super(source);
		this.type = type;
		this.evaluatorName = evaluatorName;
		this.evaluatorVersion = evaluatorVersion;
		this.timestamp = timestamp;
	}

	
	/**
	 * Getting event type.
	 * @return event type.
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * Setting event type.
	 * @param type specified event type.
	 */
	public void setType(Type type) {
		this.type = type;
	}
	
	
	/**
	 * Getting time stamp.
	 * @return time stamp.
	 */
	public long getTimestamp() {
		return timestamp;
	}
	
	
	/**
	 * Setting time stamp.
	 * @param timestamp time stamp.
	 */
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	
	
	/**
	 * Getting evaluator name.
	 * @return evaluator name.
	 */
	public String getEvaluatorName() {
		return evaluatorName;
	}
	
	
	/**
	 * Setting evaluator name.
	 * @param evaluatorName evaluator name.
	 */
	public void setEvaluatorName(String evaluatorName) {
		this.evaluatorName = evaluatorName;
	}
	
	
	/**
	 * Getting evaluator version.
	 * @return evaluator version.
	 */
	public String getEvaluatorVersion() {
		return evaluatorVersion;
	}
	
	
	/**
	 * Setting evaluator version.
	 * @param evaluatorVersion evaluator version.
	 */
	public void setEvaluatorVersion(String evaluatorVersion) {
		this.evaluatorVersion = evaluatorVersion;
	}
	
	
}
