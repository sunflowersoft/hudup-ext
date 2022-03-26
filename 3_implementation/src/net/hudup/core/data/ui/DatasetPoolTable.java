/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.Color;
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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import net.hudup.core.alg.KBase;
import net.hudup.core.alg.ui.KBaseConfigDlg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.KBasePointer;
import net.hudup.core.data.KBasePointerImpl;
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
	 * Highlight cell renderer.
	 */
	private HighlightCellRenderer highlightCellRenderer = new HighlightCellRenderer();
	

	/**
	 * Enabled flag.
	 */
	protected boolean enabled = true;
	
	
	/**
	 * Flag to indicate whether to clear dataset when remove it.
	 */
	protected boolean clearDatasetWhenRemove = false;

	
	/**
	 * Bound URI.
	 */
	protected xURI bindUri = null;
	
	
	/**
	 * Default constructor.
	 */
	public DatasetPoolTable() {
		this(false, null);
	}
	
	
	/**
	 * Constructor with clearing flag.
	 * @param clearDatasetWhenRemove flag to indicate whether to clear dataset when remove it.
	 */
	public DatasetPoolTable(boolean clearDatasetWhenRemove) {
		this(clearDatasetWhenRemove, null);
	}
	
	
	/**
	 * Constructor with clearing flag and bound URI.
	 * @param clearDatasetWhenRemove flag to indicate whether to clear dataset when remove it.
	 * @param bindUri bound URI.
	 */
	public DatasetPoolTable(boolean clearDatasetWhenRemove, xURI bindUri) {
		super(new DatasetPoolTableModel());
		this.clearDatasetWhenRemove = clearDatasetWhenRemove;
		this.bindUri = bindUri;
		
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
				if (!isEnabled2()) return;
				
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
		
		JMenuItem miSaveScript = UIUtil.makeMenuItem((String)null, "Save script", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					saveScript();
				}
			});
		contextMenu.add(miSaveScript);

		if (isEnabled2()) {
			JMenuItem miAddScript = UIUtil.makeMenuItem((String)null, "Add script", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						addScript();
					}
				});
			contextMenu.add(miAddScript);
			
			JMenuItem miAddTraining = UIUtil.makeMenuItem((String)null, "Add training set", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						addTraining();
					}
				});
			contextMenu.add(miAddTraining);
		}
		
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
				
				JMenuItem miViewMetadata = UIUtil.makeMenuItem((String)null, "View metadata", 
					new ActionListener() {
						
						public void actionPerformed(ActionEvent e) {
							
							DatasetMetadataTable.showDlg(getThis(), dataset);
							
						} // end action
						
					});
				
				contextMenu.add(miViewMetadata);

				JMenuItem miViewData = UIUtil.makeMenuItem((String)null, "View data", 
					new ActionListener() {
						
						@Override
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
				
				contextMenu.add(miViewData);
				
			}
			else if (dataset instanceof KBasePointer) {
				contextMenu.addSeparator();

				JMenuItem miViewKB = UIUtil.makeMenuItem((String)null, "View knowledge base", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							int confirm = JOptionPane.showConfirmDialog(
									getThis(), 
									"Be careful, out of memory in case of huge knowledge base", 
									"Out of memory in case of huge knowledge base", 
									JOptionPane.OK_CANCEL_OPTION, 
									JOptionPane.WARNING_MESSAGE);
							if (confirm != JOptionPane.OK_OPTION)
								return;
							
							KBase kbase = KBasePointerImpl.createKB(dataset);
							if (kbase == null) {
								JOptionPane.showMessageDialog(
										getThis(), 
										"KBase not viewed", 
										"KBase not viewed", 
										JOptionPane.ERROR_MESSAGE);
								return;
							}
							
							kbase.getInspector().inspect();
							try {
								kbase.close();
							} catch (Throwable ex) {ex.printStackTrace();}
							
						} //end actionPerformed
						
					}); //end ActionListener
				contextMenu.add(miViewKB);

				JMenuItem miConfigKB = UIUtil.makeMenuItem((String)null, "Configure knowledge base", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							DataConfig kbaseConfig = KBasePointerImpl.loadKBaseConfig(dataset);
							if (kbaseConfig == null) {
								JOptionPane.showMessageDialog(
										getThis(), 
										"KBase not configured", 
										"KBase not configured", 
										JOptionPane.ERROR_MESSAGE);
								return;
							}
							
							KBaseConfigDlg dlgKBase = new KBaseConfigDlg(UIUtil.getDialogForComponent(getThis()), kbaseConfig);
							dlgKBase.setVisible(true);
						} //end actionPerformed
						
					}); //end ActionListener
				contextMenu.add(miConfigKB);
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
		if (clearDatasetWhenRemove)
			pool.remove(idxes);
		else
			pool.removeWithoutClear(idxes);
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
	 * Save script.
	 */
	protected void saveScript() {
		JOptionPane.showMessageDialog(this, 
			"Saving script not implemented yet", "Not implement yet", JOptionPane.WARNING_MESSAGE);
	}

	
	/**
	 * Add and load script.
	 */
	protected void addScript() {
		JOptionPane.showMessageDialog(this, 
			"Adding and loading script not implemented yet", "Not implement yet", JOptionPane.WARNING_MESSAGE);
	}
	
	
	/**
	 * Add training dataset.
	 */
	protected void addTraining() {
		JOptionPane.showMessageDialog(this, 
			"Adding training set not implemented yet", "Not implement yet", JOptionPane.WARNING_MESSAGE);
	}

	
	/**
	 * Setting optionally enabled flag.
	 * @param enabled optionally enabled flag.
	 */
	public void setEnabled2(boolean enabled) {
		this.enabled = enabled;
	}

	
	/**
	 * Getting optionally enabled flag.
	 * @return whether optionally enabled.
	 */
	public boolean isEnabled2() {
		return enabled;
	}


	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		if (column >=1 && column <= 3)
			return highlightCellRenderer;
		else
			return super.getCellRenderer(row, column);
	}


	/**
	 * This class represents highlight cell renderer according to pool.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	private class HighlightCellRenderer extends DefaultTableCellRenderer {

		/**
		 * Serial version UID for serializable class.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default background color.
		 */
		private Color defaultBackgroundColor = null;
		
		@Override
		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus,
				int row, int column) {
			Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
			defaultBackgroundColor = defaultBackgroundColor != null ? defaultBackgroundColor : comp.getBackground();
			
			if (bindUri == null) return comp;
			
			DatasetPool pool = getPool();
			if (pool == null || row >= pool.size()) return comp;
			
			Dataset dataset = null;
			if (column == 1)
				dataset = pool.get(row).getTraining();
			else if (column == 2)
				dataset = pool.get(row).getTesting();
			else if (column == 3)
				dataset = pool.get(row).getWhole();
				
			dataset = DatasetUtil.getMostInnerDataset(dataset);
			if (dataset != null)
				comp.setBackground(new Color(0, 255, 0));
			else
				comp.setBackground(defaultBackgroundColor);
			return comp;
		}
		
		
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
		int n = pool != null ? pool.size() : 0;
		for (int i = 0; i < n; i++) {
			DatasetPair pair = pool.get(i);
			
			Vector<Object> row = new Vector<Object>();
			
			row.add(i + 1);
			
			row.add(DatasetUtil.extractUriIdText(pair.getTraining()));
			row.add(DatasetUtil.extractUriIdText(pair.getTesting()));
			row.add(DatasetUtil.extractUriIdText(pair.getWhole()));
			
			data.add(row);
		}
		
		this.setDataVector(data, columns);
	}


	@Override
	public boolean isCellEditable(int row, int column) {
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

