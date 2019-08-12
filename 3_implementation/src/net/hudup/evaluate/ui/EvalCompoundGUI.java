package net.hudup.evaluate.ui;

import static net.hudup.core.Constants.ROOT_PACKAGE;

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
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import net.hudup.core.Constants;
import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.client.ConnectDlg;
import net.hudup.core.client.Service;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.evaluate.AbstractEvaluator;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.StartDlg;
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
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(EvalCompoundGUI.class);
	
	
	/**
	 * Evaluation configuration.
	 */
	private EvaluatorConfig thisConfig = null;
	
	
	/**
	 * Body panel.
	 */
	private JTabbedPane body = null;
	
	
//	/**
//	 * Single mode evaluator GUI.
//	 */
//	@Deprecated
//	private EvaluateGUI evaluateGUI = null;
	
	
	/**
	 * Batch mode evaluator GUI.
	 */
	private BatchEvaluateGUI batchEvaluateGUI = null;
	
	
	/**
	 * Bound URI.
	 */
	protected xURI bindUri = null;
	
	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified {@link AbstractEvaluator}.
	 * @param bindUri bound URI.
	 */
	public EvalCompoundGUI(Evaluator evaluator, xURI bindUri) {
		super("Evaluator GUI");
		try {
			this.thisConfig = evaluator.getConfig();
			this.thisConfig.setSaveAbility(bindUri == null); //Do not save remote configuration.
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error in getting evaluator configuration");
		}
		this.bindUri = bindUri;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosed(e);
				
				//evaluateGUI.dispose(); //Single mode is removed. Fix bug date: August 6, 2019.
				batchEvaluateGUI.dispose();
				
				thisConfig.save();
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
		
		body = new JTabbedPane();
		content.add(body, BorderLayout.CENTER);
		
//		 //Single mode is removed. Fix bug date: August 6, 2019.
//		evaluateGUI = new EvaluateGUI(evaluator, bindUri);
//		body.add(getMessage("evaluate"), evaluateGUI);
		
		batchEvaluateGUI = new BatchEvaluateGUI(evaluator, bindUri);
		body.add(getMessage("evaluate_batch"), batchEvaluateGUI);
		
		setTitle(getMessage("evaluator"));
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
	 * Getting localized message by specified key.
	 * @param key specified key.
	 * @return message according to key.
	 */
	protected String getMessage(String key) {
		return I18nUtil.getMessage(thisConfig, key);
	}

	
	/**
	 * Creating main menu bar.
	 * @return main menu bar as {@link JMenuBar}.
	 */
	private JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTools = new JMenu(getMessage("tool"));
		mnBar.add(mnTools);
		
		JMenuItem mniSysConfig = new JMenuItem(
			new AbstractAction(getMessage("system_configure")) {

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

		JMenuItem mniSwitchEvaluator = new JMenuItem(
			new AbstractAction(getMessage("switch_evaluator")) {

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
			

		JMenu mnHelp = new JMenu(getMessage("help"));
		mnBar.add(mnHelp);
		
		JMenuItem mniHelpContent = new JMenuItem(
			new AbstractAction(getMessage("help_content")) {

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
			if (bindUri == null)
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
		
		SysConfigDlgExt cfg = new SysConfigDlgExt(this, getMessage("system_configure"), this);
		cfg.update(thisConfig);
		cfg.setVisible(true);
	}


	@Override
	public void pluginChanged(PluginChangedEvent evt) {
		// TODO Auto-generated method stub
//		evaluateGUI.pluginChanged(); //Single mode is removed. Fix bug date: August 6, 2019.
		batchEvaluateGUI.pluginChanged();
	}


	@Override
	public boolean isIdle() {
		// TODO Auto-generated method stub
		try {
//			//Single mode is removed. Fix bug date: August 6, 2019.
//			return !evaluateGUI.getEvaluator().remoteIsStarted() 
//					&& !batchEvaluateGUI.getEvaluator().remoteIsStarted();
			return !batchEvaluateGUI.getEvaluator().remoteIsStarted();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
	/**
	 * Switching evaluator.
	 * @param selectedEvName selected evaluator name.
	 * @param oldGUI old GUI.
	 */
	public static void switchEvaluator(String selectedEvName, Window oldGUI) {
		List<Evaluator> evList = SystemUtil.getInstances(ROOT_PACKAGE, Evaluator.class);
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
					return o1.getName().compareTo(o2.getName());
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
				if (oldGUI != null) oldGUI.dispose();

				run(ev, null, null);
			}
			
			@Override
			protected JComboBox<?> createItemControl() {
				// TODO Auto-generated method stub
				return new JComboBox<Evaluator>(evList.toArray(new Evaluator[0]));
			}
			
			@Override
			protected JTextArea createHelp() {
				// TODO Auto-generated method stub
				JTextArea toolkit = new JTextArea("Thank you for choosing evaluators");
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
		ConnectDlg connectDlg = ConnectDlg.connect();
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
			final ConnectDlg finalConnectDlg = connectDlg;
			final StartDlg dlgEvStarter = new StartDlg((JFrame)null, "List of evaluators") {
				
				/**
				 * Serial version UID for serializable class.
				 */
				private static final long serialVersionUID = 1L;

				@Override
				protected void start() {
					// TODO Auto-generated method stub
					String evName = (String) getItemControl().getSelectedItem();
					try {
						final Evaluator ev = finalService.getEvaluator(evName);
						dispose();
						if (oldGUI != null) oldGUI.dispose();
						
						if (finalService instanceof SocketConnection)
							((SocketConnection)finalService).close();
						
						ev.getPluginStorage().assignToSystem(); //This code line is very important for initializing plug-in storage.
						run(ev, finalConnectDlg.getBindUri(), null);
					}
					catch (Exception e) {
						e.printStackTrace();
						JOptionPane.showMessageDialog(
								null, "Can't get remote evaluator", "Connection to evaluator fail", JOptionPane.ERROR_MESSAGE);
					}
				}
				
				@Override
				protected JComboBox<?> createItemControl() {
					// TODO Auto-generated method stub
					return new JComboBox<String>(evNames);
				}
				
				@Override
				protected JTextArea createHelp() {
					// TODO Auto-generated method stub
					JTextArea toolkit = new JTextArea("Thank you for choosing evaluators");
					toolkit.setEditable(false);
					return toolkit;
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
	 * @param oldGUI old GUI.
	 */
	public static void run(Evaluator evaluator, xURI bindUri, Window oldGUI) {
		try {
			RegisterTable algReg = evaluator.extractAlgFromPluginStorage();
			if (algReg == null || algReg.size() == 0) {
				JOptionPane.showMessageDialog(null, 
						"There is no registered algorithm.\nProgramm not run", 
						"No algorithm", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			//PluginStorageWrapper.assignToSystem() must be called before.
			RegisterTable parserReg = PluginStorage.getParserReg();
			if (parserReg.size() == 0) {
				JOptionPane.showMessageDialog(null, 
						"There is no registered dataset parser.\n Programm not run", 
						"No dataset parser", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			//PluginStorageWrapper.assignToSystem() must be called before.
			RegisterTable metricReg = PluginStorage.getMetricReg();
			NoneWrapperMetricList metricList = evaluator.defaultMetrics();
			for (int i = 0; i < metricList.size(); i++) {
				Metric metric = metricList.get(i);
				if(!(metric instanceof MetaMetric))
					continue;
				metricReg.unregister(metric.getName());
				
				boolean registered = metricReg.register(metric);
				if (registered)
					logger.info("Registered algorithm: " + metricList.get(i).getName());
			}

			if (oldGUI != null) oldGUI.dispose();
			new EvalCompoundGUI(evaluator, bindUri);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


}


