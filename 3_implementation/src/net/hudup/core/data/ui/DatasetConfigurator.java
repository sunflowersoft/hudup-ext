/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.NumberFormatter;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.HiddenText;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.TextField;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.DatasetParser;

/**
 * This class represents the main graphic user interface (GUI) allowing users to select the configuration of dataset.
 * Such configuration represented by {@link DataConfig} is returned by the method {@link #getResultedConfig()} of this class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetConfigurator extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Setting array.
	 */
	protected static final Object[][] settings = {
			{DataConfig.ACCOUNT_UNIT, "Set account", true},
			{DataConfig.ATTRIBUTE_MAP_UNIT, "Set attribute", true},
			{DataConfig.CONFIG_UNIT, "Set config", true},
			{DataConfig.CONTEXT_UNIT, "Set context", true},
			{DataConfig.CONTEXT_TEMPLATE_UNIT, "Set ctx template", true},
			{DataConfig.ITEM_UNIT, "Set item", true},
			{DataConfig.NOMINAL_UNIT, "Set nominal", true},
			{DataConfig.RATING_UNIT, "Set rating", true},
			{DataConfig.USER_UNIT, "Set user", true},
			{DataConfig.SAMPLE_UNIT, "Set sample", true},
		};
	
	
	/**
	 * Host text field.
	 */
	private JTextField txtHost = null;
	
	/**
	 * Port text field.
	 */
	private JFormattedTextField txtPort = null;
	
	/**
	 * Path text field.
	 */
	private TextField txtPath = null;

	/**
	 * Path button.
	 */
	private JButton btnPath = null;
	
	/**
	 * User name text field.
	 */
	private JTextField txtUsername = null;
	
	/**
	 * Password text field.
	 */
	private JPasswordField txtPassword = null;
	
	/**
	 * Dataset parser combo-box.
	 */
	private DatasetParserComboBox cmbParsers = null;
	
	/**
	 * Data driver combo-box.
	 */
	private DataDriverComboBox cmbDataDrivers = null;
	
	/**
	 * List box of units.
	 */
	private UnitListBox lbUnits = null;
	
	/**
	 * Properties (data configuration) table.
	 */
	private PropTable tblConfig = null;

	
	/**
	 * Reference to main unit, which is the key of main unit in configuration. It can be null.
	 */
	private String mainUnit = null;
	
	
	/**
	 * Data driver list.
	 */
	private DataDriverList thisDataDriverList = null;
	
	/**
	 * Internal configuration.
	 */
	private DataConfig internalConfig = null;
	
	/**
	 * Resulted configuration returned by {@link #getResultedConfig()}.
	 */
	private DataConfig resultedConfig = null;
	
	
	/**
	 * Constructor with specified parser list, data driver list, and default configuration.
	 * @param comp parent control of this dialog.
	 * @param parserList specified parser list.
	 * @param dataDriverList specified data driver list.
	 * @param defaultConfig default configuration.
	 */
	public DatasetConfigurator(
			Component comp, 
			List<Alg> parserList, 
			DataDriverList dataDriverList,
			DataConfig defaultConfig) {
		super(UIUtil.getDialogForComponent(comp), "Data configurator", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		
		this.thisDataDriverList = dataDriverList;
		this.internalConfig = new DataConfig();
		if (defaultConfig != null)
			this.mainUnit = defaultConfig.getMainUnit();
		
		setLayout(new BorderLayout());
		
		////////////////////// Footer //////////////////////////
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				onOk();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				resultedConfig = null;
				internalConfig = null;
				dispose();
			}
		});
		footer.add(cancel);

		////////////////////// Body //////////////////////////
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		body.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("List of units"), BorderLayout.NORTH);
		lbUnits = new UnitListBoxExt() {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected JPopupMenu createContextMenu() {
				if (config == null || getSelectedValue() == null) return null;
				
				JPopupMenu ctxMenu = new JPopupMenu();
				for (int i = 0; i < settings.length; i++) {
					final int index = i;
					JMenuItem mi = UIUtil.makeMenuItem((String)null, (String)settings[index][1], 
							new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									setUnit((String)settings[index][0]);
								}
							});
					ctxMenu.add(mi);
				}
				
				return ctxMenu;
			}
			
		};
		left.add(new JScrollPane(lbUnits), BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(0, 1));
		left.add(buttons, BorderLayout.EAST);
		
		for (int i = 0; i < settings.length; i++) {
			final int index = i;
			if (!(boolean)settings[index][2]) continue;
			
			JButton button = new JButton((String)settings[index][1]);
			button.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					setUnit((String)settings[index][0]);
				}
			});
			buttons.add(button);
		}
		
		
		JPanel right = new JPanel(new BorderLayout());
		body.add(right, BorderLayout.CENTER);
		
		right.add(new JLabel("Configurations"), BorderLayout.NORTH);
		tblConfig = new PropTable();
		right.add(new JScrollPane(tblConfig), BorderLayout.CENTER);

		
		////////////////////// Header //////////////////////////
		JPanel pane = null;
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel attNames = new JPanel(new GridLayout(0, 1));
		header.add(attNames, BorderLayout.WEST);
		attNames.add(new JLabel("Host:"));
		attNames.add(new JLabel("Port:"));
		attNames.add(new JLabel("Path:"));
		attNames.add(new JLabel("Username:"));
		attNames.add(new JLabel("Password:"));

		JPanel attValues = new JPanel(new GridLayout(0, 1));
		header.add(attValues, BorderLayout.CENTER);
		//
		txtHost = new JTextField("localhost");
		attValues.add(txtHost);
		//
		txtPort = new JFormattedTextField(new NumberFormatter());
		txtPort.setValue(-1);
		attValues.add(txtPort);
		//
		pane = new JPanel(new BorderLayout());
		attValues.add(pane);
		txtPath = new TextField();
		pane.add(txtPath, BorderLayout.CENTER);
		btnPath = new JButton("Browse");
		pane.add(btnPath, BorderLayout.EAST);
		btnPath.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				UriAdapter adapter = new UriAdapter();
				xURI uri = adapter.chooseUri(getThis(), true, null, null, null, null);
				adapter.close();
				
				if (uri == null)
					return;
				
				String host = uri.getHost();
				if (host != null && !host.isEmpty())
					txtHost.setText(host);
				
				int port = uri.getPort();
				if (port > 0)
					txtPort.setValue(port);
				
				String username = uri.getUserName();
				if (username != null && !username.isEmpty())
					txtUsername.setText(username);
				
				String password = uri.getPassword();
				if (password != null && !password.isEmpty())
					txtPassword.setText(password);

				String path = uri.getPath();
				if (path != null && !path.isEmpty())
					txtPath.setText(path);
			}
		});
		//
		txtUsername = new JTextField(DataConfig.ADMIN_ACCOUNT);
		attValues.add(txtUsername);
		//
		txtPassword = new JPasswordField(DataConfig.ADMIN_PASSWORD);
		attValues.add(txtPassword);

		pane = new JPanel();
		JButton connect = new JButton("Connect");
		pane.add(connect);
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connect();
			}
		});
		header.add(pane, BorderLayout.SOUTH);

		
		JPanel tools = new JPanel(new GridLayout(1, 0));
		header.add(tools, BorderLayout.NORTH);
		
		JPanel parsers = new JPanel();
		tools.add(parsers);
		
		parsers.add(new JLabel("Parsers"));
		
		cmbParsers = new DatasetParserComboBox(parserList);
		cmbParsers.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				parserChanged();
			}
		});
	
		parsers.add(cmbParsers);
		
		JPanel dataDrivers = new JPanel();
		tools.add(dataDrivers);
		
		dataDrivers.add(new JLabel("Drivers"));
		
		cmbDataDrivers = new DataDriverComboBox(dataDriverList, (DatasetParser)cmbParsers.getSelectedItem());
		cmbDataDrivers.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED)
					dataDriverChanged();
			}
		});
		dataDrivers.add(cmbDataDrivers);
		
		dataDriverChanged();
		updateDefaultConfig(defaultConfig);
		
		setVisible(true);
	}
	
	
	/**
	 * Getting this configuration dialog.
	 * @return this {@link DatasetConfigurator}
	 */
	private DatasetConfigurator getThis() {
		return this;
	}
	
	
	/**
	 * Updating default configuration.
	 * @param defaultCfg specified default configuration.
	 */
	private void updateDefaultConfig(DataConfig defaultCfg) {
		if (defaultCfg == null)
			return;
		xURI store = defaultCfg.getStoreUri();
		
		DataDriver dataDriver = null;
		try {
			String dataDriverName = defaultCfg.getDataDriverName();
			if (dataDriverName != null)
				dataDriver = DataDriver.createByName(dataDriverName);
			else if (store != null)
				dataDriver = DataDriver.create(store);
		}
		catch (Exception e) {
			LogUtil.trace(e);
			dataDriver = null;
		}
		
		cmbParsers.setDefaultSelected(defaultCfg.getParser());
		if (dataDriver != null)
			cmbDataDrivers.selectDataDriver(dataDriver.getType());
		dataDriver = (DataDriver)cmbDataDrivers.getSelectedItem();
		
		if (store == null || dataDriver == null)
			return;
		
		String host = store.getHost();
		if (host == null && dataDriver.getType() != DataType.file)
			host = "localhost";
		txtHost.setText(host);
		
		int port = store.getPort();
		if (port < 0 && dataDriver != null)
			port = dataDriver.getDefaultPort();
		txtPort.setValue(port);
		
		String path = store.getPath();
		if (path == null) {
			path = store.getLastName();
		}
		else if (dataDriver != null && dataDriver.isFlatServer()) {
			String ratingUnit = defaultCfg.getRatingUnit();
			if (ratingUnit != null && !ratingUnit.isEmpty()) {
				if (path.endsWith("/"))
					path += ratingUnit;
				else
					path += "/" + ratingUnit;
			}
		}
		if (path == null)
			path = "";
		txtPath.setText(normalizePath(path));
		
		String username = defaultCfg.getStoreAccount();
		if (username == null)
			username = "";
		txtUsername.setText(username);
		
		HiddenText password = defaultCfg.getStorePassword();
		if (password == null)
			password = new HiddenText("");
		txtPassword.setText(password.getText());
		
	}
	
	
	/**
	 * Parser changed.
	 */
	private void parserChanged() {
		cmbDataDrivers.update(
			thisDataDriverList, 
			(DatasetParser)cmbParsers.getSelectedItem());
	}
	
	
	/**
	 * Data driver changed.
	 */
	private void dataDriverChanged() {
		DataDriver driver = (DataDriver)cmbDataDrivers.getSelectedItem();
		if (driver == null)
			return;
		
		setConnectInfoControlVisible(true);
		txtPort.setValue(driver.getDefaultPort());
		txtHost.setText("localhost");
		txtUsername.setText(DataConfig.ADMIN_ACCOUNT);
		txtPassword.setText(DataConfig.ADMIN_PASSWORD);
		
		if (driver.getType() == DataType.file) {
			txtHost.setText("");
			txtHost.setVisible(false);
			
			txtPort.setValue(-1);
			txtPort.setVisible(false);
			
			txtUsername.setText("");
			txtUsername.setVisible(false);
			
			txtPassword.setText("");
			txtPassword.setVisible(false);
		}
		else if (!driver.isFlatServer()) {
			btnPath.setVisible(false);
		}
		
	}
	
	
	/**
	 * Setting visibility of information control according specified flag.
	 * @param aFlag specified flag.
	 */
	private void setConnectInfoControlVisible(boolean aFlag) {
		txtHost.setVisible(aFlag);
		txtPort.setVisible(aFlag);
		txtPath.setVisible(aFlag);
		btnPath.setVisible(aFlag);
		txtUsername.setVisible(aFlag);
		txtPassword.setVisible(aFlag);
	}

	
	/**
	 * Getting the resulted configuration from this dialog.
	 * @return {@link DataConfig} as the resulted configuration from this dialog.
	 */
	public DataConfig getResultedConfig() {
		return resultedConfig;
	}
	
	
	/**
	 * Setting unit according to specified unit type.
	 * @param unitType specified unit type.
	 */
	private void setUnit(String unitType) {
		if (lbUnits.getSelectedValue() == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Not choose unit yet", 
					"Not choose unit yet", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		String unit = lbUnits.getSelectedValue().getName();
		internalConfig.setUnit(unitType, unit);
		internalConfig.setParser(getParser());
		internalConfig.setDataDriverName(getDataDriver().getName());
		
		tblConfig.update(internalConfig);
		
	}
	
	
	/**
	 * Getting data driver.
	 * @return {@link DataDriver}.
	 */
	private DataDriver getDataDriver() {
		return (DataDriver)cmbDataDrivers.getSelectedItem();
	}
	
	
	/**
	 * Getting dataset parser.
	 * @return {@link DatasetParser}.
	 */
	private DatasetParser getParser() {
		return (DatasetParser) cmbParsers.getSelectedAlg();
	}
	
	
	/**
	 * Event-driven method for clicking OK button.
	 */
	private void onOk() {
		if (internalConfig == null || internalConfig.size() == 0 || internalConfig.getStoreUri() == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Not connect yet", 
					"Not connect yet", 
					JOptionPane.WARNING_MESSAGE);
			
			return;
		}
		
		resultedConfig = (DataConfig) internalConfig.clone();
		internalConfig = null;
		dispose();
	}

	
	/**
	 * Event-driven method for clicking connection button.
	 */
	private void connect() {
		internalConfig.clear();
		tblConfig.update(internalConfig);
		
		txtPath.setText(normalizePath(txtPath.getText()));
		DataDriver dataDriver = getDataDriver();
		String host = txtHost.getText().trim();
		host = host.isEmpty() ? null : host;
		int port = txtPort.getValue() instanceof Number ? ( (Number) txtPort.getValue()).intValue() : -1; 
		xURI uri = dataDriver.getUri(host, port, txtPath.getText());
		
		DataConfig config = new DataConfig();
		config.setStoreAccount(txtUsername.getText().trim());
		config.setStorePassword( new HiddenText(txtPassword.getPassword()) );
		config.setStoreUri(uri);
		
		boolean connect = lbUnits.connectUpdate(config);
		lbUnits.validate();
		lbUnits.updateUI();
		if (!connect) {
			internalConfig.clear();
			JOptionPane.showMessageDialog(
					this, "Connect fail", "Connect fail", JOptionPane.ERROR_MESSAGE);
			return;
		}
		else
			JOptionPane.showMessageDialog(
					this, "Connect successfully", "Connect successfully", JOptionPane.INFORMATION_MESSAGE);
		
		String referredUnitName = null;
		if (dataDriver.isFlatServer()) {
			UriAdapter adapter = new UriAdapter(uri);
			if (!adapter.isStore(uri)) {
				internalConfig.putAll(adapter.makeFlatDataConfig(uri, null));
				referredUnitName = uri.getLastName();
			}
			else
				internalConfig.putDefaultUnitList(uri);
			adapter.close();
		}
		else if (dataDriver.isDbServer())
			internalConfig.putDefaultUnitList(uri);
		else if (dataDriver.isHudupServer()) {
			txtPath.setText("");
			uri = dataDriver.getUri(host, port, txtPath.getText());
			internalConfig.putDefaultUnitList(uri);
		}
		
		if (dataDriver.getType() != DataType.file) {
			internalConfig.setStoreAccount(txtUsername.getText().trim());
			internalConfig.setStorePassword( new HiddenText(txtPassword.getPassword()) );
		}
		
		List<String> unitNameList = lbUnits.getUnitList().toNameList();
		
		if (unitNameList.contains(DataConfig.ACCOUNT_UNIT))
			internalConfig.setConfigUnit(DataConfig.ACCOUNT_UNIT);
		else
			internalConfig.remove(DataConfig.ACCOUNT_UNIT);

		if (unitNameList.contains(DataConfig.ATTRIBUTE_MAP_UNIT))
			internalConfig.setConfigUnit(DataConfig.ATTRIBUTE_MAP_UNIT);
		else
			internalConfig.remove(DataConfig.ATTRIBUTE_MAP_UNIT);

		if (unitNameList.contains(DataConfig.CONFIG_UNIT))
			internalConfig.setConfigUnit(DataConfig.CONFIG_UNIT);
		else
			internalConfig.remove(DataConfig.CONFIG_UNIT);
		
		if (unitNameList.contains(DataConfig.CONTEXT_TEMPLATE_UNIT))
			internalConfig.setContextTemplateUnit(DataConfig.CONTEXT_TEMPLATE_UNIT);
		else
			internalConfig.remove(DataConfig.CONTEXT_TEMPLATE_UNIT);

		if (unitNameList.contains(DataConfig.CONTEXT_UNIT))
			internalConfig.setContextUnit(DataConfig.CONTEXT_UNIT);
		else
			internalConfig.remove(DataConfig.CONTEXT_UNIT);

		if (unitNameList.contains(DataConfig.ITEM_UNIT))
			internalConfig.setItemUnit(DataConfig.ITEM_UNIT);
		else
			internalConfig.remove(DataConfig.ITEM_UNIT);
		
		if (unitNameList.contains(DataConfig.NOMINAL_UNIT))
			internalConfig.setNominalUnit(DataConfig.NOMINAL_UNIT);
		else
			internalConfig.remove(DataConfig.NOMINAL_UNIT);

		if (unitNameList.contains(DataConfig.RATING_UNIT))
			internalConfig.setUserUnit(DataConfig.RATING_UNIT);
		else
			internalConfig.remove(DataConfig.RATING_UNIT);

		if (unitNameList.contains(DataConfig.USER_UNIT))
			internalConfig.setUserUnit(DataConfig.USER_UNIT);
		else
			internalConfig.remove(DataConfig.USER_UNIT);

		if (unitNameList.contains(DataConfig.SAMPLE_UNIT))
			internalConfig.setSampleUnit(DataConfig.SAMPLE_UNIT);
		else
			internalConfig.remove(DataConfig.SAMPLE_UNIT);

		if (mainUnit != null && referredUnitName != null) {
			internalConfig.setMainUnit(mainUnit);
			internalConfig.put(mainUnit, referredUnitName);
		}
		
		internalConfig.setParser(getParser());
		internalConfig.setDataDriverName(getDataDriver().getName());

		tblConfig.update(internalConfig);
	}
	
	
	/**
	 * Normalizing the specified path.
	 * @param path specified path.
	 * @return normalized path, remove all back slash.
	 */
	private String normalizePath(String path) {
		return path.replaceAll("\\\\", "/");
	}
	
	
}
