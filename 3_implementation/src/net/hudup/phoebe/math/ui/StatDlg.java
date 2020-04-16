/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.phoebe.math.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import flanagan.analysis.Stat;
import flanagan.math.Fmath;
import flanagan.plot.PlotGraph;
import net.hudup.core.Constants;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.TextParserUtil;
import net.hudup.phoebe.math.FlanaganStat;

/**
 * This class is the dialog to show statistic information of given data.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class StatDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Constructor with data (list), parent component, and modal mode.
	 * @param comp parent component.
	 * @param data specified data.
	 * @param modal model mode.
	 */
	public StatDlg(Component comp, List<Double> data, boolean modal) {
		this(comp, DSUtil.toDoubleArray(data), modal);
	}
	
	/**
	 * Constructor with data, parent component, and modal mode.
	 * @param comp parent component.
	 * @param data specified data.
	 * @param modal model mode.
	 */
	public StatDlg(Component comp, double[] data, boolean modal) {
		super(UIUtil.getFrameForComponent(comp), "Estimated values Statistics", modal);
	
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);

		Vector<Vector<Object>> rowData = new Vector<Vector<Object>>();
		rowData.add(createRow(data));
		JTable tblStat = new JTable(new DefaultTableModel(rowData, createColumns()) {
			
	    	/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
	        	return false;
	    	}
		});
		
		tblStat.setPreferredScrollableViewportSize(new Dimension(200, 40));   
		header.add(new JScrollPane(tblStat), BorderLayout.CENTER);
		
		JPanel paneStat = new JPanel(new BorderLayout());
		header.add(paneStat, BorderLayout.SOUTH);
		
		JButton btnCopy = UIUtil.makeIconButton("copy-16x16.png", 
				"copy", "Copy values to clipboard", "Copy values to clipboard", 
					
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						ClipboardUtil.util.setText(TextParserUtil.toColumnText(data, Constants.DECIMAL_PRECISION).toString());
					}
			});
		btnCopy.setMargin(new Insets(0, 0 , 0, 0));
		paneStat.add(btnCopy, BorderLayout.EAST);

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		double bintWidth = (Fmath.maximum(data) - Fmath.minimum(data)) / FlanaganStat.DEFAULT_BIN_NUMBER;
		PlotGraph graph = FlanaganStat.histogramBinsPlot2(data, bintWidth);
		body.add(graph, BorderLayout.CENTER);
		graph.setBackground(Color.WHITE);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		//
		JButton btnClose = new JButton(new AbstractAction("Close") {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnClose);
		
		setSize(600, 480);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		setVisible(true);
	}
	
	
	/**
	 * Creating row.
	 * @param array specified data.
	 * @return row created.
	 */
	private Vector<Object> createRow(double[] array) {
		Vector<Object> row = new Vector<Object>();
		
		Stat stat = new Stat(array);
		
		row.add(MathUtil.round(stat.minimum()));
		row.add(MathUtil.round(stat.maximum()));
		row.add(MathUtil.round(stat.mean()));
		row.add(MathUtil.round(stat.median()));
		row.add(MathUtil.round(stat.standardDeviation()));
		row.add(MathUtil.round(stat.standardError()));
		row.add(MathUtil.round(stat.variance()));
		row.add(MathUtil.round(stat.momentSkewness()));
		row.add(MathUtil.round(stat.kurtosis()));
		
		return row;
	}

	
	/**
	 * Create column names.
	 * @return column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = new Vector<String>();
		
		columns.add("Min");
		columns.add("Max");
		columns.add("Mean");
		columns.add("Median");
		columns.add("Sd");
		columns.add("Se");
		columns.add("Variance");
		columns.add("Skewness");
		columns.add("Kurtosis");
		
		return columns;
	}
	
	
}


