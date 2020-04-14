/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.hudup.core.alg.KBase;
import net.hudup.core.alg.ui.KBaseConfigDlg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.KBasePointer;
import net.hudup.core.data.KBasePointerImpl;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.ui.TagTextField;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * Text field for storing dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetTextField extends TagTextField {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public DatasetTextField() {
		super();
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Getting this text field.
	 * @return this text field.
	 */
	private DatasetTextField getThis() {
		return this;
		
	}

	
	@Override
	protected JPopupMenu createContextMenu() {
		if (getDataset() == null) return null;
		
		JPopupMenu contextMenu = new JPopupMenu();
		
		String uriText = getText();
		uriText = uriText == null ? "" : uriText;
		if (!uriText.isEmpty()) {
			JMenuItem miCopyURI = UIUtil.makeMenuItem((String)null, "Copy URL", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						ClipboardUtil.util.setText(getThis().getText());
					}
				});
			contextMenu.add(miCopyURI);
		}

		final Dataset dataset = getDataset();
		if (dataset == null) return contextMenu;
		
		if (!(dataset instanceof Pointer) ) {
			if (!uriText.isEmpty()) contextMenu.addSeparator();
			
			JMenuItem miViewMetadata = UIUtil.makeMenuItem((String)null, "View metadata", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						DatasetMetadataTable.showDlg(getThis(), dataset);
					}
				});
			contextMenu.add(miViewMetadata);

			JMenuItem miViewData = UIUtil.makeMenuItem((String)null, "View data", 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							int confirm = JOptionPane.showConfirmDialog(
									getThis(), 
									"Be careful, out of memory in case of huge dataset", 
									"Out of memory in case of huge dataset", 
									JOptionPane.OK_CANCEL_OPTION, 
									JOptionPane.WARNING_MESSAGE);
							
							if (confirm == JOptionPane.OK_OPTION)
								new DatasetViewer(getThis(), dataset);
						}
					});
			contextMenu.add(miViewData);
		}
		else if (dataset instanceof KBasePointer) {
			if (!uriText.isEmpty()) contextMenu.addSeparator();

			JMenuItem miViewKB = UIUtil.makeMenuItem((String)null, "View knowledge base", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						int confirm = JOptionPane.showConfirmDialog(
								getThis(), 
								"Be careful, out of memory in case of huge knowledge base", 
								"Out of memory in case of huge knowledge base", 
								JOptionPane.OK_CANCEL_OPTION, 
								JOptionPane.WARNING_MESSAGE);
						if (confirm != JOptionPane.OK_OPTION)
							return;
						
						KBase kbase = KBasePointerImpl.createKB(dataset);
						if (kbase == null) {
							JOptionPane.showMessageDialog(
									getThis(), 
									"KBase not viewed", 
									"KBase not viewed", 
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						kbase.getInspector().inspect();
						try {
							kbase.close();
						} catch (Throwable ex) {ex.printStackTrace();}
							
					} //end actionPerformed
					
				}); //end ActionListener
			contextMenu.add(miViewKB);
			
			JMenuItem miConfigKB = UIUtil.makeMenuItem((String)null, "Configure knowledge base", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						DataConfig kbaseConfig = KBasePointerImpl.loadKBaseConfig(dataset);
						if (kbaseConfig == null) {
							JOptionPane.showMessageDialog(
									getThis(), 
									"KBase not configured", 
									"KBase not configured", 
									JOptionPane.ERROR_MESSAGE);
							return;
						}
						
						KBaseConfigDlg dlgKBase = new KBaseConfigDlg(UIUtil.getFrameForComponent(getThis()), kbaseConfig);
						dlgKBase.setVisible(true);
					} //end actionPerformed
					
				}); //end ActionListener
			contextMenu.add(miConfigKB);
		}
		
		return contextMenu;
	}

	
	/**
	 * Setting dataset.
	 * @param dataset specified dataset.
	 */
	public void setDataset(Dataset dataset) {
		setDataset(dataset, true);
	}
	
	
	/**
	 * Setting dataset.
	 * @param dataset specified dataset.
	 * @param clearPreviousDataset true if clearing previous dataset. 
	 */
	public void setDataset(Dataset dataset, boolean clearPreviousDataset) {
		if (tag != null && clearPreviousDataset && tag instanceof Dataset)
			((Dataset)tag).clear();
		
		tag = dataset;
		setText(DatasetUtil.extractUriIdText(dataset));
	}
	
	
	/**
	 * Getting dataset.
	 * @return {@link Dataset}.
	 */
	public Dataset getDataset() {
		return (Dataset)tag;
	}
	
	
	/**
	 * Getting configuration.
	 * @return internal configuration represented by {@link DataConfig}.
	 */
	public DataConfig getConfig() {
		Dataset dataset = getDataset();
		if (dataset == null)
			return null;
		else
			return dataset.getConfig();
	}
	
	
}
