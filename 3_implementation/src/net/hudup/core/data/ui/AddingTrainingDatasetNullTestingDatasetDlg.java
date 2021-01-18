/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui;

import java.awt.Component;
import java.util.List;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.logistic.xURI;

/**
 * This class shows a dialog to allow users to add a training dataset and a null testing dataset.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@Deprecated
public class AddingTrainingDatasetNullTestingDatasetDlg extends AddingDatasetDlg2 {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified dataset pool and algorithm list.
	 * @param comp parent component.
	 * @param pool specified dataset pool.
	 * @param algList specified algorithm list.
	 * @param mainUnit main unit.
	 */
	public AddingTrainingDatasetNullTestingDatasetDlg(Component comp, DatasetPool pool, List<Alg> algList,
			String mainUnit) {
		this(comp, pool, algList, mainUnit, null);
	}
	
	
	/**
	 * Constructor with specified dataset pool, algorithm list, and bind URI.
	 * @param comp parent component.
	 * @param pool specified dataset pool.
	 * @param algList specified algorithm list.
	 * @param mainUnit main unit.
	 * @param bindUri bound URI.
	 */
	public AddingTrainingDatasetNullTestingDatasetDlg(Component comp, DatasetPool pool, List<Alg> algList,
			String mainUnit, xURI bindUri) {
		super(comp, pool, algList, mainUnit, bindUri);
		
		chkTraining.setSelected(true);
		chkTesting.setSelected(false);
		chkWhole.setSelected(false);
		
		chkTraining.setVisible(false);
		chkTesting.setVisible(false);
		chkWhole.setVisible(false);
	}

	
}
