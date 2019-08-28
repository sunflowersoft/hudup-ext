package net.hudup.core.alg;

import java.rmi.RemoteException;

import org.apache.log4j.Logger;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.NetUtil;


/**
 * This is abstract class of {@link Alg} interface.
 * This abstract class gives out default implementation for some methods such as {@link #getConfig()},
 * {@link #resetConfig()} and {@link #createDefaultConfig()}. 
 * It also declares the configuration variable {@link #config}.
 * Note that every {@link Alg} owns a configuration specified by {@link DataConfig} class.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public abstract class AlgAbstract implements Alg, AlgRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Logger of this class.
	 */
	protected final static Logger logger = Logger.getLogger(AlgAbstract.class);

	
	/**
	 * This variable represents configuration of algorithm. It is returned value of {@link #getConfig()} method.
	 * The {@link #resetConfig()} resets it.
	 */
	protected DataConfig config = null;

	
	/**
	 * Exported flag.
	 */
	protected Boolean exported = false;

	
	/**
	 * Default constructor.
	 */
	public AlgAbstract() {
		this.config = createDefaultConfig();
	}

	
	@Override
	public DataConfig getConfig() {
		// TODO Auto-generated method stub
		return config;
	}

	
	/**
	 * Setting the configuration of this algorithm by specified configuration.
	 * @param config specified configuration.
	 */
	public synchronized void setConfig(DataConfig config) {
		this.config = config;
	}
	
	
	@Override
	public synchronized void resetConfig() {
		// TODO Auto-generated method stub
		config.clear();
		config.putAll(createDefaultConfig());
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = new DataConfig();
		config.put(DataConfig.DELAY_UNSETUP, false); //Please pay attention to this code line.
		return config;
	}

	
	@Override
	public String toString() {
		return getName();
	}


	@Override
	public synchronized void export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (exported) {
			if (!exported) {
				NetUtil.RegistryRemote.export(this, serverPort);
				exported = true;
			}
		}
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (exported) {
			if (exported) {
				NetUtil.RegistryRemote.unexport(this);
				exported = false;
			}
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
