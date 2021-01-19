/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.Remote;
import java.util.EventObject;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetRemote;
import net.hudup.core.data.DatasetRemoteWrapper;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Exportable;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.LogUtil;

/**
 * This class represents an event fired by a source (often evaluator) for successful setting up an algorithm.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class SetupAlgEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Type of setting up event.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	public static enum Type {
		
		/**
		 * Evaluation task in progress.
		 */
		doing,
		
		/**
		 * All evaluation tasks are done, which means that evaluation process is finished.
		 */
		done
	}

	
	/**
	 * Type of setting up event. 
	 */
	protected Type type = Type.doing;
	
	
	/**
	 * Name of algorithm issuing the setup result.
	 */
	protected String algName = null;

	
	/**
	 * Training dataset identifier.
	 */
	protected int trainingDatasetId = -1;

	
	/**
	 * Training dataset. It must be serializable in remote call.
	 */
	protected DatasetRemote trainingDataset = null;
	
	
	/**
	 * Result of successful setting up an algorithm.
	 */
	protected Serializable setupResult = null;
	
	
	/**
	 * Current step in progress.
	 */
	protected int progressStep = 0;
	
	
	/**
	 * Total estimated number of steps in progress.
	 */
	protected int progressTotalEstimated = 0;
	
	
	/**
	 * Constructor with a source of event, algorithm name, and setting up result.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param setupResult specified result.
	 */
	public SetupAlgEvent(Object source, Type type, String algName, Serializable setupResult) {
		this(source, type, algName, -1, null, setupResult, 0, 0);
	}

	
	/**
	 * Constructor with a source of event, algorithm name, training dataset identifier, and setting up result.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param trainingDatasetId training dataset identifier.
	 * @param setupResult specified result.
	 */
	public SetupAlgEvent(Object source, Type type, String algName, int trainingDatasetId, Serializable setupResult) {
		this(source, type, algName, trainingDatasetId, null, setupResult, 0, 0);
	}

	
	/**
	 * Constructor with a source of event, algorithm name, training dataset, and setting up result.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param trainingDataset training dataset.
	 * @param setupResult specified result.
	 */
	public SetupAlgEvent(Object source, Type type, String algName, Dataset trainingDataset, Serializable setupResult) {
		this(source, type, algName, -1, trainingDataset, setupResult, 0, 0);
	}

	
	/**
	 * Constructor with a source of event, algorithm name, training dataset identifier, training dataset, and setting up result.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param trainingDatasetId training dataset identifier.
	 * @param trainingDataset training dataset.
	 * @param setupResult specified result.
	 */
	public SetupAlgEvent(Object source, Type type, String algName, int trainingDatasetId, Dataset trainingDataset, Serializable setupResult) {
		this(source, type, algName, trainingDatasetId, trainingDataset, setupResult, 0, 0);
	}

	
	/**
	 * Constructor with a source of event, algorithm name, setting up result, progress step, and progress total.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself. This source is invalid in remote call because the source is transient variable.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param setupResult specified result.
	 * @param progressStep progress step.
	 * @param progressTotalEstimated progress total estimated.
	 */
	public SetupAlgEvent(Object source, Type type, String algName, Serializable setupResult,
			int progressStep, int progressTotalEstimated) {
		this(source, type, algName, -1, null, setupResult, progressStep, progressTotalEstimated);
	}

	
	/**
	 * Constructor with a source of event, algorithm name, training dataset, setting up result, progress step, and progress total.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself. This source is invalid in remote call because the source is transient variable.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param trainingDataset training dataset.
	 * @param setupResult specified result.
	 * @param progressStep progress step.
	 * @param progressTotalEstimated progress total estimated.
	 */
	public SetupAlgEvent(Object source, Type type, String algName, Dataset trainingDataset, Serializable setupResult,
			int progressStep, int progressTotalEstimated) {
		this(source, type, algName, -1, trainingDataset, setupResult, progressStep, progressTotalEstimated);
	}
	
	
	/**
	 * Constructor with a source of event, algorithm name, training dataset identifier, training dataset, setting up result, progress step, and progress total.
	 * @param source source of event. It is usually an evaluator but it can be the algorithm itself. This source is invalid in remote call because the source is transient variable.
	 * @param type type of event.
	 * @param algName name of the algorithm issuing the setup result.
	 * @param trainingDatasetId training dataset identifier.
	 * @param trainingDataset training dataset.
	 * @param setupResult specified result.
	 * @param progressStep progress step.
	 * @param progressTotalEstimated progress total estimated.
	 */
	public SetupAlgEvent(Object source, Type type, String algName, int trainingDatasetId, Dataset trainingDataset, Serializable setupResult,
			int progressStep, int progressTotalEstimated) {
		super(source);
		this.type = type;
		this.algName = algName;
		this.trainingDatasetId = trainingDatasetId;
		
		if (trainingDataset == null)
			this.trainingDataset = null; 
		else if (trainingDataset instanceof DatasetRemoteWrapper)
			this.trainingDataset = (DatasetRemoteWrapper)trainingDataset;
		else if (trainingDataset instanceof DatasetRemote)
			this.trainingDataset = Util.getPluginManager().wrap((DatasetRemote)trainingDataset, false);
		this.setupResult = setupResult;
		
		this.progressStep = progressStep;
		this.progressTotalEstimated = progressTotalEstimated;
	}
	
	
	/**
	 * Transferring to a new event without training dataset.
	 * @return a new event without training dataset.
	 */
	public SetupAlgEvent transferForRemote() {
		SetupAlgEvent evt = new SetupAlgEvent(
				this.source,
				this.type, 
				this.algName,
				this.trainingDatasetId,
				null,
				this.setupResult,
				this.progressStep,
				this.progressTotalEstimated);
		if (this.trainingDataset == null) return evt;
		
		if (this.trainingDatasetId < 0)
			evt.trainingDatasetId = DatasetUtil.getDatasetId(getTrainingDataset());
		
		Dataset dataset = DatasetUtil.getMostInnerDataset2(this.trainingDataset);
		if ((dataset != null) && (dataset instanceof Exportable)) {
			Remote stub = null;
			try {
				stub = ((Exportable)dataset).getExportedStub();
			} catch (Exception e) {LogUtil.trace(e);}
			
			if (stub != null) evt.trainingDataset = this.trainingDataset;
		}
		
		return evt;
	}
	
	
	/**
	 * Getting evaluator. This method cannot be called remotely because the source is transient variable.
	 * @return evaluator.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private Evaluator getEvaluator() {
		Object source = getSource();
		if (source == null)
			return null;
		else if (source instanceof Evaluator)
			return (Evaluator)source;
		else
			return null;
	}

	
	/**
	 * Getting event type.
	 * @return event type.
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * Getting name of algorithm that issues the setup result.
	 * @return name of algorithm that issues the setup result.
	 */
	public String getAlgName() {
		return algName;
	}

	
	/**
	 * Getting training dataset identifier.
	 * @return training dataset identifier.
	 */
	public int getTrainingDatasetId() {
		return trainingDatasetId;
	}

	
	/**
	 * Getting training dataset.
	 * @return training dataset.
	 */
	public Dataset getTrainingDataset() {
		if (trainingDataset == null)
			return null;
		else if (trainingDataset instanceof Dataset)
			return (Dataset)trainingDataset;
		else
			return null;
	}
	
	
	/**
	 * Getting the setup result issued by the algorithm.
	 * @return setup result issued by the algorithm.
	 */
	public Serializable getSetupResult() {
		return setupResult;
	}
	
	
	/**
	 * Getting progress step.
	 * @return progress step.
	 */
	public int getProgressStep() {
		return progressStep;
	}
	
	
	/**
	 * Getting progress total in estimation.
	 * @return progress total in estimation.
	 */
	public int getProgressTotalEstimated() {
		return progressTotalEstimated;
	}

	
	/**
	 * Extracting identifier or name of the training dataset. If the identifier exists, it is returned. Otherwise, the name is returned.
	 * Note that the identifier is often number, not URI identifier. The identifier is often assigned in evaluation process.
	 * @return identifier or name of the training dataset. Return null if there is nor identifier neither name.
	 */
	public String extractTrainingDatasetIdOrName() {
		if (trainingDatasetId >= 0)
			return "" + trainingDatasetId;
		
		Dataset trainingDataset = getTrainingDataset();
		int id = DatasetUtil.getDatasetId(trainingDataset);
		if (id >= 0)
			return "" + id;
		else
			return DatasetUtil.extractDatasetName(trainingDataset);
	}

	
	/**
	 * Translating this event into text.
	 * @return translated text of this event.
	 */
	public String translate() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Setup result of algorithm");
		if (algName != null)
			buffer.append(" \"" + algName + "\"");
		else
			buffer.append(" \"noname\"");
		
		Dataset trainingDataset = getTrainingDataset();
		if (trainingDataset == null) {
			if (trainingDatasetId >= 0)
				buffer.append(" on training dataset with assigned ID '" + trainingDatasetId + "'");
		}
		else {
			String datasetName = DatasetUtil.extractDatasetName(trainingDataset);
			if (datasetName != null)
				buffer.append(" on training dataset '" + datasetName + "'");
			int datasetId = trainingDatasetId >= 0 ? trainingDatasetId : DatasetUtil.getDatasetId(trainingDataset);
			if (datasetId >= 0)
				buffer.append(" with assigned ID '" + datasetId + "'");
		}
		
		if (setupResult != null) {
			if (type == Type.doing)
				buffer.append(" (doing) are\n" + setupResult.toString());
			else
				buffer.append(" (done) are " + setupResult.toString());
		}
		
		return buffer.toString();
	}
	
	
	
	
}
