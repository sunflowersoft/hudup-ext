/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.util.Collection;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;

/**
 * This class represents description of an algorithm. In current version, it includes algorithm class name and configuration of such algorithm.
 * Please distinguish algorithm name and algorithm class name. The algorithm name is the string returned by {@link Alg#getName()} while the algorithm class name is class name of the class that implements interface {@link Alg}. 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class AlgDesc implements Serializable, net.hudup.core.Cloneable {

	
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
		 * Unknown algorithm.
		 */
		unknown
	}

	
	/**
	 * Algorithm name.
	 */
	protected String algClassName = null;
	
	
	/**
	 * Configuration of the referred algorithm.
	 */
	protected DataConfig config = null;
	
	
	/**
	 * Default constructor
	 */
	public AlgDesc() {
		
	}
	
	
	/**
	 * Constructor with algorithm class name and algorithm configuration.
	 * 
	 * @param algClassName class name of algorithm. 
	 * @param config configuration of algorithm.
	 */
	public AlgDesc(String algClassName, DataConfig config) {
		this.algClassName = algClassName;
		this.config = config;
	}
	
	
	/**
	 * Constructor with specified algorithm. This construct extracts the class name and configuration of specified algorithm.
	 * @param alg specified algorithm
	 */
	public AlgDesc(Alg alg) {
		this(alg.getClass().getName(), alg.getConfig());
	}

	
	/**
	 * Getting algorithm class name.
	 * @return algorithm class name
	 */
	public String getAlgClassName() {
		return algClassName;
	}
	
	
	/**
	 * Setting algorithm class name.
	 * @param algClassName specified algorithm class name.
	 */
	public void setAlgClassName(String algClassName) {
		this.algClassName = algClassName;
	}
	
	
	/**
	 * Getting algorithm configuration as {@link DataConfig}.
	 * @return algorithm configuration
	 */
	public DataConfig getConfig() {
		return config;
	}
	
	
	/**
	 * Setting algorithm configuration via the specified {@link DataConfig}.
	 * @param config Specified configuration {@link DataConfig}
	 */
	public void setConfig(DataConfig config) {
		this.config = config;
	}
	
	
	/**
	 * Creating algorithm from its description.
	 * @return Created algorithm as an instance of {@link Alg}
	 */
	public Alg createAlg() {
		Alg alg = (Alg) Util.newInstance(algClassName);
		if (alg != null)
			alg.getConfig().putAll(config);
		
		return alg;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		
		return new AlgDesc(algClassName.toString(), (DataConfig) config.clone());
	}
	
	
	/**
	 * Getting methodological type of given algorithm.
	 * @param alg given algorithm.
	 * @return methodological type of given algorithm.
	 */
	public static MethodType methodTypeOf(Alg alg) {
		if (alg instanceof MemoryBasedAlg)
			return MethodType.memorybased;
		else if (alg instanceof ModelBasedAlg)
			return MethodType.modelbased;
		else if (alg instanceof CompositeAlg)
			return MethodType.composite;
		else if (alg instanceof ServiceAlg)
			return MethodType.service;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if (remoteAlg instanceof Alg)
				return methodTypeOf((Alg)remoteAlg);
			else if (remoteAlg instanceof MemoryBasedAlgRemote)
				return MethodType.memorybased;
			else if (remoteAlg instanceof ModelBasedAlgRemote)
				return MethodType.modelbased;
			else if (remoteAlg instanceof CompositeAlgRemote)
				return MethodType.composite;
			else if (remoteAlg instanceof ServiceAlg)
				return MethodType.service;
			else
				return MethodType.unknown;
		}
		else
			return MethodType.unknown;
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
		if (alg instanceof Recommender)
			return FunctionType.recommend;
		else if (alg instanceof ExecutableAlg)
			return FunctionType.execute;
		else if (alg instanceof AlgRemoteWrapper) {
			AlgRemote remoteAlg = ((AlgRemoteWrapper)alg).getRemoteAlg();
			if (remoteAlg instanceof Alg)
				return functionTypeOf((Alg)remoteAlg);
			else if (remoteAlg instanceof RecommenderRemote)
				return FunctionType.recommend;
			else if (remoteAlg instanceof ExecutableAlgRemote)
				return FunctionType.execute;
			else
				return FunctionType.unknown;
		}
		else
			return FunctionType.unknown;
	}

	
	/**
	 * Getting functional type of given algorithms.
	 * @param algs given algorithms.
	 * @return functional type of given algorithms.
	 */
	public static FunctionType functionTypeOf(Collection<Alg> algs) {
		if (algs == null || algs.size() == 0)
			return FunctionType.unknown;
		
		boolean recommend = true, execute = true;
		for (Alg alg : algs) {
			FunctionType type = functionTypeOf(alg);
			if (type == FunctionType.recommend) {
				recommend = recommend && true;
				execute = execute && false;
			}
			else if (type == FunctionType.execute) {
				recommend = recommend && false;
				execute = execute && true;
			}
			else {
				recommend = recommend && false;
				execute = execute && false;
			}
		}
		
		if (recommend)
			return FunctionType.recommend;
		else if (execute)
			return FunctionType.execute;
		else
			return FunctionType.unknown;
	}
	

}
