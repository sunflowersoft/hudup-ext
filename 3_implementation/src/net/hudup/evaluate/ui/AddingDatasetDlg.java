package net.hudup.evaluate.ui;

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

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc;
import net.hudup.core.alg.AlgDesc.MethodologyType;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.Pointer;
import net.hudup.core.data.ui.DataConfigTextField;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.DatasetUtil2;


/**
 * Graphic user interface (GUI) allows users to adding a pair of training dataset and testing dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
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
	 * Text field to show training dataset.
	 */
	protected DataConfigTextField txtTrainingBrowse = null;

	/**
	 * Text field to show testing dataset.
	 */
	protected DataConfigTextField txtTestingBrowse = null;
	
	
	/**
	 * Constructor with specified dataset pool and algorithm list.
	 * @param comp parent component.
	 * @param pool specified dataset pool.
	 * @param algList specified algorithm list.
	 * @param mainUnit main unit.
	 */
	public AddingDatasetDlg(Component comp, DatasetPool pool, List<Alg> algList, String mainUnit) {
		super(UIUtil.getFrameForComponent(comp), "Add datasets", true);
		this.setTitle(I18nUtil.message("add_datasets"));
		this.pool = pool;
		this.algList = algList;
		
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

		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		

		txtTrainingBrowse = new DataConfigTextField();
		txtTrainingBrowse.setEditable(false);
		right.add(txtTrainingBrowse);
		
		txtTestingBrowse = new DataConfigTextField();
		txtTestingBrowse.setEditable(false);
		right.add(txtTestingBrowse);
		
		
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
		
		setVisible(true);
	}

	
	/**
	 * Change GUI according to algorithms. 
	 */
	private void algChanged() {
		if (this.algList == null || this.algList.size() == 0)
			return;
		
		MethodologyType type = AlgDesc.getTypeOf(this.algList);
		if (type == MethodologyType.memorybased)
			btnTrainingBrowse.setText(I18nUtil.message("training_set"));
		else if (type == MethodologyType.modelbased)
			btnTrainingBrowse.setText(I18nUtil.message("training_set_kbase"));
		else if (type == MethodologyType.composite)
			btnTrainingBrowse.setText(I18nUtil.message("any_source"));
		else if (type == MethodologyType.service)
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
		MethodologyType type = AlgDesc.getTypeOf(this.algList);
		
		//Getting default configuration according to algorithm if possible.
		if (defaultCfg == null) {
			defaultCfg = type != MethodologyType.unknown ?
						DatasetUtil2.createDefaultConfig(this.algList.get(0))
						: new DataConfig();
		}
		
		if (mainUnit != null) {
			defaultCfg = (DataConfig)defaultCfg.clone();
			defaultCfg.setMainUnit(mainUnit);
		}
		
		//Getting default configuration according to algorithm if possible.
		DataConfig config = DatasetUtil2.chooseTrainingConfig(this, defaultCfg,
				type != MethodologyType.unknown ? this.algList.get(0) : null);
		
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
	 * Clearing text fields of configurations of training dataset and testing dataset.
	 */
	protected void clear() {
		txtTrainingBrowse.setConfig(null);
		txtTestingBrowse.setConfig(null);
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
		
		DatasetPair found = pool.findTrainingTesting(
				trainingCfg.getUriId(), 
				testingCfg.getUriId());
		
		if (found != null) {
			JOptionPane.showMessageDialog(
					this, 
					"Can't add dataset because of duplication", 
					"Can't add dataset because of duplication", 
					JOptionPane.ERROR_MESSAGE);
			clear();
			return;
		}
		
		Dataset trainingSet = DatasetUtil.loadDataset(trainingCfg);
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

		
		Dataset testingSet = DatasetUtil.loadDataset(testingCfg);
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
