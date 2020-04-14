/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetAbstract;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.TextField;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This is Java table to show meta-data of dataset.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetMetadataTable extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public DatasetMetadataTable() {
		// TODO Auto-generated constructor stub
		super(new DatasetMetadataTM());
	}

	
	/**
	 * Update this table with specified dataset meta-data.
	 * @param metadata specified dataset meta-data.
	 */
	public void update(DatasetMetadata metadata) {
		getDatasetMetadataTM().update(metadata);
	}

	
	/**
	 * Getting table model of dataset meta-data.
	 * @return table model of dataset meta-data.
	 */
	public DatasetMetadataTM getDatasetMetadataTM() {
		return (DatasetMetadataTM)getModel();
	}
	
	
	/**
	 * Showing dialog containing table of dataset meta-data.
	 * @param comp parent component.
	 * @param dataset specified dataset.
	 */
	public static void showDlg(Component comp, Dataset dataset) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Dataset metadata", true);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		
		dlg.setSize(400, 250);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		dlg.setLayout(new BorderLayout());

		TextField txtUriId = new TextField("Metadata of dataset \"" + dataset.getConfig().getUriId() + "\"");
		txtUriId.setEditable(false);
		txtUriId.setCaretPosition(0);
		dlg.add(txtUriId, BorderLayout.NORTH);
		
		DatasetMetadataTable tblMetadata = new DatasetMetadataTable();
		DatasetMetadata metadata = DatasetMetadata.create(dataset);
		tblMetadata.update(metadata);
		dlg.add(new JScrollPane(tblMetadata), BorderLayout.CENTER);
		
		TextArea info = new TextArea(3, 5);
		info.setEditable(false);
		dlg.add(new JScrollPane(info), BorderLayout.SOUTH);
		StringBuffer infoBuffer = new StringBuffer();
		if (dataset.getConfig() != null) {
			DataConfig config = dataset.getConfig();
			if (config.containsKey(DatasetAbstract.HARDWARE_ADDR_FIELD)) {
				infoBuffer.append("Hardware address: " + config.getAsString(DatasetAbstract.HARDWARE_ADDR_FIELD));
			}
			if (config.containsKey(DatasetAbstract.HOST_ADDR_FIELD)) {
				infoBuffer.append("\nHost address: " + config.getAsString(DatasetAbstract.HOST_ADDR_FIELD));
			}
		}
		info.setText(infoBuffer.toString());
		
		dlg.setVisible(true);
	}
	
	
}



/**
 * This class implements table model of dataset meta-data.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class DatasetMetadataTM extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public DatasetMetadataTM() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Updating table model by dataset meta-data.
	 * @param metadata dataset meta-data.
	 */
	public void update(DatasetMetadata metadata) {
		if (metadata == null) {
			clear();
			return;
		}
		
		setDataVector(new Object[][] {
				{"Min rating", metadata.minRating},
				{"Max rating", metadata.maxRating},
				{"Number of users", metadata.numberOfUsers},
				{"Number of rating users", metadata.numberOfRatingUsers},
				{"Number of items", metadata.numberOfItems},
				{"Number of rated items", metadata.numberOfRatedItems},
			}, 
			createColumns());
		
	}
	
	
	/**
	 * Clearing table model.
	 */
	public void clear() {
		
		setDataVector(new Object[][] {}, createColumns());
	}
	
	
	/**
	 * Creating column names.
	 * @return vector of column names.
	 */
	private Object[] createColumns() {
		return new Object[] { "Attribute", "Value"};
	}
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
