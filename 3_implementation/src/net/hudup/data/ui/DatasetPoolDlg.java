/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.data.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetPair;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.data.BatchScript;
import net.hudup.data.DatasetUtil2;
import net.hudup.evaluate.ui.AddingDatasetDlg;

/**
 * This class is the dialog to process dataset pool.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public class DatasetPoolDlg extends JDialog {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of algorithms.
	 */
	protected List<Alg> algList = Util.newList();
	
	/**
	 * Dataset pool.
	 */
	protected DatasetPool pool = null;
	
	/**
	 * Main unit.
	 */
	protected String mainUnit = null;
	
	
	/**
	 * Table to show dataset pool.
	 */
	protected DatasetPoolTable tblDatasetPool = null;
	
	
	/**
	 * Refresh button.
	 */
	protected JButton btnRefresh = null;
	
	/**
	 * Clear button.
	 */
	protected JButton btnClear = null;
	
	/**
	 * Adding dataset button.
	 */
	protected JButton btnAddDataset = null;
	
	/**
	 * Loading batch script button.
	 */
	protected JButton btnLoadBatchScript = null;
	
	/**
	 * Closing dialog.
	 */
	protected JButton btnClose = null;

	
	/**
	 * Bound URI.
	 */
	protected xURI bindUri = null;
	
	
	/**
	 * Constructor with dataset pool, algorithm list, and main unit.
	 * @param comp parent component.
	 * @param pool dataset pool.
	 * @param algList algorithm list.
	 * @param mainUnit main unit.
	 * @param bindUri bound URI.
	 */
	public DatasetPoolDlg(Component comp, DatasetPool pool, List<Alg> algList, String mainUnit) {
		this(comp, pool, algList, mainUnit, null);
	}
	
	
	/**
	 * Constructor with dataset pool, algorithm list, main unit, and bound URI.
	 * @param comp parent component.
	 * @param pool dataset pool.
	 * @param algList algorithm list.
	 * @param mainUnit main unit.
	 * @param bindUri bound URI.
	 */
	public DatasetPoolDlg(Component comp, DatasetPool pool, List<Alg> algList, String mainUnit, xURI bindUri) {
		super(UIUtil.getFrameForComponent(comp), "Dataset pool dialog", true);
		this.pool = pool;
		this.algList = algList;
		this.mainUnit = mainUnit;
		this.bindUri = bindUri;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosed(WindowEvent e) {
				// TODO Auto-generated method stub
				super.windowClosed(e);
			}
			
		});
		
		setLayout(new BorderLayout());
		JPanel header = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		add(header, BorderLayout.NORTH);
		
		this.btnRefresh = UIUtil.makeIconButton(
				"refresh-16x16.png", 
				"refresh", 
				"Refresh", 
				"Refresh", 
					
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						refresh();
					}
				});
		this.btnRefresh.setMargin(new Insets(0, 0 , 0, 0));
		header.add(this.btnRefresh);
		
		this.btnClear = UIUtil.makeIconButton(
			"clear-16x16.png", 
			"clear", 
			"Clear", 
			"Clear", 
				
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					clear();
				}
			});
		this.btnClear.setMargin(new Insets(0, 0 , 0, 0));
		header.add(this.btnClear);

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		this.tblDatasetPool = new DatasetPoolTable(true) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean removeSelectedRows() {
				// TODO Auto-generated method stub
				boolean ret = super.removeSelectedRows();
				if (ret)
					updateGUI();
				return ret;
			}
			
			@Override
			public void save() {
		        saveBatchScript();
			}
			
		};
		this.tblDatasetPool.update(this.pool);
		body.add(new JScrollPane(this.tblDatasetPool), BorderLayout.CENTER);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		this.btnLoadBatchScript = new JButton("Load script");
		this.btnLoadBatchScript.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				loadBatchScript();
			}
		});
		
		footer.add(this.btnLoadBatchScript);
		
		this.btnAddDataset = new JButton("Add dataset");
		this.btnAddDataset.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				addDataset();
			}
		});
		footer.add(this.btnAddDataset);
		
		this.btnClose = new JButton("Close");
		this.btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});
		footer.add(this.btnClose);
	}


	/**
	 * Refreshing pool.
	 */
	protected void refresh() {
		this.pool.reload();
		this.tblDatasetPool.update(this.pool);
		
		updateGUI();
	}
	

	/**
	 * Clear pool.
	 */
	protected void clear() {
		this.pool.clear();
		this.tblDatasetPool.update(this.pool);
		
		updateGUI();
	}

	
	/**
	 * Adding dataset;
	 */
	private void addDataset() {
		new AddingDatasetDlg(this, this.pool, this.algList, this.mainUnit, bindUri);
		this.tblDatasetPool.update(this.pool);
		updateGUI();
	}
	
	
	/**
	 * Load batch script.
	 */
	protected void loadBatchScript() {
		if (this.algList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithms list empty",
					"Batch script not suitable", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		UriAdapter adapter = new UriAdapter();
		xURI uri = adapter.chooseUri(
				this, 
				true, 
				new String[] {"properties", "script", "hudup"}, 
				new String[] {"Properties files", "Script files", "Hudup files"},
				null,
				null);
		adapter.close();
		
		if (uri == null) {
			JOptionPane.showMessageDialog(
					this, 
					"URI not open", 
					"URI not open", 
					JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			adapter = new UriAdapter(uri);
			Reader reader = adapter.getReader(uri);
			BatchScript script = BatchScript.parse(reader, this.mainUnit);
			reader.close();
			adapter.close();
			
			if (script == null) {
				JOptionPane.showMessageDialog(
						this, 
						"Batch not created", 
						"Batch not created", 
						JOptionPane.ERROR_MESSAGE);
				return;
			}

			this.pool.clear();
			DatasetPool scriptPool = script.getPool();
			for (int i = 0; i < scriptPool.size(); i++) {
				DatasetPair pair = scriptPool.get(i);
				if (pair == null || pair.getTraining() == null)
					continue;
				
				Dataset trainingSet = pair.getTraining();
				if (DatasetUtil2.validateTrainingset(this, trainingSet, algList.toArray(new Alg[] {})));
					this.pool.add(pair);
			}
			this.tblDatasetPool.update(this.pool);
			
			updateGUI();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}

	
	/**
	 * Saving batch script.
	 */
	private void saveBatchScript() {
		UriAdapter adapter = new UriAdapter(); 
        xURI uri = adapter.chooseUri(
				this, 
				false, 
				new String[] {"properties", "script", "hudup"}, 
				new String[] {"Properties URI (s)", "Script files", "Hudup URI (s)"},
				null,
				null);
        adapter.close();
        
        if (uri == null) {
			JOptionPane.showMessageDialog(
					this, 
					"URI not save", 
					"URI not save", 
					JOptionPane.WARNING_MESSAGE);
			return;
        }
        
		adapter = new UriAdapter(uri);
		boolean existed = adapter.exists(uri);
		adapter.close();
        if (existed) {
        	int ret = JOptionPane.showConfirmDialog(
        			this, 
        			"URI exist. Do you want to override it?", 
        			"URI exist", 
        			JOptionPane.YES_NO_OPTION, 
        			JOptionPane.QUESTION_MESSAGE);
        	if (ret == JOptionPane.NO_OPTION)
        		return;
        }
		
		adapter = null;
		Writer writer = null;
        try {
			adapter = new UriAdapter(uri);
    		writer = adapter.getWriter(uri, false);
    		
    		List<String> algNameList = Util.newList();
    		for (Alg alg : this.algList)
    			algNameList.add(alg.getName());
    		
			BatchScript script = BatchScript.assign(
					this.pool, algNameList, this.mainUnit);
			
			script.save(writer);
    		writer.flush();
    		writer.close();
    		writer = null;
	        
        	JOptionPane.showMessageDialog(this, 
        			"URI saved successfully", "URI saved successfully", JOptionPane.INFORMATION_MESSAGE);
        }
		catch(Exception e) {
			e.printStackTrace();
		}
        finally {
        	try {
        		if (writer != null)
        			writer.close();
        	}
        	catch (Exception e) {
        		e.printStackTrace();
        	}
        	
        	if (adapter != null)
        		adapter.close();
        }
		
	}
	
	
	/**
	 * Update user graphic interface.
	 */
	private void updateGUI() {
		this.btnRefresh.setEnabled(pool.size() > 0);
		this.btnClear.setEnabled(pool.size() > 0);
	}
	
	
}
