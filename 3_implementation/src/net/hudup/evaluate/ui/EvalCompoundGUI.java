package net.hudup.evaluate.ui;

import static net.hudup.core.Constants.ROOT_PACKAGE;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
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

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.evaluate.AbstractEvaluator;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.ui.HelpContent;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ui.SysConfigDlgExt;


/**
 * This class represents the entire frame allowing users to interact fully with evaluator in single mode or batch mode.
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
	 * 
	 */
	private JTabbedPane body = null;
	
	
	/**
	 * 
	 */
	private EvaluateGUI evaluateGUI = null;
	
	
	/**
	 * 
	 */
	private BatchEvaluateGUI batchEvaluateGUI = null;
	
	
	/**
	 * Constructor with specified evaluator.
	 * @param evaluator specified {@link AbstractEvaluator}.
	 */
	public EvalCompoundGUI(Evaluator evaluator) {
		super("Evaluator GUI");
		try {
			this.thisConfig = evaluator.getConfig();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Error in getting evaluator configuration");
		}
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				
				evaluateGUI.dispose();
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
		
		evaluateGUI = new EvaluateGUI(evaluator);
		body.add(getMessage("evaluate"), evaluateGUI);
		
		batchEvaluateGUI = new BatchEvaluateGUI(evaluator);
		body.add(getMessage("evaluate_batch"), batchEvaluateGUI);
		
		setTitle(getMessage("evaluator"));
		setVisible(true);
	}
	
	
	/**
	 * Getting this GUI.
	 * @return this GUI.
	 */
	private EvalCompoundGUI getThis() {
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
					new HelpContent(getThis());
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
					UIUtil.getFrameForComponent(getThis()), 
					"Evaluation task will be terminated if switching evaluator.\n" +
						"Are you sure to switch evaluator?", 
					"Switching evaluator", 
					JOptionPane.YES_NO_OPTION, 
					JOptionPane.QUESTION_MESSAGE);
				
			if (confirm != JOptionPane.YES_OPTION)
				return;
		}
	
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
		
        AbstractEvaluateGUI evaluatorGUI = (AbstractEvaluateGUI) body.getSelectedComponent();
		Evaluator initialEv = null;
		for (Evaluator ev : evList) {
			try {
				if (ev.getName().equals(evaluatorGUI.getEvaluator().getName())) {
					initialEv = ev;
					break;
				}
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        
		final StartDlg dlgEvSwitcher = new StartDlg(this, "List of evaluators") {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void start() {
				// TODO Auto-generated method stub
				Evaluator newEvaluator = (Evaluator) getItemControl().getSelectedItem();
				dispose();
				switchEvaluator0(newEvaluator);
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
			dlgEvSwitcher.getItemControl().setSelectedItem(initialEv);
		dlgEvSwitcher.setSize(400, 150);
        dlgEvSwitcher.setVisible(true);
	}
	
	
	/**
	 * Switch to the new evaluator 
	 * @param newEvaluator new evaluator.
	 */
	private void switchEvaluator0(Evaluator newEvaluator) {
		try {
			RegisterTable algReg = newEvaluator.extractAlgFromPluginStorage();
			if (algReg.size() == 0) {
				JOptionPane.showMessageDialog(this, 
						"There is no registered algorithm.\nProgramm not run", 
						"No algorithm", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			RegisterTable parserReg = PluginStorage.getParserReg();
			if (parserReg.size() == 0) {
				JOptionPane.showMessageDialog(this, 
						"There is no registered dataset parser.\n Programm not run", 
						"No dataset parser", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			
			RegisterTable metricReg = PluginStorage.getMetricReg();
			NoneWrapperMetricList metricList = newEvaluator.defaultMetrics();
			for (int i = 0; i < metricList.size(); i++) {
				Metric metric = metricList.get(i);
				if(!(metric instanceof MetaMetric))
					continue;
				metricReg.unregister(metric.getName());
				
				boolean registered = metricReg.register(metric);
				if (registered)
					logger.info("Registered algorithm: " + metricList.get(i).getName());
			}
			
			this.dispose();
			new EvalCompoundGUI(newEvaluator.getClass().newInstance());
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Show an dialog allowing users to see and modify the configuration of system.
	 */
	private void sysConfig() {
		if (!isIdle()) {
			JOptionPane.showMessageDialog(
					getThis(), 
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
		evaluateGUI.pluginChanged();
		batchEvaluateGUI.pluginChanged();
	}


	@Override
	public boolean isIdle() {
		// TODO Auto-generated method stub
		try {
			return !evaluateGUI.getEvaluator().getController().isStarted() 
					&& !batchEvaluateGUI.getEvaluator().getController().isStarted();
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	
}


