package net.hudup.core.alg.ui;

import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.TestingAlg;
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
		
		//Showing description of the algorithm
		if (alg instanceof TestingAlg) {
			JMenuItem miDesc = new JMenuItem("Description");
			miDesc.addActionListener( 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						JOptionPane.showMessageDialog(
								getFrame(ui), 
								((TestingAlg)alg).getDescription(), 
								"Description of " + alg.getName(), 
								JOptionPane.INFORMATION_MESSAGE);
					}
				});
			ctxMenu.add(miDesc);
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
