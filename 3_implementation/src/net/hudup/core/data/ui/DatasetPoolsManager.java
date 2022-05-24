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
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import net.hudup.core.data.DatasetPoolExchangedItem;
import net.hudup.core.data.DatasetPoolsService;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This is dataset pool manager.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
public class DatasetPoolsManager extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Dataset pool service.
	 */
	protected DatasetPoolsService poolsService = null;
	
	
	/**
	 * Pools list
	 */
	protected DatasetPoolsList poolList = null;

	
	/**
	 * Pool table.
	 */
	protected DatasetPoolTable poolTable = null;
	
	
	/**
	 * Selected dataset pool.
	 */
	protected DatasetPoolExchangedItem selectedPool = null;
	
	
	/**
	 * Constructor with pool manager.
	 * @param poolsService specified pool manager.
	 * @param comp parent component.
	 */
	public DatasetPoolsManager(DatasetPoolsService poolsService, Component comp) {
		super(UIUtil.getDialogForComponent(comp), "Dataset pool manager", true);
		this.poolsService = poolsService;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());

		DatasetPoolsManager thisManager = this;
		setJMenuBar(createMenuBar());
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if(contextMenu == null) return;
					contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
			}
		});
	    
		
		JPanel left = new JPanel(new BorderLayout());
		add(left, BorderLayout.WEST);
		
		poolList = new DatasetPoolsList(poolsService);
		poolList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				onPoolListChanged();
			}
		});
		poolList.getModel().addListDataListener(new ListDataListener() {
			@Override
			public void intervalRemoved(ListDataEvent e) {
			}
			
			@Override
			public void intervalAdded(ListDataEvent e) {
			}
			
			@Override
			public void contentsChanged(ListDataEvent e) {
			}
		});
		left.add(new JScrollPane(poolList), BorderLayout.CENTER);
		
		JPanel leftToolbar = new JPanel(new FlowLayout(FlowLayout.LEFT));
		left.add(leftToolbar, BorderLayout.SOUTH);
		
		JButton btnAddPool = UIUtil.makeIconButton("add-16x16.png", "addpool", "Add pool", "Add pool",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				
				}
			});
		btnAddPool.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnAddPool);

		JButton btnDeletePool = UIUtil.makeIconButton("delete-16x16.png", "deletepool", "Delete pool", "Delete pool",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
				
				}
			});
		btnDeletePool.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnDeletePool);

		JButton btnRefreshAll = UIUtil.makeIconButton("refresh-16x16.png", "refreshall", "Refresh all", "Refresh all",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					poolList.update();
				}
			});
		btnRefreshAll.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnRefreshAll);
					
		JButton btnClearAll = UIUtil.makeIconButton("clear-16x16.png", "clearall", "Clear all", "Clear all",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

				}
			});
		btnClearAll.setMargin(new Insets(0, 0 , 0, 0));
		leftToolbar.add(btnClearAll);


		JPanel right = new JPanel(new BorderLayout());
		add(right, BorderLayout.CENTER);
		
		poolTable = new DatasetPoolTable();
		right.add(new JScrollPane(poolTable), BorderLayout.CENTER);
		
		JPanel rightToolbar = new JPanel(new BorderLayout());
		right.add(rightToolbar, BorderLayout.SOUTH);

		JPanel rightToolbar1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		rightToolbar.add(rightToolbar1, BorderLayout.WEST);

		JButton btnAddDataset = new JButton(I18nUtil.message("add_dataset"));
		btnAddDataset.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnAddDataset.setMnemonic('a');
		rightToolbar1.add(btnAddDataset);
		
		JButton btnLoadBatchScript = new JButton(I18nUtil.message("load_script"));
		btnLoadBatchScript.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

			}
		});
		btnLoadBatchScript.setMnemonic('l');
		rightToolbar1.add(btnLoadBatchScript);

		JPanel rightToolbar2 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		rightToolbar.add(rightToolbar2, BorderLayout.EAST);
		
		JButton btnRefreshPool = UIUtil.makeIconButton("refresh-16x16.png", "refreshpool", "Refresh pool", "Refresh pool",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

				}
			});
		btnRefreshPool.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar2.add(btnRefreshPool);

		JButton btnClearPool = UIUtil.makeIconButton("clear-16x16.png", "clearpool", "Clear pool", "Clear pool",
			new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {

				}
			});
		btnClearPool.setMargin(new Insets(0, 0 , 0, 0));
		rightToolbar2.add(btnClearPool);

		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				onOK();
			}
		});
		footer.add(btnOK);
		
		JButton btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				thisManager.dispose();
			}
		});
		footer.add(btnCancel);
	}

	
	/**
	 * Creating main menu bar.
	 * @return main menu bar.
	 */
	protected JMenuBar createMenuBar() {
		JMenuBar mnBar = new JMenuBar();
		
		JMenu mnTool = new JMenu("Tool");
		mnTool.setMnemonic('t');
		mnBar.add(mnTool);
		
		return mnBar;
	}

	
	/**
	 * Creating context menu.
	 * @return pop-up menu.
	 */
	protected JPopupMenu createContextMenu() {
		JPopupMenu ctxMenu = new JPopupMenu();
		return ctxMenu;
	}
	
	
	/**
	 * Driven function for pool selection.
	 */
	private void onPoolListChanged() {
		System.out.println("Pool list changed");
	}
	
	
	/**
	 * Driven function for OK button.
	 */
	private void onOK() {
		this.selectedPool = poolList.getSelectedValue();
		dispose();
	}
	
	
	/**
	 * Getting selected dataset pool.
	 * @return selected dataset pool.
	 */
	public DatasetPoolExchangedItem getSelectedPool() {
		return selectedPool;
	}
	
	
	/**
	 * Showing pools manager.
	 * @param poolsService pool manager.
	 * @param comp parent component.
	 * @return dataset pools manager.
	 */
	public static DatasetPoolsManager show(DatasetPoolsService poolsService, Component comp) {
		DatasetPoolsManager manager = new DatasetPoolsManager(poolsService, comp);
		manager.setVisible(true);
		return manager;
	}
	
	
 }
