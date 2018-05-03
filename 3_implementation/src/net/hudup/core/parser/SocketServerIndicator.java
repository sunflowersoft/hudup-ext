/**
 * 
 */
package net.hudup.core.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.ServerPointer;

/**
 * There are two typical {@code Dataset} such as {@code Snapshot} and {@code Scanner}.
 * {@code Snapshot} or {@code Scanner} is defined as an image of piece of {@code Dataset} and knowledge base ({@code KBase}) at certain time point.
 * The difference between {@code Snapshot} and {@code Scanner} that {@code Snapshot} copies whole piece of data into memory while scanner is merely a reference to such data piece.
 * Another additional {@code Dataset} is {@code Pointer}.
 * {@code Pointer} does not contain any information nor provide any access means to dataset.
 * It only points to another {@code Snapshot}, {@code Scanner}, or {@code KBase}.<br>
 * Although you can create your own {@code Dataset}, {@code Dataset} is often retrieved from utility class parsers that implement interface {@link DatasetParser}.
 * {@code Snapshot} is retrieved from {@link SnapshotParser}.
 * {@code Scanner} is retrieved from {@link ScannerParser}. Both {@link SnapshotParser} and {@link ScannerParser} implement interface {@link DatasetParser}.
 * {@code Pointer} is retrieved from {@link Indicator}. {@link Indicator} is {@link DatasetParser} specified to create {@code Pointer}.
 * <br><br>
 * So this class is an {@link Indicator} to retrieve a {@link ServerPointer} pointing to a remote recommendation service with socket connection represented by {@link SocketConnection}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class SocketServerIndicator extends Indicator {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public SocketServerIndicator() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public Dataset parse(DataConfig config) {
		// TODO Auto-generated method stub
		String account = config.getStoreAccount();
		String password = config.getStorePassword().getText();
		SocketConnection conn = new SocketConnection(
				config.getStoreUri().getHost(), 
				config.getStoreUri().getPort());
		if (!conn.connect(account, password, DataConfig.ACCOUNT_ACCESS_PRIVILEGE))
			return null;
		
		try {
			DataConfig srvConfig = conn.getServerConfig();
			
			DatasetMetadata metadata = srvConfig.getMetadata();
			config.setMetadata(metadata);
			
			ServerPointer pointer = new ServerPointer();
			config.setParser(this);
			pointer.setConfig(config);
			return pointer;
		}
		catch (RemoteException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "socket_server_indicator";
	}

	
	@Override
	public boolean support(DataDriver driver) {
		// TODO Auto-generated method stub
		return driver.getType() == DataType.hudup_socket;
	}

	
	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new SocketServerIndicator();
	}
	

}
