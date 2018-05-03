package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.table.TableColumn;

import net.hudup.core.PluginStorage;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricWrapper;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.core.logistic.ui.UIUtil;


/**
 * Dialog allows users to selected metrics.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MetricsOptionDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of resulted selected metrics.
	 */
	protected List<Metric> result = Util.newList();
	
	
	/**
	 * Constructor with default selected metrics.
	 * @param comp parent component.
	 * @param selectedMetricList specified default selected metrics.
	 */
	public MetricsOptionDlg(Component comp, List<Metric> selectedMetricList) {
		super(UIUtil.getFrameForComponent(comp), "Metrics option", true);
		
		result.addAll(selectedMetricList);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));

		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		final MetricsOptionTable tblMetricsOption = new MetricsOptionTable();
		tblMetricsOption.update(selectedMetricList);
		body.add(new JScrollPane(tblMetricsOption), BorderLayout.CENTER);
		
		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		body.add(toolbar, BorderLayout.SOUTH);
		
		JButton selectAll = UIUtil.makeIconButton(
			"selectall-16x16.png", 
			"select_all", "Select all", "Select all", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					tblMetricsOption.selectAll(true);
				}
			});
		selectAll.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(selectAll);
		
		JButton unselectAll = UIUtil.makeIconButton(
			"unselectall-16x16.png", 
			"unselect_all", "Unselect all", "Unselect all", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					tblMetricsOption.selectAll(false);
				}
			});
		unselectAll.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(unselectAll);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				result = tblMetricsOption.getSelectedMetricList();
				dispose();
			}
		});
		footer.add(ok);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		footer.add(cancel);
		
		setVisible(true);
	}
	
	
	/**
	 * Getting resulted list of selected metrics.
	 * @return resulted list of selected metrics.
	 */
	public List<Metric> getSelectedMetricList() {
		return result;
	}
	
	
}


/**
 * Table for showing metric list.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MetricsOptionTable extends SortableTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public MetricsOptionTable() {
		super(new MetricsOptionTM());
	}


	@Override
	protected void init() {
		// TODO Auto-generated method stub
		super.init();
		
		if (getColumnCount() > 4) {
			TableColumn tc = getColumn(getColumnName(4)); 
			tc.setMinWidth(0);
			tc.setMaxWidth(0);
		}
	}
	
	
	/**
	 * Updating table by default selected metrics.
	 * 
	 * @param selectedMetricList default selected metrics.
	 */
	public void update(List<Metric> selectedMetricList) {
		getMetricsOptionTM().update(selectedMetricList);
		init();
	}

	
	/**
	 * Getting selected metrics list.
	 * @return selected metrics list.
	 */
	public List<Metric> getSelectedMetricList() {
		return getMetricsOptionTM().getSelectedMetricList();
	}

	
	/**
	 * Getting model for this table as {@link MetricsOptionTM}.
	 * @return model for this table as {@link MetricsOptionTM}.
	 */
	public MetricsOptionTM getMetricsOptionTM() {
		return (MetricsOptionTM)getModel();
	}


	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, row, column);
		
        if (column != 3)
        	return;
        
        boolean selected = (Boolean)aValue;
    	Metric metric = (Metric) getValueAt(row, 4);
    	if (metric instanceof MetricWrapper)
    		return;
    	
    	if (selected && (metric instanceof MetaMetric)) {
    		selectRelatedMetrics( (MetaMetric)metric );
    	}
    	else if (!selected)
    		unselectRelatedMetrics(metric);
		
	}


	/**
	 * Selecting / deselecting all metrics according to flag.
	 * @param selected specified flag to select / deselect all metrics.
	 */
	public void selectAll(boolean selected) {
		int rows = getRowCount();
		for (int row = 0; row < rows; row++) {
			boolean aValue = (Boolean)getValueAt(row, 3);
			if (aValue != selected)
				setValueAt(selected, row, 3);
		}
	}
	
	
	/**
	 * A meta-metric depends on other metrics and so this method selects such other metrics that relate to the specified meta-metric.
	 * 
	 * @param metaMetric specified meta-metric.
	 */
	private void selectRelatedMetrics(MetaMetric metaMetric) {
		int rows = getRowCount();
		for (int row = 0; row < rows; row++) {
			Metric otherMetric = (Metric) getValueAt(row, 4);
			
			if (!otherMetric.getName().equals(metaMetric.getName()) && 
					!(otherMetric instanceof MetricWrapper) &&
					metaMetric.referTo(otherMetric.getName()))
				setValueAt(true, row, 3);
			
		}
	}
	
	
	/**
	 * A meta-metric depends on other metrics and so this method deselects meta-metrics that relate to the specified metric.
	 * @param metric specified metric.
	 */
	private void unselectRelatedMetrics(Metric metric) {
		
		int rows = getRowCount();
		for (int row = 0; row < rows; row++) {
			Metric otherMetric = (Metric) getValueAt(row, 4);
			if (!(otherMetric instanceof MetaMetric))
				continue;
			
			MetaMetric metaMetric = (MetaMetric)otherMetric;
			if (!metaMetric.getName().equals(metric.getName()) && 
					metaMetric.referTo(metric.getName()))
				setValueAt(false, row, 3);
			
		}
	}
	
	
}


/**
 * Model of the metrics table.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class MetricsOptionTM extends SortableTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public MetricsOptionTM() {
		
	}

	
	/**
	 * Update this model by specified selected metrics list.
	 * @param selectedMetricList specified selected metrics list.
	 */
	public void update(List<Metric> selectedMetricList) {
		Vector<Vector<Object>> data = Util.newVector();
		
		List<Alg> metricList = PluginStorage.getMetricReg().getAlgList(); 
		for (int i = 0; i < metricList.size(); i++) {
			Metric metric = (Metric) metricList.get(i);
			
			Vector<Object> row = Util.newVector();
			row.add(i + 1);
			row.add(metric.getName());
			row.add(metric.getTypeName());
			
			boolean found = false;
			for (Metric selectedMetric : selectedMetricList) {
				if (selectedMetric.getName().equals(metric.getName())) {
					found = true;
					break;
				}
			}
			if (found)
				row.add(true);
			else
				row.add(false);
			
			row.add(metric);
			
			data.add(row);
		}
		
		setDataVector(data, createColumns());
	}
	
	
	/**
	 * Getting selected metrics list.
	 * @return selected metrics list.
	 */
	public List<Metric> getSelectedMetricList() {
		List<Metric> selectedList = Util.newList();
		
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			boolean selected = (Boolean) getValueAt(i, 3);
			
			if (selected)
				selectedList.add((Metric)getValueAt(i, 4));
		}
		
		return selectedList;
	}
	
	
	/**
	 * Creating columns.
	 * @return vector of column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Metric name");
		columns.add("Metric type");
		columns.add("Select");
		columns.add("Metric");
		
		return columns;
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 3)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}


	@Override
	public boolean isSortable(int column) {
		// TODO Auto-generated method stub
		return true;
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return column == 3;
	}
	
	
}
