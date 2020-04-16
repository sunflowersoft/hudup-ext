/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.io.Serializable;

/**
 * This class is similar to {@link xURI} except that the scheme inside the path of {@link xSubURI} is simplest.
 * For example, given a JDBC MySQL connection string &quot;jdbc:mysql://localhost:3306/hudup&quot;, specified fully by {@link xURI}, in which the whole complex scheme is &quot;jdbc:mysql&quot;.
 * In {@link xSubURI}, the variable {@link #brief} only specifies &quot;mysql://localhost:3306/hudup&quot; in which the scheme is &quot;mysql&quot;.
 * In other words, scheme inside the path of {@link xSubURI} is the most important part in the whole complex scheme.
 * The whole complex scheme is stored in its other variable {@link #scheme}.
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class xSubURI implements Serializable {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * The whole complex scheme, for example, &quot;jdbc:mysql&quot;.
	 */
	public String scheme = null;
	
	
	/**
	 * The brief URI with simplest scheme, for example, &quot;mysql://localhost:3306/hudup&quot;.
	 */
	public xURI brief = null;
	
	
	/**
	 * Constructor with the whole complex scheme and the brief URI with simplest scheme.
	 * @param scheme The whole complex scheme, for example, &quot;jdbc:mysql&quot;
	 * @param brief The brief URI with simplest scheme, for example, &quot;mysql://localhost:3306/hudup&quot;
	 */
	public xSubURI(String scheme, String brief) {
		this.scheme = scheme;
		this.brief = xURI.create(brief);
	}
	
	
}
