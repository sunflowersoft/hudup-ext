/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.data.Exportable;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.parser.DatasetParser;

/**
 * 
 * Plug-in manager is responsible for discovering and managing registered recommendation algorithms.
 * The class that implements {@code PluginManager} is {@code Firer}.
 * {@code Alg} is the most abstract interface for all algorithms.
 * Every algorithm has a unique name. Every algorithm is registered in system register table and identified by its name.
 * Such system register table is modeled as {@code RegisterTable} class.
 * Actually, plug-in manager discovers automatically all algorithms via their names at the booting time and requires {@link PluginStorage} to add such algorithms into register tables represented by {@link RegisterTable} classes.
 * After Hudup framework started, it is easy to retrieve any any algorithms by looking up algorithms in register tables from {@link PluginStorage}.
 * Please pay attention that system register tables are stored in a so-called {@code plug-in storage} which is represented by this {@code PluginStorage} class.
 * <br><br>
 * Note, general algorithm is represented by {@link Alg} interface. {@link Alg} has five typical inherited interfaces as follows:
 * <ul>
 * <li>Recommendation algorithm {@code Recommender}.</li> 
 * <li>Context template schema manager {@code CTSManager}.</li> 
 * <li>Data set parser {@code DatasetParser}.</li>
 * <li>External query {@code ExternalQuery}.</li>
 * <li>Metric {@code Metric}.</li>
 * </ul>
 * In general, this {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For examples, the internal variable {@link #normalAlgReg} which is a register table manages normal algorithms.
 * The internal variable {@link #metricReg} which is a register table manages metrics for evaluating algorithms.
 * {@link PluginStorage} also manages a list of next-update algorithms. Next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
 * Currently, next-update algorithms are not used.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PluginStorage implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of normal algorithm (s).
	 */
	public static final String NORMAL_ALG     = "Alg";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code DatasetParser} (s).
	 */
	public static final String PARSER         = "Parser";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code Metric} (s).
	 */
	public static final String METRIC         = "Metric";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code ExternalQuery} (s).
	 */
	public static final String EXTERNAL_QUERY = "External query";
	
	/**
	 * As aforementioned, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores only the same type algorithms.
	 * This constant is the name of {@link RegisterTable} of {@code CTSManager} (s).
	 */
	public static final String CTS_MANAGER    = "CTS manager";
	

	/**
	 * Normal algorithm register. Note, this is public and static variable.
	 */
	protected static RegisterTable normalAlgReg = new RegisterTable();

	
	/**
	 * {@link DatasetParser} register. Note, this is public and static variable.
	 */
	protected static RegisterTable parserReg = new RegisterTable();

	
	/**
	 * {@link Metric} register. Note, this is public and static variable.
	 */
	protected static RegisterTable metricReg = new RegisterTable();

	
	/**
	 * {@link ExternalQuery} register. Note, this is public and static variable.
	 */
	protected static RegisterTable externalQueryReg = new RegisterTable();

	
	/**
	 * {@link CTSManager} register. Note, this is public and static variable.
	 */
	protected static RegisterTable ctsmReg = new RegisterTable();

	
	/**
	 * List of next-update algorithms. Next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * Currently, next-update algorithms are not used. Note, this is public and static variable.
	 */
	protected static AlgList nextUpdateList = new AlgList();
	
	
	/**
	 * This static method gets normal algorithm register.
	 * @return register table of {@link Alg}.
	 */
	public final static RegisterTable getNormalAlgReg() {
		return normalAlgReg;
	}
	
	
	/**
	 * This static method gets {@link DatasetParser} register.
	 * @return register table of {@link DatasetParser}.
	 */
	public final static RegisterTable getParserReg() {
		return parserReg;
	}

	
	/**
	 * This static method gets {@link Metric} register.
	 * @return {@link Metric} register.
	 */
	public final static RegisterTable getMetricReg() {
		return metricReg;
	}

	
	/**
	 * This static method gets {@link ExternalQuery} register.
	 * @return {@link ExternalQuery} register.
	 */
	public final static RegisterTable getExternalQueryReg() {
		return externalQueryReg;
	}

	
	/**
	 * This static method gets {@link CTSManager} register.
	 * @return {@link CTSManager} register.
	 */
	public final static RegisterTable getCTSManagerReg() {
		return ctsmReg;
	}

	
	/**
	 * This static method gets list of next-update algorithms.
	 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * Currently, next-update algorithms are not used.
	 * @return list of next-update algorithms.
	 */
	public final static AlgList getNextUpdateList() {
		return nextUpdateList;
	}

	
	/**
	 * This static method clears this storage, which means that all register tables become empty.
	 */
	public final static void clear() {
		try {
			synchronized (NORMAL_ALG) {
				RegisterTableList tableList = getRegisterTableList();
				for (int i = 0; i < tableList.size(); i++) {
					RegisterTable registeredTable = tableList.get(i).getRegisterTable();
					List<Alg> algList = registeredTable.getAlgList();
					for (Alg alg : algList) {
						try {
							if (alg instanceof Exportable)
								((Exportable)alg).unexport();
						}
						catch (Throwable e) {LogUtil.trace(e);}
					}
					
					registeredTable.clear();
				}
				
				for (int i = 0; i < nextUpdateList.size(); i++) {
					Alg alg = nextUpdateList.get(i);
					try {
						if (alg instanceof Exportable)
							((Exportable)alg).unexport();
					}
					catch (Throwable e) {LogUtil.trace(e);}
				}
				nextUpdateList.clear();
			}
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * This static method gets all register tables as a {@link RegisterTableList}.
	 * @return all register tables as a {@link RegisterTableList}.
	 */
	public final static RegisterTableList getRegisterTableList() {
		return new RegisterTableList(
				new RegisterTableList.RegisterTableItem(NORMAL_ALG, normalAlgReg), 
				new RegisterTableList.RegisterTableItem(PARSER, parserReg), 
				new RegisterTableList.RegisterTableItem(METRIC, metricReg), 
				new RegisterTableList.RegisterTableItem(EXTERNAL_QUERY, externalQueryReg), 
				new RegisterTableList.RegisterTableItem(CTS_MANAGER, ctsmReg)); 
	}
	
	
	/**
	 * Testing whether the specified algorithm is normal algorithm.
	 * @param alg specified algorithm.
	 * @return whether the specified algorithm is normal algorithm.
	 */
	public final static boolean isNormalAlg(Alg alg) {
		if (alg == null)
			return false;
		else if (alg instanceof DatasetParser)
			return false;
		else if (alg instanceof Metric)
			return false;
		else if (alg instanceof ExternalQuery)
			return false;
		else if (alg instanceof CTSManager)
			return false;
		else
			return true;
	}
	
	
	/**
	 * Testing whether the specified algorithm class is class of normal algorithm.
	 * @param algClass specified algorithm class.
	 * @return whether the specified algorithm class is class of normal algorithm.
	 */
	public final static boolean isNormalAlg(Class<? extends Alg> algClass) {
		if (DatasetParser.class.isAssignableFrom(algClass))
			return false;
		else if (Metric.class.isAssignableFrom(algClass))
			return false;
		else if (ExternalQuery.class.isAssignableFrom(algClass))
			return false;
		else if (CTSManager.class.isAssignableFrom(algClass))
			return false;
		else
			return true;
	}

	
	/**
	 * Looking up class.
	 * @param tableName specified table name.
	 * @return the class corresponding with table name.
	 */
	public final static Class<? extends Alg> lookupClass(String tableName) {
		if (tableName == null)
			return null;
		else if (tableName.equals(NORMAL_ALG))
			return Alg.class;
		else if (tableName.equals(PARSER))
			return DatasetParser.class;
		else if (tableName.equals(METRIC))
			return Metric.class;
		else if (tableName.equals(EXTERNAL_QUERY))
			return ExternalQuery.class;
		else if (tableName.equals(CTS_MANAGER))
			return CTSManager.class;
		else
			return null;
	}
	
	
	/**
	 * Looking up the register table whose algorithms have the class which is equal to specified algorithm class.
	 * @param algClass specified algorithm class.
	 * @return {@link RegisterTable} whose algorithms have the class which is equal to specified algorithm class.
	 */
	public final static RegisterTable lookupTable(Class<? extends Alg> algClass) {
		if (DatasetParser.class.isAssignableFrom(algClass))
			return parserReg;
		else if (Metric.class.isAssignableFrom(algClass))
			return metricReg;
		else if (ExternalQuery.class.isAssignableFrom(algClass))
			return externalQueryReg;
		else if (CTSManager.class.isAssignableFrom(algClass))
			return ctsmReg;
		else
			return normalAlgReg;
	}
	
	
	/**
	 * Looking up the register table whose table name is equal to specified name
	 * @param tableName specified table name. This is the name implying the type of algorithm.
	 * <ul>
	 * <li>Register table of {@link Alg} (s) has name specified by the constant {@link #NORMAL_ALG}.</li>
	 * <li>Register table of {@link DatasetParser} (s) has name specified by the constant {@link #PARSER}.</li>
	 * <li>Register table of {@link Metric} (s) has name specified by the constant {@link #METRIC}.</li>
	 * <li>Register table of {@link ExternalQuery} (s) has name specified by the constant {@link #EXTERNAL_QUERY}.</li>
	 * <li>Register table of {@link CTSManager} (s) has name specified by the constant {@link #CTS_MANAGER}.</li>
	 * </ul>
	 * @return {@link RegisterTable} whose name is equal to specified table name.
	 */
	public final static RegisterTable lookupTable(String tableName) {
		if (tableName == null)
			return null;
		else if (tableName.equals(NORMAL_ALG))
			return normalAlgReg;
		else if (tableName.equals(PARSER))
			return parserReg;
		else if (tableName.equals(METRIC))
			return metricReg;
		else if (tableName.equals(EXTERNAL_QUERY))
			return externalQueryReg;
		else if (tableName.equals(CTS_MANAGER))
			return ctsmReg;
		else
			return null;
	}
	
	
	/**
	 * Looking up the table name of specified class of algorithm.
	 * @param algClass specified class of algorithm.
	 * @return type name of specified class of algorithm.
	 * <ul>
	 * <li>Class of {@link Alg} (s) has type name specified by the constant {@link #NORMAL_ALG}.</li>
	 * <li>Class of {@link DatasetParser} (s) has type name specified by the constant {@link #PARSER}.</li>
	 * <li>Class of {@link Metric} (s) has type name specified by the constant {@link #METRIC}.</li>
	 * <li>Class of {@link ExternalQuery} (s) has type name specified by the constant {@link #EXTERNAL_QUERY}.</li>
	 * <li>Class of {@link CTSManager} (s) has type name specified by the constant {@link #CTS_MANAGER}.</li>
	 * </ul>
	 */
	public final static String lookupTableName(Class<? extends Alg> algClass) {
		if (DatasetParser.class.isAssignableFrom(algClass))
			return PARSER;
		else if (Metric.class.isAssignableFrom(algClass))
			return METRIC;
		else if (ExternalQuery.class.isAssignableFrom(algClass))
			return EXTERNAL_QUERY;
		else if (CTSManager.class.isAssignableFrom(algClass))
			return CTS_MANAGER;
		else
			return NORMAL_ALG;
	
	}
	
	
	/**
	 * Checking whether the specified algorithm class and algorithm name stored in this plug-in storage.
	 * @param algClass specified algorithm class.
	 * @param algName specified algorithm name.
	 * @return whether the specified algorithm class and algorithm name stored in this plug-in storage.
	 */
	public final static boolean contains(Class<? extends Alg> algClass, String algName) {
		RegisterTable table = lookupTable(algClass);
		if (table != null && table.contains(algName))
			return true;
		else
			return lookupNextUpdateList(algClass, algName) != -1;
	}
	
	
	/**
	 * Checking whether the specified algorithm stored in this plug-in storage.
	 * @param algs specified algorithm.
	 * @return whether the specified algorithm stored in this plug-in storage.
	 */
	public final static boolean contains(Alg alg) {
		if (alg == null) return false;
		
		RegisterTable table = lookupTable(alg.getClass());
		if (table != null && table.contains(alg.getName()))
			return true;
		else
			return lookupNextUpdateList(alg) != -1;
	}

		
	/**
	 * Looking whether the specified algorithm class and algorithm name stored in next update list.
	 * @param algClass specified algorithm class.
	 * @param algName specified algorithm name.
	 * @return the index of the specified algorithm class and algorithm name stored in next update list.
	 * Return -1 if not found.
	 */
	public final static int lookupNextUpdateList(Alg alg) {
		if (alg == null) return -1;
		
		for (int i = 0; i < nextUpdateList.size(); i++) {
			int idx = nextUpdateList.indexOf(alg.getName());
			if (idx == -1) continue;
			
			Class<? extends Alg> cls = nextUpdateList.get(idx).getClass();
			if (alg.getClass().equals(cls)) return idx;
		}
		
		return -1;
	}
	
	
	/**
	 * Looking whether the specified algorithm class and algorithm name stored in next update list by possible assignment.
	 * @param algClass specified algorithm class.
	 * @param algName specified algorithm name.
	 * @return the index of the specified algorithm class and algorithm name stored in next update list by possible assignment.
	 * Return -1 if not found.
	 */
	public final static int lookupNextUpdateList(Class<? extends Alg> algClass, String algName) {
		if (algClass == null || algName == null) return -1;
		
		for (int i = 0; i < nextUpdateList.size(); i++) {
			Alg alg = nextUpdateList.get(i); 
			if (!alg.getName().equals(algName)) continue;
			
			String tableName1 = PluginStorage.lookupTableName(algClass);
			String tableName2 = PluginStorage.lookupTableName(alg.getClass()); 
			if (tableName1 != null && tableName2 != null && tableName1.equals(tableName2))
				return i;
		}

		return -1;
	}
	
	
	/**
	 * Looking whether the specified algorithm class stored in next update list by possible assignment.
	 * @param algClass specified algorithm class.
	 * @return array of algorithms whose classed are specified.
	 * Return empty if not found.
	 */
	public final static List<Alg> lookupNextUpdateList(Class<? extends Alg> algClass) {
		List<Alg> algs = Util.newList();
		
		for (int i = 0; i < nextUpdateList.size(); i++) {
			Alg alg = nextUpdateList.get(i);
			
			String tableName1 = PluginStorage.lookupTableName(algClass);
			String tableName2 = PluginStorage.lookupTableName(alg.getClass()); 
			if (tableName1 != null && tableName2 != null && tableName1.equals(tableName2))
				algs.add(alg);
		}
		
		return algs;
	}

	
	/**
	 * Synchronizing plug-in storage with evaluator.
	 * @param evaluator specified evaluator.
	 * @param algClass specified algorithm class.
	 * @param remote true if evaluator is remote.
	 */
	public static void syncWithEvaluator(Evaluator evaluator, Class<? extends Alg> algClass, boolean remote) {
		List<String> algEvNames = updateFromEvaluator(evaluator, algClass, remote);
		RegisterTable algReg = lookupTable(algClass);
		if (algReg == null) return; 
		
		List<String> algNames = algReg.getAlgNames();
		algNames.removeAll(algEvNames);
		for (String algName : algNames) {
			Alg alg = algReg.query(algName);
			if (alg instanceof Exportable) {
				try {
					((Exportable)alg).unexport(); //Finalize method will call unsetup method if unsetup method exists in this algorithm.
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			
			algReg.unregister(algName);
		}
		
		List<Alg> nextUpdateAlgs = lookupNextUpdateList(algClass);
		for (Alg nextUpdateAlg : nextUpdateAlgs) {
			if (!algEvNames.contains(nextUpdateAlg.getName())) {
				if (nextUpdateAlg instanceof Exportable) {
					try {
						((Exportable)nextUpdateAlg).unexport(); //Finalize method will call unsetup method if unsetup method exists in this algorithm.
					} catch (Throwable e) {LogUtil.trace(e);}
				}
				
				nextUpdateList.remove(nextUpdateAlg);
			}
		}
	}
	
	
	/**
	 * Updating plug-in storage from evaluator. Only update new algorithms from evaluator.
	 * @param evaluator specified evaluator.
	 * @param algClass specified algorithm class.
	 * @param remote true if evaluator is remote.
	 * @return evaluated algorithm names of evaluator.
	 */
	public static List<String> updateFromEvaluator(Evaluator evaluator, Class<? extends Alg> algClass, boolean remote) {
		RegisterTable algReg = lookupTable(algClass);
		List<String> algEvNames = Util.newList();
		if (algReg == null) return algEvNames; 

		try {
			algEvNames = evaluator.getPluginAlgNames(algClass);
		} catch (Exception e) {LogUtil.trace(e);}
		
		for (String algEvName : algEvNames) {
			if (algReg.contains(algEvName)) continue;
			
			int idx = lookupNextUpdateList(algClass, algEvName);
			if (idx != -1) {
				Alg alg = nextUpdateList.get(idx);
				nextUpdateList.remove(idx);
				algReg.register(alg);
			}
			else {
				Alg alg = null;
				try {
					alg = evaluator.getPluginAlg(algClass, algEvName, remote);
				}
				catch (Exception e) {
					System.out.println("Retrieving remote algorithm causes error by " + e.getMessage());
					alg = null;
				}
				if (alg != null) algReg.register(alg);
			}
		}
		
		return algEvNames;
	}

	
//	/**
//	 * Testing whether the specified name contains in registered tables.
//	 * Using this method is careful because different registered tables can have same algorithm names.
//	 * @param algName specified name.
//	 * @return whether the specified name contains in registered tables.
//	 */
//	public final static boolean containsIncludeNextUpdate(String algName) {
//		return parserReg.contains(algName)
//				|| metricReg.contains(algName)
//				|| externalQueryReg.contains(algName)
//				|| ctsmReg.contains(algName)
//				|| normalAlgReg.contains(algName)
//				|| nextUpdateList.indexOf(algName) >= 0;
//	}
//	
//	
//	/**
//	 * Querying registered algorithm via name over all registered tables.
//	 * Using this method is careful because different registered tables can have same algorithm names.
//	 * @param algName specified name.
//	 * @return registered algorithm having specified name over all registered tables.
//	 */
//	public static Alg query(String algName) {
//		if (parserReg.contains(algName))
//			return parserReg.query(algName);
//		else if (metricReg.contains(algName))
//			return metricReg.query(algName);
//		else if (externalQueryReg.contains(algName))
//			return externalQueryReg.query(algName);
//		else if (ctsmReg.contains(algName))
//			return ctsmReg.query(algName);
//		else if (normalAlgReg.contains(algName))
//			return normalAlgReg.query(algName);
//		else
//			return null;
//	}
//
//	
//	/**
//	 * Return an array of names of registered algorithms.
//	 * Using this method is careful because different registered tables can have same algorithm names.
//	 * @return array of names of registered algorithms.
//	 */
//	public static List<String> getAlgNames() {
//		Set<String> namesSet = Util.newSet();
//		namesSet.addAll(parserReg.getAlgNames());
//		namesSet.addAll(metricReg.getAlgNames());
//		namesSet.addAll(externalQueryReg.getAlgNames());
//		namesSet.addAll(ctsmReg.getAlgNames());
//		namesSet.addAll(normalAlgReg.getAlgNames());
//		
//		List<String> namesList = Util.newList(namesSet.size());
//		namesList.addAll(namesSet);
//		return namesList;
//	}

	
	/**
	 * Adding shutdown hook to release all registered algorithms.
	 */
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				//This code line is not redundant. Please concern the keyword synchronized in releaseAllRegisteredAlgs().
				clear();
			}
			
		});
		
	}
	
	
}
