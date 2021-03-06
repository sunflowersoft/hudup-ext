/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * {@link DatasetMetadata} stores essential information about dataset represented by {@link Dataset}, for example:
 * minimum rating value, maximum rating value, number of items, number of users, etc.
 * As a convention, {@code DatasetMetadata} is called {@code dataset meta-data}.
 * It is a short description (manifest) of dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class DatasetMetadata implements Serializable, TextParsable, Cloneable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Minimum rating value, for example, the minimum rating value of Movielens data is 1.
	 */
	public double minRating = Constants.DEFAULT_MIN_RATING;
	
	
	/**
	 * Maximum rating value, for example, the minimum rating value of Movielens data is 5.
	 */
	public double maxRating = Constants.DEFAULT_MAX_RATING;


	/**
	 * Number of users available in dataset.
	 */
	public int numberOfUsers = 0;
	
	
	/**
	 * Number of users who rated on items in dataset.
	 */
	public int numberOfRatingUsers = 0;
	
	
	/**
	 * Number of users available in dataset.
	 */
	public int numberOfItems = 0;
	
	
	/**
	 * Number of users that are rated in dataset.
	 */
	public int numberOfRatedItems = 0;

	
	/**
	 * Default constructor.
	 */
	public DatasetMetadata() {
		
	}
	
	
	@Override
	public String toText() {
		return minRating + "," + 
				maxRating + ", " + 
				numberOfUsers + ", " + 
				numberOfRatingUsers +
				numberOfItems + ", " + 
				numberOfRatedItems;
	}


	@Override
	public void parseText(String spec) {
		List<String> textList = TextParserUtil.split(spec, ",", null);
		minRating = Integer.parseInt(textList.get(0));
		maxRating = Integer.parseInt(textList.get(1));
		numberOfUsers = Integer.parseInt(textList.get(2));
		numberOfRatingUsers = Integer.parseInt(textList.get(3));
		numberOfItems = Integer.parseInt(textList.get(4));
		numberOfRatedItems = Integer.parseInt(textList.get(5));
	}
	
	
	@Override
	public String toString() {
		return toText();
	}


	/**
	 * Translating this meta-data to nice text form.
	 * @return nice text form of this meta-data.
	 */
	public String toText2() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Min rating = " + minRating + "\n");
		buffer.append("Max rating = " + maxRating + "\n");
		buffer.append("Number of users = " + numberOfUsers + "\n");
		buffer.append("Number of rating users = " + numberOfRatingUsers + "\n");
		buffer.append("Number of items = " + numberOfItems + "\n");
		buffer.append("Number of rated items = " + numberOfRatedItems);
		
		return buffer.toString();
	}
	
	
	/**
	 * Convert this meta-data to list of profiles, based on specified attribute list.
	 * @param attributes specified attribute list.
	 * @return list of converted profiles.
	 */
	public List<Profile> toProfiles(AttributeList attributes) {
		List<Profile> profileList = Util.newList();
		if (attributes.size() != 2)
			return Util.newList();
		
		Profile cfgProfile1 = new Profile(attributes);
		cfgProfile1.setValue(0, DataConfig.MIN_RATING_FIELD, null);
		cfgProfile1.setValue(1, minRating);
		profileList.add(cfgProfile1);
		
		Profile cfgProfile2 = new Profile(attributes);
		cfgProfile2.setValue(0, DataConfig.MAX_RATING_FIELD, null);
		cfgProfile2.setValue(1, maxRating);
		profileList.add(cfgProfile2);
		
		return profileList;
	}
	
	
	@Override
	public Object clone() {
		DatasetMetadata metadata = new DatasetMetadata();
		metadata.minRating = this.minRating;
		metadata.maxRating = this.maxRating;
		metadata.numberOfUsers = this.numberOfUsers;
		metadata.numberOfRatingUsers = this.numberOfRatingUsers;
		metadata.numberOfItems = this.numberOfItems;
		metadata.numberOfRatedItems = this.numberOfRatedItems;
		
		return metadata;
	}
	
	
	/**
	 * Assigning from other meta-data.
	 * @param other other meta-data.
	 */
	protected void assignFrom(DatasetMetadata other) {
		this.minRating = other.minRating;
		this.maxRating = other.maxRating;
		this.numberOfUsers = other.numberOfUsers;
		this.numberOfRatingUsers = other.numberOfRatingUsers;
		this.numberOfItems = other.numberOfItems;
		this.numberOfRatedItems = other.numberOfRatedItems;
	}
	
	
	/**
	 * Extracting meta-data about specific dataset.
	 * @param dataset Specific dataset
	 * @return {@link DatasetMetadata}
	 */
	public static DatasetMetadata create(Dataset dataset) {
		DatasetMetadata metadata = dataset.getConfig().getMetadata();
		if (dataset instanceof Pointer)
			return metadata;
		
		Fetcher<Integer> userIds = null;
		try {
			userIds = dataset.fetchUserIds();
			metadata.numberOfUsers = userIds.getMetadata().getSize();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (userIds != null)
					userIds.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		Fetcher<RatingVector> vUserRatings = null;
		try {
			vUserRatings = dataset.fetchUserRatings();
			metadata.numberOfRatingUsers = vUserRatings.getMetadata().getSize();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (vUserRatings != null)
					vUserRatings.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

	
		Fetcher<Integer> itemIds = null;
		try {
			itemIds = dataset.fetchItemIds();
			metadata.numberOfItems = itemIds.getMetadata().getSize();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (itemIds != null)
					itemIds.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		Fetcher<RatingVector> vItemRatings = null;
		try {
			vItemRatings = dataset.fetchItemRatings();
			metadata.numberOfRatedItems = vItemRatings.getMetadata().getSize();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (vItemRatings != null)
					vItemRatings.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
		return metadata;
	}
	
	
}



