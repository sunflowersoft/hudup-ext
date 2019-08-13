package net.hudup.listener;

import java.io.Serializable;
import java.util.Map;

import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;


/**
 * User session is used to store user information such as account name, password, and privileges on server in a session.
 * In current implementation, it is used by delegators represented {@link AbstractDelegator} classes.
 * This class has the internal variable {@link #userSession} as a map.
 * Each user information is stored as an entry in such map. Every entry is a pair of key and value.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class UserSession implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * The internal map stores user information as entries.
	 */
	protected Map<String, Serializable> userSession = Util.newMap();

	
	/**
	 * Default constructor.
	 */
	public UserSession() {
		
	}
	
	
	/**
	 * Constructor with account name, password, and privileges.
	 * @param account account name.
	 * @param password password.
	 * @param priv privileges.
	 */
	public UserSession(String account, String password, int priv) {
		putAccount(account);
		putPassword(password);
		putPriv(priv);
	}
	
	
	/**
	 * Constructor with account name and privileges.
	 * @param account account name.
	 * @param priv privileges.
	 */
	public UserSession(String account, int priv) {
		putAccount(account);
		putPriv(priv);
	}

	
	/**
	 * Getting the size of this session.
	 * @return size of this session which is the size of the internal map {@link #userSession}.
	 */
	public int size() {
		return userSession.size();
	}
	
	
	/**
	 * Putting the user information as an entry with specified key and value.
	 * @param key specified key.
	 * @param value specified value.
	 */
	public void put(String key, Serializable value) {
		userSession.put(key, value);
	}
	
	
	/**
	 * Getting the value (user information) associated with the specified key.
	 * @param key specified key.
	 * @return value the value (user information) associated with the specified key.
	 */
	public Object get(String key) {
		return userSession.get(key);
	}
	
	
	/**
	 * Putting the account information into this session.
	 * The account information has the key {@link DataConfig#ACCOUNT_NAME_FIELD} and the value is the account name specified by the parameter.
	 * For example, the account &quot;John&quot; is stored as the entry (&quot;account_name&quot;, &quot;John&quot;) in which key is &quot;account_name&quot; and value is &quot;John&quot;.
	 * @param account specified account name.
	 */
	public void putAccount(String account) {
		userSession.put(DataConfig.ACCOUNT_NAME_FIELD, account);
	}
	
	
	/**
	 * Getting the account (name).
	 * The account information has the key {@link DataConfig#ACCOUNT_NAME_FIELD} and the value is the account name.
	 * For example, the account &quot;John&quot; is stored as the entry (&quot;account_name&quot;, &quot;John&quot;) in which key is &quot;account_name&quot; and value is &quot;John&quot;.
	 * @return account account (name).
	 */
	public String getAccount() {
		return (String) userSession.get(DataConfig.ACCOUNT_NAME_FIELD);
	}
	
	
	/**
	 * Putting user password into this session.
	 * The password information has the key {@link DataConfig#ACCOUNT_PASSWORD_FIELD} and the value is password specified by the parameter.
	 * For example, the password &quot;secret&quot; is stored as the entry (&quot;account_password&quot;, &quot;secret&quot;) in which key is &quot;account_password&quot; and value is &quot;secret&quot;.
	 * @param password specified password.
	 */
	public void putPassword(String password) {
		userSession.put(DataConfig.ACCOUNT_PASSWORD_FIELD, password);
	}
	
	
	/**
	 * Getting user password.
	 * The password information has the key {@link DataConfig#ACCOUNT_PASSWORD_FIELD} and the value is the password as text.
	 * For example, the password &quot;secret&quot; is stored as the entry (&quot;account_password&quot;, &quot;secret&quot;) in which key is &quot;account_password&quot; and value is &quot;secret&quot;.
	 * @return password password as text.
	 */
	public String getPassword() {
		return (String) userSession.get(DataConfig.ACCOUNT_PASSWORD_FIELD);
	}
	
	
	/**
	 * Putting user privileges into this session.
	 * The privileges information has the key {@link DataConfig#ACCOUNT_PRIVILEGES_FIELD} and the value is user privileges specified by the parameter.
	 * For example, the administration privilege &quot;1&quot; is stored as the entry (&quot;account_privs&quot;, &quot;1&quot;) in which key is &quot;account_privs&quot; and value is &quot;1&quot;.
	 * @param priv specified user privileges as an integer.
	 */
	public void putPriv(int priv) {
		userSession.put(DataConfig.ACCOUNT_PRIVILEGES_FIELD, priv);
	}
	
	
	/**
	 * Getting user privileges.
	 * The privileges information has the key {@link DataConfig#ACCOUNT_PRIVILEGES_FIELD} and the value is user privileges as an integer.
	 * For example, the administration privilege &quot;1&quot; is stored as the entry (&quot;account_privs&quot;, &quot;1&quot;) in which key is &quot;account_privs&quot; and value is &quot;1&quot;.
	 * @return privileges user privileges as an integer.
	 */
	public int getPriv() {
		return (Integer) userSession.get(DataConfig.ACCOUNT_PRIVILEGES_FIELD);
	}


	/**
	 * Clearing this session, which means that all user information (all entries) is removed from this session.
	 */
	public void clear() {
		userSession.clear();
	}
	
	
}
