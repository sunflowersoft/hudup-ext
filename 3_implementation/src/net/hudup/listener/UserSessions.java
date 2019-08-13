package net.hudup.listener;

import java.util.Map;

import net.hudup.core.Util;

/**
 * Class contains user sessions.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
public class UserSessions {

	
	/**
	 * Map of user sessions. Keys in the maps are accounts.
	 */
	protected Map<String, UserSession> sessions = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public UserSessions() {
		// TODO Auto-generated constructor stub
	}
	
	
	/**
	 * Putting an specified session of given account.
	 * @param account given account.
	 * @param session specified session of given account.
	 * @return previous user session.
	 */
	public UserSession put(String account, UserSession session) {
		return sessions.put(account, session);
	}

	
	/**
	 * Getting session of a given account.
	 * @param account given account.
	 * @return session of a given account.
	 */
	public UserSession get(String account) {
		return sessions.get(account);
	}
	
	
	/**
	 * Clearing this sessions.
	 */
	public void clear() {
		sessions.clear();
	}
	
	
}
