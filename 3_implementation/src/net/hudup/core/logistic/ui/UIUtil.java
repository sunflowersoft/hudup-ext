/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;

/**
 * This utility class provides utility methods relevant to user interface (UI) such as getting image from URI, creating button, and creating menu item.
 *  
 * @author Loc Nguyen
 * @version 10.0
 */
public final class UIUtil {

	
	/**
	 * Retrieving URL of an image from image name.
	 * The directory of such image is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param imageName image name.
	 * @return URL of an image from image name.
	 */
	private static URL getImageUrl(String imageName) {
		if (imageName == null || imageName.isEmpty())
			return null;
		
		URL imageURL = null;
		try {
			String path = Constants.IMAGES_PACKAGE;
			if (!path.endsWith("/"))
				path = path + "/";
			path = path + imageName;
			imageURL = UIUtil.class.getResource(path);
		}
		catch (Exception e) {
			LogUtil.trace(e);
			imageURL = null;
		}
		
		return imageURL;
	}
	
	
	/**
	 * Creating an image from image name.
	 * The directory of such image is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param imageName image name.
	 * @return image from image name.
	 */
	public static Image getImage(String imageName) {
		URL url = getImageUrl(imageName);
		if (url == null)
			return null;
		
		try {
			return Toolkit.getDefaultToolkit().getImage(url);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Creating an icon from icon name.
	 * The directory of such icon is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param iconName icon name.
	 * @param alt alternative name.
	 * @return icon from icon name.
	 */
	public static ImageIcon getImageIcon(String iconName, String alt) {
		if (alt == null)
			return new ImageIcon(getImageUrl(iconName));
		else
			return new ImageIcon(getImageUrl(iconName), alt);
	}
	
	
	/**
	 * Creating an icon button.
	 * @param iconName icon file name.
	 * The directory of icon is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param cmd action command for this button.
	 * @param tooltip tool-tip for this button.
	 * @param alt alternative text for this button.
	 * @param listener action listener for this button.
	 * @return {@link JButton} with icon.
	 */
	public static JButton makeIconButton(String iconName, String cmd, String tooltip, String alt, 
			ActionListener listener) {
		
		
		return makeIconButton(getImageUrl(iconName), cmd, tooltip, alt, listener);
	}
	
	
	/**
	 * Creating an icon button.
	 * @param iconURL {@link URL} of icon. URL, abbreviation of Uniform Resource Locator, locates any resource on internet.
	 * @param cmd action command for this button.
	 * @param tooltip tool-tip for this button.
	 * @param alt alternative text for this button.
	 * @param listener action listener for this button.
	 * @return {@link JButton} with icon.
	 */
	private static JButton makeIconButton(URL iconURL, String cmd, String tooltip, String alt, 
			ActionListener listener) {
		JButton button = new JButton();
	    button.setActionCommand(cmd);
	    button.setToolTipText(tooltip);
	    button.addActionListener(listener);
	
	    try {
		    if (iconURL != null) {
		        button.setIcon(new ImageIcon(iconURL, alt));
		    }
		    else {  //no image found
		        button.setText(alt);
		    }
	    }
	    catch (Exception e) {
	    	LogUtil.trace(e);
	    }
	    
	    return button;
	}

	
	/**
	 * Creating a check-box menu item with icon.
	 * @param iconName icon file name.
	 * The directory of icon is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param text text of menu item.
	 * @param listener action listener for this menu item.
	 * @return {@link JCheckBoxMenuItem} with icon.
	 */
	public static JCheckBoxMenuItem makeCheckBoxMenuItem(String iconName, String text, ActionListener listener) {
		return (JCheckBoxMenuItem)makeMenuItem(getImageUrl(iconName), text, listener, true);
	}

	
	/**
	 * Creating a menu item with icon.
	 * @param iconName icon file name.
	 * The directory of icon is specified by constants {@link Constants#IMAGES_PACKAGE}.
	 * @param text text of menu item.
	 * @param listener action listener for this menu item.
	 * @return {@link JMenuItem} with icon.
	 */
	public static JMenuItem makeMenuItem(String iconName, String text, ActionListener listener) {
		return makeMenuItem(getImageUrl(iconName), text, listener, false);
	}
	
	
	/**
	 * Creating a menu item with icon.
	 * @param iconURL {@link URL} of icon. URL, abbreviation of Uniform Resource Locator, locates any resource on Internet.
	 * @param text text of menu item.
	 * @param listener action listener for this menu item.
	 * @param isCheckbox true if this is check-box menu item.
	 * @return {@link JMenuItem} with icon.
	 */
	private static JMenuItem makeMenuItem(URL iconURL, String text, ActionListener listener, boolean isCheckbox) {
		JMenuItem item = null;
		if (isCheckbox)
			item = new JCheckBoxMenuItem(text);
		else
			item = new JMenuItem(text);
		
		item.addActionListener(listener);
		
		try {
		    if (iconURL != null) {
		        item.setIcon(new ImageIcon(iconURL, text));
		    }
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
	    return item;
	}

	
	/**
	 * Getting the parent window {@link Window} of the specified component.
	 * @param comp specified component.
	 * @return {@link Window} of specified component. The method return {@code null} if the specified component has no parent {@link Window}.
	 */
	public static Window getWindowForComponent(Component comp) {
	    if (comp == null)
	        return null;
	    if (comp instanceof Frame || comp instanceof Dialog)
	        return (Window)comp;
	    else if (comp instanceof Window)
	        return (Window)comp;
	    else
	    	return getWindowForComponent(comp.getParent());
	}

	
	/**
	 * Getting the parent frame of the specified component.
	 * @param comp specified component.
	 * @return {@link Frame} of the specified component. The method return {@code null} if the specified component has no parent Frame.
	 */
	public static Frame getFrameForComponent(Component comp) {
	    if (comp == null)
	        return null;
	    else if (comp instanceof Frame)
	        return (Frame)comp;
	    else
	    	return getFrameForComponent(comp.getParent());
	}

	
	/**
	 * Getting the parent dialog of the specified component.
	 * @param comp specified component.
	 * @return {@link Dialog} of the specified component. The method return {@code null} if the specified component has no parent dialog.
	 */
	public static Dialog getDialogForComponent(Component comp) {
	    if (comp == null)
	        return null;
	    else if (comp instanceof Dialog)
	        return (Dialog)comp;
	    else
	    	return getDialogForComponent(comp.getParent());
	}

	
//	/**
//	 * Getting parent dialog, frame, or window of the specified component.
//	 * @param comp  specified component.
//	 * @return parent dialog, frame, or window of the specified component.
//	 */
//	public static Window getDialogOrFrameOrWindowForComponent(Component comp) {
//		Dialog dlgParent = getDialogForComponent(comp);
//		Frame frmParent = getFrameForComponent(comp);
//		if (dlgParent == null && frmParent == null) return getWindowForComponent(comp);
//		if (dlgParent != null && frmParent == null) return dlgParent;
//		if (dlgParent == null && frmParent != null) return frmParent;
//		
//	}
	
	
	/**
	 * Setting UI look and feel randomly. 
	 */
	public static void randomLookAndFeel() {
		if (GraphicsEnvironment.isHeadless()) return;
		
		boolean lfRnd = false;
		try {
			String lfText = Util.getHudupProperty("look_and_feel_random");
			if (lfText != null && !lfText.isEmpty())
				lfRnd = Boolean.parseBoolean(lfText);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			lfRnd = false;
		}
		if (!lfRnd) return;
		
		LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();

		Random rnd = new Random();
		int index = rnd.nextInt(looks.length);
		
		try {
			UIManager.setLookAndFeel(looks[index].getClassName());
			LogUtil.info("Look and feel used: " + UIManager.getLookAndFeel().getName());
		}
		catch (Throwable e) {
			LogUtil.info("Look and feel '" + looks[index].getClassName() + "' not supported");
		}
	}
	
	
	/**
	 * Getting image from path.
	 * @param path specified path.
	 * @return image loaded from path.
	 */
	public static BufferedImage loadImage(Path path) {
		try {
			InputStream is = Files.newInputStream(path);
			BufferedImage image = ImageIO.read(is);
			is.close();
			
			return image;
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		return null;
	}

	
	/**
	 * Resize image.
	 * @param image specific image.
	 * @param newWidth new width.
	 * @param newHeight new height.
	 * @return resized image.
	 */
	public static BufferedImage resizeImage(BufferedImage image, int newWidth, int newHeight) {
		if (image == null || newWidth <= 0 || newHeight <= 0)
			return null;
		else if (image.getWidth() != newWidth || image.getHeight() != newHeight) {
			int sourceImageType = image.getType();
			Image resizedImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_DEFAULT);
			if (resizedImage == null) return null;
			
			image = convertToSourceTypeImage(resizedImage, sourceImageType);
			if (image == null)
				return null;
			//else if (image.getWidth() != newWidth || image.getHeight() != newHeight)
			//	return null;
			else
				return image;
		}
		else
			return image;
	}


	/**
	 * Converting image to source type image. The code is available at https://stackoverflow.com/questions/13605248/java-converting-image-to-bufferedimage 
	 * @param image specific image.
	 * @param sourceImageType source image type.
	 * @return buffered image.
	 */
	private static BufferedImage convertToSourceTypeImage(Image image, int sourceImageType) {
		if (image == null) return null;
		
		if (image instanceof BufferedImage) {
			BufferedImage bufferedImage = (BufferedImage)image;
			if (bufferedImage.getType() == sourceImageType) return bufferedImage;
		}
	
		BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), sourceImageType);
	    Graphics2D g = bufferedImage.createGraphics();
	    g.drawImage(image, 0, 0, null);
	    g.dispose();
	
	    return bufferedImage;
	}


}
