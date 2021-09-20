/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import net.hudup.core.client.Connector;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.LightRemoteServerCP;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.RemoteServerCP;
import net.hudup.core.client.Server;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.server.ui.PowerServerCP;

/**
 * This class is extended version of power server control panel.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtendedServerCP extends PowerServerCP {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor with specified server and connection information of such server.
	 * @param server specified server
	 * @param connectInfo connection information of the specified.
	 */
	public ExtendedServerCP(PowerServer server, ConnectInfo connectInfo) {
		super(server, connectInfo);
	}


	/**
	 * Constructor with specified server.
	 * @param server specified server.
	 */
	public ExtendedServerCP(PowerServer server) {
		this(server, null);
	}

	
	@Override
	protected JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu(I18nUtil.message("tool"));
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);
		
		PowerServerCP cp = this;
		JMenuItem mniWorkingDirectoryManager = new JMenuItem(
			new AbstractAction(I18nUtil.message("working_directory_manager")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						WorkingDirectoryManager.showManager(cp, server.getStorageService());
					} catch (RemoteException ex) {LogUtil.trace(ex);}
				}
			});
		mniWorkingDirectoryManager.setMnemonic('w');
		mnTool.add(mniWorkingDirectoryManager);
		mnTool.addSeparator();

		JMenuItem mniInstallService = new JMenuItem(
			new AbstractAction(I18nUtil.message("install_service")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					installService(true);
				}
			});
		mniInstallService.setMnemonic('i');
//		mnTool.add(mniInstallService);

		JMenuItem mniUninstallService = new JMenuItem(
			new AbstractAction(I18nUtil.message("uninstall_service")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					installService(false);
				}
			});
		mniUninstallService.setMnemonic('u');
//		mnTool.add(mniUninstallService);

		JMenuItem mniEvaluatorCP = new JMenuItem(
			new AbstractAction(I18nUtil.message("evaluator")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (server instanceof ExtendedServer)
						((ExtendedServer)server).showEvaluatorCP();
					else {
						try {
							EvaluatorCPList ecp = new EvaluatorCPList(server.getService(), connectInfo);
							ecp.setVisible(true);
						} catch (Exception ex) {LogUtil.trace(ex);}
					}
				}
			});
		mniEvaluatorCP.setMnemonic('e');
		mniEvaluatorCP.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniEvaluatorCP);

		addToToolMenu(mnTool);

		
		JMenu mnApps = new JMenu(I18nUtil.message("applications"));
		mnApps.setMnemonic('a');
		addToAppsMenu(mnApps);
		if (mnApps.getItemCount() > 0) mnBar.add(mnApps);

		
		JMenu mnHelp = new JMenu(I18nUtil.message("help"));
		mnHelp.setMnemonic('h');
		mnBar.add(mnHelp);
		
		PowerServerCP thisCP = this;
		JMenuItem mniHelpContent = new JMenuItem(
			new AbstractAction(I18nUtil.message("help_content")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					new HelpContent(thisCP);
				}
			});
		mniHelpContent.setMnemonic('c');
		mniHelpContent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mnHelp.add(mniHelpContent);
		
		addToHelpMenu(mnHelp);
		

		return mnBar;
	}
	
	
	/**
	 * Installing service
	 * @param install flag to indicate whether to install or uninstall service.
	 */
	private boolean installService(boolean install) {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running.\nUnable to install / uninstall service", 
					"Server running", 
					JOptionPane.ERROR_MESSAGE);
				return false;
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
			return false;
		}

		JOptionPane.showMessageDialog(
			this, 
			"Not support installing / uninstalling service yet.\nPlease use associated YAJWS software instead.", 
			"Not support yet", 
			JOptionPane.ERROR_MESSAGE);
		return false;
			
			
//		WrappedService w = new WrappedService();
//		Configuration conf = w.getLocalConfiguration();
//		
//		conf.addProperty("wrapper.java.command", "java");
//		//conf.addProperty("wrapper.app.account", "Loc Nguyen");
//		//conf.addProperty("wrapper.app.password", "Inspire@789");
//
//		xURI curDir = xURI.create(".");
//		conf.addProperty("wrapper.working.dir", curDir.getPath());
//		conf.addProperty("wrapper.tmp.path", curDir.concat("working/temp").getPath());
//		conf.addProperty("wrapper.logfile", curDir.concat("working/log/hudup.log").getPath());
//
//		conf.addProperty("wrapper.java.classpath.1", curDir.concat("hudup.jar").getPath());
//		conf.addProperty("wrapper.java.classpath.2", curDir.concat("hudup-runtime-lib.jar").getPath());
//		conf.addProperty("wrapper.java.classpath.3", curDir.concat("sim.jar").getPath());
//		conf.addProperty("wrapper.java.classpath.4", curDir.concat("sim-runtime-lib.jar").getPath());
//		conf.addProperty("wrapper.java.classpath.5", curDir.concat("bin").getPath());
//		conf.addProperty("wrapper.java.classpath.6", curDir.concat("lib/*").getPath());
//		conf.addProperty("wrapper.java.classpath.7", curDir.concat("lib/ext/*").getPath());
//
//		conf.addProperty("wrapper.java.app.mainclass", "net.hudup.Server");
//		
//		conf.addProperty("wrapper.ntservice.name", "hudupserver");
//		conf.addProperty("wrapper.ntservice.displayname", "Hudup server");
//		conf.addProperty("wrapper.ntservice.description", "Hudup is recommendation server");
//		//conf.addProperty("wrapper.app.account", "Loc Nguyen");
//		//conf.addProperty("wrapper.app.password", "Inspire@789");
//		
//		conf.addProperty("wrapper.startup.timeout", 300);
//		conf.addProperty("wrapper.shutdown.timeout", 300);
//
//		w.init();
//		
//		if (install) {
//			if (w.install()) {
//				JOptionPane.showMessageDialog(
//					this, 
//					"Successful to install Hudup service", 
//					"Successful to install service", 
//					JOptionPane.INFORMATION_MESSAGE);
//				return true;
//			}
//			else {
//				JOptionPane.showMessageDialog(
//					this, 
//					"Fail to install Hudup service", 
//					"Fail to install service", 
//					JOptionPane.ERROR_MESSAGE);
//				return false;
//			}
//		}
//		else {
//			if (w.uninstall()) {
//				JOptionPane.showMessageDialog(
//					this, 
//					"Successful to uninstall Hudup service", 
//					"Successful to uninstall service", 
//					JOptionPane.INFORMATION_MESSAGE);
//				return true;
//			}
//			else {
//				JOptionPane.showMessageDialog(
//					this, 
//					"Fail to uninstall Hudup service", 
//					"Fail to uninstall service", 
//					JOptionPane.ERROR_MESSAGE);
//				return false;
//			}
//		}
	}
	
	
	protected void addToToolMenu(JMenu mnTool) {
		
	}
	
	
	protected void addToHelpMenu(JMenu mnHelp) {
		
	}
	
	
	protected void addToAppsMenu(JMenu mnApps) {
		
	}

	
	/**
	 * Main method.
	 * @param args specified arguments.
	 */
	public static void main(String[] args) {
		boolean console = args != null && args.length >= 1
				&& args[0] != null && args[0].toLowerCase().equals("console");
		if (console || GraphicsEnvironment.isHeadless()) {
			LightRemoteServerCP.console();
			return;
		}

		Connector dlg = Connector.connect();
        Image image = UIUtil.getImage("server-32x32.png");
        if (image != null) dlg.setIconImage(image);
		
		Server server = dlg.getServer();
		ConnectInfo connectInfo = dlg.getConnectInfo();
		if (server == null) {
			JOptionPane.showMessageDialog(
				null, "Fail to retrieve server", "Fail to retrieve server", JOptionPane.ERROR_MESSAGE);
		}
		else if (connectInfo.bindUri != null && !connectInfo.pullMode && Connector.isPullModeRequired(server)) {
			JOptionPane.showMessageDialog(null,
				"Can't retrieve server because PULL MODE is not set\n" +
				"whereas the remote server requires PULL MODE.\n" +
				"You have to check PULL MODE in connection dialog.",
				"Retrieval to server failed", JOptionPane.ERROR_MESSAGE);
		}
		else if (!(server instanceof PowerServer))
			new RemoteServerCP(server, connectInfo);
		else
			new ExtendedServerCP((PowerServer)server, connectInfo);
	}
	
	
}
