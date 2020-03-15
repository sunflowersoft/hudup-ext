/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;

import net.hudup.core.RegisterTableList.RegisterTableItem;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.alg.ui.AlgConfigDlg;
import net.hudup.core.alg.ui.AlgDesc2ConfigDlg;
import net.hudup.core.client.ClientUtil;
import net.hudup.core.client.Service;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.Exportable;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.SortableSelectableTable;
import net.hudup.core.logistic.ui.SortableSelectableTableModel;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.TagTextField;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.RmiServerIndicator;
import net.hudup.core.parser.SocketServerIndicator;
import net.hudup.data.ui.DatasetConfigurator;
import net.hudup.parser.SnapshotParserImpl;

/**
 * {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For example, a register table manages recommendation algorithms (recommenders) whereas another manages metrics for evaluating recommenders.
 * This {@link PluginStorageManifest} which is the graphic user interface (GUI) allows users to manage {@link PluginStorage}.
 * Every time {@link PluginStorage} was changed, an event {@link PluginChangedEvent} is issued and dispatched to a listener {@link PluginChangedListener}.
 * Later on, {@link PluginChangedListener} can do some tasks in its method {@link PluginChangedListener#pluginChanged(PluginChangedEvent)}.
 * Please pay attention that such {@link PluginChangedListener} must be registered with {@link PluginStorageManifest} before to receive {@link PluginChangedEvent}.
 * <br> 
 * As a convention, this class is called {@code plug-in storage manifest}, which is really a sortable table represented by {@link SortableTable}.
 * Note, {@link SortableTable} written by David Gilbert and Nobuo Tamemasa allows users to create and show a table which can be sorted its rows according its columns.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PluginStorageManifest extends SortableSelectableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal list of registered {@link PluginChangedListener} (s).
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
     * Exported port. This port is used to export algorithms.
     */
    protected int port = 0;
    
    
	/**
	 * Default constructor.
	 */
	public PluginStorageManifest() {
		super(new RegisterTM());
		update();
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if (contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					int selectedColumn = getSelectedColumn();
					if (selectedColumn != 4 && selectedColumn != 5 && selectedColumn != 6)
						showConfig();
				}
			}
			
		});
		
	}
	
	
	/**
	 * Default constructor with specified exported port.
	 * @param port specified exported port.
	 */
	public PluginStorageManifest(int port) {
		this();
		this.port = port;
	}
	
	
	/**
	 * Create context menu.
	 * @return context menu.
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		int selectedRow = getSelectedRow();
		Alg alg = selectedRow < 0 ? null : (Alg) getModel().getValueAt(selectedRow, 3);
		DataConfig config = alg == null ? null : alg.getConfig(); 
		if (config != null) {
			JMenuItem miConfig = UIUtil.makeMenuItem( (String)null, "Configuration", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						showConfig();
					}
				});
			contextMenu.add(miConfig);
			
			contextMenu.addSeparator();
		}
		
		JMenuItem miRegisterAllAlgs = UIUtil.makeMenuItem( (String)null, "Register all normal algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(true, 4);
				}
			});
		contextMenu.add(miRegisterAllAlgs);
		
		JMenuItem miUnregisterAllAlgs = UIUtil.makeMenuItem( (String)null, "Unregister all normal algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(false, 4);
				}
			});
		contextMenu.add(miUnregisterAllAlgs);

		contextMenu.addSeparator();
		
		JMenuItem miExportAllAlgs = UIUtil.makeMenuItem( (String)null, "Export all normal algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(true, 5);
				}
			});
		contextMenu.add(miExportAllAlgs);
		
		JMenuItem miUnexportAllAlgs = UIUtil.makeMenuItem( (String)null, "Unexport all normal algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(false, 5);
				}
			});
		contextMenu.add(miUnexportAllAlgs);

		contextMenu.addSeparator();
		
		JMenuItem miRemoveAllAlgs = UIUtil.makeMenuItem( (String)null, "Remove all normal algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(true, 6);
				}
			});
		contextMenu.add(miRemoveAllAlgs);
		
		JMenuItem miUnremoveAllAlgs = UIUtil.makeMenuItem( (String)null, "Unremove all normal algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(false, 6);
				}
			});
		contextMenu.add(miUnremoveAllAlgs);
		
		
		return contextMenu;
	}

	
	/**
	 * Showing configuration dialog of selected algorithm. 
	 */
	private void showConfig() {
		int selectedRow = getSelectedRow();
		Alg alg = selectedRow < 0 ? null : (Alg) getModel().getValueAt(selectedRow, 3);
		DataConfig config = alg == null ? null : alg.getConfig(); 
		
		if (config == null) {
			JOptionPane.showMessageDialog(
					UIUtil.getFrameForComponent(this), 
					"No configuration", 
					"No configuration", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			AlgConfigDlg dlgConfig = new AlgConfigDlg(UIUtil.getFrameForComponent(this), alg);
			dlgConfig.getPropPane().setToolbarVisible(false);
			dlgConfig.getPropPane().setControlVisible(false);
			dlgConfig.getPropPane().setEnabled(false);
			dlgConfig.setVisible(true);
		}
	}
	
	
	/**
	 * Updating {@link PluginStorageManifest} according to {@link RegisterTM}.
	 */
	private void update() {
		getRegisterTM().update();
		init();
		
		if (getColumnModel().getColumnCount() > 3) {
			getColumnModel().getColumn(3).setMaxWidth(0);
			getColumnModel().getColumn(3).setMinWidth(0);
			getColumnModel().getColumn(3).setPreferredWidth(0);
		}
	}

	
	/**
	 * This public method is called from outside in order to update {@link PluginStorageManifest} according to {@link RegisterTM}.
	 * It really calls method {@link #update()}.
	 * @return true if {@link PluginStorageManifest} is updated successfully from outside.
	 */
	public boolean apply() {
		boolean idle = isListenersIdle();
		if (!idle) return false;

		
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		int n = getRowCount();
		List<Alg> unexportedAlgList = Util.newList();
		boolean changed = false;
		for (int i = 0; i < n; i++) {
			Alg alg = (Alg) getValueAt(i, 3);
			RegisterTable table = PluginStorage.lookupTable(alg.getClass());
			if (table == null) continue;

			boolean registered = (Boolean)getValueAt(i, 4);
			boolean exported = (Boolean)getValueAt(i, 5);
			boolean removed = (Boolean)getValueAt(i, 6);
			
			if (removed) {
				if (!table.contains(alg.getName()))
					nextUpdateList.remove(alg);
				else
					table.unregister(alg.getName());
				
				unexportedAlgList.add(alg);
				changed = true;
			}
			else {
				if (registered) {
					if (!table.contains(alg.getName())) {
						table.register(alg);
						nextUpdateList.remove(alg);
						changed = true;
					}
				}
				else if(table.contains(alg.getName())) {
					table.unregister(alg.getName());
					nextUpdateList.add(alg);
					changed = true;
				}
				
				if (alg instanceof Exportable) {
					try {
						boolean algExported = ((Exportable)alg).getExportedStub() != null;
						if (exported) {
							if (!algExported) {
								((Exportable)alg).export(this.port);
								changed = true;
							}
						}
						else {
							if (algExported) {
								unexportedAlgList.add(alg);
								changed = true;
							}
						}
					} 
					catch (Throwable e) {
						e.printStackTrace();
						changed = true;
					}
				}
			}
		}
		
		if (changed)
			firePluginChangedEvent(new PluginChangedEvent(this));
		
		for (Alg alg : unexportedAlgList) {
			if (alg instanceof Exportable) {
				try {
					((Exportable)alg).unexport(); //Finalize method will call unsetup method if unsetup method exists in this algorithm.
				} catch (Throwable e) {e.printStackTrace();}
			}
		}
		
		update();

		return true;
	}
	

	/**
	 * Selecting or unselecting all rows according the specified input parameter {@code selected}.
	 * @param selected if {@code true} then all rows are selected. Otherwise, all rows are unselected.
	 * @param column column to be selected or not selected.
	 */
	protected void selectAll(boolean selected, int column) {
		if (column != 4 && column != 5 && column != 6)
			return;
		
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			setValueAt(selected, i, column);
		}
	}

	
	/**
	 * Selecting or unselecting all normal algorithms.
	 * @param selected if {@code true} then all normal algorithms are selected. Otherwise, all normal algorithms are unselected. 
	 * @param column column to be selected or not selected.
	 */
	protected void selectAllNormalAlgs(boolean selected, int column) {
		if (column != 4 && column != 5 && column != 6)
			return;

		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			String algTypeName = getValueAt(i, 0).toString();
			if (algTypeName.equals(PluginStorage.NORMAL_ALG))
				setValueAt(selected, i, column);
		}
	}

	
	/**
	 * Getting the model of this {@link PluginStorageManifest}.
	 * @return register table model {@link RegisterTM}
	 */
	public RegisterTM getRegisterTM() {
		return (RegisterTM)getModel();
	}
	
	
	/**
	 * Adding the specified listener to the end of list of listeners, which means that such listener is registered.
	 * 
	 * @param listener specified {@link PluginChangedListener} that is registered.
	 * 
	 */
	public void addPluginChangedListener(PluginChangedListener listener) {
		synchronized (listenerList) {
			listenerList.add(PluginChangedListener.class, listener);
		}
    }

    
	/**
	 * Remove the specified listener from the list of listener
	 * 
	 * @param listener {@link PluginChangedListener} that is unregistered.
	 */
    public void removePluginChangedListener(PluginChangedListener listener) {
		synchronized (listenerList) {
			listenerList.remove(PluginChangedListener.class, listener);
		}
    }
	
    
    /**
     * Return an array of registered {@link PluginChangedListener} (s).
     * 
     * @return array of registered {@link PluginChangedListener} (s).
     * 
     */
    protected PluginChangedListener[] getPluginChangedListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(PluginChangedListener.class);
		}
    }

    
    /**
     * Dispatching {@link PluginChangedEvent} event to registered {@link PluginChangedListener} (s) after {@link PluginStorageManifest} was changed.
     * @param evt {@link PluginChangedEvent} event is issued to registered {@link PluginChangedListener} (s) after {@link PluginStorageManifest} was changed.
     */
    protected void firePluginChangedEvent(PluginChangedEvent evt) {
		PluginChangedListener[] listeners = getPluginChangedListeners();
		
		for (PluginChangedListener listener : listeners) {
			try {
				listener.pluginChanged(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
    }

    
    /**
     * Testing whether a registered {@link PluginChangedListener} is idle.
     * @return whether a registered {@link PluginChangedListener} is idle. Note, there can be many registered {@link PluginChangedListener} (s). 
     */
    protected boolean isListenersIdle() {
		PluginChangedListener[] listeners = getPluginChangedListeners();
		
		for (PluginChangedListener listener : listeners) {
			try {
				if (!listener.isIdle())
					return false;
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return true;
	
    }

    
	/**
	 * Testing whether manifest table is modified.
	 * @return whether manifest table is modified.
	 */
	public boolean isModified() {
		return getRegisterTM().isModified();
	}

	
	/**
	 * Panel for plug-in storage manifest.
	 * @author Loc Nguyen
	 * @version 12.0
	 */
	public static class PluginStorageManifestPanel extends JPanel {

		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Plug-in storage manifest.
		 */
		protected PluginStorageManifest tblRegister = null;
		
		/**
		 * Constructor with plug-in changed listener.
		 * @param listener plug-in changed listener.
		 */
		public PluginStorageManifestPanel(PluginChangedListener listener) {
			setLayout(new BorderLayout());
			JPanel body = new JPanel(new BorderLayout());
			add(body, BorderLayout.CENTER);
			
			tblRegister = new PluginStorageManifest(listener == null || listener.getPort() < 0 ? 0 : listener.getPort());
			if (listener != null)
				tblRegister.addPluginChangedListener(listener);
			body.add(new JScrollPane(tblRegister), BorderLayout.CENTER);
			
			JPanel footer = new JPanel(new BorderLayout());
			add(footer, BorderLayout.SOUTH);

			JPanel toolbar1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			footer.add(toolbar1, BorderLayout.NORTH);
			
			JPanel toolbar1Grp1 = new JPanel(new BorderLayout());
			toolbar1Grp1.setBorder(BorderFactory.createEtchedBorder());
			toolbar1.add(toolbar1Grp1);
			toolbar1.setVisible(false);

			toolbar1Grp1.add(new JLabel("Register/Unregister"), BorderLayout.NORTH);
			JPanel toolbar1Grp1Buttons = new JPanel();
			toolbar1Grp1.add(toolbar1Grp1Buttons, BorderLayout.SOUTH);
			
			JButton registerAll = UIUtil.makeIconButton(
				"selectall-16x16.png", 
				"register_all", "Register all", "Register all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(true, 4);
					}
				});
			registerAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp1Buttons.add(registerAll);

			JButton unregisterAll = UIUtil.makeIconButton(
				"unselectall-16x16.png", 
				"unregister_all", "Unregister all", "Unregister all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(false, 4);
					}
				});
			unregisterAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp1Buttons.add(unregisterAll);

			JPanel toolbar1Grp2 = new JPanel(new BorderLayout());
			toolbar1Grp2.setBorder(BorderFactory.createEtchedBorder());
			toolbar1.add(toolbar1Grp2);

			toolbar1Grp2.add(new JLabel("Export/Unexport"), BorderLayout.NORTH);
			JPanel toolbar1Grp2Buttons = new JPanel();
			toolbar1Grp2.add(toolbar1Grp2Buttons, BorderLayout.SOUTH);

			JButton exportAll = UIUtil.makeIconButton(
				"selectall-16x16.png", 
				"export_all", "Export all", "Export all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(true, 5);
					}
				});
			exportAll.setMargin(new Insets(0, 0 , 0, 0));
			exportAll.setToolTipText("Only export normal algorithms");
			toolbar1Grp2Buttons.add(exportAll);

			JButton unexportAll = UIUtil.makeIconButton(
				"unselectall-16x16.png", 
				"unexport_all", "Unexport all", "Unexport all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(false, 5);
					}
				});
			unexportAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp2Buttons.add(unexportAll);

			JPanel toolbar1Grp3 = new JPanel(new BorderLayout());
			toolbar1Grp3.setBorder(BorderFactory.createEtchedBorder());
			toolbar1.add(toolbar1Grp3);

			toolbar1Grp3.add(new JLabel("Remove/Unremove"), BorderLayout.NORTH);
			JPanel toolbar1Grp3Buttons = new JPanel();
			toolbar1Grp3.add(toolbar1Grp3Buttons, BorderLayout.SOUTH);

			JButton removeAll = UIUtil.makeIconButton(
				"selectall-16x16.png", 
				"remove_all", "Remove all", "Remove all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(true, 6);
					}
				});
			removeAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp3Buttons.add(removeAll);

			JButton unremoveAll = UIUtil.makeIconButton(
				"unselectall-16x16.png", 
				"unremove_all", "Unremove all", "Unremove all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(false, 6);
					}
				});
			unremoveAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp3Buttons.add(unremoveAll);

			
			JPanel toolbar2 = new JPanel(new BorderLayout());
			footer.add(toolbar2, BorderLayout.SOUTH);
			
			JPanel toolbar2Grp1 = new JPanel();
			toolbar2.add(toolbar2Grp1, BorderLayout.WEST);

			//Reload plugin storage from built packages.
			JButton reloadAlg = new JButton("Reload");
			reloadAlg.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					tblRegister.firePluginChangedEvent(new PluginChangedEvent(tblRegister)); //Force to unsetting up algorithms.
					Util.getPluginManager().discover();
					tblRegister.update();
					tblRegister.firePluginChangedEvent(new PluginChangedEvent(tblRegister));
				}
			});
			reloadAlg.setToolTipText("Reload plugin storage from built packages");
			toolbar2Grp1.add(reloadAlg);

			//Only import normal algorithms
			JButton importAlg = new JButton("Import");
			importAlg.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (tblRegister.isModified()) {
						int confirm = JOptionPane.showConfirmDialog(
								tblRegister, 
								"System properties are modified. Do you want to apply them?", 
								"System properties are modified", 
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						
						if (confirm == JOptionPane.YES_OPTION)
							apply();
					}
					
					ImportAlgDlg importAlgDlg = new ImportAlgDlg(tblRegister);
					importAlgDlg.setVisible(true);
					if (importAlgDlg.getImportedCount() > 0) {
						tblRegister.update();
						tblRegister.firePluginChangedEvent(new PluginChangedEvent(tblRegister));
					}
				}
			});
			importAlg.setToolTipText("Only import normal algorithms");
			toolbar2Grp1.add(importAlg);
			
			JPanel toolbar2Grp2 = new JPanel();
			toolbar2.add(toolbar2Grp2, BorderLayout.CENTER);
			
			JButton apply = new JButton("Apply");
			apply.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					apply();
				}
			});
			toolbar2Grp2.add(apply);

			JButton reset = new JButton("Reset");
			reset.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					tblRegister.update();
				}
			});
			toolbar2Grp2.add(reset);

		}
		
		/**
		 * Testing whether the plug-in storage manifest is modified.
		 * @return whether the plug-in storage manifest is modified.
		 */
		public boolean isModified() {
			return tblRegister.isModified();
		}
		
		/**
		 * Applying changes to the plug-in storage manifest.
		 * @return true if applying is successful.
		 */
		public boolean apply() {
			boolean ret = tblRegister.apply();
			if (ret) {
				JOptionPane.showMessageDialog(
						UIUtil.getFrameForComponent(tblRegister), 
						"Apply plug-in storage successfully.\nAlgorithms were registered/unregistered exported/unexported removed/unremoved.", 
						"Apply successfully", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(
						UIUtil.getFrameForComponent(tblRegister), 
						"Apply plug-in storage failed", 
						"Apply failed", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			return ret;
		}
		
	}
	
	
	/**
	 * Showing a dialog containing {@link PluginStorageManifest}.
	 * @param comp parent component.
	 * @param listener {@link PluginChangedListener} to receive {@link PluginChangedEvent} if {@link PluginStorageManifest} is changed. 
	 */
	public static void showDlg(Component comp, PluginChangedListener listener) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), I18nUtil.message("plugin_manager"), true);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		dlg.setLayout(new BorderLayout());
		
		
		JPanel body = new JPanel(new BorderLayout());
		dlg.add(body, BorderLayout.CENTER);
		
		final PluginStorageManifestPanel paneManifest= new PluginStorageManifestPanel(listener);
		body.add(paneManifest, BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		dlg.add(footer, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (paneManifest.isModified())
					paneManifest.apply();
				dlg.dispose();
			}
		});
		footer.add(ok);

		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dlg.dispose();
			}
		});
		footer.add(cancel);

		
		dlg.addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosed(e);
				
				if (!paneManifest.isModified())
					return;
				
				int confirm = JOptionPane.showConfirmDialog(
						comp, 
						"Plug-ins are changed. Do you want to apply them?", 
						"Plug-ins are changed", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				
				if (confirm == JOptionPane.YES_OPTION)
					paneManifest.apply();
			}

		});
		
		dlg.setVisible(true);
	}
	
	
}



/**
 * This is table model of {@link PluginStorageManifest} because {@link PluginStorageManifest} is itself a table. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RegisterTM extends SortableSelectableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Whether or not this model was modified.
	 */
	protected boolean modified = false;

	
	/**
	 * Default constructor.
	 */
	public RegisterTM() {
		super();
		update();
	}
	
	
	/**
	 * Updating this model.
	 */
	public void update() {
		Vector<Vector<Object>> data = Util.newVector();
		
		updateReg(data);
		
		updateNextUpdateList(data);
		
		setDataVector(data, toColumns());
		
		modified = false;
	}
	
	
	/**
	 * Updating this model with a matrix of objects.
	 * @param data specified data as a matrix of objects.
	 */
	private void updateReg(Vector<Vector<Object>> data) {
		RegisterTableList list = PluginStorage.getRegisterTableList();
		for (int i = 0; i < list.size(); i++) {
			RegisterTableItem item = list.get(i);
			updateEachReg(data, item.getRegisterTable());
			
		}
		
	}
	
	
	/**
	 * Creating data from the specified register table for this model.
	 * @param data output parameter that is filled from the specified register table.
	 * @param regTable specified register table.
	 */
	private void updateEachReg(Vector<Vector<Object>> data, RegisterTable regTable) {
		
		List<Alg> algList = regTable.getAlgList();
		for (Alg alg : algList) {
			Vector<Object> row = Util.newVector();
			
			row.add(PluginStorage.lookupTableName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
			row.add(true);

			boolean exported = false;
			if (alg instanceof Exportable) {
				try {
					exported = ((Exportable)alg).getExportedStub() != null;
				} catch (Throwable e) {
					e.printStackTrace();
					exported = false;
				}
			}
			row.add(exported);
			
			row.add(false);

			data.add(row);
		}
		
	}
	
	
	/**
	 * Creating data from next-update algorithms for this model.
	 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * List of next-update algorithms is managed by {@link PluginStorage}.
	 * 
	 * @param data output parameter that is filled from next-update algorithms.
	 */
	private void updateNextUpdateList(Vector<Vector<Object>> data) {
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (int i = 0; i < nextUpdateList.size(); i++) {
			Alg alg = nextUpdateList.get(i);
			Vector<Object> row = Util.newVector();
			
			row.add(PluginStorage.lookupTableName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
			row.add(false);
			
			boolean exported = false;
			if (alg instanceof Exportable) {
				try {
					exported = ((Exportable)alg).getExportedStub() != null;
				} catch (Throwable e) {
					e.printStackTrace();
					exported = false;
				}
			}
			row.add(exported);

			row.add(false);

			data.add(row);
			
		}
	}
	
	
	/**
	 * Creating a string vector for columns of this model.
	 * @return a string vector for columns of this model, specified by {@link Vector} of string.
	 */
	protected Vector<String> toColumns() {
		Vector<String> columns = Util.newVector();
		columns.add("Type");
		columns.add("Name");
		columns.add("Java class");
		columns.add("Object");
		columns.add("Registered");
		columns.add("Exported");
		columns.add("Removed");
		
		return columns;
	}


	/**
	 * Testing whether this model is modified.
	 * @return whether model is modified.
	 */
	public boolean isModified() {
		return modified;
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 4 || columnIndex == 5 || columnIndex == 6)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		if (column == 4 || column == 5 || column == 6)
			return true;
		else
			return false;
	}


	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, row, column);
		
		modified = true;
	}
	
	
}



/**
 * This is GUI allowing users to import/register dynamically and remotely algorithms from Hudup server/service.
 * @author Loc Nguyen
 * @version 12.0
 */
class ImportAlgDlg extends JDialog {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Browsing button.
	 */
	protected JButton btnBrowse = null;
	
	
	/**
	 * Browsing text field.
	 */
	protected TagTextField txtBrowse = null;
	
	
	/**
	 * Connection button.
	 */
	protected JButton btnConnect = null;

	
	/**
	 * Table of extended algorithm descriptions.
	 */
	protected AlgDescImportTable tblAlgDescImport = null;
	
	
	
	/**
	 * Result as list of chosen algorithms.
	 */
	protected AlgList result = new AlgList();

	
	/**
	 * If {@code true}, users press OK button to close this dialog.
	 */
	protected boolean ok = false;

	
	/**
	 * Importing count.
	 */
	protected int importedCount = 0;
	
	
	/**
	 * Constructor with parent component.
	 * @param comp parent component.
	 */
	public ImportAlgDlg(Component comp) {
		super(UIUtil.getFrameForComponent(comp), "Import remotely algorithms from Hudup server/service", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		JPanel pane = null;
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		pane = new JPanel(new BorderLayout());
		header.add(pane, BorderLayout.NORTH);
		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				browse();
			}
		});
		pane.add(btnBrowse, BorderLayout.WEST);
		txtBrowse = new TagTextField();
		txtBrowse.setEditable(false);
		pane.add(txtBrowse, BorderLayout.CENTER);
		
		pane = new JPanel();
		header.add(pane, BorderLayout.SOUTH);
		btnConnect = new JButton("Load");
		btnConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					load();
				} catch (Throwable ex) {ex.printStackTrace();}
			}
		});
		pane.add(btnConnect);

		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		tblAlgDescImport = new AlgDescImportTable();
		body.add(new JScrollPane(tblAlgDescImport), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ok();
			}
		});
		
		JButton cancel = new JButton("Cancel");
		footer.add(cancel);
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});

	}

	
	/**
	 * Event-driven method for browsing button to browse place to store algorithms.
	 */
	protected void browse() {
		List<Alg> parserList = Util.newList();
		RmiServerIndicator rmiIndicator = new RmiServerIndicator();
		parserList.add(rmiIndicator);
		SocketServerIndicator socketIndicator = new SocketServerIndicator();
		parserList.add(socketIndicator);
		SnapshotParserImpl snapshotParser = new SnapshotParserImpl(); 
		parserList.add(snapshotParser);
		
		DataDriverList dataDriverList = new DataDriverList();
		dataDriverList.add(new DataDriver(DataType.file));
		dataDriverList.add(new DataDriver(DataType.hudup_rmi));
		dataDriverList.add(new DataDriver(DataType.hudup_socket));
		
		DataConfig defaultConfig = (DataConfig)txtBrowse.getTag();
		if (defaultConfig == null) {
			defaultConfig = new DataConfig();
			defaultConfig.setParser(rmiIndicator);
		}
		DatasetConfigurator configurator = new DatasetConfigurator(this, parserList, dataDriverList, defaultConfig);
		DataConfig config = configurator.getResultedConfig();
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Configuration was not established", 
				"Configuration was not established", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		txtBrowse.setText(config.getStoreUri().toString(), config);
	}
	
	
	/**
	 * Event-driven method for connecting button to connect place to store algorithms.
	 * @throws RemoteException if any error raises.
	 */
	protected void load() {
		DataConfig config = (DataConfig)txtBrowse.getTag();
		if (config == null || config.getParser() == null || config.getStoreUri() == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Configuration was not established", 
				"Configuration was not established", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		DatasetParser parser = config.getParser();
		xURI storeUri = config.getStoreUri();
		if ((parser instanceof RmiServerIndicator) || (parser instanceof SocketServerIndicator)) {
			AlgDesc2List algDescList = new AlgDesc2List();
			Service service = null;
			if (parser instanceof RmiServerIndicator)
				service = ClientUtil.getRemoteService(storeUri.getHost(), storeUri.getPort(), config.getStoreAccount(), config.getStorePassword().getText());
			else if (parser instanceof SocketServerIndicator)
				service = ClientUtil.getSocketConnection(storeUri.getHost(), storeUri.getPort(),config.getStoreAccount(), config.getStorePassword().getText());
			
			try {
				algDescList = service.getAlgDescs();
			} catch (Throwable e) {
				e.printStackTrace();
				algDescList = new AlgDesc2List();
			}

			tblAlgDescImport.update(algDescList);
			
			if ((service != null) && (service instanceof SocketConnection))
				((SocketConnection)service).close();
		}
		else {
			List<Alg> availableAlgList = Util.newList();
			loadClassesFromStore(storeUri, availableAlgList);
			tblAlgDescImport.update(availableAlgList);
		}
		
		
		if (tblAlgDescImport.getRowCount() == 0)
			JOptionPane.showMessageDialog(this, "Algorithm list empty", "Algorithm list empty", JOptionPane.WARNING_MESSAGE);
	}
	
	
	/**
	 * Loading classes from store.
	 * @param storeUri store URI.
	 * @param outAlgList list of algorithms as output.
	 */
	private void loadClassesFromStore(xURI storeUri, List<Alg> outAlgList) {
		if (storeUri == null) return;

		List<Alg> algList = Util.getPluginManager().discover(storeUri, Alg.class);
		RegisterTable normalReg = PluginStorage.getNormalAlgReg();
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (Alg alg : algList) {
			if (normalReg.contains(alg.getName())) continue;
			
			int idx = nextUpdateList.indexOf(alg.getName());
			if (idx < 0)
				outAlgList.add(alg);
			else {
				Alg nextUpdateAlg = nextUpdateList.get(idx);
				if (PluginStorage.lookupTableName(nextUpdateAlg.getClass()) != PluginStorage.lookupTableName(alg.getClass()))
					outAlgList.add(alg);
			}
		}
		
	}
	
	
	/**
	 * Event-driven method response to OK button command.
	 */
	@NextUpdate
	protected void ok() {
		importedCount = 0;

		DataConfig config = (DataConfig)txtBrowse.getTag();
		if (config == null || config.getParser() == null || config.getStoreUri() == null) {
			JOptionPane.showMessageDialog(this, "Configuration was not established", "Configuration was not established", JOptionPane.ERROR_MESSAGE);
			return;
		}

		DatasetParser parser = config.getParser();
		xURI storeUri = config.getStoreUri();
		if ((parser instanceof RmiServerIndicator) || (parser instanceof SocketServerIndicator)) {
			List<AlgDesc2> selectedList = tblAlgDescImport.getSelectedAlgDescList();
			if (selectedList.size() == 0) {
				JOptionPane.showMessageDialog(this, "No algorithm selected", "No algorithm selected", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			AlgList nextUpdateList = PluginStorage.getNextUpdateList();
			Service service = null;
			for (AlgDesc2 algDesc : selectedList) {
				if (parser instanceof RmiServerIndicator) {
					if (service == null)
						service = ClientUtil.getRemoteService(storeUri.getHost(), storeUri.getPort(), config.getStoreAccount(), config.getStorePassword().getText());
				}
				else if (parser instanceof SocketServerIndicator) {
					if (service != null)
						((SocketConnection)service).close();
					service = ClientUtil.getSocketConnection(storeUri.getHost(), storeUri.getPort(),config.getStoreAccount(), config.getStorePassword().getText());
				}
				
				boolean localExist = true;
				if (algDesc.baseRemoteInterfaceNames != null) {
					for (String iName : algDesc.baseRemoteInterfaceNames) {
						try {
							Class.forName(iName);
						}
						catch (Throwable e) {
							e.printStackTrace();
							LogUtil.error("Interface '" + iName + "' not exists, error by " + e.getMessage());
							localExist = localExist && false;
						}
						
						if (!localExist) break;
					}
				}
				if (!localExist) continue;
				
				Alg alg = null;
				try {
					alg = service.getAlg(algDesc.algName);
				}
				catch (Throwable e) {
					e.printStackTrace();
					LogUtil.error("Retrieving remote algorithm error by: " + e.getMessage());
					alg = null;
				}
				if (alg == null) continue;
				
				/////////////////////////////////////////////////
				if (alg instanceof AlgRemoteWrapper) //Fix later
					((AlgRemoteWrapper)alg).setExclusive(true);
				/////////////////////////////////////////////////
				
				RegisterTable table = PluginStorage.lookupTable(alg.getClass());
				if (table == null || table.contains(algDesc.algName)) {
					unexportRemoteWrapperAlg(alg);
					continue;
				}
				
				int idx = nextUpdateList.indexOf(algDesc.algName);
				if (idx < 0) {
					if (table.register(alg))
						importedCount++;
					else
						unexportRemoteWrapperAlg(alg);
				}
				else {
					Alg nextUpdateAlg = nextUpdateList.get(idx);
					if (PluginStorage.lookupTableName(nextUpdateAlg.getClass()) != PluginStorage.lookupTableName(alg.getClass())) {
						if (table.register(alg))
							importedCount++;
						else
							unexportRemoteWrapperAlg(alg);
					}
					else
						unexportRemoteWrapperAlg(alg);
				}
			}

			if ((service != null) && (service instanceof SocketConnection))
				((SocketConnection)service).close();
		}
		else {
			List<Alg> selectedList = tblAlgDescImport.getSelectedAlgList();
			if (selectedList.size() == 0) {
				JOptionPane.showMessageDialog(this, "No algorithm selected", "No algorithm selected", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			AlgList nextUpdateList = PluginStorage.getNextUpdateList();
			for (Alg alg : selectedList) {
				RegisterTable table = PluginStorage.lookupTable(alg.getClass());
				if (table == null || table.contains(alg.getName())) continue;
				
				int idx = nextUpdateList.indexOf(alg.getName());
				if (idx < 0) {
					if (table.register(alg))
						importedCount++;
				}
				else {
					Alg nextUpdateAlg = nextUpdateList.get(idx);
					if (PluginStorage.lookupTableName(nextUpdateAlg.getClass()) != PluginStorage.lookupTableName(alg.getClass())) {
						if (table.register(alg))
							importedCount++;
					}
				}
			}
		}
		
		if (importedCount == 0)
			JOptionPane.showMessageDialog(this, 
				"No algorithm imported", 
				"No algorithm imported", 
				JOptionPane.INFORMATION_MESSAGE);
		else
			JOptionPane.showMessageDialog(this, 
				"Importing " + importedCount + " algorithm successfully", 
				"Successful importing", 
				JOptionPane.INFORMATION_MESSAGE);
		
		ok = true;
		dispose();
	}
	
	
	/**
	 * Unexporting wrapper of a remote algorithm.
	 * @param alg wrapper of a remote algorithm.
	 */
	private void unexportRemoteWrapperAlg(Alg alg) {
		if ((alg != null) && (alg instanceof AlgRemoteWrapper)) {
			try {
				((AlgRemoteWrapper)alg).forceUnexport();
			} catch (Throwable e) {e.printStackTrace();}
		}
		
	}
	
	
	/**
	 * Checking whether or not the OK button is pressed.
	 * @return whether or not the OK button is pressed.
	 */
	public boolean isOK() {
		return ok;
	}

	
	/**
	 * Getting imported count.
	 * @return imported count.
	 */
	public int getImportedCount() {
		return importedCount;
	}

	
}


/**
 * This is table of extended algorithm descriptions. 
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
class AlgDescImportTable extends SortableSelectableTable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public AlgDescImportTable() {
		super(new AlgDescImportTM());
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if (contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					int selectedColumn = getSelectedColumn();
					if (selectedColumn != 5)
						showConfig();
				}
			}
			
		});
	}

	
	/**
	 * Creating context menu.
	 * @return context menu.
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		int selectedRow = getSelectedRow();
		AlgDesc2 algDesc = selectedRow < 0 ? null : (AlgDesc2) getModel().getValueAt(selectedRow, 3);

		if (algDesc != null) {
			JMenuItem miConfig = UIUtil.makeMenuItem( (String)null, "Configuration", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						showConfig();
					}
				});
			contextMenu.add(miConfig);
			
			contextMenu.addSeparator();
		}
		
		JMenuItem miRegisterAllAlgs = UIUtil.makeMenuItem( (String)null, "Select all normal algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(true);
				}
			});
		contextMenu.add(miRegisterAllAlgs);
		
		JMenuItem miUnregisterAllAlgs = UIUtil.makeMenuItem( (String)null, "Unselect all normal algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(false);
				}
			});
		contextMenu.add(miUnregisterAllAlgs);

		return contextMenu;
	}
	
	
	/**
	 * Showing configuration.
	 */
	private void showConfig() {
		int selectedRow = getSelectedRow();
		AlgDesc2 algDesc = selectedRow < 0 ? null : (AlgDesc2) getModel().getValueAt(selectedRow, 3);
		if (algDesc == null) {
			JOptionPane.showMessageDialog(
					UIUtil.getFrameForComponent(this), 
					"No algorithm description", 
					"No algorithm description", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			AlgDesc2ConfigDlg dlgConfig = new AlgDesc2ConfigDlg(UIUtil.getFrameForComponent(this), algDesc);
			dlgConfig.setVisible(true);
		}
	}
	
	
	/**
	 * Selecting or unselecting all normal algorithms.
	 * @param selected if {@code true} then all normal algorithms are selected. Otherwise, all normal algorithms are unselected. 
	 */
	protected void selectAllNormalAlgs(boolean selected) {
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			String algTypeName = getValueAt(i, 0).toString();
			if (algTypeName.equals(PluginStorage.NORMAL_ALG))
				setValueAt(selected, i, 5);
		}
	}

	
	/**
	 * Updating this table by specified list of algorithms.
	 * @param algDescList list of algorithms.
	 */
	public void update(List<Alg> algList) {
		((AlgDescImportTM)getModel()).update(algList);
		init();
	}
	
	
	/**
	 * Updating this table by specified list of extended algorithm descriptions.
	 * @param algDescList list of extended algorithm descriptions.
	 */
	public void update(AlgDesc2List algDescList) {
		((AlgDescImportTM)getModel()).update(algDescList);
		init();
	}
	
	
	/**
	 * Getting list of selected extended algorithm descriptions.
	 * @return list of selected extended algorithm descriptions.
	 */
	public List<AlgDesc2> getSelectedAlgDescList() {
		List<AlgDesc2> selectedList = Util.newList();
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			boolean selected = (Boolean)getValueAt(i, 5);
			if (!selected) continue;
			
			AlgDesc2 algDesc = (AlgDesc2)getValueAt(i, 3);
			if (algDesc != null)
				selectedList.add(algDesc);
		}
		
		return selectedList;
	}
	
	
	/**
	 * Getting list of selected algorithm.
	 * @return list of selected algorithm.
	 */
	public List<Alg> getSelectedAlgList() {
		List<Alg> selectedList = Util.newList();
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			boolean selected = (Boolean)getValueAt(i, 5);
			if (!selected) continue;
			
			Alg alg = (Alg)getValueAt(i, 4);
			if (alg != null)
				selectedList.add(alg);
		}
		
		return selectedList;
	}
	
	
	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		if (getColumnModel().getColumnCount() > 3) {
			getColumnModel().getColumn(3).setMaxWidth(0);
			getColumnModel().getColumn(3).setMinWidth(0);
			getColumnModel().getColumn(3).setPreferredWidth(0);
		}
		if (getColumnModel().getColumnCount() > 4) {
			getColumnModel().getColumn(4).setMaxWidth(0);
			getColumnModel().getColumn(4).setMinWidth(0);
			getColumnModel().getColumn(4).setPreferredWidth(0);
		}
	}


	/**
	 * Showing a dialog containing table of extended algorithm descriptions.
	 * @param comp parent component.
	 * @param algDescList list of extended algorithm descriptions.
	 */
	public static void showDlg(Component comp, AlgDesc2List algDescList) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Extended algorithm descriptions", true);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		
		AlgDescImportTable table = new AlgDescImportTable();
		table.update(algDescList);
		dlg.add(new JScrollPane(table), BorderLayout.CENTER);
		
		dlg.setVisible(true);
	}
}



/**
 * This is table model of algorithm descriptions. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class AlgDescImportTM extends SortableSelectableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Whether or not this model was modified.
	 */
	protected boolean modified = false;

	
	/**
	 * Default constructor.
	 */
	public AlgDescImportTM() {
		super();
	}
	
	
	/**
	 * Updating this model by specified list of algorithms.
	 * @param algDescList list of algorithms.
	 */
	public void update(List<Alg> algList) {
		Vector<Vector<Object>> data = Util.newVector();
		
		for (Alg alg : algList) {
			AlgDesc2 algDesc = new AlgDesc2(alg);
			Vector<Object> row = Util.newVector();
			
			row.add(algDesc.tableName);
			row.add(algDesc.algName);
			row.add(algDesc.getAlgClassName());
			row.add(algDesc);
			row.add(alg);
			row.add(false);

			data.add(row);
		}
		
		setDataVector(data, toColumns());
		
		modified = false;
	}

	
	/**
	 * Updating this model by specified list of extended algorithm descriptions.
	 * @param algDescList list of extended algorithm descriptions.
	 */
	public void update2(List<AlgDesc2> algDescList) {
		AlgDesc2List list = new AlgDesc2List(algDescList);
		update(list);
	}

	
	/**
	 * Updating this model by specified list of extended algorithm descriptions.
	 * @param algDescList list of extended algorithm descriptions.
	 */
	public void update(AlgDesc2List algDescList) {
		Vector<Vector<Object>> data = Util.newVector();
		
		for (int i = 0; i < algDescList.size(); i++) {
			AlgDesc2 algDesc = algDescList.get(i);
			Vector<Object> row = Util.newVector();
			
			row.add(algDesc.tableName);
			row.add(algDesc.algName);
			row.add(algDesc.getAlgClassName());
			row.add(algDesc);
			row.add(null);
			row.add(false);

			data.add(row);
		}
		
		setDataVector(data, toColumns());
		
		modified = false;
	}
	
	
	/**
	 * Creating a string vector for columns of this model.
	 * @return a string vector for columns of this model.
	 */
	protected Vector<String> toColumns() {
		Vector<String> columns = Util.newVector();
		columns.add("Type");
		columns.add("Name");
		columns.add("Java class");
		columns.add("Description object");
		columns.add("Alg object");
		columns.add("Registered");
		
		return columns;
	}


	/**
	 * Testing whether this model is modified.
	 * @return whether model is modified.
	 */
	public boolean isModified() {
		return modified;
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 5)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		if (column == 5)
			return true;
		else
			return false;
	}


	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, row, column);
		
		modified = true;
	}
	
	
}

