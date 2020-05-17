/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.SnapshotImplExt;

/**
 * This class implements extended snapshot parser which supports dyadic database.
 * 
 * @author Loc Nguyen
 * @version 2.0
 *
 */
public class SnapshotParserImplExt extends SnapshotParserImpl {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public SnapshotParserImplExt() {

	}

	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		
		config.setParser(this);
		return SnapshotImplExt.create(config);
	}

	
	@Override
	public Alg newInstance() {
		return new SnapshotParserImplExt();
	}
	
	
	@Override
	public String getName() {
		return "snapshot_parser_ext";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		return "Snapshot parser_ext";
	}
	
	
}
