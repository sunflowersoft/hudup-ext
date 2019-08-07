package net.hudup.listener;

import java.io.Serializable;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.data.HiddenText;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class called remote information contains information of remote server or service.
 * In current implementation, it contains host and port of the remote server (service) and account name / password to access such server (service).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfo implements Serializable, Cloneable, TextParsable {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Host of remote server (service).
	 */
	public String host = "localhost";
	
	
	/**
	 * Port of remote server (service).
	 */
	public int port = 0;

	
	/**
	 * Account name to access remote server (service).
	 */
	public String account = "";

	
	/**
	 * Password to access remote server (service).
	 */
	public HiddenText password = new HiddenText("");
	
	
	/**
	 * Default constructor.
	 */
	public RemoteInfo() {
		
	}
	
	
	/**
	 * Constructor with host and port of the remote server (service) and account name / password to access such server (service).
	 * 
	 * @param host specified host of remote server (service).
	 * @param port specified port of remote server (service).
	 * @param account specified account name to access remote server (service).
	 * @param password specified password to access remote server (service).
	 */
	public RemoteInfo(String host, int port, String account, String password) {
		this.host = host;
		this.port = port;
		this.account = account;
		this.password = new HiddenText(password);
	}

	
	/**
	 * Constructor with host and port of the remote server (service) and account name / password to access such server (service).
	 * 
	 * @param host specified host of remote server (service).
	 * @param port specified port of remote server (service).
	 * @param account specified account name to access remote server (service).
	 * @param password specified password to access remote server (service) by {@link HiddenText}.
	 */
	public RemoteInfo(String host, int port, String account, HiddenText password) {
		this.host = host;
		this.port = port;
		this.account = account;
		this.password = password;
	}

	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return host + TextParserUtil.MAIN_SEP + 
				port + TextParserUtil.MAIN_SEP +
				account + TextParserUtil.MAIN_SEP +
				Util.getCipher().encrypt(password.getText());
	}

	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new RemoteInfo(host, port, account, password.getText());
	}



	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toText();
	}



	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		reset();
		if (spec.isEmpty())
			return;
		
		int begin = 0;
		int next = 0;
		int index = 0;
		while (true) {
			next = spec.indexOf(TextParserUtil.MAIN_SEP, begin);
			String part = "";
			if (next == -1)
				part = spec.substring(begin);
			else
				part = spec.substring(begin, next);
				
			if (!part.isEmpty()) {
				switch (index) {
				case 0:
					host = part;
					break;
				case 1:
					port = Integer.parseInt(part);
					break;
				case 2:
					account = part;
					break;
				case 3:
					password = new HiddenText(Util.getCipher().decrypt(part));
					break;
				}
				index ++;
			}
			if (next == -1 || next >= spec.length() - 1 || index > 4)
				break;
			
			begin = next + 1;
		}
	}
	
	
	/**
	 * Reseting this remote information. All fields are set by default values.
	 * Remote host is &quot;localhost&quot;. Port is 0. Account name and password are empty strings.  
	 */
	protected void reset() {
		host = "localhost";
		port = 0;
		account = "";
		password = new HiddenText("");
	}
	
	
}
