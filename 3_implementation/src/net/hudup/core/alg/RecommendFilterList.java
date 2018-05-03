package net.hudup.core.alg;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.data.Dataset;


/**
 * This class contains a list of filters, called {@code filter list} as a convention.
 * This class provides utility methods for process such list, for example, getting filter, adding filter, removing filter, etc.
 * Note, filter represented by {@link RecommendFilter} interface specifies tasks which be performed before any actual recommendation tasks.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RecommendFilterList implements Serializable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * An internal list contains a list of filters.
	 */
	protected List<RecommendFilter> filters = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public RecommendFilterList() {
		
	}
	
	
	/**
	 * Getting the size of this list.
	 * @return size of this list.
	 */
	public int size() {
		return filters.size();
	}
	
	
	/**
	 * Getting the filter at specified index.
	 * @param index specified index.
	 * @return filter at specified index.
	 */
	public RecommendFilter get(int index) {
		return filters.get(index);
	}


	/**
	 * Adding the specified filter into this list.
	 * @param filter specified filter.
	 */
	public void add(RecommendFilter filter) {
		filters.add(filter);
	}
	
	
	/**
	 * Adding all filters of the specified list into this list.
	 * @param filterList specified filter list.
	 */
	public void addAll(RecommendFilterList filterList) {
		filters.addAll(filterList.filters);
	}
	
	
	/**
	 * Adding all filters of the specified collection into this list.
	 * @param filters specified collection of filters.
	 */
	public void addAll(Collection<RecommendFilter> filters) {
		this.filters.addAll(filters);
	}

	
	/**
	 * Adding all filters of the specified array into this list.
	 * @param filters specified array of filters.
	 */
	public void addAll(RecommendFilter[] filters) {
		this.filters.addAll(Arrays.asList(filters));
	}

	
	/**
	 * Removing the filter as specified index.
	 * @param index specified index.
	 */
	public void remove(int index) {
		filters.remove(index);
	}
	
	
	/**
	 * Removing the specified filter from this list.
	 * @param filter the specified filter.
	 */
	public void remove(RecommendFilter filter) {
		filters.remove(filter);
	}
	
	
	/**
	 * Clearing this list, which means that removing all filters from this list.
	 */
	public void clear() {
		filters.clear();
	}
	
	
	/**
	 * Setting up (preparing) the recommendation parameter over all filters in this list. The preparation is performed before doing the filtering tasks.
	 * Of course this method calls {@link RecommendFilter#prepare(RecommendParam)} many times.
	 * @param param specified recommendation parameter.
	 */
	public void prepare(RecommendParam param) {
		for (RecommendFilter filter : filters) {
			filter.prepare(param);
		}
	}
	
	
	/**
	 * Performing filtering tasks over all filters of this list.
	 * The filtering tasks are based on specified dataset and filter parameter.
	 * Of course this method calls {@link RecommendFilter#filter(Dataset, RecommendFilterParam)} many times.
	 * @param dataset specified dataset.
	 * @param param specified filter parameter.
	 * @return whether filtering tasks are performed successfully.
	 */
	public boolean filter(Dataset dataset, RecommendFilterParam param) {
		if (dataset == null || param == null)
			return true;
		
		boolean result = true;
		for (RecommendFilter filter : filters) {
			result &= filter.filter(dataset, param);
		}
		
		return result;
	}
	
	
	/**
	 * Performing filtering tasks on all filters of this list.
	 * The filtering tasks are based on specified dataset and many filter parameters.
	 * Of course this method calls {@link RecommendFilter#filter(Dataset, RecommendFilterParam)} many times.
	 * @param dataset specified dataset.
	 * @param params specified collection of filter parameters.
	 * @return whether filtering tasks are performed successfully.
	 */
	public boolean filter(Dataset dataset, Collection<RecommendFilterParam> params) {
		if (dataset == null || params == null | params.size() == 0)
			return true;
		
		boolean result = true;
		for (RecommendFilterParam param : params) {
			result &= filter(dataset, param);
		}
		
		return result;
	}
	
	
	/**
	 * Performing filtering tasks on all filters of this list.
	 * The filtering tasks are based on specified dataset and many filter parameters.
	 * Of course this method calls {@link RecommendFilter#filter(Dataset, RecommendFilterParam)} many times.
	 * @param dataset specified dataset.
	 * @param params specified array of filter parameters.
	 * @return whether filtering tasks are performed successfully.
	 */
	public boolean filter(Dataset dataset, RecommendFilterParam[] params) {
		if (params == null | params.length == 0)
			return true;

		return filter(dataset, Arrays.asList(params));
	}

	
}
