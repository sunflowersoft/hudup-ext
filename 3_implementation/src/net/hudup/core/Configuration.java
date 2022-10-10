/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.hudup.core.logistic.ui.UIUtil;

/**
 * Starting configuration.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class Configuration implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The root package (root directory) of all classes.
	 */
	public final static String  ROOT_PACKAGE = "/net/hudup/"; //Setting ROOT_PACKAGE="" will retrieve all classes from plug-in manager. 
	
	
	/**
	 * The resources directory that contains any resources except classes such as images, template files.
	 */
	public final static String  RESOURCES_PACKAGE = ROOT_PACKAGE + "core/resources/";

	
	/**
	 * Working directory name.
	 */
	public static String WORKING_NAME = "working";
	
	
	/**
	 * Internal properties.
	 */
	private static Properties properties = new Properties();
	
	
	/**
	 * Running flag.
	 */
	private static boolean run = false;
	
	
	
	/**
	 * Text field of working directory name.
	 */
	private JTextField txtWorkingName = null;

	
	/**
	 * Default constructor.
	 */
	public Configuration() {
		if (run) return;
		
		startGUI();
		run = true;
	}
	
	
	/**
	 * Starting GUI.
	 */
	private void startGUI() {
		final JDialog cfg = new JDialog((JFrame)null, "Starting configuration", true);
		cfg.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		cfg.setSize(400, 150);
		cfg.setLocationRelativeTo(null);
		cfg.setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		cfg.add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);

		left.add(new JLabel("Working directory name:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		JPanel paneWorkingName = new JPanel(new BorderLayout());
		right.add(paneWorkingName);
		
		txtWorkingName = new JTextField(WORKING_NAME);
		paneWorkingName.add(txtWorkingName, BorderLayout.CENTER);

		JButton btnWorkingName = UIUtil.makeIconButton(
			"checking-16x16.png",
			"checking", 
			"Checking whether working directory is existent - http://www.iconarchive.com", 
			"Checking", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String enterWorkingName = txtWorkingName.getText();
					enterWorkingName = enterWorkingName != null ? enterWorkingName.trim() : "";
					if (enterWorkingName.isEmpty()) {
						JOptionPane.showMessageDialog(cfg, "Empty working directory name", "Empty name", JOptionPane.ERROR_MESSAGE);
						return;
					}
					else if (!Files.exists(Paths.get(enterWorkingName))) {
						JOptionPane.showMessageDialog(cfg, "Woring directory \"" + enterWorkingName + "\" is inexistent.", "Inexistent", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
							
					String msg = "Working directory \"" + enterWorkingName + "\" is existent.";
					msg += "\nSuggested working directory is \"" + WORKING_NAME + new Date().getTime() + "\"";
					JOptionPane.showMessageDialog(cfg, msg, "Existent", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		paneWorkingName.add(btnWorkingName, BorderLayout.EAST);
		
		JPanel footer = new JPanel(new BorderLayout());
		cfg.add(footer, BorderLayout.SOUTH);
		
		JPanel buttons = new JPanel();
		footer.add(buttons, BorderLayout.NORTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String workingName = txtWorkingName.getText();
				workingName = workingName != null ? workingName.trim() : null;
				if (workingName != null && !workingName.isEmpty()) {
					properties.put(WORKING_NAME, workingName);
				}
				cfg.dispose();
			}
		});
		buttons.add(ok);
		
		cfg.setVisible(true);
	}
	
	
	/**
	 * Getting property given key.
	 * @param key specified value.
	 * @param defaultValue default value if there is no specified key.
	 * @return property of given key.
	 */
	public static String getProperty(String key, String defaultValue) {
		try {
			return properties.getProperty(key, defaultValue);
		} catch (Exception e) { }
		
		if (key != null && key.equals(WORKING_NAME))
			return WORKING_NAME;
		else
			return defaultValue;
	}
	
	
}
