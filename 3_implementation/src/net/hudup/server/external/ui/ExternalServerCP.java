/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.external.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.client.Connector;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.LightRemoteServerCP;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.RemoteServerCP;
import net.hudup.core.client.Server;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.ui.ExternalConfigurator;
import net.hudup.core.data.ui.SysConfigDlgExt;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.PluginStorageManifestPanel;
import net.hudup.core.logistic.ui.PluginStorageManifestPanelRemote;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.server.external.ExternalServerConfig;
import net.hudup.server.ui.PowerServerCP;

/**
 * This class provides a remote control panel for external server. It is an enhanced version of {@link PowerServerCP}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ExternalServerCP extends PowerServerCP {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * External configuration.
	 */
	protected JButton btnExternalConfig;
	
	
	/**
	 * Constructor with specified server and connection information of such server.
	 * @param server specified server
	 * @param connectInfo connection information of the specified.
	 */
	public ExternalServerCP(PowerServer server, ConnectInfo connectInfo) {
		super(server, connectInfo);
	}

	
	/**
	 * Constructor with specified power server.
	 * @param server specified power server.
	 */
	public ExternalServerCP(PowerServer server) {
		this(server, null);
	}

	
	@Override
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
				}
			});
		btnSystem.setMargin(new Insets(0, 0 , 0, 0));
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

		
		btnExternalConfig = new JButton("External configuration");
		btnExternalConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				externalConfig();
			}
		});
		configbar.add(btnExternalConfig);

		
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

	
	@Override
	protected void updateControls(ServerStatusEvent.Status state) {
		
		super.updateControls(state);
		
		if (state == Status.started || state == Status.resumed) {
			btnExternalConfig.setEnabled(false);
		}
		else if (state == Status.paused) {
			btnExternalConfig.setEnabled(false);
		}
		else if (state == Status.stopped) {
			btnExternalConfig.setEnabled(true);
		}
		else if (state == Status.setconfig) {
			
		}
		else if (state == Status.exit) {
		}
		
		
	}
	
	
	/**
	 * Load external configuration.
	 */
	protected void externalConfig() {
		DataConfig svrConfig = null;
		try {
			svrConfig = server.getConfig();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			svrConfig = null;
		}
		if (svrConfig == null)
			return;
		
		ExternalServerConfig extSrvConfig = new ExternalServerConfig();
		extSrvConfig.clear();
		extSrvConfig.putAll(svrConfig);
		
		ExternalConfig defaultExternalConfig = extSrvConfig.getExternalConfig(); 
		ExternalConfigurator configurator = new ExternalConfigurator(
				this, DataDriverList.get(), defaultExternalConfig);
		
		ExternalConfig externalConfig = configurator.getResult();
		if (externalConfig == null || externalConfig.size() == 0) {
			JOptionPane.showMessageDialog(
				this, 
				"Not query external configuration", 
				"Not query external configuration", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		extSrvConfig.setExternalConfig(externalConfig);
		
		paneConfig.getPropTable().updateNotSetup(extSrvConfig);
		JOptionPane.showMessageDialog(
				this, 
				"Load external configuration successfully. \n" + 
				"Please press button 'Apply configuration' to make external configuration effect", 
				"Please press button 'Apply configuration'", 
				JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	
	@Override
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

			SetupExternalServerWizard dlg = new SetupExternalServerWizard(this,
					(ExternalServerConfig)server.getConfig());
			
			DataConfig config = dlg.getServerConfig();
			if (connectInfo.bindUri != null) {
				server.setConfig(config);
				if (connectInfo.pullMode) paneConfig.update(config);
			}
			else
				paneConfig.update();
		} 
		catch (RemoteException e) {
			LogUtil.trace(e);
		}
		
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

		Connector dlg = Connector.connect();
        Image image = UIUtil.getImage("server-32x32.png");
        if (image != null) dlg.setIconImage(image);
		
		Server server = dlg.getServer();
		ConnectInfo connectInfo = dlg.getConnectInfo();
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
			new ExternalServerCP((PowerServer)server, connectInfo);
	}
	
	
}
