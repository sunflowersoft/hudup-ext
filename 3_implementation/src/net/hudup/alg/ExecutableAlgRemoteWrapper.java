package net.hudup.alg;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.ExecutableAlg;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.data.DataConfig;
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
public class ExecutableAlgRemoteWrapper implements ExecutableAlg {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(ExecutableAlgRemoteWrapper.class);


	/**
	 * Exclusive mode.
	 */
	protected boolean exclusive = true;
	
	
	/**
	 * Internal remote executable algorithm.
	 */
	protected ExecutableAlg remoteExecutableAlg = null;
	
	
	/**
	 * Constructor with specified executable algorithm.
	 * @param remoteExecutableAlg remote executable algorithm.
	 */
	public ExecutableAlgRemoteWrapper(ExecutableAlg remoteExecutableAlg) {
		// TODO Auto-generated constructor stub
		this(remoteExecutableAlg, true);
	}

	
	/**
	 * Constructor with specified executable algorithm and exclusive mode.
	 * @param ExecutableAlg remote executable algorithm.
	 * @param exclusive exclusive mode.
	 */
	public ExecutableAlgRemoteWrapper(ExecutableAlg remoteExecutableAlg, boolean exclusive) {
		// TODO Auto-generated constructor stub
		this.remoteExecutableAlg = remoteExecutableAlg;
		this.exclusive = exclusive;
	}

	
	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		try {
			return remoteExecutableAlg.queryConfig();
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

	
	@Override
	public void resetConfig() {
		// TODO Auto-generated method stub
		logger.error("resetConfig() not supported");
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		logger.warn("createDefaultConfig() not supported");
		return null;
	}

	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		try {
			return remoteExecutableAlg.queryName();
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		logger.warn("newInstance() returns itselfs and so does not return new object");
		return this;
	}

	
	@Override
	public String queryName() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteExecutableAlg.queryName();
	}

	
	@Override
	public DataConfig queryConfig() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteExecutableAlg.queryConfig();
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteExecutableAlg.getDescription();
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
		remoteExecutableAlg.setup(dataset, info);
	}

	
	@Override
	public void setup(Fetcher<Profile> sample, Object... info) throws RemoteException {
		// TODO Auto-generated method stub
		remoteExecutableAlg.setup(sample, info);
	}

	
	@Override
	public void unsetup() throws RemoteException {
		// TODO Auto-generated method stub
		remoteExecutableAlg.unsetup();
	}

	
	@Override
	public Object execute(Object input) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteExecutableAlg.execute(input);
	}

	
	@Override
	public Object getParameter() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteExecutableAlg.getParameter();
	}

	@Override
	public String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteExecutableAlg.parameterToShownText(parameter, info);
	}

	
	@Override
	public void addSetupListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteExecutableAlg.addSetupListener(listener);
	}

	
	@Override
	public void removeSetupListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		remoteExecutableAlg.removeSetupListener(listener);
	}

	
	@Override
	public void export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
		logger.warn("export(int) not supported");
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteExecutableAlg != null) {
			remoteExecutableAlg.unsetup();
			remoteExecutableAlg.unexport();
			remoteExecutableAlg = null;
		}
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			unexport();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
