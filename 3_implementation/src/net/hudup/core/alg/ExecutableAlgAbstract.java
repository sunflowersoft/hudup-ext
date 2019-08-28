package net.hudup.core.alg;

import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.hudup.core.Util;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.Inspector;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This is the most abstract class for executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class ExecutableAlgAbstract extends AlgAbstract implements ExecutableAlg {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Data sample for testing algorithm.
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * Data sample for testing algorithm.
	 */
	protected Fetcher<Profile> sample = null;
	
	
	/**
	 * Holding a list of listeners.
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();
    

	/**
     * Default constructor.
     */
    public ExecutableAlgAbstract() {
		super();
		// TODO Auto-generated constructor stub
	}


	@SuppressWarnings("unchecked")
	@Override
	public synchronized void setup(Dataset dataset, Object...info) throws RemoteException {
		// TODO Auto-generated method stub
		unsetup();
		this.dataset = dataset;
		if (info != null && info.length > 0 && (info[0] instanceof Fetcher<?>))
			this.sample = (Fetcher<Profile>)info[0];
		else
			this.sample = dataset.fetchSample();
		
		learn();
		
		SetupAlgEvent evt = new SetupAlgEvent(
				this,
				Type.done,
				this,
				dataset,
				"Learned models: " + this.getDescription());
		fireSetupEvent(evt);
	}

	
	@Override
	public void setup(Fetcher<Profile> sample, Object...info) throws RemoteException {
		// TODO Auto-generated method stub
		List<Object> additionalInfo = Util.newList();
		additionalInfo.add(sample);
		additionalInfo.addAll(Arrays.asList(info));

		setup((Dataset)null, additionalInfo.toArray());
	}


	/**
	 * Main method to learn parameters. As usual, it is called by {@link #remoteSetup(Dataset, Object...)}.
	 * @param info additional parameter.
	 * @return the parameter to be learned. Return null if learning is failed.
	 * @exception RemoteException if any error occurs.
	 */
	protected abstract Object learn(Object...info) throws RemoteException;

	
	@Override
	public synchronized void unsetup() throws RemoteException {
		try {
			if (dataset != null && sample != null)
				sample.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			sample = null;
		}
	
		if (dataset != null && dataset.isExclusive())
			dataset.clear();
		dataset = null;
	}

	
//	/**
//	 * Getting parameter of the algorithm. Actually, this method call {@link #getParameter()}.
//	 * @return parameter of the algorithm. Return null if the algorithm does not run yet or run failed. 
//	 */
//	protected Object queryParameter() {
//		try {
//			return getParameter();
//		} catch (Throwable e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
	
//    /**
//	 * Getting description of this algorithm.
//	 * This is remote method.
//	 * @return text form of this model.
//	 */
//	protected String getDescription0() {
//		// TODO Auto-generated method stub
//		try {
//			return getDescription();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return "";
//	}


	@Override
	public synchronized Inspector getInspector() {
		// TODO Auto-generated method stub
		String desc = "";
		try {
			desc = getDescription();
		} catch (Exception e) {e.printStackTrace();}
		
		return new DescriptionDlg(UIUtil.getFrameForComponent(null), "Inspector", desc);
	}

	
	@Override
	public void addSetupListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			listenerList.add(SetupAlgListener.class, listener);
		}
	}


	@Override
	public void removeSetupListener(SetupAlgListener listener) throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
	}


	/**
	 * Getting an array of listeners for this EM.
	 * @return array of listeners for this EM.
	 */
	protected SetupAlgListener[] getSetupListeners() {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			return listenerList.getListeners(SetupAlgListener.class);
		}
	}


    /**
     * Firing (issuing) an event from this EM to all listeners. 
     * @param evt event from this EM.
     */
	protected void fireSetupEvent(SetupAlgEvent evt) {
		// TODO Auto-generated method stub
		SetupAlgListener[] listeners = getSetupListeners();
		for (SetupAlgListener listener : listeners) {
			try {
				listener.receivedSetup(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
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
			e.printStackTrace();
			attList = null;
		}
		
		return attList;
	}
	
	
	@Override
	public String queryName() throws RemoteException {
		// TODO Auto-generated method stub
		return getName();
	}


	@Override
	public DataConfig queryConfig() throws RemoteException {
		// TODO Auto-generated method stub
		return getConfig();
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			unsetup();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
	}


}
