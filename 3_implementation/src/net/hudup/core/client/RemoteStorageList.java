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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class is an implementation of remote storage manager as list.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RemoteStorageList extends JList<StorageItem> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Storage service.
	 */
	protected VirtualStorageService service = null;
	
	
	/**
	 * Parent unit.
	 */
	protected VirtualStorageUnit parent = null;
	
	
	/**
	 * Constructor with service and parent unit.
	 * @param service storage service.
	 * @param parent parent unit.
	 */
	public RemoteStorageList(VirtualStorageService service, VirtualStorageUnit parent) {
		super(new DefaultListModel<StorageItem>());
		this.service = service;
		
		setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(!SwingUtilities.isRightMouseButton(e) ) return;
				final StorageItem item = getSelectedValue();
				JPopupMenu ctxMenu = new JPopupMenu();
				
				if (item != null) {
					JMenuItem miEdit = new JMenuItem("Edit");
					miEdit.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								edit();
							}
							
						});
					ctxMenu.add(miEdit);

					JMenuItem miRename = new JMenuItem("Rename");
					miRename.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								rename(item);
							}
							
						});
					ctxMenu.add(miRename);

					JMenuItem miCopy = new JMenuItem("Copy to");
					miCopy.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								copyTo();
							}
							
						});
					ctxMenu.add(miCopy);

					JMenuItem miMove = new JMenuItem("Move to");
					miMove.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								moveTo();
							}
							
						});
					ctxMenu.add(miMove);

					JMenuItem miDelete = new JMenuItem("Delete");
					miDelete.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								delete();
							}
							
						});
					ctxMenu.add(miDelete);
					
					ctxMenu.addSeparator();
				}
				
				JMenuItem miAdd = new JMenuItem("Add new");
				miAdd.addActionListener( 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							addNew();
						}
						
					});
				ctxMenu.add(miAdd);

				JMenuItem miUpload = new JMenuItem("Upload");
				miUpload.addActionListener( 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							upload();
						}
						
					});
				ctxMenu.add(miUpload);
				
				if (item != null) {
					JMenuItem miDownload = new JMenuItem("Download");
					miDownload.addActionListener( 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								download();
							}
							
						});
					ctxMenu.add(miDownload);
				}
				
				ctxMenu.addSeparator();

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

		update(parent);
	}
	
	
	/**
	 * Constructor with service.
	 * @param service storage service.
	 */
	public RemoteStorageList(VirtualStorageService service) {
		this(service, null);
	}
	
	
	/**
	 * Getting list model.
	 * @return list model.
	 */
	private DefaultListModel<StorageItem> getListModel() {
		return (DefaultListModel<StorageItem>)getModel();
	}
	
	
	/**
	 * Checking whether the list contains the item that has specified name.
	 * @param itemName specified name.
	 * @return whether the list contains the item that has specified name.
	 */
	private boolean exists(String itemName) {
		if (parent == null || itemName == null) return false;
		
		ListModel<StorageItem> model = getModel();
		int n = model.getSize();
		for (int i = 0; i < n; i++) {
			StorageItem item = model.getElementAt(i);
			if (item.toString().equals(itemName)) return true;
		}
		
		return false;
	}


	/**
	 * Editing item.
	 */
	public void edit() {
		StorageItem item = getSelectedValue();
		if (item == null) return;
		
		JDialog editor = new JDialog(UIUtil.getFrameForComponent(this), DSUtil.shortenVerbalName(item.toString()), true);
		editor.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		editor.setSize(400, 400);
		editor.setLocationRelativeTo(UIUtil.getFrameForComponent(this));
		editor.setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new FlowLayout(FlowLayout.LEFT));
		editor.add(header, BorderLayout.NORTH);
		header.add(new JLabel("Editing file \"" + DSUtil.shortenVerbalName(item.toString()) + "\""));
		
		JPanel body = new JPanel(new BorderLayout());
		editor.add(body, BorderLayout.CENTER);
		String content = "";
		try {
			byte[] data = service.readFile(item.unit);
			if (data != null) content = new String(data);
		} catch (Exception e) {LogUtil.trace(e);}
		final TextArea txtArea = new TextArea(content);
		txtArea.setEditable(true);
		body.add(new JScrollPane(txtArea), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		editor.add(footer, BorderLayout.SOUTH);

		JButton save = new JButton("Save");
		save.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean ret = uploadOverwrite(item.unit.getLastName(), txtArea.getText());
				txtArea.setChanged(false);
				if (ret) JOptionPane.showMessageDialog(txtArea, "Save and upload sucessfully");
			}
		});
		footer.add(save);

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				editor.dispose();
			}
		});
		footer.add(close);

		editor.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				if (!txtArea.isChanged()) return;
				
				int answer = JOptionPane.showConfirmDialog(txtArea, "File changed.\nDo you want to save?", "File changed", JOptionPane.OK_CANCEL_OPTION);
				if (answer == JOptionPane.OK_OPTION) {
					boolean ret = uploadOverwrite(item.unit.getLastName(), txtArea.getText());
					txtArea.setChanged(false);
					if (ret) JOptionPane.showMessageDialog(txtArea, "Save and upload sucessfully");
				}
			}
			
		});
		
		editor.setVisible(true);
	}
	
	
	/**
	 * Renaming item.
	 * @param item item.
	 */
	private void rename(StorageItem item) {
		String newName = JOptionPane.showInputDialog(this, "Node name", item.toString() + "-" + System.currentTimeMillis());
		if (newName == null) return;
		newName = newName.replace("/", ""); newName = newName.trim();
		if (newName.isEmpty()) return;
		
		if (exists(newName)) return;
		
		try {
			VirtualStorageUnit unit = service.rename(item.unit, newName);
			if (unit != null) {
				getListModel().removeElement(item);
				getListModel().addElement(new StorageItem(unit));
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}

	}

	
	/**
	 * Copying item to somewhere.
	 * @param item item.
	 */
	private void copyTo() {
		VirtualStorageUnit selectedStore = RemoteStorageTree.chooseStore(this, service);
		if (selectedStore == null || selectedStore.equals(parent)) return;
		
		try {
			List<StorageItem> items = getSelectedValuesList();
			for (StorageItem item : items) {
				try {
					service.copy(item.unit, selectedStore.contact(item.unit.getLastName()));
				} catch (Exception e) {LogUtil.trace(e);}
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Moving item to somewhere.
	 * @param item item.
	 */
	private void moveTo() {
		VirtualStorageUnit selectedStore = RemoteStorageTree.chooseStore(this, service);
		if (selectedStore == null) return;
		
		try {
			List<StorageItem> items = getSelectedValuesList();
			for (StorageItem item : items) {
				try {
					boolean ret = service.move(item.unit, selectedStore.contact(item.unit.getLastName()));
					if (ret) getListModel().removeElement(item);
				} catch (Exception e) {LogUtil.trace(e);}
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Deleting selected items.
	 */
	public void delete() {
		List<StorageItem> items = getSelectedValuesList();
		for (StorageItem item : items) {
			try {
				if (service.delete(item.unit)) getListModel().removeElement(item);
			} catch (Exception e) {LogUtil.trace(e);}
		}
	}


	/**
	 * Adding new item.
	 */
	public void addNew() {
		if (parent == null) return;
		String newName = JOptionPane.showInputDialog(this, "Node name", "new node");
		if (newName == null) return;
		newName = newName.replace("/", ""); newName = newName.trim();
		if (newName.isEmpty()) return;
		
		if (exists(newName)) return;

		try {
			VirtualStorageUnit unit = service.createFile(parent.contact(newName));
			if (unit != null) {
				StorageItem item = new StorageItem(unit);
				getListModel().addElement(item);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}


	/**
	 * Uploading items.
	 */
	public void upload() {
		if (parent == null) return;
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fc.setMultiSelectionEnabled(true);
		fc.showOpenDialog(this);
		
		File[] files = fc.getSelectedFiles();
		if (files == null || files.length == 0) return;
		for (File file : files) {
			String newName = file.getName();
			boolean overwrite = false;
			if (exists(newName)) {
				overwrite = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "File name \"" + newName + "\" exists.\nDo you want to overwrite?", "File name exists", JOptionPane.OK_CANCEL_OPTION);
				if (!overwrite) {
					int last = newName.lastIndexOf(".");
					if (last < 0)
						newName = newName + "-" + System.currentTimeMillis();
					else
						newName = newName.substring(0, last) + "-" + System.currentTimeMillis() + "." + newName.substring(last + 1);
				}
			}
			
			try {
				byte[] data = Files.readAllBytes(file.toPath());
				VirtualStorageUnit unit = parent.contact(newName);
				boolean ret = service.writeFile(parent.contact(newName), data);
				if (ret && !overwrite) getListModel().addElement(new StorageItem(unit));
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
	}
	
	
	/**
	 * Uploading and overwriting text.
	 * @param unitName specified unit name.
	 * @param data specified data.
	 */
	private boolean uploadOverwrite(String unitName, String data) {
		if (unitName == null || data == null) return false;
		
		try {
			VirtualStorageUnit unit = parent.contact(unitName);
			boolean ret = service.writeFile(unit, data.getBytes());
			if (ret && !exists(unit.getLastName())) getListModel().addElement(new StorageItem(unit));
			return ret;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return false;
	}
	
	
	/**
	 * Downloading selected items.
	 */
	public void download() {
		List<StorageItem> items = getSelectedValuesList();
		if (items == null || items.size() == 0) return;
		
		JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File("."));
		fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fc.showSaveDialog(this);

		File dir = fc.getSelectedFile();
		if (dir == null) return;
		for (StorageItem item : items) {
			String newName = item.unit.getLastName();
			if (new File(dir, newName).exists()) {
				boolean overwrite = JOptionPane.YES_OPTION == JOptionPane.showConfirmDialog(this, "File name \"" + newName + "\" exists.\nDo you want to overwrite?", "File name exists", JOptionPane.OK_CANCEL_OPTION);
				if (!overwrite) {
					int last = newName.lastIndexOf(".");
					if (last < 0)
						newName = newName + "-" + System.currentTimeMillis();
					else
						newName = newName.substring(0, last) + "-" + System.currentTimeMillis() + "." + newName.substring(last + 1);
				}
			}
			
			try {
				byte[] data = service.readFile(item.unit);
				if (data != null) {
					Files.write(new File(dir, newName).toPath(), data);
				}
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
	}
	
	
	/**
	 * Refreshing the list.
	 */
	public void refresh() {
		update(parent);
	}
	
	
	/**
	 * Updating with parent unit.
	 * @param parent parent unit.
	 */
	public void update(VirtualStorageUnit parent) {
		try {
		    this.parent = parent;
		    getListModel().clear();
			if (parent == null) return;
			
		    List<VirtualStorageUnit> units = service.list(parent);
		    Vector<StorageItem> items = Util.newVector();
		    for (VirtualStorageUnit unit : units) {
		    	if (!service.isStore(unit)) items.add(new StorageItem(unit));
		    }
			
		    Collections.sort(items, new Comparator<StorageItem>() {
	
				@Override
				public int compare(StorageItem item1, StorageItem item2) {
					return item1.toString().compareToIgnoreCase(item2.toString());
				}
		    	
			});
		    
		    getListModel().addAll(items);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}

	}
	
	
}



/**
 * Storage node.
 * @author Loc Nguyen
 * @version 1.0
 */
class StorageItem implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Storage unit.
	 */
	public VirtualStorageUnit unit = null;
	
	
	/**
	 * Construction with directory.
	 * @param dir directory.
	 */
	public StorageItem(VirtualStorageUnit unit) {
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
