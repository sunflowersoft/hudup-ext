/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.Util;
import net.hudup.core.client.Connector;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.LightRemoteServerCP;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.RemoteServerCP;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.Unit;
import net.hudup.core.data.ui.SysConfigDlgExt;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.data.ui.UnitListBox;
import net.hudup.core.data.ui.UnitTable;
import net.hudup.core.data.ui.UnitTable.SelectionChangedEvent;
import net.hudup.core.data.ui.UnitTable.SelectionChangedListener;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.PluginStorageManifestPanel;
import net.hudup.core.logistic.ui.PluginStorageManifestPanelRemote;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.server.PowerServerConfig;

/**
 * This class provides a remote control panel for power server.
 * The difference from {@link RemoteServerCP} is to have full of features of server
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PowerServerCP extends JFrame implements ServerStatusListener {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Current server status.
	 */
	protected Status currentStatus = Status.unknown;
	
//	/**
//	 * External configuration button.
//	 */
//	protected JButton btnExternalConfig = null;

	/**
	 * System configuration pane.
	 */
	protected SysConfigPane paneConfig = null;
	
	/**
	 * Setting server button.
	 */
	protected JButton btnSetupServer = null;
	
	/**
	 * Exiting server button.
	 */
	protected JButton btnExitServer = null;
	
	/**
	 * Starting server button.
	 */
	protected JButton btnStart = null;
	
	/**
	 * Pause/resume server button.
	 */
	protected JButton btnPauseResume = null;

	/**
	 * Stopping server button.
	 */
	protected JButton btnStop = null;
	
	/**
	 * System button.
	 */
	protected JButton btnSystem = null;

	/**
	 * Applying configuration server.
	 */
	protected JButton btnApplyConfig = null;
	
	/**
	 * Resetting configuration button.
	 */
	protected JButton btnResetConfig = null;

	/**
	 * Refreshing button.
	 */
	protected JButton btnRefresh = null;

	/**
	 * Loading store button.
	 */
	protected JButton btnLoadStore = null;

	/**
	 * Unit list box.
	 */
	protected UnitListBox unitList = null;
	
	/**
	 * Unit table.
	 */
	protected UnitTable unitTable = null;
	
	/**
	 * Unit table for account.
	 */
	protected UnitTable accUnitTable = null;

	/**
	 * Updating account table.
	 */
	protected JButton btnUpdateAcc = null;

	/**
	 * Deleting account button.
	 */
	protected JButton btnDeleteAcc = null;
	
	/**
	 * Account name text box.
	 */
	protected JTextField txtAccName = null;
	
	/**
	 * Account password.
	 */
	protected JPasswordField txtAccPass = null;
	
	/**
	 * Account privileges.
	 */
	protected JTextField txtAccPrivs = null;
	
	/**
	 * Power server.
	 */
	protected PowerServer server = null;
	
	/**
	 * Data provider.
	 */
	protected Provider provider = null;
	
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
	 * @param connectInfo connection information.
	 */
	public PowerServerCP(PowerServer server, ConnectInfo connectInfo) {
		super("Server control panel");
		
		try {
			this.connectInfo = (connectInfo != null ? connectInfo : new ConnectInfo());
			this.server = server;
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
			
	        Image image = UIUtil.getImage("server-32x32.png");
	        if (image != null) setIconImage(image);

		    setJMenuBar(createMenuBar());

		    Container container = getContentPane();
			JTabbedPane main = new JTabbedPane();
			container.add(main);
			
			main.add(createGeneralPane(), "General");
			main.add(createStorePane(), "Store");
			main.add(createAccountPane(), "Account");
			//main.add(new SystemPropertiesPane(), "System properties");

			bindServer();
			
			updateControls();
			
			ConnectInfo thisConnectInfo = this.connectInfo;
			addWindowListener(new WindowAdapter() {

				@Override
				public void windowOpened(WindowEvent e) {
					super.windowOpened(e);
					
					if (timer != null || thisConnectInfo.bindUri == null || !thisConnectInfo.pullMode)
						return;
					
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
	 * Constructor with specified power server.
	 * @param server specified power server.
	 */
	public PowerServerCP(PowerServer server) {
		this(server, null);
	}
	
	
	/**
	 * Binding remote server.
	 */
	protected void bindServer() {
		if (connectInfo.bindUri == null) {
			try {
				server.addStatusListener(this);
			}
			catch (Throwable e) {e.printStackTrace();}
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
	 * Creating main menu bar.
	 * @return main menu bar.
	 */
	protected JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnHelp = new JMenu(I18nUtil.message("help"));
		mnBar.add(mnHelp);
		
		PowerServerCP thisCP = this;
		JMenuItem mniHelpContent = new JMenuItem(
			new AbstractAction(I18nUtil.message("help_content")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					new HelpContent(thisCP);
				}
			});
		mniHelpContent.setMnemonic('h');
		mniHelpContent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mnHelp.add(mniHelpContent);
		
		return mnBar;
	}
	
	
	/**
	 * Create general panel.
	 * @return general {@link JPanel}.
	 * @throws Exception if any error raises.
	 */
	protected JPanel createGeneralPane() throws Exception {
		JPanel general = new JPanel(new BorderLayout());
		

		JPanel body = new JPanel(new BorderLayout());
		general.add(body, BorderLayout.CENTER);
		
		JPanel configGrp1 = new JPanel(new BorderLayout());
		body.add(configGrp1, BorderLayout.NORTH);
		configGrp1.add(new JLabel("Server configuration"), BorderLayout.WEST);
		btnSystem = UIUtil.makeIconButton(
			"system-16x16.png", 
			"system", 
			I18nUtil.message("system_configure"), 
			I18nUtil.message("system_configure"), 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					SysConfigDlgExt cfg = new SysConfigDlgExt(general, I18nUtil.message("system_configure")) {

						/**
						 * Serial version UID for serializable class. 
						 */
						private static final long serialVersionUID = 1L;
						
						@Override
						protected PluginStorageManifestPanel createPluginStorageManifest(Object... vars) {
							return new PluginStorageManifestPanelRemote(server, connectInfo);
						}

						@Override
						protected void onApply() {
							super.onApply();
							updateControls();
						}
						
					};
					
					cfg.removeSysConfigPane();
					if (connectInfo != null && connectInfo.bindUri != null) {
						cfg.removeDataDriverPane();
						cfg.removeSystemPropertiesPane();
					}
//					try {
//						cfg.getPluginStorageManifest().setEnabled(!server.isStarted());
//					}
//					catch (Exception e1) {LogUtil.trace(e1);}
					
					cfg.setVisible(true);
					
					updateControls();
				}
			});
		btnSystem.setMargin(new Insets(0, 0 , 0, 0));
		configGrp1.add(btnSystem, BorderLayout.EAST);
		
		JPanel configGrp2 = new JPanel(new BorderLayout());
		body.add(configGrp2, BorderLayout.CENTER);
		paneConfig = new SysConfigPane();
		paneConfig.setControlVisible(false);
		paneConfig.update(server.getConfig());
		configGrp2.add(paneConfig, BorderLayout.CENTER);
		
		
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

		
		btnLoadStore = new JButton("Load store");
		btnLoadStore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadStore();
			}
		});
		configbar.add(btnLoadStore);

		
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

		btnSetupServer = new JButton("Setup server");
		btnSetupServer.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				setupServer();
			}
		});
		leftToolbar.add(btnSetupServer);

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
		
		
		return general;
	}
	
	
	/**
	 * Create store pane.
	 * @return store {@link JPanel}.
	 * @throws Exception if any error raises.
	 */
	protected JPanel createStorePane() throws Exception {
		JPanel store = new JPanel(new BorderLayout());
		
		unitTable = Util.getFactory().createUnitTable(
				((DataConfig)paneConfig.getPropTable().getPropList()).getStoreUri());
		store.add(unitTable.getComponent(), BorderLayout.CENTER);

		
		unitList = new UnitListBox();
		store.add(new JScrollPane(unitList), BorderLayout.WEST);
		unitList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
			
				Unit unit = unitList.getSelectedValue();
				if (unit == null) {
					unitTable.clear();
					return;
				}
				
				unitTable.update(provider.getAssoc(), unit.getName());
			}
		});
		
		return store;
	}
	
	
	/**
	 * Create account pane.
	 * @return account {@link JPanel}.
	 */
	protected JPanel createAccountPane() {
		final JPanel main = new JPanel(new BorderLayout());
		
		accUnitTable = Util.getFactory().createUnitTable(
				((DataConfig)paneConfig.getPropTable().getPropList()).getStoreUri());
		accUnitTable.addSelectionChangedListener(new SelectionChangedListener() {
			
			@Override
			public void respond(SelectionChangedEvent evt) {
				updateAccData();
			}
		});
			
		
		JPanel body = new JPanel(new BorderLayout());
		main.add(body, BorderLayout.CENTER);
		
		body.add(accUnitTable.getComponent(), BorderLayout.CENTER);

		JPanel records = new JPanel(new BorderLayout());
		body.add(records, BorderLayout.SOUTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		records.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Account name:"));
		left.add(new JLabel("Account password:"));
		left.add(new JLabel("Account privileges:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		records.add(right, BorderLayout.CENTER);
		
		txtAccName = new JTextField();
		right.add(txtAccName);
		
		txtAccPass = new JPasswordField();
		right.add(txtAccPass);
		
		txtAccPrivs = new JTextField();
		right.add(txtAccPrivs);
		
		JPanel footer = new JPanel();
		main.add(footer, BorderLayout.SOUTH);
		
		btnUpdateAcc = new JButton("Add / Update");
		btnUpdateAcc.addActionListener(new ActionListener() {
			
			@SuppressWarnings("deprecation")
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Profile accProfile = new Profile(
							provider.getProfileAttributes(
							server.getConfig().getAccountUnit()));
					
					String acc = txtAccName.getText().trim();
					String password = txtAccPass.getText();
					String priv = txtAccPrivs.getText().trim();
					if (acc.isEmpty() || priv.isEmpty()) {
						JOptionPane.showMessageDialog(
								main, "Empty field", "Empty field", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					accProfile.setValue(
							DataConfig.ACCOUNT_NAME_FIELD, acc);
					accProfile.setValue(DataConfig.ACCOUNT_PASSWORD_FIELD, 
							Util.getCipher().md5Encrypt(password));
					accProfile.setValue(
							DataConfig.ACCOUNT_PRIVILEGES_FIELD, priv);
					
					provider.updateAccount(accProfile);
					accUnitTable.refresh();
					updateAccData();
				}
				catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		});
		footer.add(btnUpdateAcc);
		
		btnDeleteAcc = new JButton("Delete");
		btnDeleteAcc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String accName = txtAccName.getText().trim();
					if (accName.isEmpty() || accName.isEmpty()) {
						JOptionPane.showMessageDialog(
								main, 
								"Empty field", 
								"Empty field", 
								JOptionPane.INFORMATION_MESSAGE);
						
						return;
					}
					
					provider.deleteAccount(accName);
					accUnitTable.refresh();
					updateAccData();
				}
				catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
		});
		footer.add(btnDeleteAcc);
		
		return main;
	}
	
	
	/**
	 * Update account data.
	 */
	protected void updateAccData() {
		txtAccName.setText("");
		txtAccPass.setText("");
		txtAccPrivs.setText("");
		
		int selected = accUnitTable.getSelectedRow();
		if (selected == -1)
			return;
		
		String accName = accUnitTable.getValueAt(selected, 0).toString();
		txtAccName.setText(accName);
		
		txtAccPass.setText("");
		
		String accPrivs = accUnitTable.getValueAt(selected, 2).toString();
		txtAccPrivs.setText(accPrivs);
	}
	
	
	/**
	 * Start remote server.
	 */
	protected synchronized void start() {
		if (paneConfig.isModified()) {
			int confirm = JOptionPane.showConfirmDialog(
				this, 
				"Attributes are modified.\nDo you want to apply them before starting server?", 
				"Attributes are modified", 
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			
			if (confirm == JOptionPane.YES_OPTION)
				applyConfig();
		}
		
		
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
	 * Pause and resume remote server.
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
	 * Stop remote server.
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
	 * Setting up remote server.
	 */
	protected synchronized void setupServer() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't set up", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			if (paneConfig.isModified()) {
				int confirm = JOptionPane.showConfirmDialog(
					this, 
					"Attributes are modified.\nDo you want to apply them before setting up server?", 
					"Attributes are modified", 
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
				
				if (confirm == JOptionPane.YES_OPTION)
					applyConfig();
			}

			SetupServerWizard dlg = new SetupServerWizard(this, (PowerServerConfig)server.getConfig(), connectInfo);
			
			DataConfig config = dlg.getServerConfig();
			if (connectInfo.bindUri != null) {
				server.setConfig(config);
				if (connectInfo.pullMode) paneConfig.update(config);
			}
			else
				paneConfig.update();
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
	}
	
	
	/**
	 * Exiting remote server.
	 */
	protected synchronized void exit() {
		if (provider != null) provider.close();
		provider = null;

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
	 * Applying configuration to remote server.
	 * @return true if configuration is applied successfully.
	 */
	protected synchronized boolean applyConfig() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't save configuration", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return false;
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
				server.setConfig(
						(DataConfig)paneConfig.getPropTable().getPropList());
		
				JOptionPane.showMessageDialog(
						this, 
						"Apply configuration to server successfully", 
						"Apply configuration successfully", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			
			return apply;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return false;
	}
	
	
	/**
	 * Reset configuration for remote server.
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
	 * Loading store for remote server.
	 */
	protected synchronized void loadStore() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't load store", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			DataConfig config = server.getConfig();
			DataConfig storeConfig = DatasetUtil2.chooseServerConfig(this, config);
			
			if (storeConfig == null) {
				JOptionPane.showMessageDialog(
						this, 
						"Not load store", 
						"Not load store", 
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			DataConfig cfg = new DataConfig();
			cfg.putAll(config);
			cfg.putAll(storeConfig);
			
			paneConfig.getPropTable().updateNotSetup(cfg);
			JOptionPane.showMessageDialog(
					this, 
					"Load store configuration successfully. \n" + 
					"Please press button 'Apply configuration' to make store configuration effect", 
					"Please press button 'Apply configuration'", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Enable / disable controls.
	 * @param enabled if true then, controls are enabled and vice versa.
	 */
	protected void enableControls(boolean enabled) {
		btnSetupServer.setEnabled(enabled);
		btnExitServer.setEnabled(enabled);
		btnStart.setEnabled(enabled);
		btnPauseResume.setEnabled(enabled);
		btnStop.setEnabled(enabled);
		btnSystem.setEnabled(enabled);
		btnApplyConfig.setEnabled(enabled);
		btnResetConfig.setEnabled(enabled);
		btnRefresh.setEnabled(enabled);
		btnLoadStore.setEnabled(enabled);
		paneConfig.setEnabled(enabled);
		
		btnUpdateAcc.setEnabled(enabled);
		btnDeleteAcc.setEnabled(enabled);
		txtAccName.setEnabled(enabled);
		txtAccPass.setEnabled(enabled);
		txtAccPrivs.setEnabled(enabled);
	}
	
	
	/**
	 * Update current controls when receiving specified remote status.
	 * @param status specified remote status.
	 */
	protected void updateControls(Status status) {
		if (status == Status.unknown) return;
		currentStatus = status;

		if (status == Status.started || status == Status.resumed) {
			enableControls(false);
			DataConfig config = null;
			try {
				config = server.getConfig();
			}
			catch (Throwable e) {LogUtil.trace(e);}

			btnSetupServer.setEnabled(false);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			btnSystem.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnLoadStore.setEnabled(false);
			paneConfig.setEnabled(false);
			paneConfig.update(config);
			
			boolean editData = isEditData(config);
			btnUpdateAcc.setEnabled(editData && true);
			btnDeleteAcc.setEnabled(editData && true);
			txtAccName.setEnabled(editData && true);
			txtAccPass.setEnabled(editData && true);
			txtAccPrivs.setEnabled(editData && true);
			
			if (provider != null) provider.close();
			provider = null;
			if (editData) {
				provider = new ProviderImpl(config);
	
				unitList.connectUpdate(config);
				unitList.validate();
				unitList.updateUI();
				
				try {
					accUnitTable.update(provider.getAssoc(), config.getAccountUnit());
				}
				catch (Throwable e) {LogUtil.trace(e);}
				updateAccData();
			}
			else {
				unitTable.clear();
				unitList.setListData(new Unit[0]);
				unitList.validate();
				unitList.updateUI();
				
				accUnitTable.clear();
				updateAccData();
			}
		}
		else if (status == Status.paused) {
			enableControls(false);

			btnSetupServer.setEnabled(false);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			btnSystem.setEnabled(true);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnLoadStore.setEnabled(false);
			paneConfig.setEnabled(false);
			
			boolean editData = isEditData();
			btnUpdateAcc.setEnabled(editData && true);
			btnDeleteAcc.setEnabled(editData && true);
			txtAccName.setEnabled(editData && true);
			txtAccPass.setEnabled(editData && true);
			txtAccPrivs.setEnabled(editData && true);
		}
		else if (status == Status.stopped) {
			enableControls(false);

			btnSetupServer.setEnabled(true);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(true);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(false);
			btnSystem.setEnabled(true);
			
			btnApplyConfig.setEnabled(true);
			btnResetConfig.setEnabled(true);
			btnLoadStore.setEnabled(true);
			paneConfig.setEnabled(true);
			
			btnUpdateAcc.setEnabled(false);
			btnDeleteAcc.setEnabled(false);
			txtAccName.setEnabled(false);
			txtAccPass.setEnabled(false);
			txtAccPrivs.setEnabled(false);

			unitTable.clear();
			unitList.setListData(new Unit[0]);
			unitList.validate();
			unitList.updateUI();
			
			accUnitTable.clear();
			updateAccData();
			
			if (provider != null) provider.close();
			provider = null;
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
				if (provider != null) provider.close();
				provider = null;

				if (timer != null) timer.cancel();
				timer = null;
			}
		}
		
		btnRefresh.setEnabled(true);
		
		//Fixing error when deadlock with pause/resume because of transaction lock. The solution is work-around. User can exit server from command line or system tray.
		try {
			if (status == Status.paused) btnStop.setEnabled(false);
		} catch (Exception e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * Update controls.
	 */
	protected synchronized void updateControls() {
		if (server == null) return;
		
		Status status = ServerStatusEvent.getStatus(server);
		
		updateControls(status);
	}
	
	
	@Override
	public void statusChanged(ServerStatusEvent evt) throws RemoteException {
		Status status = evt.getStatus();
		
		if (connectInfo.bindUri == null || !connectInfo.pullMode) {
			if (connectInfo.bindUri != null)
				updateControls(status);
			else if (!evt.getShutdownHookStatus())
				updateControls(status);
		}
		else {
			synchronized (this) {
				if (connectInfo.bindUri != null)
					updateControls(status);
				else if (!evt.getShutdownHookStatus())
					updateControls(status);
			}
		}

	}
	
	
	/**
	 * Testing whether data can be edited remotely.
	 * @param config configuration.
	 * @return whether data can be edited remotely.
	 */
	protected boolean isEditData(DataConfig config) {
		if (config == null) return false;
		xURI storeUri = config.getStoreUri();
		if (storeUri == null) return false;
		
		DataDriver driver = DataDriver.create(storeUri);
		return connectInfo.bindUri == null || !driver.isLocal();
	}

	
	/**
	 * Testing whether data can be edited remotely.
	 * @return whether data can be edited remotely.
	 */
	protected boolean isEditData() {
		DataConfig config = null;
		try {
			config = server.getConfig();
		}
		catch (Exception e) {
			config = null;
			LogUtil.trace(e);
		}
		
		if (config == null) return false;
		xURI storeUri = config.getStoreUri();
		if (storeUri == null) return false;
		
		DataDriver driver = DataDriver.create(storeUri);
		return connectInfo.bindUri == null || driver.getType() != DataType.file;
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
		
		if (provider != null) provider.close();
		provider = null;
		
		if (timer != null) timer.cancel();
		timer = null;

		super.dispose();
	}
	
	
	/**
	 * Main method.
	 * @param args specified arguments.
	 */
	public static void main(String[] args) {
		boolean console = args != null && args.length >= 1
			&& args[0] != null && args[0].toLowerCase().equals("console");
		if (console || GraphicsEnvironment.isHeadless()) {
			LightRemoteServerCP.console();
			return;
		}

		Connector connector = Connector.connect();
        Image image = UIUtil.getImage("server-32x32.png");
        if (image != null) connector.setIconImage(image);
		
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
		else if (!(server instanceof PowerServer))
			new RemoteServerCP(server, connectInfo);
		else
			new PowerServerCP((PowerServer)server, connectInfo);
	}
	
	
}
