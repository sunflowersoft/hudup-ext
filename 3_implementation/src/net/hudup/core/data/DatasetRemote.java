/**
 * 
 */
package net.hudup.core.data;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;

import net.hudup.core.data.ctx.Context;
import net.hudup.core.data.ctx.ContextList;
import net.hudup.core.data.ctx.ContextTemplate;
import net.hudup.core.data.ctx.ContextTemplateSchema;


/**
 * This interface represents remote dataset.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public interface DatasetRemote extends Remote {

	
	/**
	 * Getting configuration of rating data remotely.
	 * @return Configuration of rating data {@link DataConfig}
	 * @throws RemoteException if any error raises.
	 */
	DataConfig remoteGetConfig() throws RemoteException;
	
	
	/**
	 * Retrieving collection of user ID (s) via {@link Fetcher} remotely.
	 * @return {@link Fetcher} of user ID (s).
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Integer> remoteFetchUserIds() throws RemoteException;


	/**
	 * In the case that a user ID is associated with an external user ID stored in external database,
	 * this function returns the user ID of specified external user ID remotely.
	 * @param externalUserId External user ID stored in external database
	 * @return The user ID associated with the specified external user ID
	 * @throws RemoteException if any error raises.
	 */
	int remoteGetUserId(Serializable externalUserId) throws RemoteException;
	
	
	/**
	 * Retrieving remotely external user ID associated with the specified user ID stored in this {@code Dataset}.
	 * In the case that {@code Dataset} is linked to an external database,
	 * each user ID in {@code Dataset} has different ID in such external database.
	 * @param userId specified internal user identifier.
	 * @return The external record {@link ExternalRecord} that contains the external user ID
	 * @throws RemoteException if any error raises.
	 */
	ExternalRecord remoteGetUserExternalRecord(int userId) throws RemoteException;
	
	
	/**
	 * Retrieving collection of item ID (s) via {@link Fetcher} remotely.
	 * @return {@link Fetcher} of item ID (s)
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Integer> remoteFetchItemIds() throws RemoteException;

	
	/**
	 * 
	 * In the case that an item ID is associated with an external item ID stored in external database,
	 * this function returns remotely the item ID of specified external item ID.
	 * @param externalItemId External item ID stored in external database
	 * @return The item ID associated with the specified external item ID
	 * @throws RemoteException if any error raises.
	 */
	int remoteGetItemId(Serializable externalItemId) throws RemoteException;
	
	
	/**
	 * 
	 * Retrieving remotely external item ID associated with the specified item ID stored in this {@code Dataset}.
	 * In the case that {@code Dataset} is linked to an external database,
	 * each item ID in {@code Dataset} has different ID in such external database.
	 * @param itemId specified internal item identifier.
	 * @return The external record {@link ExternalRecord} that contains the external item ID
	 * @throws RemoteException if any error raises.
	 */
	ExternalRecord remoteGetItemExternalRecord(int itemId) throws RemoteException;
	
	
	/**
	 * Getting {@link Rating} of specified user ID and item ID remotely.
	 * {@link Rating} includes rating value, rated date and context information.
	 * @param userId Specified user ID
	 * @param itemId Specified item ID
	 * @return {@link Rating} of specified user ID and item ID includes rating value, rated date and context information
	 * @throws RemoteException if any error raises.
	 */
	Rating remoteGetRating(int userId, int itemId) throws RemoteException;
	
	
	/**
	 * Getting rating vector {@link RatingVector} of specified user ID remotely.
	 * @param userId Specified user ID
	 * @return {@link RatingVector} of specified user ID
	 * @throws RemoteException if any error raises.
	 */
	RatingVector remoteGetUserRating(int userId) throws RemoteException;
	
	
	/**
	 * Retrieving collection of user {@link RatingVector} (s) via {@link Fetcher} remotely.
	 * @return {@link Fetcher} of user {@link RatingVector}
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<RatingVector> remoteFetchUserRatings() throws RemoteException;
	
	
	/**
	 * Getting rating vector {@link RatingVector} of specified item ID remotely.
	 * @param itemId Specified item ID
	 * @return {@link RatingVector} of specified item ID
	 * @throws RemoteException if any error raises.
	 */
	RatingVector remoteGetItemRating(int itemId) throws RemoteException;
	
	
	/**
	 * Retrieving collection of item {@link RatingVector} (s) via {@link Fetcher} remotely.
	 * @return {@link Fetcher} of item {@link RatingVector}
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<RatingVector> remoteFetchItemRatings() throws RemoteException;
	
	
	/**
	 * Creating remotely the user-based rating matrix whose each row represents ratings that a concrete user gives to many items.
	 * Please be careful to use this method in case of huge {@code Dataset} because such matrix is stored in memory.
	 * @return User-based rating matrix via {@link RatingMatrix}
	 * @throws RemoteException if any error raises.
	 */
	RatingMatrix remoteCreateUserMatrix() throws RemoteException;
	
	
	/**
	 * Creating remotely the item-based rating matrix whose each row represents ratings that a concrete item receives from many users.
	 * Please be careful to use this method in case of huge {@code Dataset} because such matrix is stored in memory.
	 * @return Item-based rating matrix via {@link RatingMatrix}
	 * @throws RemoteException if any error raises.
	 */
	RatingMatrix remoteCreateItemMatrix() throws RemoteException;
	
	
	/**
	 * Getting {@link Profile} of specified user ID remotely.
	 * {@link Profile} contains associative information of a given subject: user, item, context, etc.
	 * For example, {@link Profile} of user ID 1 can contains: John, male, john at mail.com
	 * @param userId Specified user ID
	 * @return {@link Profile} of specified user ID
	 * @throws RemoteException if any error raises.
	 */
	Profile remoteGetUserProfile(int userId) throws RemoteException;

	
	/**
	 * Retrieving collection of user {@link Profile} (s) via {@link Fetcher} remotely.
	 * {@link Profile} contains associative information of a given subject: user, item, context, etc.
	 * For example, {@link Profile} of user ID 1 can contains: John, male, john at mail.com.
	 * @return {@link Fetcher} of user {@link Profile} (s)
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Profile> remoteFetchUserProfiles() throws RemoteException;
	
	
	/**
	 * Getting list of user attributes via {@link AttributeList} remotely.
	 * For example, user attributes can include user name, gender, email address, etc.
	 * @return {@link AttributeList} represents list of user attributes
	 * @throws RemoteException if any error raises.
	 */
	AttributeList remoteGetUserAttributes() throws RemoteException;

	
	/**
	 * Getting {@link Profile} of specified item ID remotely.
	 * {@link Profile} contains associative information of a given subject: user, item, context, etc.
	 * For example, {@link Profile} of item ID 1 can contains: Computer COM1, CPU Intel, 2GB RAM.
	 * @param itemId Specified item ID
	 * @return {@link Profile} of specified item ID
	 * @throws RemoteException if any error raises.
	 */
	Profile remoteGetItemProfile(int itemId) throws RemoteException;
	
	
	/**
	 * Retrieving collection of item {@link Profile} (s) via {@link Fetcher} remotely.
	 * {@link Profile} contains associative information of a given subject: user, item, context, etc.
	 * For example, {@link Profile} of item ID 1 can contains: Computer COM1, CPU Intel, 2GB RAM.
	 * @return {@link Fetcher} of item {@link Profile} (s)
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Profile> remoteFetchItemProfiles() throws RemoteException;

	
	/**
	 * Getting list of item attributes via {@link AttributeList} remotely.
	 * For example, item attributes can include item name, serial number, type, price, etc.
	 * @return {@link AttributeList} represents list of item attributes
	 * @throws RemoteException if any error raises.
	 */
	AttributeList remoteGetItemAttributes() throws RemoteException;

	
	/**
	 * Getting profile of the specified {@link Context} remotely.
	 * {@link Context} represents time, location, companion associated with a user when she/he is buying, studying, etc.
	 * {@link Context} is an instance of context template {@link ContextTemplate} with specific value.
	 * @param context Specified {@link Context}
	 * @return {@link Profile} of specified {@link Context}
	 * @throws RemoteException if any error raises.
	 */
	Profile remoteProfileOf(Context context) throws RemoteException;
	
	
	/**
	 * Retrieving remotely {@link Profile} (s) collection of specified context template ID via {@link Profiles}.
	 * Please see {@link ContextTemplate} and {@link ContextTemplateSchema} for more details of context template.
	 * @param ctxTemplateId Specified context template ID
	 * @return {@link Profiles} represents {@link Profile} collection of specified context template ID
	 * @throws RemoteException if any error raises.
	 */
	Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException;

	
	/**
	 * Retrieving remotely collection of {@link Profile} (s) of sample via {@link Fetcher}.
	 * <code>Profile</code> contains associative information of a given subject: user, item, context, etc.
	 * For example, <code>profile</code> of item ID 1 can contains: Computer COM1, CPU Intel, 2GB RAM.
	 * @return <code>Fetcher</code> of profiles of sample.
	 * @throws RemoteException if any error raises.
	 */
	Fetcher<Profile> remoteFetchSample() throws RemoteException;
	
	
	/**
	 * Retrieving remotely the {@link Snapshot} of this {@code Dataset} remotely.
	 * If this {@code Dataset} is {@link Snapshot}, the method returns this {@code Dataset}. 
	 * @return {@link Snapshot} created from this {@code Dataset}
	 * @throws RemoteException if any error raises.
	 */
	Dataset remoteCatchup() throws RemoteException;
	
	
	/**
	 * Creating remotely the sub {@code Dataset} (filtered {@code Dataset}) of this {@code Dataset} by selecting ratings whose contexts are in the specified context list.
	 * {@link Context} represents time, location, companion associated with a user when she/he is buying, studying, etc.
	 * Please see {@link Context} and {@link ContextList} for more details of context and context list.
	 * @param contexts Specified context list
	 * @return Sub {@code Dataset} whose contexts are in the specified context list
	 * @throws RemoteException if any error raises.
	 */
	Dataset remoteSelectByContexts(ContextList contexts) throws RemoteException;
	
	
	/**
	 * Checking remotely whether or not this {@code Dataset} is exclusive.
	 * {@code Dataset} is often loaded and feed to the {@code Recommender} algorithm via method {@code Recommender#setup(Dataset, Object...)}.
	 * If this {@code Dataset} is in exclusive mode, it is clear (method clear()} is called)
	 * when method {@code Recommender#unsetup()} is called ({@code Recommender} algorithm is stopped).
	 * @return Whether or not this {@code Dataset} is exclusive
	 * @throws RemoteException if any error raises.
	 */
	boolean remoteIsExclusive() throws RemoteException;
	
	
	/**
	 * Getting remotely schema of context template {@link ContextTemplate}.
	 * {@link ContextTemplate} is template for a context represented by the class {@link Context}.
	 * {@link Context} includes time, location, companion, etc. associated with a user when she/he is buying, studying, etc.
	 * For example, <i>Time</i> is the template for time context <i>31/10/2015 6:45</i>.
	 * In other words, context is an instance of context template with specific value.
	 * A set of context templates is structured as the model which is called <i>context template schema</i> represented by interface {@link ContextTemplateSchema}.
	 * Please see {@link Context}, {@link ContextTemplate} and {@link ContextTemplateSchema} for more details of context, context template and context template schema.
	 * @return {@link ContextTemplateSchema}
	 * @throws RemoteException if any error raises.
	 */
	ContextTemplateSchema remoteGetCTSchema() throws RemoteException;
	
	
	/**
     * Remote exporting this remote dataset.
     * @param serverPort server port.
     * @return stub as remote dataset. Return null if exporting fails.
     * @throws RemoteException if any error raises.
     */
    DatasetRemote remoteExport(int serverPort) throws RemoteException;
    
    
    /**
     * Remote unexporting remote dataset.
     * @throws RemoteException if any error raises.
     */
    void remoteUnexport() throws RemoteException;


}
