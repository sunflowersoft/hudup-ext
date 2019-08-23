package net.hudup.core.data;

import java.util.List;
import java.util.Set;

import net.hudup.core.evaluate.recommend.Accuracy;
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
	 * Minimum relevant rating count of a user.
	 */
	public int userMinRelevantRatingCount = 0;

	
	/**
	 * Maximum rating count of a user.
	 */
	public int userMaxRatingCount = 0;
	
	
	/**
	 * Maximum relevant rating count of a user.
	 */
	public int userMaxRelevantRatingCount = 0;

	
	/**
	 * Average rating count of a user.
	 */
	public double userAverageRatingCount = 0;

	
	/**
	 * Average relevant rating count of a user.
	 */
	public double userAverageRelevantRatingCount = 0;

	
	/**
	 * Minimum rating count of an item.
	 */
	public int itemMinRatingCount = 0;

	
	/**
	 * Minimum relevant rating count of an item.
	 */
	public int itemMinRelevantRatingCount = 0;

	
	/**
	 * Maximum rating count of an item.
	 */
	public int itemMaxRatingCount = 0;

	
	/**
	 * Maximum relevant rating count of an item.
	 */
	public int itemMaxRelevantRatingCount = 0;

	
	/**
	 * Average rating count of an item.
	 */
	public double itemAverageRatingCount = 0;

	
	/**
	 * Average relevant rating count of an item.
	 */
	public double itemAverageRelevantRatingCount = 0;

	
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
	 * Number of relevant ratings.
	 */
	public int numberOfRelevantRatings = 0;
	
	
	/**
	 * Relevant ratio.
	 */
	public double relevantRatio = 0;
	
	
	/**
	 * Relevant rating mean.
	 */
	public double relevantRatingMean = 0;

	
	/**
	 * Relevant rating standard deviation.
	 */
	public double relevantRatingSd = 0;

	
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
				userMinRelevantRatingCount + ", " + 
				userMaxRatingCount + ", " + 
				userMaxRelevantRatingCount + ", " + 
				MathUtil.format(userAverageRatingCount) + ", " + 
				MathUtil.format(userAverageRelevantRatingCount) + ", " + 
				itemMinRatingCount + ", " + 
				itemMinRelevantRatingCount + ", " + 
				itemMaxRatingCount + ", " + 
				itemMaxRelevantRatingCount + ", " + 
				MathUtil.format(itemAverageRatingCount) + ", " + 
				MathUtil.format(itemAverageRelevantRatingCount) + ", " + 
				numberOfRatings + ", " + 
				MathUtil.format(sparseRatio) + ", " + 
				MathUtil.format(ratingMean) + ", " + 
				MathUtil.format(ratingSd) + ", " + 
				numberOfRelevantRatings + ", " + 
				MathUtil.format(relevantRatio) + ", " + 
				MathUtil.format(relevantRatingMean) + ", " + 
				MathUtil.format(relevantRatingSd) + ", " + 
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
		userMinRelevantRatingCount = Integer.parseInt(textList.get(7));
		userMaxRatingCount = Integer.parseInt(textList.get(8));
		userMaxRelevantRatingCount = Integer.parseInt(textList.get(9));
		userAverageRatingCount = Double.parseDouble(textList.get(10));
		userAverageRelevantRatingCount = Double.parseDouble(textList.get(11));
		itemMinRatingCount = Integer.parseInt(textList.get(12));
		itemMinRelevantRatingCount = Integer.parseInt(textList.get(13));
		itemMaxRatingCount = Integer.parseInt(textList.get(14));
		itemMaxRelevantRatingCount = Integer.parseInt(textList.get(15));
		itemAverageRatingCount = Double.parseDouble(textList.get(16));
		itemAverageRelevantRatingCount = Double.parseDouble(textList.get(17));
		numberOfRatings = Integer.parseInt(textList.get(18));
		sparseRatio = Double.parseDouble(textList.get(19));
		ratingMean = Double.parseDouble(textList.get(20));
		ratingSd = Double.parseDouble(textList.get(21));
		numberOfRelevantRatings = Integer.parseInt(textList.get(22));
		relevantRatio = Integer.parseInt(textList.get(23));
		relevantRatingMean = Double.parseDouble(textList.get(24));
		relevantRatingSd = Double.parseDouble(textList.get(25));
	}
	
	
	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		DatasetMetadata2 metadata2 = new DatasetMetadata2();
		metadata2.assignFrom(this);
		metadata2.userMinRatingCount = this.userMinRatingCount;
		metadata2.userMinRelevantRatingCount = this.userMinRelevantRatingCount;
		metadata2.userMaxRatingCount = this.userMaxRatingCount;
		metadata2.userMaxRelevantRatingCount = this.userMaxRelevantRatingCount;
		metadata2.userAverageRatingCount = this.userAverageRatingCount;
		metadata2.userAverageRelevantRatingCount = this.userAverageRelevantRatingCount;
		metadata2.itemMinRatingCount = this.itemMinRatingCount;
		metadata2.itemMinRelevantRatingCount = this.itemMinRelevantRatingCount;
		metadata2.itemMaxRatingCount = this.itemMaxRatingCount;
		metadata2.itemMaxRelevantRatingCount = this.itemMaxRelevantRatingCount;
		metadata2.itemAverageRatingCount = this.itemAverageRatingCount;
		metadata2.itemAverageRelevantRatingCount = this.itemAverageRelevantRatingCount;
		metadata2.numberOfRatings = this.numberOfRatings;
		metadata2.sparseRatio = this.sparseRatio;
		metadata2.ratingMean = this.ratingMean;
		metadata2.ratingSd = this.ratingSd;
		metadata2.numberOfRelevantRatings = this.numberOfRelevantRatings;
		metadata2.relevantRatio = this.relevantRatio;
		metadata2.relevantRatingMean = this.relevantRatingMean;
		metadata2.relevantRatingSd = this.relevantRatingSd;

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
			int minRateCount = Integer.MAX_VALUE;
			int maxRateCount = Integer.MIN_VALUE;
			int minRelevantRateCount = Integer.MAX_VALUE;
			int maxRelevantRateCount = Integer.MIN_VALUE;
			int rateCount = 0;
			int relevantRateCount = 0;
			double rateSum = 0;
			double relevantRateSum = 0;
			int n = 0;
			while (users.next()) {
				RatingVector user = users.pick();
				if (user == null) continue;
				int count = user.count(true);
				if (count == 0) continue;
				
				n++;
				
				if (count < minRateCount) minRateCount = count;
				if (count > maxRateCount) maxRateCount = count;
				rateCount += count;
				
				int relevantCount = Accuracy.countForRelevant(user, true, metadata2.minRating, metadata2.maxRating);
				if (relevantCount < minRelevantRateCount) minRelevantRateCount = relevantCount;
				if (relevantCount > maxRelevantRateCount) maxRelevantRateCount = relevantCount;
				relevantRateCount += relevantCount;
				
				Set<Integer> itemIds = user.fieldIds(true);
				for (int itemId : itemIds) {
					double value = user.get(itemId).value;
					rateSum += value;
					
					if (Accuracy.isRelevant(value, metadata2.minRating, metadata2.maxRating)) {
						relevantRateSum += value;
					}
				}
			}
			
			metadata2.numberOfRatingUsers = n;
			
			if (minRateCount != Integer.MAX_VALUE)
				metadata2.userMinRatingCount = minRateCount;
			if (maxRateCount != Integer.MIN_VALUE)
				metadata2.userMaxRatingCount = maxRateCount;
			if (n != 0)
				metadata2.userAverageRatingCount = (double)rateCount / n;
			
			if (minRelevantRateCount != Integer.MAX_VALUE)
				metadata2.userMinRelevantRatingCount = minRelevantRateCount;
			if (maxRelevantRateCount != Integer.MIN_VALUE)
				metadata2.userMaxRelevantRatingCount = maxRelevantRateCount;
			if (n != 0)
				metadata2.userAverageRelevantRatingCount = (double)relevantRateCount / n;

			metadata2.numberOfRatings = rateCount;
			if (metadata2.numberOfRatingUsers != 0 && metadata2.numberOfRatedItems != 0)
				metadata2.sparseRatio = (double)rateCount / (metadata2.numberOfRatingUsers*metadata2.numberOfRatedItems);
			if (rateCount != 0) {
				metadata2.ratingMean = (double)rateSum / rateCount;
				
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
				
				metadata2.ratingSd = ratingVarSum / rateCount;
			}
			
			metadata2.numberOfRelevantRatings = relevantRateCount;
			metadata2.relevantRatio = (double)relevantRateCount / rateCount;
			if (relevantRateCount != 0) {
				metadata2.relevantRatingMean = (double)relevantRateSum / relevantRateCount;
				
				double relevantRatingVarSum = 0;
				users.reset();
				while (users.next()) {
					RatingVector user = users.pick();
					if (user == null) continue;
					Set<Integer> itemIds = user.fieldIds(true);
					
					for (int itemId : itemIds) {
						double value = user.get(itemId).value;
						if (Accuracy.isRelevant(value, metadata2.minRating, metadata2.maxRating)) {
							double d = value - metadata2.relevantRatingMean;
							relevantRatingVarSum += d*d;
						}
					}
				}
				
				metadata2.relevantRatingSd = relevantRatingVarSum / relevantRateCount;
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			metadata2 = new DatasetMetadata2();
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
			int minRateCount = Integer.MAX_VALUE;
			int maxRateCount = Integer.MIN_VALUE;
			int minRelevantRateCount = Integer.MAX_VALUE;
			int maxRelevantRateCount = Integer.MIN_VALUE;
			int rateCount = 0;
			int relevantRateCount = 0;
			int n = 0;
			while (items.next()) {
				RatingVector item = items.pick();
				if (item == null) continue;
				int count = item.count(true);
				if (count == 0) continue;
				
				n++;
				
				if (count < minRateCount) minRateCount = count;
				if (count > maxRateCount) maxRateCount = count;
				rateCount += count;
				
				int relevantRatedCount = Accuracy.countForRelevant(item, true, metadata2.minRating, metadata2.maxRating);
				if (relevantRatedCount < minRelevantRateCount) minRelevantRateCount = relevantRatedCount;
				if (relevantRatedCount > maxRelevantRateCount) maxRelevantRateCount = relevantRatedCount;
				relevantRateCount += relevantRatedCount;
			}
			
			metadata2.numberOfRatedItems = n;
			
			if (minRateCount != Integer.MAX_VALUE)
				metadata2.itemMinRatingCount = minRateCount;
			if (maxRateCount != Integer.MIN_VALUE)
				metadata2.itemMaxRatingCount = maxRateCount;
			if (n != 0)
				metadata2.itemAverageRatingCount = (double)rateCount / n;
			
			if (minRelevantRateCount != Integer.MAX_VALUE)
				metadata2.itemMinRelevantRatingCount = minRelevantRateCount;
			if (maxRelevantRateCount != Integer.MIN_VALUE)
				metadata2.itemMaxRelevantRatingCount = maxRelevantRateCount;
			if (n != 0)
				metadata2.itemAverageRelevantRatingCount = (double)relevantRateCount / n;
		}
		catch (Throwable e) {
			e.printStackTrace();
			metadata2 = new DatasetMetadata2();
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
