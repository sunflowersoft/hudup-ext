/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.evaluate;

import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemote;

/**
 * This interface represents remote metric.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface MetricRemote extends MetricRemoteTask, AlgRemote {


	@Override
	String getTypeName() throws RemoteException;
	
	
	@Override
	void setup(Object...params) throws RemoteException;
	
	
	@Override
	MetricValue getCurrentValue() throws RemoteException;
	
	
	@Override
	MetricValue getAccumValue() throws RemoteException;

	
	@Override
	boolean recalc(Object...params) throws RemoteException;
	
	
	@Override
	void reset() throws RemoteException;
	
	
	@Override
	boolean isValid() throws RemoteException;


}
