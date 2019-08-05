package net.hudup;

import javax.swing.JOptionPane;

import net.hudup.core.AccessPoint;
import net.hudup.core.client.ConnectServerDlg;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Server;
import net.hudup.core.client.Service;
import net.hudup.evaluate.ui.EvalCompoundGUI;


/**
 * This class implements a remote evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluatorRemote implements AccessPoint {
	
	
	/**
	 * Default constructor.
	 */
	public EvaluatorRemote() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * The main method to start evaluator.
	 * @param args The argument parameter of main method. It contains command line arguments.
	 * @throws Exception if there is any error.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		new EvaluatorRemote().run(args);
	}

	
	@Override
	public void run(String[] args) {
		// TODO Auto-generated method stub
		ConnectServerDlg dlg = new ConnectServerDlg();
		Server server = dlg.getServer();
		if (server == null || !(server instanceof PowerServer)) {
			JOptionPane.showMessageDialog(
					null, "Can't connect to server or server is not power server", "Connection to server fail", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		try {
			Service service = ((PowerServer)server).getService();
			if (service == null) {
				JOptionPane.showMessageDialog(
						null, "Can't get remote service", "Connection to service fail", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			net.hudup.core.evaluate.Evaluator evaluator = service.getEvaluator("Estimation Evaluator");
			evaluator.getPluginStorage().assignToSystem();
			EvalCompoundGUI.run(evaluator);
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Remote Evaluator";
	}

	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return getName();
	}
	
	
}
