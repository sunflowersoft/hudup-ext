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
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.ui.AlgListBox;
import net.hudup.core.alg.ui.AlgListBox.AlgListChangedEvent;
import net.hudup.core.alg.ui.AlgListChooser;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.NullPointer;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorEvent.Type;
import net.hudup.core.evaluate.EvaluatorProgressEvent;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.SortableSelectableTable;
import net.hudup.core.logistic.ui.SortableSelectableTableModel;
import net.hudup.core.logistic.ui.SortableTable;
import net.hudup.core.logistic.ui.SortableTableModel;
import net.hudup.core.logistic.ui.TextField;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.logistic.ui.WaitPanel;
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
	 * Table of algorithms.
	 */
	protected RegisterTable algRegTable = new RegisterTable();
	
	/**
	 * Dataset pool.
	 */
	protected DatasetPool pool = new DatasetPool();
	
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
	 * Waiting panel which is the alternative of testing result panel.
	 */
	protected WaitPanel paneWait = null; //Added date: 2019.09.03 by Loc Nguyen.

	
	/**
	 * Constructor with specified evaluator and bound URI.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI.
	 */
	public BatchEvaluateGUI(Evaluator evaluator, xURI bindUri) {
		super(evaluator, bindUri);
		init(evaluator, bindUri, null);
	}

	
	/**
	 * Constructor with specified evaluator, bound URI, and GUI data.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI.
	 * @param referredData GUI parameter.
	 */
	public BatchEvaluateGUI(Evaluator evaluator, xURI bindUri, EvaluateGUIData referredData) {
		super(evaluator, bindUri);
		// TODO Auto-generated constructor stub
		init(evaluator, bindUri, referredData);
	}

	
	/**
	 * Initializing the evaluator batch GUI with specified evaluator, bound URI, and GUI data.
	 * @param evaluator specified evaluator.
	 * @param bindUri bound URI.
	 * @param referredData GUI data.
	 */
	private void init(Evaluator evaluator, xURI bindUri, EvaluateGUIData referredData) {
		if (referredData == null || !referredData.wasGUIRun) {
			RegisterTable algRegTable = null;
			try {
				algRegTable = EvaluatorAbstract.extractAlgFromPluginStorage(evaluator);
			}
			catch (Throwable e) {e.printStackTrace();}
			if (algRegTable == null) algRegTable = new RegisterTable();
			
			this.algRegTable.register(algRegTable.getAlgList()); //Algorithms are not cloned because of saving memory when evaluator GUI keep algorithms for a long time. 
			
			setLayout(new BorderLayout(2, 2));
			
			JPanel header = createHeader();
			add(header, BorderLayout.NORTH);
			
			JPanel body = createBody();
			add(body, BorderLayout.CENTER);
			
			JPanel footer = createFooter();
			add(footer, BorderLayout.SOUTH);
			
			setVerbal(false);
		}
		else {
			this.result = referredData.result;
			
			this.algRegTable = referredData.algRegTable;
			
			this.pool = referredData.pool;
			
			setLayout(new BorderLayout(2, 2));
			
			JPanel header = createHeader();
			add(header, BorderLayout.NORTH);
			this.tblDatasetPool.update(referredData.pool);
			this.lbAlgs.update(referredData.lbAlgs);
					
			JPanel body = createBody();
			add(body, BorderLayout.CENTER);
			this.txtRunSaveBrowse.setText(referredData.txtRunSaveBrowse);
			this.chkRunSave.setSelected(referredData.chkRunSave);
			this.prgRunning.setValue(referredData.prgRunning[0]);
			this.prgRunning.setMaximum(referredData.prgRunning[1]);
			
			JPanel footer = createFooter();
			add(footer, BorderLayout.SOUTH);
			this.tblMetrics.update(referredData.result);
			this.statusBar.setTexts(referredData.statusBar);
			this.paneWait.setWaitText(referredData.paneWait);
			
			setVerbal(referredData.chkVerbal);

			updateMode();
			updateGUI();
		}
		
		if (referredData != null) {
			referredData.wasGUIRun = true;
			referredData.active = true;
			this.referredData = referredData;
		}
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
				
		    	final int selectedRow = getSelectedIndex();
		    	if (selectedRow == -1) return;
				
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
		this.lbAlgs.update(algRegTable.getAlgList());
		this.lbAlgs.setVisibleRowCount(3);
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
		preferredSize.width = Math.max(preferredSize.width, 200);
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
					
					List<Alg> list = lbAlgs.getAlgList();
					if (list.size() == 0) {
						JOptionPane.showMessageDialog(
								getThisGUI(), 
								"List empty", 
								"List empty", 
								JOptionPane.ERROR_MESSAGE);
						
						return;
						
					}
					
					AlgListChooser dlg = new AlgListChooser(getThisGUI(), algRegTable.getAlgList(), lbAlgs.getAlgList());
					if (!dlg.isOK())
						return;
					
					lbAlgs.update(dlg.getResult());
					updateMode();
					
					updateGUI();
				}
			});
		this.btnConfigAlgs.setMargin(new Insets(0, 0 , 0, 0));
		paneAlgTool.add(this.btnConfigAlgs);
			

		JPanel down = new JPanel(new BorderLayout(2, 2));
		header.add(down, BorderLayout.CENTER);
		
		this.tblDatasetPool = new DatasetPoolTable() {

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
					updateMode();
				}
				
				return ret;
			}
			
			@Override
			public void save() {
				saveBatchScript();
			}

		};
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
		this.txtRunSaveBrowse.setToolTipText(I18nUtil.message("save_evaluate_place"));;
		pane.add(this.txtRunSaveBrowse, BorderLayout.CENTER);
		this.chkRunSave = new JCheckBox(I18nUtil.message("save_evaluate"));
		this.chkRunSave.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(chkRunSave.isSelected()) {
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
						txtRunSaveBrowse.setText(store.toString());
				}
				else {
					txtRunSaveBrowse.setText("");
				}
				updateMode();
			}
			
		});
		this.chkRunSave.setToolTipText(I18nUtil.message("save_evaluate_tooltip"));
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
		this.prgRunning.setMaximum(0);
		this.prgRunning.setValue(0);
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
		
		this.tblMetrics = new MetricsTable(new RegisterTable(lbAlgs.getAlgList()));
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
						new MetricsAnalyzeDlg(getThisGUI(), result, new RegisterTable(lbAlgs.getAlgList()));
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
		this.counterClock.setTimeTextPane(this.statusBar.getLastPane());
		statusPane.add(this.statusBar, BorderLayout.CENTER);

		//Added date: 2019.09.03 by Loc Nguyen.
		this.paneWait = new WaitPanel();
		statusPane.add(this.paneWait, BorderLayout.SOUTH);
		this.paneWait.setVisible(false);


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
			
			this.pool.reload();
	
			tblDatasetPool.update(pool);
			clearResult();
			updateMode();
			
			updateGUI();
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
			
			this.pool.clear();
			this.tblDatasetPool.update(this.pool);
			this.lbAlgs.update(algRegTable.getAlgList());
			
			clearResult();
			
			updateMode();
			updateGUI();
		}
		catch (Throwable e) {
			e.printStackTrace();
			updateMode();
		}
	}
	
	
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		
		if (this.referredData != null) {
			this.referredData.extractFrom(this);
			this.referredData.active = false;
			this.referredData = null;
		}
	}


	@Override
	protected void run() {
		try {
			if (evaluator.remoteIsStarted())
				return;
			
			if (pool.size() == 0 || lbAlgs.getAlgList().size() == 0) {
				JOptionPane.showMessageDialog(
					getThisGUI(), 
					"Not load data set pool", 
					"Not load data set pool", 
					JOptionPane.WARNING_MESSAGE);
				
				return;
			}
			
			clearResult();
			evaluator.remoteStart(lbAlgs.getAlgList(), pool, null);
			
			counterClock.start();
			updateMode();
		}
		catch (Throwable e) {
			e.printStackTrace();
			LogUtil.error("Error in evaluation");
			updateMode(); //Added date: 2019.08.12 by Loc Nguyen
		}
	}

	
	@Override
	public void receivedEvaluation(EvaluatorEvent evt) throws RemoteException {
		
		if (chkVerbal.isSelected()) {
			String info = evt.translate() + "\n\n\n\n";
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkRunSave.isSelected()){
			String storePath = this.txtRunSaveBrowse.getText().trim();
			if (storePath.length() == 0)
				return;
			
			try {
				xURI store = xURI.create(storePath);
				UriAdapter adapter = new UriAdapter(store);
				boolean existed = adapter.exists(store);
				if (!existed)
					adapter.create(store, true);
				adapter.close();
				
				List<Alg> algs = lbAlgs.getAlgList();
				for (Alg alg : algs) {
					if (evt.getType() == Type.done) {
						String key = alg.getName() + EVALUATION_FILE_EXTENSION;
						ByteChannel channel = getIOChannel(store, key, true);
						
						String info = evt.translate(alg.getName(), -1) + "\n\n\n\n";
						ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
						channel.write(buffer);
					}
					else {
						Map<Integer, Metrics> map = evt.getMetrics().gets(alg.getName());
						Set<Integer> datasetIdList = map.keySet();
						for (int datasetId : datasetIdList) {
							String key = alg.getName() + "@" + datasetId + EVALUATION_FILE_EXTENSION;
							ByteChannel channel = getIOChannel(store, key, true);

							String info = evt.translate(alg.getName(), datasetId) + "\n\n\n\n";
							ByteBuffer buffer = ByteBuffer.wrap(info.getBytes());
							channel.write(buffer);
							
							if (evt.getType() == Type.done_one)
								closeIOChannel(key);
						}
					}
				}
				
			    // Exporting excel file
				if (evt.getType() == Type.done || evt.getType() == Type.done_one) {
				    // Exporting excel file
					MetricsUtil util = new MetricsUtil(this.result, new RegisterTable(lbAlgs.getAlgList()));
					util.createExcel(store.concat(METRICS_ANALYZE_EXCEL_FILE_NAME));
					// Begin exporting plain text. It is possible to remove this snippet.
					ByteChannel channel = getIOChannel(store, METRICS_ANALYZE_EXCEL_FILE_NAME2, false);
					ByteBuffer buffer = ByteBuffer.wrap(util.createPlainText().getBytes());
					channel.write(buffer);
					closeIOChannel(METRICS_ANALYZE_EXCEL_FILE_NAME2);
					// End exporting plain text. It is possible to remove this snippet.
					
					if (evt.getType() == Type.done)
						closeIOChannels();
				}
			} 
			catch (Exception e) {
				e.printStackTrace();
			}
			
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
//			this.paneWait.setWaitText(I18nUtil.message("setting_up_algorithm") + " '" + DSUtil.shortenVerbalName(algName) + "'. " + I18nUtil.message("please_wait") + "...");
//			this.paneWait.setVisible(true);
		}
		else if (evt.getType() == SetupAlgEvent.Type.done) {
			this.statusBar.setTextPane1("");
//			this.paneWait.setWaitText(WaitPanel.WAIT_TEXT);
//			this.paneWait.setVisible(false);
		}
		
		
		String info = "========== Algorithm \"" + algName + "\" ==========\n";
		info = info + evt.translate() + "\n\n\n\n";
		
		if (chkVerbal.isSelected()) {
			this.txtRunInfo.insert(info, 0);
			this.txtRunInfo.setCaretPosition(0);
		}
		else if (chkRunSave.isSelected()) {
			String storePath = this.txtRunSaveBrowse.getText().trim();
			if (storePath.length() == 0)
				return;
			
			try {
				xURI store = xURI.create(storePath);
				UriAdapter adapter = new UriAdapter(store);
				boolean existed = adapter.exists(store);
				if (!existed)
					adapter.create(store, true);
				adapter.close();
				
				String key = algName;
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
			
			if (lbAlgs.getAlgList().size() == 0) {
				setInternalEnable(false);
				setResultVisible(false);
				
				btnConfigAlgs.setEnabled(algRegTable.size() > 0);
				
				prgRunning.setMaximum(0);
				prgRunning.setValue(0);
				prgRunning.setVisible(false);
			}
			else if (pool.size() == 0) {
				setInternalEnable(false);
				setResultVisible(false);
				
				lbAlgs.setEnabled(true);
				btnConfigAlgs.setEnabled(true);
				btnAddDataset.setEnabled(true);
				btnLoadBatchScript.setEnabled(true);
				
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
		}
		
		//Added date: 2019.09.03 by Loc Nguyen.
		this.paneWait.setVisible(false);
	}
	
	
	/**
	 * Enable / disable internal controls.
	 * @param flag flag to / disable internal controls.
	 */
	private void setInternalEnable(boolean flag) {
		flag = flag && algRegTable.size() > 0;
		
		this.btnConfigAlgs.setEnabled(flag);
		this.lbAlgs.setEnabled(flag);
		this.tblDatasetPool.setEnabled2(flag && pool.size() > 0);
		this.btnAddDataset.setEnabled(flag);
		this.btnLoadBatchScript.setEnabled(flag);
		this.btnSaveBatchScript.setEnabled(flag && pool.size() > 0);
		
		this.btnRefresh.setEnabled(flag && pool.size() > 0);
		
		this.btnClear.setEnabled(flag && pool.size() > 0);

		this.btnRun.setEnabled(flag && pool.size() > 0);

		this.btnPauseResume.setEnabled(flag && pool.size() > 0);

		this.btnStop.setEnabled(flag && pool.size() > 0);

		this.btnForceStop.setEnabled(flag && pool.size() > 0);

		this.txtRunInfo.setEnabled(flag && pool.size() > 0);
		
		this.chkRunSave.setEnabled(flag && pool.size() > 0);

		this.chkRunSave.setEnabled(flag && pool.size() > 0);

		this.txtRunSaveBrowse.setEnabled(flag && pool.size() > 0);

		this.chkVerbal.setEnabled(flag);
		
		this.btnMetricsOption.setEnabled(flag);
		
		this.btnAnalyzeResult.setEnabled(
			flag && pool.size() > 0 && result != null && result.size() > 0);
		
		this.btnCopyResult.setEnabled(
				flag && pool.size() > 0 && result != null && result.size() > 0);
	}

	
	/**
	 * Making visibility of result controls.
	 * @param flag flag to make visibility of result controls.
	 */
	private void setResultVisible(boolean flag) {
		
		boolean visible = flag && pool.size() > 0 && result != null && result.size() > 0;
 
		paneResult.setVisible(visible);
		btnAnalyzeResult.setEnabled(visible);
		btnCopyResult.setEnabled(visible);
		
		//Added date: 2019.09.03 by Loc Nguyen.
		try {
			//paneWait.setVisible(!visible && evaluator.remoteIsStarted());
		} catch (Exception e) {
			e.printStackTrace();
			paneWait.setVisible(false);
		}
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
			
			this.pool.clear();
			DatasetPool scriptPool = script.getPool();
			Alg[] algList = this.lbAlgs.getAlgList().toArray(new Alg[] {} );
			for (int i = 0; i < scriptPool.size(); i++) {
				DatasetPair pair = scriptPool.get(i);
				if (pair == null || pair.getTraining() == null)
					continue;
				
				Dataset trainingSet = pair.getTraining();
				if (DatasetUtil2.validateTrainingset(this, trainingSet, algList))
					this.pool.add(pair);
			}
			this.tblDatasetPool.update(this.pool);
			clearResult();
			updateMode();
			
			updateGUI();
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
		if (pool.size() == 0) {
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
					pool, lbAlgs.getAlgNameList(), evaluator.getMainUnit());
			
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
			
			if (nullTesting)
				new AddingTrainingDatasetNullTestingDatasetDlg(this, pool, this.lbAlgs.getAlgList(), evaluator.getMainUnit()).setVisible(true);
			else
				new AddingDatasetDlg(this, pool, this.lbAlgs.getAlgList(), evaluator.getMainUnit()).setVisible(true);
			this.tblDatasetPool.update(this.pool);
			
			clearResult();
			updateMode();
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
			this.counterClock.stopAndClearText();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
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
			validate(); //This code line can be removed.
			updateUI();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
	
	
}
