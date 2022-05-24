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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Reader;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgRemote;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.ui.AlgListBox;
import net.hudup.core.alg.ui.AlgListBox.AlgListChangedEvent;
import net.hudup.core.alg.ui.AlgListChooser;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Service;
import net.hudup.core.client.ServiceExt;
import net.hudup.core.data.BatchScript;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.DatasetPoolExchangedItem;
import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.NullPointer;
import net.hudup.core.data.ui.AddingDatasetDlg2;
import net.hudup.core.data.ui.DatasetPoolTable;
import net.hudup.core.data.ui.DatasetPoolsManager;
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
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.CounterElapsedTimeEvent;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.Light;
import net.hudup.core.logistic.ui.LoginDlg;
import net.hudup.core.logistic.ui.SortableSelectableTable;
import net.hudup.core.logistic.ui.SortableSelectableTableModel;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.core.logistic.ui.StatusBar;
import net.hudup.core.logistic.ui.TextField;
import net.hudup.core.logistic.ui.TxtOutput;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.logistic.ui.WaitDialog;
import net.hudup.server.ext.ExtendedService;

/**
 * This class represents a graphic user interface (GUI) for {@link EvaluatorAbstract} with many pairs of training dataset and testing dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 * 
 */
public class BatchEvaluateGUI extends AbstractEvaluateGUI {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Table to show dataset pool.
	 */
	protected DatasetPoolTable tblDatasetPool = null;
	
	
	/**
	 * Algorithm configuration button.
	 */
	protected JButton btnConfigAlgs = null;
	
	/**
	 * Algorithm list box.
	 */
	protected AlgListBox lbAlgs = null;
	
	/**
	 * Refresh button.
	 */
	protected JButton btnRefresh = null;
	
	/**
	 * Clear button.
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
	 * Adding dataset button.
	 */
	protected JButton btnAddDataset = null;
	
	/**
	 * Loading batch script button.
	 */
	protected JButton btnLoadBatchScript = null;
	
	/**
	 * Saving batch script button.
	 */
	protected JButton btnSaveBatchScript = null;
	
	
	/**
	 * Run button.
	 */
	protected JButton btnRun = null;
	
	/**
	 * Pause / resume button.
	 */
	protected JButton btnPauseResume = null;
	
	/**
	 * Stop button.
	 */
	protected JButton btnStop = null;
	
	/**
	 * Forced stop button.
	 */
	protected JButton btnForceStop = null;

	
	/**
	 * Panel of running info.
	 */
	protected JPanel paneRunInfo = null;
	
	/**
	 * Text area of running info.
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
	 * Signal light.
	 */
	protected Light signalLight = null;
	
	
	/**
	 * Constructor with local evaluator.
	 * @param evaluator local evaluator.
	 */
	public BatchEvaluateGUI(Evaluator evaluator) {
		this(evaluator, null, null);
	}

	
	/**
	 * Constructor with specified evaluator and bound URI.
	 * @param evaluator specified evaluator.
	 * @param connectInfo connection information.
	 */
	public BatchEvaluateGUI(Evaluator evaluator, ConnectInfo connectInfo) {
		this(evaluator, connectInfo, null);
	}

	
	/**
	 * Constructor with local evaluator and referred GUI data.
	 * @param evaluator local evaluator.
	 * @param referredGUIData referred GUI data.
	 */
	public BatchEvaluateGUI(Evaluator evaluator, EvaluateGUIData referredGUIData) {
		this(evaluator, null, referredGUIData);
	}

	
	/**
	 * Constructor with specified evaluator, bound URI, and GUI data.
	 * @param evaluator specified evaluator.
	 * @param connectInfo connection information.
	 * @param referredGUIData referred GUI data.
	 */
	public BatchEvaluateGUI(Evaluator evaluator, ConnectInfo connectInfo, EvaluateGUIData referredGUIData) {
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
		
		setVerbal(guiData != null ? guiData.chkVerbal : false);
		
		setupListeners(this.evaluator);
	}

	
	/**
	 * Returning this batch evaluator.
	 * @return this batch evaluator.
	 */
	private BatchEvaluateGUI getThisGUI() {
		return this;	
	}
	
	
	@Override
	protected synchronized boolean taskQueueFeed() {
		boolean connected = super.taskQueueFeed();
		if (signalLight != null && connectInfo.checkPullMode())
			signalLight.turn(connected);
		
		return connected;
	}


	@Override
	public void pluginChanged(PluginChangedEvent evt) throws RemoteException {
		try {
			if (evaluator.remoteIsStarted()) return;
			
			algRegTable.unexportNonPluginAlgs();
			algRegTable.clear();
			algRegTable.registerAsTheSame(EvaluatorAbstract.extractNormalAlgFromPluginStorage(evaluator, connectInfo)); //Algorithms are not cloned because of saving memory when evaluator GUI keep algorithms for a long time.
			
			List<String> algNames = updateAlgRegFromEvaluator();
			if (algNames != null && algNames.size() > 0)
				lbAlgs.update(algRegTable.getAlgList(algNames));
			else
				lbAlgs.update(algRegTable.getAlgList());
			
			clearResult();
			
			guiData.pool = getLocalDatasetPool();
			tblDatasetPool.update(guiData.pool);

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

		JPanel header = new JPanel(new BorderLayout(2, 2));
		
		JPanel up = new JPanel();
		up.setLayout(new GridLayout(1, 0));
		header.add(up, BorderLayout.NORTH);

		JPanel paneAlg = new JPanel();
		up.add(paneAlg);
		
		paneAlg.add(new JLabel(I18nUtil.message("algorithms") + ":"));
		
		this.lbAlgs = new AlgListBox(false, evaluator) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void addToContextMenu(JPopupMenu contextMenu) {
				super.addToContextMenu(contextMenu);
				
		    	final Alg selectedAlg = getSelectedAlg();
		    	if (selectedAlg == null) return;
				
		    	if (!PluginStorage.contains(selectedAlg) &&
		    			algRegTable.contains(selectedAlg.getName())) {
			    	
					JMenuItem miRegister = UIUtil.makeMenuItem((String)null, "Register", 
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								boolean ret = PluginStorage.getNormalAlgReg().register(selectedAlg);
								if (ret) JOptionPane.showMessageDialog(
										getThisGUI(), 
										"Algorithm '" + DSUtil.shortenVerbalName(selectedAlg.getName()) + "' is registered successfully.", 
										"Successfully registered", 
										JOptionPane.INFORMATION_MESSAGE);
							}
						});
					contextMenu.add(miRegister);

					JMenuItem miDiscard = UIUtil.makeMenuItem((String)null, "Discard", 
						new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent e) {
								algRegTable.unregister(selectedAlg.getName());
								lbAlgs.remove(selectedAlg);
								if (selectedAlg instanceof AlgRemote) {
									try {
										((AlgRemote)selectedAlg).unexport();
									} catch (Exception ex) {LogUtil.trace(ex);}
								}
								updateMode();
							}
						});
					contextMenu.add(miDiscard);
		    	}

				if (!guiData.isRefPool) {
			    	contextMenu.addSeparator();
					JMenuItem miTraining = UIUtil.makeMenuItem((String)null, "Add training set", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								addDataset(false, true);
							}
						});
					contextMenu.add(miTraining);
					
					Alg alg = getSelectedAlg();
					if (AlgDesc2.isAllowNullTrainingSet(alg)) {
						JMenuItem miNull = UIUtil.makeMenuItem((String)null, "Add null set",
							new ActionListener() {
								
								@Override
								public void actionPerformed(ActionEvent e) {
									addDataset(true, true);
								}
							});
						contextMenu.add(miNull);
					}
				}
			}
			
		};
		if (guiData.algNames != null && guiData.algNames.size() > 0)
			this.lbAlgs.update(algRegTable.getAlgList(guiData.algNames));
		else
			this.lbAlgs.update(algRegTable.getAlgList());
		this.lbAlgs.setVisibleRowCount(4);
		this.lbAlgs.addAlgListChangedListener(new AlgListBox.AlgListChangedListener() {
			
			@Override
			public void algListChanged(AlgListChangedEvent evt) {
				if (evaluator == null || algRegTable == null)
					return;
				
				try {
					List<Alg> list = evt.getAlgList();
					for (Alg alg : list) {
						if (!algRegTable.contains(alg.getName()))
							algRegTable.register(alg);
					}
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
		});
		JScrollPane scrollAlgsPane = new JScrollPane(this.lbAlgs);
		paneAlg.add(scrollAlgsPane);
		Dimension preferredSize = scrollAlgsPane.getPreferredSize();
		preferredSize.width = Math.max(preferredSize.width, 300);
		preferredSize.height = Math.max(preferredSize.height, 50);
		scrollAlgsPane.setPreferredSize(preferredSize);
		
		JPanel paneAlgTool = new JPanel();
		paneAlgTool.setLayout(new BoxLayout(paneAlgTool, BoxLayout.Y_AXIS));
		paneAlg.add(paneAlgTool);
		
		this.btnConfigAlgs = UIUtil.makeIconButton(
			"config-16x16.png", 
			"config", 
			I18nUtil.message("config"), 
			I18nUtil.message("config"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					AlgListChooser dlg = new AlgListChooser(getThisGUI(), algRegTable.getAlgList(), lbAlgs.getAlgList(), evaluator);
					if (!dlg.isOK())
						return;
					
					lbAlgs.update(dlg.getResult());
					updateMode();
				}
			});
		this.btnConfigAlgs.setMargin(new Insets(0, 0 , 0, 0));
		paneAlgTool.add(this.btnConfigAlgs);
			

		JPanel down = new JPanel(new BorderLayout(2, 2));
		header.add(down, BorderLayout.CENTER);
		
		this.tblDatasetPool = new DatasetPoolTable(false, connectInfo.bindUri) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void addToContextMenu(JPopupMenu contextMenu) {
				super.addToContextMenu(contextMenu);
			}

			@Override
			public boolean removeSelectedRows() {
				boolean ret = super.removeSelectedRows();
				
				if (ret) {
					clearResult();
					
					if (bindUri == null) {
						try {
							evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), getThisGUI(), timestamp = new Timestamp());
						} catch (Throwable e) {LogUtil.trace(e);}
					}
					else
						updateMode();
				}
				
				return ret;
			}
			
			@Override
			public void saveScript() {
				saveBatchScript(true);
			}

			@Override
			protected void addScript() {
				loadBatchScript(true);
			}

			@Override
			protected void addTraining() {
				addDataset(false, true);
			}

		};
		this.tblDatasetPool.update(guiData.pool);
		this.tblDatasetPool.setPreferredScrollableViewportSize(new Dimension(600, 80));
		down.add(new JScrollPane(this.tblDatasetPool), BorderLayout.CENTER);

		JPanel tool = new JPanel(new BorderLayout());
		down.add(tool, BorderLayout.SOUTH);
		
		JPanel toolGrp1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		tool.add(toolGrp1, BorderLayout.WEST);
		
		this.btnAddDataset = new JButton(I18nUtil.message("add_dataset"));
		this.btnAddDataset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				addDataset(false, false);
			}
		});
		this.btnAddDataset.setMnemonic('a');
		toolGrp1.add(this.btnAddDataset);
		
		this.btnLoadBatchScript = new JButton(I18nUtil.message("load_script"));
		this.btnLoadBatchScript.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				loadBatchScript(false);
			}
		});
		this.btnLoadBatchScript.setMnemonic('l');
		toolGrp1.add(this.btnLoadBatchScript);

		JPanel toolGrp2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		tool.add(toolGrp2, BorderLayout.EAST);
		
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
		toolGrp2.add(this.btnRefresh);

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
		toolGrp2.add(this.btnClear);

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
		try {
			this.btnUpload.setVisible(connectInfo.bindUri != null || evaluator.isAgent());
		} catch (Exception e) {LogUtil.trace(e);}
		toolGrp2.add(this.btnUpload);

		this.btnDownload = UIUtil.makeIconButton(
			"download-16x16.png", 
			"download", 
			I18nUtil.message("retrieve_dataset_ref"), 
			I18nUtil.message("retrieve_dataset_ref"), 
				
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
							tblDatasetPool.update(guiData.pool);
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
		toolGrp2.add(this.btnDownload);

		this.btnSaveBatchScript = UIUtil.makeIconButton(
			"save-16x16.png", 
			"save", 
			I18nUtil.message("save_script"), 
			I18nUtil.message("save_script"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					saveBatchScript(true);
				}
				
			});
		this.btnSaveBatchScript.setMargin(new Insets(0, 0 , 0, 0));
		this.btnSaveBatchScript.setVisible(false);
		toolGrp2.add(this.btnSaveBatchScript);

		this.btnForceStop = UIUtil.makeIconButton(
			"forcestop-16x16.png", 
			I18nUtil.message("force_stop"), 
			I18nUtil.message("force_stop_tooltip"), 
			I18nUtil.message("force_stop"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					forceStop();
				}
				
			});
		this.btnForceStop.setMargin(new Insets(0, 0 , 0, 0));
		toolGrp2.add(this.btnForceStop);

		return header;
	}

	
	/**
	 * Create body.
	 * @return {@link JPanel} as body.
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
		this.btnRun.setMnemonic('r');
		paneControl.add(this.btnRun);
		
		this.btnPauseResume = new JButton(I18nUtil.message("pause"));
		this.btnPauseResume.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				pauseResume();
			}
		});
		this.btnPauseResume.setMnemonic('u');
		paneControl.add(this.btnPauseResume);

		this.btnStop = new JButton(I18nUtil.message("stop"));
		this.btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stop();
			}
			
		});
		this.btnStop.setMnemonic('s');
		paneControl.add(this.btnStop);

		JPanel main = new JPanel(new GridLayout(0, 1));
		body.add(main, BorderLayout.CENTER);

		this.paneRunInfo = new JPanel(new BorderLayout());
		main.add(this.paneRunInfo);
		this.txtRunInfo = new TxtOutput();
		this.txtRunInfo.setRows(10);
		this.txtRunInfo.setColumns(10);
		this.txtRunInfo.setEditable(false);
		this.paneRunInfo.add(new JScrollPane(this.txtRunInfo), BorderLayout.CENTER);
		
		this.paneRunSave = new JPanel(new BorderLayout(2, 2));
		main.add(this.paneRunSave);
		JPanel pane = new JPanel(new BorderLayout(2, 2));
		this.paneRunSave.add(pane, BorderLayout.NORTH);
		this.txtRunSaveBrowse = new TextField();
		this.txtRunSaveBrowse.setEditable(false);
		this.txtRunSaveBrowse.setToolTipText(I18nUtil.message("save_evaluate_place"));
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
					else {
						txtRunSaveBrowse.setText(store.toString());
					}
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
		this.prgRunning.setMaximum(otherResult.progressTotal);
		this.prgRunning.setValue(otherResult.progressStep);
		this.prgRunning.setVisible(false);
		toolbar.add(this.prgRunning, BorderLayout.CENTER);

		return body;
	}

	
	/**
	 * Create footer.
	 * @return {@link JPanel} as footer.
	 */
	private JPanel createFooter() {
		JPanel footer = new JPanel(new BorderLayout());
		
		this.paneResult = new JPanel(new BorderLayout());
		footer.add(this.paneResult, BorderLayout.CENTER);
		
		this.tblMetrics = new MetricsTable(new RegisterTable(lbAlgs.getAlgList()), evaluator) {

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
							boolean success = true;
							try {
								result = getThisGUI().evaluator.getResult();
							}
							catch (Exception ex) {success = false; ex.printStackTrace();}
							if (!success) {
								JOptionPane.showMessageDialog(
									getThisGUI(), 
									"Unable to connect server", 
									"Unable to connect server", 
									JOptionPane.ERROR_MESSAGE);
								return;
							}
							
							if (result == null) {
								getThisGUI().recoveredResult = getThisGUI().result;
								getThisGUI().result = result;
								
								JOptionPane.showMessageDialog(
										getThisGUI(), 
										"Empty resulted metrics", 
										"Empty resulted metrics", 
										JOptionPane.WARNING_MESSAGE);
							}
							else
								getThisGUI().recoveredResult = getThisGUI().result = result;
							
							update(result);
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
					}
					catch (Exception ex) {
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
		this.btnAnalyzeResult.setMnemonic('y');
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
		
		this.statusBar = new StatusBar() {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected List<String> getAdditionalTexts() {
				List<String> texts = Util.newList();
				
				EvaluateInfo otherResult0 = null;
				try {
					otherResult0 = evaluator.getOtherResult();
				} catch (Exception e) {otherResult0 = null; LogUtil.trace(e);}
				
				if (otherResult0 != null) {
					otherResult = otherResult0;
					if (otherResult.startDate > 0)
						texts.add("Started date: " + MathUtil.format(new Date(otherResult.startDate)));
					if (otherResult.endDate > 0)
						texts.add("Ended date: " + MathUtil.format(new Date(otherResult.endDate)));
				}
				
				if (connectInfo != null) texts.add("Connection information: " + connectInfo.toString());
				
				if (guiData.isRefPool) {
					try {
						texts.add("Referred pool: " + evaluator.getInfo().refPoolResultName);
					} catch (Throwable e) {}
				}
				
				return texts;
			}
			
		};
		updateStatusBar();
		statusPane.add(this.statusBar, BorderLayout.CENTER);

		this.signalLight = new Light();
		this.signalLight.turn(connectInfo.checkPullMode());
		statusPane.add(this.signalLight, BorderLayout.EAST);

		return footer;
		
	}

	
	/**
	 * Setting verbal mode.
	 * @param verbal specified verbal mode.
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
	
	
	@Override
	protected List<Alg> getCurrentAlgList() {
		return lbAlgs.getAlgList();
	}


	@Override
	protected void refresh() {
		try {
			if (evaluator.remoteIsStarted() || connectInfo.bindUri != null)
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
			updateMode();
		}
	}
	
	
	@Override
	public synchronized void refreshEvaluate() {
		super.refreshEvaluate();
		
		List<String> algNames = otherResult != null ? otherResult.algNames : null;
		lbAlgs.unexportNonPluginAlgs();
		if (algNames != null && algNames.size() > 0) {
			updateAlgRegFromEvaluator(algNames);
			lbAlgs.update(algRegTable.getAlgList(algNames));
		}
		else
			lbAlgs.update(algRegTable.getAlgList());

		try {
			DatasetPoolExchanged pool = this.evaluator.getDatasetPool();
			if (pool != null) {
				guiData.pool = pool.toDatasetPoolClient();
				tblDatasetPool.update(guiData.pool);
			}
		} catch (Throwable e) {LogUtil.trace(e);}

		tblMetrics.update(result);

		updateStatusBar();
		
		updateMode();
	}


	@Override
	protected void clear() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			lbAlgs.update(algRegTable.getAlgList());
			clearResult();

			if (connectInfo.bindUri == null) {
				try {
					evaluator.updatePool(null, this, timestamp = new Timestamp());
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			else {
				guiData.pool = new DatasetPool();
				tblDatasetPool.update(guiData.pool);
				updateMode();
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			updateMode();
		}
	}
	
	
	@Override
	protected void run() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			if (guiData.pool.size() == 0 || lbAlgs.getAlgList().size() == 0) {
				JOptionPane.showMessageDialog(
					getThisGUI(), 
					"Not load data set pool", 
					"Not load data set pool", 
					JOptionPane.WARNING_MESSAGE);
				
				return;
			}
			
			clearResult();
			boolean started = false;
			if (connectInfo.bindUri == null)
				started = evaluator.remoteStart(lbAlgs.getAlgList(), toDatasetPoolExchangedClient(guiData.pool), timestamp = new Timestamp(), null);
			else {
				DataConfig config = lbAlgs.getAlgDescMapRemote();
				started = evaluator.remoteStart(lbAlgs.getAlgNameList(), toDatasetPoolExchangedClient(guiData.pool), connectInfo.checkPullMode() ? null : this, config, timestamp = new Timestamp(), null);
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

		if (type == EvaluatorEvent.Type.start || type == EvaluatorEvent.Type.update_pool || type == EvaluatorEvent.Type.ref_pool || type == EvaluatorEvent.Type.unref_pool) {
			if (evt.getType() == EvaluatorEvent.Type.start) {
				updatePluginFromEvaluator();
				
				EvaluateInfo otherResult = evt.getOtherResult();
				List<String> algNames = otherResult != null ? otherResult.algNames : null;
				lbAlgs.unexportNonPluginAlgs();
				if (algNames != null && algNames.size() > 0) {
					updateAlgRegFromEvaluator(algNames);
					lbAlgs.update(algRegTable.getAlgList(algNames));
				}
				else
					lbAlgs.update(algRegTable.getAlgList());
			}
			
			if (type != EvaluatorEvent.Type.start && timeDiff)
				guiData.pool = getLocalDatasetPool();
			else
				guiData.pool = new DatasetPool();
			guiData.pool.add(evt.getPoolResult().toDatasetPoolClient());
			guiData.isRefPool = evt.getEvInfo().isRefPoolResult;
			
			tblDatasetPool.update(guiData.pool);
			
			timestamp = null;
		}
		else if (type == EvaluatorEvent.Type.pause ||
				type == EvaluatorEvent.Type.resume || 
				type == EvaluatorEvent.Type.stop) {
		}
		
		try {
			EvaluateInfo otherResult = evt.getOtherResult();
			if (otherResult != null) this.otherResult = otherResult;
		} catch (Exception e) {LogUtil.trace(e);}
		
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

			evProcessor.saveSetupResult(txtRunSaveBrowse.getText(), evt, saveResultSummary, EV_RESULT_FILENAME_PREFIX);
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
		try {
			evProcessor.closeIOChannels();
			
			if (lbAlgs.getAlgList().size() == 0) {
				setInternalEnable(false);
				setResultVisible(result != null && result.size() > 0);
				
				btnConfigAlgs.setEnabled(algRegTable.size() > 0);
				btnAddDataset.setEnabled(PluginStorage.getParserReg().size() > 0 && !guiData.isRefPool);
				btnClear.setEnabled(guiData.pool.size() > 0 && !guiData.isRefPool);
				btnUpload.setEnabled(true && !guiData.isRefPool);
				btnDownload.setEnabled(true && !guiData.isRefPool);
				
				tblMetrics.update(result);
				prgRunning.setMaximum(0);
				prgRunning.setValue(0);
				prgRunning.setVisible(false);
			}
			else if (guiData.pool.size() == 0) {
				setInternalEnable(false);
				setResultVisible(result != null && result.size() > 0);
				
				lbAlgs.setEnabled(true);
				btnConfigAlgs.setEnabled(true);
				btnAddDataset.setEnabled(true && !guiData.isRefPool);
				btnLoadBatchScript.setEnabled(true && !guiData.isRefPool);
				btnUpload.setEnabled(true && !guiData.isRefPool);
				btnDownload.setEnabled(true && !guiData.isRefPool);
				
				tblMetrics.update(result);
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
				
				btnSaveBatchScript.setEnabled(true);
			}
			else {
				setInternalEnable(true);
				setResultVisible(true);
				
				btnPauseResume.setText(I18nUtil.message("pause"));
				btnPauseResume.setEnabled(false);
				btnStop.setEnabled(false);
				btnForceStop.setEnabled(false);
				
				btnMetricsOption.setEnabled(PluginStorage.getMetricReg().size() > 0);
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
			
			boolean flag = lbAlgs.getAlgList().size() > 0 && guiData.pool.size() > 0;
			setInternalEnable(flag);
			setResultVisible(flag);
			
			btnConfigAlgs.setEnabled(algRegTable.size() > 0);
			btnAddDataset.setEnabled(PluginStorage.getParserReg().size() > 0 && !guiData.isRefPool);
			btnUpload.setEnabled(true && !guiData.isRefPool);
			btnDownload.setEnabled(true && !guiData.isRefPool);
		}
		
		updateGUI();
	}
	
	
	/**
	 * Enable / disable internal controls.
	 * @param flag flag to / disable internal controls.
	 */
	private void setInternalEnable(boolean flag) {
		flag = flag && algRegTable.size() > 0;
		
		this.btnConfigAlgs.setEnabled(flag);
		this.lbAlgs.setEnabled(flag);
		this.tblDatasetPool.setEnabled2(flag && guiData.pool.size() > 0 && !guiData.isRefPool);
		this.btnAddDataset.setEnabled(flag && !guiData.isRefPool);
		this.btnLoadBatchScript.setEnabled(flag && !guiData.isRefPool);
		this.btnSaveBatchScript.setEnabled(flag && guiData.pool.size() > 0 && !guiData.isRefPool);
		
		this.btnRefresh.setEnabled(flag && guiData.pool.size() > 0 && !guiData.isRefPool);
		this.btnClear.setEnabled(flag && guiData.pool.size() > 0 && !guiData.isRefPool);
		this.btnUpload.setEnabled(flag && !guiData.isRefPool);
		this.btnDownload.setEnabled(flag && !guiData.isRefPool);

		this.btnRun.setEnabled(flag && guiData.pool.size() > 0);
		this.btnPauseResume.setEnabled(flag && guiData.pool.size() > 0);
		this.btnStop.setEnabled(flag && guiData.pool.size() > 0);
		this.btnForceStop.setEnabled(flag && guiData.pool.size() > 0);

		this.txtRunInfo.setEnabled(flag && guiData.pool.size() > 0);
		this.chkRunSave.setEnabled(flag && guiData.pool.size() > 0);
		this.chkRunSave.setEnabled(flag && guiData.pool.size() > 0);
		this.txtRunSaveBrowse.setEnabled(flag && guiData.pool.size() > 0);
		this.chkVerbal.setEnabled(flag);
		
		this.btnMetricsOption.setEnabled(flag);
		this.btnAnalyzeResult.setEnabled(
			flag && result != null && result.size() > 0);
		this.btnCopyResult.setEnabled(
				flag && result != null && result.size() > 0);
	}

	
	/**
	 * Making visibility of result controls.
	 * @param flag flag to make visibility of result controls.
	 */
	private void setResultVisible(boolean flag) {
		
		boolean visible = flag && result != null && result.size() > 0;
 
		paneResult.setVisible(visible);
		btnAnalyzeResult.setEnabled(visible);
		btnCopyResult.setEnabled(visible);
	}

	
	/**
	 * Load batch script.
	 * @param append Flag to indicate whether to append to current pool.
	 */
	private void loadBatchScript(boolean append) {
		try {
			if (evaluator.remoteIsStarted() || this.lbAlgs.getAlgList().size() == 0)
				return;
			
			UriAdapter adapter = new UriAdapter();
			xURI uri = adapter.chooseUri(
					this, 
					true, 
					new String[] {"properties", "script", Constants.DEFAULT_EXT}, 
					new String[] {"Properties files (*.properties)", "Script files (*.script)", "Hudup files (*." + Constants.DEFAULT_EXT + ")"},
					null,
					null);
			adapter.close();
			
			if (uri == null) {
				JOptionPane.showMessageDialog(
						this, 
						"URI not open", 
						"URI not open", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			adapter = new UriAdapter(uri);
			Reader reader = adapter.getReader(uri);
			JDialog dlgWait = WaitDialog.createDialog(this); dlgWait.setUndecorated(true);
			SwingWorker<BatchScript, BatchScript> worker = new SwingWorker<BatchScript, BatchScript>() {
				@Override
				protected BatchScript doInBackground() throws Exception {
					return BatchScript.parse(reader, evaluator.getMainUnit());
				}
				
				@Override
				protected void done() {
					super.done(); dlgWait.dispose();
				}
			};
			worker.execute(); dlgWait.setVisible(true);
			BatchScript script = worker.get();
			reader.close();
			adapter.close();
			
			if (script == null) {
				JOptionPane.showMessageDialog(
						this, 
						"Batch not created", 
						"Batch not created", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			List<Alg> batchAlgList = algRegTable.getAlgList(script.getAlgNameListNoDuplicate());
			if (batchAlgList.size() == 0) {
				JOptionPane.showMessageDialog(
						this, 
						"Algorithms in batch script are not suitable to this evaluator",
						"Batch script has unsuitable algorithms", 
						JOptionPane.WARNING_MESSAGE);
			}
			else {
				if (append)
					this.lbAlgs.addAllNoRefDuplicate(batchAlgList);
				else
					this.lbAlgs.update(batchAlgList);
			}
			
			if (!append) guiData.pool.removeAllNoClearDatasets();
			DatasetPool scriptPool = script.getPool();
			Alg[] algList = this.lbAlgs.getAlgList().toArray(new Alg[] {} );
			for (int i = 0; i < scriptPool.size(); i++) {
				DatasetPair pair = scriptPool.get(i);
				if (pair == null || pair.getTraining() == null)
					continue;
				
				Dataset trainingSet = pair.getTraining();
				if (DatasetUtil2.validateTrainingset(this, trainingSet, algList))
					guiData.pool.add(pair);
			}
			
			clearResult();
			if (connectInfo.bindUri == null) {
				try {
					evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), this, timestamp = new Timestamp());
				} catch (Throwable e) {LogUtil.trace(e);}
				
			}
			else {
				tblDatasetPool.update(guiData.pool);
				updateMode();
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			updateMode();
		}
	}
	
	
	/**
	 * Saving batch script.
	 * @param strict strict mode.
	 */
	protected void saveBatchScript(boolean strict) {
		List<String> algNameList = lbAlgs.getAlgNameList();
		if (strict && (algNameList.size() == 0 || guiData.pool.size() == 0)) {
			JOptionPane.showMessageDialog(
				this, 
				"No algorithm or empty pool",
				"No algorithm or empty pool", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		UriAdapter adapter = new UriAdapter();
        xURI uri = adapter.chooseUri(
				this, 
				false, 
				new String[] {"properties", "script", Constants.DEFAULT_EXT}, 
				new String[] {"Properties files (*.properties)", "Script files (*.script)", "Hudup files (*." + Constants.DEFAULT_EXT + ")"},
				null,
				null);
        adapter.close();
        
        if (uri == null) {
			JOptionPane.showMessageDialog(
				this, 
				"URI not save", 
				"URI not save", 
				JOptionPane.WARNING_MESSAGE);
			return;
        }
        
		adapter = new UriAdapter(uri);
		boolean existed = adapter.exists(uri);
		adapter.close();
        if (existed) {
        	int ret = JOptionPane.showConfirmDialog(
    			this, 
    			"URI exist. Do you want to override it?", 
    			"URI exist", 
    			JOptionPane.YES_NO_OPTION, 
    			JOptionPane.QUESTION_MESSAGE);
        	if (ret == JOptionPane.NO_OPTION)
        		return;
        }
		
		adapter = null;
		Writer writer = null;
        try {
			adapter = new UriAdapter(uri);
    		writer = adapter.getWriter(uri, false);
    		
			BatchScript script = BatchScript.assign(
					guiData.pool, algNameList, evaluator.getMainUnit());
			
			if (strict)
				script.save(writer);
			else
				script.saveEasy(writer);
    		writer.flush();
    		writer.close();
    		writer = null;
	        
        	JOptionPane.showMessageDialog(this, 
        			"URI saved successfully", "URI saved successfully", JOptionPane.INFORMATION_MESSAGE);
        }
		catch(Exception e) {
			LogUtil.trace(e);
		}
        finally {
        	try {
        		if (writer != null)
        			writer.close();
        	}
        	catch (Exception e) {
        		LogUtil.trace(e);
        	}
        	
        	if (adapter != null)
        		adapter.close();
        }
	}
	
	
	/**
	 * Adding dataset.
	 * @param nullTraining true if add null training dataset {@link NullPointer}.
	 * @param nullTesting true if add null testing dataset {@link NullPointer}.
	 */
	protected void addDataset(boolean nullTraining, boolean nullTesting) {
		try {
			if (evaluator.remoteIsStarted() || PluginStorage.getParserReg().size() == 0)
				return;
			
			clearResult();

			if (nullTraining && nullTesting) {
				DatasetPair pair = new DatasetPair(new NullPointer(), new NullPointer(), null);
				guiData.pool.add(pair);
			}
			else {
				AddingDatasetDlg2 adder = new AddingDatasetDlg2(this, guiData.pool, this.lbAlgs.getAlgList(), evaluator.getMainUnit(), connectInfo.bindUri);
				adder.setMode(nullTraining, nullTesting);
				adder.setVisible(true);
			}
			
			if (connectInfo.bindUri == null) {
				try {
					evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), this, timestamp = new Timestamp());
				} catch (Throwable e) {LogUtil.trace(e);}
			}
			else {
				this.tblDatasetPool.update(guiData.pool);
				updateMode();
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			updateMode();
		}
	}


	/**
	 * Clearing result information.
	 */
	private void clearResult() {
		try {
			this.txtRunInfo.setText("");
			this.recoveredResult = result;
			this.result = null;
			this.tblMetrics.clear();
			this.statusBar.clearText();
			this.statusBar.setTextPane0(DSUtil.shortenVerbalName(evaluator.getVersionName()));
			this.timestamp = null;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Updating status bar.
	 */
	private void updateStatusBar() {
		if (statusBar == null) return;
		
		try {
			statusBar.setTextPane0(DSUtil.shortenVerbalName(evaluator.getVersionName()));
		} catch (Exception e) {LogUtil.trace(e);}
		
		if (otherResult.statuses != null && otherResult.statuses.length > 0) {
			if (otherResult.statuses[0] != null)
				statusBar.setTextPane1(otherResult.statuses[0]);
			if (otherResult.statuses.length > 1 && otherResult.statuses[1] != null)
				statusBar.setTextPane2(otherResult.statuses[1]);
		}
		
		if (otherResult.elapsedTime > 0) {
			String elapsedTimeText = Counter.formatTime(otherResult.elapsedTime);
			statusBar.getLastPane().setText(elapsedTimeText);
		}
	}
	
	
	@Override
	protected void updateGUIData() {
		guiData.algNames = lbAlgs.getAlgNameList();
		
		guiData.txtRunSaveBrowse = this.chkRunSave.isSelected() ? this.txtRunSaveBrowse.getText() : null;
		
		guiData.chkVerbal = this.chkVerbal.isSelected();
	}


	/**
	 * Update (repaint) all controls.
	 * This method can cause error ({@link MetricsTable}): The call of {@link DefaultTableModel#getValueAt(int, int)} can cause out of bound error from {@link DefaultTableColumnModel#getColumn(int)}
	 * Please see:
	 * {@link SortableTable#getCellRenderer(int, int)},
	 * {@link SortableTable#getCellEditor(int, int)},
	 * {@link SortableTableModel#getColumnClass(int)},
	 * {@link SortableSelectableTable#getCellRenderer(int, int)},
	 * {@link SortableSelectableTable#getCellEditor(int, int)},
	 * {@link SortableSelectableTableModel#getColumnClass(int)}
	 */
	@Deprecated
	private void updateGUI() {
		try {
//			validate(); //This code line can be removed.
//			updateUI();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Attaching referred dataset pool.
	 */
	protected void attachRefDatasetPool() {
		if (guiData.isRefPool) {
			JOptionPane.showMessageDialog(this, "Dataset pool was attached", "Dataset pool was attached", JOptionPane.ERROR_MESSAGE);
			return;
		}
		PowerServer server = EvaluatorAbstract.getServerByPluginChangedListenersPath(this.evaluator);
		if (server == null) {
			JOptionPane.showMessageDialog(this, "This is not server related evaluator", "Not server related evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			if (evaluator.remoteIsStarted()) {
				JOptionPane.showMessageDialog(this, "Evaluator started", "Evaluator started", JOptionPane.ERROR_MESSAGE);
				return;
			}

			Service service = server.getService();
			if (service == null || !(service instanceof ServiceExt)) return;
			DatasetPoolsService poolsService = null;
			if (service instanceof ExtendedService)
				poolsService = ((ExtendedService)service).getDatasetPoolsService();
			else {
				LoginDlg login = new LoginDlg(this, "Enter user name and password");
				if (!login.wasLogin()) return;
				poolsService = ((ServiceExt)service).getDatasetPoolsService(login.getUsername(), login.getPassword());
			}
			if (poolsService == null) {
				JOptionPane.showMessageDialog(this, "Cannot get pools service", "Cannot get pools service.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			DatasetPoolsManager manager = DatasetPoolsManager.show(poolsService, this);
			DatasetPoolExchangedItem poolItem = manager.getSelectedPool();
			if (poolItem == null) {
				JOptionPane.showMessageDialog(this, "Pool not selected", "Pool not selected.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			try {
				evaluator.refPool(true, poolItem.getDatasetPool(), poolItem.getName(), this, timestamp = new Timestamp());
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		catch (Throwable e) {
			updateMode();
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Detaching referred dataset pool.
	 */
	protected void detachRefDatasetPool() {
		if (!guiData.isRefPool) {
			JOptionPane.showMessageDialog(this, "Dataset pool was not attached", "Dataset pool was not attached", JOptionPane.ERROR_MESSAGE);
			return;
		}
		PowerServer server = EvaluatorAbstract.getServerByPluginChangedListenersPath(this.evaluator);;
		if (server == null) {
			JOptionPane.showMessageDialog(this, "This is not server related evaluator", "Not server related evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			if (evaluator.remoteIsStarted()) {
				JOptionPane.showMessageDialog(this, "Evaluator started", "Evaluator started", JOptionPane.ERROR_MESSAGE);
				return;
			}

			evaluator.refPool(false, null, null, this, timestamp = new Timestamp());
		}
		catch (Throwable e) {
			updateMode();
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Putting referred dataset pool.
	 */
	protected void putRefDatasetPool() {
		if (guiData.isRefPool) {
			JOptionPane.showMessageDialog(this, "Dataset pool was attached", "Dataset pool was attached", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (guiData.pool.size() == 0) {
			JOptionPane.showMessageDialog(this, "Empty dataset pool", "Empty pool", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (guiData.pool.containsClients()) {
			JOptionPane.showMessageDialog(this, "Client dataset pool", "Client dataset pool", JOptionPane.ERROR_MESSAGE);
			return;
		}
		PowerServer server = EvaluatorAbstract.getServerByPluginChangedListenersPath(this.evaluator);;
		if (server == null) {
			JOptionPane.showMessageDialog(this, "This is not server related evaluator", "Not server related evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			if (evaluator.remoteIsStarted()) {
				JOptionPane.showMessageDialog(this, "Evaluator started", "Evaluator started", JOptionPane.ERROR_MESSAGE);
				return;
			}

			Service service = server.getService();
			if (service == null || !(service instanceof ServiceExt)) return;
			DatasetPoolsService poolsService = null;
			if (service instanceof ExtendedService)
				poolsService = ((ExtendedService)service).getDatasetPoolsService();
			else {
				LoginDlg login = new LoginDlg(this, "Enter user name and password");
				if (!login.wasLogin()) return;
				poolsService = ((ServiceExt)service).getDatasetPoolsService(login.getUsername(), login.getPassword());
			}
			if (poolsService == null) {
				JOptionPane.showMessageDialog(this, "Cannot get pools service", "Cannot get pools service.", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (poolsService.contains(evaluator.getName())) {
				JOptionPane.showMessageDialog(this, "This pool was put before", "This pool was put before", JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			DatasetPoolExchanged exchangedPool = guiData.pool.toDatasetPoolExchanged().toDatasetPool(null).toDatasetPoolExchanged();
			evaluator.updatePoolWithoutClear(null, this, timestamp = new Timestamp());
			if (poolsService.put(evaluator.getName(), exchangedPool)) {
				JOptionPane.showMessageDialog(this, "Successfull to put this pool", "Successfull to put this pool", JOptionPane.INFORMATION_MESSAGE);
			}
		}
		catch (Throwable e) {
			updateMode();
			LogUtil.trace(e);
		}
	}
	
	
}
