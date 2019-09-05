package net.hudup.core.alg;

import java.rmi.RemoteException;

import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * The class is a wrapper of remote executable algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote executable algorithm) and REM wrapper (normal executable algorithm).
 * The server creates REM stub (remote executable algorithm) and the client creates and uses the REM wrapper as normal executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class ExecutableAlgRemoteWrapper extends AlgRemoteWrapper implements ExecutableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor with specified remote executable algorithm.
	 * @param remoteExecutableAlg remote executable algorithm.
	 */
	public ExecutableAlgRemoteWrapper(ExecutableAlgRemote remoteExecutableAlg) {
		// TODO Auto-generated constructor stub
		super(remoteExecutableAlg);
	}

	
	/**
	 * Constructor with specified remote executable algorithm and exclusive mode.
	 * @param remoteExecutableAlg remote executable algorithm.
	 * @param exclusive exclusive mode.
	 */
	public ExecutableAlgRemoteWrapper(ExecutableAlgRemote remoteExecutableAlg, boolean exclusive) {
		// TODO Auto-generated constructor stub
		super(remoteExecutableAlg, exclusive);
	}


	@Override
	public Inspector getInspector() {
		// TODO Auto-generated method stub
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {e.printStackTrace();}
		
		return new DescriptionDlg(UIUtil.getFrameForComponent(null), "Inspector", desc);
	}

	
	@Override
	public void setup(Dataset dataset, Object... info) throws RemoteException {
		// TODO Auto-generated method stub
		((ExecutableAlgRemote)remoteAlg).setup(dataset, info);
	}

	
	@Override
	public void setup(Fetcher<Profile> sample, Object... info) throws RemoteException {
		// TODO Auto-generated method stub
		((ExecutableAlgRemote)remoteAlg).setup(sample, info);
	}

	
	@Override
	public void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		((ExecutableAlgRemote)remoteAlg).unsetup();
	}

	
	@Override
	public Object execute(Object input) throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExecutableAlgRemote)remoteAlg).execute(input);
	}

	
	@Override
	public Object getParameter() throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExecutableAlgRemote)remoteAlg).getParameter();
	}

	@Override
	public String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExecutableAlgRemote)remoteAlg).parameterToShownText(parameter, info);
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		if (remoteAlg instanceof ExecutableAlg) {
			ExecutableAlg newAlg = (ExecutableAlg) ((ExecutableAlg)remoteAlg).newInstance();
			return new ExecutableAlgRemoteWrapper(newAlg, exclusive);
		}
		else {
			logger.warn("newInstance() returns itselfs and so does not return new object");
			return this;
		}
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteAlg != null) {
			((ExecutableAlgRemote)remoteAlg).unsetup();
		}

		super.unexport();
	}

	
}
