package net.hudup.core.alg;

import java.util.Map;

import net.hudup.core.data.DataConfig;

/**
 * This abstract implements basically algorithms which support to cache in calculation or in doing some tasks.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class AbstractSupportCacheAlg extends AbstractAlg implements SupportCacheAlg {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public AbstractSupportCacheAlg() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public Object cacheTask(int id1, int id2, Map<Integer, Map<Integer, Object>> cache, Task task, Object... params) {
		// TODO Auto-generated method stub
		return SupportCacheAlg.cacheTask(this, id1, id2, cache, task, params);
	}

	
	
	@Override
	public Object cacheTask(int id, Map<Integer, Object> cache, Task task, Object... params) {
		// TODO Auto-generated method stub
		return SupportCacheAlg.cacheTask(this, id, cache, task, params);
	}


	@Override
	public boolean isCached() {
		// TODO Auto-generated method stub
		return getConfig().getAsBoolean(SupportCacheAlg.SUPPORT_CACHE_FIELD);
	}

	
	@Override
	public void setCached(boolean cached) {
		// TODO Auto-generated method stub
		getConfig().put(SupportCacheAlg.SUPPORT_CACHE_FIELD, cached);
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		DataConfig config = super.createDefaultConfig();
		config.put(SUPPORT_CACHE_FIELD, SUPPORT_CACHE_DEFAULT);
		return config;
	}


}
