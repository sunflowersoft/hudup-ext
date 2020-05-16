/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.BooleanWrapper;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.data.ui.AttributeListTable;
import net.hudup.core.data.ui.DataConfigTextField;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.data.ui.UnitListBoxExt;
import net.hudup.core.data.ui.UnitTable;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.logistic.ui.WaitDialog;
import net.hudup.server.PowerServerConfig;

/**
 * This class provides a wizard to set up server.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SetupServerWizard extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Steps to set up server.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	protected enum Step {
		
		/**
		 * Configuration step.
		 */
		config,
		
		/**
		 * Creating schema step.
		 */
		create_schema,
		
		/**
		 * Importing data step.
		 */
		import_data,
		
		/**
		 * Finished step.
		 */
		finished
	}
	
	
	/**
	 * Current step.
	 */
	protected Step currentStep = Step.config;
	
	/**
	 * Back button.
	 */
	protected JButton btnBack = null;
	
	/**
	 * Next button.
	 */
	protected JButton btnNext = null;
	
	/**
	 * Finished button.
	 */
	protected JButton btnFinished = null;
	
	/**
	 * Cancel button.
	 */
	protected JButton btnCancel = null;
	
	/**
	 * Main panel.
	 */
	protected JPanel main = null;

	
	/**
	 * Server configuration.
	 */
	protected PowerServerConfig config = null;
	
	/**
	 * Provider.
	 */
	protected Provider provider = null;

	
	/**
	 * Constructor with server configuration.
	 * @param comp parent component.
	 * @param srvConfig server configuration.
	 */
	public SetupServerWizard(Component comp, PowerServerConfig srvConfig) {
		super(UIUtil.getFrameForComponent(comp), "Setup server", true);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		if (UIUtil.getFrameForComponent(comp) == null) {
	        Image image = UIUtil.getImage("server-32x32.png");
	        if (image != null)
	        	setIconImage(image);
		}
		
		this.config = srvConfig;
		
		setLayout(new BorderLayout());
		main = new JPanel(new BorderLayout());
		add(main, BorderLayout.CENTER);
		
		add(createFooter(), BorderLayout.SOUTH);
		
		updateStep();
		
		setVisible(true);
		
	}
	
	
	/**
	 * Getting this wizard.
	 * @return this wizard.
	 */
	protected SetupServerWizard getWizard() {
		return this;
	}
	
	
	/**
	 * Creating footer panel.
	 * @return footer panel.
	 */
	private JPanel createFooter() {
		JPanel footer = new JPanel();
		
		btnBack = new JButton("Back");
		btnBack.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				back();
			}
		});
		footer.add(btnBack);
		
		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				next();
			}
		});
		footer.add(btnNext);

		
		btnFinished = new JButton("Finished");
		btnFinished.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				finished();
			}
		});
		footer.add(btnFinished);

		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
			}
		});
		footer.add(btnCancel);

		
		return footer;
		
	}
	
	
	/**
	 * Create provider.
	 */
	private void createProvider() {
		if (this.provider != null) {
			this.provider.close();
			this.provider = null;
		}
		
		this.provider = new ProviderImpl(config);
	}

	
	/**
	 * Create configuration panel.
	 * @return configuration panel.
	 */
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
		
		JButton btnApplyConfig = new JButton("Apply config");
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

		JButton btnResetConfig = new JButton("Reset config");
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
							"Please press button 'Apply Config' to make store configuration effect later", 
							"Please press button 'Apply Config' to make store configuration effect later", 
							JOptionPane.INFORMATION_MESSAGE);
				}

			
			}
		});
		footer.add(btnResetConfig);

		
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
						"Please press button 'Apply Config' to make store configuration effect", 
						"Please press button 'Apply Config' to make store configuration effect", 
						JOptionPane.INFORMATION_MESSAGE);
			}
		});
		footer.add(btnLoadStore);

		
		return main;
	}

	
	/**
	 * Create schema panel.
	 * @return schema panel.
	 */
	private JPanel createCreateSchemaPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel header = new JPanel();
		main.add(header, BorderLayout.NORTH);
		header.add(new JLabel("Creating schema"));
		
		JPanel body = new JPanel(new BorderLayout());
		main.add(body, BorderLayout.CENTER);
		
		final AttributeListTable attTable = new AttributeListTable();
		attTable.setEnabled(false);
		body.add(new JScrollPane(attTable), BorderLayout.CENTER);
		
		final UnitListBoxExt unitList = new UnitListBoxExt() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void clearData() {
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Not support this method", 
						"Not support this method", 
						JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void modify() {
				Unit selected = getSelectedValue();
				if (selected == null)
					return;
				
				UnitList modifiableList = getWizard().config.getModifiableUnitList();
				UnitList mainList = getWizard().config.getUnitList();
				if (mainList.contains(selected) && !modifiableList.contains(selected)) {
					JOptionPane.showMessageDialog(
							getWizard(), 
							"Unit not modifiable", 
							"Unit not modifiable", 
							JOptionPane.WARNING_MESSAGE);
					
					return;
				}
				
				boolean result = createModifyUnit(selected.getName());
				if (!result) {
					JOptionPane.showMessageDialog(
						getWizard(), 
						"Unit not modified or created", 
						"Unit not modified or created", 
						JOptionPane.ERROR_MESSAGE);
					
					return;
				}
				connectUpdate(getWizard().config);
				clearSelection();
				attTable.clear();
			}


			@Override
			public void drop() {
				super.drop();
				clearSelection();
				attTable.clear();
			}
			
		};
		
		unitList.connectUpdate(config);
		body.add(new JScrollPane(unitList), BorderLayout.WEST);
		unitList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
			
				Unit unit = unitList.getSelectedValue();
				if (unit == null) {
					attTable.clear();
					return;
				}
				
				AttributeList attList = provider.getProfileAttributes(unit.getName());
				attTable.set(attList);
				
			}
		});
		
		
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		main.add(footer, BorderLayout.SOUTH);
		JButton createSchema = new JButton("Create schema");
		createSchema.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (unitList.getUnitList().getMainList().size() > 0) {
					int confirm = JOptionPane.showConfirmDialog(
							getWizard(), 
							"Schema created. Do you want to renew it?", 
							"Schema created. Do you want to renew it?", 
							JOptionPane.YES_NO_OPTION, 
							JOptionPane.INFORMATION_MESSAGE);
					
					if (confirm == JOptionPane.NO_OPTION)
						return;
				}
				
				boolean result = createSchema();
				provider.getCTSManager().reload();
				unitList.connectUpdate(config);
				unitList.clearSelection();
				attTable.clear();
				
				if (!result) {
					JOptionPane.showMessageDialog(
						getWizard(), 
						"Schema not created", 
						"Schema not created", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Schema created successfully", 
						"Schema created successfully", 
						JOptionPane.INFORMATION_MESSAGE);
				
				int response = JOptionPane.showConfirmDialog(getWizard(), 
						"Do you want to configure context template?", 
						"Configure context template", JOptionPane.YES_NO_OPTION);
				if (response == JOptionPane.YES_OPTION) {
					provider.getCTSManager().getInspector().inspect();
					provider.getCTSManager().reload();
					unitList.connectUpdate(config);
					unitList.clearSelection();
					attTable.clear();
				}
			}
		});
		footer.add(createSchema);

		JButton dropSchema = new JButton("Drop schema");
		dropSchema.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (unitList.getUnitList().size() == 0) {
					JOptionPane.showMessageDialog(
							getWizard(), 
							"Schema empty", 
							"Schema empty", 
							JOptionPane.INFORMATION_MESSAGE);
						
						return;
				}
				
				provider.dropSchema();
				provider.getCTSManager().reload();
				config.removeUnitList(DataConfig.getDefaultUnitList());
				config.putUnitList(provider.getUnitList().getMainList());
				unitList.connectUpdate(config);
				
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Schema dropped successfully", 
						"Schema dropped successfully", 
						JOptionPane.INFORMATION_MESSAGE);
				
			}
		});
		footer.add(dropSchema);
		
		JButton newExtraUnit = new JButton("New extra unit");
		newExtraUnit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				boolean result = createModifyUnit(null);
				unitList.connectUpdate(config);
				unitList.clearSelection();
				attTable.clear();
				
				if (!result) {
					JOptionPane.showMessageDialog(
						getWizard(), 
						"Extra unit not created", 
						"Extra unit not created", 
						JOptionPane.ERROR_MESSAGE);
					
				}
				else {
					JOptionPane.showMessageDialog(
							null, 
							"Extra unit created successfully", 
							"Extra unit created successfully", 
							JOptionPane.INFORMATION_MESSAGE);
					
				}
			}
		});
		footer.add(newExtraUnit);

		
		return main;
	}

	
	/**
	 * Create schema.
	 * @return whether create schema successfully.
	 */
	private boolean createSchema() {
		final JDialog createAttDlg = new JDialog(
				UIUtil.getFrameForComponent(this), "Creating schema", true);
		createAttDlg.setLayout(new BorderLayout());
		createAttDlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		createAttDlg.setSize(600, 400);
		
		JPanel body = new JPanel(new GridLayout(1, 0));
		createAttDlg.add(body, BorderLayout.CENTER);
		
		JPanel user = new JPanel(new BorderLayout());
		body.add(user);
		user.add(new JLabel("User attributes"), BorderLayout.NORTH);
		final AttributeListTable userTable = new AttributeListTable();
		if (config.getUserUnit() != null) {
			AttributeList preUserAtt = null;
			try {
				preUserAtt = provider.getProfileAttributes(config.getUserUnit());
			}
			catch (Throwable e) {
				LogUtil.trace(e); //Exception here is not a problem.
				preUserAtt = null;
			}
			
			if (preUserAtt != null && preUserAtt.size() > 0) //When database is empty, there is no attribute.
				userTable.set(preUserAtt);
		}
		user.add(new JScrollPane(userTable), BorderLayout.CENTER);
		
		JPanel item = new JPanel(new BorderLayout());
		body.add(item);
		item.add(new JLabel("Item attributes"), BorderLayout.NORTH);
		final AttributeListTable itemTable = new AttributeListTable();
		if (config.getItemUnit() != null) {
			AttributeList preItemAtt = null;
			try {
				preItemAtt = provider.getProfileAttributes(config.getItemUnit());
			}
			catch (Throwable e) {
				LogUtil.trace(e); //Exception here is not a problem.
				preItemAtt = null;
			}
			
			if (preItemAtt != null && preItemAtt.size() > 0) //When database is empty, there is no attribute.
				itemTable.set(preItemAtt);
		}
		item.add(new JScrollPane(itemTable), BorderLayout.CENTER);

		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		createAttDlg.add(footer, BorderLayout.SOUTH);
		
		final BooleanWrapper flag = new BooleanWrapper(false);

		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				userTable.apply();
				itemTable.apply();
				flag.set(true);
				createAttDlg.dispose();
			}
		});
		footer.add(create);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				createAttDlg.dispose();
			}
		});
		footer.add(cancel);

		createAttDlg.setVisible(true);
		if (!flag.get())
			return false;
		
		AttributeList userAtt = userTable.getAttributeList();
		if (userAtt.size() == 0)
			userAtt = AttributeList.defaultUserAttributeList();
		userAtt = userAtt.nomalizeId(
				DataConfig.USERID_FIELD, Attribute.Type.integer, Constants.SUPPORT_AUTO_INCREMENT_ID);
		
		AttributeList itemAtt = itemTable.getAttributeList();
		if (itemAtt.size() == 0)
			itemAtt = AttributeList.defaultItemAttributeList();
		itemAtt = itemAtt.nomalizeId(
				DataConfig.ITEMID_FIELD, Attribute.Type.integer, Constants.SUPPORT_AUTO_INCREMENT_ID);
		
		if (userAtt.size() < 1 || itemAtt.size() < 1)
			return false;
		
		config.putUnitList(DataConfig.getDefaultUnitList());
		provider.createSchema(userAtt, itemAtt);
		config.removeUnitList(DataConfig.getDefaultUnitList());
		config.putUnitList(provider.getUnitList().getMainList());
		
		return true;
	}
	
	
	/**
	 * Create or modify extra unit.
	 * @return whether create or modify extra unit successfully.
	 */
	private boolean createModifyUnit(String newUnit) {
		String ctxTemplateUnit = config.getContextTemplateUnit();
		if (ctxTemplateUnit != null && newUnit != null && newUnit.equals(ctxTemplateUnit)) {
			provider.getCTSManager().getInspector().inspect();
			provider.getCTSManager().reload();
			return true;
		}
		
		final JDialog createDlg = new JDialog(
				UIUtil.getFrameForComponent(this), "Creating unit", true);
		createDlg.setLayout(new BorderLayout());
		createDlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		createDlg.setSize(600, 400);
		
		JPanel header = new JPanel();
		createDlg.add(header, BorderLayout.NORTH);
		
		JPanel unitPane = new JPanel();
		header.add(unitPane);
		unitPane.add(new JLabel("Unit name:"));
		final JTextField txtUnitName = new JTextField("newUnit");
		if (newUnit != null && !newUnit.isEmpty()) {
			txtUnitName.setText(newUnit);
			txtUnitName.setEditable(false);
		}
		unitPane.add(txtUnitName);
		

		JPanel body = new JPanel(new BorderLayout());
		createDlg.add(body, BorderLayout.CENTER);
		
		UnitList unitList = provider.getUnitList();
		final AttributeListTable attTable = new AttributeListTable();
		if (newUnit != null && !newUnit.isEmpty() && unitList.contains(newUnit)) {
			attTable.set(provider.getProfileAttributes(newUnit));
		}
		body.add(new JScrollPane(attTable), BorderLayout.CENTER);
		
		JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		createDlg.add(footer, BorderLayout.SOUTH);
		
		final BooleanWrapper flag = new BooleanWrapper(false);

		JButton create = new JButton("Create");
		create.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String unitName = txtUnitName.getText().trim();
				if (unitName.isEmpty()) {
					JOptionPane.showMessageDialog(
							getWizard(), 
							"Empty unit name", 
							"Empty unit name", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				attTable.apply();
				AttributeList attList = attTable.getAttributeList();
				
				if (attList.size() < 1) {
					JOptionPane.showMessageDialog(
							getWizard(), 
							"Empty attribute", 
							"Empty attribute", 
							JOptionPane.INFORMATION_MESSAGE);
					return;
				}
				
				if (attList.getKeys().size() == 0) {
					JOptionPane.showMessageDialog(
						getWizard(), 
						"Unit has no key", 
						"Unit has no key", 
						JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				flag.set(true);
				createDlg.dispose();
			}
		});
		footer.add(create);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				createDlg.dispose();
			}
		});
		footer.add(cancel);

		createDlg.setVisible(true);
		if (!flag.get())
			return false;
		
		
		String unitName = txtUnitName.getText().trim();
		if (newUnit != null && !newUnit.isEmpty()) {
			if (unitList.contains(newUnit)) {
				JOptionPane.showMessageDialog(
						getWizard(), 
						"Unit " + unitName + " exist. Drop it before create new one", 
						"Unit exist", 
						JOptionPane.INFORMATION_MESSAGE);
				provider.dropUnit(newUnit);
			}
		}
		
		AttributeList attList = attTable.getAttributeList();
		provider.createUnit(unitName, attList);
		
		return true;
	}

	
	/**
	 * Create importing data panel.
	 * @return importing data panel.
	 */
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
		
		JButton btnImport = new JButton("Import");
		toolbar.add(btnImport, BorderLayout.EAST);
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


		return main;
	}
	
	
	/**
	 * Create finished panel.
	 * @return finished panel.
	 */
	private JPanel createFinishedPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel header = new JPanel();
		main.add(header, BorderLayout.NORTH);
		header.add(new JLabel("Finished"));
		
		JPanel body = new JPanel();
		main.add(body, BorderLayout.CENTER);
		body.add(new JLabel("Thank you for setting up server"), BorderLayout.CENTER);
		
		return main;
	}

	
	/**
	 * Setting visibility of controls according to specified visible flag.
	 * @param flag visible flag.
	 */
	private void setVisibleControls(boolean flag) {
		btnBack.setVisible(flag);
		btnNext.setVisible(flag);
		btnFinished.setVisible(flag);
		btnCancel.setVisible(flag);
	}
	
	
	/**
	 * Back step.
	 */
	private void back() {
		switch (currentStep) {
		case config:
			JOptionPane.showMessageDialog(
					this, 
					"Wrong current step", 
					"Wrong current step", 
					JOptionPane.ERROR_MESSAGE);
			break;
		case create_schema:
			createProvider();
			currentStep = Step.config;
			break;
		case import_data:
			currentStep = Step.create_schema;
			break;
		case finished:
			currentStep = Step.import_data;
			break;
		}
		
		updateStep();
	}
	
	
	/**
	 * Next step.
	 */
	private void next() {
		switch (currentStep) {
		case config:
			createProvider();
			if (provider == null) {
				JOptionPane.showMessageDialog(
						this, 
						"Can't move next because of invalid provider", 
						"Null provider", 
						JOptionPane.INFORMATION_MESSAGE);
				break;
			}
			currentStep = Step.create_schema;
			break;
		case create_schema:
			if (!config.getUnitList().contains(DataConfig.getDefaultUnitList())) {
				JOptionPane.showMessageDialog(
						this, 
						"Schema creation not complete", 
						"Schema creation not complete", 
						JOptionPane.INFORMATION_MESSAGE);
				break;
			}
				
			currentStep = Step.import_data;
			break;
		case import_data:
			currentStep = Step.finished;
			break;
		case finished:
			JOptionPane.showMessageDialog(
					this, 
					"Wrong current step", 
					"Wrong current step", 
					JOptionPane.ERROR_MESSAGE);
			break;
		}
		
		updateStep();
	}
	
	
	/**
	 * Update step.
	 */
	private void updateStep() {
		main.removeAll();
		setVisibleControls(false);
		
		switch (currentStep) {
		case config:
			main.add(createConfigPane(), BorderLayout.CENTER);
			btnNext.setVisible(true);
			btnCancel.setVisible(true);
			break;
		case create_schema:
			main.add(createCreateSchemaPane(), BorderLayout.CENTER);
			btnBack.setVisible(true);
			btnNext.setVisible(true);
			btnCancel.setVisible(true);
			break;
		case import_data:
			main.add(createImportDataPane(), BorderLayout.CENTER);
			btnBack.setVisible(true);
			btnNext.setVisible(true);
			btnCancel.setVisible(true);
			break;
		case finished:
			main.add(createFinishedPane(), BorderLayout.CENTER);
			btnBack.setVisible(true);
			btnFinished.setVisible(true);
			break;
		}
		
		main.updateUI();
		config.save();
	}
	
	
	/**
	 * Finish the setting up server process.
	 */
	private void finished() {
		config.save();
		dispose();
	}
	
	
	/**
	 * Cancel the setting up server process.
	 */
	private void cancel() {
		if (provider == null) {
			dispose();
			return;
		}
		
		UnitList unitList = config.getUnitList();
		
		if (!unitList.contains(DataConfig.getDefaultUnitList())) {
			int confirm = JOptionPane.showConfirmDialog(
					this, 
					"Schema creation not complete", 
					"Schema creation not complete", 
					JOptionPane.YES_NO_OPTION);
			
			if (confirm == JOptionPane.OK_OPTION)
				dispose();
		}
		else
			dispose();
		
		config.save();
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
		
		if (provider != null)
			provider.close();
		provider = null;
	}
	
	
	/**
	 * Testing the setting up server process is finished.
	 * @return whether finished
	 */
	public boolean isFinished() {
		return currentStep == Step.finished;
	}
	
	
	/**
	 * Getting server configuration.
	 * @return server configuration.
	 */
	public PowerServerConfig getServerConfig() {
		return config;
	}
	
	
}
