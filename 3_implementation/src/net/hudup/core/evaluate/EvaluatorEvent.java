package net.hudup.core.evaluate;

import java.util.Collections;
import java.util.EventObject;
import java.util.List;

import net.hudup.core.data.RatingVector;
import net.hudup.core.parser.TextParsable;


/**
 * This class represents an event fired by evaluator in evaluation process.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class EvaluatorEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Type of evaluator event.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	public static enum Type {
		
		/**
		 * Evaluation task in progress.
		 */
		doing,
		
		/**
		 * One evaluation task is done.
		 */
		done_one,
		
		/**
		 * All evaluation tasks are done, which means that evaluation process is finished.
		 */
		done
	}
	
	
	/**
	 * Type of event.
	 */
	protected Type type = Type.doing;

	
	/**
	 * List of metrics.
	 */
	protected Metrics metrics = null;
	
	
	/**
	 * Additional information for calculating metrics.
	 */
	protected Object[] params = null;
	
	
	/**
	 * Constructor with specified evaluator and event type.
	 * @param evaluator reference to an {@link Evaluator}.
	 * @param type type of this event.
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type) {
		super(evaluator);
		// TODO Auto-generated constructor stub
		
		this.type = type;
	}

	
	/**
	 * Constructor with specified evaluator, type of evaluation event, and list of metrics. 
	 * @param evaluator specified evaluator.
	 * @param type specified type of evaluation event.
	 * @param metrics specified list of metrics.
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type, Metrics metrics) {
		this(evaluator, type);
		// TODO Auto-generated constructor stub
		
		setMetrics(metrics);
	}

	
	/**
	 * Constructor with specified evaluator, type of evaluation event, list of metrics, and additional parameters. 
	 * @param evaluator specified evaluator.
	 * @param type specified type of evaluation event.
	 * @param metrics specified list of metrics.
	 * @param params additional parameters.
	 */
	public EvaluatorEvent(Evaluator evaluator, Type type, Metrics metrics, Object... params) {
		this(evaluator, type, metrics);
		// TODO Auto-generated constructor stub
		
		setParams(params);
	}

	
	/**
	 * Getting evaluator.
	 * @return {@link Evaluator} that fires this event.
	 */
	public Evaluator getEvaluator() {
		return (Evaluator) getSource();
	}
	
	
	/**
	 * Getting type of this evaluation event.
	 * @return {@link Type} of this evaluation event.
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * Getting list of metrics.
	 * @return {@link Metrics} as list of metrics.
	 */
	public Metrics getMetrics() {
		return metrics;
	}
	
	
	/**
	 * Getting list of metrics.
	 * @param metrics specified list of metrics.
	 */
	public void setMetrics(Metrics metrics) {
		this.metrics = metrics;
	}
	
	
	
	/**
	 * Getting additional parameter list.
	 * @return additional  parameter list
	 */
	public Object[] getParams() {
		return params;
	}
	
	
	/**
	 * Setting additional parameter list.
	 * @param params additional parameter list.
	 */
	public void setParams(Object... params) {
		this.params = params;
	}
	
	
	/**
	 * Translating this event into text for all algorithm and all datasets.
	 * @return translated text of this event for all datasets.
	 */
	public String translate() {
		return translate(null, -1);
	}
	
	
	/**
	 * Translating this event into text for specified algorithm and specified dataset.
	 * @param fAlgName specified algorithm name.
	 * @param fDatasetId specified dataset identifier.
	 * @return translated text of this event.
	 */
	public String translate(String fAlgName, int fDatasetId) {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		if (this.metrics == null)
			return buffer.toString();
		
		String testingResult = "";
		String testingRecord = "";
		if (params != null) {
			if (params.length >= 1 && params[0] != null) {
				testingResult += "\nResult = [";
				if (params[0] instanceof RatingVector)
					testingResult += ((RatingVector)params[0]).toTextNice();
				else if (params[0] instanceof TextParsable)
					testingResult += ((TextParsable)params[0]).toText();
				else
					testingResult += params[0].toString();
				testingResult += "]";
			}
			if (params.length >= 2 && params[1] != null) {
				testingRecord += "\nTesting = [";
				if (params[1] instanceof RatingVector)
					testingRecord += ((RatingVector)params[1]).toText(); //Can be toTextNice()
				else if (params[1] instanceof TextParsable)
					testingRecord += ((TextParsable)params[1]).toText();
				else
					testingRecord += params[1].toString();
				testingRecord += "]";
			}
		}
		
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		int i = 0;
		for (String algName : algNameList) {
			if (fAlgName != null && !fAlgName.isEmpty() && !algName.equals(fAlgName))
				continue;
			
			if (i > 0)
				buffer.append("\n\n\n");
			
			i++;
			buffer.append("========== Algorithm \"" + algName + "\"" + (type == Type.doing ? "" : " - Final result ") + "==========");
			List<Integer> datasetIdList = this.metrics.getDatasetIdList(algName);
			Collections.sort(datasetIdList);
			
			for (int datasetId : datasetIdList) {
				if (fDatasetId >= 0 && datasetId != fDatasetId)
					continue;
				
				buffer.append("\n\n----- Testing dataset \"" + datasetId + "\" -----");
				buffer.append(testingRecord);
				buffer.append(testingResult);
				
				Metrics metrics = this.metrics.gets(algName, datasetId);
				for (int k = 0; k < metrics.size(); k++) {
					
					MetricWrapper wrapper = metrics.get(k);
					if (!wrapper.isValid())
						continue;
					
					MetricValue metricValue = (type == Type.doing ? wrapper.getCurrentValue() : wrapper.getAccumValue());
					buffer.append("\n" + wrapper.getName() + " = " + MetricValue.valueToText(metricValue));
				}
				buffer.append("\n----- Testing dataset \"" + datasetId + "\" -----");
			}
			
			buffer.append("\n\n========== Algorithm \"" + algName + "\"" + (type == Type.doing ? "" : " - Final result ") + "==========");
			
			
		}
		
		return buffer.toString();
	}

	
}
