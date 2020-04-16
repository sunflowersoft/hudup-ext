/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import java.awt.MenuItem;
import java.awt.PopupMenu;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.Socket;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import net.hudup.core.Constants;
import net.hudup.core.client.Gateway;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.client.ServerTrayIcon;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.listener.ui.ListenerCP;

/**
 * Because server supports many clients, it is more effective if deploying server on different platforms.
 * It means that we can distribute service layer and interface layer in different sites. Site can be a personal computer, mainframe, etc.
 * There are many scenarios of distribution, for example, many sites for service layer and one site for interface layer.
 * Interface layer has another component - {@code listener} component which is responsible for supporting distributed deployment.
 * Listener which has load balancing function is called {@code balancer}. For example, service layer is deployed on three sites and balancer is deployed on one site; whenever balancer receives user request, it looks up service sites and choose the site whose recommender service is least busy to require such recommender service to perform recommendation task.
 * Load balancing improves system performance and supports a huge of clients. Note that it is possible for the case that balancer or listener is deployed on more than one site.
 * <br>
 * The listener is modeled as this {@link Listener} class.
 * {@link Listener} is also a {@link Server} but it is not recommendation server.
 * {@link Listener} is deployed in interface layer, which supports distributed environment.
 * {@link Listener} stores a list of binded servers. Note, binded server is represented by {@link BindServer} class.
 * It can bind a new server by setting configuration and calling {@link #rebind()} method.
 * In case that there are many binded servers and one {@link Listener} then, {@link Listener} is responsible for dispatching user requests to its proper binded server.
 * {@link Listener} starts, pauses, resumes, and stops by its methods {@link #start()}, {@link #pause()}, {@link #resume()}, and {@link #stop()}, respectively.
 * <br>
 * Because listener binds servers, it implements {@link ServerStatusListener} in order to receive server statuses.
 * Because listener exposes a gateway that allows a control panel to control it, it implements {@link Gateway} interface.
 * <br>
 * Note, the sub-architecture of recommendation server (recommender) is inspired from the architecture of Oracle database management system (Oracle DBMS);
 * especially concepts of listener and share memory layer are borrowed from concepts &quot;Listener&quot; and &quot;System Global Area&quot; of Oracle DBMS, respectively,
 * available at <a href="https://docs.oracle.com/database/122/index.htm">https://docs.oracle.com/database/122/index.htm</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Listener extends SocketServer implements ServerStatusListener, Gateway {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Listener stores a list of binded servers. Such list is referred by this variable.
	 */
	protected BindServerList bindServerList = new BindServerList();

	
	/**
	 * RMI registry to register the gateway of listener as RMI object for remote calling.
	 * Such gateway allows remote control panel to control this listener remotely.
	 */
	protected Registry registry = null;


	/**
	 * Constructor with the specified configuration.
	 * @param config specified configuration for listener.
	 */
	public Listener(ListenerConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
		
		try {
			int port = NetUtil.getPort(config.getExportPort(), Constants.TRY_RANDOM_PORT);
			if (port < 0)
				throw new Exception("Invalid port number");
			config.setExportPort(port);

			registry = LocateRegistry.createRegistry(port);
			UnicastRemoteObject.exportObject(this, port);
			
			Naming.rebind(getGatewayBindUri().toString(), this);
			
			if (!createSysTray())
				showCP();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
			LogUtil.error("Listener/Balancer (socket server) failed to be constructed in constructor method, caused by " + e.getMessage());
			System.exit(0);
		}

	}

	
	/**
	 * {@link Listener} stores a list of binded servers. This method is responsible for binding such servers.
	 * This method is called inside or by derivative class, for example it is called by {@link #start()} method
	 */
	protected void rebind() {
		
		synchronized (bindServerList) {
			
			
			try {
				bindServerList.prune();
				boolean bound = bindServerList.bind(
						((ListenerConfig)config).getRemoteInfo(), this);
				
				if (bound)
					LogUtil.info("Listener/Balancer (socket server) bound remote server successfully");
				else
					LogUtil.error("Listener/Balancer (socket server) failed to bind remote server");
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				LogUtil.trace(e);
				LogUtil.error("Listener/Balancer (socket server) failed to bind remote server, caused by " + e.getMessage());
			}
			
		}
		
	}
	
	
	@Override
	public synchronized boolean start() {
		// TODO Auto-generated method stub
		if (isStarted()) return false;
		
		super.start();
		
		rebind();
		LogUtil.info("Listener/Balancer (socket server) exported at port " + ((ListenerConfig)config).getExportPort());
		
		return true;
	}
	
	
	@Override
	public synchronized boolean stop() {
		// TODO Auto-generated method stub
		if (!isStarted()) return false;
		
		super.stop();
		
		synchronized (bindServerList) {
			bindServerList.unbindAll();
		}
		
		return true;
	}
	
	
	@Override
	protected synchronized void shutdown() throws RemoteException {
		// TODO Auto-generated method stub
		if (config == null)
			return;

		stop();

    	try {
    		Naming.unbind(getGatewayBindUri().toString());
    		
        	UnicastRemoteObject.unexportObject(this, true);
    		UnicastRemoteObject.unexportObject(registry, true);
    		registry = null;
    	}
    	catch (Throwable e) {
    		LogUtil.trace(e);
    		LogUtil.error("Listener/Balancer (socket server) failed to shutdown, caused by" + e.getMessage());
    	}

		config.save();
		LogUtil.info("Listener/Balancer (socket server) shutdown");
		config = null;
		
		fireStatusEvent(new ServerStatusEvent(this, Status.exit));
	}


	@Override
	public synchronized void setConfig(DataConfig config) 
			throws RemoteException {
		if (isStarted())
			return;
		
		
		super.setConfig(config);
		rebind();
	}
	
	
	@Override
	protected AbstractDelegator delegate(Socket socket) {
		// TODO Auto-generated method stub
		return new Delegator(getBindServer(), socket, this);
	}


	/**
	 * Getting bound server.
	 * @return bound server.
	 */
	protected PowerServer getBindServer() {
		synchronized (bindServerList) {
			
			if (bindServerList.size() > 0)
				return bindServerList.get().getServer();
			else
				return null;
		}
	}
	
	
	@Override
	protected void serverTasks() {
		rebind();
	}
	
	
	@Override
	public synchronized void statusChanged(ServerStatusEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		switch(evt.getStatus()) {
		case started:
			break;
		case paused:
			pauseDelegators();
			break;
		case resumed:
			resumeDelegators();
			break;
		case stopped:
			stopDelegators();
			break;
		case setconfig:
			LogUtil.error("Status setconfig is invalid");
			break;
		case exit:
			synchronized (bindServerList) {
				bindServerList.prune();
			}
			break;
		}
	}


	@Override
	public boolean validateAccount(String account, String password, int privileges) {
		// TODO Auto-generated method stub
		boolean validated = true;
		
		PowerServer bindServer = getBindServer();
		if (bindServer == null)
			validated = false;
		else {
			try {
				validated = bindServer.validateAccount(account, password, privileges);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				validated = false;
			}
		}
		
		if (validated)
			return validated;
		if ((config == null) || !(config instanceof ListenerConfig))
			return false;
		
		//Checking password according to configuration.
		ListenerConfig listenerConfig = (ListenerConfig)config;
		String remoteAccount = listenerConfig.getRemoteAccount();
		if (remoteAccount == null)
			return false;
		else if (!remoteAccount.equals(account))
			return false;
		else {
			HiddenText pwd = listenerConfig.getRemotePassword();
			if (pwd == null)
				return false;
			else
				return pwd.getText().equals(password);
		}
	}


	@Override
	public boolean validateAdminAccount(String account, String password) {
		// TODO Auto-generated method stub
		boolean validated = true;
		
		PowerServer bindServer = getBindServer();
		if (bindServer == null)
			validated = false;
		else {
			try {
				validated = bindServer.validateAccount(account, password, DataConfig.ACCOUNT_ADMIN_PRIVILEGE);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				validated = false;
			}
		}
		
		if (validated)
			return validated;
		
		if ((config == null) || !(config instanceof ListenerConfig))
			return super.validateAdminAccount(account, password);
		
		ListenerConfig listenerConfig = (ListenerConfig)config;
		String remoteAccount = listenerConfig.getRemoteAccount();
		if (remoteAccount == null)
			return super.validateAdminAccount(account, password);
		
		//Checking password according to configuration.
		if (!remoteAccount.equals(account))
			return false;
		else {
			HiddenText pwd = listenerConfig.getRemotePassword();
			if (pwd == null)
				return false;
			else
				return pwd.getText().equals(password);
		}
	}

	
	/**
	 * For security, each listener exposes a gateway that allowing remote control panel to control this listener remotely.
	 * This method returns the URI of such gateway.
	 * @return gateway bind {@link xURI}
	 */
	protected xURI getGatewayBindUri() {
		ListenerConfig cfg = (ListenerConfig)config;
		return xURI.create(
				"rmi://localhost:" + cfg.getExportPort() + "/" + Protocol.GATEWAY);
	}


	@Override
	public Server getRemoteServer(String account, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		if (config == null)
			return null;
		else
			return this;
	}


	@Override
	public Service getRemoteService(String account, String password)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	/**
	 * When listener started, it has an icon together a pop-up menu on system tray of operating system.
	 * This method creates such icon and pop-up menu. The icon indicates current status of listener and the pop-up menu is to show the control panel that allows users to control the listener.
	 * @return whether create successfully the icon together the pop-up menu on system tray of operating system.
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
					// TODO Auto-generated method stub
					showCP();
				}
			});
            popup.add(cp);
            
            popup.addSeparator();

            MenuItem helpContent = new MenuItem(I18nUtil.message("help_content"));
            helpContent.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					try {
						new HelpContent(null);
					} 
					catch (Throwable ex) {
						// TODO Auto-generated catch block
						ex.printStackTrace();
					}
				}
			});
            popup.add(helpContent);

            
            MenuItem exit = new MenuItem(I18nUtil.message("exit"));
            exit.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					try {
						exit();
					} 
					catch (RemoteException re) {
						// TODO Auto-generated catch block
						LogUtil.trace(re);
					}
				}
			});
            popup.add(exit);
            
            
            TrayIcon trayIcon = new ServerTrayIcon(
            		this, 
            		UIUtil.getImage("listener-16x16.png"), 
            		UIUtil.getImage("listener-paused-16x16.png"), 
            		UIUtil.getImage("listener-stopped-16x16.png"), 
            		I18nUtil.message("hudup_listener"), 
            		popup); 
            trayIcon.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					showCP();
				}
			});
			SystemTray tray = SystemTray.getSystemTray();
			tray.add(trayIcon);
            
			return true;
		}
		catch (Exception e) {
			LogUtil.error("Listener/Balancer (socket server) failed to create system tray, caused by" + e.getMessage());
		}
		
		return false;
	}
	
	
	/**
	 * This method creates and shows the control panel represented by {@link ListenerCP} class that allows users to control this listener such as starting and stopping listener.
	 * The control panel can connect the listener remotely.
	 */
	protected void showCP() {
		try {
			new ListenerCP(this);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			/*
			 * It is possible that current Java environment does not support GUI.
			 * Use of GraphicsEnvironment.isHeadless() tests Java GUI.
			 * Hence, create control panel with console here.
			 */
		}
	}


	/**
	 * This static method is used to create listener from the default listener configuration specified by {@link ListenerConfig#listenerConfig}.
	 * @return {@link Listener} from the default balancer configuration specified by {@link ListenerConfig#listenerConfig}.
	 */
	public static Listener create() {
		ListenerConfig config = new ListenerConfig(xURI.create(ListenerConfig.listenerConfig));
		
		return new Listener(config);
	}
	
	
}
