/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;

import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
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
	 * Constructor with specified server.
	 * @param server specified server.
	 */
	public ExtendedServerCP(PowerServer server) {
		this(server, null);
	}

	
	/**
	 * Constructor with specified server and binded URI of such server.
	 * @param server specified server
	 * @param bindUri bound URI.
	 */
	public ExtendedServerCP(PowerServer server, xURI bindUri) {
		super(server, bindUri);
	    setJMenuBar(createMenuBar());
	}

	
	/**
	 * Creating main menu bar.
	 * @return main menu bar.
	 */
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu(I18nUtil.message("tool"));
		mnBar.add(mnTool);
		
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

		mnTool.addSeparator();
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
					else
						JOptionPane.showMessageDialog(
							null, 
							"Server control panel does not provide evaluator control panel", 
							"Evaluator control panel not provided", 
							JOptionPane.ERROR_MESSAGE);
				}
			});
		mniEvaluatorCP.setMnemonic('e');
		mniEvaluatorCP.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniEvaluatorCP);

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
	
	
}
