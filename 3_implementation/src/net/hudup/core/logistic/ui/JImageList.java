/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.nio.file.Path;
import java.util.List;
import java.util.Vector;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import net.hudup.core.Util;

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
	 * Default constructor.
	 */
	public JImageList() {
		super();
		
		setCellRenderer(new ImageListRenderer<E>());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setLayoutOrientation(JList.HORIZONTAL_WRAP);
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {

			}
			
		});
	}
	
	
	/**
	 * Constructor with a specified list of attached objects. Each object is attached with an item.
	 * @param listData specified list of attached object.
	 */
	public JImageList(List<? extends E> listData) {
		this();
		setListData(listData);
	}

	
	/**
	 * Setting data for this check list.
	 * @param listData data list.
	 */
	public void setListData(List<? extends E> listData) {
		Vector<ImageListItem<E>> vector = Util.newVector();
		for (E element : listData) {
			vector.add(new ImageListItem<E>(element));
		}
		
		setListData(vector);
	}
	
	
}



/**
 * This class is the render that guides how to show image items in image list.
 * 
 * @param <E> type of the attached object.
 * @author <a href = "https://stackoverflow.com/questions/22266506/how-to-add-image-in-jlist">Paul Samsotha</a>
 * @version 1.0
 */
class ImageListRenderer<E> extends JLabel implements ListCellRenderer<ImageListItem<E>> {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public ImageListRenderer() {
		super();
	}


	@Override
	public Component getListCellRendererComponent(
			JList<? extends ImageListItem<E>> list, ImageListItem<E> value,
			int index, boolean isSelected, boolean cellHasFocus) {
		
		setEnabled(list.isEnabled());
		setFont(list.getFont());
		setBackground(list.getBackground());
		setForeground(list.getForeground());
		
		if (value.getIcon() != null) {
			setIcon(value.getIcon());
	        setHorizontalTextPosition(JLabel.BOTTOM);
		}
		else if (value.getImage() != null) {
			setIcon(new ImageIcon(value.getImage()));
	        setHorizontalTextPosition(JLabel.BOTTOM);
		}
		setText(value.toString());
		
		return this;
	}
	
	
}



/**
 * This class is a wrapper of an object attached of an image item in image list.
 * 
 * @param <E> type of attached object.
 * @author Loc Nguyen
 * @version 1.0
 * 
 */
class ImageListItem<E> {
	
	
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
	 * Constructor with the wrapped object (item), icon, and image.
	 * @param item specified object (item).
	 * @param icon specified icon.
	 * @param image specified image.
	 */
	protected ImageListItem(E item, Icon icon, Image image) {
		this.item = item;
		this.icon = icon;
		this.image = image;
	}
	
	
	/**
	 * Constructor with the wrapped object (item).
	 * @param item specified object (item).
	 */
	public ImageListItem(E item) {
		this(item, null, null);
	}
	
	
	/**
	 * Setting icon
	 * @param icon specified icon.
	 */
	public void setIcon(Icon icon) {
		this.icon = icon;
	}
	
	
	/**
	 * Getting icon.
	 * @return associated icon.
	 */
	public Icon getIcon() {
		return icon;
	}
	
	
	/**
	 * Setting image
	 * @param image specified image.
	 */
	public void setImage(Image image) {
		this.image = image;
	}
	
	
	/**
	 * Getting image.
	 * @return associated icon.
	 */
	public Image getImage() {
		return image;
	}

	
	/**
	 * Getting the internal object (item).
	 * @return internal object (item).
	 */
	public E getItem() {
		return item;
	}
	
	
	@Override
	public String toString() {
		if (item == null)
			return super.toString();
		else if (item instanceof Path) {
			Path fileName = ((Path)item).getFileName();
			return fileName != null ? fileName.toString() : item.toString();
		}
		else
			return item.toString();
	}
	
	
	/**
	 * Creating image list item from path.
	 * @param imagePath specified path.
	 * @param createIcon flag to indicate whether to create icon.
	 * @param iconSize icon size.
	 * @return image list item created from path.
	 */
	private ImageListItem<Path> create(Path imagePath, boolean createIcon, int iconSize) {
		if (imagePath == null) return null;
		BufferedImage image = UIUtil.loadImage(imagePath);
		if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) return null;
		
		if (!createIcon) return new ImageListItem<Path>(imagePath, null, image);
		iconSize = iconSize < ICON_MINSIZE ? ICON_MINSIZE : iconSize;
		if (iconSize <= 0 || (image.getWidth() <= iconSize && image.getHeight() <= iconSize))
			return new ImageListItem<Path>(imagePath, null, image);
		
		int ratio = image.getWidth() / iconSize;
		ratio = Math.max(ratio, image.getHeight() / iconSize);
		if (ratio == 1) return new ImageListItem<Path>(imagePath, null, image);
		int newWidth = image.getWidth() / ratio;
		newWidth = newWidth <= 0 ? image.getWidth() : newWidth;
		int newHeight = image.getHeight() / ratio;
		newHeight = newHeight <= 0 ? image.getHeight() : newHeight;
		
		image = UIUtil.resizeImage(image, newWidth, newHeight);
		return new ImageListItem<Path>(imagePath, new ImageIcon(image), null);
	}
	
	
	/**
	 * Creating image list item from path.
	 * @param imagePath specified path.
	 * @param iconSize icon size.
	 * @return image list item created from path.
	 */
	public ImageListItem<Path> create(Path imagePath, int iconSize) {
		return create(imagePath, true, iconSize);
	}
	
	
	/**
	 * Creating image list item from path.
	 * @param imagePath specified path.
	 * @param iconSize icon size.
	 * @return image list item created from path.
	 */
	public ImageListItem<Path> create(Path imagePath) {
		return create(imagePath, false, 0);
	}


}
