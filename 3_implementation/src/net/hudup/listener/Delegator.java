/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.listener;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorWrapperExt;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.TaskQueue;
import net.hudup.core.logistic.Timestamp;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.HtmlParsable;

/**
 * This class is full implementation of delegator. It inherits directly {@link AbstractDelegator}.
 * Delegator is responsible for handling and processing user request represented by {@link Request}. Each time {@link Listener} receives a user request, it creates a respective delegator and passes such request to delegator.
 * After that delegator processes and dispatches the request to the proper binding server. The result of recommendation process from server, represented by {@link Response}, is sent back to users/applications by delegator.
 * In fact, delegator interacts directly with server. However, delegator is a part of {@link Listener} and the client-server interaction is always ensured.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class Delegator extends AbstractDelegator {

	
	/**
	 * The power server binds with this delegator.
	 * The delegator dispatches request to this server.
	 * The result of recommendation process from server, represented by {@link Response}, is sent back to users/applications by delegator.
	 * In fact, the service {@link #remoteService} of this server processed the request.
	 */
	protected PowerServer remoteServer = null;

	
	/**
	 * The service of the binding server {@link #remoteServer} actually processes request from the delegator.
	 */
	protected Service remoteService = null;
	
	
	/**
	 * Constructor with specified socket and power server.
	 * @param remoteServer power server binds with this delegator.
	 * @param socket specified Java socket for receiving (reading) requests from client and sending back (writing) responses to client via input / output streams.
	 * @param socketServer socket server is often listener.
	 * The delegator dispatches request to this server.
	 * The result of recommendation process from server, represented by {@link Response}, is sent back to users/applications by delegator.
	 * In fact, the service {@link #remoteService} of this server processed the request.
	 */
	public Delegator(PowerServer remoteServer, Socket socket, SocketServer socketServer) {
		super(socket, socketServer);
		
		this.remoteServer = remoteServer;
		
		try {
			this.remoteService = remoteServer.getService();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			this.remoteService = null;
			
			LogUtil.error("Delegator fail to be constructed in constructor method, causes error " + e.getMessage());
		}
	}
	
	
	@Override
	public void run() {
		try {
			remoteServer.incRequest();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Delegator fail to increase server request, causes error " + e.getMessage());
		}
		
		super.run();
		
		try {
			remoteServer.decRequest();
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Delegator fail to decrease server request, causes error " + e.getMessage());
		}
		
		//Clear extra data.
		remoteServer = null;
		remoteService = null;
	}
	
	
	@Override
	protected Request parseRequest(String requestText) {
		return parseRequest0(requestText);
	}
	
	
	@Override
	protected boolean handleRequest(Request request, DataOutputStream out) {
		int protocol = request.protocol;
		
		try {
			if (protocol == HDP_PROTOCOL) {
				Response response = processRequest(request);
				if (request.notJsonParsing)
					response.toObject(out);
				else
					out.write( (response.toJson() + "\n").getBytes() );
			}
			else if (protocol == HTTP_PROTOCOL) {
				String action = request.action;
				
				if (action != null && !action.equals(READ_FILE)) {
					Response response = processRequest(request);
					Object result = response.getResult();
					
					if (result != null && result instanceof HtmlParsable) {
						out.write(HttpUtil.createResponseHeader(200, HTML_FILE_TYPE).getBytes());
						String html = ((HtmlParsable)result).toHtml();
						out.write( (html + "\n").getBytes());
					}
					else {
						
						out.write(HttpUtil.createResponseHeader(200, JSON_FILE_TYPE).getBytes());
						out.write( (response.toJson() + "\n").getBytes());
					}
					
				}
				else {
					int type = request.file_type;
					out.write(HttpUtil.createResponseHeader(200, type).getBytes());
					
					xURI uri = xURI.create(request.file_path);
					UriAdapter adapter = new UriAdapter(uri);
					InputStream is = adapter.getInputStream(uri);
					
					while (true) {
						byte[] bytes = new byte[1024];
						int length = is.read(bytes);
						if (length == -1)
							break;
						
						out.write(bytes, 0, length);
					}
					
					is.close();
					adapter.close();
				}
				
			} // else if (protocol.equals(HTTP_PROTOCOL))
			
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Delegator fail to handle request, error is " + e.getMessage());
		}
		
		return false;
	}

	
	/**
	 * Processing specified {@link Request} directly.
	 * Firstly, it requires remote {@link Service} to process {@link Request} and receives results.
	 * After that it translates results into {@link Response}.
	 * 
	 * This method is called by {@link #handleRequest(Request, DataOutputStream)} method.
	 * 
	 * @param request specified request.
	 * @return translated result as {@link Response}.
	 */
	@SuppressWarnings("deprecation")
	private Response processRequest(Request request) {
		
		try {
			String action = request.action;
			
			if (action.equals(Protocol.ESTIMATE))
				return Response.create(remoteService.estimate(request.recommend_param, request.queryids));
			
			else if (action.equals(RECOMMEND))
				return Response.create(remoteService.recommend(request.recommend_param, request.max_recommend));
			
			else if (action.equals(RECOMMEND_USER))
				return Response.create(remoteService.recommend(request.userid, request.max_recommend));
			
			else if (action.equals(RECOMMENDLET))
				return Response.create(remoteService.recommend(request.host, request.port, request.reg_name, 
						request.external_userid, request.external_itemid, request.max_recommend, request.rating));

			else if (action.equals(UPDATE_RATING))
				return Response.create(remoteService.updateRating(request.rating_vector));
			
			else if (action.equals(DELETE_RATING))
				return Response.create(remoteService.deleteRating(request.rating_vector));

			else if (action.equals(GET_USERIDS))
				return Response.createIdFetcher(new MemFetcher<Integer>(remoteService.getUserIds(), true));

			else if (action.equals(GET_USER_RATING))
				return Response.create(remoteService.getUserRating(request.userid));
			
			else if (action.equals(GET_USER_RATINGS))
				return Response.createRatingVectorFetcher(remoteService.getUserRatings());
			
			else if (action.equals(DELETE_USER_RATING))
				return Response.create(remoteService.deleteUserRating(request.userid));

			else if (action.equals(GET_USER_PROFILE))
				return Response.create(remoteService.getUserProfile(request.userid));

			else if (action.equals(GET_USER_PROFILE_BY_EXTERNAL))
				return Response.create(remoteService.getUserProfileByExternal(request.external_userid));

			else if (action.equals(GET_USER_PROFILES))
				return Response.createProfileFetcher(remoteService.getUserProfiles());
			
			else if (action.equals(GET_USER_ATTRIBUTE_LIST))
				return Response.create(remoteService.getUserAttributeList());
			
			else if (action.equals(UPDATE_USER_PROFILE))
				return Response.create(remoteService.updateUserProfile(request.profile));

			else if (action.equals(DELETE_USER_PROFILE))
				return Response.create(remoteService.deleteUserProfile(request.userid));

			else if (action.equals(GET_USER_EXTERNAL_RECORD))
				return Response.create(remoteService.getUserExternalRecord(request.userid));

			else if (action.equals(GET_ITEMIDS))
				return Response.createIdFetcher(new MemFetcher<Integer>(remoteService.getItemIds(), true));
			
			else if (action.equals(GET_ITEM_RATING))
				return Response.create(remoteService.getItemRating(request.itemid));
			
			else if (action.equals(GET_ITEM_RATINGS))
				return Response.createRatingVectorFetcher(remoteService.getItemRatings());
			
			else if (action.equals(DELETE_ITEM_RATING))
				return Response.create(remoteService.deleteItemRating(request.itemid));
			
			else if (action.equals(GET_ITEM_PROFILE))
				return Response.create(remoteService.getItemProfile(request.itemid));
			
			else if (action.equals(GET_ITEM_PROFILE_BY_EXTERNAL))
				return Response.create(remoteService.getItemProfileByExternal(request.external_itemid));

			else if (action.equals(GET_ITEM_PROFILES))
				return Response.createProfileFetcher(remoteService.getItemProfiles());
			
			else if (action.equals(GET_ITEM_ATTRIBUTE_LIST))
				return Response.create(remoteService.getItemAttributeList());

			else if (action.equals(UPDATE_ITEM_PROFILE))
				return Response.create(remoteService.updateItemProfile(request.profile));
			
			else if (action.equals(DELETE_ITEM_PROFILE))
				return Response.create(remoteService.deleteUserProfile(request.itemid));
			
			else if (action.equals(GET_ITEM_EXTERNAL_RECORD))
				return Response.create(remoteService.getItemExternalRecord(request.itemid));

			else if (action.equals(GET_NOMINAL))
				return Response.create(remoteService.getNominal(request.unit, request.attribute));

			else if (action.equals(UPDATE_NOMINAL))
				return Response.create(remoteService.updateNominal(request.unit, request.attribute, request.nominal));
			
			else if (action.equals(DELETE_NOMINAL))
				return Response.create(remoteService.deleteNominal(request.unit, request.attribute));

			else if (action.equals(GET_EXTERNAL_RECORD))
				return Response.create(remoteService.getExternalRecord(request.internal_record));

			else if (action.equals(UPDATE_EXTERNAL_RECORD))
				return Response.create(remoteService.updateExternalRecord(request.internal_record, request.external_record));

			else if (action.equals(DELETE_EXTERNAL_RECORD))
				return Response.create(remoteService.deleteExternalRecord(request.internal_record));

			else if (action.equals(VALIDATE_ACCOUNT))
				return Response.create(validateAccount(
						request.account_name, request.account_password, request.account_privileges));
			
			else if (action.equals(GET_SERVER_CONFIG))
				return Response.create(remoteService.getServerConfig());
			
			else if (action.equals(GET_SESSION_ATTRIBUTE))
				return Response.create(userSession.get(request.attribute).toString());
			
			else if (action.equals(GET_SNAPSHOT))
				return Response.create(remoteService.getSnapshot());
			
			else if (action.equals(GET_EVALUATOR)) {
				Evaluator remoteEvaluator = remoteService.getEvaluator(request.evaluatorName, request.account_name, request.account_password);
				if (remoteEvaluator == null)
					return null;
				DelegatorEvaluator evaluator = new DelegatorEvaluator(this, remoteEvaluator);
				evaluator.stimulate();
				return Response.create((Evaluator)evaluator.getExportedStub());
			}
			
			else if (action.equals(GET_EVALUATOR_NAMES))
				return Response.create(remoteService.getEvaluatorNames());
			
			else if (action.equals(GET_ALG)) {
				Alg remoteAlg = remoteService.getAlg(request.algName);
				if (remoteAlg == null)
					return null;
				//This is work-around solution because it is necessary to wrap remote algorithm one more time at listener like delegator-evaluator but this is so complicated.
				return Response.create(remoteAlg);
			}
			
			else if (action.equals(GET_ALG_NAMES))
				return Response.create(remoteService.getAlgNames());
			
			else if (action.equals(GET_ALG_DESCS))
				return Response.create(remoteService.getAlgDescs());
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			LogUtil.error("Delegator fail to process request, error is " + e.getMessage());
		}
		
		return null;
	}

	
	@Override
	public boolean validateAccount(String account, String password, int privileges) {
		
		try {
			return remoteServer.validateAccount(account, password, privileges);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}


	@Override
	public boolean validateAdminAccount(String account, String password) {
		boolean validated = validateAccount(account, password, DataConfig.ACCOUNT_ADMIN_PRIVILEGE);
		if (validated)
			return true;
		else if (!account.equals("admin"))
			return false;
		else {
			//Checking Hudup properties.
			String pwd = Util.getHudupProperty(account);
			if (pwd == null)
				return false;
			else
				return pwd.equals(password);
		}
	}

	
}



/**
 * This class is a wrapper of evaluator used for listener.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class DelegatorEvaluator extends EvaluatorWrapperExt {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Remote server.
	 */
	protected PowerServer remoteServer = null;

	
    /**
     * Socket server is often listener.
     */
    protected SocketServer socketServer = null;
    
    
	/**
	 * Constructor with evaluator and socket server.
	 * @param delegator internal delegator.
	 * @param remoteEvaluator remote evaluator.
	 */
	public DelegatorEvaluator(Delegator delegator, Evaluator remoteEvaluator) {
		super(null, -1);
		
		try {
			this.remoteServer = delegator.remoteServer;
			this.socketServer = delegator.socketServer;
			this.remoteEvaluator = remoteEvaluator;
			
			export(delegator.socketServer.getConfig().getAsInt(ListenerConfig.EXPORT_PORT_FIELD));
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		//Delegated evaluator may not use timer because of suppose that there are limited clients connecting to listeners
		//when there can be many different listeners. It is complicated with timer.
		this.purgeTimer = EvaluatorAbstract.createPurgeListenersTimer(listenerList);
	}
	
	
	@Override
	public synchronized boolean remoteStart(List<Alg> algList, DatasetPoolExchanged pool, Timestamp timestamp, Serializable parameter) throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		stimulate();
		
		boolean flag = socketServer.getFlag();
		
		if (flag)
			return remoteEvaluator.remoteStart(algList, pool, timestamp, parameter);
		else if (socketServer.isRunning())
			return remoteEvaluator.remoteStart(algList, pool, timestamp, parameter);
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public boolean remoteStart(List<String> algNameList, DatasetPoolExchanged pool, ClassProcessor cp, DataConfig config, Timestamp timestamp, Serializable parameter)
			throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		stimulate();
		
		boolean flag = socketServer.getFlag();
		
		if (flag)
			return remoteEvaluator.remoteStart(algNameList, pool, cp, config, timestamp, parameter);
		else if (socketServer.isRunning())
			return remoteEvaluator.remoteStart(algNameList, pool, cp, config, timestamp, parameter);
		else
			throw new RemoteException("Socket server is not running");
	}


	@Override
	public synchronized boolean remoteStart(Serializable... parameters) throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		stimulate();
		
		boolean flag = socketServer.getFlag();
		
		if (flag)
			return remoteEvaluator.remoteStart(parameters);
		else if (socketServer.isRunning())
			return remoteEvaluator.remoteStart(parameters);
		else
			throw new RemoteException("Socket server is not running");
	}


	@Override
	public synchronized boolean remotePause() throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		
		boolean flag = socketServer.getFlag();

		if (flag)
			return remoteEvaluator.remotePause();
		else if (socketServer.isRunning())
			return remoteEvaluator.remotePause();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public synchronized boolean remoteResume() throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		
		boolean flag = socketServer.getFlag();

		if (flag)
			return remoteEvaluator.remoteResume();
		else if (socketServer.isRunning())
			return remoteEvaluator.remoteResume();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public synchronized boolean remoteStop() throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		
		boolean flag = socketServer.getFlag();

		if (flag)
			return remoteEvaluator.remoteStop();
		else if (socketServer.isRunning())
			return remoteEvaluator.remoteStop();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public boolean remoteForceStop() throws RemoteException {
		TaskQueue.waitForTaskQueue(taskMap);
		
		boolean flag = socketServer.getFlag();
		
		if (flag)
			return remoteEvaluator.remoteForceStop();
		else if (socketServer.isRunning())
			return remoteEvaluator.remoteForceStop();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		if (exportedStub == null) {
			exportedStub = (Evaluator)NetUtil.RegistryRemote.export(this, serverPort);
			if (exportedStub != null)
				LogUtil.info("Delegator evaluator served at port " + serverPort);
			else
				LogUtil.info("Delegator evaluator failed to export");

			try {
				remoteEvaluator.addPluginChangedListener(this);
				remoteEvaluator.addEvaluatorListener(this);
				remoteEvaluator.addEvaluateListener(this);
				remoteEvaluator.addEvaluateProgressListener(this);
				remoteEvaluator.addSetupAlgListener(this);
				remoteEvaluator.addElapsedTimeListener(this);
			} catch (Exception e) {LogUtil.trace(e);}
			
			socketServer.addRunner(this);
			remoteServer.incRequest();
		}
		
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		if (exportedStub != null) {
			if (!remoteEvaluator.containsAgent())
				this.remoteStop();

			try {
				remoteEvaluator.removePluginChangedListener(this);
				remoteEvaluator.removeEvaluatorListener(this);
				remoteEvaluator.removeEvaluateListener(this);
				remoteEvaluator.removeEvaluateProgressListener(this);
				remoteEvaluator.removeSetupAlgListener(this);
				remoteEvaluator.removeElapsedTimeListener(this);
				if (!remoteEvaluator.isAgent())
					remoteEvaluator.unexport();
			} catch (Exception e) {LogUtil.trace(e);}
			
			socketServer.removeRunner(this);
			boolean ret = NetUtil.RegistryRemote.unexport(this);
			exportedStub = null;
			remoteServer.decRequest();
			if (ret)
				LogUtil.info("Delegator evaluator unexported successfully");
			else
				LogUtil.info("Delegator evaluator unexported failedly");
		}
	}


}
