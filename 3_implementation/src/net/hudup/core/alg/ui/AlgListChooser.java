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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.ui.AlgListBox.AlgListChangedEvent;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.logistic.ui.UIUtil;

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
				leftToRight();
			}
		});
		pane = new JPanel();
		pane.add(leftToRight);
		buttons.add(pane);
		
		JButton leftToRightAll = new JButton(">>");
		leftToRightAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				leftToRightAll();
			}
		});
		pane = new JPanel();
		pane.add(leftToRightAll);
		buttons.add(pane);
		
		JButton rightToLeft = new JButton("< ");
		rightToLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rightToLeft();
			}
		});
		pane = new JPanel();
		pane.add(rightToLeft);
		buttons.add(pane);
		
		JButton rightToLeftAll = new JButton("<<");
		rightToLeftAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				rightToLeftAll();
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
	 * Updating numbers of algorithms.
	 */
	private void updateAlgNumbers() {
		leftLabel.setText(AVAILABLE + ": " + leftList.getModel().getSize());
		rightLabel.setText(SELECTED + ": " + rightList.getModel().getSize());
	}
	
	
	/**
	 * Transferring selected algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 */
	private void leftToRight() {
		List<Alg> list = leftList.removeSelectedList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
				this, 
				"Algorithm not selected or empty list", 
				"Algorithm not selected or empty list", 
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		rightList.addAll(list);
		
		updateAlgNumbers();
	}
	

	/**
	 * Transferring all algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 */
	private void leftToRightAll() {
		List<Alg> list = leftList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
				this, 
				"List empty", 
				"List empty", 
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		rightList.addAll(list);
		leftList.clear();
		
		updateAlgNumbers();
	}

	
	/**
	 * Transferring selected algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 */
	private void rightToLeft() {
		List<Alg> list = rightList.removeSelectedList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
				this, 
				"Algorithm not selected or empty list", 
				"Algorithm not selected or empty list", 
				JOptionPane.WARNING_MESSAGE);
			return;
		}
		leftList.addAll(list);
		
		updateAlgNumbers();
	}
	

	/**
	 * Transferring all algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 */
	private void rightToLeftAll() {
		List<Alg> list = rightList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
				this, 
				"List empty", 
				"List empty", 
				JOptionPane.WARNING_MESSAGE);
			return;
		}

		leftList.addAll(list);
		rightList.clear();
		
		updateAlgNumbers();
	}

	
	/**
	 * Switching left and right algorithms.
	 */
	private void switchLeftRight() {
		List<Alg> leftAlgs = leftList.getAlgList();
		List<Alg> rightAlgs = rightList.getAlgList();
		if (leftAlgs.isEmpty() && rightAlgs.isEmpty()) {
			JOptionPane.showMessageDialog(
				this, 
				"List empty", 
				"List empty", 
				JOptionPane.WARNING_MESSAGE);
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
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.ERROR_MESSAGE);
			
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
