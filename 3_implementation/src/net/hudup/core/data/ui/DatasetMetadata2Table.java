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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JDialog;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetAbstract;
import net.hudup.core.data.DatasetMetadata2;
import net.hudup.core.logistic.ClipboardUtil;
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
		super(new DatasetMetadata2TM());
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
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
	 * Creating context menu.
	 * @return context menu.
	 */
	protected JPopupMenu createContextMenu() {
		DatasetMetadata2 metadata = getDatasetMetadata2TM().getDatasetMetadata();
		if (metadata == null) return null;
		
		JPopupMenu contextMenu = new JPopupMenu();
		
		JMenuItem miCopy = UIUtil.makeMenuItem((String)null, "Copy", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					ClipboardUtil.util.setText(metadata.toText2());
				}
			});
		contextMenu.add(miCopy);
		
		return contextMenu;
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
	 * Internal dataset meta-data.
	 */
	protected DatasetMetadata2 metadata = null;
	
	
	/**
	 * Default constructor.
	 */
	public DatasetMetadata2TM() {
		super();
	}


	/**
	 * Updating table model by extended dataset meta-data.
	 * @param metadata extended dataset meta-data.
	 */
	public void update(DatasetMetadata2 metadata) {
		this.metadata = metadata;

		if (metadata == null) {
			clear();
			return;
		}
		
		setDataVector(new Object[][] {
				{"Minimum rating", MathUtil.round(metadata.minRating)},
				{"Maximum rating", MathUtil.round(metadata.maxRating)},
				{"Relevant rating", MathUtil.round(metadata.relevantRating)},
				{"Number of users", metadata.numberOfUsers},
				{"Number of rating users", metadata.numberOfRatingUsers},
				{"Number of items", metadata.numberOfItems},
				{"Number of rated items", metadata.numberOfRatedItems},
				{"Min rating count of a user", metadata.userMinRatingCount},
				{"Min favorite rating count of a user", metadata.userMinRelevantRatingCount},
				{"Max rating count of a user", metadata.userMaxRatingCount},
				{"Max favorite rating count of a user", metadata.userMaxRelevantRatingCount},
				{"Average rating count of a user", MathUtil.round(metadata.userAverageRatingCount)},
				{"Average favorite rating count of a user", MathUtil.round(metadata.userAverageRelevantRatingCount)},
				{"Min rating count of an item", metadata.itemMinRatingCount},
				{"Min favorite rating count of an item", metadata.itemMinRelevantRatingCount},
				{"Max rating count of an item", metadata.itemMaxRatingCount},
				{"Max favorite rating count of an item", metadata.itemMaxRelevantRatingCount},
				{"Average rating count of an item", MathUtil.round(metadata.itemAverageRatingCount)},
				{"Average favorite rating count of an item", MathUtil.round(metadata.itemAverageRelevantRatingCount)},
				{"Number of ratings", metadata.numberOfRatings},
				{"Rating cover ratio", MathUtil.round(metadata.ratingCoverRatio*100) + "%"},
				{"Rating mean", MathUtil.round(metadata.ratingMean)},
				{"Rating standard deviation", MathUtil.round(metadata.ratingSd)},
				{"Number of favorite ratings", metadata.numberOfRelevantRatings},
				{"Rating favorite ratio", MathUtil.round(metadata.ratingRelevantRatio*100) + "%"},
				{"Favorite rating mean", MathUtil.round(metadata.relevantRatingMean)},
				{"Favorite rating standard deviation", MathUtil.round(metadata.relevantRatingSd)},
				{"Sample row count", metadata.sampleRowCount},
				{"Sample column count", metadata.sampleColumnCount},
				{"Sample cell count", metadata.sampleCellCount},
				{"Sample cover ratio", MathUtil.round(metadata.sampleCoverRatio*100) + "%"},
			}, 
			createColumns());
		
	}
	
	
	/**
	 * Getting dataset meta-data.
	 * @return dataset meta-data.
	 */
	public DatasetMetadata2 getDatasetMetadata() {
		return metadata;
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
		return false;
	}
	
	
}

