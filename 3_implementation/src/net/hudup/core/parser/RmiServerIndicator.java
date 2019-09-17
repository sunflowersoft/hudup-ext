/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.parser;

import java.rmi.RemoteException;

import net.hudup.core.alg.Alg;
import net.hudup.core.client.ClientUtil;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.DatasetMetadata;
import net.hudup.core.data.ServerPointer;
import net.hudup.core.logistic.xURI;


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
 * So this class is an {@link Indicator} to retrieve a {@link ServerPointer} pointing to a remote recommendation service represented by {@link Service}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class RmiServerIndicator extends Indicator {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * 
	 */
	public RmiServerIndicator() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public Dataset parse(DataConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		xURI uri = config.getStoreUri();
		
		try {
			Service service = ClientUtil.getRemoteService(
					uri.getHost(),
					uri.getPort(),
					config.getStoreAccount(), 
					config.getStorePassword().getText());
			
			if (service == null)
				return null;
			else {
				DataConfig srvConfig = service.getServerConfig();
				
				DatasetMetadata metadata = srvConfig.getMetadata();
				config.setMetadata(metadata);
				
				ServerPointer pointer = new ServerPointer();
				config.setParser(this);
				pointer.setConfig(config);
				return pointer;
				
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}

	
	
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "rmi_server_indicator";
	}

	
	@Override
	public String getDescription() throws RemoteException {
		// TODO Auto-generated method stub
		return "RMI server indicator";
	}


	@Override
	public boolean support(DataDriver driver) throws RemoteException {
		// TODO Auto-generated method stub
		return driver.getType() == DataType.hudup_rmi;
	}

	
	
	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return new DataConfig();
	}

	
	@Override
	public Alg newInstance() {
		// TODO Auto-generated method stub
		return new RmiServerIndicator();
	}
	
	
}
