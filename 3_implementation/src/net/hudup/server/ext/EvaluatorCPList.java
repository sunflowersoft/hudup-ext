/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.Util;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.Connector;
import net.hudup.core.client.Service;
import net.hudup.core.client.ServiceExt;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.SysConfigPane;
import net.hudup.core.data.ui.toolkit.Dispose;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.server.ext.EvaluatorCP.EvaluatorItem;
import net.hudup.server.ext.ServiceNoticeEvent.Type;

/**
 * This class is advanced control panel for evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluatorCPList extends JFrame {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Table of evaluators.
	 */
	protected EvaluatorTable tblEvaluator = null;
	
	
    /**
     * Starting button.
     */
    protected JButton btnOpenStart = null;

    
    /**
     * Configuration button.
     */
    protected JButton btnConfig = null;

    
    /**
     * Adding button.
     */
    protected JButton btnAdd = null;
    
    
    /**
     * Deleting button.
     */
    protected JButton btnDelete = null;
    
    
    /**
     * Refreshing all evaluator button.
     */
    protected JButton btnRefreshAll = null;

    
    /**
     * Closing button.
     */
    protected JButton btnClose = null;

    
    /**
	 * Constructor with specified service.
	 * @param service specified service.
	 */
	public EvaluatorCPList(Service service) {
		this(service, null);
	}

	
	/**
	 * Constructor with specified service, account, password, and bound URI.
	 * @param service specified service.
	 * @param connectInfo connection information.
	 */
	public EvaluatorCPList(Service service, ConnectInfo connectInfo) {
		super("Evaluator control panel list");
		connectInfo = connectInfo != null ? connectInfo : (connectInfo = new ConnectInfo());
		tblEvaluator = new EvaluatorTable(service, connectInfo);
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null);
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null) setIconImage(image);
		
	    setJMenuBar(createMenuBar(service, connectInfo));

        addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}
			
		});
        
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        add(header, BorderLayout.NORTH);
        
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		body.add(new JScrollPane(tblEvaluator), BorderLayout.CENTER);

		JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		body.add(toolbar, BorderLayout.SOUTH);
		
		btnOpenStart = UIUtil.makeIconButton(
			"open-start-16x16.png", 
			"start", 
			I18nUtil.message("open"), 
			I18nUtil.message("open"), 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					open();
				}
			});
		btnOpenStart.setMargin(new Insets(0, 0 , 0, 0));
        toolbar.add(btnOpenStart);

	    btnConfig = UIUtil.makeIconButton(
			"config-16x16.png", 
			"config", 
			I18nUtil.message("config"), 
			I18nUtil.message("config"), 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					config();
				}
			});
	    btnConfig.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(btnConfig);

		btnAdd = UIUtil.makeIconButton(
			"add-16x16.png", 
			"add", 
			I18nUtil.message("add"), 
			I18nUtil.message("add"), 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					reproduce();
				}
			});
		btnAdd.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(btnAdd);
		
		btnDelete = UIUtil.makeIconButton(
			"delete-16x16.png", 
			"delete", 
			I18nUtil.message("delete"), 
			I18nUtil.message("delete"), 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					remove();
				}
			});
		btnDelete.setMargin(new Insets(0, 0 , 0, 0));
		toolbar.add(btnDelete);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		btnRefreshAll = new JButton("Refresh");
		btnRefreshAll.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refreshAll();
			}
		});
		footer.add(btnRefreshAll);

		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(btnClose);
		
		refreshAll();
	}

	
	/**
	 * Getting this evaluator control panel.
	 * @return this evaluator control panel.
	 */
	private EvaluatorCPList getThisEvaluatorCP() {
		return this;
	}
	
	
	/**
	 * Create menu bar.
	 * @param service specified service.
	 * @param connectInfo connection information.
	 * @return menu bar.
	 */
	protected JMenuBar createMenuBar(Service service, ConnectInfo connectInfo) {
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
					refreshAll();
				}
			});
		mniRefresh.setMnemonic('r');
		mniRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0));
		mnTool.add(mniRefresh);

		JMenuItem mniSwitch = new JMenuItem(
			new AbstractAction("Switch single mode") {
				
				/**
				 * Serial version UID for serializable class. 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public void actionPerformed(ActionEvent e) {
					getThisEvaluatorCP().dispose();
					EvaluatorCP ecp = new EvaluatorCP(service, connectInfo);
					ecp.setVisible(true);
				}
			});
		mniSwitch.setMnemonic('w');
		mniSwitch.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK));
		mnTool.add(mniSwitch);

		return mnBar;
	}

	
	/**
	 * Open evaluator.
	 */
	private void open() {
		int selectedRow = tblEvaluator.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "There is no selected evaluator", "No selected evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}
		EvaluatorCP.EvaluatorItem evItem = tblEvaluator.getModel2().getEvaluatorItem(selectedRow);
		if (evItem == null || evItem.evaluator == null) return;
		
		EvaluatorItem.openEvaluator(evItem, tblEvaluator.getModel2().connectInfo, this);
	}
	
	
	/**
	 * Configuring evaluator.
	 */
	private void config() {
		int selectedRow = tblEvaluator.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "There is no selected evaluator", "No selected evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}
		EvaluatorCP.EvaluatorItem evItem = tblEvaluator.getModel2().getEvaluatorItem(selectedRow);
		if (evItem == null || evItem.evaluator == null) return;
		
		tblEvaluator.config(evItem, selectedRow);
	}

	
	/**
	 * Reproduce evaluator.
	 */
	private void reproduce() {
		int selectedRow = tblEvaluator.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "There is no selected evaluator", "No selected evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}
		EvaluatorCP.EvaluatorItem evItem = tblEvaluator.getModel2().getEvaluatorItem(selectedRow);
		if (evItem == null || evItem.evaluator == null) return;
		
		tblEvaluator.reproduce(evItem, selectedRow);
	}
	
	
	/**
	 * Remove evaluator.
	 */
	private void remove() {
		int selectedRow = tblEvaluator.getSelectedRow();
		if (selectedRow < 0) {
			JOptionPane.showMessageDialog(this, "There is no selected evaluator", "No selected evaluator", JOptionPane.ERROR_MESSAGE);
			return;
		}
		EvaluatorCP.EvaluatorItem evItem = tblEvaluator.getModel2().getEvaluatorItem(selectedRow);
		if (evItem == null || evItem.evaluator == null) return;
		
		tblEvaluator.remove(evItem, selectedRow);
	}
	
	
	/**
	 * Refreshing evaluators.
	 */
	private void refreshAll() {
		tblEvaluator.update();
	}

		
	@Override
	public void dispose() {
		tblEvaluator.dispose();
		super.dispose();
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
		
		EvaluatorCPList ecp = new EvaluatorCPList(service, connector.getConnectInfo());
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



/**
 * Table to show evaluators.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class EvaluatorTable extends JTable implements EvaluatorListener, ServiceNoticeListener, Remote, Dispose {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Exported stub (EvaluatorListener).
	 */
	protected Remote exportedStub = null;

	
	/**
	 * Time stamp.
	 */
	protected long timestamp = 0;
	
	
	/**
	 * Constructor with specified service and connection information.
	 * @param service specified service.
	 * @param connectInfo specified connection information.
	 */
	public EvaluatorTable(Service service, ConnectInfo connectInfo) {
		super();
		
		timestamp = System.currentTimeMillis();
		if (connectInfo.bindUri != null) { //Evaluator is remote
			exportedStub = NetUtil.RegistryRemote.export(this, connectInfo.bindUri.getPort());
			if (exportedStub != null)
				LogUtil.info("Evaluator control panel (table) exported at port " + connectInfo.bindUri.getPort());
			else
				LogUtil.info("Evaluator control panel (table) failed to exported");
		}

		setModel(new EvaluatorTableModel(service, connectInfo, this));
		setAutoCreateRowSorter(true); 
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu == null) return;
					contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
		
		update();
		
		if ((!connectInfo.pullMode) && (service instanceof ServiceExt)) {
			try {
				((ServiceExt)service).addNoticeListener(this);
			} catch (Exception e) {LogUtil.trace(e);}
		}
	}
	
	
	/**
	 * Getting table model.
	 * @return table model.
	 */
	public EvaluatorTableModel getModel2() {
		return (EvaluatorTableModel)getModel();
	}

	
	/**
	 * Create context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu() {
		int selectedRow = getSelectedRow();
		if (selectedRow < 0) return null;
		EvaluatorCP.EvaluatorItem evItem = getModel2().getEvaluatorItem(selectedRow);
		if (evItem == null || evItem.evaluator == null) return null;
		
		JPopupMenu ctxMenu = new JPopupMenu();
		
		JMenuItem miOpen = new JMenuItem("Open");
		miOpen.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					EvaluatorItem.openEvaluator(evItem, getModel2().connectInfo, getEvaluatorTable());
				}
			});
		ctxMenu.add(miOpen);
		
		JMenuItem miConfig = new JMenuItem("Configure");
		miConfig.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					config(evItem, selectedRow);
				}
			});
		ctxMenu.add(miConfig);

		ctxMenu.addSeparator();
		
		JMenuItem miRefresh = new JMenuItem("Refresh");
		miRefresh.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					refresh(evItem, selectedRow);
				}
			});
		ctxMenu.add(miRefresh);

		JMenuItem miReproduce = new JMenuItem("Reproduce");
		miReproduce.addActionListener( 
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					reproduce(evItem, selectedRow);
				}
			});
		ctxMenu.add(miReproduce);

		try {
			if (evItem.evaluator.getConfig().isReproduced()) {
				JMenuItem miRemove = new JMenuItem("Remove");
				miRemove.addActionListener( 
					new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							remove(evItem, selectedRow);
						}
					});
				ctxMenu.add(miRemove);
			}
		} catch (Exception e) {LogUtil.trace(e);}
		
		return ctxMenu;
	}


	/**
	 * Configure evaluator.
	 * @param selectedRow selected row.
	 * @param evItem evaluator item.
	 */
	protected synchronized void config(EvaluatorCP.EvaluatorItem evItem, int selectedRow) {
		JDialog dlgConfig = new JDialog(UIUtil.getFrameForComponent(this), "Configure evaluator", true);
		dlgConfig.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgConfig.setSize(600, 400);
		dlgConfig.setLocationRelativeTo(UIUtil.getFrameForComponent(this));
		dlgConfig.setLayout(new BorderLayout());
		
		SysConfigPane paneConfig = new SysConfigPane() {

			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean apply() {
				boolean ret = super.apply();
				if (!ret) return false;
				if (getModel2().service == null) return ret;
				
				EvaluatorConfig config = (EvaluatorConfig) tblProp.getPropList();
				if (config == null) return ret;
				
				try {
					if (getModel2().connectInfo.bindUri != null) evItem.evaluator.setConfig(config);
				}
				catch (Exception e) {LogUtil.trace(e);}
				
				return true;
			}

			@Override
			public void close() {
				dlgConfig.dispose();
			}

		};
		try {
			paneConfig.setToolbarVisible(true);
			paneConfig.setEnabled(false);
			paneConfig.btnLoad.setEnabled(false);
			paneConfig.btnSave.setEnabled(false);
			if (!evItem.evaluator.remoteIsStarted()) {
				paneConfig.setEnabled(true);
				paneConfig.btnLoad.setEnabled(true);
				paneConfig.btnSave.setEnabled(true);
			}
			
			EvaluatorConfig config = evItem.evaluator.getConfig();
			if (config != null) {
				if (getModel2().connectInfo.bindUri != null) config.setSaveAbility(false);
				paneConfig.update(evItem.evaluator.getConfig());
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		dlgConfig.add(paneConfig, BorderLayout.CENTER);
		
		dlgConfig.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
				
				if (!paneConfig.isModified()) return;
				
				int confirm = JOptionPane.showConfirmDialog(
					paneConfig,
					"System properties are modified. Do you want to apply them?",
					"System properties are modified",
					JOptionPane.YES_NO_OPTION,
					JOptionPane.QUESTION_MESSAGE);
				
				if (confirm == JOptionPane.YES_OPTION) paneConfig.apply();
			}

		});

		try {
			String name = evItem.evaluator.getVersionName();
			dlgConfig.setTitle("Configure evaluator \"" + DSUtil.shortenVerbalName(name) + "\"");
		} catch (Exception e) {LogUtil.trace(e);}
		
		dlgConfig.setVisible(true);
	}
	
	
	/**
	 * Refreshing evaluator.
	 * @param selectedRow selected row.
	 * @param evItem evaluator item.
	 */
	protected synchronized void refresh(EvaluatorCP.EvaluatorItem evItem, int selectedRow) {
		setValueAt(EvaluatorTableModel.getStatusText(evItem.evaluator), selectedRow, 1);
	}

	
	/**
	 * Reproducing evaluator.
	 * @param selectedRow selected row.
	 * @param evItem evaluator item.
	 */
	protected synchronized void reproduce(EvaluatorCP.EvaluatorItem evItem, int selectedRow) {
		Service service = getModel2().service;
		ConnectInfo connectInfo = getModel2().connectInfo;
		if (!(service instanceof ServiceExt)) {
			JOptionPane.showMessageDialog(
				this, 
				"This service does not support to reproduce evaluator", 
				"Evaluator reproduction not supported", 
				JOptionPane.ERROR_MESSAGE);
        	return;
		}
		
		if (evItem == null || evItem.evaluator == null) return;
		Evaluator evaluator = evItem.evaluator;
		
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
				selectDlgNameDlg.dispose();
			}
		});
        footer.add(btnCancel);
        
        selectDlgNameDlg.setSize(400, 150);
        selectDlgNameDlg.setVisible(true);
        
        if (versionName.length() == 0) {
			JOptionPane.showMessageDialog(this, "Empty reproduced version", "Empty reproduced version", JOptionPane.ERROR_MESSAGE);
        	return;
        }
        
        try {
        	String evaluatorName = evaluator.getName();
        	
			if (connectInfo.account != null)
				evaluator = ((ServiceExt)service).getEvaluator(evaluatorName, connectInfo.account.getName(), connectInfo.account.getPassword(), versionName.toString());
			else if (service instanceof ExtendedService)
				evaluator = ((ExtendedService)service).getEvaluator(evaluatorName, versionName.toString());
			else
				evaluator = null;
        	
            if (evaluator != null) {
        		JOptionPane.showMessageDialog(
    				this, 
    				"Success to reproduce evaluator '" + EvaluatorAbstract.createVersionName(evaluatorName, evaluatorName) + "'", 
    				"Success to reproduce evaluator", 
    				JOptionPane.INFORMATION_MESSAGE);
        		
        		((DefaultTableModel)getModel()).addRow(EvaluatorTableModel.toRow(evaluator));
    			if (!EvaluatorAbstract.isPullModeRequired(evaluator) && !connectInfo.pullMode)
					evaluator.addEvaluatorListener(this);
    			
    			((ServiceExt)service).fireNoticeEvent(new ServiceNoticeEvent(this, Type.add_reproduced_evaluator, evaluatorName, versionName.toString(), timestamp));
        		return;
            }
        }
        catch (Exception e) {
        	LogUtil.trace(e);
        }
        
		JOptionPane.showMessageDialog(this, "Fail to reproduce evaluator", "Fail to reproduce evaluator", JOptionPane.ERROR_MESSAGE);
	}

	
	/**
	 * Adding row.
	 * @param evName evaluator name.
	 * @param evVersion evaluator version.
	 * @return whether adding row is successful.
	 */
	protected synchronized boolean addRow(String evName, String evVersion) {
		String name = EvaluatorAbstract.createVersionName(evName, evVersion);
		if (name == null) return false;
		int n = getRowCount(); if (n == 0) return false;
		
		for (int i = 0; i < n; i++) {
			try {
				Evaluator evaluator = getModel2().getEvaluatorItem(i).evaluator;
				if (evaluator.getVersionName().equals(name)) return false;
			} catch (Exception e) {LogUtil.trace(e);}
		}

		Service service = getModel2().service;
		ConnectInfo connectInfo = getModel2().connectInfo;
		if (!(service instanceof ServiceExt)) return false;

		Evaluator evaluator = null;
		try {
			if (connectInfo.account != null)
				evaluator = ((ServiceExt)service).getEvaluator(evName, connectInfo.account.getName(), connectInfo.account.getPassword(), evVersion);
			else if (service instanceof ExtendedService)
				evaluator = ((ExtendedService)service).getEvaluator(evName, evVersion);
			
            if (evaluator != null) {
        		((DefaultTableModel)getModel()).addRow(EvaluatorTableModel.toRow(evaluator));
    			if (!EvaluatorAbstract.isPullModeRequired(evaluator) && !connectInfo.pullMode)
					evaluator.addEvaluatorListener(this);
        		return true;
            }
			
		} catch (Exception e) {LogUtil.trace(e);}
		
		return false;
	}

	
	/**
	 * Removing evaluator.
	 * @param selectedRow selected row.
	 * @param evItem evaluator item.
	 */
	protected synchronized void remove(EvaluatorCP.EvaluatorItem evItem, int selectedRow) {
		Service service = getModel2().service;
		ConnectInfo connectInfo = getModel2().connectInfo;
		if (!(service instanceof ServiceExt)) {
			JOptionPane.showMessageDialog(
				this, 
				"This service does not support to remove reproduced evaluator", 
				"Evaluator reproduction not supported", 
				JOptionPane.ERROR_MESSAGE);
        	return;
		}
		
		if (evItem == null || evItem.evaluator == null) return;
		Evaluator evaluator = evItem.evaluator;
		
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
        		
				getModel2().removeRow(selectedRow);
				
    			((ServiceExt)service).fireNoticeEvent(new ServiceNoticeEvent(this, Type.remove_reproduced_evaluator, evaluatorName, versionName.toString(), timestamp));

    			return;
            }
		}
        catch (Exception e) {
        	LogUtil.trace(e);
        }
        
		JOptionPane.showMessageDialog(this, "Fail to remove evaluator", "Fail to remove evaluator", JOptionPane.ERROR_MESSAGE);
	}

	
	/**
	 * Remove row.
	 * @param evName evaluator name.
	 * @param evVersion evaluator version.
	 * @return whether adding row is successful.
	 */
	protected synchronized boolean removeRow(String evName, String evVersion) {
		String versionName = EvaluatorAbstract.createVersionName(evName, evVersion);
		if (versionName == null) return false;
		int n = getRowCount(); if (n == 0) return false;
		
		for (int i = 0; i < n; i++) {
			try {
				String name = getModel2().getEvaluatorItem(i).getName();
				if (name.equals(versionName)) {
					getModel2().removeRow(i);
					return true;
				}
			} catch (Exception e) {LogUtil.trace(e);}
		}

		return false;
	}
	
	
	/**
	 * Getting this evaluator table.
	 * @return this evaluator table.
	 */
	private EvaluatorTable getEvaluatorTable() {
		return this;
	}
	
	
	/**
	 * Clearing evaluator table.
	 */
	public synchronized void clear() {
		getModel2().clear();
	}
	
	
	/**
	 * Updating evaluator table.
	 */
	public synchronized void update() {
		getModel2().update();
	}
	
	
	@Override
	public synchronized void receivedEvaluator(EvaluatorEvent evt) throws RemoteException {
		int n = getRowCount(); if (n == 0) return;
		for (int i = 0; i < n; i++) {
			try {
				Evaluator evaluator = getModel2().getEvaluatorItem(i).evaluator;
				if (evaluator == evt.getEvaluator()) {
					setValueAt(EvaluatorTableModel.getStatusText(evaluator), i, 1);
					return;
				}
			} catch (Exception e) {LogUtil.trace(e);}
		}
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
	public void notify(ServiceNoticeEvent evt) throws RemoteException {
		if (evt.getTimestamp() == this.timestamp) return;
		String evaluatorName = evt.getEvaluatorName();
		String evaluatorVersion = evt.getEvaluatorVersion();
		
		switch (evt.getType()) {
		case add_reproduced_evaluator:
			addRow(evaluatorName, evaluatorVersion);
			break;
		case remove_reproduced_evaluator:
			removeRow(evaluatorName, evaluatorVersion);
			break;
		default:
			break;
		}
	}


	@Override
	public synchronized void dispose() {
		clear();
		
		EvaluatorTableModel m = getModel2();
		if ((!m.connectInfo.pullMode) && (m.service instanceof ServiceExt)) {
			try {
				((ServiceExt)m.service).removeNoticeListener(this);
			} catch (Exception e) {LogUtil.trace(e);}
		}

		if (exportedStub != null) {
			boolean ret = NetUtil.RegistryRemote.unexport(this);
			if (ret)
				LogUtil.info("Evaluator control panel (table) unexported successfully");
			else
				LogUtil.info("Evaluator control panel (table) unexported failedly");
			exportedStub = null;
		}
	}


	@Override
	public boolean isRunning() {
		return true;
	}


}



/**
 * This class is the model for evaluator table.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class EvaluatorTableModel extends DefaultTableModel {

	
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
	 * Evaluator listener.
	 */
	protected EvaluatorListener listener = null;
	
	
	/**
	 * Constructor with service and connection information.
	 * @param service specified service.
	 * @param connectInfo connection information.
	 * @param listener evaluator listener.
	 */
	public EvaluatorTableModel(Service service, ConnectInfo connectInfo, EvaluatorListener listener) {
		super();
		this.service = service;
		this.connectInfo = connectInfo;
		this.listener = listener;
	}

	
	/**
	 * Clearing this model.
	 */
	public void clear() {
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			try {
				Evaluator evaluator = ((EvaluatorCP.EvaluatorItem)getValueAt(i, 0)).evaluator;
				if (!EvaluatorAbstract.isPullModeRequired(evaluator) && !connectInfo.pullMode) {
					evaluator.removeEvaluatorListener(listener);
				}
			} catch (Exception e) {LogUtil.error("Clear table error: " + e.getMessage());}
		} 

		setDataVector(new Object[][] {}, new Object[] {});
	}

	
	/**
	 * Updating this model.
	 */
	public void update() {
		clear();
		
		List<Evaluator> evaluators = ExtendedService.getEvaluators(service, connectInfo);
		if (evaluators.size() == 0) return;
		
		Vector<Vector<Object>> data = Util.newVector();
		for (Evaluator evaluator : evaluators) {
			data.add(toRow(evaluator));
		}
		
		setDataVector(data, toColumns());
		
		for (Evaluator evaluator : evaluators) {
			if (!EvaluatorAbstract.isPullModeRequired(evaluator) && !connectInfo.pullMode) {
				try {
					evaluator.addEvaluatorListener(listener);
				} catch (Exception e) {LogUtil.trace(e);}
			}
		}
		
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		return false;
	}


	/**
	 * Getting evaluator items.
	 * @return list of evaluator items.
	 */
	public List<EvaluatorItem> getEvaluatorItems() {
		List<EvaluatorItem> evItems = Util.newList();
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			try {
				evItems.add((EvaluatorCP.EvaluatorItem)getValueAt(i, 0));
			} catch (Exception e) {LogUtil.trace(e);}
		} 
		
		return evItems;
	}
	
	
	/**
	 * Getting evaluator item at specified row.
	 * @param row specified row.
	 * @return evaluator item at specified row.
	 */
	public EvaluatorItem getEvaluatorItem(int row) {
		return (EvaluatorCP.EvaluatorItem)getValueAt(row, 0);
	}
	
	
	/**
	 * Getting evaluator status at specified row.
	 * @param row specified row.
	 * @return evaluator status at specified row.
	 */
	public String getEvaluatorStatus(int row) {
		return (String)getValueAt(row, 1);
	}

	
	/**
	 * Getting status text of specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return status text of specified evaluator.
	 */
	public static String getStatusText(Evaluator evaluator) {
		try {
			if (!evaluator.remoteIsStarted())
				return "stopped";
			else if (evaluator.remoteIsRunning())
				return "running...";
			else
				return "paused";
		}
		catch (Exception e) {LogUtil.trace(e);}

		return "unknown";
	}

	
	/**
	 * Converting evaluator to row.
	 * @param evaluator specified evaluator.
	 * @return row the contains evaluator.
	 */
	public static Vector<Object> toRow(Evaluator evaluator) {
		Vector<Object> row = Util.newVector();
		row.add(new EvaluatorCP.EvaluatorItem(evaluator));
		row.add(getStatusText(evaluator));
		return row;
	}
	
	
	/**
	 * Getting list of column names.
	 * @return list of column names.
	 */
	public static Vector<String> toColumns() {
		Vector<String> columns = Util.newVector();
		columns.add("Evaluator");
		columns.add("Status");
		return columns;
	}
	
	
}
