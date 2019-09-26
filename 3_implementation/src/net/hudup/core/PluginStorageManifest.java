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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.RegisterTableList.RegisterTableItem;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.alg.ui.AlgConfigDlg;
import net.hudup.core.alg.ui.AlgListBox;
import net.hudup.core.client.ClientUtil;
import net.hudup.core.client.Service;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
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
public class PluginStorageManifest extends SortableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal list of registered {@link PluginChangedListener} (s).
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
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
				int row = getSelectedRow();
				if (row < 0) return;
				Alg alg = (Alg) getModel().getValueAt(row, 3);
				if (alg.getConfig() == null) return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = new JPopupMenu();
					
					JMenuItem miConfig = UIUtil.makeMenuItem( (String)null, "Configuration", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								AlgConfigDlg dlgConfig = new AlgConfigDlg(UIUtil.getFrameForComponent(getThisManifest()), alg);
								dlgConfig.getPropPane().setToolbarVisible(false);
								dlgConfig.getPropPane().setControlVisible(false);
								dlgConfig.getPropPane().setEnabled(false);
								dlgConfig.setVisible(true);
							}
						});
					contextMenu.add(miConfig);

					contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					
				}
			}
			
		});
	}
	
	
	/**
	 * Getting this manifest.
	 * @return this manifest.
	 */
	private PluginStorageManifest getThisManifest() {
		return this;
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
		
		if (!idle)
			return false;

		
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			
			Alg alg = (Alg) getValueAt(i, 3);
			
			boolean registered = (Boolean)getValueAt(i, 4);
			RegisterTable table = PluginStorage.lookupTable(alg.getClass());
			if (table == null)
				continue;
			
			if (registered) {
				if (!table.contains(alg.getName())) {
					table.register(alg);
					nextUpdateList.remove(alg);
				}
			}
			else if(table.contains(alg.getName())) {
				table.unregister(alg.getName());
				nextUpdateList.add(alg);
			}
		}
		
		update();
		firePluginChangedEvent(new PluginChangedEvent(this));
		
		return true;
	}
	

	/**
	 * Selecting or unselecting all rows according the specified input parameter {@code selected}.
	 * @param selected if {@code true} then all rows are selected. Otherwise, all rows are unselected. 
	 */
	public void selectAll(boolean selected) {
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			
			setValueAt(selected, i, 4);
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
	 * Creating {@link JPanel} that contains {@link PluginStorageManifest}.
	 * @param listener {@link PluginChangedListener} to receive {@link PluginChangedEvent} if {@link PluginStorageManifest} is changed.
	 * @return {@link JPanel} that contains {@link PluginStorageManifest}.
	 */
	public static JPanel createPane(PluginChangedListener listener) {
		
		JPanel result = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		result.add(body, BorderLayout.CENTER);
		
		final PluginStorageManifest tblRegister = new PluginStorageManifest();
		if (listener != null)
			tblRegister.addPluginChangedListener(listener);
		body.add(new JScrollPane(tblRegister), BorderLayout.CENTER);
		
		JPanel footer = new JPanel(new BorderLayout());
		result.add(footer, BorderLayout.SOUTH);

		JPanel buttonGrp1 = new JPanel();
		footer.add(buttonGrp1, BorderLayout.CENTER);
		
		JButton apply = new JButton("Apply");
		buttonGrp1.add(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (tblRegister.apply()) {
					
					JOptionPane.showMessageDialog(
							UIUtil.getFrameForComponent(tblRegister), 
							"Apply successfully. Algorithms are registered / unregistered", 
							"Apply successfully", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(
							UIUtil.getFrameForComponent(tblRegister), 
							"Listeners not idle", 
							"Listeners not idle", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		});
		
		JButton selectAll = new JButton("Select all");
		buttonGrp1.add(selectAll);
		selectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tblRegister.selectAll(true);
			}
		});

		JButton unselectAll = new JButton("Unselect all");
		buttonGrp1.add(unselectAll);
		unselectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tblRegister.selectAll(false);
			}
		});

		
		JPanel buttonGrp2 = new JPanel();
		footer.add(buttonGrp2, BorderLayout.EAST);
		
		JButton importAlg = new JButton("Import");
		buttonGrp2.add(importAlg);
		importAlg.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ImportAlgDlag(tblRegister).setVisible(true);
				
				tblRegister.update();
			}
		});
		if (listener != null && !listener.isSupportImport()) importAlg.setVisible(false);
		
		
		return result;
	}
	
	
	/**
	 * Showing a dialog containing {@link PluginStorageManifest}.
	 * @param comp parent component.
	 * @param listener {@link PluginChangedListener} to receive {@link PluginChangedEvent} if {@link PluginStorageManifest} is changed. 
	 * @param modal whether or not the dialog is modal. The modal dialog will block user inputs. Please see {@link JDialog} for more details. 
	 */
	public static void showDlg(Component comp, PluginChangedListener listener, boolean modal) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Plugin storage", modal);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		dlg.add(createPane(listener), BorderLayout.CENTER);
		
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
class RegisterTM extends SortableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
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
			
			row.add(PluginStorage.lookupAlgTypeName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
			row.add(true);
			
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
			
			row.add(PluginStorage.lookupAlgTypeName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
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
		
		return columns;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 4)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return column == 4;
	}
	
	
}



/**
 * {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For example, a register table manages recommendation algorithms (recommenders) whereas another manages metrics for evaluating recommenders.
 * This {@link PluginStorageManifest2} which is the graphic user interface (GUI) allows users to manage {@link PluginStorage}.
 * Every time {@link PluginStorage} was changed, an event {@link PluginChangedEvent} is issued and dispatched to a listener {@link PluginChangedListener}.
 * Later on, {@link PluginChangedListener} can do some tasks in its method {@link PluginChangedListener#pluginChanged(PluginChangedEvent)}.
 * Please pay attention that such {@link PluginChangedListener} must be registered with {@link PluginStorageManifest2} before to receive {@link PluginChangedEvent}.
 * <br> 
 * As a convention, this class is called {@code plug-in storage manifest}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class PluginStorageManifest2 extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal list of registered {@link PluginChangedListener} (s).
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
	/**
	 * Default constructor.
	 */
	public PluginStorageManifest2() {
		super(new RegisterTM2());
		update();
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				int row = getSelectedRow();
				if (row < 0) return;
				Alg alg = (Alg) getModel().getValueAt(row, 3);
				if (alg.getConfig() == null) return;
				
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = new JPopupMenu();
					
					JMenuItem miConfig = UIUtil.makeMenuItem( (String)null, "Configuration", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								AlgConfigDlg dlgConfig = new AlgConfigDlg(UIUtil.getFrameForComponent(getThisManifest()), alg);
								dlgConfig.getPropPane().setToolbarVisible(false);
								dlgConfig.getPropPane().setControlVisible(false);
								dlgConfig.getPropPane().setEnabled(false);
								dlgConfig.setVisible(true);
							}
						});
					contextMenu.add(miConfig);

					contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					
				}
			}
			
		});
	}
	
	
	/**
	 * Getting this manifest.
	 * @return this manifest.
	 */
	private PluginStorageManifest2 getThisManifest() {
		return this;
	}
	
	
	/**
	 * Updating {@link PluginStorageManifest2} according to {@link RegisterTM2}.
	 */
	private void update() {
		getRegisterTM().update();
		
		if (getColumnModel().getColumnCount() > 3) {
			getColumnModel().getColumn(3).setMaxWidth(0);
			getColumnModel().getColumn(3).setMinWidth(0);
			getColumnModel().getColumn(3).setPreferredWidth(0);
		}
	}

	
	/**
	 * This public method is called from outside in order to update {@link PluginStorageManifest2} according to {@link RegisterTM2}.
	 * It really calls method {@link #update()}.
	 * @return true if {@link PluginStorageManifest2} is updated successfully from outside.
	 */
	public boolean apply() {
		boolean idle = isListenersIdle();
		
		if (!idle)
			return false;

		
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			
			Alg alg = (Alg) getValueAt(i, 3);
			
			boolean registered = (Boolean)getValueAt(i, 4);
			RegisterTable table = PluginStorage.lookupTable(alg.getClass());
			if (table == null)
				continue;
			
			if (registered) {
				if (!table.contains(alg.getName())) {
					table.register(alg);
					nextUpdateList.remove(alg);
				}
			}
			else if(table.contains(alg.getName())) {
				table.unregister(alg.getName());
				nextUpdateList.add(alg);
			}
		}
		
		update();
		firePluginChangedEvent(new PluginChangedEvent(this));
		
		return true;
	}
	

	/**
	 * Selecting or unselecting all rows according the specified input parameter {@code selected}.
	 * @param selected if {@code true} then all rows are selected. Otherwise, all rows are unselected. 
	 */
	public void selectAll(boolean selected) {
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			
			setValueAt(selected, i, 4);
		}
	}

	
	/**
	 * Getting the model of this {@link PluginStorageManifest2}.
	 * @return register table model {@link RegisterTM2}
	 */
	public RegisterTM2 getRegisterTM() {
		return (RegisterTM2)getModel();
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
     * Dispatching {@link PluginChangedEvent} event to registered {@link PluginChangedListener} (s) after {@link PluginStorageManifest2} was changed.
     * @param evt {@link PluginChangedEvent} event is issued to registered {@link PluginChangedListener} (s) after {@link PluginStorageManifest2} was changed.
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
	 * Creating {@link JPanel} that contains {@link PluginStorageManifest2}.
	 * @param listener {@link PluginChangedListener} to receive {@link PluginChangedEvent} if {@link PluginStorageManifest2} is changed.
	 * @return {@link JPanel} that contains {@link PluginStorageManifest2}.
	 */
	public static JPanel createPane(PluginChangedListener listener) {
		
		JPanel result = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		result.add(body, BorderLayout.CENTER);
		
		final PluginStorageManifest2 tblRegister = new PluginStorageManifest2();
		if (listener != null)
			tblRegister.addPluginChangedListener(listener);
		body.add(new JScrollPane(tblRegister), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		result.add(footer, BorderLayout.SOUTH);

		JPanel buttonGrp1 = new JPanel();
		footer.add(buttonGrp1, BorderLayout.CENTER);

		JButton apply = new JButton("Apply");
		buttonGrp1.add(apply);
		apply.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (tblRegister.apply()) {
					
					JOptionPane.showMessageDialog(
							UIUtil.getFrameForComponent(tblRegister), 
							"Apply successfully. Algorithms are registered", 
							"Apply successfully", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				else {
					JOptionPane.showMessageDialog(
							UIUtil.getFrameForComponent(tblRegister), 
							"Listeners not idle", 
							"Listeners not idle", 
							JOptionPane.INFORMATION_MESSAGE);
				}
				
			}
		});
		
		JButton selectAll = new JButton("Select all");
		buttonGrp1.add(selectAll);
		selectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tblRegister.selectAll(true);
			}
		});

		JButton unselectAll = new JButton("Unselect all");
		buttonGrp1.add(unselectAll);
		unselectAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				tblRegister.selectAll(false);
			}
		});

		
		JPanel buttonGrp2 = new JPanel();
		footer.add(buttonGrp2, BorderLayout.EAST);
		
		JButton importAlg = new JButton("Import");
		buttonGrp2.add(importAlg);
		importAlg.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				new ImportAlgDlag(tblRegister).setVisible(true);
				
				tblRegister.update();
			}
		});
		if (!listener.isSupportImport()) importAlg.setVisible(false);

		
		return result;
	}
	
	
	/**
	 * Showing a dialog containing {@link PluginStorageManifest2}.
	 * @param comp parent component.
	 * @param listener {@link PluginChangedListener} to receive {@link PluginChangedEvent} if {@link PluginStorageManifest2} is changed. 
	 * @param modal whether or not the dialog is modal. The modal dialog will block user inputs. Please see {@link JDialog} for more details. 
	 */
	public static void showDlg(Component comp, PluginChangedListener listener, boolean modal) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Plugin storage", modal);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		dlg.add(createPane(listener), BorderLayout.CENTER);
		
		dlg.setVisible(true);
	}
	
	
}



/**
 * This is table model of {@link PluginStorageManifest2} because {@link PluginStorageManifest2} is itself a table. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class RegisterTM2 extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public RegisterTM2() {
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
			
			row.add(PluginStorage.lookupAlgTypeName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
			row.add(true);
			
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
			
			row.add(PluginStorage.lookupAlgTypeName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
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
		
		return columns;
	}


	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 4)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return column == 4;
	}
	
	
}



/**
 * This is GUI allowing users to import/register dynamically algorithms from jar files.
 * @author Loc Nguyen
 * @version 12.0
 */
@Deprecated
class JarImportAlgDlag extends JDialog {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with parent component.
	 * @param comp parent component.
	 */
	public JarImportAlgDlag(Component comp) {
		super(UIUtil.getFrameForComponent(comp), "Import algorithms from jar file", true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				onOk();
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

		
		setVisible(true);
	}
	
	
	/**
	 * Event-driven method response to OK button command.
	 */
	protected void onOk() {
		dispose();
	}
	
	
}



/**
 * This is GUI allowing users to import/register dynamically and remotely algorithms from Hudup server/service.
 * @author Loc Nguyen
 * @version 12.0
 */
class ImportAlgDlag extends JDialog {
	
	
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
	 * The left algorithm list box assists users to select algorithms.
	 */
	protected AlgListBox leftList = null;

	
	/**
	 * The right algorithm list box contains chosen algorithms.
	 */
	protected AlgListBox rightList = null;

	
	/**
	 * Result as list of chosen algorithms.
	 */
	protected AlgList result = new AlgList();

	
	/**
	 * If {@code true}, users press OK button to close this dialog.
	 */
	private boolean ok = false;

	
	/**
	 * Constructor with parent component.
	 * @param comp parent component.
	 */
	public ImportAlgDlag(Component comp) {
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
					connect();
				} catch (Throwable ex) {ex.printStackTrace();}
			}
		});
		pane.add(btnConnect);

		
		JPanel body = new JPanel(new GridLayout(1, 0));
		add(body, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		body.add(left);
		
		left.add(new JLabel("Available algorithm list"), BorderLayout.NORTH);
		leftList = new AlgListBox(true);
		//leftList.update(remainList);
		leftList.setEnableDoubleClick(false);
		left.add(new JScrollPane(leftList), BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(0, 1));
		left.add(buttons, BorderLayout.EAST);
		
		JButton leftToRight = new JButton("> ");
		leftToRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				leftToRight();
			}
		});
		pane = new JPanel();
		pane.add(leftToRight);
		buttons.add(pane);
		
		JButton leftToRightAll = new JButton(">>");
		leftToRightAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				leftToRightAll();
			}
		});
		pane = new JPanel();
		pane.add(leftToRightAll);
		buttons.add(pane);
		
		JButton rightToLeft = new JButton("< ");
		rightToLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				rightToLeft();
			}
		});
		pane = new JPanel();
		pane.add(rightToLeft);
		buttons.add(pane);
		
		JButton rightToLeftAll = new JButton("<<");
		rightToLeftAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				rightToLeftAll();
			}
		});
		pane = new JPanel();
		pane.add(rightToLeftAll);
		buttons.add(pane);

		JPanel right = new JPanel(new BorderLayout());
		body.add(right);
		
		right.add(new JLabel("Selected algorithm list"), BorderLayout.NORTH);
		
		rightList = new AlgListBox(true);
		rightList.setEnableDoubleClick(false);
		//rightList.update(selectedList);
		right.add(new JScrollPane(rightList), BorderLayout.CENTER);
		
		
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
	protected void connect() {
		DataConfig config = (DataConfig)txtBrowse.getTag();
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Configuration was not established", 
				"Configuration was not established", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		DatasetParser parser = config.getParser();
		List<Alg> availableAlgList = Util.newList();
		if (parser instanceof RmiServerIndicator)
			loadClassesFromRmiServer(config, availableAlgList);
		else if (parser instanceof SocketServerIndicator)
			loadClassesFromSocketServer(config, availableAlgList);
		else
			loadClassesFromStore(config.getStoreUri(), availableAlgList);
		
		if (availableAlgList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Empty algorithm list", 
					"Empty algorithm list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		leftList.update(availableAlgList);
		rightList.clear();
	}
	
	
	/**
	 * Loading classes from RMI server specified by configuration.
	 * @param config server configuration.
	 * @param outAlgList list of algorithms as output.
	 */
	private void loadClassesFromRmiServer(DataConfig config, List<Alg> outAlgList) {
		xURI storeUri = config.getStoreUri();
		if (storeUri == null) return;
		Service service = ClientUtil.getRemoteService(storeUri.getHost(), storeUri.getPort(), config.getStoreAccount(), config.getStorePassword().getText());
		if (service == null) return;
		
		String[] algNames = new String[0];
		try {
			algNames = service.getAlgNames();
		}
		catch (Throwable e) {
			LogUtil.error("Retrieving remote algorithm names error by: " + e.getMessage());
		}
		if (algNames == null) algNames = new String[0];
		
		RegisterTable normalReg = PluginStorage.getNormalAlgReg();
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (String algName : algNames) {
			if (normalReg.contains(algName)) continue;
			
			Alg alg = null;
			try {
				alg = service.getAlg(algName);
			}
			catch (Throwable e) {
				LogUtil.error("Retrieving remote algorithm error by: " + e.getMessage());
				alg = null;
			}
			if (alg == null) continue;
			
			int idx = nextUpdateList.indexOf(algName);
			if (idx < 0)
				outAlgList.add(alg);
			else {
				Alg nextUpdateAlg = nextUpdateList.get(idx);
				if (PluginStorage.lookupAlgTypeName(nextUpdateAlg.getClass()) != PluginStorage.lookupAlgTypeName(alg.getClass()))
					outAlgList.add(alg);
			}
		}
	}
	
	
	/**
	 * Loading classes from socket server specified by configuration.
	 * @param config server configuration.
	 * @param outAlgList list of algorithms as output.
	 */
	private void loadClassesFromSocketServer(DataConfig config, List<Alg> outAlgList) {
		xURI storeUri = config.getStoreUri();
		if (storeUri == null) return;
		
		SocketConnection service = null;
		String[] algNames = new String[0];
		try {
			service = ClientUtil.getSocketConnection(storeUri.getHost(), storeUri.getPort(),config.getStoreAccount(), config.getStorePassword().getText());
			algNames = service.getAlgNames();
			service.close(); service = null;
		}
		catch (Throwable e) {
			LogUtil.error("Retrieving remote algorithm names error by: " + e.getMessage());
		}
		if (algNames == null) algNames = new String[0];
		
		RegisterTable normalReg = PluginStorage.getNormalAlgReg();
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (String algName : algNames) {
			if (normalReg.contains(algName)) continue;
			
			Alg alg = null;
			try {
				service = ClientUtil.getSocketConnection(storeUri.getHost(), storeUri.getPort(),config.getStoreAccount(), config.getStorePassword().getText());
				alg = service.getAlg(algName);
				service.close(); service = null;
			}
			catch (Throwable e) {
				LogUtil.error("Retrieving remote algorithm error by: " + e.getMessage());
				alg = null;
			}
			if (alg == null) continue;
			
			int idx = nextUpdateList.indexOf(algName);
			if (idx < 0)
				outAlgList.add(alg);
			else {
				Alg nextUpdateAlg = nextUpdateList.get(idx);
				if (PluginStorage.lookupAlgTypeName(nextUpdateAlg.getClass()) != PluginStorage.lookupAlgTypeName(alg.getClass()))
					outAlgList.add(alg);
			}
		}
		
		if (service != null) service.close();
	}

	
	/**
	 * Loading classes from store.
	 * @param storeUri store URI.
	 * @param outAlgList list of algorithms as output.
	 */
	private void loadClassesFromStore(xURI storeUri, List<Alg> outAlgList) {
		if (storeUri == null) return;

		List<Alg> algList = Firer.getInstances(storeUri, Alg.class);
		RegisterTable normalReg = PluginStorage.getNormalAlgReg();
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (Alg alg : algList) {
			if (normalReg.contains(alg.getName())) continue;
			
			int idx = nextUpdateList.indexOf(alg.getName());
			if (idx < 0)
				outAlgList.add(alg);
			else {
				Alg nextUpdateAlg = nextUpdateList.get(idx);
				if (PluginStorage.lookupAlgTypeName(nextUpdateAlg.getClass()) != PluginStorage.lookupAlgTypeName(alg.getClass()))
					outAlgList.add(alg);
			}
		}
		
	}
	
	
	/**
	 * Transferring selected algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 */
	protected void leftToRight() {
		List<Alg> list = leftList.removeSelectedList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithm not selected or empty list", 
					"Algorithm not selected or empty list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		rightList.addAll(list);
		
	}
	

	/**
	 * Transferring all algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 */
	protected void leftToRightAll() {
		List<Alg> list = leftList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		rightList.addAll(list);
		leftList.clear();
	}

	
	/**
	 * Transferring selected algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 */
	protected void rightToLeft() {
		List<Alg> list = rightList.removeSelectedList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithm not selected or empty list", 
					"Algorithm not selected or empty list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		leftList.addAll(list);
	}
	

	/**
	 * Transferring all algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 */
	protected void rightToLeftAll() {
		List<Alg> list = rightList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		leftList.addAll(list);
		rightList.clear();
	}

	
	/**
	 * Event-driven method response to OK button command.
	 */
	protected void ok() {
		List<Alg> selectedAlgList = this.rightList.getAlgList();
		if (selectedAlgList.size() == 0) {
			if (leftList.getAlgList().size() > 0) {
				JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.ERROR_MESSAGE);
			}
			
			dispose();
			return;
		}
		
		for (Alg selectedAlg : selectedAlgList) {
			if (selectedAlg instanceof AlgRemoteWrapper)
				((AlgRemoteWrapper)selectedAlg).setExclusive(true);
			
			if (selectedAlg != null)
				PluginStorage.getNextUpdateList().add(selectedAlg);
		}
		this.result = PluginStorage.getNextUpdateList();
		this.rightList.clear();
		
		List<Alg> remainAlgs = this.leftList.getAlgList();
		for (Alg remainAlg : remainAlgs) {
			if (remainAlg instanceof AlgRemoteWrapper) {
				((AlgRemoteWrapper)remainAlg).setExclusive(true);
				try {
					((AlgRemoteWrapper)remainAlg).unexport();
				} catch (Throwable e) {e.printStackTrace();}
			}
		}
		this.leftList.clear();
		
		ok = true;
		dispose();
	}
	
	
	/**
	 * Getting the result as list of chosen algorithms.
	 * @return result as list of chosen algorithms.
	 */
	public AlgList getResult() {
		return result;
	}
	
	
	/**
	 * Checking whether or not the OK button is pressed.
	 * @return whether or not the OK button is pressed.
	 */
	public boolean isOK() {
		return ok;
	}


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		List<Alg> algList = this.leftList.getAlgList();
		algList.addAll(this.rightList.getAlgList());
		for (Alg alg : algList) {
			if (alg instanceof AlgRemoteWrapper) {
				((AlgRemoteWrapper)alg).setExclusive(true);
				try {
					((AlgRemoteWrapper)alg).unexport();
				} catch (Throwable e) {e.printStackTrace();}
			}
		}
		
		this.leftList.clear();
		this.rightList.clear();
		
		super.dispose();
	}

	
}
