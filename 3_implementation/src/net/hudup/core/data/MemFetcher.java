/**
 * 
 */
package net.hudup.core.data;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class implements a fetcher {@link Fetcher} whose data elements are stored in memory.
 * Actually, these data elements are stored in its internal collection {@link #data}.
 * As a convention, it is called memory fetcher. 
 *  
 * @param <E> type of data elements iterated by this memory fetcher.
 * @author Loc Nguyen
 * @version 10.0
 */
public class MemFetcher<E> implements Fetcher<E> {
	
	
	/**
	 * The internal collection stores elements over which this memory fetcher iterates.
	 */
	private Collection<E> data = null;
	
	
	/**
	 * The iterator browses elements stored in the internal collection {@link #data}. This is the iterator of such collection.
	 * In fact, the memory fetcher uses this iterator to browse data elements.
	 */
	private Iterator<E> iterator = null;
	
	
	/**
	 * The current data element.
	 */
    private E current = null;

    
    /**
     * Meta-data which is additional information of this memory fetcher.
     */
    private FetcherMetadata metadata = null;
    
    
	/**
	 * Constructor with specified data collection. The internal data collection is assigned by the specified collection.
	 * @param data specified data collection
	 */
	public MemFetcher(Collection<E> data) {
		update(data);
	}
	
	
	/**
	 * Default constructor with empty data collection.
	 */
	public MemFetcher() {
		List<E> data = Util.newList();
		update(data);
	}
	
	
	/**
	 * Constructor with other fetcher. This constructor fill in the internal collection {@link #data} by elements retrieved from the other fetcher.
	 * @param fetcher other fetcher.
	 * @param autoClose if true, the other fetcher will be closed after the constructor is completed. 
	 */
	public MemFetcher(Fetcher<E> fetcher, boolean autoClose) {
		List<E> data = Util.newList();
		FetcherUtil.fillCollection(data, fetcher, false);
		try {
			if (autoClose)
				fetcher.close();
			else
				fetcher.reset();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		update(data);
	}
	
	
	/**
	 * Initializing this memory fetcher from specified data collection.
	 * Constructors calls this method to construct this memory fetcher.
	 * @param data specified data collection.
	 */
	private void update(Collection<E> data) {
		this.data = data;
		this.iterator = data.iterator();
		this.current = null;
		
		this.metadata = new FetcherMetadata();
		this.metadata.setSize(this.data.size());
	}

	
	@Override
	public boolean next() {
		// TODO Auto-generated method stub
		if (iterator.hasNext()) {
			current = iterator.next();
			return true;
		}
		else {
			current = null;
			return false;
		}
	}


	@Override
	public E pick() {
		// TODO Auto-generated method stub
		return current;
	}


	@Override
	public void reset() {
		// TODO Auto-generated method stub
		current = null;
		iterator = data.iterator();
	}
	
	
	@Override
	public void close() {
		// TODO Auto-generated method stub
		data = null;
		current = null;
		iterator = null;
		metadata = null;
	}


	@Override
	public FetcherMetadata getMetadata() {
		return metadata;
	}
	
	
	/**
	 * This static method creates an empty memory fetcher which has no element.
	 * @return empty memory fetcher.
	 */
	public static <E> MemFetcher<E> createEmpty() {
		Set<E> set = Util.newSet();
		return new MemFetcher<E>(set);
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		StringBuffer buffer = new StringBuffer();
		
		int i = 0;
		for (E el : data) {
			if (i > 0)
				buffer.append("\n");
			
			String row = el.getClass().toString() + TextParserUtil.LINK_SEP;
			if (el instanceof TextParsable)
				buffer.append( row + ((TextParsable)el).toText());
			else
				buffer.append(row + el.toString());
					
			i++;
		}
		
		
		return buffer.toString();
	}


	@SuppressWarnings("unchecked")
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		try {
			List<E> dataList = Util.newList();
			List<String> textList = TextParserUtil.split(spec, "\n", null);
			for (String text : textList) {
				int index = text.indexOf(TextParserUtil.LINK_SEP);
				String className = text.substring(0, index);
				String value = text.substring(index + 1);
				Object element = TextParserUtil.parseObjectByClass(value, className);
				
				if (element != null)
					dataList.add((E)element);
			}
			
			update(dataList);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
}
