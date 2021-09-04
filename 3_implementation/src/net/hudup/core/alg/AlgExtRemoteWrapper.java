/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Collection;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * The class is a wrapper of remote extensive algorithm. This is a trick to use RMI object but not to break the defined programming architecture.
 * In fact, RMI mechanism has some troubles or it it affect negatively good architecture.
 * For usage, an algorithm as REM will has a pair: REM stub (remote extensive algorithm) and REM wrapper (normal extensive algorithm).
 * The server creates REM stub (remote extensive algorithm) and the client creates and uses the REM wrapper as normal extensive algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class AlgExtRemoteWrapper extends AlgRemoteWrapper implements AlgExt, AlgExtRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Constructor with specified remote extensive algorithm.
	 * @param remoteAlgExt remote extensive algorithm.
	 */
	public AlgExtRemoteWrapper(AlgExtRemote remoteAlgExt) {
		super(remoteAlgExt);
	}

	
	/**
	 * Constructor with specified remote extensive algorithm and exclusive mode.
	 * @param remoteAlgExt remote extensive algorithm.
	 * @param exclusive exclusive mode.
	 */
	public AlgExtRemoteWrapper(AlgExtRemote remoteAlgExt, boolean exclusive) {
		super(remoteAlgExt, exclusive);
	}


	@Override
	public Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new DescriptionDlg(UIUtil.getDialogForComponent(null), "Inspector", desc);
	}

	
	@Override
	public void setup(Dataset dataset, Object... info) throws RemoteException {
		((AlgExtRemote)remoteAlg).setup(dataset, info);
	}

	
	@Override
	public void setup(Fetcher<Profile> sample, Object... info) throws RemoteException {
		((AlgExtRemote)remoteAlg).setup(sample, info);
	}

	
	@Override
	public void setup(Collection<Profile> sample, Object... info) throws RemoteException {
		((AlgExtRemote)remoteAlg).setup(sample, info);
	}


	@Override
	public void unsetup() throws RemoteException {
		((AlgExtRemote)remoteAlg).unsetup();
	}

	
	@Override
	public Object getParameter() throws RemoteException {
		return ((AlgExtRemote)remoteAlg).getParameter();
	}

	@Override
	public String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		return ((AlgExtRemote)remoteAlg).parameterToShownText(parameter, info);
	}

	
	@Override
	public synchronized void unexport() throws RemoteException {
		if (exclusive && remoteAlg != null) {
			((AlgExtRemote)remoteAlg).unsetup();
		}

		super.unexport();
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {AlgExtRemote.class.getName()};
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		if (remoteAlg instanceof Alg)
			return ((Alg)remoteAlg).createDefaultConfig();
		else {
			LogUtil.warn("Wrapper of remote extensive algorithm does not support createDefaultConfig()");
			return null;
		}
	}

	
}
