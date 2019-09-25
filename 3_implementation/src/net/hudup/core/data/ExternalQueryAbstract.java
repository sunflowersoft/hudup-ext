/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import net.hudup.core.alg.AlgAbstract;
import net.hudup.core.logistic.LogUtil;

/**
 * This abstract class implements basically external query.
 * 
 * @author Loc Nguyen
 * @version 12.0
 * 
 */
public abstract class ExternalQueryAbstract extends AlgAbstract implements ExternalQuery, ExternalQueryRemote {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public ExternalQueryAbstract() {
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public synchronized void setConfig(DataConfig config) {
		LogUtil.info("External query does not support method setConfig(DataConfig)");
	}

	
	@Override
	public synchronized void resetConfig() {
		// TODO Auto-generated method stub
		LogUtil.info("External query does not support method resetConfig()");
	}

	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			unexport();
		} catch (Throwable e) {e.printStackTrace();}
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}

	
}
