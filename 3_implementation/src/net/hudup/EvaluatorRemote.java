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
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new EvaluatorRemote().run(args);
	}

	
	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		EvalCompoundGUI.switchRemoteEvaluator(Constants.DEFAULT_EVALUATOR_NAME, null);
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Remote Evaluator";
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}
	
	
}
