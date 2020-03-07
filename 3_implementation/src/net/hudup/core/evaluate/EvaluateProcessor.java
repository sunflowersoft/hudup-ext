/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
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
	 * Default constructor.
	 */
	public EvaluateProcessor() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Saving bath evaluation results.
	 * @param storePath directory path to store evaluation results.
	 * @param evt evaluation event.
	 * @param algs list of algorithms.
	 * @param fastsave fast saving mode.
	 */
	public void saveEvaluateResult(String storePath, EvaluatorEvent evt, List<Alg> algs, boolean fastsave) {
		if (storePath == null) return;
		storePath = storePath.trim();
		
		try {
			xURI store = xURI.create(storePath);
			UriAdapter adapter = new UriAdapter(store);
			boolean existed = adapter.exists(store);
			if (!existed)
				adapter.create(store, true);
			adapter.close();
			
			if (!fastsave) {
				for (Alg alg : algs) {
					if (evt.getType() == Type.done) {
						String key = alg.getName() + EVALUATION_FILE_EXTENSION;
						ByteChannel channel = getIOChannel(store, key, true);
						
						String info = evt.translate(alg.getName(), -1) + "\n\n\n\n";
						ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
						channel.write(buffer);
					}
					else {
						Map<Integer, Metrics> map = evt.getMetrics().gets(alg.getName());
						Set<Integer> datasetIdList = map.keySet();
						for (int datasetId : datasetIdList) {
							String key = alg.getName() + "@" + datasetId + EVALUATION_FILE_EXTENSION;
							ByteChannel channel = getIOChannel(store, key, true);
	
							String info = evt.translate(alg.getName(), datasetId) + "\n\n\n\n";
							ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
							channel.write(buffer);
							
							if (evt.getType() == Type.done_one)
								closeIOChannel(key);
						}
					}
				}
			}
			
		    // Exporting excel file
			if (evt.getType() == Type.done || evt.getType() == Type.done_one) {
			    // Exporting excel file
				MetricsUtil util = new MetricsUtil(evt.getMetrics(), new RegisterTable(algs));
				util.createExcel(store.concat(METRICS_ANALYZE_EXCEL_FILE_NAME));
				// Begin exporting plain text. It is possible to remove this snippet.
				ByteChannel channel = getIOChannel(store, METRICS_ANALYZE_EXCEL_FILE_NAME2, false);
				ByteBuffer buffer = ByteBuffer.wrap(util.createPlainText().getBytes());
				channel.write(buffer);
				closeIOChannel(METRICS_ANALYZE_EXCEL_FILE_NAME2);
				// End exporting plain text. It is possible to remove this snippet.
				
				if (evt.getType() == Type.done)
					closeIOChannels();
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Saving bath evaluation results.
	 * @param storePath directory path to store evaluation results.
	 * @param evt setting up event.
	 * @param algName list of algorithm name.
	 * @param fastsave fast saving mode.
	 */
	public void saveSetupResult(String storePath, SetupAlgEvent evt, String algName, boolean fastsave) {
		if (storePath == null) return;
		if (fastsave && (evt.getType() != SetupAlgEvent.Type.done))
			return;

		storePath = storePath.trim();
		
		String info = "========== Algorithm \"" + algName + "\" ==========\n";
		info = info + evt.translate() + "\n\n\n\n";
		
		try {
			xURI store = xURI.create(storePath);
			UriAdapter adapter = new UriAdapter(store);
			boolean existed = adapter.exists(store);
			if (!existed)
				adapter.create(store, true);
			adapter.close();
			
			String key = algName;
			if (evt.getType() == SetupAlgEvent.Type.doing)
				key += EvaluateProcessor.SETUP_DOING_FILE_EXTENSION;
			else
				key += EvaluateProcessor.SETUP_DONE_FILE_EXTENSION;
				
			ByteChannel channel = getIOChannel(store, key, true);
			ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
			channel.write(buffer);
			closeIOChannel(key);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
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
				// TODO Auto-generated catch block
				e.printStackTrace();
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
		
		try {
			ByteChannel channel = ioChannels.get(key);
			channel.close();
			ioChannels.remove(key);
			
			return true;
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		
		return false;
		
	}
	
	
	/**
	 * Clearing internal data.
	 */
	public void clear() {
		// TODO Auto-generated method stub
		closeIOChannels();
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			clear();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
