/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.hudup.core.logistic.Inspector;

/**
 * This class represents a description dialog.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DescriptionDlg extends JDialog implements Inspector {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with parent component, title, and description.
	 * @param comp parent component.
	 * @param title title.
	 * @param description description.
	 */
	public DescriptionDlg(Component comp, String title, String description) {
		super(UIUtil.getDialogForComponent(comp), title, true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		TextArea txtDesc = new TextArea(description);
		txtDesc.setEditable(false);
		body.add(new JScrollPane(txtDesc), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		footer.add(btnOK);
	}


	@Override
	public void inspect() {
		// TODO Auto-generated method stub
		setVisible(true);
	}

	
}
