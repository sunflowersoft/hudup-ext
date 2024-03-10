/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.console;

import java.rmi.RemoteException;

import net.hudup.core.app.App;
import net.hudup.core.app.ApporAbstract;
import net.hudup.core.client.PowerServer;
import net.hudup.core.logistic.LogUtil;

/**
 * This class console application creator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class ConsoleAppor extends ApporAbstract {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default application creator.
	 */
	public ConsoleAppor() {
		super();
	}

	
	@Override
	public App create(PowerServer server) {
		if (app != null) return app;
		if (server == null) return null;
		
		try {
			ConsoleImpl console = new ConsoleImpl() {

				/**
				 * Serial version UID for serializable class.
				 */
				private static final long serialVersionUID = 1L;
				
				@Override
				public String getName() throws RemoteException {
					return app.getName();
				}
				
				@Override
				protected void consoleTask() {
					boolean editable = getComponent().isEditable();
					getComponent().setEditable(true);
					
					try {
						((ConsoleApp)app).consoleTask();
					} catch (Throwable e) {LogUtil.trace(e);}
					
					getComponent().setEditable(editable);
				}
				
			};
			try {console.export(server.getPort());} catch (Throwable e) {LogUtil.trace(e);}
			
			app = newApp(server, this, console);
			try {app.export(server.getPort());} catch (Throwable e) {LogUtil.trace(e);}
			return app;
		} catch (Throwable e) {LogUtil.trace(e);}
		
		return null;
	}

	
	/**
	 * Creating console application with server, console application creator, and console.
	 * @param server power server.
	 * @param consoleAppor console application creator.
	 * @param console console.
	 * @return console application.
	 */
	protected abstract ConsoleApp newApp(PowerServer server, ConsoleAppor consoleAppor, Console console);


}
