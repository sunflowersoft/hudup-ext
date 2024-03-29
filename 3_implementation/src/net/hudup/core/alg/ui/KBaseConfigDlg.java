/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.hudup.core.alg.KBase;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface (GUI) component shows a dialog for users to configure knowledge base.
 * In other words, the configuration of knowledge base is edited.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class KBaseConfigDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Information label.
	 */
	protected JLabel lblInfo = null;

	
	/**
	 * The graphic user interface (GUI) component as panel shows a properties list.
	 */
	protected PropPane paneCfg = null;
	
	
	/**
	 * Note panel.
	 */
	protected JPanel paneNote = null;
	
	
	/**
	 * Note text area.
	 */
	TextArea txtNote = null;

	
	/**
	 * Constructor with parent component and specified knowledge base.
	 * @param comp parent component.
	 * @param kbase specified knowledge base whose configuration is edited.
	 */
	public KBaseConfigDlg(final Component comp, KBase kbase) {
		this(comp, kbase.getConfig());
	}

	
	/**
	 * Constructor with parent component and specified configuration of knowledge base.
	 * @param comp parent component.
	 * @param kbaseConfig specified configuration of knowledge base.
	 */
	public KBaseConfigDlg(final Component comp, DataConfig kbaseConfig) {
		super(UIUtil.getDialogForComponent(comp), "Knowledge base configuration", true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				if (paneCfg.getPropTable().isModified()) {
					int confirm = JOptionPane.showConfirmDialog(
							comp, 
							"Attributes are modified. Do you want to apply them?", 
							"Attributes are modified",
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					
					if (confirm == JOptionPane.YES_OPTION)
						paneCfg.apply();
				}
			}
			
		});
		
		
		JPanel paneInfo = new JPanel(new BorderLayout());
		add(paneInfo, BorderLayout.NORTH);
		lblInfo = new JLabel();
		paneInfo.add(lblInfo, BorderLayout.NORTH);

		
		paneCfg = new PropPane() {
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void customizeControls() {
				btnReset.setVisible(false);
			}

			@Override
			public void close() {
				// TODO Auto-generated method stub
				if (paneCfg.getPropTable().isModified()) {
					int confirm = JOptionPane.showConfirmDialog(
							comp, 
							"Attributes are modified. Do you want to apply them?", 
							"Attributes are modified", 
							JOptionPane.YES_NO_OPTION,
							JOptionPane.QUESTION_MESSAGE);
					
					if (confirm == JOptionPane.YES_OPTION)
						apply();
				}
				dispose();
			}

			@Override
			public boolean apply() {
				// TODO Auto-generated method stub
				boolean ret = super.apply();

				try {
					DataConfig kbaseCfg = ((DataConfig)getPropTable().getPropList());
					xURI storeUri = kbaseCfg.getStoreUri();
					xURI kbaseCfgUri = storeUri.concat(KBase.KBASE_CONFIG);
					ret = ret && kbaseCfg.save(kbaseCfgUri);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
					ret = ret && false;
				}
				
				return ret;
			}

		};
		add(paneCfg, BorderLayout.CENTER);

		
		paneNote = new JPanel(new BorderLayout());
		add(paneNote, BorderLayout.SOUTH);
		paneNote.add(new JLabel("Note: "), BorderLayout.WEST);
		txtNote = new TextArea();
		txtNote.setEditable(false);
		txtNote.setRows(3);
		paneNote.add(new JScrollPane(txtNote), BorderLayout.CENTER);
		
		update(kbaseConfig);
	}

	
	/**
	 * Updating this configuration dialog by specified configuration of knowledge base.
	 * @param kbaseConfig specified configuration of knowledge base.
	 */
	public void update(DataConfig kbaseConfig) {
		this.setTitle("Configure knowledge base '" + kbaseConfig.getAsString(KBase.KBASE_NAME) + "'");
		
		paneCfg.update(kbaseConfig);
		paneCfg.setToolbarVisible(false);
		
		lblInfo.setText("Configuration of knowledge base '" + kbaseConfig.getAsString(KBase.KBASE_NAME) + "'");
		paneNote.setVisible(false);
	}

	
	@Override
	public void setVisible(boolean flag) {
		if (flag && paneCfg.getPropTable().getPropList().size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Configuration empty", 
					"Configuration empty", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		super.setVisible(flag);
	}
	
	
}
