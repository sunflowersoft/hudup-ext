package net.hudup.server;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.hudup.core.Util;
import net.hudup.core.client.ClientStub;
import net.hudup.core.data.ExternalItemInfo;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class is a stub of server at client. It is called client stub. Please see {@link ClientStub}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public class ClientStubImpl implements ClientStub {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Registered name.
	 */
	protected String regName = "";
	
	
	/**
	 * Map of external item information.
	 */
	protected Map<Integer, ExternalItemInfo> itemInfo = Util.newMap();
	
	
	/**
	 * Default constructor.
	 */
	public ClientStubImpl() {
		if (!(itemInfo instanceof Serializable))
			throw new RuntimeException("Invalid field");
	}
	
	
	/**
	 * Constructor with registered name and map of external item formation.
	 * @param regName registered name.
	 * @param itemInfos external item formation.
	 */
	public ClientStubImpl(String regName, Map<Integer, ExternalItemInfo> itemInfos) {
		if (!(itemInfos instanceof Serializable))
			throw new RuntimeException("Invalid field");
		
		this.regName = regName;
		this.itemInfo = itemInfos;
	}
	
	
	/**
	 * Clear information of this stub.
	 */
	protected void clear() {
		regName = "";
		itemInfo.clear();
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return TextParserUtil.encryptReservedChars(regName) + TextParserUtil.MAIN_SEP + TextParserUtil.toText(itemInfo, ",");
	}

	
	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		clear();
		
		List<String> textList = TextParserUtil.split(spec, TextParserUtil.MAIN_SEP, null);
		if (textList.size() == 0)
			return;
		
		if (textList.size() < 2) {
			regName = TextParserUtil.decryptReservedChars(textList.get(0));
			return;
		}
		
		String itemSpec = textList.get(1);
		Map<String, String> itemSpecMap = TextParserUtil.parseTextMap(itemSpec, ",");
		Set<String> keys = itemSpecMap.keySet();
		for (String key : keys) {
			try {
				String itemInfoText = itemSpecMap.get(key);
				ExternalItemInfo itemInfo = new ExternalItemInfo();
				itemInfo.parseText(itemInfoText);
				
				int itemId = Integer.parseInt(key);
				
				this.itemInfo.put(itemId, itemInfo);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		
	}

	
	@Override
	public Object clone() {
		Map<Integer, ExternalItemInfo> itemInfos = Util.newMap();
		Set<Integer> itemIds = this.itemInfo.keySet();
		for (int itemId : itemIds) {
			ExternalItemInfo itemInfo = this.itemInfo.get(itemId);
			itemInfos.put(itemId, itemInfo);
		}
		
		return new ClientStubImpl(regName.toString(), itemInfos);
	}
	
	
	
	@Override
	public String toString() {
		return toText();
	}


	@Override
	public String getRegName() {
		// TODO Auto-generated method stub
		return regName;
	}


	@Override
	public ExternalItemInfo getExternalItemInfo(int itemId) {
		// TODO Auto-generated method stub
		return itemInfo.get(itemId);
	}


	/**
	 * Create this stub with specified registered name, item identifiers, and external query.
	 * @param regName registered name.
	 * @param itemIds item identifiers.
	 * @param externalQuery external query.
	 * @return Client stub created with specified registered name, item identifiers, and external query.
	 */
	public static ClientStubImpl create(String regName, Collection<Integer> itemIds, ExternalQuery externalQuery) {
		Map<Integer, ExternalItemInfo> itemInfos = Util.newMap();

		for (int itemId : itemIds) {
			try {
				ExternalItemInfo itemInfo = externalQuery != null ? externalQuery.getItemInfo(itemId) : null;
				if (itemInfo == null) { // for testing, should be removed
					itemInfo = new ExternalItemInfo();
					itemInfo.externalId = itemId + "";
					itemInfo.title = "There is no link for recommended item '" + itemId + "'";
				}
				itemInfos.put(itemId, itemInfo);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return new ClientStubImpl(regName, itemInfos);
	}
	
	
	
}
