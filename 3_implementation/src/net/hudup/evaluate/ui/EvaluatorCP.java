/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.Util;
import net.hudup.core.client.ConnectDlg;
import net.hudup.core.client.Service;
import net.hudup.core.client.ServiceExt;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class is control panel for evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluatorCP extends JFrame {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal service.
	 */
	protected Service service = null;
	
	
	/**
	 * Bound URI.
	 */
	protected xURI bindUri = null;
	
	
	/**
	 * Account name.
	 */
	protected String account = null;
	
	
	/**
	 * Password.
	 */
	protected String password = null;
	
	
	/**
	 * Status of current evaluator.
	 */
	protected JLabel lblStatus = null;
	
	
	/**
	 * Control for list of items.
	 */
    protected JComboBox<EvaluatorItem> cmbEvaluators = null;

    
    /**
     * Adding button.
     */
    protected JButton btnRefresh = null;
    
    
    /**
     * Adding button.
     */
    protected JButton btnAdd = null;
    
    
    /**
     * Deleting button.
     */
    protected JButton btnDelete = null;
    
    
    /**
     * Starting button.
     */
    protected JButton btnOpenStart = null;
    
    
    /**
     * Configuration panel.
     */
    protected SysConfigPane paneConfig = null;
    
    
    /**
     * Closing button.
     */
    protected JButton btnClose = null;

    
    /**
	 * Constructor with evaluator and bound URI.
	 * @param service specified service.
	 * @param bindUri bound URI.
	 */
	public EvaluatorCP(ServiceExt service, xURI bindUri) {
		this(service, null, null, bindUri);
	}
	
	
    /**
	 * Constructor with evaluator and bound URI.
	 * @param service specified service.
	 * @param account account.
	 * @param password password.
	 * @param bindUri bound URI.
	 */
	public EvaluatorCP(Service service, String account, String password, xURI bindUri) {
		super("Evaluator control panel");
		this.service = service;
		this.account = account;
		this.password = password;
		this.bindUri = bindUri;
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(null);
		setSize(600, 400);
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null)
        	setIconImage(image);
		
        addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosed(e);
				if (!paneConfig.isModified())
					return;
				
				int confirm = JOptionPane.showConfirmDialog(
						getThisEvaluatorCP(), 
						"System properties are modified. Do you want to apply them?", 
						"System properties are modified", 
						JOptionPane.YES_NO_OPTION,
						JOptionPane.QUESTION_MESSAGE);
				
				if (confirm == JOptionPane.YES_OPTION)
					paneConfig.apply();
			}
			
		});
        
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        add(header, BorderLayout.NORTH);
        
        lblStatus = new JLabel();
        header.add(lblStatus, BorderLayout.NORTH);
        
        cmbEvaluators = new JComboBox<EvaluatorItem>();
        List<Evaluator> evaluators = getEvaluators();
        for  (Evaluator evaluator : evaluators) {
        	cmbEvaluators.addItem(new EvaluatorItem(evaluator));
        }
        header.add(cmbEvaluators, BorderLayout.CENTER);
        cmbEvaluators.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() != ItemEvent.SELECTED)
					return;
				
				updateMode();
			}
		});
        
		JPanel headerTool = new JPanel(new BorderLayout());
		header.add(headerTool, BorderLayout.SOUTH);
		
		JPanel headerTool1 = new JPanel();
		headerTool.add(headerTool1, BorderLayout.WEST);
		
		btnRefresh = UIUtil.makeIconButton(
			"refresh-16x16.png", 
			"refresh", 
			I18nUtil.message("refresh"), 
			I18nUtil.message("refresh"), 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
			        cmbEvaluators.removeAllItems();
			        List<Evaluator> evaluators = getEvaluators();
			        for  (Evaluator evaluator : evaluators) {
			        	cmbEvaluators.addItem(new EvaluatorItem(evaluator));
			        }
			        
			        updateMode();
				}
			});
		btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		headerTool1.add(btnRefresh);

		btnAdd = UIUtil.makeIconButton(
			"add-16x16.png", 
			"add", 
			I18nUtil.message("add"), 
			I18nUtil.message("add"), 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
				}
			});
		btnAdd.setMargin(new Insets(0, 0 , 0, 0));
		headerTool1.add(btnAdd);
		
		btnDelete = UIUtil.makeIconButton(
			"delete-16x16.png", 
			"delete", 
			I18nUtil.message("delete"), 
			I18nUtil.message("delete"), 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
				}
			});
		btnDelete.setMargin(new Insets(0, 0 , 0, 0));
		headerTool1.add(btnDelete);

		JPanel headerTool2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		headerTool.add(headerTool2, BorderLayout.EAST);

		btnOpenStart = UIUtil.makeIconButton(
			"open-start-16x16.png", 
			"start", 
			I18nUtil.message("open"), 
			I18nUtil.message("open"), 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					EvaluatorItem evaluatorItem = (EvaluatorItem)cmbEvaluators.getSelectedItem();
					if (evaluatorItem != null && evaluatorItem.evaluator != null)
						new EvalCompoundGUI(evaluatorItem.evaluator, bindUri, null);
				}
			});
		btnOpenStart.setMargin(new Insets(0, 0 , 0, 0));
		headerTool2.add(btnOpenStart);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		paneConfig = new SysConfigPane() {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onApply() {
				// TODO Auto-generated method stub
				super.onApply();
				if (service == null) return;
				
				EvaluatorConfig config = (EvaluatorConfig) tblProp.getPropList();
				if (config == null) return;
				
				EvaluatorItem evaluatorItem = (EvaluatorItem) cmbEvaluators.getSelectedItem();
				if (evaluatorItem == null || evaluatorItem.evaluator == null) return;
				
				try {
					if (bindUri != null)
						evaluatorItem.evaluator.setConfig(config);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

		};
		paneConfig.setToolbarVisible(true);
		paneConfig.hideOkCancel();
		body.add(paneConfig, BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		footer.add(btnClose);
		
		updateMode();
	}
	
	
	/**
	 * Getting this evaluator control panel.
	 * @return this evaluator control panel.
	 */
	private EvaluatorCP getThisEvaluatorCP() {
		return this;
	}
	
	
	/**
	 * Getting list of evaluators.
	 * @return list of evaluators.
	 */
	protected List<Evaluator> getEvaluators() {
		List<Evaluator> evaluators = Util.newList();
		if (service == null) return evaluators;
		
		if (service instanceof ServiceExt) {
			try {
				evaluators = ((ServiceExt)service).getEvaluators();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return evaluators;
		}
		
		String[] evaluatorNames = null;
		try {
			evaluatorNames = service.getEvaluatorNames();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		if (evaluatorNames == null || evaluatorNames.length == 0)
			return evaluators;
		
		for (String evaluatorName : evaluatorNames) {
			Evaluator evaluator = null;
			try {
				evaluator = service.getEvaluator(evaluatorName, account, password);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			if (evaluator != null) evaluators.add(evaluator);
		}
		
		return evaluators;
	}
	
	
	/**
	 * Updating mode.
	 */
	protected void updateMode() {
		lblStatus.setText("");
		btnRefresh.setEnabled(false);
		btnAdd.setEnabled(false);
		btnDelete.setEnabled(false);
		btnOpenStart.setEnabled(false);
		paneConfig.setEnabled(false);
		paneConfig.btnLoad.setEnabled(false);
		paneConfig.btnSave.setEnabled(false);
		
		if (service == null) return;
		
		btnRefresh.setEnabled(true);
		
		EvaluatorItem evaluatorItem = (EvaluatorItem)cmbEvaluators.getSelectedItem();
		if (evaluatorItem == null || evaluatorItem.evaluator == null) return;
		
		btnAdd.setEnabled(true);
		try {
			Evaluator evaluator = evaluatorItem.evaluator;
			if (!evaluator.remoteIsStarted()) {
				lblStatus.setText("stopped");
				btnDelete.setEnabled(true);
				btnOpenStart.setEnabled(true);
				paneConfig.setEnabled(true);
				paneConfig.btnLoad.setEnabled(true);
				paneConfig.btnSave.setEnabled(true);
			}
			else if (evaluator.remoteIsRunning()) {
				lblStatus.setText("running");
				btnOpenStart.setEnabled(true);
			}
			else {
				lblStatus.setText("paused");
				btnOpenStart.setEnabled(true);
			}
			
			EvaluatorConfig config = evaluator.getConfig();
			if (config != null) {
				config.setSaveAbility(bindUri == null /*|| evaluator.isTemplate()*/);
				paneConfig.update(evaluator.getConfig());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * This class is item wrapper of evaluator for combo-box
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	protected class EvaluatorItem {
		
		/**
		 * Internal evaluator.
		 */
		public Evaluator evaluator = null;
		
		/**
		 * Constructor with evaluator.
		 * @param evaluator evaluator.
		 */
		public EvaluatorItem(Evaluator evaluator) {
			this.evaluator = evaluator;
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			if (evaluator == null)
				return "";
			else {
				try {
					return evaluator.getName();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
				return "";
			}
		}
		
	}
	
	
	/**
	 * Starting evaluator control panel.
	 */
	public static void start() {
		final ConnectDlg connectDlg = ConnectDlg.connect();
		Service service = connectDlg.getService();

		if (service == null) {
			JOptionPane.showMessageDialog(
				null, "Can't retrieve service", "Retrieval to service failed", JOptionPane.ERROR_MESSAGE);
			return;
		}

		boolean validated = false;
		try {
			validated = service.validateAccount(connectDlg.getRemoteUsername(), connectDlg.getRemotePassword(), DataConfig.ACCOUNT_ADMIN_PRIVILEGE);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
		if (!validated) {
			JOptionPane.showMessageDialog(
					null, "Account is not administrator", "Not administration account", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		EvaluatorCP ecp = null;
		if (service instanceof ServiceExt)
			ecp = new EvaluatorCP((ServiceExt)service, ConnectDlg.getBindUri());
		else
			ecp = new EvaluatorCP(service, connectDlg.getRemoteUsername(), connectDlg.getRemotePassword(), ConnectDlg.getBindUri());
		ecp.setVisible(true);
	}
	
	
	/**
	 * Main method.
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		start();
	}
	
	
}
