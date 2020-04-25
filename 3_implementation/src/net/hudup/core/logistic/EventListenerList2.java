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
import java.util.Map;

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
	 * This class represents listener information.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static class ListenerInfo implements Serializable {
		
		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Count of failed ping.
		 */
		public int failedPingCount = 0;
		
	}

	
	/**
	 * Internal list of listeners.
	 */
	protected List<EventListener> listeners = Util.newList();
	
	
    /**
     * Map of listener information.
     */
    protected Map<EventListener, ListenerInfo> listenerInfoMap = Util.newMap();

    
    /**
	 * Default constructor.
	 */
	public EventListenerList2() {

	}

	
	/**
	 * Adding listener with specified class.
	 * @param <T> specified type of event listener.
	 * @param t specified class.
	 * @param l specified listener.
	 */
    public synchronized <T extends EventListener> void add(Class<T> t, T l) {
    	if ((l == null) || !(t.isInstance(l)) || (listenerInfoMap.containsKey(l))) return;
    	
    	listeners.add(l);
   		listenerInfoMap.put(l, new ListenerInfo());
    }


    /**
	 * Removing listener with specified class.
	 * @param <T> specified type of event listener.
	 * @param t specified class.
	 * @param l specified listener.
     */
    public synchronized <T extends EventListener> void remove(Class<T> t, T l) {
    	if ((l == null) || !(t.isInstance(l))) return;
    	
    	while (listeners.remove(l)) {};
		listenerInfoMap.remove(l);
    }
    
    
    /**
	 * Removing listener.
	 * @param l specified listener.
     */
    public synchronized void remove(EventListener l) {
    	if (l == null) return;
    	
    	while (listeners.remove(l)) {};
		listenerInfoMap.remove(l);
    }

    
    /**
     * Getting array of listeners with specified class.
	 * @param <T> specified type of event listener.
     * @param t specified class.
     * @return array of listeners with specified class.
     */
    @SuppressWarnings("unchecked")
	public synchronized <T extends EventListener> T[] getListeners(Class<T> t) {
    	List<T> list = Util.newList();
    	for (EventListener l : listeners) {
    		if (t.isInstance(l)) list.add((T)l);
    	}
    	
    	T[] array = (T[])Array.newInstance(t, list.size());
    	for (int i = 0; i < list.size(); i++) array[i] = list.get(i);
    	
    	return array;
    }
    
    
    /**
     * Getting entire listeners. Using this method is careful.
     * @return entire listeners.
     */
    public synchronized List<EventListener> getListeners() {
    	return listeners;
    }
    
    
    /**
     * Getting information of specified listener.
     * @param l specified listener.
     * @return information of specified listener.
     */
    public synchronized ListenerInfo getInfo(EventListener l) {
		return listenerInfoMap.get(l);
    }
    
    
    /**
     * Updating listener information.
     */
    public synchronized void updateInfo() {
    	for (EventListener l : listeners) {
    		ListenerInfo info = listenerInfoMap.get(l);
    		if (info == null) continue;
    		
    		if (l instanceof Pingable) {
    			boolean success = true;
    			try {
    				((Pingable)l).ping();
    			}
    			catch (Exception e) {success = false;}
    			
        		if (success)
        			info.failedPingCount = 0;
        		else
        			info.failedPingCount = info.failedPingCount + 1;
    		}
    		
    	}
    	
    }
    
    
}
