/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.Reader;
import java.io.Writer;
import java.rmi.Remote;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.Constants;
import net.hudup.core.PluginManager;
import net.hudup.core.Util;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.data.BatchScript;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.DatasetPoolExchangedItem;
import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.data.DatasetPoolsServiceImpl;
import net.hudup.core.data.NullPointer;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.logistic.ui.WaitDialog;
import net.hudup.core.parser.DatasetParser;

/**
 * This is dataset pool manager.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
public class DatasetPoolsManager extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Dataset pool service.
	 */
	protected DatasetPoolsService poolsService = null;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = new ConnectInfo();
	
	
	/**
	 * Pools list
	 */
	protected DatasetPoolsList poolList = null;

	
	/**
	 * Pool table.
	 */
	protected DatasetPoolTable poolTable = null;
	
	
	/**
	 * Adding dataset button.
	 */
	protected JButton btnAddDataset = null;
	
	
	/**
	 * Loading batch script button.
	 */
	protected JButton btnLoadBatchScript = null;
	
	
	/**
	 * Uploading button.
	 */
	protected JButton btnUpload = null;
	
	
	/**
	 * Refreshing button.
	 */
	protected JButton btnRefreshPool = null;
	
	
	/**
	 * Clearing pool button.
	 */
	protected JButton btnClearPool = null;
	
	
	/**
	 * Selected dataset pool.
	 */
	protected DatasetPoolExchangedItem selectedPool = null;
	
	
	/**
	 * Constructor with pool manager.
	 * @param poolsService specified pool manager.
	 * @param connectInfo connection information.
	 * @param comp parent component.
	 */
	public DatasetPoolsManager(DatasetPoolsService poolsService, ConnectInfo connectInfo, Component comp) {
		super(UIUtil.getDialogForComponent(comp), "Dataset pool manager", true);
		this.poolsService = poolsService;
		if (connectInfo != null) this.connectInfo = connectInfo;
		if (this.connectInfo.bindUri != null) {
			try {
				PluginManager pm = Util.getPluginManager();
				pm.discover(DatasetParser.class);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());

		DatasetPoolsManager thisManager = this;
		setJMenuBar(createMenuBar());
		
		JPanel left = new JPanel(new BorderLayout());
		add(left, BorderLayout.WEST);
		
		poolList = new DatasetPoolsList(poolsService);
		poolList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu == null) return;
					contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		poolList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onPoolListChanged();
			}
		});
		poolList.getModel().addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent e) {}
			
			@Override
			public void intervalAdded(ListDataEvent e) {}
			
			@Override
			public void contentsChanged(ListDataEvent e) {}
		});
		left.add(new JScrollPane(poolList), BorderLayout.CENTER);
		
		JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		left.add(leftToolbar, BorderLayout.SOUTH);
		
		JButton btnAddPool = UIUtil.makeIconButton("add-16x16.png", "addpool", "Add pool", "Add pool",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					addPool();
				}
			});
		btnAddPool.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnAddPool);

		JButton btnDeletePool = UIUtil.makeIconButton("delete-16x16.png", "deletepool", "Delete pool", "Delete pool",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					DatasetPoolExchangedItem pool = poolList.getSelectedValue();
					if (pool == null) {
						JOptionPane.showMessageDialog(thisManager, "No pool selected", "No pool selected", JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					try {
						poolsService.remove(pool.getName());
						poolList.update();
					} catch (Throwable ex) {LogUtil.trace(ex);}
				}
			});
		btnDeletePool.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnDeletePool);

		JButton btnRefreshAll = UIUtil.makeIconButton("refresh-16x16.png", "refreshall", "Refresh all", "Refresh all",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					poolList.update();
				}
			});
		btnRefreshAll.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnRefreshAll);
					
		JButton btnClearAll = UIUtil.makeIconButton("clear-16x16.png", "clearall", "Clear all", "Clear all",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						poolsService.clear();
					} catch (Throwable ex) {LogUtil.trace(ex);}
					
					poolList.update();
				}
			});
		btnClearAll.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnClearAll);


		JPanel right = new JPanel(new BorderLayout());
		add(right, BorderLayout.CENTER);
		
		poolTable = new DatasetPoolTable(false, this.connectInfo.bindUri) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean removeSelectedRows() {
				DatasetPoolExchangedItem item = poolList.getSelectedValue();
				if (item == null) return false;
				boolean ret = super.removeSelectedRows();
				if (!ret) return false;

				poolTable.update(new DatasetPool());
				updateLocalServicePool(false);
				
				enableControls(true);
				JOptionPane.showMessageDialog(this, "Remove rows successfully.\nYou need to upload/scatter the pool change.", "Remove successfully rows", JOptionPane.INFORMATION_MESSAGE);
				
				return true;
			}
			
			@Override
			public void saveScript() {
				saveBatchScript();
			}

			@Override
			protected void addScript() {
				loadBatchScript(true);
			}

			@Override
			protected void addTraining() {
				addDataset(false, true);
			}

		};
		right.add(new JScrollPane(poolTable), BorderLayout.CENTER);
		
		JPanel rightToolbar = new JPanel(new BorderLayout());
		right.add(rightToolbar, BorderLayout.SOUTH);

		JPanel rightToolbar1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		rightToolbar.add(rightToolbar1, BorderLayout.WEST);

		btnAddDataset = new JButton(I18nUtil.message("add_dataset"));
		btnAddDataset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addDataset(false, false);
			}
		});
		btnAddDataset.setMnemonic('a');
		rightToolbar1.add(btnAddDataset);
		
		btnLoadBatchScript = new JButton(I18nUtil.message("load_script"));
		btnLoadBatchScript.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loadBatchScript(false);
			}
		});
		btnLoadBatchScript.setMnemonic('l');
		rightToolbar1.add(btnLoadBatchScript);
		
		btnUpload = UIUtil.makeIconButton(
			this.connectInfo.bindUri == null ? "scatter-16x16.png" : "upload-16x16.png", 
			this.connectInfo.bindUri == null ? "scatter" : "upload", 
			this.connectInfo.bindUri == null ? I18nUtil.message("scatter") : I18nUtil.message("upload"), 
			this.connectInfo.bindUri == null ? I18nUtil.message("scatter") : I18nUtil.message("upload"), 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					upload();
				}
			});
		btnUpload.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar1.add(this.btnUpload);
		
		JPanel rightToolbar2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightToolbar.add(rightToolbar2, BorderLayout.EAST);
		
		btnRefreshPool = UIUtil.makeIconButton("refresh-16x16.png", "refreshpool", "Refresh pool", "Refresh pool",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					refreshPool();
				}
			});
		btnRefreshPool.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar2.add(btnRefreshPool);

		btnClearPool = UIUtil.makeIconButton("clear-16x16.png", "clearpool", "Clear pool", "Clear pool",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					clearTablePool();
				}
			});
		btnClearPool.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar2.add(btnClearPool);

		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		footer.add(btnOK);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thisManager.dispose();
			}
		});
		footer.add(btnCancel);
		
		onPoolListChanged();
	}

	
	/**
	 * Creating main menu bar.
	 * @return main menu bar.
	 */
	protected JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu("Tool");
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);
		
		return mnBar;
	}

	
	/**
	 * Creating context menu.
	 * @return pop-up menu.
	 */
	protected JPopupMenu createContextMenu() {
		DatasetPoolExchangedItem item = poolList.getSelectedValue();
		if (item == null) return null;

		JPopupMenu ctxMenu = new JPopupMenu();
		DatasetPoolsManager thisManager = this;
		
		JMenuItem miListClients = UIUtil.makeMenuItem((String)null, "List clients", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisManager, "Not implemented yet");
				}
			});
		ctxMenu.add(miListClients);

		JMenuItem miAttachClient = UIUtil.makeMenuItem((String)null, "Attach client", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisManager, "Not implemented yet");
				}
			});
		ctxMenu.add(miAttachClient);

		JMenuItem miDetachClient = UIUtil.makeMenuItem((String)null, "Detach client", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					JOptionPane.showMessageDialog(thisManager, "Not implemented yet");
				}
			});
		ctxMenu.add(miDetachClient);

		return ctxMenu;
	}
	
	
	/**
	 * Driven function for pool selection.
	 */
	private void onPoolListChanged() {
		poolTable.update(new DatasetPool());
		enableControls(false);
		
		DatasetPoolExchangedItem item = poolList.getSelectedValue();
		if (item == null || item.getPool() == null) return;
		
		DatasetPool pool = item.getPool().toDatasetPoolClient();
		poolTable.update(pool);
		
		enableControls(true);
	}
	
	
	/**
	 * Enabling controls.
	 * @param flag enabling flag.
	 */
	private void enableControls(boolean flag) {
		poolTable.setEnabled2(flag);
		btnAddDataset.setEnabled(flag);
		btnLoadBatchScript.setEnabled(flag);
		btnUpload.setEnabled(flag);
		btnRefreshPool.setEnabled(flag);
		btnClearPool.setEnabled(flag && poolTable.getPool().size() > 0);
		
	}
	
	/**
	 * Adding pool.
	 */
	private void addPool() {
		String poolName = JOptionPane.showInputDialog(this, "Enter pool name", "Pool" + System.currentTimeMillis());
		if (poolName == null || poolName.isEmpty()) return;
		poolName = poolName.trim();
		if (poolName.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Invalid pool name", "Invalid pool name", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			if (poolsService.contains(poolName)) {
				JOptionPane.showMessageDialog(this, "Pool name exists", "Pool name exists", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			DatasetPoolExchanged pool = new DatasetPoolExchanged();
			boolean ret = poolsService.put(poolName, pool);
			if (!ret) {
				JOptionPane.showMessageDialog(this, "Fail to add pool", "Failed adding pool", JOptionPane.ERROR_MESSAGE);
				return;
			}
			poolList.update();
			poolList.selectPool(poolName);
			
		} catch(Exception e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * Uploading pool.
	 */
	private void upload() {
		DatasetPoolExchangedItem item = poolList.getSelectedValue();
		if (item == null) return;
		DatasetPool pool = poolTable.getPool();
		if (pool == null) return;
		
		String poolName = item.getName();
		if (connectInfo.bindUri != null) {
			try {
				DatasetPoolExchanged expool = pool.toDatasetPoolExchangedClient(connectInfo);
				if (expool != null) poolsService.put(poolName, expool);
			} catch (Exception e) {LogUtil.trace(e);}
		}
		
		resetClients();
		
		poolList.update();
		poolList.selectPool(poolName);
		
		JOptionPane.showMessageDialog(this, "Upload/scatter successfully", "Upload/scatter successfully", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Updating local pool.
	 * @param replace replacing flag.
	 */
	private void updateLocalServicePool(boolean replace) {
		if (connectInfo.bindUri != null) return;

		DatasetPoolExchangedItem item = poolList.getSelectedValue();
		if (item == null) return;
		DatasetPool pool = poolTable.getPool();
		if (pool == null) return;

		String poolName = item.getName();
		try {
			DatasetPoolExchanged expool = pool.toDatasetPoolExchangedClient(connectInfo);
			if (expool == null) return;
			
			if (replace)
				poolsService.replacePool(poolName, expool);
			else
				poolsService.put(poolName, expool);
		} catch (Exception e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * Resetting clients.
	 */
	private void resetClients() {
		DatasetPoolExchangedItem item = poolList.getSelectedValue();
		if (item == null) return;

		for (int i = 0; i < item.getClientSize(); i++) {
			Remote client = item.getClient(i).getClient();
			if (client == null || !(client instanceof Evaluator)) continue;
			try {
				Evaluator evaluator = (Evaluator)client;
				evaluator.remoteStop();
				evaluator.reloadPool(null, new Timestamp());
			} catch (Exception e) {LogUtil.trace(e);}
		}
	}
	
	
	/**
	 * Refreshing pool.
	 */
	private void refreshPool() {
		DatasetPoolExchangedItem item = poolList.getSelectedValue();
		if (item == null) return;
		
		item.getPool().reload();
		item.getPool().fillMissingUUID();
		
		for (int i = 0; i < item.getClientSize(); i++) {
			Remote client = item.getClient(i).getClient();
			if (client == null || !(client instanceof Evaluator)) continue;
			try {
				Evaluator evaluator = (Evaluator)client;
				evaluator.remoteStop();
				evaluator.reloadPool(null, new Timestamp());
			} catch (Exception e) {LogUtil.trace(e);}
		}
		
		String poolName = item.getName();
		poolList.update();
		poolList.selectPool(poolName);
	}
	
	
	/**
	 * Clearing table pool.
	 */
	private void clearTablePool() {
		DatasetPoolExchangedItem item = poolList.getSelectedValue();
		if (item == null) return;

		poolTable.update(new DatasetPool());
		updateLocalServicePool(true);
		
		enableControls(true);
		JOptionPane.showMessageDialog(this, "Clear successfully batch.\nYou need to upload/scatter the pool change.", "Clear successfully batch", JOptionPane.INFORMATION_MESSAGE);
	}
	

	/**
	 * Load batch script.
	 * @param append Flag to indicate whether to append to current pool.
	 */
	private void loadBatchScript(boolean append) {
		try {
			DatasetPoolExchangedItem item = poolList.getSelectedValue();
			if (item == null) return;

			String defaultUnit = JOptionPane.showInputDialog(this, "Enter main unit", DataConfig.RATING_UNIT);
			if (defaultUnit == null || defaultUnit.isEmpty()) return;
			defaultUnit = defaultUnit.trim();
			if (defaultUnit.isEmpty()) {
				JOptionPane.showMessageDialog(this, "Invalid main unit", "Invalid main unit", JOptionPane.ERROR_MESSAGE);
				return;
			}

			UriAdapter adapter = new UriAdapter();
			xURI uri = adapter.chooseUri(
				this, 
				true, 
				new String[] {"properties", "script", Constants.DEFAULT_EXT}, 
				new String[] {"Properties files (*.properties)", "Script files (*.script)", "Hudup files (*." + Constants.DEFAULT_EXT + ")"},
				null,
				null);
			adapter.close();
			
			if (uri == null) {
				JOptionPane.showMessageDialog(
					this, 
					"URI not open", 
					"URI not open", 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			adapter = new UriAdapter(uri);
			Reader reader = adapter.getReader(uri);
			JDialog dlgWait = WaitDialog.createDialog(this); dlgWait.setUndecorated(true);
			final String mainUnit = defaultUnit;
			SwingWorker<BatchScript, BatchScript> worker = new SwingWorker<BatchScript, BatchScript>() {
				@Override
				protected BatchScript doInBackground() throws Exception {
					return BatchScript.parse(reader, mainUnit);
				}
				
				@Override
				protected void done() {
					super.done(); dlgWait.dispose();
				}
			};
			worker.execute(); dlgWait.setVisible(true);
			BatchScript script = worker.get();
			reader.close();
			adapter.close();
			
			if (script == null) {
				JOptionPane.showMessageDialog(
					this, 
					"Batch not created", 
					"Batch not created", 
					JOptionPane.ERROR_MESSAGE);
				return;
			}

			DatasetPool pool = append ? poolTable.getPool() : new DatasetPool();
			DatasetPool scriptPool = script.getPool();
			for (int i = 0; i < scriptPool.size(); i++) {
				DatasetPair pair = scriptPool.get(i);
				if (pair == null || pair.getTraining() == null) continue;
				
				pool.add(pair);
			}
			poolTable.update(pool);
			updateLocalServicePool(true);
			
			JOptionPane.showMessageDialog(this, "Load successfully batch.\nYou need to upload/scatter the pool change.", "Load successfully batch", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Saving batch script.
	 */
	private void saveBatchScript() {
		String mainUnit = JOptionPane.showInputDialog(this, "Enter main unit", DataConfig.RATING_UNIT);
		if (mainUnit == null || mainUnit.isEmpty()) return;
		mainUnit = mainUnit.trim();
		if (mainUnit.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Invalid main unit", "Invalid main unit", JOptionPane.ERROR_MESSAGE);
			return;
		}

		UriAdapter adapter = new UriAdapter();
        xURI uri = adapter.chooseUri(
			this, 
			false, 
			new String[] {"properties", "script", Constants.DEFAULT_EXT}, 
			new String[] {"Properties files (*.properties)", "Script files (*.script)", "Hudup files (*." + Constants.DEFAULT_EXT + ")"},
			null,
			null);
        adapter.close();
        
        if (uri == null) {
			JOptionPane.showMessageDialog(
				this, 
				"URI not save", 
				"URI not save", 
				JOptionPane.WARNING_MESSAGE);
			return;
        }
        
		adapter = new UriAdapter(uri);
		boolean existed = adapter.exists(uri);
		adapter.close();
        if (existed) {
        	int ret = JOptionPane.showConfirmDialog(
    			this, 
    			"URI exist. Do you want to override it?", 
    			"URI exist", 
    			JOptionPane.YES_NO_OPTION, 
    			JOptionPane.QUESTION_MESSAGE);
        	if (ret == JOptionPane.NO_OPTION) return;
        }
		
		adapter = null;
		Writer writer = null;
        try {
			adapter = new UriAdapter(uri);
    		writer = adapter.getWriter(uri, false);
    		
			BatchScript script = BatchScript.assign(poolTable.getPool(), Util.newList(), mainUnit);
			script.saveEasy(writer);
    		writer.flush();
    		writer.close();
    		writer = null;
	        
        	JOptionPane.showMessageDialog(this, "URI saved successfully", "URI saved successfully", JOptionPane.INFORMATION_MESSAGE);
        }
		catch(Exception e) {
			LogUtil.trace(e);
		}
        finally {
        	try {
        		if (writer != null) writer.close();
        	}
        	catch (Exception e) {LogUtil.trace(e);}
        	
        	if (adapter != null) adapter.close();
        }
	}
	
	
	/**
	 * Adding dataset.
	 * @param nullTraining true if add null training dataset {@link NullPointer}.
	 * @param nullTesting true if add null testing dataset {@link NullPointer}.
	 */
	private void addDataset(boolean nullTraining, boolean nullTesting) {
		String mainUnit = JOptionPane.showInputDialog(this, "Enter main unit", DataConfig.RATING_UNIT);
		if (mainUnit == null || mainUnit.isEmpty()) return;
		mainUnit = mainUnit.trim();
		if (mainUnit.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Invalid main unit", "Invalid main unit", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			DatasetPool pool = poolTable.getPool();
			pool = pool != null ? pool : new DatasetPool();
			if (nullTraining && nullTesting) {
				DatasetPair pair = new DatasetPair(new NullPointer(), new NullPointer(), null);
				pool.add(pair);
			}
			else {
				AddingDatasetDlg2 adder = new AddingDatasetDlg2(this, pool, Util.newList(), mainUnit, connectInfo.bindUri);
				adder.setMode(nullTraining, nullTesting);
				adder.setVisible(true);
			}
			
			poolTable.update(pool);
			updateLocalServicePool(false);
			
			enableControls(true);
			JOptionPane.showMessageDialog(this, "Add dataset successfully.\nYou need to upload/scatter the pool change.", "Add successfully dataset", JOptionPane.INFORMATION_MESSAGE);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}


	/**
	 * Driven function for OK button.
	 */
	private void onOK() {
		this.selectedPool = poolList.getSelectedValue();
		dispose();
	}
	
	
	/**
	 * Getting selected dataset pool.
	 * @return selected dataset pool.
	 */
	public DatasetPoolExchangedItem getSelectedPool() {
		return selectedPool;
	}
	
	
	/**
	 * Showing pools manager.
	 * @param poolsService pool manager.
	 * @param connectInfo connection information.
	 * @param comp parent component.
	 * @return dataset pools manager.
	 */
	public static DatasetPoolsManager show(DatasetPoolsService poolsService, ConnectInfo connectInfo, Component comp) {
		DatasetPoolsManager manager = new DatasetPoolsManager(poolsService, connectInfo, comp);
		manager.setVisible(true);
		return manager;
	}
	
	
	/**
	 * Main method.
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		Util.getPluginManager().fire();
		DatasetPoolsServiceImpl service = new DatasetPoolsServiceImpl(null);
		DatasetPoolsManager.show(service, null, null);
		try {
			service.close();
		} catch (Exception e) {LogUtil.trace(e);}
	}
	
	
 }
