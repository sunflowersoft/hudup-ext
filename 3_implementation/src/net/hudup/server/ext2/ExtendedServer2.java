/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext2;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.alg.cf.gfall.GreenFallCF;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.SnapshotParserImpl;
import net.hudup.server.DefaultService;
import net.hudup.server.ext.EvaluatorCPList;
import net.hudup.server.ext.ExtendedService;
import net.hudup.server.external.ExternalServer;
import net.hudup.server.external.ExternalServerConfig;
import net.hudup.server.external.ui.SetupExternalServerWizard;
import net.hudup.server.external.ui.SetupExternalServerWizardConsole;

/**
 * This class is extended version of default server with improvement.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtendedServer2 extends ExternalServer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with configuration.
	 * @param config server configuration.
	 */
	public ExtendedServer2(ExternalServerConfig config) {
		super(config);
	}

	
	@Override
	protected DefaultService createService() {
		return new ExtendedService2(trans, this);
	}


	@Override
	protected void serverTasks() {
		super.serverTasks();
		
		//Task 1: Purging disconnected listeners.
		if (Constants.SERVER_PURGE_LISTENERS && (service != null) && (service instanceof ExtendedService2)) {
			((ExtendedService2)service).purgeListeners();
			LogUtil.info("Server timer internal tasks: Purging disconnected listeners is successful");
		}
	}


	/**
	 * Show control panel.
	 */
	protected void showCP() {
		try {
			new ExtendedServer2CP(this);
		}
		catch (Throwable e) {
			//LogUtil.trace(e);
			LogUtil.error("Extended server fail to show control panel, caused by " + e.getMessage());
			
			/*
			 * It is possible that current Java environment does not support GUI.
			 * Use of GraphicsEnvironment.isHeadless() tests Java GUI.
			 * Hence, create control panel with console here or improve PowerServerCP to support console.
			 */
		}
	}

	
	@Override
	protected PopupMenu createSysTrayMenuExt() {
		
		try {
			
	        PopupMenu popup = new PopupMenu();
	
	        MenuItem evItem = new MenuItem(I18nUtil.message("evaluator"));
	        evItem.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						showEvaluatorCP();
					}
					catch (Throwable ex) {
						//LogUtil.trace(ex);
						LogUtil.error("Server fail to show GUI evaluator, caused by " + ex.getMessage());
						
						/*
						 * It is possible that current Java environment does not support GUI.
						 * Use of GraphicsEnvironment.isHeadless() tests Java GUI.
						 * Hence, create control panel with console here.
						 */
					}
				}
			});
	        popup.add(evItem);
	
	        return popup;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}


	/**
	 * Showing evaluator control panel.
	 */
	protected void showEvaluatorCP() {
		if ((service == null) || !(service instanceof ExtendedService)) {
			LogUtil.error("Service is not initialized yet or not extended service");
			JOptionPane.showMessageDialog(
					null, 
					"Service is not initialized yet or not extended service", 
					"Evaluator control panel now shown", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			if (!isRunning()) {
				LogUtil.error("Server is not running");
				JOptionPane.showMessageDialog(
						null, 
						"Server is not running", 
						"Evaluator control panel now shown", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			EvaluatorCPList ecp = new EvaluatorCPList((ExtendedService)service);
			ecp.setVisible(true);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Static method to create default server.
	 * @return extended default server.
	 */
	public static ExtendedServer2 create() {
		return create(xURI.create(ExternalServerConfig.serverConfig));
	}
	
	
	/**
	 * Static method to create extended default server with specified configuration URI.
	 * @param srvConfigUri specified configuration URI.
	 * @return extended default server.
	 */
	public static ExtendedServer2 create(xURI srvConfigUri) {
		boolean require = requireSetup(srvConfigUri);
		ExternalServerConfig config = new ExternalServerConfig(srvConfigUri);
		config.setRecommender(new GreenFallCF());
		config.setParser(new SnapshotParserImpl());
		
		if (!require)
			return new ExtendedServer2(config);
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
							"Server not set up yet.\nDo you want to setup server?", 
							"Setup server", 
							JOptionPane.OK_CANCEL_OPTION, 
							JOptionPane.INFORMATION_MESSAGE, 
							image == null ? null : new ImageIcon(image));
					
					if (confirm != JOptionPane.OK_OPTION) {
						LogUtil.error("Server not created");
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
				return new ExtendedServer2(config);
			else {
				LogUtil.error("Server not created");
				return null;
			}
		}
		
	}


}
