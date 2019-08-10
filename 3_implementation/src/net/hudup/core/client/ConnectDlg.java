package net.hudup.core.client;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.Remote;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

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
	 * {@link JTextField} to fill remote host.
	 */
	protected JTextField txtRemoteHost = null;
	
	
	/**
	 * {@link JTextField} to fill remote port.
	 */
	protected JTextField txtRemotePort = null;
	
	
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
	public ConnectDlg() {
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
		
		left.add(new JLabel("Remote host:"));
		left.add(new JLabel("Remote port:"));
		left.add(new JLabel("Remote username:"));
		left.add(new JLabel("Remote password:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		txtRemoteHost = new JTextField("localhost");
		right.add(txtRemoteHost);
		
		txtRemotePort = new JTextField("" + Constants.DEFAULT_SERVER_PORT);
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
				connect();
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
	 * Getting RMI server.
	 * @return RMI server.
	 */
	public Server getServer() {
		return (Server)connector;
	}

	
	/**
	 * Getting RMI service.
	 * @return RMI service.
	 */
	public Service getRmiService() {
		return (Service)connector;
	}

	
	/**
	 * Getting socket service.
	 * @return socket service.
	 */
	public SocketConnection getSocketService() {
		return (SocketConnection)connector;
	}

	
	/**
	 * Connect to remote server / service.
	 */
	protected abstract void connect();
	
	
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
	 * Connecting server.
	 * @return connection dialog.
	 */
	public static ConnectDlg connectServer() {
		return new ConnectDlg() {
			
			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			protected void connect() {
				// TODO Auto-generated method stub
				String remoteHost = txtRemoteHost.getText().trim();
				String remotePort_s = txtRemotePort.getText().trim();
				int remotePort = -1;
				if (!remotePort_s.isEmpty()) remotePort = Integer.parseInt(remotePort_s);
					
				connector = DriverManager.getRemoteServer(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
				
				if (connector == null) {
					JOptionPane.showMessageDialog(
						this, "Can't connect to server", "Can't connect to server", JOptionPane.ERROR_MESSAGE);
				}
				else {
					dispose();
				}
			}
		};
	}
	
	
	/**
	 * Connecting RMI service.
	 * @return connection dialog.
	 */
	public static ConnectDlg connectRmiService() {
		return new ConnectDlg() {
			
			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			protected void connect() {
				// TODO Auto-generated method stub
				String remoteHost = txtRemoteHost.getText().trim();
				String remotePort_s = txtRemotePort.getText().trim();
				int remotePort = -1;
				if (!remotePort_s.isEmpty()) remotePort = Integer.parseInt(remotePort_s);
					
				connector = DriverManager.getRemoteService(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
				
				if (connector == null) {
					JOptionPane.showMessageDialog(
						this, "Can't connect to RMI service", "Can't connect to RMI service", JOptionPane.ERROR_MESSAGE);
				}
				else {
					dispose();
				}
			}
		};
	}

	
	/**
	 * Connecting socket service.
	 * @return connection dialog.
	 */
	public static ConnectDlg connectSocketService() {
		return new ConnectDlg() {
			
			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@SuppressWarnings("deprecation")
			@Override
			protected void connect() {
				// TODO Auto-generated method stub
				String remoteHost = txtRemoteHost.getText().trim();
				String remotePort_s = txtRemotePort.getText().trim();
				int remotePort = -1;
				if (!remotePort_s.isEmpty()) remotePort = Integer.parseInt(remotePort_s);
					
				connector = DriverManager.getSocketConnection(remoteHost, remotePort, txtRemoteUsername.getText(), txtRemotePassword.getText());
				
				if (connector == null) {
					JOptionPane.showMessageDialog(
						this, "Can't connect to socket service", "Can't connect to socket service", JOptionPane.ERROR_MESSAGE);
				}
				else {
					dispose();
				}
			}
		};
	}

	
}
