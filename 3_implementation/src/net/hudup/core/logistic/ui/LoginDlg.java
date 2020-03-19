package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

/**
 * This is login dialog.
 * 
 * @author Loc Nguyen
 * @version 13.0
 *
 */
public class LoginDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Text field to fill user name.
	 */
	protected JTextField txtUsername = null;
	
	
	/**
	 * Text field to fill password.
	 */
	protected JPasswordField txtPassword = null;

	
	/**
	 * User name.
	 */
	protected String username = null;
	
	
	/**
	 * Password.
	 */
	protected String password = null;
	
	
	/**
	 * Default constructor.
	 */
	public LoginDlg() {
		this(null, "Login dialog");
	}
	
	
	/**
	 * Default constructor.
	 * @param comp parent component.
	 * @param title title.
	 */
	public LoginDlg(Component comp, String title) {
		// TODO Auto-generated constructor stub
		super(UIUtil.getFrameForComponent(comp), title, true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(250, 150);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());
		
        JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);

		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		left.add(new JLabel("Username:"));
		left.add(new JLabel("Password:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		txtUsername = new JTextField("admin");
		right.add(txtUsername);

		txtPassword = new JPasswordField("admin");
		right.add(txtPassword);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton login = new JButton("Login");
		login.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				login();
				dispose();
			}
		});
		footer.add(login);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				username = password = null;
				dispose();
			}
		});
		footer.add(cancel);
		
		setVisible(true);
	}

	
	/**
	 * Login method can be overrided.
	 */
	@SuppressWarnings("deprecation")
	protected void login() {
		username = txtUsername.getText();
		password= txtPassword.getText();
	}
	
	
	/**
	 * Testing whether user login.
	 * @return whether user login.
	 */
	public boolean wasLogin() {
		return username != null && password != null;
	}
	
	
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
	
	
}
