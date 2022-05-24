/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.text.NumberFormatter;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.Connector;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Service;
import net.hudup.core.data.BooleanWrapper;
import net.hudup.core.data.ui.SysConfigDlgExt;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.ui.EvaluateGUIData;
import net.hudup.core.evaluate.ui.EvaluatorWrapper;
import net.hudup.core.evaluate.ui.MetricsAnalyzeDlg;
import net.hudup.core.evaluate.ui.MetricsTable;
import net.hudup.core.logistic.Account;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.SnapshotParserImpl;

/**
 * This class represents the entire frame allowing users to interact fully with evaluator.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class EvalCompoundGUI extends JFrame {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Evaluation configuration.
	 */
	private EvaluatorConfig thisConfig = null;
	
	
	/**
	 * Batch mode evaluator GUI.
	 */
	private BatchEvaluateGUI batchEvaluateGUI = null;
	
	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified evaluator.
	 */
	public EvalCompoundGUI(Evaluator evaluator) {
		this(evaluator, null, null);
	}

	
	/**
	 * Constructor with specified evaluator and connection information.
	 * @param evaluator specified evaluator.
	 * @param connectInfo connection information.
	 */
	public EvalCompoundGUI(Evaluator evaluator, ConnectInfo connectInfo) {
		this(evaluator, connectInfo, null);
	}
	
	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified evaluator.
	 * @param connectInfo connection information.
	 * @param referredData evaluator GUI data.
	 */
	public EvalCompoundGUI(Evaluator evaluator, ConnectInfo connectInfo, final EvaluateGUIData referredData) {
		super("Evaluator GUI");
		connectInfo = connectInfo != null ? connectInfo : new ConnectInfo();

		try {
			thisConfig = evaluator.getConfig();
			thisConfig.setSaveAbility(connectInfo.bindUri == null && !thisConfig.isReproduced()); //Save only local configuration.
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Error in getting evaluator configuration");
		}
		batchEvaluateGUI = new BatchEvaluateGUI(evaluator, connectInfo, referredData);
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
				handleClose();
			}
		});
		
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null) setIconImage(image);
		
		setSize(800, 600);
		setLocationRelativeTo(null);
	    setJMenuBar(createMenuBar());
	    
		setLayout(new BorderLayout());
		
//		add(createToolbar(), BorderLayout.NORTH);
		
		add(batchEvaluateGUI, BorderLayout.CENTER);
		
		try {
			if (batchEvaluateGUI.getConnectInfo().namingUri != null) {
				int port = batchEvaluateGUI.getPort();
				setTitle(I18nUtil.message("evaluator") + " (" + port + ")");
			}
			else
				setTitle(I18nUtil.message("evaluator"));
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Error in getting evaluator port");
		}
		
		setVisible(true);
	}
	
	
	/**
	 * Getting this GUI.
	 * @return this GUI.
	 */
	private EvalCompoundGUI getThisEvalGUI() {
		return this;
	}
	
	
	/**
	 * Creating main menu bar.
	 * @return main menu bar.
	 */
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnFile = new JMenu(I18nUtil.message("file"));
		mnFile.setMnemonic('f');
		mnBar.add(mnFile);

		JMenuItem mniSaveScript = new JMenuItem(
			new AbstractAction(I18nUtil.message("save_script")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					batchEvaluateGUI.saveBatchScript(false);
				}
			});
		mniSaveScript.setMnemonic('s');
		mniSaveScript.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		mnFile.add(mniSaveScript);

		PowerServer server = EvaluatorAbstract.getServerByPluginChangedListenersPath(batchEvaluateGUI.getEvaluator());;
		if (server != null) {
			mnFile.addSeparator();
			JMenu mnDatasetPool = new JMenu(I18nUtil.message("dataset_pool"));
			mnFile.add(mnDatasetPool);
			
			JMenuItem mniDatasetPoolAttach = new JMenuItem(
				new AbstractAction("Attach") {
					
					/**
					 * Serial version UID for serializable class. 
					 */
					private static final long serialVersionUID = 1L;
	
					@Override
					public void actionPerformed(ActionEvent e) {
						batchEvaluateGUI.attachRefDatasetPool();
					}
				});
			mniDatasetPoolAttach.setMnemonic('a');
			mnDatasetPool.add(mniDatasetPoolAttach);
	
			JMenuItem mniDatasetPoolDetach = new JMenuItem(
				new AbstractAction("Detach") {
					
					/**
					 * Serial version UID for serializable class. 
					 */
					private static final long serialVersionUID = 1L;
	
					@Override
					public void actionPerformed(ActionEvent e) {
						batchEvaluateGUI.detachRefDatasetPool();
					}
				});
			mniDatasetPoolDetach.setMnemonic('d');
			mnDatasetPool.add(mniDatasetPoolDetach);
			
			JMenuItem mniDatasetPoolPut = new JMenuItem(
				new AbstractAction("Put") {
					
					/**
					 * Serial version UID for serializable class. 
					 */
					private static final long serialVersionUID = 1L;
	
					@Override
					public void actionPerformed(ActionEvent e) {
						batchEvaluateGUI.putRefDatasetPool();
					}
				});
			mniDatasetPoolPut.setMnemonic('p');
			mnDatasetPool.add(mniDatasetPoolPut);
		}

		boolean agent = false;
		Service service = null;
		try {
			agent = batchEvaluateGUI.getEvaluator().isAgent();
			service = batchEvaluateGUI.getEvaluator().getReferredService();
		}
		catch (Throwable e) {LogUtil.trace(e);}
		if (!agent && service == null) {
			mnFile.addSeparator();

			JMenuItem mniSwitchLocalEvaluator = new JMenuItem(
				new AbstractAction(I18nUtil.message("switch_local_evaluator")) {
					
					/**
					 * Serial version UID for serializable class. 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						switchEvaluator(false);
					}
				});
			mniSwitchLocalEvaluator.setMnemonic('l');
			mnFile.add(mniSwitchLocalEvaluator);

			JMenuItem mniSwitchRemoteEvaluator = new JMenuItem(
				new AbstractAction(I18nUtil.message("switch_remote_evaluator")) {
					
					/**
					 * Serial version UID for serializable class. 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						switchEvaluator(true);
					}
				});
			mniSwitchRemoteEvaluator.setMnemonic('m');
			mnFile.add(mniSwitchRemoteEvaluator);

			if (batchEvaluateGUI.getConnectInfo().bindUri == null)
				mniSwitchLocalEvaluator.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
			else
				mniSwitchRemoteEvaluator.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		}

		mnFile.addSeparator();
		JMenuItem mniClose = new JMenuItem(
			new AbstractAction(I18nUtil.message("close")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					getThisEvalGUI().handleClose();
				}
			});
		mniClose.setMnemonic('c');
		mniClose.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.ALT_DOWN_MASK));
		mnFile.add(mniClose);
		
		JMenu mnTools = new JMenu(I18nUtil.message("tool"));
		mnTools.setMnemonic('t');
		mnBar.add(mnTools);
		
		JMenuItem mniSysConfig = new JMenuItem(
			new AbstractAction(I18nUtil.message("system_configure")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					sysConfig();
				}
			});
		mniSysConfig.setMnemonic('s');
		mnTools.add(mniSysConfig);

		if (batchEvaluateGUI.getConnectInfo().bindUri != null) {
			JMenuItem mniUpdateFromServer = new JMenuItem(
				new AbstractAction(I18nUtil.message("update_system_from_server")) {
					
					/**
					 * Serial version UID for serializable class. 
					 */
					private static final long serialVersionUID = 1L;
	
					@Override
					public void actionPerformed(ActionEvent e) {
						updateFromServer();
					}
				});
			mniUpdateFromServer.setMnemonic('u');
			mniUpdateFromServer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, InputEvent.CTRL_DOWN_MASK));
			mnTools.add(mniUpdateFromServer);
		}

		mnTools.addSeparator();
		JMenuItem mniRefresh = new JMenuItem(
			new AbstractAction(I18nUtil.message("refresh_evaluate")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					batchEvaluateGUI.refreshEvaluate();
				}
				
			});
		mniRefresh.setMnemonic('f');
		mniRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnTools.add(mniRefresh);

		JMenuItem mniRetrieveResult = new JMenuItem(
			new AbstractAction(I18nUtil.message("retrieve_evaluate_result")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					MetricsTable.showDlg(getThisEvalGUI(),
						batchEvaluateGUI.getResult(),
						batchEvaluateGUI.getAlgRegTable(),
						batchEvaluateGUI.getEvaluator());
				}
			});
		mniRetrieveResult.setMnemonic('r');
		mniRetrieveResult.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, InputEvent.CTRL_DOWN_MASK));
		mnTools.add(mniRetrieveResult);

		JMenuItem mniRecoverResult = new JMenuItem(
			new AbstractAction(I18nUtil.message("recover_evaluate_result")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					Metrics recoveredResult = batchEvaluateGUI.getRecoveredResult();
					if (recoveredResult == null) {
						JOptionPane.showMessageDialog(
							getThisEvalGUI(), 
							"Evaluated result is empty", "Empty evaluated result", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					if (recoveredResult == batchEvaluateGUI.getResult()) {
						JOptionPane.showMessageDialog(
							getThisEvalGUI(), 
							"Evaluated result is not lost and so\n it is not necessary to recover it.", 
							"Evaluated result is not lost", JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					try {
						new MetricsAnalyzeDlg(
							getThisEvalGUI(), recoveredResult,
							batchEvaluateGUI.getAlgRegTable(), batchEvaluateGUI.getEvaluator());
					}
					catch (Exception ex) {
						LogUtil.trace(ex);
						JOptionPane.showMessageDialog(
							getThisEvalGUI(), 
							"Cannot recover evaluated result", "Cannot recover evaluated result", JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		mniRecoverResult.setMnemonic('c');
		mnTools.add(mniRecoverResult);

		
		JMenu mnHelp = new JMenu(I18nUtil.message("help"));
		mnHelp.setMnemonic('h');
		mnBar.add(mnHelp);
		
		JMenuItem mniHelpContent = new JMenuItem(
			new AbstractAction(I18nUtil.message("help_content")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					new HelpContent(getThisEvalGUI());
				}
			});
		mniHelpContent.setMnemonic('c');
		mniHelpContent.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));
		mnHelp.add(mniHelpContent);

		return mnBar;
	}

	
	/**
	 * Creating tool bar.
	 * @return tool bar.
	 */
	@SuppressWarnings("unused")
	private JToolBar createToolbar() {
		JToolBar toolbar = new JToolBar();

		JButton btnRefreshResult = UIUtil.makeIconButton(
			"refresh2-16x16.png", 
			"refresh_result", 
			I18nUtil.message("refresh"), 
			I18nUtil.message("refresh"), 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					batchEvaluateGUI.refreshEvaluate();
				}
			});
		btnRefreshResult.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(btnRefreshResult);

		return toolbar;
	}
	
	
	/**
	 * Show an dialog allowing users to see and modify the configuration of system.
	 */
	private void sysConfig() {
		boolean isIdle = false;
		try {
			isIdle = batchEvaluateGUI.isIdle();
		} catch (Exception e) {LogUtil.trace(e);}
		
		if (!isIdle) {
			JOptionPane.showMessageDialog(
					getThisEvalGUI(), 
					"Evaluator is running.\n It is impossible to configure system", 
					"Evaluator is running", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		PluginChangedListener pluginChangedListener = batchEvaluateGUI.getEvaluator();
		if (batchEvaluateGUI.getConnectInfo().bindUri == null)
			pluginChangedListener = EvaluatorAbstract.getTopMostPluginChangedListener((Evaluator)pluginChangedListener, true);
		
		SysConfigDlgExt cfg = new SysConfigDlgExt(this, I18nUtil.message("system_configure"), pluginChangedListener, batchEvaluateGUI.getConnectInfo()) {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onApply() {
				if (!isModified()) return;
				
				if (paneSysConfig != null && paneSysConfig.isModified()) {
					paneSysConfig.apply();
					
					try {
						if (batchEvaluateGUI.getConnectInfo().bindUri != null)
							batchEvaluateGUI.getEvaluator().setConfig(thisConfig);
					} catch (Throwable e) {LogUtil.trace(e);}
				}
				
				if (paneRegister != null && paneRegister.isModified())
					paneRegister.apply();
			}
			
		};
		
		cfg.update(thisConfig);
		
		cfg.setVisible(true);
	}


	/**
	 * Updating the GUI from server.
	 */
	private void updateFromServer() {
		boolean isIdle = false;
		try {
			isIdle = batchEvaluateGUI.isIdle();
		} catch (Exception e) {LogUtil.trace(e);}
		
		if (!isIdle) {
			JOptionPane.showMessageDialog(
				this, 
				"Evaluator is running.\n It is impossible to update system from server", 
				"Evaluator is running", 
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		if (batchEvaluateGUI.getConnectInfo().bindUri == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Evaluator is local.\n It is impossible to update from server", 
				"Evaluator is local", 
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		batchEvaluateGUI.updateFromServer();
		JOptionPane.showMessageDialog(
				this, 
				"Update from server successfully", 
				"Update from server successfully", 
				JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	/**
	 * Handling closing windows
	 */
	private void handleClose() {
		if (batchEvaluateGUI.getConnectInfo().bindUri != null) {
			dispose();
			return;
		}

		try {
			if (batchEvaluateGUI.getEvaluator().getReferredService() != null) {
				dispose();
				return;
			}
		}
		catch (Throwable e) {
			dispose();
			LogUtil.trace(e);
		}
		
		boolean isIdle = false;
		try {
			isIdle = batchEvaluateGUI.isIdle();
		} 
		catch (Exception e) {
			dispose();
			LogUtil.trace(e);
		}

		if (!isIdle) {
			int confirm = JOptionPane.showConfirmDialog(
				UIUtil.getDialogForComponent(getThisEvalGUI()), 
				"Evaluation task will be terminated if closing window.\n" +
					"Are you sure to close window?", 
				"Close window", 
				JOptionPane.YES_NO_OPTION, 
				JOptionPane.QUESTION_MESSAGE);
				
			if (confirm == JOptionPane.YES_OPTION)
				dispose();
		}
		else
			dispose();
	}

	
	@Override
	public void dispose() {
		try {
			boolean agent = false;
			try {
				agent = batchEvaluateGUI.getEvaluator().isAgent();
			} 
			catch (Exception e) {LogUtil.trace(e);}
	
			batchEvaluateGUI.dispose();
			if (!agent || batchEvaluateGUI.getConnectInfo().bindUri != null)
				PluginStorage.clear();
			
			super.dispose();
		}
		catch (Throwable e) {
			if (batchEvaluateGUI.getConnectInfo().bindUri != null)
				System.exit(0);
		}
	}


	/**
	 * Switch evaluator.
	 * @param remote remote flag.
	 */
	private void switchEvaluator(boolean remote) {
		boolean isIdle = false;
		try {
			isIdle = batchEvaluateGUI.isIdle();
		} catch (Exception e) {LogUtil.trace(e);}

		if (!isIdle) {
			int confirm = JOptionPane.showConfirmDialog(
					UIUtil.getDialogForComponent(getThisEvalGUI()), 
					"Evaluation task will be terminated if switching evaluator.\n" +
						"Are you sure to switch evaluator?", 
					"Switching evaluator", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
				
			if (confirm != JOptionPane.YES_OPTION)
				return;
		}
	
		try {
			if (!remote)
				switchEvaluator(batchEvaluateGUI.getEvaluator().getName(), this);
			else
				switchRemoteEvaluator(batchEvaluateGUI.getEvaluator().getName(), this);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}

	
	/**
	 * Switching evaluator.
	 * @param selectedEvName selected evaluator name.
	 * @param oldGUI old GUI.
	 */
	@SuppressWarnings("unused")
	@Deprecated
	private static void switchEvaluator0(String selectedEvName, Window oldGUI) {
		List<Evaluator> evList = Util.getPluginManager().loadInstances(Evaluator.class);
		if (evList.size() == 0) {
			JOptionPane.showMessageDialog(
					null, 
					"There is no evaluator", 
					"There is no evaluator", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Collections.sort(evList, new Comparator<Evaluator>() {

			@Override
			public int compare(Evaluator o1, Evaluator o2) {
				try {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
				catch (Throwable e) {
					LogUtil.trace(e);
					return -1;
				}
			}
		});
		
		Evaluator initialEv = null;
		if (selectedEvName != null) {
			for (Evaluator ev : evList) {
				try {
					if (ev.getName().equals(selectedEvName)) {
						initialEv = ev;
						break;
					}
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		
		StartDlg dlgEvStarter = new StartDlg((JFrame)null, "List of evaluators") {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			/**
			 * Flag to indicate whether evaluator is started.
			 */
			private boolean started = false;
			
			@Override
			protected void start() {
				final Evaluator evaluator = (Evaluator) getItemControl().getSelectedItem();
				dispose();
				if (oldGUI != null)
					oldGUI.dispose();

				for (Evaluator ev : evList) {
					if (ev != evaluator) {
						try {
							ev.getConfig().setSaveAbility(false);
							ev.close();
						} catch (Exception e1) {LogUtil.trace(e1);}
					}
				}
				started = true;
				
				try {
					evaluator.stimulate();
				} catch (Exception e) {LogUtil.trace(e);}
				run(evaluator, null, null, null);
			}
			
			@Override
			protected JComboBox<?> createItemControl() {
				return new JComboBox<Evaluator>(evList.toArray(new Evaluator[0]));
			}
			
			@Override
			protected TextArea createHelp() {
				TextArea toolkit = new TextArea("Thank you for choosing evaluators");
				toolkit.setEditable(false);
				return toolkit;
			}

			@Override
			public void dispose() {
				super.dispose();
				
				if (started) return;
				
				for (Evaluator ev : evList) {
					try {
						ev.getConfig().setSaveAbility(false);
						ev.close();
					} catch (Exception e1) {LogUtil.trace(e1);}
				}
			}
			
		};
		
		if (initialEv != null)
			dlgEvStarter.getItemControl().setSelectedItem(initialEv);
		dlgEvStarter.setSize(400, 150);
        dlgEvStarter.setVisible(true);
	}
	
	
	/**
	 * Switching evaluator.
	 * @param selectedEvName selected evaluator name.
	 * @param oldGUI old GUI.
	 */
	public static void switchEvaluator(String selectedEvName, Window oldGUI) {
		List<Evaluator> evList = Util.getPluginManager().loadInstances(Evaluator.class);
		if (evList.size() == 0) {
			JOptionPane.showMessageDialog(
					null, 
					"There is no evaluator", 
					"There is no evaluator", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Collections.sort(evList, new Comparator<Evaluator>() {

			@Override
			public int compare(Evaluator o1, Evaluator o2) {
				try {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
				catch (Throwable e) {
					LogUtil.trace(e);
					return -1;
				}
			}
		});
		
		Evaluator initialEv = null;
		if (selectedEvName != null) {
			for (Evaluator ev : evList) {
				try {
					if (ev.getName().equals(selectedEvName)) {
						initialEv = ev;
						break;
					}
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		
		final BooleanWrapper started = new BooleanWrapper(false);
		JDialog dlgEvStarter = new JDialog((JFrame)null, "Start evaluator", true) {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void dispose() {
				super.dispose();
				if (started.get()) return;
				
				for (Evaluator ev : evList) {
					try {
						ev.getConfig().setSaveAbility(false);
						ev.close();
					} catch (Exception e1) {LogUtil.trace(e1);}
				}
			}
		}; 
		
		dlgEvStarter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dlgEvStarter.setSize(400, 320);
		dlgEvStarter.setLocationRelativeTo(null);
		dlgEvStarter.setLayout(new BorderLayout());
		
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null)
        	dlgEvStarter.setIconImage(image);
        
        JPanel header = new JPanel(new BorderLayout());
        dlgEvStarter.add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		left.add(new JLabel("Evaluator:"));
		left.add(new JLabel("Hosting:"));
		left.add(new JLabel("    Naming port:"));
		left.add(new JLabel("    Naming path:"));
		left.add(new JLabel("Global address:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
        
		final JComboBox<Evaluator> cmbEvs = new JComboBox<>(evList.toArray(new Evaluator[] {}));
		if (initialEv != null)
			cmbEvs.setSelectedItem(initialEv);
		right.add(cmbEvs);
		
		final JCheckBox chkHosting = new JCheckBox("", false);
		right.add(chkHosting);
		
		JPanel paneNamingPort = new JPanel(new BorderLayout());
		right.add(paneNamingPort);
		paneNamingPort.setVisible(false);

		final JFormattedTextField txtNamingPort = new JFormattedTextField(new NumberFormatter());
		paneNamingPort.add(txtNamingPort, BorderLayout.CENTER);
		txtNamingPort.setValue(Constants.DEFAULT_EVALUATOR_PORT);

		JButton btnCheckNamingPort = UIUtil.makeIconButton(
			"checking-16x16.png",
			"checking", 
			"Checking whether naming port is available - http://www.iconarchive.com", 
			"Checking", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					int port = txtNamingPort.getValue() instanceof Number ? ( (Number) txtNamingPort.getValue()).intValue() : 0;
					boolean ret = NetUtil.testPort(port);
					if (ret) {
						JOptionPane.showMessageDialog(dlgEvStarter,
								"The port " + port + " is valid",
								"Valid port", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					int suggestedPort = NetUtil.getPort(-1, true);
					JOptionPane.showMessageDialog(dlgEvStarter,
							"The port " + port + " is invalid (used).\n The suggested port is " + suggestedPort,
							"Invalid port", JOptionPane.ERROR_MESSAGE);
				}
			});
		paneNamingPort.add(btnCheckNamingPort, BorderLayout.EAST);

		JPanel paneNamingPath = new JPanel(new BorderLayout());
		right.add(paneNamingPath);
		paneNamingPath.setVisible(chkHosting.isSelected());
		
		final JTextField txtNamingPath = new JTextField("connect1");
		paneNamingPath.add(txtNamingPath, BorderLayout.CENTER);
		
		JButton btnGenNamingPath = UIUtil.makeIconButton(
			"generate-16x16.png",
			"generate", 
			"Generate naming path - http://www.iconarchive.com", 
			"Generate", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					txtNamingPath.setText("connect" + new Date().getTime());
				}
			});
		paneNamingPath.add(btnGenNamingPath, BorderLayout.EAST);

		JPanel paneGlobalAddress = new JPanel(new BorderLayout());
		right.add(paneGlobalAddress);

		JTextField txtGlobalAddress = new JTextField("");
		paneGlobalAddress.add(txtGlobalAddress, BorderLayout.CENTER);

		final JLabel lblPublicIP = new JLabel();
		JButton btnGenGlobalAddress = UIUtil.makeIconButton(
			"generate-16x16.png",
			"generate", 
			"Retrieve internet address as global address - http://www.iconarchive.com", 
			"Generate", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String publicIP = NetUtil.getPublicInetAddress();
					publicIP = publicIP != null ? publicIP.trim() : "";
					txtGlobalAddress.setText(publicIP);
					
					if (!publicIP.isEmpty())
						lblPublicIP.setText("Internet address: " + publicIP);
				}
			});
		paneGlobalAddress.add(btnGenGlobalAddress, BorderLayout.EAST);

		chkHosting.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				paneNamingPort.setVisible(chkHosting.isSelected());
				paneNamingPath.setVisible(chkHosting.isSelected());
			}
		});

		JPanel footer = new JPanel(new BorderLayout());
		dlgEvStarter.add(footer, BorderLayout.SOUTH);
		
		JPanel buttons = new JPanel();
		footer.add(buttons, BorderLayout.NORTH);

		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectInfo connectInfo = new ConnectInfo();
				
				String globalAddress = txtGlobalAddress.getText() != null ? txtGlobalAddress.getText().trim() : ""; 
				if (globalAddress.isEmpty() || globalAddress.compareToIgnoreCase("localhost") == 0 || globalAddress.compareToIgnoreCase("127.0.0.1") == 0)
					globalAddress = null;
				connectInfo.globalAddress = globalAddress;

				if (chkHosting.isSelected()) {
					String namingPath = Connector.normalizeNamingPath(txtNamingPath.getText());
					int namingPort = txtNamingPort.getValue() instanceof Number ? ( (Number) txtNamingPort.getValue()).intValue() : 0;
					namingPort = NetUtil.getPort(namingPort, Constants.TRY_RANDOM_PORT);
					
					String host = connectInfo.globalAddress;
					host = host != null ? host : "localhost";
					connectInfo.namingUri = xURI.create("rmi://" + host + ":" + namingPort);
						
					if (namingPath != null && !namingPath.isEmpty())
						connectInfo.namingUri = connectInfo.namingUri.concat(namingPath);
				}
				

				Evaluator evaluator = (Evaluator) cmbEvs.getSelectedItem();
				dlgEvStarter.dispose();
				if (oldGUI != null) {
					oldGUI.dispose();
					if (!Util.getPluginManager().isFired())
						Util.getPluginManager().fire();
					else
						Util.getPluginManager().discover();
				}

				for (Evaluator ev : evList) {
					if (ev != evaluator) {
						try {
							ev.getConfig().setSaveAbility(false);
							ev.close();
						} catch (Exception e1) {LogUtil.trace(e1);}
					}
				}
				started.set(true);
				
				try {
					evaluator.stimulate();
				} catch (Exception ex) {LogUtil.trace(ex);}
				run(evaluator, connectInfo, null, null);
			}
		});
		buttons.add(start);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dlgEvStarter.dispose();
			}
		});
		buttons.add(cancel);

		JPanel status = new JPanel(new FlowLayout(FlowLayout.LEFT));
		footer.add(status, BorderLayout.SOUTH);
		
		JLabel lblLocalIP = new JLabel();
		status.add(lblLocalIP);
		if (Constants.hostAddress != null)
			lblLocalIP.setText("Local address: " + Constants.hostAddress + "  ");

		status.add(lblPublicIP);

		dlgEvStarter.setVisible(true);
	}
	
	
	/**
	 * Switching remote evaluator.
	 * @param selectedEvName selected evaluator name.
	 * @param oldGUI old GUI.
	 */
	public static void switchRemoteEvaluator(String selectedEvName, Window oldGUI) {
		final Connector connectDlg = Connector.connect();
		Service service = connectDlg.getService();
		ConnectInfo connectInfo = connectDlg.getConnectInfo();
		boolean pullMode = connectInfo != null ? connectInfo.pullMode : false;

		if (service == null) {
			Evaluator ev = connectDlg.getEvaluator();
			if (ev == null) {
				JOptionPane.showMessageDialog(
					null, "Can't retrieve evaluator", "Retrieval to evaluator failed", JOptionPane.ERROR_MESSAGE);
			}
			else {
				if (EvaluatorAbstract.isPullModeRequired(ev) && !pullMode) {
					JOptionPane.showMessageDialog(null,
						"Can't retrieve evaluator because PULL MODE is not set\n" +
						"whereas the remote evaluator requires PULL MODE.\n" +
						"You have to check PULL MODE in connection dialog.",
						"Retrieval to evaluator failed", JOptionPane.ERROR_MESSAGE);
				}
				else {
					if (oldGUI != null) {
						oldGUI.dispose();
						if (!Util.getPluginManager().isFired())
							Util.getPluginManager().fire();
						else
							Util.getPluginManager().discover();
					}
					run(ev, connectInfo, null, null);
				}
			}
			
			return;
		}
		
		
		try {
			String[] evNames = service.getEvaluatorNames();
			if (evNames == null || evNames.length == 0) {
				JOptionPane.showMessageDialog(
						null, "No remote evaluator", "No remote evaluator", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			String initialEvName = null;
			if (selectedEvName != null) {
				for (String evName : evNames) {
					if (evName.equals(selectedEvName)) {
						initialEvName = evName;
						break;
					}
				}
			}
			
			final Service finalService = service;
			final StartDlg dlgEvStarter = new StartDlg((JFrame)null, "List of evaluators") {
				
				/**
				 * Serial version UID for serializable class.
				 */
				private static final long serialVersionUID = 1L;

				/**
				 * Internal service.
				 */
				protected Service service = finalService;
				
				@Override
				protected void start() {
					String evName = (String) getItemControl().getSelectedItem();
					try {
						Account account = connectInfo.account;
						final Evaluator ev = service.getEvaluator(evName, account.getName(), account.getPassword());
						if (ev == null) {
							JOptionPane.showMessageDialog(
								this, "Can't get remote evaluator", "Connection to evaluator fail", JOptionPane.ERROR_MESSAGE);
							return;
						}
						if (EvaluatorAbstract.isPullModeRequired(ev) && !pullMode) {
							JOptionPane.showMessageDialog(this,
								"Can't retrieve evaluator because PULL MODE is not set\n" +
								"whereas the remote evaluator requires PULL MODE.\n" +
								"You have to check PULL MODE in connection dialog.",
								"Retrieval to evaluator failed", JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						dispose();
						if (oldGUI != null) {
							oldGUI.dispose();
							if (!Util.getPluginManager().isFired())
								Util.getPluginManager().fire();
							else
								Util.getPluginManager().discover();
						}
						
						run(ev, connectInfo, null, null);
					}
					catch (Exception e) {
						LogUtil.trace(e);
						JOptionPane.showMessageDialog(
							this, "Can't get remote evaluator", "Connection to evaluator fail", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				@Override
				protected JComboBox<?> createItemControl() {
					return new JComboBox<String>(evNames);
				}
				
				@Override
				protected TextArea createHelp() {
					TextArea helper = new TextArea("Thank you for choosing evaluators");
					helper.setEditable(false);
					return helper;
				}

				@Override
				public void dispose() {
					super.dispose();
					
					if (service != null)
						Connector.disconnect(service);
					service = null;
				}
				
			};
			
			if (initialEvName != null)
				dlgEvStarter.getItemControl().setSelectedItem(initialEvName);
			dlgEvStarter.setSize(400, 150);
			dlgEvStarter.setLocationRelativeTo(null);
	        dlgEvStarter.setVisible(true);
		}
		catch (Exception e) {LogUtil.trace(e);}
	}

	
	/**
	 * Staring the particular evaluator selected by user.
	 * @param evaluator particular evaluator selected by user.
	 * @param connectInfo connection information.
	 * @param referredData evaluator GUI data.
	 * @param oldGUI old GUI.
	 */
	public static void run(Evaluator evaluator, ConnectInfo connectInfo, EvaluateGUIData referredData, Window oldGUI) {
		if (oldGUI != null) oldGUI.dispose();

		if (!Util.getPluginManager().isFired())
			Util.getPluginManager().fire();
			
		try {
			RegisterTable parserReg = PluginStorage.getParserReg();
			if (parserReg.size() == 0) {
				parserReg.register(new SnapshotParserImpl());
			}
			
			RegisterTable metricReg = PluginStorage.getMetricReg();
			NoneWrapperMetricList metricList = evaluator.defaultMetrics();
			for (int i = 0; i < metricList.size(); i++) {
				Metric metric = metricList.get(i);
				if(!(metric instanceof MetaMetric))
					continue;
				metricReg.unregister(metric.getName());
				
				boolean registered = metricReg.register(metric);
				if (registered)
					LogUtil.info("Registered algorithm: " + metricList.get(i).getName());
			}

			new EvalCompoundGUI(evaluator, connectInfo, referredData);
		}
		catch (Exception e) {LogUtil.trace(e);}
	}


	/**
	 * Open evaluator.
	 * @param evaluatorItem specified evaluator item.
	 * @param connectInfo connection information.
	 * @param parent parent component.
	 */
	public static void run(EvaluatorWrapper evaluatorItem, ConnectInfo connectInfo, Component parent) {
		if (evaluatorItem == null || evaluatorItem.evaluator == null) {
			JOptionPane.showMessageDialog(parent, "Cannot open evaluator", "Cannot open evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (EvaluatorAbstract.isPullModeRequired(evaluatorItem.evaluator) && !connectInfo.pullMode) {
			JOptionPane.showMessageDialog(parent,
				"Can't retrieve evaluator because PULL MODE is not set\n" +
				"whereas the remote evaluator requires PULL MODE.\n" +
				"You have to check PULL MODE in connection dialog.",
				"Retrieval to evaluator failed", JOptionPane.ERROR_MESSAGE);
			return;
		}

		if (evaluatorItem.guiData == null) evaluatorItem.guiData = new EvaluateGUIData();
		
		if (evaluatorItem.guiData.active) {
			JOptionPane.showMessageDialog(
				parent, 
				"GUI of evaluator named '" + DSUtil.shortenVerbalName(evaluatorItem.getName()) + "' is running.", 
				"Evaluator GUI running", 
				JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		try {
			EvalCompoundGUI.class.getClass();
		}
		catch (Exception ex) {
			JOptionPane.showMessageDialog(
					parent, 
					"Cannot open evaluator control panel due to lack of evaluate package", 
					"lack of evaluate package", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}

		run(evaluatorItem.evaluator, connectInfo, evaluatorItem.guiData, null);
	}


}


