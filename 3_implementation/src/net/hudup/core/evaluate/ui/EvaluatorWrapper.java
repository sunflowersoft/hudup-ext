package net.hudup.core.evaluate.ui;

import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;

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
	 * Configuration.
	 */
	private EvaluatorConfig tempConfig = null;
	
	
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
		this.tempConfig = new EvaluatorConfig();
		if (evaluator != null) {
			try {
				this.name = evaluator.getVersionName();
				this.tempConfig = evaluator.getConfig();
			}
			catch (Exception e) {
				LogUtil.trace(e);
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
	
	
	/**
	 * Getting temporal configuration.
	 * @return temporal configuration.
	 */
	public EvaluatorConfig getTempConfig() {
		return tempConfig;
	}
	
	
	@Override
	public String toString() {
		return DSUtil.shortenVerbalName(getName());
	}
	
	
}


