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
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

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
import net.hudup.core.client.ConnectDlg;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.RemoteServerCP;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.client.ServerStatusListener;
import net.hudup.core.data.DataConfig;
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
	 * Binded URI of this control panel.
	 */
	protected xURI bindUri = null;
	
	
	/**
	 * Constructor with specified server and binded URI of such server.
	 * @param server specified server
	 * @param bindUri bound URI of such server. If it is not null, the server is remote.
	 */
	public PowerServerCP(PowerServer server, xURI bindUri) {
		super("Server control panel");
		try {
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setSize(600, 400);
			setLocationRelativeTo(null);
			
	        Image image = UIUtil.getImage("server-32x32.png");
	        if (image != null)
	        	setIconImage(image);

			this.server = server;
			this.bindUri = bindUri;
			
		    Container container = getContentPane();
			JTabbedPane main = new JTabbedPane();
			container.add(main);
			
			main.add(createGeneralPane(), "General");
			main.add(createStorePane(), "Store");
			main.add(createAccountPane(), "Account");
			//main.add(new SystemPropertiesPane(), "System properties");

			bindServer();
			
			updateControls();
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
	    setJMenuBar(createMenuBar());
	}
	
	
	/**
	 * Binding remote server.
	 * @throws RemoteException if any error raises.
	 */
	protected void bindServer() throws RemoteException {
		boolean result = false;
		
		if (bindUri == null) {
			result = server.addStatusListener(this);
		}
		else {
			try {
				UnicastRemoteObject.exportObject(this, bindUri.getPort());
				
				result = server.addStatusListener(this);
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
		this.btnSystem = UIUtil.makeIconButton(
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
							return new PluginStorageManifestPanelRemote(server, bindUri);
						}
						
					};
					
					cfg.removeSysConfigPane();
//					try {
//						cfg.getPluginStorageManifest().setEnabled(!server.isStarted());
//					}
//					catch (Exception e1) {LogUtil.trace(e1);}
					
					cfg.setVisible(true);
				}
			});
		this.btnSystem.setMargin(new Insets(0, 0 , 0, 0));
		configGrp1.add(this.btnSystem, BorderLayout.EAST);
		
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
				try {
					updateControls();
				} 
				catch (RemoteException e1) {
					LogUtil.trace(e1);
				}
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
	private JPanel createStorePane() throws Exception {
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
	private JPanel createAccountPane() {
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
	private void updateAccData() {
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
	protected void start() {
		try {
			server.start();
			
			if (btnRefresh.isVisible())
				updateControls();
			
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Pause and resume remote server.
	 */
	protected void pauseResume() {
		try {
			if (server.isPaused())
				server.resume();
			else if (server.isRunning())
				server.pause();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Stop remote server.
	 */
	protected void stop() {
		try {
			server.stop();
			
			if (btnRefresh.isVisible())
				updateControls();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
	}
	
	
	/**
	 * Setting up remote server.
	 */
	protected void setupServer() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't set up", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			SetupServerWizard dlg = new SetupServerWizard(this,
					(PowerServerConfig)server.getConfig());
			if (bindUri != null)
				server.setConfig(dlg.getServerConfig());
			
			
		} 
		catch (RemoteException e) {
			LogUtil.trace(e);
		}
		
	}
	
	
	/**
	 * Exiting remote server.
	 */
	protected void exit() {
		if (bindUri != null) {
			try {
				server.removeStatusListener(this);
			} catch (Exception e) {LogUtil.trace(e);}
			try {
				server.exit();
			} catch (Exception e) {}
			server = null;
		}
		else {
			if (provider != null) provider.close();
			provider = null;

			try {
				server.removeStatusListener(this);
			} catch (Exception e) {LogUtil.trace(e);}
			try {
				server.exit();
			} catch (Exception e) {}
			server = null;
		}
		
		dispose();
	}
	
	
	/**
	 * Applying configuration to remote server.
	 */
	protected void applyConfig() {
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
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Reset configuration for remote server.
	 */
	protected void resetConfig() {
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
	 * Loading store for remote server.
	 */
	protected void loadStore() {
		try {
			if (server.isRunning()) {
				JOptionPane.showMessageDialog(
					this, 
					"Server running. Can't load store", 
					"Server running", 
					JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			DataConfig config = DatasetUtil2.chooseServerConfig(this, server.getConfig());
			
			if (config == null) {
				JOptionPane.showMessageDialog(
						this, 
						"Not load store", 
						"Not load store", 
						JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			DataConfig cfg = new DataConfig();
			cfg.putAll(server.getConfig());
			cfg.putAll(config);
			
			paneConfig.getPropTable().updateNotSetup(cfg);
			JOptionPane.showMessageDialog(
					this, 
					"Load store configuration successfully. \n" + 
					"Please press button 'Apply Config' to make store configuration effect", 
					"Please press button 'Apply Config'", 
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
	private void enableControls(boolean enabled) {
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
	 * @param state specified remote status.
	 * @throws RemoteException if any error raises.
	 */
	protected void updateControls(ServerStatusEvent.Status state)
			throws RemoteException {
		
		if (state == Status.started || state == Status.resumed) {
			enableControls(false);
			DataConfig config = server.getConfig();

			if (provider != null)
				provider.close();
			provider = new ProviderImpl(config);

			btnSetupServer.setEnabled(false);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && bindUri == null);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(true);
			btnSystem.setEnabled(true /*&& !bRemote*/);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnLoadStore.setEnabled(false);
			paneConfig.setEnabled(false);
			
			btnUpdateAcc.setEnabled(true);
			btnDeleteAcc.setEnabled(true);
			txtAccName.setEnabled(true);
			txtAccPass.setEnabled(true);
			txtAccPrivs.setEnabled(true);
			
			unitList.connectUpdate(config);
			unitList.validate();
			unitList.updateUI();
			
			accUnitTable.update(provider.getAssoc(), config.getAccountUnit());
			updateAccData();
			
			try {
				paneConfig.update(config);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
		}
		else if (state == Status.paused) {
			enableControls(false);

			btnSetupServer.setEnabled(false);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(false);
			btnPauseResume.setEnabled(true && bindUri == null);
			btnPauseResume.setText("Resume");
			btnStop.setEnabled(true);
			btnSystem.setEnabled(true /*&& !bRemote*/);
			
			btnApplyConfig.setEnabled(false);
			btnResetConfig.setEnabled(false);
			btnLoadStore.setEnabled(false);
			paneConfig.setEnabled(false);
			
			btnUpdateAcc.setEnabled(true);
			btnDeleteAcc.setEnabled(true);
			txtAccName.setEnabled(true);
			txtAccPass.setEnabled(true);
			txtAccPrivs.setEnabled(true);
		}
		else if (state == Status.stopped) {
			enableControls(false);

			btnSetupServer.setEnabled(true);
			btnExitServer.setEnabled(true);
			btnStart.setEnabled(true);
			btnPauseResume.setEnabled(false);
			btnPauseResume.setText("Pause");
			btnStop.setEnabled(false);
			btnSystem.setEnabled(true /*&& !bRemote*/);
			
			btnApplyConfig.setEnabled(true);
			btnResetConfig.setEnabled(true);
			btnLoadStore.setEnabled(true);
			paneConfig.setEnabled(true);
			
			btnUpdateAcc.setEnabled(false);
			btnDeleteAcc.setEnabled(false);
			txtAccName.setEnabled(false);
			txtAccPass.setEnabled(false);
			txtAccPrivs.setEnabled(false);

			try {
				unitTable.clear();
				accUnitTable.clear();
				updateAccData();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
			unitList.setListData(new Unit[0]);
			unitList.validate();
			unitList.updateUI();
			
			if (provider != null)
				provider.close();
			provider = null;

		}
		else if (state == Status.setconfig) {
			paneConfig.update(server.getConfig());
		}
		else if (state == Status.exit) {
			server = null;
			if (bindUri != null)
				dispose();
			else if (provider != null)
				provider.close();
			
			bindUri = null;
			provider = null;
		}
	}
	
	
	/**
	 * Update controls.
	 * @throws RemoteException if any error raises.
	 */
	protected void updateControls() throws RemoteException {
		if (server == null)
			return;

		if (server.isRunning()) {
			updateControls(Status.started);
			btnRefresh.setEnabled(true);
		}
		else if (server.isPaused()) {
			updateControls(Status.paused);
			btnRefresh.setEnabled(true);
		}
		else {
			updateControls(Status.stopped);
			btnRefresh.setEnabled(true);
		}
			
	}
	
	
	@Override
	public void statusChanged(ServerStatusEvent evt) throws RemoteException {
		if (bindUri != null)
			updateControls(evt.getStatus());
		else if (!evt.getShutdownHookStatus())
			updateControls(evt.getStatus());
	}
	

	@Override
	public void dispose() {
		try {
			if (server != null) server.removeStatusListener(this);
		}
		catch (Throwable e) {LogUtil.trace(e);}
		server = null;
		
		try {
			if (bindUri != null) UnicastRemoteObject.unexportObject(this, true);
		}
		catch (Throwable e) {LogUtil.trace(e);}
		bindUri = null;
		
		if (provider != null) provider.close();
		provider = null;
		
		super.dispose();
	}
	
	
	/**
	 * Main method.
	 * @param args specified arguments.
	 */
	public static void main(String[] args) {
		ConnectDlg dlg = ConnectDlg.connect();
		
		Server server = dlg.getServer();
		if (server != null)
			new PowerServerCP((PowerServer)server, dlg.getConnectInfo().bindUri);
		else {
			JOptionPane.showMessageDialog(
					null, "Can't retrieve server", "Can't retrieve server", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
}
