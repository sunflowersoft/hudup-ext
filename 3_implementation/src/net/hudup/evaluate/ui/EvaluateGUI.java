/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.CompositeAlg;
import net.hudup.core.alg.ModelBasedAlg;
import net.hudup.core.alg.Recommender;
import net.hudup.core.alg.ServiceAlg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.ui.AlgComboBox;
import net.hudup.core.alg.ui.AlgConfigDlg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Pointer;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
import net.hudup.core.evaluate.EvaluatorProgressEvent;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.TextField;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.ui.DatasetTextField;
import net.hudup.data.ui.StatusBar;
import net.hudup.data.ui.TxtOutput;
import net.hudup.evaluate.RecommendEvaluator;

/**
 * This class represents a graphic user interface (GUI) for {@link EvaluatorAbstract} with a pair of training dataset and testing dataset.
 * This class is deprecated because it is less useful than the batch evaluator.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class EvaluateGUI extends AbstractEvaluateGUI {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * List of used algorithms stored in register table.
	 */
	protected RegisterTable algRegTable = null;

	
	/**
	 * Algorithm combo-box.
	 */
	protected AlgComboBox cmbAlgs = null;
	
	/**
	 * Configuration button.
	 */
	protected JButton btnConfig = null;
	
	/**
	 * Refreshing button.
	 */
	protected JButton btnRefresh = null;
	
	/**
	 * Clearing button
	 */
	protected JButton btnClear = null;
	
	
	/**
	 * Training browse button.
	 */
	protected JButton btnTrainingBrowse = null;
	
	/**
	 * Training browse text field.
	 */
	protected DatasetTextField txtTrainingBrowse = null;
	
	/**
	 * Testing browse button.
	 */
	protected JButton btnTestingBrowse = null;
	
	/**
	 * Testing browse field.
	 */
	protected DatasetTextField txtTestingBrowse = null;
	
	
	/**
	 * Run button.
	 */
	protected JButton btnRun = null;
	
	/**
	 * Pause/resume button.
	 */
	protected JButton btnPauseResume = null;
	
	/**
	 * Stop button.
	 */
	protected JButton btnStop = null;
	
	/**
	 * Force stop button.
	 */
	protected JButton btnForceStop = null;

	
	/**
	 * Running info panel.
	 */
	protected JPanel paneRunInfo = null;
	
	/**
	 * Running information text field.
	 */
	protected TxtOutput txtRunInfo = null;
	
	
	/**
	 * Saving running information panel.
	 */
	protected JPanel paneRunSave = null;
	
	/**
	 * Text field to show place of saving running information.
	 */
	protected TextField txtRunSaveBrowse = null;
	
	/**
	 * Check box for whether or not to save running information.
	 */
	protected JCheckBox chkRunSave = null;
	
	
	/**
	 * Verbal check box.
	 */
	protected JCheckBox chkVerbal = null;
	
	/**
	 * Metric options button.
	 */
	protected JButton btnMetricsOption = null;

	
	/**
	 * Testing result panel.
	 */
	protected JPanel paneResult = null;
	
	/**
	 * Table of listing metrics.
	 */
	protected MetricsTable tblMetrics = null;
	
	/**
	 * Button to analyze testing results.
	 */
	protected JButton btnAnalyzeResult = null;
	
	/**
	 * Copying resulting to clipboard button.
	 */
	protected JButton btnCopyResult = null;
	
	
	/**
	 * Running progress bar
	 */
	protected JProgressBar prgRunning = null;

	
	/**
	 * Status bar.
	 */
	protected StatusBar statusBar = null;
//	protected StatusBar2 statusBar = null;
	
	
	/**
	 * Constructor with local evaluator.
	 * @param evaluator local evaluator.
	 */
	public EvaluateGUI(Evaluator evaluator) {
		this(evaluator, null, null);
	}

	/**
	 * Constructor with specified evaluator and bound URI.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 */
	public EvaluateGUI(Evaluator evaluator, xURI bindUri) {
		this(evaluator, bindUri, null);
	}

	
	/**
	 * Constructor with specified evaluator and algorithm.
	 * @param evaluator specified evaluator.
	 * @param alg referred algorithm.
	 */
	private EvaluateGUI(Evaluator evaluator, Alg alg) {
		super(evaluator, null, null);
		init(alg);
	}

	
	/**
	 * Constructor with specified evaluator, bound URI, and GUI data.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 * @param referredData GUI parameter.
	 */
	public EvaluateGUI(Evaluator evaluator, xURI bindUri, EvaluateGUIData referredData) {
		super(evaluator, bindUri, referredData);
		init(null);
	}

	
	/**
	 * Initializing the evaluator batch GUI with specified referred algorithm.
	 * @param referredAlg referred algorithm.
	 */
	private void init(Alg referredAlg) {
		if (referredAlg != null) {
			try {
				if (evaluator.acceptAlg(referredAlg))
					this.algRegTable = new RegisterTable(Arrays.asList(referredAlg));
			} catch (Throwable e) {e.printStackTrace();}
			if (this.algRegTable == null) this.algRegTable = new RegisterTable();
		}
		
		setLayout(new BorderLayout(2, 2));
		
		JPanel header = createHeader();
		add(header, BorderLayout.NORTH);
		
		JPanel body = createBody();
		add(body, BorderLayout.CENTER);
		
		JPanel footer = createFooter();
		add(footer, BorderLayout.SOUTH);
		
		algChanged();
		setVerbal(referredData != null ? referredData.chkVerbal : false);
		
		if (this.pool.size() > 0) {
			this.txtTrainingBrowse.setDataset(this.pool.get(0).getTraining());
			this.txtTestingBrowse.setDataset(this.pool.get(0).getTesting());
		}
	}

	
	/**
	 * Getting this evaluator GUI.
	 * @return this evaluator GUI.
	 */
	private EvaluateGUI getThisGUI() {
		return this;	
	}
	
	
	@Override
	public void pluginChanged(PluginChangedEvent evt) {
		try {
			evaluator.clearDelayUnsetupAlgs();

			algRegTable.clear();
			algRegTable.register(EvaluatorAbstract.extractAlgFromPluginStorage(evaluator)); //Algorithms are not cloned because of saving memory when evaluator GUI keep algorithms for a long time.
			cmbAlgs.update(algRegTable.getAlgList());
			updateMode();
		}
		catch (Throwable e) {
			e.printStackTrace();
			updateMode();
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
		this.cmbAlgs = new AlgComboBox(algRegTable.getAlgList()) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void addToContextMenu(JPopupMenu contextMenu) {
				// TODO Auto-generated method stub
				super.addToContextMenu(contextMenu);

				contextMenu.addSeparator();
		    	
				JMenuItem miTraining = UIUtil.makeMenuItem((String)null, "Training", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							addTrainingSet();
						}
					});
				contextMenu.add(miTraining);
			}
			
		};
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
		if (otherResult.algName != null)
			this.cmbAlgs.setDefaultSelected(otherResult.algName);

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
		this.txtRunSaveBrowse = new TextField();
		this.txtRunSaveBrowse.setEditable(false);
		this.txtRunSaveBrowse.setToolTipText(I18nUtil.message("save_evaluate_place"));;
		pane.add(this.txtRunSaveBrowse, BorderLayout.CENTER);
		this.chkRunSave = new JCheckBox(I18nUtil.message("save_evaluate"));
		this.chkRunSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				xURI store = null;
				if(chkRunSave.isSelected()) {
					UriAdapter adapter = new UriAdapter();
					store = adapter.chooseStore(getThisGUI());
					adapter.close();
					
					if (store == null) {
						JOptionPane.showMessageDialog(
							getThisGUI(), 
							"Not open store", 
							"Not open store", 
							JOptionPane.WARNING_MESSAGE);
					}
					else
						txtRunSaveBrowse.setText(store.toString());
				}
				else {
					txtRunSaveBrowse.setText("");
				}
				updateMode();
				
				//Setting evaluation store path to evaluator.
				try {
					if (bindUri == null) //Local evaluator.
						evaluator.setEvaluateStorePath(store != null ? store.toString() : null);
				} catch (Throwable ex) {ex.printStackTrace();}
			}
		});
		this.chkRunSave.setToolTipText(I18nUtil.message("save_evaluate_tooltip"));
		if (referredData != null && referredData.txtRunSaveBrowse != null) {
			this.txtRunSaveBrowse.setText(referredData.txtRunSaveBrowse);
			this.chkRunSave.setSelected(true);
		}
		pane.add(this.chkRunSave, BorderLayout.WEST);
		
		JPanel toolbar = new JPanel(new BorderLayout());
		body.add(toolbar, BorderLayout.SOUTH);
		
		JPanel leftButtons = new JPanel();
		toolbar.add(leftButtons, BorderLayout.WEST);
		
		this.chkVerbal = new JCheckBox(new AbstractAction(I18nUtil.message("verbal_evaluate")) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				boolean verbal = chkVerbal.isSelected();
				setVerbal(verbal);
			}
		});
		this.chkVerbal.setToolTipText(I18nUtil.message("verbal_evaluate_tooltip"));
		leftButtons.add(this.chkVerbal);
		
		JPanel rightButtons = new JPanel();
		toolbar.add(rightButtons, BorderLayout.EAST);
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
		rightButtons.add(this.btnMetricsOption);

		this.prgRunning = new JProgressBar();
		this.prgRunning.setStringPainted(true);
		this.prgRunning.setToolTipText(I18nUtil.message("evaluation_progress"));
		this.prgRunning.setVisible(false);
		this.prgRunning.setMaximum(otherResult.progressTotal);
		this.prgRunning.setValue(otherResult.progressStep);
		toolbar.add(this.prgRunning, BorderLayout.CENTER);
		
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
		this.tblMetrics.update(result);
		this.tblMetrics.setPreferredScrollableViewportSize(new Dimension(600, 80));
		this.paneResult.add(new JScrollPane(this.tblMetrics), BorderLayout.CENTER);

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.paneResult.add(toolbar, BorderLayout.SOUTH);
		
		this.btnAnalyzeResult = new JButton(I18nUtil.message("analyze_result"));
		this.btnAnalyzeResult.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if (result != null) {
					try {
						new MetricsAnalyzeDlg(getThisGUI(), result, new RegisterTable(Arrays.asList(getAlg())));
					} catch (Exception ex) {
						ex.printStackTrace();
						result = null;
					}
				}
				
				if (result == null) {
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
							try {
								ClipboardUtil.util.setText(result.translate());
							} catch (Exception ex) {ex.printStackTrace();}
						}
					}
			});
		this.btnCopyResult.setMargin(new Insets(0, 0 , 0, 0));
		this.btnCopyResult.setVisible(false); //Added date: 2019.09.13 by Loc Nguyen
		toolbar.add(this.btnCopyResult);

		
		JPanel statusPane = new JPanel(new BorderLayout());
		footer.add(statusPane, BorderLayout.SOUTH);

		this.statusBar = new StatusBar();
		if (otherResult.inSetup) {
			this.statusBar.setTextPane1(I18nUtil.message("setting_up_algorithm") + " '" + DSUtil.shortenVerbalName(otherResult.algName) + "'. " + I18nUtil.message("please_wait") + "...");
		}
		else if (otherResult.progressTotal > 0) {
			this.statusBar.setTextPane1(
					I18nUtil.message("algorithm") + " '" + DSUtil.shortenVerbalName(otherResult.algName) + "' " +
					I18nUtil.message("dataset") + " '" + otherResult.datasetId + "': " + 
					otherResult.vCurrentCount + "/" + otherResult.vCurrentTotal);
			
			this.statusBar.setTextPane2(I18nUtil.message("total") + ": " + otherResult.progressStep + "/" + otherResult.progressTotal);
		}
		this.counterClock.setTimeElapse(otherResult.timeElapse);
		this.counterClock.setAssocTimeTextPane(this.statusBar.getLastPane());
		statusPane.add(this.statusBar, BorderLayout.CENTER);

		return footer;
	}

	
	/**
	 * Setting whether testing results are verbalized.
	 * @param verbal if true, testing results are verbalized.
	 */
	private void setVerbal(boolean verbal) {
		
		Container container = this.paneRunSave.getParent();
		if (container == null)
			container = this.paneRunInfo.getParent();
		
		container.remove(this.paneRunInfo);
		container.remove(this.paneRunSave);
		if (verbal)
			container.add(this.paneRunInfo);
		else
			container.add(this.paneRunSave);
			
		this.paneRunInfo.setVisible(verbal);
		this.paneRunSave.setVisible(!verbal);
		
		this.chkVerbal.setSelected(verbal);
		
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
			updateMode();
		}
		
		//Additional code.
		this.pool.removeAllNoClearDatasets();
		this.pool.add(new DatasetPair(txtTrainingBrowse.getDataset(), txtTestingBrowse.getDataset()));
	}
	
	
	/**
	 * Adding training dataset.
	 */
	protected void addTrainingSet() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			DatasetPool pool = new DatasetPool();
			new AddingTrainingDatasetNullTestingDatasetDlg(this, pool, Arrays.asList(getAlg()), evaluator.getMainUnit()).setVisible(true);
			if (pool.size() == 0) {
				JOptionPane.showMessageDialog(
						this, 
						"Training set not parsed", 
						"Training set not parsed", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			this.txtTrainingBrowse.setDataset(pool.get(0).getTraining());
			this.txtTestingBrowse.setDataset(pool.get(0).getTesting());
			
			clearResult();
			updateMode();
		}
		catch (Throwable e) {
			e.printStackTrace();
			updateMode();
		}
		
		//Additional code.
		this.pool.removeAllNoClearDatasets();
		this.pool.add(new DatasetPair(txtTrainingBrowse.getDataset(), txtTestingBrowse.getDataset()));
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
			updateMode();
		}
		
		//Additional code.
		this.pool.removeAllNoClearDatasets();
		this.pool.add(new DatasetPair(txtTrainingBrowse.getDataset(), txtTestingBrowse.getDataset()));
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
		
		//Additional code.
		this.pool.removeAllNoClearDatasets();
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
			LogUtil.error("Error in evaluation");
			updateMode(); //Added date: 2019.08.12 by Loc Nguyen
		}
	}

	
	@Override
	public void receivedEvaluation(EvaluatorEvent evt) throws RemoteException {
		if (evt.getMetrics() == null)
			return;
		
		if (chkVerbal.isSelected()) {
			String info = evt.translate() + "\n\n\n\n";
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkRunSave.isSelected()) {
			boolean fastsave = false;
			try {
				fastsave = evaluator.getConfig().isFastSave();
			} catch (Throwable e) {e.printStackTrace();}

			evProcessor.saveEvaluateResult(txtRunSaveBrowse.getText(), evt, Arrays.asList(getAlg()), fastsave);
		}
		
		
		//this.result = evaluator.getResult();
		this.result = evt.getMetrics(); //Fix bug date: 2019.09.04 by Loc Nguyen.
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
				I18nUtil.message("algorithm") + " '" + DSUtil.shortenVerbalName(algName) + "' " +
				I18nUtil.message("dataset") + " '" + datasetId + "': " + 
				vCurrentCount + "/" + vCurrentTotal);

		statusBar.setTextPane2(I18nUtil.message("total") + ": " + progressStep + "/" + progressTotal);
	}


	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		Alg alg = evt.getAlg();
		if (alg == null) return;
		String algName = alg.getName();

		if (evt.getType() == SetupAlgEvent.Type.doing) {
			this.statusBar.setTextPane1(I18nUtil.message("setting_up_algorithm") + " '" + DSUtil.shortenVerbalName(algName) + "'. " + I18nUtil.message("please_wait") + "...");
		}
		else if (evt.getType() == SetupAlgEvent.Type.done) {
			this.statusBar.setTextPane1("");
		}

		
		if (chkVerbal.isSelected()) {
			String info = "========== Algorithm \"" + algName + "\" ==========\n";
			info = info + evt.translate() + "\n\n\n\n";
			
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkRunSave.isSelected()) {
			boolean fastsave = false;
			try {
				fastsave = evaluator.getConfig().isFastSave();
			} catch (Throwable e) {e.printStackTrace();}

			evProcessor.saveSetupResult(this.txtRunSaveBrowse.getText(), evt, algName, fastsave);
		}
		
	}


	@Override
	protected void updateMode() {
		try {
			evProcessor.closeIOChannels();
			
			Dataset training = txtTrainingBrowse.getDataset();
			Dataset testing = txtTestingBrowse.getDataset();
			
			if (cmbAlgs.getItemCount() == 0) {
				setInternalEnable(false);
				setResultVisible(false);
				
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
					chkRunSave.setEnabled(true);
					chkVerbal.setEnabled(true);
					
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
			
			if (chkRunSave.isSelected() && (txtRunSaveBrowse.getText() == null || txtRunSaveBrowse.getText().isEmpty()))
				chkRunSave.setSelected(false);
			
			if (result == null)
				statusBar.clearText();
			
			this.statusBar.setTextPane0(DSUtil.shortenVerbalName(evaluator.getName()));
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
		flag = flag && algRegTable.size() > 0;
		
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
		
		this.chkRunSave.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);
		
		this.txtRunSaveBrowse.setEnabled(
				flag && 
				trainingSet != null && 
				testingSet != null);

		this.chkVerbal.setEnabled(flag);
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
			this.counterClock.stopAndClearAssoc();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Creating inspector for recommendation algorithm.
	 * @param recommender specified recommendation algorithm.
	 * @return inspector for recommendation algorithm.
	 */
	public static Inspector createInspector(Recommender recommender) {
		if (recommender == null) return null;
		
		try {
			RecommendEvaluator evaluator = new RecommendEvaluator();
			evaluator.getConfig().setSaveAbility(false);
			
			return new RecommenderInspector(evaluator, recommender);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	
	/**
	 * Inspector of recommendation algorithm.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	private static class RecommenderInspector extends JDialog implements Inspector {
		
		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Instructor with specified evaluator and bound URI.
		 * @param evaluator specified evaluator.
		 * @param recommender recommendation algorithm.
		 */
		private RecommenderInspector(Evaluator evaluator, Recommender recommender) {
			super((Frame)null,
					"Inspector of the recommender \"" + recommender.getName() + "\"", true);
			setSize(600, 400);
			setLocationRelativeTo(null);
			
			EvaluateGUI evaluateGUI = new EvaluateGUI(evaluator, recommender);

			setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			addWindowListener(new WindowAdapter() {

				@Override
				public void windowClosed(WindowEvent e) {
					// TODO Auto-generated method stub
					super.windowClosed(e);
					
					evaluateGUI.dispose();
				}

			});
			
			setLayout(new BorderLayout(2, 2));
			
			add(evaluateGUI, BorderLayout.CENTER);
		}

		@Override
		public void inspect() {
			// TODO Auto-generated method stub
			setVisible(true);
		}
		
	}
	
	
}
