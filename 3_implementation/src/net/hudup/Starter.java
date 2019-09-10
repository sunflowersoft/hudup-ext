package net.hudup;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.core.logistic.ui.TooltipTextArea;



/**
 * This is the initial application called starter of Hudup framework, which is the first graphic user interface (GUI) shown in front of users.
 * It lists access points which are applications of Hudup framework such as {@link Evaluator}, {@link Server}, {@link Listener}, {@link Balancer}, and {@link Toolkit}
 * so that users start one among such applications.
 * 
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class Starter {

	
	/**
	 * The main method to run this starter.
	 * It lists access points which are applications of Hudup framework such as {@link Evaluator}, {@link Server}, {@link Listener}, {@link Balancer}, and {@link Toolkit}
	 * so that users start one among such applications.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 */
	public static void main(final String[] args) {
		// TODO Auto-generated method stub
		List<AccessPoint> apList = Firer.getInstances(AccessPoint.class);
		if (apList.size() == 0) {
			JOptionPane.showMessageDialog(
					null, 
					"There is no access point", 
					"There is no access point", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Collections.sort(apList, new Comparator<AccessPoint>() {

			@Override
			public int compare(AccessPoint o1, AccessPoint o2) {
				// TODO Auto-generated method stub
				return o1.getName().compareTo(o2.getName());
			}
		});
		
		AccessPoint initialAP = null;
		for (AccessPoint ap : apList) {
			if (ap instanceof Evaluator) {
				initialAP = ap;
				break;
			}
		}
		
		final StartDlg dlgStarter = new StartDlg((JFrame)null, "Hudup framework starter") {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void start() {
				// TODO Auto-generated method stub
				AccessPoint ap = (AccessPoint) getItemControl().getSelectedItem();
				dispose();
				ap.run(args);
			}
			
			@Override
			protected JComboBox<?> createItemControl() {
				// TODO Auto-generated method stub
				return new JComboBox<AccessPoint>(apList.toArray(new AccessPoint[0]));
			}
			
			@Override
			protected JTextArea createHelp() {
				// TODO Auto-generated method stub
				TooltipTextArea toolkit = new TooltipTextArea();
				toolkit.setEditable(false);
				return toolkit;
			}
		};
		
		dlgStarter.getItemControl().setSelectedItem(initialAP);
		dlgStarter.setSize(600, 300);
        dlgStarter.setVisible(true);
	}
	
	
}
