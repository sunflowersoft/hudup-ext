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
public class NullPointer extends Pointer {

	
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
		// TODO Auto-generated constructor stub
	}


	@Override
	public Fetcher<Integer> fetchUserIds() {
		// TODO Auto-generated method stub
		return new MemFetcher<>();
	}


	@Override
	public int getUserId(Serializable externalUserId) {
		// TODO Auto-generated method stub
		return -1;
	}


	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		// TODO Auto-generated method stub
		return new MemFetcher<>();
	}


	@Override
	public int getItemId(Serializable externalItemId) {
		// TODO Auto-generated method stub
		return -1;
	}


	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Rating getRating(int userId, int itemId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RatingVector getUserRating(int userId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		// TODO Auto-generated method stub
		return new MemFetcher<>();
	}


	@Override
	public RatingVector getItemRating(int itemId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		// TODO Auto-generated method stub
		return new MemFetcher<>();
	}


	@Override
	public RatingMatrix createUserMatrix() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RatingMatrix createItemMatrix() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Profile getUserProfile(int userId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		// TODO Auto-generated method stub
		return new MemFetcher<>();
	}


	@Override
	public AttributeList getUserAttributes() {
		// TODO Auto-generated method stub
		return new AttributeList();
	}


	@Override
	public Profile getItemProfile(int itemId) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		// TODO Auto-generated method stub
		return new MemFetcher<>();
	}


	@Override
	public AttributeList getItemAttributes() {
		// TODO Auto-generated method stub
		return new AttributeList();
	}


	@Override
	public Profile profileOf(Context context) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		// TODO Auto-generated method stub
		return new MemProfiles();
	}


	@Override
	public Fetcher<Profile> fetchSample() {
		// TODO Auto-generated method stub
		return new MemFetcher<>();
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new NullPointer();
	}


	@Override
	public Dataset catchup() {
		return this;
	}


	@Override
	public Dataset selectByContexts(ContextList contexts) {
		// TODO Auto-generated method stub
		return this;
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public Provider getProvider() {
		// TODO Auto-generated method stub
		return null;
	}

	
}
