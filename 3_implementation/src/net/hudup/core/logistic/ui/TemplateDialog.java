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
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.hudup.core.Util;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is a dialog template.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class TemplateDialog extends JDialog implements ProgressListener {

	
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
	 * Up buttons of body.
	 */
	protected JPanel bodyUpButtons;
	
	
	/**
	 * Down buttons of body.
	 */
	protected JPanel bodyDownButtons;

	
	/**
	 * Footer panel.
	 */
	protected JPanel footer;

	
	/**
	 * Panel of last footer buttons.
	 */
	protected JPanel footerButtons;
	
	
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
	 * Progress bar of a ongoing process.
	 */
	protected JProgressBar prgRunning;

	
	/**
	 * Status bar.
	 */
	protected StatusBar statusBar;
	
	
	/**
	 * Task runner.
	 */
	protected AbstractRunner runner = null;
	
	
	/**
	 * Constructor with parent component and title.
	 * @param comp parent component
	 * @param title specified title.
	 */
	public TemplateDialog(Component comp, String title) {
		super(UIUtil.getDialogForComponent(comp), title, true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(UIUtil.getWindowForComponent(comp));
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
		
	    header = createHeader();
		if (header != null) add(header, BorderLayout.NORTH);

		toolBar = createToolBar();
		if (toolBar != null && header != null) header.add(toolBar, BorderLayout.NORTH);

		body = createBody();
		if (body != null) add(body, BorderLayout.CENTER);

		bodyUpButtons = createUpButtons();
		if (bodyUpButtons != null && body != null) body.add(bodyUpButtons, BorderLayout.NORTH);

		bodyDownButtons = createDownButtons();
		if (bodyDownButtons != null && body != null) body.add(bodyDownButtons, BorderLayout.SOUTH);

		footer = createFooter();
		if (footer != null) add(footer, BorderLayout.SOUTH);
		
		footerButtons = new JPanel();
		if (footerButtons != null && footer != null) footer.add(footerButtons, BorderLayout.NORTH);
		
		btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		if (btnOK != null && footerButtons != null) footerButtons.add(btnOK);
		
		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onApply();
			}
		});
		if (btnApply != null && footerButtons != null) footerButtons.add(btnApply);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		if (btnCancel != null && footerButtons != null) footerButtons.add(btnCancel);
		
		prgRunning = createProgressBar();
		if (prgRunning != null && footer != null) footer.add(prgRunning, BorderLayout.CENTER);

		statusBar = createStatusBar();
		if (statusBar != null && footer != null) footer.add(statusBar, BorderLayout.SOUTH);
		
		reset();
	}

	
	/**
	 * Getting this application.
	 * @return this application.
	 */
	private TemplateDialog getThisApp() {return this;}
	
	
	/**
	 * Create menu bar.
	 * @return menu bar.
	 */
	protected JMenuBar createMenuBar() {
		TemplateDialog thisApp = this;
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

		JMenuItem mniStopTask = new JMenuItem(
			new AbstractAction("Stop task") {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!isRunning()) {
						JOptionPane.showMessageDialog(getThisApp(), "No task running", "No task running", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					runner.forceStop();
					runner = null;
					updateControls();
				}
				
			});
		mniStopTask.setMnemonic('t');
		mniStopTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniStopTask);
			
		if (mnFile.getMenuComponentCount() > 0) mnFile.addSeparator();
		
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
		TemplateDialog thisApp = this;
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
     * Creating header.
     * @return header.
     */
    protected JPanel createHeader() {
    	return new JPanel(new BorderLayout());
    }

    
    /**
	 * Creating tool bar.
	 * @return tool bar.
	 */
	protected JToolBar createToolBar() {
		TemplateDialog thisApp = this;
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
	 * Creating main panel.
	 * @return main panel.
	 */
	protected JPanel createBody() {
		TemplatePanel body = new TemplatePanel() {
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onOK0() {
				getThisApp().onOK();
			}

			@Override
			protected void onApply0() {
				getThisApp().onApply();
			}

			@Override
			protected void onCancel0() {
				getThisApp().onCancel();
			}

		};
		
		if (body.toolBar0 != null) body.toolBar0.setVisible(false);
		if (body.footerButtons0 != null) body.footerButtons0.setVisible(false);
		if (body.prgRunning0 != null) body.prgRunning0.setVisible(false);
		if (body.statusBar0 != null) body.statusBar0.setVisible(false);
		return body;
	}
	
	
    /**
     * Creating up buttons.
     * @return up buttons.
     */
    protected JPanel createUpButtons() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		
		JButton btnConfig = UIUtil.makeIconButton(
			"config-16x16.png",
			"config", 
			"Configure generative model", 
			"Configure generative model", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(getThisApp(), "Configure", "Configure", JOptionPane.INFORMATION_MESSAGE);
				}
				
			});
		btnConfig.setMargin(new Insets(0, 0 , 0, 0));
		buttons.add(btnConfig);

		return buttons;
    }

    
    /**
     * Creating down buttons.
     * @return down buttons.
     */
    protected JPanel createDownButtons() {
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton btnStart = UIUtil.makeIconButton(
			"start-16x16.png",
			"start", 
			"Start task", 
			"Start task", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					doTask();
				}
				
			});
		btnStart.setMargin(new Insets(0, 0 , 0, 0));
		buttons.add(btnStart);

		return buttons;
    }

    
    /**
     * Creating footer.
     * @return footer.
     */
    protected JPanel createFooter() {
    	return new JPanel(new BorderLayout());    	
    }

    
    /**
     * Creating progress bar.
     * @return progress bar.
     */
    protected JProgressBar createProgressBar() {
    	JProgressBar prgRunning = new JProgressBar();
		prgRunning.setStringPainted(true);
		prgRunning.setToolTipText("Running progress");
		prgRunning.setValue(0);
		return prgRunning;
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
	public void receiveProgress(ProgressEvent evt) throws RemoteException {
		if (this.prgRunning == null) return;
		
		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		
		this.prgRunning.setMaximum(progressTotal);
		if (this.prgRunning.getValue() < progressStep) 
			this.prgRunning.setValue(progressStep);
		
		System.out.println(evt.getMsg());
	}


	/**
	 * Enabling controls
	 * @param enabled enabling flag.
	 */
	protected void enableControls(boolean enabled) {
		if (toolBar != null ) {
			toolBar.setEnabled(enabled);
			if (toolBar instanceof MovingToolbar) ((MovingToolbar)toolBar).enableButtons(enabled);
		}
		if (body != null && body instanceof TemplatePanel) ((TemplatePanel)body).enableControls(enabled);
		if (btnOK != null) btnOK.setEnabled(enabled);
		if (btnApply != null) btnApply.setEnabled(enabled);
		if (btnCancel != null) btnCancel.setEnabled(enabled);
		if (prgRunning != null) prgRunning.setEnabled(enabled);
	}
	
	
	/**
	 * Updating controls.
	 */
	protected void updateControls() {
		enableControls(!isRunning());
		
		if (body != null && body instanceof TemplatePanel) {
			TemplatePanel templatePanel = (TemplatePanel)body;
			if (templatePanel.toolBar0 != null) templatePanel.toolBar0.setVisible(false);
			if (templatePanel.bodyUpButtons0 != null) templatePanel.bodyUpButtons0.setVisible(false);
			if (templatePanel.bodyDownButtons0 != null) templatePanel.bodyDownButtons0.setVisible(false);
			if (templatePanel.footerButtons0 != null) templatePanel.footerButtons0.setVisible(false);
			if (templatePanel.prgRunning0 != null) templatePanel.prgRunning0.setVisible(false);
			if (templatePanel.statusBar0 != null) templatePanel.statusBar0.setVisible(false);
		}
		
		if (prgRunning != null) prgRunning.setEnabled(isRunning());
		
		if (statusBar != null) statusBar.setVisible(false);
	}

	
	/*
	 * Doing some finalizing tasks here.
	 */
	@Override
	public void dispose() {
		super.dispose();
		
		if (runner != null) {
			runner.stop();
			runner = null;
		}
	}


	/**
	 * Resetting.
	 */
	protected void reset() {
		if (body != null && body instanceof TemplatePanel) ((TemplatePanel)body).reset();
		
		if (prgRunning != null) {
			prgRunning.setMaximum(0);
			prgRunning.setValue(0);
			prgRunning.setVisible(false);
		}
		
		if (runner != null) {
			runner.stop();
			runner = null;
		}
		
		updateControls();
	}

	
	/**
	 * Checking whether some task is running.
	 * @return whether some task is running.
	 */
	protected boolean isRunning() {
		return runner != null && runner.isStarted();
	}

	
	/**
	 * Doing main task.
	 */
	protected void doTask() {
		if (body != null && body instanceof TemplatePanel) {
			TemplatePanel templatePanel = (TemplatePanel)body;
			if (templatePanel.runner0 != null && templatePanel.runner0.isRunning()) templatePanel.runner0.stop();
		}
		
		if (runner != null && runner.isRunning()) runner.stop();
		
		runner = new AbstractRunner() {
			
			@Override
			protected void task() {
				updateControls();
				taskDefined();
				
				thread = null;
				if (paused) {
					paused = false;
					notifyAll();
				}
			}
			
			@Override
			protected void clear() {
				updateControls();
				JOptionPane.showMessageDialog(getThisApp(), "Done", "Done", JOptionPane.INFORMATION_MESSAGE);
			}
			
		};
		runner.start();
	}

	
	/**
	 * Customized task. Derived class overrides this method.
	 */
	protected void taskDefined() {
		if (prgRunning != null) {
			prgRunning.setValue(0);
			prgRunning.setVisible(true);
		}
		
		for (int time = 0; time < 10; time++) {
			try {
				receiveProgress(new ProgressEvent(this, 10, time+1, "Task defined"));
				Thread.sleep(1000);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		if (prgRunning != null) prgRunning.setVisible(false);
	}
	

	/**
	 * Show dialog.
	 * @param comp parent component
	 * @param title specified title.
	 */
	protected static void example(Component comp, String title) {
		new TemplateDialog(comp, title).setVisible(true);
	}
	
	
	/**
	 * Main method.
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		TemplateDialog.example(null, "Template dialog 1");
		
		TemplateDialog templateDlg = new TemplateDialog(null, "Template dialog 2 with table");
		TemplateTableModel templateTableModel = new TemplateTableModel() {
			
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
		TemplateTable templateTable = new TemplateTable(templateTableModel);
		templateTable.update((Object)null);
		
		if (templateDlg.body instanceof TemplatePanel) {
			TemplatePanel templatePanel = (TemplatePanel)templateDlg.body;
			if (templatePanel.table0 != null) templatePanel.body0.remove(templatePanel.table0);
			templatePanel.body0.add(new JScrollPane(templateTable), BorderLayout.CENTER);
		}
		templateDlg.setVisible(true);
		
		TemplateFrame.example("Frame");
	}
	
	
}



/**
 * This class is a frame template.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class TemplateFrame extends JFrame implements ProgressListener {

	
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
	 * Up buttons of body.
	 */
	protected JPanel bodyUpButtons;
	
	
	/**
	 * Down buttons of body.
	 */
	protected JPanel bodyDownButtons;

	
	/**
	 * Footer panel.
	 */
	protected JPanel footer;

	
	/**
	 * Panel of last footer buttons.
	 */
	protected JPanel footerButtons;
	
	
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
	 * Progress bar of a ongoing process.
	 */
	protected JProgressBar prgRunning;

	
	/**
	 * Status bar.
	 */
	protected StatusBar statusBar;
	
	
	/**
	 * Task runner.
	 */
	protected AbstractRunner runner = null;
	
	
	/**
	 * Constructor with parent component and title.
	 * @param comp parent component
	 * @param title specified title.
	 */
	public TemplateFrame(String title) {
		super(title);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(800, 600);
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
		
	    header = createHeader();
		if (header != null) add(header, BorderLayout.NORTH);

		toolBar = createToolBar();
		if (toolBar != null && header != null) header.add(toolBar, BorderLayout.NORTH);

		body = createBody();
		if (body != null) add(body, BorderLayout.CENTER);
		
		bodyUpButtons = createUpButtons();
		if (bodyUpButtons != null && body != null) body.add(bodyUpButtons, BorderLayout.NORTH);

		bodyDownButtons = createDownButtons();
		if (bodyDownButtons != null && body != null) body.add(bodyDownButtons, BorderLayout.SOUTH);

		footer = createFooter();
		if (footer != null) add(footer, BorderLayout.SOUTH);
		
		footerButtons = new JPanel();
		if (footerButtons != null && footer != null) footer.add(footerButtons, BorderLayout.NORTH);
		
		btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		if (btnOK != null && footerButtons != null) footerButtons.add(btnOK);
		
		btnApply = new JButton("Apply");
		btnApply.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onApply();
			}
		});
		if (btnApply != null && footerButtons != null) footerButtons.add(btnApply);

		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel();
			}
		});
		if (btnCancel != null && footerButtons != null) footerButtons.add(btnCancel);
		
		prgRunning = createProgressBar();
		if (prgRunning != null && footer != null) footer.add(prgRunning, BorderLayout.CENTER);

		statusBar = createStatusBar();
		if (statusBar != null && footer != null) footer.add(statusBar, BorderLayout.SOUTH);
		
		reset();
	}

	
	/**
	 * Getting this application.
	 * @return this application.
	 */
	private TemplateFrame getThisApp() {return this;}

	
	/**
	 * Create menu bar.
	 * @return menu bar.
	 */
	protected JMenuBar createMenuBar() {
		TemplateFrame thisApp = this;
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

		JMenuItem mniStopTask = new JMenuItem(
			new AbstractAction("Stop task") {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					if (!isRunning()) {
						JOptionPane.showMessageDialog(getThisApp(), "No task running", "No task running", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					runner.forceStop();
					runner = null;
					updateControls();
				}
				
			});
		mniStopTask.setMnemonic('t');
		mniStopTask.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniStopTask);
		
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
		TemplateFrame thisApp = this;
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
     * Creating header.
     * @return header.
     */
    protected JPanel createHeader() {
    	return new JPanel(new BorderLayout());
    }

    
    /**
	 * Creating tool bar.
	 * @return tool bar.
	 */
	protected JToolBar createToolBar() {
		TemplateFrame thisApp = this;
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
	 * Creating main panel.
	 * @return main panel.
	 */
	protected JPanel createBody() {
		TemplatePanel body = new TemplatePanel() {
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onOK0() {
				getThisApp().onOK();
			}

			@Override
			protected void onApply0() {
				getThisApp().onApply();
			}

			@Override
			protected void onCancel0() {
				getThisApp().onCancel();
			}

		};
		
		return body;
	}
    

    /**
     * Creating up buttons.
     * @return up buttons.
     */
    protected JPanel createUpButtons() {
		JPanel buttons = new JPanel();
		buttons.setLayout(new BoxLayout(buttons, BoxLayout.X_AXIS));
		
		JButton btnConfig = UIUtil.makeIconButton(
			"config-16x16.png",
			"config", 
			"Configure generative model", 
			"Configure generative model", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(getThisApp(), "Configure", "Configure", JOptionPane.INFORMATION_MESSAGE);
				}
				
			});
		btnConfig.setMargin(new Insets(0, 0 , 0, 0));
		buttons.add(btnConfig);

		return buttons;
    }

    
    /**
     * Creating down buttons.
     * @return down buttons.
     */
    protected JPanel createDownButtons() {
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton btnStart = UIUtil.makeIconButton(
			"start-16x16.png",
			"start", 
			"Start task", 
			"Start task", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					doTask();
				}
				
			});
		btnStart.setMargin(new Insets(0, 0 , 0, 0));
		buttons.add(btnStart);

		return buttons;
    }

    
    /**
     * Creating footer.
     * @return footer.
     */
    protected JPanel createFooter() {
    	return new JPanel(new BorderLayout());    	
    }
    
    
    /**
     * Creating progress bar.
     * @return progress bar.
     */
    protected JProgressBar createProgressBar() {
    	JProgressBar prgRunning = new JProgressBar();
		prgRunning.setStringPainted(true);
		prgRunning.setToolTipText("Running progress");
		prgRunning.setValue(0);
		return prgRunning;
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
	public void receiveProgress(ProgressEvent evt) throws RemoteException {
		if (this.prgRunning == null) return;
		
		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		
		this.prgRunning.setMaximum(progressTotal);
		if (this.prgRunning.getValue() < progressStep) this.prgRunning.setValue(progressStep);
		
		System.out.println(evt.getMsg());
	}


	/**
	 * Enabling controls
	 * @param enabled enabling flag.
	 */
	protected void enableControls(boolean enabled) {
		if (toolBar != null ) {
			toolBar.setEnabled(enabled);
			if (toolBar instanceof MovingToolbar) ((MovingToolbar)toolBar).enableButtons(enabled);
		}
		if (body != null && body instanceof TemplatePanel) ((TemplatePanel)body).enableControls(enabled);
		if (btnOK != null) btnOK.setEnabled(enabled);
		if (btnApply != null) btnApply.setEnabled(enabled);
		if (btnCancel != null) btnCancel.setEnabled(enabled);
		if (prgRunning != null) prgRunning.setEnabled(enabled);
	}
	
	
	/**
	 * Updating controls.
	 */
	protected void updateControls() {
		enableControls(!isRunning());
		
		if (body != null && body instanceof TemplatePanel) {
			TemplatePanel templatePanel = (TemplatePanel)body;
			if (templatePanel.toolBar0 != null) templatePanel.toolBar0.setVisible(false);
			if (templatePanel.bodyUpButtons0 != null) templatePanel.bodyUpButtons0.setVisible(false);
			if (templatePanel.bodyDownButtons0 != null) templatePanel.bodyDownButtons0.setVisible(false);
			if (templatePanel.footerButtons0 != null) templatePanel.footerButtons0.setVisible(false);
			if (templatePanel.prgRunning0 != null) templatePanel.prgRunning0.setVisible(false);
			if (templatePanel.statusBar0 != null) templatePanel.statusBar0.setVisible(false);
		}
		
		if (prgRunning != null) prgRunning.setEnabled(isRunning());
		
		if (statusBar != null) statusBar.setVisible(false);
	}

	
	/*
	 * Doing some finalizing tasks here.
	 */
	@Override
	public void dispose() {
		super.dispose();
		
		if (runner != null) {
			runner.stop();
			runner = null;
		}
	}


	/**
	 * Resetting.
	 */
	protected void reset() {
		if (body != null && body instanceof TemplatePanel) ((TemplatePanel)body).reset();
		
		if (prgRunning != null) {
			prgRunning.setMaximum(0);
			prgRunning.setValue(0);
			prgRunning.setVisible(false);
		}
		
		if (runner != null) {
			runner.stop();
			runner = null;
		}
		
		updateControls();
	}

	
	/**
	 * Checking whether some task is running.
	 * @return whether some task is running.
	 */
	protected boolean isRunning() {
		return runner != null && runner.isStarted();
	}

	
	/**
	 * Doing main task.
	 */
	protected void doTask() {
		if (body != null && body instanceof TemplatePanel) {
			TemplatePanel templatePanel = (TemplatePanel)body;
			if (templatePanel.runner0 != null && templatePanel.runner0.isRunning()) templatePanel.runner0.stop();
		}
		
		if (runner != null && runner.isRunning()) runner.stop();
		
		runner = new AbstractRunner() {
			
			@Override
			protected void task() {
				updateControls();
				taskDefined();
				
				thread = null;
				if (paused) {
					paused = false;
					notifyAll();
				}
			}
			
			@Override
			protected void clear() {
				updateControls();
				JOptionPane.showMessageDialog(getThisApp(), "Done", "Done", JOptionPane.INFORMATION_MESSAGE);
			}
			
		};
		runner.start();
	}

	
	/**
	 * Customized task. Derived class overrides this method.
	 */
	protected void taskDefined() {
		if (prgRunning != null) {
			prgRunning.setValue(0);
			prgRunning.setVisible(true);
		}
		
		for (int time = 0; time < 10; time++) {
			try {
				receiveProgress(new ProgressEvent(this, 10, time+1, "Task defined"));
				Thread.sleep(1000);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		if (prgRunning != null) prgRunning.setVisible(false);
	}
	

	/**
	 * Show frame.
	 * @param title specified title.
	 */
	protected static void example(String title) {new TemplateFrame(title).setVisible(true);}
	
	
}
