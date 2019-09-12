/**
 * 
 */
package net.hudup.data.ui;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import net.hudup.core.PluginStorage;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.ModelBasedRecommender;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.KBasePointer;
import net.hudup.core.data.NullPointer;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.xURI;
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
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e) && getDataset() != null) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
			
		});
	}

	
	/**
	 * Getting this text field.
	 * @return this text field.
	 */
	private DatasetTextField getThis() {
		return this;
		
	}

	
	/**
	 * Creating context menu.
	 * @return {@link JPopupMenu} as context menu.
	 */
	private JPopupMenu createContextMenu() {
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
			
			JMenuItem miMetadata = UIUtil.makeMenuItem((String)null, "View metadata", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						DatasetMetadataTable.showDlg(getThis(), dataset);
					}
				});
			contextMenu.add(miMetadata);

			JMenuItem miData = UIUtil.makeMenuItem((String)null, "View data", 
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
			contextMenu.add(miData);
		}
		else if (dataset instanceof KBasePointer) {
			if (!uriText.isEmpty()) contextMenu.addSeparator();

			JMenuItem miData = UIUtil.makeMenuItem((String)null, "View knowledge base", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						int confirm = JOptionPane.showConfirmDialog(
								getThis(), 
								"Be careful, out of memory in case of huge knowledge base", 
								"Out of memory in case of huge dataset", 
								JOptionPane.OK_CANCEL_OPTION, 
								JOptionPane.WARNING_MESSAGE);
						if (confirm != JOptionPane.OK_OPTION)
							return;
						
						try {
							DataConfig config = (DataConfig) dataset.getConfig().clone(); 
							xURI store = config.getStoreUri();
							xURI cfgUri = store.concat(KBase.KBASE_CONFIG);
							config.load(cfgUri);
							
							String kbaseName = config.getAsString(KBase.KBASE_NAME);
							if (kbaseName == null)
								throw new Exception("KBase not viewed");
							
							Alg alg = PluginStorage.getNormalAlgReg().query(kbaseName);
							if (alg == null || !(alg instanceof ModelBasedRecommender))
								throw new Exception("KBase not viewed");
							
							ModelBasedRecommender recommender = (ModelBasedRecommender) alg.newInstance();
							KBase kbase = recommender.newKBase(dataset);
							kbase.getInspector().inspect();
							kbase.close();
						}
						catch (Throwable ex) {
							ex.printStackTrace();
							JOptionPane.showMessageDialog(
									getThis(), 
									"KBase not viewed", 
									"KBase not viewed", 
									JOptionPane.ERROR_MESSAGE);
						} // end try-catch
							
					} //end actionPerformed
					
				}); //end ActionListener
			contextMenu.add(miData);
			
		}
		
		return contextMenu;
	}

	
	/**
	 * Setting dataset.
	 * @param dataset specified dataset.
	 */
	public void setDataset(Dataset dataset) {
		if (tag != null && tag instanceof Dataset)
			((Dataset)tag).clear();
		
		tag = dataset;
		if (dataset == null)
			setText("");
		else {
			xURI uriId = dataset.getConfig() != null ? dataset.getConfig().getUriId() : null;
			if (uriId == null) //Null pointer
				setText(NullPointer.NULL_POINTER);
			else
				setText(uriId.toString());
		}
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
