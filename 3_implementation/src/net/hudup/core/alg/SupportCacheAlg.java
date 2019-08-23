package net.hudup.core.alg;

import java.util.Map;

import net.hudup.core.Util;

/**
 * This interface represents algorithms which support to cache in calculation or in doing some tasks.
 * Whether or not such algorithms support to cache depends on the method {@link #isCached()}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public interface SupportCacheAlg extends Alg {

	
	/**
	 * Supporting cache field name
	 */
	final static String SUPPORT_CACHE_FIELD = "alg_support_cache";

	
	/**
	 * The default setting of whether to support cache. By default cache mode is true because fast recommendation task should run in cache mode.
	 * Cache is used to accelerate estimation process in which the user rating vector in recommendation parameter is made to be empty.
	 */
	final static boolean SUPPORT_CACHE_DEFAULT = true;
	
	
	/**
	 * Task interface for putting result into cache.
	 * @author Loc Nguyen
	 * @version 1.0
	 *
	 */
	interface Task {
		
		/**
		 * Main method to do some task.
		 * @param params specified parameter.
		 * @return result of doing some task.
		 */
		Object perform(Object...params);
	}

	
	/**
	 * Doing some task and put the result into the specified cache.
	 * @param id1 first ID.
	 * @param id2 second ID.
	 * @param cache specified cache.
	 * @param task specified task.
	 * @param params specified parameters.
	 * @return the result which is put into the specified cache.
	 */
	Object cacheTask(int id1, int id2, Map<Integer, Map<Integer, Object>> cache, Task task, Object...params);

		
	/**
	 * Doing some task and put the result into the specified cache. This is static method.
	 * @param alg specified algorithm that support to cache.
	 * @param id1 first ID.
	 * @param id2 second ID.
	 * @param cache specified cache.
	 * @param task specified task.
	 * @param params specified parameters.
	 * @return the result which is put into the specified cache.
	 */
	static Object cacheTask(SupportCacheAlg alg, int id1, int id2, Map<Integer, Map<Integer, Object>> cache, Task task, Object...params) {
		// TODO Auto-generated method stub
		if (!alg.isCached() || id1 < 0 || id2 < 0) //Negative ID indicate unknown user/item that do not exist in dataset.
			return task.perform(params);
		
		Object result = null;
		if (cache.containsKey(id1)) {
			Map<Integer, Object> map1 = cache.get(id1);
			if (map1.containsKey(id2))
				result = map1.get(id2);
			else {
				result = task.perform(params);
				map1.put(id2, result);
				
				if (id1 != id2) {
					Map<Integer, Object> map2 = null;
					if (cache.containsKey(id2))
						map2 = cache.get(id2);
					else {
						map2 = Util.newMap();
						cache.put(id2, map2);
					}
					map2.put(id1, result);
				}
			}
		}
		else if (cache.containsKey(id2)) {
			result = task.perform(params);
			Map<Integer, Object> map2 = cache.get(id2);
			map2.put(id1, result);
			
			if (id1 != id2) {
				Map<Integer, Object> map1 = Util.newMap();
				cache.put(id1, map1);
				map1.put(id2, result);
			}
		}
		else {
			result = task.perform(params);
			Map<Integer, Object> map1 = Util.newMap();
			cache.put(id1, map1);
			map1.put(id2, result);
			
			if (id1 != id2) {
				Map<Integer, Object> map2 = Util.newMap();
				cache.put(id2, map2);
				map2.put(id1, result);
			}
		}
		
		return result;
	}


	/**
	 * Doing some task and put the result into the specified cache.
	 * @param id specified ID.
	 * @param cache specified cache.
	 * @param task specified task.
	 * @param params specified parameters.
	 * @return the result which is put into the specified cache.
	 */
	Object cacheTask(int id, Map<Integer, Object> cache, Task task, Object...params);

	
	/**
	 * Doing some task and put the result into the specified cache. This is static method.
	 * @param alg specified algorithm that support to cache.
	 * @param id specified ID.
	 * @param cache specified cache.
	 * @param task specified task.
	 * @param params specified parameters.
	 * @return the result which is put into the specified cache.
	 */
	static Object cacheTask(SupportCacheAlg alg, int id, Map<Integer, Object> cache, Task task, Object...params) {
		if (!alg.isCached() || id < 0) //Negative ID indicate unknown user/item that do not exist in dataset.
			return task.perform(params);
		else if (cache.containsKey(id))
			 return cache.get(id);
		else {
			Object result = task.perform(params);
			cache.put(id, result);
			return result;
		}
	}

	
	/**
	 * Getting cached mode.
	 * @return true if cached mode is used.
	 */
	boolean isCached();
	
	
	/**
	 * Setting cached mode.
	 * @param cached cached mode.
	 */
	void setCached(boolean cached);


}
