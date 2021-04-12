/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This is the most abstract class for extensive algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class AlgExtAbstract extends AlgAbstract implements AlgExt, AlgExtRemote {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Data sample for testing algorithm.
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * Data sample for this algorithm.
	 */
	protected Object sample = null;
	
	
	/**
     * Default constructor.
     */
    public AlgExtAbstract() {
		super();
	}


	/**
	 * Fetching sample from dataset.
	 * @param dataset specified dataset.
	 * @return sample fetched from dataset.
	 */
	protected abstract Object fetchSample(Dataset dataset);
	
	
    /**
     * In the this version, the setup method is not marked synchronized because it calls {@link #learnStart(Object...)} method.
     * Note, method {@link #learnStart(Object...)} can have thread-supporting loop with synchronization.
     */
	@Override
	public /*synchronized*/ void setup(Dataset dataset, Object...info) throws RemoteException {
		unsetup();
		this.dataset = dataset;
		if (info != null && info.length > 0 && (info[0] instanceof Fetcher<?> || info[0] instanceof Collection<?>)) {
			this.sample = info[0];
			if (info.length ==  1)
				info = null;
			else
				info = Arrays.copyOfRange(info, 1, info.length);
		}
		else
			this.sample = fetchSample(dataset);
		
		if (info != null)
			learnStart(info);
		else
			learnStart();
		
		SetupAlgEvent evt = new SetupAlgEvent(
				this,
				Type.done,
				this.getName(),
				dataset,
				"Learned models: " + this.getDescription());
		fireSetupEvent(evt);
	}

	
	@Override
	public void setup(Fetcher<Profile> sample, Object...info) throws RemoteException {
		List<Object> additionalInfo = Util.newList();
		additionalInfo.add(sample);
		additionalInfo.addAll(Arrays.asList(info));

		setup((Dataset)null, additionalInfo.toArray());
	}


	@Override
	public void setup(Collection<Profile> sample, Object...info) throws RemoteException {
		List<Object> additionalInfo = Util.newList();
		additionalInfo.add(sample);
		additionalInfo.addAll(Arrays.asList(info));

		setup((Dataset)null, additionalInfo.toArray());
	}


	@Override
	public synchronized void unsetup() throws RemoteException {
		try {
			if (dataset != null && sample != null) {
				if (sample instanceof Fetcher<?>) ((Fetcher<?>)sample).close();
			}
		}
		catch(Exception e) {
			LogUtil.trace(e);
		}
		finally {
			sample = null;
		}
	
		if (dataset != null && dataset.isExclusive())
			dataset.clear();
		dataset = null;
	}

	
	@Override
	public synchronized Inspector getInspector() {
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {LogUtil.trace(e);}
		
		return new DescriptionDlg(UIUtil.getFrameForComponent(null), "Inspector", desc);
	}

	
	/**
	 * Getting attribute list of sample.
	 * @param sample specified sample.
	 * @return attribute list of sample.
	 */
	public static AttributeList getSampleAttributeList(Fetcher<Profile> sample) {
		AttributeList attList = null;
		try {
			synchronized (sample) {
				while (sample.next()) {
					Profile profile = sample.pick();
					if (profile == null)
						continue;
					
					attList = profile.getAttRef();
					if (attList != null)
						break;
				}
				sample.reset();
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			attList = null;
		}
		
		return attList;
	}
	
	
	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {AlgExtRemote.class.getName()};
	}

	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		try {
			unsetup();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
	}


}
