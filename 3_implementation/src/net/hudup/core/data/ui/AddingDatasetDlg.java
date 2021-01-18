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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2.MethodType;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.Pointer;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.logistic.ui.WaitDialog;

/**
 * Graphic user interface (GUI) allows users to adding a pair of training dataset and testing dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public class AddingDatasetDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Dataset pool.
	 */
	protected DatasetPool pool = null;
	
	/**
	 * Algorithm list.
	 */
	protected List<Alg> algList = null;
	
	/**
	 * Button for browsing training dataset.
	 */
	protected JButton btnTrainingBrowse = null;

	/**
	 * Button for browsing testing dataset.
	 */
	protected JButton btnTestingBrowse = null;
	
	/**
	 * Button for browsing whole dataset.
	 */
	protected JButton btnWholeBrowse = null;
	
	/**
	 * Text field to show training dataset.
	 */
	protected DataConfigTextField txtTrainingBrowse = null;

	/**
	 * Text field to show testing dataset.
	 */
	protected DataConfigTextField txtTestingBrowse = null;
	
	/**
	 * Text field to show testing dataset.
	 */
	protected DataConfigTextField txtWholeBrowse = null;
	
	
	/**
	 * Bound URI.
	 */
	protected xURI bindUri = null;
	
	
	/**
	 * Constructor with specified dataset pool and algorithm list.
	 * @param comp parent component.
	 * @param pool specified dataset pool.
	 * @param algList specified algorithm list.
	 * @param mainUnit main unit.
	 */
	public AddingDatasetDlg(Component comp, DatasetPool pool, List<Alg> algList, String mainUnit) {
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
	public AddingDatasetDlg(Component comp, DatasetPool pool, List<Alg> algList, final String mainUnit, xURI bindUri) {
		super(UIUtil.getFrameForComponent(comp), "Add datasets", true);
		this.setTitle(I18nUtil.message("add_datasets"));
		this.pool = pool;
		this.algList = algList;
		this.bindUri = bindUri;
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(600, 200);
		this.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));

		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		
		btnTrainingBrowse = new JButton(I18nUtil.message("training_set"));
		btnTrainingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				openTrainingSet(mainUnit);
			}
		});
		left.add(btnTrainingBrowse);
		
		btnTestingBrowse = new JButton(I18nUtil.message("testing_set"));
		btnTestingBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				openTestingSet(mainUnit);
			}
		});
		left.add(btnTestingBrowse);

		btnWholeBrowse = new JButton(I18nUtil.message("whole_set"));
		btnWholeBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				openWholeSet(mainUnit);
			}
		});
		btnWholeBrowse.setVisible(false);
		left.add(btnWholeBrowse);

		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		
		txtTrainingBrowse = new DataConfigTextField();
		txtTrainingBrowse.setEditable(false);
		right.add(txtTrainingBrowse);
		
		txtTestingBrowse = new DataConfigTextField();
		txtTestingBrowse.setEditable(false);
		right.add(txtTestingBrowse);
		
		txtWholeBrowse = new DataConfigTextField();
		txtWholeBrowse.setEditable(false);
		txtWholeBrowse.setVisible(false);
		right.add(txtWholeBrowse);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnAdd = new JButton(I18nUtil.message("add_dataset"));
		btnAdd.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addDataset();
			}
		});
		footer.add(btnAdd);
		
		JButton btnClose = new JButton(I18nUtil.message("close"));
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		footer.add(btnClose);
		
		algChanged(); //Update GUI according to algorithm.
		
		//setVisible(true);
	}

	
	/**
	 * Change GUI according to algorithms. 
	 */
	private void algChanged() {
		if (this.algList == null || this.algList.size() == 0)
			return;
		
		MethodType type = AlgDesc2.methodTypeOf(this.algList);
		if (type == MethodType.memorybased)
			btnTrainingBrowse.setText(I18nUtil.message("training_set"));
		else if (type == MethodType.modelbased)
			btnTrainingBrowse.setText(I18nUtil.message("training_set_kbase"));
		else if (type == MethodType.composite)
			btnTrainingBrowse.setText(I18nUtil.message("any_source"));
		else if (type == MethodType.service)
			btnTrainingBrowse.setText(I18nUtil.message("service_pointer"));
		else
			btnTrainingBrowse.setText(I18nUtil.message("training_set"));
	}
	
	
	/**
	 * Open training set.
	 * @param mainUnit main unit.
	 */
	protected void openTrainingSet(String mainUnit) {
		
		DataConfig defaultCfg = txtTrainingBrowse.getConfig();
		MethodType type = AlgDesc2.methodTypeOf(this.algList);
		
		//Getting default configuration according to algorithm if possible.
		if (defaultCfg == null) {
			defaultCfg = type != MethodType.unknown ?
						DatasetUtil2.createDefaultConfig(this.algList.get(0))
						: new DataConfig();
		}
		
		if (mainUnit != null) {
			defaultCfg = (DataConfig)defaultCfg.clone();
			defaultCfg.setMainUnit(mainUnit);
		}
		
		//Getting default configuration according to algorithm if possible.
		DataConfig config = DatasetUtil2.chooseTrainingConfig(this, defaultCfg,
				type != MethodType.unknown ? this.algList.get(0) : null);
		
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Not open training set", 
				"Not open training set", 
				JOptionPane.ERROR_MESSAGE);
			
			return;
		}
	
		txtTrainingBrowse.setConfig(config);
	}
	
	
	/**
	 * Open testing set.
	 * @param mainUnit main unit.
	 */
	protected void openTestingSet(String mainUnit) {
		
		DataConfig defaultCfg = txtTestingBrowse.getConfig();
		if (defaultCfg == null)
			defaultCfg = new DataConfig();
		if (mainUnit != null) {
			defaultCfg = (DataConfig)defaultCfg.clone();
			defaultCfg.setMainUnit(mainUnit);
		}
		
		DataConfig config = DatasetUtil2.chooseTestingConfig(this, defaultCfg);
			
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Not open testing set", 
				"Not open testing set", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		txtTestingBrowse.setConfig(config);
	}
	
	
	/**
	 * Open testing set.
	 * @param mainUnit main unit.
	 */
	protected void openWholeSet(String mainUnit) {
		
		DataConfig defaultCfg = txtWholeBrowse.getConfig();
		if (defaultCfg == null)
			defaultCfg = new DataConfig();
		if (mainUnit != null) {
			defaultCfg = (DataConfig)defaultCfg.clone();
			defaultCfg.setMainUnit(mainUnit);
		}
		
		DataConfig config = DatasetUtil2.chooseWholeConfig(this, defaultCfg);
			
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Not open whole set", 
				"Not open whole set", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		txtWholeBrowse.setConfig(config);
	}

	
	/**
	 * Clearing text fields of configurations of training dataset and testing dataset.
	 */
	protected void clear() {
		txtTrainingBrowse.setConfig(null);
		txtTestingBrowse.setConfig(null);
		txtWholeBrowse.setConfig(null);
	}
	
	
	/**
	 * Adding a pair of training dataset and testing dataset. 
	 */
	protected void addDataset() {
		DataConfig trainingCfg = txtTrainingBrowse.getConfig();
		DataConfig testingCfg = txtTestingBrowse.getConfig();
		
		if (trainingCfg == null || testingCfg == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Can't add dataset because of empty dataset", 
					"Can't add dataset because of empty dataset", 
					JOptionPane.ERROR_MESSAGE);
			clear();
			return;
		}
		
		if (bindUri == null) {
			DatasetPair found = pool.findTrainingTesting(
					trainingCfg.getUriId(), 
					testingCfg.getUriId());
			
			if (found != null) {
				JOptionPane.showMessageDialog(
						this, 
						"Notice: Duplicated training/testing datasets", 
						"Duplication training/testing datasets", 
						JOptionPane.INFORMATION_MESSAGE);
			}
		}
		
		JDialog dlgWait1 = WaitDialog.createDialog(this); dlgWait1.setUndecorated(true);
		SwingWorker<Dataset, Dataset> worker1 = new SwingWorker<Dataset, Dataset>() {
			@Override
			protected Dataset doInBackground() throws Exception {
				return DatasetUtil.loadDataset(trainingCfg);
			}

			@Override
			protected void done() {
				super.done(); dlgWait1.dispose();
			}
		};
		worker1.execute(); dlgWait1.setVisible(true);
		Dataset trainingSet = null;
		try {
			trainingSet = worker1.get();
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

		
		JDialog dlgWait2 = WaitDialog.createDialog(this); dlgWait2.setUndecorated(true);
		SwingWorker<Dataset, Dataset> worker2 = new SwingWorker<Dataset, Dataset>() {
			@Override
			protected Dataset doInBackground() throws Exception {
				return DatasetUtil.loadDataset(testingCfg);
			}

			@Override
			protected void done() {
				super.done(); dlgWait2.dispose();
			}
		};
		worker2.execute(); dlgWait2.setVisible(true);
		Dataset testingSet = null;
		try {
			testingSet = worker2.get();
		} catch (Exception e) {LogUtil.trace(e);}
		if (testingSet == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Testing dataset is null", 
				"Invalid testing dataset", 
				JOptionPane.ERROR_MESSAGE);
			clear();
			return;
		}
		if (testingSet instanceof Pointer) {
			JOptionPane.showMessageDialog(
					this, 
					"Testing dataset is pointer", 
					"Invalid testing dataset", 
					JOptionPane.ERROR_MESSAGE);
			
			clear();
			return;
		}
		
		
		Dataset wholeSet = null;
		DataConfig wholeCfg = txtWholeBrowse.getConfig();
		if (wholeCfg != null) {
			wholeSet = DatasetUtil.loadDataset(wholeCfg);
			if (wholeSet == null) {
				JOptionPane.showMessageDialog(
						this, 
						"Whole dataset is null", 
						"Invalid whole dataset", 
						JOptionPane.WARNING_MESSAGE);
			}
			if (wholeSet instanceof Pointer) {
				JOptionPane.showMessageDialog(
						this, 
						"Whole dataset is pointer", 
						"Invalid whole dataset", 
						JOptionPane.ERROR_MESSAGE);
				
				wholeSet = null;
			}
		}
		

		DatasetPair pair = new DatasetPair(trainingSet, testingSet, wholeSet);
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
