/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.parser;

import java.io.BufferedReader;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Map;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.ExternalRecord;
import net.hudup.core.data.ItemRating;
import net.hudup.core.data.MemProfiles;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Rating;
import net.hudup.core.data.RatingVector;
import net.hudup.core.data.SnapshotImpl;
import net.hudup.core.data.UserRating;
import net.hudup.core.data.ctx.CTSMemMultiProfiles;
import net.hudup.core.data.ctx.ContextTemplateSchemaImpl;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LineProcessor;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.SnapshotParser;
import net.hudup.core.parser.TextParserUtil;

/**
 * This class implements Jesterjoke parser to parse Jesterjoke dataset.
 *  
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class JesterjokeParser extends SnapshotParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public JesterjokeParser() {
		
	}
	
	
	@Override
	public String getName() {
		return "jesterjoke_parser";
	}


	@Override
	public String getDescription() throws RemoteException {
		return "Jester Joke parser";
	}


	@Override
	public boolean support(DataDriver driver) throws RemoteException {
		return driver.isFlatServer();
	}
	
	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		Map<Integer, RatingVector> userRatingMap = Util.newMap();
		Map<Integer, RatingVector> itemRatingMap = Util.newMap();
		loadRatingMatrix(userRatingMap, itemRatingMap, config);
		
		MemProfiles users = MemProfiles.createEmpty();
		MemProfiles items = MemProfiles.createEmpty();
		if (users.size() == 0) users = MemProfiles.createEmpty(DataConfig.USERID_FIELD, Type.integer);
		users.fillAs(userRatingMap.keySet());
		if (items.size() == 0) items = MemProfiles.createEmpty(DataConfig.ITEMID_FIELD, Type.integer);
		items.fillAs(itemRatingMap.keySet());
		
		DatasetMetadata metadata = this.config.getMetadata();
		metadata.minRating = -10;
		metadata.maxRating = 10;
		config.setMetadata(metadata);
		
		SnapshotImpl dataset = new SnapshotImpl();
		Map<Integer, ExternalRecord> externalUserRecordMap = Util.newMap();
		Map<Integer, ExternalRecord> externalItemRecordMap = Util.newMap();
		
		ContextTemplateSchemaImpl ctschema = ContextTemplateSchemaImpl.create();
		ctschema.defaultCTSchema();
		
		List<Profile> sampleProfiles = Util.newList();
		
		config.setParser(this);
		
		dataset.assign(
				config, 
				externalUserRecordMap, 
				userRatingMap, 
				users, 
				externalItemRecordMap, 
				itemRatingMap, 
				items,
				ctschema,
				CTSMemMultiProfiles.create(),
				sampleProfiles);
		
		return dataset;
	}


	/**
	 * Private method for loading only rating matrix from Movielens database.
	 * @param outUserMap
	 * @param outItemMap
	 */
	private void loadRatingMatrix(final Map<Integer, RatingVector> outUserMap,
			final Map<Integer, RatingVector> outItemMap,
			DataConfig config) {
		
		outUserMap.clear();
		outItemMap.clear();
		UriAdapter adapter = null;
		BufferedReader reader = null;
		try {
			adapter = new UriAdapter(config);
			xURI store = config.getStoreUri();
			String ratingsUnit = config.getRatingUnit();
			if (store == null || ratingsUnit == null) return;
			xURI ruUri = store.concat(ratingsUnit);
			
			reader = new BufferedReader(adapter.getReader(ruUri));
			Id id = new Id();
			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> arr = TextParserUtil.split(line, TextParserUtil.DEFAULT_SEP, null);
					if (arr == null || arr.size() < 2) return;
					int n = 0;
					try {
						n = Integer.parseInt(arr.get(0));
					} catch (Exception e) {};
					if (n == 0) return;
					n = Math.min(100, arr.size() - 1);
					
					int rowId = id.get();
					for (int colId = 1; colId <= n; colId++) {
						double value = Constants.UNUSED;
						try {
							value = Double.parseDouble(arr.get(colId));
						} catch (Exception e) {};
						if (!Util.isUsed(value) || value == 99) continue;
						
						Rating rating = new Rating(value);
						RatingVector user = null;
						if (outUserMap.containsKey(rowId))
							user = outUserMap.get(rowId);
						else {
							user = new UserRating(rowId);
							outUserMap.put(rowId, user);
						}
						user.put(colId, rating);
						
						RatingVector item = null;
						if (outItemMap.containsKey(colId))
							item = outItemMap.get(colId);
						else {
							item = new ItemRating(colId);
							outItemMap.put(colId, item);
						}
						item.put(rowId, rating);
					}
					
				}
			});
			
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (reader != null) reader.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			if (adapter != null) adapter.close();
		}
		
	}
	
	
	/**
	 * Auto-increased identifier.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	private class Id implements Serializable, Cloneable {

		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Internal identifier.
		 */
		private int id = 0;
		
		/**
		 * Constructor.
		 */
		public Id() {

		}

		/**
		 * Increase identifier.
		 * @return increment and returning the identifier.
		 */
		public int get() {
			id++;
			return id;
		}
		
	}

	
//	/**
//	 * Equation y = (y1 - y0) / (x1 - x0) * x - (x0*y1 - x1*y0) / (x1 - x0)
//	 * Mapping (1, -10) and (5, +10)
//	 * @return {@link Mapper}
//	 */
//	@SuppressWarnings({ "unused", "deprecation" })
//	private net.hudup.core.parser.Mapper getMapper() {
//		return new net.hudup.core.parser.Mapper() {
//			
//			@Override
//			public double map(double value) {
//				return 5f * value -  15;
//			}
//			
//			@Override
//			public double imap(double value) {
//				return (value + 15f) / 5f;
//			}
//		};
//	}
	
	
}
