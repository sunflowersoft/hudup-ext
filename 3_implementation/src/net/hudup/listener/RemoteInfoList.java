/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Util;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class represents a list of remote information. Note, remote information represented by {@link RemoteInfo} that contains information of remote server or service.
 * For instance, {@link RemoteInfo} contains host and port of the remote server (service) and account name / password to access such server (service).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RemoteInfoList implements Serializable, Cloneable, TextParsable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * The internal list remote information.
	 */
	protected List<RemoteInfo> rInfoList = Util.newList();
	
	
	/**
	 * Default constructor.
	 */
	public RemoteInfoList() {
		
	}
	
	
	/**
	 * Getting the remote information at specified index.
	 * @param index specified index.
	 * @return {@link RemoteInfo} at specified index.
	 */
	public RemoteInfo get(int index) {
		return rInfoList.get(index);
	}
	
	
	/**
	 * Adding the specified remote information into this list.
	 * @param rInfo specified {@link RemoteInfo}.
	 * @return whether add {@link RemoteInfo} successfully.
	 */
	public boolean add(RemoteInfo rInfo) {
		int index = indexOf(rInfo.host, rInfo.port);
		if (index != -1)
			return false;
		return rInfoList.add(rInfo);
	}
	
	
	/**
	 * Removing the remote information at specified index.
	 * @param index specified index.
	 * @return removed {@link RemoteInfo}.
	 */
	public RemoteInfo remove(int index) {
		return rInfoList.remove(index);
	}
	
	
	/**
	 * Removing the remote information that contains specified host and port.
	 * @param host specified host.
	 * @param port specified port.
	 * @return removed {@link RemoteInfo}.
	 */
	public RemoteInfo remove(String host, int port) {
		int index = indexOf(host, port);
		if (index == -1)
			return null;
		else
			return remove(index);
	}
	
	
	/**
	 * Removing the specified remote information from this list.
	 * @param rInfo specified {@link RemoteInfo}.
	 * @return whether remove successfully.
	 */
	public boolean remove(RemoteInfo rInfo) {
		return rInfoList.remove(rInfo);
	}
	
	
	/**
	 * Setting the specified remote information at specified index.
	 * @param index specified index.
	 * @param rInfo specified {@link RemoteInfo}.
	 * @return the previous remote information that replaced by the specified remote information.
	 */
	public RemoteInfo set(int index, RemoteInfo rInfo) {
		int idx = indexOf(rInfo.host, rInfo.port);
		if (idx != -1 && idx != index)
			return null;
		else
			return rInfoList.set(index, rInfo);
	}
	
	
	/**
	 * Replacing the remote information that has the same host and post with the specified remote information.
	 * @param rInfo specified {@link RemoteInfo}.
	 * @return old {@link RemoteInfo} replaced by the specified {@link RemoteInfo}. 
	 */
	public RemoteInfo set(RemoteInfo rInfo) {
		int idx = indexOf(rInfo.host, rInfo.port);
		if (idx == -1)
			return null;
		else
			return set(idx, rInfo);
	}
	
	
	/**
	 * Getting the size of this list.
	 * @return size of this list.
	 */
	public int size() {
		return rInfoList.size();
	}
	
	
	/**
	 * Clearing this list, which means that all {@link RemoteInfo} (s) are removed from this list.
	 */
	public void clear() {
		rInfoList.clear();
	}
	
	
	/**
	 * Finding the remote information that has the specified host and post.
	 * @param host specified host.
	 * @param port specified port.
	 * @return index of the remote information that has the specified host and post.
	 */
	public int indexOf(String host, int port) {
		for (int i = 0; i < rInfoList.size(); i++) {
			RemoteInfo rInfo = rInfoList.get(i);
			if (rInfo.host.compareToIgnoreCase(host) == 0 && rInfo.port == port)
				return i;
		}
		
		return -1;
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return TextParserUtil.toText(rInfoList, ",");
	}

	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		RemoteInfoList list = new RemoteInfoList();
		for (RemoteInfo rInfo : rInfoList) {
			list.add((RemoteInfo)rInfo.clone());
		}
		
		return list;
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return toText();
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		if (spec == null || spec.isEmpty())
			return;
		
		List<RemoteInfo> rInfoList = TextParserUtil.parseTextParsableList(
				spec, RemoteInfo.class, ",");
		for (RemoteInfo rInfo : rInfoList)
			this.rInfoList.add(rInfo);
	}

	
}
