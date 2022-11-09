/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Constants;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.evaluate.EvaluateEvent.Type;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.UriAdapter.AdapterWriteChannel;
import net.hudup.core.logistic.xURI;

/**
 * This class provides method to process evaluation results such as saving and parsing.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluateProcessor {

	
	/**
	 * Evaluation file extension.
	 */
	public final static String EVALUATION_FILE_EXTENSION = ".eval";
	
	
	/**
	 * Setting up doing mode file extension.
	 */
	public final static String SETUP_DOING_FILE_EXTENSION = ".setup.doing";
	
	
	/**
	 * Setting up doing mode file extension.
	 */
	public final static String SETUP_DONE_FILE_EXTENSION = ".setup.done";

	
	/**
	 * Metrics analyzing Excel file name.
	 */
	public final static String METRICS_ANALYZE_EXCEL_FILE_NAME = "analyze.xls";
	
	
	/**
	 * Metrics analyzing Excel file name.
	 */
	public final static String METRICS_ANALYZE_EXCEL_FILE_NAME2 = "analyze.hdp";

	
	/**
	 * IO channels for IO writing evaluation results.
	 */
	protected Map<String, ByteChannel> ioChannels = Util.newMap();

	
	/**
	 * Referred evaluator.
	 */
	protected Evaluator referredEvaluator = null;
	
	
	/**
	 * Default constructor.
	 */
	public EvaluateProcessor() {
		this(null);
	}

	
	/**
	 * Default constructor with referred evaluator.
	 * @param referredEvaluator referred evaluator.
	 */
	public EvaluateProcessor(Evaluator referredEvaluator) {
		this.referredEvaluator = referredEvaluator;
	}

	
	/**
	 * Saving bath evaluation results.
	 * @param storePath directory path to store evaluation results.
	 * @param evt evaluation event.
	 * @param algTable algorithm table.
	 */
	public void saveEvaluateResult(String storePath, EvaluateEvent evt, RegisterTable algTable) {
		saveEvaluateResult(storePath, evt, algTable, false, null);
	}

	
	/**
	 * Saving bath evaluation results.
	 * @param storePath directory path to store evaluation results.
	 * @param evt evaluation event.
	 * @param algTable algorithm table.
	 * @param saveResultSummary fast saving mode.
	 */
	public void saveEvaluateResult(String storePath, EvaluateEvent evt, RegisterTable algTable, boolean saveResultSummary) {
		saveEvaluateResult(storePath, evt, algTable, saveResultSummary, null);
	}
	
	
	/**
	 * Saving bath evaluation results.
	 * @param storePath directory path to store evaluation results.
	 * @param evt evaluation event.
	 * @param algTable algorithm table.
	 * @param saveResultSummary fast saving mode.
	 * @param prefix prefix of file name.
	 */
	public void saveEvaluateResult(String storePath, EvaluateEvent evt, RegisterTable algTable, boolean saveResultSummary, String prefix) {
		if (storePath == null) return;
		storePath = storePath.trim();
		
		prefix = (prefix != null && !prefix.isEmpty() ? prefix + "-" : "");
		xURI store = xURI.create(storePath);
		UriAdapter adapter = new UriAdapter(store);
		boolean existed = adapter.exists(store);
		if (!existed)
			adapter.create(store, true);
		adapter.close();
		
		List<String> algNames = evt.getMetrics().getAlgNameList();
		if (!saveResultSummary) {
			for (String algName : algNames) {
				if (evt.getType() == Type.done) {
					String key = prefix + algName + EVALUATION_FILE_EXTENSION;
					try {
						ByteChannel channel = getIOChannel(store, key, true);
						String info = evt.translate(algName, -1) + "\n\n\n\n";
						ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
						channel.write(buffer);
					} catch (Throwable e) {LogUtil.trace(e);}
				}
				else {
					Map<Integer, Metrics> map = evt.getMetrics().gets(algName);
					Set<Integer> datasetIdList = map.keySet();
					for (int datasetId : datasetIdList) {
						String key = prefix + algName + "@" + datasetId + EVALUATION_FILE_EXTENSION;
						try {
							ByteChannel channel = getIOChannel(store, key, true);
							String info = evt.translate(algName, datasetId) + "\n\n\n\n";
							ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
							channel.write(buffer);
						} catch (Throwable e) {LogUtil.trace(e);}
						
						if (evt.getType() == Type.done_one)
							closeIOChannel(key);
					}
				}
			}
		}
		
	    // Exporting excel file
		if (evt.getType() == Type.done || evt.getType() == Type.done_one) {
		    // Exporting excel file
			MetricsUtil util = new MetricsUtil(evt.getMetrics(), algTable, referredEvaluator);
			
			try {
				util.createExcel(store.concat(prefix + METRICS_ANALYZE_EXCEL_FILE_NAME));
			} catch (Throwable e) {LogUtil.trace(e);}
			
			// Begin exporting plain text. It is possible to remove this snippet.
			String key = prefix + METRICS_ANALYZE_EXCEL_FILE_NAME2;
			try {
				ByteChannel channel = getIOChannel(store, key, false);
				ByteBuffer buffer = ByteBuffer.wrap(util.createPlainText().getBytes());
				channel.write(buffer);
			} catch (Throwable e) {LogUtil.trace(e);}
			closeIOChannel(key);
			// End exporting plain text. It is possible to remove this snippet.
			
			if (evt.getType() == Type.done)
				closeIOChannels();
		}
	}
	
	
	/**
	 * Saving setting up results.
	 * @param storePath directory path to store setting up results.
	 * @param evt setting up event.
	 */
	public void saveSetupResult(String storePath, SetupAlgEvent evt) {
		saveSetupResult(storePath, evt, false, null);
	}

	
	/**
	 * Saving setting up results.
	 * @param storePath directory path to store setting up results.
	 * @param evt setting up event.
	 * @param saveResultSummary fast saving mode.
	 */
	public void saveSetupResult(String storePath, SetupAlgEvent evt, boolean saveResultSummary) {
		saveSetupResult(storePath, evt, saveResultSummary, null);
	}
	
	
	/**
	 * Saving setting up results.
	 * @param storePath directory path to store setting up results.
	 * @param evt setting up event.
	 * @param saveResultSummary fast saving mode.
	 * @param prefix prefix of file name.
	 */
	public void saveSetupResult(String storePath, SetupAlgEvent evt, boolean saveResultSummary, String prefix) {
		if (storePath == null) return;
		if (saveResultSummary && (evt.getType() != SetupAlgEvent.Type.done))
			return;

		storePath = storePath.trim();
		
		String algName = evt.getAlgName();
		String info = "========== Algorithm \"" + algName + "\" ==========\n";
		info = info + evt.translate() + "\n\n\n\n";
		
		prefix = (prefix != null && !prefix.isEmpty() ? prefix + "-" : "");
		xURI store = xURI.create(storePath);
		UriAdapter adapter = new UriAdapter(store);
		boolean existed = adapter.exists(store);
		if (!existed) adapter.create(store, true);
		adapter.close();
		
		String key = prefix + algName;
		if (evt.getType() == SetupAlgEvent.Type.doing)
			key += EvaluateProcessor.SETUP_DOING_FILE_EXTENSION;
		else
			key += EvaluateProcessor.SETUP_DONE_FILE_EXTENSION;
			
		try {
			ByteChannel channel = getIOChannel(store, key, true);
			ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
			channel.write(buffer);
		} catch (Throwable e) {LogUtil.trace(e);}
		
		closeIOChannel(key);
	}

		
	/**
	 * Getting channel from specified storage and key.
	 * @param store URI of specified storage.
	 * @param key specified key.
	 * @param append if true then the write channel allows appending writing.
	 * @return byte channel from specified storage and key.
	 */
	public ByteChannel getIOChannel(xURI store, String key, boolean append) {
		if (ioChannels.containsKey(key))
			return ioChannels.get(key);
		
		xURI uri = store.concat(key);
		AdapterWriteChannel channel = new AdapterWriteChannel(uri, append);
		ioChannels.put(key, channel);
		return channel;
	}

	
	/**
	 * Close IO channels.
	 */
	public void closeIOChannels() {
		if (ioChannels.size() == 0) return;
		
		Set<String> keys = ioChannels.keySet();
		for (String key : keys) {
			try {
				ioChannels.get(key).close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		ioChannels.clear();
		
	}
	
	
	/**
	 * Close IO channels by specified key.
	 * @param key specified key.
	 * @return whether close channel successfully.
	 */
	public boolean closeIOChannel(String key) {
		if (!ioChannels.containsKey(key))
			return false;
		
		ByteChannel channel = ioChannels.get(key);
		boolean closed = true;
		try {
			channel.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			closed = false;
		}
		ioChannels.remove(key);
		
		return closed;

	}
	
	
	/**
	 * Clearing internal data.
	 */
	public void close() {
		closeIOChannels();
		referredEvaluator = null;
	}


	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			if (!Constants.CALL_FINALIZE) return;
			close();
		} catch (Throwable e) {LogUtil.errorNoLog("Finalize error: " + e.getMessage());}
	}


}
