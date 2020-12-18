/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup;

import net.hudup.core.AccessPoint;
import net.hudup.core.Constants;
import net.hudup.evaluate.ui.EvalCompoundGUI;


/**
 * This class is a access point for remote evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluatorRemote implements AccessPoint {
	
	
	/**
	 * Default constructor.
	 */
	public EvaluatorRemote() {

	}

	
	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		new EvaluatorRemote().run(args);
	}

	
	@Override
	public void run(String[] args) {
		EvalCompoundGUI.switchRemoteEvaluator(Constants.DEFAULT_EVALUATOR_NAME, null);
	}

	
	@Override
	public String getName() {
		return "Remote Evaluator";
	}

	
	@Override
	public String toString() {
		return getName();
	}
	
	
}
