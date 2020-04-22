/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
import javax.swing.text.NumberFormatter;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.client.ConnectDlg;
import net.hudup.core.client.Service;
import net.hudup.core.data.ui.SysConfigDlgExt;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.evaluate.ui.EvaluateGUIData;
import net.hudup.core.evaluate.ui.MetricsAnalyzeDlg;
import net.hudup.core.logistic.Account;
import net.hudup.core.logistic.ConnectInfo;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

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
	 * @param evaluator specified {@link EvaluatorAbstract}.
	 * @param ConnectInfo connection information.
	 */
	public EvalCompoundGUI(Evaluator evaluator, ConnectInfo connectInfo) {
		this(evaluator, connectInfo, null);
	}
	
	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified {@link EvaluatorAbstract}.
	 * @param connectInfo connection information.
	 * @param referredData evaluator GUI data.
	 */
	public EvalCompoundGUI(Evaluator evaluator, ConnectInfo connectInfo, final EvaluateGUIData referredData) {
		super("Evaluator GUI");
		connectInfo = connectInfo != null ? connectInfo : new ConnectInfo();

		try {
			thisConfig = evaluator.getConfig();
			thisConfig.setSaveAbility(connectInfo.bindUri == null); //Save only local configuration.
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Error in getting evaluator configuration");
		}
		batchEvaluateGUI = new BatchEvaluateGUI(evaluator, connectInfo, referredData);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}
		});
		
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null) setIconImage(image);
		
		setSize(800, 600);
		setLocationRelativeTo(null);
	    setJMenuBar(createMenuBar());
	    
		setLayout(new BorderLayout());
		
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
		
		JMenu mnTools = new JMenu(I18nUtil.message("tool"));
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
		mnTools.add(mniSysConfig);

		if (batchEvaluateGUI.getConnectInfo().bindUri != null) {
			JMenuItem mniUpdateFromServer = new JMenuItem(
				new AbstractAction(I18nUtil.message("update_from_server")) {
					
					/**
					 * Serial version UID for serializable class. 
					 */
					private static final long serialVersionUID = 1L;
	
					@Override
					public void actionPerformed(ActionEvent e) {
						updateFromServer();
					}
				});
			mnTools.add(mniUpdateFromServer);
		}

		
		mnTools.addSeparator();
		JMenuItem mniRefreshGUI = new JMenuItem(
			new AbstractAction(I18nUtil.message("refresh_gui")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					batchEvaluateGUI.updateMode();
				}
			});
		mnTools.add(mniRefreshGUI);

		JMenuItem mniRefreshResult = new JMenuItem(
			new AbstractAction(I18nUtil.message("refresh_evaluate_result")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					boolean idle = false;
					try {
						idle = batchEvaluateGUI.isIdle();
					} catch (Exception ex) {LogUtil.trace(ex);}
					
					if (!idle) {
						int confirm = JOptionPane.showConfirmDialog(
							getThisEvalGUI(), 
							"System is not idle or corrupted.\nDo you want to continue to get refreshing?", 
							"Refreshing confirmation",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
						if (confirm != JOptionPane.YES_OPTION)
							return;
					}
					
					Metrics result = null;
					boolean success = true;
					try {
						result = batchEvaluateGUI.getEvaluator().getResult();
					} catch (Exception ex) {LogUtil.trace(ex); success = false;}
					
					if (result != null) {
						batchEvaluateGUI.setResult(result);
						batchEvaluateGUI.updateMode();
					}
					
					if (success) {
						JOptionPane.showMessageDialog(
							getThisEvalGUI(), 
							"Result refreshing successful", 
							"Result refreshing successful", 
							JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(
							getThisEvalGUI(), 
							"Result refreshing failed", 
							"Result refreshing failed", 
							JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		mnTools.add(mniRefreshResult);

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
							"Evaluated result empty", 
							"Evaluated result empty", 
							JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					
					if (recoveredResult == batchEvaluateGUI.getResult()) {
						JOptionPane.showMessageDialog(
							getThisEvalGUI(), 
							"Evaluated result is not lost and so\n it is not necessary to recover it.", 
							"Evaluated result is not lost", 
							JOptionPane.INFORMATION_MESSAGE);
						return;
					}

					try {
						new MetricsAnalyzeDlg(
							getThisEvalGUI(),
							recoveredResult,
							batchEvaluateGUI.getAlgRegTable(),
							batchEvaluateGUI.getEvaluator());
					}
					catch (Exception ex) {
						LogUtil.trace(ex);
						JOptionPane.showMessageDialog(
							getThisEvalGUI(), 
							"Cannot recover evaluated result", 
							"Cannot recover evaluated result", 
							JOptionPane.ERROR_MESSAGE);
					}
				}
			});
		mnTools.add(mniRecoverResult);

		

		boolean agent = false;
		try {
			agent = batchEvaluateGUI.getEvaluator().isAgent();
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		if (batchEvaluateGUI.getConnectInfo().bindUri != null || !agent) {
			mnTools.addSeparator();
			JMenuItem mniSwitchEvaluator = new JMenuItem(
				new AbstractAction(I18nUtil.message("switch_evaluator")) {
					
					/**
					 * Serial version UID for serializable class. 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public void actionPerformed(ActionEvent e) {
						switchEvaluator();
					}
				});
			mnTools.add(mniSwitchEvaluator);
		}
			

		JMenu mnHelp = new JMenu(I18nUtil.message("help"));
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
		mnHelp.add(mniHelpContent);

		
		return mnBar;
	}

	
	/**
	 * Switch evaluator.
	 */
	private void switchEvaluator() {
		boolean isIdle = false;
		try {
			isIdle = batchEvaluateGUI.isIdle();
		} catch (Exception e) {LogUtil.trace(e);}

		if (!isIdle) {
			int confirm = JOptionPane.showConfirmDialog(
					UIUtil.getFrameForComponent(getThisEvalGUI()), 
					"Evaluation task will be terminated if switching evaluator.\n" +
						"Are you sure to switch evaluator?", 
					"Switching evaluator", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
				
			if (confirm != JOptionPane.YES_OPTION)
				return;
		}
	
		try {
			if (batchEvaluateGUI.getConnectInfo().bindUri == null)
				switchEvaluator(batchEvaluateGUI.getEvaluator().getName(), this);
			else
				switchRemoteEvaluator(batchEvaluateGUI.getEvaluator().getName(), this);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
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
		
		SysConfigDlgExt cfg = new SysConfigDlgExt(this, I18nUtil.message("system_configure"), pluginChangedListener, batchEvaluateGUI.getConnectInfo().bindUri) {

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
				"Evaluator is running.\n It is impossible to update from server", 
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
					if (ev.getName().equals(Constants.DEFAULT_EVALUATOR_NAME)) {
						initialEv = ev;
						break;
					}
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		
		final StartDlg dlgEvStarter = new StartDlg((JFrame)null, "List of evaluators") {
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void start() {
				final Evaluator ev = (Evaluator) getItemControl().getSelectedItem();
				dispose();
				if (oldGUI != null)
					oldGUI.dispose();

				run(ev, null, null, null);
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
					if (ev.getName().equals(Constants.DEFAULT_EVALUATOR_NAME)) {
						initialEv = ev;
						break;
					}
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		
		
		final JDialog dlgEvStarter = new JDialog((JFrame)null, "Start evaluator", true); 
		dlgEvStarter.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		dlgEvStarter.setSize(400, 300);
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
		left.add(new JLabel("Export:"));
		left.add(new JLabel("My naming port:"));
		left.add(new JLabel("My naming path:"));
		left.add(new JLabel("Deploy global:"));
		left.add(new JLabel("My global address:"));
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
        
		final JComboBox<Evaluator> cmbEvs = new JComboBox<>(evList.toArray(new Evaluator[] {}));
		if (initialEv != null)
			cmbEvs.setSelectedItem(initialEv);
		right.add(cmbEvs);
		
		final JCheckBox chkExport = new JCheckBox("", false);
		right.add(chkExport);
		
		JPanel paneNamingPort = new JPanel(new BorderLayout());
		right.add(paneNamingPort);
		paneNamingPort.setVisible(false);

		final JFormattedTextField txtNamingPort = new JFormattedTextField(new NumberFormatter());
		paneNamingPort.add(txtNamingPort, BorderLayout.CENTER);
		txtNamingPort.setValue(Constants.DEFAULT_EVALUATOR_PORT);

		JButton btnCheckNamingPort = UIUtil.makeIconButton(
			"checking-16x16.png",
			"checking", 
			"Checking - http://www.iconarchive.com/show/outline-icons-by-iconsmind/Check-2-icon.html", 
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
		paneNamingPath.setVisible(chkExport.isSelected());
		
		final JTextField txtNamingPath = new JTextField("connect1");
		paneNamingPath.add(txtNamingPath, BorderLayout.CENTER);
		
		JButton btnGenNamingName = UIUtil.makeIconButton(
			"generate-16x16.png",
			"generate", 
			"Generate - http://www.iconarchive.com/show/flatastic-9-icons-by-custom-icon-design/Generate-keys-icon.html", 
			"Generate", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					txtNamingPath.setText("connect" + new Date().getTime());
				}
			});
		paneNamingPath.add(btnGenNamingName, BorderLayout.EAST);

		JCheckBox chkDeployGlobal = new JCheckBox("", false);
		right.add(chkDeployGlobal);

		JPanel paneDeployGlobalAddress = new JPanel(new BorderLayout());
		right.add(paneDeployGlobalAddress);
		paneDeployGlobalAddress.setVisible(chkDeployGlobal.isSelected());

		JTextField txtDeployGlobalAddress = new JTextField("");
		paneDeployGlobalAddress.add(txtDeployGlobalAddress, BorderLayout.CENTER);

		JButton btnGenDeployGlobalAddress = UIUtil.makeIconButton(
			"generate-16x16.png",
			"generate", 
			"Generate - http://www.iconarchive.com/show/flatastic-9-icons-by-custom-icon-design/Generate-keys-icon.html", 
			"Generate", 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					String publicIP = NetUtil.getPublicInetAddress();
					txtDeployGlobalAddress.setText(publicIP != null ? publicIP : "");
				}
			});
		paneDeployGlobalAddress.add(btnGenDeployGlobalAddress, BorderLayout.EAST);

		chkExport.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				paneNamingPort.setVisible(chkExport.isSelected());
				paneNamingPath.setVisible(chkExport.isSelected());
			}
		});

		chkDeployGlobal.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				paneDeployGlobalAddress.setVisible(chkDeployGlobal.isSelected());
			}
		});
		
		JPanel footer = new JPanel();
		dlgEvStarter.add(footer, BorderLayout.SOUTH);
		
		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ConnectInfo connectInfo = new ConnectInfo();
				
				connectInfo.deployGlobal = chkDeployGlobal.isSelected();
				connectInfo.deployGlobalAddress = txtDeployGlobalAddress.getText();
				if (connectInfo.deployGlobalAddress != null) {
					connectInfo.deployGlobalAddress = connectInfo.deployGlobalAddress.trim();
					if (connectInfo.deployGlobalAddress.isEmpty())
						connectInfo.deployGlobalAddress = null;
				}

				if (chkExport.isSelected()) {
					String namingPath = ConnectDlg.normalizeNamingPath(txtNamingPath.getText());
					int namingPort = txtNamingPort.getValue() instanceof Number ? ( (Number) txtNamingPort.getValue()).intValue() : 0;
					namingPort = NetUtil.getPort(namingPort, Constants.TRY_RANDOM_PORT);
					
					String host = null;
					if (connectInfo.deployGlobal)
						host = connectInfo.getDeployGlobalHost();
					host = host != null ? host : "localhost";
					connectInfo.namingUri = xURI.create("rmi://" + host + ":" + namingPort);
						
					if (namingPath != null && !namingPath.isEmpty())
						connectInfo.namingUri = connectInfo.namingUri.concat(namingPath);
				}
				

				Evaluator ev = (Evaluator) cmbEvs.getSelectedItem();
				dlgEvStarter.dispose();
				if (oldGUI != null) oldGUI.dispose();

				run(ev, connectInfo, null, null);
			}
		});
		footer.add(start);
		
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dlgEvStarter.dispose();
			}
		});
		footer.add(cancel);

		
		dlgEvStarter.setVisible(true);
	}
	
	
	/**
	 * Switching remote evaluator.
	 * @param selectedEvName selected evaluator name.
	 * @param oldGUI old GUI.
	 */
	public static void switchRemoteEvaluator(String selectedEvName, Window oldGUI) {
		final ConnectDlg connectDlg = ConnectDlg.connect();
		Service service = connectDlg.getService();

		if (service == null) {
			Evaluator ev = connectDlg.getEvaluator();
			if (ev == null) {
				JOptionPane.showMessageDialog(
					null, "Can't retrieve evaluator", "Retrieval to evaluator failed", JOptionPane.ERROR_MESSAGE);
			}
			else {
				if (oldGUI != null) oldGUI.dispose();
				run(ev, connectDlg.getConnectInfo(), null, null);
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
						Account account = connectDlg.getConnectInfo().account;
						final Evaluator ev = service.getEvaluator(evName, account.getName(), account.getPassword());
						if (ev == null) {
							JOptionPane.showMessageDialog(
									this, "Can't get remote evaluator", "Connection to evaluator fail", JOptionPane.ERROR_MESSAGE);
							return;
						}
						dispose();
						if (oldGUI != null) oldGUI.dispose();
						
						run(ev, connectDlg.getConnectInfo(), null, null);
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
						ConnectDlg.disconnect(service);
					service = null;
				}
				
			};
			
			if (initialEvName != null)
				dlgEvStarter.getItemControl().setSelectedItem(initialEvName);
			dlgEvStarter.setSize(400, 150);
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
		if (!Util.getPluginManager().isFired())
			Util.getPluginManager().fire();
			
		try {
			RegisterTable parserReg = PluginStorage.getParserReg();
			if (parserReg.size() == 0) {
				Util.getPluginManager().discover();
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

			if (oldGUI != null) oldGUI.dispose();
			new EvalCompoundGUI(evaluator, connectInfo, referredData);
		}
		catch (Exception e) {LogUtil.trace(e);}
	}


}


