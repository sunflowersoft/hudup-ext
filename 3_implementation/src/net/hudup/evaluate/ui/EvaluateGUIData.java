/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate.ui;

import java.io.Serializable;

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
	public boolean wasGUIRun = false;
	

	/**
	 * Dataset pool.
	 */
	public DatasetPool pool = null;

	
	/**
	 * Text field to show place of saving running information.
	 */
	public String txtRunSaveBrowse = null;

	
	/**
	 * Check box variable.
	 */
	public boolean chkVerbal = false;


	/**
	 * Default constructor.
	 */
	public EvaluateGUIData() {
		
	}
	
	
	/**
	 * Extracting GUI data.
	 * @param gui batch evaluator GUI.
	 */
	public void extractFrom(BatchEvaluateGUI gui) {
		this.pool = gui.pool;
		this.txtRunSaveBrowse = gui.chkRunSave.isSelected() ? gui.txtRunSaveBrowse.getText() : null;
		this.chkVerbal = gui.chkVerbal.isSelected();
	}


	/**
	 * Extracting GUI data.
	 * @param gui evaluator GUI.
	 */
	public void extractFrom(EvaluateGUI gui) {
		this.pool = gui.pool;
		this.txtRunSaveBrowse = gui.chkRunSave.isSelected() ? gui.txtRunSaveBrowse.getText() : null;
		this.chkVerbal = gui.chkVerbal.isSelected();
	}


}

