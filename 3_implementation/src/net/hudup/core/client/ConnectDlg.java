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
	protected final static String SERVER_CONNECT = "Server";
	
	
	/**
	 * RMI service connection.
	 */
	protected final static String RMI_SERVICE_CONNECT = "RMI service";

	
	/**
	 * Socket service connection.
	 */
	protected final static String SOCKET_SERVICE_CONNECT = "Socket service";

	
	/**
	 * Connection type.
	 */
	protected JComboBox<String> cmbConnectType = null;
	
	
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
		
		cmbConnectType = new JComboBox<String>(new String[] {
				SERVER_CONNECT, RMI_SERVICE_CONNECT, SOCKET_SERVICE_CONNECT
			});
		cmbConnectType.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.SELECTED)
					return;
				
				String connectType = cmbConnectType.getSelectedItem().toString();
				if (connectType.equals(SERVER_CONNECT))
					txtRemotePort.setValue(Constants.DEFAULT_SERVER_PORT);
				else if (connectType.equals(RMI_SERVICE_CONNECT))
					txtRemotePort.setValue(Constants.DEFAULT_SERVER_PORT);
				else if (connectType.equals(SOCKET_SERVICE_CONNECT))
					txtRemotePort.setValue(Constants.DEFAULT_LISTENER_PORT);
			}
		});
		right.add(cmbConnectType);
		
		txtRemoteHost = new JTextField("localhost");
		right.add(txtRemoteHost);
		
		txtRemotePort = new JFormattedTextField(new NumberFormatter());
		txtRemotePort.setValue(Constants.DEFAULT_SERVER_PORT);
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
		
		setVisible(true);
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
	 * Connecting to server or service.
	 * @return connection dialog.
	 */
	public static ConnectDlg connect() {
		return new ConnectDlg() {
			
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
					connector = DriverManager.getRemoteServer(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
				else if (connectType.equals(RMI_SERVICE_CONNECT))
					connector = DriverManager.getRemoteService(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
				else if (connectType.equals(SOCKET_SERVICE_CONNECT))
					connector = DriverManager.getSocketConnection(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
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
		
	}
	
	

	
}
