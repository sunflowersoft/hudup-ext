/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import net.hudup.core.client.PowerServer;
import net.hudup.core.data.ui.toolkit.Dispose;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class provides explorer tool to manage files in working directory on server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class WorkingDirectoryManager extends JPanel implements Dispose {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Power server.
	 */
	protected PowerServer server = null;
	
	/**
	 * Binded URI of this control panel.
	 */
	protected xURI bindUri = null;

	
	/**
	 * Default constructor.
	 * @param server specified server
	 * @param bindUri bound URI of such server. If it is not null, the server is remote.
	 */
	public WorkingDirectoryManager(PowerServer server, xURI bindUri) {
		this.server = server;
		this.bindUri = bindUri;
		
		setLayout(new BorderLayout());
	}


	@Override
	public void dispose() {
		
	}


	@Override
	public boolean isRunning() {
		return true;
	}


	/**
	 * Showing working directory manager.
	 * @param server specified server
	 * @param bindUri bound URI of such server. If it is not null, the server is remote.
	 */
	public static void showManager(Component comp, PowerServer server, xURI bindUri) {
		JDialog dlgManager = new JDialog(UIUtil.getFrameForComponent(comp), true);
		dlgManager.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgManager.setSize(600, 600);
		dlgManager.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		dlgManager.setLayout(new BorderLayout());
		
		WorkingDirectoryManager manager = new WorkingDirectoryManager(server, bindUri);
		dlgManager.add(manager, BorderLayout.CENTER);
		dlgManager.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				manager.dispose();
			}
			
		});
		
		JPanel footer = new JPanel();
		dlgManager.add(footer, BorderLayout.SOUTH);

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dlgManager.dispose();
			}
		});
		footer.add(close);

		dlgManager.setVisible(true);
	}
	
	
}
