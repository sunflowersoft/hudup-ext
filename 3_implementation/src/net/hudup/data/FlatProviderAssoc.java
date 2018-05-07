package net.hudup.data;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Condition;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderAssocAbstract;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;


/**
 * This class is default implementation of a provider associator.
 * It inherits directly the abstract class {@link ProviderAssocAbstract}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FlatProviderAssoc extends ProviderAssocAbstract {

	
	/**
	 * Default delimiter.
	 */
	public final static char DELIMITER = ',';
	
	
	/**
	 * Internal URI adapter.
	 */
	protected UriAdapter adapter = null;
	
	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	public FlatProviderAssoc(DataConfig config) {
		super(config);
		this.adapter = new UriAdapter(config);
	}
	

	@Override
	public boolean createUnit(String unit, AttributeList attList) {
		CsvWriter writer = null;
		boolean result = true;
		
		try {
			xURI unitURI = getUnitUri(unit);
			if (adapter.exists(unitURI))
				adapter.delete(unitURI, null);
			adapter.create(unitURI, false);
			writer = getWriter(unit, false);
			
			writeHeader(writer, attList);
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			try {
				if (writer != null)
					writer.close();
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return result;
	}
	
	
	@Override
	public UnitList getUnitList() {
		UnitList tblList = new UnitList();
		try {
			UnitList defaultUnitList = DataConfig.getDefaultUnitList();
			
			xURI uri = config.getStoreUri(); // the method DataConfig.getStoreUri() may return URI of a unit.
			xURI store = adapter.isStore(uri) ? uri : adapter.getStoreOf(uri); // Getting URI of real store.
			List<xURI> uriList = adapter.getUriList(store, null);
			for (xURI u : uriList) {
				if (adapter.isStore(u))
					continue;
				
				String unitName = u.getLastName();
				Unit unit = new Unit(unitName);
				if (!defaultUnitList.contains(unitName))
					unit.setExtra(true);
					
				tblList.add(unit);
			}

		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Get file system metadata error: " + e.getMessage());
		}
		
		return tblList;
	}

	
	@Override
	public boolean deleteUnitData(String unitName) {
		xURI unitURI = getUnitUri(unitName);
		if (unitURI == null || !adapter.exists(unitURI))
			return false;
		else {
			AttributeList attributes = getAttributes(unitName);
			if (attributes.size() == 0)
				return false;
			else
				return createUnit(unitName, attributes);
				
		}
	}

	
	@Override
	public boolean dropUnit(String unitName) {
		xURI unitURI = getUnitUri(unitName);
		if (unitURI == null)
			return true;
		else if (adapter.exists(unitURI))
			return adapter.delete(unitURI, null);
		else
			return true;
	}
	
	
	@Override
	public NominalList getNominalList(String filterUnit, String attName) {
		NominalList nominalList = new NominalList();
		UnitList unitList = getUnitList();
		if (!unitList.contains(getConfig().getNominalUnit()))
			return nominalList;
		
		Profile profile = new Profile(getAttributes(getConfig().getNominalUnit()));
		profile.setValue(DataConfig.NOMINAL_REF_UNIT_FIELD, filterUnit);
		profile.setValue(DataConfig.ATTRIBUTE_FIELD, attName);
		
		Fetcher<Profile> fetcher = getProfiles(getConfig().getNominalUnit(), profile);
		try {
			while (fetcher.next()) {
				Profile nprofile = fetcher.pick();
				if (nprofile == null)
					continue;
				
				String nominalValue = nprofile.getValueAsString(DataConfig.NOMINAL_VALUE_FIELD);
				int nominalindex = nprofile.getValueAsInt(DataConfig.NOMINAL_INDEX_FIELD);
				if (nominalValue == null || nominalindex < 0)
					continue;
				
				int parentindex = nprofile.getValueAsInt(DataConfig.NOMINAL_PARENT_INDEX_FIELD);
				if (parentindex < 0)
					parentindex = Nominal.NULL_INDEX;
				
				nominalList.add(new Nominal(nominalValue, nominalindex, parentindex));
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				fetcher.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return nominalList;
	}

	
	@Override
	public AttributeList getAttributes(String profileUnit) {
		AttributeList list = new AttributeList();
		
		CsvReader reader = getReader(profileUnit);
		try {
			reader.readHeader();
			String[] header = reader.getHeader();
			reader.close();
			reader = null;
			
			for (int i = 0; i < header.length; i++) {
				Attribute attribute = new Attribute();
				attribute.parseText(header[i]);
				
				if (attribute.getType() == Type.integer && !profileUnit.equals(getConfig().getNominalUnit())) {
					NominalList nominalList = getNominalList(profileUnit, attribute.getName());
					if (nominalList.size() > 0) {
						Attribute newAtt = new Attribute(attribute.getName(), nominalList);
						newAtt.setAutoInc(attribute.isAutoInc());
						newAtt.setIndex(attribute.getIndex());
						newAtt.setKey(attribute.isKey());
						
						attribute = newAtt;
					}
				}
				
				list.add(attribute);
			}
			
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return list;
	}

	
	@Override
	public AttributeList getAttributes(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement yet");
	}


	@Override
	public boolean containsProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		
		CsvReader reader = getReader(profileUnit);
		boolean found = false;
		try {
			reader.readHeader();
			String[] header = reader.getHeader();
			while (reader.readRecord()) {
				String[] record = reader.getRecord();
				if (record.length == 0)
					continue;
				
				if(recordEqualsProfileValues(header, profile, record)) {
					found = true;
					break;
				}
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			found = false;
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return found;
	}


	@Override
	public Profile getProfile(String profileUnit, Profile condition) {
		
		AttributeList attributes = getAttributes(profileUnit);
		Profile returnProfile = null;
		CsvReader reader = null;
		try {
			reader = getReader(profileUnit);
			reader.readHeader();
			String[] header = reader.getHeader();
			
			while (reader.readRecord()) {
				String[] record = reader.getRecord();
				if (record.length == 0)
					continue;
				
				if(recordEqualsProfileValues(header, condition, record)) {
					returnProfile = getProfile(record, attributes);
					break;
				}
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			returnProfile = null;
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return returnProfile;
	}
	
	
	@Override
	public Fetcher<Profile> getProfiles(String profileUnit,
			Profile condition) {
		// TODO Auto-generated method stub

		List<Profile> list = Util.newList();
		
		AttributeList attributes = getAttributes(profileUnit);
		CsvReader reader = null;
		try {
			reader = getReader(profileUnit);
			reader.readHeader();
			String[] header = reader.getHeader(); 
			
			while (reader.readRecord()) {
				String[] record = reader.getRecord();
				if (record.length == 0)
					continue;
				
				Profile csvProfile = getProfile(record, attributes);
				if (csvProfile == null)
					continue;
				else if (condition == null)
					list.add(csvProfile);
				else if (recordEqualsProfileValues(header, condition, record))
					list.add(csvProfile);
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return new MemFetcher<Profile>(list);
	}


	@Override
	public Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implement yet");
	}


	@Override
	public Fetcher<Integer> getProfileIds(String profileUnit) {
		// TODO Auto-generated method stub
		AttributeList attributes = getAttributes(profileUnit);
		final Attribute idAtt = attributes.getId();
		if (idAtt == null || idAtt.getType() != Type.integer)
			return new MemFetcher<Integer>();
		
		List<Integer> ids = Util.newList();
		Fetcher<Profile> fetcher = getProfiles(profileUnit, null);
		try {
			while (fetcher.next()) {
				Profile profile = fetcher.pick();
				if (profile == null)
					continue;
				
				int id = profile.getValueAsInt(idAtt.getName());
				if (id >= 0)
					ids.add(id);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (fetcher != null)
					fetcher.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return new MemFetcher<Integer>(ids);
	}


	@Override
	public int getProfileMaxId(String profileUnit) {
		// TODO Auto-generated method stub
		
		AttributeList attributes = getAttributes(profileUnit);
		CsvReader reader = null;
		int maxId = -1;
		try {
			reader = getReader(profileUnit);
			reader.readHeader();
			
			Attribute id = attributes.getId();
			if (id == null)
				throw new Exception("Null id");
			
			while (reader.readRecord()) {
				String[] record = reader.getRecord();
				if (record.length == 0)
					continue;
				
				Profile profile = getProfile(record, attributes);
				maxId = Math.max(maxId, profile.getIdValueAsInt());
			}
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			
			maxId = -1;
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return maxId < 0 ? -1 : maxId;
	}


	@Override
	public boolean insertProfile(String profileUnit, Profile profile) {
		if (profile == null || profile.getAttCount() == 0)
			return false;
		
		boolean result = true;
		CsvWriter writer = getWriter(profileUnit, true);
		try {
			writer.writeRecord(toStringArray(profile));
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			try {
				if (writer != null)
					writer.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}

		return result;
	}
	
	
	@NextUpdate
	@Override
	public boolean updateProfile(String profileUnit, Profile profile) {
		if (profile == null || profile.getAttCount() == 0)
			return false;
		
		CsvReader reader = null;
		CsvWriter writer = null;
		boolean result = true;
		try {
			reader = getReader(profileUnit);
			reader.readHeader();
			
			String[] header = reader.getHeader(); 
			List<String[]> data = Util.newList();
			while (reader.readRecord()) {
				String[] record = reader.getRecord();
				if (record.length == 0)
					continue;
				
				data.add(record);
			}
			reader.close();
			reader = null;
			
			deleteUnitData(profileUnit);
			writer = getWriter(profileUnit, true);

			boolean updated = false;
			for (String[] record : data) {
				if (profile != null && recordEqualsProfileKeyValues(header, profile, record)) {
					writer.writeRecord(toStringArray(profile));
				}
				else {
					updated = true;
					writer.writeRecord(record);
				}
			}
			if (!updated)
				writer.writeRecord(toStringArray(profile));
			
			writer.close();
			writer = null;
		}
		catch (Throwable e) {
			e.printStackTrace();
			
			result = false;
		}
		finally {
			try {
				if (reader != null)
					reader.close();
					
				if (writer != null)
					writer.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}

	
	@NextUpdate
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) {
		if (condition == null || condition.getAttCount() == 0)
			return false;
		
		CsvReader reader = null;
		CsvWriter writer = null;
		boolean result = true;
		try {
			reader = getReader(profileUnit);
			reader.readHeader();
			String[] headers = reader.getHeader(); 
			List<String[]> data = Util.newList();
			while (reader.readRecord()) {
				String[] record = reader.getRecord();
				if (record.length == 0)
					continue;
				
				data.add(record);
			}
			reader.close();
			reader = null;
			
			deleteUnitData(profileUnit);
			writer = getWriter(profileUnit, true);
			for (String[] record : data) {
				if (!recordEqualsProfileValues(headers, condition, record))
					writer.writeRecord(record);
			}
			writer.close();
			writer = null;
			
		}
		catch (Throwable e) {
			e.printStackTrace();
			result = false;
		}
		finally {
			try {
				if (reader != null)
					reader.close();
					
				if (writer != null)
					writer.close();
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		return result;
	}


	/**
	 * Converting the specified profile values into an array of strings
	 * @param profile specified profile.
	 * @return string array of profile values.
	 */
	public static String[] toStringArray(Profile profile) {
		List<String> record = Util.newList();

		int n = profile.getAttCount();
		for (int i = 0; i < n; i++) {
			Object value = profile.getValue(i);
			if (value == null)
				record.add("");
			else if (value instanceof Date) {
				SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
				record.add(df.format(value));
			}
			else
				record.add(value.toString());
		}
		
		return record.toArray(new String[] { });
	}
	
	
	/**
	 * Getting URI of specified unit.
	 * @param unit specified unit
	 * @return {@link xURI} of specified unit.
	 */
	private xURI getUnitUri(String unit) {
		xURI store = config.getStoreUri();
		if (store == null)
			return null;
		else
			return store.concat(unit);
		
	}
	
	
	/**
	 * Comparing the specified record and the specified profile.
	 * @param header specified header.
	 * @param profile specified header.
	 * @param record specified record.
	 * @return whether record at current row equals profile key values.
	 */
	private static boolean recordEqualsProfileKeyValues(String[] header, Profile profile, String[] record) {
		Condition keyValues = profile.getKeyValues();
		int n = keyValues.getAttCount();
		
		boolean equal = true;
		for (int i = 0; i < n; i++) {
			try {
				Object columnValue = getValue(header, keyValues.getAtt(i).getName(), record);
				
				if (columnValue == null || 
						!columnValue.equals(profile.getValue(i))) {
					equal = false;
					break;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				equal = false;
				break;
			}
		}
		
		return equal;
	}

	
	/**
	 * Comparing the specified record and the specified profile.
	 * @param header specified header.
	 * @param profile specified profile.
	 * @param record specified record.
	 * @return whether record at current row equals profile values.
	 */
	private static boolean recordEqualsProfileValues(String[] header, Profile profile, String[] record) {
		int n = profile.getAttCount();
		
		boolean equal = true;
		for (int i = 0; i < n; i++) {
			try {
				Object profileValue = profile.getValue(i);
				if (profileValue == null)
					continue;
				
				Object columnValue = getValue(header, profile.getAtt(i).getName(), record);
				
				if (columnValue == null || 
						!columnValue.equals(profileValue)) {
					equal = false;
					break;
				}
			} 
			catch (Throwable e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				equal = false;
				break;
			}
		}
		
		return equal;
	}

	
	/**
	 * Writing CSV header by attribute list.
	 * @param writer CSV writer.
	 * @param attributes specified attribute list.
	 */
	private static void writeHeader(CsvWriter writer, AttributeList attributes) {
		if (attributes.size() == 0)
			return;
		
		String[] header = new String[attributes.size()];
		for (int i = 0; i < attributes.size(); i++) {
			Attribute attribute = attributes.get(i);
			Attribute newAtt = attribute;
			
			if (attribute.getType() == Type.nominal) {
				newAtt = new Attribute(attribute.getName(), Type.integer);
				newAtt.setAutoInc(attribute.isAutoInc());
				newAtt.setIndex(attribute.getIndex());
				newAtt.setKey(attribute.isKey());
			}
			
			header[i] = newAtt.toText();
		}
		
		try {
			writer.writeRecord(header);
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Getting the value of specified record at specified attribute.
	 * @param header specified header.
	 * @param attributeName specified attribute.
	 * @param record specified record.
	 * @return the value of specified record at specified attribute.
	 */
	private static Object getValue(String[] header, String attributeName, String[] record) {
		try {
			int foundIdx = -1;
			Attribute found = null;
			for (int i = 0; i < header.length; i++) {
				Attribute attribute = new Attribute();
				attribute.parseText(header[i]);
				if (attribute.getName().equals(attributeName)) {
					foundIdx = i;
					found = attribute;
					break;
				}
			}
			
			if (foundIdx != -1)
				return Profile.createValue(found, record[foundIdx]);
			else
				return null;
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}

	
	/**
	 * Getting profile with regard to specified attribute list.
	 * @param record specified record.
	 * @param attributes specified attribute list.
	 * @return {@link Profile} with regard to specified attribute list.
	 */
	public static Profile getProfile(String[] record, AttributeList attributes) {
		if (attributes.size() == 0)
			return null;
		
		try {
			Profile profile = new Profile(attributes);
			for (int i = 0; i < attributes.size(); i++) {
				Object value = null;
				try {
					value = Profile.createValue(attributes.get(i), record[i]);
				} 
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				profile.setValue(i, value);
			}
			return profile;
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		if (adapter != null)
			adapter.close();
		adapter = null;
	}

	
	/**
	 * Interface for CSV reader.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static interface CsvReader {
		
		/**
		 * Reading header of CSV file.
		 * @return whether reading successfully.
		 * @throws IOException if any IO error raises.
		 */
		boolean readHeader() throws IOException;
		
		/**
		 * Getting header of CSV file.
		 * @return header as an array of strings.
		 * @throws IOException if any IO error raises.
		 */
		String[] getHeader() throws IOException;
		
		/**
		 * Reading record of CSV file.
		 * @return whether reading successfully.
		 * @throws IOException if any IO error raises.
		 */
		boolean readRecord() throws IOException;
		
		/**
		 * Getting a record of CSV file.
		 * @return record as an array of strings.
		 * @throws IOException if any IO error raises.
		 */
		String[] getRecord() throws IOException;
		
		/**
		 * Close this reader.
		 */
		void close();
	}
	
	
	/**
	 * Interface for CSV writer.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static interface CsvWriter {
		
		/**
		 * Writing the specified column to current record.
		 * @param column column (field) to be written.
		 * @throws IOException if any error raises.
		 */
		void write(String column) throws IOException;
		
		/**
		 * Writing the specified record into CSV file.
		 * @param record record (row) to be written. 
		 * @throws IOException if any error raises.
		 */
		void writeRecord(String[] record) throws IOException;
		
		/**
		 * Close this writer.
		 */
		void close();
	}
	
	
	/**
	 * Creating CSV from from specified unit.
	 * @param unit specified unit.
	 * @return {@link CsvReader} from specified unit.
	 */
	public CsvReader getReader(String unit) {
		if (unit == null || unit.isEmpty())
			return null;
		xURI unitURI = getUnitUri(unit);
		if (unitURI == null || !adapter.exists(unitURI))
			return null;

		Reader reader = adapter.getReader(unitURI);
		final com.csvreader.CsvReader csvReader = new com.csvreader.CsvReader(reader);
		return new CsvReader() {
			
			@Override
			public boolean readHeader() throws IOException {
				// TODO Auto-generated method stub
				return csvReader.readHeaders();
			}
			
			@Override
			public String[] getHeader() throws IOException {
				// TODO Auto-generated method stub
				return csvReader.getHeaders();
			}

			@Override
			public boolean readRecord() throws IOException {
				// TODO Auto-generated method stub
				return csvReader.readRecord();
			}
			
			@Override
			public String[] getRecord() throws IOException {
				// TODO Auto-generated method stub
				return csvReader.getValues();
			}
			
			@Override
			public void close() {
				// TODO Auto-generated method stub
				csvReader.close();
			}
		};
	}
	
	
	/**
	 * Creating CSV writer from specified unit.
	 * @param unit specified unit.
	 * @param append if true that allowing to continue to write at the end of this CSV file.
	 * @return {@link CsvWriter} from specified unit.
	 */
	public CsvWriter getWriter(String unit, boolean append) {
		xURI unitURI = getUnitUri(unit);
		if (unitURI == null || !adapter.exists(unitURI))
			return null;
		
		Writer writer = adapter.getWriter(unitURI, append);
		final com.csvreader.CsvWriter csvWriter = new com.csvreader.CsvWriter(writer, DELIMITER);
		return new CsvWriter() {
			
			@Override
			public void write(String column) throws IOException {
				// TODO Auto-generated method stub
				csvWriter.write(column);
			}
			
			@Override
			public void writeRecord(String[] record) throws IOException {
				// TODO Auto-generated method stub
				csvWriter.writeRecord(record);
			}

			@Override
			public void close() {
				// TODO Auto-generated method stub
				csvWriter.close();
			}
		};
	}
	
	
}
