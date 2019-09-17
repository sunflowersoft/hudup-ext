/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server;

import net.hudup.core.client.ActiveMeasure;

/**
 * This class implements active measure of server. Please see {@link ActiveMeasure}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ActiveMeasureImpl implements ActiveMeasure {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Request count.
	 */
	private int requestCount = 0;
	
	
	/**
	 * Default constructor.
	 */
	public ActiveMeasureImpl() {
		
	}
	
	
	@Override
	public int compareTo(ActiveMeasure o) {
		// TODO Auto-generated method stub
		
		double a = this.compute();
		double b = o.compute();
		if (a < b)
			return -1;
		else if(a == b)
			return 0;
		else
			return 1;
	}

	
	@Override
	public double compute() {
		return requestCount;
	}
	
	
	@Override
	public synchronized void reset() {
		requestCount = 0;
	}
	
	
	@Override
	public int getRequestCount() {
		return requestCount;
	}
	
	
	@Override
	public synchronized void incRequestCount() {
		requestCount++;
	}
	
	
	@Override
	public synchronized void decRequestCount() {
		if (requestCount == 0)
			return;
		requestCount--;
	}

	
	@Override
	public synchronized void setRequestCount(int requestCount) {
		this.requestCount = Math.max(requestCount, 0);
	}


	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		double a = this.compute();
		double b = ((ActiveMeasure)obj).compute();
		return a == b;
	}
	
	
	
}
