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

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.NumberFormatter;

import net.hudup.core.Constants;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
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
	 * This type represents connection type
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public enum ConnectType {
		server,
		service,
		socket_service
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
				new ConnectTypeDesc(ConnectType.socket_service)
			};
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return desc;
		}

		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
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
	 * {@link JTextField} to fill remote host.
	 */
	protected JTextField txtRemoteHost = null;
	
	
	/**
	 * {@link JFormattedTextField} to fill remote port.
	 */
	protected JFormattedTextField txtRemotePort = null;
	
	
	/**
	 * {@link JTextField} to fill remote user name.
	 */
	protected JTextField txtRemoteUsername = null;
	
	
	/**
	 * {@link JPasswordField} to fill remote password.
	 */
	protected JPasswordField txtRemotePassword = null;
	
	
	/**
	 * Remote connector.
	 */
	protected Remote connector = null;
	
	
	/**
	 * Default constructor.
	 */
	protected ConnectDlg() {
		super((JFrame)null, "Remote connection", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(400, 200);
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
		left.add(new JLabel("Remote host:"));
		left.add(new JLabel("Remote port:"));
		left.add(new JLabel("Remote username:"));
		left.add(new JLabel("Remote password:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		ConnectTypeDesc[] connectTypes = ConnectTypeDesc.defaultTypes();
		cmbConnectType = new JComboBox<ConnectTypeDesc>(connectTypes);
		cmbConnectType.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED) {
					ConnectTypeDesc connectType = (ConnectTypeDesc)cmbConnectType.getSelectedItem();
					txtRemotePort.setValue(connectType.getDefaultPort());
				}
			}
		});
		right.add(cmbConnectType);
		
		txtRemoteHost = new JTextField("localhost");
		right.add(txtRemoteHost);
		
		txtRemotePort = new JFormattedTextField(new NumberFormatter());
		if (connectTypes.length > 0) {
			cmbConnectType.setSelectedItem(connectTypes[0]);
			txtRemotePort.setValue(connectTypes[0].getDefaultPort());
		}
		right.add(txtRemotePort);
		
		txtRemoteUsername = new JTextField("admin");
		right.add(txtRemoteUsername);

		txtRemotePassword = new JPasswordField("admin");
		right.add(txtRemotePassword);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton connect = new JButton("Connect");
		connect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				connect0();
			}
		});
		footer.add(connect);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
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
					// TODO Auto-generated catch block
					e.printStackTrace();
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
	 * Connect to remote server / service.
	 */
	protected abstract void connect0();
	
	
	/**
	 * Disconnecting.
	 */
	public void disconnect() {
		if ((connector != null) && (connector instanceof SocketConnection))
			((SocketConnection)connector).close();
		
		connector = null;
	}
	
	
	/**
	 * Creating the binded URI for the control panel. In current implementation, it is &quot;rmi://localhost:&lt;port&gt;/connect&quot;
	 * @return binded URI.
	 */
	public xURI getBindUri() {
		int port = NetUtil.getPort(Constants.DEFAULT_CONTROL_PANEL_PORT, true);
		if (port < 0)
			return null;
		else 
			return xURI.create( "rmi://localhost:" + port + "/connect");
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
				// TODO Auto-generated method stub
				String remoteHost = txtRemoteHost.getText().trim();
				int remotePort = txtRemotePort.getValue() instanceof Number ? ( (Number) txtRemotePort.getValue()).intValue() : -1; 
				
				String connectType = cmbConnectType.getSelectedItem().toString();
				if (connectType.equals(SERVER_CONNECT))
					connector = ClientUtil.getRemoteServer(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
				else if (connectType.equals(SERVICE_CONNECT))
					connector = ClientUtil.getRemoteService(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
				else if (connectType.equals(SOCKET_SERVICE_CONNECT))
					connector = ClientUtil.getSocketConnection(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
				else
					connector = null;
				
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
			connectDlg.txtRemotePort.setValue(hintPort);
		
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


}
