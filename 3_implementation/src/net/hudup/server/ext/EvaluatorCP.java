/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

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
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.hudup.core.Util;
import net.hudup.core.client.ConnectDlg;
import net.hudup.core.client.Service;
import net.hudup.core.client.ServiceExt;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.ui.EvaluateGUIData;
import net.hudup.core.logistic.Account;
import net.hudup.core.logistic.BindNamingURI;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.evaluate.ui.EvalCompoundGUI;
import net.hudup.server.DefaultService;

/**
 * This class is control panel for evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluatorCP extends JFrame implements EvaluatorListener {

	
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
	 * Account.
	 */
	protected Account account = null;
	
	
	/**
	 * Exported stub (EvaluatorListener,).
	 */
	protected Remote exportedStub = null;

	
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
	 * Constructor with specified service.
	 * @param service specified service.
	 */
	public EvaluatorCP(Service service) {
		this(service, null, null, null);
	}
	
	
    /**
	 * Constructor with specified service, account, password, and bound URI.
	 * @param service specified service.
	 * @param account account.
	 * @param password password.
	 * @param bindUri bound URI.
	 */
	public EvaluatorCP(Service service, String account, String password, xURI bindUri) {
		super("Evaluator control panel");
		this.service = service;
		this.account = Account.create(account, password);
		this.bindUri = bindUri;
		
		if (bindUri != null) { //Evaluator is remote
			this.exportedStub = NetUtil.RegistryRemote.export(this, bindUri.getPort());
			if (this.exportedStub != null)
				LogUtil.info("Evaluator control panel exported at port " + bindUri.getPort());
			else
				LogUtil.info("Evaluator control panel failed to exported");
		}

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
			}
			
		});
        
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        add(header, BorderLayout.NORTH);
        
        lblStatus = new JLabel();
        header.add(lblStatus, BorderLayout.NORTH);
        
		JPanel evPane = new JPanel(new BorderLayout());
        header.add(evPane, BorderLayout.CENTER);
		
        cmbEvaluators = new JComboBox<EvaluatorItem>();
        cmbEvaluators.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				// TODO Auto-generated method stub
				if (e.getStateChange() == ItemEvent.DESELECTED) {
				}
				else if (e.getStateChange() == ItemEvent.SELECTED) {
					updateMode();
				}
			}
		});
        evPane.add(cmbEvaluators, BorderLayout.CENTER);
        
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
					if (evaluatorItem == null || evaluatorItem.evaluator == null) {
						JOptionPane.showMessageDialog(
								getThisEvaluatorCP(), 
								"Cannot open evaluator", 
								"Cannot open evaluator", 
								JOptionPane.ERROR_MESSAGE);
						return;
					}
					
					if (evaluatorItem.guiData == null)
						evaluatorItem.guiData = new EvaluateGUIData();
					
					if (evaluatorItem.guiData.active) {
						JOptionPane.showMessageDialog(
							getThisEvaluatorCP(), 
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
								getThisEvaluatorCP(), 
								"Cannot open evaluator control panel due to lack of evaluate package", 
								"lack of evaluate package", 
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					EvalCompoundGUI.run(evaluatorItem.evaluator, BindNamingURI.createBindUri(bindUri), evaluatorItem.guiData, null);
				}
			});
		btnOpenStart.setMargin(new Insets(0, 0 , 0, 0));
        evPane.add(btnOpenStart, BorderLayout.EAST);

		JPanel headerTool = new JPanel(new FlowLayout(FlowLayout.LEFT));
		header.add(headerTool, BorderLayout.SOUTH);
		
		btnRefresh = UIUtil.makeIconButton(
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
		btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		headerTool.add(btnRefresh);

		btnAdd = UIUtil.makeIconButton(
			"add-16x16.png", 
			"add", 
			I18nUtil.message("add"), 
			I18nUtil.message("add"), 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					reproduceEvaluator();
				}
			});
		btnAdd.setMargin(new Insets(0, 0 , 0, 0));
		headerTool.add(btnAdd);
		
		btnDelete = UIUtil.makeIconButton(
			"delete-16x16.png", 
			"delete", 
			I18nUtil.message("delete"), 
			I18nUtil.message("delete"), 
			
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					removeEvaluator();
				}
			});
		btnDelete.setMargin(new Insets(0, 0 , 0, 0));
		headerTool.add(btnDelete);

		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		paneConfig = new SysConfigPane() {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean apply() {
				// TODO Auto-generated method stub
				boolean ret = super.apply();
				if (!ret) return false;
				if (service == null) return ret;
				
				EvaluatorConfig config = (EvaluatorConfig) tblProp.getPropList();
				if (config == null) return ret;
				
				EvaluatorItem evaluatorItem = (EvaluatorItem) cmbEvaluators.getSelectedItem();
				if (evaluatorItem == null || evaluatorItem.evaluator == null) return ret;
				
				try {
					if (bindUri != null)
						evaluatorItem.evaluator.setConfig(config);
				}
				catch (Exception e) {
					LogUtil.trace(e);
				}
				
				return true;
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
		
		refresh();
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
				if (account != null)
					evaluators = ((ServiceExt)service).getEvaluators(account.getName(), account.getPassword());
				else if (service instanceof DefaultServiceExt)
					evaluators = ((DefaultServiceExt)service).getEvaluators();
				else
					evaluators = Util.newList();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
			return evaluators;
		}
		
		String[] evaluatorNames = null;
		try {
			evaluatorNames = service.getEvaluatorNames();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		if (evaluatorNames == null || evaluatorNames.length == 0)
			return evaluators;
		
		for (String evaluatorName : evaluatorNames) {
			Evaluator evaluator = null;
			try {
				if (account != null)
					evaluator = service.getEvaluator(evaluatorName, account.getName(), account.getPassword());
				else if (service instanceof DefaultService)
					evaluator = ((DefaultService)service).getEvaluator(evaluatorName);
				else
					evaluator = null;
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			if (evaluator != null) evaluators.add(evaluator);
		}
		
		return evaluators;
	}
	
	
	/**
	 * Updating mode.
	 */
	protected void updateMode() {
		checkConfigModified();
		
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
			EvaluatorConfig config = evaluator.getConfig();
			if (!evaluator.remoteIsStarted()) {
				lblStatus.setText("Status: stopped");
				btnDelete.setEnabled(config == null || config.isReproduced());
				btnOpenStart.setEnabled(true);
				paneConfig.setEnabled(true);
				paneConfig.btnLoad.setEnabled(true);
				paneConfig.btnSave.setEnabled(true);
			}
			else if (evaluator.remoteIsRunning()) {
				lblStatus.setText("Status: running...");
				btnOpenStart.setEnabled(true);
			}
			else {
				lblStatus.setText("Status: paused");
				btnOpenStart.setEnabled(true);
			}
			
			if (config != null) {
				if (bindUri != null)
					config.setSaveAbility(false);
				paneConfig.update(evaluator.getConfig());
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Refreshing evaluators.
	 */
	protected synchronized void refresh() {
		unsetupListeners();
		
        cmbEvaluators.removeAllItems();
        List<Evaluator> evaluators = getEvaluators();
        for  (Evaluator evaluator : evaluators) {
        	cmbEvaluators.addItem(new EvaluatorItem(evaluator));
        }
        
        setupListeners();
        
        updateMode();
	}
	
	
	/**
	 * Refreshing evaluators.
	 * @param evaluatorName evaluator name.
	 * @param reproducedVersion reproduced version.
	 */
	protected synchronized void refresh(String evaluatorName, String reproducedVersion) {
		unsetupListeners();
		
        cmbEvaluators.removeAllItems();
        List<Evaluator> evaluators = getEvaluators();
        EvaluatorItem selectedItem = null;
        for (Evaluator evaluator : evaluators) {
        	EvaluatorItem item = new EvaluatorItem(evaluator);
        	cmbEvaluators.addItem(item);
        	
        	if (service != null && service instanceof DefaultServiceExt)
        		item.guiData = ((DefaultServiceExt)service).getEvaluateGUIData(evaluator);
        	
        	if (item.getName().equals(evaluatorName + "-" + reproducedVersion))
        		selectedItem = item;
        }
        if (selectedItem != null)
        	cmbEvaluators.setSelectedItem(selectedItem);

        setupListeners();
        
        updateMode();
	}

	
	/**
	 * Duplicate / reproduce the selected evaluator.
	 */
	protected void reproduceEvaluator() {
		if (!(service instanceof ServiceExt)) {
			JOptionPane.showMessageDialog(
				this, 
				"This service does not support to reproduce evaluator", 
				"Evaluator reproduction not supported", 
				JOptionPane.ERROR_MESSAGE);
        	return;
		}
		
		EvaluatorItem evaluatorItem = (EvaluatorItem)cmbEvaluators.getSelectedItem();
		if (evaluatorItem == null || evaluatorItem.evaluator == null) return;
		Evaluator evaluator = evaluatorItem.evaluator;
		
		JDialog selectDlgNameDlg = new JDialog(UIUtil.getFrameForComponent(this), "Select a algorithm name", true);
		selectDlgNameDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		selectDlgNameDlg.setLocationRelativeTo(this);
		
		selectDlgNameDlg.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        selectDlgNameDlg.add(header, BorderLayout.NORTH);
		
        header.add(new JLabel("Type reproduced version"), BorderLayout.WEST);
        final JTextField txtVersionName = new JTextField("" + new Date().getTime());
        header.add(txtVersionName, BorderLayout.CENTER);
        
        final StringBuffer versionName = new StringBuffer();
        JPanel footer = new JPanel();
        selectDlgNameDlg.add(footer, BorderLayout.SOUTH);
        
        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				String text = txtVersionName.getText();
				if (text != null && !text.trim().isEmpty())
					versionName.append(text.trim());
				selectDlgNameDlg.dispose();
			}
		});
        footer.add(btnOK);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				selectDlgNameDlg.dispose();
			}
		});
        footer.add(btnCancel);
        
        selectDlgNameDlg.setSize(400, 150);
        selectDlgNameDlg.setVisible(true);
        
        if (versionName.length() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Empty reproduced version", 
					"Empty reproduced version", 
					JOptionPane.ERROR_MESSAGE);
        	return;
        }
        
        try {
        	String evaluatorName = evaluator.getName();
        	
			if (account != null)
				evaluator = ((ServiceExt)service).getEvaluator(evaluatorName, account.getName(), account.getPassword(), versionName.toString());
			else if (service instanceof DefaultServiceExt)
				evaluator = ((DefaultServiceExt)service).getEvaluator(evaluatorName, versionName.toString());
			else
				evaluator = null;
        	
            if (evaluator != null) {
        		JOptionPane.showMessageDialog(
    				this, 
    				"Success to reproduce evaluator '" + evaluatorName + "'", 
    				"Success to reproduce evaluator", 
    				JOptionPane.INFORMATION_MESSAGE);
        		
        		refresh(evaluatorName, versionName.toString());
        		return;
            }
        }
        catch (Exception e) {
        	LogUtil.trace(e);
        }
        
		JOptionPane.showMessageDialog(
			this, 
			"Fail to reproduce evaluator", 
			"Fail to reproduce evaluator", 
			JOptionPane.ERROR_MESSAGE);
	}
	
	
	/**
	 * Removing the evaluator.
	 */
	protected void removeEvaluator() {
		if (!(service instanceof ServiceExt)) {
			JOptionPane.showMessageDialog(
				this, 
				"This service does not support to reproduce evaluator", 
				"Evaluator reproduction not supported", 
				JOptionPane.ERROR_MESSAGE);
        	return;
		}
		
		EvaluatorItem evaluatorItem = (EvaluatorItem)cmbEvaluators.getSelectedItem();
		if (evaluatorItem == null || evaluatorItem.evaluator == null) return;
		Evaluator evaluator = evaluatorItem.evaluator;
		
		try {
			EvaluatorConfig config = evaluator.getConfig();
			if (!config.isReproduced()) {
				JOptionPane.showMessageDialog(
						this, 
						"Cannot remove non-reproduced evaluator", 
						"Evaluator removal fails", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
        	String evaluatorName = evaluator.getName();
        	String versionName = config.getReproducedVersion();
            boolean ret = false;
            
			if (account != null)
				ret = ((ServiceExt)service).removeEvaluator(evaluatorName, account.getName(), account.getPassword(), versionName);
			else if (service instanceof DefaultServiceExt)
				ret = ((DefaultServiceExt)service).removeEvaluator(evaluatorName, versionName);
			else
				ret = false;
			
            if (ret) {
        		JOptionPane.showMessageDialog(
    				this, 
    				"Success to reproduce evaluator '" + evaluatorName + "'", 
    				"Success to reproduce evaluator", 
    				JOptionPane.INFORMATION_MESSAGE);
        		
        		refresh();
        		return;
            }
		}
        catch (Exception e) {
        	LogUtil.trace(e);
        }
        
		JOptionPane.showMessageDialog(
			this, 
			"Fail to remove evaluator", 
			"Fail to remove evaluator", 
			JOptionPane.ERROR_MESSAGE);
	}
	
	
	@Override
	public synchronized void receivedEvaluator(EvaluatorEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		updateMode();
	}


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		checkConfigModified();
		
		unsetupListeners();
		
		if (exportedStub != null) {
			boolean ret = NetUtil.RegistryRemote.unexport(this);
			if (ret)
				LogUtil.info("Evaluator control panel unexported successfully");
			else
				LogUtil.info("Evaluator control panel unexported failedly");
			exportedStub = null;
		}

		super.dispose();
	}


	/**
	 * Checking whether configuration is modified.
	 */
	private void checkConfigModified() {
		if (paneConfig == null || !paneConfig.isModified()) return;
		
		int confirm = JOptionPane.showConfirmDialog(
			getThisEvaluatorCP(),
			"Evaluator properties are modified. Do you want to apply them?",
			"Evaluator properties are modified",
			JOptionPane.YES_NO_OPTION,
			JOptionPane.QUESTION_MESSAGE);
			
		if (confirm == JOptionPane.YES_OPTION)
			paneConfig.apply();
	}
	
	
	/**
	 * Setting up listeners.
	 */
	protected void setupListeners() {
		if (cmbEvaluators == null) return;
		
		for (int i = 0; i < cmbEvaluators.getItemCount(); i++) {
			EvaluatorItem item = cmbEvaluators.getItemAt(i);
			try {
				item.evaluator.addEvaluatorListener(this);
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
	}
	
	
	/**
	 * Unsetting up listeners.
	 */
	protected void unsetupListeners() {
		if (cmbEvaluators == null) return;
		
		for (int i = 0; i < cmbEvaluators.getItemCount(); i++) {
			EvaluatorItem item = cmbEvaluators.getItemAt(i);
			try {
				item.evaluator.removeEvaluatorListener(this);
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
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
		 * Item name.
		 */
		private String name = null;
		
		/**
		 * Evaluation GUI data.
		 */
		public EvaluateGUIData guiData = null;
		
		/**
		 * Constructor with evaluator.
		 * @param evaluator evaluator.
		 */
		public EvaluatorItem(Evaluator evaluator) {
			this.evaluator = evaluator;
			this.name = "";
			if (evaluator != null) {
				try {
					EvaluatorConfig config = evaluator.getConfig();
					if (config.isReproduced())
						name = evaluator.getName() + "-" + config.getReproducedVersion();
					else
						name = evaluator.getName();
				}
				catch (Exception e) {
					LogUtil.trace(e);
					name = "";
				}
			}
		}

		/**
		 * Getting item name.
		 * @return item name.
		 */
		public String getName() {
			return name;
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return DSUtil.shortenVerbalName(getName());
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
			validated = service.validateAccount(connectDlg.getUsername(), connectDlg.getPassword(), DataConfig.ACCOUNT_ADMIN_PRIVILEGE);
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
		if (!validated) {
			JOptionPane.showMessageDialog(
					null, "Account is not administrator", "Not administration account", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		EvaluatorCP ecp = null;
		ecp = new EvaluatorCP(service, connectDlg.getUsername(), connectDlg.getPassword(), connectDlg.getBindNamingUri().bindUri);
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
