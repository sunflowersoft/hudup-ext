/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.rmi.RemoteException;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;

/**
 * This abstract class represents a knowledge base that contains serialized nut called KBase nut.
 * You uses the nut to wrapper data structure, knowledge base, etc. without regarding how to store such data structure and knowledge base
 * because the nut is serialized and deserialized from file. The weak point of KBase node is not to interchange knowledge base among non-Java applications
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public abstract class KBaseAbstractSerializable extends KBaseAbstract {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * File extension of knowledge base nut.
	 */
	public final static String KBASE_NUT_EXT = ".nut";
	
	
	/**
	 * Serialized nut. You uses the nut to wrapper data structure, knowledge base, etc. without regarding how to store such data structure and knowledge base
	 * because the nut is serialized and deserialized from file.
	 */
	protected KBaseSerializedNut nut = null;
	
	
	/**
	 * Default constructor.
	 */
	public KBaseAbstractSerializable() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void load() throws RemoteException {
		// TODO Auto-generated method stub
		super.load();
		deserializeNut(config.getStoreUri());
	}


	@Override
	public void save(DataConfig storeConfig) throws RemoteException {
		// TODO Auto-generated method stub
		super.save(storeConfig);
		serializeNut(storeConfig.getStoreUri());
	}


	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		super.close();
		
		if (nut != null) {
			try {
				nut.close();
			} catch (Throwable e) {e.printStackTrace();}
			nut = null;
		}
	}


	@Override
	public boolean isEmpty() throws RemoteException {
		// TODO Auto-generated method stub
		return nut != null;
	}


	/**
	 * Reading (deserializing) nut from knowledge base store.
	 * @param storeUri store URI.
	 * @return true if reading (deserializing) is successful. 
	 */
	protected boolean deserializeNut(xURI storeUri) {
		try {
			if (nut != null) {
				nut.close();
				nut = null;
			}
			
			xURI nutUri = storeUri.concat(getName() + KBASE_NUT_EXT);
			UriAdapter adapter = new UriAdapter(nutUri);
			if (!adapter.exists(nutUri)) return false;
			
			ObjectInputStream input = new ObjectInputStream(adapter.getInputStream(nutUri));
			nut = (KBaseSerializedNut)input.readObject();
			input.close();
		}
		catch (Throwable e) {
			e.printStackTrace();
			if (nut != null) {
				try {
					nut.close();
				} catch (Exception e2) {e2.printStackTrace();}
				nut = null;
			}
		}
		
		return nut != null;
	}
	
	
	/**
	 * Writing (serializing) nut to knowledge base store.
	 * @param storeUri store URI.
	 * @return true if writing (serializing) is successful. 
	 */
	protected boolean serializeNut(xURI storeUri) {
		try {
			if (nut == null) return false;
			
			xURI nutUri = storeUri.concat(getName() + KBASE_NUT_EXT);
			UriAdapter adapter = new UriAdapter(nutUri);
			
			ObjectOutputStream output = new ObjectOutputStream(adapter.getOutputStream(nutUri, true));
			output.writeObject(nut);
			output.close();
			
			return true;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return false;
	}
	
	
}
