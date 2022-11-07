/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.ui;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.data.DatasetPool;

/**
 * This class contains data fields of an evaluator GUI.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluateGUIData implements Serializable {
	
	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * If true, the GUI is being shown.
	 */
	public boolean active = false;
	
	
	/**
	 * Flag to indicate whether the GUI was run.
	 */
	public boolean wasRun = false;
	

	/**
	 * Algorithm name.
	 */
	public String algName = null;
	
	
	/**
	 * List of algorithm names.
	 */
	public List<String> algNames = null;

	
	/**
	 * Dataset pool.
	 */
	public DatasetPool pool = null;

	
	/**
	 * Flag to indicate referred pool.
	 */
	public boolean isRefPool = false;
	
	
	/**
	 * Referred pool name.
	 */
	public String refPoolName = null;
	
	
	/**
	 * Text field to show place of saving running information.
	 */
	public String txtRunSaveBrowse = null;

	
	/**
	 * Check box variable.
	 */
	public boolean chkVerbal = false;

	
	/**
	 * Resetting GUI data.
	 */
	public void reset() {
		active = false;
		wasRun = false;
		
		algName = null;
		algNames = null;
		pool = null;
		isRefPool = false;
		refPoolName = null;
		txtRunSaveBrowse = null;
		chkVerbal = false;
	}
	

}

