/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.ui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.ModelBasedAlg;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.logistic.Inspectable;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This utility class assists users to work with {@link AlgListUI}.
 * Note, {@link AlgListUI} specifies user interface (UI) component showing algorithms for selection and configuration.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class AlgListUIUtil {

	
	/**
	 * Creating the context menu for algorithm list UI.
	 * @param ui specified algorithm list UI.
	 * @return context pop-up menu for algorithm list UI.
	 */
	public final static JPopupMenu createContextMenu(AlgListUI ui) {
		return createContextMenu0(ui, null);
	}

	
	/**
	 * Creating the context menu for algorithm list UI.
	 * @param ui specified algorithm list UI.
	 * @param evaluator referred evaluator. It can be null.
	 * @return context pop-up menu for algorithm list UI.
	 */
	public final static JPopupMenu createContextMenu(AlgListUI ui, Evaluator evaluator) {
		return createContextMenu0(ui, evaluator);
	}
	
	
	/**
	 * Creating the context menu for algorithm list UI. This method needs to be updated with remote knowledge base inspector.
	 * @param ui specified algorithm list UI.
	 * @param evaluator referred evaluator.
	 * @return context pop-up menu for algorithm list UI.
	 */
	private final static JPopupMenu createContextMenu0(AlgListUI ui, Evaluator evaluator) {
		Alg alg = ui.getSelectedAlg();
		if (alg == null)
			return null;
		if (!ui.isEnabled())
			return null;
		
		JPopupMenu ctxMenu = new JPopupMenu();
		
		//Configure the algorithm
		JMenuItem miConfig = new JMenuItem("Configure");
		miConfig.addActionListener( 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					config(ui);
				}
			});
		ctxMenu.add(miConfig);
		
		//Showing inspector of the algorithm
		if ((alg instanceof Inspectable) || (EvaluatorAbstract.isRemote(evaluator))) {
			JMenuItem miInspect = new JMenuItem("Inspect");
			miInspect.addActionListener( 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						Inspector inspector = null;
						
						if (EvaluatorAbstract.isRemote(evaluator)) {
							int confirm = JOptionPane.showConfirmDialog(
								UIUtil.getDialogForComponent((Component)ui), 
								"Because of using remote evaluator.\n" +
									"Would you like to show remote description?\n" +
									"If press Yes, description dialog will be shown.\n" + 
									"If press No, inspector can be shown.", 
								"Inspector option", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
							
							if (confirm == JOptionPane.NO_OPTION) {
								if (alg instanceof Inspectable) inspector = getInspector(alg, evaluator);
							}
							else {
								try {
									String desc = evaluator.getEvaluatedAlgDescText(alg.getName());
									if (desc != null)
										inspector = new DescriptionDlg(UIUtil.getDialogForComponent((Component)ui), "Inspector", desc);
								} catch (Exception ex) {LogUtil.trace(ex);}
							}
						}
						else if (alg instanceof Inspectable) {
							inspector = getInspector(alg, evaluator);
						}
						
						if (inspector != null)
							inspector.inspect();
						else
							JOptionPane.showMessageDialog(UIUtil.getDialogForComponent((Component)ui), "Cannot retrieve inspector", "Cannot retrieve inspector", JOptionPane.ERROR_MESSAGE);
					}
				});
			ctxMenu.add(miInspect);
		}
		
		//Showing inspector of the algorithm
		if (alg instanceof ModelBasedAlg) {
			KBase kb = null;
			try {
				kb = ((ModelBasedAlg)alg).getKBase();
			} catch (Throwable e) {LogUtil.trace(e); kb = null;}
			
			boolean empty = false;
			if (kb == null)
				empty = true;
			else {
				try {
					empty = kb.isEmpty();
				}
				catch (Throwable e) {LogUtil.trace(e); empty = true;}
			}
			
			final KBase kbase = kb;
			if (!empty) {
				JMenuItem miViewKB = new JMenuItem("View knowledge base");
				miViewKB.addActionListener( 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							int confirm = JOptionPane.showConfirmDialog(
									UIUtil.getDialogForComponent((Component)ui), 
									"Be careful, out of memory in case of huge knowledge base", 
									"Out of memory in case of huge knowledge base", 
									JOptionPane.OK_CANCEL_OPTION, 
									JOptionPane.WARNING_MESSAGE);
							if (confirm != JOptionPane.OK_OPTION)
								return;

							Inspector inspector = kbase.getInspector();
							if (inspector != null)
								inspector.inspect();
							else
								JOptionPane.showMessageDialog(UIUtil.getDialogForComponent((Component)ui), "Cannot view knowledge base", "Cannot view knowledge base", JOptionPane.ERROR_MESSAGE);
						}
					});
				ctxMenu.add(miViewKB);
				
				JMenuItem miConfigureKB = new JMenuItem("Configure knowledge base");
				miConfigureKB.addActionListener( 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							KBaseConfigDlg dlgKBase = new KBaseConfigDlg(UIUtil.getDialogForComponent((Component)ui), kbase);
							dlgKBase.setVisible(true);
						}
					});
				ctxMenu.add(miConfigureKB);
			}
		}

		
		return ctxMenu;
	}


	/**
	 * Getting inspector of the algorithm with evaluator.
	 * @param alg specified algorithm.
	 * @param evaluator specified evaluator.
	 * @return inspector of the algorithm with evaluator.
	 */
	private static Inspector getInspector(Alg alg, Evaluator evaluator) {
		if (alg == null) return null;
		if ((evaluator == null) || !(EvaluatorAbstract.isRemote(evaluator)) || (AlgDesc2.isRemote(alg))) {
			if (alg instanceof Inspectable)
				return ((Inspectable)alg).getInspector();
			else
				return null;
		}
		else {
			Alg remoteAlg = null;
			try {
				remoteAlg = evaluator.getEvaluatedAlg(alg.getName(), true);
			} catch (Exception e) {LogUtil.trace(e);}
			
			if (remoteAlg == null)
				return getInspector(remoteAlg, null);
			else
				return getInspector(alg, null);
		}
		
	}
	
	
	/**
	 * Getting the windows frame of specified algorithm list UI.
	 * @param ui specified algorithm list UI.
	 * @return windows frame of specified algorithm list UI.
	 */
	private static Window getWindow(AlgListUI ui) {
		if (ui instanceof Frame)
			return (Frame) ui;
		else if (ui instanceof Component)
			return UIUtil.getDialogForComponent((Component)ui);
		else
			return null;
	}
	
	
	/**
	 * Showing the configuration dialog to assist users to configure the algorithm selected from the specified {@link AlgListUI}.
	 * @param ui specified algorithm list UI.
	 */
	public static void config(AlgListUI ui) {
		Alg alg = ui.getSelectedAlg();
		if (alg == null || alg.getConfig() == null)
			return;

		new AlgConfigDlg(getWindow(ui), alg).setVisible(true);
	}

	
}
