/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This interface is flag to indicate singleton exporting class. 
 * If registered object of a singleton exporting class was exported, such singleton exporting class will not be re-instantiated (new object will not be created) to export
 * unless the registered object is unexported. Service class often implements this interface. 
 *  
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface SingletonExport {

	
}
