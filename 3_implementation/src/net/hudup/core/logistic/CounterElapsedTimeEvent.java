/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.util.EventObject;

/**
 * This class represents an event for monitoring the counter.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class CounterElapsedTimeEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Elapsed time.
	 */
	protected long elapsedTime = 0;
	
	
	/**
	 * Constructor with source.
	 * @param source source.
	 * @param elapsedTime elapsed time.
	 */
	public CounterElapsedTimeEvent(Object source, long elapsedTime) {
		super(source != null ? source : Integer.valueOf(0));
		this.elapsedTime = elapsedTime;
	}

	
	/**
	 * Getting elapsed time.
	 * @return elapsed time.
	 */
	public long getElapsedTime() {
		return elapsedTime;
	}
	
	
	/**
	 * Setting elapsed time.
	 * @param elapsedTime elapsed time.
	 */
	public void setElapsedTime(long elapsedTime) {
		this.elapsedTime = elapsedTime >= 0 ? elapsedTime : elapsedTime;
	}
	
	
	/**
	 * Getting counter.
	 * @return counter.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Counter getCounter() {
		Object source = getSource();
		if (source == null)
			return null;
		else if (source instanceof Counter)
			return (Counter)source;
		else
			return null;
	}
	
	
}
