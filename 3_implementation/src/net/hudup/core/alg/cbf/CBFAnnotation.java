/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cbf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * This annotation indicates that the class to which it is applied is a content-based filtering (CBF) recommendation algorithm.
 *  
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Documented
@Target ( {ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CBFAnnotation {

}
