/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;

import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplateSchema;

/**
 * This class represents a null pointer, which is a null dataset.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class NullPointer extends PointerAbstract {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Null pointer name.
	 */
	public final static String NULL_POINTER = "(null)";
	
	
	/**
	 * Default constructor.
	 */
	public NullPointer() {

	}


	@Override
	public Fetcher<Integer> fetchUserIds() {
		return new MemFetcher<>();
	}


	@Override
	public int getUserId(Serializable externalUserId) {
		return -1;
	}


	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		return null;
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		return new MemFetcher<>();
	}


	@Override
	public int getItemId(Serializable externalItemId) {
		return -1;
	}


	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		return null;
	}


	@Override
	public Rating getRating(int userId, int itemId) {
		return null;
	}


	@Override
	public RatingVector getUserRating(int userId) {
		return null;
	}


	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		return new MemFetcher<>();
	}


	@Override
	public RatingVector getItemRating(int itemId) {
		return null;
	}


	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		return new MemFetcher<>();
	}


	@Override
	public RatingMatrix createUserMatrix() {
		return null;
	}


	@Override
	public RatingMatrix createItemMatrix() {
		return null;
	}


	@Override
	public Profile getUserProfile(int userId) {
		return null;
	}


	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		return new MemFetcher<>();
	}


	@Override
	public AttributeList getUserAttributes() {
		return new AttributeList();
	}


	@Override
	public Profile getItemProfile(int itemId) {
		return null;
	}


	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		return new MemFetcher<>();
	}


	@Override
	public AttributeList getItemAttributes() {
		return new AttributeList();
	}


	@Override
	public Profile profileOf(Context context) {
		return null;
	}


	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		return new MemProfiles();
	}


	@Override
	public Fetcher<Profile> fetchSample() {
		return new MemFetcher<>();
	}


	@Override
	public Object clone() {
		return new NullPointer();
	}


	@Override
	public Dataset catchup() {
		return this;
	}


	@Override
	public Dataset selectByContexts(ContextList contexts) {
		return this;
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		return null;
	}

	
	@Override
	public Provider getProvider() {
		return null;
	}

	
}
