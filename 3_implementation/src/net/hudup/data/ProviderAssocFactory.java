package net.hudup.data;

import net.hudup.core.client.Service;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderAssoc;
import net.hudup.core.data.ProviderAssocAbstract;
import net.hudup.core.data.UnitList;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.logistic.xURI;


/**
 * Factory utility class for creating suitable provider associator {@link ProviderAssoc}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ProviderAssocFactory {

	
	/**
	 * Creating suitable provider associator according to data configuration.
	 * @param config specified configuration.
	 * @return suitable provider associator according to data configuration referred by {@link ProviderAssoc}.
	 */
	public static ProviderAssoc create(DataConfig config) {
		DataDriver dataDriver = DataDriver.create(config.getStoreUri());
		if (dataDriver == null)
			return null;
		ProviderAssoc providerAssoc = null;
		
		if (dataDriver.isFlatServer())
			providerAssoc = new FlatProviderAssoc(config);
		else if (dataDriver.isDbServer())
			providerAssoc = new DbProviderAssoc(config);
		else if (dataDriver.isHudupServer()) {
			xURI uri = config.getStoreUri();
			if (dataDriver.getType() == DataType.hudup_rmi) {
				Service service = net.hudup.core.client.DriverManager.getRemoteService(
						uri.getHost(),
						uri.getPort(),
						config.getStoreAccount(), 
						config.getStorePassword().getText());
				
				if (service != null)
					providerAssoc = new HudupProviderAssoc(config);
			}
			else if (dataDriver.getType() == DataType.hudup_socket) {
				SocketConnection connection = net.hudup.core.client.DriverManager.getSocketConnection(
						uri,
						config.getStoreAccount(), 
						config.getStorePassword().getText());
				
				if (connection != null && connection.isConnected())
					providerAssoc = new HudupProviderAssoc(config);
				if (connection != null)
					connection.close();
			}
		}
		
		return providerAssoc;
	}
	
	
}


/**
 * Provider associator for Hudup server. This class is incomplete and so it is used for consistency along with database associator and file system associator.
 * 
 * @author Loc Nguyen
 * @version 11
 *
 */
class HudupProviderAssoc extends ProviderAssocAbstract {

	
	/**
	 * Constructor with specified configuration.
	 * 
	 * @param config specified configuration.
	 */
	public HudupProviderAssoc(DataConfig config) {
		super(config);
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean createUnit(String unitName, AttributeList attList) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public boolean deleteUnitData(String unitName) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public boolean dropUnit(String unitName) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public UnitList getUnitList() {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public NominalList getNominalList(String filterUnit, String attName) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public AttributeList getAttributes(String profileUnit) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public AttributeList getAttributes(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public boolean containsProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public Profile getProfile(String profileUnit, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public Fetcher<Profile> getProfiles(String profileUnit, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public Fetcher<Integer> getProfileIds(String profileUnit) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public int getProfileMaxId(String profileUnit) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public boolean insertProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public boolean updateProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) {
		// TODO Auto-generated method stub
		throw new RuntimeException("Not implemented yet");
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	
}
