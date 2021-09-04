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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetUtil;
import net.hudup.core.data.DatasetUtil2;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This graphic user interface assists users to choose an dataset.
 *  
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public class SimpleDatasetChooser extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Browse button
	 */
	protected JButton btnBrowse = null;
	
	
	/**
	 * Browse text field.
	 */
	protected DatasetTextField txtBrowse = null;
	
	
	/**
	 * Constructor with file extensions and descriptions.
	 * @param comp parent component.
	 * @param title dialog title.
	 * @param exts file extensions.
	 * @param descs file descriptions.
	 * @param mode model mode or not.
	 */
	public SimpleDatasetChooser(Component comp, String title, 
			final String[] exts, final String[] descs, final int mode) {
		super(UIUtil.getDialogForComponent(comp), "Choose data configuration", true);
		
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.setSize(600, 300);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));

		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		JPanel left = new JPanel(new GridLayout(0, 1));
		header.add(left, BorderLayout.WEST);
		
		btnBrowse = new JButton("Dataset");
		btnBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				onBrowse();
			}
		});
		JPanel pane = new JPanel();
		pane.add(btnBrowse);
		left.add(pane);
		
		JPanel right = new JPanel(new GridLayout(0, 1));
		header.add(right, BorderLayout.CENTER);
		

		txtBrowse = new DatasetTextField();
		txtBrowse.setEditable(false);
		right.add(txtBrowse);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				
				onOK();
			}
		});
		footer.add(btnOK);
		
		JButton btnClose = new JButton("Cancel");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				txtBrowse.setDataset(null, true);
				dispose();
			}
		});
		footer.add(btnClose);
		
		setVisible(true);
	}
	
	
	/**
	 * Browsing dataset.
	 */
	private void onBrowse() {
		DataConfig config = DatasetUtil2.chooseConfig(this, null);
		
		if (config == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Dataset not open", 
					"Dataset not open", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		Dataset dataset = DatasetUtil.loadDataset(config);
		this.txtBrowse.setDataset(dataset, true);
	}
	
	
	/**
	 * Event-driven for OK button.
	 */
	private void onOK() {
		Dataset dataset = txtBrowse.getDataset();
		
		if (dataset == null) {
			JOptionPane.showMessageDialog(
					this, 
					"Dataset not open", 
					"Dataset not open", 
					JOptionPane.ERROR_MESSAGE);
			
			txtBrowse.setDataset(null, true);
			return;
		}
		
		
		dispose();
	}
	
	
	/**
	 * Getting dataset.
	 * @return {@link Dataset}.
	 */
	public Dataset getDataset() {
		return txtBrowse.getDataset();
	}
	
	
}
