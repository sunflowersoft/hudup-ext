/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.hudup.core.Constants;
import net.hudup.core.client.Connector;
import net.hudup.core.client.Connector.ConnectType;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.LightRemoteServerCP;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.listener.BalancerConfig;
import net.hudup.listener.RemoteInfoList;

/**
 * This graphic user interface (GUI) as a window application {@link JFrame} allows users to control remotely balancer
 * such as starting, stopping, pausing, resuming, and configuring balancer. It is called balancer control panel.
 * This control panel updates balancer status immediately because it has two advanced features as follows:
 * <ul>
 * <li>This control panel is itself a {@link ServerStatusListener} that is registered with balancer before.</li>
 * <li>This control panel is itself a remote RMI object that allows other applications to interact with it via RMI protocol.</li>
 * </ul>
 * Therefore, every time the balancer changes status, the balancer calls the method {@link #statusChanged(ServerStatusEvent)} of this control panel to update balancer current status immediately.
 * <br>
 * RMI is abbreviation of Java Remote Method Invocation for remote interaction in client-server architecture. The tutorial of RMI is available at <a href="https://docs.oracle.com/javase/tutorial/rmi">https://docs.oracle.com/javase/tutorial/rmi</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class BalancerCP extends JFrame implements ServerStatusListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Current server status.
	 */
	protected Status currentStatus = Status.unknown;
	
	/**
	 * Pane of balancer configuration as {@link SysConfigPane}.
	 */
	protected SysConfigPane paneConfig = null;
	
	/**
	 * Button to exit balancer.
	 */
	protected JButton btnExitListener = null;
	
	/**
	 * Button to start balancer.
	 */
	protected JButton btnStart = null;
	
	/**
	 * Button to pause/resume balancer.
	 */
	protected JButton btnPauseResume = null;

	/**
	 * Button to stop balancer.
	 */
	protected JButton btnStop = null;
	
	/**
	 * Button to apply changes of balancer configuration.
	 */
	protected JButton btnApplyConfig = null;
	
	/**
	 * Button to reset balancer configuration.
	 */
	protected JButton btnResetConfig = null;

	/**
	 * Button to refresh this control panel.
	 */
	protected JButton btnRefresh = null;
	
	/**
	 * Button to set (add) remote balancer into the list of remote information of this control panel.
	 * Such list is stored and shown in {@link #paneConfig}.
	 */
	protected JButton btnSetupRemoteHosts = null;
	
	/**
	 * Reference to remote balancer. Note, balancer is special listener.
	 */
	protected Server listener = null;
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = null;
	
	/**
	 * Internal time counter.
	 * Every period in seconds, this control panel updates itself by server information.
	 */
	protected Timer timer = null;
	

	/**
	 * Constructor with specified server and connection information of such server.
	 * @param server specified server
	 * @param connectInfo connection information of the specified.
	 */
	public BalancerCP(Server listener, ConnectInfo connectInfo) {
		super("Balancer control panel");
		
		try {
			this.connectInfo = connectInfo != null ? connectInfo : new ConnectInfo();
			this.listener = listener;

			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
	        
			Image image = UIUtil.getImage("balancer-32x32.png");
	        if (image != null)
	        	setIconImage(image);
			
			Container container = getContentPane();
			JTabbedPane main = new JTabbedPane();
			container.add(main);
			
			main.add(createGeneralPane(), "General");
	
			bindServer();

			updateControls();
			
			ConnectInfo thisConnectInfo = this.connectInfo;
			addWindowListener(new WindowAdapter() {

				@Override
				public void windowOpened(WindowEvent e) {
					super.windowOpened(e);
					
					if (timer != null || !thisConnectInfo.pullMode) return;
					
					timer = new Timer();
					long milisec = thisConnectInfo.accessPeriod < Counter.PERIOD*1000 ? Counter.PERIOD*1000 : thisConnectInfo.accessPeriod;
					timer.schedule(
						new TimerTask() {
						
							@Override
							public void run() {
								Status status = ServerStatusEvent.getStatus(listener);
								if (status == Status.unknown || ServerStatusEvent.isSame(status, currentStatus))
									return;

								updateControls();
							}
						}, 
						milisec, 
						milisec);
				}
				
			});
			
			setVisible(true);
		}
		catch (RemoteException e) {
			LogUtil.trace(e);
		}
		
	}

	
	/**
	 * Constructor with reference to local balancer. The binded URI is null.
	 * @param listener local balancer.
	 */
	public BalancerCP(Server listener) {
		this(listener, null);
	}

	
	/**
	 * Binding (exposing) this control panel as remote RMI object so that server or other applications can interact with it via RMI protocol.
	 * The internal variable {@link #bindUri} pointing to where to locate this control panel.
	 */
	protected void bindServer() {
		if (connectInfo.bindUri == null) {
			try {
				listener.addStatusListener(this);
			}
			catch (Throwable e1) {e1.printStackTrace();}
		}
		else if (!connectInfo.pullMode) {
			try {
				UnicastRemoteObject.exportObject(this, connectInfo.bindUri.getPort());
				listener.addStatusListener(this);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				try {
		        	UnicastRemoteObject.unexportObject(this, true);
				}
				catch (Throwable e1) {e1.printStackTrace();}
			}
		}
		
		btnRefresh.setVisible(connectInfo.pullMode);
		
		/*
		 * Hide pause/resume button in some cases because of error remote lock. The next version will be improve this method.
		 * So the following code lines need to be removed.
		 */
		btnPauseResume.setVisible(connectInfo.bindUri == null);
	}

	
	/**
	 * Creating the general pane containing all GUI controls (buttons).
	 * @return {@link JPanel} containing all GUI controls (buttons).
	 * @throws RemoteException if any error raises.
	 */
	protected JPanel createGeneralPane() throws RemoteException {
		JPanel general = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		general.add(body, BorderLayout.CENTER);
		
		body.add(new JLabel("Balancer configuration"), BorderLayout.NORTH);
		paneConfig = new SysConfigPane();
		paneConfig.setToolbarVisible(false);
		paneConfig.setControlVisible(false);
		paneConfig.update(listener.getConfig());
		body.add(paneConfig, BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
		general.add(footer, BorderLayout.SOUTH);
		
		JPanel configbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		footer.add(configbar);
		
		btnApplyConfig = new JButton("Apply configuration");
		btnApplyConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				applyConfig();
			}
		});
		configbar.add(btnApplyConfig);

		btnResetConfig = new JButton("Reset configuration");
		btnResetConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resetConfig();
			}
		});
		configbar.add(btnResetConfig);

		
		btnSetupRemoteHosts = new JButton("Setup remote hosts");
		btnSetupRemoteHosts.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setupRemoteHosts();
			}
		});
		configbar.add(btnSetupRemoteHosts);

		
		JPanel mainToolbar = new JPanel(new BorderLayout());
		footer.add(mainToolbar);
		
		JPanel leftToolbar = new JPanel(new FlowLayout());
		mainToolbar.add(leftToolbar, BorderLayout.WEST);
		
		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateControls();
			}
		});
		leftToolbar.add(btnRefresh);

		
		btnExitListener = new JButton("Exit balancer");
		btnExitListener.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				exit();
			}
		});
		leftToolbar.add(btnExitListener);

		
		JPanel centerToolbar = new JPanel(new FlowLayout());
		mainToolbar.add(centerToolbar, BorderLayout.CENTER);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				start();
			}
		});
		centerToolbar.add(btnStart);

		btnPauseResume = new JButton("Pause");
		btnPauseResume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseResume();
			}
		});
		centerToolbar.add(btnPauseResume);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
		});
		centerToolbar.add(btnStop);
		
		return general;
	}


	/**
	 * Start balancer remotely.
	 */
	protected synchronized void start() {
		try {
			listener.start();
			
			if (connectInfo.pullMode)
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Pause/resume balancer remotely.
	 */
	protected synchronized void pauseResume() {
		try {
			if (listener.isPaused())
				listener.resume();
			else if (listener.isRunning())
				listener.pause();
			
			if (connectInfo.pullMode)
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Stop balancer remotely.
	 */
	protected synchronized void stop() {
		try {
			listener.stop();
			
			if (connectInfo.pullMode)
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Exit balancer remotely. After exiting, balancer is destroyed and cannot be re-started.
	 */
	protected synchronized void exit() {
		if (timer != null) timer.cancel();
		timer = null;

		try {
			if (listener != null && (connectInfo.bindUri == null || !connectInfo.pullMode))
				listener.removeStatusListener(this);
		} catch (Exception e) {LogUtil.trace(e);}
		
		try {
			if (listener != null) listener.exit();
		} catch (Exception e) {}
		listener = null;
		
		dispose();
	}

	
	/**
	 * Apply changes into balancer configuration remotely.
	 */
	protected synchronized void applyConfig() {
		try {
			if (listener.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Balancer running. Can't save configuration", 
					"Balancer running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			boolean apply = paneConfig.apply();
			if (!apply) {
				JOptionPane.showMessageDialog(
					this, 
					"Cannot apply configuration", 
					"Cannot apply configuration", 
					JOptionPane.ERROR_MESSAGE);
			}
			else {
				listener.setConfig(
					(DataConfig)paneConfig.getPropTable().getPropList());

				JOptionPane.showMessageDialog(
					this, 
					"Apply configuration to balancer successfully", 
					"Apply configuration successfully", 
					JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	

	/**
	 * Reset balancer configuration.
	 */
	protected synchronized void resetConfig() {
		try {
			if (listener.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Balancer running. Can't reset configuration", 
					"Balancer running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			paneConfig.reset();
			int confirm = JOptionPane.showConfirmDialog(
				this, 
				"Reset configuration successfully. \n" + 
				"Do you want to apply configuration into being effective?", 
				"Reset configuration successfully", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			
			if (confirm == JOptionPane.YES_OPTION)
				applyConfig();
			else {
				JOptionPane.showMessageDialog(
					this, 
					"Please press button 'Apply configuration' to make store configuration effect later", 
					"Please press button 'Apply configuration'", 
					JOptionPane.INFORMATION_MESSAGE);
			}
			
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Setting (adding) remote balancer into the list of remote information of this control panel.
	 * Such list is stored and shown in {@link #paneConfig}.
	 * Actually, this method shows the dialog RemoteInfoDlg allowing users to set up remote hosts (remote balancers).
	 */
	protected synchronized void setupRemoteHosts() {
		try {
			if (listener.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Balancer running. Can't reset configuration", 
					"Balancer running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			BalancerConfig cfg = (BalancerConfig)listener.getConfig();
			
			RemoteInfoDlg rInfoDlg = new RemoteInfoDlg(this, cfg.getRemoteInfoList());
			RemoteInfoList result = rInfoDlg.getResult();
			if (result == null) {
				JOptionPane.showMessageDialog(
					this, 
					"Not set up remote hosts", 
					"Not set up remote hosts", 
					JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				cfg.setRemoteInfoList(result);
				
				paneConfig.update(cfg);
				JOptionPane.showMessageDialog(
					this, 
					"Set up remote hosts successfully" + 
					"Please press button 'Apply configuration' to make store configuration effect", 
					"Please press button 'Apply configuration'", 
					JOptionPane.INFORMATION_MESSAGE);
			}
			
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Enable/Disable all controls (components) in this control panel.
	 * @param enabled if {@code true}, all controls are enabled. Otherwise, all controls are disabled.
	 */
	protected void enableControls(boolean enabled) {
		btnExitListener.setEnabled(enabled);
		btnStart.setEnabled(enabled);
		btnPauseResume.setEnabled(enabled);
		btnStop.setEnabled(enabled);
		btnApplyConfig.setEnabled(enabled);
		btnResetConfig.setEnabled(enabled);
		btnRefresh.setEnabled(enabled);
		btnSetupRemoteHosts.setEnabled(enabled);
		paneConfig.setEnabled(enabled);
	}
	
	
	/**
	 * Update all controls (components) in this control panel according to current listener status.
	 * @param status listener current status.
	 */
	protected void updateControls(Status status) {
		if (status == Status.exit) {
			updateControls0(status);
		}
		else {
			synchronized (this) {
				updateControls0(status);
			}
		}
	}
	
	
	/**
	 * Update all controls (components) in this control panel according to current balancer status.
	 * Please see {@link ServerStatusEvent#status} for more details about balancer statuses.
	 * @param status balancer current status.
	 */
	private void updateControls0(Status status) {
		if (status == Status.unknown) return;
		currentStatus = status;

		if (status == Status.started || status == Status.resumed) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnSetupRemoteHosts.setEnabled(false);
			paneConfig.setEnabled(false);
			
			try {
				paneConfig.update(listener.getConfig());
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		else if (status == Status.paused) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnSetupRemoteHosts.setEnabled(false);
			paneConfig.setEnabled(false);
			
		}
		else if (status == Status.stopped) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(true);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(false);
			
			btnApplyConfig.setEnabled(true);
			btnResetConfig.setEnabled(true);
			btnSetupRemoteHosts.setEnabled(true);
			paneConfig.setEnabled(true);
			
		}
		else if (status == Status.setconfig) {
			try {
				paneConfig.update(listener.getConfig());
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		else if (status == Status.exit) {
			listener = null;
			if (connectInfo.bindUri != null)
				dispose();
			else {
				if (timer != null) timer.cancel();
				timer = null;
			}
		}
		
		btnRefresh.setEnabled(true);
	}
	
	
	/**
	 * Update all controls (components) in this control panel according to current balancer status.
	 * Please see {@link ServerStatusEvent#status} for more details about balancer statuses.
	 */
	protected void updateControls() {
		if (listener == null) return;
		
		Status status = ServerStatusEvent.getStatus(listener);
		
		if (status == Status.exit) {
			updateControls(status);
		}
		else {
			synchronized (this) {
				updateControls(status);
			}
		}
	}
	
	
	@Override
	public synchronized void statusChanged(ServerStatusEvent evt) 
			throws RemoteException {
		Status status = evt.getStatus();
		
		if (status == Status.exit) {
			if (connectInfo.bindUri != null)
				updateControls0(status);
			else if (!evt.getShutdownHookStatus())
				updateControls0(status);
		}
		else {
			synchronized (this) {
				if (connectInfo.bindUri != null)
					updateControls0(status);
				else if (!evt.getShutdownHookStatus())
					updateControls0(status);
			}
		}
	}
	

	@Override
	public void dispose() {
		try {
			if (listener != null && (connectInfo.bindUri == null || !connectInfo.pullMode))
				listener.removeStatusListener(this);
		} 
		catch (Throwable e) {LogUtil.trace(e);}
		listener = null;

		try {
    		if (connectInfo.bindUri != null && !connectInfo.pullMode)
				UnicastRemoteObject.unexportObject(this, true);
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		if (timer != null) timer.cancel();
		timer = null;

		super.dispose();
	}

	
	/**
	 * The main method firstly shows the connection dialog in other to connect remote balancer and then show the control panel ({@link BalancerCP}) associated with such remote balancer.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		boolean console = args != null && args.length >= 1 
				&& args[0] != null && args[0].toLowerCase().equals("console");
		if (console || GraphicsEnvironment.isHeadless()) {
			LightRemoteServerCP.console();
			return;
		}

		Connector connector = Connector.connect(ConnectType.server, Constants.DEFAULT_LISTENER_EXPORT_PORT);
		Image image = UIUtil.getImage("balancer-32x32.png");
        if (image != null) connector.setIconImage(image);
		
		Server server = connector.getServer();
		ConnectInfo connectInfo = connector.getConnectInfo();
		if (server == null) {
			JOptionPane.showMessageDialog(
				null, "Fail to retrieve balancer", "Fail to retrieve balancer", JOptionPane.ERROR_MESSAGE);
		}
		else if (connectInfo.bindUri != null && !connectInfo.pullMode && Connector.isPullModeRequired(server)) {
			JOptionPane.showMessageDialog(null,
				"Can't retrieve balancer because PULL MODE is not set\n" +
				"whereas the remote balancer requires PULL MODE.\n" +
				"You have to check PULL MODE in connection dialog.",
				"Retrieval to balancer failed", JOptionPane.ERROR_MESSAGE);
		}
		else
			new BalancerCP(server, connectInfo);
	}
	
	
}
