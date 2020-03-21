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
import java.awt.Image;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTabbedPane;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.client.ConnectDlg;
import net.hudup.core.client.Service;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ui.SysConfigDlgExt;

/**
 * This class represents the entire frame allowing users to interact fully with evaluator.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class EvalCompoundGUI extends JFrame implements PluginChangedListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Evaluation configuration.
	 */
	private EvaluatorConfig thisConfig = null;
	
	
	/**
	 * Body panel.
	 */
	private JTabbedPane body = null;
	
	
	/**
	 * Batch mode evaluator GUI.
	 */
	private BatchEvaluateGUI batchEvaluateGUI = null;
	
	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified {@link EvaluatorAbstract}.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 */
	public EvalCompoundGUI(Evaluator evaluator, xURI bindUri) {
		this(evaluator, bindUri, null);
	}
	
	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified {@link EvaluatorAbstract}.
	 * @param bindUri bound URI. If this parameter is null, evaluator is local.
	 * @param referredData evaluator GUI data.
	 */
	public EvalCompoundGUI(Evaluator evaluator, xURI bindUri, final EvaluateGUIData referredData) {
		super("Evaluator GUI");
		try {
			this.thisConfig = evaluator.getConfig();
			this.thisConfig.setSaveAbility(bindUri == null); //Save only local configuration.
		}
		catch (Throwable e) {
			e.printStackTrace();
			LogUtil.error("Error in getting evaluator configuration");
		}
		this.batchEvaluateGUI = new BatchEvaluateGUI(evaluator, bindUri, referredData);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
//				batchEvaluateGUI.dispose(); //Calling in dispose() method instead.
			}

		});
		
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null)
        	setIconImage(image);
		
		setSize(800, 600);
		setLocationRelativeTo(null);
	    setJMenuBar(createMenuBar());
	    
		Container content = getContentPane();
		content.setLayout(new BorderLayout(2, 2));
		
		this.body = new JTabbedPane();
		content.add(this.body, BorderLayout.CENTER);
		
		this.body.add(I18nUtil.message("evaluate_batch"), this.batchEvaluateGUI);
		
		setTitle(I18nUtil.message("evaluator"));
		
//		this.body.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if(!SwingUtilities.isRightMouseButton(e)) return;
//				JPopupMenu contextMenu = createContextMenu();
//				if (contextMenu != null) contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
//			}
//		});
//		this.batchEvaluateGUI.addMouseListener(new MouseAdapter() {
//			@Override
//			public void mouseClicked(MouseEvent e) {
//				if(!SwingUtilities.isRightMouseButton(e)) return;
//				JPopupMenu contextMenu = createContextMenu();
//				if (contextMenu != null) contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
//			}
//		});

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
	 * @return main menu bar as {@link JMenuBar}.
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

		boolean agent = false;
		try {
			agent = batchEvaluateGUI.evaluator.isAgent();
		}
		catch (Throwable e) {e.printStackTrace();}
		if (batchEvaluateGUI.bindUri != null || !agent) {
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

	
//	/**
//	 * Create context menu.
//	 * @return context menu.
//	 */
//	private JPopupMenu createContextMenu() {
//		if (!isIdle()) return null;
//		
//		boolean agent = false;
//		try {
//			agent = batchEvaluateGUI.evaluator.isAgent();
//		}
//		catch (Throwable e) {
//			e.printStackTrace();
//		}
//		if (agent && batchEvaluateGUI.bindUri == null)
//			return null;
//
//		JPopupMenu contextMenu = new JPopupMenu();
//		
//		JMenuItem mniPluginConfig = UIUtil.makeMenuItem(null, I18nUtil.message("plugin_manager"), 
//			new ActionListener() {
//				
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					PluginStorageManifest.showDlg(getThisEvalGUI(), getThisEvalGUI());
//				}
//			});
//		contextMenu.add(mniPluginConfig);
//
//		return contextMenu;
//	}
	
	
	/**
	 * Switch evaluator.
	 */
	private void switchEvaluator() {
		if (!isIdle()) {
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
			if (batchEvaluateGUI.bindUri == null)
				switchEvaluator(batchEvaluateGUI.getEvaluator().getName(), this);
			else
				switchRemoteEvaluator(batchEvaluateGUI.getEvaluator().getName(), this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Show an dialog allowing users to see and modify the configuration of system.
	 */
	private void sysConfig() {
		if (!isIdle()) {
			JOptionPane.showMessageDialog(
					getThisEvalGUI(), 
					"Evaluatior is running.\n It is impossible to configure system", 
					"Evaluatior is running", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		boolean agent = false;
		try {
			agent = batchEvaluateGUI.evaluator.isAgent();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		SysConfigDlgExt cfg = new SysConfigDlgExt(this, I18nUtil.message("system_configure"), this) {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onApply() {
				// TODO Auto-generated method stub
				if (!isModified())
					return;
				
				if (paneSysConfig != null && paneSysConfig.isModified()) {
					paneSysConfig.apply();
					
					try {
						if (batchEvaluateGUI.bindUri != null)
							batchEvaluateGUI.evaluator.setConfig(thisConfig);
					} catch (Throwable e) {e.printStackTrace();}
				}
				
				if (paneRegister != null && paneRegister.isModified())
					paneRegister.apply();
			}
			
		};
		
		cfg.update(thisConfig);
		if ((batchEvaluateGUI.bindUri != null) || (agent))
			cfg.getPluginStorageManifest().setEnabled(false);
		
		cfg.setVisible(true);
	}


	@Override
	public void dispose() {
		try {
			boolean agent = false;
			try {
				agent = batchEvaluateGUI.evaluator.isAgent();
			} 
			catch (Exception e) {e.printStackTrace();}
	
			batchEvaluateGUI.dispose();
			if (!agent || batchEvaluateGUI.bindUri != null)
				PluginStorage.clear();
			
			super.dispose();
		}
		catch (Throwable e) {
			if (batchEvaluateGUI.bindUri != null)
				System.exit(0);
		}
	}


	@Override
	public void pluginChanged(PluginChangedEvent evt) {
		// TODO Auto-generated method stub
		batchEvaluateGUI.pluginChanged(evt);
	}


	@Override
	public boolean isIdle() {
		// TODO Auto-generated method stub
		return batchEvaluateGUI.isIdle();
	}
	
	
	@Override
	public int getPort() {
		// TODO Auto-generated method stub
		return batchEvaluateGUI.getPort();
	}


	/**
	 * Switching evaluator.
	 * @param selectedEvName selected evaluator name.
	 * @param oldGUI old GUI.
	 */
	public static void switchEvaluator(String selectedEvName, Window oldGUI) {
		List<Evaluator> evList = Util.getPluginManager().discover(Evaluator.class);
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
				// TODO Auto-generated method stub
				try {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
				catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		final StartDlg dlgEvStarter = new StartDlg((JFrame)null, "List of evaluators") {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void start() {
				// TODO Auto-generated method stub
				final Evaluator ev = (Evaluator) getItemControl().getSelectedItem();
				dispose();
				if (oldGUI != null)
					oldGUI.dispose();

				run(ev, null, null, null);
			}
			
			@Override
			protected JComboBox<?> createItemControl() {
				// TODO Auto-generated method stub
				return new JComboBox<Evaluator>(evList.toArray(new Evaluator[0]));
			}
			
			@Override
			protected TextArea createHelp() {
				// TODO Auto-generated method stub
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
	 * Switching remote evaluator.
	 * @param selectedEvName selected evaluator name.
	 * @param oldGUI old GUI.
	 */
	public static void switchRemoteEvaluator(String selectedEvName, Window oldGUI) {
		final ConnectDlg connectDlg = ConnectDlg.connect();
		Service service = connectDlg.getService();

		if (service == null) {
			JOptionPane.showMessageDialog(
				null, "Can't retrieve service", "Retrieval to service failed", JOptionPane.ERROR_MESSAGE);
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
					// TODO Auto-generated method stub
					String evName = (String) getItemControl().getSelectedItem();
					try {
						final Evaluator ev = service.getEvaluator(evName, connectDlg.getRemoteUsername(), connectDlg.getRemotePassword());
						if (ev == null) {
							JOptionPane.showMessageDialog(
									this, "Can't get remote evaluator", "Connection to evaluator fail", JOptionPane.ERROR_MESSAGE);
							return;
						}
						dispose();
						if (oldGUI != null) oldGUI.dispose();
						
						xURI bindUri = ConnectDlg.getBindUri();
						
						//ev.getPluginStorage().assignToSystem();
						run(ev, bindUri, null, null);
					}
					catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(
								this, "Can't get remote evaluator", "Connection to evaluator fail", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				@Override
				protected JComboBox<?> createItemControl() {
					// TODO Auto-generated method stub
					return new JComboBox<String>(evNames);
				}
				
				@Override
				protected TextArea createHelp() {
					// TODO Auto-generated method stub
					TextArea helper = new TextArea("Thank you for choosing evaluators");
					helper.setEditable(false);
					return helper;
				}

				@Override
				public void dispose() {
					// TODO Auto-generated method stub
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
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * Staring the particular evaluator selected by user.
	 * @param evaluator particular evaluator selected by user.
	 * @param bindUri bound URI. If this parameter is null, evaluator is inside evaluator, otherwise it is remote evaluator.
	 * @param referredData evaluator GUI data.
	 * @param oldGUI old GUI.
	 */
	public static void run(Evaluator evaluator, xURI bindUri, EvaluateGUIData referredData, Window oldGUI) {
		if (!Util.getPluginManager().isFired())
			Util.getPluginManager().fire();
			
		try {
//			RegisterTable algReg = EvaluatorAbstract.extractAlgFromPluginStorage(evaluator);
//			if (algReg == null || algReg.size() == 0) {
//				JOptionPane.showMessageDialog(null, 
//						"WARNING: There is no registered algorithm.\n Program cannot run.", 
//						"No registered algorithm", JOptionPane.ERROR_MESSAGE);
//				return;
//			}
			
			RegisterTable parserReg = PluginStorage.getParserReg();
			if (parserReg.size() == 0) {
				Util.getPluginManager().discover();
//				JOptionPane.showMessageDialog(null,
//						"There is no registered dataset parser.\n Program cannot run.", 
//						"No dataset parser", JOptionPane.ERROR_MESSAGE);
//				return;
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
			new EvalCompoundGUI(evaluator, bindUri, referredData);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}


