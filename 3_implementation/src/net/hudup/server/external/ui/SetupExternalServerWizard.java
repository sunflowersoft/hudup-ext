/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.external.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.Util;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.DefaultExternalQuery;
import net.hudup.core.data.ExternalConfig;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.Unit;
import net.hudup.core.data.ui.DataConfigTextField;
import net.hudup.core.data.ui.ExternalConfigurator;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.data.ui.UnitListBoxExt;
import net.hudup.core.data.ui.UnitTable;
import net.hudup.core.logistic.ui.WaitDialog;
import net.hudup.server.external.ExternalServerConfig;
import net.hudup.server.ui.SetupServerWizard;

/**
 * This class provides a wizard to set up external server. It is an enhanced version of {@link SetupServerWizard}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SetupExternalServerWizard extends SetupServerWizard {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with server configuration and connection information.
	 * @param comp parent component.
	 * @param srvConfig server configuration.
	 */
	public SetupExternalServerWizard(Component comp, ExternalServerConfig srvConfig) {
		this(comp, srvConfig, null);
	}
	
	
	/**
	 * Constructor with server configuration and connection information.
	 * @param comp parent component.
	 * @param srvConfig external server configuration.
	 * @param connectInfo connection information.
	 */
	public SetupExternalServerWizard(Component comp, ExternalServerConfig srvConfig, ConnectInfo connectInfo) {
		super(comp, srvConfig, connectInfo);
	}

	
	@Override
	protected JPanel createConfigPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel header = new JPanel();
		main.add(header, BorderLayout.NORTH);
		header.add(new JLabel("Configuration server"));
		
		JPanel body = new JPanel(new BorderLayout());
		main.add(body, BorderLayout.CENTER);
		
		final SysConfigPane paneConfig = new SysConfigPane();
		paneConfig.setControlVisible(false);
		paneConfig.update(config);
		body.add(new JScrollPane(paneConfig), BorderLayout.CENTER);

		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		main.add(footer, BorderLayout.SOUTH);
		
		JButton btnApplyConfig = new JButton("Apply configuration");
		btnApplyConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean apply = paneConfig.apply();
				if (!apply) {
					JOptionPane.showMessageDialog(
							getWizard(), 
							"Cannot apply configuration", 
							"Cannot apply configuration", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				else {
					JOptionPane.showMessageDialog(
							getWizard(), 
							"Apply configuration successfully", 
							"Apply successfully", 
							JOptionPane.INFORMATION_MESSAGE);
				}
			}
		});
		footer.add(btnApplyConfig);

		JButton btnResetConfig = new JButton("Reset configuration");
		btnResetConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				paneConfig.reset();
				int confirm = JOptionPane.showConfirmDialog(
						getWizard(), 
						"Reset configuration successfully. \n" + 
						"Do you want to apply configuration into being effective?", 
						"Reset configuration successfully", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				
				if (confirm == JOptionPane.YES_OPTION)
					paneConfig.apply();
				else {
					JOptionPane.showMessageDialog(
							getWizard(), 
							"Please press button 'Apply configuration' to make store configuration effect later", 
							"Please press button 'Apply configuration'", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		});
		footer.add(btnResetConfig);

		
		JButton btnExternalConfig = new JButton("External configuration");
		btnExternalConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ExternalConfig defaultExternalConfig = ((ExternalServerConfig)config).getExternalConfig();
				
				ExternalConfigurator configurator = new ExternalConfigurator(
						getWizard(), DataDriverList.get(), defaultExternalConfig);
				
				ExternalConfig externalConfig = configurator.getResult();
				if (externalConfig == null || externalConfig.size() == 0) {
					JOptionPane.showMessageDialog(
						getWizard(), 
						"Not query external configuration", 
						"Not query external configuration", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				ExternalServerConfig extSrvConfig = new ExternalServerConfig();
				extSrvConfig.clear();
				extSrvConfig.putAll(config);
				extSrvConfig.setExternalConfig(externalConfig);
				
				paneConfig.getPropTable().updateNotSetup(extSrvConfig);
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Load external configuration successfully. \n" + 
						"Please press button 'Apply configuration' to make external configuration effect", 
						"Please press button 'Apply configuration'", 
						JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		footer.add(btnExternalConfig);
		
		
		JButton btnLoadStore = new JButton("Load store");
		btnLoadStore.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				DataConfig configExt = DatasetUtil2.chooseServerConfig(getWizard(), config);
				
				if (configExt == null) {
					JOptionPane.showMessageDialog(
							getWizard(), 
							"Not load store", 
							"Not load store", 
							JOptionPane.WARNING_MESSAGE);
					return;
				}
				
				DataConfig cfg = new DataConfig();
				cfg.putAll(config);
				cfg.putAll(configExt);
				
				paneConfig.getPropTable().updateNotSetup(cfg);
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Load store configuration successfully. \n" + 
						"Please press button 'Apply configuration' to make store configuration effect", 
						"Please press button 'Apply configuration'", 
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		footer.add(btnLoadStore);

		
		return main;
	}
	

	@Override
	protected JPanel createImportDataPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		main.add(body, BorderLayout.CENTER);
		
		final UnitTable unitTable = Util.getFactory().createUnitTable(config.getStoreUri());
		body.add(unitTable.getComponent(), BorderLayout.CENTER);

		final UnitListBoxExt unitList = new UnitListBoxExt() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void clearData() {
				super.clearData();
				unitTable.clear();
			}

			@Override
			public void modify() {
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Not support this method", 
						"Not support this method", 
						JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void drop() {
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Not support this method", 
						"Not support this method", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			
		};
		unitList.connectUpdate(config);
		body.add(new JScrollPane(unitList), BorderLayout.WEST);
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

		
		JPanel header = new JPanel(new BorderLayout());
		main.add(header, BorderLayout.NORTH);
		header.add(new JLabel("Import data"), BorderLayout.NORTH);
		
		JPanel toolbar = new JPanel(new BorderLayout());
		header.add(toolbar, BorderLayout.SOUTH);
		
		final DataConfigTextField txtSrc = new DataConfigTextField();
		toolbar.add(txtSrc, BorderLayout.CENTER);
		
		JPanel importPane = new JPanel();
		toolbar.add(importPane, BorderLayout.EAST);
		JButton btnImport = new JButton("Import");
		importPane.add(btnImport);
		btnImport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				DataConfig srcConfig = DatasetUtil2.chooseConfig(getWizard(), null);
				
				if (srcConfig == null) {
					JOptionPane.showMessageDialog(
						getWizard(), 
						"Not open data config", 
						"Not open data config", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				JDialog dlgWait = WaitDialog.createDialog(getWizard()); dlgWait.setUndecorated(true);
				SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
					@Override
					protected Void doInBackground() throws Exception {
						provider.importData(srcConfig, false, null);
						return null;
					}
					
					@Override
					protected void done() {
						super.done(); dlgWait.dispose();
					}
				};
				worker.execute(); dlgWait.setVisible(true);
				
				unitList.connectUpdate(config);
				
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Import data successfully", 
						"Import data successfully", 
						JOptionPane.INFORMATION_MESSAGE);

				txtSrc.setConfig(srcConfig);
			}
		});


		JButton btnImportExternal = new JButton("External query");
		importPane.add(btnImportExternal);
		btnImportExternal.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ExternalConfig externalConfig = ((ExternalServerConfig) config).getExternalConfig();
				
				if (externalConfig == null || externalConfig.size() == 0) {
					JOptionPane.showMessageDialog(
						getWizard(), 
						"External configuration empty", 
						"External configuration empty", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}

				try {
					ExternalQuery externalQuery = new DefaultExternalQuery();
					boolean setup = externalQuery.setup(config, externalConfig);
					if (!setup)
						externalQuery.close();
					else {
						JDialog wait = new JDialog(getWizard(), "Please wait...", false);
						wait.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
						wait.setSize(200, 100);
						wait.setLocationRelativeTo(getWizard());
						wait.setVisible(true);
	
						externalQuery.importData(null);
						externalQuery.close();
						
						wait.dispose();
					}
				}
				catch (Throwable ex) {ex.printStackTrace();}
				
				JOptionPane.showMessageDialog(
					getWizard(), 
					"Import external succuessfully", 
					"Import external succuessfully", 
					JOptionPane.INFORMATION_MESSAGE);
			}
		});

		
		return main;
	}

	
}
