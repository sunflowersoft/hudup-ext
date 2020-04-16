/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
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
import net.hudup.core.data.DatasetMetadata2;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.TextField;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This is Java table to show extended meta-data of dataset.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetMetadata2Table extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public DatasetMetadata2Table() {
		// TODO Auto-generated constructor stub
		super(new DatasetMetadata2TM());
	}

	
	/**
	 * Update this table with specified dataset meta-data.
	 * @param metadata2 specified extended dataset meta-data.
	 */
	public void update(DatasetMetadata2 metadata2) {
		getDatasetMetadata2TM().update(metadata2);
	}

	
	/**
	 * Getting table model of extended dataset meta-data.
	 * @return table model of extended dataset meta-data.
	 */
	public DatasetMetadata2TM getDatasetMetadata2TM() {
		return (DatasetMetadata2TM)getModel();
	}
	
	
	
	/**
	 * Showing dialog containing table of extended dataset meta-data.
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
		
		DatasetMetadata2Table tblMetadata2 = new DatasetMetadata2Table();
		DatasetMetadata2 metadata2 = DatasetMetadata2.create(dataset);
		tblMetadata2.update(metadata2);
		dlg.add(new JScrollPane(tblMetadata2), BorderLayout.CENTER);
		
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
 * This class implements table model of extended dataset meta-data.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class DatasetMetadata2TM extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public DatasetMetadata2TM() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Updating table model by extended dataset meta-data.
	 * @param metadata2 extended dataset meta-data.
	 */
	public void update(DatasetMetadata2 metadata2) {
		if (metadata2 == null) {
			clear();
			return;
		}
		
		setDataVector(new Object[][] {
				{"Min rating", MathUtil.round(metadata2.minRating)},
				{"Max rating", MathUtil.round(metadata2.maxRating)},
				{"Number of users", metadata2.numberOfUsers},
				{"Number of rating users", metadata2.numberOfRatingUsers},
				{"Number of items", metadata2.numberOfItems},
				{"Number of rated items", metadata2.numberOfRatedItems},
				{"Min rating count of a user", metadata2.userMinRatingCount},
				{"Min favorite rating count of a user", metadata2.userMinRelevantRatingCount},
				{"Max rating count of a user", metadata2.userMaxRatingCount},
				{"Max favorite rating count of a user", metadata2.userMaxRelevantRatingCount},
				{"Average rating count of a user", MathUtil.round(metadata2.userAverageRatingCount)},
				{"Average favorite rating count of a user", MathUtil.round(metadata2.userAverageRelevantRatingCount)},
				{"Min rating count of an item", metadata2.itemMinRatingCount},
				{"Min favorite rating count of an item", metadata2.itemMinRelevantRatingCount},
				{"Max rating count of an item", metadata2.itemMaxRatingCount},
				{"Max favorite rating count of an item", metadata2.itemMaxRelevantRatingCount},
				{"Average rating count of an item", MathUtil.round(metadata2.itemAverageRatingCount)},
				{"Average favorite rating count of an item", MathUtil.round(metadata2.itemAverageRelevantRatingCount)},
				{"Number of ratings", metadata2.numberOfRatings},
				{"Rating cover ratio", MathUtil.round(metadata2.ratingCoverRatio*100) + "%"},
				{"Rating mean", MathUtil.round(metadata2.ratingMean)},
				{"Rating standard deviation", MathUtil.round(metadata2.ratingSd)},
				{"Number of favorite ratings", metadata2.numberOfRelevantRatings},
				{"Rating favorite ratio", MathUtil.round(metadata2.ratingRelevantRatio*100) + "%"},
				{"Favorite rating mean", MathUtil.round(metadata2.relevantRatingMean)},
				{"Favorite rating standard deviation", MathUtil.round(metadata2.relevantRatingSd)},
				{"Sample row count", metadata2.sampleRowCount},
				{"Sample column count", metadata2.sampleColumnCount},
				{"Sample cell count", metadata2.sampleCellCount},
				{"Sample cover ratio", MathUtil.round(metadata2.sampleCoverRatio*100) + "%"},
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

