/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ctx;

import java.io.Serializable;
import java.rmi.RemoteException;

import net.hudup.core.alg.AlgRemoteTask2;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;

/**
 * This interface declares methods for remote context template schema manager.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface CTSManagerRemoteTask2 extends AlgRemoteTask2, AutoCloseable {


	/**
	 * Setting up (initializing) remotely CTS manager via specified configuration.
	 * @param config specified configuration.
	 * @throws RemoteException if any error raises.
	 */
	void remoteSetup(DataConfig config) throws RemoteException;

	
	/**
	 * Retrieving remotely the context template schema that CTS manager manages.
	 * @return context template schema that CTS manager manages.
	 * @throws RemoteException if any error raises.
	 */
	ContextTemplateSchema remoteGetCTSchema() throws RemoteException;
	
	
	/**
	 * Creating the unit for storing context templates. Unit can be a CSV file, a database table, etc.
	 * @return whether creating context template successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteCreateContextTemplateUnit() throws RemoteException;

	
	/**
	 * Reloading remotely context template schema.
	 * @throws RemoteException if any error raises.
	 */
	public void remoteReload() throws RemoteException;
	
	
	/**
	 * Creating remotely a context from the specified template ID and the specified context value.
	 * @param ctxTemplateId specified context template identification (ID).
	 * @param assignedValue specified value that is assigned to the returned context. Such value must be serializable.
	 * @return context created from the specified template ID and the specified value. 
	 * @throws RemoteException if any error raises.
	 */
	Context remoteCreateContext(
			int ctxTemplateId, 
			Serializable assignedValue) throws RemoteException;
	
	
	/**
	 * Retrieving remotely a list of contexts relevant to a specified user and a specified item.
	 * @param userId specified user identification (user ID).
	 * @param itemId specified item identification (item ID).
	 * @return context list represented by {@link ContextList} relevant to a specified user and a specified item.
	 * @throws RemoteException if any error raises.
	 */
	ContextList remoteGetContexts(int userId, int itemId) throws RemoteException;

	
	/**
	 * Retrieving remotely a list of contexts relevant to a specified user, a specified item, and rated date.
	 * @param userId specified user identification (user ID).
	 * @param itemId specified item identification (item ID).
	 * @param ratedDate rated date.
	 * @return context list represented by {@link ContextList} relevant to a specified user, a specified item, and rated date.
	 * @throws RemoteException if any error raises.
	 */
	ContextList remoteGetContexts(int userId, int itemId, long ratedDate) throws RemoteException;

	
	/**
	 * Getting remotely {@link Profile} of each template with regard to its value {@link ContextValue}.
	 * The specified context contains such template and such value.
	 * This method is the same to {@link #remoteProfileOf(int, ContextValue)}.
	 * @param context specified context.
	 * @return profile of the specified context.
	 * @throws RemoteException if any error raises.
	 */
	Profile remoteProfileOf(Context context) throws RemoteException;

	
	/**
	 * Getting remotely {@link Profile} of each template with regard to its value {@link ContextValue}.
	 * Every template owns a profile table and each value {@link ContextValue} of template corresponds to a row in this table.
	 * Moreover such value is id of such row.
	 * 
	 * @param ctxTemplateId specified template identification (ID).
	 * @param ctxValue specified context value.
	 * @return profile of each template with regard to its value.
	 * @throws RemoteException if any error raises.
	 */
	Profile remoteProfileOf(int ctxTemplateId, ContextValue ctxValue) throws RemoteException;

	
	/**
	 * Retrieving remotely all profiles associating with the same template identification (ID).
	 * 
	 * @param ctxTemplateId specified template ID.
	 * @return all profiles associating with the same template identification (ID).
	 * @throws RemoteException if any error raises.
	 */
	Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException;
	
	
	/**
	 * Every template owns a profile table and each value {@link ContextValue} of template corresponds to a row in this table.
	 * Each row in such template profile table is represented by {@link Profile} class.
	 * This method retrieves remotely all profiles of all templates represented by {@link CTSMultiProfiles} class.
	 * @return all profiles of all templates, represented by {@link CTSMultiProfiles} class.
	 * @throws RemoteException if any error raises.
	 */
	CTSMultiProfiles remoteCreateCTSProfiles() throws RemoteException;
	
	
	/**
	 * This method commits remotely that the context template schema that {@code CTS manager} manages is valid in both memory and archive (file).
	 * @return whether the commitment is successful.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteCommitCTSchema() throws RemoteException;
	
	
	/**
	 * Importing remotely context template schema from other CTS manager.
	 * @param ctsm other CTS manager.
	 * @return whether import successfully.
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteImportCTSchema(CTSManager ctsm) throws RemoteException;
	
	
	/**
	 * Importing remotely context template schema from other dataset.
	 * @param dataset other dataset represented by {@link Dataset} class.
	 * @return whether import successfully
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteImportCTSchema(Dataset dataset) throws RemoteException;

	
	/**
	 * Initializing remotely the internal context template schema and committing such schema.
	 * @throws RemoteException if any error raises.
	 */
	void remoteDefaultCTSchema() throws RemoteException;


}
