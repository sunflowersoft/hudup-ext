/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.parser;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.swing.JOptionPane;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.DatasetUtil2;
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
 * This parser is used to parse original Movielens database into snapshot.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MovielensParser extends SnapshotParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Movielens type field name.
	 */
	protected static final String MOVIELENS_TYPE_FIELD = "type";
	
	
	/**
	 * Movielens 100K type.
	 */
	public static final String MOVIELENS_TYPE_100K = "100k";

	
	/**
	 * Movielens 1M type.
	 */
	public static final String MOVIELENS_TYPE_1M = "1m";


	/**
	 * Array of supported Movielens types.
	 */
	public static final String[] MOVIELENS_TYPES_SUPPORTED = {
		MOVIELENS_TYPE_100K,
		MOVIELENS_TYPE_1M,
	};
	
	
	/**
	 * Default constructor.
	 */
	public MovielensParser() {
		
	}
	
	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		
		Map<Integer, RatingVector> userRatingMap = Util.newMap();
		Map<Integer, RatingVector> itemRatingMap = Util.newMap();
		loadRatingMatrix(userRatingMap, itemRatingMap, config);
		
		MemProfiles users = MemProfiles.createEmpty();
		MemProfiles items = MemProfiles.createEmpty();

		Properties props = DatasetUtil2.loadFlatConfig(config);
		String type = props.getProperty(MOVIELENS_TYPE_FIELD);
		type = type != null ? type : getConfig().getAsString(MOVIELENS_TYPE_FIELD);
		type = type != null ? type : MOVIELENS_TYPE_100K;
		if (type.equalsIgnoreCase(MOVIELENS_TYPE_100K)) {
			users = load100KUserProfiles(config);
			items = load100KItemProfiles(config);
		}
		else if (type.equalsIgnoreCase(MOVIELENS_TYPE_1M)) {
			users = load1MUserProfiles(config);
			items = load1MItemProfiles(config);
		}
		
		if (users.size() == 0)
			users = MemProfiles.createEmpty(DataConfig.USERID_FIELD, Type.integer);
		users.fillAs(userRatingMap.keySet());
		
		if (items.size() == 0)
			items = MemProfiles.createEmpty(DataConfig.ITEMID_FIELD, Type.integer);
		items.fillAs(itemRatingMap.keySet());
		
		double minRating = DataConfig.MIN_RATING_DEFAULT;
		double maxRating = DataConfig.MAX_RATING_DEFAULT;
		try {
			minRating = Double.parseDouble(props.getProperty(DataConfig.MIN_RATING_FIELD));
			maxRating = Double.parseDouble(props.getProperty(DataConfig.MAX_RATING_FIELD));
		}
		catch (Throwable e) {
			minRating = Constants.UNUSED;
			maxRating = Constants.UNUSED;
		}
		DatasetMetadata metadata = this.config.getMetadata();
		metadata.minRating = Util.isUsed(minRating) ? minRating : DataConfig.MIN_RATING_DEFAULT;
		metadata.maxRating = Util.isUsed(maxRating) ? maxRating : DataConfig.MAX_RATING_DEFAULT;
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

	
	@Override
	public boolean support(DataDriver driver) throws RemoteException {
		
		return driver.isFlatServer();
	}


	/**
	 * Private method for loading only rating matrix from Movielens database.
	 * @param outUserMap
	 * @param outItemMap
	 */
	private void loadRatingMatrix(
			final Map<Integer, RatingVector> outUserMap,
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
			xURI ruUri = store.concat(ratingsUnit);
			
			reader = new BufferedReader(adapter.getReader(ruUri));
			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					int rowId = -1;
					int colId = -1;
					double value = Constants.UNUSED;
					
					List<String> arr = TextParserUtil.split(line, TextParserUtil.DEFAULT_SEP, null);
					try {
						// check row is valid ? column must better than 3
						if(arr.size() >= 3) {
							rowId = Integer.parseInt(arr.get(0));	
							colId = Integer.parseInt(arr.get(1));								
							value = Double.parseDouble(arr.get(2));
						}
						else {
							LogUtil.error("Rating column count is < 3");
						}
						
						if (rowId == -1 || colId == -1)
							return;
					}
					catch (NumberFormatException e) {
						LogUtil.trace(e);
						LogUtil.error("Processing rating line causes error " + e.getMessage());
						return;
					}
					
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
			});
			
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			if (adapter != null)
				adapter.close();
		}
		
		
	}
	
	
	/**
	 * Private method to load user profiles for 100K Movielens database.
	 * @return {@link MemProfiles} as user profiles for 100K Movielens database.
	 */
	private MemProfiles load100KUserProfiles(DataConfig config) {
		UriAdapter adapter = null;
		BufferedReader reader = null;
		MemProfiles memProfiles = MemProfiles.createEmpty();
		if (config.getUserUnit() == null) return memProfiles;
		
		try {
			adapter = new UriAdapter(config);
			xURI store = config.getStoreUri();
			
			Map<String, Attribute> attributeMap = Util.newMap();
			if (config.getNominalUnit() != null) {
				xURI nominalUri = store.concat(config.getNominalUnit());
				attributeMap = DatasetUtil2.loadNominalAttributes(adapter, nominalUri, config.getUserUnit());
			}
			
			Attribute userid = new Attribute(DataConfig.USERID_FIELD, Type.integer);

			Attribute age = new Attribute("age", Type.integer);
			if (attributeMap.containsKey("age"))
				age = attributeMap.get("age");

			Attribute gender = new Attribute("gender", Type.string);
			if (attributeMap.containsKey("gender"))
				gender = attributeMap.get("gender");

			Attribute occupation = new Attribute("occupation", Type.string);
			if (attributeMap.containsKey("occupation"))
				occupation = attributeMap.get("occupation");
			
			Attribute zipcode = new Attribute("zipcode", Type.string);

			final AttributeList attList = AttributeList.create(
					new Attribute[] {userid, age, gender, occupation, zipcode});
			
			final Map<Integer, Profile> profileMap = Util.newMap();
			
			xURI usersUri = store.concat(config.getUserUnit());
			reader = new BufferedReader(adapter.getReader(usersUri));
			
			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> array = DSUtil.splitAllowEmpty(line, "|", null);
					if (array.size() < 5)
						return;
					
					Profile profile = new Profile(attList);
					profile.setKey(0);
					
					int id = Integer.parseInt(array.get(0));
					profile.setValue(0, id);
					profile.setValue(1, array.get(1));
					profile.setValue(2, array.get(2));
					profile.setValue(3, array.get(3));
					profile.setValue(4, array.get(4));
					
					profileMap.put(id, profile);
				}
			});
			
			memProfiles = MemProfiles.assign(attList, profileMap);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return memProfiles;
		
	}
	
	
	/**
	 * Private method to load user profiles for 1M Movielens database.
	 * @return {@link MemProfiles} as user profiles for 1M Movielens database.
	 */
	private MemProfiles load1MUserProfiles(DataConfig config) {
		UriAdapter adapter = null;
		BufferedReader reader = null;
		MemProfiles memProfiles = MemProfiles.createEmpty();
		if (config.getUserUnit() == null) return memProfiles;

		try {
			adapter = new UriAdapter(config);
			xURI store = config.getStoreUri();
			
			Map<String, Attribute> attributeMap = Util.newMap();
			if (config.getNominalUnit() != null) {
				xURI nominalUri = store.concat(config.getNominalUnit());
				attributeMap = DatasetUtil2.loadNominalAttributes(adapter, nominalUri, config.getUserUnit());
			}
			
			Attribute userid = new Attribute(DataConfig.USERID_FIELD, Type.integer);

			Attribute age = new Attribute("age", Type.integer);
			if (attributeMap.containsKey("age"))
				age = attributeMap.get("age");

			Attribute gender = new Attribute("gender", Type.string);
			if (attributeMap.containsKey("gender"))
				gender = attributeMap.get("gender");

			Attribute occupation = new Attribute("occupation", Type.string);
			if (attributeMap.containsKey("occupation"))
				occupation = attributeMap.get("occupation");
			
			final Attribute zipcode = new Attribute("zipcode", Type.string);

			final AttributeList attList = AttributeList.create(
					new Attribute[] {userid, age, gender, occupation, zipcode});
			final Map<Integer, Profile> profileMap = Util.newMap();

			xURI usersUri = store.concat(config.getUserUnit());
			reader = new BufferedReader(adapter.getReader(usersUri));
			
			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> array = DSUtil.splitAllowEmpty(line, "::", null);
					if (array.size() < 5)
						return;
					
					Profile profile = new Profile(attList);
					profile.setKey(0);
					
					int id = Integer.parseInt(array.get(0));
					profile.setValue(0, id);
					profile.setValue(1, array.get(2));
					profile.setValue(2, array.get(1));
					profile.setValue(3, Integer.parseInt(array.get(3)));
					profile.setValue(4, array.get(4));
					
					profileMap.put(id, profile);
					
				}
			});
			
			memProfiles = MemProfiles.assign(attList, profileMap);
			
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return memProfiles;
		
	}

	
	/**
	 * Private method to load item profiles for 100K Movielens database.
	 * @return {@link MemProfiles} as item profiles for 100K Movielens database.
	 */
	private MemProfiles load100KItemProfiles(DataConfig config) {
		UriAdapter adapter = null;
		BufferedReader reader = null;
		MemProfiles memProfiles = MemProfiles.createEmpty();
		if (config.getItemUnit() == null) return memProfiles;
		
		try {
			
			Attribute itemid = new Attribute(DataConfig.ITEMID_FIELD, Type.integer);
			
			Attribute title = new Attribute("title", Type.string);
			
			Attribute release_date = new Attribute("release_date", Type.date);
			
			Attribute video_release_date = new Attribute("video_release_date", Type.date);

			Attribute imdb_url = new Attribute("imdb_url", Type.string);
			
			Attribute unknown = new Attribute("unknown", Type.bit);
			
			Attribute action = new Attribute("action", Type.bit);
			
			Attribute adventure = new Attribute("adventure", Type.bit);
			
			Attribute animation = new Attribute("animation", Type.bit);
			
			Attribute children = new Attribute("children", Type.bit);
			
			Attribute comedy = new Attribute("comedy", Type.bit);
			
			Attribute crime = new Attribute("crime", Type.bit);
			
			Attribute documentary = new Attribute("documentary", Type.bit);

			Attribute drama = new Attribute("drama", Type.bit);

			Attribute fantasy = new Attribute("fantasy", Type.bit);
			
			Attribute film_noir = new Attribute("film_noir", Type.bit);

			Attribute horror = new Attribute("horror", Type.bit);
			
			Attribute musical = new Attribute("musical", Type.bit);
			
			Attribute mystery = new Attribute("mystery", Type.bit);
			
			Attribute romance = new Attribute("romance", Type.bit);

			Attribute sci_fi = new Attribute("sci_fi", Type.bit);
			
			Attribute thriller = new Attribute("thriller", Type.bit);

			Attribute war = new Attribute("war", Type.bit);
			
			Attribute western = new Attribute("western", Type.bit);

			final AttributeList attList = AttributeList.create(new Attribute[] {
					itemid,
					title, release_date, video_release_date, imdb_url,
					unknown, action, adventure, animation, children, 
					comedy, crime, documentary, drama, fantasy, film_noir,
					horror, musical, mystery, romance, sci_fi, thriller, 
					war, western});
			final Map<Integer, Profile> profileMap = Util.newMap();
			
			adapter = new UriAdapter(config);
			
			xURI store = config.getStoreUri();
			xURI itemsUri = store.concat(config.getItemUnit());
			reader = new BufferedReader(adapter.getReader(itemsUri));

			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> array = DSUtil.splitAllowEmpty(line, "|", null);
					if (array.size() < 24)
						return;
					
					Profile profile = new Profile(attList);
					profile.setKey(0);
					
					SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
					
					int id = Integer.parseInt(array.get(0));
					profile.setValue(0, id);
					profile.setValue(1, array.get(1));
					profile.setValue(2, array.get(2), df);
					profile.setValue(3, array.get(3), df);
					profile.setValue(4, array.get(4));
					profile.setValue(5, array.get(5));
					profile.setValue(6, array.get(6));
					profile.setValue(7, array.get(7));
					profile.setValue(8, array.get(8));
					profile.setValue(9, array.get(9));
					profile.setValue(10, array.get(10));
					profile.setValue(11, array.get(11));
					profile.setValue(12, array.get(12));
					profile.setValue(13, array.get(13));
					profile.setValue(14, array.get(14));
					profile.setValue(15, array.get(15));
					profile.setValue(16, array.get(16));
					profile.setValue(17, array.get(17));
					profile.setValue(18, array.get(18));
					profile.setValue(19, array.get(19));
					profile.setValue(20, array.get(20));
					profile.setValue(21, array.get(21));
					profile.setValue(22, array.get(22));
					profile.setValue(23, array.get(23));

					profileMap.put(id, profile);
					
				}
			});
			
			memProfiles = MemProfiles.assign(attList, profileMap);
			
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return memProfiles;
		
	}

	
	/**
	 * Private method to load item profiles for 1M Movielens database.
	 * @return {@link MemProfiles} as item profiles for 1M Movielens database.
	 */
	private MemProfiles load1MItemProfiles(DataConfig config) {
		UriAdapter adapter = null;
		BufferedReader reader = null;
		MemProfiles memProfiles = MemProfiles.createEmpty();
		if (config.getItemUnit() == null) return memProfiles;

		try {
			
			final List<String> movies = Util.newList();
			movies.addAll(Arrays.asList(new String[] {
				"Action", "Adventure", "Animation", "Children's", "Comedy", 
				"Crime", "Documentary", "Drama", "Fantasy", "Film-Noir", 
				"Horror", "Musical", "Mystery", "Romance", "Sci-Fi", 
				"Thriller", "War", "Western" }));

			Attribute itemid = new Attribute(DataConfig.ITEMID_FIELD, Type.integer);

			Attribute title = new Attribute("title", Type.string);
			
			Attribute release_date = new Attribute("release_date", Type.date);
			
			Attribute video_release_date = new Attribute("video_release_date", Type.date);

			Attribute imdb_url = new Attribute("imdb_url", Type.string);
			
			Attribute unknown = new Attribute("unknown", Type.bit);

			Attribute action = new Attribute("action", Type.bit);
			
			Attribute adventure = new Attribute("adventure", Type.bit);
			
			Attribute animation = new Attribute("animation", Type.bit);
			
			Attribute children = new Attribute("children", Type.bit);
			
			Attribute comedy = new Attribute("comedy", Type.bit);
			
			Attribute crime = new Attribute("crime", Type.bit);
			
			Attribute documentary = new Attribute("documentary", Type.bit);

			Attribute drama = new Attribute("drama", Type.bit);

			Attribute fantasy = new Attribute("fantasy", Type.bit);
			
			Attribute film_noir = new Attribute("film_noir", Type.bit);

			Attribute horror = new Attribute("horror", Type.bit);
			
			Attribute musical = new Attribute("musical", Type.bit);
			
			Attribute mystery = new Attribute("mystery", Type.bit);
			
			Attribute romance = new Attribute("romance", Type.bit);

			Attribute sci_fi = new Attribute("sci_fi", Type.bit);
			
			Attribute thriller = new Attribute("thriller", Type.bit);

			Attribute war = new Attribute("war", Type.bit);
			
			Attribute western = new Attribute("western", Type.bit);
			
			final AttributeList attList = AttributeList.create(new Attribute[] {
					itemid,
					title, release_date, video_release_date, imdb_url,
					unknown, action, adventure, animation, children, 
					comedy, crime, documentary, drama, fantasy, film_noir,
					horror, musical, mystery, romance, sci_fi, thriller, 
					war, western});
			
			final Map<Integer, Profile> profileMap = Util.newMap();
			
			adapter = new UriAdapter(config);
			
			xURI store = config.getStoreUri();
			xURI itemsUri = store.concat(config.getItemUnit());
			reader = new BufferedReader(adapter.getReader(itemsUri));

			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					List<String> array = DSUtil.splitAllowEmpty(line, "::", null);
					if (array.size() < 3)
						return;
					
					Profile profile = new Profile(attList);
					profile.setKey(0);
					
					int id = Integer.parseInt(array.get(0));
					profile.setValue(0, id);
					profile.setValue(1, array.get(1));
					
					profile.setMissing(2);
					profile.setMissing(3);
					profile.setMissing(4);
					
					if (array.get(2).isEmpty())
						profile.setValue(5, 1);
					
					List<String> idxsList = TextParserUtil.split(array.get(2), "[\\|]", null);
					List<Integer> idxList = Util.newList();
					for (String idxs : idxsList) {
						int idx = movies.indexOf(idxs);
						if (idx != -1)
							idxList.add(idx);
					}
					
					for (int idx : idxList) {
						profile.setValue(idx + 6, 1);
					}

					profileMap.put(id, profile);
					
				}
			});
			
			memProfiles = MemProfiles.assign(attList, profileMap);
			
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
			if (adapter != null)
				adapter.close();
		}
		
		return memProfiles;
		
	}

	
	@Override
	public String getName() {
		return "movielens_parser";
	}
	

	@Override
	public String getDescription() throws RemoteException {
		return "Movielens parser";
	}


	@Override
	public DataConfig createDefaultConfig() {
		DataConfig tempConfig = super.createDefaultConfig();
		tempConfig.put(MOVIELENS_TYPE_FIELD, MOVIELENS_TYPE_100K);

		DataConfig config = new DataConfig() {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Serializable userEdit(Component comp, String key, Serializable defaultValue) {
				if (key.equals(MOVIELENS_TYPE_FIELD)) {
					String type = getAsString(MOVIELENS_TYPE_FIELD);
					type = type == null ? MOVIELENS_TYPE_100K : type;
					return (Serializable) JOptionPane.showInputDialog(
							comp, 
							"Please choose one Movielens type", 
							"Choosing Movielens type", 
							JOptionPane.INFORMATION_MESSAGE, 
							null, 
							MOVIELENS_TYPES_SUPPORTED, 
							type);
				}
				else 
					return tempConfig.userEdit(comp, key, defaultValue);
			}
			
		};
		
		config.putAll(tempConfig);
		return config;
	}

	
}
