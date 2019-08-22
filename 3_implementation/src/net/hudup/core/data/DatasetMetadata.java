/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.util.List;

import net.hudup.core.Cloneable;
import net.hudup.core.Constants;
import net.hudup.core.Util;
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
	public int numberOfUserRatings = 0;
	
	
	/**
	 * Number of users available in dataset.
	 */
	public int numberOfItems = 0;
	
	
	/**
	 * Number of users that are rated in dataset.
	 */
	public int numberOfItemRatings = 0;

	
	/**
	 * Default constructor.
	 */
	public DatasetMetadata() {
		
	}
	
	
	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return minRating + "," + 
				maxRating + ", " + 
				numberOfUsers + ", " + 
				numberOfUserRatings +
				numberOfItems + ", " + 
				numberOfItemRatings;
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> textList = TextParserUtil.split(spec, ",", null);
		minRating = Integer.parseInt(textList.get(0));
		maxRating = Integer.parseInt(textList.get(1));
		numberOfUsers = Integer.parseInt(textList.get(2));
		numberOfUserRatings = Integer.parseInt(textList.get(3));
		numberOfItems = Integer.parseInt(textList.get(4));
		numberOfItemRatings = Integer.parseInt(textList.get(5));
	}
	
	
	@Override
	public String toString() {
		return toText();
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
		// TODO Auto-generated method stub
		DatasetMetadata metadata = new DatasetMetadata();
		metadata.minRating = this.minRating;
		metadata.maxRating = this.maxRating;
		metadata.numberOfUsers = this.numberOfUsers;
		metadata.numberOfUserRatings = this.numberOfUserRatings;
		metadata.numberOfItems = this.numberOfItems;
		metadata.numberOfItemRatings = this.numberOfItemRatings;
		
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
		this.numberOfUserRatings = other.numberOfUserRatings;
		this.numberOfItems = other.numberOfItems;
		this.numberOfItemRatings = other.numberOfItemRatings;
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
			e.printStackTrace();
		}
		finally {
			try {
				if (userIds != null)
					userIds.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Fetcher<RatingVector> vUserRatings = null;
		try {
			vUserRatings = dataset.fetchUserRatings();
			metadata.numberOfUserRatings = vUserRatings.getMetadata().getSize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (vUserRatings != null)
					vUserRatings.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	
		Fetcher<Integer> itemIds = null;
		try {
			itemIds = dataset.fetchItemIds();
			metadata.numberOfItems = itemIds.getMetadata().getSize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (itemIds != null)
					itemIds.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Fetcher<RatingVector> vItemRatings = null;
		try {
			vItemRatings = dataset.fetchItemRatings();
			metadata.numberOfItemRatings = vItemRatings.getMetadata().getSize();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (vItemRatings != null)
					vItemRatings.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		return metadata;
	}
	
	
}



/**
 * This class is an extension of dataset meta-data. Please see {@link DatasetMetadata}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
class DatasetMetadata2 extends DatasetMetadata {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Minimum number of user ratings
	 */
	public int userMinNumberOfRatings = 0;

	
	/**
	 * Maximum number of user ratings
	 */
	public int userMaxNumberOfRatings = 0;
	
	
	/**
	 * Minimum number of item ratings
	 */
	public int itemMinNumberOfRatings = 0;

	
	/**
	 * Maximum number of item ratings
	 */
	public int itemMaxNumberOfRatings = 0;

	
	/**
	 * Default constructor.
	 */
	protected DatasetMetadata2() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return minRating + "," + 
				maxRating + ", " + 
				numberOfUsers + ", " + 
				numberOfUserRatings +
				numberOfItems + ", " + 
				numberOfItemRatings + ", " + 
				userMinNumberOfRatings + ", " + 
				userMaxNumberOfRatings + ", " + 
				itemMinNumberOfRatings + ", " + 
				itemMaxNumberOfRatings;
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> textList = TextParserUtil.split(spec, ",", null);
		minRating = Integer.parseInt(textList.get(0));
		maxRating = Integer.parseInt(textList.get(1));
		numberOfUsers = Integer.parseInt(textList.get(2));
		numberOfUserRatings = Integer.parseInt(textList.get(3));
		numberOfItems = Integer.parseInt(textList.get(4));
		numberOfItemRatings = Integer.parseInt(textList.get(5));
		userMinNumberOfRatings = Integer.parseInt(textList.get(6));
		userMaxNumberOfRatings = Integer.parseInt(textList.get(7));
		itemMinNumberOfRatings = Integer.parseInt(textList.get(8));
		itemMaxNumberOfRatings = Integer.parseInt(textList.get(9));
	}
	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		DatasetMetadata2 metadata2 = new DatasetMetadata2();
		metadata2.assignFrom(this);
		metadata2.userMinNumberOfRatings = this.userMinNumberOfRatings;
		metadata2.userMaxNumberOfRatings = this.userMaxNumberOfRatings;
		metadata2.itemMinNumberOfRatings = this.itemMinNumberOfRatings;
		metadata2.itemMaxNumberOfRatings = this.itemMaxNumberOfRatings;

		return metadata2;
	}


	/** 
	 * Extracting meta-data about specific dataset.
	 * @param dataset Specific dataset
	 * @return {@link DatasetMetadata2}
	 */
	public static DatasetMetadata2 create(Dataset dataset) {
		
		DatasetMetadata metadata = DatasetMetadata.create(dataset);
		DatasetMetadata2 metadata2 = new DatasetMetadata2();
		metadata2.assignFrom(metadata);
		
		Fetcher<RatingVector> users = null;
		try {
			users = dataset.fetchUserRatings();
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				
				int count = user.count(true);
				if (count < min) min = count;
				if (count > max) max = count;
			}
			
			if (min != Integer.MAX_VALUE)
				metadata2.userMinNumberOfRatings = min;
			if (max != Integer.MIN_VALUE)
				metadata2.userMaxNumberOfRatings = max;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (users != null)
					users.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		Fetcher<RatingVector> items = null;
		try {
			items = dataset.fetchItemRatings();
			int min = Integer.MAX_VALUE;
			int max = Integer.MIN_VALUE;
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null) continue;
				
				int count = item.count(true);
				if (count < min) min = count;
				if (count > max) max = count;
			}
			
			if (min != Integer.MAX_VALUE)
				metadata2.userMinNumberOfRatings = min;
			if (max != Integer.MIN_VALUE)
				metadata2.userMaxNumberOfRatings = max;
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (items != null)
					items.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return metadata2;
	}


}
