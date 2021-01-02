/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.external;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.data.DefaultExternalQuery;
import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.server.DefaultServer;
import net.hudup.server.external.ui.ExternalServerCP;
import net.hudup.server.external.ui.SetupExternalServerWizard;
import net.hudup.server.external.ui.SetupExternalServerWizardConsole;

/**
 * This class is  a powerful server that supports external mapping.
 * It is an extension of default server.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalServer extends DefaultServer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified external server configuration.
	 * @param config external server configuration.
	 */
	public ExternalServer(ExternalServerConfig config) {
		super(config);
	}


	@Override
	protected void serverTasks() {
		
		ExternalConfig externalConfig = ((ExternalServerConfig) config).getExternalConfig();
		if (externalConfig == null || externalConfig.size() == 0 || externalConfig.getStoreUri() == null) {
			super.serverTasks();
			return;
		}
		
		trans.lockRead();
		try {
			ExternalQuery query = new DefaultExternalQuery();
			query.setup(config, externalConfig);
			query.importData(null);
			query.close();
			
			LogUtil.info("External server imported external data successfully (in server tasks)");
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("External server fail to import external data (in server tasks), caused by " + e.getMessage());
			
		}
		finally {
			trans.unlockRead();
		}
		
		super.serverTasks();
	}


	@Override
	protected void showCP() {
		try {
			new ExternalServerCP(this);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("External server fail to show control panel, caused by " + e.getMessage());
			
			/*
			 * It is possible that current Java environment does not support GUI.
			 * Use of GraphicsEnvironment.isHeadless() tests Java GUI.
			 * Hence, create control panel with console here.
			 */
		}
	}

	
	/**
	 * Creating external server.
	 * @return {@link ExternalServer}
	 */
	public static ExternalServer create() {
		return create(xURI.create(ExternalServerConfig.serverConfig));
	}

	
	/**
	 * Creating external server by configuration URI.
	 * @param srvConfigUri configuration URI.
	 * @return external server by configuration URI.
	 */
	public static ExternalServer create(xURI srvConfigUri) {
		boolean require = requireSetup(srvConfigUri);
		ExternalServerConfig config = new ExternalServerConfig(srvConfigUri);
		
		if (!require)
			return new ExternalServer(config);
		else {
			boolean finished = true;
			if (Constants.SERVER_UI) {
				boolean isHeadLess = GraphicsEnvironment.isHeadless();
				if (isHeadLess) {
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(System.in);
					System.out.print("\nServer not set up yet.\nDo you want to setup server? (y|n): ");
					String confirm = scanner.next().trim();
					if (confirm.compareToIgnoreCase("n") == 0) {
						LogUtil.error("Server not created due to not confirm");
						return null;
					}
				}
				else {
			        Image image = UIUtil.getImage("server-32x32.png");
					int confirm = JOptionPane.showConfirmDialog(
							null, 
							"External server not set up yet.\n Do you want to setup server?", 
							"Setup external server", 
							JOptionPane.OK_CANCEL_OPTION, 
							JOptionPane.INFORMATION_MESSAGE, 
							image == null ? null : new ImageIcon(image));
					
					if (confirm != JOptionPane.OK_OPTION) {
						LogUtil.error("External server not created");
						return null;
					}
				}
				
				if (isHeadLess) {
					SetupExternalServerWizardConsole wizard = new SetupExternalServerWizardConsole(config);
					finished = wizard.isFinished();
				}
				else {
					SetupExternalServerWizard wizard = new SetupExternalServerWizard(null, config, null);
					finished = wizard.isFinished();
				}
			}
			else {
				SetupExternalServerWizardConsole wizard = new SetupExternalServerWizardConsole(config);
				finished = wizard.isFinished();
			}
			
			if (finished && !requireSetup(srvConfigUri))
				return new ExternalServer(config);
			else {
				LogUtil.error("Server not created");
				return null;
			}
		}
		
	}
	
	
}
