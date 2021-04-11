/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.factory;

import java.rmi.NoSuchObjectException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.sql.Types;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.FetcherMetadata;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.Keys;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.data.Nominal;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderAssocAbstract;
import net.hudup.core.data.Unit;
import net.hudup.core.data.UnitList;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.TextParsable;
import net.hudup.core.parser.TextParserUtil;

/**
 * Associator of provider for database.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class DbProviderAssoc extends ProviderAssocAbstract {

	
	/**
	 * Internal connection.
	 */
	protected Connection conn = null;
	
	
	/**
	 * Constructor with specified configuration.
	 * 
	 * @param config specified configuration.
	 */
	public DbProviderAssoc(DataConfig config) {
		super(config);
		this.conn = createConnection(config);
	}

	
	/**
	 * Getting internal database connection.
	 * @return {@link Connection} as internal database connection.
	 */
	public Connection getConnection() {
		return conn;
	}
	
	
	/**
	 * Converting internal attribute type into SQL type.
	 * @param type specified attribute type.
	 * @return SQL type of {@link Type}
	 */
	public static int toSqlType(Type type) {
		switch (type) {
		case bit:
			return Types.INTEGER; //Types.BIT
		case nominal:
			return Types.INTEGER;
		case integer:
			return Types.INTEGER;
		case real:
			return Types.REAL;
		case string:
			return Types.VARCHAR;
		case date:
			return Types.DATE;
		case time:
			return Types.INTEGER; //Types.TIMESTAMP
		case object:
			return Types.JAVA_OBJECT;
		default:
			return Types.JAVA_OBJECT;
		}
		
	}

	
	/**
	 * Converting internal attribute type into SQL type name.
	 * @param type internal attribute type.
	 * @return SQL type name of {@link Type}
	 */
	public String toSqlTypeName(Type type) {
		int sqlType = toSqlType(type);
		String sqlTypeName = "";
		
		try {
			DatabaseMetaData meta = conn.getMetaData();
		    ResultSet rs = meta.getTypeInfo();
		    
		    while (rs.next()) {
		    	int dbType = rs.getInt("DATA_TYPE");
		    	
		    	if (dbType == sqlType) {
		    		sqlTypeName = rs.getString("TYPE_NAME");
		    		break;
		    	}
		    }
		    
		    if (!sqlTypeName.isEmpty()) {
		    	if(type == Type.string)
		    		sqlTypeName += "(256)";
		    }
		    
		    rs.close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return sqlTypeName;
	}

	
	/**
	 * Converting SQL type into attribute type.
	 * @param sqlType SQL type.
	 * @return {@link Type} from SQL type.
	 */
	public static Attribute.Type fromSqlType(int sqlType) {
		if (sqlType == Types.ARRAY) //java.lang.reflect.Array
			return Attribute.Type.object;
		else if (sqlType == Types.BIGINT) //Long
			return Attribute.Type.integer;
		else if (sqlType == Types.BINARY) //String, byte[]
			return Attribute.Type.object;
		else if (sqlType == Types.BIT) //Boolean
			return Attribute.Type.bit;
		else if (sqlType == Types.BLOB) // java.sql.Blob
			return Attribute.Type.object;
		else if (sqlType == Types.BOOLEAN) // Boolean
			return Attribute.Type.bit;
		else if (sqlType == Types.CHAR) // String
			return Attribute.Type.string;
		else if (sqlType == Types.CLOB) //java.sql.Clob
			return Attribute.Type.object;
		else if (sqlType == Types.DATALINK)
			return Attribute.Type.object; 
		else if (sqlType == Types.DATE)  // java.sql.Date
			return Attribute.Type.date; 
		else if (sqlType == Types.DECIMAL)  // Double
			return Attribute.Type.real;
		else if (sqlType == Types.DISTINCT)
			return Attribute.Type.object;
		else if (sqlType == Types.DOUBLE) // Double
			return Attribute.Type.real;
		else if (sqlType == Types.FLOAT) // Float
			return Attribute.Type.real;
		else if (sqlType == Types.INTEGER) // Integer
			return Attribute.Type.integer;
		else if (sqlType == Types.JAVA_OBJECT) // Object
			return Attribute.Type.object;
		else if (sqlType == Types.LONGNVARCHAR) // String
			return Attribute.Type.string;
		else if (sqlType == Types.LONGVARBINARY) // String, byte[]
			return Attribute.Type.object;
		else if (sqlType == Types.LONGVARCHAR) // String
			return Attribute.Type.string;
		else if (sqlType == Types.NULL)
			return Attribute.Type.object;
		else if (sqlType == Types.NUMERIC) // Double
			return Attribute.Type.real;
		else if (sqlType == Types.OTHER)
			return Attribute.Type.object;
		else if (sqlType == Types.REAL) // Double
			return Attribute.Type.real;
		else if (sqlType == Types.REF) //java.sql.Ref
			return Attribute.Type.real;
		else if (sqlType == Types.SMALLINT) // Integer
			return Attribute.Type.integer;
		else if (sqlType == Types.STRUCT) // java.sql.Struct
			return Attribute.Type.object;
		else if (sqlType == Types.TIME) // java.sql.Time
			return Attribute.Type.date;
		else if (sqlType == Types.TIMESTAMP) // java.sql.Timestamp
			return Attribute.Type.date;
		else if (sqlType == Types.TINYINT) // Integer
			return Attribute.Type.integer;
		else if (sqlType == Types.VARBINARY) // String, byte[]
			return Attribute.Type.object;
		else if (sqlType == Types.VARCHAR)
			return Attribute.Type.string;
		else
			return Attribute.Type.object;
			
	}

	
	/**
	 * Normalizing the specified string into a string for particular database.
	 * For example, a table name of MySQL database should be wrapped by quotes like `Table 1`. 
	 * @param string specified string.
	 * @return normalized DBMS SQL string like `Table 1` for MySQL.
	 */
	public String norm(String string) {
		xURI store = config.getStoreUri();
		if (store == null)
			return "\"" + string + "\"";
		
		DataDriver driver = DataDriver.create(store);
		if (driver.getInnerClassName().equals(DataDriver.MYSQL_DRIVER_CLASS_NAME))
			return "`" + string + "`";
		else
			return "\"" + string + "\"";
	}
	
	
	/**
	 * Converting a specified list of attributes into a condition in form of &quot;a1 = ? and a2 = ?&quot;.
	 * @param list a specified list of attributes.
	 * @return condition text of list of {@link Attribute} in form of &quot;a1 = ? and a2 = ?&quot;.
	 */
	private String toAndConditionText(List<Attribute> list) {
		StringBuffer buffer = new StringBuffer();
		
		for (int i = 0; i < list.size(); i++) {
			if (i > 0)
				buffer.append(" and ");
			
			buffer.append(norm(list.get(i).getName()) + " = ?");
		}
		
		return buffer.toString();
	}
	
	
	/**
	 * Getting indexes of the specified list of attributes.
	 * @param list the specified list of attributes.
	 * @return list of indices of attributes
	 */
	private List<Integer> getIndexes(List<Attribute> list) {
		List<Integer> indexes = Util.newList();
		
		for (int i = 0; i < list.size(); i++) {
			indexes.add(list.get(i).getIndex());
		}
		
		return indexes;
	}

	
	/**
	 * Generate the create SQL for configuration table.
	 * 
	 * @return create SQL for configuration table.
	 */
	public String genConfigCreateSql() {
		return "create table " + norm(config.getConfigUnit()) + " ( " + 
				norm(DataConfig.ATTRIBUTE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				"primary key (" + norm(DataConfig.ATTRIBUTE_FIELD) + ") )";
	}
	
	
	/**
	 * Generate the select SQL given specified attribute for configuration table.
	 * @param att specified attribute.
	 * @return select SQL given specified attribute for configuration table.
	 */
	public String genConfigSelectSql(String att) {
		return  "select * from " + norm(config.getConfigUnit()) + 
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}

	
	/**
	 * Generate the select SQL for configuration table.
	 * @return select SQL for configuration table.
	 */
	public String genConfigSelectSql() {
		return "select * from " + norm(config.getConfigUnit());
	}
	
	
	/**
	 * Generate select SQL for retrieving minimum rating value from configuration table.
	 * @return select SQL for retrieving minimum rating value from configuration table.
	 */
	public String genMinRatingSelectSql() {
		return "select " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " from " + norm(config.getConfigUnit()) + 
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + DataConfig.MIN_RATING_FIELD + "'";
	}
	
	
	/**
	 * Generate select SQL for retrieving maximum rating value from configuration table.
	 * @return select SQL for retrieving maximum rating value from configuration table.
	 */
	public String genMaxRatingSelectSql() {
		return "select " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " from " + norm(config.getConfigUnit()) + 
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + DataConfig.MAX_RATING_FIELD + "'";
	}

	
	/**
	 * Generate an insert SQL statement for inserting a pair of attribute and value into configuration table.
	 * @param att specified attribute.
	 * @param value specified value.
	 * @return insert SQL statement for inserting a pair of attribute and value into configuration table.
	 */
	public String genConfigInsertSql(String att, String value) {
		return 	"insert into " + norm(config.getConfigUnit()) + 
				" (" + norm(DataConfig.ATTRIBUTE_FIELD) + ", " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + ")" +
				" values ('" + att + "', '" + value + "')";
	}

	
	/**
	 * Generate an parametric insertion SQL for configuration table.
	 * @return parametric insertion SQL for configuration table.
	 */
	public ParamSql genConfigInsertSql() {
		return new ParamSql(
				"insert into " + norm(config.getConfigUnit()) + 
				" (" + norm(DataConfig.ATTRIBUTE_FIELD) + ", " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + ")" +
				" values (?, ?)",
				new int[] {0, 1});
	}

	
	/**
	 * Generate an updating SQL statement for updating a pair of attribute and value into configuration table.
	 * @param att specified attribute.
	 * @param value specified value.
	 * @return updating SQL statement for updating a pair of attribute and value into configuration table.
	 */
	public String genConfigUpdateSql(String att, String value) {
		return 	"update " + norm(config.getConfigUnit()) + 
				" set " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " = '" + value + "' " +
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}

	
	/**
	 * Generate an parametric updating SQL for configuration table.
	 * @return parametric updating SQL for configuration table.
	 */
	public ParamSql genConfigUpdateSql() {
		return new ParamSql(
				"update " + norm(config.getConfigUnit()) + 
				" set " + norm(DataConfig.ATTRIBUTE_VALUE_FIELD) + " = ?" + 
				" where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = ?",
				new int[] {1, 0});
	}

	
	/**
	 * Generate an deletion SQL statement for deleting a specified attribute from configuration table.
	 * @param att specified attribute.
	 * @return deletion SQL statement for deleting a specified attribute from configuration table.
	 */
	public String genConfigDeleteSql(String att) {
		return 
			"delete from " + norm(config.getConfigUnit()) + " where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}
	
	
	/**
	 * Generate an parametric deletion SQL statement for deleting a specified attribute from configuration table.
	 * @return parametric deletion SQL statement for deleting a specified attribute from configuration table.
	 */
	public ParamSql genConfigDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getConfigUnit()) + " where " + norm(DataConfig.ATTRIBUTE_FIELD) + " = ?",
				new int[] {0});
	}

	
//	/**
//	 * Generating rating creation table SQL.
//	 * @return rating creation table SQL.
//	 */
//	public String genRatingCreateSql() {
//		return 	"create table " + norm(config.getRatingUnit()) + " ( " + 
//				norm(DataConfig.USERID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
//				norm(DataConfig.ITEMID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
//				norm(DataConfig.RATING_FIELD) + " " + toSqlTypeName(Type.real) + " not null, " +
//				norm(DataConfig.RATING_DATE_FIELD) + " " + toSqlTypeName(Type.time) + ", " +
//				"primary key (" + norm(DataConfig.USERID_FIELD) + ", " + norm(DataConfig.ITEMID_FIELD) + ") )";
//	}
	
	
	/**
	 * Generating rating creation table SQL.
	 * @return rating creation table SQL.
	 */
	public String genRatingCreateSql() {
		return 	"create table " + norm(config.getRatingUnit()) + " ( " + 
				norm(DataConfig.USERID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.ITEMID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.RATING_FIELD) + " " + toSqlTypeName(Type.real) + " not null, " +
				norm(DataConfig.RATING_DATE_FIELD) + " " + toSqlTypeName(Type.time);
	}

	
	/**
	 * Generate rating select SQL.
	 * @return rating select SQL
	 */
	public String genRatingSelectSql() {
		return "select * from " + norm(config.getRatingUnit());
	}


	/**
	 * Generate rating insert SQL.
	 * @return rating insert SQL.
	 */
	public ParamSql genRatingInsertSql() {
		return new ParamSql(
				"insert into " + norm(config.getRatingUnit()) + 
				" (" + norm(DataConfig.USERID_FIELD) + ", " + norm(DataConfig.ITEMID_FIELD) + ", " + norm(DataConfig.RATING_FIELD) + ", " + norm(DataConfig.RATING_DATE_FIELD) + 
				") values (?, ?, ?, ?)",
				new int[] {0, 1, 2, 3});
	}

	
	/**
	 * Generate rating update SQL.
	 * @return rating update SQL.
	 */
	public ParamSql genRatingUpdateSql() {
		return new ParamSql(
				"update " + norm(config.getRatingUnit()) + 
			   " set " + norm(DataConfig.RATING_FIELD) + " = ?" + ", " + norm(DataConfig.RATING_DATE_FIELD) + " = ?" +
			   " where " + norm(DataConfig.USERID_FIELD) + " = ?" + 
			   " and " + norm(DataConfig.ITEMID_FIELD) + " = ?",
			   new int[] {2, 3, 0, 1});
	}

	
	/**
	 * Generate rating delete SQL.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @return delete SQL
	 */
	public String genRatingDeleteSql(int userId, int itemId) {
		return 
			"delete from " + norm(config.getRatingUnit()) + 
			" where " + norm(DataConfig.USERID_FIELD) + " = " + userId + " and " + 
						norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}
	
	
	/**
	 * Generate rating delete parameterized SQL.
	 * @return rating delete parameterized SQL.
	 */
	public ParamSql genRatingDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getRatingUnit()) + 
				" where " + norm(DataConfig.USERID_FIELD) + " = ?" + " and " + norm(DataConfig.ITEMID_FIELD) + " = ?",
				new int[] {0, 1});
	}

	
	/**
	 * Generating user rating select SQL.
	 * @param userId user identifier.
	 * @return select SQL
	 */
	public String genUserRatingSelectSql(int userId) {
		return genRatingSelectSql() + " where " + norm(DataConfig.USERID_FIELD) + " = " + userId;
	}
	
	
	/**
	 * Generating user id rating select SQL.
	 * @return select SQL
	 */
	public String genUserIdsRatingSelectSql() {
		return "select distinct " + norm(DataConfig.USERID_FIELD) + " from " + norm(config.getRatingUnit());
	}
	
	
	/**
	 * Generating user-item rating select SQL.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @return select SQL.
	 */
	public String genUserItemRatingSelectSql(int userId, int itemId) {
		return genRatingSelectSql() + " where " + 
				norm(DataConfig.USERID_FIELD) + " = " + userId + " and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}
	
	
	/**
	 * Generating user rating delete SQL.
	 * @param userId user identifier.
	 * @return delete SQL
	 */
	public String genUserRatingDeleteSql(int userId) {
		return "delete from " + norm(config.getRatingUnit()) + " where " + norm(DataConfig.USERID_FIELD) + " = " + userId;
		
	}
	
	
	/**
	 * Generating user id profile select SQL.
	 * @return select SQL.
	 */
	public String genUserIdsProfileSelectSql() {
		return "select " + norm(DataConfig.USERID_FIELD) + " from " + norm(config.getUserUnit());
	}
	
	
	/**
	 * Generating maximum user id profile select SQL.
	 * @return select SQL.
	 */
	public String genMaxUserIdProfileSelectSql() {
		return "select max(" + norm(DataConfig.USERID_FIELD) + ") from " + norm(config.getUserUnit());
	}

	
	/**
	 * Generating user profile select SQL.
	 * @return select SQL.
	 */
	public String genUserProfileSelectSql() {
		return "select * from " + norm(config.getUserUnit());
	}
	
	
	/**
	 * Generating user profile select SQL.
	 * @param userId user identifier.
	 * @return select SQL.
	 */
	public String genUserProfileSelectSql(int userId) {
		return genUserProfileSelectSql() + " where " + norm(DataConfig.USERID_FIELD) + " = " + userId;
	}
	
	
	/**
	 * Generating user profile delete SQL.
	 * @param userId user identifier.
	 * @return delete SQL.
	 */
	public String genUserProfileDeleteSql(int userId) {
		return 
			"delete from " + norm(config.getUserUnit()) + " where " + norm(DataConfig.USERID_FIELD) + " = " + userId; 
	}

	
	/**
	 * Generating user profile delete SQL.
	 * @return delete SQL.
	 */
	public ParamSql genUserProfileDeleteSql() {
		return new ParamSql(
			"delete from " + norm(config.getUserUnit()) + " where " + norm(DataConfig.USERID_FIELD) + " = ?",
			new int[] {0}); 
	}

	
	/**
	 * Generating item rating select SQL.
	 * @param itemId item identifier.
	 * @return select SQL.
	 */
	public String genItemRatingSelectSql(int itemId) {
		return genRatingSelectSql() + " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}
	
	
	/**
	 * Generating item id rating select SQL.
	 * @return select SQL.
	 */
	public String genItemIdsRatingSelectSql() {
		return "select distinct " + norm(DataConfig.ITEMID_FIELD) + " from " + norm(config.getRatingUnit());
	}

	
	/**
	 * Generating user rating delete SQL.
	 * @param itemId item identifier.
	 * @return delete SQL.
	 */
	public String genItemRatingDeleteSql(int itemId) {
		return "delete from " + norm(config.getRatingUnit()) + " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
		
	}

	
	/**
	 * Generating item id profile select SQL.
	 * @return select SQL.
	 */
	public String genItemIdsProfileSelectSql() {
		return "select " + norm(DataConfig.ITEMID_FIELD) + " from " + norm(config.getItemUnit());
	}

	
	/**
	 * Generating maximum item id profile select SQL.
	 * @return select SQL.
	 */
	public String genMaxItemIdProfileSelectSql() {
		return "select max(" + norm(DataConfig.ITEMID_FIELD) + ") from " + norm(config.getItemUnit());
	}
	
	
	/**
	 * Generating item profile select SQL.
	 * @return select SQL.
	 */
	public String genItemProfileSelectSql() {
		return "select * from " + norm(config.getItemUnit());
	}

	
	/**
	 * Generating item profile select SQL.
	 * @param itemId item identifier.
	 * @return select SQL.
	 */
	public String genItemProfileSelectSql(int itemId) {
		return genItemProfileSelectSql() + " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}

	
	/**
	 * Generating item profile delete SQL.
	 * @param itemId item identifier.
	 * @return delete SQL.
	 */
	public String genItemProfileDeleteSql(int itemId) {
		return 
			"delete from " + norm(config.getItemUnit()) + " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId; 
	}

	
	/**
	 * Generating item profile delete SQL.
	 * @return delete SQL.
	 */
	public ParamSql genItemProfileDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getItemUnit()) + " where " + norm(DataConfig.ITEMID_FIELD) + " = ?",
				new int[] {0}); 
	}

	
	/**
	 * Generating nominal create SQL.
	 * @return create SQL.
	 */
	public String genNominalCreateSql() {
		return "create table " + norm(config.getNominalUnit()) + " ( " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.NOMINAL_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + " " + toSqlTypeName(Type.integer) + ", " +
				"primary key (" + 
					norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + ", " + 
					norm(DataConfig.ATTRIBUTE_FIELD) + ", " + 
					norm(DataConfig.NOMINAL_INDEX_FIELD) + ") )";
	}
	
	
	/**
	 * Generating nominal select SQL.
	 * @return select SQL.
	 */
	public String genNominalSelectSql() {
		return "select * from " + norm(config.getNominalUnit());
	}

	
	/**
	 * Generating nominal select SQL.
	 * @param refUnit reference unit.
	 * @param att specified attribute.
	 * @return select SQL.
	 */
	public String genNominalSelectSql(String refUnit, String att) {
		return 
			"select * from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}
	
	
	/**
	 * Generating nominal select SQL.
	 * @param refUnit reference unit.
	 * @param att specified attribute.
	 * @param nominal_index nominal index.
	 * @return select SQL.
	 */
	public String genNominalSelectSql(String refUnit, String att, int nominal_index) {
		return 
			"select * from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "' and " + 
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " = " + nominal_index;
	}
	
	
	/**
	 * Generating nominal insert SQL.
	 * @param refUnit reference unit.
	 * @param att specified attribute.
	 * @param nominal_index nominal index.
	 * @param nominal_value nominal value.
	 * @param parent_index parent index of specified nominal.
	 * @return insert SQL.
	 */
	public String genNominalInsertSql(
			String refUnit, String att, int nominal_index, String nominal_value, int parent_index) {
		
		return 
			"insert into " + norm(config.getNominalUnit()) + " (" + 
			norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + ", " + 
			norm(DataConfig.ATTRIBUTE_FIELD) + ", " + 
			norm(DataConfig.NOMINAL_INDEX_FIELD) + ", " + 
			norm(DataConfig.NOMINAL_VALUE_FIELD) + ", " + 
			norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + ") values ("+ 
			"'" + refUnit + "', '" + att + "', " + nominal_index + ", '" + nominal_value + "', " + parent_index + ")";
	}
	
	
	/**
	 * Generating nominal insert SQL.
	 * @return insert SQL.
	 */
	public ParamSql genNominalInsertSql() {
		
		return new ParamSql(
				"insert into " + norm(config.getNominalUnit()) + " (" + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + ", " + 
				norm(DataConfig.ATTRIBUTE_FIELD) + ", " + 
				norm(DataConfig.NOMINAL_INDEX_FIELD) + ", " + 
				norm(DataConfig.NOMINAL_VALUE_FIELD) + ", " + 
				norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + ") values (?, ?, ?, ?, ?)",
				new int[] {0, 1, 2, 3, 4});
		
	}

	
	/**
	 * Generating nominal update SQL.
	 * @param refUnit reference unit.
	 * @param att specified attribute.
	 * @param nominal_index nominal index.
	 * @param nominal_value nominal value.
	 * @param parent_index parent index of specified nominal.
	 * @return update SQL.
	 */
	public String genNominalUpdateSql(
			String refUnit, String att, int nominal_index, String nominal_value, int parent_index) {
		return 
			"update " + norm(config.getNominalUnit()) + " set " + 
				norm(DataConfig.NOMINAL_VALUE_FIELD) + " = '" + nominal_value + "', " +
				norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + " = " + parent_index + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " + 
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "' and " + 
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " = " + nominal_index;
	}


	/**
	 * Generating nominal update SQL.
	 * @return update SQL.
	 */
	public ParamSql genNominalUpdateSql() {
		return new ParamSql(
				"update " + norm(config.getNominalUnit()) + " set " + 
					norm(DataConfig.NOMINAL_VALUE_FIELD) + " = ?, " +
					norm(DataConfig.NOMINAL_PARENT_INDEX_FIELD) + " = ?" + 
				" where " + 
					norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = ? and " + 
					norm(DataConfig.ATTRIBUTE_FIELD) + " = ? and " + 
					norm(DataConfig.NOMINAL_INDEX_FIELD) + " = ?",
				new int[] {3, 4, 0, 1, 2});
	}

	
	/**
	 * Generating nominal delete SQL.
	 * @param refUnit reference unit.
	 * @param att specified attribute.
	 * @param nominal_index nominal index.
	 * @return delete SQL.
	 */
	public String genNominalDeleteSql(String refUnit, String att, int nominal_index) {
		return 
			"delete from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "' and " +
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " = " + nominal_index;
	}
	
	
	/**
	 * Generating nominal delete SQL.
	 * @param refUnit reference unit.
	 * @param att specified attribute.
	 * @return delete SQL.
	 */
	public String genNominalDeleteSql(String refUnit, String att) {
		return 
			"delete from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "' and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + att + "'";
	}

	
	/**
	 * Generating nominal delete SQL.
	 * @param refUnit reference unit.
	 * @return delete SQL.
	 */
	public String genNominalDeleteSql(String refUnit) {
		return 
			"delete from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + refUnit + "'";
	}

	
	/**
	 * Generating nominal delete SQL.
	 * @return delete SQL.
	 */
	public ParamSql genNominalDeleteSql() {
		return new ParamSql(
			"delete from " + norm(config.getNominalUnit()) + 
			" where " + 
				norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = ? and " +
				norm(DataConfig.ATTRIBUTE_FIELD) + " = ? and " +
				norm(DataConfig.NOMINAL_INDEX_FIELD) + " = ?",
			new int[] {0, 1, 2});
	}

	
	/**
	 * Generating attribute-map create SQL.
	 * @return create SQL.
	 */
	public String genAttributeMapCreateSql() {
		return "create table " + norm(config.getAttributeMapUnit()) + " ( " + 
				norm(DataConfig.INTERNAL_UNIT_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.EXTERNAL_UNIT_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				"primary key (" + 
					norm(DataConfig.INTERNAL_UNIT_FIELD) + ", " + 
					norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + ", " + 
					norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + ") )";
	}
	
	
	/**
	 * Generating attribute-map parametric select SQL.
	 * @return select SQL.
	 */
	public String genAttributeMapNoParamSelectSql() {
		return "select * from " + norm(config.getAttributeMapUnit());
	}
	
	
	/**
	 * Generating attribute-map select SQL.
	 * @return select SQL.
	 */
	public ParamSql genAttributeMapSelectSql() {
		return new ParamSql(
			"select * from " + norm(config.getAttributeMapUnit()) + 
			" where " + 
				norm(DataConfig.INTERNAL_UNIT_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? ",
			new int[] {0, 1, 2});
	}
	
	
	/**
	 * Generating attribute-map select SQL.
	 * @return select SQL by external values.
	 */
	public ParamSql genAttributeMapSelectSqlByExternal() {
		return new ParamSql(
			"select * from " + norm(config.getAttributeMapUnit()) + 
			" where " + 
				norm(DataConfig.EXTERNAL_UNIT_FIELD) + " = ? and " + 
				norm(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD) + " = ? and " + 
				norm(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? ",
			new int[] {0, 1, 2});
	}

	
	/**
	 * Generating attribute-map insert SQL.
	 * @return insert SQL.
	 */
	public ParamSql genAttributeMapInsertSql() {
		return new ParamSql(
			"insert into " + norm(config.getAttributeMapUnit()) + " (" +
				norm(DataConfig.INTERNAL_UNIT_FIELD) + ", " +
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + ", " +
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + ", " +
				norm(DataConfig.EXTERNAL_UNIT_FIELD) + ", " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD) + ", " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD) + ") values (?, ?, ?, ?, ?, ?)",
			new int[] {0, 1, 2, 3, 4, 5});
		
	}
	
	
	/**
	 * Generating attribute-map update SQL.
	 * @return update SQL.
	 */
	public ParamSql genAttributeMapUpdateSql() {
		return new ParamSql("update " + norm(config.getAttributeMapUnit()) + 
			" set " + 
				norm(DataConfig.EXTERNAL_UNIT_FIELD) + " = ?, " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_NAME_FIELD) + " = ?, " +
				norm(DataConfig.EXTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? " +
			" where " + 
				norm(DataConfig.INTERNAL_UNIT_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? ",
			new int[] {3, 4, 5, 0, 1, 2});
		
	}
	
	
	/**
	 * Generating attribute-map delete SQL.
	 * @return delete SQL.
	 */
	public ParamSql genAttributeMapDeleteSql() {
		return new ParamSql("delete from " + norm(config.getAttributeMapUnit()) + 
			" where " + 
				norm(DataConfig.INTERNAL_UNIT_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_NAME_FIELD) + " = ? and " + 
				norm(DataConfig.INTERNAL_ATTRIBUTE_VALUE_FIELD) + " = ? ",
			new int[] {0, 1, 2});
		
	}

	
	/**
	 * Generating attribute-map delete SQL.
	 * @param internalUnit internal unit.
	 * @return delete SQL.
	 */
	public String genAttributeMapDeleteSql(String internalUnit) {
		return 
			"delete from " + norm(config.getAttributeMapUnit()) + 
			" where " + norm(DataConfig.INTERNAL_UNIT_FIELD) + " = '" + internalUnit + "'";
		
	}

	
	/**
	 * Generating account create SQL.
	 * @return create SQL.
	 */
	public String genAccountCreateSql() {
		return "create table " + norm(config.getAccountUnit()) + " ( " + 
			norm(DataConfig.ACCOUNT_NAME_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
			norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
			norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
			"primary key (" + norm(DataConfig.ACCOUNT_NAME_FIELD) + ") )";
	}

	
	/**
	 * Generating account select SQL.
	 * @return select SQL.
	 */
	public String genAccountSelectSql() {
		return "select * from " + norm(config.getAccountUnit());
	}
	
	
	/**
	 * Generating account select SQL.
	 * @return select SQL.
	 */
	public String genAccountSelectSql(String accName) {
		return	"select * from " + norm(config.getAccountUnit()) + 
				" where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = '" + accName + "'";
	}

	
	/**
	 * Generating account insert SQL.
	 * @param accName account name.
	 * @param accPwd account password.
	 * @param privs privileges.
	 * @return insert SQL.
	 */
	public String genAccountInsertSql(String accName, String accPwd, int privs) {
		return 
			"insert into " + norm(config.getAccountUnit()) + 
			" (" + norm(DataConfig.ACCOUNT_NAME_FIELD) + ", " + norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + ", " + norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + ")" +
			" values ('" + accName + "', '" + accPwd + "', '" + privs + "')";
	}

	
	/**
	 * Generating account insert SQL.
	 * @return insert SQL.
	 */
	public ParamSql genAccountInsertSql() {
		return new ParamSql(
				"insert into " + norm(config.getAccountUnit()) + 
				" (" + norm(DataConfig.ACCOUNT_NAME_FIELD) + ", " + norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + ", " + norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + ")" +
				" values (?, ?, ?)",
				new int[] {0, 1, 2});
	}

	
	/**
	 * Generating account update SQL.
	 * @param accName account name.
	 * @param accPwd account password.
	 * @param privs specified privileges.
	 * @return update SQL.
	 */
	public String genAccountUpdateSql(String accName, String accPwd, int privs) {
		return 	
			"update " + norm(config.getAccountUnit()) + 
			" set " + norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + " = '" + accPwd + "', " + 
			norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + " = '" + privs + "'" + 
			" where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = '" + accName + "'";
	}

	
	/**
	 * Generating account update SQL.
	 * @return update SQL.
	 */
	public ParamSql genAccountUpdateSql() {
		return new ParamSql("update " + norm(config.getAccountUnit()) + 
				" set " + norm(DataConfig.ACCOUNT_PASSWORD_FIELD) + " = ?, " + 
				norm(DataConfig.ACCOUNT_PRIVILEGES_FIELD) + " = ?" + 
				" where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = ?",
				new int[] {1, 2, 0});
	}

	
	/**
	 * Generating account delete SQL.
	 * @return delete SQL.
	 */
	public String genAccountDeleteSql(String accName) {
		return 
			"delete from " + norm(config.getAccountUnit()) + " where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = '" + accName + "'";
	}

	
	/**
	 * Generating account delete SQL.
	 * @return delete SQL.
	 */
	public ParamSql genAccountDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getAccountUnit()) + " where " + norm(DataConfig.ACCOUNT_NAME_FIELD) + " = ?",
				new int[] {0});
	}

	
	/**
	 * Generating profile create SQL.
	 * @param profileUnit specified profile unit.
	 * @param attList specified attribute list.
	 * @return create SQL.
	 */
	public String genProfileCreateSql(String profileUnit, AttributeList attList) {
		Keys keys = attList.getKeys();
		
		StringBuffer sql = new StringBuffer();
		sql.append("create table " + norm(profileUnit) + " (");
		
		for (int i = 0; i < attList.size(); i++) {
			Attribute att = attList.get(i);
			if (i > 0)
				sql.append(", ");
			
			if (keys.contains(att))
				sql.append(norm(att.getName()) + " " + toSqlTypeName(att.getType()) + " not null");
			else
				sql.append(norm(att.getName()) + " " + toSqlTypeName(att.getType()));
			
			if (att.isAutoInc())
				sql.append(" AUTO_INCREMENT");
		}
		
		if (keys.size() == 0)
			sql.append(" )");
		else {
			sql.append(", primary key (");
			for (int i = 0; i < keys.size(); i++) {
				Attribute key = keys.get(i);
				
				if (i > 0)
					sql.append(", ");
				sql.append( norm(key.getName()) );
			}
			sql.append(") )");
		}
		
		return sql.toString();
	}
	
	
	/**
	 * Generating profile select SQL.
	 * @param profileUnit specified profile unit.
	 * @return select SQL.
	 */
	public String genProfileSelectSql(String profileUnit) {
		return "select * from " + norm(profileUnit);
	}
	

	/**
	 * Generating profile select SQL.
	 * @param profileUnit specified profile unit.
	 * @param conditionAttList conditional attribute list.
	 * @return select SQL.
	 */
	public ParamSql genProfileSelectSql(String profileUnit, AttributeList conditionAttList) {
		Keys keys = conditionAttList.getKeys();
		
		if (keys.size() > 0) 
			return new ParamSql("select * from " + norm(profileUnit) + " where " + 
				toAndConditionText(keys.getList()),
				getIndexes(keys.getList()));
		else
			return new ParamSql("select * from " + norm(profileUnit) + " where " + 
				toAndConditionText(conditionAttList.getList()),
				getIndexes(conditionAttList.getList()) );
	}

	
	/**
	 * Generating profile select SQL.
	 * @param profileUnit specified profile unit.
	 * @param condition specified condition.
	 * @return {@link ParamSql}
	 */
	public ParamSql genProfileSelectSql2(String profileUnit, Profile condition) {
		Keys keys = condition.getNotMissingAtts();
		
		if (keys.size() > 0) 
			return new ParamSql("select * from " + norm(profileUnit) + " where " + 
				toAndConditionText(keys.getList()),
				getIndexes(keys.getList()));
		else
			return new ParamSql(genProfileSelectSql(profileUnit), new int[] { });
	}

	
	/**
	 * Generating profile id select SQL.
	 * @param profileUnit specified profile unit.
	 * @param profileIdName name of ID field in the unit.
	 * @return select SQL.
	 */
	public String genProfileIdsSelectSql(String profileUnit, String profileIdName) {
		return "select " + norm(profileIdName) + " from " + norm(profileUnit);
	}
	
	
	/**
	 * Generating maximum profile id select SQL.
	 * @param profileUnit specified profile unit.
	 * @param profileIdName name of ID field in the unit.
	 * @return select SQL
	 */
	public String genMaxIdProfileSelectSql(String profileUnit, String profileIdName) {
		return "select max(" + norm(profileIdName) + ") from " + norm(profileUnit);
	}
	
	
	/**
	 * Generating profile insert SQL.
	 * @param profileUnit specified profile unit.
	 * @param attList specified attribute list.
	 * @return insert SQL.
	 */
	public ParamSql genProfileInsertSql(
			String profileUnit, 
			AttributeList attList) {
		
		int n = attList.size();
		
		StringBuffer sql = new StringBuffer();
		sql.append("insert into " + norm(profileUnit) + " (");
		List<Integer> indexes = Util.newList();
		
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sql.append(", ");
			
			Attribute att = attList.get(i);
			sql.append(norm(att.getName()));
			if (!att.isAutoInc())
				indexes.add(att.getIndex());
		}
		
		sql.append(") values (" );
		for (int i = 0; i < n; i++) {
			if (i > 0)
				sql.append(", ");
			
			Attribute att = attList.get(i);
			if (!att.isAutoInc())
				sql.append("?");
			else
				sql.append("NULL");
		}
		sql.append(")");
		
		return new ParamSql(sql.toString(), indexes);
	}


	/**
	 * Generating profile update SQL.
	 * @param profileUnit specified profile unit.
	 * @param conditionAttList conditional attribute list.
	 * @return update SQL.
	 */
	public ParamSql genProfileUpdateSql(
			String profileUnit, 
			AttributeList conditionAttList) {

		Keys keys = conditionAttList.getKeys();
		
		StringBuffer sql = new StringBuffer("update " + norm(profileUnit) + " set ");
		List<Integer> indexes = Util.newList();
		
		int k = 0;
		int n = conditionAttList.size();
		for (int i = 0; i < n; i++) {
			Attribute att = conditionAttList.get(i);
			
			if (!keys.contains(att)) {
				if (k > 0)
					sql.append(", ");
				
				sql.append(norm(att.getName()) + " = ?");
				indexes.add(att.getIndex());
				k++;
			}
		}
		
		sql.append(" where ");
		for (int i = 0; i < keys.size(); i++) {
			Attribute key = keys.get(i);
			
			if (i > 0)
				sql.append(" and ");
			sql.append(norm(key.getName()) + " = ?");
			
			indexes.add(key.getIndex());
		}
		
		
		return new ParamSql(sql.toString(), indexes);
	}

	
	/**
	 * Generating profile delete SQL.
	 * @param profileUnit specified profile unit.
	 * @return delete SQL.
	 */
	public String genProfileDeleteSql(String profileUnit) {
		return "delete from " + norm(profileUnit);
	}

	
	/**
	 * Generating profile delete SQL.
	 * @param profileUnit specified profile unit.
	 * @param conditionAttList conditional attribute list.
	 * @return delete SQL.
	 */
	public ParamSql genProfileDeleteSql(String profileUnit, AttributeList conditionAttList) {
		Keys keys = conditionAttList.getKeys();
		
		if (keys.size() > 0) 
			return new ParamSql("delete from " + norm(profileUnit) + " where " + 
				toAndConditionText(keys.getList()),
				getIndexes(keys.getList()));
		else
			return new ParamSql("delete from " + norm(profileUnit) + " where " + 
				toAndConditionText(conditionAttList.getList()),
				getIndexes(conditionAttList.getList()) );
	}

	
	/**
	 * Generating profile delete SQL.
	 * @param profileUnit specified profile unit.
	 * @param condition specified condition.
	 * @return delete SQL.
	 */
	public ParamSql genProfileDeleteSql2(String profileUnit, Profile condition) {
		Keys keys = condition.getNotMissingAtts();
		
		if (keys.size() > 0) 
			return new ParamSql("delete from " + norm(profileUnit) + " where " + 
				toAndConditionText(keys.getList()),
				getIndexes(keys.getList()));
		else
			return new ParamSql(genProfileDeleteSql(profileUnit), new int[] { });
	}

	
	/**
	 * Generating context template create SQL.
	 * Templates are stored in table whose key is template id and whose fields are name, type and parent.
	 * @return create SQL.
	 */
	public String genContextTemplateCreateSql() {
		return "create table " + norm(getConfig().getContextTemplateUnit()) + " ( " + 
				norm(DataConfig.CTX_TEMPLATEID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.CTX_NAME_FIELD) + " " + toSqlTypeName(Type.string) + " not null, " +
				norm(DataConfig.CTX_TYPE_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.CTX_PARENT_FIELD) + " " + toSqlTypeName(Type.integer) + ", " +
				"primary key (" + norm(DataConfig.CTX_TEMPLATEID_FIELD) + ") )";
	}

	
	/**
	 * Generating context template select SQL.
	 * @return select SQL.
	 */
	public String genContextTemplateSelectSql() {
		return "select * from " + norm(getConfig().getContextTemplateUnit());
	}
	

	/**
	 * Generating context template select SQL.
	 * @param ctxTemplateId specified context template ID.
	 * @return select SQL.
	 */
	public String genContextTemplateSelectSql(int ctxTemplateId) {
		return "select * from " + norm(getConfig().getContextTemplateUnit()) + 
				" where " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = " + ctxTemplateId;
	}

	
	/**
	 * Generating context template insert SQL.
	 * @return insert SQL.
	 */
	public ParamSql genContextTemplateInsertSql() {
		return new ParamSql( 
			"insert into " + norm(getConfig().getContextTemplateUnit()) + 
			" (" +
				norm(DataConfig.CTX_TEMPLATEID_FIELD) + ", " + 
				norm(DataConfig.CTX_NAME_FIELD) + ", " + 
				norm(DataConfig.CTX_TYPE_FIELD) + ", " + 
				norm(DataConfig.CTX_PARENT_FIELD) + 
			") values (?, ?, ?, ?) ",
			new int[] { 0, 1, 2, 3 });
	}

	
	/**
	 * Generating context template update SQL.
	 * @return update SQL.
	 */
	public ParamSql genContextTemplateUpdateSql() {
		return new ParamSql(
				"update " + norm(getConfig().getContextTemplateUnit()) + 
				" set " + norm(DataConfig.CTX_NAME_FIELD) + " = ?, " +
				norm(DataConfig.CTX_TYPE_FIELD) + " = ?, "  +
				norm(DataConfig.CTX_PARENT_FIELD) + " = ? "  +
				" where " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ?",
				new int [] { 1, 2, 3, 0 }
			);
	}
	
	
	/**
	 * Generating context template delete SQL.
	 * @return parametric SQL.
	 */
	public ParamSql genContextTemplateDeleteSql() {
		return new ParamSql(
				"delete from " + norm(getConfig().getContextTemplateUnit()) + 
				" where " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ?",
				new int [] { 0 }
			);
	}

	
//	/**
//	 * Generating context creation table SQL.
//	 * @return context creation table SQL.
//	 */
//	public String genContextCreateSql() {
//		return "create table " + norm(config.getContextUnit()) + " ( " + 
//			norm(DataConfig.USERID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
//			norm(DataConfig.ITEMID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
//			norm(DataConfig.CTX_TEMPLATEID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
//			norm(DataConfig.CTX_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + ", " +
//			"primary key (" + 
//				norm(DataConfig.USERID_FIELD) + ", " + 
//				norm(DataConfig.ITEMID_FIELD) + ", " + 
//				norm(DataConfig.CTX_TEMPLATEID_FIELD) + ") )";
//	}

	
	/**
	 * Generating context creation table SQL.
	 * @return context creation table SQL.
	 */
	public String genContextCreateSql() {
		return "create table " + norm(config.getContextUnit()) + " ( " + 
			norm(DataConfig.USERID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.ITEMID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.CTX_TEMPLATEID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.CTX_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + ", " +
			norm(DataConfig.RATING_DATE_FIELD) + " " + toSqlTypeName(Type.time);
	}

	
	/**
	 * Generating context select SQL.
	 * @return select SQL.
	 */
	public String genContextSelectSql() {
		return "select * from " + norm(config.getContextUnit());
	}
	
	
	/**
	 * Generating context select SQL.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @param ctxTemplateId context template ID.
	 * @return select SQL.
	 */
	public String genContextSelectSql(int userId, int itemId, int ctxTemplateId) {
		return "select * from " + norm(config.getContextUnit()) + 
				" where " + norm(DataConfig.USERID_FIELD) + " = " + userId +
				" and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId +	
				" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = " + ctxTemplateId;	
	}

	
	/**
	 * Generating context select SQL.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @return select SQL.
	 */
	public String genContextSelectSql(int userId, int itemId) {
		return "select * from " + norm(config.getContextUnit()) + 
				" where " + norm(DataConfig.USERID_FIELD) + " = " + userId +
				" and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;	
	}

	
//	/**
//	 * Generating context insert SQL.
//	 * @return context insert SQL.
//	 */
//	public ParamSql genContextInsertSql() {
//		return new ParamSql( 
//			"insert into " + norm(config.getContextUnit()) + 
//			" (" +
//				norm(DataConfig.USERID_FIELD) + ", " + 
//				norm(DataConfig.ITEMID_FIELD) + ", " + 
//				norm(DataConfig.CTX_TEMPLATEID_FIELD) + ", " + 
//				norm(DataConfig.CTX_VALUE_FIELD) + 
//			") values (?, ?, ?, ?) ",
//			new int[] { 0, 1, 2, 3 });
//	}

	
	/**
	 * Generating context insert SQL.
	 * @return context insert SQL.
	 */
	public ParamSql genContextInsertSql() {
		return new ParamSql( 
			"insert into " + norm(config.getContextUnit()) + 
			" (" +
				norm(DataConfig.USERID_FIELD) + ", " + 
				norm(DataConfig.ITEMID_FIELD) + ", " + 
				norm(DataConfig.CTX_TEMPLATEID_FIELD) + ", " + 
				norm(DataConfig.CTX_VALUE_FIELD) + ", " +
				norm(DataConfig.RATING_DATE_FIELD) + 
			") values (?, ?, ?, ?, ?) ",
			new int[] { 0, 1, 2, 3, 4 });
	}

	
//	/**
//	 * Generating context update SQL.
//	 * @return context update SQL.
//	 */
//	public ParamSql genContextUpdateSql() {
//		return new ParamSql(
//				"update " + norm(config.getContextUnit()) + 
//				" set " + norm(DataConfig.CTX_VALUE_FIELD) + " = ? " +
//				" where " + norm(DataConfig.USERID_FIELD) + " = ? " +
//				" and " + norm(DataConfig.ITEMID_FIELD) + " = ? " +
//				" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ? " ,
//				new int [] { 3, 0, 1, 2 }
//			);
//	}

	
	/**
	 * Generating context update SQL.
	 * @return context update SQL.
	 */
	public ParamSql genContextUpdateSql() {
		return new ParamSql(
				"update " + norm(config.getContextUnit()) + 
				" set " + norm(DataConfig.CTX_VALUE_FIELD) + " = ?, " + 
					norm(DataConfig.RATING_DATE_FIELD) + " = ? " +
				" where " + norm(DataConfig.USERID_FIELD) + " = ? " +
					" and " + norm(DataConfig.ITEMID_FIELD) + " = ? " +
					" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ? " ,
				new int [] { 3, 4, 0, 1, 2 }
			);
	}

	
	/**
	 * Generating context delete SQL.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @param ctxTemplateId context template identifier.
	 * @return delete SQL.
	 */
	public String genContextDeleteSql(int userId, int itemId, int ctxTemplateId) {
		return "delete from " + norm(config.getContextUnit()) + 
		" where " + norm(DataConfig.USERID_FIELD) + " = " + userId +
		" and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId +
		" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = " + ctxTemplateId;
		
	}
	
	
	/**
	 * Generating context delete SQL.
	 * @param userId user identifier.
	 * @param itemId item identifier.
	 * @return delete SQL.
	 */
	public String genContextDeleteSql(int userId, int itemId) {
		return "delete from " + norm(config.getContextUnit()) + 
		" where " + norm(DataConfig.USERID_FIELD) + " = " + userId +
		" and " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}

	
	/**
	 * Generating context delete SQL.
	 * @return delete SQL.
	 */
	public ParamSql genContextDeleteSql() {
		return new ParamSql(
				"delete from " + norm(config.getContextUnit()) + 
				" where " + norm(DataConfig.USERID_FIELD) + " = ? " +
				" and " + norm(DataConfig.ITEMID_FIELD) + " = ? " +
				" and " + norm(DataConfig.CTX_TEMPLATEID_FIELD) + " = ? " ,
				new int [] { 0, 1, 2 }
			);
	}

	
	/**
	 * Generating user context delete SQL.
	 * @param userId user identifier.
	 * @return delete SQL.
	 */
	public String genUserContextDeleteSql(int userId) {
		return "delete from " + norm(config.getContextUnit()) + 
			   " where " + norm(DataConfig.USERID_FIELD) + " = " + userId;
	}
	
	
	/**
	 * Generating item context delete SQL.
	 * @param itemId item identifier.
	 * @return delete SQL.
	 */
	public String genItemContextDeleteSql(int itemId) {
		return "delete from " + norm(config.getContextUnit()) + 
			   " where " + norm(DataConfig.ITEMID_FIELD) + " = " + itemId;
	}

	
	/**
	 * Generating attribute bound SQL.
	 * @param unit specified unit.
	 * @return select SQL.
	 */
	public Map<Integer, String> genAttributeBoundSql(String unit) {
		Map<Integer, String> map = Util.newMap();
		
		if (config.getContextUnit() != null && unit.equals(config.getContextUnit())) {
			map.put(2, genContextTemplateSelectSql());
		}
		else {
			AttributeList attList = getAttributes(unit);
			for (int i = 0; i < attList.size(); i++) {
				Attribute att = attList.get(i);
				if (att.getType() != Type.nominal)
					continue;
				
				String attName = att.getName();
				String sql = "select " + norm(DataConfig.NOMINAL_INDEX_FIELD) + 
						" from " + norm(config.getNominalUnit()) + 
						" where " + 
							norm(DataConfig.NOMINAL_REF_UNIT_FIELD) + " = '" + unit + "' and " +
							norm(DataConfig.ATTRIBUTE_FIELD) + " = '" + attName + "' ";
				
				map.put(i, sql);
			}
			
		}
		
		return map;
	}

	
	/**
	 * Generating select SQL.
	 * @param unit specified unit.
	 * @return select SQL.
	 */
	public String genSelectSql(String unit) {
		Map<String, String> map = Util.newMap();
		
		String configUnit = config.getConfigUnit();
		if (configUnit != null)
			map.put(configUnit, genConfigSelectSql());
		
		String ratingUnit = config.getRatingUnit();
		if (ratingUnit != null)
			map.put(ratingUnit, genRatingSelectSql());
		
		String userUnit = config.getUserUnit();
		if (userUnit != null)
			map.put(userUnit, genUserProfileSelectSql());
		
		String itemUnit = config.getItemUnit();
		if (itemUnit != null)
			map.put(itemUnit, genItemProfileSelectSql());
		
		String nominalUnit = config.getNominalUnit();
		if (nominalUnit != null)
			map.put(nominalUnit, genNominalSelectSql());
		
		String accountUnit = config.getAccountUnit();
		if (accountUnit != null)
			map.put(accountUnit, genAccountSelectSql());

		String attributeMapUnit = config.getAttributeMapUnit();
		if (attributeMapUnit != null)
			map.put(attributeMapUnit, genAttributeMapNoParamSelectSql());

		String contextTemplateUnit = config.getContextTemplateUnit();
		if (contextTemplateUnit != null)
			map.put(contextTemplateUnit, genContextTemplateSelectSql());

		String contextUnit = config.getContextUnit();
		if (contextUnit != null)
			map.put(contextUnit, genContextSelectSql());

		if (map.containsKey(unit))
			return map.get(unit);
		else
			return genProfileSelectSql(unit);
		
	}
	
	
	/**
	 * Generating insert SQL.
	 * @param unit specified unit.
	 * @return insert SQL.
	 */
	public ParamSql genInsertSql(String unit) {
		Map<String, ParamSql> map = Util.newMap();
		
		String configUnit = config.getConfigUnit();
		if (configUnit != null)
			map.put(configUnit, genConfigInsertSql());
		
		String ratingUnit = config.getRatingUnit();
		if (ratingUnit != null)
			map.put(ratingUnit, genRatingInsertSql());
		
		String userUnit = config.getUserUnit();
		if (userUnit != null) {
			map.put(userUnit, genProfileInsertSql(userUnit, getAttributes(config.getUserUnit())));
		}
		
		String itemUnit = config.getItemUnit();
		if (itemUnit != null) {
			map.put(itemUnit, genProfileInsertSql(itemUnit, getAttributes(config.getItemUnit())));
		}
		
		String nominalUnit = config.getNominalUnit();
		if (nominalUnit != null)
			map.put(nominalUnit, genNominalInsertSql());
		
		String accountUnit = config.getAccountUnit();
		if (accountUnit != null)
			map.put(accountUnit, genAccountInsertSql());

		String attributeMapUnit = config.getAttributeMapUnit();
		if (attributeMapUnit != null)
			map.put(attributeMapUnit, genAttributeMapInsertSql());

		String contextTemplateUnit = config.getContextTemplateUnit();
		if (contextTemplateUnit != null)
			map.put(contextTemplateUnit, genContextTemplateInsertSql());

		String contextUnit = config.getContextUnit();
		if (contextUnit != null)
			map.put(contextUnit, genContextInsertSql());

		if (map.containsKey(unit))
			return map.get(unit);
		else {
			AttributeList attList = getAttributes(unit);
			return genProfileInsertSql(unit, attList);
		}
		
	}

	
	/**
	 * Generating update SQL.
	 * @param unit specified unit.
	 * @return update SQL.
	 */
	public ParamSql genUpdateSql(String unit) {
		Map<String, ParamSql> map = Util.newMap();
		
		String configUnit = config.getConfigUnit();
		if (configUnit != null)
			map.put(configUnit, genConfigUpdateSql());
		
		String ratingUnit = config.getRatingUnit();
		if (ratingUnit != null)
			map.put(ratingUnit, genRatingUpdateSql());
		
		String userUnit = config.getUserUnit();
		if (userUnit != null) {
			map.put(userUnit, genProfileUpdateSql(userUnit, getAttributes(config.getUserUnit())));
		}

		String itemUnit = config.getItemUnit();
		if (itemUnit != null) {
			map.put(itemUnit, genProfileUpdateSql(itemUnit, getAttributes(config.getItemUnit())));
		}
		
		String nominalUnit = config.getNominalUnit();
		if (nominalUnit != null)
			map.put(nominalUnit, genNominalUpdateSql());
		
		String accountUnit = config.getAccountUnit();
		if (accountUnit != null)
			map.put(accountUnit, genAccountUpdateSql());

		String attributeMapUnit = config.getAttributeMapUnit();
		if (attributeMapUnit != null)
			map.put(attributeMapUnit, genAttributeMapUpdateSql());

		String contextTemplateUnit = config.getContextTemplateUnit();
		if (contextTemplateUnit != null)
			map.put(contextTemplateUnit, genContextTemplateUpdateSql());

		String contextUnit = config.getContextUnit();
		if (contextUnit != null)
			map.put(contextUnit, genContextUpdateSql());

		if (map.containsKey(unit))
			return map.get(unit);
		else {
			AttributeList attList = getAttributes(unit);
			return genProfileUpdateSql(unit, attList);
		}
		
	}


	/**
	 * Generating delete SQL.
	 * @param unit specified unit.
	 * @return delete SQL.
	 */
	public ParamSql genDeleteSql(String unit) {
		Map<String, ParamSql> map = Util.newMap();
		
		String configUnit = config.getConfigUnit();
		if (configUnit != null)
			map.put(configUnit, genConfigDeleteSql());
		
		String ratingUnit = config.getRatingUnit();
		if (ratingUnit != null)
			map.put(ratingUnit, genRatingDeleteSql());
		
		String userUnit = config.getUserUnit();
		if (userUnit != null) {
			map.put(userUnit, genUserProfileDeleteSql());
		}

		String itemUnit = config.getItemUnit();
		if (itemUnit != null) {
			map.put(itemUnit, genItemProfileDeleteSql());
		}
		
		String nominalUnit = config.getNominalUnit();
		if (nominalUnit != null)
			map.put(nominalUnit, genNominalDeleteSql());
		
		String accountUnit = config.getAccountUnit();
		if (accountUnit != null)
			map.put(accountUnit, genAccountDeleteSql());

		String attributeMapUnit = config.getAttributeMapUnit();
		if (attributeMapUnit != null)
			map.put(attributeMapUnit, genAttributeMapDeleteSql());

		String contextTemplateUnit = config.getContextTemplateUnit();
		if (contextTemplateUnit != null)
			map.put(contextTemplateUnit, genContextTemplateDeleteSql());

		String contextUnit = config.getContextUnit();
		if (contextUnit != null)
			map.put(contextUnit, genContextDeleteSql());

		if (map.containsKey(unit))
			return map.get(unit);
		else {
			AttributeList attList = getAttributes(unit);
			return genProfileDeleteSql(unit, attList);
		}
		
	}

	
	@Override
	public boolean createUnit(String unitName, AttributeList attList) {
		String createSql = genProfileCreateSql(unitName, attList);
		
		return executeUpdate(createSql);
	}


	@Override
	public boolean deleteUnitData(String unitName) {
		return executeUpdate("delete from " + norm(unitName));
	}


	@Override
	public boolean dropUnit(String unitName) {
		return executeUpdate("drop table " + norm(unitName) );
	}

	
	@Override
	public UnitList getUnitList() {
		UnitList tblList = new UnitList();
		try {
			UnitList defaultUnitList = DataConfig.getDefaultUnitList();

			DatabaseMetaData meta = conn.getMetaData();
			ResultSet rs = meta.getTables(null, null, null, null);
			while (rs.next()) {
				String unitName = rs.getString("TABLE_NAME");
				Unit unit = new Unit(unitName);
				if (!defaultUnitList.contains(unitName))
					unit.setExtra(true);
					
				tblList.add(unit);
			}
			
			rs.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Get database metadata error: " + e.getMessage());
		}
		
		return tblList;
	}
	

	@Override
	public NominalList getNominalList(String filterUnit, String attName) {
		NominalList nominalList = new NominalList();
		UnitList unitList = getUnitList();
		if (!unitList.contains(config.getNominalUnit()))
			return nominalList;
		
		ResultSet rs = null;
		try {
			
			String sql = genNominalSelectSql(filterUnit, attName);
			rs = executeQuery(sql);
			
			while(rs.next()) {
				String nominalValue = rs.getString(DataConfig.NOMINAL_VALUE_FIELD);
				int nominalindex = rs.getInt(DataConfig.NOMINAL_INDEX_FIELD);
				int parentindex = rs.getInt(DataConfig.NOMINAL_PARENT_INDEX_FIELD);
				if (rs.wasNull())
					parentindex = Nominal.NULL_INDEX;
				
				nominalList.add(new Nominal(nominalValue, nominalindex, parentindex));
			}
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			closeResultSet(rs);
		}
		
		return nominalList;
	}
	

	/**
	 * Getting primary keys.
	 * @param unitName
	 * @return list of primary keys.
	 */
	private List<String> getPrimaryKeys(String unitName) {
		List<String> keys = Util.newList();
		try {
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet rs = metadata.getPrimaryKeys(null, null, unitName);
			while (rs.next()) {
				String key = rs.getString("COLUMN_NAME"); 
				keys.add(key);
			}
			rs.close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return keys;
	}

	
	/**
	 * Getting attribute list.
	 * @param rs result set.
	 * @return attribute list.
	 */
	private AttributeList getAttributes(ResultSet rs) {
		AttributeList attList = new AttributeList();
		
		try {
			ResultSetMetaData metadata = rs.getMetaData();
			int n = metadata.getColumnCount();
			for (int i = 1; i <= n; i++) {
				List<String> keys = getPrimaryKeys(metadata.getTableName(i));
				
				String name = metadata.getColumnLabel(i); 
				Type type = fromSqlType(metadata.getColumnType(i));
				Attribute att = new Attribute(name, type);
				
				if (type == Type.integer) {
					String unitName = metadata.getTableName(i);
					if (unitName != null && !unitName.isEmpty()) {
						NominalList nominalList = getNominalList(unitName, name);
						if (nominalList.size() > 0)
							att = new Attribute(name, nominalList);
					}
				}
				
				if (keys.contains(name))
					att.setKey(true);
				
				attList.add(att);
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return attList;
	}
	
	
	@Override
	public AttributeList getAttributes(String profileUnit) {
		AttributeList attList = new AttributeList();
		
		try {
			List<String> keys = getPrimaryKeys(profileUnit);
			
			DatabaseMetaData metadata = conn.getMetaData();
			ResultSet rs = metadata.getColumns(null, null, profileUnit, null);
			while (rs.next()) {
				String name = rs.getString("COLUMN_NAME"); 
				Type type = fromSqlType(rs.getInt("DATA_TYPE"));
				 
				Attribute att = new Attribute(name, type);
				if (type == Type.integer) {
					NominalList nominalList = getNominalList(profileUnit, name);
					if (nominalList.size() > 0)
						att = new Attribute(name, nominalList);
				}
				
				if (keys.contains(name))
					att.setKey(true);
				
				attList.add(att);
			}
			rs.close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return attList;
	}

	
	@Override
	public AttributeList getAttributes(ParamSql selectSql, Profile condition) {
		AttributeList attributes = new AttributeList();
		ResultSet rs = executeQuery(selectSql, condition);
		try {
			attributes = getAttributes(rs);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			closeResultSet(rs);
		}
		
		
		return attributes;
	}


	@Override
	public boolean containsProfile(String profileUnit, Profile profile) {
		ParamSql selectSql = genProfileSelectSql2(profileUnit, profile);
		
		return exists(selectSql, profile);
	}


	@Override
	public Profile getProfile(String profileUnit, Profile condition) {
		ResultSet rs = null;
		Profile returnProfile = null;
		try {
			if (condition != null)
				rs = executeQuery(genProfileSelectSql2(profileUnit, condition), 
						condition);
			else
				rs = executeQuery(genProfileSelectSql(profileUnit));
			
			if (rs != null && rs.next())
				returnProfile = getProfile(rs);
			
			closeResultSet(rs);
			rs = null;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		finally {
			closeResultSet(rs);
		}
		
		return returnProfile;
	}

	
	/**
	 * Creating profile from result set.
	 * @param rs result set.
	 * @return profile created from result set.
	 */
	private Profile getProfile(ResultSet rs) {
		AttributeList attRef = getAttributes(rs);
		
		Profile profile = new Profile(attRef);
		
		for (int i = 0; i < attRef.size(); i++) {
			Attribute att = attRef.get(i);
			
			String name = att.getName();
			Type type = att.getType();
			Object value = null;
			
			try {
				if (type == Type.bit)
					value = Byte.valueOf(rs.getByte(name));
				else if (type == Type.integer)
					value = Integer.valueOf(rs.getInt(name));
				else if (type == Type.real)
					value = Double.valueOf(rs.getDouble(name));
				else if (type == Type.string)
					value = rs.getString(name);
				else if (type == Type.date)
					value = rs.getDate(name);
				else 
					value = rs.getObject(name);
				
				if (rs.wasNull())
					profile.setMissing(i);
				else
					profile.setValue(i, value);
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
		}
		
		return profile;
	}

	
	@Override
	public Fetcher<Profile> getProfiles(String profileUnit, Profile condition) {
		ResultSet rs = null;
		if (condition != null)
			rs = executeQuery(genProfileSelectSql2(profileUnit, condition), condition);
		else
			rs = executeQuery(genProfileSelectSql(profileUnit));
		if (rs == null) return new MemFetcher<Profile>();
		
		return new DbFetcher<Profile>(rs) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Profile create(ResultSet rs) {
				return getProfile(rs);
			}
			
		};
	}
	
	
	
	@Override
	public Collection<Profile> getProfiles2(String profileUnit, Profile condition) {
		List<Profile> profiles = Util.newList();
		ResultSet rs = null;
		if (condition != null)
			rs = executeQuery(genProfileSelectSql2(profileUnit, condition), condition);
		else
			rs = executeQuery(genProfileSelectSql(profileUnit));
		if (rs == null) return profiles;

		try {
			while (rs.next()) {
				Profile profile = getProfile(rs);
				if (profile != null) profiles.add(profile);
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return profiles;
	}


	@Override
	public Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition) {
		ResultSet rs = executeQuery(selectSql, condition);
		if (rs == null) return new MemFetcher<Profile>();
		
		return new DbFetcher<Profile>(rs) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Profile create(ResultSet rs) {
				return getProfile(rs);
			}
			
		};
	}


	@Override
	public Collection<Profile> getProfiles2(ParamSql selectSql, Profile condition) {
		List<Profile> profiles = Util.newList();
		ResultSet rs = executeQuery(selectSql, condition);
		if (rs == null) return profiles;
		
		try {
			while (rs.next()) {
				Profile profile = getProfile(rs);
				if (profile != null) profiles.add(profile);
			}
		}
		catch (Exception e) {LogUtil.trace(e);}

		return profiles;
	}


	@Override
	public Fetcher<Integer> getProfileIds(String profileUnit) {
		AttributeList attributes = getAttributes(profileUnit);
		final Attribute idAtt = attributes.getId();
		if (idAtt == null || idAtt.getType() != Type.integer)
			return new MemFetcher<Integer>();
		
		ResultSet rs = executeQuery(genProfileIdsSelectSql(profileUnit, idAtt.getName()));
		if (rs == null) return new MemFetcher<Integer>();
		
		return new DbFetcher<Integer>(rs) {

			/**
			 * Default serial version UID.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public Integer create(ResultSet rs) {
				try {
					int id = rs.getInt(idAtt.getName());
					if (rs.wasNull() || id < 0)
						return null;
					else
						return id;
				} 
				catch (Throwable e) {
					LogUtil.trace(e);
				}
				
				return null;
			}
			
		};
	}


	@Override
	public Collection<Integer> getProfileIds2(String profileUnit) {
		List<Integer> ids = Util.newList();
		AttributeList attributes = getAttributes(profileUnit);
		final Attribute idAtt = attributes.getId();
		if (idAtt == null || idAtt.getType() != Type.integer) return ids;
		
		ResultSet rs = executeQuery(genProfileIdsSelectSql(profileUnit, idAtt.getName()));
		if (rs == null) return ids;
		
		try {
			while (rs.next()) {
				int id = rs.getInt(idAtt.getName());
				if (rs.wasNull() || id < 0) continue;

				ids.add(id);
			}
		}
		catch (Exception e) {LogUtil.trace(e);}

		return ids;
	}


	@Override
	public int getProfileMaxId(String profileUnit) {
		Attribute id = getAttributes(profileUnit).getId();
		if (id == null)
			return -1;
		
		String maxSql = genMaxIdProfileSelectSql(profileUnit, id.getName());
		return (int) (max(new ParamSql(maxSql), null) + 0.5);
	}


	@Override
	public boolean insertProfile(String profileUnit, Profile profile) {
		ParamSql insertSql = genProfileInsertSql(profileUnit, 
				profile.getAttRef());
		
		return executeUpdate(insertSql, profile);
	}
	
	
	@Override
	public boolean updateProfile(String profileUnit, Profile profile) {
		ParamSql updateSql = genProfileUpdateSql(profileUnit, 
				profile.getAttRef());
		
		return executeUpdate(updateSql, profile);
	}

	
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) {
		ParamSql deleteSql = genProfileDeleteSql2(profileUnit, condition);
		
		return executeUpdate(deleteSql, condition);
	}

	
	/**
	 * Executing select SQL statement.
	 * @param selectSql select SQL statement.
	 * @return result set.
	 */
	private ResultSet executeQuery(String selectSql) {
		try {
			Statement stm = conn.createStatement(
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY);
			ResultSet rs = stm.executeQuery(selectSql);
			
			return rs;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Executing select SQL statement with specified condition.
	 * @param selectSql select SQL statement.
	 * @param condition profile as specified condition.
	 * @return result set.
	 */
	private ResultSet executeQuery(ParamSql selectSql, Profile condition) {
		
		if (selectSql.getIndexes().size() == 0 || condition == null)
			return executeQuery(selectSql.getSql());
		
		try {
			AttributeList attList = condition.getAttRef();
			
			PreparedStatement stm = conn.prepareStatement(selectSql.getSql(), 
					ResultSet.TYPE_SCROLL_INSENSITIVE, 
					ResultSet.CONCUR_READ_ONLY );
			
			List<Integer> indexes = selectSql.getIndexes();
			for (int i = 0; i < indexes.size(); i++) {
				int index = indexes.get(i);
				Attribute att = attList.get(index);
				Type type = att.getType();
				
				if (condition.isMissing(index)) {
					stm.setNull(i + 1, toSqlType(type));
					continue;
				}
				
				switch (type) {
				case bit:
					stm.setByte(i + 1, (byte)condition.getValueAsInt(index));
					break;
				case nominal:
					stm.setInt(i + 1, condition.getValueAsInt(index));
					break;
				case integer:
					stm.setInt(i + 1, condition.getValueAsInt(index));
					break;
				case real:
					stm.setDouble(i + 1, condition.getValueAsReal(index));
					break;
				case string:
					stm.setString(i + 1, condition.getValueAsString(index));
					break;
				case date:
					Date date =  (Date)condition.getValue(index);
					stm.setDate(i + 1, new java.sql.Date(date.getTime()));
					break;
				case object:
					stm.setObject(i + 1, condition.getValue(index));
					break;
				default:
					stm.setObject(i + 1, condition.getValue(index));
				}
			}
			
			return stm.executeQuery();
			
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}
	
	
	/**
	 * Executing update SQL statement.
	 * @param updateSql update SQL statement.
	 * @return whether update successfully.
	 */
	private boolean executeUpdate(String updateSql) {
		try {
			Statement stm = conn.createStatement();
			stm.executeUpdate(updateSql);
			stm.close();
			
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}
	
	
	/**
	 * Executing update SQL statement with specified condition.
	 * @param updateSql update SQL statement.
	 * @param profile profile as specified condition.
	 * @return whether update successfully.
	 */
	private boolean executeUpdate(ParamSql updateSql, Profile profile) {
		
		if (updateSql.getIndexes().size() == 0 || profile == null)
			return executeUpdate(updateSql.getSql());
		
		try {
			AttributeList attList = profile.getAttRef();
			
			PreparedStatement stm = conn.prepareStatement(updateSql.getSql());
			
			List<Integer> indexes = updateSql.getIndexes();
			for (int i = 0; i < indexes.size(); i++) {
				
				int index = indexes.get(i);
				Attribute att = attList.get(index);
				Type type = att.getType();
				
				if (profile.isMissing(index)) {
					stm.setNull(i + 1, toSqlType(type));
					continue;
				}
				
				switch (type) {
				case bit:
					stm.setByte(i + 1, (byte)profile.getValueAsInt(index));
					break;
				case nominal:
					stm.setInt(i + 1, profile.getValueAsInt(index));
					break;
				case integer:
					stm.setInt(i + 1, profile.getValueAsInt(index));
					break;
				case real:
					stm.setDouble(i + 1, profile.getValueAsReal(index));
					break;
				case string:
					stm.setString(i + 1, profile.getValueAsString(index));
					break;
				case date:
					Date date =  (Date)profile.getValue(index);
					stm.setDate(i + 1, new java.sql.Date(date.getTime()));
					break;
				case object:
					stm.setObject(i + 1, profile.getValue(index));
					break;
				default:
					stm.setObject(i + 1, profile.getValue(index));
				}
			}
			
			stm.executeUpdate();
			stm.close();
			
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			
		}
		return false;
	
		
	}
	

	/**
	 * Getting maximum number of first field from executing select SQL statement.
	 * @param selectSql select SQL statement.
	 * @param condition profile as condition.
	 * @return maximum value.
	 */
	private double max(ParamSql selectSql, Profile condition) {

		double max = Constants.UNUSED;
		try {
			
			ResultSet rs = executeQuery(selectSql, condition);
			
			while(rs.next()) {
				max = rs.getDouble(1);
				break;
			}
			
			closeResultSet(rs);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}

		return max;
		
	}

	
	/**
	 * Checking whether the record that contains the specified condition is existent.
	 * @param selectSql select SQL statement.
	 * @param condition profile as specified condition.
	 * @return whether the record that contains the specified condition is existent.
	 */
	private boolean exists(ParamSql selectSql, Profile condition) {
		
		try {
			
			ResultSet rs = executeQuery(selectSql, condition);
			if (rs == null)
				return false;
			
			boolean contain = rs.next();
			closeResultSet(rs);
			
			return contain;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}

	
	@Override
	public CsvReader getReader(String unit) {
		throw new RuntimeException("Not support CSV reader");
	}


	@Override
	public CsvWriter getWriter(String unit, boolean append) {
		throw new RuntimeException("Not support CSV writer");
	}
	
	
	@Override
	public void close() throws Exception {
		if (conn != null) {
			try {
				conn.close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		conn = null;
		
		config = null;
	}


	/**
	 * Create connection from configuration.
	 * 
	 * @return {@link Connection} created.
	 */
	private static Connection createConnection(DataConfig config) {
		String username = config.getStoreAccount();
		HiddenText password = config.getStorePassword();
		
		try {
			
			String url = config.getStoreUri().toString();
			Properties properties = new Properties();
			properties.setProperty("useSSL", "false");
			if (username != null && password != null) {
				properties.setProperty("user", username);
				properties.setProperty("password", password.getText());
			}
			return DriverManager.getConnection(url, properties);
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}


	/**
	 * Close the specified result set.
	 * @param rs specified result set.
	 */
	private static void closeResultSet(ResultSet rs) {
		if (rs == null)
			return;
		
		try {
			Statement stm = rs.getStatement();
			if (stm != null && !stm.isClosed())
				stm.close();
			
			rs.close();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	

	/**
	 * This class is fetcher of database table.
	 * @param <E> type of elements.
	 * @author Loc Nguyen
	 * @version 10.0
	 */
	static abstract class DbFetcher<E /*extends Serializable*/> implements Fetcher<E> {
		
		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Internal record set.
		 */
		protected ResultSet rs = null;
		
		/**
		 * Fetcher meta-data.
		 */
		protected FetcherMetadata metadata = null;
		
		/**
		 * Constructor with specified result set.
		 * @param rs specified result set.
		 */
		public DbFetcher(ResultSet rs) {
			this.rs = rs;
			this.metadata = new FetcherMetadata();
			
			try {
				if (rs.last()) {
					this.metadata.setSize(rs.getRow());
					rs.beforeFirst();
				}
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("DbFetcher initialized fail, error " + e.getMessage());
			}
		}
		
		@Override
		public boolean next() throws RemoteException {
			try {
				return (rs != null && rs.next());
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				return false;
			}
		}

		@Override
		public E pick() throws RemoteException {
			return create(rs);
		}

		@Override
		public void reset() throws RemoteException {
			try {
				rs.beforeFirst();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

		@Override
		public FetcherMetadata getMetadata() throws RemoteException {
			return metadata;
		}

		@Override
		public void close() throws RemoteException {
			if (rs == null)
				return;
			
			closeResultSet(rs);
			rs = null;
			metadata = null;
			
		}

		@Override
		protected void finalize() throws Throwable {
//			super.finalize();
			
			try {
				close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

		/**
		 * Creating a element with specified result set.
		 * @param rs specified result set.
		 * @return element created from specified result set.
		 */
		public abstract E create(ResultSet rs);
		
		@Override
		public String toText() {
			StringBuffer buffer = new StringBuffer();
			int i = 0;
			
			try {
				while(next()) {
					if (i > 0)
						buffer.append("\n");
					
					E el = pick();
					if (el == null)
						continue;
					
					String row = el.getClass().getName() + TextParserUtil.LINK_SEP;
					if (el instanceof TextParsable)
						buffer.append(row + ((TextParsable)el).toText());
					else
						buffer.append(row + el.toString());
							
					i++;
				}
				reset();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			finally {
				try {
					reset();
				} 
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
			
			return buffer.toString();
		}

		@Override
		public void parseText(String spec) {
			throw new RuntimeException("Not support this method");
		}
		
	}
	
	
	/**
	 * This class is fetcher of database table, which support RMI calls.
	 * @author Loc Nguyen
	 * @version 10.0
	 * @param <E> type of elements.
	 */
	static abstract class RmiDbFetcher<E /*extends Serializable*/> extends DbFetcher<E> {
		
		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * Constructor with record set.
		 * @param rs specified record set.
		 */
		public RmiDbFetcher(ResultSet rs) {
			super(rs);
			try {
				UnicastRemoteObject.exportObject(this, 0);
			} 
			catch (RemoteException e) {
				LogUtil.trace(e);
			}
		}

		@Override
		public void close() throws RemoteException {
			if (rs != null) {
				try {
					UnicastRemoteObject.unexportObject(this, true);
				} 
				catch (NoSuchObjectException e) {
					LogUtil.trace(e);
					LogUtil.error("No such object exported, error " + e.getMessage());
				}
			}
			
			super.close();
		}

	}


}



