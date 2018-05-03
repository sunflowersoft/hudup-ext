package net.hudup.em;

/**
 * This abstract class model a expectation maximization (EM) algorithm for exponential family.
 * In other words, probabilistic distributions in this class belongs to exponential family.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ExponentialEM extends AbstractEM {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExponentialEM() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * This method implement expectation step (E-step) of EM.
	 * @param currentParameter current parameter.
	 * @return sufficient statistic given current parameter.
	 * @throws Exception if any error raises
	 */
	protected abstract Object expectation(Object currentParameter) throws Exception;
	
	
	/**
	 * This method implement maximization step (M-step) of EM.
	 * @param currentStatistic current sufficient statistic.
	 * @return estimated parameter given current sufficient statistic.
	 * @throws Exception if any error raises
	 */
	protected abstract Object maximization(Object currentStatistic) throws Exception;
	
	
	@Override
	public synchronized Object learn() throws Exception {
		// TODO Auto-generated method stub
		estimatedParameter = currentParameter = initializeParameter();
		if (estimatedParameter == null)
			return null;
		
		currentIteration = 1;
		int maxIteration = getMaxIteration();
		while(currentIteration < maxIteration) {
			Object currentStatistic = expectation(currentParameter);
			if (currentStatistic == null)
				break;
			estimatedParameter = maximization(currentStatistic);
			if (estimatedParameter == null)
				break;
			
			//Firing event
			fireSetupEvent(new EMLearningEvent(this, getDataset(), currentStatistic));
			
			boolean terminated = terminatedCondition(currentParameter, estimatedParameter);
			if (terminated)
				break;
			else {
				currentParameter = estimatedParameter;
				currentIteration++;
			}
		}
		
		return estimatedParameter;
	}


}
