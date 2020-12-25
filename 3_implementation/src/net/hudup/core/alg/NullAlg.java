/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.BaseClass;

/**
 * This class represents a null algorithm which refers to any algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //It is not base class. The notation BaseClass is used to prevent from automatic registering.
public class NullAlg extends AlgAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
    /**
     * Algorithm name.
     */
    protected String name = "null";
    
    
    /**
     * Referred algorithm class name.
     */
    protected String referredAlgClassName = null;
    
    
    /**
	 * Default constructor.
	 */
	public NullAlg() {

	}

	
	/**
	 * Constructor with name and configuration.
	 * @param name algorithm name.
	 * @param config configuration.
	 */
	public NullAlg(String name, DataConfig config) {
		this.name = name;
		this.config = config;
	}

	
	/**
	 * Constructor with specified algorithm.
	 * @param alg specified algorithm.
	 */
	public NullAlg(Alg alg) {
		this.name = alg.getName();
		this.config.putAll(alg.getConfig());
		this.referredAlgClassName = alg.getClass().getName();
	}

	
	/**
	 * Constructor with specified algorithm description.
	 * @param algDesc specified algorithm description.
	 */
	public NullAlg(AlgDesc2 algDesc) {
		this.name = algDesc.algName;
		this.config.putAll(algDesc.config);
		this.referredAlgClassName = algDesc.algClassName;
	}
	
	
	@Override
	public String getName() {
		return name;
	}

	
	/**
	 * Getting referred algorithm class name.
	 * @return referred algorithm class name.
	 */
	public String getReferredAlgClassName() {
		return referredAlgClassName;
	}
	
	
	@Override
	public String getDescription() throws RemoteException {
		return "Null algorithm referring to the algorithm " + getName();
	}


	@Override
	public Alg newInstance() {
		return new NullAlg(this.name, (DataConfig)this.config.clone());
	}

	
}
