/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.Serializable;
import java.util.EventObject;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetRemote;
import net.hudup.core.data.DatasetRemoteWrapper;

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
	 * Algorithm issues the setup result.
	 */
	protected Alg alg = null;
	
	
	/**
	 * Name of algorithm issuing the setup result.
	 */
	protected String algName = null;

	
	/**
	 * Training dataset. It must be serializable in remote call.
	 */
	private DatasetRemote trainingDataset = null;
	
	
	/**
	 * Result of successful setting up an algorithm.
	 */
	protected Serializable setupResult = null;
	
	
	/**
	 * Constructor with a source of event, algorithm, training dataset, and setting up result.
	 * @param source source of event. Usually, it is an evaluator.
	 * @param type type of event.
	 * @param alg algorithm issues the setup result.
	 * @param trainingDataset training dataset.
	 * @param setupResult specified result.
	 */
	public SetupAlgEvent(Serializable source, Type type, Alg alg, Dataset trainingDataset, Serializable setupResult) {
		super(source);
		// TODO Auto-generated constructor stub
		if (alg != null) {
			this.algName = alg.getName();
			if (AlgDesc2.isRemote(alg))
				this.alg = alg;
			else
				this.alg = alg.newInstance();
		}
		if (source == alg) this.source = this.alg;
		
		this.type = type;
		if (trainingDataset instanceof DatasetRemoteWrapper)
			this.trainingDataset = (DatasetRemoteWrapper)trainingDataset;
		else if (trainingDataset instanceof DatasetRemote)
			this.trainingDataset = new DatasetRemoteWrapper((DatasetRemote)trainingDataset, false);
		this.setupResult = setupResult;
	}

	
	/**
	 * Getting event type.
	 * @return event type.
	 */
	public Type getType() {
		return type;
	}
	
	
	/**
	 * Getting algorithm that issues the setup result.
	 * @return algorithm that issues the setup result.
	 */
	public Alg getAlg() {
		return alg;
	}

	
	/**
	 * Getting name of algorithm that issues the setup result.
	 * @return name of algorithm that issues the setup result.
	 */
	public String getAlgName() {
		return algName;
	}

	
	/**
	 * Getting training dataset.
	 * @return training dataset.
	 */
	public Dataset getTrainingDataset() {
		return trainingDataset instanceof Dataset ? (Dataset)trainingDataset : null;
	}
	
	
	/**
	 * Getting the setup result issued by the algorithm.
	 * @return setup result issued by the algorithm.
	 */
	public Serializable getSetupResult() {
		return setupResult;
	}
	
	
	/**
	 * Translating this event into text.
	 * @return translated text of this event.
	 */
	public String translate() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("Setup result of algorithm");
		if (alg != null)
			buffer.append(" \"" + alg.getName() + "\"");
		
		Dataset trainingDataset = getTrainingDataset();
		DataConfig config = trainingDataset != null ? trainingDataset.getConfig() : null;
		if (config != null && config.getMainUnit() != null) {
			String mainUnit = config.getMainUnit();
			String datasetName = config.getAsString(mainUnit);
			if (datasetName != null)
				buffer.append(" on training dataset \"" + datasetName + "\"");
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
