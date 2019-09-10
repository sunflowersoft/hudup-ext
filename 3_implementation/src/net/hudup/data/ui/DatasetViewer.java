package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata2;
import net.hudup.core.data.Provider;
import net.hudup.core.data.ui.ProfileTable;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.ProviderImpl;
import net.hudup.data.ctx.ui.CTSviewer;


/**
 * This class represents the viewer of dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetViewer extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Rating matrix name.
	 */
	protected final static String VIEW_RATING_MATRIX = "Rating matrix";
	
	/**
	 * User profile name.
	 */
	protected final static String VIEW_USER_PROFILE = "User profile";

	/**
	 * User external name.
	 */
	protected final static String VIEW_USER_EXTERNAL_RECORD = "User external";

	/**
	 * Item profile name.
	 */
	protected final static String VIEW_ITEM_PROFILE = "Item profile";

	/**
	 * Item external record name.
	 */
	protected final static String VIEW_ITEM_EXTERNAL_RECORD = "Item external";

	/**
	 * Context template name.
	 */
	protected final static String VIEW_CTS = "Context template";

	/**
	 * Sample name.
	 */
	protected final static String VIEW_SAMPLE = "Sample";

	
	/**
	 * Internal dataset.
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * Rating matrix panel
	 */
	private RatingMatrixPane paneRatingMatrix = null;
	
	/**
	 * User profile panel
	 */
	private JPanel paneUserProfile = null;

	/**
	 * User external panel
	 */
	private JPanel paneUserExternal = null;
	
	/**
	 * Item profile panel.
	 */
	private JPanel paneItemProfile = null;

	/**
	 * External item panel.
	 */
	private JPanel paneItemExternal = null;

	/**
	 * Context template schema panel.
	 */
	private JPanel paneCTS = null;
	
	/**
	 * Sample panel.
	 */
	private JPanel paneSample = null;
	
	
	/**
	 * Constructor with specified dataset.
	 * @param comp parent component.
	 * @param dataset specified dataset.
	 */
	public DatasetViewer(Component comp, Dataset dataset) {
		super(UIUtil.getFrameForComponent(comp), "Dataset viewer", true);
		
		this.dataset = dataset;
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 600);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosed(e);
				clear();
			}
			
		});
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel();
		add(header, BorderLayout.NORTH);
		
		JLabel datasetId = new JLabel("Dataset \"" + dataset.getConfig().getUriId() + "\"");
		header.add(datasetId);
		
		final JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		paneRatingMatrix = createRatingMatrixPane();
		paneUserProfile = createUserProfilePane();
		paneUserExternal = createUserExternalPane();
		paneItemProfile = createItemProfilePane();
		paneItemExternal = createItemExternalPane();
		paneCTS = createCTSPane();
		paneSample = createSamplePane();
		
		final JList<String> unitList = new JList<String>(new String[] {
			VIEW_RATING_MATRIX,
			VIEW_USER_PROFILE,
			VIEW_USER_EXTERNAL_RECORD,
			VIEW_ITEM_PROFILE,
			VIEW_ITEM_EXTERNAL_RECORD,
			VIEW_CTS,
			VIEW_SAMPLE
		});
		unitList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		unitList.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				
				String unit = unitList.getSelectedValue();
				body.removeAll();
				
				if (unit.equals(VIEW_RATING_MATRIX)) {
					body.add(paneRatingMatrix, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_USER_PROFILE)) {
					body.add(paneUserProfile, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_USER_EXTERNAL_RECORD)) {
					body.add(paneUserExternal, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_ITEM_PROFILE)) {
					body.add(paneItemProfile, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_ITEM_EXTERNAL_RECORD)) {
					body.add(paneItemExternal, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_CTS)) {
					body.add(paneCTS, BorderLayout.CENTER);
				}
				else if (unit.equals(VIEW_SAMPLE)) {
					body.add(paneSample, BorderLayout.CENTER);
				}
				
				//body.validate(); //Added date: 2019.08.22 by Loc Nguyen. This code line can be removed.
				body.updateUI();
			}
			
		});
		add(new JScrollPane(unitList), BorderLayout.WEST);

		JPanel footer = new JPanel(new BorderLayout());
		add(footer, BorderLayout.SOUTH);
		
		JLabel summary = new JLabel("Summary");
		footer.add(summary, BorderLayout.NORTH);

		//Added date: 2019.08.22 by Loc Nguyen.
		DatasetMetadata2Table tblMetadata2 = new DatasetMetadata2Table();
		DatasetMetadata2 metadata2 = DatasetMetadata2.create(dataset);
		tblMetadata2.update(metadata2);
		tblMetadata2.setPreferredScrollableViewportSize(new Dimension(50, 50));
		footer.add(new JScrollPane(tblMetadata2), BorderLayout.CENTER);
		
		JPanel buttons = new JPanel();
		footer.add(buttons, BorderLayout.SOUTH);
		
		JButton refresh = new JButton("Refresh");
		refresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				clear();
				
				paneRatingMatrix = createRatingMatrixPane();
				paneUserProfile = createUserProfilePane();
				paneUserExternal = createUserExternalPane();
				paneItemProfile = createItemProfilePane();
				paneItemExternal = createItemExternalPane();
				paneSample = createSamplePane();
				
				body.removeAll();
				unitList.getSelectionModel().addSelectionInterval(0, 0);
				
				//body.validate(); //Added date: 2019.08.22 by Loc Nguyen. This code line can be removed.
				body.updateUI();
			}
		});
		buttons.add(refresh);
		
		JButton export = new JButton("Export");
		export.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JDialog exportDlg = new JDialog(UIUtil.getFrameForComponent(comp), "Export", true);
				exportDlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				exportDlg.setSize(500, 300);
				exportDlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
				
				DatasetExporter exporter = new DatasetExporter();
				exportDlg.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
						// TODO Auto-generated method stub
						try {
							exporter.dispose();
						}
						catch (Throwable ex) {
							ex.printStackTrace();
						}
					}
					
				});
				exportDlg.add(exporter);
				
				exportDlg.setVisible(true);
			}
		});
		buttons.add(export);

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		buttons.add(close);
		
		
		body.removeAll();
		unitList.getSelectionModel().addSelectionInterval(0, 0);
		
		setVisible(true);
	}
	
	
	/**
	 * Create the rating matrix panel.
	 * @return rating matrix panel.
	 */
	private RatingMatrixPane createRatingMatrixPane() {
		return new RatingMatrixPane(dataset);
	}
	
	
	/**
	 * Create user profile panel.
	 * @return user profile panel.
	 */
	private JPanel createUserProfilePane() {
		JPanel main = new JPanel(new BorderLayout());
		
		UserProfileTable tblProfile = new UserProfileTable(true);
		tblProfile.update(dataset);
		main.add(new JScrollPane(tblProfile), BorderLayout.CENTER);

		return main;
	}
	

	/**
	 * Create user external record panel.
	 * @return user external record panel
	 */
	private JPanel createUserExternalPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		ExternalRecordTable tblExternalRecord = new ExternalRecordTable(true);
		tblExternalRecord.update(dataset);
		main.add(new JScrollPane(tblExternalRecord), BorderLayout.CENTER);
		
		return main;
	}


	/**
	 * Create item profile panel.
	 * @return item profile panel.
	 */
	private JPanel createItemProfilePane() {
		JPanel main = new JPanel(new BorderLayout());
		
		UserProfileTable tblProfile = new UserProfileTable(false);
		tblProfile.update(dataset);
		main.add(new JScrollPane(tblProfile), BorderLayout.CENTER);
		
		return main;
	}
	

	/**
	 * Creating item external panel.
	 * @return item external panel.
	 */
	private JPanel createItemExternalPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		ExternalRecordTable tblExternalRecord = new ExternalRecordTable(false);
		tblExternalRecord.update(dataset);
		main.add(new JScrollPane(tblExternalRecord), BorderLayout.CENTER);
		
		return main;
	}


	/**
	 * Creating context template schema viewer.
	 * @return context template schema viewer
	 */
	private JPanel createCTSPane() {
		JPanel main = new JPanel(new BorderLayout());
		
		CTSviewer ctsViewer = new CTSviewer(dataset.getCTSchema());
		main.add(new JScrollPane(ctsViewer), BorderLayout.CENTER);
		
		return main;
	}
	
	
	/**
	 * Creating sample viewer.
	 * @return sample viewer.
	 */
	private JPanel createSamplePane() {
		JPanel main = new JPanel(new BorderLayout());
		
		ProfileTable sampleViewer = new ProfileTable(dataset.fetchSample());
		main.add(new JScrollPane(sampleViewer), BorderLayout.CENTER);
		
		return main;
	}

	
	/**
	 * Clearing UI.
	 */
	private void clear() {
		if (paneRatingMatrix != null)
			paneRatingMatrix.clear();
	}
	
	
	/**
	 * This class shows a GUI which allows users to export dataset.
	 * @author Loc Nguyen
	 * @version 12.0
	 */
	private class DatasetExporter extends net.hudup.data.ui.toolkit.DatasetExporter {

		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default constructor.
		 */
		DatasetExporter() {
			super();
			btnSource.setVisible(false);
			
			txtSource.setConfig(dataset.getConfig());
			txtSource.setVisible(false);
		}
		
		@Override
		protected void enableControls(boolean flag) {
			DataConfig destConfig = (DataConfig) txtDestination.getConfig();
			boolean flag2 = destConfig != null;
			
			btnSource.setEnabled(flag);
			txtSource.setEnabled(flag);
			btnDestination.setEnabled(flag);
			txtDestination.setEnabled(flag);
			btnExport.setEnabled(flag && flag2);
			prgRunning.setEnabled(flag && flag2);
			
		}

		@Override
		protected void exportData() {
			// TODO Auto-generated method stub
			final DataConfig destConfig = (DataConfig) txtDestination.getConfig();
			
			if (destConfig == null) {
				JOptionPane.showMessageDialog(
						this, 
						"There is no configuration", 
						"No config", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			if (dataset.getConfig().getUriId().equals(destConfig.getUriId())) {
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
					// TODO Auto-generated method stub
					super.run();
					
					Provider provider = new ProviderImpl(destConfig);
					provider.importData(dataset, true, getThisExporter());
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
		
	}
	
	
}
