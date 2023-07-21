/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate.wrapper.adapter;

import java.rmi.RemoteException;

import net.hudup.core.alg.AllowNullTrainingSet;
import net.hudup.core.alg.ExecuteAsLearnAlgRemoteWrapper;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * The class is a wrapper of remote algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@BaseClass //The annotation is very important which prevent Firer to instantiate the wrapper without referred remote object. This wrapper is not normal algorithm.
public class WRemoteWrapper extends ExecuteAsLearnAlgRemoteWrapper implements W, WRemote, AllowNullTrainingSet {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with specified remote W algorithm.
	 * @param remoteW remote W algorithm.
	 */
	public WRemoteWrapper(WRemote remoteW) {
		super(remoteW);
	}

	
	/**
	 * Constructor with specified remote W algorithm and exclusive mode.
	 * @param remoteW remote W algorithm.
	 * @param exclusive exclusive mode.
	 */
	public WRemoteWrapper(WRemote remoteW, boolean exclusive) {
		super(remoteW, exclusive);
	}

	
	@Override
	public void setup() throws RemoteException {
		((WRemote)this.remoteAlg).setup();
	}


	@Override
	public Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new DescriptionDlg(UIUtil.getDialogForComponent(null), "Inspector", desc);
	}


}
