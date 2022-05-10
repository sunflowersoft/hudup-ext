/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;
import java.util.List;

import javax.swing.JOptionPane;

import net.hudup.core.App;
import net.hudup.core.Constants;
import net.hudup.core.client.ExtraGateway;
import net.hudup.core.client.ExtraGatewayImpl;
import net.hudup.core.client.ExtraService;
import net.hudup.core.client.ExtraServiceImpl;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.server.DefaultServer;
import net.hudup.server.DefaultService;
import net.hudup.server.PowerServerConfig;

/**
 * This class is extended version of default server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ExtendedServer extends DefaultServer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with configuration.
	 * @param config power server configuration.
	 */
	public ExtendedServer(PowerServerConfig config) {
		super(config);
	}

	
	@Override
	protected DefaultService createService() {
		return new ExtendedService(trans, this);
	}


	@Override
	protected ExtraService createExtraService() {
		try {
			return new ExtraServiceImpl(this);
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		return null;
	}


	@Override
	protected ExtraGateway createExtraGateway() {
		return new ExtraGatewayImpl(this);
	}


	@Override
	protected boolean onWatcherLoadLib(Path libPath) {
		boolean ret = super.onWatcherLoadLib(libPath);
		if (!ret) return false;
		
		try {
			if ((service instanceof ExtendedService) && service.isOpened())
				((ExtendedService)service).loadEvaluators();
		} catch (Throwable e) {}
		
		try {
			ExtraService extraService = getExtraService();
			if (extraService != null) extraService.updateApps();
		} catch (Throwable e) {}
		
		return ret;
	}


	@Override
	protected void serverTasks() {
		super.serverTasks();
		
		//Task 1: Purging disconnected listeners.
		if (Constants.SERVER_PURGE_LISTENERS && (service != null) && (service instanceof ExtendedService)) {
			try {
				((ExtendedService)service).purgeListeners();
			} catch (Throwable e) {LogUtil.trace(e);}
			LogUtil.info("Server timer internal tasks: Purging disconnected listeners is successful");
		}
		
		//Task 2: doing applications.
		try {
			ExtraService extraService = getExtraService();
			if (extraService != null) {
				List<App> apps = extraService.getApps();
				for (App app : apps) {
					try {
						app.serverTask();
					} catch (Throwable e) {LogUtil.trace(e);}
				}
			}
		} catch (Throwable e) {LogUtil.trace(e);}
		
	}


	/**
	 * Show control panel.
	 */
	protected void showCP() {
		try {
			new ExtendedServerCP(this);
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
	 * @return extended server.
	 */
	public static PowerServer create() {
		return create(xURI.create(PowerServerConfig.serverConfig), new Creator() {
			@Override
			public PowerServer create(PowerServerConfig config) {
				return new ExtendedServer(config);
			}
		});
	}
	
	
}
