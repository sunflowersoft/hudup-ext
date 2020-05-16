/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.external.ui;

import net.hudup.core.logistic.NextUpdate;
import net.hudup.server.PowerServerConfig;
import net.hudup.server.ui.SetupServerWizardConsole;

/**
 * This class provides a console wizard to set up external server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
public class SetupExternalServerWizardConsole extends SetupServerWizardConsole {

	
	/**
	 * Constructor with configuration.
	 * @param srvConfig power server configuration.
	 */
	public SetupExternalServerWizardConsole(PowerServerConfig srvConfig) {
		super(srvConfig);
	}

	
}
