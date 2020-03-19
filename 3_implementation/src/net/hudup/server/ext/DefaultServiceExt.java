/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
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

import net.hudup.core.Util;
import net.hudup.core.client.ServiceExt;
import net.hudup.core.data.DataConfig;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.LogUtil;
import net.hudup.evaluate.ui.EvaluateGUIData;
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
public class DefaultServiceExt extends DefaultService implements ServiceExt {

	
	/**
	 * Internal map of evaluator pair, each evaluator pair has a unique name which is evaluator name.
	 */
	protected Map<String, Evaluator> pairMap = Util.newMap();
	
	
	/**
	 * Internal map of reproduced evaluator pair, each evaluator pair has a unique name which is reproduced evaluator name.
	 */
	protected Map<String, Evaluator> pairReproducedMap = Util.newMap();

	
	/**
	 * Constructor with specified transaction.
	 * @param trans specified transaction.
	 */
	public DefaultServiceExt(Transaction trans) {
		super(trans);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean open(PowerServerConfig serverConfig, Object... params) {
		// TODO Auto-generated method stub
		boolean opened = super.open(serverConfig, params);
		if (!opened) return false;
		
		List<Evaluator> evList = Util.getPluginManager().discover(Evaluator.class);
		for (int i = 0; i < evList.size(); i++) {
			Evaluator ev = evList.get(i);
			try {
				if (pairMap.containsKey(ev.getName())) continue;
				
				ev.getConfig().setEvaluatorPort(serverConfig.getServerPort());
				ev.setAgent(true);
				ev.setTag(new EvaluateGUIData());
				ev.export(serverConfig.getServerPort());
				
				pairMap.put(ev.getName(), ev);
			}
			catch (Throwable e) {e.printStackTrace();}
		}

		return opened;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		if (pairMap != null) {
			Collection<Evaluator> evs = pairMap.values();
			for (Evaluator ev : evs) {
				try {
					ev.close();
				} catch (Throwable e) {e.printStackTrace();}
			}
			pairMap.clear();
		}
		
		if (pairReproducedMap != null) {
			Collection<Evaluator> evs = pairReproducedMap.values();
			for (Evaluator ev : evs) {
				try {
					ev.close();
				} catch (Throwable e) {e.printStackTrace();}
			}
			pairReproducedMap.clear();
		}
	}

	
	@Override
	public List<Evaluator> getEvaluators(String account, String password) throws RemoteException {
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return Util.newList();
		
		List<Evaluator> evList = Util.newList();
		trans.lockWrite();
		try {
			if (pairMap != null) {
				Collection<Evaluator> evs = pairMap.values();
				for (Evaluator ev : evs) {
					evList.add(ev);
				}
			}
			
			if (pairReproducedMap != null) {
				Collection<Evaluator> evs = pairReproducedMap.values();
				for (Evaluator ev : evs) {
					evList.add(ev);
				}
			}

			Collections.sort(evList, new Comparator<Evaluator>() {
				
				@Override
				public int compare(Evaluator o1, Evaluator o2) {
					// TODO Auto-generated method stub
					try {
						return o1.getName().compareToIgnoreCase(o2.getName());
					}
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						return -1;
					}
				}
			});
		}
		catch (Throwable e) {
			e.printStackTrace();
			evList = Util.newList();
			
			LogUtil.error("Service fail to get evaluator list, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return evList;
	}


	@Override
	public Evaluator getEvaluator(String evaluatorName, String account, String password) throws RemoteException {
		// TODO Auto-generated method stub
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return null;
		
		Evaluator evaluator = null;
		trans.lockWrite();
		try {
			if (pairMap.containsKey(evaluatorName))
				evaluator = pairMap.get(evaluatorName);
		}
		catch (Throwable e) {
			e.printStackTrace();
			evaluator = null;
			
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return evaluator;
	}


	@Override
	public Evaluator getEvaluator(String evaluatorName, String account, String password, String reproducedVersion)
			throws RemoteException {
		// TODO Auto-generated method stub
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return null;

		Evaluator reproducedEvaluator = null;
		trans.lockWrite();
		try {
			if (reproducedVersion == null || reproducedVersion.isEmpty())
				reproducedEvaluator = getEvaluator(evaluatorName, account, password);
			else if (validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE)) {
				String evaluatorReproducedName = evaluatorName + "-" + reproducedVersion;
				
				if (pairReproducedMap.containsKey(evaluatorReproducedName))
					reproducedEvaluator = pairReproducedMap.get(evaluatorReproducedName);
				else if (pairMap.containsKey(evaluatorName)) {
					reproducedEvaluator = pairMap.get(evaluatorName).getClass().newInstance();
					
					reproducedEvaluator.getConfig().setReproducedVersion(reproducedVersion);
					reproducedEvaluator.getConfig().setEvaluatorPort(serverConfig.getServerPort());
					reproducedEvaluator.getConfig().setSaveAbility(false);
					reproducedEvaluator.setAgent(true);
					reproducedEvaluator.setTag(new EvaluateGUIData());
					reproducedEvaluator.export(serverConfig.getServerPort());
					
					pairReproducedMap.put(evaluatorReproducedName, reproducedEvaluator);
				}
			}
			else {
				//Return evaluator client version. Next updated version.
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			reproducedEvaluator = null;
			
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return reproducedEvaluator;
	}


	@Override
	public boolean removeEvaluator(String evaluatorName, String account, String password, String reproducedVersion)
			throws RemoteException {
		// TODO Auto-generated method stub
		if (reproducedVersion == null || reproducedVersion.isEmpty())
			return false;
		if (!validateAccount(account, password, DataConfig.ACCOUNT_EVALUATE_PRIVILEGE))
			return false;
		
		boolean ret = true;
		trans.lockWrite();
		try {
			String evaluatorReproducedName = evaluatorName + "-" + reproducedVersion;
			
			if (pairReproducedMap.containsKey(evaluatorReproducedName)) {
				Evaluator reproducedEvaluator = pairReproducedMap.get(evaluatorReproducedName);
				try {
					reproducedEvaluator.close();
				} catch (Throwable e) {e.printStackTrace();}
				
				pairReproducedMap.remove(evaluatorReproducedName);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			ret = false;
			
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockWrite();
		}
		
		return ret;
	}


	@Override
	public String[] getEvaluatorNames() throws RemoteException {
		// TODO Auto-generated method stub
		List<String> evaluatorNames = Util.newList();
		
		trans.lockRead();
		try {
			Collection<Evaluator> evs = pairMap.values();
			for (Evaluator ev : evs) {
				evaluatorNames.add(ev.getName());
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			LogUtil.error("Service fail to get evaluator, caused by " + e.getMessage());
		}
		finally {
			trans.unlockRead();
		}
		
		Collections.sort(evaluatorNames, new Comparator<String>() {

			@Override
			public int compare(String o1, String o2) {
				// TODO Auto-generated method stub
				return o1.compareToIgnoreCase(o2);
			}
			
		});
		return evaluatorNames.toArray(new String[0]);
	}


}
