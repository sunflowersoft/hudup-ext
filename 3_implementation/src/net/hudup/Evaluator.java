package net.hudup;

import static net.hudup.core.Constants.ROOT_PACKAGE;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;

import org.apache.log4j.Logger;

import net.hudup.core.AccessPoint;
import net.hudup.core.Firer;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.evaluate.ui.EvalCompoundGUI;

/**
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Evaluator implements AccessPoint {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Evaluator.class);
	
	
	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new Evaluator().run(args);
	}


	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		new Firer();
		
		if (args != null && args.length > 0) {
			String evClassName = args[0];
			try {
				net.hudup.core.evaluate.Evaluator ev = (net.hudup.core.evaluate.Evaluator)Class.forName(evClassName).newInstance();
				EvalCompoundGUI.run(ev);
				return;
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		List<net.hudup.core.evaluate.Evaluator> evList = SystemUtil.getInstances(ROOT_PACKAGE, net.hudup.core.evaluate.Evaluator.class);
		if (evList.size() == 0) {
			JOptionPane.showMessageDialog(
					null, 
					"There is no evaluator", 
					"There is no evaluator", 
					JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Collections.sort(evList, new Comparator<net.hudup.core.evaluate.Evaluator>() {

			@Override
			public int compare(net.hudup.core.evaluate.Evaluator o1, net.hudup.core.evaluate.Evaluator o2) {
				// TODO Auto-generated method stub
				try {
					return o1.getName().compareTo(o2.getName());
				}
				catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return -1;
				}
			}
		});
		
		net.hudup.core.evaluate.Evaluator initialEv = null;
		for (net.hudup.core.evaluate.Evaluator ev : evList) {
			try {
				if (ev.getName().equals("Recommendation")) {
					initialEv = ev;
					break;
				}
			}
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		final StartDlg dlgEvStarter = new StartDlg((JFrame)null, "List of evaluators") {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void start() {
				// TODO Auto-generated method stub
				final net.hudup.core.evaluate.Evaluator ev = (net.hudup.core.evaluate.Evaluator) getItemControl().getSelectedItem();
				dispose();
				EvalCompoundGUI.run(ev);
			}
			
			@Override
			protected JComboBox<?> createItemControl() {
				// TODO Auto-generated method stub
				return new JComboBox<net.hudup.core.evaluate.Evaluator>(evList.toArray(new net.hudup.core.evaluate.Evaluator[0]));
			}
			
			@Override
			protected JTextArea createHelp() {
				// TODO Auto-generated method stub
				JTextArea toolkit = new JTextArea("Thank you for choosing evaluators");
				toolkit.setEditable(false);
				return toolkit;
			}
		};
		
		if (initialEv != null)
			dlgEvStarter.getItemControl().setSelectedItem(initialEv);
		dlgEvStarter.setSize(400, 150);
        dlgEvStarter.setVisible(true);
	}


	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Evaluator";
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}

	
}


