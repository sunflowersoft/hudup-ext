/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.Account;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface (GUI) component shows a dialog for remote connecting.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ConnectDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Server connection.
	 */
	public final static String SERVER_CONNECT = "Server";
	
	
	/**
	 * RMI service connection.
	 */
	public final static String SERVICE_CONNECT = "Service";

	
	/**
	 * Socket service connection.
	 */
	public final static String SOCKET_SERVICE_CONNECT = "Socket service";

	
	/**
	 * Evaluator connection.
	 */
	public final static String EVALUATOR_CONNECT = "Evaluator";

	
	/**
	 * This type represents connection type
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public enum ConnectType {
		server,
		service,
		socket_service,
		evaluator
	}
	
	
	/**
	 * This class represent description of a connection type.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static class ConnectTypeDesc {
		
		/**
		 * Connection type.
		 */
		private ConnectType type = ConnectType.server;
		
		/**
		 * Description of connection type.
		 */
		private String desc = SERVER_CONNECT;
		
		/**
		 * Default port.
		 */
		private int defaultPort = Constants.DEFAULT_SERVER_PORT;
		
		/**
		 * Constructor with connection type.
		 * @param type connection type.
		 */
		public ConnectTypeDesc(ConnectType type) {
			this.type = type;
			
			if (type == ConnectType.server) {
				this.desc = SERVER_CONNECT;
				this.defaultPort = Constants.DEFAULT_SERVER_PORT;
			}
			else if (type == ConnectType.service) {
				this.desc = SERVICE_CONNECT;
				this.defaultPort = Constants.DEFAULT_SERVER_PORT;
			}
			else if (type == ConnectType.socket_service) {
				this.desc = SOCKET_SERVICE_CONNECT;
				this.defaultPort = Constants.DEFAULT_LISTENER_PORT;
			}
			else if (type == ConnectType.evaluator) {
				this.desc = EVALUATOR_CONNECT;
				this.defaultPort = Constants.DEFAULT_EVALUATOR_PORT;
			}
		}
	
		/**
		 * Getting connection type.
		 * @return connection type.
		 */
		public ConnectType getType() {
			return type;
		}
		
		/**
		 * Getting description of connection type.
		 * @return description of connection type.
		 */
		public String getDesc() {
			return desc;
		}
		
		/**
		 * Getting default port.
		 * @return default port.
		 */
		public int getDefaultPort() {
			return defaultPort;
		}
		
		/**
		 * Defining default connection types.
		 * @return default connection types.
		 */
		public static ConnectTypeDesc[] defaultTypes() {
			return new ConnectTypeDesc[] {
				new ConnectTypeDesc(ConnectType.server),
				new ConnectTypeDesc(ConnectType.service),
				new ConnectTypeDesc(ConnectType.socket_service),
				new ConnectTypeDesc(ConnectType.evaluator)
			};
		}

		@Override
		public String toString() {
			return desc;
		}

		@Override
		public boolean equals(Object obj) {
			if (obj == null)
				return false;
			else if (obj instanceof ConnectType)
				return this.type == (ConnectType)obj;
			else if (obj instanceof ConnectTypeDesc)
				return this.type == ((ConnectTypeDesc)obj).type;
			else
				return false;
		}
		
	}
	
	
	/**
	 * Connection type.
	 */
	protected JComboBox<ConnectTypeDesc> cmbConnectType = null;
	
	
	/**
	 * Text field to fill host.
	 */
	protected JTextField txtHost = null;
	
	
	/**
	 * Text field to fill port.
	 */
	protected JFormattedTextField txtPort = null;
	
	
	/**
	 * Text field to fill naming path.
	 */
	protected JTextField txtConnectPath = null;
	
	
	/**
	 * Text field to fill user name.
	 */
	protected JTextField txtUsername = null;
	
	
	/**
	 * Password field to fill password.
	 */
	protected JPasswordField txtPassword = null;
	
	
	/**
	 * Check box as flag to indicate whether pull mode is set. In pull mode, client is active to retrieve (pull) events from server.
	 */
	protected JCheckBox chkPullMode = null;

	
	/**
	 * Text field to fill server access period.
	 */
	protected JFormattedTextField txtMyAccessPeriod = null;

	
	/**
	 * Check box as flag to indicate whether exporting again.
	 */
	protected JCheckBox chkHostingAgain = null;
	
	
	/**
	 * Text field to fill naming path.
	 */
	protected JTextField txtMyNamingPath = null;

	
	/**
	 * Text field to generate bind path.
	 */
	protected JButton btnGenBindPath = null; 
			
	
	/**
	 * Text field to fill naming port.
	 */
	protected JFormattedTextField txtMyBindPort = null;

	
	/**
	 * Button to check port.
	 */
	protected JButton btnCheckMyBindPort = null;
	
	
	/**
	 * Text field to fill global deployed host.
	 */
	protected JTextField txtMyGlobalAddress = null;

	
	/**
	 * Text field to generate bind path.
	 */
	protected JButton btnGenMyGlobalAddress = null; 
			
	
	/**
	 * Remote connector.
	 */
	protected Remote connector = null;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = null;
	
	
	/**
	 * Default constructor.
	 */
	protected ConnectDlg() {
		super((JFrame)null, "Remote connection", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 450);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
        Image image = UIUtil.getImage("connect-32x32.png");
        if (image != null)
        	setIconImage(image);

		JMenuBar mnBar = new JMenuBar();
		setJMenuBar(mnBar);
		
		JMenu mnHelp = new JMenu(I18nUtil.message("Help"));
		mnBar.add(mnHelp);
		
		JMenuItem mniGuideline = new JMenuItem(
			new AbstractAction(I18nUtil.message("guidance")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					JDialog dlgGuide = new JDialog(getThisConnectDlg(), I18nUtil.message("guidance"), true);
					dlgGuide.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					dlgGuide.setSize(300, 200);
					dlgGuide.setLocationRelativeTo(getThisConnectDlg());
					dlgGuide.setLayout(new BorderLayout());
					
					JPanel body = new JPanel(new BorderLayout());
					dlgGuide.add(body, BorderLayout.CENTER);
					
					TextArea txtInfo = new TextArea();
					body.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
					txtInfo.setEditable(false);
					txtInfo.setText(
						  "Server serves at default port (RMI) " + Constants.DEFAULT_SERVER_PORT + ".\n\n"
						+ "Listener serves at default port (Socket) " + Constants.DEFAULT_LISTENER_PORT + ".\n"
						+ "Listener exports at default port (RMI) " + Constants.DEFAULT_LISTENER_EXPORT_PORT + " for control connection.\n\n"
						+ "Balancer serves at default port (Socket) " + Constants.DEFAULT_BALANCER_PORT + ".\n"
						+ "Balancer exports at default port (RMI) " + Constants.DEFAULT_BALANCER_EXPORT_PORT + " for control connection.\n\n"
						+ "Default socket control port is (Socket) " + Constants.DEFAULT_SOCKET_CONTROL_PORT + ".\n\n"
						+ "Evaluator serves at default port (RMI) " + Constants.DEFAULT_EVALUATOR_PORT + ".\n\n"
						+ "Default control panel port is (RMI) " + Constants.DEFAULT_CONTROL_PANEL_PORT + ".\n\n");
					
					JPanel footer = new JPanel();
					dlgGuide.add(footer, BorderLayout.SOUTH);
					
					JButton ok = new JButton("OK");
					ok.addActionListener(new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							dlgGuide.dispose();
						}
					});
					footer.add(ok);
					
					dlgGuide.setVisible(true);
				}
			});
		mnHelp.add(mniGuideline);
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Connection type:"));
		left.add(new JLabel("Host:"));
		left.add(new JLabel("Port:"));
		left.add(new JLabel("Connect path:"));
		left.add(new JLabel("User name:"));
		left.add(new JLabel("Password:"));
		left.add(new JLabel("Pull mode:"));
		left.add(new JLabel("    My access period (s):"));
		left.add(new JLabel("My bound port:"));
		left.add(new JLabel("Hosting again:"));
		left.add(new JLabel("    My hosting naming path:"));
		left.add(new JLabel("My global address:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		ConnectTypeDesc[] connectTypes = ConnectTypeDesc.defaultTypes();
		cmbConnectType = new JComboBox<ConnectTypeDesc>(connectTypes);
		cmbConnectType.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ConnectTypeDesc connectType = (ConnectTypeDesc)cmbConnectType.getSelectedItem();
					txtPort.setValue(connectType.getDefaultPort());
					
					if (connectType.type == ConnectType.evaluator) {
						txtUsername.setVisible(false);
						txtPassword.setVisible(false);
						txtConnectPath.setVisible(true);
					}
					else {
						txtUsername.setVisible(true);
						txtPassword.setVisible(true);
						txtConnectPath.setVisible(false);
					}
				}
			}
		});
		right.add(cmbConnectType);
		
		txtHost = new JTextField("localhost");
		right.add(txtHost);
		
		txtPort = new JFormattedTextField(new NumberFormatter());
		if (connectTypes.length > 0) {
			cmbConnectType.setSelectedItem(connectTypes[0]);
			txtPort.setValue(connectTypes[0].getDefaultPort());
		}
		right.add(txtPort);
		
		txtConnectPath = new JTextField("connect1");
		right.add(txtConnectPath);
		txtConnectPath.setVisible(false);

		txtUsername = new JTextField("admin");
		right.add(txtUsername);

		txtPassword = new JPasswordField("admin");
		right.add(txtPassword);
		String pwd = Util.getHudupProperty("admin");
		if (pwd == null) txtPassword.setText(pwd);

		chkPullMode = new JCheckBox("", false);
		right.add(chkPullMode);

		txtMyAccessPeriod = new JFormattedTextField(new NumberFormatter());
		right.add(txtMyAccessPeriod);
		txtMyAccessPeriod.setValue((int)(5*Counter.PERIOD/1000));
		txtMyAccessPeriod.setVisible(chkPullMode.isSelected());

		JPanel paneBindPort = new JPanel(new BorderLayout());
		right.add(paneBindPort);
		
		txtMyBindPort = new JFormattedTextField(new NumberFormatter());
		paneBindPort.add(txtMyBindPort, BorderLayout.CENTER);
		txtMyBindPort.setValue(0);
		txtMyBindPort.setToolTipText("It is possible to set my bound port 0 if not hosting again");

		btnCheckMyBindPort = UIUtil.makeIconButton(
			"checking-16x16.png",
			"checking", 
			"Checking whether bound port to client is available - http://www.iconarchive.com/show/outline-icons-by-iconsmind/Check-2-icon.html", 
			"Checking", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int port = txtMyBindPort.getValue() instanceof Number ? ( (Number) txtMyBindPort.getValue()).intValue() : 0;
					boolean ret = NetUtil.testPort(port);
					if (ret) {
						JOptionPane.showMessageDialog(getThisConnectDlg(),
								"The port " + port + " is valid",
								"Valid port", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					int suggestedPort = NetUtil.getPort(-1, true);
					JOptionPane.showMessageDialog(getThisConnectDlg(),
							"The port " + port + " is invalid (used).\n The suggested port is " + suggestedPort,
							"Invalid port", JOptionPane.ERROR_MESSAGE);
				}
			});
		paneBindPort.add(btnCheckMyBindPort, BorderLayout.EAST);

		chkHostingAgain = new JCheckBox("", false);
		right.add(chkHostingAgain);

		JPanel paneMyNamingPath = new JPanel(new BorderLayout());
		right.add(paneMyNamingPath);
		paneMyNamingPath.setVisible(chkHostingAgain.isSelected());
		
		txtMyNamingPath = new JTextField("connect1");
		paneMyNamingPath.add(txtMyNamingPath, BorderLayout.CENTER);
		btnGenBindPath = UIUtil.makeIconButton(
			"generate-16x16.png",
			"generate", 
			"Generate naming path - http://www.iconarchive.com/show/flatastic-9-icons-by-custom-icon-design/Generate-keys-icon.html", 
			"Generate", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					txtMyNamingPath.setText("connect" + new Date().getTime());
				}
			});
		paneMyNamingPath.add(btnGenBindPath, BorderLayout.EAST);

		JPanel paneMyGlobalAddress = new JPanel(new BorderLayout());
		right.add(paneMyGlobalAddress);

		txtMyGlobalAddress = new JTextField("");
		paneMyGlobalAddress.add(txtMyGlobalAddress, BorderLayout.CENTER);
		txtMyGlobalAddress.setToolTipText("It is possible to leave my global address (WAN address, internet address) empty");
		
		btnGenMyGlobalAddress = UIUtil.makeIconButton(
			"generate-16x16.png",
			"generate", 
			"Retrieve internet address as global address - http://www.iconarchive.com/show/flatastic-9-icons-by-custom-icon-design/Generate-keys-icon.html", 
			"Generate", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String publicIP = NetUtil.getPublicInetAddress();
					txtMyGlobalAddress.setText(publicIP != null ? publicIP : "");
				}
			});
		paneMyGlobalAddress.add(btnGenMyGlobalAddress, BorderLayout.EAST);
		
		chkPullMode.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtMyAccessPeriod.setVisible(chkPullMode.isSelected());
			}
		});

		chkHostingAgain.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				paneMyNamingPath.setVisible(chkHostingAgain.isSelected());
				int port = txtMyBindPort.getValue() instanceof Number ? ( (Number) txtMyBindPort.getValue()).intValue() : 0;
				if (chkHostingAgain.isSelected()) {
					if (port == 0) {
						port = NetUtil.getPort(Constants.DEFAULT_CONTROL_PANEL_PORT, true);
						txtMyBindPort.setValue(port >= 0 ? port : port);
					}
				}
				else if (port != 0)
					txtMyBindPort.setValue(0);
			}
		});

		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		JPanel buttons = new JPanel();
		footer.add(buttons, BorderLayout.NORTH);
		
		JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connect0();
			}
		});
		buttons.add(connect);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		buttons.add(close);
		
		JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footer.add(status, BorderLayout.SOUTH);
		
		status.add(new JLabel("Local address: " + Constants.hostAddress + "  "));
		String publicIP = NetUtil.getPublicInetAddress();
		if (publicIP != null)
			status.add(new JLabel("Internet address: " + publicIP));
	}
	
	
	/**
	 * Testing whether connecting to server.
	 * @return true if connecting to server.
	 */
	public boolean isServer() {
		if (connector == null)
			return false;
		else
			return (connector instanceof Server);
	}

	/**
	 * Getting server.
	 * @return server.
	 */
	public Server getServer() {
		if (isServer())
			return (Server)connector;
		else
			return null;
	}

	
	/**
	 * Testing whether connecting to service.
	 * @return true if connecting to service.
	 */
	public boolean isService() {
		if (connector == null)
			return false;
		else
			return (connector instanceof Service);
	}
	
	
	/**
	 * Getting service.
	 * @return service.
	 */
	public Service getService() {
		if (connector == null)
			return null;
		else if (isService())
			return (Service)connector;
		else if (isServer()) {
			Server server = getServer();
			if (server instanceof PowerServer) {
				try {
					return ((PowerServer)server).getService();
				}
				catch (RemoteException e) {
					LogUtil.trace(e);
				}
				
				return null;
			}
			else
				return null;
		}
		else
			return null;
	}

	
	/**
	 * Testing whether connecting to evaluator.
	 * @return true if connecting to evaluator.
	 */
	public boolean isEvaluator() {
		if (connector == null)
			return false;
		else
			return (connector instanceof Evaluator);
	}

	
	/**
	 * Getting evaluator.
	 * @return evaluator.
	 */
	public Evaluator getEvaluator() {
		if (isEvaluator())
			return (Evaluator)connector;
		else
			return null;
	}

	
	/**
	 * Getting this connection dialog.
	 * @return this connection dialog.
	 */
	private ConnectDlg getThisConnectDlg() {
		return this;
	}
	
	
	/**
	 * Connect to remote server / service.
	 */
	protected abstract void connect0();
	
	
	/**
	 * Disconnecting specified connector.
	 * @param connector the remote connector returned by connecting method.
	 */
	public static void disconnect(Remote connector) {
		if ((connector != null) && (connector instanceof SocketConnection))
			((SocketConnection)connector).close();
	}
	
	
	/**
	 * Getting connection information.
	 * @return connection information.
	 */
	public ConnectInfo getConnectInfo() {
		return connectInfo;
	}
	
	
	/**
	 * Connecting to server or service with hint connect type and port.
	 * @param hintConnectType hint connection type.
	 * @param hintPort hint port.
	 * @return connection dialog.
	 */
	public static ConnectDlg connect(ConnectType hintConnectType, int hintPort) {
		ConnectDlg connectDlg = new ConnectDlg() {
			
			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			protected void connect0() {
				String host = txtHost.getText().trim();
				int port = txtPort.getValue() instanceof Number ? ( (Number) txtPort.getValue()).intValue() : 0; 
				String connectPath = normalizeNamingPath(txtConnectPath.getText());
				String username = txtUsername.getText();
				String password = txtPassword.getText();
				
				String myNamingPath = normalizeNamingPath(txtMyNamingPath.getText());
				int bindPort = txtMyBindPort.getValue() instanceof Number ? ( (Number) txtMyBindPort.getValue()).intValue() : 0;
				if (bindPort <= 0)
					bindPort = 0;
				else
					bindPort = NetUtil.getPort(bindPort, chkHostingAgain.isSelected() ? Constants.TRY_RANDOM_PORT : true);

				ConnectTypeDesc connectType = (ConnectTypeDesc)cmbConnectType.getSelectedItem();
				xURI connectUri = null;
				if (connectType.equals(ConnectType.server)) {
					connector = ClientUtil.getRemoteServer(host, port, username, password);
					connectUri = xURI.create("rmi://" + host + ":" + port);
				}
				else if (connectType.equals(ConnectType.service)) {
					connector = ClientUtil.getRemoteService(host, port, username, password);
					connectUri = xURI.create("rmi://" + host + ":" + port);
				}
				else if (connectType.equals(ConnectType.socket_service)) {
					connector = ClientUtil.getSocketConnection(host, port, username, password);
					connectUri = xURI.create("hdp://" + host + ":" + port);
				}
				else  if (connectType.equals(ConnectType.evaluator)) {
					connector = ClientUtil.getRemoteEvaluator(host, port, connectPath);
					connectUri = xURI.create("rmi://" + host + ":" + port + "/" + connectPath);
				}
				if (connector == null) {
					JOptionPane.showMessageDialog(
						this, "Can't connect to server", "Can't connect to server", JOptionPane.ERROR_MESSAGE);
					return;
				}

				connectInfo = new ConnectInfo();
				connectInfo.account = Account.create(username, password);
				connectInfo.connectUri = connectUri;
				
				connectInfo.pullMode = chkPullMode.isSelected();
				int myAccessPeriod = txtMyAccessPeriod.getValue() instanceof Number ? ( (Number) txtMyAccessPeriod.getValue()).intValue() : 1;
				myAccessPeriod = 1000 * myAccessPeriod;
				connectInfo.accessPeriod = myAccessPeriod < Counter.PERIOD ? Counter.PERIOD : myAccessPeriod;   

				connectInfo.bindUri = xURI.create("rmi://localhost:" + bindPort);
				if (chkHostingAgain.isSelected()) {
					connectInfo.namingUri = connectInfo.bindUri;
					if (myNamingPath != null && !myNamingPath.isEmpty())
						connectInfo.namingUri = connectInfo.namingUri.concat(myNamingPath);
				}
				
				connectInfo.globalAddress = txtMyGlobalAddress.getText();
				if (connectInfo.globalAddress != null) {
					connectInfo.globalAddress = connectInfo.globalAddress.trim();
					if (connectInfo.globalAddress.isEmpty())
						connectInfo.globalAddress = null;
				}
				
				dispose();
			}
		};
		
		if (hintConnectType != null)
			connectDlg.cmbConnectType.setSelectedItem(new ConnectTypeDesc(hintConnectType));
		
		if (hintPort > 0)
			connectDlg.txtPort.setValue(hintPort);
		
		connectDlg.setVisible(true);
		
		return connectDlg;
	}
	
	

	/**
	 * Connecting to server or service with hint connect type.
	 * @param hintConnectType hint connection type.
	 * @return connection dialog.
	 */
	public static ConnectDlg connect(ConnectType hintConnectType) {
		return connect(hintConnectType, -1);
	}
	
	
	/**
	 * Connecting to server or service with hint port.
	 * @param hintPort hint port.
	 * @return connection dialog.
	 */
	public static ConnectDlg connect(int hintPort) {
		return connect(null, hintPort);
	}
	
	
	/**
	 * Connecting to server or service.
	 * @return connection dialog.
	 */
	public static ConnectDlg connect() {
		return connect(null, -1);
	}


	/**
	 * Normalizing naming path.
	 * @param namingPath naming path.
	 * @return normalized naming path.
	 */
	public static String normalizeNamingPath(String namingPath) {
		namingPath = namingPath != null ? namingPath.trim() : "";
		namingPath = namingPath.replace('\\', '/');
		if (namingPath.startsWith("/")) namingPath = namingPath.substring(1);
		
		return namingPath;
	}
	
	
}
