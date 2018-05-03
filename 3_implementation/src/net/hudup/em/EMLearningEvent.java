package net.hudup.em;

import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.data.Dataset;


/**
 * This class represents events when expectation maximization (EM) algorithm runs.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EMLearningEvent extends SetupAlgEvent {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Current sufficient statistics.
	 */
	protected Object currentStatistic = null;
	
	
	/**
	 * Default constructor with the source as EM algorithm.
	 * @param em the EM algorithm as the source of this event.
	 * @param trainingDataset training dataset.
	 * @param currentStatistic current sufficient statistic.
	 */
	public EMLearningEvent(EM em, Dataset trainingDataset, Object currentStatistic) {
		// TODO Auto-generated constructor stub
		super(em, Type.doing, em, trainingDataset, em.getEstimatedParameter());
		this.currentStatistic = currentStatistic;
	}

	
	@Override
	public String translate() {
		return translate(false);
	}
	
	
	/**
	 * Translate this event into text.
	 * @param showStatistic whether sufficient statistic is shown.
	 * This parameter is established because sufficient statistic often as a collection (fetcher) is very large.
	 * @return translated text from content of this event.
	 */
	private String translate(boolean showStatistic) {
		EM em = (EM) getSource();
		StringBuffer buffer = new StringBuffer();
		buffer.append("At the " + em.getCurrentIteration() + " iteration");
		buffer.append(" of algorithm \"" + em.getName() + "\"");
		if (getTrainingDataset() != null) {
			String mainUnit = getTrainingDataset().getConfig().getMainUnit();
			String datasetName = getTrainingDataset().getConfig().getAsString(mainUnit);
			if (datasetName != null)
				buffer.append(" on training dataset \"" + datasetName + "\"");
		}
		
		try { 
			if (currentStatistic != null && showStatistic) {
				buffer.append("\nCurrent statistic:");
				buffer.append("\n  " + currentStatistic.toString());
			}
			
			if (em.getCurrentParameter() != null) {
				buffer.append("\nCurrent parameter:");
				buffer.append("\n  " + em.parameterToShownText(em.getCurrentParameter()));
			}
			
			if (em.getEstimatedParameter() != null) {
				buffer.append("\nEstimated parameter:");
				buffer.append("\n  " + em.parameterToShownText(em.getEstimatedParameter()));
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return buffer.toString();
	}
	
	
	
}
