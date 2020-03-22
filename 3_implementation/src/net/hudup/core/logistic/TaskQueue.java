/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.util.List;

import net.hudup.core.Util;

/**
 * This class represents a queue of tasks.
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
public class TaskQueue extends AbstractRunner {

	
	/**
	 * This interface represents a task whose main declared method is {@link #doTask(Object...)}.
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
	 * Internal list of tasks.
	 */
	private List<Task> taskList = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public TaskQueue() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected void task() {
		// TODO Auto-generated method stub
		synchronized (taskList) {
			while (taskList.size() > 0) {
				try {
					Task task = taskList.remove(0);
					if (task != null) task.doTask();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	
	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		synchronized (taskList) {
			taskList.clear();
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
	
	
}
