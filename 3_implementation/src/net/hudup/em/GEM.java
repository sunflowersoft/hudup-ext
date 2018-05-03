package net.hudup.em;

/**
 * This class represents the generalized expectation maximization (GEM) algorithm.
 * It inherits directly from {@link AbstractEM}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class GEM extends AbstractEM {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public GEM() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Finding a maximizer of the conditional expectation Q based on current parameter.
	 * @param currentParameter current parameter.
	 * @return a maximizer of the conditional expectation Q based on current parameter.
	 * @throws Exception if any error raises.
	 */
	protected abstract Object argmaxQ(Object currentParameter) throws Exception;
	
	
	@Override
	public synchronized Object learn() throws Exception {
		// TODO Auto-generated method stub
		estimatedParameter = currentParameter = initializeParameter();
		if (estimatedParameter == null)
			return null;
		
		currentIteration = 1;
		int maxIteration = getMaxIteration();
		while(currentIteration < maxIteration) {
			estimatedParameter = argmaxQ(currentParameter);
			if (estimatedParameter == null)
				break;
			
			//Firing event
			fireSetupEvent(new EMLearningEvent(this, getDataset(), null));
			
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
