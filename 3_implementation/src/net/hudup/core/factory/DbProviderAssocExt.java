/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.factory;

import net.hudup.core.data.DataConfig;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Attribute.Type;

/**
 * This class is extended associator which supports dyadic database.
 * 
 * @author Loc Nguyen
 * @version 2.0
 *
 */
class DbProviderAssocExt extends DbProviderAssoc {

	
	/**
	 * Constructor with specified configuration.
	 * @param config specified configuration.
	 */
	public DbProviderAssocExt(DataConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public String genRatingCreateSql() {
		return 	"create table " + norm(config.getRatingUnit()) + " ( " + 
				norm(DataConfig.USERID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.ITEMID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
				norm(DataConfig.RATING_FIELD) + " " + toSqlTypeName(Type.real) + " not null, " +
				norm(DataConfig.RATING_DATE_FIELD) + " " + toSqlTypeName(Type.date);
	}

	
	@Override
	public String genContextCreateSql() {
		return "create table " + norm(config.getContextUnit()) + " ( " + 
			norm(DataConfig.USERID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.ITEMID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.CTX_TEMPLATEID_FIELD) + " " + toSqlTypeName(Type.integer) + " not null, " +
			norm(DataConfig.CTX_VALUE_FIELD) + " " + toSqlTypeName(Type.string) + ", " +
			norm(DataConfig.RATING_DATE_FIELD) + " " + toSqlTypeName(Type.date);
	}

	
	@Override
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

	
	@Override
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


}
