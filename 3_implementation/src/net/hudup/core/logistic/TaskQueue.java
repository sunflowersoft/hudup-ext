/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.util.Collection;
import java.util.EventObject;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Simplify;

/**
 * This class represents a queue of tasks.
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
public class TaskQueue extends AbstractRunner {

	
	/**
	 * This interface represents a task whose main declared method is {@link #doTask()}.
	 * @author Loc Nguyen
	 * @version 13.0
	 */
	public static interface Task {
		
		/**
		 * Main method to do some task.
		 * @throws Exception if any error raises.
		 */
		void doTask() throws Exception;
		
	}
	
	
	/**
	 * This class represents a tasks driven events.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static class EventTask {
		
		/**
		 * List of events.
		 */
		protected List<EventObject> evtList = Util.newList();
		
		/**
		 * Last access.
		 */
		protected long lastDone = 0;
		
		/**
		 * Default constructor.
		 */
		public EventTask() {
			this(null, 0);
		}

		/**
		 * Constructor with event list and last access.
		 * @param evtList event list.
		 */
		public EventTask(List<EventObject> evtList) {
			this(evtList, 0);
		}

		/**
		 * Constructor with event list and last access.
		 * @param evtList event list.
		 * @param lastAccess last access.
		 */
		public EventTask(List<EventObject> evtList, long lastAccess) {
			this.evtList = evtList != null ? evtList : Util.newList();
			this.lastDone = lastAccess;
		}
		
		/**
		 * Getting event at specified index.
		 * @param index specified index.
		 * @return event at specified index.
		 */
		public EventObject get(int index) {
			return evtList.get(index);
		}
		
		/**
		 * Adding event object.
		 * @param evt specified event object.
		 * @return true if adding is successful.
		 */
		public boolean add(EventObject evt) {
			return evtList.add(evt);
		}
		
		/**
		 * Removing event at specified index.
		 * @param index specified index.
		 * @return removed event.
		 */
		public EventObject remove(int index) {
			return evtList.remove(index);
		}
		
		/**
		 * Getting number of events.
		 * @return number of events.
		 */
		public int size() {
			return evtList.size();
		}
		
		/**
		 * Clearing all events.
		 */
		public void clear() {
			evtList.clear();
		}
		
		/**
		 * Getting last time to do task.
		 * @return last time to do task.
		 */
		public long getLastDone() {
			return lastDone;
		}
		
		/**
		 * Updating last time to do task.
		 */
		public void updateLastDone() {
			lastDone = System.currentTimeMillis();
		}
		
		/**
		 * Getting list of events.
		 * @return list of events.
		 */
		public List<EventObject> getEventList() {
			return evtList;
		}
		
	}
	
	
	/**
	 * Default maximum number of event objects. Suppose a task needs at least 5 miliseconds. There are 1000 tasks in 5 seconds at most
	 * and so the default access period from client is 5 seconds. Suppose the networking cost is as same as a task cost.
	 * So the default maximum number of event objects is 2000. Suppose the length of array is 200,000 in average, there can be 100 clients in average without concerning other resources and costs.
	 * If resources are limited 10 times in decrease, there can be 10 client in average.
	 */
	public static int MAX_NUMBER_EVENT_OBJECTS = 2000;
	
	
	/**
	 * Internal list of tasks.
	 */
	protected List<Task> taskList = Util.newList();
	
	
	/**
	 * Internal map of event tasks. Each entry is an event-driven task of a listener ID.
	 */
	protected Map<UUID, EventTask> taskMap = Util.newMap();

	
	/**
	 * Holding an extra list of event listeners.
	 * 
	 */
    protected EventListenerList2 listenerList = new EventListenerList2();

    
	/**
	 * Default constructor.
	 */
	public TaskQueue() {

	}

	
	/**
	 * Setting listener list. Using this method is careful.
	 * @param listenerList listener list.
	 */
	public synchronized void setListenerList(EventListenerList2 listenerList) {
		synchronized (this.listenerList) {
			if (listenerList != null) this.listenerList = listenerList;
		}
	}


	@Override
	protected void task() {
		while (true) {
			Task task = null;
			
			synchronized (taskList) {
				if (taskList.size() == 0) break;
				task = taskList.remove(0);
			}
			
			try {
				if (task != null) task.doTask();
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
		
		waitForTaskQueue(taskMap);
	}

	
	/**
	 * Waiting for task queue.
	 * @param taskMap map of tasks.
	 */
	public static void waitForTaskQueue(Map<UUID, EventTask> taskMap) {
		if (taskMap == null) return;
		
		long startTime = System.currentTimeMillis();
		while (true) {
			synchronized (taskMap) {
				Set<UUID> listenerUUIDs = Util.newSet();
				listenerUUIDs.addAll(taskMap.keySet());
				boolean empty = true;
				for (UUID listenerUUID : listenerUUIDs) {
					EventTask task = taskMap.get(listenerUUID);
					if (task == null) continue;
					
					if (task.getLastDone() > 0 && System.currentTimeMillis() - task.getLastDone() > Constants.DEFAULT_LONG_TIMEOUT*1000) {
						task.clear();
						taskMap.remove(listenerUUID);
					}
					
					empty = empty && (task.size() == 0);
				}
				if (empty) break;
				
				if (System.currentTimeMillis() - startTime > Constants.DEFAULT_SHORT_TIMEOUT*1000) {
					Collection<EventTask> tasks = taskMap.values();
					for (EventTask task : tasks) {
						task.clear();
						if (task.getLastDone() <= 0) task.updateLastDone(); //Fixing bug date 2021.07.18 by Loc Nguyen.
					}
					break;
				}
			}
			
			try {
				Thread.sleep(Counter.PERIOD*1000); //1 second for listeners to occupy doing tasks (taskMap).
			} catch (Exception e) {LogUtil.trace(e);}
			
		}
	}

	
	@Override
	protected void clear() {
		synchronized (taskList) {
			taskList.clear();
		}
		
		synchronized (taskMap) {
			Collection<EventTask> tasks = taskMap.values();
			for (EventTask task : tasks) {
				task.clear();
			}
		}
	}

	
	/**
	 * Adding a task to this queue.
	 * @param task specified task to be added.
	 * @return true if adding is success.
	 */
	public synchronized boolean addTask(Task task) {
		if (task == null) return false;
		
		synchronized (taskList) {
			return taskList.add(task);
		}
	}
	
	
	/**
	 * Adding task to this queue by listener ID and event object.
	 * @param listenerID listener ID.
	 * @param evt event object.
	 * @return true if adding successfully.
	 */
	public synchronized boolean addTask(UUID listenerID, EventObject evt) {
		if (listenerID == null || evt == null) return false;
		
		synchronized (taskMap) {
			EventTask task = null;
			if (taskMap.containsKey(listenerID))
				task = taskMap.get(listenerID);
			else {
				task = new EventTask();
				taskMap.put(listenerID, task);
			}
			
			boolean ret = task.add(evt);
			if (task.size() > MAX_NUMBER_EVENT_OBJECTS)
				task.remove(0);
			
			return ret;
		}
	}
	
	
	/**
	 * Adding task to this queue by event object.
	 * @param evt event object.
	 * @return true if adding successfully.
	 */
	public synchronized boolean addTask(EventObject evt) {
		if (evt == null) return false;
		
		synchronized (taskMap) {
			Collection<EventTask> tasks = taskMap.values();
			for (EventTask task : tasks) {
				task.add(evt);
				if (task.size() > MAX_NUMBER_EVENT_OBJECTS)
					task.remove(0);
			}
			
			return true;
		}
	}
	
	
//	/**
//	 * Performing as task of specified listener ID, which simply getting and removing an event from event list of such listener.
//	 * @param listenerID specified listener ID.
//	 * @return true if successful.
//	 */
//	public EventObject doTask(UUID listenerID) {
//		if (listenerID == null) return null;
//		
//		synchronized (taskMap) {
//			EventTask task = null;
//			if (!taskMap.containsKey(listenerID)) return null;
//			
//			task = taskMap.get(listenerID);
//			task.updateLastDone();
//			if (task.size() == 0)
//				return null;
//			else
//				return task.remove(0);
//		}
//		
//	}
	
	
	/**
	 * Performing a task list of specified listener.
	 * @param listenerID specified listener ID.
	 * @return list of events as the task list.
	 */
	public List<EventObject> doTask(UUID listenerID) {
		if (listenerID == null) return Util.newList();
		
		synchronized (taskMap) {
			EventTask task = null;
			if (!taskMap.containsKey(listenerID)) return Util.newList();
			
			task = taskMap.get(listenerID);
			List<EventObject> returnedEvtList = Util.newList(task.size());
			returnedEvtList.addAll(task.getEventList());
			task.clear();
			task.updateLastDone();
			return returnedEvtList;
		}
	}

	
	/**
	 * Performing a task list of specified listener with simplified event if necessary.
	 * The function of this method is the same to the method {@link #doTask(UUID)}.
	 * @param listenerID specified listener ID.
	 * @return list of events as the task list.
	 */
	public List<EventObject> doTask2(UUID listenerID) {
		List<EventObject> evtList = doTask(listenerID);
		List<EventObject> returnedEvtList = Util.newList(evtList.size());
		for (EventObject evt : evtList) {
			if (evt instanceof Simplify) {
	    		try {
	    			EventObject simplifiedEvt = (EventObject) ((Simplify)evt).simplify();
	    			if (simplifiedEvt != null) evt = simplifiedEvt;
	    		} catch (Exception e) {LogUtil.trace(e);}
			}
			
			returnedEvtList.add(evt);
		}
		
		return returnedEvtList;
	}
	
	
	/**
	 * Adding listener via listener ID.
	 * @param listenerID listener ID.
	 * @return true if adding successful.
	 */
	public boolean addListener(UUID listenerID) {
		if (listenerID == null) return false;
		
		synchronized (taskMap) {
			if (taskMap.containsKey(listenerID)) 
				return true;
			else {
				EventTask task = new EventTask();
				taskMap.put(listenerID, task);
				return true;
			}
		}
	}
	
	
	/**
	 * Removing listener via listener ID. 
	 * @param listenerID listener ID. 
	 * @return true if removing is successful.
	 */
	public boolean removeListener(UUID listenerID) {
		if (listenerID == null) return false;
		
		synchronized (taskMap) {
			return taskMap.remove(listenerID) != null;
		}
	}
	
	
}


