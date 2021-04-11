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
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.WindowConstants;

import net.hudup.core.client.Connector;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.RemoteStorageList;
import net.hudup.core.client.RemoteStorageTree;
import net.hudup.core.client.Server;
import net.hudup.core.client.VirtualFileService;
import net.hudup.core.client.VirtualStorageService;
import net.hudup.core.client.VirtualStorageUnit;
import net.hudup.core.data.ui.toolkit.Dispose;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
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
	 * Storage service.
	 */
	protected VirtualStorageService service = null;
	
	
	/**
	 * Storage tree.
	 */
	protected RemoteStorageTree tree = null;
	
	
	/**
	 * Storage list.
	 */
	protected RemoteStorageList list = null;
	
	
	/**
	 * Label about selected directory.
	 */
	protected JLabel lblSelectedDir = null;
	
	
	/**
	 * Default constructor with storage service.
	 * @param service specified storage service.
	 */
	public WorkingDirectoryManager(VirtualStorageService service) {
		this.service = service;
		
		setLayout(new BorderLayout());
		
		lblSelectedDir = new JLabel("No selected directory");
		list = new RemoteStorageList(service);
		tree = new RemoteStorageTree(service, false) {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void onSelectNode(Node node) {
				if (node != null) {
					lblSelectedDir.setText("Files of directory \"" + DSUtil.shortenVerbalName(node.toString()) + "\"");
					list.update(node.unit);
				}
				else {
					lblSelectedDir.setText("No selected directory");
					list.update((VirtualStorageUnit)null);
				}
			}
			
		};

		
		JPanel left = new JPanel(new BorderLayout());
		left.add(new JLabel("Directories"), BorderLayout.NORTH);
		left.add(new JScrollPane(tree), BorderLayout.CENTER);
		
		JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		left.add(leftToolbar, BorderLayout.SOUTH);

		JButton btnRefreshTree = UIUtil.makeIconButton("refresh-16x16.png", "refresh_tree", "Refresh", "Refresh",
		new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				tree.refresh();
			}
		});
		btnRefreshTree.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnRefreshTree);
			
		
		JPanel right = new JPanel(new BorderLayout());
		right.add(lblSelectedDir, BorderLayout.NORTH);
		right.add(new JScrollPane(list), BorderLayout.CENTER);
		
		JPanel rightToolbars = new JPanel(new BorderLayout());
		right.add(rightToolbars, BorderLayout.SOUTH);
		
		JPanel rightToolbar1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		rightToolbars.add(rightToolbar1, BorderLayout.WEST);
		
		JButton btnAdd = UIUtil.makeIconButton("add-16x16.png", "add", "Add new", "Add new",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					list.addNew();
				}
			});
		btnAdd.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar1.add(btnAdd);

		JButton btnEdit = UIUtil.makeIconButton("edit-16x16.png", "edit", "Edit", "Edit",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					list.edit();
				}
			});
		btnEdit.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar1.add(btnEdit);

		JButton btnDelete = UIUtil.makeIconButton("delete-16x16.png", "delete", "Delete", "Delete",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					list.delete();
				}
			});
		btnDelete.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar1.add(btnDelete);

		JPanel rightToolbar2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightToolbars.add(rightToolbar2, BorderLayout.EAST);
		
		JButton btnDownload = UIUtil.makeIconButton("download-16x16.png", "download", "Download", "Download",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					list.download();
				}
			});
		btnDownload.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar2.add(btnDownload);

		JButton btnUpload = UIUtil.makeIconButton("upload-16x16.png", "upload", "Upload", "Upload",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					list.upload();
				}
			});
		btnUpload.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar2.add(btnUpload);

		JButton btnRefresh = UIUtil.makeIconButton("refresh-16x16.png", "refresh", "Refresh", "Refresh",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					list.refresh();
				}
			});
		btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar2.add(btnRefresh);

		
		JSplitPane body = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, right);
		body.setOneTouchExpandable(true);
		body.setDividerLocation(200);
		add(body, BorderLayout.CENTER);
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
	 * @param comp parent component.
	 * @param service storage service.
	 */
	public static void showManager(Component comp, VirtualStorageService service) {
		JDialog dlgManager = new JDialog(UIUtil.getFrameForComponent(comp), "Working directory manager", true);
		dlgManager.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgManager.setSize(600, 600);
		dlgManager.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		dlgManager.setLayout(new BorderLayout());
		
		if (service == null) service = new VirtualFileService();
		WorkingDirectoryManager manager = new WorkingDirectoryManager(service);
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
		//footer.add(close);

		dlgManager.setVisible(true);
	}
	
	
	/**
	 * Showing working directory manager.
	 * @param comp parent component.
	 */
	public static void showManager(Component comp) {
		showManager(comp, null);
	}
	
	
	/**
	 * Main method.
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		Connector dlg = Connector.connect();
        Image image = UIUtil.getImage("server-32x32.png");
        if (image != null) dlg.setIconImage(image);
		
		Server server = dlg.getServer();
		if ((server == null) && !(server instanceof PowerServer)) {
			showManager(null);
		}
		else {
			try {
				showManager(null, ((PowerServer)server).getStorageService());
			} catch (Exception e) {LogUtil.trace(e);}
		}
	}
	
	
}
