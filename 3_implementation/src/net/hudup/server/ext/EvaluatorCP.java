/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
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
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import net.hudup.core.Util;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.Connector;
import net.hudup.core.client.Service;
import net.hudup.core.client.ServiceExt;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.ui.EvaluatorWrapper;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.evaluate.ui.EvalCompoundGUI;

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
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = null;
	
	
	/**
	 * Exported stub (EvaluatorListener).
	 */
	protected Remote exportedStub = null;

	
	/**
	 * Status of current evaluator.
	 */
	protected JLabel lblStatus = null;
	
	
	/**
	 * Control for list of items.
	 */
    protected JComboBox<EvaluatorWrapper> cmbEvaluators = null;

    
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
		this(service, null);
	}
	
	
    /**
	 * Constructor with specified service, account, password, and bound URI.
	 * @param service specified service.
	 * @param connectInfo connection information.
	 */
	public EvaluatorCP(Service service, ConnectInfo connectInfo) {
		super("Evaluator control panel");
		this.service = service;
		this.connectInfo = connectInfo != null ? connectInfo : (connectInfo = new ConnectInfo());
		
		if (connectInfo.bindUri != null) { //Evaluator is remote
			this.exportedStub = NetUtil.RegistryRemote.export(this, connectInfo.bindUri.getPort());
			if (this.exportedStub != null)
				LogUtil.info("Evaluator control panel exported at port " + connectInfo.bindUri.getPort());
			else
				LogUtil.info("Evaluator control panel failed to exported");
		}

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null);
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null) setIconImage(image);
		
	    setJMenuBar(createMenuBar());

	    addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
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
		
        cmbEvaluators = new JComboBox<EvaluatorWrapper>();
        cmbEvaluators.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
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
					EvaluatorWrapper evaluatorItem = (EvaluatorWrapper)cmbEvaluators.getSelectedItem();
					EvalCompoundGUI.run(evaluatorItem, getThisEvaluatorCP().connectInfo, getThisEvaluatorCP());
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
				boolean ret = super.apply();
				if (!ret) return false;
				if (service == null) return ret;
				
				EvaluatorConfig config = (EvaluatorConfig) tblProp.getPropList();
				if (config == null) return ret;
				
				EvaluatorWrapper evaluatorItem = (EvaluatorWrapper) cmbEvaluators.getSelectedItem();
				if (evaluatorItem == null || evaluatorItem.evaluator == null) return ret;
				
				try {
					if (getThisEvaluatorCP().connectInfo.bindUri != null)
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
	 * Create menu bar.
	 * @return menu bar.
	 */
	protected JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu(I18nUtil.message("tool"));
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);
		
		JMenuItem mniRefresh = new JMenuItem(
			new AbstractAction(I18nUtil.message("refresh")) {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					refresh();
				}
			});
		mniRefresh.setMnemonic('r');
		mniRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnTool.add(mniRefresh);

		JMenuItem mniSwitch = new JMenuItem(
			new AbstractAction("Switch list mode") {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					getThisEvaluatorCP().dispose();
					EvaluatorCPList ecp = new EvaluatorCPList(service, connectInfo);
					ecp.setVisible(true);
				}
			});
		mniSwitch.setMnemonic('w');
		mniSwitch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniSwitch);

		return mnBar;
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
		
		EvaluatorWrapper evaluatorItem = (EvaluatorWrapper)cmbEvaluators.getSelectedItem();
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
				if (connectInfo.bindUri != null)
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
        List<Evaluator> evaluators = ExtendedService.getEvaluators(service, connectInfo);
        for  (Evaluator evaluator : evaluators) {
        	cmbEvaluators.addItem(new EvaluatorWrapper(evaluator));
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
        List<Evaluator> evaluators = ExtendedService.getEvaluators(service, connectInfo);
        EvaluatorWrapper selectedItem = null;
        for (Evaluator evaluator : evaluators) {
        	EvaluatorWrapper item = new EvaluatorWrapper(evaluator);
        	cmbEvaluators.addItem(item);
        	
        	if (service != null && service instanceof ExtendedService)
        		item.guiData = ((ExtendedService)service).getEvaluateGUIData(evaluator);
        	
        	if (item.getName().equals(EvaluatorAbstract.createVersionName(evaluatorName, reproducedVersion)))
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
		
		EvaluatorWrapper evaluatorItem = (EvaluatorWrapper)cmbEvaluators.getSelectedItem();
		if (evaluatorItem == null || evaluatorItem.evaluator == null) return;
		Evaluator evaluator = evaluatorItem.evaluator;
		
		JDialog reproducer = new JDialog(UIUtil.getDialogForComponent(this), "Reproduce evaluator", true);
		reproducer.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		reproducer.setLocationRelativeTo(this);
		
		reproducer.setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        reproducer.add(header, BorderLayout.NORTH);
		
        header.add(new JLabel("Type reproduced version"), BorderLayout.WEST);
        final JTextField txtVersion = new JTextField("" + new Date().getTime());
        header.add(txtVersion, BorderLayout.CENTER);
        
        final StringBuffer version = new StringBuffer();
        JPanel footer = new JPanel();
        reproducer.add(footer, BorderLayout.SOUTH);
        
        JButton btnOK = new JButton("OK");
        btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				String text = txtVersion.getText();
				if (text != null && !text.trim().isEmpty())
					version.append(text.trim());
				reproducer.dispose();
			}
		});
        footer.add(btnOK);
        
        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				reproducer.dispose();
			}
		});
        footer.add(btnCancel);
        
        reproducer.setSize(400, 150);
        reproducer.setVisible(true);
        
        if (version.length() == 0) {
			JOptionPane.showMessageDialog(this, "Empty reproduced version", "Empty reproduced version", JOptionPane.ERROR_MESSAGE);
        	return;
        }
        
        try {
        	String evaluatorName = evaluator.getName();
        	
			if (connectInfo.account != null)
				evaluator = ((ServiceExt)service).getEvaluator(evaluatorName, connectInfo.account.getName(), connectInfo.account.getPassword(), version.toString());
			else if (service instanceof ExtendedService)
				evaluator = ((ExtendedService)service).getEvaluator(evaluatorName, version.toString());
			else
				evaluator = null;
        	
            if (evaluator != null) {
        		JOptionPane.showMessageDialog(
    				this, 
    				"Success to reproduce evaluator '" + EvaluatorAbstract.createVersionName(evaluatorName, version.toString()) + "'", 
    				"Success to reproduce evaluator", 
    				JOptionPane.INFORMATION_MESSAGE);
        		
        		refresh(evaluatorName, version.toString());
        		return;
            }
        }
        catch (Exception e) {
        	LogUtil.trace(e);
        }
        
		JOptionPane.showMessageDialog(this, "Fail to reproduce evaluator", "Fail to reproduce evaluator", JOptionPane.ERROR_MESSAGE);
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
		
		EvaluatorWrapper evaluatorItem = (EvaluatorWrapper)cmbEvaluators.getSelectedItem();
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
            
			if (connectInfo.account != null)
				ret = ((ServiceExt)service).removeEvaluator(evaluatorName, connectInfo.account.getName(), connectInfo.account.getPassword(), versionName);
			else if (service instanceof ExtendedService)
				ret = ((ExtendedService)service).removeEvaluator(evaluatorName, versionName);
			else
				ret = false;
			
            if (ret) {
        		JOptionPane.showMessageDialog(
    				this, 
    				"Success to remove evaluator '" + EvaluatorAbstract.createVersionName(evaluatorName, versionName) + "'", 
    				"Success to remove evaluator", 
    				JOptionPane.INFORMATION_MESSAGE);
        		
        		refresh();
        		return;
            }
		}
        catch (Exception e) {
        	LogUtil.trace(e);
        }
        
		JOptionPane.showMessageDialog(this, "Fail to remove evaluator", "Fail to remove evaluator", JOptionPane.ERROR_MESSAGE);
	}
	
	
	@Override
	public synchronized void receivedEvaluator(EvaluatorEvent evt) throws RemoteException {
		updateMode();
	}


	@Override
	public boolean classPathContains(String className) throws RemoteException {
    	try {
    		Util.getPluginManager().loadClass(className, false);
    		return true;
    	} catch (Exception e) {}
    	
		return false;
	}


	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


	@Override
	public void dispose() {
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
			EvaluatorWrapper item = cmbEvaluators.getItemAt(i);
			if (item.addedListener) continue;
			
			if (!EvaluatorAbstract.isPullModeRequired(item.evaluator) && !connectInfo.pullMode) {
				try {
					item.evaluator.addEvaluatorListener(this);
				} catch (Exception e) {LogUtil.trace(e);}
				
				item.addedListener = true;
			}
		}
	}
	
	
	/**
	 * Unsetting up listeners.
	 */
	protected void unsetupListeners() {
		if (cmbEvaluators == null) return;
		
		for (int i = 0; i < cmbEvaluators.getItemCount(); i++) {
			EvaluatorWrapper item = cmbEvaluators.getItemAt(i);
			if (!item.addedListener) continue;
			
			try {
				item.evaluator.removeEvaluatorListener(this);
			} catch (Exception e) {LogUtil.trace(e);}
			
			item.addedListener = false;
		}
	}
	
	
	/**
	 * Starting evaluator control panel.
	 */
	public static void start() {
		final Connector connector = Connector.connect();
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null) connector.setIconImage(image);
        
		Service service = connector.getService();

		if (service == null) {
			JOptionPane.showMessageDialog(
				null, "Fail to retrieve service", "Fail to retrieve service", JOptionPane.ERROR_MESSAGE);
			return;
		}

		boolean validated = false;
		try {
			validated = service.validateAccount(connector.getConnectInfo().account.getName(), connector.getConnectInfo().account.getPassword(), DataConfig.ACCOUNT_ADMIN_PRIVILEGE);
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
		if (!validated) {
			JOptionPane.showMessageDialog(
					null, "Account is not administrator", "Not administration account", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		EvaluatorCP ecp = new EvaluatorCP(service, connector.getConnectInfo());
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
