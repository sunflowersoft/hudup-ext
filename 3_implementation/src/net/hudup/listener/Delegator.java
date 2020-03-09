/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
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
import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.evaluate.EvaluateInfo;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.EvaluatorProgressEvent;
import net.hudup.core.evaluate.EvaluatorProgressListener;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;
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
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.remoteService = null;
			
			LogUtil.error("Delegator fail to be constructed in constructor method, causes error " + e.getMessage());
		}
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			remoteServer.incRequest();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.error("Delegator fail to increase server request, causes error " + e.getMessage());
		}
		
		super.run();
		
		try {
			remoteServer.decRequest();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			e.printStackTrace();
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
		// TODO Auto-generated method stub
		
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
				Evaluator remoteEvaluator = remoteService.getEvaluator(request.evaluatorName);
				if (remoteEvaluator == null)
					return null;
				DelegatorEvaluator evaluator = new DelegatorEvaluator(this, remoteEvaluator);
				return Response.create(evaluator.exportedStub);
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
			e.printStackTrace();
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
			e.printStackTrace();
		}
		
		return false;
	}


	@Override
	public boolean validateAdminAccount(String account, String password) {
		// TODO Auto-generated method stub
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
class DelegatorEvaluator implements Evaluator, EvaluatorListener, EvaluatorProgressListener {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Holding a list of {@link EventListener} (s)
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
	 * Remote server.
	 */
	protected PowerServer remoteServer = null;

	
    /**
	 * Remote evaluator.
	 * 
	 */
    protected Evaluator remoteEvaluator = null;

    
    /**
     * Socket server is often listener.
     */
    protected SocketServer socketServer = null;
    
    
	/**
	 * Stub object of this evaluator which is serialized to client.
	 */
	protected Evaluator exportedStub = null;
	
	
	/**
	 * Constructor with evaluator and socket server.
	 * @param delegator internal delegator.
	 * @param remoteEvaluator remote evaluator.
	 */
	public DelegatorEvaluator(Delegator delegator, Evaluator remoteEvaluator) {
		try {
			this.remoteServer = delegator.remoteServer;
			this.socketServer = delegator.socketServer;
			this.remoteEvaluator = remoteEvaluator;
			
			export(delegator.socketServer.getConfig().getAsInt(ListenerConfig.EXPORT_PORT_FIELD));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getName();
	}


	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.acceptAlg(alg);
	}


	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.defaultMetrics();
	}


	@Override
	public String getMainUnit() throws RemoteException {
		return remoteEvaluator.getMainUnit();
	}
	
	
	@Override
	public Metrics getResult() throws RemoteException {
		return remoteEvaluator.getResult();
	}

	
	@Override
	public EvaluateInfo getOtherResult() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getOtherResult();
	}


	@Override
	public List<Metric> getMetricList() throws RemoteException {
		return remoteEvaluator.getMetricList();
	}

	
	@Override
	public void setMetricList(List<Metric> metricList) throws RemoteException {
		remoteEvaluator.setMetricList(metricList);
	}
	
	
	@Deprecated
	@Override
	public RegisterTable extractAlgFromPluginStorage0() throws RemoteException {
		return remoteEvaluator.extractAlgFromPluginStorage0();
	}
	
	
	@Override
	public void clearDelayUnsetupAlgs() throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.clearDelayUnsetupAlgs();
	}


//	@Override
//	public List<String> getAlgNames() throws RemoteException {
//		// TODO Auto-generated method stub
//		return remoteEvaluator.getAlgNames();
//	}
//
//
//	@NextUpdate
//	@Deprecated
//	@Override
//	public DatasetPool getDatasetPool() throws RemoteException {
//		// TODO Auto-generated method stub
//		return remoteEvaluator.getDatasetPool();
//	}

	
	@Deprecated
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.getPluginStorage();
	}


	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		return remoteEvaluator.getConfig();
	}
	
	
	@Override
	public void addEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluatorListener.class, listener);
		}
    }

    
	@Override
    public void removeEvaluatorListener(EvaluatorListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluatorListener.class, listener);
		}
    }
	
    
    /**
     * Return a {@link EvaluatorListener} list for this evaluator.
     * 
     * @return array of {@link EvaluatorListener} for this evaluator.
     * 
     */
    protected EvaluatorListener[] getEvaluatorListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluatorListener.class);
		}
    }

    
    /**
     * Firing (issuing) an event from this evaluator to all listeners. 
     * 
     * @param evt event from this evaluator.
     */
    protected void fireEvaluatorEvent(EvaluatorEvent evt) {
    	
		EvaluatorListener[] listeners = getEvaluatorListeners();
		
		for (EvaluatorListener listener : listeners) {
			try {
				listener.receivedEvaluation(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
    }

    
    @Override
	public void receivedEvaluation(EvaluatorEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
    	fireEvaluatorEvent(evt);
	}


	@Override
	public void addProgressListener(EvaluatorProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluatorProgressListener.class, listener);
		}
    }

    
    @Override
    public void removeProgressListener(EvaluatorProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluatorProgressListener.class, listener);
		}
    }
	
    
    /**
     * Getting an array of evaluation progress listener.
     * @return array of {@link ProgressListener} (s).
     */
    protected EvaluatorProgressListener[] getProgressListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluatorProgressListener.class);
		}
    }
    
    
    /**
     * Firing {@link ProgressEvent}.
     * @param evt the specified for evaluation progress.
     */
    protected void fireProgressEvent(EvaluatorProgressEvent evt) {
    	EvaluatorProgressListener[] listeners = getProgressListeners();
		
		for (EvaluatorProgressListener listener : listeners) {
			try {
				listener.receivedProgress(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
    }


    @Override
	public void receivedProgress(EvaluatorProgressEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		fireProgressEvent(evt);
	}


	@Override
	public void addSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(SetupAlgListener.class, listener);
		}
    }

    
    @Override
    public void removeSetupAlgListener(SetupAlgListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
    }
	
    
    /**
     * Getting an array of setup algorithm listeners.
     * @return array of setup algorithm listeners.
     */
    protected SetupAlgListener[] getSetupAlgListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(SetupAlgListener.class);
		}
    }
    
    
    /**
     * Firing setup algorithm event.
     * @param evt the specified for setup algorithm event.
     */
    protected void fireSetupAlgEvent(SetupAlgEvent evt) {
    	SetupAlgListener[] listeners = getSetupAlgListeners();
		
		for (SetupAlgListener listener : listeners) {
			try {
				listener.receivedSetup(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	
    }

    
	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		// TODO Auto-generated method stub
		fireSetupAlgEvent(evt);
	}


	@Override
	public synchronized void remoteStart(Serializable... parameters) throws RemoteException {
		// TODO Auto-generated method stub
		boolean flag = socketServer.getFlag();
		
		if (flag)
			remoteEvaluator.remoteStart(parameters);
		else if (socketServer.isRunning())
			remoteEvaluator.remoteStart(parameters);
		else
			throw new RemoteException("Socket server is not running");
	}


	@Override
	public synchronized void remoteStart(List<Alg> algList, DatasetPool pool, Serializable parameter) throws RemoteException {
		// TODO Auto-generated method stub
		boolean flag = socketServer.getFlag();
		
		if (flag)
			remoteEvaluator.remoteStart(algList, pool, parameter);
		else if (socketServer.isRunning())
			remoteEvaluator.remoteStart(algList, pool, parameter);
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public synchronized void remotePause() throws RemoteException {
		// TODO Auto-generated method stub
		boolean flag = socketServer.getFlag();

		if (flag)
			remoteEvaluator.remotePause();
		else if (socketServer.isRunning())
			remoteEvaluator.remotePause();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public synchronized void remoteResume() throws RemoteException {
		// TODO Auto-generated method stub
		boolean flag = socketServer.getFlag();

		if (flag)
			remoteEvaluator.remoteResume();
		else if (socketServer.isRunning())
			remoteEvaluator.remoteResume();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public synchronized void remoteStop() throws RemoteException {
		// TODO Auto-generated method stub
		boolean flag = socketServer.getFlag();

		if (flag)
			remoteEvaluator.remoteStop();
		else if (socketServer.isRunning())
			remoteEvaluator.remoteStop();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public void remoteForceStop() throws RemoteException {
		// TODO Auto-generated method stub
		boolean flag = socketServer.getFlag();
		
		if (flag)
			remoteEvaluator.remoteForceStop();
		else if (socketServer.isRunning())
			remoteEvaluator.remoteForceStop();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public boolean remoteIsStarted() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteIsStarted();
	}

	
	@Override
	public boolean remoteIsPaused() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteIsPaused();
	}

	
	@Override
	public boolean remoteIsRunning() throws RemoteException {
		// TODO Auto-generated method stub
		return remoteEvaluator.remoteIsRunning();
	}


	@Override
	public synchronized Remote export(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
		if (exportedStub == null) {
			exportedStub = (Evaluator)NetUtil.RegistryRemote.export(this, serverPort);
			if (exportedStub != null)
				LogUtil.info("Delegator evaluator served at port " + serverPort);
			else
				LogUtil.info("Delegator evaluator failed to export");

			try {
				remoteEvaluator.addEvaluatorListener(this);
				remoteEvaluator.addProgressListener(this);
				remoteEvaluator.addSetupAlgListener(this);
			} catch (Exception e) {e.printStackTrace();}
			
			socketServer.addRunner(this);
			remoteServer.incRequest();
		}
		
		return exportedStub;
	}


	@Override
	public synchronized void unexport() throws RemoteException {
		// TODO Auto-generated method stub
		if (exportedStub != null) {
			this.remoteStop(); //It is possible to stop this evaluator because its is delegated evaluator.
			
			try {
				remoteEvaluator.removeEvaluatorListener(this);
				remoteEvaluator.removeProgressListener(this);
				remoteEvaluator.removeSetupAlgListener(this);
				if (!remoteEvaluator.isAgent())
					remoteEvaluator.unexport();
			} catch (Exception e) {e.printStackTrace();}
			
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


	@Override
	public void setEvaluateStorePath(String evStorePath) throws RemoteException {
		// TODO Auto-generated method stub
		remoteEvaluator.setEvaluateStorePath(evStorePath);
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	public boolean isAgent() throws RemoteException {
		// TODO Auto-generated method stub
		return false;
	}


	@Override
	public void setAgent(boolean agent) throws RemoteException {
		// TODO Auto-generated method stub
		LogUtil.info("Evaluator-Delegator wrapper not support setting agent");
	}


	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			unexport();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


	@Override
	public String toString() {
		// TODO Auto-generated method stub
    	String evaluatorName = "No name";
		try {
			evaluatorName = getName();
		}
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return DSUtil.shortenVerbalName(evaluatorName);
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
			

//	@Override
//	public Object ping(Object o) throws RemoteException {
//		// TODO Auto-generated method stub
//		return "Ping sucessful: " + o.toString() + " " + o.toString();
//	}
			

}
