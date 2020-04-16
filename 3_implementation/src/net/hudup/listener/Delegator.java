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
import java.util.EventListener;
import java.util.List;

import javax.swing.event.EventListenerList;

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorageWrapper;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2List;
import net.hudup.core.alg.SetupAlgEvent;
import net.hudup.core.alg.SetupAlgListener;
import net.hudup.core.client.ClassProcessor;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Protocol;
import net.hudup.core.client.Request;
import net.hudup.core.client.Response;
import net.hudup.core.client.Service;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DatasetPoolExchanged;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.evaluate.EvaluateEvent;
import net.hudup.core.evaluate.EvaluateInfo;
import net.hudup.core.evaluate.EvaluateListener;
import net.hudup.core.evaluate.EvaluateProgressEvent;
import net.hudup.core.evaluate.EvaluateProgressListener;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorConfig;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.NoneWrapperMetricList;
import net.hudup.core.logistic.CounterElapsedTimeEvent;
import net.hudup.core.logistic.CounterElapsedTimeListener;
import net.hudup.core.logistic.DSUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.Timestamp;
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
			LogUtil.trace(e);
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
			LogUtil.trace(e);
			LogUtil.error("Delegator fail to increase server request, causes error " + e.getMessage());
		}
		
		super.run();
		
		try {
			remoteServer.decRequest();
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
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
				Evaluator remoteEvaluator = remoteService.getEvaluator(request.evaluatorName, request.account_name, request.account_password);
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
class DelegatorEvaluator implements Evaluator, EvaluatorListener, EvaluateListener, EvaluateProgressListener, CounterElapsedTimeListener {

	
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
			LogUtil.trace(e);
		}
	}
	
	
	@Override
	public String getName() throws RemoteException {
		return remoteEvaluator.getName();
	}


	@Override
	public boolean acceptAlg(Alg alg) throws RemoteException {
		return remoteEvaluator.acceptAlg(alg);
	}


	@Override
	public NoneWrapperMetricList defaultMetrics() throws RemoteException {
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
		return remoteEvaluator.getOtherResult();
	}


	@Override
	public List<String> getMetricNameList() throws RemoteException {
		return remoteEvaluator.getMetricNameList();
	}

	
	@Override
	public void setMetricNameList(List<String> metricNameList) throws RemoteException {
		remoteEvaluator.setMetricNameList(metricNameList);
	}
	
	
	@Deprecated
	@Override
	public RegisterTable extractNormalAlgFromPluginStorage0() throws RemoteException {
		return remoteEvaluator.extractNormalAlgFromPluginStorage0();
	}
	
	
//	@Override
//	public void clearDelayUnsetupAlgs() throws RemoteException {
//		remoteEvaluator.clearDelayUnsetupAlgs();
//	}
//
//
//	@Override
//	public void clearResult(Timestamp timestamp) throws RemoteException {
//		remoteEvaluator.clearResult(timestamp);
//	}


	@NextUpdate
	@Override
	public DatasetPoolExchanged getDatasetPool() throws RemoteException {
		return remoteEvaluator.getDatasetPool();
	}

	
	@Deprecated
	@Override
	public PluginStorageWrapper getPluginStorage() throws RemoteException {
		return remoteEvaluator.getPluginStorage();
	}


	@Override
	public List<String> getPluginAlgNames(Class<? extends Alg> algClass) throws RemoteException {
		return remoteEvaluator.getPluginAlgNames(algClass);
	}


	@Override
	public AlgDesc2List getPluginAlgDescs(Class<? extends Alg> algClass) throws RemoteException {
		return remoteEvaluator.getPluginAlgDescs(algClass);
	}


	@Override
	public Alg getPluginAlg(Class<? extends Alg> algClass, String algName, boolean remote) throws RemoteException {
		return remoteEvaluator.getPluginAlg(algClass, algName, remote);
	}


	@Override
	public Alg getEvaluatedAlg(String algName, boolean remote) throws RemoteException {
		return remoteEvaluator.getEvaluatedAlg(algName, remote);
	}


	@Override
	public EvaluatorConfig getConfig() throws RemoteException {
		return remoteEvaluator.getConfig();
	}
	
	
	@Override
	public void setConfig(EvaluatorConfig config) throws RemoteException {
		remoteEvaluator.setConfig(config);
	}


	@Override
	public boolean isWrapper() throws RemoteException {
		return true;
	}


	@Override
	public void pluginChanged(PluginChangedEvent evt) throws RemoteException {
		firePluginChangedEvent(evt);;
	}


	@Override
	public boolean isIdle() throws RemoteException {
		return remoteEvaluator.isIdle();
	}


	@Override
	public int getPort() throws RemoteException {
		return remoteEvaluator.getPort();
	}


	@Override
	public void addPluginChangedListener(PluginChangedListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(PluginChangedListener.class, listener);
		}
    }

	
	@Override
    public void removePluginChangedListener(PluginChangedListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(PluginChangedListener.class, listener);
		}
    }
	
    
    /**
     * Return an array of registered plug-in changed listeners.
     * @return array of registered plug-in changed listeners.
     */
    protected PluginChangedListener[] getPluginChangedListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(PluginChangedListener.class);
		}
    }

    
    /**
     * Dispatching plug-in changed event to registered plug-in changed listeners after plug-in storage was changed.
     * @param evt plug-in changed event is issued to registered plug-in changed listeners after plug-in storage was changed.
     */
    protected void firePluginChangedEvent(PluginChangedEvent evt) {
		synchronized (listenerList) {
			PluginChangedListener[] listeners = getPluginChangedListeners();
			for (PluginChangedListener listener : listeners) {
				try {
					listener.pluginChanged(evt);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
		}
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
     * Return array of evaluator listeners for this evaluator.
     * @return array of evaluator listeners for this evaluator.
     */
    protected EvaluatorListener[] getEvaluatorListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluatorListener.class);
		}
    }

    
    /**
     * Firing (issuing) an event from this evaluator to all evaluator listeners. 
     * @param evt event from this evaluator.
     * @param localTargetListener local target listener.
     */
    protected void fireEvaluatorEvent(EvaluatorEvent evt) {
		synchronized (listenerList) {
			EvaluatorListener[] listeners = getEvaluatorListeners();
			for (EvaluatorListener listener : listeners) {
				try {
					listener.receivedEvaluator(evt);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
		}
    }

    
    @Override
	public void receivedEvaluator(EvaluatorEvent evt) throws RemoteException {
    	fireEvaluatorEvent(evt);
	}

    
    @Override
	public void addEvaluateListener(EvaluateListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluateListener.class, listener);
		}
    }

    
	@Override
    public void removeEvaluateListener(EvaluateListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluateListener.class, listener);
		}
    }
	
    
    /**
     * Return array of evaluation listeners for this evaluator.
     * @return array of evaluation listeners.
     */
    protected EvaluateListener[] getEvaluateListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluateListener.class);
		}
    }

    
    /**
     * Firing an evaluation event from this evaluator to all evaluation listeners. 
     * @param evt event from this evaluator.
     */
    protected void fireEvaluateEvent(EvaluateEvent evt) {
		synchronized (listenerList) {
			EvaluateListener[] listeners = getEvaluateListeners();
			for (EvaluateListener listener : listeners) {
				try {
					listener.receivedEvaluation(evt);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
		}
    }

    
    @Override
	public void receivedEvaluation(EvaluateEvent evt) throws RemoteException {
    	fireEvaluateEvent(evt);
	}


	@Override
	public void addEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(EvaluateProgressListener.class, listener);
		}
    }

    
    @Override
    public void removeEvaluateProgressListener(EvaluateProgressListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(EvaluateProgressListener.class, listener);
		}
    }
	
    
    /**
     * Getting an array of evaluation progress listener.
     * @return array of {@link ProgressListener} (s).
     */
    protected EvaluateProgressListener[] getProgressListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(EvaluateProgressListener.class);
		}
    }
    
    
    /**
     * Firing {@link ProgressEvent}.
     * @param evt the specified for evaluation progress.
     */
    protected void fireProgressEvent(EvaluateProgressEvent evt) {
		synchronized (listenerList) {
	    	EvaluateProgressListener[] listeners = getProgressListeners();
			for (EvaluateProgressListener listener : listeners) {
				try {
					listener.receivedProgress(evt);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
		}
    }


    @Override
	public void receivedProgress(EvaluateProgressEvent evt) throws RemoteException {
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
		synchronized (listenerList) {
	    	SetupAlgListener[] listeners = getSetupAlgListeners();
			for (SetupAlgListener listener : listeners) {
				try {
					listener.receivedSetup(evt);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
		}
    }

    
	@Override
	public void receivedSetup(SetupAlgEvent evt) throws RemoteException {
		fireSetupAlgEvent(evt);
	}


	/**
	 * Adding elapsed time listener.
	 * @param listener elapsed time listener.
	 * @throws RemoteException if any error raises.
	 */
	public void addElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(CounterElapsedTimeListener.class, listener);
		}
    }

    
	/**
	 * Removing elapsed time listener.
	 * @param listener elapsed time listener.
	 * @throws RemoteException if any error raises.
	 */
    public void removeElapsedTimeListener(CounterElapsedTimeListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(CounterElapsedTimeListener.class, listener);
		}
    }
	
    
    /**
     * Getting an array of elapsed time listeners.
     * @return array of elapsed time listeners.
     */
    protected CounterElapsedTimeListener[] getElapsedTimeListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(CounterElapsedTimeListener.class);
		}
    }
    
    
    /**
     * Firing elapsed time event.
     * @param evt elapsed time event.
     */
    protected void fireElapsedTimeEvent(CounterElapsedTimeEvent evt) {
		synchronized (listenerList) {
	    	CounterElapsedTimeListener[] listeners = getElapsedTimeListeners();
			for (CounterElapsedTimeListener listener : listeners) {
				try {
					listener.receivedElapsedTime(evt);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
		}
    }

    
    @Override
	public void receivedElapsedTime(CounterElapsedTimeEvent evt) throws RemoteException {
    	fireElapsedTimeEvent(evt);
	}


	@Override
	public synchronized boolean remoteStart0(List<Alg> algList, DatasetPoolExchanged pool, Timestamp timestamp, Serializable parameter) throws RemoteException {
		boolean flag = socketServer.getFlag();
		
		if (flag)
			return remoteEvaluator.remoteStart0(algList, pool, timestamp, parameter);
		else if (socketServer.isRunning())
			return remoteEvaluator.remoteStart0(algList, pool, timestamp, parameter);
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public boolean remoteStart(List<String> algNameList, DatasetPoolExchanged pool, ClassProcessor cp, DataConfig config, Timestamp timestamp, Serializable parameter)
			throws RemoteException {
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
		boolean flag = socketServer.getFlag();
		
		if (flag)
			return remoteEvaluator.remoteForceStop();
		else if (socketServer.isRunning())
			return remoteEvaluator.remoteForceStop();
		else
			throw new RemoteException("Socket server is not running");
	}

	
	@Override
	public boolean remoteIsStarted() throws RemoteException {
		return remoteEvaluator.remoteIsStarted();
	}

	
	@Override
	public boolean remoteIsPaused() throws RemoteException {
		return remoteEvaluator.remoteIsPaused();
	}

	
	@Override
	public boolean remoteIsRunning() throws RemoteException {
		return remoteEvaluator.remoteIsRunning();
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
			if (!remoteEvaluator.isAgent())
				this.remoteStop(); //It is possible to stop this evaluator because its is delegated evaluator.
			
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


	@Override
	public synchronized void forceUnexport() throws RemoteException {
		unexport();
	}


	@Override
	public String getEvaluateStorePath() throws RemoteException {
		return remoteEvaluator.getEvaluateStorePath();
	}


	@Override
	public void setEvaluateStorePath(String evStorePath) throws RemoteException {
		remoteEvaluator.setEvaluateStorePath(evStorePath);
	}


	@Override
	public Service getReferredService() throws RemoteException {
		return remoteEvaluator.getReferredService();
	}


	@Override
	public void setReferredService(Service referredService) throws RemoteException {
		remoteEvaluator.setReferredService(referredService);
	}


	@Override
	public Remote getExportedStub() throws RemoteException {
		return exportedStub;
	}

	
	@Override
	public boolean isAgent() throws RemoteException {
		return remoteEvaluator.isAgent();
	}


	@Override
	public void setAgent(boolean agent) throws RemoteException {
		remoteEvaluator.setAgent(agent);
	}


	@Override
	public boolean updatePool(DatasetPoolExchanged pool, EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.updatePool(pool, localTargetListener, timestamp);
	}

	
	@Override
	public boolean reloadPool(EvaluatorListener localTargetListener, Timestamp timestamp) throws RemoteException {
		return remoteEvaluator.reloadPool(localTargetListener, timestamp);
	}


	@Override
	public void close() throws Exception {
		try {
			unexport();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}


	@Override
	public String toString() {
    	String evaluatorName = "No name";
		try {
			evaluatorName = getName();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		return DSUtil.shortenVerbalName(evaluatorName);
	}

	
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		
		try {
			close();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
			

}
