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
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.hudup.core.Util;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.JImageList.ImageListItem;

/**
 * This class creates a graphic user interface (GUI) component as a list box whose items are images.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class JImageList<E> extends JList<ImageListItem<E>> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Modal mode.
	 */
	protected boolean modal = true;
	
	
	/**
	 * Default constructor.
	 */
	public JImageList() {
		super(new DefaultListModel<ImageListItem<E>>());
		
		setCellRenderer(createRender());
		setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		setVisibleRowCount(-1);
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				onMouseClicked(e);
			}
			
		});
	}
	
	
	/**
	 * Getting model.
	 * @return model.
	 */
	public DefaultListModel<ImageListItem<E>> getModel2() {
		return (DefaultListModel<ImageListItem<E>>)super.getModel();
	}
	
	
	/**
	 * Setting modal mode.
	 * @param modal modal mode.
	 */
	public void setModal(boolean modal) {
		this.modal = modal;
	}
	
	
	/**
	 * Setting list data.
	 * @param listData list data.
	 */
	public void setListData(List<ImageListItem<E>> listData) {
		DefaultListModel<ImageListItem<E>> model = getModel2();
		model.clear();
		model.addAll(listData);
	}
	
	@Override
	public void setListData(Vector<? extends ImageListItem<E>> listData) {
		DefaultListModel<ImageListItem<E>> model = getModel2();
		model.clear();
		model.addAll(listData);
	}


	@Override
	public void setListData(ImageListItem<E>[] listData) {
		DefaultListModel<ImageListItem<E>> model = getModel2();
		model.clear();
		for (ImageListItem<E> element : listData) model.addElement(element);
	}


	/**
	 * Creating render.
	 * @return render.
	 */
	protected ListCellRenderer<? super ImageListItem<E>> createRender() {
		return new ImageListRenderer<ImageListItem<E>>();
	}
	
	
	/**
	 * Handling mouse-clicking event.
	 * @param e mouse event.
	 */
	private void onMouseClicked(MouseEvent e) {
		ImageListItem<E> item = getSelectedValue();
		if(SwingUtilities.isRightMouseButton(e) ) {
			JPopupMenu contextMenu = createContextMenu();
			if(contextMenu == null) return;
			
			addToContextMenu(contextMenu);
			contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
		}
		else if (e.getClickCount() >= 2) {
			if (item == null) return;
			viewItem(item);
		}
	}
	
	
	/**
	 * Creating context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu() {
		ImageListItem<E> item = getSelectedValue();
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem miViewItem = new JMenuItem("View");
		miViewItem.addActionListener( 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					viewItem(item);
				}
				
			});
		if (item != null) ctxMenu.add(miViewItem);
		
		JMenuItem miDesc = new JMenuItem("Description");
		miDesc.addActionListener( 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					showInfo(item, item.getDesc());
				}
				
			});
		if (item != null) ctxMenu.add(miDesc);

		JMenuItem miPath = new JMenuItem("Path");
		miPath.addActionListener( 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					Path path = item.queryPath();
					if (path != null) showInfo(item, path.toAbsolutePath().toString());
				}
				
			});
		if ((item != null) && (!item.isPseudoPath()) && (item.queryPath() != null)) ctxMenu.add(miPath);

		JMenuItem miTag = new JMenuItem("Tag");
		miTag.addActionListener( 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					tagUI(item);
				}
				
			});
		if ((item != null) && (item.getTag() != null)) ctxMenu.add(miTag);

		if (ctxMenu.getComponentCount() > 0) ctxMenu.addSeparator();
		
		JMenuItem miRemoveSelectedItems = new JMenuItem("Remove");
		miRemoveSelectedItems.addActionListener( 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					removeSelectedItems();
				}
				
			});
		if (item != null) ctxMenu.add(miRemoveSelectedItems);

		return ctxMenu.getComponentCount() > 0 ? ctxMenu : null;
	}
	
	
    /**
     * Adding the context menu to this list.
     * @param contextMenu specified context menu.
     */
    protected void addToContextMenu(JPopupMenu contextMenu) {

    }
    
    
    /**
     * Show image
     * @param item specified item.
     */
    protected void viewItem(ImageListItem<E> item) {
    	if (item != null) UIUtil.viewImage(item.queryImage(), modal, this);
    }
    
    
    /**
     * Show description
     * @param item specified item.
     * @param info specified information.
     */
    protected void showInfo(ImageListItem<E> item, String info) {
    	if (item == null) {
    		if (modal) JOptionPane.showMessageDialog(this, "No information", "No information", JOptionPane.ERROR_MESSAGE);
    		return;
    	}
    	
		JDialog dlgImage = new JDialog(UIUtil.getDialogForComponent(this), "Information", modal);
		dlgImage.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgImage.setSize(400, 300);
		dlgImage.setLocationRelativeTo(UIUtil.getDialogForComponent(this));
		dlgImage.setLayout(new BorderLayout());

		TextArea txtInfo = new TextArea(info != null ? info : item.getDesc());
		txtInfo.setEditable(false);
		dlgImage.add(new JScrollPane(txtInfo), BorderLayout.CENTER);
		
		dlgImage.setVisible(true);
    }

    
    /**
     * User interface for tag.
     * @param item specified item.
     */
    protected void tagUI(ImageListItem<E> item) {
    	if (item == null || item.getTag() == null) {
    		if (modal) JOptionPane.showMessageDialog(this, "No tag", "No tag", JOptionPane.ERROR_MESSAGE);
    		return;
    	}

    	if (modal) JOptionPane.showMessageDialog(this, "Not implemented yet", "Not implemented yet", JOptionPane.WARNING_MESSAGE);
    }
    
    
    /**
     * Removing selected items
     */
    protected void removeSelectedItems() {
    	List<ImageListItem<E>> selectedItems = getSelectedValuesList();
    	if (selectedItems == null || selectedItems.size() == 0) return;
    	for (ImageListItem<E> selectedItem : selectedItems) removeItem(selectedItem);
    }

    
    /**
     * Getting the number of items.
     * @return the number of items.
     */
    public int getItemCount() {
    	DefaultListModel<ImageListItem<E>> model = getModel2();
    	return model.size();
    }
    
    
    /**
     * Getting item at specified index.
     * @param index specified index.
     * @return item at specified index.
     */
    public ImageListItem<E> getItem(int index) {
    	DefaultListModel<ImageListItem<E>> model = getModel2();
    	return model.get(index);
    }
    
    
    /**
     * Adding item.
     * @param item specified item.
     */
    public void addItem(ImageListItem<E> item) {
    	DefaultListModel<ImageListItem<E>> model = getModel2();
    	model.addElement(item);
    }
    
    
    /**
     * Setting item at specified index.
     * @param index specified index.
     * @param item specified item.
     * @return replaced item.
     */
    public ImageListItem<E> setItem(int index, ImageListItem<E> item) {
    	DefaultListModel<ImageListItem<E>> model = getModel2();
    	return model.set(index, item);
    }
    
    
    /**
     * Removing item at specified index.
     * @param index specified index.
     * @return removed item.
     */
    public ImageListItem<E> removeItem(int index) {
    	DefaultListModel<ImageListItem<E>> model = getModel2();
    	return model.remove(index);
    }
    
    
    /**
     * Removing specified item.
     * @param item specified item.
     * @return true if removal is successful.
     */
    public boolean removeItem(ImageListItem<E> item) {
    	DefaultListModel<ImageListItem<E>> model = getModel2();
    	return model.removeElement(item);
    }
    
    
    /**
     * Clearing all items.
     */
    public void clearItems() {
    	DefaultListModel<ImageListItem<E>> model = getModel2();
    	model.clear();
    }
    
    
    /**
     * This class is the image list with image directory.
     * @author Loc Nguyen
     * @version 1.0
     */
    public static class ImagePathList extends JImageList<Path> {
    	
    	/**
    	 * Serial version UID for serializable class. 
    	 */
    	private static final long serialVersionUID = 1L;

    	/**
    	 * Default constructor.
    	 */
		public ImagePathList() {
			super();
		}

		/**
		 * Constructor from image directory.
		 * @param imageDir image directory.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 */
		public ImagePathList(Path imageDir, int iconSize, boolean storeImage) {
			this();
			setListData(imageDir, true, iconSize, storeImage);
		}
		
		/**
		 * Constructor from image directory.
		 * @param imageDir image directory.
		 * @param iconSize icon size.
		 */
		public ImagePathList(Path imageDir, int iconSize) {
			this();
			setListData(imageDir, true, iconSize, false);
		}

		/**
		 * Constructor from image directory.
		 * @param imageDir image directory.
		 */
		public ImagePathList(Path imageDir) {
			this();
			setListData(imageDir, false, 0, true);
		}

		/**
		 * Setting image list from image directory.
		 * @param imageDir image directory.
		 * @param createIcon flag to indicate whether to create icon.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 */
		private void setListData(Path imageDir, boolean createIcon, int iconSize, boolean storeImage) {
			List<ImageListItem<Path>> list = Util.newList();
			if (imageDir == null || !Files.exists(imageDir) || !Files.isDirectory(imageDir)) {
				setListData(list);
				return;
			}
			
			File[] imageFiles = imageDir.toFile().listFiles();
			if (imageFiles == null) {
				setListData(list);
				return;
			}
			
			for (File imageFile : imageFiles) {
				if (!imageFile.isFile()) continue;
				try {
					Path imagePath = imageFile.toPath();
					ImageListItem<Path> item = null;
					if (createIcon)
						item = ImageListItem.create(imagePath, iconSize, storeImage);
					else
						item = ImageListItem.create(imagePath);
					if (item != null) list.add(item);
				} catch (Exception e) {LogUtil.trace(e);}
			}

			setListData(list);
		}
    	
		/**
		 * Setting image list from image directory.
		 * @param imageDir image directory.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 */
		public void setListData(Path imageDir, int iconSize, boolean storeImage) {
			setListData(imageDir, true, iconSize, storeImage);
		}
		
		/**
		 * Setting image list from image directory.
		 * @param imageDir image directory.
		 * @param iconSize icon size.
		 */
		public void setListData(Path imageDir, int iconSize) {
			setListData(imageDir, true, iconSize, false);
		}

		/**
		 * Setting image list from image directory.
		 * @param imageDir image directory.
		 * @param storeImage flag to indicate whether to store image.
		 */
		public void setListData(Path imageDir) {
			setListData(imageDir, false, 0, true);
		}

		/**
		 * Creating path item from path.
		 * @param imagePath specified path.
		 * @param createIcon flag to indicate whether to create icon.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return item created from path.
		 */
		private ImageListItem<Path> createItem(Path imagePath, boolean createIcon, int iconSize, boolean storeImage) {
			if (createIcon)
				return ImageListItem.create(imagePath, iconSize, storeImage);
			else
				return ImageListItem.create(imagePath);
		}
		
		/**
		 * Adding path item.
		 * @param imagePath specified path.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return added item.
		 */
		public ImageListItem<Path> addItem(Path imagePath, int iconSize, boolean storeImage) {
			ImageListItem<Path> item = createItem(imagePath, true, iconSize, storeImage);
			if (item != null) super.addItem(item);
			return item;
		}

		/**
		 * Adding path item.
		 * @param imagePath specified path.
		 * @param iconSize icon size.
		 * @return added item.
		 */
		public ImageListItem<Path> addItem(Path imagePath, int iconSize) {
			ImageListItem<Path> item = createItem(imagePath, true, iconSize, false);
			if (item != null) super.addItem(item);
			return item;
		}
		
		/**
		 * Adding path item.
		 * @param imagePath specified path.
		 * @param iconSize icon size.
		 * @return added item.
		 */
		public ImageListItem<Path> addItem(Path imagePath) {
			ImageListItem<Path> item = createItem(imagePath, false, 0, true);
			if (item != null) super.addItem(item);
			return item;
		}

		/**
		 * Setting path item at specified index.
		 * @param index specified index.
		 * @param imagePath specified path.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return set item.
		 */
		public ImageListItem<Path> setItem(int index, Path imagePath, int iconSize, boolean storeImage) {
			ImageListItem<Path> item = createItem(imagePath, true, iconSize, storeImage);
			if (item != null) super.setItem(index, item);
			return item;
		}

		/**
		 * Setting path item at specified index.
		 * @param index specified index.
		 * @param imagePath specified path.
		 * @param iconSize icon size.
		 * @return set item.
		 */
		public ImageListItem<Path> setItem(int index, Path imagePath, int iconSize) {
			ImageListItem<Path> item = createItem(imagePath, true, iconSize, false);
			if (item != null) super.setItem(index, item);
			return item;
		}
		
		/**
		 * Setting path item at specified index.
		 * @param index specified index.
		 * @param imagePath specified path.
		 * @param iconSize icon size.
		 * @return set item.
		 */
		public ImageListItem<Path> setItem(int index, Path imagePath) {
			ImageListItem<Path> item = createItem(imagePath, false, 0, true);
			if (item != null) super.setItem(index, item);
			return item;
		}
		
		/**
		 * Creating path item from image.
		 * @param imageName image name.
		 * @param image specified image.
		 * @param createIcon flag to indicate whether to create icon.
		 * @param iconSize icon size.
		 * @return item created from image.
		 */
		private ImageListItem<Path> createItem(String imageName, Image image, boolean createIcon, int iconSize) {
			ImageListItem<String> item0 = ImageListItem.create(imageName, image, createIcon, iconSize, true);
			if (item0 == null)
				return null;
			else {
				ImageListItem<Path> item = new ImageListItem<Path>(Paths.get(item0.getItem()), item0.getIcon(), item0.getImage());
				item.setPseudoPath(true);
				return item;
			}
		}

		/**
		 * Adding image item.
		 * @param imageName image name.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @return added item.
		 */
		public ImageListItem<Path> addItem(String imageName, Image image, int iconSize) {
			ImageListItem<Path> item = createItem(imageName, image, true, iconSize);
			if (item != null) super.addItem(item);
			return item;
		}
		
		/**
		 * Adding image item.
		 * @param imageName image name.
		 * @param image specified image.
		 * @return added item.
		 */
		public ImageListItem<Path> addItem(String imageName, Image image) {
			ImageListItem<Path> item = createItem(imageName, image, false, 0);
			if (item != null) super.addItem(item);
			return item;
		}

		/**
		 * Adding image item.
		 * @param imageName image name.
		 * @return added item.
		 */
		public ImageListItem<Path> addItem(String imageName) {
			ImageListItem<Path> item = createItem(imageName, null, false, 0);
			if (item != null) super.addItem(item);
			return item;
		}

		/**
		 * Adding image item.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @return added item.
		 */
		public ImageListItem<Path> addItem(Image image, int iconSize) {
			return addItem("", image, iconSize);
		}
		
		/**
		 * Adding image item.
		 * @param image specified image.
		 * @return added item.
		 */
		public ImageListItem<Path> addItem(Image image) {
			return addItem("", image);
		}

		/**
		 * Setting image item at specified index.
		 * @param index specified index.
		 * @param imageName image name.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @return set item.
		 */
		public ImageListItem<Path> setItem(int index, String imageName, Image image, int iconSize) {
			ImageListItem<Path> item = createItem(imageName, image, true, iconSize);
			if (item != null) super.setItem(index, item);
			return item;
		}
		
		/**
		 * Setting image item at specified index.
		 * @param index specified index.
		 * @param imageName image name.
		 * @param image specified image.
		 * @return set item.
		 */
		public ImageListItem<Path> setItem(int index, String imageName, Image image) {
			ImageListItem<Path> item = createItem(imageName, image, false, 0);
			if (item != null) super.setItem(index, item);
			return item;
		}

		/**
		 * Setting image item at specified index.
		 * @param index specified index.
		 * @param imageName image name.
		 * @return set item.
		 */
		public ImageListItem<Path> setItem(int index, String imageName) {
			ImageListItem<Path> item = createItem(imageName, null, false, 0);
			if (item != null) super.setItem(index, item);
			return item;
		}

		/**
		 * Setting image item at specified index.
		 * @param index specified index.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @return set item.
		 */
		public ImageListItem<Path> setItem(int index, Image image, int iconSize) {
			return setItem(index, "", image, iconSize);
		}

		/**
		 * Setting image item at specified index.
		 * @param index specified index.
		 * @param image specified image.
		 * @return set item.
		 */
		public ImageListItem<Path> setItem(int index, Image image) {
			return setItem(index, "", image);
		}

    }
    
    
	/**
	 * This class is a wrapper of an object attached of an image item in image list.
	 * @param <E> type of attached object.
	 * @author Loc Nguyen
	 * @version 1.0
	 * 
	 */
	public static class ImageListItem<E> implements Serializable, Cloneable {
		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Minimum icon size.
		 */
		public static final int ICON_MINSIZE = 32;
		
		/**
		 * The internal object (item).
		 */
		protected E item = null;
		
		/**
		 * Icon associated with item.
		 */
		protected Icon icon = null;
		
		/**
		 * Image associated with item.
		 */
		protected Image image = null;
		
		/**
		 * Extra path.
		 */
		protected Path altPath = null;
		
		/**
		 * Flag to indicate whether path is pseudo.
		 */
		protected boolean pseudoPath = false;
		
		/**
		 * Tag object.
		 */
		protected Object tag = null;
		
		/**
		 * Constructor with the wrapped object (item), icon, and image.
		 * @param item specified object (item).
		 * @param icon specified icon.
		 * @param image specified image.
		 */
		public ImageListItem(E item, Icon icon, Image image) {
			this.item = item;
			this.icon = icon;
			this.image = image;
		}
		
		/**
		 * Constructor with the wrapped object (item) and icon.
		 * @param item specified object (item).
		 * @param icon specified icon.
		 */
		public ImageListItem(E item, Icon icon) {
			this(item, icon, null);
		}

		/**
		 * Constructor with the wrapped object (item) and image.
		 * @param item specified object (item).
		 * @param image specified image.
		 */
		public ImageListItem(E item, Image image) {
			this(item, null, image);
		}

		/**
		 * Constructor with the wrapped object (item).
		 * @param item specified object (item).
		 */
		public ImageListItem(E item) {
			this(item, null, null);
		}
		
		/**
		 * Getting the internal object (item).
		 * @return internal object (item).
		 */
		public E getItem() {
			return item;
		}

		/**
		 * Setting item.
		 * @param item specified item.
		 */
		public void setItem(E item) {
			this.item = item;
		}
		
		/**
		 * Getting icon.
		 * @return associated icon.
		 */
		public Icon getIcon() {
			return icon;
		}

		/**
		 * Setting icon
		 * @param icon specified icon.
		 */
		public void setIcon(Icon icon) {
			this.icon = icon;
		}
		
		/**
		 * Getting image.
		 * @return associated icon.
		 */
		public Image getImage() {
			return image;
		}

		/**
		 * Setting image
		 * @param image specified image.
		 */
		public void setImage(Image image) {
			this.image = image;
		}
		
		/**
		 * Getting alternative path.
		 * @return alternative path.
		 */
		public Path getAltPath() {
			return altPath;
		}
		
		/**
		 * Setting alternative path.
		 * @param altPath alternative path.
		 */
		public void setAltPath(Path altPath) {
			this.altPath = altPath;
		}
		
		/**
		 * Checking whether path is pseudo.
		 * @return whether path is pseudo.
		 */
		public boolean isPseudoPath() {
			return pseudoPath;
		}
		
		/**
		 * Setting whether path is pseudo.
		 * @param pseudoPath flag to indicate path is pseudo.
		 */
		public void setPseudoPath(boolean pseudoPath) {
			this.pseudoPath = pseudoPath;
		}
		
		/**
		 * Getting tag.
		 * @return tag object.
		 */
		public Object getTag() {
			return tag;
		}
		
		/**
		 * Setting tag.
		 * @param tag specified tag.
		 */
		public void setTag(Object tag) {
			this.tag = tag;
		}
		
		/**
		 * Query icon.
		 * @return icon.
		 */
		public Icon queryIcon() {
			if (icon != null) return icon;
			Image image = queryImage();
			return image != null ? new ImageIcon(image) : null;
		}
		
		/**
		 * Querying image.
		 * @return image.
		 */
		public Image queryImage() {
			if (image != null)
				return image;
			else if (icon != null && icon instanceof Image)
				return (Image)icon;
			else {
				Path path = queryPath();
				if (path != null)
					return UIUtil.loadImage(path);
				else
					return null;
			}
		}
		
		/**
		 * Querying path.
		 * @return path.
		 */
		public Path queryPath() {
			if (item != null && item instanceof Path)
				return (Path)item;
			else if (altPath != null)
				return altPath;
			else
				return null;
		}
		
		
		/**
		 * Getting possible name.
		 * @return possible name.
		 */
		public String getPossibleName() {
			if (item != null) {
				if (item instanceof Path) {
					Path fileName = ((Path)item).getFileName();
					return fileName != null ? fileName.toString() : item.toString();
				}
				else if (item instanceof String)
					return (String)item;
				else
					return item.toString();
			}
			else if (altPath != null) {
				Path fileName = altPath.getFileName();
				return fileName != null ? fileName.toString() : altPath.toString();
			}
			else
				return super.toString();
		}
		
		
		/**
		 * Getting item description.
		 * @return item description.
		 */
		public String getDesc() {
			if (item != null) {
				if (item instanceof Path) {
					if (isPseudoPath())
						return getPossibleName();
					else
						return ((Path)item).toAbsolutePath().toString();
				}
				else
					return getPossibleName();
			}
			else if (altPath != null) {
				if (isPseudoPath())
					return getPossibleName();
				else
					return altPath.toAbsolutePath().toString();
			}
			else
				return getPossibleName();
		}
		
		@Override
		public String toString() {
			return DSUtil.shortenVerbalName(getPossibleName());
		}
		
		/**
		 * Creating image list item from item and image.
		 * @param <E> item type.
		 * @param item specified item.
		 * @param image specified image.
		 * @param createIcon flag to indicate whether to create icon.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return image list item created from item and image.
		 */
		@SuppressWarnings("unchecked")
		private static <E> ImageListItem<E> create(E item, Image image, boolean createIcon, int iconSize, boolean storeImage) {
			if (item == null) return null;
			if (image == null) return new ImageListItem<E>(item, null, null);
			
			BufferedImage bufImage = null;
			if (image instanceof BufferedImage)
				bufImage = (BufferedImage)image;
			else
				bufImage = UIUtil.convertToBufferedImage(image);
			
			if (bufImage == null || bufImage.getWidth() <= 0 || bufImage.getHeight() <= 0) return null;
			if (item instanceof String) {
				String name = (String)item;
				name = name.trim();
				name = name.isEmpty() ? "" + System.currentTimeMillis() : name;
				item = (E)name;
			}
			
			if (!createIcon) return new ImageListItem<E>(item, null, bufImage);
			iconSize = iconSize < ICON_MINSIZE ? ICON_MINSIZE : iconSize;
			if (iconSize <= 0 || (bufImage.getWidth() <= iconSize && bufImage.getHeight() <= iconSize))
				return new ImageListItem<E>(item, null, bufImage);
			
			int ratio = bufImage.getWidth() / iconSize;
			ratio = Math.max(ratio, bufImage.getHeight() / iconSize);
			if (ratio == 1) return new ImageListItem<E>(item, null, bufImage);
			int newWidth = bufImage.getWidth() / ratio;
			newWidth = newWidth <= 0 ? bufImage.getWidth() : newWidth;
			int newHeight = bufImage.getHeight() / ratio;
			newHeight = newHeight <= 0 ? bufImage.getHeight() : newHeight;
			
			BufferedImage iconImage = UIUtil.resizeImage(bufImage, newWidth, newHeight);
			if (storeImage)
				return new ImageListItem<E>(item, new ImageIcon(iconImage), bufImage);
			else
				return new ImageListItem<E>(item, new ImageIcon(iconImage), null);
		}

		/**
		 * Creating image list item from item and image.
		 * @param <E> item type.
		 * @param item specified item.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return image list item created from item and image.
		 */
		public static <E> ImageListItem<E> create(E item, Image image, int iconSize, boolean storeImage) {
			return create(item, image, true, iconSize, storeImage);
		}
		
		/**
		 * Creating image list item from item and image.
		 * @param <E> item type.
		 * @param item specified item.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @return image list item created from item and image.
		 */
		public static <E> ImageListItem<E> create(E item, Image image, int iconSize) {
			return create(item, image, true, iconSize, false);
		}

		/**
		 * Creating image list item from item and image.
		 * @param <E> item type.
		 * @param item specified item.
		 * @param image specified image.
		 * @return image list item created from item and image.
		 */
		public static <E> ImageListItem<E> create(E item, Image image) {
			return create(item, image, false, 0, true);
		}

		/**
		 * Creating image list item from path.
		 * @param imagePath specified path.
		 * @param createIcon flag to indicate whether to create icon.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return image list item created from path.
		 */
		private static ImageListItem<Path> create(Path imagePath, boolean createIcon, int iconSize, boolean storeImage) {
			if (imagePath == null) return null;
			BufferedImage image = UIUtil.loadImage(imagePath);
			return create(imagePath, image, createIcon, iconSize, storeImage);
		}
		
		/**
		 * Creating image list item from path.
		 * @param imagePath specified path.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return image list item created from path.
		 */
		public static ImageListItem<Path> create(Path imagePath, int iconSize, boolean storeImage) {
			return create(imagePath, true, iconSize, storeImage);
		}
		
		/**
		 * Creating image list item from path.
		 * @param imagePath specified path.
		 * @param iconSize icon size.
		 * @return image list item created from path.
		 */
		public static ImageListItem<Path> create(Path imagePath, int iconSize) {
			return create(imagePath, true, iconSize, false);
		}

		/**
		 * Creating image list item from path.
		 * @param imagePath specified path.
		 * @return image list item created from path.
		 */
		public static ImageListItem<Path> create(Path imagePath) {
			return create(imagePath, false, 0, true);
		}

		/**
		 * Creating image list item from image and image name.
		 * @param imageName image name.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return image list item created from image.
		 */
		public static ImageListItem<String> create(String imageName, Image image, int iconSize, boolean storeImage) {
			return create(imageName, image, true, iconSize, storeImage);
		}

		/**
		 * Creating image list item from image and image name.
		 * @param imageName image name.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @return image list item created from image.
		 */
		public static ImageListItem<String> create(String imageName, Image image, int iconSize) {
			return create(imageName, image, true, iconSize, false);
		}

		/**
		 * Creating image list item from image and image name.
		 * @param imageName image name.
		 * @param image specified image.
		 * @return image list item created from image.
		 */
		public static ImageListItem<String> create(String imageName, Image image) {
			return create(imageName, image, false, 0, true);
		}

		/**
		 * Creating image list item from image name.
		 * @param imageName image name.
		 * @return image list item created from image name.
		 */
		public static ImageListItem<String> create(String imageName) {
			return create(imageName, null, false, 0, false);
		}

		/**
		 * Creating image list item from image.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @param storeImage flag to indicate whether to store image.
		 * @return image list item created from image.
		 */
		public static ImageListItem<String> create(Image image, int iconSize, boolean storeImage) {
			return create("", image, true, iconSize, storeImage);
		}

		/**
		 * Creating image list item from image.
		 * @param image specified image.
		 * @param iconSize icon size.
		 * @return image list item created from image.
		 */
		public static ImageListItem<String> create(Image image, int iconSize) {
			return create("", image, true, iconSize, false);
		}

		/**
		 * Creating image list item from image.
		 * @param image specified image.
		 * @return image list item created from image.
		 */
		public static ImageListItem<String> create(Image image) {
			return create("", image, false, 0, true);
		}

	}


}



/**
 * This class is the render that guides how to show image items in image list.
 * 
 * @param <E> type of the attached object.
 * @author Loc Nguyen
 * @version 1.0
 */
class ImageListRenderer<T extends ImageListItem<?>> extends DefaultListCellRenderer {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	@Override
	public Component getListCellRendererComponent(
		JList<?> list, Object value,
		int index, boolean isSelected, boolean cellHasFocus) {
		Component comp = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
		
		if (value instanceof ImageListItem<?>) {
			Icon icon = ((ImageListItem<?>)value).queryIcon();
			setIcon(icon);
	        setVerticalTextPosition(JLabel.CENTER);
		}
		
		return comp;
	}

	
}



/**
 * This class is the render that guides how to show image items in image list.
 * 
 * @param <E> type of the attached object.
 * @author <a href = "https://stackoverflow.com/questions/22266506/how-to-add-image-in-jlist">Paul Samsotha</a>
 * @version 1.0
 */
@Deprecated
class ImageListRenderer2<E> extends JLabel implements ListCellRenderer<ImageListItem<E>> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public ImageListRenderer2() {
		super();
	}


	@Override
	public Component getListCellRendererComponent(
			JList<? extends ImageListItem<E>> list, ImageListItem<E> value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		setEnabled(list.isEnabled());
		setFont(list.getFont());
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
		
		Icon icon = value.queryIcon();
		if (icon != null) {
			setIcon(icon);
	        setVerticalTextPosition(JLabel.CENTER);
		}
		setText(value.toString());
		
		return this;
	}
	
	
}
