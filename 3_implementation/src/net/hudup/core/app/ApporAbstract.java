/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.app;

import net.hudup.core.logistic.LogUtil;

/**
 * This abstract implements partially the application creator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ApporAbstract implements Appor {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal application.
	 */
	protected App app = null;
	
	
	/**
	 * Default constructor.
	 */
	public ApporAbstract() {

	}

	
	/**
	 * Discarding application.
	 * @param app specified application.
	 * @return true if discarding is successful.
	 */
	protected boolean discard(App app) {
		if (app == null)
			return false;
		else {
			boolean discarded = false;
			try {
				discarded = (app instanceof AppAbstract) ? ((AppAbstract)app).discard0() : app.discard();
				try {app.unexport();} catch (Throwable e) {LogUtil.trace(e);}
			} catch (Throwable e) {LogUtil.trace(e);}
			
			this.app = (app == this.app ? null : this.app);
			return discarded;
		}
	}

	
	@Override
	public void close() throws Exception {
		try {
			if (this.app != null) discard(this.app);
			this.app = null;
		} catch (Throwable e) {LogUtil.trace(e);}
	}

	
}
