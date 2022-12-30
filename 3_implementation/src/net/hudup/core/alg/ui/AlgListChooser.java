/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.ui.AlgListBox.AlgListChangedEvent;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.TextParserUtil;

/**
 * This graphic user interface (GUI) component as a dialog shows algorithms.
 * It also allows users to choose and configure algorithms.
 * It contains two {@link AlgListBox} (s) in which the left one assists users to select algorithms whereas the right one contains chosen algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public class AlgListChooser extends JDialog {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of available algorithm.
	 */
	private final static String AVAILABLE = "Available algorithms";

	
	/**
	 * List of selected algorithm.
	 */
	private final static String SELECTED = "Selected algorithms";
	
	
	/**
	 * The left label.
	 */
	private JLabel leftLabel = null;

	
	/**
	 * The left list box assists users to select algorithms.
	 */
	private AlgListBox leftList = null;

	
	/**
	 * The right label.
	 */
	private JLabel rightLabel = null;

	
	/**
	 * The right list box contains chosen algorithms.
	 */
	private AlgListBox rightList = null;
	
	
	/**
	 * Result as list of chosen algorithms.
	 */
	private List<Alg> result = null;
	
	
	/**
	 * If {@code true}, users press OK button to close this dialog.
	 */
	private boolean ok = false;
	
	
	/**
	 * Constructor with parent component, the whole list of algorithms, and the list of firstly selected algorithms.
	 * @param comp parent component.
	 * @param wholeList whole list of algorithms.
	 * @param selectedList list of firstly selected algorithms.
	 */
	public AlgListChooser(Component comp, List<Alg> wholeList, List<Alg> selectedList) {
		this(comp, wholeList, selectedList, false, null);
	}

	
	/**
	 * Constructor with parent component, the whole list of algorithms, the list of firstly selected algorithms, and sorting mode.
	 * @param comp parent component.
	 * @param wholeList whole list of algorithms.
	 * @param selectedList list of firstly selected algorithms.
	 * @param sorting sorting mode.
	 */
	public AlgListChooser(Component comp, List<Alg> wholeList, List<Alg> selectedList, boolean sorting) {
		this(comp, wholeList, selectedList, sorting, null);
	}
	
	
	/**
	 * Constructor with parent component, the whole list of algorithms, the list of firstly selected algorithms, sorting mode, and referred evaluator.
	 * @param comp parent component.
	 * @param wholeList whole list of algorithms.
	 * @param selectedList list of firstly selected algorithms.
	 * @param sorting sorting mode.
	 * @param referredEvaluator referred evaluator.
	 */
	public AlgListChooser(Component comp, List<Alg> wholeList, List<Alg> selectedList, boolean sorting, Evaluator referredEvaluator) {
		super(UIUtil.getDialogForComponent(comp), "Choosing algorithms", true);
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		
		JMenuBar menuBar = createMenuBar();
	    if (menuBar != null) setJMenuBar(menuBar);

	    JPanel pane = null;
		
		if (sorting) {
			Collections.sort(wholeList, new Comparator<Alg>() {
				
				@Override
				public int compare(Alg alg1, Alg alg2) {
					return alg1.getName().compareTo(alg2.getName());
				}
			});
			Collections.sort(selectedList, new Comparator<Alg>() {
				
				@Override
				public int compare(Alg alg1, Alg alg2) {
					return alg1.getName().compareTo(alg2.getName());
				}
			});
		}
		
		this.result = Util.newList();
		this.result.addAll(selectedList);
		
		List<Alg> remainList = Util.newList();
		remainList.addAll(wholeList);
		remainList.removeAll(selectedList); 
		
		setLayout(new BorderLayout());
		JPanel body = new JPanel(new GridLayout(1, 0));
		add(body, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		body.add(left);
		
		leftLabel = new JLabel(AVAILABLE);
		left.add(leftLabel, BorderLayout.NORTH);
		leftList = new AlgListBox(sorting, referredEvaluator);
		leftList.update(remainList);
		leftList.setEnableDoubleClick(false);
		leftList.addAlgListChangedListener(new AlgListBox.AlgListChangedListener() {
			@Override
			public void algListChanged(AlgListChangedEvent evt) {
				updateAlgNumbers();
			}
		});
		left.add(new JScrollPane(leftList), BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(0, 1));
		left.add(buttons, BorderLayout.EAST);
		
		JButton leftToRight = new JButton("> ");
		leftToRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
					leftToRight();
				else
					leftToRight(null);
			}
		});
		pane = new JPanel();
		pane.add(leftToRight);
		buttons.add(pane);
		
		JButton leftToRightAll = new JButton(">>");
		leftToRightAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
					leftToRightAll();
				else
					leftToRightAll(null);
			}
		});
		pane = new JPanel();
		pane.add(leftToRightAll);
		buttons.add(pane);
		
		JButton rightToLeft = new JButton("< ");
		rightToLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
					rightToLeft();
				else
					rightToLeft(null);
			}
		});
		pane = new JPanel();
		pane.add(rightToLeft);
		buttons.add(pane);
		
		JButton rightToLeftAll = new JButton("<<");
		rightToLeftAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if ((e.getModifiers() & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK)
					rightToLeftAll();
				else
					rightToLeftAll(null);
			}
		});
		pane = new JPanel();
		pane.add(rightToLeftAll);
		buttons.add(pane);
		
		JButton switchLeftRight = new JButton("<>");
		switchLeftRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				switchLeftRight();
			}
		});
		pane = new JPanel();
		pane.add(switchLeftRight);
		buttons.add(pane);

		
		JPanel right = new JPanel(new BorderLayout());
		body.add(right);
		
		rightLabel = new JLabel(SELECTED);
		right.add(rightLabel, BorderLayout.NORTH);
		rightList = new AlgListBox(sorting, referredEvaluator);
		rightList.setEnableDoubleClick(false);
		rightList.update(selectedList);
		rightList.addAlgListChangedListener(new AlgListBox.AlgListChangedListener() {
			@Override
			public void algListChanged(AlgListChangedEvent evt) {
				updateAlgNumbers();
			}
		});
		right.add(new JScrollPane(rightList), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		JButton ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
			}
		});
		footer.add(ok);
	
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		});
		footer.add(cancel);
		
		updateAlgNumbers();
		
		
		setVisible(true);
	}
	
	
	/**
	 * Create menu bar.
	 * @return menu bar.
	 */
	private JMenuBar createMenuBar() {
		return null;
	}
	

	/**
	 * Updating numbers of algorithms.
	 */
	private void updateAlgNumbers() {
		leftLabel.setText(AVAILABLE + ": " + leftList.getModel().getSize());
		rightLabel.setText(SELECTED + ": " + rightList.getModel().getSize());
	}
	
	
	/**
	 * This class represents a algorithm filter dialog.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	private class AlgFilter extends JDialog {
		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Default Java regular expression for splitting a sentence into many words (tokens), including space.
		 */
		private final static String DEFAULT_SEP  = "[[\\s][,]]";
		
		/**
		 * Text area for algorithms.
		 */
		private TextArea txtAlgs = null;
		
		/**
		 * Filtered algorithms.
		 */
		private List<String> filterAlgs = null;
		
		/**
		 * Default constructor.
		 * @param algNameList list of algorithm names.
		 */
		public AlgFilter(List<String> algNameList) {
			super(UIUtil.getDialogForComponent(getThisChooser()), "Algorithm filter", true);

			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			setSize(400, 300);
			setLocationRelativeTo(UIUtil.getDialogForComponent(getThisChooser()));
			setLayout(new BorderLayout());
		
			JPanel body = new JPanel(new BorderLayout());
			add(body, BorderLayout.CENTER);
			
			txtAlgs = new TextArea(algNameList != null && algNameList.size() > 0 ? TextParserUtil.toTextWithoutBlanks(algNameList, "\n") : "");
			body.add(new JScrollPane(txtAlgs), BorderLayout.CENTER);
			
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
					dispose();
				}
			});
			footer.add(btnCancel);
		}
		
		/**
		 * Event-driven method for OK button.
		 */
		private void onOK() {
			String algsText = txtAlgs.getText();
			List<String> algNames = algsText != null ? Arrays.asList(algsText.split(DEFAULT_SEP)) : Util.newList(0);
			
			filterAlgs = Util.newList();
			for (String algName : algNames) {
				algName = algName != null ? algName.trim() : "";
				if (!algName.isEmpty()) filterAlgs.add(algName);
			}
			
			dispose();
		}
		
		/**
		 * Getting filtered algorithms.
		 * @return list of filtered algorithms.
		 */
		public List<String> getFilterAlgs() {
			return filterAlgs;
		}

	}
	

	/**
	 * Transferring selected algorithms from the left to the right.
	 */
	private void leftToRight() {
		if (leftList.getSelectedAlgList().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Algorithm not selected or list empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}

		AlgFilter dlgFilter = new AlgFilter(leftList.getSelectedAlgNameList());
		dlgFilter.setVisible(true);
		List<String> filterAlgs = dlgFilter.getFilterAlgs(); 
		if (filterAlgs != null) leftToRight(filterAlgs);
	}

	
	/**
	 * Transferring selected algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 * @param filterAlgs filtered algorithms.
	 */
	private void leftToRight(Collection<String> filterAlgs) {
		List<Alg> list = leftList.getSelectedAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Algorithm not selected or list empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		if (filterAlgs == null || filterAlgs.isEmpty()) {
			rightList.addAll(list);
			leftList.removeAll(list);
		}
		else {
			List<Alg> removed = Util.newList();
			for (Alg alg : list) {
				if (filterAlgs.contains(alg.getName())) {
					rightList.add(alg);
					removed.add(alg);
				}
			}
			if (removed.size() > 0) leftList.removeAll(removed);
		}
		
		updateAlgNumbers();
	}
	
	
	/**
	 * Transferring all algorithms from the left to the right.
	 */
	private void leftToRightAll() {
		if (leftList.getAlgList().isEmpty()) {
			JOptionPane.showMessageDialog(this, "List empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}

		AlgFilter dlgFilter = new AlgFilter(leftList.getAlgNameList());
		dlgFilter.setVisible(true);
		List<String> filterAlgs = dlgFilter.getFilterAlgs(); 
		if (filterAlgs != null) leftToRightAll(filterAlgs);
	}

	
	/**
	 * Transferring all algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 * @param filterAlgs filtered algorithms.
	 */
	private void leftToRightAll(Collection<String> filterAlgs) {
		List<Alg> list = leftList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(this, "List empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		if (filterAlgs == null || filterAlgs.isEmpty()) {
			rightList.addAll(list);
			leftList.clear();
		}
		else {
			List<Alg> removed = Util.newList();
			for (Alg alg : list) {
				if (filterAlgs.contains(alg.getName())) {
					rightList.add(alg);
					removed.add(alg);
				}
			}
			if (removed.size() > 0) leftList.removeAll(removed);
		}
		
		updateAlgNumbers();
	}

	
	/**
	 * Transferring selected algorithms from the right to the left.
	 */
	private void rightToLeft() {
		if (rightList.getSelectedAlgList().isEmpty()) {
			JOptionPane.showMessageDialog(this, "Algorithm not selected or list empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}

		AlgFilter dlgFilter = new AlgFilter(rightList.getSelectedAlgNameList());
		dlgFilter.setVisible(true);
		List<String> filterAlgs = dlgFilter.getFilterAlgs(); 
		if (filterAlgs != null) rightToLeft(filterAlgs);
	}
	
	
	/**
	 * Transferring selected algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 * @param filterAlgs filtered algorithms.
	 */
	private void rightToLeft(Collection<String> filterAlgs) {
		List<Alg> list = rightList.getSelectedAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(this, "Algorithm not selected or list empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		if (filterAlgs == null || filterAlgs.isEmpty()) {
			leftList.addAll(list);
			rightList.removeAll(list);
		}
		else {
			List<Alg> removed = Util.newList();
			for (Alg alg : list) {
				if (filterAlgs.contains(alg.getName())) {
					leftList.add(alg);
					removed.add(alg);
				}
			}
			if (removed.size() > 0) rightList.removeAll(removed);
		}
		
		updateAlgNumbers();
	}
	

	/**
	 * Transferring all algorithms from the right to the left.
	 */
	private void rightToLeftAll() {
		if (rightList.getAlgList().isEmpty()) {
			JOptionPane.showMessageDialog(this, "List empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}

		AlgFilter dlgFilter = new AlgFilter(rightList.getAlgNameList());
		dlgFilter.setVisible(true);
		List<String> filterAlgs = dlgFilter.getFilterAlgs(); 
		if (filterAlgs != null) rightToLeftAll(filterAlgs);
	}
	
	
	/**
	 * Transferring all algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 * @param filterAlgs filtered algorithms.
	 */
	private void rightToLeftAll(Collection<String> filterAlgs) {
		List<Alg> list = rightList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(this, "List empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}

		if (filterAlgs == null || filterAlgs.isEmpty()) {
			leftList.addAll(list);
			rightList.clear();
		}
		else {
			List<Alg> removed = Util.newList();
			for (Alg alg : list) {
				if (filterAlgs.contains(alg.getName())) {
					leftList.add(alg);
					removed.add(alg);
				}
			}
			if (removed.size() > 0) rightList.removeAll(removed);
		}

		updateAlgNumbers();
	}

	
	/**
	 * Switching left and right algorithms.
	 */
	private void switchLeftRight() {
		List<Alg> leftAlgs = leftList.getAlgList();
		List<Alg> rightAlgs = rightList.getAlgList();
		if (leftAlgs.isEmpty() && rightAlgs.isEmpty()) {
			JOptionPane.showMessageDialog(this, "List empty", "List empty", JOptionPane.WARNING_MESSAGE);
			return;
		}

		leftList.update(rightAlgs);
		rightList.update(leftAlgs);
		
		updateAlgNumbers();
	}
	
	
	/**
	 * This is event-driven method which responses the action user pressed on OK button.
	 * This method will sets up the result as list of selected algorithms and then closes this dialog.
	 */
	private void ok() {
		List<Alg> list = rightList.getAlgList();
		if (list.size() == 0) {
			JOptionPane.showMessageDialog(this, "List empty", "List empty", JOptionPane.ERROR_MESSAGE);
			return;
		}
		this.result.clear();
		this.result.addAll(list);
		
		ok = true;
		dispose();
	}
	
	
	/**
	 * Getting the result as list of chosen algorithms.
	 * @return result as list of chosen algorithms.
	 */
	public List<Alg> getResult() {
		return result;
	}
	
	
	/**
	 * Checking whether or not the OK button is pressed.
	 * @return whether or not the OK button is pressed.
	 */
	public boolean isOK() {
		return ok;
	}
	
	
	/**
	 * Getting this algorithm list chooser.
	 * @return this algorithm list chooser.
	 */
	private AlgListChooser getThisChooser() {
		return this;
	}
	
	
	/**
	 * This static method gives convenience for users by showing the {@link AlgListChooser} dialog with specified array of algorithms.
	 * @param comp parent component.
	 * @param algs specified array of algorithms.
	 * @return result as list of chosen algorithms.
	 */
	public static Alg chooseAlg(Component comp, Alg[] algs) {
		return (Alg) JOptionPane.showInputDialog(
				comp, 
				"Choose algorithm", 
				"Choose algorithm", 
				JOptionPane.INFORMATION_MESSAGE, 
				null, 
				algs, 
				algs[0]);		
	}
	
	
}
