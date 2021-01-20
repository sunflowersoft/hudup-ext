/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.util.Collection;

import net.hudup.core.PluginManager;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.data.Exportable;
import net.hudup.core.logistic.ForTest;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class represents extended description of an algorithm.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AlgDesc2 extends AlgDesc {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Methodological type of algorithm.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public enum MethodType {
		
		/**
		 * Memory-based algorithm.
		 */
		memorybased,
		
		/**
		 * Model-based algorithm.
		 */
		modelbased,
		
		/**
		 * Composite algorithm.
		 */
		composite,
		
		/**
		 * Service algorithm is a service that points to another algorithm.
		 */
		service,
		
		/**
		 * Unknown algorithm.
		 */
		unknown
	}
	
	
	/**
	 * Functional type of algorithm.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public enum FunctionType {
		
		/**
		 * Recommendation algorithm.
		 */
		recommend,
		
		/**
		 * Executable algorithm.
		 */
		execute,
		
		/**
		 * Non-executable algorithm.
		 */
		nonexecute,
		
		/**
		 * Unknown algorithm.
		 */
		unknown
	}

	
	/**
	 * Algorithm name.
	 */
	public String algName = null;
	
	
    /**
     * Referred algorithm class name.
     */
    protected String referredAlgClassName = null;
    
    
	/**
	 * Base remote interface names.
	 */
	public String[] baseRemoteInterfaceNames = new String[0];
	
	
	/**
	 * Methodological type.
	 */
	public MethodType methodType = MethodType.memorybased;
	
	
	/**
	 * Functional type.
	 */
	public FunctionType functionType = FunctionType.recommend;
	
	
	/**
	 * Flag to indicate whether algorithm is registered.
	 */
	public boolean registered = false;
	
	
	/**
	 * Flag to indicate whether algorithm is in next update list.
	 */
	public boolean inNextUpdateList = false;

	
	/**
	 * Potential registered table name.
	 */
	public String tableName = PluginStorage.NORMAL_ALG;
	
	
	/**
	 * Flag to indicate whether algorithm is exportable which implements interface {@link Exportable}.
	 */
	public boolean isExportable = false;
	
	
	/**
	 * Flag to indicate whether algorithm is exported. Only exportable algorithms can be exported.
	 */
	public boolean isExported = false;
	
	
	/**
	 * Flag to indicate whether algorithm is remote object.
	 */
	public boolean isRemote = false;
			

	/**
	 * Flag to indicate whether algorithm is a wrapper which inherits from {@link AlgRemoteWrapper}.
	 */
	public boolean isWrapper = false;
	
	
	/**
	 * Flag to indicate exclusive mode if algorithm is wrapper.
	 */
	public boolean isExclusive = false;
	
	
	/**
	 * Flag indicates whether algorithm is removed from plug-in storage.
	 */
	public boolean removed = false;
	
	
	/**
	 * Default constructor.
	 */
	public AlgDesc2() {

	}


	/**
	 * Constructor with specified algorithm.
	 * @param alg specified algorithm.
	 */
	public AlgDesc2(Alg alg) {
		super(alg);
		
		algName = alg.getName();
		
		if (alg instanceof NullAlg)
			referredAlgClassName = ((NullAlg)alg).getReferredAlgClassName();
		
		if (alg instanceof AlgRemote) {
			try {
				baseRemoteInterfaceNames = ((AlgRemote)alg).getBaseRemoteInterfaceNames();
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		baseRemoteInterfaceNames = baseRemoteInterfaceNames != null ? baseRemoteInterfaceNames : new String[0];
		
		methodType = methodTypeOf(alg);
		functionType = functionTypeOf(alg);
		registered = isRegistered(alg);
		inNextUpdateList = isInUpdateList(alg);
		tableName = lookupTableName(alg);
		isExportable = alg instanceof Exportable;
		
		try {
			isExported = isExportable && (((Exportable)alg).getExportedStub() != null);
		} catch (Throwable e) {
			LogUtil.trace(e);
			isExported = false;
		}
		
		isRemote = isRemote(alg);
		isWrapper = alg instanceof AlgRemoteWrapper;
		if (isWrapper)
			isExclusive = ((AlgRemoteWrapper)alg).isExclusive();
		
		removed = !PluginStorage.contains(alg);
	}

	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Algorithm name: " + algName + "\n");
		buffer.append("Class name: " + algClassName + "\n");
		if (referredAlgClassName != null) buffer.append("Referred class name: " + referredAlgClassName + "\n");
		buffer.append("Remote interfaces: " + TextParserUtil.toText(baseRemoteInterfaceNames, ",") + "\n");
		buffer.append("Methodological type: " + toString(methodType) + "\n");
		buffer.append("Functional type: " + toString(functionType) + "\n");
		buffer.append("Registered: " + registered + "\n");
		buffer.append("In next update list: " + inNextUpdateList + "\n");
		buffer.append("Table name (if registered): " + tableName + "\n");
		buffer.append("Exportable: " + isExportable + "\n");
		buffer.append("Exported: " + isExported + "\n");
		buffer.append("Remote: " + isRemote + "\n");
		buffer.append("Wrapper: " + isWrapper + "\n");
		buffer.append("Exclusive mode (for wrapper): " + isExclusive);
		
		return buffer.toString();
	}
	
	
	/**
	 * Getting methodological type of given algorithm.
	 * @param alg given algorithm.
	 * @return methodological type of given algorithm.
	 */
	public static MethodType methodTypeOf(Alg alg) {
		PluginManager pm = Util.getPluginManager();
		int type = pm.methodTypeOf(alg);
		switch (type) {
		case 0:
			return MethodType.memorybased;
		case 1:
			return MethodType.modelbased;
		case 2:
			return MethodType.composite;
		case 3:
			return MethodType.service;
		case -1:
			return MethodType.unknown;
		default:
			return MethodType.unknown;
		}
	}

	
	/**
	 * Getting text presentation of methodological type.
	 * @param methodType methodological type.
	 * @return text presentation of methodological type.
	 */
	public static String toString(MethodType methodType) {
		switch (methodType) {
		case memorybased:
			return "memory-based";
		case modelbased:
			return "model-based";
		case composite:
			return "composite";
		case service:
			return "service";
		case unknown:
			return "unknown";
		default:
			return "unknown";
		}
	}
	
	
	/**
	 * Getting methodological type of given algorithms.
	 * @param algs given algorithms.
	 * @return methodological type of given algorithms.
	 */
	public static MethodType methodTypeOf(Collection<Alg> algs) {
		if (algs == null || algs.size() == 0)
			return MethodType.unknown;
		
		boolean memorybased = true, modelbased = true, composite = true, service = true;
		for (Alg alg : algs) {
			MethodType type = methodTypeOf(alg);
			if (type == MethodType.memorybased) {
				memorybased = memorybased && true;
				modelbased = modelbased && false;
				composite = composite && false;
				service = service && false;
			}
			else if (type == MethodType.modelbased) {
				memorybased = memorybased && false;
				modelbased = modelbased && true;
				composite = composite && false;
				service = service && false;
			}
			else if (type == MethodType.composite) {
				memorybased = memorybased && false;
				modelbased = modelbased && false;
				composite = composite && true;
				service = service && false;
			}
			else if (type == MethodType.service) {
				memorybased = memorybased && false;
				modelbased = modelbased && false;
				composite = composite && false;
				service = service && true;
			}
			else {
				memorybased = memorybased && false;
				modelbased = modelbased && false;
				composite = composite && false;
				service = service && false;
			}
		}
		
		if (memorybased)
			return MethodType.memorybased;
		else if (modelbased)
			return MethodType.modelbased;
		else if (composite)
			return MethodType.composite;
		else if (service)
			return MethodType.service;
		else
			return MethodType.unknown;
	}
	

	/**
	 * Getting functional type of given algorithm.
	 * @param alg given algorithm.
	 * @return functional type of given algorithm.
	 */
	public static FunctionType functionTypeOf(Alg alg) {
		PluginManager pm = Util.getPluginManager();
		int type = pm.functionTypeOf(alg);
		switch (type) {
		case 0:
			return FunctionType.recommend;
		case 1:
			return FunctionType.execute;
		case 2:
			return FunctionType.nonexecute;
		case -1:
			return FunctionType.unknown;
		default:
			return FunctionType.unknown;
		}
	}
	
	
	/**
	 * Getting text presentation of methodological type.
	 * @param methodType methodological type.
	 * @return text presentation of methodological type.
	 */
	public static String toString(FunctionType methodType) {
		switch (methodType) {
		case recommend:
			return "recommend";
		case execute:
			return "execute";
		case nonexecute:
			return "nonexecute";
		case unknown:
			return "unknown";
		default:
			return "unknown";
		}
	}

	
	/**
	 * Getting functional type of given algorithms.
	 * @param algs given algorithms.
	 * @return functional type of given algorithms.
	 */
	public static FunctionType functionTypeOf(Collection<Alg> algs) {
		if (algs == null || algs.size() == 0)
			return FunctionType.unknown;
		
		boolean recommend = true, execute = true, nonexecute = true;
		for (Alg alg : algs) {
			FunctionType type = functionTypeOf(alg);
			if (type == FunctionType.recommend) {
				recommend = recommend && true;
				execute = execute && false;
				nonexecute = nonexecute && false;
			}
			else if (type == FunctionType.execute) {
				recommend = recommend && false;
				execute = execute && true;
				nonexecute = nonexecute && false;
			}
			else if (type == FunctionType.nonexecute) {
				recommend = recommend && false;
				execute = execute && false;
				nonexecute = nonexecute && true;
			}
			else {
				recommend = recommend && false;
				execute = execute && false;
				nonexecute = nonexecute && false;
			}
		}
		
		if (recommend)
			return FunctionType.recommend;
		else if (execute)
			return FunctionType.execute;
		else if (nonexecute)
			return FunctionType.nonexecute;
		else
			return FunctionType.unknown;
	}


	/**
	 * Looking up the table name of specified algorithm.
	 * @param alg specified algorithm.
	 * @return table name of specified algorithm.
	 */
	public static String lookupTableName(Alg alg) {
		return PluginStorage.lookupTableName(alg.getClass());
	}
	
	
	/**
	 * Checking whether the specified algorithm is remote object.
	 * @param alg specified algorithm.
	 * @return whether the specified algorithm is remote object.
	 */
	public static boolean isRemote(Alg alg) {
		if (alg == null)
			return false;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			return ((remoteAlg != null) && !(remoteAlg instanceof Alg));
		}
		else
			return false;
	}
	
	
	/**
	 * Getting remote algorithm of a given algorithm.
	 * @param alg given algorithm.
	 * @return remote algorithm of a given algorithm.
	 */
	public static AlgRemote getAlgRemote(Alg alg) {
		if (alg == null)
			return null;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if ((remoteAlg == null) || (remoteAlg instanceof Alg))
				return null;
			else
				return remoteAlg;
		}
		else
			return null;
	}
	
	
	/**
	 * Checking whether specified algorithm is registered.
	 * @param alg specified algorithm.
	 * @return whether specified algorithm is registered.
	 */
	public static boolean isRegistered(Alg alg) {
		RegisterTable table = PluginStorage.lookupTable(alg.getClass());
		if (table == null)
			return false;
		else
			return table.contains(alg.getName());
	}
	
	
	/**
	 * Checking whether specified algorithm is stored in next update list.
	 * @param alg specified algorithm.
	 * @return whether specified algorithm is stored in next update list.
	 */
	public static boolean isInUpdateList(Alg alg) {
		return PluginStorage.lookupNextUpdateList(alg) >= 0;
	}


	/**
	 * Wrapping (and instantiating if necessary) the specified algorithm.
	 * @param alg specified algorithm.
	 * @param exclusive exclusive mode.
	 * @return wrapper of specified algorithm.
	 */
	public static Alg wrapNewInstance(Alg alg, boolean exclusive) {
		if (alg == null)
			return null;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if (remoteAlg == null)
				return null;
			else if (remoteAlg instanceof Alg)
				return wrapNewInstance((Alg)remoteAlg, exclusive);
			else
				return Util.getPluginManager().wrap(remoteAlg, exclusive);
		}
		else
			return alg.newInstance();
	}


	/**
	 * Getting the most inner algorithm of the specified algorithm.
	 * @param alg specified algorithm.
	 * @return the most inner algorithm of the specified algorithm.
	 */
	public static Alg getMostInnerAlg(Alg alg) {
		if (alg == null)
			return null;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if (remoteAlg == null)
				return null;
			else if (remoteAlg instanceof Alg)
				return getMostInnerAlg((Alg)remoteAlg);
			else
				return null;
		}
		else
			return alg;
	}

	
	/**
	 * Getting the most inner algorithm or remote algorithm of the specified algorithm.
	 * @param alg specified algorithm.
	 * @return the most inner algorithm or remote algorithm of the specified algorithm.
	 */
	public static Object getMostInner(Alg alg) {
		if (alg == null)
			return null;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if (remoteAlg == null)
				return null;
			else if (remoteAlg instanceof Alg)
				return getMostInner((Alg)remoteAlg);
			else
				return remoteAlg;
		}
		else
			return alg;
	}

	
	/**
	 * Checking whether the specified algorithm is only for testing.
	 * @param alg specified algorithm
	 * @return whether the specified algorithm is only for testing.
	 */
	public static boolean isForTest(Alg alg) {
		if (alg == null) return false;
		
		Object obj = getMostInner(alg);
		if (obj == null)
			return false;
		else
			return obj instanceof ForTest;
	}

	
	/**
	 * Checking whether the specified algorithm class is only for testing.
	 * @param algClass specified algorithm class.
	 * @return whether the specified algorithm class is only for testing.
	 */
	public static boolean isForTest(Class<? extends Alg> algClass) {
		return ForTest.class.isAssignableFrom(algClass);
	}
	
	
	/**
	 * Check whether to call the specified algorithm remotely. There exists a reference to unexported object.
	 * @param alg specified algorithm.
	 * @return whether to call the specified algorithm remotely.
	 */
	public static boolean canCallRemote(Alg alg) {
		if (alg == null)
			return false;
		else if (alg instanceof AlgRemote) {
			String name = null;
			try {
				name = ((AlgRemote)alg).queryName();
			} catch (Throwable e) {}
			return name != null;
		}
		else
			return alg.getName() != null;
	}
	
	
	/**
	 * Checking whether the specified algorithm allows null training dataset.
	 * @param alg specified algorithm.
	 * @return whether the specified algorithm allows null training dataset.
	 */
	public static boolean isAllowNullTrainingSet(Alg alg) {
		if (alg == null) return false;
		
		Object obj = getMostInner(alg);
		if (obj == null)
			return false;
		else
			return obj instanceof AllowNullTrainingSet;
		
	}
	
	
}
