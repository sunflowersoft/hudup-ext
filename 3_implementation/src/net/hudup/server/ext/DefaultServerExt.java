/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.evaluate.ui.EvalCompoundGUI;
import net.hudup.server.DefaultServer;
import net.hudup.server.DefaultService;
import net.hudup.server.PowerServerConfig;
import net.hudup.server.ext.DefaultServiceExt.EvaluatorPair;
import net.hudup.server.ui.SetupServerWizard;

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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	protected DefaultService createService() {
		// TODO Auto-generated method stub
		return new DefaultServiceExt(trans);
	}


	@Override
	protected PopupMenu createSysTrayMenuExt() {
		// TODO Auto-generated method stub
        PopupMenu popup = new PopupMenu();

        MenuItem evItem = new MenuItem(I18nUtil.message("evaluator"));
        evItem.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					showEvaluator();
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
	 * Showing evaluator.
	 */
	protected void showEvaluator() {
		final DefaultServiceExt finalService = ((DefaultServiceExt)service);
		final List<Evaluator> evList;
		try {
			if (finalService == null || !isRunning()) {
				LogUtil.error("Service is not initialized yet or server is not running");
				JOptionPane.showMessageDialog(
						null, 
						"Service is not initialized yet or server is not running", 
						"Evaluator now shown", 
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			evList = finalService.getLocalEvaluators();
			if (evList == null || evList.size() == 0) {
				JOptionPane.showMessageDialog(
						null, 
						"There is no evaluator", 
						"There is no evaluator", 
						JOptionPane.INFORMATION_MESSAGE);
				return;
			}
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		final StartDlg dlgEvStarter = new StartDlg((JFrame)null, "List of evaluators") {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void start() {
				// TODO Auto-generated method stub
				final Evaluator ev = (Evaluator) getItemControl().getSelectedItem();
				dispose();
				
				try {
					if (!finalService.pairMap.containsKey(ev.getName())) {
						JOptionPane.showMessageDialog(
								null, 
								"There is no evaluator named '" + ev.getName() + "'.", 
								"No evaluator", 
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					EvaluatorPair pair = finalService.pairMap.get(ev.getName());
					if (pair.data.active) {
						JOptionPane.showMessageDialog(
								null, 
								"GUI of evaluator named '" + ev.getName() + "' is running.", 
								"Evaluator GUI running", 
								JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					new EvalCompoundGUI(pair.evaluator, null, pair.data);
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					LogUtil.error("Error in showing evaluator GUI.");
				}
			}
			
			@Override
			protected JComboBox<?> createItemControl() {
				// TODO Auto-generated method stub
				return new JComboBox<Evaluator>(evList.toArray(new Evaluator[0]));
			}
			
			@Override
			protected TextArea createHelp() {
				// TODO Auto-generated method stub
				TextArea toolkit = new TextArea("Thank you for choosing evaluators");
				toolkit.setEditable(false);
				return toolkit;
			}
		};
		
		dlgEvStarter.setSize(400, 150);
        dlgEvStarter.setVisible(true);
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
			
	        Image image = UIUtil.getImage("server-32x32.png");
			int confirm = JOptionPane.showConfirmDialog(
					null, 
					"Server not set up yet.\n Do you want to setup server?", 
					"Setup server", 
					JOptionPane.OK_CANCEL_OPTION, 
					JOptionPane.INFORMATION_MESSAGE, 
					image == null ? null : new ImageIcon(image));
			
			if (confirm != JOptionPane.OK_OPTION) {
				LogUtil.info("Server not created");
				return null;
			}
			
			
			PowerServerConfig config = new PowerServerConfig(srvConfigUri);
			SetupServerWizard dlg = new SetupServerWizard(null, config);
			
			if (!dlg.isFinished()) {
				LogUtil.info("Server not created");
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
