/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate.ui;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.evaluate.Metrics;

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
	 * Metrics as result of evaluation.
	 */
	public Metrics result = null;

	
	/**
	 * Table of algorithms.
	 */
	public RegisterTable algRegTable = new RegisterTable();
	
	
	/**
	 * Dataset pool.
	 */
	public DatasetPool pool = new DatasetPool();

	
	/**
	 * Algorithm list box.
	 */
	public List<Alg> lbAlgs = Util.newList();

	
	/**
	 * Text field to show place of saving running information.
	 */
	protected String txtRunSaveBrowse = "";

	
	/**
	 * Check box for whether or not to save running information.
	 */
	public boolean chkRunSave = false;
	
	
	/**
	 * Check box variable.
	 */
	public boolean chkVerbal = false;


	/**
	 * Running progress bar.
	 */
	public int[] prgRunning = new int[2];
	
	
	/**
	 * Status bar.
	 */
	public String[] statusBar = null;

	
	/**
	 * Waiting panel which is the alternative of testing result panel.
	 */
	public String paneWait = "";


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
		this.result = gui.result;
		this.algRegTable = gui.algRegTable;
		this.pool = gui.pool;
		this.lbAlgs = gui.lbAlgs.getAlgList();
		this.txtRunSaveBrowse = gui.txtRunSaveBrowse.getText();
		this.txtRunSaveBrowse = this.txtRunSaveBrowse != null ? this.txtRunSaveBrowse : "";
		this.chkRunSave = gui.chkRunSave.isSelected();
		this.chkVerbal = gui.chkVerbal.isSelected();
		this.prgRunning[0] = gui.prgRunning.getValue();
		this.prgRunning[1] = gui.prgRunning.getMaximum();
		this.statusBar = gui.statusBar.getTexts();
		this.paneWait = gui.paneWait.getWaitText();
	}


}

