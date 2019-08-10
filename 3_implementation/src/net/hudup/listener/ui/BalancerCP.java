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
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import net.hudup.core.client.ConnectDlg;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.logistic.xURI;
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
	 * Pane of balancer configuration as {@link SysConfigPane}.
	 */
	private SysConfigPane paneConfig = null;
	
	/**
	 * Button to exit balancer.
	 */
	private JButton btnExitListener = null;
	
	/**
	 * Button to start balancer.
	 */
	private JButton btnStart = null;
	
	/**
	 * Button to pause/resume balancer.
	 */
	private JButton btnPauseResume = null;

	/**
	 * Button to stop balancer.
	 */
	private JButton btnStop = null;
	
	/**
	 * Button to apply changes of balancer configuration.
	 */
	private JButton btnApplyConfig = null;
	
	/**
	 * Button to reset balancer configuration.
	 */
	private JButton btnResetConfig = null;

	/**
	 * Button to refresh this control panel.
	 */
	private JButton btnRefresh = null;
	
	/**
	 * Button to set (add) remote balancer into the list of remote information of this control panel.
	 * Such list is stored and shown in {@link #paneConfig}.
	 */
	private JButton btnSetupRemoteHosts = null;
	
	/**
	 * Reference to remote balancer. Note, balancer is special listener.
	 */
	protected Server listener = null;
	
	/**
	 * RMI registry for exposing this control panel as remote RMI object. Please see {@link Registry} for more details about RMI registry.
	 */
	private Registry registry = null;

	/**
	 * Binded URI of this control panel as remote RMI object. It is URI pointing to where this control panel is located.
	 */
	private xURI bindUri = null;
	
	/**
	 * If this flag is true, this control panel associates with remote balancer on remote host.
	 * If this flag is false, this control panel associates with local balancer on the same host.
	 */
	private boolean bRemote = false;

	
	/**
	 * Constructor with reference to specified balancer and binded URI of this control panel as remote RMI object.
	 * @param listener specified balancer.
	 * @param bindUri binded URI of this control panel as remote RMI object.
	 * @param bRemote if this flag is true, this control panel associates with remote balancer on remote host.
	 * If this flag is false, this control panel associates with local balancer on the same host.
	 */
	public BalancerCP(Server listener, xURI bindUri, boolean bRemote) {
		super("Balancer control panel");
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
			addWindowListener(new WindowAdapter() {
	
				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub
					super.windowClosed(e);
					close();
				}
				
			});
	        
			Image image = UIUtil.getImage("balancer-32x32.png");
	        if (image != null)
	        	setIconImage(image);
			
			this.listener = listener;
			this.bindUri = bindUri;
			this.bRemote = bRemote;
			
			Container container = getContentPane();
			JTabbedPane main = new JTabbedPane();
			container.add(main);
			
			main.add(createGeneralPane(), "General");
	
			bindServer();

			updateControls();
			setVisible(true);
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		if(!this.bRemote) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
	
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						close();
					} 
					catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
			});
		}
		
	}

	
	/**
	 * Constructor with reference to local balancer. The binded URI is null.
	 * @param listener local balancer.
	 */
	public BalancerCP(Server listener) {
		this(listener, null, false);
	}

	
	/**
	 * Binding (exposing) this control panel as remote RMI object so that server or other applications can interact with it via RMI protocol.
	 * The internal variable {@link #bindUri} pointing to where to locate this control panel.
	 */
	private void bindServer() throws RemoteException {
		boolean result = false;
		
		if (!bRemote) {
			bindUri = null;
			registry = null;
			
			result = listener.addStatusListener(this);
		}
		else {
			btnExitListener.setVisible(false);
			
			if (bindUri != null) {
			
				try {
					registry = LocateRegistry.createRegistry(bindUri.getPort());
					UnicastRemoteObject.exportObject(this, bindUri.getPort());
					
					result = listener.addStatusListener(this);
					if (!result)
						throw new Exception();
				}
				catch (Throwable e) {
					e.printStackTrace();
					
					try {
			        	UnicastRemoteObject.unexportObject(this, true);
					}
					catch (Throwable e1) {
						e1.printStackTrace();
					}
					
					try {
			    		UnicastRemoteObject.unexportObject(registry, true);
					}
					catch (Throwable e1) {
						e1.printStackTrace();
					}
					
					registry = null;
					bindUri = null;
					result = false;
				}
			
			} // if (bindUri_ != null)
			
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
		
		btnApplyConfig = new JButton("Apply config");
		btnApplyConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				applyConfig();
			}
		});
		configbar.add(btnApplyConfig);

		btnResetConfig = new JButton("Reset config");
		btnResetConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				resetConfig();
			}
		});
		configbar.add(btnResetConfig);

		
		btnSetupRemoteHosts = new JButton("Setup remote hosts");
		btnSetupRemoteHosts.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				try {
					updateControls();
				} 
				catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		leftToolbar.add(btnRefresh);

		
		btnExitListener = new JButton("Exit balancer");
		btnExitListener.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
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
				// TODO Auto-generated method stub
				start();
			}
		});
		centerToolbar.add(btnStart);

		btnPauseResume = new JButton("Pause");
		btnPauseResume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				pauseResume();
			}
		});
		centerToolbar.add(btnPauseResume);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				stop();
			}
		});
		centerToolbar.add(btnStop);
		
		return general;
	}


	/**
	 * Start balancer remotely.
	 */
	private void start() {
		try {
			listener.start();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Pause/resume balancer remotely.
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
			e.printStackTrace();
		}
	}

	
	/**
	 * Stop balancer remotely.
	 */
	private void stop() {
		try {
			listener.stop();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/**
	 * Exit balancer remotely. After exiting, balancer is destroyed and cannot be re-started.
	 */
	private void exit() {
		if (bRemote)
			return;

		try {
			listener.exit();
		} 
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Apply changes into balancer configuration remotely.
	 */
	private void applyConfig() {
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
			e.printStackTrace();
		}
	}
	

	/**
	 * Reset balancer configuration.
	 */
	private void resetConfig() {
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
						"Please press button 'Apply Config' to make store configuration effect later", 
						"Please press button 'Apply Config' to make store configuration effect later", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Setting (adding) remote balancer into the list of remote information of this control panel.
	 * Such list is stored and shown in {@link #paneConfig}.
	 * Actually, this method shows the dialog RemoteInfoDlg allowing users to set up remote hosts (remote balancers).
	 */
	private void setupRemoteHosts() {
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
						"Please press button 'Apply Config' to make store configuration effect", 
						"Please press button 'Apply Config' to make store configuration effect", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			
		}
		catch (Exception e) {
			e.printStackTrace();
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
		btnSetupRemoteHosts.setEnabled(enabled);
		paneConfig.setEnabled(enabled);
	}
	
	
	/**
	 * Update all controls (components) in this control panel according to current balancer status.
	 * Please see {@link ServerStatusEvent#status} for more details about balancer statuses.
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
	 * Update all controls (components) in this control panel according to current balancer status.
	 * Please see {@link ServerStatusEvent#status} for more details about balancer statuses.
	 * @param status balancer current status.
	 * @throws RemoteException if any error raises.
	 */
	private void updateControls(ServerStatusEvent.Status status) 
			throws RemoteException {
		
		if (status == Status.started || status == Status.resumed) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && !bRemote);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnSetupRemoteHosts.setEnabled(false);
			paneConfig.setEnabled(false);
			
			try {
				paneConfig.update(listener.getConfig());
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		else if (status == Status.paused) {
			enableControls(false);

			btnExitListener.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && !bRemote);
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
			paneConfig.update(listener.getConfig());
			
		}
		else if (status == Status.exit) {
			if (bRemote) {
				listener = null;
				dispose();
			}
		}
		
	}
	
	
	@Override
	public void statusChanged(ServerStatusEvent evt) 
			throws RemoteException {
		// TODO Auto-generated method stub
		if (bRemote)
			updateControls(evt.getStatus());
		else if (!evt.getShutdownHookStatus())
			updateControls(evt.getStatus());
	}
	
	
	/**
	 * Closing this control panel. All resources such as reference to balancer and time counter are released.
	 */
	private void close() {
		
		try {
			if (listener != null)
				listener.removeStatusListener(this);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (bRemote) {
			try {
				if (bindUri != null)
					UnicastRemoteObject.unexportObject(this, true);
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			try {
				if (registry != null)
					UnicastRemoteObject.unexportObject(registry, true);
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		listener = null;
		bindUri = null;
		registry = null;
	}

	
	/**
	 * The main method firstly shows the connection dialog in other to connect remote balancer and then show the control panel ({@link BalancerCP}) associated with such remote balancer.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(String[] args) {
		ConnectDlg dlg = ConnectDlg.connectServer();
		Image image = UIUtil.getImage("balancer-32x32.png");
        if (image != null)
        	dlg.setIconImage(image);
		
		Server server = dlg.getServer();
		if (server != null)
			new BalancerCP(server, dlg.getBindUri(), true);
	}
	
	
}
