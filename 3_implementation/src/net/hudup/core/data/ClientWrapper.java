/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.rmi.Remote;

import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.Timestamp;

/**
 * This class wraps a client.
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ClientWrapper implements Serializable, java.lang.AutoCloseable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal remote client.
	 */
	protected Remote client = null;
	
	
	/**
	 * Client name.
	 */
	protected String name = null;
	
	
	/**
	 * Auxiliary name. Note that it is not client name.
	 */
	protected String auxName = null;
	
	
	/**
	 * Constructor with remote client and auxiliary name.
	 * @param client remote client.
	 * @param name client name.
	 * @param auxName auxiliary name. Note that it is not client name.
	 */
	protected ClientWrapper(Remote client, String name, String auxName) {
		this.client = client;
		this.name = name != null ? name : "noname";
		this.auxName = auxName;
		
		initialize();
	}


	/**
	 * Initialize something.
	 */
	protected void initialize() {
		if (client == null) return;
		
		if (client instanceof Evaluator) {
			try {
				Evaluator evaluator = (Evaluator)client;
				this.name = evaluator.getVersionName();
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
	}
	
	
	/**
	 * Getting client name.
	 * @return client name.
	 */
	public String getName() {
		return name;
	}
	
	
	/**
	 * Getting client.
	 * @return remote client.
	 */
	public Remote getClient() {
		return client;
	}
	
	
	/**
	 * Getting client status.
	 * @return client status.
	 */
	public String getStatus() {
		if (client == null) return "";
		
		if (client instanceof Evaluator) {
			Evaluator evaluator = (Evaluator)client;
			try {
				return EvaluatorAbstract.getStatusText(evaluator);
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		
		return "";
	}
	
	
	/**
	 * Resetting client.
	 */
	public void reset() {
		if (client == null) return;
		
		if (client instanceof Evaluator) {
			try {
				Evaluator evaluator = (Evaluator)client;
				evaluator.remoteStop();
				evaluator.reloadPool(null, new Timestamp());
			} catch (Exception e) {LogUtil.trace(e);}
		}
	}
	
	
	@Override
	public void close() throws Exception {
		if (client == null) return;
		
		if (client instanceof Evaluator) {
			try {
				Evaluator evaluator = (Evaluator)client;
				evaluator.remoteStop();
				evaluator.refPool(false, (DatasetPoolsService)null, null, null, new Timestamp());
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		
		this.client = null;
		this.name = null;
		this.auxName = null;
	}


	@Override
	public String toString() {
		String name = getName();
		return name != null ? DSUtil.shortenVerbalName(name) : super.toString();
	}
	

	/**
	 * Create client wrapper.
	 * @param client remote client.
	 * @param name client name.
	 * @param auxName auxiliary name.
	 * @return client wrapper.
	 */
	public static ClientWrapper create(Remote client, String name, String auxName) {
		if (client == null) return null;
		return new ClientWrapper(client, name, auxName);
	}
	
	
	/**
	 * Create client wrapper.
	 * @param client remote client.
	 * @param name client name.
	 * @return client wrapper.
	 */
	public static ClientWrapper create(Remote client, String name) {
		return create(client, name, null);
	}


}
