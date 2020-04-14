/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ui.toolkit;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;

import net.hudup.core.data.SysConfig;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * Toolkit provides utility method for prcoessing dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetToolkit extends JFrame {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Dataset creator.
	 */
	protected DatasetCreator datasetCreator = null;

	
	/**
	 * Entering dataset.
	 */
	protected DatasetInput datasetInput = null;

	
	/**
	 * Exporting dataset.
	 */
	protected DatasetExporter datasetExporter = null;
	
	
	/**
	 * External importer for dataset
	 */
	protected ExternalImporter externalImporter = null;
	
	
	/**
	 * Making sparse dataset.
	 */
	protected DatasetSampling datasetSampling = null;
	
	
	/**
	 * Default toolkit.
	 */
	public DatasetToolkit() {
		super("Dataset tool");
		
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null);
		
        Image image = UIUtil.getImage("datatool-32x32.png");
        if (image != null)
        	setIconImage(image);
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosing(e);
				
				try {
					datasetCreator.dispose();
					datasetInput.dispose();
					datasetExporter.dispose();
					externalImporter.dispose();
					datasetSampling.dispose();
				}
				catch (Throwable ex) {
					ex.printStackTrace();
				}
			}
			
		});
		
		Container content = getContentPane();
		content.setLayout(new BorderLayout(2, 2));
		
		JTabbedPane body = new JTabbedPane();
		content.add(body, BorderLayout.CENTER);
		
		datasetCreator = new DatasetCreator(new SysConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public void reset() {
				// TODO Auto-generated method stub
			}
		});
		body.add("Dataset creator", datasetCreator);
		
		datasetInput = new DatasetInput();
		body.add("Dataset input", datasetInput);

		datasetExporter = new DatasetExporter();
		body.add("Dataset exporter", datasetExporter);
		
		externalImporter = new ExternalImporter();
		body.add("External importer", externalImporter);
		
		datasetSampling = new DatasetSampling();
		body.add("Dataset sampler", datasetSampling);
		
		setVisible(true);
	}
	
	
}





