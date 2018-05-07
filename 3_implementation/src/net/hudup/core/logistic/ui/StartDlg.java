package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


/**
 * This dialog allows users to choose and start some class or application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class StartDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Control for list of items.
	 */
    protected JComboBox<?> cmbItem = null;
    
    
	/**
	 * Constructor with parent component and title.
	 * @param comp parent component.
	 * @param title title of dialog.
	 */
	public StartDlg(Component comp, String title) {
		// TODO Auto-generated constructor stub
		super(UIUtil.getFrameForComponent(comp), title, true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		
        Image image = UIUtil.getImage("start-32x32.png");
        if (image != null)
        	setIconImage(image);
        
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        add(header, BorderLayout.NORTH);
        
        header.add(new JLabel(getGuidedText()), BorderLayout.NORTH);
        
        this.cmbItem = createItemControl();
        header.add(this.cmbItem, BorderLayout.CENTER);
        
        JButton btnStart = new JButton("Start");
        btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				start();
			}
		});
        header.add(btnStart, BorderLayout.EAST);
        
        JPanel body = new JPanel(new BorderLayout());
        add(body, BorderLayout.CENTER);
        
        JTextArea txtHelp = createHelp();
        body.add(new JScrollPane(txtHelp), BorderLayout.CENTER);
	}


	/**
	 * Create control for items to be selected.
	 * @return control for items to be selected.
	 */
	protected abstract JComboBox<?> createItemControl();
	
	
	/**
	 * Creating text area for showing some helpful information.
	 * @return text area for showing some helpful information.
	 */
	protected abstract JTextArea createHelp();
	
	
	/**
	 * Starting some application according to selected item.
	 */
	protected abstract void start();
	
	
	/**
	 * Getting item control.
	 * @return item control.
	 */
	public JComboBox<?> getItemControl() {
		return cmbItem;
	}
	
	
	/**
	 * Getting guidance text.
	 * @return guidance text.
	 */
	public String getGuidedText() {
		return "Please choose an item and press \"Start\" button";
	}
	
	
}
