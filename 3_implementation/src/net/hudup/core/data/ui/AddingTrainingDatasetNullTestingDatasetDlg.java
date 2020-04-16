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

import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.NullPointer;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.WaitDialog;

/**
 * This class shows a dialog to allow users to add a training dataset and a null testing dataset.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class AddingTrainingDatasetNullTestingDatasetDlg extends AddingDatasetDlg {

	
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
		// TODO Auto-generated constructor stub
		
		btnTestingBrowse.setVisible(false);
		txtTestingBrowse.setVisible(false);
	}

	
	@Override
	protected void addDataset() {
		DataConfig trainingCfg = txtTrainingBrowse.getConfig();
		
		if (trainingCfg == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Can't add dataset because of empty dataset", 
					"Can't add dataset because of empty dataset", 
					JOptionPane.ERROR_MESSAGE);
			clear();
			return;
		}
		
		if (bindUri == null) {
			DatasetPair found = pool.findTraining(trainingCfg.getUriId());
			if (found != null) {
				JOptionPane.showMessageDialog(
					this, 
					"Notice: Duplicated training/testing datasets", 
					"Duplication training/testing datasets", 
					JOptionPane.INFORMATION_MESSAGE);
			}
		}

		JDialog dlgWait = WaitDialog.createDialog(this); dlgWait.setUndecorated(true);
		SwingWorker<Dataset, Dataset> worker = new SwingWorker<Dataset, Dataset>() {
			@Override
			protected Dataset doInBackground() throws Exception {
				return DatasetUtil.loadDataset(trainingCfg);
			}

			@Override
			protected void done() {
				super.done(); dlgWait.dispose();
			}
		};
		worker.execute(); dlgWait.setVisible(true);
		Dataset trainingSet = null;
		try {
			trainingSet = worker.get();
		} catch (Exception e) {LogUtil.trace(e);}
		if (trainingSet == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Training dataset is null", 
				"Invalid training dataset", 
				JOptionPane.ERROR_MESSAGE);
			clear();
			return;
		}
		
		if (!DatasetUtil2.validateTrainingset(this, trainingSet, algList.toArray(new Alg[] { })) ) {
			trainingSet.clear();
			clear();
			return;
		}

		
		Dataset testingSet = new NullPointer();
		
		
		DatasetPair pair = new DatasetPair(trainingSet, testingSet, null);
		pool.add(pair);
		
		JOptionPane.showMessageDialog(
				this, 
				"Added successfully", 
				"Added successfully", 
				JOptionPane.INFORMATION_MESSAGE);

		clear();
		dispose();
	}


}
