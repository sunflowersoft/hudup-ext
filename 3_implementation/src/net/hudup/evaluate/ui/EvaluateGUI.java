/**
 * 
 */
package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.CompositeAlg;
import net.hudup.core.alg.ModelBasedAlg;
import net.hudup.core.alg.ServiceAlg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.ui.AlgComboBox;
import net.hudup.core.alg.ui.AlgConfigDlg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Pointer;
import net.hudup.core.evaluate.AbstractEvaluator;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
import net.hudup.core.evaluate.EvaluatorProgressEvent;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.ui.DatasetTextField;
import net.hudup.data.ui.StatusBar;
import net.hudup.data.ui.TxtOutput;

/**
 * This class represents a graphic user interface (GUI) for {@link AbstractEvaluator} with a pair of training dataset and testing dataset.
 * This class is deprecated because it is less useful than the batch evaluator.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public class EvaluateGUI extends AbstractEvaluateGUI {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * List of used algorithms stored in register table.
	 */
	protected RegisterTable algRegTable = new RegisterTable();

	
	/**
	 * Algorithm combo-box.
	 */
	protected AlgComboBox cmbAlgs = null;
	protected JButton btnConfig = null;
	protected JButton btnRefresh = null;
	protected JButton btnClear = null;
	
	protected JButton btnTrainingBrowse = null;
	protected DatasetTextField txtTrainingBrowse = null;
	protected JButton btnTestingBrowse = null;
	protected DatasetTextField txtTestingBrowse = null;
	
	protected JButton btnRun = null;
	protected JButton btnPauseResume = null;
	protected JButton btnStop = null;
	protected JButton btnForceStop = null;

	protected JPanel paneRunInfo = null;
	protected TxtOutput txtRunInfo = null;
	
	protected JPanel paneRunSave = null;
	protected JTextField txtSaveBrowse = null;
	protected JCheckBox chkSave = null;
	protected JProgressBar prgRunning = null;
	
	protected JCheckBox chkDisplay = null;
	protected JButton btnMetricsOption = null;

	protected JPanel paneResult = null;
	protected MetricsTable tblMetrics = null;
//	protected MetricsTable2 tblMetrics = null; //MetricsTable2 (normal JTable, not sortable) avoids out of array index error.
	protected JButton btnAnalyzeResult = null;
	protected JButton btnCopyResult = null;
	
	protected StatusBar statusBar = null;

	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI.
	 */
	public EvaluateGUI(Evaluator evaluator, xURI bindUri) {
		super(evaluator, bindUri);
		try {
			algRegTable.copy(evaluator.extractAlgFromPluginStorage());
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setLayout(new BorderLayout(2, 2));
		
		JPanel header = createHeader();
		add(header, BorderLayout.NORTH);
		
		JPanel body = createBody();
		add(body, BorderLayout.CENTER);
		
		JPanel footer = createFooter();
		add(footer, BorderLayout.SOUTH);
		
		algChanged();
		setDisplay(false);
	}
	
	
	/**
	 * Getting this evaluator GUI.
	 * @return this evaluator GUI.
	 */
	private EvaluateGUI getThisGUI() {
		return this;	
	}
	
	
	@Override
	public void pluginChanged() {
		try {
			algRegTable.clear();
			algRegTable.copy(evaluator.extractAlgFromPluginStorage());
			cmbAlgs.update(algRegTable.getAlgList());
			updateMode();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Creating header.
	 * @return {@link JPanel} as header.
	 */
	private JPanel createHeader() {
		this.btnTrainingBrowse = new JButton(I18nUtil.message("training_set"));

		JPanel header = new JPanel(new BorderLayout(2, 2));
		
		JPanel up = new JPanel();
		up.setLayout(new GridLayout(1, 0));
		header.add(up, BorderLayout.NORTH);

		JPanel paneAlg = new JPanel();
		up.add(paneAlg);
		paneAlg.add(new JLabel(I18nUtil.message("algorithm") + ":"));
		this.cmbAlgs = new AlgComboBox(algRegTable.getAlgList());
		this.cmbAlgs.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				
				algChanged();
			}
		});
		paneAlg.add(this.cmbAlgs);
		Dimension preferredSize = this.cmbAlgs.getPreferredSize();
		preferredSize.width = Math.max(preferredSize.width, 200);
		this.cmbAlgs.setPreferredSize(preferredSize);

		this.btnConfig = UIUtil.makeIconButton(
			"config-16x16.png", 
			"config", 
			I18nUtil.message("config"), 
			I18nUtil.message("config"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					new AlgConfigDlg(getThisGUI(), getAlg()).
						setVisible(true);
				}
			});
		this.btnConfig.setMargin(new Insets(0, 0 , 0, 0));
		paneAlg.add(this.btnConfig);
		
		JPanel down = new JPanel(new BorderLayout(2, 2));
		header.add(down, BorderLayout.SOUTH);

		JPanel left = new JPanel();
		left.setLayout(new GridLayout(0, 1));
		down.add(left, BorderLayout.WEST);

		this.btnTrainingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				openTrainingSet();
			}
		});
		left.add(this.btnTrainingBrowse);
		
		this.btnTestingBrowse = new JButton(I18nUtil.message("testing_set"));
		this.btnTestingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				openTestingSet();
			}
		});
		left.add(this.btnTestingBrowse);
		
		JPanel center = new JPanel();
		center.setLayout(new GridLayout(0, 1));
		down.add(center, BorderLayout.CENTER);
		
		this.txtTrainingBrowse = new DatasetTextField();
		this.txtTrainingBrowse.setEditable(false);
		center.add(this.txtTrainingBrowse);
		
		this.txtTestingBrowse = new DatasetTextField();
		this.txtTestingBrowse.setEditable(false);
		center.add(this.txtTestingBrowse);

		
		JPanel tool = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		down.add(tool, BorderLayout.SOUTH);
		
		this.btnRefresh = UIUtil.makeIconButton(
			"refresh-16x16.png", 
			"refresh", 
			I18nUtil.message("refresh"), 
			I18nUtil.message("refresh"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					refresh();
				}
			});
		this.btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		tool.add(this.btnRefresh);

		this.btnClear = UIUtil.makeIconButton(
			"clear-16x16.png", 
			"clear", 
			I18nUtil.message("clear"), 
			I18nUtil.message("clear"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					clear();
				}
			});
		this.btnClear.setMargin(new Insets(0, 0 , 0, 0));
		tool.add(this.btnClear);

		this.btnForceStop = UIUtil.makeIconButton(
			"forcestop-16x16.png", 
			"force_stop", 
			I18nUtil.message("force_stop_tooltip"), 
			I18nUtil.message("force_stop"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					forceStop();
				}
			});
		this.btnForceStop.setMargin(new Insets(0, 0 , 0, 0));
		tool.add(this.btnForceStop);

		return header;
	}
	
	
	/**
	 * Event-driven method to respond to change of algorithm. 
	 */
	private void algChanged() {
		Alg alg = (Alg)cmbAlgs.getSelectedItem();
		if (alg == null)
			return;
		
		if (alg instanceof ModelBasedAlg) {
			btnTrainingBrowse.setText(I18nUtil.message("training_set_kbase"));
			Dataset dataset = txtTrainingBrowse.getDataset();
			if (dataset != null && (dataset instanceof Pointer))
				txtTrainingBrowse.setDataset(null);
		}
		else if (alg instanceof CompositeAlg) {
			btnTrainingBrowse.setText(I18nUtil.message("any_source"));
			Dataset dataset = txtTrainingBrowse.getDataset();
			if (dataset != null && (dataset instanceof Pointer))
				txtTrainingBrowse.setDataset(null);
		}
		else if (alg instanceof ServiceAlg) {
			btnTrainingBrowse.setText(I18nUtil.message("service_pointer"));
			txtTrainingBrowse.setDataset(null);
		}
		else
			btnTrainingBrowse.setText(I18nUtil.message("training_set"));
		
		DatasetUtil2.validateTrainingset(getThisGUI(), txtTrainingBrowse.getDataset(), new Alg[] { alg });
		updateMode();
	}
	
	
	/**
	 * Creating body of GUI.
	 * @return {@link JPanel} as body of GUI.
	 */
	private JPanel createBody() {
		JPanel body = new JPanel(new BorderLayout(2, 2));
		
		JPanel paneControl = new JPanel();
		body.add(paneControl, BorderLayout.NORTH);
		
		this.btnRun = new JButton(I18nUtil.message("run"));
		this.btnRun.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				run();
			}
		});
		paneControl.add(this.btnRun);
		
		this.btnPauseResume = new JButton(I18nUtil.message("pause"));
		this.btnPauseResume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				pauseResume();
			}
		});
		paneControl.add(this.btnPauseResume);

		this.btnStop = new JButton(I18nUtil.message("stop"));
		this.btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				stop();
			}
		});
		paneControl.add(this.btnStop);

		JPanel main = new JPanel(new GridLayout(0, 1));
		body.add(main, BorderLayout.CENTER);
		
		this.paneRunInfo = new JPanel(new BorderLayout());
		main.add(this.paneRunInfo);
		this.txtRunInfo = new TxtOutput();
		this.txtRunInfo.setRows(10);
		this.txtRunInfo.setColumns(10);
		this.txtRunInfo.setEditable(false);
		paneRunInfo.add(new JScrollPane(this.txtRunInfo), BorderLayout.CENTER);
		
		this.paneRunSave = new JPanel(new BorderLayout(2, 2));
		main.add(this.paneRunSave);
		JPanel pane = new JPanel(new BorderLayout(2, 2));
		this.paneRunSave.add(pane, BorderLayout.NORTH);
		this.txtSaveBrowse = new JTextField();
		this.txtSaveBrowse.setEditable(false);
		pane.add(this.txtSaveBrowse, BorderLayout.CENTER);
		this.chkSave = new JCheckBox(I18nUtil.message("save"));
		this.chkSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(chkSave.isSelected()) {
					
					UriAdapter adapter = new UriAdapter();
					xURI store = adapter.chooseStore(getThisGUI());
					adapter.close();
					
					if (store == null) {
						JOptionPane.showMessageDialog(
							getThisGUI(), 
							"Not open store", 
							"Not open store", 
							JOptionPane.WARNING_MESSAGE);
					}
					else
						txtSaveBrowse.setText(store.toString());
				}
				else {
					txtSaveBrowse.setText("");
				}
				updateMode();
			}
		});
		pane.add(this.chkSave, BorderLayout.WEST);
		
		JPanel tool = new JPanel(new BorderLayout());
		body.add(tool, BorderLayout.SOUTH);
		JPanel buttons = new JPanel();
		tool.add(buttons, BorderLayout.EAST);
		
		this.chkDisplay = new JCheckBox(new AbstractAction(I18nUtil.message("display")) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				boolean display = chkDisplay.isSelected();
				setDisplay(display);
			}
		});
		buttons.add(this.chkDisplay);
		
		this.btnMetricsOption = UIUtil.makeIconButton(
			"option-16x16.png", 
			"metrics_option", 
			I18nUtil.message("metrics_option"), 
			I18nUtil.message("metrics_option"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					
					metricsOption();
				}
			});
		this.btnMetricsOption.setMargin(new Insets(0, 0 , 0, 0));
		buttons.add(this.btnMetricsOption);

		this.prgRunning = new JProgressBar();
		this.prgRunning.setStringPainted(true);
		this.prgRunning.setToolTipText(I18nUtil.message("evaluation_progress"));
		this.prgRunning.setVisible(false);
		this.prgRunning.setMaximum(0);
		this.prgRunning.setValue(0);
		tool.add(this.prgRunning, BorderLayout.CENTER);
		
		return body;
	}
	
	
	/**
	 * Creating footer of GUI.
	 * @return {@link JPanel} as footer of GUI.
	 */
	private JPanel createFooter() {
		JPanel footer = new JPanel(new BorderLayout());
		
		this.paneResult = new JPanel(new BorderLayout());
		footer.add(this.paneResult, BorderLayout.CENTER);
		
		this.tblMetrics = new MetricsTable(new RegisterTable(Arrays.asList(getAlg())));
		//this.tblMetrics = new MetricsTable2(new RegisterTable(Arrays.asList(getAlg()))); //MetricsTable2 (normal JTable, not sortable) avoids out of array index error.
		this.tblMetrics.setPreferredScrollableViewportSize(new Dimension(600, 100));
		this.paneResult.add(new JScrollPane(this.tblMetrics), BorderLayout.CENTER);

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.paneResult.add(toolbar, BorderLayout.SOUTH);
		
		this.btnAnalyzeResult = new JButton(I18nUtil.message("analyze_result"));
		this.btnAnalyzeResult.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (result != null)
					new MetricsAnalyzeDlg(getThisGUI(), result, new RegisterTable(Arrays.asList(getAlg())));
				else {
					JOptionPane.showMessageDialog(
							getThisGUI(), 
							"No result", 
							"No result", 
							JOptionPane.WARNING_MESSAGE);
				}
			}
		});
		toolbar.add(this.btnAnalyzeResult);
		

		this.btnCopyResult = UIUtil.makeIconButton(
				"copy-16x16.png", 
				"copy_result_to_clipboard", 
				I18nUtil.message("copy_result_to_clipboard"), 
				I18nUtil.message("copy_result_to_clipboard"), 
					
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						
						if (result != null) {
							ClipboardUtil.util.setText(result.translate());
						}
					}
			});
		this.btnCopyResult.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(this.btnCopyResult);

		this.statusBar = new StatusBar();
		this.counterClock.setTimeLabel(this.statusBar.getLastPane());
		footer.add(statusBar, BorderLayout.SOUTH);

		return footer;
	}

	
	/**
	 * Setting whether testing results are displayed.
	 * @param display if true, testing results are displayed.
	 */
	private void setDisplay(boolean display) {
		
		Container container = this.paneRunSave.getParent();
		if (container == null)
			container = this.paneRunInfo.getParent();
		
		container.remove(this.paneRunInfo);
		container.remove(this.paneRunSave);
		if (display)
			container.add(this.paneRunInfo);
		else
			container.add(this.paneRunSave);
			
		this.paneRunInfo.setVisible(display);
		this.paneRunSave.setVisible(!display);
		
		this.chkDisplay.setSelected(display);
		
		updateMode();
	}

	
	/**
	 * Opening training dataset.
	 */
	protected void openTrainingSet() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			DataConfig defaultConfig = txtTrainingBrowse.getConfig(); 
			if (defaultConfig == null)
				defaultConfig = DatasetUtil2.createDefaultConfig(getAlg());
			
			if (evaluator.getMainUnit() != null) {
				defaultConfig = (DataConfig)defaultConfig.clone();
				defaultConfig.setMainUnit(evaluator.getMainUnit());
			}
			
			DataConfig config = DatasetUtil2.chooseTrainingConfig(this, defaultConfig, getAlg());
			
			if (config == null) {
				JOptionPane.showMessageDialog(
					this, 
					"Not open training set", 
					"Not open training set", 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			openTrainingSet(config);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Opening training dataset with specified configuration.
	 * @param config specified configuration.
	 */
	protected void openTrainingSet(DataConfig config) {
		try {
			if (evaluator.remoteIsStarted())
				return;
		
			clearResult();
			updateMode();
			
			Dataset dataset = DatasetUtil.loadDataset(config);
			if (dataset == null) {
				JOptionPane.showMessageDialog(
					this, 
					"Training set not parsed", 
					"Training set not parsed", 
					JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			if (!DatasetUtil2.validateTrainingset(this, dataset, new Alg[] { getAlg() })) {
				dataset.clear();
				return;
			}
			
			this.txtTrainingBrowse.setDataset(dataset);
			updateMode();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Opening testing dataset.
	 */
	protected void openTestingSet() {
		try {
			if (evaluator.remoteIsStarted())
				return;
	
			DataConfig defaultConfig = txtTestingBrowse.getConfig();
			if (defaultConfig == null)
				defaultConfig = new DataConfig();
			
			if (evaluator.getMainUnit() != null) {
				defaultConfig = (DataConfig)defaultConfig.clone();
				defaultConfig.setMainUnit(evaluator.getMainUnit());
			}
			
			DataConfig config = DatasetUtil2.chooseTestingConfig(this, defaultConfig);
			
			if (config == null) {
				JOptionPane.showMessageDialog(
					this, 
					"Not open testing dataset", 
					"Not open testing dataset", 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			openTestingSet(config);
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
}
	
	
	/**
	 * Opening testing dataset with specified configuration.
	 * @param config specified configuration.
	 */
	protected void openTestingSet(DataConfig config) {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			clearResult();
			updateMode();
			
			Dataset dataset = DatasetUtil.loadDataset(config);
			if (dataset == null) {
				JOptionPane.showMessageDialog(
					this, 
					"Testing dataset not parsed", 
					"Testing dataset not parsed", 
					JOptionPane.ERROR_MESSAGE);
				
				return;
			}
			
			if (dataset instanceof Pointer) {
				JOptionPane.showMessageDialog(
						this, 
						"Testing dataset is pointer", 
						"Invalid testing dataset", 
						JOptionPane.ERROR_MESSAGE);
					
					return;
			}
	
			this.txtTestingBrowse.setDataset(dataset);
			updateMode();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	protected void refresh() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			DataConfig trainingCfg = txtTrainingBrowse.getConfig();
			if (trainingCfg != null)
				trainingCfg = (DataConfig) trainingCfg.clone();
			
			DataConfig testingCfg = txtTestingBrowse.getConfig();
			if (testingCfg != null)
				testingCfg = (DataConfig) testingCfg.clone();
			
			if (trainingCfg != null)
				openTrainingSet(trainingCfg);
			
			if (testingCfg != null)
				openTestingSet(testingCfg);
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Override
	protected void clear() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			this.txtTrainingBrowse.setDataset(null);
			this.txtTestingBrowse.setDataset(null);
			
			clearResult();
			updateMode();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Getting current algorithm.
	 * @return {@link Alg} as current algorithm.
	 */
	private Alg getAlg() {
		// TODO Auto-generated method stub
		if (cmbAlgs.getItemCount() == 0)
			return null;
		else
			return (Alg) cmbAlgs.getSelectedItem();
	}

	
	@Override
	protected List<Alg> getCurrentAlgList() {
		// TODO Auto-generated method stub
		List<Alg> algList = Util.newList();
		
		Alg alg = getAlg();
		if (alg != null)
			algList.add(alg);
		
		return algList;
	}


	@Override
	protected void run() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			Alg alg = getAlg();
			Dataset training = txtTrainingBrowse.getDataset();
			Dataset testing = txtTestingBrowse.getDataset();
			if (alg == null || training == null || testing == null)
				return;
			
			if (!DatasetUtil2.validateTrainingset(this, training, new Alg[] { getAlg() }))
				return;
			
			clearResult();
			
			DatasetPool pool = new DatasetPool(training, testing);
				
			List<Alg> algList = Util.newList();
			algList.add(alg);
			evaluator.remoteStart(algList, pool, null);
		
			counterClock.start();
			updateMode();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error in evaluation");
			updateMode(); //Added date: 2019.08.12 by Loc Nguyen
		}
	}

	
	@Override
	public void receivedEvaluation(EvaluatorEvent evt) throws RemoteException {
		if (evt.getMetrics() == null)
			return;
		
		if (chkDisplay.isSelected()) {
			String info = evt.translate() + "\n\n\n\n";
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkSave.isSelected()) {
			String storePath = this.txtSaveBrowse.getText().trim();
			if (storePath.length() == 0)
				return;

			try {
				xURI store = xURI.create(storePath);
				UriAdapter adapter = new UriAdapter(store);
				boolean existed = adapter.exists(store);
				if (!existed)
					adapter.create(store, true);
				adapter.close();
				
				if (evt.getType() == Type.done) {
					String key = getAlg().getName() + EVALUATION_FILE_EXTENSION;
					ByteChannel channel = getIOChannel(store, key, true);

					String info = evt.translate(getAlg().getName(), -1) + "\n\n\n\n";
					ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
					channel.write(buffer);

				    // Exporting excel file
					MetricsUtil util = new MetricsUtil(this.result, new RegisterTable(Arrays.asList(getAlg())));
					util.createExcel(store.concat(METRICS_ANALYZE_EXCEL_FILE_NAME));
					// Begin exporting plain text. It is possible to remove this snippet.
					channel = getIOChannel(store, METRICS_ANALYZE_EXCEL_FILE_NAME2, false);
					buffer = ByteBuffer.wrap(util.createPlainText().getBytes());
					channel.write(buffer);
					// End exporting plain text. It is possible to remove this snippet.
					
					closeIOChannels();
				}
				else {
					Map<Integer, Metrics> map = evt.getMetrics().gets(getAlg().getName());
					Set<Integer> datasetIdList = map.keySet();
					for (int datasetId : datasetIdList) {
						String key = getAlg().getName() + "@" + datasetId + EVALUATION_FILE_EXTENSION;
						ByteChannel channel = getIOChannel(store, key, true);

						String info = evt.translate(getAlg().getName(), datasetId) + "\n\n\n\n";
						ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
						channel.write(buffer);
						
						if (evt.getType() == Type.done_one)
							closeIOChannel(key);
					}
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		
		//this.result = evt.getEvaluator().getResult();
		this.result = evaluator.getResult(); //Fix bug date: 2019.08.06 by Loc Nguyen.
		if (evt.getType() == Type.done) {
			counterClock.stop();
			updateMode();
			
		}
	}
	
	
	@Override
	public void receivedProgress(EvaluatorProgressEvent evt) throws RemoteException {
		// TODO Auto-generated method stub

		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		String algName = evt.getAlgName();
		int datasetId = evt.getDatasetId();
		int vCurrentCount = evt.getCurrentCount();
		int vCurrentTotal = evt.getCurrentTotal();
		
		if (this.prgRunning.getMaximum() < progressTotal)
			this.prgRunning.setMaximum(progressTotal);
		if (this.prgRunning.getValue() < progressStep) 
			this.prgRunning.setValue(progressStep);
		
		statusBar.setTextPane1(
				"Algorithm '" + algName + "' " +
				"dataset '" + datasetId + "': " + 
				vCurrentCount + "/" + vCurrentTotal);
		
		statusBar.setTextPane2("Total: " + progressStep + "/" + progressTotal);
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		Alg alg = evt.getAlg();
		if (alg == null) return;
		
		String info = "========== Algorithm \"" + alg.getName() + "\" ==========\n";
		info = info + evt.translate() + "\n\n\n\n";
		
		if (chkDisplay.isSelected()) {
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkSave.isSelected()) {
			String storePath = this.txtSaveBrowse.getText().trim();
			if (storePath.length() == 0)
				return;

			try {
				xURI store = xURI.create(storePath);
				UriAdapter adapter = new UriAdapter(store);
				boolean existed = adapter.exists(store);
				if (!existed)
					adapter.create(store, true);
				adapter.close();
				
				String key = alg.getName();
				if (evt.getType() == SetupAlgEvent.Type.doing)
					key += SETUP_DOING_FILE_EXTENSION;
				else
					key += SETUP_DONE_FILE_EXTENSION;
				
				ByteChannel channel = getIOChannel(store, key, true);
				ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
				channel.write(buffer);
				closeIOChannel(key);
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
	}


	@Override
	protected void updateMode() {
		try {
			closeIOChannels();
			
			Dataset training = txtTrainingBrowse.getDataset();
			Dataset testing = txtTestingBrowse.getDataset();
			
			if (cmbAlgs.getItemCount() == 0) {
				setInternalEnable(false);
				setResultVisible(false);
				
				cmbAlgs.setEnabled(true);
				
				prgRunning.setMaximum(0);
				prgRunning.setValue(0);
				prgRunning.setVisible(false);
			}
			else if (training == null || testing == null) {
				setInternalEnable(false);
				setResultVisible(false);
				
				cmbAlgs.setEnabled(true);
				btnConfig.setEnabled(true);
				btnTrainingBrowse.setEnabled(true);
				btnTestingBrowse.setEnabled(true);
				btnRefresh.setEnabled(training != null || testing != null);
				btnClear.setEnabled(training != null || testing != null);
				
				prgRunning.setMaximum(0);
				prgRunning.setValue(0);
				prgRunning.setVisible(false);
			}
			else if (evaluator.remoteIsStarted()) {
				if (evaluator.remoteIsRunning()) {
					setInternalEnable(false);
					setResultVisible(false);
					
					btnPauseResume.setText(I18nUtil.message("pause"));
					btnPauseResume.setEnabled(true);
					btnStop.setEnabled(true);
					btnForceStop.setEnabled(true);
					
					prgRunning.setVisible(true);
					
				}
				else {
					setInternalEnable(false);
					setResultVisible(true);
					
					btnPauseResume.setText(I18nUtil.message("resume"));
					btnPauseResume.setEnabled(true);
					btnStop.setEnabled(true);
					btnForceStop.setEnabled(true);
					txtRunInfo.setEnabled(true);
					chkSave.setEnabled(true);
					chkDisplay.setEnabled(true);
					
					tblMetrics.update(result);
				}
			}
			else {
				setInternalEnable(true);
				setResultVisible(true);
				
				btnPauseResume.setText(I18nUtil.message("pause"));
				btnPauseResume.setEnabled(false);
				btnStop.setEnabled(false);
				btnForceStop.setEnabled(false);
	
				tblMetrics.update(result);
				prgRunning.setMaximum(0);
				prgRunning.setValue(0);
				prgRunning.setVisible(false);
				
			}
			
			if (chkSave.isSelected() && (txtSaveBrowse.getText() == null || txtSaveBrowse.getText().isEmpty()))
				chkSave.setSelected(false);
			
			if (result == null)
				statusBar.clearText();
			
			this.statusBar.setTextPane0( 
					evaluator.getName() + " - " +
					(chkDisplay.isSelected() ? I18nUtil.message("display") : I18nUtil.message("undisplay"))
				);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Enable / disable internal controls.
	 * @param flag flag to enable / disable internal controls.
	 */
	private void setInternalEnable(boolean flag) {
		
		this.cmbAlgs.setEnabled(flag);
		this.btnConfig.setEnabled(flag);
		
		Dataset trainingSet = txtTrainingBrowse.getDataset();
		Dataset testingSet = txtTestingBrowse.getDataset();
		
		this.btnTrainingBrowse.setEnabled(flag);
		this.txtTrainingBrowse.setEnabled(flag);
		
		this.btnTestingBrowse.setEnabled(
			flag && trainingSet != null);
		this.txtTestingBrowse.setEnabled(
			flag && trainingSet != null);
		
		this.btnRefresh.setEnabled(
				flag && (trainingSet != null || testingSet != null) );
		
		this.btnClear.setEnabled(
				flag && (trainingSet != null || testingSet != null));

		this.btnRun.setEnabled(
			flag && 
			trainingSet != null && 
			testingSet != null && 
			DatasetUtil2.validateTrainingset(this, trainingSet, new Alg[] { getAlg() }) );

		this.btnPauseResume.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.btnStop.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.btnForceStop.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.txtRunInfo.setEnabled(
			flag && 
			trainingSet != null && 
			testingSet != null);
		
		this.chkSave.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);
		
		this.txtSaveBrowse.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.chkDisplay.setEnabled(flag);
		this.btnMetricsOption.setEnabled(flag);
		
		this.btnAnalyzeResult.setEnabled(
			flag && trainingSet != null && testingSet != null && result != null && result.size() > 0);
			
		this.btnCopyResult.setEnabled(
			flag && trainingSet != null && testingSet != null && result != null && result.size() > 0);
	}

	
	/**
	 * Setting whether results are visible.
	 * @param flag flag to set whether results are visible.
	 */
	private void setResultVisible(boolean flag) {
		Dataset trainingSet = txtTrainingBrowse.getDataset();
		Dataset testingSet = txtTestingBrowse.getDataset();

		boolean visible = flag && 
				trainingSet != null && 
				testingSet != null &&
				result != null && result.size() > 0;
 
		paneResult.setVisible(visible);
		btnAnalyzeResult.setEnabled(visible);
		btnCopyResult.setEnabled(visible);
	}

	
	/**
	 * Clearing result content.
	 */
	private void clearResult() {
		try {
			this.txtRunInfo.setText("");
			this.result = null;
			this.tblMetrics.clear();
			this.counterClock.stopAndClearText();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}
