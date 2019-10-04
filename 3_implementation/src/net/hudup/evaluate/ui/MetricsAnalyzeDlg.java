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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.Collections;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import net.hudup.core.RegisterTable;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.logistic.SystemPropertiesTextArea;

/**
 * This is the dialog shows analysis of metrics for evaluating algorithms.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MetricsAnalyzeDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Metrics.
	 */
	private Metrics metrics = null;
	
	
	/**
	 * Registered table of algorithms.
	 */
	private RegisterTable algTable = null;
	
	
	/**
	 * Constructor with specified metrics and registered table of algorithms.
	 * 
	 * @param comp parent component.
	 * @param metrics specified metrics.
	 * @param algTable specified registered table of algorithms.
	 * @throws RemoteException if any error raises.
	 */
	public MetricsAnalyzeDlg(final Component comp, final Metrics metrics, final RegisterTable algTable) throws RemoteException {
		super(UIUtil.getFrameForComponent(comp), "Metrics viewer", true);
		this.metrics = metrics;
		this.algTable = algTable;
		MetricsUtil util = new MetricsUtil(metrics, algTable);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));

		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		header.add(new JLabel("Note"), BorderLayout.NORTH);

		TextArea info = new SystemPropertiesTextArea(5, 10);
		header.add(new JScrollPane(info), BorderLayout.CENTER);
		
		StringBuffer buffer = new StringBuffer();
		
		List<Integer> datasetIdList = metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		for (int i = 0; i < datasetIdList.size(); i++) {
			int datasetId = datasetIdList.get(i);
			xURI datasetUri = metrics.getDatasetUri(datasetId);
			if (datasetUri != null) {
				if (i > 0)
					buffer.append("\n");
				
				buffer.append("Dataset \"" + datasetId + "\" has path \"" + datasetUri + "\"");
			}
		}
		buffer.append("\n\n");
		info.insert(buffer.toString(), 0);
		info.setCaretPosition(0);
		
		
		JPanel body = new JPanel(new GridLayout(0, 1));
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		add(new JScrollPane(body), BorderLayout.CENTER);
		
		JPanel general = new JPanel(new BorderLayout());
		body.add(general);
		
		general.add(new JLabel("General evaluation"), BorderLayout.NORTH);
		
		JTable tblGeneral = util.createDatasetTable();
		tblGeneral.setPreferredScrollableViewportSize(new Dimension(200, 100));
		general.add(new JScrollPane(tblGeneral), BorderLayout.CENTER);
		
		
		JPanel datasetDetails = new JPanel(new BorderLayout());
		body.add(datasetDetails);
		
		datasetDetails.add(new JLabel("Dataset evaluation"), BorderLayout.NORTH);
		
		JPanel datasetDetailsBody = new JPanel(new GridLayout(1, 0));
		datasetDetails.add(datasetDetailsBody, BorderLayout.CENTER);
		
		for (final int datasetId : datasetIdList) {
			JPanel paneDs = new JPanel(new BorderLayout());
			datasetDetailsBody.add(paneDs);
			
			paneDs.add(new JLabel("Dataset \"" + datasetId + "\""), BorderLayout.NORTH);
			
			JTable tblDetail = util.createDatasetTable(datasetId);
			tblDetail.setPreferredScrollableViewportSize(new Dimension(200, 100));
			paneDs.add(new JScrollPane(tblDetail), BorderLayout.CENTER);

			JPanel tool = new JPanel();
			paneDs.add(tool, BorderLayout.SOUTH);
			
			JButton zoom = UIUtil.makeIconButton(
				"zoomin-16x16.png", 
				"zoom", "Zoom", "Zoom", 
					
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						try {
							zoomMetrics(datasetId);
						} catch (Exception ex) {ex.printStackTrace();}
					}
				});
			zoom.setMargin(new Insets(0, 0 , 0, 0));
			tool.add(zoom);
		}
		
		List<String> metricNameList = metrics.getMetricNameList();
		Collections.sort(metricNameList);
		for (String metricName : metricNameList) {
			JPanel metricDetails = new JPanel(new BorderLayout());
			body.add(metricDetails);
			metricDetails.add(new JLabel(metricName + " evaluation "), BorderLayout.NORTH);
			JTable tblMetric = util.createMetricTable(metricName);
			tblMetric.setPreferredScrollableViewportSize(new Dimension(200, 100));
			metricDetails.add(new JScrollPane(tblMetric), BorderLayout.CENTER);
		}
		
		
		JPanel parameters = new JPanel(new BorderLayout());
		body.add(parameters);

		parameters.add(new JLabel("Algorithm parameters"), BorderLayout.NORTH);
		
		JTable tblParameters = util.createAlgParamsTable();
		tblParameters.setPreferredScrollableViewportSize(new Dimension(200, 100));
		parameters.add(new JScrollPane(tblParameters), BorderLayout.CENTER);
		
		JPanel parametersTool = new JPanel();
		parameters.add(parametersTool, BorderLayout.SOUTH);
		JButton zoomParameters = UIUtil.makeIconButton(
			"zoomin-16x16.png", 
			"zoom", "Zoom", "Zoom", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					zoomParameters();
				}
			});
		zoomParameters.setMargin(new Insets(0, 0 , 0, 0));
		parametersTool.add(zoomParameters);

		
		JPanel algDescs = new JPanel(new BorderLayout());
		body.add(algDescs);

		algDescs.add(new JLabel("Algorithm descriptions"), BorderLayout.NORTH);
		
		TextArea tblAlgDescs = util.createAlgDescsTextArea();
		algDescs.add(new JScrollPane(tblAlgDescs), BorderLayout.CENTER);
		
		JPanel algDescsTool = new JPanel();
		algDescs.add(algDescsTool, BorderLayout.SOUTH);
		JButton zoomAlgDescs = UIUtil.makeIconButton(
			"zoomin-16x16.png", 
			"zoom", "Zoom", "Zoom", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					zoomAlgDescs();
				}
			});
		zoomAlgDescs.setMargin(new Insets(0, 0 , 0, 0));
		algDescsTool.add(zoomAlgDescs);

		
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

		JButton viewResults = new JButton("Graph");
		viewResults.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				try {
					new MetricsGraphDlg(comp, metrics, algTable);
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
	 * Zooming evaluation of specified dataset ID.
	 * @param datasetId specified dataset ID.
	 * @throws RemoteException if any error raises.
	 */
	private void zoomMetrics(int datasetId) throws RemoteException {
		final JDialog zoomDlg = new JDialog(this, 
				"Zoom for " + " dataset \"" + datasetId + "\"", false);
		zoomDlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		zoomDlg.setSize(600, 400);
		zoomDlg.setLocationRelativeTo(this);
		zoomDlg.setLayout(new BorderLayout());
		
		zoomDlg.add(new JLabel("Dataset \"" + datasetId + "\""), BorderLayout.NORTH);
		
		MetricsUtil util = new MetricsUtil(metrics, algTable);
		JTable tblDetail = util.createDatasetTable(datasetId);
		zoomDlg.add(new JScrollPane(tblDetail), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		zoomDlg.add(footer, BorderLayout.SOUTH);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				zoomDlg.dispose();
			}
		});
		footer.add(close);

		zoomDlg.setVisible(true);
	}
	
	
	/**
	 * Zooming parameters.
	 */
	private void zoomParameters() {
		final JDialog zoomDlg = new JDialog(this, 
				"Zoom for algorithm parameters", false);
		zoomDlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		zoomDlg.setSize(600, 400);
		zoomDlg.setLocationRelativeTo(this);
		zoomDlg.setLayout(new BorderLayout());
		
		zoomDlg.add(new JLabel("Algorithm parameters"), BorderLayout.NORTH);
		
		MetricsUtil util = new MetricsUtil(metrics, algTable);
		JTable tblParameters = util.createAlgParamsTable();
		zoomDlg.add(new JScrollPane(tblParameters), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		zoomDlg.add(footer, BorderLayout.SOUTH);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				zoomDlg.dispose();
			}
		});
		footer.add(close);

		zoomDlg.setVisible(true);
	}

	
	/**
	 * Zooming show of algorithm descriptions.
	 */
	private void zoomAlgDescs() {
		final JDialog zoomDlg = new JDialog(this, 
				"Zoom for algorithm descriptions", false);
		zoomDlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		zoomDlg.setSize(600, 400);
		zoomDlg.setLocationRelativeTo(this);
		zoomDlg.setLayout(new BorderLayout());
		
		zoomDlg.add(new JLabel("Algorithm descriptions"), BorderLayout.NORTH);
		
		MetricsUtil util = new MetricsUtil(metrics, algTable);
		TextArea tblAlgDescs = util.createAlgDescsTextArea();
		zoomDlg.add(new JScrollPane(tblAlgDescs), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		zoomDlg.add(footer, BorderLayout.SOUTH);
		
		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				zoomDlg.dispose();
			}
		});
		footer.add(close);

		zoomDlg.setVisible(true);
	}

	
	/**
	 * Exporting metrics to file.
	 */
	private void export() {
		MetricsUtil util = new MetricsUtil(metrics, algTable);
		util.export(this);
	}
	
	
}
