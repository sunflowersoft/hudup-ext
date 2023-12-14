/**
 * 
 */
package net.hudup.plugins.demo;

import net.hudup.core.alg.cf.nb.beans.Cosine;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PluginDemoCF extends Cosine {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public PluginDemoCF() {
		super();
	}


	@Override
	public String getName() {
		return "plugin_demo_cf";
	}

	
}
