/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.evaluate.Evaluator;
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
public class DefaultServiceExt extends DefaultService {

	
	/**
	 * Internal map of evaluators, each evaluator has a unique name.
	 */
	protected Map<String, Evaluator> evMap = Util.newMap();
	
	
	/**
	 * Internal map of evaluators GUI data, each evaluator has a unique name.
	 */
	protected Map<String, EvaluateGUIData> evGUIDataMap = Util.newMap();

	
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
				if (evMap.containsKey(ev.getName())) continue;
				
				ev.getConfig().setEvaluatorPort(serverConfig.getServerPort());
				ev.getConfig().setStandalone(true);
				ev.export(serverConfig.getServerPort());
				evMap.put(ev.getName(), ev);
			}
			catch (Throwable e) {e.printStackTrace();}
		}

		return opened;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		
		Collection<Evaluator> evs = evMap.values();
		for (Evaluator ev : evs) {
			try {
				ev.getConfig().save();
				ev.close();
			} catch (Throwable e) {e.printStackTrace();}
		}
		evMap.clear();
	}

	
	/**
	 * Getting list of local evaluators.
	 * @return list of local evaluators.
	 */
	public List<Evaluator> getLocalEvaluatorList() {
		List<Evaluator> evList = Util.newList();
		evList.addAll(evMap.values());
		
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
		
		return evList;
	}


}
