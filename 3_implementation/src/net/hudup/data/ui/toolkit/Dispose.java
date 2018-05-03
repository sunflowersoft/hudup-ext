package net.hudup.data.ui.toolkit;


/**
 * This interface indicates the component that implements it needs to be closed when closing.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface Dispose {
	
	
	/**
	 * 
	 */
	void dispose();
	
	
	/**
	 * 
	 * @return whether running
	 */
	boolean isRunning();
	
	
}
