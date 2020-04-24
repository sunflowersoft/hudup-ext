/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ui;

import java.util.Scanner;

import net.hudup.core.Constants;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.server.PowerServerConfig;

/**
 * This class provides a wizard to set up server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
public class SetupServerWizardConsole {

	
	/**
	 * Steps to set up server.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	protected enum Step {
		
		/**
		 * Configuration step.
		 */
		config,
		
		/**
		 * Creating schema step.
		 */
		create_schema,
		
		/**
		 * Importing data step.
		 */
		import_data,
		
		/**
		 * Finished step.
		 */
		finished
	}
	
	
	/**
	 * Current step.
	 */
	protected Step currentStep = Step.config;

	
	/**
	 * Server configuration.
	 */
	protected PowerServerConfig config = null;
	

	/**
	 * Provider.
	 */
	protected Provider provider = null;

	
	/**
	 * Constructor with configuration.
	 * @param srvConfig power server configuration.
	 */
	public SetupServerWizardConsole(PowerServerConfig srvConfig) {
		this.config = srvConfig;
		this.provider = new ProviderImpl(config);
		start();
	}

	
	/**
	 * Main method to start the setting up process.
	 */
	private void start() {
		if (provider == null) provider = new ProviderImpl(config);
		
		@SuppressWarnings("resource")
		Scanner scanner = new Scanner(System.in);

		xURI storeUri = config.getStoreUri();
		UriAdapter adapter = new UriAdapter(storeUri);
		if (storeUri != null && adapter.exists(storeUri)) {
			System.out.print("\nStore URI exists: " + storeUri.toString());
			System.out.print("\nDo you want to clear it? (y|n): ");
			String clearStore = scanner.next().trim();
			if (clearStore.compareToIgnoreCase("y") == 0)
				adapter.clearContent(storeUri, null);
		}
		else {
			if (storeUri == null) storeUri = xURI.create(PowerServerConfig.STORE_PATH_DEFAULT2);
			
			System.out.print("\nEnter store URI (default <" + storeUri.toString() + ">: ");
			String storeUriText = scanner.next().trim();
			if (!storeUriText.isEmpty())
				storeUri = xURI.create(storeUriText);
			if (!adapter.exists(storeUri))
				adapter.create(storeUri, true);
			else
				adapter.clearContent(storeUri, null);
		}
		adapter.close();
		
		config.setStoreUri(storeUri);
		
		AttributeList userAtt = new AttributeList();
		userAtt = AttributeList.defaultUserAttributeList();
		userAtt = userAtt.nomalizeId(
				DataConfig.USERID_FIELD, Attribute.Type.integer, Constants.SUPPORT_AUTO_INCREMENT_ID);
		
		AttributeList itemAtt = AttributeList.defaultItemAttributeList();
		itemAtt = itemAtt.nomalizeId(
				DataConfig.ITEMID_FIELD, Attribute.Type.integer, Constants.SUPPORT_AUTO_INCREMENT_ID);
		
		provider.createSchema(userAtt, itemAtt);
		config.removeUnitList(DataConfig.getDefaultUnitList());
		config.putUnitList(provider.getUnitList().getMainList());

		provider.getCTSManager().defaultCTSchema();
		provider.getCTSManager().reload();
		
		//Importing data. Improving later.
		
		config.save();
		provider.close();
		provider = null;
		
		currentStep = Step.finished;
	}
	
	
	/**
	 * Testing the setting up server process is finished.
	 * @return whether finished
	 */
	public boolean isFinished() {
		return currentStep == Step.finished;
	}


}
