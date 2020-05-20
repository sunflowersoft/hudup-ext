/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.factory;

import java.awt.Component;
import java.io.File;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.JScrollPane;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.swing.TFileChooser;
import de.schlichtherle.truezip.nio.file.TPath;
import net.hudup.core.Constants;
import net.hudup.core.client.Service;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.MemFetcher;
import net.hudup.core.data.NominalList;
import net.hudup.core.data.ParamSql;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderAssoc;
import net.hudup.core.data.ProviderAssocAbstract;
import net.hudup.core.data.UnitList;
import net.hudup.core.data.ui.ProfileTable;
import net.hudup.core.data.ui.UnitTable;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAssoc;
import net.hudup.core.logistic.UriAssocAbstract;
import net.hudup.core.logistic.xURI;
import quick.dbtable.Column;
import quick.dbtable.DBTable;
import quick.dbtable.DBTableEventListener;
import quick.dbtable.DatabaseChangeListener;

/**
 * This is default implementation of factory interface.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class FactoryImpl implements Factory {

	
	/**
	 * Default constructor.
	 */
	public FactoryImpl() {
		
	}
	
	
	@Override
	public ProviderAssoc createProviderAssoc(DataConfig config) {
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
				Service service = net.hudup.core.client.ClientUtil.getRemoteService(
						uri.getHost(),
						uri.getPort(),
						config.getStoreAccount(), 
						config.getStorePassword().getText());
				
				if (service != null)
					providerAssoc = new HudupProviderAssoc(config);
			}
			else if (dataDriver.getType() == DataType.hudup_socket) {
				SocketConnection connection = net.hudup.core.client.ClientUtil.getSocketConnection(
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
	
	
	@Override
	public UnitTable createUnitTable(xURI uri) {
		if (uri == null) return new FlatUnitTable();
		
		DataDriver dataDriver = DataDriver.create(uri);
		if (dataDriver == null)
			return new FlatUnitTable();
		else if (dataDriver.isFlatServer())
			return new FlatUnitTable();
		else if (dataDriver.isDbServer())
			return new DBUnitTable();
		else
			return new FlatUnitTable();
	}
	
	
	@Override
	public UriAssoc createUriAssoc(xURI uri) {
		if (uri == null)
			return null;
		
		UriAssoc assoc = null;
		String schema = uri.getScheme();
		if (schema == null) {
			if (Constants.COMPRESSED_FILE_SUPPORT)
				assoc = new UriAssocTrueZip();
			else
				assoc = UriAssocAbstract.createUriAssoc();
		}
		else if (schema.equals("ftp")) {
			//Current implementation does not support FTP file system yet.
			throw new RuntimeException("Current implementation does not support FTP file system yet.");
		}
		else {
			if (Constants.COMPRESSED_FILE_SUPPORT)
				assoc = new UriAssocTrueZip();
			else
				assoc = UriAssocAbstract.createUriAssoc();
		}
		
		return assoc;
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
	}

	
	@Override
	public boolean createUnit(String unitName, AttributeList attList) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean deleteUnitData(String unitName) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean dropUnit(String unitName) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public UnitList getUnitList() {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new UnitList(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public NominalList getNominalList(String filterUnit, String attName) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new NominalList(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public AttributeList getAttributes(String profileUnit) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new AttributeList(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public AttributeList getAttributes(ParamSql selectSql, Profile condition) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new AttributeList(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean containsProfile(String profileUnit, Profile profile) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public Profile getProfile(String profileUnit, Profile condition) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return null; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public Fetcher<Profile> getProfiles(String profileUnit, Profile condition) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new MemFetcher<Profile>(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new MemFetcher<Profile>(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public Fetcher<Integer> getProfileIds(String profileUnit) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new MemFetcher<Integer>(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public int getProfileMaxId(String profileUnit) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return -1; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean insertProfile(String profileUnit, Profile profile) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean updateProfile(String profileUnit, Profile profile) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public CsvReader getReader(String unit) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return null; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}


	@Override
	public CsvWriter getWriter(String unit, boolean append) {
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return null; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}


	@Override
	public void close() throws Exception {

	}
	
	
}



/**
 * This is an implementation of {@link UnitTable} for flat structure. It is also the default implementation of unit table.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class FlatUnitTable extends ProfileTable implements UnitTable, AutoCloseable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal provider associator.
	 */
	protected ProviderAssoc providerAssoc = null;
	
	
	/**
	 * Internal unit.
	 */
	protected String unit = null;
	
	
	/**
	 * Default constructor.
	 */
	public FlatUnitTable() {
		super();
		setEditable(false);
		getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				fireSelectionChangedEvent(new SelectionChangedEvent(getThisUnitTable()));
			}
		});
	}


	/**
	 * Getting this table.
	 * @return this table.
	 */
	private UnitTable getThisUnitTable() {
		return this;
	}

	
	@Override
	public void update(ProviderAssoc providerAssoc, String unit) {
		if (providerAssoc == null || unit == null)
			return;
		
		try {
			Fetcher<Profile> fetcher = providerAssoc.getProfiles(unit, null);
			update(fetcher);
			fetcher.close();
			
			this.providerAssoc = providerAssoc;
			this.unit = unit;
		}
		catch (Exception e){
			LogUtil.trace(e);
			this.providerAssoc = null;
			this.unit = null;
		}
		
	}

	
	@Override
	public void clear() {
		update(getAttributeList());
	}

	
	@Override
	public void refresh() {
		update(this.providerAssoc, this.unit);
	}

	
	@Override
	public Component getComponent() {
		return new JScrollPane(this);
	}

	
	@Override
	public void addSelectionChangedListener(SelectionChangedListener listener) {
		synchronized (listenerList) {
			listenerList.add(SelectionChangedListener.class, listener);
		}
    }

    
	@Override
    public void removeSelectionChangedListener(SelectionChangedListener listener) {
		synchronized (listenerList) {
			listenerList.remove(SelectionChangedListener.class, listener);
		}
    }
	
    
    /**
     * Return array of selection changed listeners.
     * @return array of selection changed listeners.
     */
    protected SelectionChangedListener[] getSelectionChangedListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(SelectionChangedListener.class);
		}
    }

    
    /**
     * Firing (issuing) an event from this table to all selection changed listeners. 
     * @param evt selection changed event.
     */
    protected void fireSelectionChangedEvent(SelectionChangedEvent evt) {
    	
    	SelectionChangedListener[] listeners = getSelectionChangedListeners();
		
		for (SelectionChangedListener listener : listeners) {
			try {
				listener.respond(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
	
    }


	@Override
	public void close() throws Exception {
		if (providerAssoc != null)
			providerAssoc.close();
		providerAssoc = null;
		unit = null;
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


/**
 * This is the implementation of {@link UnitTable} for database.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
class DBUnitTable extends DBTable implements UnitTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Holding a list of event listeners.
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();
    
    
	/**
	 * Default constructor.
	 */
	public DBUnitTable() {
		super();
		setEditable(true);
		createControlPanel();
		
		addDBTableEventListener(new DBTableEventListener() {

			@Override
			public void afterRowSelectionChange(int fromRow, int toRow) {
				super.afterRowSelectionChange(fromRow, toRow);
				fireSelectionChangedEvent(new SelectionChangedEvent(getThisUnitTable()));
			}
			
		});
		
		addDatabaseChangeListener(new DatabaseChangeListener() {

			@Override
			public void afterDelete(int row) {
				super.afterDelete(row);
				fireSelectionChangedEvent(new SelectionChangedEvent(getThisUnitTable()));
			}

			@Override
			public void afterInsert(int row) {
				super.afterInsert(row);
				fireSelectionChangedEvent(new SelectionChangedEvent(getThisUnitTable()));
			}

			@Override
			public void afterUpdate(int row) {
				super.afterUpdate(row);
				fireSelectionChangedEvent(new SelectionChangedEvent(getThisUnitTable()));
			}
			
		});
	}


	/**
	 * Getting this table.
	 * @return this table.
	 */
	private UnitTable getThisUnitTable() {
		return this;
	}
	
	
	@Override
	public void update(ProviderAssoc providerAssoc, String unit) {
		if (providerAssoc == null)
			return;
		
		if (! (providerAssoc instanceof DbProviderAssoc) ) {
			LogUtil.error("UnitTable currently not support none-database provider");
			return;
		}
		
		DbProviderAssoc dbAssoc = (DbProviderAssoc) providerAssoc;
		setConnection(dbAssoc.getConnection());

		String select = dbAssoc.genSelectSql(unit);
		setSelectSql(select);
		
		ParamSql insert = dbAssoc.genInsertSql(unit);
		clearAllInsertSql();
		addInsertSql(insert.getSql(), insert.getIndexText(true));
		
		ParamSql update = dbAssoc.genUpdateSql(unit);
		clearAllUpdateSql();
		addUpdateSql(update.getSql(), update.getIndexText(true));
		
		ParamSql delete = dbAssoc.genDeleteSql(unit);
		clearAllDeleteSql();
		addDeleteSql(delete.getSql(), delete.getIndexText(true));
		
		try {
			refresh();
			
			Map<Integer, String> boundSql = dbAssoc.genAttributeBoundSql(unit);
			
			if (boundSql.size() > 0) {
				int n = getColumnCount();
				for (int i = 0; i < n; i++) {
					if (boundSql.containsKey(i)) {
						Column column = getColumn(i);
						String sql = boundSql.get(i);
						column.setBoundSql(sql);
					}
				}
			}
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
	}
	
	
	@Override
	public void clear() {
		clearAllDeleteSql();
		clearAllInsertSql();
		clearAllUpdateSql();
		removeAllRows();
		
		try {
			close();
		} 
		catch (SQLException e) {
			LogUtil.trace(e);
		}
	}


	@Override
	public void refresh() {
		try {
			super.refresh();
		} 
		catch (SQLException e) {
			LogUtil.trace(e);
		}
	}


	@Override
	public Component getComponent() {
		return this;
	}
	
	
	@Override
	public void addSelectionChangedListener(SelectionChangedListener listener) {
		synchronized (listenerList) {
			listenerList.add(SelectionChangedListener.class, listener);
		}
    }

    
	@Override
    public void removeSelectionChangedListener(SelectionChangedListener listener) {
		synchronized (listenerList) {
			listenerList.remove(SelectionChangedListener.class, listener);
		}
    }
	
    
    /**
     * Return array of selection changed listeners.
     * @return array of selection changed listeners.
     */
    protected SelectionChangedListener[] getSelectionChangedListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(SelectionChangedListener.class);
		}
    }

    
    /**
     * Firing (issuing) an event from this table to all selection changed listeners. 
     * @param evt selection changed event.
     */
    protected void fireSelectionChangedEvent(SelectionChangedEvent evt) {
    	
    	SelectionChangedListener[] listeners = getSelectionChangedListeners();
		
		for (SelectionChangedListener listener : listeners) {
			try {
				listener.respond(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
	
    }


}


/**
 * This class is default implementation of the interface {@link UriAssoc}. In other words, it is the default URI associator.
 * In current implementation, {@link UriAssocTrueZip} uses the programming library TrueZip for processing on file system, compressed file, HTTP.
 * TrueZip is developed by Schlichtherle IT Services, available at <a href="https://christian-schlichtherle.bitbucket.io/truezip/">https://christian-schlichtherle.bitbucket.io/truezip</a>
 * @author Loc Nguyen
 * @version 11.0
 *
 */
class UriAssocTrueZip extends UriAssocAbstract {

	
	/**
	 * Default constructor
	 */
	public UriAssocTrueZip() {
		super();
	}


	@Override
	public Path newPath(xURI uri) {
		return new TPath(uri.getURI()).toAbsolutePath();
	}


	@Override
	public Path newPath(String path) {
		return new TPath(path).toAbsolutePath();
	}

	
	@Override
	public boolean isArchive(xURI uri) {
		TPath path = (TPath) newPath(uri); 
		return path.isArchive();
	}

	
	@Override
	public xURI chooseStore(Component comp) {
		
		TFileChooser uc = newUriChooser();
		
		xURI curStore = getCurrentStore();
		File curDir = null;
		if (curStore == null)
			curDir = null;
		else if (curStore.getScheme() == null)
			curDir = new File(curStore.getURI().toString());
		else
			curDir = new File(curStore.getURI());
		
		if (curDir != null)
			uc.setCurrentDirectory(curDir);
		uc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		int ret = uc.showOpenDialog(comp);
        if (ret != JFileChooser.APPROVE_OPTION)
        	return null;
		
        return xURI.create(uc.getSelectedFile());
	}

	
	/**
	 * Creating the file chooser.The most important function of such chooser is to show a graphic user interface (GUI) allowing users to select files they want.
	 * @return file chooser.
	 */
	private TFileChooser newUriChooser() {
		return new TFileChooser();
	}


	@Override
	protected ChosenUriResult chooseUriResult(
			Component comp, 
			boolean open, 
			final String[] exts, 
			final String[] descs,
			xURI curStore) {
		
		TFileChooser uc = newUriChooser(); //This is so-called choice dialog
		if (curStore == null || !exists(curStore))
			curStore = getCurrentStore();
		
		File curDir = null;
		if (curStore == null)
			curDir = null;
		else if (curStore.getScheme() == null)
			curDir = new File(curStore.getURI().toString());
		else
			curDir = new File(curStore.getURI());
		
		if (curDir != null)
			uc.setCurrentDirectory(curDir);
		
		if (exts != null && descs != null) {
			for (int i = 0; i < exts.length; i++) {
				uc.addChoosableFileFilter(new ChosenFileFilter(exts[i], descs[i]));
			}
		}
		
		uc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		
		int ret = open ? 
				uc.showOpenDialog(comp) : uc.showSaveDialog(comp);
        if (ret != JFileChooser.APPROVE_OPTION)
        	return null;
        
        TFile file = uc.getSelectedFile();
        FileFilter filter = uc.getFileFilter();
        if (file == null)
        	return null;
        
        xURI uri = null;
        if (file.isArchive()) {
        	TFile parent = file.getParentFile();
        	uri = xURI.create(parent).concat(file.getName());
        }
        else
        	uri = xURI.create(file.toURI());
        
        if ( (filter != null) && (filter instanceof ChosenFileFilter) )
        	return new ChosenUriResult(uri, ((ChosenFileFilter)filter).getExt());
        else
        	return new ChosenUriResult(uri, null);
	}
	
	
}

