/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import javax.swing.event.EventListenerList;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgAbstract;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.alg.KBaseAbstract;
import net.hudup.core.evaluate.Metric;

/**
 * This aims to solve the serialization problem when the system class {@link EventListenerList} does not specify the serial version UID.
 * Other classes use this new event listener list are {@link AlgAbstract}, {@link AlgRemoteWrapper}, and {@link KBaseAbstract}.
 * Note that some classes that extend {@link Alg} such as {@link Metric} are now do not support remote calling when they are still serialized.
 * However, this solution that specifies explicitly the serial version UID {@link #serialVersionUID} is work-around.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EventListenerList2 extends EventListenerList {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public EventListenerList2() {

	}

	
}
