/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.Collection;

import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplateSchema;

/**
 * This class is abstract implementation of interface {@link Pointer}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class PointerAbstract extends DatasetAbstract implements Pointer, PointerRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public PointerAbstract() {
		super();
	}


	@Override
	public Fetcher<Integer> fetchUserIds() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Collection<Integer> fetchUserIds2() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public int getUserId(Serializable externalUserId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Collection<Integer> fetchItemIds2() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public int getItemId(Serializable externalItemId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Rating getRating(int userId, int itemId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingVector getUserRating(int userId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Collection<RatingVector> fetchUserRatings2() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingVector getItemRating(int itemId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Collection<RatingVector> fetchItemRatings2() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingMatrix createUserMatrix() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingMatrix createItemMatrix() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile getUserProfile(int userId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Collection<Profile> fetchUserProfiles2() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public AttributeList getUserAttributes() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile getItemProfile(int itemId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Collection<Profile> fetchItemProfiles2() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public AttributeList getItemAttributes() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile profileOf(Context context) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Profile> fetchSample() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Collection<Profile> fetchSample2() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Object clone() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Dataset catchup() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Dataset selectByContexts(ContextList contexts) {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		throw new RuntimeException("Not implement this method yet");
	}

	
	@Override
	public Provider getProvider() {
		throw new RuntimeException("Not implement this method yet");
	}
	
	
}
