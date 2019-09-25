/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ctx;

import java.io.Serializable;
import java.rmi.RemoteException;

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
					// TODO Auto-generated method stub
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
			e.printStackTrace();
		}
		
		return -1;
	}
	
	
	@Override
	public void resetConfig() {
		// TODO Auto-generated method stub
		LogUtil.warn("CTSManager.resetConfig() not supported");
	}


	@Override
	public DataConfig createDefaultConfig() {
		// TODO Auto-generated method stub
		return null;
	}

	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		try {
			unexport();
		} catch (Throwable e) {e.printStackTrace();}
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		try {
			close();
		} catch (Throwable e) {e.printStackTrace();}
	}

	
	@Override
	public void remoteSetup(DataConfig config) throws RemoteException {
		// TODO Auto-generated method stub
		setup(config);
	}


	@Override
	public ContextTemplateSchema remoteGetCTSchema() throws RemoteException {
		// TODO Auto-generated method stub
		return getCTSchema();
	}


	@Override
	public boolean remoteCreateContextTemplateUnit() throws RemoteException {
		// TODO Auto-generated method stub
		return createContextTemplateUnit();
	}


	@Override
	public void remoteReload() throws RemoteException {
		// TODO Auto-generated method stub
		reload();
	}


	@Override
	public Context remoteCreateContext(int ctxTemplateId, Serializable assignedValue) throws RemoteException {
		// TODO Auto-generated method stub
		return createContext(ctxTemplateId, assignedValue);
	}


	@Override
	public ContextList remoteGetContexts(int userId, int itemId) throws RemoteException {
		// TODO Auto-generated method stub
		return getContexts(userId, itemId);
	}


	@Override
	public Profile remoteProfileOf(Context context) throws RemoteException {
		// TODO Auto-generated method stub
		return profileOf(context);
	}


	@Override
	public Profile remoteProfileOf(int ctxTemplateId, ContextValue ctxValue) throws RemoteException {
		// TODO Auto-generated method stub
		return profileOf(ctxTemplateId, ctxValue);
	}


	@Override
	public Profiles remoteProfilesOf(int ctxTemplateId) throws RemoteException {
		// TODO Auto-generated method stub
		return profilesOf(ctxTemplateId);
	}


	@Override
	public CTSMultiProfiles remoteCreateCTSProfiles() throws RemoteException {
		// TODO Auto-generated method stub
		return createCTSProfiles();
	}


	@Override
	public boolean remoteCommitCTSchema() throws RemoteException {
		// TODO Auto-generated method stub
		return commitCTSchema();
	}


	@Override
	public boolean remoteImportCTSchema(CTSManager ctsm) throws RemoteException {
		// TODO Auto-generated method stub
		return importCTSchema(ctsm);
	}


	@Override
	public boolean remoteImportCTSchema(Dataset dataset) throws RemoteException {
		// TODO Auto-generated method stub
		return importCTSchema(dataset);
	}


	@Override
	public void remoteDefaultCTSchema() throws RemoteException {
		// TODO Auto-generated method stub
		defaultCTSchema();
	}


}
