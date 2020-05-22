/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.logistic.LogUtil;

/**
 * This class represents a collection of many profiles. It can be considered as a table or storage in memory.
 * It implements directly the interface {@link Profiles}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MemProfiles implements Profiles {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Attribute list of all profiles.
	 */
	private AttributeList attList = new AttributeList();
	
	
	/**
	 * Map of profiles. Each profile is associated with an identifier.
	 */
	private Map<Integer, Profile> profileMap = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	protected MemProfiles() {
//		if ( !(attList instanceof Serializable) || 
//			 !(profileMap instanceof Serializable))
//			throw new RuntimeException("Not serializable class");
	}
	
	
	/**
	 * Constructor with specified attribute list and map of profiles.
	 * @param attList specified attribute list.
	 * @param profileMap specified map of profiles.
	 */
	protected MemProfiles(AttributeList attList, Map<Integer, Profile> profileMap) {
		this();
		this.attList = attList;
		this.profileMap = profileMap;
	}


	@Override
	public Fetcher<Integer> fetchIds() {
		// TODO Auto-generated method stub
		return new MemFetcher<Integer>(profileMap.keySet());
	}


	@Override
	public Profile get(int id) {
		return profileMap.get(id);
	}
	
	
	@Override
	public Fetcher<Profile> fetch() {
		return new MemFetcher<Profile>(profileMap.values());
	}
	
	
	@Override
	public boolean contains(int id) {
		return profileMap.containsKey(id);
	}
	
	
	/**
	 * Putting the specified profile associated specified identifier into this table.
	 * @param id specified identifier.
	 * @param profile specified profile
	 */
	public void put(int id, Profile profile) {
		profileMap.put(id, profile);
	}
	
	
	@Override
	public AttributeList getAttributes() {
		return attList;
	}
	
	
	@Override
	public void clear() {
		attList.clear();
		profileMap.clear();
	}
	
	
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return profileMap.size();
	}


	/**
	 * Making union of profiles in this collection and profiles whose identifiers are specified by the input parameters.
	 * @param profileIds specified identifiers of profiles.
	 * @param possibleIdName specified ID name.
	 */
	public void fillUnion(Collection<Integer> profileIds, String possibleIdName) {
		//Attribute list must not be empty. Fixing bug date: 2019.07.09
		if (attList.size() == 0) {
			attList.add(new Attribute(possibleIdName.trim(), Type.integer));
			attList.setKey(0);
		}
		
		for (int profileId : profileIds) {
			if (profileMap.containsKey(profileId))
				continue;
			
			try {
				Profile profile = new Profile(attList);
				profile.setIdValue(Integer.valueOf(profileId));
				profileMap.put(profileId, profile);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
		}
	}
	
	
	/**
	 * Making union of profiles in this collection and profiles whose identifiers are specified by the input parameters.
	 * @param profileIds specified identifiers of profiles.
	 */
	public void fillUnion(Collection<Integer> profileIds) {
		fillUnion(profileIds, "profileid");
	}
	
	
	/**
	 * Making intersection of profiles in this collection and profiles whose identifiers are specified by the input parameters.
	 * Profiles whose identifiers are different from the specified identifiers are removed from this collection. 
	 * @param profileIds specified identifiers of profiles.
	 */
	public void fillIntersec(Collection<Integer> profileIds) {
		for (int profileId : profileIds) {
			if (!profileMap.containsKey(profileId))
				profileMap.remove(profileId);
		}
	}

	
	/**
	 * Replacing all profiles in this collection by profiles specified by main identifiers.
	 * @param mainIds specified main identifiers.
	 */
	public void fillAs(Collection<Integer> mainIds) {
		Set<Integer> profileIds = Util.newSet();
		profileIds.addAll(profileMap.keySet());
		
		for (int profileId : profileIds) {
			if (!mainIds.contains(profileId))
				profileMap.remove(profileId);
		}
		
		fillUnion(mainIds);
	}

	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		
		AttributeList newAttList = (AttributeList)this.attList.clone();
		Map<Integer, Profile> newProfileMap = Util.newMap();
		
		Set<Integer> ids = this.profileMap.keySet();
		for (int id : ids) {
			Profile profile = this.profileMap.get(id);
			Profile newProfile = (Profile)profile.clone();
			
			newProfile.setAttRef(newAttList);
			newProfileMap.put(id, newProfile);
			
		}
		
		return MemProfiles.assign(newAttList, newProfileMap);
	}
	
	
	@Override
	public Object transfer() {
		AttributeList newAttList = (AttributeList)this.attList.transfer();
		Map<Integer, Profile> newProfileMap = Util.newMap();
		newProfileMap.putAll(this.profileMap);
		
		return MemProfiles.assign(newAttList, newProfileMap);
	}
	
	
	/**
	 * Getting a sub-collection by the specified identifiers.
	 * @param ids specified identifiers.
	 * @return sub {@link MemProfiles} by the specified identifiers.
	 */
	public MemProfiles getSub(Collection<Integer> ids) {
		Map<Integer, Profile> newProfileMap = Util.newMap();
		for (int id : ids) {
			Profile profile = this.profileMap.get(id);
			if (profile != null)
				newProfileMap.put(id, profile);
		}
		return MemProfiles.assign(this.attList, newProfileMap);
	}
	
	
	/**
	 * Enhancing the performance of this collection.
	 */
	public void enhance() {
		profileMap.keySet();
		profileMap.values();
	}
	
	
	/**
	 * This static method creates an empty collection of profiles.
	 * @return empty {@link MemProfiles}.
	 */
	public static MemProfiles createEmpty() {
		return new MemProfiles();
	}
	
	
	/**
	 * This static method creates an empty collection of profiles.
	 * The resulted collection has no profile but its attribute list has one ID attribute with specified name and type.
	 * @param idName specified name of ID attribute.
	 * @param idType specified type of ID attribute.
	 * @return {@link MemProfiles} as the resulted collection has no profile but its attribute list has one ID attribute with specified name and type.
	 */
	public static MemProfiles createEmpty(String idName, Type idType) {
		Map<Integer, Profile> profileMap = Util.newMap();
		AttributeList attList = AttributeList.create(
				new Attribute[] { new Attribute(idName, idType)});
		attList.setKey(0);
		
		return MemProfiles.assign(attList, profileMap);
	}

	
	/**
	 * This static method creates a collection with specified attribute list and profile map.
	 * @param attList specified attribute list.
	 * @param profileMap specified profile map.
	 * @return {@link MemProfiles} as a collection with specified attribute list and profile map.
	 */
	public static MemProfiles assign(AttributeList attList, Map<Integer, Profile> profileMap) {
		return new MemProfiles(attList, profileMap);
	}
	

	/**
	 * This static method creates a collection from the specified fetcher.
	 * @param fetcher specified fetcher.
	 * @param closeFetcher if true the, the specified fetcher is closed after the collection was created.
	 * @return {@link MemProfiles} as a collection from the specified fetcher.
	 */
	public static MemProfiles create(Fetcher<Profile> fetcher, boolean closeFetcher) {
		
		Map<Integer, Profile> profileMap = Util.newMap();
		AttributeList attList = null;
		try {
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				int profileId = profile.getIdValueAsInt();
				if (profileId >= 0) {
					attList = profile.getAttRef();
					profileMap.put(profileId, profile);
				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (closeFetcher)
					fetcher.close();
				else
					fetcher.reset();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				LogUtil.trace(e);
			}
		}
	
		
		if (attList == null || profileMap.size() == 0)
			return MemProfiles.createEmpty();
		else
			return new MemProfiles(attList, profileMap);
	}


}
