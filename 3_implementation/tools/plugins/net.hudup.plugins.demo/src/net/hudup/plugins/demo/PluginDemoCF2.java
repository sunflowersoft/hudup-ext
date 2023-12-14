/**
 * 
 */
package net.hudup.plugins.demo;

import net.hudup.core.alg.cf.nb.beans.Pearson;

/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PluginDemoCF2 extends Pearson {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public PluginDemoCF2() {
		super();
	}


	@Override
	public String getName() {
		return "plugin_demo_cf2";
	}

	
}
