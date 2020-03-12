/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import flanagan.plot.PlotGraph;
import net.hudup.core.RegisterTable;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class represents a dialog to show graphs of metrics.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class MetricsGraphDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Metrics
	 */
	private Metrics metrics = null;
	
	
	/**
	 * Algorithm table.
	 */
	private RegisterTable algTable = null;

	
	/**
	 * Referred evaluator.
	 */
	private Evaluator referredEvaluator = null;

	
	/**
	 * Constructor with metrics.
	 * @param comp parent component.
	 * @param metrics metrics.
	 * @throws RemoteException if any error raises.
	 */
	public MetricsGraphDlg(Component comp, Metrics metrics) throws RemoteException {
		this(comp, metrics, null, null);
	}

	
	/**
	 * Constructor with metrics and algorithm table.
	 * @param comp parent component.
	 * @param metrics metrics.
	 * @param algTable algorithm table.
	 * @throws RemoteException if any error raises.
	 */
	public MetricsGraphDlg(Component comp, Metrics metrics, RegisterTable algTable) throws RemoteException {
		this(comp, metrics, algTable, null);
	}
	
	
	/**
	 * Constructor with metrics, algorithm table, and referred evaluator.
	 * @param comp parent component.
	 * @param metrics metrics.
	 * @param algTable algorithm table.
	 * @param referredEvaluator referred evaluator.
	 * @throws RemoteException if any error raises.
	 */
	public MetricsGraphDlg(Component comp, final Metrics metrics, final RegisterTable algTable, Evaluator referredEvaluator) throws RemoteException {
		super(UIUtil.getFrameForComponent(comp), "Metrics graph viewer", true);
		this.metrics = metrics;
		this.algTable = algTable;
		this.referredEvaluator = referredEvaluator;
		final MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 500);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		header.add(new JLabel("Note"), BorderLayout.NORTH);
		TextArea info = new TextArea(5, 10);
		info.setEditable(false);
		header.add(new JScrollPane(info), BorderLayout.CENTER);
		
		StringBuffer buffer = new StringBuffer();
		List<String> algNameList = metrics.getAlgNameList();
		Collections.sort(algNameList);
		buffer.append("X-axis\n");
		buffer.append("  Each graph, points from left to right, algorithms as:\n");
		for (int i = 0; i < algNameList.size(); i++) {
			String algName = algNameList.get(i);
			if (i > 0)
				buffer.append("\n");
			buffer.append("    Algorithm \"" + algName + "\"");
		}
		
		buffer.append("\n\n");
		buffer.append("Y-axis: Evaluation measures");
		buffer.append("\n");
		
		buffer.append("  Curves:\n");
		buffer.append("    Circle: Dataset " +         "1\n");
		buffer.append("    Square: Dataset " +         "2\n");
		buffer.append("    Diamond: Dataset " +        "3\n");
		buffer.append("    Filled circle: Dataset " +  "4\n");
		buffer.append("    Filled square: Dataset " +  "5\n");
		buffer.append("    Filled diamond: Dataset " + "6\n");
		buffer.append("    x cross: Dataset " +        "7\n");
		buffer.append("    + cross: Dataset " +        "8\n");
		buffer.append("    Repeat from Dataset " +     "9");
		
		buffer.append("\n\n\n");
		List<Integer> datasetIdList = metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		for (int i = 0; i < datasetIdList.size(); i++) {
			int datasetId = datasetIdList.get(i);
			xURI datasetUri = metrics.getDatasetUri(datasetId);
			if (datasetUri != null) {
				if (i > 0)
					buffer.append("\n");
				
				buffer.append("  Dataset \"" + datasetId + "\" has path \"" + datasetUri + "\"");
			}
		}
		
		info.setText(buffer.toString());
		info.setCaretPosition(0);
		
		List<String> metricNameList = metrics.getMetricNameList();
		Collections.sort(metricNameList);
		
		JPanel body = new JPanel(new GridLayout(Math.max(
				metricNameList.size() / 10 + (metricNameList.size() % 10 > 0 ? 1 : 0), 1), 
				0));
		add(body, BorderLayout.CENTER);
		
		for (final String metricName : metricNameList) {
			JPanel paneMetric = new JPanel(new BorderLayout());
			paneMetric.add(new JLabel(metricName), BorderLayout.NORTH);
			
			PlotGraph metricGraph = util.createPlotGraph(metricName);
			if (metricGraph != null) {
				paneMetric.add(metricGraph, BorderLayout.CENTER);
				JButton zoom = UIUtil.makeIconButton(
					"zoomin-16x16.png", 
					metricName, metricName, metricName, 
						
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							// TODO Auto-generated method stub
							JDialog dlg = new JDialog(
									UIUtil.getFrameForComponent(comp), 
									"Graph for metric \"" + metricName + "\"", true);
							try {
								dlg.setSize(400, 400);
								dlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
								dlg.setLayout(new BorderLayout());
								dlg.add(util.createPlotGraph(metricName), BorderLayout.CENTER);
								dlg.setVisible(true);
							}
							catch (Exception ex) {
								ex.printStackTrace();
								dlg.dispose();
							}
						}
					});
				zoom.setMargin(new Insets(0, 0 , 0, 0));
				JPanel spane = new JPanel();
				paneMetric.add(spane, BorderLayout.SOUTH);
				spane.add(zoom);
				
				
				body.add(paneMetric);
			}
		}

		
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				export();
			}
		});
		footer.add(export);

		JButton viewResults = new JButton("Analyze");
		viewResults.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					new MetricsAnalyzeDlg(comp, metrics, algTable, referredEvaluator);
				} catch (Exception ex) {ex.printStackTrace();}
			}
		});
		footer.add(viewResults);

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		footer.add(close);
		
		setVisible(true);
	}
	
	
	/**
	 * Exporting / aving metrics.
	 */
	private void export() {
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		util.export(this);
	}
	

	
}
