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
	
	
//	@Override
//	@Deprecated
//	public UriAssoc createUriAssoc(DataConfig config) {
//		xURI uri = config.getStoreUri();
//		return createUriAssoc(uri);
//	}


	@Override
	public UriAssoc createUriAssoc(xURI uri) {
		if (uri == null)
			return null;
		
		UriAssoc assoc = null;
		String schema = uri.getScheme();
		if (schema == null)
			assoc = new UriAssocTrueZip();
		else if (schema.equals("ftp")) {
			//Current implementation does not support FTP file system yet.
			throw new RuntimeException("Current implementation does not support FTP file system yet.");
		}
		else
			assoc = new UriAssocTrueZip();
		
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
		// TODO Auto-generated constructor stub
	}

	
	@Override
	public boolean createUnit(String unitName, AttributeList attList) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean deleteUnitData(String unitName) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean dropUnit(String unitName) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public UnitList getUnitList() {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new UnitList(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public NominalList getNominalList(String filterUnit, String attName) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new NominalList(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public AttributeList getAttributes(String profileUnit) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new AttributeList(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public AttributeList getAttributes(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new AttributeList(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean containsProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public Profile getProfile(String profileUnit, Profile condition) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return null; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public Fetcher<Profile> getProfiles(String profileUnit, Profile condition) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new MemFetcher<Profile>(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public Fetcher<Profile> getProfiles(ParamSql selectSql, Profile condition) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new MemFetcher<Profile>(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public Fetcher<Integer> getProfileIds(String profileUnit) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return new MemFetcher<Integer>(); //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public int getProfileMaxId(String profileUnit) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return -1; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean insertProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean updateProfile(String profileUnit, Profile profile) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public boolean deleteProfile(String profileUnit, Profile condition) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return false; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}

	
	@Override
	public CsvReader getReader(String unit) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return null; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}


	@Override
	public CsvWriter getWriter(String unit, boolean append) {
		// TODO Auto-generated method stub
		System.out.println("Hudup server does not provide any Provider or ProviderAssoc and so this HudupProviderAssoc is only pointer to Hudup server.");
		return null; //Fixing bug date: 2019.08.07 by Loc Nguyen
	}


	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated constructor stub
		setEditable(false);
		getSelectionModel().addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		update(getAttributeList());
	}

	
	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		update(this.providerAssoc, this.unit);
	}

	
	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		if (providerAssoc != null)
			providerAssoc.close();
		providerAssoc = null;
		unit = null;
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated constructor stub
		setEditable(true);
		createControlPanel();
		
		addDBTableEventListener(new DBTableEventListener() {

			@Override
			public void afterRowSelectionChange(int fromRow, int toRow) {
				// TODO Auto-generated method stub
				super.afterRowSelectionChange(fromRow, toRow);
				fireSelectionChangedEvent(new SelectionChangedEvent(getThisUnitTable()));
			}
			
		});
		
		addDatabaseChangeListener(new DatabaseChangeListener() {

			@Override
			public void afterDelete(int row) {
				// TODO Auto-generated method stub
				super.afterDelete(row);
				fireSelectionChangedEvent(new SelectionChangedEvent(getThisUnitTable()));
			}

			@Override
			public void afterInsert(int row) {
				// TODO Auto-generated method stub
				super.afterInsert(row);
				fireSelectionChangedEvent(new SelectionChangedEvent(getThisUnitTable()));
			}

			@Override
			public void afterUpdate(int row) {
				// TODO Auto-generated method stub
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
			// TODO Auto-generated catch block
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
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
	}


	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		try {
			super.refresh();
		} 
		catch (SQLException e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
	}


	@Override
	public Component getComponent() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated constructor stub
	}


	@Override
	public boolean isArchive(xURI uri) {
		// TODO Auto-generated method stub
		TPath path = (TPath) newPath(uri); 
		return path.isArchive();
	}

	
	@Override
	public Path newPath(xURI uri) {
		// TODO Auto-generated method stub
		return new TPath(uri.getURI()).toAbsolutePath();
	}


	@Override
	public Path newPath(String path) {
		// TODO Auto-generated method stub
		return new TPath(path).toAbsolutePath();
	}

	
	@Override
	public xURI chooseUri(Component comp, boolean open, 
			String[] exts, String[] descs, xURI curStore, String defaultExt) {
		
		ChosenUriResult result = chooseUriResult(
				comp, 
				open, 
				exts, 
				descs,
				curStore);
		
        if (result == null)
			return null;
        
        xURI uri = result.getChosenUri();
        String ext = uri.getLastNameExtension();
        if (open == false && ext == null) {
        	ext = result.getChosenExt();
        	if (ext == null) {
        		if (defaultExt == null)
        			ext = Constants.DEFAULT_EXT;
        		else
        			ext = defaultExt;
        	}
        	uri = xURI.create(uri.toString() + "." + ext);
        }
        
        return uri;
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
	 * @return File chooser represented by {@link TFileChooser}
	 */
	private TFileChooser newUriChooser() {
		return new TFileChooser();
	}

	
	/**
	 * This method shows a graphic user interface (GUI) allowing users to select files they want. Such GUI is called <i>choice dialog</i>.
	 * Such chosen files are returned as the class {@link ChosenUriResult}.
	 * @param comp The graphic user interface (GUI) component works as a parent component of choice dialog. 
	 * @param open If true then, choice dialog is <i>open</i> dialog allowing users to choose and open files. Otherwise, choice dialog is <i>save</i> dialog allowing users to choose and save files. 
	 * @param exts The specified array of archive (file) extensions which are used to filter objects that users select, for example, &quot;*.hdp&quot;, &quot;*.xls&quot;. Each extension has a description. The respective array of extension descriptions is specified by the parameter {@code descs}.  
	 * @param descs The specified array of descriptions, for example, &quot;Hudup file&quot;, &quot;Excel 97-2003&quot;. Note that each extension has a description and the respective array of file extensions is represented by the parameter {@code exts}. The combination of parameter {@code exts} and parameter {@code descs} forms filters for selection such as &quot;Hudup file (*.hdp)&quot; and &quot;Excel 97-2003 (*.xls)&quot;.
	 * @param mode The specified mode sets the <i>choice dialog</i> to show only archives (files) or only store (directories) or both archives (files) and store (directories). 
	 * @param curDir Current store (directory) to open <i>choice dialog</i>. Current store can be null.
	 * @return Chosen files are returned as the class {@link ChosenUriResult}
	 */
	private ChosenUriResult chooseUriResult(
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
	
	
	/**
	 * The class {@link ChosenUriResult} represents chosen files are returned by method {@link UriAssocTrueZip#chooseUriResult(Component, boolean, String[], String[], xURI)}.
	 * It contains the URI and extension of chosen archive (file).
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private static class ChosenUriResult {
		
		/**
		 * The URI of chosen archive (file).
		 */
		private xURI chosenUri = null;
		
		
		/**
		 * The extension of chosen archive (file).
		 */
		private String chosenExt = null;
		
		
		/**
		 * Constructor with URI and extension of chosen archive (file).
		 * @param chosenFile
		 * @param chosenExt
		 */
		public ChosenUriResult(xURI chosenUri, String chosenExt) {
			this.chosenUri = chosenUri;
			this.chosenExt = chosenExt;
		}
		
		
		/**
		 * Getting the URI of chosen archive (file).
		 * @return chosen {@link File}
		 */
		public xURI getChosenUri() {
			return chosenUri;
		}
		
		
		/**
		 * Getting the extension of chosen archive (file).
		 * @return file extension
		 */
		public String getChosenExt() {
			return chosenExt;
		}
		
	}
	
	
	/**
	 * This class is used for filtering file or directories shown in choice dialog allowing users to choose objects.
	 * This class implements the interface {@link FileFilter} in which the most important method {@link ChosenFileFilter#accept(File)} must be defined for filtering.
	 * If this method returns true, the file will be accepted to be shown in choice dialog. Otherwise, the file is rejected to be shown in choice dialog.
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private static class ChosenFileFilter extends FileFilter {

		/**
		 * The extension of files will be accepted for filtering, for example, &quot;*.hdp&quot;, &quot;*.xls&quot;.
		 */
		private String ext = null;
		
		
		/**
		 * The description of files will be accepted for filtering. Each extension is attached to a particular description, for example, &quot;Hudup file&quot;, &quot;Excel 97-2003&quot;.
		 */
		private String desc = null;
		
		
		/**
		 * Constructor with extension and description of files which will be accepted to be shown in choice dialog. In other words, such files will be filtered.
		 * @param ext Extension of files which will be accepted for filtering
		 * @param desc Description of files which will be accepted for filtering
		 */
		public ChosenFileFilter(String ext, String desc) {
			this.ext = ext;
			this.desc = desc;
		}

		
		@Override
		public boolean accept(File f) {
			if (f.isDirectory())
				return true;
			
			String ext = xURI.create(f).getLastNameExtension();
			if (ext != null && 
					ext.toLowerCase().equals(this.ext.toLowerCase()))
				return true;
			
			return false;
		}

		
		@Override
		public String getDescription() {
			return desc;
		}
	
		
		/**
		 * Getting description of files which will be accepted for filtering.
		 * @return file extension
		 */
		public String getExt() {
			return ext;
		}
		
		
	}

	
}

