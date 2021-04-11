/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.hudup.core.logistic.LogUtil;

/**
 * This class represents a storage unit on server.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class VirtualStorageUnit implements Serializable {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Physical base path.
	 */
	protected URI physicBase = null;
	
	
	/**
	 * Base.
	 */
	protected URI base = null;
	
	
	/**
	 * Path.
	 */
	protected String path = null;
	
	
	/**
	 * Construction with base and path.
	 * @param base base.
	 * @param path path.
	 * @param physicBase physical base, which can be null.
	 */
	protected VirtualStorageUnit(URI base, String path, URI physicBase) {
		this.base = base;
		this.path = path;
		this.physicBase = physicBase;
	}
	
	
	/**
	 * Getting physical base.
	 * @return path as physical base.
	 */
	public URI getPhysicBase() {
		return physicBase;
	}
	
	
	/**
	 * Getting base.
	 * @return base.
	 */
	public URI getBase() {
		return base;
	}
	
	
	/**
	 * Getting path.
	 * @return path.
	 */
	public String getPath() {
		return path;
	}
	
	
	/**
	 * Getting last name.
	 * @return last name.
	 */
	public String getLastName() {
		if (path.equals("/"))
			return path;
		else
			return path.substring(path.lastIndexOf("/") + 1);
	}
	
	
	/**
	 * Getting absolute URI.
	 * @param base base.
	 * @param path path.
	 * @return absolute URI.
	 */
	private static URI getAbsoluteUri(URI base, String path) {
		try {
			if (path.isEmpty() || path.equals("/")) return base;
			path = path.replaceAll(" ", "%20");
			
//			List<String> texts = TextParserUtil.split(path, "/", null);
//			if (texts.size() == 0) return base;
//			StringBuffer buffer = new StringBuffer();
//			for (int i = 0; i < texts.size(); i++) {
//				if (i > 0) buffer.append("/");
//				buffer.append(URLEncoder.encode(texts.get(i), "UTF-8"));
//			}
//			path = buffer.toString();
			
			if (path.startsWith("/"))
				return base.resolve(path.substring(1));
			else
				return base.resolve(path);
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
	
		return null;
	}
	
	
	/**
	 * Getting absolute URI.
	 * @return absolute URI.
	 */
	public URI getAbsoluteUri() {
		return getAbsoluteUri(base, path);
	}
	
	
	/**
	 * Getting absolute physical URI.
	 * @return absolute physical URI.
	 */
	public URI getPhysicAbsoluteUri() {
		return getAbsoluteUri(physicBase != null ? physicBase : base, path);
	}
	
	
	/**
	 * Getting absolute physical file.
	 * @return absolute physical file.
	 */
	public File getPhysicAbsoluteFile() {
		return new File(getPhysicAbsoluteUri());
	}
	
	
	/**
	 * Getting absolute physical path.
	 * @return absolute physical path.
	 */
	public Path getPhysicAbsolutePath() {
		return Paths.get(getPhysicAbsoluteUri());
	}

	
	/**
	 * Getting parent unit.
	 * @return parent unit.
	 */
	public VirtualStorageUnit getParent() {
		if (path.equals("/")) return null;
		
		int last = path.lastIndexOf("/");
		if (last == 0)
			return new VirtualStorageUnit(base, "/", physicBase);
		else
			return new VirtualStorageUnit(base, path.substring(0, last), physicBase);
	}
	
	
	/**
	 * Getting root unit.
	 * @return root unit.
	 */
	public VirtualStorageUnit getRoot() {
		return new VirtualStorageUnit(base, "/", physicBase);
	}
	
	
	/**
	 * Contacting with other path.
	 * @param otherPath other path.
	 * @return contacted unit.
	 */
	public VirtualStorageUnit contact(String otherPath) {
		otherPath = normalizePath(otherPath);
		if (otherPath.equals("/")) 
			return this;
		else
			return new VirtualStorageUnit(base, path.equals("/") ? otherPath : path + otherPath, physicBase);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof VirtualStorageUnit))
			return false;
		else
			return getPhysicAbsoluteUri().equals(((VirtualStorageUnit)obj).getPhysicAbsoluteUri());
	}


	/**
	 * Normalizing path.
	 * @param path specified path.
	 * @return normalized path.
	 */
	private static String normalizePath(String path) {
		if (path == null) return null;
		
		path = path.startsWith("/") ? path : "/" + path;
		int lastIndex = path.lastIndexOf("/");
		if (lastIndex > 0 && lastIndex == path.length() - 1) path = path.substring(0, lastIndex);
		
		return path;
	}
	
	
	/**
	 * Creating storage item with base and path.
	 * @param base URI as base.
	 * @param path path.
	 * @param physicBase URI as physical base, which can be null.
	 * @return storage item created with URI base and path.
	 */
	public static VirtualStorageUnit create(URI base, String path, URI physicBase) {
		if (base == null || path == null) return null;
		return new VirtualStorageUnit(base, normalizePath(path), physicBase != null ? physicBase : null);
	}
	
	
	/**
	 * Creating storage item with base and path.
	 * @param base URI as base.
	 * @param path path.
	 * @return storage item created with URI base and path.
	 */
	public static VirtualStorageUnit create(URI base, String path) {
		return create(base, path, null);
	}
	
	
	/**
	 * Creating storage item with base and path.
	 * @param base file system base.
	 * @param path string as path.
	 * @param physicBase physical base, which can be null.
	 * @return storage item created with file system base and path.
	 */
	public static VirtualStorageUnit create(File base, String path, File physicBase) {
		if (base == null || path == null || !base.isDirectory()) return null;
		return new VirtualStorageUnit(base.getAbsoluteFile().toURI(), normalizePath(path), physicBase != null ? physicBase.getAbsoluteFile().toURI() : null);
	}
	
	
	/**
	 * Creating storage item with base and path.
	 * @param base file system base.
	 * @param path string as path.
	 * @return storage item created with file system base and path.
	 */
	public static VirtualStorageUnit create(File base, String path) {
		return create(base, path, null);
	}
	
	
	/**
	 * Creating storage item with base and path.
	 * @param base path as base.
	 * @param path string as path.
	 * @param physicBase physical base, which can be null.
	 * @return storage item created with path base and path.
	 */
	public static VirtualStorageUnit create(Path base, String path, Path physicBase) {
		if (base == null || path == null) return null;
		return new VirtualStorageUnit(base.toAbsolutePath().toUri(), normalizePath(path), physicBase != null ? physicBase.toAbsolutePath().toUri() : null);
	}
	
	
	/**
	 * Creating storage item with base and path.
	 * @param base path as base.
	 * @param path string as path.
	 * @return storage item created with path base and path.
	 */
	public static VirtualStorageUnit create(Path base, String path) {
		return create(base, path, null);
	}
	
	
}
