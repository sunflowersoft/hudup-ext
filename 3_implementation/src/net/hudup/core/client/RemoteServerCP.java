/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

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

import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface (GUI) as a window application {@link JFrame} allows users to start, stop, pause, resume, configure {@link Server} remotely.
 * This class is also called powerful control panel for remote server, which shares many function which the lightweight control panel represented by {@link LightRemoteServerCP}.
 * The difference from {@link LightRemoteServerCP} is to update server status immediately because this {@link RemoteServerCP} has two advanced features as follows:
 * <ul>
 * <li>This control panel is itself a {@link ServerStatusListener} that is registered with server before.</li>
 * <li>This control panel is itself a remote RMI object that allows other applications to interact with it via RMI protocol.</li>
 * </ul>
 * Therefore, every time the server changes status, the server calls the method {@link #statusChanged(ServerStatusEvent)} of this control panel to update server current status immediately.
 * <br>
 * RMI is abbreviation of Java Remote Method Invocation for remote interaction in client-server architecture. The tutorial of RMI is available at <a href="https://docs.oracle.com/javase/tutorial/rmi">https://docs.oracle.com/javase/tutorial/rmi</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteServerCP extends JFrame implements ServerStatusListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Current server status.
	 */
	protected Status currentStatus = Status.unknown;
	
	/**
	 * Pane of server configuration.
	 */
	protected PropPane paneConfig = null;
	
	/**
	 * Exiting server button.
	 */
	protected JButton btnExitServer = null;

	/**
	 * Button to start server.
	 */
	protected JButton btnStart = null;
	
	/**
	 * Button to pause/resume server.
	 */
	protected JButton btnPauseResume = null;
	
	/**
	 * Button to stop server.
	 */
	protected JButton btnStop = null;
	
	/**
	 * Button to apply changes of server configuration.
	 */
	protected JButton btnApplyConfig = null;
	
	/**
	 * Button to reset server configuration.
	 */
	protected JButton btnResetConfig = null;
	
	/**
	 * Button to refresh this control panel.
	 */
	protected JButton btnRefresh = null;
	
	/**
	 * Reference to remote server.
	 */
	protected Server server = null;
	
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
	public RemoteServerCP(Server server, ConnectInfo connectInfo) {
		super("Power remote server control panel");
		
		try {
			this.connectInfo = connectInfo != null ? connectInfo : new ConnectInfo();
			this.server = server;

			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
	        Image image = UIUtil.getImage("remotecp-32x32.png");
	        if (image != null)
	        	setIconImage(image);
			
			Container container = getContentPane();
			container.setLayout(new BorderLayout());
			
			JPanel body = new JPanel(new BorderLayout());
			container.add(body, BorderLayout.CENTER);
			
			body.add(new JLabel("Server configuration"), BorderLayout.NORTH);
			paneConfig = new PropPane();
			paneConfig.setControlVisible(false);
			paneConfig.update(server.getConfig());
			body.add(paneConfig);
			
			
			JPanel footer = new JPanel();
			footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
			container.add(footer, BorderLayout.SOUTH);
			
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
			btnRefresh.setAlignmentX(LEFT_ALIGNMENT);
			leftToolbar.add(btnRefresh);

			btnExitServer = new JButton("Exit server");
			btnExitServer.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					exit();
				}
			});
			leftToolbar.add(btnExitServer);

			
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
								Status status = ServerStatusEvent.getStatus(server);
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
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		
	}
	
	
	/**
	 * Binding (exposing) this control panel as remote RMI object so that server or other applications can interact with it via RMI protocol.
	 * The internal variable {@link #bindUri} pointing to where to locate this control panel.
	 */
	protected void bindServer() {
		if (connectInfo.bindUri == null) {
			try {
				server.addStatusListener(this);
			}
			catch (Throwable e1) {e1.printStackTrace();}
		}
		else if (!connectInfo.pullMode) {
			try {
				UnicastRemoteObject.exportObject(this, connectInfo.bindUri.getPort());
				server.addStatusListener(this);
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
	 * Start server remotely.
	 */
	protected synchronized void start() {
		try {
			server.start();
			
			if (connectInfo.pullMode)
				updateControls();
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Pause/resume server remotely.
	 */
	protected synchronized void pauseResume() {
		try {
			if (server.isPaused())
				server.resume();
			else if (server.isRunning())
				server.pause();
			
			if (connectInfo.pullMode)
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Stop server remotely.
	 */
	protected synchronized void stop() {
		try {
			server.stop();
			
			if (connectInfo.pullMode)
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Exiting remote server.
	 */
	protected synchronized void exit() {
		if (timer != null) timer.cancel();
		timer = null;

		try {
			if (server != null && (connectInfo.bindUri == null || !connectInfo.pullMode))
				server.removeStatusListener(this);
		} catch (Exception e) {LogUtil.trace(e);}
		
		try {
			if (server != null) server.exit();
		} catch (Exception e) {}
		server = null;
		
		dispose();
	}

	
	/**
	 * Apply changes into server configuration remotely.
	 */
	protected synchronized void applyConfig() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't save configuration", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			boolean apply = paneConfig.apply();
			if (!apply) {
				JOptionPane.showMessageDialog(
						this, 
						"Cannot apply", 
						"Cannot apply", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			server.setConfig((DataConfig)paneConfig.getPropTable().getPropList());
			JOptionPane.showMessageDialog(
					this, 
					"Apply configuration to server successfully", 
					"Apply successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Reset server configuration.
	 */
	protected synchronized void resetConfig() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't reset configuration", 
					"Server running", 
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
					"Please press button 'Apply configuration' to make configuration effect later", 
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
		btnExitServer.setEnabled(enabled);
		btnStart.setEnabled(enabled);
		btnPauseResume.setEnabled(enabled);
		btnStop.setEnabled(enabled);
		btnApplyConfig.setEnabled(enabled);
		btnResetConfig.setEnabled(enabled);
		btnRefresh.setEnabled(enabled);
		paneConfig.setEnabled(enabled);
	}
	
	
	/**
	 * Update all controls (components) in this control panel according to current server status.
	 * @param status server current status.
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
	 * Update all controls (components) in this control panel according to current server status.
	 * Please see {@link ServerStatusEvent#status} for more details about server statuses.
	 * This method currently hide pause/resume button because of error remote lock. The next version will be improve this method.
	 * @param status server current status.
	 */
	private void updateControls0(Status status) {
		if (status == Status.unknown) return;
		currentStatus = status;
		
		if (status == Status.started || status == Status.resumed) {
			enableControls(false);

			btnExitServer.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			paneConfig.setEnabled(false);
			
			try {
				paneConfig.update(server.getConfig());
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		else if (status == Status.paused) {
			enableControls(false);

			btnExitServer.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			paneConfig.setEnabled(false);
		}
		else if (status == Status.stopped) {
			enableControls(false);

			btnExitServer.setEnabled(true);
			btnStart.setEnabled(true);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(false);
			
			btnApplyConfig.setEnabled(true);
			btnResetConfig.setEnabled(true);
			paneConfig.setEnabled(true);
		}
		else if (status == Status.setconfig) {
			try {
				paneConfig.update(server.getConfig());
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		else if (status == Status.exit) {
			server = null;
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
	 * Update all controls (components) in this control panel according to current server status.
	 * Please see {@link ServerStatusEvent#status} for more details about server statuses.
	 */
	protected void updateControls() {
		if (server == null) return;
		
		Status status = ServerStatusEvent.getStatus(server);
		
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
	public void statusChanged(ServerStatusEvent evt) throws RemoteException {
		Status status = evt.getStatus();
		
		if (status == Status.exit) {
			updateControls0(status);
		}
		else {
			synchronized (this) {
				updateControls0(status);
			}
		}
	}
	
	
	@Override
	public void dispose() {
    	try {
			if (server != null && (connectInfo.bindUri == null || !connectInfo.pullMode))
    			server.removeStatusListener(this);
    	}
    	catch (Throwable e) {LogUtil.trace(e);}
    	server = null;
		
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
	 * The main method shows the {@link Connector} for users to enter authenticated information to connect server.
	 * Later on this method shows the this control panel for users to start, stop, pause and configure sever remotely.
	 * @param args argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		boolean console = args != null && args.length >= 1 
				&& args[0] != null && args[0].toLowerCase().equals("console");
		if (console || GraphicsEnvironment.isHeadless()) {
			LightRemoteServerCP.console();
			return;
		}
		
		Connector connector = Connector.connect();
		
		Server server = connector.getServer();
		ConnectInfo connectInfo = connector.getConnectInfo();
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
		else
			new RemoteServerCP(server, connectInfo);
	}


}
