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
import java.io.Console;
import java.rmi.RemoteException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.Constants;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface and a window application {@link JFrame} allows users to start, stop, pause, resume, configure {@link Server} remotely.
 * This class is also called lightweight control panel for remote server.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@NextUpdate //Next updating for console
public class LightRemoteServerCP extends JFrame {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Current server status.
	 */
	protected Status currentStatus = Status.unknown;
	
	/**
	 * Pane of server configuration as {@link PropPane}. 
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
	 * Internal time counter.
	 * Every period in seconds, this control panel updates itself by server information.
	 */
	protected Timer timer = null;
	
	
	/**
	 * Constructor with reference to remote server.
	 * @param server reference to remote server.
	 */
	public LightRemoteServerCP(Server server) {
		super("Light remote server control panel");
		
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
	        Image image = UIUtil.getImage("remotecp-32x32.png");
	        if (image != null)
	        	setIconImage(image);
			
			this.server = server;
			
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
			/*
			 * Hide pause/resume button in some cases because of error remote lock. The next version will be improve this method.
			 * So the following code lines need to be removed.
			 */
			btnPauseResume.setVisible(false);
			centerToolbar.add(btnPauseResume);
			
			btnStop = new JButton("Stop");
			btnStop.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					stop();
				}
			});
			centerToolbar.add(btnStop);
			
			updateControls();
			
			addWindowListener(new WindowAdapter() {

				@Override
				public void windowOpened(WindowEvent e) {
					super.windowOpened(e);
					
					if (timer != null) return;
					
					timer = new Timer();
					long milisec = 10 * Counter.PERIOD * 1000; //Every 10 seconds, this control panel updates itself by server information.
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
	 * Start server remotely.
	 */
	protected synchronized void start() {
		try {
			server.start();
			updateControls();
		} catch (RemoteException e) {
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
			updateControls();
		} catch (RemoteException e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Exiting remote server.
	 */
	protected synchronized void exit() {
		try {
			server.exit();
		} 
		catch (Exception e) {}
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
			JOptionPane.showMessageDialog(
					this, 
					"Reset configuration successfully. \n" + 
					"Please press button 'Apply configuration' to make store configuration effect", 
					"Please press button 'Apply configuration'", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Enable/Disable all controls (components) in this control panel.
	 * 
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
	 * This method currently hide pause/resume button because of error remote lock. The next version will be improve this method.
	 */
	protected synchronized void updateControls() {
		if (server == null) return;
		Status status = ServerStatusEvent.getStatus(server);
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
		else {
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
		
		btnRefresh.setEnabled(true);
		
		//Fixing error when deadlock with pause/resume because of transaction lock. The solution is work-around. User can exit server from command line or system tray.
		try {
			if (status == Status.paused) btnStop.setEnabled(false);
		} catch (Exception e) {LogUtil.trace(e);}
	}
	

	@Override
	public synchronized void dispose() {
		if (timer != null) timer.cancel();
		
		server = null;
		timer = null;
		
		super.dispose();
	}
	
	
	/**
	 * The main method shows the {@link Connector} for users to enter authenticated information to connect server.
	 * Later on this method shows this light remote control panel for users to start, stop, pause and configure sever remotely.
	 * @param args argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		boolean console = args != null && args.length >= 1 
				&& args[0] != null && args[0].toLowerCase().equals("console");
		if (console || GraphicsEnvironment.isHeadless())
			console();
		else {
			Connector connector = Connector.connect();
			
			Server server = connector.getServer();
			if (server != null)
				new LightRemoteServerCP(server);
			else {
				JOptionPane.showMessageDialog(
						null, "Can't retrieve remote server", "Retrieval to server failed", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	 
	
	/**
	 * Remote control panel via console.
	 */
	public static void console() {
		Console console = System.console();
		
		System.out.print("\nEnter connection type (rmi|socket): ");
		String connectType = console.readLine();
		if (connectType == null || connectType.isEmpty())
			connectType = "rmi";
		System.out.println("You select connection type \"" + connectType + "\"\n");

		System.out.print("Enter remote host (\"localhost\" default): ");
		String host = console.readLine();
		if (host == null || host.isEmpty())
			host = "localhost";
		System.out.println("You select remote host \"" + host + "\"\n");
		
		if (connectType.equals("rmi"))
			System.out.print("Enter remote port (\"" + Constants.DEFAULT_SERVER_PORT + "\" default): ");
		else
			System.out.print("Enter remote port (\"" + Constants.DEFAULT_SOCKET_CONTROL_PORT + "\" default): ");
		String portText = console.readLine();
		int port = -1;
		try {
			port = Integer.parseInt(portText);
		}
		catch (Exception e) {
			port = -1;
		}
		if (connectType.equals("rmi"))
			port = port == -1? Constants.DEFAULT_SERVER_PORT : port;
		else
			port = port == -1? Constants.DEFAULT_SOCKET_CONTROL_PORT : port;
		System.out.println("You use remote port " + port + "\n");
		
		System.out.print("Enter user name (\"admin\" default): ");
		String username = console.readLine();
		if (username == null || username.isEmpty())
			username = DataConfig.ADMIN_ACCOUNT;
		System.out.println("User name is \"" + username + "\"\n");

		System.out.print("Enter password (\"admin\" default): ");
		String password = new String(console.readPassword());
		if (password == null || password.isEmpty())
			password = DataConfig.ADMIN_PASSWORD;
		System.out.println();
		
		
//		System.out.print("Enter command (start|stop|pause|resume|exit): ");
		if (connectType.equals("rmi"))
			System.out.print("Enter command (" + Request.START_CONTROL_COMMAND
				+ "|" + Request.STOP_CONTROL_COMMAND + "|" + Request.EXIT_CONTROL_COMMAND + "): ");
		else
			System.out.print("Enter command (" + Request.START_CONTROL_COMMAND 
				+ "|" + Request.STOP_CONTROL_COMMAND
				+ "|" + Request.PAUSE_CONTROL_COMMAND
				+ "|" + Request.RESUME_CONTROL_COMMAND + "): ");
		String command = console.readLine();
		if (command == null || command.isEmpty())
			command = Request.START_CONTROL_COMMAND;
		System.out.println("Your command is \"" + command + "\"\n");

		
		boolean connected = true;
		if (connectType.equals("rmi")) {
			try {
				Server server = ClientUtil.getRemoteServer(host, port, username, password);
				if (server == null) {
					System.out.println("Cannot connect RMI server");
					connected = false;
				}
				else if (command.equals(Request.START_CONTROL_COMMAND))
					server.start();
				else if (command.equals(Request.STOP_CONTROL_COMMAND))
					server.stop();
				else if (command.equals(Request.PAUSE_CONTROL_COMMAND))
					server.pause();
				else if (command.equals(Request.RESUME_CONTROL_COMMAND))
					server.resume();
				else if (command.equals(Request.EXIT_CONTROL_COMMAND)) {
					try {
						server.exit();
					} catch (RemoteException e) {}
				}
				else
					connected = false;
			}
			catch (Exception e) {
				LogUtil.trace(e);
				connected = false;
			}
		}
		else if (connectType.equals("socket")) {
			SocketConnection connector = null;
			try {
				connector = ClientUtil.getSocketConnection(host, port, username, password);
				if (connector == null) {
					System.out.println("Cannot connect socket server");
					connected = false;
				}
				if (command.equals(Request.START_CONTROL_COMMAND))
					connected = connector.control(Request.START_CONTROL_COMMAND);
				else if (command.equals(Request.STOP_CONTROL_COMMAND))
					connected = connector.control(Request.STOP_CONTROL_COMMAND);
				else if (command.equals(Request.PAUSE_CONTROL_COMMAND))
					connected = connector.control(Request.PAUSE_CONTROL_COMMAND);
				else if (command.equals(Request.RESUME_CONTROL_COMMAND))
					connected = connector.control(Request.RESUME_CONTROL_COMMAND);
				else if (command.equals(Request.EXIT_CONTROL_COMMAND))
					connected = connector.control(Request.EXIT_CONTROL_COMMAND);
				else
					connected = false;
			}
			catch (Exception e) {
				LogUtil.trace(e);
				connected = false;
			}
			finally {
				if (connector != null)
					connector.close();
			}
		}
		else {
			System.out.println("Connection type \"" + connectType + "\" not supported");
			connected = false;
		}
		
		String result = "Control command \"" + command 
				+ "\" to remote host \"" + host + "\" at remote port \"" + port + "\"";
		if (connected)
			result = result + " is successful.";
		else
			result = result + " is failed.";

		System.out.println(result);
	}
	
	
}
