package net.hudup.core.alg;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.BaseClass;

/**
 * The class is a wrapper of remote algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote algorithm) and REM wrapper (normal algorithm).
 * The server creates REM stub (remote algorithm) and the client creates and uses the REM wrapper as normal algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class AlgRemoteWrapper implements Alg, AlgRemote, AutoCloseable {

	
	/**
	 * Defautl serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(AlgRemoteWrapper.class);


	/**
	 * Exclusive mode.
	 */
	protected boolean exclusive = true;
	
	
	/**
	 * Internal remote executable algorithm.
	 */
	protected AlgRemote remoteAlg = null;
	
	
	/**
	 * Constructor with specified remote algorithm.
	 * @param remoteAlg remote algorithm.
	 */
	public AlgRemoteWrapper(AlgRemote remoteAlg) {
		// TODO Auto-generated constructor stub
		this(remoteAlg, true);
	}

	
	/**
	 * Constructor with specified remote algorithm and exclusive mode.
	 * @param ExecutableAlg remote algorithm.
	 * @param exclusive exclusive mode.
	 */
	public AlgRemoteWrapper(AlgRemote remoteAlg, boolean exclusive) {
		// TODO Auto-generated constructor stub
		this.remoteAlg = remoteAlg;
		this.exclusive = exclusive;
	}

	
	@Override
	public String queryName() throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExecutableAlgRemote)remoteAlg).queryName();
	}

	
	@Override
	public DataConfig queryConfig() throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExecutableAlgRemote)remoteAlg).queryConfig();
	}

	
	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		try {
			return remoteAlg.queryConfig();
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
			return remoteAlg.queryName();
		} catch (Exception e) { e.printStackTrace(); }
		
		return null;
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return ((ExecutableAlgRemote)remoteAlg).getDescription();
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		logger.warn("newInstance() returns itselfs and so does not return new object");
		return this;
	}

	
	@Override
	public void export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
		logger.error("export() rnot supported");
	}


	@Override
	public void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exclusive && remoteAlg != null) {
			remoteAlg.unexport();
			remoteAlg = null;
		}
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			unexport();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
