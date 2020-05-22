/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.channels.ByteChannel;
import java.nio.charset.Charset;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;

/**
 * This class is the abstract URI associator which defines most of simple methods of the interface {@link UriAssoc}.
 * @author Loc Nguyen
 * @version 11.0
 *
 */
public abstract class UriAssocAbstract implements UriAssoc {

	
	/**
	 * The root store (root directory) of this abstract URI associator.
	 * {@link UriAssocAbstract} uses {@link Path} instead of {@link xURI}. It is possible to consider {@link Path} is particular case of {@link xURI}.
	 */
	protected Path store = null;
	
	
	/**
	 * Default constructor
	 */
	public UriAssocAbstract() {
		
	}
	
	
	/**
	 * Normalizing the specified URI. For example, the URI referring to compressed file contains character &quot;!&quot; and so it is necessary to remove such character &quot;!&quot; from the URI so that we get a normalized URI. 
	 * @param uri specified URI
	 * @return normalized URI
	 */
	private static xURI normalize(xURI uri) {
		if (uri == null)
			return null;
		
		if (DataDriver.isCompressed(uri)) {
			xURI newUri = null;
			try {
				xSubURI subUri = DataDriver.createSubURI(uri);
				String subUriText = subUri.brief.toString();
				subUriText = subUriText.replace("!/", "/");
				subUriText = subUriText.replace("!", "");
				newUri = xURI.create(subUriText);
			}
			catch (Exception e) {
				LogUtil.trace(e);
				newUri = null;
			}
			
			return newUri;
		}
		else
			return uri;
	}

	
	@Override
	public boolean connect(DataConfig config) {
		close();
		
		xURI uri = normalize(config.getStoreUri());
		Path path = newPath(uri);
		if (Files.isDirectory(path) || isArchive(uri) || DataDriver.isCompressed(config.getStoreUri())) {
			store = path;
			return true;
		}
		else {
			store = path.getParent();
			if (store != null && !Files.exists(store)) {
				try {
					Files.createDirectories(store);
				} 
				catch (IOException e) {
					LogUtil.trace(e);
					return false;
				}
			}
			
			return true;
		}
	}

	
	@Override
	public boolean exists(xURI uri) {
		uri = normalize(uri);
		Path path = newPath(uri);
		return Files.exists(path);
	}

	
	@Override
	public boolean isStore(xURI uri) {
		uri = normalize(uri);
		Path path = newPath(uri);
		if (store != null && path.equals(store))
			return true;
		else
			return Files.isDirectory(path);
	}

	
	@Override
	public boolean create(xURI uri, boolean isStore) {
		uri = normalize(uri);
		if (exists(uri))
			return false;

		Path path = newPath(uri);
		try {
			if (isStore)
				return Files.createDirectories(path) != null;
			else {
//				try {
					Files.newOutputStream(path, 
							StandardOpenOption.CREATE, 
							StandardOpenOption.TRUNCATE_EXISTING).close();
					return true;
//				}
//				catch (Exception e) {
//					System.out.println("Impossible to write this special file but it can be created.");
//					Files.deleteIfExists(path);
//					return Files.createFile(path) != null;
//				}
			}
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}

	
	@Override
	public boolean clearContent(xURI uri, UriFilter filter) {
		uri = normalize(uri);
		if (!exists(uri))
			return false;
		else if (isStore(uri)) {
			List<xURI> uriList = getUriList(uri, filter);
			for (xURI item : uriList) {
				if (isStore(item))
					clearContent(item, filter);
				
				Path path = newPath(item);
				try {
					Files.deleteIfExists(path);
				} 
				catch (Throwable e) {
					LogUtil.trace(e);
				}
			}
			
			return true;
		}
		else {
			Path path = newPath(uri);
			try {
				Files.deleteIfExists(path);
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}

			return create(uri, false);
		}
	}

	
	@Override
	public boolean delete(xURI uri, UriFilter filter) {
		uri = normalize(uri);
		if (!exists(uri))
			return false;
		if (isStore(uri))
			clearContent(uri, filter);
		
		Path path = newPath(uri);
		try {
			Files.deleteIfExists(path);
			return true;
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}

	
	@Override
	public boolean copy(xURI src, xURI dst, final boolean moved, final UriFilter filter) {
		src = normalize(src);
		dst = normalize(dst);
		if (isStore(src)) {
			if (!exists(dst))
				create(dst, true);
			else if (!isStore(dst))
				return false;
			
			final xURI finalDst = dst;
			uriListProcess(
					this,
					src, 
					filter, 
					new UriProcessor() {
						
						@Override
						public void uriProcess(xURI uri) throws Exception {
							xURI dstItem = finalDst.concat(uri.getLastName()); 
							copy(uri, dstItem, moved, filter);
						}
					});
			
			if (moved)
				delete(src, null);
			return true;
		}
		else if (exists(dst) && isStore(dst)) {
			xURI dstItem = dst.concat(src.getLastName()); 
			return copy(src, dstItem, moved, filter);
		}
		else {
			Path srcPath = newPath(src);
			Path dstPath = newPath(dst);
			try {
				
				if (moved) {
					if (exists(dst))
						return Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING) != null;
					else
						return Files.move(srcPath, dstPath) != null;
				}
				else {
					if (exists(dst))
						return Files.copy(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING) != null;
					else
						return Files.copy(srcPath, dstPath) != null;
				}
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			return false;
		} // end if
		
		
	}


	@Override
	public boolean rename(xURI src, xURI dst) {
		src = normalize(src);
		dst = normalize(dst);
		Path srcPath = newPath(src);
		Path dstPath = newPath(dst);
		
		try {
			return Files.move(srcPath, dstPath, StandardCopyOption.REPLACE_EXISTING) != null;
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return false;
	}

	
	@Override
	public List<xURI> getUriList(xURI store, final UriFilter filter) {
		store = normalize(store);
		if (!exists(store))
			return Util.newList();
		
		final List<Path> pathList = Util.newList();

		Path storePath = newPath(store);
		pathListProcess(
			storePath, 
			new DirectoryStream.Filter<Path>() {
				
				@Override
				public boolean accept(Path entry) throws IOException {
					if (filter == null)
						return true;
					else
						return filter.accept(xURI.create(entry));
				}
			},
			
			new PathProcessor() {
				
				@Override
				public void pathProcess(Path path) throws Exception {
					pathList.add(path);
				}
			});
		
		
		List<xURI> uriList = Util.newList();
		for (Path path : pathList)
			uriList.add(xURI.create(path));
		
		return uriList;
	}

	
	@Override
	public InputStream getInputStream(xURI uri) {
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			return Files.newInputStream(path);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}

	
	@Override
	public OutputStream getOutputStream(xURI uri, boolean append) {
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			if (append)
				return Files.newOutputStream(path, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.APPEND);
			else
				return Files.newOutputStream(path, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.TRUNCATE_EXISTING);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}

	
	@Override
	public Reader getReader(xURI uri) {
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			return Files.newBufferedReader(path, Charset.defaultCharset());
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}

	
	@Override
	public Writer getWriter(xURI uri, boolean append) {
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			if (append)
				return Files.newBufferedWriter(path, Charset.defaultCharset(), 
						StandardOpenOption.CREATE, 
						StandardOpenOption.APPEND);
			else
				return Files.newBufferedWriter(path, Charset.defaultCharset(), 
						StandardOpenOption.CREATE, 
						StandardOpenOption.TRUNCATE_EXISTING);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}

	
	@Override
	public ByteChannel getReadChannel(xURI uri) {
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			return Files.newByteChannel(path, StandardOpenOption.READ);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}


	@Override
	public ByteChannel getWriteChannel(xURI uri, boolean append) {
		uri = normalize(uri);
		Path path = newPath(uri);
		try {
			if (append)
				return Files.newByteChannel(path, 
						StandardOpenOption.WRITE, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.APPEND);
			else
				return Files.newByteChannel(path, 
						StandardOpenOption.WRITE, 
						StandardOpenOption.CREATE, 
						StandardOpenOption.TRUNCATE_EXISTING);
		} 
		catch (Throwable e) {
			LogUtil.trace(e);
		}
		
		return null;
	}


	@Override
	public xURI getStoreOf(xURI uri) {
		uri = normalize(uri);
		Path path = newPath(uri);
		Path parent = path.getParent();
		if (parent == null)
			return null;
		else if (Files.isDirectory(parent))
			return xURI.create(parent);
		else
			return getStoreOf(xURI.create(parent));
	}

	
	/**
	 * Retrieving current store (current directory) such as (.)
	 * @return current store (current directory)
	 */
	public xURI getCurrentStore() {
		return xURI.create(new File("."));
	}
	
	
	/**
	 * Firstly, this static method retrieves a list of URI (s) referring objects inside the specified store with regard to specified filter. For each retrieved URI, some task is processed on the object referred by such URI and so the specified {@link UriProcessor} is responsible for doing such task.
	 * This method also uses the specified URI associator {@link UriAssoc} as the main associator.
	 * @param assoc specified URI associator {@link UriAssoc} as the main associator.
	 * @param store URI of specified store
	 * @param filter Specified filter represented by {@link UriFilter}
	 * @param processor Specified {@link UriProcessor} is responsible for doing such task on the object referred by each retrieved URI
	 */
	public static void uriListProcess(UriAssoc assoc, xURI store, UriFilter filter, UriProcessor processor) {
		store = normalize(store);
		
		if (!assoc.exists(store))
			return;
		
		List<xURI> uriList = assoc.getUriList(store, filter);
		
		for (xURI uri : uriList) {
			
			try {
				
				processor.uriProcess(uri);
				
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			} 
			
		} // end for
		
	}

	
	/**
	 * This interface is used for processing some task on specified {@link Path}. Please see its method {@link #pathProcess(Path)}.
	 * This interface is the input parameter of the method {@link UriAssocAbstract#pathListProcess(Path, java.nio.file.DirectoryStream.Filter, PathProcessor)} of the abstract URI associator {@link UriAssocAbstract}.
	 * @author Loc Nguyen
	 * @version 11.0
	 *
	 */
	private static interface PathProcessor {

		
		/**
		 * Processing some task on specified path. Programmer is responsible for defining this method according to some purpose.
		 * @param path Specified path represented by {@link Path}. It is possible to consider {@link Path} is particular case of {@link xURI}. 
		 * @throws Exception
		 */
		void pathProcess(Path path) throws Exception;
		
		
	}
	
	
	/**
	 * Firstly, this method retrieves a list of path (s) referring objects inside the specified store with regard to specified filter. For each retrieved path, some task is processed on the object referred by such path and so the specified {@link PathProcessor} is responsible for doing such task.
	 * @param store Specified store represented by {@link Path}. It is possible to consider {@link Path} is particular case of {@link xURI}.
	 * @param filter Specified filter represented by {@link UriFilter}
	 * @param processor Specified {@link PathProcessor} is responsible for doing such task on the object referred by each retrieved path
	 */
	private static void pathListProcess(
			Path store, 
			DirectoryStream.Filter<Path> filter, 
			PathProcessor processor) {
		
		if (!Files.exists(store))
			return;
		
		try {
			if (!Files.isDirectory(store))
				return;
		} 
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		DirectoryStream<Path> ds = null;
		try {
			ds = filter != null ? 
					Files.newDirectoryStream(store, filter) : Files.newDirectoryStream(store);
		} 
		catch (Exception e) {
			LogUtil.trace(e);
			return;
		}
		
		for (Path path : ds) {
			
			try {
				
				processor.pathProcess(path);
				
			}
			catch (Exception e) {
				LogUtil.trace(e);
			} 
		}
		
		try {
			ds.close();
		} 
		catch (IOException e) {
			LogUtil.trace(e);
		}
		
	}

	
	@Override
	public xURI chooseDefaultUri(Component comp, boolean open, xURI curStore) {
		curStore = normalize(curStore);
		
		return chooseUri(
				comp, 
				open, 
				new String[] {
						Constants.DEFAULT_EXT,
						"xls"
					}, 
				new String[] {
						"Hudup file (*." + Constants.DEFAULT_EXT + ")",
						"Excel 97-2003 (*.xls)"
					},
				curStore,
				null);
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
	protected abstract ChosenUriResult chooseUriResult(
			Component comp, 
			boolean open, 
			final String[] exts, 
			final String[] descs,
			xURI curStore);

		
	/**
	 * This class represents result of choosing file.
	 * It contains the URI and extension of chosen archive (file).
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	protected static class ChosenUriResult {
		
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
	protected static class ChosenFileFilter extends FileFilter {

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

	
	@Override
	public void close() {
		this.store = null;
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
	 * Creating default URI associator.
	 * @return default URI associator.
	 */
	public static UriAssoc createUriAssoc() {
		return new UriAssocAbstract() {

			@Override
			public Path newPath(xURI uri) {
				return Paths.get(uri.getURI());
			}

			@Override
			public Path newPath(String path) {
				return Paths.get(path);
			}

			@Override
			public boolean isArchive(xURI uri) {
				return DataDriver.isCompressed(uri);
			}

			@Override
			public xURI chooseStore(Component comp) {
				
				JFileChooser uc = newUriChooser();
				
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
			
			@Override
			protected ChosenUriResult chooseUriResult(Component comp,
					boolean open, String[] exts, String[] descs, xURI curStore) {
				
				JFileChooser uc = newUriChooser(); //This is so-called choice dialog
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
		        
		        File file = uc.getSelectedFile();
		        FileFilter filter = uc.getFileFilter();
		        if (file == null)
		        	return null;
		        
		        xURI uri = null;
		        if (file.isFile()) {
		        	File parent = file.getParentFile();
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
			 * Creating the file chooser.The most important function of such chooser is to show a graphic user interface (GUI) allowing users to select files they want.
			 * @return file chooser.
			 */
			private JFileChooser newUriChooser() {
				return new JFileChooser();
			}

		};
		
	}


}
