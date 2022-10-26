/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.hudup.core.Util;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class is used to get evaluator information.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluatorSysInfoGetter extends JDialog {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Main unit type.
	 */
	public final static String DEFAULT_EVALUATOR = "estimate";

	
	/**
	 * Main unit type.
	 */
	public final static String MAIN_UNIT = "main_unit";
	
	
	/**
	 * Evaluator name.
	 */
	public final static String EVALUATOR_NAME = "evaluator_name";

	
	/**
	 * Defining default connection types.
	 */
	public final static String[] defaultInfoTypes = new String[] {
		EVALUATOR_NAME,
		MAIN_UNIT,
	};

	
	/**
	 * List of evaluators.
	 */
	protected JComboBox<Evaluator> cmbEvaluators = null;

	
	/**
	 * Evaluator information types.
	 */
	protected JComboBox<String> cmbInfoTypes = null;
	
	
	/**
	 * Text field for evaluator information.
	 */
	protected TextArea txtInfo = null; 
			
	
	/**
	 * Evaluator information.
	 */
	protected String info = null;
	
	
	/**
	 * Turning on changing flag.
	 */
	protected boolean turnOnChange = true;
	
	
	/**
	 * Constructor with parent component.
	 * @param comp parent component.
	 */
	public EvaluatorSysInfoGetter(Component comp) {
		super(UIUtil.getDialogForComponent(comp), "Evaluator system information", true);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(400, 200);
		setLocationRelativeTo(null);

		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		List<Evaluator> evList = Util.getPluginManager().loadInstances(Evaluator.class);
		Collections.sort(evList, new Comparator<Evaluator>() {
			@Override
			public int compare(Evaluator o1, Evaluator o2) {
				try {
					return o1.getName().compareToIgnoreCase(o2.getName());
				}
				catch (Throwable e) {
					LogUtil.trace(e);
					return -1;
				}
			}
		});
		cmbEvaluators = new JComboBox<Evaluator>(evList.toArray(new Evaluator[] {}));
		cmbEvaluators.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) return;
				onChange();
			}
		});
		header.add(cmbEvaluators, BorderLayout.CENTER);

		Arrays.sort(defaultInfoTypes);
		cmbInfoTypes = new JComboBox<String>(defaultInfoTypes);
		cmbInfoTypes.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() != ItemEvent.SELECTED) return;
				onChange();
			}
		});
		header.add(cmbInfoTypes, BorderLayout.EAST);

		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		txtInfo = new TextArea(EvaluatorAbstract.MAIN_UNIT_DEFAULT);
		body.add(new JScrollPane(txtInfo), BorderLayout.CENTER);

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
				info = null;
				dispose();
			}
		});
        footer.add(btnCancel);
	}

	
	/**
	 * Event-driven method for OK button.
	 */
	protected void onOK() {
		info = txtInfo.getText();
		if (info == null) return;
		info = info.trim();
		if (info.isEmpty()) info = null;

		dispose();
	}
	
	
	/**
	 * Event-driven method for changing.
	 */
	protected void onChange() {
		if (!turnOnChange) return;
		if (cmbEvaluators == null || cmbInfoTypes == null || txtInfo == null) return;
		if (cmbEvaluators.getModel().getSize() == 0) return;
		if (cmbInfoTypes.getModel().getSize() == 0) return;
		
		Evaluator evaluator = (Evaluator)cmbEvaluators.getSelectedItem();
		String infoType = (String)cmbInfoTypes.getSelectedItem();
		if (evaluator == null || infoType == null) return;
		
		try {
			switch (infoType) {
			case MAIN_UNIT:
				txtInfo.setText(evaluator.getMainUnit());
				break;
			case EVALUATOR_NAME:
				txtInfo.setText(evaluator.getVersionName());
				break;
			}
		} catch (Throwable e) {LogUtil.trace(e);}
	}
	
	
	/**
	 * Getting information.
	 * @return evaluator information.
	 */
	public String getInfo() {
		return info;
	}
	
	
	/**
	 * Selecting evaluator by its name.
	 * @param evaluatorName evaluator name.
	 */
	public void selectEvaluator(String evaluatorName) {
		if (evaluatorName == null) return;
		
		Evaluator found = null;
		for (int i = 0; i < cmbEvaluators.getModel().getSize(); i++) {
			Evaluator evaluator = cmbEvaluators.getModel().getElementAt(i);
			if (evaluator == null) continue;
			try {
				if (evaluator.getVersionName().equals(evaluatorName)) {
					found = evaluator;
					break;
				}
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		if (found != null) cmbEvaluators.setSelectedItem(found);
	}
	
	
	/**
	 * Selecting information type.
	 * @param infoType information type.
	 */
	public void selectInfoType(String infoType) {
		if (infoType == null) return;
		cmbInfoTypes.setSelectedItem(infoType);
	}
	
	
	/**
	 * Adding information types.
	 * @param infoTypes array of information types.
	 */
	public void addInfoTypes(String...infoTypes) {
		if (infoTypes == null || infoTypes.length == 0) return;
		Set<String> typeSet = Util.newSet();
		for (int i = 0; i < cmbInfoTypes.getModel().getSize(); i++) {
			typeSet.add(cmbInfoTypes.getModel().getElementAt(i));
		}
		
		for (String infoType : infoTypes) {
			if (infoType == null) continue;
			infoType = infoType.trim();
			if (!infoType.isEmpty()) typeSet.add(infoType);
		}
		
		List<String> typeList = Util.newList();
		typeList.addAll(typeSet);
		Collections.sort(typeList);
		
		boolean oldTurnOnChange = turnOnChange;
		turnOnChange = false;
		cmbInfoTypes.removeAllItems();
		for (String type : typeList) cmbInfoTypes.addItem(type);
		turnOnChange = oldTurnOnChange;
		
		onChange();
	}
	
	
	/**
	 * Getting evaluator information.
	 * @param evaluatorName evaluator name.
	 * @param infoType information type.
	 * @param comp parent component.
	 * @return evaluator information.
	 */
	public static String get(String evaluatorName, String infoType, Component comp) {
		EvaluatorSysInfoGetter getter = new EvaluatorSysInfoGetter(comp);
		getter.selectEvaluator(evaluatorName);
		getter.selectInfoType(infoType);
		getter.setVisible(true);
		return getter.getInfo();
	}
	
	
}
