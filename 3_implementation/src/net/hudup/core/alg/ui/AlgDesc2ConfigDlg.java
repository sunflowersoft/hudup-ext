/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface (GUI) component shows extended algorithm description.
 * 
 * @author Loc Nguyen
 * @version 12.0
 */
public class AlgDesc2ConfigDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Extended algorithm description.
	 */
	protected AlgDesc2 algDesc = null;

	
	/**
	 * Information label.
	 */
	protected TextArea txtInfo = null;

	
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
	protected TextArea txtNote = null;

	
	/**
	 * Constructor with parent component and specified algorithm.
	 * @param comp parent component.
	 * @param algDesc specified extended algorithm description.
	 */
	public AlgDesc2ConfigDlg(final Component comp, final AlgDesc2 algDesc) {
		super(UIUtil.getFrameForComponent(comp), "Algorithm description", true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		JPanel paneInfo = new JPanel(new BorderLayout());
		add(paneInfo, BorderLayout.NORTH);
		txtInfo = new TextArea();
		txtInfo.setEditable(false);
		txtInfo.setText(algDesc.toString());
		txtInfo.setRows(3);
		paneInfo.add(new JScrollPane(txtInfo), BorderLayout.NORTH);
		
		
		paneCfg = new PropPane(); 
		paneCfg.setToolbarVisible(false);
		add(paneCfg, BorderLayout.CENTER);

		
		paneNote = new JPanel(new BorderLayout());
		add(paneNote, BorderLayout.SOUTH);
		paneNote.add(new JLabel("Note: "), BorderLayout.WEST);
		txtNote = new TextArea();
		txtNote.setEditable(false);
		txtNote.setRows(3);
		paneNote.add(new JScrollPane(txtNote), BorderLayout.CENTER);
		
		update(algDesc);
	}

	
	/**
	 * Updating this configuration dialog by specified extended algorithm configuration.
	 * @param algDesc specified extended algorithm configuration.
	 */
	public void update(AlgDesc2 algDesc) {
		this.setTitle("Description of algorithm '" + algDesc.algName + "'");
		this.algDesc = algDesc;
		paneCfg.update(algDesc.getConfig());
		paneCfg.setToolbarVisible(false);
		paneCfg.setControlVisible(false);
		paneCfg.setEnabled(false);
		
		paneNote.setVisible(false);
	}

	
	@Override
	public void setVisible(boolean flag) {
		if (flag && algDesc.getConfig().size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Configuration empty", 
					"Configuration empty", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		super.setVisible(flag);
	}


	/**
	 * Getting properties panel.
	 * @return properties panel.
	 */
	public PropPane getPropPane() {
		return this.paneCfg;
	}
	
	
}
