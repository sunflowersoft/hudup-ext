/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server;

import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Scanner;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.Util;
import net.hudup.core.client.ExtraService;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.ServerTrayIcon;
import net.hudup.core.client.Service;
import net.hudup.core.client.VirtualFileService;
import net.hudup.core.client.VirtualStorageService;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.UnitList;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.server.ui.PowerServerCP;
import net.hudup.server.ui.SetupServerWizard;
import net.hudup.server.ui.SetupServerWizardConsole;

/**
 * {@link DefaultServer} class is default and complete implementation of {@link net.hudup.core.client.Server} interface.
 * Actually, default server inherits {@link PowerServerImpl} class.
 * Default server creates and manages {@link DefaultService}. Developers are suggested to build up their own servers.
 * <br>
 * Note, the sub-architecture of recommendation server (recommender) is inspired from the architecture of Oracle database management system (Oracle DBMS);
 * especially concepts of listener and share memory layer are borrowed from concepts &quot;Listener&quot; and &quot;System Global Area&quot; of Oracle DBMS, respectively,
 * available at <a href="https://docs.oracle.com/database/122/index.htm">https://docs.oracle.com/database/122/index.htm</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DefaultServer extends PowerServerImpl {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Internal default service.
	 */
	protected DefaultService service = null;
	
	
	/**
	 * Internal storage service.
	 */
	protected VirtualStorageService storageService = null;
	
	
	/**
	 * Internal extra service.
	 */
	protected ExtraService extraService = null;

	
	/**
	 * Constructor with configuration.
	 * @param config power server configuration.
	 */
	protected DefaultServer(PowerServerConfig config) {
		super(config);
		
		service = createService();
		storageService = createStorageService();
		extraService = createExtraService();
		
		if (Constants.SERVER_UI) {
			if (!createSysTray()) showCP();
		}
	}


	@Override
	protected Transaction createTransaction() {
		return new TransactionImpl();
	}

	
	/**
	 * Create default service.
	 * @return service.
	 */
	protected DefaultService createService() {
		return new DefaultService(trans);
	}
	
	
	/**
	 * Create storage service.
	 * @return storage service.
	 */
	protected VirtualStorageService createStorageService() {
		return new VirtualFileService();
	}
	
	
	/**
	 * Create extra service.
	 * @return extra service.
	 */
	protected ExtraService createExtraService() {
		return null;
	}

	
	@Override
	protected void doWhenStart() {
		service.open(config);
	}
	
	
	@Override
	protected void doWhenStop() {
		try {
			Collection<EvaluatorConfig> configs = service.evaluatorConfigMap.values();
			for (EvaluatorConfig config : configs) {
				if (config != null)	config.save();
			}
			
			service.evaluatorConfigMap.clear();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		service.close();
	}
	
	
	@Override
	protected void serverTasks() {
		
		// Task 1: Period learning.
		if (config.isPeriodLearn() && !config.isDatasetEmpty()) { //These methods are used to prevent time consuming from learning internal recommender.
			try {
//				DefaultService newService = createService();
//				newService.open(config); //This statement is important, which takes much time.
//				newService.transferTo(service);
				
				service.updateRecommender();
				if (service.recommender != null)
					LogUtil.info("Server timer internal tasks: Learning recommendation algorithm is successful");
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		
		//Task 2: clearing log files
		try {
			long m10 = 1024*1024*10; //10 MB
			File log = new File(Constants.LOGS_DIRECTORY + "/" + LogUtil.LOG_FILENAME);
			if (log.exists() && log.length() > m10) {
				try {
					new PrintWriter(log).close();
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
			
			File nextupdate = new File(Constants.LOGS_DIRECTORY + "/" + LogUtil.NEXTUPDATE_FILENAME);
			if (nextupdate.exists() && nextupdate.length() > m10) {
				try {
					new PrintWriter(nextupdate).close();
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	@Override
	public synchronized void pluginChanged(PluginChangedEvent evt) throws RemoteException {
		super.pluginChanged(evt);
		
		if (service != null) service.pluginChanged(evt);
	}


	@Override
	protected void initStorageSystem() {
		
		try {
			DataDriver driver = DataDriver.create(config.getStoreUri());
			if (driver != null && driver.isDbServer())
				driver.loadDriver();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Server fail to initialize storage system, error is " + e.getMessage());
		}
	}


	@Override
	protected void destroyStorageSystem() {
		
		DataDriver driver = DataDriver.create(config.getStoreUri());
		
		if (driver != null && driver.getType() == DataType.derby_engine) {
			try {
				DriverManager.getConnection("jdbc:derby:;shutdown=true");
			} 
			catch (Throwable e) {
				if (e instanceof SQLException) {
					if (! ((SQLException)e).getSQLState().equals("XJ015") ) {
						LogUtil.trace(e);
						LogUtil.error("Server fail to destroy storage system (shutdown Derby engine), error is " + e.getMessage());
					}
				}
				else {
					LogUtil.trace(e);
					LogUtil.error("Server fail to destroy storage system (shutdown Derby engine), error is " + e.getMessage());
				}
			}
		}
		
	}


	@Override
	public synchronized boolean validateAccount(String account, String password, int privileges) 
			throws RemoteException {
		
		if (service.isOpened()) {
			Provider provider = service.getProvider();
			if (provider == null)
				return true;
			else {
				boolean validated = provider.validateAccount(account, password, privileges);
				if (validated)
					return true;
				else if (account.equals(DataConfig.ADMIN_ACCOUNT)) {
					String pwd = Util.getHudupProperty(DataConfig.ADMIN_ACCOUNT);
					if (pwd == null)
						return false;
					else
						return password.equals(pwd);
				}
				else
					return false;
				
			}
		}
		
		ProviderImpl provider = new ProviderImpl(config);
		boolean validate = provider.validateAccount(account, password, privileges);
		provider.close();
		return validate;
	}

	
	@Override
	public synchronized int getPrivileges(String account, String password) throws RemoteException {
		if (service.isOpened()) {
			Provider provider = service.getProvider();
			if (provider == null)
				return 0;
			else {
				int privs = provider.getPrivileges(account, password);
				if (privs != 0)
					return privs;
				else if (account.equals(DataConfig.ADMIN_ACCOUNT)) {
					String pwd = Util.getHudupProperty(DataConfig.ADMIN_ACCOUNT);
					if (pwd == null)
						return 0;
					else
						return password.equals(pwd) ? DataConfig.ACCOUNT_ADMIN_PRIVILEGE : 0;
				}
				else
					return 0;
				
			}
		}
		
		ProviderImpl provider = new ProviderImpl(config);
		int privs = provider.getPrivileges(account, password);
		provider.close();
		return privs;
	}


	@Override
	public Service getService() throws RemoteException {
		return service;
	}


	@Override
	public VirtualStorageService getStorageService() throws RemoteException {
		return storageService;
	}


	@Override
	public ExtraService getExtraService() throws RemoteException {
		return extraService;
	}


	/**
	 * Create system tray.
	 * @return whether create system tray successfully.
	 */
	protected boolean createSysTray() {
		if (!SystemTray.isSupported())
			return false;
		
		try {
            PopupMenu popup = new PopupMenu();

            MenuItem cp = new MenuItem(I18nUtil.message("control_panel"));
            cp.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showCP();
				}
			});
            popup.add(cp);
            
            //Creating extended menu items.
            PopupMenu extMenu = createSysTrayMenuExt();
            if (extMenu != null && extMenu.getItemCount() > 0) {
                popup.addSeparator();
            	for (int i = 0; i < extMenu.getItemCount(); i++) {
            		MenuItem menuItem = extMenu.getItem(i);
                    popup.add(menuItem);
            	}
            }
            
            popup.addSeparator();

            MenuItem helpContent = new MenuItem(I18nUtil.message("help_content"));
            helpContent.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						new HelpContent(null);
					} 
					catch (Throwable ex) {
						LogUtil.trace(ex);
					}
				}
			});
            popup.add(helpContent);

            MenuItem exit = new MenuItem(I18nUtil.message("exit"));
            exit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						exit();
					} 
					catch (Throwable ex) {
						LogUtil.trace(ex);
					}
				}
			});
            popup.add(exit);
            
            
            TrayIcon trayIcon = new ServerTrayIcon(
            		this, 
            		UIUtil.getImage("server-16x16.png"), 
            		UIUtil.getImage("server-paused-16x16.png"), 
            		UIUtil.getImage("server-stopped-16x16.png"), 
            		I18nUtil.message("hudup_server") + " " + Constants.VERSION, 
            		popup); 
            		
            trayIcon.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showCP();
				}
			});
            
//            trayIcon.addMouseListener(new MouseAdapter() {
//
//				@Override
//				public void mouseClicked(MouseEvent e) {
//					super.mouseClicked(e);
//					if(!SwingUtilities.isLeftMouseButton(e) || e.getClickCount() >= 2)
//						return;
//				}
//            	
//			});
			
			SystemTray tray = SystemTray.getSystemTray();
			tray.add(trayIcon);

			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Server fail to create system tray fail, caused by" + e.getMessage());
		}
		
		return false;
	}
	
	
	/**
	 * Creating extended pop-up menu of system tray.
	 * @return extended pop-up menu of system tray or null if there is no extended pop-up menu.
	 */
	protected PopupMenu createSysTrayMenuExt() {
		return null;
	}
	
	
	/**
	 * Show control panel.
	 */
	protected void showCP() {
		try {
			new PowerServerCP(this);
		}
		catch (Throwable e) {
			//LogUtil.trace(e);
			LogUtil.error("Server fail to show control panel, caused by " + e.getMessage());
			
			/*
			 * It is possible that current Java environment does not support GUI.
			 * Use of GraphicsEnvironment.isHeadless() tests Java GUI.
			 * Hence, create control panel with console here or improve PowerServerCP to support console.
			 */
		}
	}
	
	
	/**
	 * Performing server tasks with risks because server tasks are often called by internal timer.
	 * This method is not important but please use carefully it.
	 */
	protected void doServerTasksWithRisks() {
		serverTasks();
	}
	
	
	/**
	 * This class represents the creator to create server.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static interface Creator {
		
		/**
		 * Create server from configuration.
		 * @param config specified configuration.
		 * @return server created from configuration.
		 */
		PowerServer create(PowerServerConfig config);
		
	}
	
	
	/**
	 * Static method to create server.
	 * @return default server.
	 */
	public static PowerServer create() {
		return create(xURI.create(PowerServerConfig.serverConfig), new Creator() {
			@Override
			public PowerServer create(PowerServerConfig config) {
				return new DefaultServer(config);
			}
		});
	}
	
	
	/**
	 * Static method to create extended default server with specified configuration URI.
	 * @param srvConfigUri specified configuration URI.
	 * @param creator the creator to create server.
	 * @return default server.
	 */
	protected static PowerServer create(xURI srvConfigUri, Creator creator) {
		boolean require = requireSetup(srvConfigUri);
		PowerServerConfig config = new PowerServerConfig(srvConfigUri);
		
		if (!require)
			return creator.create(config);
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
					SetupServerWizardConsole wizard = new SetupServerWizardConsole(config);
					finished = wizard.isFinished(); 
				}
				else {
					SetupServerWizard wizard = new SetupServerWizard(null, config, null);
					finished = wizard.isFinished(); 
				}
			}
			else {
				SetupServerWizardConsole wizard = new SetupServerWizardConsole(config);
				finished = wizard.isFinished(); 
			}
			
			if (finished && !requireSetup(srvConfigUri))
				return creator.create(config);
			else {
				LogUtil.error("Server not created");
				return null;
			}
		}
		
	}
	
	
	/**
	 * Testing whether requiring set up given configuration URI.
	 * @param srvConfigUri given configuration URI.
	 * @return whether requiring set up.
	 */
	public static boolean requireSetup(xURI srvConfigUri) {
		
		try {
			UriAdapter adapter = new UriAdapter(srvConfigUri);
			
			if (!adapter.exists(srvConfigUri)) {
				adapter.close();
				return true;
			}
			adapter.close();
			
			PowerServerConfig config = new PowerServerConfig(srvConfigUri);
			
			if (config.size() == 0)
				return true;
			else {
				Provider provider = new ProviderImpl(config);
				
				UnitList unitList = provider.getUnitList();
				UnitList basicUnitList = DataConfig.getBasicUnitList();
				provider.close();

				if (!unitList.contains(basicUnitList))
					return true;
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Server requiring set up cause error " + e.getMessage());
			return true;
		}
		
		return false;
		
	}

	
}
