/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.logistic.math;

import java.io.Serializable;
import java.util.BitSet;
import java.util.Collection;
import java.util.List;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.Profile;
import net.hudup.core.data.bit.BitData;
import net.hudup.core.data.bit.BitItem;
import net.hudup.core.logistic.DSUtil;

/**
 * This is utility class which calculates probabilities on bit data (binary data).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public final class BitDatasetStatsProcessor implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Bit data (binary data).
	 */
	protected BitData bitData = null;
	
	
	/**
	 * Constructor with bit data (binary data).
	 * @param bitData  bit data (binary data).
	 */
	public BitDatasetStatsProcessor(BitData bitData) {
		this.bitData = bitData;
	}
	
	
	/**
	 * Calculating probability of specified bit item id.
	 * @param bitItemId specified bit item id.
	 * @return probability of specified bit item id.
	 */
	public double prob(int bitItemId) {
		return bitData.get(bitItemId).getSupport();
		
	}
	
	
	/**
	 * Calculating AND probability of a collection of bit item id (s).
	 * @param bitItemIds collection of bit item id (s).
	 * @return AND probability of a collection of bit item id (s).
	 */
	public double probAnd(Collection<Integer> bitItemIds) {
		BitSet bs = null;
		
		for (int bitItemId : bitItemIds) {
			BitItem item1 = bitData.get(bitItemId);
			BitSet bs1 = item1.bitItem().getBitSet();
			
			if (bs == null)
				bs = (BitSet)bs1.clone();
			else
				bs.and(bs1);
		}
		
		return (double)DSUtil.countSetBit(bs) / (double)
				bitData.realSessionIds().size();
	}
	
	
	/**
	 * Calculating AND probability of two bit item id (s).
	 * @param bitItemId1 bit item id 1.
	 * @param bitItemId2 bit item id 2.
	 * @return AND probability of two bit item id (s).
	 */
	public double probAnd(int bitItemId1, int bitItemId2) {
		BitItem item1 = bitData.get(bitItemId1);
		BitSet bs1 = (BitSet)item1.bitItem().getBitSet().clone();
		
		BitItem item2 = bitData.get(bitItemId2);
		BitSet bs2 = item2.bitItem().getBitSet();
		
		bs1.and(bs2);
		
		return (double)DSUtil.countSetBit(bs1) / (double)
				bitData.realSessionIds().size();
	}
	
	
	/**
	 * Calculating AND probability of two bit item id (s).
	 * @param bitItemId1 bit item id 1.
	 * @param bitItemId2 bit item id 2.
	 * @return AND probability of two bit item id (s).
	 */
	public double probAndNot(int bitItemId1, int bitItemId2) {
		BitItem item1 = bitData.get(bitItemId1);
		BitSet bs1 = (BitSet)item1.bitItem().getBitSet().clone();
		
		BitItem item2 = bitData.get(bitItemId2);
		BitSet bs2 = item2.bitItem().getBitSet();
		
		bs1.andNot(bs2);
		return (double)DSUtil.countSetBit(bs1) / (double)
				bitData.realSessionIds().size();
	}
	
	
	/**
	 * Calculating probability of a nominal session (often, a user) on bit item id.
	 * @param sessionNominalAtt session nominal attribute. Session id is often user id.
	 * @param sessionNominalAttValue value of session nominal attribute. Session id is often user id.
	 * @param bitItemId bit item id.
	 * @param occur flag to indicate whether bit is set.
	 * @return probability of a nominal session (often, a user) on bit item id.
	 */
	public double probSessionOnBitItem(
			Attribute sessionNominalAtt, 
			int sessionNominalAttValue, 
			int bitItemId,
			boolean occur) {
		
		if (!sessionNominalAtt.isCategory())
			throw new RuntimeException("Attribute not category");
		
		BitItem item = bitData.get(bitItemId);
		BitSet bs = (BitSet)item.bitItem().getBitSet();
		
		int count = 0;
		List<Integer> realSessionIds = bitData.realSessionIds();
		int n = realSessionIds.size();
		for (int i = 0; i < n; i++) {
			if (bs.get(i) != occur)
				continue;
			
			int realSessionId = realSessionIds.get(i);
			Profile realSessionProfile = bitData.getRealSessionProfile(realSessionId);
			if (realSessionProfile == null)
				continue;
			
			int value = realSessionProfile.getValueAsInt(sessionNominalAtt.getIndex());
			
			if (value == sessionNominalAttValue)
				count ++;
		}
		
		return (double)count / (double)n;
	}
	
	
	/**
	 * Calculating probability of a nominal session (often, a user) on bit item id.
	 * @param sessionNominalAtt session nominal attribute. Session id is often user id.
	 * @param sessionNominalAttValue value of session nominal attribute. Session id is often user id.
	 * @param bitItemId bit item id.
	 * @return probability of a nominal session (often, a user) on bit item id.
	 */
	public double probSessionOnBitItem(
			Attribute sessionNominalAtt, 
			int sessionNominalAttValue, 
			int bitItemId) {
		
		return probSessionOnBitItem(sessionNominalAtt, sessionNominalAttValue, bitItemId, true);
	}
	
	
	/**
	 * Calculating probability of a nominal session (often, a user) on bit item id with note that bit is set.
	 * @param sessionNominalAtt session nominal attribute. Session id is often user id.
	 * @param sessionNominalAttValue value of session nominal attribute. Session id is often user id.
	 * @param bitItemId bit item id.
	 * @return probability of a nominal session (often, a user) on bit item id.
	 */
	public double probSessionOnNotBitItem(
			Attribute sessionNominalAtt, 
			int sessionNominalAttValue, 
			int bitItemId) {
		
		return probSessionOnBitItem(sessionNominalAtt, sessionNominalAttValue, bitItemId, false);
	}


}
