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
import java.rmi.RemoteException;

import net.hudup.core.client.PowerServer;
import net.hudup.core.client.ServerTrayIcon;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.listener.ui.BalancerCP;

/**
 * Because server supports many clients, it is more effective if deploying server on different platforms.
 * It means that we can distribute service layer and interface layer in different sites. Site can be a personal computer, mainframe, etc.
 * There are many scenarios of distribution, for example, many sites for service layer and one site for interface layer.
 * Interface layer has another component - {@code listener} component which is responsible for supporting distributed deployment.
 * Listener which has load balancing function is called {@code balancer}. For example, service layer is deployed on three sites and balancer is deployed on one site; whenever balancer receives user request, it looks up service sites and choose the site whose recommender service is least busy to require such recommender service to perform recommendation task.
 * Load balancing improves system performance and supports a huge of clients. Note that it is possible for the case that balancer or listener is deployed on more than one site.
 * <br>
 * The balancer is modeled as this {@link Balancer} class.
 * {@link Balancer} derives from {@link Listener} and overrides the {@link Listener#rebind()} method in order to support balancing function while it inherits all other functions of {@link Listener}.
 * {@link Balancer} selects least busy binded server to which user requests are dispatched.
 * Therefore, {@link Balancer} improves system performance more than {@link Listener} does. In general, {@link Listener} and {@link Balancer} are main components of interface layer.
 * Note, binded server is represented by {@link BindServer} class.
 * <br>
 * Note, the sub-architecture of recommendation server (recommender) is inspired from the architecture of Oracle database management system (Oracle DBMS);
 * especially concepts of listener and share memory layer are borrowed from concepts &quot;Listener&quot; and &quot;System Global Area&quot; of Oracle DBMS, respectively,
 * available at <a href="https://docs.oracle.com/database/122/index.htm">https://docs.oracle.com/database/122/index.htm</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Balancer extends Listener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor with specified configuration of balancer.
	 * @param config specified configuration of balancer.
	 */
	public Balancer(BalancerConfig config) {
		super(config);
	}

	
	@Override
	protected void rebind() {
		synchronized (bindServerList) {
			
			try {
				bindServerList.prune();
				bindServerList.bind(
						((BalancerConfig)config).getRemoteInfoList(), this);
				
				if (bindServerList.size() > 0)
					LogUtil.info("Listener/Balancer (socket server) bound list of remote servers successfully");
				else
					LogUtil.error("Listener/Balancer (socket server) failed to bind list of remote servers");
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Listener/Balancer (socket server) failed to bind list of remote servers, caused by " + e.getMessage());
			}
			
		}
	}

	
	@Override
	protected PowerServer getBindServer() {
		synchronized (bindServerList) {
			BindServer bindServer = bindServerList.getIdleServer();
			if (bindServer != null)
				return bindServer.getServer();
			else
				return null;
		}
	}


	@Override
	protected void showCP() {
		try {
			new BalancerCP(this);
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


	@Override
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
            
            popup.addSeparator();

            MenuItem helpContent = new MenuItem(I18nUtil.message("help_content"));
            helpContent.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						new HelpContent(null);
					} 
					catch (Throwable ex) {
						ex.printStackTrace();
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
					catch (RemoteException re) {
						LogUtil.trace(re);
					}
				}
			});
            popup.add(exit);
            
            
            TrayIcon trayIcon = new ServerTrayIcon(
            		this, 
            		UIUtil.getImage("balancer-16x16.png"), 
            		UIUtil.getImage("balancer-paused-16x16.png"), 
            		UIUtil.getImage("balancer-stopped-16x16.png"), 
            		I18nUtil.message("hudup_balancer"), 
            		popup); 
            trayIcon.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showCP();
				}
			});
			SystemTray tray = SystemTray.getSystemTray();
			tray.add(trayIcon);
            
			return true;
		}
		catch (Exception e) {
			LogUtil.error("Listener/Balancer (socket server) failed to create system tray fail, caused by" + e.getMessage());
		}
		
		return false;
	}


	/**
	 * This static method is used to create balancer from the default balancer configuration specified by {@link BalancerConfig#balancerConfig}.
	 * @return {@link Balancer} from the default balancer configuration specified by {@link BalancerConfig#balancerConfig}.
	 */
	public static Balancer create() {
		BalancerConfig config = new BalancerConfig(xURI.create(BalancerConfig.balancerConfig));

		return new Balancer(config);
	}
	
	
}
