/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.util.List;
import java.util.Set;

import net.hudup.core.evaluate.recommend.Accuracy;
import net.hudup.core.logistic.LogUtil;
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
	public double relevantRating = DataConfig.RELEVANT_RATING_DEFAULT;

	
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
	public double ratingCoverRatio = 0;
	
	
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
	public double ratingRelevantRatio = 0;
	
	
	/**
	 * Relevant rating mean.
	 */
	public double relevantRatingMean = 0;

	
	/**
	 * Relevant rating standard deviation.
	 */
	public double relevantRatingSd = 0;

	
	/**
	 * Sample row count.
	 */
	public int sampleRowCount = 0;

	
	/**
	 * Sample column count.
	 */
	public int sampleColumnCount = 0;

	
	/**
	 * Sample cell count.
	 */
	public int sampleCellCount = 0;

	
	/**
	 * Sample sparse ratio.
	 */
	public double sampleCoverRatio = 0;

	
	/**
	 * Default constructor.
	 */
	protected DatasetMetadata2() {

	}


	@Override
	public String toText() {
		return MathUtil.format(relevantRating) + "," + 
				MathUtil.format(minRating) + ", " + 
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
				MathUtil.format(ratingCoverRatio) + ", " + 
				MathUtil.format(ratingMean) + ", " + 
				MathUtil.format(ratingSd) + ", " + 
				numberOfRelevantRatings + ", " + 
				MathUtil.format(ratingRelevantRatio) + ", " + 
				MathUtil.format(relevantRatingMean) + ", " + 
				MathUtil.format(relevantRatingSd) + ", " + 
				sampleRowCount + ", " + 
				sampleColumnCount + ", " + 
				sampleCellCount + ", " + 
				MathUtil.format(sampleCoverRatio) + ", " + 
				"";
	}


	@Override
	public void parseText(String spec) {
		List<String> textList = TextParserUtil.split(spec, ",", null);
		relevantRating = Integer.parseInt(textList.get(0));
		minRating = Integer.parseInt(textList.get(1));
		maxRating = Integer.parseInt(textList.get(2));
		numberOfUsers = Integer.parseInt(textList.get(3));
		numberOfRatingUsers = Integer.parseInt(textList.get(4));
		numberOfItems = Integer.parseInt(textList.get(5));
		numberOfRatedItems = Integer.parseInt(textList.get(6));
		userMinRatingCount = Integer.parseInt(textList.get(7));
		userMinRelevantRatingCount = Integer.parseInt(textList.get(8));
		userMaxRatingCount = Integer.parseInt(textList.get(9));
		userMaxRelevantRatingCount = Integer.parseInt(textList.get(10));
		userAverageRatingCount = Double.parseDouble(textList.get(11));
		userAverageRelevantRatingCount = Double.parseDouble(textList.get(12));
		itemMinRatingCount = Integer.parseInt(textList.get(13));
		itemMinRelevantRatingCount = Integer.parseInt(textList.get(14));
		itemMaxRatingCount = Integer.parseInt(textList.get(15));
		itemMaxRelevantRatingCount = Integer.parseInt(textList.get(16));
		itemAverageRatingCount = Double.parseDouble(textList.get(17));
		itemAverageRelevantRatingCount = Double.parseDouble(textList.get(18));
		numberOfRatings = Integer.parseInt(textList.get(19));
		ratingCoverRatio = Double.parseDouble(textList.get(20));
		ratingMean = Double.parseDouble(textList.get(21));
		ratingSd = Double.parseDouble(textList.get(22));
		numberOfRelevantRatings = Integer.parseInt(textList.get(23));
		ratingRelevantRatio = Integer.parseInt(textList.get(24));
		relevantRatingMean = Double.parseDouble(textList.get(25));
		relevantRatingSd = Double.parseDouble(textList.get(26));
		sampleRowCount = Integer.parseInt(textList.get(27));
		sampleColumnCount = Integer.parseInt(textList.get(28));
		sampleCellCount = Integer.parseInt(textList.get(29));
		sampleCoverRatio = Double.parseDouble(textList.get(30));
	}
	
	
	/**
	 * Translating this meta-data to nice text form.
	 * @return nice text form of this meta-data.
	 */
	public String toText2() {
		StringBuffer buffer = new StringBuffer();
		
		buffer.append("Minimum rating = " + MathUtil.round(minRating) + "\n");
		buffer.append("Maximum rating = " + MathUtil.round(maxRating) + "\n");
		buffer.append("Relevant rating = " + MathUtil.round(relevantRating) + "\n");
		buffer.append("Number of users = " + numberOfUsers + "\n");
		buffer.append("Number of rating users = " + numberOfRatingUsers + "\n");
		buffer.append("Number of items = " + numberOfItems + "\n");
		buffer.append("Number of rated items = " + numberOfRatedItems + "\n");
		buffer.append("Min rating count of a user = " + userMinRatingCount + "\n");
		buffer.append("Min favorite rating count of a user = " + userMinRelevantRatingCount + "\n");
		buffer.append("Max rating count of a user = " + userMaxRatingCount + "\n");
		buffer.append("Max favorite rating count of a user = " + userMaxRelevantRatingCount + "\n");
		buffer.append("Average rating count of a user = " + MathUtil.round(userAverageRatingCount) + "\n");
		buffer.append("Average favorite rating count of a user = " + MathUtil.round(userAverageRelevantRatingCount) + "\n");
		buffer.append("Min rating count of an item = " + itemMinRatingCount + "\n");
		buffer.append("Min favorite rating count of an item = " + itemMinRelevantRatingCount + "\n");
		buffer.append("Max rating count of an item = " + itemMaxRatingCount + "\n");
		buffer.append("Max favorite rating count of an item = " + itemMaxRelevantRatingCount + "\n");
		buffer.append("Average rating count of an item = " + MathUtil.round(itemAverageRatingCount) + "\n");
		buffer.append("Average favorite rating count of an item = " + MathUtil.round(itemAverageRelevantRatingCount) + "\n");
		buffer.append("Number of ratings = " + numberOfRatings + "\n");
		buffer.append("Rating cover ratio = " + MathUtil.round(ratingCoverRatio*100) + "%" + "\n");
		buffer.append("Rating mean = " + MathUtil.round(ratingMean) + "\n");
		buffer.append("Rating standard deviation = " + MathUtil.round(ratingSd) + "\n");
		buffer.append("Number of favorite ratings = " + numberOfRelevantRatings + "\n");
		buffer.append("Rating favorite ratio = " + MathUtil.round(ratingRelevantRatio*100) + "%" + "\n");
		buffer.append("Favorite rating mean = " + MathUtil.round(relevantRatingMean) + "\n");
		buffer.append("Favorite rating standard deviation = " + MathUtil.round(relevantRatingSd) + "\n");
		buffer.append("Sample row count = " + sampleRowCount + "\n");
		buffer.append("Sample column count = " + sampleColumnCount + "\n");
		buffer.append("Sample cell count = " + sampleCellCount + "\n");
		buffer.append("Sample cover ratio = " + MathUtil.round(sampleCoverRatio*100) + "%");

		return buffer.toString();
	}

	@Override
	public Object clone() {
		DatasetMetadata2 metadata2 = new DatasetMetadata2();
		metadata2.assignFrom(this);
		metadata2.relevantRating = this.relevantRating;
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
		metadata2.ratingCoverRatio = this.ratingCoverRatio;
		metadata2.ratingMean = this.ratingMean;
		metadata2.ratingSd = this.ratingSd;
		metadata2.numberOfRelevantRatings = this.numberOfRelevantRatings;
		metadata2.ratingRelevantRatio = this.ratingRelevantRatio;
		metadata2.relevantRatingMean = this.relevantRatingMean;
		metadata2.relevantRatingSd = this.relevantRatingSd;
		metadata2.sampleRowCount = this.sampleRowCount;
		metadata2.sampleColumnCount = this.sampleColumnCount;
		metadata2.sampleCellCount = this.sampleCellCount;
		metadata2.sampleCoverRatio = this.sampleCoverRatio;

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
		metadata2.relevantRating = Accuracy.getRelevantRatingThreshold(dataset, false);
		
		
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
				
				int relevantCount = Accuracy.countForRelevant(user, true, dataset);
				if (relevantCount < minRelevantRateCount) minRelevantRateCount = relevantCount;
				if (relevantCount > maxRelevantRateCount) maxRelevantRateCount = relevantCount;
				relevantRateCount += relevantCount;
				
				Set<Integer> itemIds = user.fieldIds(true);
				for (int itemId : itemIds) {
					double value = user.get(itemId).value;
					rateSum += value;
					
					if (Accuracy.isRelevant(value, dataset)) {
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
				metadata2.ratingCoverRatio = (double)rateCount / (metadata2.numberOfRatingUsers*metadata2.numberOfRatedItems);
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
			if (rateCount != 0)
				metadata2.ratingRelevantRatio = (double)relevantRateCount / rateCount;
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
						if (Accuracy.isRelevant(value, dataset)) {
							double d = value - metadata2.relevantRatingMean;
							relevantRatingVarSum += d*d;
						}
					}
				}
				
				metadata2.relevantRatingSd = relevantRatingVarSum / relevantRateCount;
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			//metadata2 = new DatasetMetadata2();
		}
		finally {
			try {
				if (users != null)
					users.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
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
				
				int relevantRatedCount = Accuracy.countForRelevant(item, true, dataset);
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
			LogUtil.trace(e);
			//metadata2 = new DatasetMetadata2();
		}
		finally {
			try {
				if (items != null)
					items.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
		Fetcher<Profile> samples = null;
		try {
			samples = dataset.fetchSample();
			AttributeList attList = null;
			while (samples.next()) {
				Profile sample = samples.pick();
				if (sample == null) continue;
				
				if (attList == null) attList = sample.getAttRef();
				
				metadata2.sampleRowCount++;
				
				for (int i = 0; i < sample.getAttCount(); i++) {
					if (sample.getValue(i) != null)
						metadata2.sampleCellCount++;
				}
			}
			
			if (attList != null)
				metadata2.sampleColumnCount = attList.size();
			if (metadata2.sampleRowCount != 0 && metadata2.sampleColumnCount != 0)
				metadata2.sampleCoverRatio = (double)metadata2.sampleCellCount / (metadata2.sampleRowCount*metadata2.sampleColumnCount);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			//metadata2 = new DatasetMetadata2();
		}
		finally {
			try {
				if (samples != null)
					samples.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

		
		return metadata2;
	}


}
