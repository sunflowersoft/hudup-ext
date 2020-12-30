/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;

/**
 * This abstract class represents a knowledge base that contains serialized nut called KBase nut.
 * You uses the nut to wrapper data structure, knowledge base, etc. without regarding how to store such data structure and knowledge base
 * because the nut is serialized and deserialized from file. The weak point of KBase node is not to interchange knowledge base among non-Java applications.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class KBaseSerializableAbstract extends KBaseAbstract implements KBaseSerializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Serialized nut. You uses the nut to wrapper data structure, knowledge base, etc. without regarding how to store such data structure and knowledge base
	 * because the nut is serialized and deserialized from file.
	 */
	protected KBaseSerializedNut nut = null;

	
	/**
	 * Default constructor.
	 */
	public KBaseSerializableAbstract() {
		super();
	}

	
	@Override
	public void load() throws RemoteException {
		super.load();
		deserializeNut(config.getStoreUri());
	}


	@Override
	public void save(DataConfig storeConfig) throws RemoteException {
		super.save(storeConfig);
		serializeNut(storeConfig.getStoreUri());
	}


	@Override
	public void close() throws Exception {
		super.close();
		
		if (nut != null) {
			try {
				nut.close();
			} catch (Throwable e) {LogUtil.trace(e);}
			nut = null;
		}
	}


	@Override
	public boolean isEmpty() throws RemoteException {
		return nut != null;
	}


	@Override
	public boolean deserializeNut(xURI storeUri) {
		KBaseSerializedNut nut = deserializeNut(getName(), storeUri);
		if (nut == null) return false;

		try {
			this.nut.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		this.nut = nut;

		return true;
	}
	
	
	@Override
	public boolean serializeNut(xURI storeUri) {
		return serializeNut(nut, getName(),  storeUri);
	}
	
	
	/**
	 * Reading (deserializing) nut from knowledge base store.
	 * @param nutName nut name.
	 * @param storeUri store URI.
	 * @return deserialized nut. Returning null if deserializing is not successful.
	 */
	public static KBaseSerializedNut deserializeNut(String nutName, xURI storeUri) {
		try {
			xURI nutUri = storeUri.concat(nutName + KBASE_NUT_EXT);
			UriAdapter adapter = new UriAdapter(nutUri);
			if (!adapter.exists(nutUri)) return null;
			
			ObjectInputStream input = new ObjectInputStream(adapter.getInputStream(nutUri));
			KBaseSerializedNut nut = (KBaseSerializedNut)input.readObject();
			input.close();
			
			return nut;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Writing (serializing) nut to knowledge base store.
	 * @param nut nut will be serialized.
	 * @param nutName nut name.
	 * @param storeUri store URI.
	 * @return true if writing (serializing) is successful. 
	 */
	public static boolean serializeNut(KBaseSerializedNut nut, String nutName, xURI storeUri) {
		try {
			if (nut == null) return false;
			
			xURI nutUri = storeUri.concat(nutName + KBASE_NUT_EXT);
			UriAdapter adapter = new UriAdapter(nutUri);
			
			ObjectOutputStream output = new ObjectOutputStream(adapter.getOutputStream(nutUri, true));
			output.writeObject(nut);
			output.close();
			
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}
	

}
