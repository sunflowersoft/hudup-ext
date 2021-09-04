/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class is an implementation of remote storage manager as tree.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RemoteStorageTree extends JTree implements TreeSelectionListener {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Storage service.
	 */
	protected VirtualStorageService service = null;
	
	
	/**
	 * Showing file mode.
	 */
	protected boolean showFile = false;
	
	
	/**
	 * Constructor with root directory.
	 * @param service storage service.
	 * @param showFile showing file mode.
	 */
	public RemoteStorageTree(VirtualStorageService service, boolean showFile) {
		this.service = service;
		this.showFile = showFile;
		
		setModel(new DefaultTreeModel(null));
		getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		setShowsRootHandles(true);
		
		addTreeSelectionListener(this);
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(!SwingUtilities.isRightMouseButton(e) ) return;
				final Node node = (Node)getLastSelectedPathComponent();
				
				JPopupMenu ctxMenu = new JPopupMenu();
				
				if (node != null) {
					JMenuItem miRename = new JMenuItem("Rename");
					miRename.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								rename(node);
							}
							
						});
					ctxMenu.add(miRename);
					
					JMenuItem miCopy = new JMenuItem("Copy to");
					miCopy.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								copyTo(node);
							}
							
						});
					ctxMenu.add(miCopy);
					
					JMenuItem miMove = new JMenuItem("Move to");
					miMove.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								moveTo(node);
							}
							
						});
					ctxMenu.add(miMove);
					
					if (node != getModel().getRoot()) {
						JMenuItem miDelete = new JMenuItem("Delete");
						miDelete.addActionListener( 
							new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									delete(node);
								}
								
							});
						ctxMenu.add(miDelete);
					}
					
					ctxMenu.addSeparator();
					
					JMenuItem miAdd = new JMenuItem("Add new");
					miAdd.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								addNew(node);
							}
							
						});
					ctxMenu.add(miAdd);

					ctxMenu.addSeparator();
				}
				
				JMenuItem miRefresh = new JMenuItem("Refresh");
				miRefresh.addActionListener( 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							refresh();
						}
						
					});
				ctxMenu.add(miRefresh);

				ctxMenu.show((Component)e.getSource(), e.getX(), e.getY());
			}
			
		});
		
		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_F5) {
					refresh();
				}
			}
		});

		update((Node)null);
	}
	
	
	/**
	 * Checking whether parent node contains the node that has specified name.
	 * @param parent parent node.
	 * @param childName specified name.
	 * @return whether parent node contains the node that has specified name.
	 */
	private boolean exists(Node parent, String childName) {
		if (parent == null || childName == null) return false;
		
		List<Node> nodes = getChildren(parent);
		for (Node node : nodes) {
			if (node.toString().equals(childName)) return true;
		}
		
		return false;
	}
	
	
	/**
	 * Renaming node.
	 * @param node node.
	 */
	private void rename(Node node) {
		if (node == null) return;
		Node parent = (Node)node.getParent();
		if (parent == null) return;
		String newName = JOptionPane.showInputDialog(this, "Node name", node.toString() + "-" + System.currentTimeMillis());
		if (newName == null) return;
		newName = newName.replace("/", ""); newName = newName.trim();
		if (newName.isEmpty()) return;
		
		if (exists(parent, newName)) return;
		
		try {
			VirtualStorageUnit childUnit = service.rename(node.unit, newName);
			if (childUnit != null) refresh();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Copying node to somewhere.
	 * @param node node.
	 */
	private void copyTo(Node node) {
		if (node == null) return;
		VirtualStorageUnit selectedStore = RemoteStorageTree.chooseStore(this, service);
		if (selectedStore == null) return;
		
		try {
			service.copy(node.unit, selectedStore.contact(node.unit.getLastName()));
			refresh();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Copying node to somewhere.
	 * @param node node.
	 */
	private void moveTo(Node node) {
		if (node == null) return;
		VirtualStorageUnit selectedStore = RemoteStorageTree.chooseStore(this, service);
		if (selectedStore == null) return;
		
		try {
			service.move(node.unit, selectedStore.contact(node.unit.getLastName()));
			refresh();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Deleting node.
	 * @param node node.
	 */
	private void delete(Node node) {
		if (node == null) return;
		Node parent = (Node)node.getParent();
		if (parent == null) return;
		
		try {
			if (service.delete(node.unit))
				((DefaultTreeModel)getModel()).removeNodeFromParent(node);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Adding new node.
	 * @param parent parent node.
	 */
	private void addNew(Node parent) {
		if (parent == null) return;
		String newName = JOptionPane.showInputDialog(this, "Node name", "new node");
		if (newName == null) return;
		newName = newName.replace("/", ""); newName = newName.trim();
		if (newName.isEmpty()) return;
		
		if (exists(parent, newName)) return;
		
		try {
			VirtualStorageUnit childUnit = service.createStore(parent.unit.contact(newName));
			if (childUnit != null) {
				Node child = new Node(childUnit);
				((DefaultTreeModel)getModel()).insertNodeInto(child, parent, parent.getChildCount());
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
	}
	
	
	/**
	 * Refreshing tree.
	 */
	public void refresh() {
		update((Node)null);
	}
	
	
	/**
	 * Adding sub-directories.
	 * @param parent node.
	 */
	private void update(Node parent) {
		try {
			if (parent == null) {
				VirtualStorageUnit root = service.getRoot();
				if (root == null)
					((DefaultTreeModel)getModel()).setRoot(null);
				else {
					Node rootNode = new Node(root);
					((DefaultTreeModel)getModel()).setRoot(rootNode);
					update(rootNode);
				}
				
				return;
			}
			if (parent.unit == null || !service.isStore(parent.unit)) return;
			
			List<VirtualStorageUnit> units = service.list(parent.unit);
		    Collections.sort(units, new Comparator<VirtualStorageUnit>() {

				@Override
				public int compare(VirtualStorageUnit u1, VirtualStorageUnit u2) {
					return u1.getLastName().compareToIgnoreCase(u2.getLastName());
				}
		    	
			});

		    List<VirtualStorageUnit> uList = Util.newList();
		    for (VirtualStorageUnit unit : units) {
		    	Node node = new Node(unit);
		    	if (service.isStore(unit)) {
		    		parent.add(node);
		    		update(node);
		    	}
		    	else if (showFile) {
		    		uList.add(unit);
		    	}
		    }
		    
		    for (VirtualStorageUnit unit : uList) {
		    	Node node = new Node(unit);
	    		parent.add(node);
		    }
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
	}
	
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		onSelectNode((Node)getLastSelectedPathComponent());
	}


	/**
	 * Processing event-driven node.
	 * @param node event-driven node.
	 */
	public void onSelectNode(Node node) {
		
	}
	
	
	/**
	 * Getting children of parent node.
	 * @param parent parent node.
	 * @return children of parent node.
	 */
	public List<Node> getChildren(Node parent) {
		List<Node> children = Util.newList();
		if (parent == null) return children;
		
		DefaultTreeModel model = (DefaultTreeModel)getModel();
		int n = model.getChildCount(parent);
		for (int i = 0; i < n; i++) {
			children.add((Node)model.getChild(parent, i));
		}
		
		return children;
	}
	
	
	/**
	 * Storage node.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static class Node extends DefaultMutableTreeNode {

		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Storage unit.
		 */
		public VirtualStorageUnit unit = null;
		
		/**
		 * Construction with unit.
		 * @param unit unit.
		 */
		public Node(VirtualStorageUnit unit) {
			this.unit = unit;
		}

		@Override
		public String toString() {
			if (unit == null)
				return super.toString();
			else
				return unit.getLastName();
		}
		
	}
	
	
	/**
	 * Dialog to select store from tree.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static class StoreChooser extends JDialog {


		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Storage tree.
		 */
		protected RemoteStorageTree tree = null;
		
		/**
		 * Selected unit.
		 */
		protected VirtualStorageUnit selectedStore = null;		
		
		/**
		 * Constructor with storage service.
		 * @param comp component.
		 * @param service storage service.
		 */
		public StoreChooser(Component comp, VirtualStorageService service) {
			super(UIUtil.getDialogForComponent(comp), true);
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setSize(300, 400);
			setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
			setLayout(new BorderLayout());
			
			if (service == null) service = new VirtualFileService();
			
			JPanel body = new JPanel(new BorderLayout());
			add(body, BorderLayout.CENTER);
			
			tree = new RemoteStorageTree(service, false);
			body.add(new JScrollPane(tree), BorderLayout.CENTER);
			
			JPanel footer = new JPanel();
			add(footer, BorderLayout.SOUTH);

			JButton ok = new JButton("OK");
			ok.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Node selectedNode = (Node)tree.getLastSelectedPathComponent();
					selectedStore = selectedNode != null ? selectedNode.unit : null;
					dispose();
				}
			});
			footer.add(ok);

			JButton cancel = new JButton("Cancel");
			cancel.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectedStore = null;
					dispose();
				}
			});
			footer.add(cancel);
		}
		
		/**
		 * Getting selected unit.
		 * @return selected unit.
		 */
		public VirtualStorageUnit getSelectedStore() {
			return selectedStore;
		}
		
	}
	
	
	/**
	 * Choosing remote store.
	 * @param comp parent component.
	 * @param service storage service.
	 * @return selected store.
	 */
	public static VirtualStorageUnit chooseStore(Component comp, VirtualStorageService service) {
		StoreChooser sc = new StoreChooser(comp, service);
		sc.setVisible(true);
		return sc.getSelectedStore();
	}
	
	
	/**
	 * Choosing remote store.
	 * @param comp parent component.
	 * @return selected store.
	 */
	public static VirtualStorageUnit chooseStore(Component comp) {
		return chooseStore(comp, null);
	}


}
