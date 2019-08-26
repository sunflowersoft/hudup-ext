package net.hudup.evaluate.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.MetricWrapper;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * Table to show metrics evaluation.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MetricsTable extends SortableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor with algorithm table.
	 * 
	 * @param algTable specified algorithm table.
	 */
	public MetricsTable(final RegisterTable algTable) {
		// TODO Auto-generated constructor stub
		super (new MetricsTM());
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e)) {
					final Metrics metrics = getMetricsTM().getMetrics();
					if (metrics == null || metrics.size() == 0)
						return;
					
					JPopupMenu contextMenu = new JPopupMenu();
					
					JMenuItem miCopyToClipboard = UIUtil.makeMenuItem((String)null, "Copy to clipboard", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								ClipboardUtil.util.setText(metrics.translate());
							}
						});
					contextMenu.add(miCopyToClipboard);
					
					JMenuItem miSave = UIUtil.makeMenuItem((String)null, "Save", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								MetricsUtil util = new MetricsUtil(metrics, algTable);
								util.export(getThis());
							}
						});
					contextMenu.add(miSave);
					
					JMenuItem miAlgDesc = createAlgDescMenuItem();
					if (miAlgDesc != null) {
						contextMenu.addSeparator();
						contextMenu.add(miAlgDesc);
					}

					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
					
				}
			}
			
		});
	}

	
	/**
	 * Creating menu item of algorithm description.
	 * @return menu item of algorithm description.
	 */
	private JMenuItem createAlgDescMenuItem() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return null;
		Object algName = getValueAt(selectedRow, 1);
		if (algName == null)
			return null;
		Object datasetId = getValueAt(selectedRow, 2);
		if (datasetId == null)
			return null;
		
		try {
			algName = algName.toString();
			datasetId = Integer.parseInt(datasetId.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String algDesc = getMetricsTM().getMetrics().getAlgDesc((String)algName, (Integer)datasetId);
		if (algDesc == null || algDesc.isEmpty())
			return null;
		
		String description = "Algorithm \"" + algName + "\":\n    " + algDesc + "\n\n" + "Testing dataset pair: " + datasetId;
		JMenuItem miAlgDesc = UIUtil.makeMenuItem((String)null, "Algorithm description", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new DescriptionDlg(getThis(), "Algorithm description", description).setVisible(true);;
				}
			});
		
		return miAlgDesc;
	}
	
	
	/**
	 * Update table by specified metrics.
	 * @param metrics specified metrics.
	 */
	public synchronized void update(Metrics metrics) {
		
		getMetricsTM().update(metrics);
		
		init();
		if (getColumnCount() > 0)
			getColumnModel().getColumn(0).setMaxWidth(50);
	}

	
	/**
	 * Clearing table.
	 */
	public synchronized void clear() {
		getMetricsTM().clear();
	}
	
	
	/**
	 * Getting metrics table model.
	 * @return {@link MetricsTM}
	 */
	private MetricsTM getMetricsTM() {
		return (MetricsTM) getModel();
	}
	
	
	/**
	 * Getting this metrics table.
	 * @return this metrics table.
	 */
	private MetricsTable getThis() {
		return this;
	}
	
	
}



/**
 * This class is Metrics table model.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MetricsTM extends SortableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of metrics.
	 */
	protected Metrics metrics = null;
	

	/**
	 * Default constructor.
	 */
	public MetricsTM() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Update this model by metrics.
	 * @param metrics specified metrics.
	 */
	public void update(Metrics metrics) {
		if (metrics == null) {
			clear();
			return;
		}
		
		this.metrics = metrics;
		
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		int no = 0;
		for (int i = 0; i < this.metrics.size(); i++) {
			MetricWrapper wrapper = this.metrics.get(i);
			if (!wrapper.isValid())
				continue;
			
			Metric metric = wrapper.getMetric();
			
			Vector<Object> row = Util.newVector();
			
			row.add(++no);
			row.add(wrapper.getAlgName());
			row.add(wrapper.getDatasetId());
			row.add(metric.getName());
			row.add(metric.getTypeName());
			
			MetricValue metricValue = wrapper.getAccumValue();
			if (metricValue != null)
				row.add(metricValue);
			else
				row.add("");
				
			data.add(row);
		}
		
		
		try {
			setDataVector(data, columns);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Clearing content of this model.
	 */
	public void clear() {
		Vector<String> columns = createColumns();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		this.metrics = null;
		
		try {
			setDataVector(data, columns);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Creating column names.
	 * @return vector of column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Algorithm name");
		columns.add("Dataset Id");
		columns.add("Metric name");
		columns.add("Metric type");
		columns.add("Value");
		
		return columns;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public boolean isSortable(int column) {
		// TODO Auto-generated method stub
		return true;
	}
	
	
	/**
	 * Getting metrics.
	 * @return {@link Metrics}.
	 */
	public Metrics getMetrics() {
		return metrics;
	}
	
	
}



/**
 * Table to show metrics evaluation.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MetricsTable2 extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor with algorithm table.
	 * 
	 * @param algTable specified algorithm table.
	 */
	public MetricsTable2(final RegisterTable algTable) {
		// TODO Auto-generated constructor stub
		super (new MetricsTM2());
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e)) {
					final Metrics metrics = getMetricsTM().getMetrics();
					if (metrics == null || metrics.size() == 0)
						return;
					
					JPopupMenu contextMenu = new JPopupMenu();
					
					JMenuItem miCopyToClipboard = UIUtil.makeMenuItem((String)null, "Copy to clipboard", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								ClipboardUtil.util.setText(metrics.translate());
							}
						});
					contextMenu.add(miCopyToClipboard);
					
					JMenuItem miSave = UIUtil.makeMenuItem((String)null, "Save", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								MetricsUtil util = new MetricsUtil(metrics, algTable);
								util.export(getThis());
							}
						});
					contextMenu.add(miSave);
					
					JMenuItem miAlgDesc = createAlgDescMenuItem();
					if (miAlgDesc != null) {
						contextMenu.addSeparator();
						contextMenu.add(miAlgDesc);
					}

					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
					
				}
			}
			
		});
	}

	
	/**
	 * Creating menu item of algorithm description.
	 * @return menu item of algorithm description.
	 */
	private JMenuItem createAlgDescMenuItem() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return null;
		Object algName = getValueAt(selectedRow, 1);
		if (algName == null)
			return null;
		Object datasetId = getValueAt(selectedRow, 2);
		if (datasetId == null)
			return null;
		
		try {
			algName = algName.toString();
			datasetId = Integer.parseInt(datasetId.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		String algDesc = getMetricsTM().getMetrics().getAlgDesc((String)algName, (Integer)datasetId);
		if (algDesc == null || algDesc.isEmpty())
			return null;
		
		String description = "Algorithm \"" + algName + "\":\n    " + algDesc + "\n\n" + "Testing dataset pair: " + datasetId;
		JMenuItem miAlgDesc = UIUtil.makeMenuItem((String)null, "Algorithm description", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new DescriptionDlg(getThis(), "Algorithm description", description).setVisible(true);;
				}
			});
		
		return miAlgDesc;
	}
	
	
	/**
	 * Update table by specified metrics.
	 * @param metrics specified metrics.
	 */
	public synchronized void update(Metrics metrics) {
		
		getMetricsTM().update(metrics);
		
		if (getColumnCount() > 0)
			getColumnModel().getColumn(0).setMaxWidth(50);
	}

	
	/**
	 * Clearing table.
	 */
	public synchronized void clear() {
		getMetricsTM().clear();
	}
	
	
	/**
	 * Getting metrics table model.
	 * @return {@link MetricsTM2}
	 */
	private MetricsTM2 getMetricsTM() {
		return (MetricsTM2) getModel();
	}
	
	
	/**
	 * Getting this metrics table.
	 * @return this metrics table.
	 */
	private MetricsTable2 getThis() {
		return this;
	}
	
	
}



/**
 * This class is Metrics table model.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MetricsTM2 extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of metrics.
	 */
	protected Metrics metrics = null;
	

	/**
	 * Default constructor.
	 */
	public MetricsTM2() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Update this model by metrics.
	 * @param metrics specified metrics.
	 */
	public void update(Metrics metrics) {
		if (metrics == null) {
			clear();
			return;
		}
		
		this.metrics = metrics;
		
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		int no = 0;
		for (int i = 0; i < this.metrics.size(); i++) {
			MetricWrapper wrapper = this.metrics.get(i);
			if (!wrapper.isValid())
				continue;
			
			Metric metric = wrapper.getMetric();
			
			Vector<Object> row = Util.newVector();
			
			row.add(++no);
			row.add(wrapper.getAlgName());
			row.add(wrapper.getDatasetId());
			row.add(metric.getName());
			row.add(metric.getTypeName());
			
			MetricValue metricValue = wrapper.getAccumValue();
			if (metricValue != null)
				row.add(metricValue);
			else
				row.add("");
				
			data.add(row);
		}
		
		
		try {
			setDataVector(data, columns);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Clearing content of this model.
	 */
	public void clear() {
		Vector<String> columns = createColumns();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		this.metrics = null;
		
		try {
			setDataVector(data, columns);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Creating column names.
	 * @return vector of column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Algorithm name");
		columns.add("Dataset Id");
		columns.add("Metric name");
		columns.add("Metric type");
		columns.add("Value");
		
		return columns;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * Getting metrics.
	 * @return {@link Metrics}.
	 */
	public Metrics getMetrics() {
		return metrics;
	}
	
	
}



