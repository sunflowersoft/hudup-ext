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
import net.hudup.core.alg.DuplicatableAlg;
import net.hudup.core.alg.ExecuteAsLearnAlgAbstract;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.NullPointer;
import net.hudup.core.evaluate.wrapper.WDoEvent;
import net.hudup.core.evaluate.wrapper.WInfoEvent;
import net.hudup.core.evaluate.wrapper.WListener;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class implements partially the algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class WAbstract extends ExecuteAsLearnAlgAbstract implements W, WRemote, WListener, AllowNullTrainingSet, DuplicatableAlg {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal internal algorithm.
	 */
	protected net.hudup.core.evaluate.wrapper.W w = null;
	
	
	/**
	 * Default constructor.
	 */
	public WAbstract() {
		w = createW();
		
		try {
			config.putAll(Util.toConfig(w.getConfig()));
		} catch (Throwable e) {Util.trace(e);}
		
		try {
			w.addListener(this);
		} catch (Throwable e) {Util.trace(e);}
	}

	
	@Override
	protected Object fetchSample(Dataset dataset) {
		return dataset != null ? dataset.fetchSample2() : null;
	}


	@Override
	public void setup() throws RemoteException {
		try {
			super.setup(new NullPointer());
		}
		catch (Throwable e) {
			Util.trace(e);
		}
	}
	

	/**
	 * Create PSO instance.
	 * @return PSO instance.
	 */
	protected abstract net.hudup.core.evaluate.wrapper.W createW();
	
	
	@Override
	public Object getParameter() throws RemoteException {
		return w;
	}

	
	@Override
	public String parameterToShownText(Object parameter, Object... info) throws RemoteException {
		if (parameter == null)
			return "";
		else if (!(parameter instanceof net.hudup.core.evaluate.wrapper.W))
			return "";
		else
			return ((net.hudup.core.evaluate.wrapper.W)parameter).toString();
	}

	
	@Override
	public synchronized String getDescription() throws RemoteException {
		return parameterToShownText(getParameter());
	}

	
	@Override
	public Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {Util.trace(e);}
		
		return new DescriptionDlg(UIUtil.getDialogForComponent(null), "Inspector", desc);
	}

	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {WRemote.class.getName()};
	}

	
	@Override
	public void receivedInfo(WInfoEvent evt) throws RemoteException {

	}

	
	@Override
	public void receivedDo(WDoEvent evt) throws RemoteException {
		if (evt.getType() == WDoEvent.Type.doing) {
			fireSetupEvent(new SetupAlgEvent(this, Type.doing, getName(), null,
				evt.getLearnResult(),
				evt.getProgressStep(), evt.getProgressTotalEstimated()));
		}
		else if (evt.getType() == WDoEvent.Type.done) {
			fireSetupEvent(new SetupAlgEvent(this, Type.done, getName(), null,
					evt.getLearnResult(),
					evt.getProgressStep(), evt.getProgressTotalEstimated()));
		}
	}


	@Override
	public void setName(String name) {
		getConfig().put(DUPLICATED_ALG_NAME_FIELD, name);
	}

	
}
