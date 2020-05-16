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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.client.PowerServer;
import net.hudup.core.client.ServerStatusEvent;
import net.hudup.core.client.ServerStatusEvent.Status;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.ui.ExternalConfigurator;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
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
	 * Constructor with specified server and binded URI of such server.
	 * @param server specified server
	 * @param bindUri binded URI of such server. If it is not null, the server is remote. 
	 */
	public ExternalServerCP(PowerServer server, xURI bindUri) {
		super(server, bindUri);
	}

	
	/**
	 * Constructor with specified power server.
	 * @param server specified power server.
	 */
	public ExternalServerCP(PowerServer server) {
		super(server);
	}

	
	@Override
	protected JPanel createGeneralPane() throws Exception {
		JPanel general = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		general.add(body, BorderLayout.CENTER);
		
		body.add(new JLabel("Server configuration"), BorderLayout.NORTH);
		paneConfig = new SysConfigPane();
		paneConfig.setControlVisible(false);
		paneConfig.update(server.getConfig());
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

		
		btnLoadStore = new JButton("Load store");
		btnLoadStore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadStore();
			}
		});
		configbar.add(btnLoadStore);

		
		btnExternalConfig = new JButton("External configure");
		btnExternalConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				externalConfig();
			}
		});
		configbar.add(btnExternalConfig);

		
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
				catch (RemoteException ex) {
					LogUtil.trace(ex);
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

	
	@Override
	protected void updateControls(ServerStatusEvent.Status state)
			throws RemoteException {
		
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
				"Please press button 'Apply Config' to make external configuration effect", 
				"Please press button 'Apply Config'", 
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
			
			SetupExternalServerWizard dlg = new SetupExternalServerWizard(this,
					(ExternalServerConfig)server.getConfig());
			if (bindUri != null)
				server.setConfig(dlg.getServerConfig());
			
			
		} 
		catch (RemoteException e) {
			LogUtil.trace(e);
		}
		
	}

	
}
