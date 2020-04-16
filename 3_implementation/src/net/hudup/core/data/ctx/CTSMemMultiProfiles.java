/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ctx;

import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;

/**
 * This class represents the memory profiles of context templates.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class CTSMemMultiProfiles implements CTSMultiProfiles {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Internal map of profiles.
	 */
	protected Map<Integer, Profiles> map = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	private CTSMemMultiProfiles() {
		
	}
	
	
	@Override
	public int size() {
		return map.size();
	}
	
	
	@Override
	public boolean contains(int ctxTemplateId) {
		return map.containsKey(ctxTemplateId);
	}
	
	
	@Override
	public Profiles get(int ctxTemplateId) {
		return map.get(ctxTemplateId);
	}
	
	
	@Override
	public void put(int ctxTemplateId, Profiles profiles) {
		map.put(ctxTemplateId, profiles);
	}
	
	
	@Override
	public Set<Integer> templateIds() {
		return map.keySet();
	}
	
	
	@Override
	public void clear() {
		map.clear();
	}
	
	
	@Override
	public Object transfer() {
		CTSMemMultiProfiles ctsProfiles = create();
		Set<Integer> templateIds = map.keySet();
		for (int templateId : templateIds) {
			ctsProfiles.put(templateId, (Profiles)map.get(templateId).transfer());
		}
		
		return ctsProfiles;
	}
	
	
	@Override
	public Profile profileOf(int ctxTemplateId, ContextValue ctxValue) {
		if (ctxValue == null || !ctxValue.isQuantized() || !contains(ctxTemplateId))
			return null;
		
		Profiles profiles = get(ctxTemplateId);
		int id = (int) ctxValue.getQuantizedValue();
		if (profiles.contains(id))
			return profiles.get(id);
		else
			return null;
	}
	
	
	@Override
	public Profile profileOf(Context context) {
		if (context == null || context.getTemplate() == null || context.getValue() == null)
			return null;
		
		return profileOf(context.getTemplate().getId(), context.getValue());
		
	}
	
	
	@Override
	public Object clone() {
		CTSMemMultiProfiles ctsProfiles = create();
		Set<Integer> templateIds = map.keySet();
		for (int templateId : templateIds) {
			ctsProfiles.put(templateId, (Profiles)map.get(templateId).clone());
		}
		
		return ctsProfiles;
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		clear();
	}


	/**
	 * Create the empty memory profiles of context templates.
	 * @return empty memory profiles of context templates.
	 */
	public static CTSMemMultiProfiles create() {
		return new CTSMemMultiProfiles();
	}
	
	
}
