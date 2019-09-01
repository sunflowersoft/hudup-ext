package net.hudup.core.evaluate;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;


/**
 * This class represents a list of metrics with note that these metrics are not wrappers.
 * Such metrics are called non-wrapper metrics.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NoneWrapperMetricList implements Cloneable, Serializable {

	
	/**
	 * Serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * List of non-wrapper metrics. This list must be serializable in remote call.
	 */
	protected List<Metric> mlist = Util.newList();
	
	
	/**
	 * Deault constructor
	 */
	public NoneWrapperMetricList() {
		
	}
	
	
	/**
	 * Getting size of this metric list.
	 * @return size of metric list.
	 */
	public int size() {
		return mlist.size();
	}
	
	
	/**
	 * Getting the metric at specified index.
	 * @param index specified index.
	 * @return {@link Metric} at specified index.
	 */
	public Metric get(int index) {
		return mlist.get(index);
	}
	
	
	/**
	 * Getting the internal list of metrics.
	 * @return internal list of metric.
	 */
	public List<Metric> list() {
		return mlist;
	}
	
	
	/**
	 * Adding a specified metric.
	 * @param metric specified metric.
	 * @return whether adding successfully
	 */
	public boolean add(Metric metric) {
		if (metric instanceof MetricWrapper)
			return false;
		return mlist.add(metric);
	}
	
	
	/**
	 * Adding the specified collection of metrics.
	 * @param metrics specified collection of metrics.
	 */
	public void addAll(Collection<Metric> metrics) {
		for (Metric metric : metrics)
			add(metric);
	}
	
	
	/**
	 * Remove the metric at specified index.
	 * @param index specified index.
	 * @return removed {@link MetaMetric}.
	 */
	public Metric remove(int index) {
		return mlist.remove(index);
	}
	
	
	/**
	 * Removing the specified metric from this list.
	 * @param metric specified metric.
	 * @return whether removal is successfully
	 */
	public boolean remove(Metric metric) {
		return mlist.remove(metric);
	}
	
	
	/**
	 * Clearing this metric list.
	 */
	public void clear() {
		mlist.clear();
	}
	
	
	/**
	 * Sorting this metric list.
	 * @return sorted this metric list.
	 */
	public NoneWrapperMetricList sort() {
		Collections.sort(mlist, new Comparator<Metric>() {

			@Override
			public int compare(Metric metric1, Metric metric2) {
				// TODO Auto-generated method stub
				return metric1.getName().compareTo(metric2.getName());
			}
		});
		
		return this;
	}
	
	
	@Override
	public Object clone() {
		NoneWrapperMetricList result = new NoneWrapperMetricList();
		
		List<MetaMetric> metaMetricList = Util.newList();
		for (Metric metric : mlist) {
			if (metric instanceof MetaMetric)
				metaMetricList.add( (MetaMetric) metric);
			else
				result.add( (Metric) metric.newInstance());
		}
		
		List<Metric> normalMetricList = Util.newList();
		normalMetricList.addAll(result.mlist);
		
		for (MetaMetric metaMetric : metaMetricList) {
			MetaMetric newMetaMetric = (MetaMetric) metaMetric.newInstance();
			
			String[] metaNameList = metaMetric.getMetaNameList();
			List<Metric> metaList = Util.newList();
			
			for (String metaName : metaNameList) {
				for (Metric m : normalMetricList) {
					if (m.getName().equals(metaName)) {
						metaList.add(m);
						break;
					}
				}
			}
			if (metaList.size() == metaNameList.length) {
				try {
					newMetaMetric.setup(metaList.toArray());
					result.add(newMetaMetric);
				}
				catch (Exception e) {e.printStackTrace();}
			}
			
			
		}
		
		
		return result;
	}
	
	
	/**
	 * Extracting {@link NoneWrapperMetricList} from a specified register table.
	 * @param rTable specified register table.
	 * @return {@link NoneWrapperMetricList} extracted from {@link RegisterTable}.
	 */
	public static NoneWrapperMetricList extract(RegisterTable rTable) {
		List<Alg> algList = rTable.getAlgList();
		
		NoneWrapperMetricList metricList = new NoneWrapperMetricList();
		for (Alg alg : algList) {
			if (alg instanceof Metric)
				metricList.add((Metric)alg);
		}
		
		return metricList;
	}
	
	
}
