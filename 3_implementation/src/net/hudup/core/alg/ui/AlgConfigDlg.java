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

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.alg.NoteAlg;
import net.hudup.core.data.ui.PropPane;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface (GUI) component shows a dialog for users to configure algorithm.
 * In other words, the configuration of the internal algorithm is edited.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class AlgConfigDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The internal algorithm which will be configured.
	 */
	protected Alg thisAlg = null;

	
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
	 * @param alg specified algorithm whose configuration is edited.
	 */
	public AlgConfigDlg(final Component comp, final Alg alg) {
		super(UIUtil.getFrameForComponent(comp), "Algorithm configuration", true);
		this.thisAlg = alg;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		setLayout(new BorderLayout());
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
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
		txtInfo = new TextArea();
		txtInfo.setEditable(false);
		txtInfo.setRows(3);
		paneInfo.add(new JScrollPane(txtInfo), BorderLayout.NORTH);
		
		
		paneCfg = new PropPane() {
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void close() {
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
			public void reset() {
				if (AlgDesc2.isRemote(thisAlg) && (thisAlg instanceof AlgRemoteWrapper)) {
					try {
						((AlgRemoteWrapper)thisAlg).queryConfig();
					} catch (Exception e) {LogUtil.trace(e);}
				}
				else
					thisAlg.resetConfig();
				
				update(thisAlg.getConfig());
				JOptionPane.showMessageDialog(
						comp, 
						"Apply successfully", 
						"Apply successfully", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			
			@Override
			public boolean apply() {
				return super.apply();
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
		
		update(alg);
	}

	
	/**
	 * Updating this {@link AlgConfigDlg} by specified algorithm.
	 * @param alg specified algorithm.
	 */
	public void update(Alg alg) {
		this.thisAlg = alg;
		this.paneCfg.update(alg.getConfig());
		
		//Added date: 2019.09.26 by Loc Nguyen
		try {
			AlgDesc2 algDesc = new AlgDesc2(alg);
			this.setTitle("Configure algorithm '" + algDesc.algName + "'");
			txtInfo.setText(algDesc.toString());
			if (algDesc.isRemote) {
				paneCfg.setToolbarVisible(false);
//				paneCfg.setControlVisible(false);
//				paneCfg.setEnabled(false);
			}
			else {
				paneCfg.setToolbarVisible(true);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}

		paneNote.setVisible(false);
		if (alg instanceof NoteAlg) {
			String note = ((NoteAlg)alg).note();
			txtNote.setText(note);
			txtNote.setToolTipText("Please pay attention to this algorithm note.");
			paneNote.setVisible(true);
		}
		else {
			txtNote.setText("");
			txtNote.setToolTipText(null);
		}
		
	}

	
	@Override
	public void setVisible(boolean flag) {
		if (flag && thisAlg.getConfig().size() == 0) {
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
