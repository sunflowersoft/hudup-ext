/**
 * 
 */
package net.hudup.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.PluginStorage;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.ModelBasedRecommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.KBasePointer;
import net.hudup.core.data.NullPointer;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * Java table shows dataset pool.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class DatasetPoolTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Enabled flag.
	 */
	protected boolean enabled = true;
	
	
	/**
	 * Default constructor.
	 */
	public DatasetPoolTable() {
		// TODO Auto-generated constructor stub
		super(new DatasetPoolTableModel());
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				DatasetPool pool = getPoolTableModel().getPool();
				if(SwingUtilities.isRightMouseButton(e) && pool != null && pool.size() > 0) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) {
						addToContextMenu(contextMenu);
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
					}
				}
			}
		});
		
		addKeyListener(new KeyAdapter() {

			@Override
			public void keyPressed(KeyEvent e) {
				// TODO Auto-generated method stub
				if (e.getKeyCode() == KeyEvent.VK_DELETE)
					removeSelectedRows();
			}
			
		});
		
	}
	
	
	/**
	 * Getting this table.
	 * @return this {@link DatasetPoolTable}.
	 */
	private DatasetPoolTable getThis() {
		return this;
	}
	
	
	/**
	 * Update this table by specified dataset pool.
	 * @param pool specified dataset pool.
	 */
	public void update(DatasetPool pool) {
		getPoolTableModel().update(pool);
		
		setupUI();
	}
	
	
	/**
	 * Setting up graphic user interface.
	 */
	private void setupUI() {
		if (getColumnCount() > 0)
			getColumnModel().getColumn(0).setMaxWidth(50);
		
		if (getColumnCount() > 3)
			getColumnModel().getColumn(3).setMaxWidth(80);
	}

	
	/**
	 * Getting model of this table.
	 * @return {@link DatasetPoolTableModel}
	 */
	public DatasetPoolTableModel getPoolTableModel() {
		return (DatasetPoolTableModel)getModel();
	}
	
	
	/**
	 * Getting dataset pool.
	 * @return internal {@link DatasetPool}.
	 */
	public DatasetPool getPool() {
		return getPoolTableModel().getPool();
	}
	
	
	/**
	 * Creating context menu.
	 * @return {@link JPopupMenu} as context menu.
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miSave = UIUtil.makeMenuItem((String)null, "Save script", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					save();
				}
			});
		contextMenu.add(miSave);

		final int selectedRow = getSelectedRow();
		final int selectedColumn = getSelectedColumn();
		if (selectedRow == -1) return contextMenu;
		
		Object cellValue = selectedColumn == -1 ? null : getValueAt(selectedRow, selectedColumn);
		if (cellValue != null && !cellValue.toString().isEmpty()) {
			contextMenu.addSeparator();
			JMenuItem miCopyURI = UIUtil.makeMenuItem((String)null, "Copy URL", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						ClipboardUtil.util.setText(cellValue.toString());
					}
				});
			contextMenu.add(miCopyURI);
		}
		
		Dataset ds = null;
		if (selectedColumn != -1) {
			DatasetPoolTableModel model = getPoolTableModel();
			DatasetPool pool = model.getPool();
			DatasetPair pair = pool.get(selectedRow);
			
			if (pair != null) {
				if (selectedColumn == 1) {
					ds = pair.getTraining();
				}
				else if (selectedColumn == 2) {
					ds = pair.getTesting();
				}
				else if (selectedColumn == 3) {
					ds = pair.getWhole();
				}
			}
		}
		
		final Dataset dataset = ds;
		if (dataset != null) {
			if (!(dataset instanceof Pointer) ) {
				contextMenu.addSeparator();
				
				JMenuItem miMetadata = UIUtil.makeMenuItem((String)null, "View metadata", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							
							DatasetMetadataTable.showDlg(getThis(), dataset);
							
						} // end action
						
					});
				
				contextMenu.add(miMetadata);

				JMenuItem miData = UIUtil.makeMenuItem((String)null, "View data", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							int confirm = JOptionPane.showConfirmDialog(
									getThis(), 
									"Be careful, out of memory in case of huge dataset", 
									"Out of memory in case of huge dataset", 
									JOptionPane.OK_CANCEL_OPTION, 
									JOptionPane.WARNING_MESSAGE);
							
							if (confirm == JOptionPane.OK_OPTION)
								new DatasetViewer(getThis(), dataset);
						}
					});
				
				contextMenu.add(miData);
			}
			else if (dataset instanceof KBasePointer) {
				contextMenu.addSeparator();

				JMenuItem miData = UIUtil.makeMenuItem((String)null, "View knowledge base", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							int confirm = JOptionPane.showConfirmDialog(
									getThis(), 
									"Be careful, out of memory in case of huge knowledge base", 
									"Out of memory in case of huge dataset", 
									JOptionPane.OK_CANCEL_OPTION, 
									JOptionPane.WARNING_MESSAGE);
							if (confirm != JOptionPane.OK_OPTION)
								return;
							
							try {
								DataConfig config = (DataConfig) dataset.getConfig().clone(); 
								xURI store = config.getStoreUri();
								xURI cfgUri = store.concat(KBase.KBASE_CONFIG);
								config.load(cfgUri);
								
								String kbaseName = config.getAsString(KBase.KBASE_NAME);
								if (kbaseName == null)
									throw new Exception("KBase not viewed");
								
								Alg alg = PluginStorage.getNormalAlgReg().query(kbaseName);
								if (alg == null || !(alg instanceof ModelBasedRecommender))
									throw new Exception("KBase not viewed");
								
								ModelBasedRecommender recommender = (ModelBasedRecommender) alg.newInstance();
								KBase kbase = recommender.newKBase(dataset);
								kbase.getInspector().inspect();
								kbase.close();
							}
							catch (Throwable ex) {
								ex.printStackTrace();
								JOptionPane.showMessageDialog(
										getThis(), 
										"KBase not viewed", 
										"KBase not viewed", 
										JOptionPane.ERROR_MESSAGE);
							} // end try-catch
								
						} //end actionPerformed
						
					}); //end ActionListener
				contextMenu.add(miData);
				
			}
		}
		

		if (!isEnabled2()) return contextMenu;
		
		contextMenu.addSeparator();
		JMenuItem miRemoveRow = UIUtil.makeMenuItem((String)null, "Remove selected row(s)", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					removeSelectedRows();
				}
			});
		contextMenu.add(miRemoveRow);
		
		if (getRowCount() > 1) {
			contextMenu.addSeparator();
			
			if (selectedRow == 0) {
				JMenuItem miMoveDown = UIUtil.makeMenuItem((String)null, "Move down", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							moveRowDown(selectedRow);
						}
					});
				contextMenu.add(miMoveDown);
				
				JMenuItem miMoveLast = UIUtil.makeMenuItem((String)null, "Move last", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							moveRowLast(selectedRow);
						}
					});
				contextMenu.add(miMoveLast);
			}
			else if (selectedRow == getRowCount() - 1) {
				JMenuItem miMoveFirst = UIUtil.makeMenuItem((String)null, "Move first", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							moveRowFirst(selectedRow);
						}
					});
				contextMenu.add(miMoveFirst);
				
				JMenuItem miMoveUp = UIUtil.makeMenuItem((String)null, "Move up", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							moveRowUp(selectedRow);
						}
					});
				contextMenu.add(miMoveUp);
			}
			else {
				JMenuItem miMoveFirst = UIUtil.makeMenuItem((String)null, "Move first", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							moveRowFirst(selectedRow);
						}
					});
				contextMenu.add(miMoveFirst);
					
				JMenuItem miMoveUp = UIUtil.makeMenuItem((String)null, "Move up", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							moveRowUp(selectedRow);
						}
					});
				contextMenu.add(miMoveUp);
				
				JMenuItem miMoveDown = UIUtil.makeMenuItem((String)null, "Move down", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							moveRowDown(selectedRow);
						}
					});
				contextMenu.add(miMoveDown);
				
				JMenuItem miMoveLast = UIUtil.makeMenuItem((String)null, "Move last", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							moveRowLast(selectedRow);
						}
					});
				contextMenu.add(miMoveLast);
			}
			
		}

		return contextMenu;
	}
	
	
	/**
	 * Adding menu items to context menu.
	 * @param contextMenu context menu.
	 */
	protected void addToContextMenu(JPopupMenu contextMenu) {
		
	}
	
	
	/**
	 * Removing selected rows.
	 * @return whether remove successfully.
	 */
	public boolean removeSelectedRows() {
		int[] idxes = this.getSelectedRows();
		if (idxes == null || idxes.length == 0) {
			JOptionPane.showMessageDialog(
				this, 
				"No selected row", 
				"No selected row", 
				JOptionPane.WARNING_MESSAGE);
			return false;
		}
		
		DatasetPoolTableModel model = getPoolTableModel();
		DatasetPool pool = model.getPool();
		pool.remove(idxes);
		model.update(pool);
		setupUI();
		
		return true;
	}
	
	
	/**
	 * Moving the row having specified index to the first.
	 * @param rowIndex specified row index.
	 */
	private void moveRowFirst(int rowIndex) {
		if (rowIndex == 0 || getRowCount() == 0)
			return;
		
		moveRow(rowIndex, rowIndex, 0);
	}

	
	/**
	 * Moving the row having specified index to up level.
	 * @param rowIndex specified row index.
	 */
	private void moveRowUp(int rowIndex) {
		if (rowIndex == 0 || getRowCount() == 0)
			return;
		
		moveRow(rowIndex, rowIndex, rowIndex - 1);
	}

	
	/**
	 * Move the row at specified index down.
	 * @param rowIndex specified index.
	 */
	private void moveRowDown(int rowIndex) {
		if (rowIndex == getRowCount() - 1 || getRowCount() == 0)
			return;
		
		moveRow(rowIndex, rowIndex, rowIndex + 1);
	}
	
	
	/**
	 * Move the row at specified to last.
	 * @param rowIndex specified index.
	 */
	private void moveRowLast(int rowIndex) {
		if (rowIndex == getRowCount() - 1 || getRowCount() == 0)
			return;
		
		moveRow(rowIndex, rowIndex, getRowCount() - 1);
	}

	
	/**
	 * Move rows (from specified starting index and ending index) to specified location.
	 * @param start specified starting index.
	 * @param end specified ending index.
	 * @param to specified location.
	 */
	private void moveRow(int start, int end, int to) {
		DatasetPoolTableModel model = getPoolTableModel();
		DatasetPool pool = model.getPool();
		
		pool.moveRow(start, end, to);
		model.update(pool);
		setupUI();
	}
	
	
	/**
	 * Save data.
	 */
	public void save() {
		JOptionPane.showMessageDialog(this, 
				"Not implement yet", "Not implement yet", JOptionPane.WARNING_MESSAGE);
	}

	
	/**
	 * Setting optionally enabled flag.
	 * @param enabled optionally enabled flag.
	 */
	public void setEnabled2(boolean enabled) {
		// TODO Auto-generated method stub
		this.enabled = enabled;
	}

	
	/**
	 * Getting optionally enabled flag.
	 * @return whether optionally enabled.
	 */
	public boolean isEnabled2() {
		// TODO Auto-generated method stub
		return enabled;
	}
	
	
}



/**
 * This class is Java table model of dataset pool.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
class DatasetPoolTableModel extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal dataset pool.
	 */
	protected DatasetPool pool = null;
	
	
	/**
	 * Default constructor.
	 */
	public DatasetPoolTableModel() {
		super();
	}
	
	
	/**
	 * Update this model by specified dataset pool.
	 * @param pool specified dataset pool.
	 */
	public void update(DatasetPool pool) {
		this.pool = pool;
		
		Vector<String> columns = new Vector<String>();
		columns.add("No");
		columns.add(I18nUtil.message("training_set"));
		columns.add(I18nUtil.message("testing_set"));
		columns.add(I18nUtil.message("whole_set"));
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < pool.size(); i++) {
			DatasetPair pair = pool.get(i);
			
			Vector<Object> row = new Vector<Object>();
			
			row.add(i + 1);
			
			Dataset trainingSet = pair.getTraining();
			if (trainingSet != null)
				row.add(trainingSet.getConfig().getUriId().toString());
			else
				row.add("");
			
			Dataset testingSet = pair.getTesting();
			if (testingSet != null) {
				xURI uriId = testingSet.getConfig() != null ? testingSet.getConfig().getUriId() : null;
				if (uriId == null) //Null pointer
					row.add(NullPointer.NULL_POINTER);
				else
					row.add(uriId.toString());
			}
			else
				row.add("");
			
			if (pair.getWhole() != null)
				row.add(pair.getWhole().getConfig().getUriId().toString());
			else
				row.add("");
			
			data.add(row);
			
		}
		
		this.setDataVector(data, columns);
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
	/**
	 * Getting dataset pool.
	 * @return {@link DatasetPool} as dataset pool.
	 */
	public DatasetPool getPool() {
		return pool;
	}
	
	
}

