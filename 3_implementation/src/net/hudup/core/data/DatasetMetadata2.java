package net.hudup.core.data;

import java.util.List;
import java.util.Set;

import net.hudup.core.logistic.MathUtil;
import net.hudup.core.parser.TextParserUtil;


/**
 * This class is an extension of dataset meta-data. Please see {@link DatasetMetadata}.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class DatasetMetadata2 extends DatasetMetadata {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Minimum rating count of a user.
	 */
	public int userMinRatingCount = 0;

	
	/**
	 * Maximum rating count of a user.
	 */
	public int userMaxRatingCount = 0;
	
	
	/**
	 * Average rating count of a user.
	 */
	public double userAverageRatingCount = 0;

	
	/**
	 * Minimum rating count of an item.
	 */
	public int itemMinRatingCount = 0;

	
	/**
	 * Maximum rating count of an item.
	 */
	public int itemMaxRatingCount = 0;

	
	/**
	 * Average rating count of an item.
	 */
	public double itemAverageRatingCount = 0;

	
	/**
	 * Number of ratings.
	 */
	public int numberOfRatings = 0;
	
	
	/**
	 * Sparse ratio.
	 */
	public double sparseRatio = 0;
	
	
	/**
	 * Rating mean.
	 */
	public double ratingMean = 0;

	
	/**
	 * Rating standard deviation.
	 */
	public double ratingSd = 0;

	
	/**
	 * Default constructor.
	 */
	protected DatasetMetadata2() {
		// TODO Auto-generated constructor stub
	}


	@Override
	public String toText() {
		// TODO Auto-generated method stub
		return MathUtil.format(minRating) + "," + 
				MathUtil.format(maxRating) + ", " + 
				numberOfUsers + ", " + 
				numberOfRatingUsers +
				numberOfItems + ", " + 
				numberOfRatedItems + ", " + 
				userMinRatingCount + ", " + 
				userMaxRatingCount + ", " + 
				MathUtil.format(userAverageRatingCount) + ", " + 
				itemMinRatingCount + ", " + 
				itemMaxRatingCount + ", " + 
				MathUtil.format(itemAverageRatingCount) + ", " + 
				numberOfRatings + ", " + 
				MathUtil.format(sparseRatio) + ", " + 
				MathUtil.format(ratingMean) + ", " + 
				MathUtil.format(ratingSd) + ", " + 
				"";
	}


	@Override
	public void parseText(String spec) {
		// TODO Auto-generated method stub
		List<String> textList = TextParserUtil.split(spec, ",", null);
		minRating = Integer.parseInt(textList.get(0));
		maxRating = Integer.parseInt(textList.get(1));
		numberOfUsers = Integer.parseInt(textList.get(2));
		numberOfRatingUsers = Integer.parseInt(textList.get(3));
		numberOfItems = Integer.parseInt(textList.get(4));
		numberOfRatedItems = Integer.parseInt(textList.get(5));
		userMinRatingCount = Integer.parseInt(textList.get(6));
		userMaxRatingCount = Integer.parseInt(textList.get(7));
		userAverageRatingCount = Double.parseDouble(textList.get(8));
		itemMinRatingCount = Integer.parseInt(textList.get(9));
		itemMaxRatingCount = Integer.parseInt(textList.get(10));
		itemAverageRatingCount = Double.parseDouble(textList.get(11));
		numberOfRatings = Integer.parseInt(textList.get(12));
		sparseRatio = Double.parseDouble(textList.get(13));
		ratingMean = Double.parseDouble(textList.get(14));
		ratingSd = Double.parseDouble(textList.get(15));
	}
	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		DatasetMetadata2 metadata2 = new DatasetMetadata2();
		metadata2.assignFrom(this);
		metadata2.userMinRatingCount = this.userMinRatingCount;
		metadata2.userMaxRatingCount = this.userMaxRatingCount;
		metadata2.userAverageRatingCount = this.userAverageRatingCount;
		metadata2.itemMinRatingCount = this.itemMinRatingCount;
		metadata2.itemMaxRatingCount = this.itemMaxRatingCount;
		metadata2.itemAverageRatingCount = this.itemAverageRatingCount;
		metadata2.numberOfRatings = this.numberOfRatings;
		metadata2.sparseRatio = this.sparseRatio;
		metadata2.ratingMean = this.ratingMean;
		metadata2.ratingSd = this.ratingSd;

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
			int minRatedCount = Integer.MAX_VALUE;
			int maxRatedCount = Integer.MIN_VALUE;
			int nUsers = 0;
			int ratingUserCountSum = 0;
			int ratingCount = 0;
			double ratingSum = 0;
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				
				nUsers++;
				
				int ratedCount = user.count(true);
				if (ratedCount < minRatedCount) minRatedCount = ratedCount;
				if (ratedCount > maxRatedCount) maxRatedCount = ratedCount;
				
				ratingUserCountSum += ratedCount;
				
				Set<Integer> itemIds = user.fieldIds(true);
				for (int itemId : itemIds) {
					ratingSum += user.get(itemId).value;
					ratingCount++;
				}
			}
			
			if (minRatedCount != Integer.MAX_VALUE)
				metadata2.userMinRatingCount = minRatedCount;
			if (maxRatedCount != Integer.MIN_VALUE)
				metadata2.userMaxRatingCount = maxRatedCount;
			if (nUsers != 0)
				metadata2.userAverageRatingCount = (double)ratingUserCountSum / nUsers;
			
			metadata2.numberOfRatings = ratingUserCountSum;
			if (metadata2.numberOfRatingUsers != 0 && metadata2.numberOfRatedItems != 0)
				metadata2.sparseRatio = (double)ratingUserCountSum / (metadata2.numberOfRatingUsers*metadata2.numberOfRatedItems);
			
			if (ratingCount != 0) {
				metadata2.ratingMean = (double)ratingSum / ratingCount;
				
				double ratingVarSum = 0;
				users.reset();
				while (users.next()) {
					RatingVector user = users.pick();
					if (user == null) continue;
					Set<Integer> itemIds = user.fieldIds(true);
					
					for (int itemId : itemIds) {
						double d = user.get(itemId).value - metadata2.ratingMean;
						ratingVarSum += d*d;
					}
				}
				
				metadata2.ratingSd = ratingVarSum / ratingCount;
			}
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
			int minRatedCount = Integer.MAX_VALUE;
			int maxRatedCount = Integer.MIN_VALUE;
			int nItems = 0;
			int ratedItemCountSum = 0;
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null) continue;
				
				nItems++;
				
				int ratedCount = item.count(true);
				if (ratedCount < minRatedCount) minRatedCount = ratedCount;
				if (ratedCount > maxRatedCount) maxRatedCount = ratedCount;
				
				ratedItemCountSum += ratedCount;
			}
			
			if (minRatedCount != Integer.MAX_VALUE)
				metadata2.itemMinRatingCount = minRatedCount;
			if (maxRatedCount != Integer.MIN_VALUE)
				metadata2.itemMaxRatingCount = maxRatedCount;
			if (nItems != 0)
				metadata2.itemAverageRatingCount = (double)ratedItemCountSum / nItems;
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
