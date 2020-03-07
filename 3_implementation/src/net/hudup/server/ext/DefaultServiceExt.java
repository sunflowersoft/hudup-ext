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
	 * This class is the pair of evaluator and evaluator GUI data.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	class EvaluatorPair {
		
		/**
		 * Evaluator.
		 */
		public Evaluator evaluator = null;
		
		/**
		 * Evaluator GUI data.
		 */
		public EvaluateGUIData data = null;
		
		/**
		 * Default constructor.
		 */
		public EvaluatorPair() {
			
		}
		
		/**
		 * Constructor with specified evaluator and GUI data.
		 * @param evaluator specified evaluator.
		 * @param data specified data.
		 */
		public EvaluatorPair(Evaluator evaluator, EvaluateGUIData data) {
			this.evaluator = evaluator;
			this.data = data;
		}
		
	}
	
	
	/**
	 * Internal map of evaluator pair, each evaluator pair has a unique name which is evaluator name.
	 */
	protected Map<String, EvaluatorPair> pairMap = Util.newMap();
	
	
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
				ev.getConfig().setStandalone(true);
				ev.export(serverConfig.getServerPort());
				
				pairMap.put(ev.getName(), new EvaluatorPair(ev, new EvaluateGUIData()));
			}
			catch (Throwable e) {e.printStackTrace();}
		}

		return opened;
	}

	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		super.close();
		if (pairMap == null) return;
		
		Collection<EvaluatorPair> pairs = pairMap.values();
		for (EvaluatorPair pair : pairs) {
			try {
				pair.evaluator.getConfig().save();
				pair.evaluator.close();
			} catch (Throwable e) {e.printStackTrace();}
		}
		pairMap.clear();
	}

	
	/**
	 * Getting local evaluators.
	 * @return local evaluators.
	 */
	public List<Evaluator> getLocalEvaluators() throws RemoteException {
		List<Evaluator> evList = Util.newList();
		if (pairMap == null) return evList;
		
		Collection<EvaluatorPair> pairs = pairMap.values();
		for (EvaluatorPair pair : pairs) {
			evList.add(pair.evaluator);
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
		
		return evList;
	}


}
