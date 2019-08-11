package net.hudup.listener;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.EventListener;
import java.util.List;
import java.util.Map;

import javax.swing.event.EventListenerList;

import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.client.ServerConfig;
import net.hudup.core.client.Service;
import net.hudup.core.data.DatasetPool;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.EvaluatorProgressEvent;
import net.hudup.core.evaluate.EvaluatorProgressListener;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
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
	 * In fact, the service {@link #service} of this server processed the request.
	 */
	protected PowerServer server = null;

	
	/**
	 * The service of the binding server {@link #server} actually processes request from the delegator.
	 */
	protected Service service = null;
	
	
	/**
	 * Constructor with specified socket and power server.
	 * @param server power server binds with this delegator.
	 * @param socket specified Java socket for receiving (reading) requests from client and sending back (writing) responses to client via input / output streams.
	 * @param socketConfig socket configuration.
	 * The delegator dispatches request to this server.
	 * The result of recommendation process from server, represented by {@link Response}, is sent back to users/applications by delegator.
	 * In fact, the service {@link #service} of this server processed the request.
	 */
	public Delegator(PowerServer server, Socket socket, ServerConfig socketConfig) {
		super(socket, socketConfig);
		
		this.server = server;
		
		try {
			this.service = server.getService();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			this.service = null;
			
			logger.error("Delegator fail to be constructed in constructor method, causes error " + e.getMessage());
		}
	}
	
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try {
			server.incRequest();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to increase server request, causes error " + e.getMessage());
		}
		
		super.run();
		
		try {
			server.decRequest();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Delegator fail to decrease server request, causes error " + e.getMessage());
		}
	}
	
	
	@Override
	protected Request parseRequest(String requestText) {
		Request request = null;
		try {
			String triple = requestText.substring(0, Math.min(3, requestText.length()));
			triple = triple.toUpperCase();
			
			if (triple.equals("GET")) {
				int fileType = HttpUtil.getFileType(requestText);
				
				if (fileType == UNKNOWN_FILE_TYPE) {
					Map<String, String> params = HttpUtil.getParameters(requestText);
					String action = HttpUtil.getAction(requestText);
					if (action != null)
						params.put("action", action);
					else
						params.put("action", Protocol.READ_FILE);
					
					request = Request.parse(params);
					request.protocol = HTTP_PROTOCOL;
					request.file_type = fileType;
					
					String path = HttpUtil.getPath(requestText);
					if (path != null) {
						UriAdapter adapter = new UriAdapter();
						request.file_path = adapter.newPath(path).toString();
						adapter.close();
					}
				}
				else {
					request = new Request();
					request.protocol = HTTP_PROTOCOL;
					request.action = Protocol.READ_FILE;
					request.file_type = fileType;
					
					String path = HttpUtil.getPath(requestText);
					if (path != null) {
						UriAdapter adapter = new UriAdapter();
						request.file_path = adapter.newPath(path).toString();
						adapter.close();
					}
				}
			}
			else {
				request = Request.parse(requestText);
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			request = null;
			
			logger.error("Delegator fail to parse request, causes error " + e.getMessage());
		}
		
		return request;
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
			logger.error("Delegator fail to handle request, error is " + e.getMessage());
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
				return Response.create(service.estimate(request.recommend_param, request.queryids));
			
			else if (action.equals(RECOMMEND))
				return Response.create(service.recommend(request.recommend_param, request.max_recommend));
			
			else if (action.equals(RECOMMEND_USER))
				return Response.create(service.recommend(request.userid, request.max_recommend));
			
			else if (action.equals(RECOMMENDLET))
				return Response.create(service.recommend(request.host, request.port, request.reg_name, 
						request.external_userid, request.external_itemid, request.max_recommend, request.rating));

			else if (action.equals(UPDATE_RATING))
				return Response.create(service.updateRating(request.rating_vector));
			
			else if (action.equals(DELETE_RATING))
				return Response.create(service.deleteRating(request.rating_vector));

			else if (action.equals(GET_USERIDS))
				return Response.createIdFetcher(new MemFetcher<Integer>(service.getUserIds(), true));

			else if (action.equals(GET_USER_RATING))
				return Response.create(service.getUserRating(request.userid));
			
			else if (action.equals(GET_USER_RATINGS))
				return Response.createRatingVectorFetcher(service.getUserRatings());
			
			else if (action.equals(DELETE_USER_RATING))
				return Response.create(service.deleteUserRating(request.userid));

			else if (action.equals(GET_USER_PROFILE))
				return Response.create(service.getUserProfile(request.userid));

			else if (action.equals(GET_USER_PROFILE_BY_EXTERNAL))
				return Response.create(service.getUserProfileByExternal(request.external_userid));

			else if (action.equals(GET_USER_PROFILES))
				return Response.createProfileFetcher(service.getUserProfiles());
			
			else if (action.equals(GET_USER_ATTRIBUTE_LIST))
				return Response.create(service.getUserAttributeList());
			
			else if (action.equals(UPDATE_USER_PROFILE))
				return Response.create(service.updateUserProfile(request.profile));

			else if (action.equals(DELETE_USER_PROFILE))
				return Response.create(service.deleteUserProfile(request.userid));

			else if (action.equals(GET_USER_EXTERNAL_RECORD))
				return Response.create(service.getUserExternalRecord(request.userid));

			else if (action.equals(GET_ITEMIDS))
				return Response.createIdFetcher(new MemFetcher<Integer>(service.getItemIds(), true));
			
			else if (action.equals(GET_ITEM_RATING))
				return Response.create(service.getItemRating(request.itemid));
			
			else if (action.equals(GET_ITEM_RATINGS))
				return Response.createRatingVectorFetcher(service.getItemRatings());
			
			else if (action.equals(DELETE_ITEM_RATING))
				return Response.create(service.deleteItemRating(request.itemid));
			
			else if (action.equals(GET_ITEM_PROFILE))
				return Response.create(service.getItemProfile(request.itemid));
			
			else if (action.equals(GET_ITEM_PROFILE_BY_EXTERNAL))
				return Response.create(service.getItemProfileByExternal(request.external_itemid));

			else if (action.equals(GET_ITEM_PROFILES))
				return Response.createProfileFetcher(service.getItemProfiles());
			
			else if (action.equals(GET_ITEM_ATTRIBUTE_LIST))
				return Response.create(service.getItemAttributeList());

			else if (action.equals(UPDATE_ITEM_PROFILE))
				return Response.create(service.updateItemProfile(request.profile));
			
			else if (action.equals(DELETE_ITEM_PROFILE))
				return Response.create(service.deleteUserProfile(request.itemid));
			
			else if (action.equals(GET_ITEM_EXTERNAL_RECORD))
				return Response.create(service.getItemExternalRecord(request.itemid));

			else if (action.equals(GET_NOMINAL))
				return Response.create(service.getNominal(request.unit, request.attribute));

			else if (action.equals(UPDATE_NOMINAL))
				return Response.create(service.updateNominal(request.unit, request.attribute, request.nominal));
			
			else if (action.equals(DELETE_NOMINAL))
				return Response.create(service.deleteNominal(request.unit, request.attribute));

			else if (action.equals(GET_EXTERNAL_RECORD))
				return Response.create(service.getExternalRecord(request.internal_record));

			else if (action.equals(UPDATE_EXTERNAL_RECORD))
				return Response.create(service.updateExternalRecord(request.internal_record, request.external_record));

			else if (action.equals(DELETE_EXTERNAL_RECORD))
				return Response.create(service.deleteExternalRecord(request.internal_record));

			else if (action.equals(VALIDATE_ACCOUNT))
				return Response.create(validateAccount(
						request.account_name, request.account_password, request.account_privileges));
			
			else if (action.equals(GET_SERVER_CONFIG))
				return Response.create(service.getServerConfig());
			
			else if (action.equals(GET_SESSION_ATTRIBUTE))
				return Response.create(userSession.get(request.attribute).toString());
			
			else if (action.equals(GET_SNAPSHOT))
				return Response.create(service.getSnapshot());
			
			else if (action.equals(GET_EVALUATOR)) {
				Evaluator remoteEvaluator = service.getEvaluator(request.evaluatorName);
				if (remoteEvaluator == null)
					return null;
				DelegatorEvaluator evaluator = new DelegatorEvaluator(this, remoteEvaluator);
				return Response.create(evaluator.stub);
			}
			
			else if (action.equals(GET_EVALUATOR_NAMES))
				return Response.create(service.getEvaluatorNames());
		}
		catch (Throwable e) {
			e.printStackTrace();
			logger.error("Delegator fail to process request, error is " + e.getMessage());
		}
		
		return null;
	}

	
	@Override
	protected boolean validateAccount(String account, String password, int privileges) {
		
		try {
			return server.validateAccount(account, password, privileges);
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
		
		return false;
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
	 * Holding a list of {@link EventListener} (s)
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
	 * Internal delegator.
	 */
	protected Delegator delegator = null;

	
	/**
	 * Internal evaluator.
	 * 
	 */
    protected Evaluator evaluator = null;

    
	/**
	 * Exported flag.
	 */
	protected Boolean remoteExported = false;
	
	
	/**
	 * Stub object as evaluator.
	 */
	protected Evaluator stub = null;
	
	
    /**
	 * Default constructor.
	 */
	public DelegatorEvaluator(Delegator delegator, Evaluator evaluator) {
		try {
			this.delegator = delegator;
			this.evaluator = evaluator;
			
			remoteExport(delegator.socketConfig.getAsInt(ListenerConfig.EXPORT_PORT_FIELD));
			
			this.delegator.addRunner(this);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	@Override
	public String getName() throws RemoteException {
		// TODO Auto-generated method stub
		return evaluator.getName();
	}


	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		// TODO Auto-generated method stub
		return evaluator.acceptAlg(alg);
	}


	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
		// TODO Auto-generated method stub
		return evaluator.defaultMetrics();
	}


	@Override
	public String getMainUnit() throws RemoteException {
		return evaluator.getMainUnit();
	}
	
	
	@Override
	public Metrics getResult() throws RemoteException {
		return evaluator.getResult();
	}

	
	@Override
	public List<Metric> getMetricList() throws RemoteException {
		return evaluator.getMetricList();
	}

	
	@Override
	public void setMetricList(List<Metric> metricList) throws RemoteException {
		evaluator.setMetricList(metricList);
	}
	
	
	@Override
	public RegisterTable extractAlgFromPluginStorage() throws RemoteException {
		return evaluator.extractAlgFromPluginStorage();
	}
	
	
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		// TODO Auto-generated method stub
		return evaluator.getPluginStorage();
	}


	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		return evaluator.getConfig();
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
	public void remoteStart(Serializable... parameters) throws RemoteException {
		// TODO Auto-generated method stub
		evaluator.remoteStart(parameters);
	}


	@Override
	public void remoteStart(List<Alg> algList, DatasetPool pool, Serializable parameter) throws RemoteException {
		// TODO Auto-generated method stub
		evaluator.remoteStart(algList, pool, parameter);
	}

	
	@Override
	public void remotePause() throws RemoteException {
		// TODO Auto-generated method stub
		evaluator.remotePause();
	}

	
	@Override
	public void remoteResume() throws RemoteException {
		// TODO Auto-generated method stub
		evaluator.remoteResume();
	}

	
	@Override
	public void remoteStop() throws RemoteException {
		// TODO Auto-generated method stub
		evaluator.remoteStop();
	}

	
	@Override
	public void remoteForceStop() throws RemoteException {
		// TODO Auto-generated method stub
		evaluator.remoteForceStop();
	}

	
	@Override
	public boolean remoteIsStarted() throws RemoteException {
		// TODO Auto-generated method stub
		return evaluator.remoteIsStarted();
	}

	
	@Override
	public boolean remoteIsPaused() throws RemoteException {
		// TODO Auto-generated method stub
		return evaluator.remoteIsPaused();
	}

	
	@Override
	public boolean remoteIsRunning() throws RemoteException {
		// TODO Auto-generated method stub
		return evaluator.remoteIsRunning();
	}


	@Override
	public synchronized void remoteExport(int serverPort) throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (remoteExported) {
			if (!remoteExported) {
				stub = (Evaluator)NetUtil.RegistryRemote.export(this, serverPort);

				evaluator.addEvaluatorListener(this);
				evaluator.addProgressListener(this);
				evaluator.addSetupAlgListener(this);
				
				delegator.server.incRequest();
				remoteExported = true;
			}
		}
	}


	@Override
	public synchronized void remoteUnexport() throws RemoteException {
		// TODO Auto-generated method stub
		synchronized (remoteExported) {
			if (remoteExported) {
				delegator.stop();
				//new SocketWrapper("localhost", delegator.socketConfig.getServerPort()).sendQuitRequest();

				evaluator.removeEvaluatorListener(this);
				evaluator.removeProgressListener(this);
				evaluator.removeSetupAlgListener(this);
				evaluator.remoteUnexport();
				
				delegator.server.decRequest();
				NetUtil.RegistryRemote.unexport(this);
				delegator.removeRunner(this);
				stub = null;
				remoteExported = false;
			}
		}
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			remoteUnexport();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}
			

}
