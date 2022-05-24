package net.hudup.core.evaluate;

import java.io.Serializable;

/**
 * This class contains persistent values for evaluation.
 * @author hp
 *
 */
public class EvaluateInfoPersit implements Cloneable, Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Referred pool result flag.
	 */
	public boolean isRefPoolResult = false;
	
	
	/**
	 * Name of referred pool result.
	 */
	public String refPoolResultName = null;
	
	
	/**
	 * Default constructor.
	 */
	public EvaluateInfoPersit() {

	}
	

}
