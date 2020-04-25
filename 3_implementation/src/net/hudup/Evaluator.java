/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup;

import java.io.Reader;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import net.hudup.core.AccessPoint;
import net.hudup.core.Constants;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.BatchScript;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.evaluate.EvaluateEvent;
import net.hudup.core.evaluate.EvaluateEvent.Type;
import net.hudup.core.evaluate.EvaluateListener;
import net.hudup.core.evaluate.EvaluateProcessor;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
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
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		new Evaluator().run(args);
	}


	@Override
	public void run(String[] args) {
		LogUtil.info("Sytax: java net.hudup.Evaluator \"EvaluatorName\" \"batch.script\"");
		Util.getPluginManager().fire();
		
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
			ev = null;
		}
		if (ev == null) {
			List<net.hudup.core.evaluate.Evaluator> tempEvList = Util.getPluginManager().loadInstances(net.hudup.core.evaluate.Evaluator.class);
			for (net.hudup.core.evaluate.Evaluator tempEv : tempEvList) {
				try {
					if (tempEv.getName().equals(args[0])) {
						ev = tempEv;
						break;
					}
				}
				catch (Throwable e) {
					ev = null;
				}
			}
		}
		if (ev == null) {
			LogUtil.error("Not evaluator class name or evaluator name");
			return;
		}
		
		if (args.length < 2) {
			EvalCompoundGUI.run(ev, null, null, null);
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
				LogUtil.error("Invalid path of batch script");
				return;
			}
			
			List<Alg> algList = PluginStorage.getNormalAlgReg().getAlgList(script.getAlgNameList());
			if (algList.size() == 0) {
				LogUtil.error("Algorithms in batch script are not suitable to this evaluator");
				return;
			}
			
			Date date = new Date();
			xURI workingDir = xURI.create(Constants.WORKING_DIRECTORY);
			final UriAdapter workingAdapter = new UriAdapter(workingDir);
			xURI analyzeDir = workingDir.concat("" + date.getTime());
			if (!workingAdapter.exists(analyzeDir))
				workingAdapter.create(analyzeDir, true);
			
			final net.hudup.core.evaluate.Evaluator finalEv = ev;
			finalEv.addEvaluateListener(new EvaluateListener() {
				
				@Override
				public void receivedEvaluation(EvaluateEvent evt) throws RemoteException {
					// TODO Auto-generated method stub
					if (evt.getType() != Type.done && evt.getType() != Type.done_one)
						return;
					
					try {
						MetricsUtil util = new MetricsUtil(finalEv.getResult(), new RegisterTable(algList), finalEv);
						util.createExcel(analyzeDir.concat(EvaluateProcessor.METRICS_ANALYZE_EXCEL_FILE_NAME));
						
						Writer writer = workingAdapter.getWriter(analyzeDir.concat(EvaluateProcessor.METRICS_ANALYZE_EXCEL_FILE_NAME2), false);
						writer.write(util.createPlainText());
						writer.close();
						
						if (evt.getType() == Type.done) {
							workingAdapter.close();
							
							long endTime = System.currentTimeMillis();
							double elapsedHours = (double)(endTime - beginTime) / 1000.0 / 60.0 / 60.0;
							LogUtil.info("Evaluation finished successfully in " + MathUtil.format(elapsedHours) + " hours.");
							LogUtil.info("Analyzed result was stored in \"" + analyzeDir.toString() + "\"");
						}
					}
					catch (Exception e) {
						LogUtil.trace(e);
					}
				}
				
				@Override
				public boolean ping() throws RemoteException {
					return true;
				}

			});
			
			DatasetPool pool = script.getPool();
			LogUtil.info("Evaluation is running...");
			ev.remoteStart0(algList, pool.toDatasetPoolExchangedClient(), null, beginTime);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
	}


	@Override
	public String getName() {
		return "Evaluator";
	}


	@Override
	public String toString() {
		return getName();
	}

	
}


