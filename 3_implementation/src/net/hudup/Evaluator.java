package net.hudup;

import org.apache.log4j.Logger;

import net.hudup.core.AccessPoint;
import net.hudup.core.Constants;
import net.hudup.core.Firer;
import net.hudup.evaluate.ui.EvalCompoundGUI;

/**
 * This class is a access point for evaluator.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Evaluator implements AccessPoint {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Evaluator.class);
	
	
	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new Evaluator().run(args);
	}


	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		new Firer();
		
		if (args != null && args.length > 0) {
			String evClassName = args[0];
			try {
				net.hudup.core.evaluate.Evaluator ev = (net.hudup.core.evaluate.Evaluator)Class.forName(evClassName).newInstance();
				EvalCompoundGUI.run(ev, null, null);
				return;
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		EvalCompoundGUI.switchEvaluator(Constants.DEFAULT_EVALUATOR_NAME, null);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Evaluator";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	
}


