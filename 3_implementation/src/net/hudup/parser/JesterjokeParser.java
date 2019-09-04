/**
 * 
 */
package net.hudup.parser;

import java.rmi.RemoteException;

import net.hudup.core.data.DataDriver;
import net.hudup.core.parser.SnapshotParser;


/**
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
public abstract class JesterjokeParser extends SnapshotParser {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public JesterjokeParser() {
		
	}
	
	
	@Override
	public String getName() {
		return "jesterjoke_parser";
	}


	/**
	 * Equation y = (y1 - y0) / (x1 - x0) * x - (x0*y1 - x1*y0) / (x1 - x0)
	 * Mapping (1, -10) and (5, +10)
	 * @return {@link Mapper}
	 */
	protected Mapper getMapper() {
		return new Mapper() {
			
			@Override
			public double map(double value) {
				// TODO Auto-generated method stub
				return 5f * value -  15;
			}
			
			@Override
			public double imap(double value) {
				// TODO Auto-generated method stub
				return (value + 15f) / 5f;
			}
		};
	}
	
	
	@Override
	public boolean support(DataDriver driver) throws RemoteException {
		// TODO Auto-generated method stub
		
		return driver.isFlatServer();
	}
	
	
}
