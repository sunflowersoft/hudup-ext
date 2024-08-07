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
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.hudup.core.Util;
import net.hudup.core.logistic.I18nUtil;

/**
 * This class is a dialog template.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class TempDialog extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Menu bar.
	 */
	protected JMenuBar menuBar;
	
	
	/**
	 * Tool bar.
	 */
	protected JToolBar toolBar;
	
	
	/**
	 * Context menu.
	 */
	protected JPopupMenu ctxMenu;
	
	
	/**
	 * Header panel.
	 */
	protected JPanel header;
	
	
	/**
	 * Body panel.
	 */
	protected JPanel body;

	
	/**
	 * Footer panel.
	 */
	protected JPanel footer;

	
	/**
	 * Panel of last footer buttons.
	 */
	protected JPanel lastFooterButtons;
	
	
	/**
	 * OK button.
	 */
	protected JButton btnOK;
	
	
	/**
	 * Applying button.
	 */
	protected JButton btnApply;

	
	/**
	 * Cancel button.
	 */
	protected JButton btnCancel;
	
	
	/**
	 * Constructor with parent component and title.
	 * @param comp parent component
	 * @param title specified title.
	 */
	public TempDialog(Component comp, String title) {
		super(UIUtil.getDialogForComponent(comp), title, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());

		menuBar = createMenuBar();
	    if (menuBar != null) setJMenuBar(menuBar);

	    addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}
		});

	    ctxMenu = createContextMenu();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					if(ctxMenu == null) return;
					addToContextMenu(ctxMenu);
					ctxMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					onDoubleClick();
				}
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				onKeyPressed(e);
			}
		});
		
		header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		toolBar = createToolBar();
		if (toolBar != null) header.add(toolBar, BorderLayout.NORTH);

		body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		lastFooterButtons = new JPanel();
		footer.add(lastFooterButtons, BorderLayout.SOUTH);
		
		btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		lastFooterButtons.add(btnOK);
		
		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onApply();
			}
		});
		lastFooterButtons.add(btnApply);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		lastFooterButtons.add(btnCancel);
	}

	
	/**
	 * Create menu bar.
	 * @return menu bar.
	 */
	protected JMenuBar createMenuBar() {
		TempDialog thisApp = this;
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnFile = new JMenu(I18nUtil.message("file"));
		mnFile.setMnemonic('f');
		mnBar.add(mnFile);
		
		JMenuItem mniRefresh = new JMenuItem(
			new AbstractAction(I18nUtil.message("refresh")) {
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisApp, "Refresh", "Refresh", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		mniRefresh.setMnemonic('r');
		mniRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnFile.add(mniRefresh);

		return mnBar.getMenuCount() > 0 ? mnBar : null;
	}

	
	/**
	 * Creating context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu() {
		TempDialog thisApp = this;
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem miConfig = new JMenuItem("Configure");
		miConfig.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisApp, "Configuration", "Configuration", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		ctxMenu.add(miConfig);
		
		return ctxMenu;
	}

	
    /**
     * Adding the context menu to this list.
     * @param contextMenu specified context menu.
     */
    protected void addToContextMenu(JPopupMenu contextMenu) {
    	
    }

    
    /**
	 * Creating tool bar.
	 * @return tool bar.
	 */
	protected JToolBar createToolBar() {
		TempDialog thisApp = this;
		return new MovingToolbar() {
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void moveFirst() {
				super.moveFirst();
				JOptionPane.showMessageDialog(thisApp, "Move first", "Move first", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void movePrevious() {
				super.movePrevious();
				JOptionPane.showMessageDialog(thisApp, "Move previous", "Move previous", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void moveNext() {
				super.moveNext();
				JOptionPane.showMessageDialog(thisApp, "Move next", "Move next", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void moveLast() {
				super.moveLast();
				JOptionPane.showMessageDialog(thisApp, "Move last", "Move last", JOptionPane.INFORMATION_MESSAGE);
			}
			
		};
	}
	
	
	/**
	 * Event-driven method for mouse double click.
	 */
	protected void onDoubleClick() {
		JOptionPane.showMessageDialog(this, "Double click", "Double click", JOptionPane.INFORMATION_MESSAGE);
	}

	
	/**
	 * Key pressed event method.
	 * @param e key event.
	 */
	protected void onKeyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			JOptionPane.showMessageDialog(this, "ENTER key pressed", "ENTER key pressed", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	
	/**
	 * Driven-event method for OK button.
	 */
	protected void onOK() {
		JOptionPane.showMessageDialog(this, "OK button pressed", "OK button pressed", JOptionPane.INFORMATION_MESSAGE);
		dispose();
	}
	
	
	/**
	 * Driven-event method for applying button.
	 */
	protected void onApply() {
		JOptionPane.showMessageDialog(this, "Applying button pressed", "Applying button pressed", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Driven-event method for Cancel button.
	 */
	protected void onCancel() {
		dispose();
	}
	
	
	@Override
	public void dispose() {
		super.dispose();
	}


	/**
	 * Show dialog.
	 * @param comp parent component
	 * @param title specified title.
	 */
	protected static void example(Component comp, String title) {
		new TempDialog(comp, title).setVisible(true);
	}
	
	
	/**
	 * Main method.
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		TempDialog.example(null, "Dialog");
		
		TempDialog tempDlg = new TempDialog(null, "Table");
		TempTableModel tempTableModel = new TempTableModel() {
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void update(Object parameter) {
				Vector<Vector<Object>> data = Util.newVector(0);
			
				Vector<Object> row = Util.newVector();
				row.add("attribute 1"); row.add("text 1"); row.add(1.0); row.add(true); data.add(row);
				
				row = Util.newVector();
				row.add("attribute 2"); row.add("text 2"); row.add(2.0); row.add(false); data.add(row);

				row = Util.newVector();
				row.add("attribute 3"); row.add("text 3"); row.add(3.0); row.add(true); data.add(row);

				setDataVector(data, toColumns());
				modified = false;
			}

			@Override
			protected Vector<String> toColumns() {
				return new Vector<>(Arrays.asList("attribute", "text", "value", "boolean"));
			}

			@Override
			public Class<?> getColumnClass(int columnIndex) {
				if (columnIndex == 3)
					return Boolean.class;
				else
					return super.getColumnClass(columnIndex);
			}
			
		};
		TempTable tempTable = new TempTable(tempTableModel);
		tempTable.update((Object)null);
		
		tempDlg.body.add(new JScrollPane(tempTable), BorderLayout.CENTER);
		tempDlg.setVisible(true);
		
		TempFrame.example("Frame");
	}
	
}



/**
 * This class is a frame template.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class TempFrame extends JFrame {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Menu bar.
	 */
	protected JMenuBar menuBar;
	
	
	/**
	 * Tool bar.
	 */
	protected JToolBar toolBar;
	
	
	/**
	 * Context menu.
	 */
	protected JPopupMenu ctxMenu;
	
	
	/**
	 * Header panel.
	 */
	protected JPanel header;
	
	
	/**
	 * Body panel.
	 */
	protected JPanel body;

	
	/**
	 * Footer panel.
	 */
	protected JPanel footer;

	
	/**
	 * Status bar.
	 */
	protected StatusBar statusBar;
	
	
	/**
	 * Constructor with parent component and title.
	 * @param comp parent component
	 * @param title specified title.
	 */
	public TempFrame(String title) {
		super(title);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null);
		setLayout(new BorderLayout());

		menuBar = createMenuBar();
	    if (menuBar != null) setJMenuBar(menuBar);

	    addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}

			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}
		});

	    ctxMenu = createContextMenu();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					if(ctxMenu == null) return;
					
					addToContextMenu(ctxMenu);
					ctxMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					onDoubleClick();
				}
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				onKeyPressed(e);
			}
		});
		
	    header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);

		toolBar = createToolBar();
		if (toolBar != null) header.add(toolBar, BorderLayout.NORTH);

		body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		statusBar = createStatusBar();
		footer.add(statusBar, BorderLayout.SOUTH);
	}

	
	/**
	 * Create menu bar.
	 * @return menu bar.
	 */
	protected JMenuBar createMenuBar() {
		TempFrame thisApp = this;
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnFile = new JMenu(I18nUtil.message("file"));
		mnFile.setMnemonic('f');
		mnBar.add(mnFile);
		
		JMenuItem mniRefresh = new JMenuItem(
			new AbstractAction(I18nUtil.message("refresh")) {
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisApp, "Refresh", "Refresh", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		mniRefresh.setMnemonic('r');
		mniRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnFile.add(mniRefresh);

		mnFile.addSeparator();
		
		JMenuItem mniExit = new JMenuItem(
			new AbstractAction(I18nUtil.message("exit")) {
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			});
		mniExit.setMnemonic('x');
		mniExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
		mnFile.add(mniExit);
		
		return mnBar.getMenuCount() > 0 ? mnBar : null;
	}

	
	/**
	 * Creating context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu() {
		TempFrame thisApp = this;
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem miConfig = new JMenuItem("Configure");
		miConfig.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisApp, "Configuration", "Configuration", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		ctxMenu.add(miConfig);
		
		return ctxMenu;
	}

	
    /**
     * Adding the context menu to this list.
     * @param contextMenu specified context menu.
     */
    protected void addToContextMenu(JPopupMenu contextMenu) {
    	
    }

    
    /**
	 * Creating tool bar.
	 * @return tool bar.
	 */
	protected JToolBar createToolBar() {
		TempFrame thisApp = this;
		return new MovingToolbar() {
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void moveFirst() {
				super.moveFirst();
				JOptionPane.showMessageDialog(thisApp, "Move first", "Move first", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void movePrevious() {
				super.movePrevious();
				JOptionPane.showMessageDialog(thisApp, "Move previous", "Move previous", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void moveNext() {
				super.moveNext();
				JOptionPane.showMessageDialog(thisApp, "Move next", "Move next", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void moveLast() {
				super.moveLast();
				JOptionPane.showMessageDialog(thisApp, "Move last", "Move last", JOptionPane.INFORMATION_MESSAGE);
			}
			
		};
	}

	
	/**
	 * Creating status bar.
	 * @return status bar.
	 */
	protected StatusBar createStatusBar() {
		return new StatusBar();
	}
	
	
	/**
	 * Event-driven method for mouse double click.
	 */
	protected void onDoubleClick() {
		JOptionPane.showMessageDialog(this, "Double click", "Double click", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Key pressed event method.
	 * @param e key event.
	 */
	protected void onKeyPressed(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			JOptionPane.showMessageDialog(this, "ENTER key pressed", "ENTER key pressed", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	
	@Override
	public void dispose() {
		super.dispose();
	}


	/**
	 * Show dialog.
	 * @param title specified title.
	 */
	protected static void example(String title) {
		new TempFrame(title).setVisible(true);
	}
	
	
}
