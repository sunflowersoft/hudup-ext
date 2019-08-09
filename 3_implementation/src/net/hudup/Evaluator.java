package net.hudup;

import static net.hudup.core.Constants.ROOT_PACKAGE;

import java.io.Reader;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import net.hudup.core.AccessPoint;
import net.hudup.core.Constants;
import net.hudup.core.Firer;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.data.BatchScript;
import net.hudup.evaluate.ui.AbstractEvaluateGUI;
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
		System.out.println("Sytax: java net.hudup.Evaluator \"EvaluatorName\" \"batch.script\"");
		new Firer();
		
		if (args == null || args.length == 0) {
			EvalCompoundGUI.switchEvaluator(Constants.DEFAULT_EVALUATOR_NAME, null);
			return;
		}
		
		String evClassName = args[0];
		net.hudup.core.evaluate.Evaluator ev = null;
		try {
			ev = (net.hudup.core.evaluate.Evaluator)Class.forName(evClassName).newInstance();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			ev = null;
		}
		if (ev == null) {
			List<net.hudup.core.evaluate.Evaluator> tempEvList = SystemUtil.getInstances(ROOT_PACKAGE, net.hudup.core.evaluate.Evaluator.class);
			for (net.hudup.core.evaluate.Evaluator tempEv : tempEvList) {
				try {
					if (tempEv.getName().equals(args[0])) {
						ev = tempEv;
						break;
					}
				}
				catch (Throwable e) {
					// TODO Auto-generated catch block
					ev = null;
				}
			}
		}
		if (ev == null) {
			System.out.println("Not evaluator class name or evaluator name");
			return;
		}
		
		if (args.length < 2) {
			EvalCompoundGUI.run(ev, null, null);
			return;
		}
		
		final Long beginTime = System.currentTimeMillis();
		try {
			xURI uri = xURI.create(args[1]);
			UriAdapter adapter = new UriAdapter(uri);
			Reader reader = adapter.getReader(uri);
			BatchScript script = BatchScript.parse(reader, ev.getMainUnit());
			reader.close();
			adapter.close();
			if (script == null) {
				System.out.println("Invalid path of batch script");
				return;
			}
			
			List<Alg> algList = PluginStorage.getNormalAlgReg().getAlgList(script.getAlgNameList());
			if (algList.size() == 0) {
				System.out.println("Algorithms in batch script are not suitable to this evaluator");
				return;
			}
			
			Date date = new Date();
			xURI workingDir = xURI.create(Constants.WORKING_DIRECTORY);
			final UriAdapter workingAdapter = new UriAdapter(workingDir);
			xURI analyzeDir = workingDir.concat("" + date.getTime());
			if (!workingAdapter.exists(analyzeDir))
				workingAdapter.create(analyzeDir, true);
			
			final net.hudup.core.evaluate.Evaluator finalEv = ev;
			ev.addEvaluatorListener(new EvaluatorListener() {
				
				@Override
				public void receivedEvaluation(EvaluatorEvent evt) throws RemoteException {
					// TODO Auto-generated method stub
					if (evt.getType() != Type.done && evt.getType() != Type.done_one)
						return;
					
					try {
						MetricsUtil util = new MetricsUtil(finalEv.getResult(), new RegisterTable(algList));
						util.createExcel(analyzeDir.concat(AbstractEvaluateGUI.METRICS_ANALYZE_EXCEL_FILE_NAME));
						
						Writer writer = workingAdapter.getWriter(analyzeDir.concat(AbstractEvaluateGUI.METRICS_ANALYZE_EXCEL_FILE_NAME2), false);
						writer.write(util.createPlainText());
						writer.close();
						
						if (evt.getType() == Type.done) {
							workingAdapter.close();
							
							long endTime = System.currentTimeMillis();
							double elapsedHours = (double)(endTime - beginTime) / 1000.0 / 60.0 / 60.0;
							System.out.println("Evaluation finished successfully in " + MathUtil.format(elapsedHours) + " hours.");
							System.out.println("Analyzed result was stored in \"" + analyzeDir.toString() + "\"");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			
			DatasetPool pool = script.getPool();
			System.out.println("Evaluation is running...");
			ev.remoteStart(algList, pool, beginTime);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
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


