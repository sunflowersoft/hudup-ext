package net.hudup.core.alg.ui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.KBase;
import net.hudup.core.alg.ModelBasedAlg;
import net.hudup.core.logistic.Inspectable;
import net.hudup.core.logistic.Inspector;
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
	 * Creating the context menu for {@link AlgListUI}.
	 * @param ui specified {@link AlgListUI}.
	 * @return context menu {@link JPopupMenu} for {@link AlgListUI}.
	 */
	public final static JPopupMenu createContextMenu(final AlgListUI ui) {
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
		if (alg instanceof Inspectable) {
			JMenuItem miInspect = new JMenuItem("Inspect");
			miInspect.addActionListener( 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						Inspector inspector = null;
						try {
							inspector = ((Inspectable)alg).getInspector();
							if (inspector != null) inspector.inspect();
						} catch (Exception ex) {
							ex.printStackTrace();
							inspector = null;
						}
						if (inspector == null)
							JOptionPane.showMessageDialog(UIUtil.getFrameForComponent((Component)ui), "Cannot retrieve inspector", "Cannot retrieve inspector", JOptionPane.ERROR_MESSAGE);
					}
				});
			ctxMenu.add(miInspect);
		}
		
		//Showing inspector of the algorithm
		if (alg instanceof ModelBasedAlg) {
			KBase kb = null;
			try {
				kb = ((ModelBasedAlg)alg).getKBase();
			} catch (Throwable e) {e.printStackTrace();}
			
			final KBase kbase = kb;
			if (kbase != null && !kbase.isEmpty()) {
				JMenuItem miViewKB = new JMenuItem("View knowledge base");
				miViewKB.addActionListener( 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							Inspector inspector = null;
							try {
								inspector = kbase.getInspector();
								if (inspector != null) inspector.inspect();
							} catch (Exception ex) {
								ex.printStackTrace();
								inspector = null;
							}
							if (inspector == null)
								JOptionPane.showMessageDialog(UIUtil.getFrameForComponent((Component)ui), "Cannot view knowledge base", "Cannot view knowledge base", JOptionPane.ERROR_MESSAGE);
						}
					});
				ctxMenu.add(miViewKB);
				
				JMenuItem miConfigureKB = new JMenuItem("Configure knowledge base");
				miConfigureKB.addActionListener( 
					new ActionListener() {
						
						@Override
						public void actionPerformed(ActionEvent e) {
							KBaseConfigDlg dlgKBase = new KBaseConfigDlg(UIUtil.getFrameForComponent((Component)ui), kbase);
							dlgKBase.setVisible(true);
						}
					});
				ctxMenu.add(miConfigureKB);
			}
		}

		
		return ctxMenu;
	}


	/**
	 * Getting the {@link Frame} of specified {@link AlgListUI}.
	 * @param ui specified {@link AlgListUI}.
	 * @return {@link Frame} of specified {@link AlgListUI}.
	 */
	private static Frame getFrame(AlgListUI ui) {
		if (ui instanceof Frame)
			return (Frame) ui;
		else if (ui instanceof Component)
			return UIUtil.getFrameForComponent((Component)ui);
		else
			return null;
	}
	
	
	/**
	 * Showing the {@link AlgConfigDlg} dialog to assist users to configure the algorithm selected from the specified {@link AlgListUI}.
	 * @param ui specified {@link AlgListUI}.
	 */
	public static void config(AlgListUI ui) {
		Alg alg = ui.getSelectedAlg();
		if (alg == null)
			return;
		
		new AlgConfigDlg(getFrame(ui), alg).setVisible(true);
	}

	
}
