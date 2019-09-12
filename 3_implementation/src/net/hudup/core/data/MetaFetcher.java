package net.hudup.core.data;

import java.rmi.RemoteException;


/**
 * This class implements the meta fetcher.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 * @param <U> Key type.
 * @param <V> Value type.
 */
public abstract class MetaFetcher<U /*extends Serializable*/, V /*extends Serializable*/> implements Fetcher<V> {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Internal fetcher.
	 */
	protected Fetcher<U> fetcher = null;


	/**
	 * Constructor with specified fetcher as internal fetcher.
	 * @param fetcher specified fetcher as internal fetcher.
	 */
	public MetaFetcher(Fetcher<U> fetcher) {
		super();
		// TODO Auto-generated constructor stub
		
		this.fetcher = fetcher;
	}


	@Override
	public boolean next() throws RemoteException {
		// TODO Auto-generated method stub
		return fetcher.next();
	}

	
	@Override
	public V pick() throws RemoteException {
		// TODO Auto-generated method stub
		U u = fetcher.pick();
		if (u == null)
			return null;
		else
			return create(u);
	}


	@Override
	public void reset() throws RemoteException {
		// TODO Auto-generated method stub
		fetcher.reset();
	}

	
	@Override
	public FetcherMetadata getMetadata() throws RemoteException {
		// TODO Auto-generated method stub
		return fetcher.getMetadata();
	}

	
	@Override
	public void close() throws RemoteException {
		// TODO Auto-generated method stub
		if (fetcher != null)
			fetcher.close();
		
		fetcher = null;
	}
	
	
	/**
	 * Creating element with specified key.
	 * @param u specified key.
	 * @return created element with specified key.
	 */
	public abstract V create(U u);
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not support this method");
	}

	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		
		throw new RuntimeException("Not support this method");
	}

}
