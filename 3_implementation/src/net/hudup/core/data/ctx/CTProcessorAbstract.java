/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ctx;

/**
 * This abstract class implements partially the {@link CTProcessor} interface called context template processor.
 * It adds the parameter for context template processor. Such parameter is the internal variable {@link #param}.
 * So any context template processor should extends this abstract class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class CTProcessorAbstract implements CTProcessor {

	
	/**
	 * Parameter for context template processor.
	 */
	protected Object param = null;
	
	
	/**
	 * Constructor with specified parameter.
	 * @param param specified parameter.
	 */
	public CTProcessorAbstract(Object param) {
		this.param = param;
	}
	
	
	@Override
	public Object getParam() {
		return param;
	}
	
	
}
