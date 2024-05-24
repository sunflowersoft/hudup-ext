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

import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import net.hudup.core.PluginStorage;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ProviderImpl;
import net.hudup.core.data.ui.DataConfigTextField;
import net.hudup.core.data.ui.DatasetConfigurator;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;

/**
 * This utility class allows users to export dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetExporter extends JPanel implements ProgressListener, Dispose {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Source button.
	 */
	protected JButton btnSource = null;

	/**
	 * Source text field.
	 */
	protected DataConfigTextField txtSource = null;
	
	/**
	 * Destination button.
	 */
	protected JButton btnDestination = null;
	
	/**
	 * Destination text field.
	 */
	protected DataConfigTextField txtDestination = null;
	
	/**
	 * Exporting button.
	 */
	protected JButton btnExport = null;
	
	/**
	 * Progressing bar.
	 */
	protected JProgressBar prgRunning = null;
	
	/**
	 * Running thread.
	 */
	protected volatile Thread runningThread = null;
	
	
	/**
	 * Default constructor.
	 */
	public DatasetExporter() {
		super();
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel header_up = new JPanel(new BorderLayout());
		header.add(header_up, BorderLayout.NORTH);

		JPanel header_up_left = new JPanel(new GridLayout(0, 1));
		header_up.add(header_up_left, BorderLayout.WEST);

		btnSource = new JButton("Source");
		btnSource.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				DatasetConfigurator chooser = new DatasetConfigurator(
						btnSource, 
						PluginStorage.getParserReg().getAlgList(),
						DataDriverList.get(),
						null);
				
				DataConfig config = chooser.getResultedConfig();
				if (config == null || config.size() == 0) {
					JOptionPane.showMessageDialog(
							getThisExporter(), 
							"Configuration empty", 
							"Configuration empty", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}

				txtSource.setConfig(config);
				enableControls(true);
			}
		});
		header_up_left.add(btnSource);

		
		btnDestination = new JButton("Destination");
		btnDestination.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				DatasetConfigurator chooser = new DatasetConfigurator(
						getThisExporter(),
						PluginStorage.getParserReg().getAlgList(),
						DataDriverList.get(),
						null);
				
				DataConfig config = chooser.getResultedConfig();
				if (config == null || config.size() == 0) {
					JOptionPane.showMessageDialog(
							getThisExporter(), 
							"Configuration empty", 
							"Configuration empty", 
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				txtDestination.setConfig(config);
				enableControls(true);
			}
		});
		header_up_left.add(btnDestination);

		JPanel header_up_center = new JPanel(new GridLayout(0, 1));
		header_up.add(header_up_center, BorderLayout.CENTER);
		
		txtSource = new DataConfigTextField();
		txtSource.setEditable(false);
		header_up_center.add(txtSource);
		
		txtDestination = new DataConfigTextField();
		txtDestination.setEditable(false);
		header_up_center.add(txtDestination);
		

		JPanel header_down = new JPanel();
		header.add(header_down, BorderLayout.SOUTH);
		
		btnExport = new JButton("Export");
		btnExport.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				
				exportData();
			}
		});
		header_down.add(btnExport);
		
		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		prgRunning = new JProgressBar();
		prgRunning.setStringPainted(true);
		prgRunning.setToolTipText("Exporting progress");
		prgRunning.setVisible(false);
		prgRunning.setValue(0);
		footer.add(prgRunning, BorderLayout.SOUTH);
		
		enableControls(true);
	}

	
	/**
	 * Getting this export GUI.
	 * @return this {@link DatasetExporter}
	 */
	protected DatasetExporter getThisExporter() {
		return this;
	}
	
	
	/**
	 * Enabling control according to flag.
	 * @param flag enable flag.
	 */
	protected void enableControls(boolean flag) {
		DataConfig srcConfig = (DataConfig) txtSource.getConfig();
		DataConfig destConfig = (DataConfig) txtDestination.getConfig();
		boolean flag2 = srcConfig != null && destConfig != null;
		
		btnSource.setEnabled(flag);
		txtSource.setEnabled(flag);
		btnDestination.setEnabled(flag);
		txtDestination.setEnabled(flag);
		btnExport.setEnabled(flag && flag2);
		prgRunning.setEnabled(flag && flag2);
		
	}
	
	
	/**
	 * Exporting data.
	 */
	protected void exportData() {
		final DataConfig srcConfig = (DataConfig) txtSource.getConfig();
		final DataConfig destConfig = (DataConfig) txtDestination.getConfig();
		
		if (srcConfig == null || destConfig == null) {
			JOptionPane.showMessageDialog(
					this, 
					"There is no configuration", 
					"No config", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		if (srcConfig.getUriId().equals(destConfig.getUriId())) {
			JOptionPane.showMessageDialog(
					this, 
					"Source and destination are the same place", 
					"Source and destination are the same place", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		

		destConfig.fillUnitList(DataConfig.getDefaultUnitList());
		
		
		enableControls(false);
		prgRunning.setValue(0);
		prgRunning.setVisible(true);
		runningThread = new Thread() {

			@Override
			public void run() {
				super.run();
				
				Provider provider = new ProviderImpl(destConfig);
				provider.importData(srcConfig, true, getThisExporter());
				provider.close();
				
				JOptionPane.showMessageDialog(
						getThisExporter(), 
						"Export successfully", 
						"Export successfully", 
						JOptionPane.INFORMATION_MESSAGE);
				
				enableControls(true);
				prgRunning.setVisible(false);
				
				runningThread = null;
			}
			
		};
		
		runningThread.start();
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

