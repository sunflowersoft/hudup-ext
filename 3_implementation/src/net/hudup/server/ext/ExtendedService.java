/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.Util;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Service;
import net.hudup.core.client.ServiceExt;
import net.hudup.core.client.ServiceLocal;
import net.hudup.core.client.ServiceNoticeEvent;
import net.hudup.core.client.ServiceNoticeListener;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.data.DatasetPoolsServiceImpl;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.ui.EvaluateGUIData;
import net.hudup.core.logistic.EventListenerList2;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.Timer2;
import net.hudup.server.DefaultService;
import net.hudup.server.PowerServerConfig;
import net.hudup.server.Transaction;

/**
 * This class is extended version of default service.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtendedService extends DefaultService implements ServiceExt, ServiceLocal, PluginChangedListener {

	
	/**
	 * Internal map of evaluator pair, each evaluator pair has a unique name which is evaluator name.
	 */
	protected Map<String, Evaluator> pairMap = Util.newMap();
	
	
	/**
	 * Internal map of reproduced evaluator pair, each evaluator pair has a unique name which is reproduced evaluator name.
	 */
	protected Map<String, Evaluator> pairReproducedMap = Util.newMap();

	
	/**
	 * Internal map of evaluation GUI data.
	 */
	protected Map<String, EvaluateGUIData> guiDataMap = Util.newMap();
	
	
	/**
	 * Referred power server.
	 */
	protected PowerServer referredServer = null;
	
	
	/**
	 * Internal timer.
	 */
	protected Timer2 timer = null;
	
	
	/**
	 * Dataset pools service.
	 */
	protected DatasetPoolsServiceImpl poolsService = null;
	
	
	/**
	 * List of listeners.
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
	 * Constructor with specified transaction.
	 * @param trans specified transaction.
	 */
	public ExtendedService(Transaction trans) {
		this(trans, null);
	}

	
	/**
	 * Constructor with specified transaction and referred power server.
	 * @param trans specified transaction.
	 * @param referredServer referred power server.
	 */
	public ExtendedService(Transaction trans, PowerServer referredServer) {
		super(trans);
		this.referredServer = referredServer;
	}

	
	@Override
	public boolean open(PowerServerConfig serverConfig, Object... params) {
		boolean opened = super.open(serverConfig, params);
		if (!opened) return false;
		
		loadEvaluators();

		try {
			if (poolsService != null) poolsService.close();
			poolsService = new DatasetPoolsServiceImpl(referredServer);
			poolsService.export(serverConfig.getServerPort());
		} catch (Throwable e) {
			if (poolsService != null) {
				try {poolsService.close();} catch (Throwable ex) {}
				poolsService = null;
			}
			LogUtil.trace(e);
		}
		
		if (timer != null) timer.stop(); timer = null;
		if (!Constants.SERVER_PURGE_LISTENERS) {
			timer = new Timer2(Constants.DEFAULT_SHORT_TIMEOUT*1000, Constants.DEFAULT_LONG_TIMEOUT*1000) {
				
				@Override
				protected void task() {
					try {
						purgeListeners();
						LogUtil.info("Service timer internal tasks: Purging disconnected listeners is successful");
					} catch (Throwable e) {LogUtil.trace(e);}
				}
				
				@Override
				protected void clear() {}
			};
			
			timer.start();
		}
		
		return opened;
	}

	
	/**
	 * Loading evaluators.
	 */
	protected void loadEvaluators() {
		List<Evaluator> evList = Util.getPluginManager().loadInstances(Evaluator.class);
		for (int i = 0; i < evList.size(); i++) {
			Evaluator ev = evList.get(i);
			try {
				if (pairMap.containsKey(ev.getName())) continue;
				
				ev.getConfig().setEvaluatorPort(serverConfig.getServerPort());
				ev.setAgent(true);
				ev.setReferredService(this);
				ev.export(serverConfig.getServerPort());
				if (ev instanceof EvaluatorAbstract)
					((EvaluatorAbstract)ev).removePurgeTimer();
				ev.stimulate();
				
				pairMap.put(ev.getName(), ev);
				guiDataMap.put(ev.getName(), new EvaluateGUIData());
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
	}
	
	
	@Override
	public void close() {
		super.close();
		
		if (pairMap != null) {
			Collection<Evaluator> evs = pairMap.values();
			for (Evaluator ev : evs) {
				try {
					if (poolsService != null) poolsService.removeClient(ev, false);
				} catch (Throwable e) {LogUtil.trace(e);}

				try {
					ev.close();
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			pairMap.clear();
		}
		
		if (pairReproducedMap != null) {
			Collection<Evaluator> evs = pairReproducedMap.values();
			for (Evaluator ev : evs) {
				try {
					if (poolsService != null) poolsService.removeClient(ev, false);
				} catch (Throwable e) {LogUtil.trace(e);}

				try {
					ev.close();
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			pairReproducedMap.clear();
		}
		
		if (guiDataMap != null) guiDataMap.clear();
		
		if (poolsService != null) {
			try {poolsService.close();} catch (Throwable ex) {}
			poolsService = null;
		}

		if (timer != null) timer.stop(); timer = null;
	}

	
	/**
	 * Purging disconnected listeners.
	 */
	protected void purgeListeners() {
		trans.lockRead();

		//Only purging disconnected listeners of reproduced evaluators.
		Collection<Evaluator> evs = Util.newList();
		evs.addAll(pairMap.values());
		synchronized (pairReproducedMap) {
			evs.addAll(pairReproducedMap.values());
		}
		
		List<EventListenerList2> evtLists = Util.newList();
		for (Evaluator ev : evs) {
			if (!(ev instanceof EvaluatorAbstract)) continue;
			EvaluatorAbstract evaluator = (EvaluatorAbstract)ev;
			if (evaluator.getPurgeTimer() != null) continue;
			
			EventListenerList2 evtList = evaluator.getListenerList();
			if (evtList != null && evtList.getListeners().size() > 0)
				evtLists.add(evtList);
		}
		
		for (EventListenerList2 evtList : evtLists) {
			EvaluatorAbstract.purgeListeners(evtList);
		}
		
		trans.unlockRead();
	}
	
	
	@Override
	public PowerServer getReferredServer(String account, String password) throws RemoteException {
		if (validateAccount(account, password, DataConfig.ACCOUNT_ADMIN_PRIVILEGE))
			return referredServer;
		else
			return null;
	}


	@Override
	public PowerServer getReferredServer() {
		return referredServer;
	}


	@Override
	public List<Evaluator> getEvaluators(String account, String password) throws RemoteException {
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return Util.newList();
		else
			return getEvaluators();
	}


	/**
	 * Getting list of evaluator pairs.
	 * @return list of evaluator pairs.
	 * @throws RemoteException if any error raises.
	 */
	public List<Evaluator> getEvaluators() throws RemoteException {
		List<Evaluator> evList = Util.newList();
		trans.lockRead();
		try {
			if (pairMap != null) {
				Collection<Evaluator> evs = pairMap.values();
				for (Evaluator ev : evs) {
					evList.add(ev);
				}
			}
			
			if (pairReproducedMap != null) {
				synchronized (pairReproducedMap) {
					Collection<Evaluator> evs = pairReproducedMap.values();
					for (Evaluator ev : evs) {
						evList.add(ev);
					}
				}
			}

			Collections.sort(evList, new Comparator<Evaluator>() {
				
				@Override
				public int compare(Evaluator o1, Evaluator o2) {
					try {
						return o1.getVersionName().compareToIgnoreCase(o2.getVersionName());
					}
					catch (Throwable e) {
						LogUtil.trace(e);
						return -1;
					}
				}
			});
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			evList = Util.newList();
			
			LogUtil.error("Service fail to get evaluator list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return evList;
	}
	
	
	@Override
	public Evaluator getEvaluator(String evaluatorName, String account, String password) throws RemoteException {
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return null;
		else
			return getEvaluator(evaluatorName);
	}


	@Override
	public Evaluator getEvaluator(String evaluatorName) throws RemoteException {
		Evaluator evaluator = null;
		trans.lockRead();
		try {
			if (pairMap.containsKey(evaluatorName))
				evaluator = pairMap.get(evaluatorName);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			evaluator = null;
			
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return evaluator;
	}


	@Override
	public Evaluator getEvaluator(String evaluatorName, String account, String password, String reproducedVersion)
			throws RemoteException {
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return null;
		
		if (reproducedVersion != null && !reproducedVersion.isEmpty()) {
			String evaluatorReproducedName = EvaluatorAbstract.createVersionName(evaluatorName, reproducedVersion);
			synchronized (pairReproducedMap) {
				if (!pairReproducedMap.containsKey(evaluatorReproducedName)) {
					if (!validateAccount(account, password, DataConfig.ACCOUNT_ADMIN_PRIVILEGE))
						return null;
				}
			}
		}

		return getEvaluator(evaluatorName, reproducedVersion);
	}


	/**
	 * Getting a evaluator with name and reproduced version.
	 * @param evaluatorName evaluator name.
	 * @param reproducedVersion reproduced version.
	 * @return evaluator with reproduced version.
	 * @throws RemoteException if any error raises.
	 */
	public Evaluator getEvaluator(String evaluatorName, String reproducedVersion) throws RemoteException {
		Evaluator reproducedEvaluator = null;
		trans.lockRead();
		try {
			if (reproducedVersion == null || reproducedVersion.isEmpty())
				reproducedEvaluator = getEvaluator(evaluatorName);
			else {
				String evaluatorReproducedName = EvaluatorAbstract.createVersionName(evaluatorName, reproducedVersion);
				
				synchronized (pairReproducedMap) {
					if (pairReproducedMap.containsKey(evaluatorReproducedName))
						reproducedEvaluator = pairReproducedMap.get(evaluatorReproducedName);
					else if (pairMap.containsKey(evaluatorName)) {
						reproducedEvaluator = pairMap.get(evaluatorName).getClass().getDeclaredConstructor().newInstance();
						
						EvaluatorConfig config = reproducedEvaluator.getConfig();
						config.setReproducedVersion(null);
						config.setReproducedVersion(reproducedVersion);
						config.setEvaluatorPort(serverConfig.getServerPort());
						config.setSaveAbility(false);
						reproducedEvaluator.setAgent(true);
						reproducedEvaluator.setReferredService(this);
						reproducedEvaluator.export(serverConfig.getServerPort());
						//Only purging disconnected listeners of reproduced evaluators.
						if (reproducedEvaluator instanceof EvaluatorAbstract)
							((EvaluatorAbstract)reproducedEvaluator).removePurgeTimer();
						reproducedEvaluator.stimulate();
						
						pairReproducedMap.put(evaluatorReproducedName, reproducedEvaluator);
						guiDataMap.put(evaluatorReproducedName, new EvaluateGUIData());
					}
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			reproducedEvaluator = null;
			
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return reproducedEvaluator;
	}
	
	
	@Override
	public boolean removeEvaluator(String evaluatorName, String account, String password, String reproducedVersion)
			throws RemoteException {
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return false;
		else
			return removeEvaluator(evaluatorName, reproducedVersion);
	}


	/**
	 * Removing a evaluator with name reproduced version.
	 * @param evaluatorName evaluator name.
	 * @param reproducedVersion reproduced version.
	 * @return evaluator with reproduced version.
	 * @throws RemoteException if any error raises.
	 */
	public boolean removeEvaluator(String evaluatorName, String reproducedVersion) throws RemoteException {
		if (reproducedVersion == null || reproducedVersion.isEmpty())
			return false;

		boolean ret = true;
		trans.lockRead();
		try {
			String evaluatorReproducedName = EvaluatorAbstract.createVersionName(evaluatorName, reproducedVersion);
			
			synchronized (pairReproducedMap) {
				if (pairReproducedMap.containsKey(evaluatorReproducedName)) {
					Evaluator reproducedEvaluator = pairReproducedMap.get(evaluatorReproducedName);
					try {
						reproducedEvaluator.close();
					} catch (Throwable e) {LogUtil.trace(e);}
					
					pairReproducedMap.remove(evaluatorReproducedName);
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			ret = false;
			
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		return ret;
	}

	
	@Override
	public String[] getEvaluatorNames() throws RemoteException {
		List<String> evaluatorNames = Util.newList();
		
		trans.lockRead();
		try {
			Collection<Evaluator> evs = pairMap.values();
			for (Evaluator ev : evs) {
				evaluatorNames.add(ev.getName());
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		Collections.sort(evaluatorNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				return o1.compareToIgnoreCase(o2);
			}
			
		});
		return evaluatorNames.toArray(new String[0]);
	}


	/**
	 * Getting evaluation GUI data of specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return evaluation GUI data of specified evaluator.
	 */
	public EvaluateGUIData getEvaluateGUIData(Evaluator evaluator) {
		try {
			return guiDataMap.get(evaluator.getVersionName());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}


	@Override
	public DatasetPoolsService getDatasetPoolsService(String account, String password) throws RemoteException {
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return null;
		else
			return poolsService;
	}


	@Override
	public DatasetPoolsService getDatasetPoolsService() {
		return poolsService;
	}
	
	
	@Override
	public void pluginChanged(PluginChangedEvent evt) throws RemoteException {
		super.pluginChanged(evt);
		
		List<Evaluator> evaluators = getEvaluators();
		for (Evaluator evaluator : evaluators) {
			try {
				evaluator.pluginChanged(evt);
			} catch (Exception e) {LogUtil.trace(e);}
		}
	}


	@Override
	public void addNoticeListener(ServiceNoticeListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(ServiceNoticeListener.class, listener);
		}
	}
	
	
	@Override
	public void removeNoticeListener(ServiceNoticeListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(ServiceNoticeListener.class, listener);
		}
	}

	
	/**
	 * Getting array of service notice listeners.
	 * @return array of service notice listeners.
	 */
	protected ServiceNoticeListener[] getNoticeListeners() {
    	synchronized (listenerList) {
	        return listenerList.getListeners(ServiceNoticeListener.class);
    	}
	}
	
	
	@Override
	public void fireNoticeEvent(ServiceNoticeEvent evt)  throws RemoteException {
		ServiceNoticeListener[] listeners = getNoticeListeners();
		for (ServiceNoticeListener listener : listeners) {
			try {
				listener.notify(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
	}
	
	
	/**
	 * Getting list of evaluators.
	 * @param service specified service.
	 * @param connectInfo connection information.
	 * @return list of evaluators.
	 */
	public static List<Evaluator> getEvaluators(Service service, ConnectInfo connectInfo) {
		List<Evaluator> evaluators = Util.newList();
		if (service == null) return evaluators;
		
		if (service instanceof ServiceExt) {
			try {
				if (connectInfo.account != null)
					evaluators = ((ServiceExt)service).getEvaluators(connectInfo.account.getName(), connectInfo.account.getPassword());
				else if (service instanceof ExtendedService)
					evaluators = ((ExtendedService)service).getEvaluators();
				else
					evaluators = Util.newList();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
			return evaluators;
		}
		
		String[] evaluatorNames = null;
		try {
			evaluatorNames = service.getEvaluatorNames();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		if (evaluatorNames == null || evaluatorNames.length == 0)
			return evaluators;
		
		for (String evaluatorName : evaluatorNames) {
			Evaluator evaluator = null;
			try {
				if (connectInfo.account != null)
					evaluator = service.getEvaluator(evaluatorName, connectInfo.account.getName(), connectInfo.account.getPassword());
				else if (service instanceof DefaultService)
					evaluator = ((DefaultService)service).getEvaluator(evaluatorName);
				else
					evaluator = null;
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			if (evaluator != null) evaluators.add(evaluator);
		}
		
		return evaluators;
	}


}
