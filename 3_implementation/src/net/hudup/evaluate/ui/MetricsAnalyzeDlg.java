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
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

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
	 * Referred evaluator.
	 */
	private Evaluator referredEvaluator = null;

	
	/**
	 * Constructor with specified metrics.
	 * @param comp parent component.
	 * @param metrics specified metrics.
	 * @throws RemoteException if any error raises.
	 */
	public MetricsAnalyzeDlg(Component comp, Metrics metrics) throws RemoteException {
		this(comp, metrics, null, null);
	}

	
	/**
	 * Constructor with specified metrics and registered table of algorithms.
	 * @param comp parent component.
	 * @param metrics specified metrics.
	 * @param algTable specified registered table of algorithms.
	 * @throws RemoteException if any error raises.
	 */
	public MetricsAnalyzeDlg(Component comp, Metrics metrics, RegisterTable algTable) throws RemoteException {
		this(comp, metrics, algTable, null);
	}

	
	/**
	 * Constructor with specified metrics and registered table of algorithms.
	 * @param comp parent component.
	 * @param metrics specified metrics.
	 * @param algTable specified registered table of algorithms.
	 * @param referredEvaluator referred evaluator.
	 * @throws RemoteException if any error raises.
	 */
	public MetricsAnalyzeDlg(Component comp, final Metrics metrics, final RegisterTable algTable, Evaluator referredEvaluator) throws RemoteException {
		super(UIUtil.getFrameForComponent(comp), "Metrics viewer", true);
		this.metrics = metrics;
		this.algTable = algTable;
		this.referredEvaluator = referredEvaluator;
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(800, 600);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));

		setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new GridLayout(0, 1));
		body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
		add(new JScrollPane(body), BorderLayout.CENTER);
		
		
		JPanel general = new JPanel(new BorderLayout());
		body.add(general);
		general.add(new JLabel("General evaluation"), BorderLayout.NORTH);
		JTable tblGeneral = util.createDatasetTable();
		tblGeneral.setPreferredScrollableViewportSize(new Dimension(200, 100));
		general.add(new JScrollPane(tblGeneral), BorderLayout.CENTER);
		
		JPanel generalTool = new JPanel();
		general.add(generalTool, BorderLayout.SOUTH);
		JButton zoomGeneral = UIUtil.makeIconButton("zoomin-16x16.png", "zoom", "Zoom", "Zoom", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {zoomGeneral();}
			});
		zoomGeneral.setMargin(new Insets(0, 0 , 0, 0));
		generalTool.add(zoomGeneral);
		
		
		JPanel datasetDetails = new JPanel(new BorderLayout());
		body.add(datasetDetails);
		datasetDetails.add(new JLabel("Dataset evaluation"), BorderLayout.NORTH);
		JPanel datasetDetailsBody = new JPanel(new GridLayout(0, 1));
		datasetDetails.add(datasetDetailsBody, BorderLayout.CENTER);
		List<Integer> datasetIdList = metrics.getDatasetIdList();
		for (final int datasetId : datasetIdList) {
			JPanel paneDs = new JPanel(new BorderLayout());
			datasetDetailsBody.add(paneDs);
			paneDs.add(new JLabel("Dataset \"" + datasetId + "\""), BorderLayout.NORTH);
			JTable tblDetail = util.createDatasetTable(datasetId);
			tblDetail.setPreferredScrollableViewportSize(new Dimension(200, 100));
			paneDs.add(new JScrollPane(tblDetail), BorderLayout.CENTER);
			
			JPanel tool = new JPanel();
			paneDs.add(tool, BorderLayout.SOUTH);
			JButton zoom = UIUtil.makeIconButton("zoomin-16x16.png", "zoom", "Zoom", "Zoom", 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {zoomMetrics(datasetId);}
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
			
			JPanel metricDetailsTool = new JPanel();
			metricDetails.add(metricDetailsTool, BorderLayout.SOUTH);
			JButton zoomMetricDetails = UIUtil.makeIconButton("zoomin-16x16.png", "zoom", "Zoom", "Zoom", 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {zoomMetricDetails(metricName);}
				});
			zoomMetricDetails.setMargin(new Insets(0, 0 , 0, 0));
			metricDetailsTool.add(zoomMetricDetails);
		}
		
		
		JPanel evaluateInfo = new JPanel(new BorderLayout());
		body.add(evaluateInfo);
		evaluateInfo.add(new JLabel("Evaluation information"), BorderLayout.NORTH);
		JTable tblEvaluateInfo = util.createEvaluateInfoTable();
		tblEvaluateInfo.setPreferredScrollableViewportSize(new Dimension(200, 100));
		evaluateInfo.add(new JScrollPane(tblEvaluateInfo), BorderLayout.CENTER);
		
		JPanel evaluateInfoTool = new JPanel();
		evaluateInfo.add(evaluateInfoTool, BorderLayout.SOUTH);
		JButton zoomEvaluateInfo = UIUtil.makeIconButton("zoomin-16x16.png", "zoom", "Zoom", "Zoom", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {zoomEvaluateInfo();}
			});
		zoomEvaluateInfo.setMargin(new Insets(0, 0 , 0, 0));
		evaluateInfoTool.add(zoomEvaluateInfo);

		
		JPanel parameters = new JPanel(new BorderLayout());
		body.add(parameters);
		parameters.add(new JLabel("Algorithm parameters"), BorderLayout.NORTH);
		JTable tblParameters = util.createAlgParamsTable();
		tblParameters.setPreferredScrollableViewportSize(new Dimension(200, 100));
		parameters.add(new JScrollPane(tblParameters), BorderLayout.CENTER);
		
		JPanel parametersTool = new JPanel();
		parameters.add(parametersTool, BorderLayout.SOUTH);
		JButton zoomParameters = UIUtil.makeIconButton("zoomin-16x16.png", "zoom", "Zoom", "Zoom", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {zoomParameters();}
			});
		zoomParameters.setMargin(new Insets(0, 0 , 0, 0));
		parametersTool.add(zoomParameters);

		
		JPanel algDescs = new JPanel(new BorderLayout());
		body.add(algDescs);
		algDescs.add(new JLabel("Algorithm descriptions"), BorderLayout.NORTH);
		TextArea txtAlgDescs = util.createAlgDescsTextArea();
		txtAlgDescs.setCaretPosition(0);
		algDescs.add(new JScrollPane(txtAlgDescs), BorderLayout.CENTER);
		
		JPanel algDescsTool = new JPanel();
		algDescs.add(algDescsTool, BorderLayout.SOUTH);
		JButton zoomAlgDescs = UIUtil.makeIconButton("zoomin-16x16.png", "zoom", "Zoom", "Zoom", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {zoomAlgDescs();}
			});
		zoomAlgDescs.setMargin(new Insets(0, 0 , 0, 0));
		algDescsTool.add(zoomAlgDescs);

		
		JPanel note = new JPanel(new BorderLayout());
		body.add(note);
		note.add(new JLabel("Note"), BorderLayout.NORTH);
		TextArea txtNote = new TextArea(5, 10);
		txtNote.setEditable(false);
		txtNote.setText(util.createNote().toString());
		txtNote.setCaretPosition(0);
		note.add(new JScrollPane(txtNote), BorderLayout.CENTER);
		
		JPanel noteTool = new JPanel();
		note.add(noteTool, BorderLayout.SOUTH);
		JButton zoomNote = UIUtil.makeIconButton("zoomin-16x16.png", "zoom", "Zoom", "Zoom", 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {zoomNote();}
			});
		zoomNote.setMargin(new Insets(0, 0 , 0, 0));
		noteTool.add(zoomNote);

		
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
				try {
					new MetricsGraphDlg(comp, metrics, algTable, referredEvaluator);
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
	 * Zooming the specified component.
	 * @param title specified title.
	 * @param label specified label.
	 * @param main specified component.
	 */
	private void zoom(String title, String label, Component main) {
		final JDialog zoomDlg = new JDialog(this, "Zoom for " + title, false);
		zoomDlg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		zoomDlg.setSize(600, 400);
		zoomDlg.setLocationRelativeTo(this);
		zoomDlg.setLayout(new BorderLayout());
		
		zoomDlg.add(new JLabel(label), BorderLayout.NORTH);
		
		zoomDlg.add(new JScrollPane(main), BorderLayout.CENTER);
		
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
	 * Zooming general evaluation.
	 */
	private void zoomGeneral() {
		String title = " general evaluation";
		String label = "General evaluation";
		
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		JTable tblGeneral = new JTable();
		try {
			tblGeneral = util.createDatasetTable();
		} catch (Exception e) {e.printStackTrace();}
		
		zoom(title, label, tblGeneral);
	}

	
	/**
	 * Zooming metric evaluation of specified dataset ID.
	 * @param datasetId specified dataset ID.
	 */
	private void zoomMetrics(int datasetId) {
		String title = " dataset \"" + datasetId + "\"";
		String label = "Dataset \"" + datasetId + "\"";
		
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		JTable tblDetail = new JTable();
		try {
			tblDetail = util.createDatasetTable(datasetId);
		} catch (Exception e) {e.printStackTrace();}
		
		zoom(title, label, tblDetail);
	}
	
	
	/**
	 * Zooming metric details.
	 * @param metricName metric name.
	 */
	private void zoomMetricDetails(String metricName) {
		String title = metricName + " evaluation";
		String label = metricName + " evaluation";
		
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		JTable tblMetric = new JTable();
		try {
			tblMetric = util.createMetricTable(metricName);
		} catch (Exception e) {e.printStackTrace();}
		
		zoom(title, label, tblMetric);
	}

	
	/**
	 * Zooming evaluation information.
	 */
	private void zoomEvaluateInfo() {
		String title = "evaluation information";
		String label = "Evaluation information";
		
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		JTable tblEvParameters = util.createEvaluateInfoTable();
		
		zoom(title, label, tblEvParameters);
	}

	
	/**
	 * Zooming parameters.
	 */
	private void zoomParameters() {
		String title = "algorithm parameters";
		String label = "Algorithm parameters";
		
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		JTable tblParameters = util.createAlgParamsTable();
		
		zoom(title, label, tblParameters);
	}

	
	/**
	 * Zooming show of algorithm descriptions.
	 */
	private void zoomAlgDescs() {
		String title = "algorithm descriptions";
		String label = "Algorithm descriptions";
		
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		TextArea txtAlgDescs = util.createAlgDescsTextArea();
		txtAlgDescs.setCaretPosition(0);
		
		zoom(title, label, txtAlgDescs);
	}

	
	/**
	 * Zooming note.
	 */
	private void zoomNote() {
		String title = "evaluation note";
		String label = "Algorithm descriptions";
		
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		TextArea txtNote = new TextArea(5, 10);
		txtNote.setEditable(false);
		txtNote.setText(util.createNote().toString());
		txtNote.setCaretPosition(0);
		
		zoom(title, label, txtNote);
	}

	
	/**
	 * Exporting metrics to file.
	 */
	private void export() {
		MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
		util.export(this);
	}
	
	
}
