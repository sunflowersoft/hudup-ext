package net.hudup.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.data.SnapshotImplExt;

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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		
		config.setParser(this);
		return SnapshotImplExt.create(config);
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SnapshotParserImplExt();
	}
	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "snapshot_parser_ext";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "Snapshot parser_ext";
	}
	
	
}
