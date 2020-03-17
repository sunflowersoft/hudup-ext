/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;

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
		// TODO Auto-generated constructor stub
	}


	@Override
	public Fetcher<Integer> fetchUserIds() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public int getUserId(Serializable externalUserId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ExternalRecord getUserExternalRecord(int userId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Integer> fetchItemIds() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public int getItemId(Serializable externalItemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ExternalRecord getItemExternalRecord(int itemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Rating getRating(int userId, int itemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingVector getUserRating(int userId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<RatingVector> fetchUserRatings() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingVector getItemRating(int itemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<RatingVector> fetchItemRatings() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingMatrix createUserMatrix() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public RatingMatrix createItemMatrix() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile getUserProfile(int userId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Profile> fetchUserProfiles() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public AttributeList getUserAttributes() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile getItemProfile(int itemId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Profile> fetchItemProfiles() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public AttributeList getItemAttributes() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profile profileOf(Context context) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Profiles profilesOf(int ctxTemplateId) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Fetcher<Profile> fetchSample() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Dataset catchup() {
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public Dataset selectByContexts(ContextList contexts) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}


	@Override
	public ContextTemplateSchema getCTSchema() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}

	
	@Override
	public Provider getProvider() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement this method yet");
	}
	
	
}
