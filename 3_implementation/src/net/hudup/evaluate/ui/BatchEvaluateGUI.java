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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Reader;
import java.io.Writer;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.ui.AlgListBox;
import net.hudup.core.alg.ui.AlgListBox.AlgListChangedEvent;
import net.hudup.core.alg.ui.AlgListChooser;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.NullPointer;
import net.hudup.core.evaluate.EvaluateEvent;
import net.hudup.core.evaluate.EvaluateEvent.Type;
import net.hudup.core.evaluate.EvaluateProgressEvent;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.CounterElapsedTimeEvent;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.SortableSelectableTable;
import net.hudup.core.logistic.ui.SortableSelectableTableModel;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.core.logistic.ui.TextField;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.BatchScript;
import net.hudup.data.DatasetUtil2;
import net.hudup.data.ui.DatasetPoolTable;
import net.hudup.data.ui.StatusBar;
import net.hudup.data.ui.TxtOutput;

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
//	protected StatusBar2 statusBar = null;
	
	
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
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 */
	public BatchEvaluateGUI(Evaluator evaluator, xURI bindUri) {
		this(evaluator, bindUri, null);
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
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 * @param referredGUIData referred GUI data.
	 */
	public BatchEvaluateGUI(Evaluator evaluator, xURI bindUri, EvaluateGUIData referredGUIData) {
		super(evaluator, bindUri, referredGUIData, null);
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
	}

	
	/**
	 * Returning this batch evaluator.
	 * @return this batch evaluator.
	 */
	private BatchEvaluateGUI getThisGUI() {
		return this;	
	}
	
	
	@Override
	public void pluginChanged(PluginChangedEvent evt) {
		try {
			evaluator.clearDelayUnsetupAlgs();
			
			algRegTable.clear();
			algRegTable.register(EvaluatorAbstract.extractAlgFromPluginStorage(evaluator)); //Algorithms are not cloned because of saving memory when evaluator GUI keep algorithms for a long time.
			lbAlgs.update(algRegTable.getAlgList());
			
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

		JPanel header = new JPanel(new BorderLayout(2, 2));
		
		JPanel up = new JPanel();
		up.setLayout(new GridLayout(1, 0));
		header.add(up, BorderLayout.NORTH);

		JPanel paneAlg = new JPanel();
		up.add(paneAlg);
		
		paneAlg.add(new JLabel(I18nUtil.message("algorithms") + ":"));
		
		this.lbAlgs = new AlgListBox(false) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void addToContextMenu(JPopupMenu contextMenu) {
				// TODO Auto-generated method stub
				super.addToContextMenu(contextMenu);
				
		    	final Alg selectedAlg = getSelectedAlg();
		    	if (selectedAlg == null) return;
				
		    	if ((selectedAlg instanceof DuplicatableAlg) && 
		    			!(PluginStorage.getNormalAlgReg().contains(selectedAlg.getName())) &&
		    			algRegTable.contains(selectedAlg.getName())) {
			    	
		    		contextMenu.addSeparator();
					JMenuItem miDiscard = UIUtil.makeMenuItem((String)null, "Discard algorithm", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								algRegTable.unregister(selectedAlg.getName());
								lbAlgs.remove(selectedAlg);
								
								updateMode();
							}
						});
					contextMenu.add(miDiscard);
		    	}

		    	contextMenu.addSeparator();
				JMenuItem miTraining = UIUtil.makeMenuItem((String)null, "Add training", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							addDataset(true);
						}
					});
				contextMenu.add(miTraining);
			}
			
		};
		this.lbAlgs.update(algRegTable.getAlgList(guiData.algNames));
		this.lbAlgs.setVisibleRowCount(4);
		this.lbAlgs.addAlgListChangedListener(new AlgListBox.AlgListChangedListener() {
			
			@Override
			public void algListChanged(AlgListChangedEvent evt) {
				// TODO Auto-generated method stub
				if (evaluator == null || algRegTable == null)
					return;
				
				try {
					List<Alg> list = evt.getAlgList();
					for (Alg alg : list) {
						if (!evaluator.acceptAlg(alg)) continue;
						if (!algRegTable.contains(alg.getName()))
							algRegTable.register(alg);
					}
				}
				catch (Throwable e) {
					e.printStackTrace();
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
					// TODO Auto-generated method stub
					
//					List<Alg> list = lbAlgs.getAlgList();
//					if (list.size() == 0) {
//						JOptionPane.showMessageDialog(
//								getThisGUI(), 
//								"List empty", 
//								"List empty", 
//								JOptionPane.ERROR_MESSAGE);
//						
//						return;
//						
//					}
					
					AlgListChooser dlg = new AlgListChooser(getThisGUI(), algRegTable.getAlgList(), lbAlgs.getAlgList());
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
		
		this.tblDatasetPool = new DatasetPoolTable(false, bindUri) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean removeSelectedRows() {
				// TODO Auto-generated method stub
				boolean ret = super.removeSelectedRows();
				
				if (ret) {
					clearResult();
					
					if (bindUri == null) {
						try {
							evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool),
									getThisGUI().timestamp = new Timestamp());
						} catch (Throwable e) {e.printStackTrace();}
					}
					else {
						updateMode();
					}
				}
				
				return ret;
			}
			
			@Override
			public void save() {
				saveBatchScript();
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
				// TODO Auto-generated method stub
				addDataset(false);
			}
		});
		toolGrp1.add(this.btnAddDataset);
		
		this.btnLoadBatchScript = new JButton(I18nUtil.message("load_script"));
		this.btnLoadBatchScript.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loadBatchScript();
			}
		});
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
					// TODO Auto-generated method stub
					refresh();
				}
			});
		this.btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		this.btnRefresh.setVisible(bindUri == null);
		toolGrp2.add(this.btnRefresh);

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
		toolGrp2.add(this.btnClear);

		this.btnUpload = UIUtil.makeIconButton(
			"upload-16x16.png", 
			"upload", 
			I18nUtil.message("upload"), 
			I18nUtil.message("upload"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (bindUri == null) return;
					
					boolean ret = true;
					try {
						ret = evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), null);
					} catch (Exception ex) {ex.printStackTrace();}
					
					if (ret) {
						JOptionPane.showMessageDialog(
							getThisGUI(), 
							"Success to upload to server", 
							"Success upload", 
							JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(
							getThisGUI(), 
							"Fail to upload to server", 
							"Fail upload", 
							JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		this.btnUpload.setMargin(new Insets(0, 0 , 0, 0));
		this.btnUpload.setVisible(bindUri != null);
		toolGrp2.add(this.btnUpload);

		this.btnDownload = UIUtil.makeIconButton(
			"download-16x16.png", 
			"download", 
			I18nUtil.message("retrieve_dataset_ref"), 
			I18nUtil.message("retrieve_dataset_ref"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					if (bindUri == null) return;

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
				}
				
			});
		this.btnDownload.setMargin(new Insets(0, 0 , 0, 0));
		this.btnDownload.setVisible(bindUri != null);
		toolGrp2.add(this.btnDownload);

		this.btnSaveBatchScript = UIUtil.makeIconButton(
			"save-16x16.png", 
			"save", 
			I18nUtil.message("save_script"), 
			I18nUtil.message("save_script"), 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					saveBatchScript();
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
					// TODO Auto-generated method stub
					
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
					if (bindUri == null) //Local evaluator.
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
				// TODO Auto-generated method stub
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
								getThisGUI().result = result;
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
				// TODO Auto-generated method stub
				if (result != null) {
					try {
						new MetricsAnalyzeDlg(getThisGUI(), result, new RegisterTable(lbAlgs.getAlgList()), evaluator);
					}
					catch (Exception ex) {
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
		if (otherResult.inAlgSetup) {
			this.statusBar.setTextPane1(I18nUtil.message("setting_up_algorithm") + " '" + DSUtil.shortenVerbalName(otherResult.algName) + "'. " + I18nUtil.message("please_wait") + "...");
		}
		else if (otherResult.progressTotal > 0) {
			this.statusBar.setTextPane1(
					I18nUtil.message("algorithm") + " '" + DSUtil.shortenVerbalName(otherResult.algName) + "' " +
					I18nUtil.message("dataset") + " '" + otherResult.datasetId + "': " + 
					otherResult.vCurrentCount + "/" + otherResult.vCurrentTotal);
			
			this.statusBar.setTextPane2(I18nUtil.message("total") + ": " + otherResult.progressStep + "/" + otherResult.progressTotal);
		}
		if (otherResult.elapsedTime > 0) {
			String elapsedTimeText = Counter.formatTime(otherResult.elapsedTime);
			statusBar.getLastPane().setText(elapsedTimeText);
		}
		statusPane.add(this.statusBar, BorderLayout.CENTER);

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
		// TODO Auto-generated method stub
		return lbAlgs.getAlgList();
	}


	@Override
	protected void refresh() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			clearResult();

			evaluator.reloadPool();
		}
		catch (Throwable e) {
			e.printStackTrace();
			updateMode();
		}
	}
	
	
	@Override
	protected void clear() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			lbAlgs.update(algRegTable.getAlgList());
			clearResult();

			if (bindUri == null) {
				try {
					evaluator.updatePool(null, null);
				} catch (Throwable e) {e.printStackTrace();}
			}
			else {
				guiData.pool = new DatasetPool();
				tblDatasetPool.update(guiData.pool);
				updateMode();
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
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
			if (bindUri == null)
				started = evaluator.remoteStart0(lbAlgs.getAlgList(), toDatasetPoolExchangedClient(guiData.pool), this.timestamp = new Timestamp());
			else
				started = evaluator.remoteStart(lbAlgs.getAlgNameList(), toDatasetPoolExchangedClient(guiData.pool), null);
			if (!started) updateMode();
		}
		catch (Throwable e) {
			e.printStackTrace();
			LogUtil.error("Error in evaluation");
			updateMode(); //Added date: 2019.08.12 by Loc Nguyen
		}
	}

	
	@Override
	public synchronized void receivedEvaluator(EvaluatorEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		if (evt.getType() == EvaluatorEvent.Type.start || evt.getType() == EvaluatorEvent.Type.update_pool) {
//			Timestamp timestamp = evt.getTimestamp();
			
			if (evt.getType() == EvaluatorEvent.Type.start) {
//				if (timestamp == null || this.timestamp == null || bindUri != null || !this.timestamp.equals(timestamp)) {
					List<String> algNames = evt.getOtherResult().algNames;
					if (algNames != null && algNames.size() > 0) {
						updateAlgRegFromRemoteEvaluator(algNames);
						lbAlgs.update(algRegTable.getAlgList(algNames));
					}
					else
						lbAlgs.update(algRegTable.getAlgList());
//				}
			}
			
//			if (timestamp == null || this.timestamp == null || bindUri != null || !this.timestamp.equals(timestamp)) {
				guiData.pool = evt.getPoolResult().toDatasetPoolClient();
				guiData.pool = guiData.pool != null ? guiData.pool : new DatasetPool();
				tblDatasetPool.update(guiData.pool);
//			}
			
			updateMode();
			this.timestamp = null;
		}
		else if (evt.getType() == EvaluatorEvent.Type.pause ||
				evt.getType() == EvaluatorEvent.Type.resume || 
				evt.getType() == EvaluatorEvent.Type.stop) {
			updateMode();
		}
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
			} catch (Throwable e) {e.printStackTrace();}
			
			evProcessor.saveEvaluateResult(txtRunSaveBrowse.getText(), evt, lbAlgs.getAlgList(), saveResultSummary, EV_RESULT_FILENAME_PREFIX);
		}
		
		
		//this.result = evaluator.getResult();
		this.result = evt.getMetrics(); //Fix bug date: 2019.09.04 by Loc Nguyen.
		if (evt.getType() == Type.done) {
			updateMode();
		}
	}

	
	@Override
	public synchronized void receivedProgress(EvaluateProgressEvent evt) throws RemoteException {
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
	public synchronized void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		String algName = evt.getAlgName();
		if (algName == null) return;

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
			boolean saveResultSummary = false;
			try {
				saveResultSummary = evaluator.getConfig().isSaveResultSummary();
			} catch (Throwable e) {e.printStackTrace();}

			evProcessor.saveSetupResult(txtRunSaveBrowse.getText(), evt, saveResultSummary, EV_RESULT_FILENAME_PREFIX);
		}
	}
	
	
	@Override
	public void receivedElapsedTime(CounterElapsedTimeEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
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
				
				prgRunning.setMaximum(0);
				prgRunning.setValue(0);
				prgRunning.setVisible(false);
			}
			else if (guiData.pool.size() == 0) {
				setInternalEnable(false);
				setResultVisible(result != null && result.size() > 0);
				
				lbAlgs.setEnabled(true);
				btnConfigAlgs.setEnabled(true);
				btnAddDataset.setEnabled(true);
				btnLoadBatchScript.setEnabled(true);
				btnUpload.setEnabled(guiData.pool.size() > 0);
				btnDownload.setEnabled(true);
				
				prgRunning.setMaximum(0);
				prgRunning.setValue(0);
				prgRunning.setVisible(false);
			}
			else if (evaluator.remoteIsStarted()) {
				if (evaluator.remoteIsRunning()) {
					setInternalEnable(false);
					setResultVisible(false);
					
					result = null;
					tblMetrics.clear();
					txtRunInfo.setText("");
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
			
			boolean flag = lbAlgs.getAlgList().size() > 0 && guiData.pool.size() > 0;
			setInternalEnable(flag);
			setResultVisible(flag);
			
			btnConfigAlgs.setEnabled(algRegTable.size() > 0);
			btnAddDataset.setEnabled(true);
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
		this.tblDatasetPool.setEnabled2(flag && guiData.pool.size() > 0);
		this.btnAddDataset.setEnabled(flag);
		this.btnLoadBatchScript.setEnabled(flag);
		this.btnSaveBatchScript.setEnabled(flag && guiData.pool.size() > 0);
		
		this.btnRefresh.setEnabled(flag && guiData.pool.size() > 0);
		this.btnClear.setEnabled(flag && guiData.pool.size() > 0);
		this.btnUpload.setEnabled(flag && guiData.pool.size() > 0);
		this.btnDownload.setEnabled(flag);

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
	 */
	protected void loadBatchScript() {
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
			BatchScript script = BatchScript.parse(reader, evaluator.getMainUnit());
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

			List<Alg> batchAlgList = algRegTable.getAlgList(script.getAlgNameList());
			if (batchAlgList.size() == 0) {
				JOptionPane.showMessageDialog(
						this, 
						"Algorithms in batch script are not suitable to this evaluator",
						"Batch script has unsuitable algorithms", 
						JOptionPane.WARNING_MESSAGE);
			}
			else
				this.lbAlgs.update(batchAlgList);
			
			guiData.pool.clear();
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
			if (bindUri == null) {
				try {
					evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), null);
				} catch (Throwable e) {e.printStackTrace();}
				
			}
			else {
				tblDatasetPool.update(guiData.pool);
				updateMode();
			}
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			updateMode();
		}
			
	}
	
	
	/**
	 * Saving batch script.
	 */
	protected void saveBatchScript() {
		if (guiData.pool.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Pool empty",
					"Pool empty", 
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
					guiData.pool, lbAlgs.getAlgNameList(), evaluator.getMainUnit());
			
			script.save(writer);
    		writer.flush();
    		writer.close();
    		writer = null;
	        
        	JOptionPane.showMessageDialog(this, 
        			"URI saved successfully", "URI saved successfully", JOptionPane.INFORMATION_MESSAGE);
        }
		catch(Exception e) {
			e.printStackTrace();
		}
        finally {
        	try {
        		if (writer != null)
        			writer.close();
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        	if (adapter != null)
        		adapter.close();
        }
	}
	
	
	/**
	 * Adding dataset.
	 * @param nullTesting true if add null testing dataset {@link NullPointer}.
	 */
	protected void addDataset(boolean nullTesting) {
		try {
			if (evaluator.remoteIsStarted() || this.lbAlgs.getAlgList().size() == 0)
				return;
			
			clearResult();

			if (nullTesting)
				new AddingTrainingDatasetNullTestingDatasetDlg(this, guiData.pool, this.lbAlgs.getAlgList(), evaluator.getMainUnit(), bindUri).setVisible(true);
			else
				new AddingDatasetDlg(this, guiData.pool, this.lbAlgs.getAlgList(), evaluator.getMainUnit(), bindUri).setVisible(true);
			
			if (bindUri == null) {
				try {
					evaluator.updatePool(toDatasetPoolExchangedClient(guiData.pool), null);
				} catch (Throwable e) {e.printStackTrace();}
			}
			else {
				this.tblDatasetPool.update(guiData.pool);
				updateMode();
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			updateMode();
		}
	}


	/**
	 * Clearing result information.
	 */
	private void clearResult() {
		try {
			this.txtRunInfo.setText("");
			this.result = null;
			this.tblMetrics.clear();
			this.statusBar.getLastPane().setText(""); //Clearing elapsed time information.
			
			this.timestamp = null;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
	@Override
	protected void updateGUIData() {
		// TODO Auto-generated method stub
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
	private void updateGUI() {
		try {
//			validate(); //This code line can be removed.
//			updateUI();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}
