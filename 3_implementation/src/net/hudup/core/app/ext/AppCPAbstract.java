/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app.ext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JMenuBar;
import javax.swing.WindowConstants;

import net.hudup.core.client.ConnectInfo;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class is abstract implementation of control panel for application.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class AppCPAbstract extends JDialog implements AppCP, AppListener {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Extensive application.
	 */
	protected AppExt app;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo;
	
	
	/**
	 * Constructor with extensive application, connection information, and parent component.
	 * @param app extensive application.
	 * @param connectInfo connection information.
	 * @param comp parent component.
	 */
	protected AppCPAbstract(AppExt app, ConnectInfo connectInfo, Component comp) {
		super(UIUtil.getDialogForComponent(comp), "Application control panel", true);
		this.connectInfo = (connectInfo != null ? connectInfo : new ConnectInfo());
		this.app = app;

		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());
		
		JMenuBar mnuBar = createMenuBar();
	    if (mnuBar != null) setJMenuBar(mnuBar);

	    initUI();
	    
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
			}
			
		});
		
	}

	
	/**
	 * Creating main menu bar.
	 * @return main menu bar.
	 */
	protected JMenuBar createMenuBar() {
		return null;
//		JMenuBar mnBar = new JMenuBar();
//		
//		JMenu mnFile = new JMenu(I18nUtil.message("file"));
//		mnFile.setMnemonic('f');
//		mnBar.add(mnFile);
//
//		JMenuItem mniSaveScript = new JMenuItem(
//			new AbstractAction(I18nUtil.message("save_script")) {
//				
//				/**
//				 * Serial version UID for serializable class. 
//				 */
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				public void actionPerformed(ActionEvent e) {
//
//				}
//				
//			});
//		mniSaveScript.setMnemonic('s');
//		mniSaveScript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
//		mnFile.add(mniSaveScript);
//		
//		return mnBar;
	}

	
	/**
	 * Initialize user interface.
	 */
	protected abstract void initUI();

	
}
