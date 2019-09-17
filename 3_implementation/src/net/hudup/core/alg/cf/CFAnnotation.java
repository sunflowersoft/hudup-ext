/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.alg.cf;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * This annotation indicates that the class to which it is applied is a collaborative filtering (CF) recommendation algorithm.
 * Please see {@code net.hudup.core.alg.cf.MemoryBasedCF} and {@code net.hudup.core.alg.cf.ModelBasedCF} for more details about CF algorithms.
 *  
 * @author Loc Nguyen
 * @version 11.0
 *
 */
@Documented
@Target ( {ElementType.TYPE} )
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface CFAnnotation {

}
