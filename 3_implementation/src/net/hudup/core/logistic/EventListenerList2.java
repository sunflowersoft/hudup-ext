/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.hudup.core.Util;
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
public class EventListenerList2 implements Serializable {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal list of listeners.
	 */
	protected List<EventListener> listeners = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public EventListenerList2() {

	}

	
	/**
	 * Adding listener with specified class.
	 * @param t specified class.
	 * @param l specified listener.
	 */
    public synchronized <T extends EventListener> void add(Class<T> t, T l) {
    	if ((l == null) || !(t.isInstance(l))) return;
    	listeners.add(l);
    }


    /**
	 * Removing listener with specified class.
	 * @param t specified class.
	 * @param l specified listener.
     */
    public synchronized <T extends EventListener> void remove(Class<T> t, T l) {
    	if ((l == null) || !(t.isInstance(l))) return;
    	listeners.remove(l);
    }
    
    
    /**
     * Getting array of listeners with specified class.
     * @param t specified class.
     * @return array of listeners with specified class.
     */
    @SuppressWarnings("unchecked")
	public <T extends EventListener> T[] getListeners(Class<T> t) {
    	List<T> list = Util.newList();
    	for (EventListener l : listeners) {
    		if (t.isInstance(l)) list.add((T)l);
    	}
    	
    	T[] array = (T[])Array.newInstance(t, list.size());
    	for (int i = 0; i < list.size(); i++) array[i] = list.get(i);
    	
    	return array;
    }
    
    
}
