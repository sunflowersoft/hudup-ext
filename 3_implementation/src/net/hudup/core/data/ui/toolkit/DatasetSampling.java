/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui.toolkit;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetSampler;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.data.ui.DataConfigTextField;
import net.hudup.core.data.ui.UnitComboBox;
import net.hudup.core.data.ui.UnitTextField;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.ui.JCheckList;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.parser.TextParserUtil;

/**
 * Graphic user interface (GUI) allows users to sampling a unit (table, CSV,...) in dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetSampling extends JPanel implements ProgressListener, Dispose {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Configuration button.
	 */
	protected JButton btnConfig = null;

	/**
	 * Configuration text field.
	 */
	protected DataConfigTextField txtConfig = null;
	
	/**
	 * Unit text field.
	 */
	protected UnitTextField txtUnit = null;

	/**
	 * Test ratio text field.
	 */
	protected JTextField txtTestRatios = null;
	
	/**
	 * Folder number text field.
	 */
	protected JTextField txtFolderNumber = null;

	/**
	 * Sparse ratio text field.
	 */
	protected JTextField txtSparseRatios = null;
	
	/**
	 * Columns for making sparse.
	 */
	protected JCheckList<String> chkSparseColumns = null;
	
	/**
	 * Making sparse button.
	 */
	protected JButton btnSampling = null;
	
	/**
	 * Running progress bar.
	 */
	protected JProgressBar prgRunning = null;
	
	/**
	 * Running thread.
	 */
	protected volatile Thread runningThread = null;

	
	/**
	 * Default constructor.
	 */
	public DatasetSampling() {
		setLayout(new BorderLayout(5, 5));
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);

		JPanel header_left = new JPanel(new GridLayout(0, 1));
		header.add(header_left, BorderLayout.WEST);
		
		btnConfig = new JButton("Browse store");
		btnConfig.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				onConfig();
			}
		});
		header_left.add(btnConfig);
		
		header_left.add(new JLabel("Unit (*)"));
		header_left.add(new JLabel("Test ratios (*)"));
		header_left.add(new JLabel("Folder number"));
		header_left.add(new JLabel("Sparse ratios"));

		JPanel header_center = new JPanel(new GridLayout(0, 1));
		header.add(header_center, BorderLayout.CENTER);
		
		txtConfig = new DataConfigTextField();
		txtConfig.setEditable(false);
		header_center.add(txtConfig);
		
		txtUnit = new UnitTextField();
		txtUnit.setEditable(false);
		header_center.add(txtUnit);
		
		txtTestRatios = new JTextField("0.1, 0.5, 0.9");
		txtTestRatios.setEditable(true);
		header_center.add(txtTestRatios);

		txtFolderNumber = new JTextField("5"); //If the folder number is zero, the folder number is inverse of the test ratio.
		txtFolderNumber.setEditable(true);
		header_center.add(txtFolderNumber);

		txtSparseRatios = new JTextField("0.2, 0.4, 0.6, 0.8");
		txtSparseRatios.setEditable(true);
		header_center.add(txtSparseRatios);

		JPanel center = new JPanel(new BorderLayout());
		add(center, BorderLayout.CENTER);
		center.add(new JLabel("Sparse columns"), BorderLayout.WEST);
		chkSparseColumns = new JCheckList<String>();
		center.add(new JScrollPane(chkSparseColumns), BorderLayout.CENTER);
		
		JPanel center_toolbar = new JPanel();
		center.add(center_toolbar, BorderLayout.SOUTH);
		btnSampling = new JButton("Sample");
		btnSampling.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				sampling();
			}
		});
		center_toolbar.add(btnSampling);
		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		prgRunning = new JProgressBar();
		prgRunning.setStringPainted(true);
		prgRunning.setToolTipText("Importing progress");
		prgRunning.setVisible(false);
		prgRunning.setValue(0);
		footer.add(prgRunning, BorderLayout.SOUTH);
		
		enableControls(true);
	}
	
	
	/**
	 * Returning this sparse maker.
	 * @return this sparse maker.
	 */
	private DatasetSampling getThis() {
		return this;
	}
	
	
	/**
	 * Parsing a text into list of double values.
	 * @param text specified text.
	 * @return list of double values.
	 */
	private List<Double> parseRatios(String text) {
		List<Double> ratios = Util.newList();
		List<String> list = TextParserUtil.split(text, ",", null);
		if (list.size() == 0)
			return ratios;
		
		try {
			for (int i = 0; i < list.size(); i++)
				ratios.add(Double.parseDouble(list.get(i)));
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return ratios;
	}
	
	
	/**
	 * Configuring store and unit to be mad sparse.
	 */
	private void onConfig() {
		final DataConfig config = DatasetUtil2.chooseConfig(getThis(), null);
		
		if (config == null) {
			JOptionPane.showMessageDialog(
				getThis(), 
				"Not open training set", 
				"Not open training set", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		else
			txtConfig.setConfig(config);

		txtUnit.setUnit(null);
		StartDlg selectUnitDlg = new StartDlg((JFrame)null, "List of units") {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;
			
			@Override
			protected void start() {
				txtUnit.setUnit((Unit)cmbItem.getSelectedItem());
				dispose();
			}
			
			@Override
			protected JComboBox<?> createItemControl() {
				UnitComboBox unitComboBox = new UnitComboBox();
				unitComboBox.connectUpdate(config);
				return unitComboBox;
			}
			
			@Override
			protected TextArea createHelp() {
				return new TextArea("Choosing an unit.");
			}
			
			@Override
			public String getGuidedText() {
				return "You choose an unit and press \"Start\" button";
			}
		};
		selectUnitDlg.setSize(400, 150);
		selectUnitDlg.setVisible(true);

		if (txtUnit.getUnit() == null) {
			JOptionPane.showMessageDialog(
					getThis(), 
					"Null source unit", 
					"Null source unit", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		Provider provider = new ProviderImpl(config);
		AttributeList attributes = provider.getProfileAttributes(txtUnit.getUnit().getName());
		List<String> columns = Util.newList();
		for (int i = 0; i < attributes.size(); i++)
			columns.add(attributes.get(i).getName());
		chkSparseColumns.setListData(columns);
		provider.close();
		
		enableControls(true);
	}
	
	
	/**
	 * Sampling the input unit
	 */
	private void sampling() {
		if (txtConfig.getConfig() == null) {
			JOptionPane.showMessageDialog(
					getThis(), 
					"Empty configuration", 
					"Empty configuration", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (txtUnit.getUnit() == null) {
			JOptionPane.showMessageDialog(
					getThis(), 
					"Empty unit", 
					"Empty unit", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		List<Double> testRatiosTemp = parseRatios(txtTestRatios.getText());
		final List<Double> testRatios = Util.newList();
		for (double ratio : testRatiosTemp) {
			if (ratio <= 0 || ratio > 1) {
				JOptionPane.showMessageDialog(
					getThis(), 
					"Test ratio cannot be less than 0, equal to 0, or greater than 1", 
					"Invalid test ratio", 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			testRatios.add(ratio);
		}
		if (testRatios.size() == 0) {
			JOptionPane.showMessageDialog(
					getThis(), 
					"Empty test ratios", 
					"Empty test ratios", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		List<Double> folderNumberTemp = parseRatios(txtFolderNumber.getText());
		final int folderNumber;
		if (folderNumberTemp.size() == 0)
			folderNumber = 0; //When the folder number is zero, the folder number is inverse of the test ratio.
		else {
			folderNumber = folderNumberTemp.get(0).intValue();
			if (folderNumber < 0) {
				JOptionPane.showMessageDialog(
					getThis(), 
					"Folder number cannot be negative", 
					"Invalid folder number", 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
		}

		List<Double> sparseRatiosTemp = parseRatios(txtSparseRatios.getText());
		final List<Double> sparseRatios = Util.newList();
		for (double ratio : sparseRatiosTemp) {
			if (ratio < 0 || ratio > 1) {
				JOptionPane.showMessageDialog(
					getThis(), 
					"Sparse ratio cannot be less than or greater than 1", 
					"Invalid sparse ratio", 
					JOptionPane.ERROR_MESSAGE);
				return;
			}
			sparseRatios.add(ratio);
		}
		
		final List<Integer> sparseIndices = chkSparseColumns.getCheckedItemIndices();
		
		enableControls(false);
		prgRunning.setValue(0);
		prgRunning.setVisible(true);
		
		runningThread = new Thread() {

			@Override
			public void run() {
				try {
					DataConfig srcConfig = txtConfig.getConfig();
					String srcUnit = txtUnit.getUnit().getName();
					DatasetSampler sampler = new DatasetSampler(srcConfig);
					sampler.addProgressListener(getThis());
					for (double testRatio : testRatios) {
						sampler.split(srcUnit, testRatio, folderNumber);
					}
					prgRunning.setValue(0);
					
					if (sparseIndices.size() > 0 && sparseRatios.size() > 0) {
						UnitList unitList = sampler.getProvider().getUnitList();
						for (int i = 0; i < unitList.size(); i++) {
							String unit = unitList.get(i).getName();
							if (!unit.endsWith(DatasetSampler.TRAINING_SUFFIX))
								continue;
							
							for (double sparseRatio : sparseRatios) {
								prgRunning.setValue(0);
								sampler.makeSparse(unit, sparseRatio, sparseIndices);
							}
						}
					}
					
					JOptionPane.showMessageDialog(
							getThis(), 
							"Sampling dataset successfully", 
							"Sampling dataset successfully",
							JOptionPane.INFORMATION_MESSAGE);
					
//					int confirm = JOptionPane.showConfirmDialog(
//							getThis(), 
//							"Do you want to view these datasets?", 
//							"Dataset viewer",
//							JOptionPane.YES_NO_OPTION,
//							JOptionPane.QUESTION_MESSAGE);
//					if (confirm != JOptionPane.YES_OPTION) {
//						sampler.close();
//						return;
//					}
					
					sampler.close();
					
				}
				catch (Throwable e) {
					JOptionPane.showMessageDialog(
							getThis(), 
							"Sampling dataset failed",
							"Sampling dataset failed",
							JOptionPane.ERROR_MESSAGE);

					LogUtil.trace(e);
				}
				
				enableControls(true);
				prgRunning.setVisible(false);
				runningThread = null;
			}
			
		};
		
		runningThread.start();
	}
	
	
	/**
	 * Enable / disable controls.
	 * @param flag flag to enable controls.
	 */
	private void enableControls(boolean flag) {
		btnConfig.setEnabled(flag);
		txtConfig.setEnabled(flag);
		txtUnit.setEnabled(flag);
		txtSparseRatios.setEnabled(flag);
		chkSparseColumns.setEnabled(flag);
		btnSampling.setEnabled(flag);
		prgRunning.setEnabled(flag);
	}


	@Override
	public void receiveProgress(ProgressEvent evt) throws RemoteException {
		int progressTotal = evt.getProgressTotal();
		int progressStep = evt.getProgressStep();
		
		this.prgRunning.setMaximum(progressTotal);
		if (this.prgRunning.getValue() < progressStep) 
			this.prgRunning.setValue(progressStep);
		
		System.out.println(evt.getMsg());
	}

	
	@SuppressWarnings("deprecation")
	@Override
	public void dispose() {
		if (runningThread == null) return;
		
		try {
			if (runningThread != null && !runningThread.isInterrupted()) runningThread.interrupt();
		}
		catch (Throwable e) {LogUtil.error("Calling thread interrupt() causes error " + e.getMessage());}
		try {
			if (runningThread != null && SystemUtil.getJavaVersion() <= 15) runningThread.stop();
		}
		catch (Throwable e) {LogUtil.error("Calling thread stop() causes error " + e.getMessage());}
	}


	@Override
	public boolean isRunning() {
		return runningThread != null;
	}

	
}


