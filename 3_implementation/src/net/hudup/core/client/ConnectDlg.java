/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.BindNamingURI;
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
	 * Text field to fill naming name.
	 */
	protected JTextField txtNamingName = null;
	
	
	/**
	 * Text field to fill user name.
	 */
	protected JTextField txtUsername = null;
	
	
	/**
	 * Password field to fill password.
	 */
	protected JPasswordField txtPassword = null;
	
	
	/**
	 * Check box as flag to indicate whether exporting again.
	 */
	protected JCheckBox chkExport = null;
	
	
	/**
	 * Text field to fill naming name.
	 */
	protected JTextField txtBindName = null;

	
	/**
	 * Text field to generate bind name.
	 */
	protected JButton btnGenBindName = null; 
			
	
	/**
	 * Text field to fill naming port.
	 */
	protected JFormattedTextField txtBindPort = null;

	
	/**
	 * Remote connector.
	 */
	protected Remote connector = null;
	
	
	/**
	 * Remote host.
	 */
	protected String host = null;
	
	
	/**
	 * Remote port.
	 */
	protected int port = 0;

	
	/**
	 * Naming name.
	 */
	protected String namingName = null;
	
	
	/**
	 * Remote user name.
	 */
	protected String username = null;
	
	
	/**
	 * Remote password.
	 */
	protected String password = null;
	
	
	/**
	 * Bound name.
	 */
	protected String bindName = null;
	
	
	/**
	 * Bound port.
	 */
	protected int bindPort = 0;
	
	
	/**
	 * Default constructor.
	 */
	protected ConnectDlg() {
		super((JFrame)null, "Remote connection", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
        Image image = UIUtil.getImage("connect-32x32.png");
        if (image != null)
        	setIconImage(image);

        JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Connection type:"));
		left.add(new JLabel("Host:"));
		left.add(new JLabel("Port:"));
		left.add(new JLabel("Naming:"));
		left.add(new JLabel("User name:"));
		left.add(new JLabel("Password:"));
		left.add(new JLabel("Export:"));
		left.add(new JLabel("Bound port:"));
		left.add(new JLabel("Bound name:"));
		
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
						txtUsername.setEditable(false);
						txtPassword.setEditable(false);
					}
					else {
						txtUsername.setEditable(true);
						txtPassword.setEditable(true);
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
		
		txtNamingName = new JTextField("connect1");
		right.add(txtNamingName);

		txtUsername = new JTextField("admin");
		right.add(txtUsername);

		txtPassword = new JPasswordField("admin");
		right.add(txtPassword);
		String pwd = Util.getHudupProperty("admin");
		if (pwd == null) txtPassword.setText(pwd);

		chkExport = new JCheckBox("", false);
		right.add(chkExport);
		chkExport.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				txtBindName.setVisible(chkExport.isSelected());
				btnGenBindName.setVisible(chkExport.isSelected());
			}
		});
		chkExport.setEnabled(false);

		txtBindPort = new JFormattedTextField(new NumberFormatter());
		txtBindPort.setValue(Constants.DEFAULT_CONTROL_PANEL_PORT);
		right.add(txtBindPort);

		JPanel paneBindName = new JPanel(new BorderLayout());
		right.add(paneBindName);
		
		txtBindName = new JTextField("connect1");
		paneBindName.add(txtBindName, BorderLayout.CENTER);
		txtBindName.setVisible(chkExport.isSelected());
		btnGenBindName = UIUtil.makeIconButton(
			"generate-16x16.png",
			"generate", 
			"Generate - http://www.iconarchive.com/show/flatastic-9-icons-by-custom-icon-design/Generate-keys-icon.html", 
			"Generate", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					txtBindName.setText("connect" + new Date().getTime());
				}
			});
		paneBindName.add(btnGenBindName, BorderLayout.EAST);
		btnGenBindName.setVisible(chkExport.isSelected());

		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
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
		add(footer, BorderLayout.SOUTH);
		
		JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				connect0();
			}
		});
		footer.add(connect);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(close);
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
	 * Connect to remote server / service.
	 */
	protected abstract void connect0();
	
	
	/**
	 * Getting user name.
	 * @return user name.
	 */
	public String getUsername() {
		return username;
	}
	
	
	/**
	 * Getting password.
	 * @return password.
	 */
	public String getPassword() {
		return password;
	}

	
	/**
	 * Disconnecting specified connector.
	 * @param connector the remote connector returned by connecting method.
	 */
	public static void disconnect(Remote connector) {
		if ((connector != null) && (connector instanceof SocketConnection))
			((SocketConnection)connector).close();
	}
	
	
	/**
	 * Getting naming name.
	 * @return naming name.
	 */
	public String getNamingName() {
		return namingName;
	}
	
	
	/**
	 * Creating the bound and naming URI.
	 * @return bound and naming URI.
	 */
	public BindNamingURI getBindNamingUri() {
		xURI bindUri = xURI.create("rmi://localhost:" + bindPort);
		
		if (chkExport.isSelected()) {
			xURI namingUri = bindUri;
			if (bindName != null) namingUri = namingUri.concat(bindName);
			return new BindNamingURI(bindUri, namingUri);
		}
		else
			return BindNamingURI.createBindUri(bindUri);
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
				host = txtHost.getText().trim();
				port = txtPort.getValue() instanceof Number ? ( (Number) txtPort.getValue()).intValue() : 0; 
				namingName = normalizeNamingName(txtNamingName.getText());
				
				username = txtUsername.getText();
				password = txtPassword.getText();
				
				bindName = normalizeNamingName(txtBindName.getText());
				bindPort = txtBindPort.getValue() instanceof Number ? ( (Number) txtBindPort.getValue()).intValue() : 0;
				if (bindPort <= 0)
					bindPort = 0;
				else
					bindPort = NetUtil.getPort(bindPort, chkExport.isSelected() ? Constants.TRY_RANDOM_PORT : true);

				ConnectTypeDesc connectType = (ConnectTypeDesc)cmbConnectType.getSelectedItem();
				if (connectType.equals(ConnectType.server))
					connector = ClientUtil.getRemoteServer(host, port, username, password);
				else if (connectType.equals(ConnectType.service))
					connector = ClientUtil.getRemoteService(host, port, username, password);
				else if (connectType.equals(ConnectType.socket_service))
					connector = ClientUtil.getSocketConnection(host, port, username, password);
				else  if (connectType.equals(ConnectType.evaluator))
					connector = ClientUtil.getRemoteEvaluator(host, port, namingName);
				
				if (connector == null) {
					JOptionPane.showMessageDialog(
						this, "Can't connect to server", "Can't connect to server", JOptionPane.ERROR_MESSAGE);
				}
				else
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
	 * Normalizing naming name.
	 * @param namingName naming name.
	 * @return normalized naming name.
	 */
	public static String normalizeNamingName(String namingName) {
		namingName = namingName != null ? namingName.trim() : "";
		namingName = namingName.replace('\\', '/');
		if (namingName.startsWith("/")) namingName = namingName.substring(1);
		
		return namingName;
	}
	
	
}
