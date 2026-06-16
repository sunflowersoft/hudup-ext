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
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is a panel template.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class TemplatePanel extends JPanel implements ProgressListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Tool bar.
	 */
	protected JToolBar toolBar0;
	
	
	/**
	 * Context menu.
	 */
	protected JPopupMenu ctxMenu0;
	
	
	/**
	 * Header panel.
	 */
	protected JPanel header0;
	
	
	/**
	 * Body panel.
	 */
	protected JPanel body0;

	
	/**
	 * Table.
	 */
	protected JTable table0;

	
	/**
	 * Up buttons of body.
	 */
	protected JPanel bodyUpButtons0;
	
	
	/**
	 * Down buttons of body.
	 */
	protected JPanel bodyDownButtons0;

	
	/**
	 * Footer panel.
	 */
	protected JPanel footer0;

	
	/**
	 * Panel of last footer buttons.
	 */
	protected JPanel footerButtons0;
	
	
	/**
	 * OK button.
	 */
	protected JButton btnOK0;
	
	
	/**
	 * Applying button.
	 */
	protected JButton btnApply0;

	
	/**
	 * Cancel button.
	 */
	protected JButton btnCancel0;
	
	
	/**
	 * Progress bar of a ongoing process.
	 */
	protected JProgressBar prgRunning0;

	
	/**
	 * Status bar.
	 */
	protected StatusBar statusBar0;

	
	/**
	 * Task runner.
	 */
	protected AbstractRunner runner0 = null;
	
	
	/**
	 * Default constructor.
	 */
	public TemplatePanel() {
		super();
		setLayout(new BorderLayout());
		
		ctxMenu0 = createContextMenu0();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					if(ctxMenu0 == null) return;
					addToContextMenu0(ctxMenu0);
					ctxMenu0.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					onDoubleClick0();
				}
			}
		});

		addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				onKeyPressed0(e);
			}
		});
		
		header0 = createHeader0();
		if (header0 != null) add(header0, BorderLayout.NORTH);
		
		toolBar0 = createToolBar0();
		if (toolBar0 != null && header0 != null) header0.add(toolBar0, BorderLayout.NORTH);

		body0 = createBody0();
		if (body0 != null) add(body0, BorderLayout.CENTER);
		
		bodyUpButtons0 = createUpButtons0();
		if (bodyUpButtons0 != null && body0 != null) body0.add(bodyUpButtons0, BorderLayout.NORTH);

    	table0 = createTable0();
		if (table0 != null && body0 != null) body0.add(new JScrollPane(table0), BorderLayout.CENTER);

		bodyDownButtons0 = createDownButtons0();
		if (bodyDownButtons0 != null && body0 != null) body0.add(bodyDownButtons0, BorderLayout.SOUTH);

		footer0 = createFooter0();
		if (footer0 != null) add(footer0, BorderLayout.SOUTH);
		
		footerButtons0 = new JPanel();
		if (footerButtons0 != null && footer0 != null) footer0.add(footerButtons0, BorderLayout.NORTH);
		
		btnOK0 = new JButton("OK");
		btnOK0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK0();
			}
		});
		if (btnOK0 != null && footerButtons0 != null) footerButtons0.add(btnOK0);
		
		btnApply0 = new JButton("Apply");
		btnApply0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onApply0();
			}
		});
		if (btnApply0 != null && footerButtons0 != null) footerButtons0.add(btnApply0);

		btnCancel0 = new JButton("Cancel");
		btnCancel0.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onCancel0();
			}
		});
		if (btnCancel0 != null && footerButtons0 != null) footerButtons0.add(btnCancel0);
		
		prgRunning0 = createProgressBar0();
		if (prgRunning0 != null && footer0 != null) footer0.add(prgRunning0, BorderLayout.CENTER);

		statusBar0 = createStatusBar0();
		if (statusBar0 != null && footer0 != null) footer0.add(statusBar0, BorderLayout.SOUTH);
		
		reset();
	}


	/**
	 * Getting this template panel.
	 * @return this template panel.
	 */
	private TemplatePanel getThisApp() {return this;}
	
	
	/**
	 * Creating context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu0() {
		TemplatePanel thisPanel = this;
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem miConfig = new JMenuItem("Configure (panel)");
		miConfig.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisPanel, "Configuration (panel)", "Configuration (panel)", JOptionPane.INFORMATION_MESSAGE);
				}
			});
		ctxMenu.add(miConfig);
		
		return ctxMenu;
	}

	
    /**
     * Adding the context menu to this list.
     * @param contextMenu specified context menu.
     */
    protected void addToContextMenu0(JPopupMenu contextMenu) {
    	
    }

    
    /**
     * Creating header.
     * @return header.
     */
    protected JPanel createHeader0() {
    	return new JPanel(new BorderLayout());
    }
    
    
    /**
	 * Creating tool bar.
	 * @return tool bar.
	 */
	protected JToolBar createToolBar0() {
		TemplatePanel thisPanel = this;
		return new MovingToolbar() {
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void moveFirst() {
				super.moveFirst();
				JOptionPane.showMessageDialog(thisPanel, "Move first", "Move first", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void movePrevious() {
				super.movePrevious();
				JOptionPane.showMessageDialog(thisPanel, "Move previous", "Move previous", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void moveNext() {
				super.moveNext();
				JOptionPane.showMessageDialog(thisPanel, "Move next", "Move next", JOptionPane.INFORMATION_MESSAGE);
			}

			@Override
			public void moveLast() {
				super.moveLast();
				JOptionPane.showMessageDialog(thisPanel, "Move last", "Move last", JOptionPane.INFORMATION_MESSAGE);
			}
			
		};
	}
	
	
    /**
     * Creating body.
     * @return body.
     */
    protected JPanel createBody0() {
    	JPanel body = new JPanel(new BorderLayout());
    	return body;
    }
	
	
    /**
     * Creating up buttons.
     * @return up buttons.
     */
    protected JPanel createUpButtons0() {
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
    protected JPanel createDownButtons0() {
		JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton btnStart = UIUtil.makeIconButton(
			"start-16x16.png",
			"start", 
			"Start task", 
			"Start task", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					doTask0();
				}
				
			});
		btnStart.setMargin(new Insets(0, 0 , 0, 0));
		buttons.add(btnStart);

		return buttons;
    }

    
    /**
     * Creating table.
     * @return table.
     */
    protected JTable createTable0() {return new TemplateTable();}
    
    
    /**
     * Creating footer.
     * @return footer.
     */
    protected JPanel createFooter0() {
    	return new JPanel(new BorderLayout());    	
    }
    
    
    /**
     * Creating progress bar.
     * @return progress bar.
     */
    protected JProgressBar createProgressBar0() {
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
	protected StatusBar createStatusBar0() {
		return new StatusBar();
	}

	
	/**
	 * Event-driven method for mouse double click.
	 */
	protected void onDoubleClick0() {
		JOptionPane.showMessageDialog(this, "Double click (panel)", "Double click (panel)", JOptionPane.INFORMATION_MESSAGE);
	}

	
	/**
	 * Key pressed event method.
	 * @param e key event.
	 */
	protected void onKeyPressed0(KeyEvent e) {
		if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			JOptionPane.showMessageDialog(this, "ENTER key pressed (panel)", "ENTER key pressed (panel)", JOptionPane.INFORMATION_MESSAGE);
		}
	}

	
	/**
	 * Driven-event method for OK button.
	 */
	protected void onOK0() {
		JOptionPane.showMessageDialog(this, "OK button pressed (panel)", "OK button pressed (panel)", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Driven-event method for applying button.
	 */
	protected void onApply0() {
		JOptionPane.showMessageDialog(this, "Applying button pressed (panel)", "Applying button pressed (panel)", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Driven-event method for Cancel button.
	 */
	protected void onCancel0() {
		JOptionPane.showMessageDialog(this, "Cancel button pressed (panel)", "Cancel button pressed (panel)", JOptionPane.INFORMATION_MESSAGE);
	}

	
	@Override
	public void receiveProgress(ProgressEvent evt) throws RemoteException {
		if (this.prgRunning0 == null) return;
		
		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		
		this.prgRunning0.setMaximum(progressTotal);
		if (this.prgRunning0.getValue() < progressStep) 
			this.prgRunning0.setValue(progressStep);
		
		System.out.println(evt.getMsg());
	}


	/**
	 * Enabling controls
	 * @param enabled enabling flag.
	 */
	protected void enableControls(boolean enabled) {
		if (toolBar0 != null ) {
			toolBar0.setEnabled(enabled);
			if (toolBar0 instanceof MovingToolbar) ((MovingToolbar)toolBar0).enableButtons(enabled);
		}
		if (table0 != null) table0.setEnabled(enabled);
		if (btnOK0 != null) btnOK0.setEnabled(enabled);
		if (btnApply0 != null) btnApply0.setEnabled(enabled);
		if (btnCancel0 != null) btnCancel0.setEnabled(enabled);
		if (prgRunning0 != null) prgRunning0.setEnabled(enabled);
	}
	
	
	/**
	 * Updating controls.
	 */
	protected void updateControls() {
		enableControls(!isRunning());
		if (prgRunning0 != null) prgRunning0.setEnabled(isRunning());
		if (statusBar0 != null) statusBar0.setVisible(false);
	}

	
	/**
	 * Disposing panel.
	 */
	protected void dispose() {
		if (runner0 != null) {
			runner0.stop();
			runner0 = null;
		}
	}
	
	
	/**
	 * Resetting.
	 */
	protected void reset() {
		if (prgRunning0 != null) {
			prgRunning0.setMaximum(0);
			prgRunning0.setValue(0);
			prgRunning0.setVisible(false);
		}
		
		if (runner0 != null) {
			runner0.stop();
			runner0 = null;
		}
		
		updateControls();
	}
	
	
	/**
	 * Checking whether some task is running.
	 * @return whether some task is running.
	 */
	protected boolean isRunning() {
		return runner0 != null && runner0.isStarted();
	}

	
	/**
	 * Doing main task.
	 */
	protected void doTask0() {
		if (runner0 != null && runner0.isRunning()) runner0.stop();
		
		runner0 = new AbstractRunner() {
			
			@Override
			protected void task() {
				updateControls();
				taskDefined0();
				
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
		runner0.start();
	}

	
	/**
	 * Customized task. Derived class overrides this method.
	 */
	protected void taskDefined0() {
		if (prgRunning0 != null) {
			prgRunning0.setValue(0);
			prgRunning0.setVisible(true);
		}
		
		for (int time = 0; time < 10; time++) {
			try {
				receiveProgress(new ProgressEvent(this, 10, time+1, "Task defined"));
				Thread.sleep(1000);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		if (prgRunning0 != null) prgRunning0.setVisible(false);
	}
	
	
}
