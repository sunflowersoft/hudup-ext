/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
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
import javax.swing.SwingWorker;

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
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
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.Pointer;
import net.hudup.core.data.ui.AddingTrainingDatasetNullTestingDatasetDlg;
import net.hudup.core.data.ui.DatasetTextField;
import net.hudup.core.evaluate.EvaluateEvent;
import net.hudup.core.evaluate.EvaluateEvent.Type;
import net.hudup.core.evaluate.EvaluateInfo;
import net.hudup.core.evaluate.EvaluateProgressEvent;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.ui.AbstractEvaluateGUI;
import net.hudup.core.evaluate.ui.EvaluateGUIData;
import net.hudup.core.evaluate.ui.MetricsAnalyzeDlg;
import net.hudup.core.evaluate.ui.MetricsTable;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.ConnectInfo;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.CounterElapsedTimeEvent;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.StatusBar;
import net.hudup.core.logistic.ui.TextField;
import net.hudup.core.logistic.ui.TxtOutput;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.logistic.ui.WaitDialog;
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
	 * Upload button.
	 */
	protected JButton btnUpload = null;
	
	/**
	 * Download button.
	 */
	protected JButton btnDownload = null;
	
	
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
	 * @param connectInfo connection information.
	 */
	public EvaluateGUI(Evaluator evaluator, ConnectInfo connectInfo) {
		this(evaluator, connectInfo, null);
	}

	
	/**
	 * Constructor with local evaluator and referred GUI data.
	 * @param evaluator local evaluator.
	 * @param referredGUIData referred GUI data.
	 */
	public EvaluateGUI(Evaluator evaluator, EvaluateGUIData referredGUIData) {
		this(evaluator, null, referredGUIData);
	}

	
	/**
	 * Constructor with local evaluator and algorithm.
	 * @param evaluator local evaluator.
	 * @param referredAlg referred algorithm.
	 */
	private EvaluateGUI(Evaluator evaluator, Alg referredAlg) {
		super(evaluator, null, null, referredAlg);
		initGUI();
	}

	
	/**
	 * Constructor with specified evaluator, bound URI, and GUI data.
	 * @param evaluator specified evaluator.
	 * @param connectInfo connection information.
	 * @param referredGUIData referred GUI data.
	 */
	public EvaluateGUI(Evaluator evaluator, ConnectInfo connectInfo, EvaluateGUIData referredGUIData) {
		super(evaluator, connectInfo, referredGUIData, null);
		initGUI();
	}


	/**
	 * Initializing GUI.
	 */
	private synchronized void initGUI() {
		setLayout(new BorderLayout(2, 2));
		
		JPanel header = createHeader();
		add(header, BorderLayout.NORTH);
		
		JPanel body = createBody();
		add(body, BorderLayout.CENTER);
		
		JPanel footer = createFooter();
		add(footer, BorderLayout.SOUTH);
		
		algChanged();
		setVerbal(guiData != null ? guiData.chkVerbal : false);
		
		if (guiData.pool.size() > 0) {
			this.txtTrainingBrowse.setDataset(guiData.pool.get(0).getTraining(), false);
			this.txtTestingBrowse.setDataset(guiData.pool.get(0).getTesting(), false);
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
	public void pluginChanged(PluginChangedEvent evt) throws RemoteException {
		try {
			if (evaluator.remoteIsStarted()) return;

			algRegTable.unexportNonPluginAlgs();
			algRegTable.clear();
			algRegTable.registerAsTheSame(EvaluatorAbstract.extractNormalAlgFromPluginStorage(evaluator, connectInfo.bindUri)); //Algorithms are not cloned because of saving memory when evaluator GUI keep algorithms for a long time.
			
			List<String> algNames = updateAlgRegFromEvaluator();
			if (algNames != null && algNames.size() > 0) {
				cmbAlgs.update(algRegTable.getAlgList());
				cmbAlgs.setDefaultSelected(algNames.get(0));
			}
			else
				cmbAlgs.update(algRegTable.getAlgList());
			
			clearResult();
			
			guiData.pool = getLocalDatasetPool();
			txtTrainingBrowse.setDataset(guiData.pool.get(0).getTraining(), false);
			txtTestingBrowse.setDataset(guiData.pool.get(0).getTesting(), false);

			updateMode();
			
			LogUtil.info("Plug-in storage is changed by you or someones else.");
		}
		catch (Throwable e) {
			LogUtil.trace(e);
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
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				
				algChanged();
			}
		});
		paneAlg.add(this.cmbAlgs);
		Dimension preferredSize = this.cmbAlgs.getPreferredSize();
		preferredSize.width = Math.max(preferredSize.width, 200);
		this.cmbAlgs.setPreferredSize(preferredSize);
		if (guiData.algName != null)
			this.cmbAlgs.setDefaultSelected(guiData.algName);

		this.btnConfig = UIUtil.makeIconButton(
			"config-16x16.png", 
			"config", 
			I18nUtil.message("config"), 
			I18nUtil.message("config"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
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
				openTrainingSet();
			}
		});
		left.add(this.btnTrainingBrowse);
		
		this.btnTestingBrowse = new JButton(I18nUtil.message("testing_set"));
		this.btnTestingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
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
					refresh();
				}
			});
		this.btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		this.btnRefresh.setVisible(connectInfo.bindUri == null);
		tool.add(this.btnRefresh);

		this.btnClear = UIUtil.makeIconButton(
			"clear-16x16.png", 
			"clear", 
			I18nUtil.message("clear"), 
			I18nUtil.message("clear"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					clear();
				}
			});
		this.btnClear.setMargin(new Insets(0, 0 , 0, 0));
		tool.add(this.btnClear);

		this.btnUpload = UIUtil.makeIconButton(
			connectInfo.bindUri == null ? "scatter-16x16.png" : "upload-16x16.png", 
			connectInfo.bindUri == null ? "scatter" : "upload", 
			connectInfo.bindUri == null ? I18nUtil.message("scatter") : I18nUtil.message("upload"), 
			connectInfo.bindUri == null ? I18nUtil.message("scatter") : I18nUtil.message("upload"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					boolean ret = true;
					try {
						if (connectInfo.bindUri == null)
							ret = evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), null, timestamp = new Timestamp());
						else
							ret = evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), null, timestamp = new Timestamp());
					} catch (Exception ex) {ex.printStackTrace();}
					
					if (ret) {
						JOptionPane.showMessageDialog(
							getThisGUI(), 
							"Success to upload/scatter", 
							"Success upload", 
							JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(
							getThisGUI(), 
							"Fail to upload/scatter", 
							"Fail upload", 
							JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		this.btnUpload.setMargin(new Insets(0, 0 , 0, 0));
		this.btnUpload.setVisible(true);
		tool.add(this.btnUpload);

		this.btnDownload = UIUtil.makeIconButton(
			"download-16x16.png", 
			"download", 
			I18nUtil.message("download"), 
			I18nUtil.message("download"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (connectInfo.bindUri == null) return;

					synchronized (getThisGUI()) {
						DatasetPoolExchanged poolResult = null;
						try {
							poolResult = evaluator.getDatasetPool();
						} catch (Exception ex) {ex.printStackTrace();}
						
						if (poolResult != null) {
							clearResult();
							guiData.pool = poolResult.toDatasetPoolClient();
							if (guiData.pool.size() > 0) {
								txtTrainingBrowse.setDataset(guiData.pool.get(0).getTraining(), false);
								txtTestingBrowse.setDataset(guiData.pool.get(0).getTesting(), false);
							}
							else {
								txtTrainingBrowse.setDataset(null, false);
								txtTestingBrowse.setDataset(null, false);
							}
							updateMode();
							
							JOptionPane.showMessageDialog(
								getThisGUI(), 
								"Success to download from server", 
								"Success download", 
								JOptionPane.INFORMATION_MESSAGE);
						}
						else {
							JOptionPane.showMessageDialog(
								getThisGUI(), 
								"Fail to download from server", 
								"Fail download", 
								JOptionPane.ERROR_MESSAGE);
						}
					} //End synchronize
				}
			});
		this.btnDownload.setMargin(new Insets(0, 0 , 0, 0));
		this.btnDownload.setVisible(connectInfo.bindUri != null);
		tool.add(this.btnDownload);

		this.btnForceStop = UIUtil.makeIconButton(
			"forcestop-16x16.png", 
			"force_stop", 
			I18nUtil.message("force_stop_tooltip"), 
			I18nUtil.message("force_stop"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
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
				txtTrainingBrowse.setDataset(null, false);
		}
		else if (alg instanceof CompositeAlg) {
			btnTrainingBrowse.setText(I18nUtil.message("any_source"));
			Dataset dataset = txtTrainingBrowse.getDataset();
			if (dataset != null && (dataset instanceof Pointer))
				txtTrainingBrowse.setDataset(null, false);
		}
		else if (alg instanceof ServiceAlg) {
			btnTrainingBrowse.setText(I18nUtil.message("service_pointer"));
			txtTrainingBrowse.setDataset(null, false);
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
				run();
			}
		});
		paneControl.add(this.btnRun);
		
		this.btnPauseResume = new JButton(I18nUtil.message("pause"));
		this.btnPauseResume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseResume();
			}
		});
		paneControl.add(this.btnPauseResume);

		this.btnStop = new JButton(I18nUtil.message("stop"));
		this.btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
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
					if (connectInfo.bindUri == null) //Local evaluator.
						evaluator.setEvaluateStorePath(store != null ? store.toString() : null);
				} catch (Throwable ex) {ex.printStackTrace();}
			}
		});
		this.chkRunSave.setToolTipText(I18nUtil.message("save_evaluate_tooltip"));
		if (guiData.txtRunSaveBrowse != null) {
			this.txtRunSaveBrowse.setText(guiData.txtRunSaveBrowse);
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
		
		this.tblMetrics = new MetricsTable(new RegisterTable(Arrays.asList(getAlg())), evaluator) {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void addToContextMenu(JPopupMenu contextMenu) {
				super.addToContextMenu(contextMenu);
				if (contextMenu == null) return;
				
				JMenuItem miRefresh = UIUtil.makeMenuItem((String)null, "Refresh", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							Metrics result = null;
							try {
								result = getThisGUI().evaluator.getResult();
							}
							catch (Exception ex) {ex.printStackTrace();}
							if (result != null) {
								getThisGUI().recoveredResult = getThisGUI().result = result;
								update(result);
							}
							else {
								JOptionPane.showMessageDialog(
									getThisGUI(), 
									"Empty resulted metrics", 
									"Empty resulted metrics", 
									JOptionPane.WARNING_MESSAGE);
							}
						}
					});
				
				try {
					if (!getThisGUI().evaluator.remoteIsRunning()) {
						contextMenu.addSeparator();
						contextMenu.add(miRefresh);
					}
				}
				catch (Exception ex) {ex.printStackTrace();}
			}
			
		};
		this.tblMetrics.update(result);
		this.tblMetrics.setPreferredScrollableViewportSize(new Dimension(600, 80));
		this.paneResult.add(new JScrollPane(this.tblMetrics), BorderLayout.CENTER);

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		this.paneResult.add(toolbar, BorderLayout.SOUTH);
		
		this.btnAnalyzeResult = new JButton(I18nUtil.message("analyze_result"));
		this.btnAnalyzeResult.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if (result != null) {
					try {
						new MetricsAnalyzeDlg(getThisGUI(), result, algRegTable, evaluator);
					} catch (Exception ex) {
						ex.printStackTrace();
						recoveredResult = result;
						result = null;
					}
				}
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
		try {
			this.statusBar.setTextPane0(DSUtil.shortenVerbalName(evaluator.getName()));
		} catch (Exception e) {LogUtil.trace(e);}
		if (otherResult.statuses != null && otherResult.statuses.length > 0) {
			if (otherResult.statuses[0] != null)
				this.statusBar.setTextPane1(otherResult.statuses[0]);
			if (otherResult.statuses.length > 1 && otherResult.statuses[1] != null)
				this.statusBar.setTextPane2(otherResult.statuses[1]);
		}
		if (otherResult.elapsedTime > 0) {
			String elapsedTimeText = Counter.formatTime(otherResult.elapsedTime);
			statusBar.getLastPane().setText(elapsedTimeText);
		}
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
			LogUtil.trace(e);
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
		
			JDialog dlgWait = WaitDialog.createDialog(this); dlgWait.setUndecorated(true);
			SwingWorker<Dataset, Dataset> worker = new SwingWorker<Dataset, Dataset>() {
				@Override
				protected Dataset doInBackground() throws Exception {
					return DatasetUtil.loadDataset(config);
				}

				@Override
				protected void done() {
					super.done(); dlgWait.dispose();
				}
			};
			worker.execute(); dlgWait.setVisible(true);
			Dataset dataset = worker.get();
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
			
			clearResult();
			addTrainingToPool(dataset);
			if (connectInfo.bindUri == null) {
				try {
					evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), this, timestamp = new Timestamp());
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			else {
				txtTrainingBrowse.setDataset(dataset, false);
				updateMode();
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			updateMode();
		}
	}
	
	
	/**
	 * Adding training dataset.
	 */
	protected void addTrainingSet() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			DatasetPool pool = new DatasetPool();
			new AddingTrainingDatasetNullTestingDatasetDlg(this, pool, Arrays.asList(getAlg()), evaluator.getMainUnit(), connectInfo.bindUri).setVisible(true);
			if (pool.size() == 0) {
				JOptionPane.showMessageDialog(
						this, 
						"Training set not parsed", 
						"Training set not parsed", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			clearResult();
			guiData.pool = pool;
			if (connectInfo.bindUri == null) {
				try {
					evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), this, timestamp = new Timestamp());
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			else {
				txtTrainingBrowse.setDataset(guiData.pool.get(0).getTraining(), false);
				txtTestingBrowse.setDataset(guiData.pool.get(0).getTesting(), false);
				updateMode();
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			updateMode();
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
			LogUtil.trace(e);
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
			
			JDialog dlgWait = WaitDialog.createDialog(this); dlgWait.setUndecorated(true);
			SwingWorker<Dataset, Dataset> worker = new SwingWorker<Dataset, Dataset>() {
				@Override
				protected Dataset doInBackground() throws Exception {
					return DatasetUtil.loadDataset(config);
				}

				@Override
				protected void done() {
					super.done(); dlgWait.dispose();
				}
			};
			worker.execute(); dlgWait.setVisible(true);
			Dataset dataset = worker.get();
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
	
			clearResult();
			addTestingToPool(dataset);
			if (connectInfo.bindUri == null) {
				try {
					evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), this, timestamp = new Timestamp());
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			else {
				txtTestingBrowse.setDataset(dataset, false);
				updateMode();
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			updateMode();
		}
	}

	
	@Override
	protected void refresh() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			clearResult();

			JDialog dlgWait = WaitDialog.createDialog(this); dlgWait.setUndecorated(true);
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					evaluator.reloadPool(getThisGUI(), timestamp = new Timestamp());
					return null;
				}
				
				@Override
				protected void done() {
					super.done(); dlgWait.dispose();
				}
			};
			worker.execute(); dlgWait.setVisible(true);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	@Override
	protected void clear() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			clearResult();
			updateMode();
			
			if (connectInfo.bindUri == null) {
				try {
					evaluator.updatePool(null, this, timestamp = new Timestamp());
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			else {
				this.txtTrainingBrowse.setDataset(null, false);
				this.txtTestingBrowse.setDataset(null, false);
				guiData.pool = new DatasetPool();
				updateMode();
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			updateMode();
		}
	}
	
	
	/**
	 * Getting current algorithm.
	 * @return {@link Alg} as current algorithm.
	 */
	private Alg getAlg() {
		if (cmbAlgs.getItemCount() == 0)
			return null;
		else
			return (Alg) cmbAlgs.getSelectedItem();
	}

	
	@Override
	protected List<Alg> getCurrentAlgList() {
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
			if (alg == null || guiData.pool == null || guiData.pool.size() == 0)
				return;

			Dataset training = guiData.pool.get(0).getTraining();
			Dataset testing = guiData.pool.get(0).getTesting();
			if (training == null || testing == null)
				return;
			
			if (!DatasetUtil2.validateTrainingset(this, training, new Alg[] { getAlg() }))
				return;
			
			List<Alg> algList = Util.newList();
			algList.add(alg);
			
			clearResult();
			boolean started = false;
			if (connectInfo.bindUri == null)
				started = evaluator.remoteStart0(algList, toDatasetPoolExchangedClient(guiData.pool), timestamp = new Timestamp(), null);
			else {
				DataConfig config = AlgList.getAlgDescMap(algList);
				started = evaluator.remoteStart(AlgList.getAlgNameList(algList), toDatasetPoolExchangedClient(guiData.pool), this, config, timestamp = new Timestamp(), null);
			}
			if (!started) updateMode();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Error in evaluation");
			updateMode(); //Added date: 2019.08.12 by Loc Nguyen
		}
	}

	
	@Override
	public synchronized void receivedEvaluator(EvaluatorEvent evt) throws RemoteException {
		EvaluatorEvent.Type type = evt.getType();

		boolean timeDiff = true;
		if (timestamp == null) {
			if (evt.getTimestamp() == null)
				timeDiff = false;
			else
				timeDiff = true;
		}
		else if (evt.getTimestamp() == null)
			timeDiff = true;
		else
			timeDiff = !timestamp.equals(evt.getTimestamp());

		if (type == EvaluatorEvent.Type.start || type == EvaluatorEvent.Type.update_pool) {
			if (evt.getType() == EvaluatorEvent.Type.start) {
				updatePluginFromEvaluator();
	
				cmbAlgs.unexportNonPluginAlgs();
				EvaluateInfo otherResult = evt.getOtherResult();
				String algName = otherResult != null ? otherResult.algName : null;
				if (algName != null) {
					updateAlgRegFromEvaluator(Arrays.asList(algName));
					cmbAlgs.update(algRegTable.getAlgList());
					cmbAlgs.setDefaultSelected(algName);
				}
				else
					cmbAlgs.update(algRegTable.getAlgList());
					
			}
			
			if (type != EvaluatorEvent.Type.start && timeDiff)
				guiData.pool = getLocalDatasetPool();
			else
				guiData.pool = new DatasetPool();
			guiData.pool.add(evt.getPoolResult().toDatasetPoolClient());
			
			if (guiData.pool.size() > 0) {
				txtTrainingBrowse.setDataset(guiData.pool.get(0).getTraining(), false);
				txtTestingBrowse.setDataset(guiData.pool.get(0).getTesting(), false);
			}
			else {
				txtTrainingBrowse.setDataset(null, false);
				txtTestingBrowse.setDataset(null, false);
			}
			
			timestamp = null;
		}
		else if (type == EvaluatorEvent.Type.pause ||
				type == EvaluatorEvent.Type.resume || 
				type == EvaluatorEvent.Type.stop) {
		}
		
		updateMode();
	}


	@Override
	public synchronized void receivedEvaluation(EvaluateEvent evt) throws RemoteException {
		if (chkVerbal.isSelected()) {
			String info = evt.translate() + "\n\n\n\n";
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkRunSave.isSelected()) {
			boolean saveResultSummary = false;
			try {
				saveResultSummary = evaluator.getConfig().isSaveResultSummary();
			} catch (Throwable e) {LogUtil.trace(e);}

			evProcessor.saveEvaluateResult(txtRunSaveBrowse.getText(), evt, algRegTable, saveResultSummary, EV_RESULT_FILENAME_PREFIX);
		}
		
		
		result = evt.getMetrics();
		if (result != null) recoveredResult = result; 
		if (evt.getType() == Type.done || evt.getType() == Type.done_one) {
			if (evt.getType() == Type.done)
				updateMode();
			else if (evt.getType() == Type.done_one) { //Limiting connect to server.
				try {
					Metrics tempResult = evaluator.getResult();
					if (tempResult != null) recoveredResult = tempResult;
				} catch (Exception e) {LogUtil.trace(e);}
			}
		}
		
	}
	
	
	@Override
	public synchronized void receivedProgress(EvaluateProgressEvent evt) throws RemoteException {
		if (this.prgRunning.getMaximum() < evt.getProgressTotal())
			this.prgRunning.setMaximum(evt.getProgressTotal());
		if (this.prgRunning.getValue() < evt.getProgressStep()) 
			this.prgRunning.setValue(evt.getProgressStep());

		String[] statuses = EvaluatorAbstract.extractEvaluateProgressInfo(evt);
		statusBar.setTextPane1(statuses != null && statuses.length > 0 ? statuses[0] : "");
		statusBar.setTextPane2(statuses != null && statuses.length > 1 ? statuses[1] : "");
	}


	@Override
	public synchronized void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		String[] statuses = EvaluatorAbstract.extractSetupInfo(evt);
		statusBar.setTextPane1(statuses != null && statuses.length > 0 ? statuses[0] : "");
		
		String algName = evt.getAlgName();
		if (algName == null) return;
		
		if (chkVerbal.isSelected()) {
			String info = "========== Algorithm \"" + algName + "\" ==========\n";
			info = info + evt.translate() + "\n\n\n\n";
			
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkRunSave.isSelected()) {
			boolean saveResultSummary = false;
			try {
				saveResultSummary = evaluator.getConfig().isSaveResultSummary();
			} catch (Throwable e) {LogUtil.trace(e);}

			evProcessor.saveSetupResult(this.txtRunSaveBrowse.getText(), evt, saveResultSummary, EV_RESULT_FILENAME_PREFIX);
		}
		
	}


	@Override
	public void receivedElapsedTime(CounterElapsedTimeEvent evt) throws RemoteException {
		if (statusBar != null && statusBar.getLastPane() != null) {
			String elapsedTimeText = Counter.formatTime(evt.getElapsedTime());
			statusBar.getLastPane().setText(elapsedTimeText);
		}
	}


	@Override
	protected synchronized void updateMode() {
		Dataset training = txtTrainingBrowse.getDataset();
		Dataset testing = txtTestingBrowse.getDataset();
		
		try {
			evProcessor.closeIOChannels();
			
			if (cmbAlgs.getItemCount() == 0) {
				setInternalEnable(false);
				setResultVisible(result != null && result.size() > 0);
				
				prgRunning.setMaximum(0);
				prgRunning.setValue(0);
				prgRunning.setVisible(false);
			}
			else if (training == null || testing == null) {
				setInternalEnable(false);
				setResultVisible(result != null && result.size() > 0);
				
				cmbAlgs.setEnabled(true);
				btnConfig.setEnabled(true);
				btnTrainingBrowse.setEnabled(true);
				btnTestingBrowse.setEnabled(true);
				btnRefresh.setEnabled(training != null || testing != null);
				btnClear.setEnabled(training != null || testing != null);
				btnUpload.setEnabled((training == null && testing == null) || (training != null && testing != null));
				btnDownload.setEnabled(true);
				
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
				else { //Paused
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
					prgRunning.setVisible(true); //Added by Loc Nguyen: 2020.03.10
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
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
			boolean flag = cmbAlgs.getItemCount() > 0 &&
				training != null && testing != null;
			setInternalEnable(flag);
			setResultVisible(flag);
			
			cmbAlgs.setEnabled(algRegTable.size() > 0);
			btnTrainingBrowse.setEnabled(true);
			btnTestingBrowse.setEnabled(true);
			btnUpload.setEnabled(
					flag && ((training == null && testing == null) || (training != null && testing != null)) );
			btnDownload.setEnabled(cmbAlgs.getItemCount() > 0);
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
		this.btnUpload.setEnabled(
			flag && ((trainingSet == null && testingSet == null) || (trainingSet != null && testingSet != null)) );
		this.btnDownload.setEnabled(flag);

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
			flag && result != null && result.size() > 0);
		this.btnCopyResult.setEnabled(
			flag && result != null && result.size() > 0);
	}

	
	/**
	 * Setting whether results are visible.
	 * @param flag flag to set whether results are visible.
	 */
	private void setResultVisible(boolean flag) {
		boolean visible = flag && 
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
			this.recoveredResult = this.result;
			this.result = null;
			this.tblMetrics.clear();
			this.statusBar.clearText();
			this.statusBar.setTextPane0(DSUtil.shortenVerbalName(evaluator.getName()));
			this.timestamp = null;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	@Override
	protected void updateGUIData() {
		if (cmbAlgs.getSelectedAlg() != null)
			guiData.algName = cmbAlgs.getSelectedAlg().getName();
		else
			guiData.algName = null;
		
		guiData.txtRunSaveBrowse = this.chkRunSave.isSelected() ? this.txtRunSaveBrowse.getText() : null;
		
		guiData.chkVerbal = this.chkVerbal.isSelected();
	}

	
	/**
	 * Add training dataset to pool.
	 * @param training specified training dataset.
	 */
	private void addTrainingToPool(Dataset training) {
		if (training == null) return;
		if (guiData.pool == null) guiData.pool = new DatasetPool();
		
		if (guiData.pool.size() == 0) {
			guiData.pool.add(new DatasetPair(training, null));
		}
		else {
			DatasetPair pair = guiData.pool.get(0);
			pair.setTraining(training);
			pair.setTrainingUUID(null);
		}
	}

	
	/**
	 * Add testing dataset to pool.
	 * @param testing specified testing dataset.
	 */
	private void addTestingToPool(Dataset testing) {
		if (testing == null) return;
		if (guiData.pool == null) guiData.pool = new DatasetPool();
		
		if (guiData.pool.size() == 0) {
			guiData.pool.add(new DatasetPair(null, testing));
		}
		else {
			DatasetPair pair = guiData.pool.get(0);
			pair.setTesting(testing);
			pair.setTestingUUID(null);
		}
	}

	
	/**
	 * Setting and backing up result.
	 * @param newResult new result.
	 */
	protected synchronized void setResult(Metrics newResult) {
		if (newResult == null) {
			this.recoveredResult = this.result;
			this.result = newResult;
		}
		else
			this.recoveredResult = this.result = newResult;
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
			LogUtil.trace(e);
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
					super.windowClosed(e);
					
					evaluateGUI.dispose();
				}

			});
			
			setLayout(new BorderLayout(2, 2));
			
			add(evaluateGUI, BorderLayout.CENTER);
		}

		@Override
		public void inspect() {
			setVisible(true);
		}
		
	}
	

	
}
