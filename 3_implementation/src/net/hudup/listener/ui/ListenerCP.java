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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.hudup.core.Constants;
import net.hudup.core.client.ConnectDlg;
import net.hudup.core.client.ConnectDlg.ConnectType;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface (GUI) as a window application {@link JFrame} allows users to control remotely listener
 * such as starting, stopping, pausing, resuming, and configuring listener. It is called listener control panel.
 * This control panel updates listener status immediately because it has two advanced features as follows:
 * <ul>
 * <li>This control panel is itself a {@link ServerStatusListener} that is registered with listener before.</li>
 * <li>This control panel is itself a remote RMI object that allows other applications to interact with it via RMI protocol.</li>
 * </ul>
 * Therefore, every time the listener changes status, the listener calls the method {@link #statusChanged(ServerStatusEvent)} of this control panel to update listener current status immediately.
 * <br>
 * RMI is abbreviation of Java Remote Method Invocation for remote interaction in client-server architecture. The tutorial of RMI is available at <a href="https://docs.oracle.com/javase/tutorial/rmi">https://docs.oracle.com/javase/tutorial/rmi</a>.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ListenerCP extends JFrame implements ServerStatusListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Pane of listener configuration as {@link SysConfigPane}.
	 */
	private SysConfigPane paneConfig = null;
	
	/**
	 * Button to exit listener.
	 */
	private JButton btnExitListener = null;
	
	/**
	 * Button to start listener.
	 */
	private JButton btnStart = null;
	
	/**
	 * Button to pause/resume listener.
	 */
	private JButton btnPauseResume = null;

	/**
	 * Button to stop listener.
	 */
	private JButton btnStop = null;
	
	/**
	 * Button to apply changes of listener configuration.
	 */
	private JButton btnApplyConfig = null;
	
	/**
	 * Button to reset listener configuration.
	 */
	private JButton btnResetConfig = null;

	/**
	 * Button to refresh this control panel.
	 */
	private JButton btnRefresh = null;

	/**
	 * Reference to remote listener.
	 */
	protected Server listener = null;
	
	/**
	 * Binded URI of this control panel as remote RMI object. It is URI pointing to where this control panel is located.
	 * If it is not null, this control panel associates with remote listener on remote host.
	 */
	private xURI bindUri = null;
	
	
	/**
	 * Constructor with reference to specified listener and binded URI of this control panel as remote RMI object.
	 * @param listener specified listener.
	 * @param bindUri binded URI of this control panel as remote RMI object. If it is not null, this control panel associates with remote listener on remote host.
	 * @param bRemote if this flag is true, this control panel associates with remote listener on remote host.
	 * If this flag is false, this control panel associates with local listener on the same host.
	 */
	public ListenerCP(Server listener, xURI bindUri) {
		super("Listener control panel");
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
			addWindowListener(new WindowAdapter() {
	
				@Override
				public void windowClosed(WindowEvent e) {
					super.windowClosed(e);
					close();
				}
				
			});
	        Image image = UIUtil.getImage("listener-32x32.png");
	        if (image != null)
	        	setIconImage(image);
			
			this.listener = listener;
			this.bindUri = bindUri;
			
			Container container = getContentPane();
			JTabbedPane main = new JTabbedPane();
			container.add(main);
			
			main.add(createGeneralPane(), "General");
	
			bindServer();

			updateControls();
			setVisible(true);
		}
		catch (RemoteException e) {
			LogUtil.trace(e);
		}
		
		
		if(bindUri == null) {
			
			Runtime.getRuntime().addShutdownHook(new Thread() {
	
				@Override
				public void run() {
					try {
						close();
					} 
					catch (Throwable e) {
						LogUtil.trace(e);
					}
				}
				
			});
		}
		
	}

	
	/**
	 * Constructor with reference to local listener. The binded URI is null.
	 * @param listener local listener
	 */
	public ListenerCP(Server listener) {
		this(listener, null);
	}

	
	/**
	 * Binding (exposing) this control panel as remote RMI object so that server or other applications can interact with it via RMI protocol.
	 * The internal variable {@link #bindUri} pointing to where to locate this control panel.
	 */
	private void bindServer() throws RemoteException {
		boolean result = false;
		
		if (bindUri == null) {
			result = listener.addStatusListener(this);
		}
		else {
			btnExitListener.setVisible(false);
			
			try {
				UnicastRemoteObject.exportObject(this, bindUri.getPort());
				
				result = listener.addStatusListener(this);
				if (!result)
					throw new Exception();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				
				try {
		        	UnicastRemoteObject.unexportObject(this, true);
				}
				catch (Throwable e1) {
					e1.printStackTrace();
				}
				
				bindUri = null;
				result = false;
			}
			
		}
			
		
		if (result)
			btnRefresh.setVisible(false);
	}

	
	/**
	 * Creating the general pane containing all GUI controls (buttons).
	 * @return {@link JPanel} containing all GUI controls (buttons).
	 * @throws RemoteException if any error raises.
	 */
	private JPanel createGeneralPane() throws RemoteException {
		JPanel general = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		general.add(body, BorderLayout.CENTER);
		
		body.add(new JLabel("Listener configuration"), BorderLayout.NORTH);
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
		
		btnApplyConfig = new JButton("Apply config");
		btnApplyConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				applyConfig();
			}
		});
		configbar.add(btnApplyConfig);

		btnResetConfig = new JButton("Reset config");
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
				try {
					updateControls();
				} 
				catch (RemoteException e1) {
					e1.printStackTrace();
				}
			}
		});
		leftToolbar.add(btnRefresh);

		
		btnExitListener = new JButton("Exit listener");
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
	 * Start listener remotely.
	 */
	private void start() {
		try {
			listener.start();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Pause/resume listener remotely.
	 */
	private void pauseResume() {
		try {
			if (listener.isPaused())
				listener.resume();
			else if (listener.isRunning())
				listener.pause();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Stop listener remotely.
	 */
	private void stop() {
		try {
			listener.stop();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Exit listener remotely. After exiting, listener is destroyed and cannot be re-started.
	 */
	private void exit() {
		if (bindUri != null)
			return;

		try {
			listener.exit();
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Apply changes into listener configuration remotely.
	 */
	private void applyConfig() {
		try {
			if (listener.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Listener running. Can't save configuration", 
					"Listener running", 
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
						"Apply configuration to listener successfully", 
						"Apply configuration successfully", 
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	

	/**
	 * Reset listener configuration.
	 */
	private void resetConfig() {
		try {
			if (listener.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Listener running. Can't reset configuration", 
					"Listener running", 
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
					"Please press button 'Apply Config' to make store configuration effect later", 
					"Please press button 'Apply Config' to make store configuration effect later", 
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
	private void enableControls(boolean enabled) {
		btnExitListener.setEnabled(enabled);
		btnStart.setEnabled(enabled);
		btnPauseResume.setEnabled(enabled);
		btnStop.setEnabled(enabled);
		btnApplyConfig.setEnabled(enabled);
		btnResetConfig.setEnabled(enabled);
		btnRefresh.setEnabled(enabled);
		paneConfig.setEnabled(enabled);
	}
	
	
	/**
	 * Update all controls (components) in this control panel according to current listener status.
	 * Please see {@link ServerStatusEvent#status} for more details about listener statuses.
	 * @throws RemoteException if any error raises.
	 */
	private void updateControls() 
			throws RemoteException {
		if (listener == null)
			return;
		
		if (listener.isRunning())
			updateControls(Status.started);
		else if (listener.isPaused())
			updateControls(Status.paused);
		else
			updateControls(Status.stopped);
			
	}
	
	
	/**
	 * Update all controls (components) in this control panel according to current listener status.
	 * Please see {@link ServerStatusEvent#status} for more details about listener statuses.
	 * @param status listener current status.
	 * @throws RemoteException if any error raises.
	 */
	private void updateControls(ServerStatusEvent.Status status) 
			throws RemoteException {
		
		if (status == Status.started || status == Status.resumed) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && bindUri == null);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			paneConfig.setEnabled(false);
			
			try {
				paneConfig.update(listener.getConfig());
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		else if (status == Status.paused) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && bindUri == null);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
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
			paneConfig.setEnabled(true);
			
		}
		else if (status == Status.setconfig) {
			paneConfig.update(listener.getConfig());
		}
		else if (status == Status.exit) {
			if (bindUri != null) {
				listener = null;
				dispose();
			}
		}
		
		
	}
	
	
	@Override
	public void statusChanged(ServerStatusEvent evt) 
			throws RemoteException {
		if (bindUri != null)
			updateControls(evt.getStatus());
		else if (!evt.getShutdownHookStatus())
			updateControls(evt.getStatus());
	}
	
	
	/**
	 * Closing this control panel. All resources such as reference to listener and time counter are released.
	 */
	private void close() {
		
		try {
			if (listener != null)
				listener.removeStatusListener(this);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		if (bindUri != null) {
			try {
				UnicastRemoteObject.unexportObject(this, true);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
		}
		
		listener = null;
		bindUri = null;
	}

	
	
	/**
	 * The main method firstly shows the connection dialog in other to connect remote listener and then show the control panel ({@link ListenerCP}) associated with such remote listener.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		ConnectDlg dlg = ConnectDlg.connect(ConnectType.server, Constants.DEFAULT_LISTENER_EXPORT_PORT);
		Image image = UIUtil.getImage("listener-32x32.png");
        if (image != null)
        	dlg.setIconImage(image);
		
		Server server = dlg.getServer();
		if (server != null)
			new ListenerCP(server, dlg.getBindNamingUri().bindUri);
		else {
			JOptionPane.showMessageDialog(
					null, "Can't retrieve listener", "Can't retrieve listener", JOptionPane.ERROR_MESSAGE);
		}
	}


}
