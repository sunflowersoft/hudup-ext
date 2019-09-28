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
import net.hudup.core.evaluate.Metric;
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
public final class PluginStorage implements Serializable {

	
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
	 * Flag to indicate whether plug-in storage is initialized.
	 */
	protected static boolean initialized = false;
	
	
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
		normalAlgReg.clear();
		parserReg.clear();
		metricReg.clear();
		externalQueryReg.clear();
		ctsmReg.clear();
		nextUpdateList.clear();
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
	 * Looking up the register table whose name is equal to specified name
	 * @param algTypeName specified name. This is the name implying the type of algorithm.
	 * <ul>
	 * <li>Register table of {@link Alg} (s) has name specified by the constant {@link #NORMAL_ALG}.</li>
	 * <li>Register table of {@link DatasetParser} (s) has name specified by the constant {@link #PARSER}.</li>
	 * <li>Register table of {@link Metric} (s) has name specified by the constant {@link #METRIC}.</li>
	 * <li>Register table of {@link ExternalQuery} (s) has name specified by the constant {@link #EXTERNAL_QUERY}.</li>
	 * <li>Register table of {@link CTSManager} (s) has name specified by the constant {@link #CTS_MANAGER}.</li>
	 * </ul>
	 * @return {@link RegisterTable} whose name is equal to specified name.
	 */
	public final static RegisterTable lookupTable(String algTypeName) {
		if (algTypeName.equals(NORMAL_ALG))
			return normalAlgReg;
		else if (algTypeName.equals(PARSER))
			return parserReg;
		else if (algTypeName.equals(METRIC))
			return metricReg;
		else if (algTypeName.equals(EXTERNAL_QUERY))
			return externalQueryReg;
		else if (algTypeName.equals(CTS_MANAGER))
			return ctsmReg;
		else
			return null;
	}
	
	
	/**
	 * Looking up the type name of specified class of algorithm.
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
	public final static String lookupAlgTypeName(Class<? extends Alg> algClass) {
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
	 * Releasing all registered algorithms.
	 */
	public final static void releaseAllRegisteredAlgs() {
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
						catch (Throwable e) {e.printStackTrace();}
					}
					
					registeredTable.clear();
				}
				
				for (int i = 0; i < nextUpdateList.size(); i++) {
					Alg alg = nextUpdateList.get(i);
					try {
						if (alg instanceof Exportable)
							((Exportable)alg).unexport();
					}
					catch (Throwable e) {e.printStackTrace();}
				}
				nextUpdateList.clear();
			}
		}
		catch (Throwable e) {e.printStackTrace();}
	}

	
	/**
	 * Checking whether plug-in storage is initialized.
	 * @return whether plug-in storage is initialized.
	 */
	public static boolean isInitialized() {
		return initialized;
	}
	
	
	/**
	 * Adding shutdown hook to release all registered algorithms.
	 */
	static {
		Runtime.getRuntime().addShutdownHook(new Thread() {

			@Override
			public void run() {
				//This code line is not redundant. Please concern the keyword synchronized in releaseAllRegisteredAlgs().
				releaseAllRegisteredAlgs();
			}
			
		});
		
	}
	
	
}
