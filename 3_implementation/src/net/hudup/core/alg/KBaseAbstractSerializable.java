package net.hudup.core.alg;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import net.hudup.core.data.DataConfig;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;

/**
 * This abstract class represents a knowledge base that contains serialized nut.
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
	 * Serialized nut.
	 */
	protected KBaseSerializedNut nut = null;
	
	
	/**
	 * Default constructor.
	 */
	public KBaseAbstractSerializable() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public void load() {
		// TODO Auto-generated method stub
		super.load();
		deserializeNut();
	}


	@Override
	public void export(DataConfig storeConfig) {
		// TODO Auto-generated method stub
		super.export(storeConfig);
		serializeNut();
	}


	@Override
	public void close() {
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
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return nut != null;
	}


//	/**
//	 * Instantiate knowledge base nut.
//	 * @return instance of knowledge base nut.
//	 */
//	protected abstract KBaseSerializedNut newNut();
	
	
	/**
	 * Reading (deserializing) nut from knowledge base store.
	 * @return true if reading (deserializing) is successful. 
	 */
	protected boolean deserializeNut() {
		try {
			if (nut != null) {
				nut.close();
				nut = null;
			}
			
			xURI nutUri = config.getStoreUri().concat(getName() + KBASE_NUT_EXT);
			UriAdapter adapter = new UriAdapter(nutUri);
			
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
	 * @return true if writing (serializing) is successful. 
	 */
	protected boolean serializeNut() {
		try {
			if (nut == null) return false;
			
			xURI nutUri = config.getStoreUri().concat(getName() + KBASE_NUT_EXT);
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
