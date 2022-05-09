/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import net.hudup.core.ExtraStorage;
import net.hudup.core.Task;
import net.hudup.core.Tasker;
import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;

/**
 * This abstract class implements partially the extra service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtraServiceImpl implements ExtraService, Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal server.
	 */
	protected PowerServer server = null;
	
	
	/**
	 * List of tasks.
	 */
	protected List<Task> tasks = Util.newList();
	
	
	/**
	 * Default constructor.
	 * @param server power server.
	 */
	public ExtraServiceImpl(PowerServer server) {
		this.server = server;
	}

	
	@Override
	public boolean open() throws RemoteException {
		try {
			close();
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		try {
			Collection<Tasker> taskers = ExtraStorage.getTaskers();
			tasks.clear();
			for (Tasker tasker : taskers) {
				try {
					Task task = tasker.create(this.server);
					if (task != null) tasks.add(task);
				}
				catch (Exception e) {LogUtil.trace(e);}
			}
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		return true;
	}


	@Override
	public void close() throws Exception {
		for (Task task : tasks) {
			try {
				task.discard();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		tasks.clear();
	}


	@Override
	public List<Task> getTasks() throws RemoteException {
		return tasks;
	}
	
	
}
