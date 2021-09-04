/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.listener.RemoteInfo;
import net.hudup.listener.RemoteInfoList;

/**
 * This graphic user interface (GUI) is the dialog for showing remote information {@link RemoteInfo}.
 * This panel contains the panel ({@link #paneRemoteInfo}) for allowing users to modify and add new remote information.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfoDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The internal panel ({@link RemoteInfoPane}) for allowing users to modify and add new remote information.
	 */
	private RemoteInfoPane paneRemoteInfo = null;
	
	
	/**
	 * The result after users press OK button of this dialog as a list of remote information.
	 */
	private RemoteInfoList result = null;
	
	
	/**
	 * Constructor with parent component and initial list of remote information.
	 * @param comp parent component.
	 * @param rInfoList {@link RemoteInfoList} to initialize the internal panel {@link #paneRemoteInfo}.
	 */
	public RemoteInfoDlg(Component comp, RemoteInfoList rInfoList) {
		super(UIUtil.getDialogForComponent(comp), "Remote information dialog", true);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		paneRemoteInfo = new RemoteInfoPane(rInfoList);
		body.add(paneRemoteInfo, BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				result = paneRemoteInfo.getRemoteInfoList();
				dispose();
			}
		});
		
		JButton cancel = new JButton("Cancel");
		footer.add(cancel);
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				result = null;
				dispose();
			}
		});

		
		setVisible(true);
	}
	
	
	/**
	 * Getting the result (after users press OK button of this dialog) as a list of remote information.
	 * @return the result (after users press OK button of this dialog) as {@link RemoteInfoList}.
	 */
	public RemoteInfoList getResult() {
		return result;
	}
	
	
}
