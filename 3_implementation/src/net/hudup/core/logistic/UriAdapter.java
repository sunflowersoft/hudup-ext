/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import net.hudup.core.Util;
import net.hudup.core.data.AutoCloseable;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.UnitList;

/**
 * This class is also a URI associator because it implements the interface {@link UriAssoc} but it hides all physical systems and protocols (file system, compressed file, HTTP, database, etc.).
 * Actually, programmers should use this class for processing (creating, copying, renaming, deleting, getting reader, getting writer, etc.) on objects identified by URI, instead of implementing {@link UriAssoc}.
 * {@link UriAdapter} is the most complete URI associator and it can use other URI associators for different physical systems and protocols.
 * Concretely, the internal variable {@link #assoc} refers to other URI associators.
 * As a convention, {@link UriAdapter} is also called URI adapter.
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public class UriAdapter implements UriAssoc, AutoCloseable {

	
	/**
	 * This is the internal variable {@link #assoc} refers to other URI associators for different storage systems and protocols.
	 */
	protected UriAssoc assoc = null;
	
	
	/**
	 * Construction with specified configuration.
	 * @param config Specified configuration
	 */
	public UriAdapter(DataConfig config) {
		connect(config);
	}
	
	
	/**
	 * Construction with specified URI.
	 * @param uri Specified URI
	 */
	public UriAdapter(xURI uri) {
		connect(uri);
	}
	
	
	/**
	 * Construction with specified URI connection string.
	 * @param urispec Specified URI connection string
	 */
	public UriAdapter(String urispec) {
		connect(urispec);
	}
	
	
	/**
	 * Default construction with current directory (.) in file system.
	 */
	public UriAdapter() {
		this(".");
	}

	
	@Override
	public boolean connect(DataConfig config) {
		try {
			close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		assoc = Util.getFactory().createUriAssoc(config.getStoreUri());
		if (assoc == null)
			return false;
		else
			return assoc.connect((DataConfig) config.clone());
	}
	
	
	/**
	 * Initializing an URI associator by connecting to the specified URI.
	 * @param uri specified URI
	 * @return whether connect successfully
	 */
	public boolean connect(xURI uri) {
		if (uri == null)
			return false;
		
		DataConfig config = new DataConfig();
		config.setStoreUri(uri);
		
		return connect(config);
	}
	
	
	/**
	 * Initializing an URI associator by connecting to the specified URI connection string.
	 * @param urispec URI connection string
	 * @return Whether connect successfully
	 */
	public boolean connect(String urispec) {
		URI uri = null;
		try {
			uri = new URI(urispec);
			if (uri.getScheme() == null)
				uri = Paths.get(urispec).toAbsolutePath().toUri();
		}
		catch (Exception e) {
			LogUtil.trace(e);
			uri = null;
		}
		if (uri == null)
			uri = Paths.get(urispec).toAbsolutePath().toUri();
			
		
		return connect(xURI.create(uri));
	}
	
	
	@Override
	public Path newPath(xURI uri) {
		return assoc.newPath(uri);
	}


	@Override
	public Path newPath(String path) {
		return assoc.newPath(path);
	}


	@Override
	public boolean exists(xURI uri) {
		return assoc.exists(uri);
	}


	@Override
	public boolean isStore(xURI uri) {
		return assoc.isStore(uri);
	}

	
	@Override
	public boolean isArchive(xURI uri) {
		return assoc.isArchive(uri);
	}


	@Override
	public boolean create(xURI uri, boolean isStore) {
		return assoc.create(uri, isStore);
	}


	@Override
	public boolean clearContent(xURI uri, UriFilter filter) {
		return assoc.clearContent(uri, filter);
	}


	@Override
	public boolean delete(xURI uri, UriFilter filter) {
		return assoc.delete(uri, filter);
	}


	@Override
	public boolean copy(xURI src, xURI dst, boolean moved, UriFilter filter) {
		return assoc.copy(src, dst, moved, filter);
	}


	@Override
	public boolean rename(xURI src, xURI dst) {
		return assoc.rename(src, dst);
	}


	@Override
	public List<xURI> getUriList(xURI store, UriFilter filter) {
		return assoc.getUriList(store, filter);
	}


	/**
	 * Getting stores or archives of parent store or archive.
	 * @param parentStoreOrArchive parent store or archive.
	 * @return stores or archives of parent store or archive.
	 */
	public List<xURI> getUriListOfStoresArchives(xURI parentStoreOrArchive) {
		if (!exists(parentStoreOrArchive) || !(isStore(parentStoreOrArchive) || isArchive(parentStoreOrArchive))) {
			return Util.newList();
		}
		
		List<xURI> storesArchivesList = getUriList(parentStoreOrArchive, new UriFilter() {
			
			@Override
			public String getDescription() {
				return "Working class paths";
			}
			
			@Override
			public boolean accept(xURI uri) {
				if (isStore(uri) || isArchive(uri))
					return true;
				else
					return false;
			}
			
		});
		
		return storesArchivesList;
	}
	
	
	@Override
	public InputStream getInputStream(xURI uri) {
		return assoc.getInputStream(uri);
	}


	@Override
	public OutputStream getOutputStream(xURI uri, boolean append) {
		return assoc.getOutputStream(uri, append);
	}


	@Override
	public Reader getReader(xURI uri) {
		return assoc.getReader(uri);
	}


	@Override
	public Writer getWriter(xURI uri, boolean append) {
		return assoc.getWriter(uri, append);
	}

	
	@Override
	public ByteChannel getReadChannel(xURI uri) {
		return assoc.getReadChannel(uri);
	}


	@Override
	public ByteChannel getWriteChannel(xURI uri, boolean append) {
		return assoc.getWriteChannel(uri, append);
	}


	@Override
	public xURI getStoreOf(xURI uri) {
		return assoc.getStoreOf(uri);
	}

	
	@Override
	public xURI chooseUri(Component comp, boolean open, String[] exts,
			String[] descs, xURI curStore, String defaultExt) {
		return assoc.chooseUri(comp, open, exts, descs, curStore, defaultExt);
	}


	@Override
	public xURI chooseDefaultUri(Component comp, boolean open, xURI curStore) {
		return assoc.chooseDefaultUri(comp, open, curStore);
	}


	@Override
	public xURI chooseStore(Component comp) {
		return assoc.chooseStore(comp);
	}


	/**
	 * This method creates a configuration represented by {@link DataConfig} with full of units (user profile, item profile, rating matrix, etc.) from the URI of the unit in the configuration.
	 * @param unitUri URI of the specified unit in configuration.
	 * @param mainUnit main unit. It can be null.
	 * @return configuration represented by {@link DataConfig} with full of units (user profile, item profile, rating matrix, etc.).
	 */
	public DataConfig makeFlatDataConfig(xURI unitUri, String mainUnit) {
		xURI storeUri = getStoreOf(unitUri);
		String referredUnit = null;
		if (!isStore(unitUri) && mainUnit != null)
			referredUnit = unitUri.getLastName();
		
		List<xURI> uriList = getUriList(storeUri, null);
		UnitList unitList = new UnitList();
		for (xURI u : uriList) {
			if (!isStore(u))
				unitList.add(u.getLastName());
		}
		
		DataConfig config = new DataConfig();
		config.putUnitList(unitList);
		
		config.setStoreUri(storeUri);
		if (mainUnit != null && referredUnit != null) {
			config.setMainUnit(mainUnit);
			config.put(mainUnit, referredUnit);
		}
		return config;
	}

	
	/**
	 * Firstly, this method retrieves a list of URI (s) referring objects inside the specified store with regard to specified filter. For each retrieved URI, some task is processed on the object referred by such URI and so the specified {@link UriProcessor} is responsible for doing such task.
	 * @param store URI of specified store.
	 * @param filter Specified filter represented by {@link UriFilter}.
	 * @param processor Specified {@link UriProcessor} is responsible for doing such task on the object referred by each retrieved URI.
	 */
	public void uriListProcess(xURI store, UriFilter filter, UriProcessor processor) {
		UriAssocAbstract.uriListProcess(assoc, store, filter, processor);
	}
	
	
	/**
	 * Reading text from the archive (file) identified by URI by optimal way.
	 * @param uri URI of specified archive (file).
	 * @return {@link StringBuffer} containing text from specified archive (file).
	 */
	public StringBuffer readText(xURI uri) {
		
		StringBuffer content = new StringBuffer();
		Reader reader = null;
		try {
			reader = getReader(uri);
	        
			char[] buffer = new char[4096];
			int read = 0;
			do {
				content.append(buffer, 0, read);
				read = reader.read(buffer);
			}
			while (read >= 0);
			
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
		
		return content;
	}
	
	
	/**
	 * Reading text from the archive (file) in line-by-line manner.
	 * @param uri URI of specified archive (file).
	 * @return List of text lines.
	 */
	public List<StringBuffer> readLines(xURI uri) {
		final List<StringBuffer> content = Util.newList();
		
		BufferedReader reader = null;
		try {
			UriAdapter adapter = new UriAdapter(uri);
			reader = new BufferedReader(adapter.getReader(uri));
			DSUtil.lineProcess(reader, new LineProcessor() {
				
				@Override
				public void process(String line) {
					content.add(new StringBuffer(line));
				}
			});
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		finally {
			try {
				if (reader != null)
					reader.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
		
		return content;
	}

	
	/**
	 * Saving (writing) text to the archive (file) identified by URI by optimal way.
	 * @param uri URI of specified archive (file).
	 * @param content Text content to saved to the specified archive (file).
	 * @param append Whether or not &quot;append&quot; mode is applied for writing. In &quot;append&quot; mode, content is added continuously to the end of the archive (file). Otherwise, the archive (file) is made empty and then content is put into the archive (file) from beginning.
	 * @return whether save successfully
	 */
	public boolean saveText(xURI uri, String content, boolean append) {
		PrintWriter writer = null;
		boolean result = true;
		try {
			writer = new PrintWriter(getWriter(uri, append));
			writer.print(content);
			writer.flush();
		} 
		catch (Exception e) {
			LogUtil.trace(e);
			result = false;
		}
		finally {
			try {
				if (writer != null)
					writer.close();
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
		
		return result;
	}

	
	@Override
	public void close() {
		try {
			if (assoc != null)
				assoc.close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		assoc = null;
	}

	
	/**
	 * Unzipping the specified file to specified store.
	 * @param zipFile specified compressed file.
	 * @param store specified store.
	 * @return true if unzipping is successful.
	 */
	public boolean unzip(xURI zipFile, xURI store) {
		boolean ret = true;
		
		try (ZipInputStream zis = new ZipInputStream(getInputStream(zipFile));) {
			ZipEntry zipEntry = null;
			byte[] buffer = new byte[1024];
	        while ((zipEntry = zis.getNextEntry()) != null) {
	            xURI newUri = store.concat(zipEntry.getName());
	            OutputStream os = getOutputStream(newUri, false);
	            int length = 0;
	            while ((length = zis.read(buffer)) > 0) {
	                os.write(buffer, 0, length);
	            }
	            os.close();
	        }
			zis.closeEntry();
		}
		catch (Exception e) {
			ret = false;
			LogUtil.trace(e);
		}
		
		return ret;
	}

	
	@Override
	protected void finalize() throws Throwable {
//		super.finalize();
		
		try {
			close();
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
	}

	
	/**
	 * Given slash package &quot;java/lang/annotation&quot; or &quot;java\lang\annotation&quot;, this method converts such slash package into normal dot package &quot;java.lang.annotation&quot;.
	 * @param slashPackage Specified slash package, for example, &quot;java/lang/annotation&quot;, &quot;java\lang\annotation&quot;.
	 * @return Dot package, for example, &quot;java.lang.annotation&quot;.
	 */
	public static String packageSlashToDot(String slashPackage) {
		slashPackage = slashPackage.trim();
		slashPackage = slashPackage.replace('\\', '/');
		if (slashPackage.isEmpty() || slashPackage.equals("/"))
			slashPackage = "";
		else {
			if (slashPackage.startsWith("/"))
				slashPackage = slashPackage.substring(1);
			if (slashPackage.endsWith("/"))
				slashPackage = slashPackage.substring(0, slashPackage.length()-1);
			slashPackage = slashPackage.replace('/', '.');
		}
		
		return slashPackage;
	}
	
	
	/**
	 * This class is the {@link Writer} (for writing operations on archives identified by URI) with support of URI adapter {@link UriAdapter}.
	 * In other words, it can do writing operations on any objects (archives) and hide all physical systems and protocols (file system, compressed file, HTTP, database, etc.).
	 * @author Loc Nguyen
	 * @version 11.0
	 *
	 */
	public static class AdapterWriter extends Writer implements AutoCloseable {

		
		/**
		 * The URI adapter that supports writing operations. The built-in variable {@link #writer} is created by this variable.
		 */
		protected UriAdapter adapter = null;
		
		
		/**
		 * The main {@link Writer} is actually used to do writing operations.
		 */
		protected Writer writer = null;

		
		/**
		 * Constructor with URI of specified object (archive).
		 * @param uri URI of specified object (archive).
		 * @param append Whether or not &quot;append&quot; mode is applied for writing. In &quot;append&quot; mode, content is added continuously to the end of the archive (file). Otherwise, the archive (file) is made empty and then content is put into the archive (file) from beginning.
		 */
		public AdapterWriter(xURI uri, boolean append) {
			adapter = new UriAdapter(uri);
			writer = adapter.getWriter(uri, append);
		}

		
		/**
		 * Constructor with configuration of specified object (archive).
		 * @param config Configuration of specified object (archive).
		 * @param append Whether or not &quot;append&quot; mode is applied for writing. In &quot;append&quot; mode, content is added continuously to the end of the archive (file). Otherwise, the archive (file) is made empty and then content is put into the archive (file) from beginning.
		 */
		public AdapterWriter(DataConfig config, boolean append) {
			this(config.getStoreUri(), append);
		}

		
		@Override
		public void write(char[] cbuf, int off, int len) throws IOException {
			writer.write(cbuf, off, len);
		}

		
		@Override
		public void flush() throws IOException {
			writer.flush();
		}

		
		@Override
		public void close() throws IOException {
			if (writer != null)
				writer.close();
			writer = null;
			
			if (adapter != null)
				adapter.close();
			adapter = null;
		}


		@Override
		protected void finalize() throws Throwable {
//			super.finalize();
			
			try {
				close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}

		
	}

	
	/**
	 * This class is the {@link Reader} (for reading operations on archives identified by URI) with support of URI adapter {@link UriAdapter}.
	 * In other words, it can do reading operations on any objects (archives) and hide all physical systems and protocols (file system, compressed file, HTTP, database, etc.).
	 * @author Loc Nguyen
	 * @version 11.0
	 *
	 */
	public static class AdapterReader extends Reader implements AutoCloseable {

		
		/**
		 * The URI adapter that supports reading operations. The built-in variable {@link #reader} is created by this variable.
		 */
		protected UriAdapter adapter = null;
		
		
		/**
		 * The main {@link Reader} is actually used to do reading operations.
		 */
		protected Reader reader = null;

		
		/**
		 * Constructor with URI of specified object (archive).
		 * @param uri URI of specified object (archive).
		 */
		public AdapterReader(xURI uri) {
			adapter = new UriAdapter(uri);
			reader = adapter.getReader(uri);
		}

		
		/**
		 * Constructor with configuration of specified object (archive).
		 * @param config Configuration of specified object (archive).
		 */
		public AdapterReader(DataConfig config) {
			this(config.getStoreUri());
		}


		@Override
		public int read(char[] cbuf, int off, int len) throws IOException {
			return reader.read(cbuf, off, len);
		}


		@Override
		public void close() throws IOException {
			if (reader != null)
				reader.close();
			reader = null;
			
			if (adapter != null)
				adapter.close();
			adapter = null;
		}


		@Override
		protected void finalize() throws Throwable {
//			super.finalize();
			
			try {
				close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
	}
	
	
	/**
	 * This class is the {@link ByteChannel} (for both writing and reading operations on archives identified by URI) with support of URI adapter {@link UriAdapter}.
	 * In other words, it can do both writing and reading operations on any objects (archives) and hide all physical systems and protocols (file system, compressed file, HTTP, database, etc.).
	 * @author Loc Nguyen
	 * @version 11.0
	 *
	 */
	public static class AdapterWriteChannel implements ByteChannel, AutoCloseable {

		
		/**
		 * The URI adapter that supports both reading and writing operations. The built-in variable {@link #channel} is created by this variable.
		 */
		protected UriAdapter adapter = null;
		
		
		/**
		 * The main {@link ByteChannel} is actually used to do both reading and writing operations.
		 */
		protected ByteChannel channel = null;

		
		/**
		 * Constructor with URI of specified object (archive).
		 * @param uri URI of specified object (archive).
		 * @param append Whether or not &quot;append&quot; mode is applied for writing. In &quot;append&quot; mode, content is added continuously to the end of the archive (file). Otherwise, the archive (file) is made empty and then content is put into the archive (file) from beginning.
		 */
		public AdapterWriteChannel(xURI uri, boolean append) {
			adapter = new UriAdapter(uri);
			channel = adapter.getWriteChannel(uri, append);
		}

		
		/**
		 * Constructor with configuration of specified object (archive).
		 * @param config Configuration of specified object.
		 * @param append Whether or not &quot;append&quot; mode is applied for writing. In &quot;append&quot; mode, content is added continuously to the end of the archive (file). Otherwise, the archive (file) is made empty and then content is put into the archive (file) from beginning.
		 */
		public AdapterWriteChannel(DataConfig config, boolean append) {
			this(config.getStoreUri(), append);
		}


		@Override
		public int read(ByteBuffer dst) throws IOException {
			return channel.read(dst);
		}


		@Override
		public int write(ByteBuffer src) throws IOException {
			return channel.write(src);
		}


		@Override
		public boolean isOpen() {
			return channel.isOpen();
		}


		@Override
		public void close() throws IOException {
			if (channel != null && channel.isOpen()) {
				try {
					channel.close();
				}
				catch (Throwable e) {LogUtil.trace(e);}
			}
			channel = null;
			
			if (adapter != null)
				adapter.close();
			adapter = null;
		}


		@Override
		protected void finalize() throws Throwable {
//			super.finalize();
			
			try {
				close();
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		
	}


}
