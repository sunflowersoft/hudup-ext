package net.hudup.core.alg;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.hudup.core.Util;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This is the most abstract class for executable algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class AbstractExecutableAlg extends AbstractAlg implements ExecutableAlg2 {


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
    public AbstractExecutableAlg() {
		super();
		// TODO Auto-generated constructor stub
	}


	@SuppressWarnings("unchecked")
	@Override
	public synchronized void setup(Dataset dataset, Object...info) throws Exception {
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
	public void remoteSetup(Dataset dataset, Serializable...info) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			setup(dataset, (Object[])info);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RemoteException(e.getMessage(), e);
		}
	}


	@Override
	public void setup(Fetcher<Profile> sample, Object...info) throws Exception {
		// TODO Auto-generated method stub
		List<Object> additionalInfo = Util.newList();
		additionalInfo.add(sample);
		additionalInfo.addAll(Arrays.asList(info));

		setup((Dataset)null, additionalInfo.toArray());
	}


	@Override
	public void remoteSetup(Fetcher<Profile> sample, Serializable... info) throws RemoteException {
		// TODO Auto-generated method stub
		try {
			setup(sample, (Object[])info);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			throw new RemoteException(e.getMessage(), e);
		}
	}


	@Override
	public synchronized void unsetup() {
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

	
	@Override
	public void remoteUnsetup() throws RemoteException {
		// TODO Auto-generated method stub
		unsetup();
	}


	@Override
	public Serializable remoteExecute(Serializable input) throws RemoteException {
		// TODO Auto-generated method stub
		Object result = execute(input);
		if (result instanceof Serializable)
			return (Serializable)result;
		else
			throw new RemoteException("Executed result is not serializable");
	}


	@Override
	public String remoteGetDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return getDescription();
	}


	@Override
	public synchronized void manifest() {
		// TODO Auto-generated method stub
		new DescriptionDlg(UIUtil.getFrameForComponent(null), "Manifest", getDescription()).setVisible(true);;
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
