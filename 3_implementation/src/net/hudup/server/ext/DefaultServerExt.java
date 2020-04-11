/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

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
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NetUtil.InetHardware;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.server.DefaultServer;
import net.hudup.server.DefaultService;
import net.hudup.server.PowerServerConfig;
import net.hudup.server.ui.SetupServerWizard;
import net.hudup.server.ui.SetupServerWizardConsole;

/**
 * This class is extended version of default server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DefaultServerExt extends DefaultServer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with configuration.
	 * @param config power server configuration.
	 */
	public DefaultServerExt(PowerServerConfig config) {
		super(config);
	}

	
	@Override
	protected DefaultService createService() {
		// TODO Auto-generated method stub
		return new DefaultServiceExt(trans, this);
	}


	@Override
	protected void doWhenStart() {
		super.doWhenStart();
		
		try {
			InetHardware ih = NetUtil.getInetHardware();
			if (ih != null && ih.ni != null && ih.inetAddr != null) {
				Constants.hardwareAddress = ih.getMACAddress();
				Constants.hostAddress = ih.inetAddr.getHostAddress();
			}
			if (Constants.hardwareAddress == null || Constants.hostAddress == null) {
				Constants.hardwareAddress = null;
				Constants.hostAddress = null;
			}
		}
		catch (Throwable e) {
			LogUtil.error("Error when getting MAC and host addresses");
			Constants.hardwareAddress = null;
			Constants.hostAddress = null;
		}
	}


	@Override
	protected PopupMenu createSysTrayMenuExt() {
		
        PopupMenu popup = new PopupMenu();

        MenuItem evItem = new MenuItem(I18nUtil.message("evaluator"));
        evItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					showEvaluatorCP();
				}
				catch (Throwable ex) {
					ex.printStackTrace();
					LogUtil.error("Server fail to show evaluator, caused by " + ex.getMessage());
					
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


	/**
	 * Showing evaluator control panel.
	 */
	protected void showEvaluatorCP() {
		if ((service == null) || !(service instanceof DefaultServiceExt)) {
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
			
			EvaluatorCP ecp = new EvaluatorCP((DefaultServiceExt)service);
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
	public static DefaultServerExt create() {
		return create(xURI.create(PowerServerConfig.serverConfig));
	}
	
	
	/**
	 * Static method to create extended default server with specified configuration URI.
	 * @param srvConfigUri specified configuration URI.
	 * @return extended default server.
	 */
	public static DefaultServerExt create(xURI srvConfigUri) {
		boolean require = requireSetup(srvConfigUri);
		
		if (!require)
			return new DefaultServerExt(new PowerServerConfig(srvConfigUri));
		else {
			boolean isHeadLess = GraphicsEnvironment.isHeadless(); 
			if (isHeadLess) {
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(System.in);
				System.out.print("\nServer not set up yet.\nDo you want to setup server? (y|n): ");
				String confirm = scanner.next().trim();
				if (confirm.compareToIgnoreCase("n") == 0) {
					LogUtil.info("Server not created due to not confirm");
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
					LogUtil.info("Server not created");
					return null;
				}
			}
			
			PowerServerConfig config = new PowerServerConfig(srvConfigUri);
			
			boolean finished = true;
			if (isHeadLess) {
				SetupServerWizardConsole wizard = new SetupServerWizardConsole(config);
				finished = wizard.isFinished(); 
			}
			else {
				SetupServerWizard wizard = new SetupServerWizard(null, config);
				finished = wizard.isFinished(); 
			}
			
			if (!finished) {
				LogUtil.info("Server not created due to not finish setting up");
				return null;
			}
			
			require = requireSetup(srvConfigUri);
			if (require) {
				LogUtil.info("Server not created");
				return null;
			}
			
			return new DefaultServerExt(new PowerServerConfig(srvConfigUri));
		}
		
	}


}
