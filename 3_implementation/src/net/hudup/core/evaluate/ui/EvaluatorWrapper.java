package net.hudup.core.evaluate.ui;

import java.awt.Component;

import javax.swing.JOptionPane;

import net.hudup.core.client.ConnectInfo;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.evaluate.ui.EvalCompoundGUI;

/**
 * This class is a wrapper of evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public class EvaluatorWrapper {
	
	
	/**
	 * Internal evaluator.
	 */
	public Evaluator evaluator = null;
	
	
	/**
	 * Item name.
	 */
	private String name = null;
	
	
	/**
	 * Evaluation GUI data.
	 */
	public EvaluateGUIData guiData = null;
	
	
	/**
	 * Flag to indicate whether listener was added.
	 */
	public boolean addedListener = false;
	
	
	/**
	 * Constructor with evaluator.
	 * @param evaluator evaluator.
	 */
	public EvaluatorWrapper(Evaluator evaluator) {
		this.evaluator = evaluator;
		this.name = "";
		if (evaluator != null) {
			try {
				name = evaluator.getVersionName();
			}
			catch (Exception e) {
				LogUtil.trace(e);
				name = "";
			}
		}
	}

	
	/**
	 * Getting item name.
	 * @return item name.
	 */
	public String getName() {
		return name;
	}
	
	
	@Override
	public String toString() {
		return DSUtil.shortenVerbalName(getName());
	}
	
	
	/**
	 * Open evaluator.
	 * @param evaluatorItem specified evaluator item.
	 * @param connectInfo connection information.
	 * @param parent parent component.
	 */
	public static void openEvaluator(EvaluatorWrapper evaluatorItem, ConnectInfo connectInfo, Component parent) {
		if (evaluatorItem == null || evaluatorItem.evaluator == null) {
			JOptionPane.showMessageDialog(parent, "Cannot open evaluator", "Cannot open evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (EvaluatorAbstract.isPullModeRequired(evaluatorItem.evaluator) && !connectInfo.pullMode) {
			JOptionPane.showMessageDialog(parent,
				"Can't retrieve evaluator because PULL MODE is not set\n" +
				"whereas the remote evaluator requires PULL MODE.\n" +
				"You have to check PULL MODE in connection dialog.",
				"Retrieval to evaluator failed", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (evaluatorItem.guiData == null) evaluatorItem.guiData = new EvaluateGUIData();
		
		if (evaluatorItem.guiData.active) {
			JOptionPane.showMessageDialog(
				parent, 
				"GUI of evaluator named '" + DSUtil.shortenVerbalName(evaluatorItem.getName()) + "' is running.", 
				"Evaluator GUI running", 
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		try {
			EvalCompoundGUI.class.getClass();
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(
					parent, 
					"Cannot open evaluator control panel due to lack of evaluate package", 
					"lack of evaluate package", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		EvalCompoundGUI.run(evaluatorItem.evaluator, connectInfo, evaluatorItem.guiData, null);
	}

	
}


