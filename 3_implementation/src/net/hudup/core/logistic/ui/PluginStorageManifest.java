/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;
import javax.swing.event.EventListenerList;

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.RegisterTableList;
import net.hudup.core.Util;
import net.hudup.core.RegisterTableList.RegisterTableItem;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.ui.AlgConfigDlg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Exportable;
import net.hudup.core.logistic.LogUtil;

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
     * Exported port. This port is used to export algorithms. Port 0 means to export algorithms at random port for getting remote references, not for naming.
     */
    protected int port = 0;
    
    
	/**
	 * Default constructor.
	 */
	public PluginStorageManifest() {
		this(new RegisterTM(), 0);
	}
	
	
	/**
	 * Default constructor with specified exported port.
	 * @param port specified exported port.
	 */
	public PluginStorageManifest(int port) {
		this(new RegisterTM(), port);
	}
	
	
	/**
	 * Default constructor with specified model and port.
	 * @param rtm specified model.
	 * @param port exported port. This port is used to export algorithms. Port 0 means to export algorithms at random port for getting remote references, not for naming.
	 */
	protected PluginStorageManifest(RegisterTM rtm, int port) {
		super(rtm);
		this.port = port;

		update();
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (!isEditable()) return;
				
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
					this, 
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
	protected void update() {
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
						LogUtil.trace(e);
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
				} catch (Throwable e) {LogUtil.trace(e);}
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
		synchronized (listenerList) {
			PluginChangedListener[] listeners = getPluginChangedListeners();
			for (PluginChangedListener listener : listeners) {
				try {
					listener.pluginChanged(evt);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
		}
    }

    
//    /**
//     * Cleaning up something from listeners.
//     */
//    protected void fireCleanupSomething() {
//		synchronized (listenerList) {
//			PluginChangedListener[] listeners = getPluginChangedListeners();
//			for (PluginChangedListener listener : listeners) {
//				try {
//					listener.requireCleanupSomething();
//				}
//				catch (Throwable e) {
//					LogUtil.trace(e);
//				}
//			}
//		}
//    }
    
    
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
				LogUtil.trace(e);
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
					LogUtil.trace(e);
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
					LogUtil.trace(e);
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
		if (!isEditable())
			return false;
		else if (column == 4 || column == 5 || column == 6)
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

