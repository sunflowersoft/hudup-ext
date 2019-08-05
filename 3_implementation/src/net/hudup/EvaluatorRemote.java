package net.hudup;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

import net.hudup.core.AccessPoint;
import net.hudup.core.client.ConnectServerDlg;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Server;
import net.hudup.core.client.Service;


/**
 * This class implements a remote evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class EvaluatorRemote implements AccessPoint {

	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(Evaluator.class);
	
	
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
			if (service == null) return;
			
			//Evaluator evaluator = service.getEvaluator(evaluatorName);
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
