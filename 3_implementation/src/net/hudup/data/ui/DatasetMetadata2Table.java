package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata2;
import net.hudup.core.logistic.MathUtil;
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
		
		dlg.setSize(400, 200);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		dlg.setLayout(new BorderLayout());

		JLabel lblUriId = new JLabel("Metadata of dataset \"" + dataset.getConfig().getUriId() + "\"");
		dlg.add(lblUriId, BorderLayout.NORTH);
		
		DatasetMetadata2Table tblMetadata2 = new DatasetMetadata2Table();
		DatasetMetadata2 metadata2 = DatasetMetadata2.create(dataset);
		tblMetadata2.update(metadata2);
		dlg.add(new JScrollPane(tblMetadata2), BorderLayout.CENTER);
		
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
				{"Max rating count of a user", metadata2.userMaxRatingCount},
				{"Average rating count of a user", MathUtil.round(metadata2.userAverageRatingCount)},
				{"Min rating count of an item", metadata2.itemMinRatingCount},
				{"Max rating count of an item", metadata2.itemMaxRatingCount},
				{"Average rating count of an item", MathUtil.round(metadata2.itemAverageRatingCount)},
				{"Number of ratings", metadata2.numberOfRatings},
				{"Sparse ratio", MathUtil.round(metadata2.sparseRatio*100) + "%"},
				{"Rating mean", MathUtil.round(metadata2.ratingMean)},
				{"Rating standard deviation", MathUtil.round(metadata2.ratingSd)},
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
