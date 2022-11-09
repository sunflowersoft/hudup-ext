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

import net.hudup.core.Constants;
import net.hudup.core.alg.AlgAbstract;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Profile;
import net.hudup.core.data.Profiles;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.parser.TextParserUtil;

/**
 * This is abstract class for context template schema (CTS) manager.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public abstract class CTSManagerAbstract extends AlgAbstract implements CTSManager, CTSManagerRemote {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
    /**
	 * Default constructor.
	 */
	public CTSManagerAbstract() {
		super();
	}
	
	
	@Override
	public Context createContext(
			int ctxTemplateId, 
			Serializable assignedValue) {
		
		ContextTemplate template = getCTSchema().getRootById(ctxTemplateId);
		if (template == null)
			return null;
		
		Attribute attribute = template.getAttribute();
		ContextValue value = ContextValueImpl.create(attribute, assignedValue);
		return Context.create(template, value);
	}

	
	@Override
	public Profile profileOf(Context context) {
		if (context == null || context.getTemplate() == null || context.getValue() == null)
			return null;
		
		return profileOf(context.getTemplate().getId(), context.getValue());
	}
	
	
	@Override
	public CTSMultiProfiles createCTSProfiles() {
		CTSMemMultiProfiles ctsProfiles = CTSMemMultiProfiles.create();
		
		ContextTemplateSchema schema = getCTSchema();
		int rootSize = schema.rootSize();
		for (int i = 0; i < rootSize; i++) {
			ContextTemplate root = schema.getRoot(i);
			
			CTProcessorAbstract processor = new CTProcessorAbstract(ctsProfiles) {
				
				@Override
				public void process(ContextTemplate template) {
					CTSMemMultiProfiles ctsProfiles = (CTSMemMultiProfiles)param;
					
					Profiles profiles = profilesOf(template.getId());
					if (profiles.size() > 0)
						ctsProfiles.put(template.getId(), profiles);
				}
			};
			
			((HierContextTemplate)root).process(processor);
		}
		
		return ctsProfiles;
	}
	
	
	@Override
	public void defaultCTSchema() {
		getCTSchema().defaultCTSchema();
		commitCTSchema();
	}

	
	/**
	 * Extracting template identifier from unit name.
	 * @param unitName specified unit name.
	 * @return extracted number (template identifier).
	 */
	protected int extractTemplateId(String unitName) {
		try {
			if (unitName == null || unitName.isEmpty())
				return -1;
			String temp = getConfig().getContextTemplateUnit() +  TextParserUtil.CONNECT_SEP;
			if (!unitName.startsWith(temp) || unitName.length() <= temp.length())
				return -1;
			
			temp = unitName.substring(temp.length()).trim();
			int index = temp.indexOf(TextParserUtil.CONNECT_SEP);
			if (index == -1)
				return -1;
			
			String snum = temp.substring(0, index);
			return Integer.parseInt(snum);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return -1;
	}
	
	
	@Override
	public void resetConfig() {
		LogUtil.warn("CTSManager.resetConfig() not supported");
	}


	@Override
	public DataConfig createDefaultConfig() {
		return null;
	}

	
	@Override
	public void close() throws Exception {
		try {
			unexport();
		} catch (Throwable e) {LogUtil.trace(e);}
	}


	@Override
	public String[] getBaseRemoteInterfaceNames() throws RemoteException {
		return new String[] {CTSManagerRemote.class.getName()};
	}

	
	@Override
	protected void finalize() throws Throwable {
		try {
			if (!Constants.CALL_FINALIZE) return;
			close();
		} catch (Throwable e) {LogUtil.errorNoLog("Finalize error: " + e.getMessage());}
	}

	
	@Override
	public void remoteSetup(DataConfig config) throws RemoteException {
		setup(config);
	}


	@Override
	public ContextTemplateSchema remoteGetCTSchema() throws RemoteException {
		return getCTSchema();
	}


	@Override
	public boolean remoteCreateContextTemplateUnit() throws RemoteException {
		return createContextTemplateUnit();
	}


	@Override
	public void remoteReload() throws RemoteException {
		reload();
	}


	@Override
	public Context remoteCreateContext(int ctxTemplateId, Serializable assignedValue) throws RemoteException {
		return createContext(ctxTemplateId, assignedValue);
	}


	@Override
	public ContextList remoteGetContexts(int userId, int itemId) throws RemoteException {
		return getContexts(userId, itemId);
	}


	@Override
	public ContextList remoteGetContexts(int userId, int itemId, long ratedDate) throws RemoteException {
		return getContexts(userId, itemId, ratedDate);
	}


	@Override
	public Profile remoteProfileOf(Context context) throws RemoteException {
		return profileOf(context);
	}


	@Override
	public Profile remoteProfileOf(int ctxTemplateId, ContextValue ctxValue) throws RemoteException {
		return profileOf(ctxTemplateId, ctxValue);
	}


	@Override
	public Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException {
		return profilesOf(ctxTemplateId);
	}


	@Override
	public CTSMultiProfiles remoteCreateCTSProfiles() throws RemoteException {
		return createCTSProfiles();
	}


	@Override
	public boolean remoteCommitCTSchema() throws RemoteException {
		return commitCTSchema();
	}


	@Override
	public boolean remoteImportCTSchema(CTSManager ctsm) throws RemoteException {
		return importCTSchema(ctsm);
	}


	@Override
	public boolean remoteImportCTSchema(Dataset dataset) throws RemoteException {
		return importCTSchema(dataset);
	}


	@Override
	public void remoteDefaultCTSchema() throws RemoteException {
		defaultCTSchema();
	}


}
