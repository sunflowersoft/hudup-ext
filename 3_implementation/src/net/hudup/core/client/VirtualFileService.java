package net.hudup.core.client;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.rmi.RemoteException;
import java.util.List;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.logistic.LogUtil;

/**
 * This is an implementation of service storage.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class VirtualFileService implements VirtualStorageService {

	
	/**
	 * Default constructor.
	 */
	public VirtualFileService() {
		
	}
	
	
	@Override
	public VirtualStorageUnit getRoot() throws RemoteException {
		File root = new File(Constants.WORKING_DIRECTORY);
		if (root.exists())
			return VirtualStorageUnit.create(new File(Constants.WORKING_DIRECTORY).getAbsoluteFile(), "/");
		else
			return null;
	}


	@Override
	public List<VirtualStorageUnit> list(VirtualStorageUnit parent) throws RemoteException {
		List<VirtualStorageUnit> children = Util.newList();
		if (!isStore(parent)) return children;
		
		Path base = Paths.get(parent.getBase());
		String path = parent.getPath();
		if (!path.endsWith("/")) path = path + "/";
		File[] files = parent.getPhysicAbsoluteFile().listFiles();
		for (File file : files) {
			VirtualStorageUnit unit = VirtualStorageUnit.create(base, path + file.getName());
			children.add(unit);
		}
		return children;
	}


	@Override
	public boolean isStore(VirtualStorageUnit unit) throws RemoteException {
		if (unit == null)
			return false;
		else
			return Files.isDirectory(unit.getPhysicAbsolutePath());
	}


	@Override
	public VirtualStorageUnit createStore(VirtualStorageUnit store) throws RemoteException {
		if (store == null) return null;
		
		try {
			Path storePath = Files.createDirectory(store.getPhysicAbsolutePath());
			if (storePath != null)
				return store;
			else
				return null;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}


	@Override
	public VirtualStorageUnit createFile(VirtualStorageUnit file) throws RemoteException {
		if (file == null) return null;
		
		try {
			Path filePath = Files.createFile(file.getPhysicAbsolutePath());
			if (filePath != null)
				return file;
			else
				return null;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}


	@Override
	public byte[] readFile(VirtualStorageUnit file) throws RemoteException {
		if (file == null) return null;
		
		try {
			return Files.readAllBytes(file.getPhysicAbsolutePath());
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return null;
	}


	@Override
	public boolean writeFile(VirtualStorageUnit file, byte[] data) throws RemoteException {
		if (file == null) return false;
		
		try {
			Path path = Files.write(file.getPhysicAbsolutePath(), data);
			return path != null;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return false;
	}


	@Override
	public VirtualStorageUnit rename(VirtualStorageUnit unit, String newName) throws RemoteException {
		if (unit == null || newName == null || newName.isEmpty()) return null;
		
		try {
			String path = unit.getPath();
			if (path.equals("/")) return null;
			int last = path.lastIndexOf("/");
			if (last < 0) return null;
			
			String newPath = last == 0 ? "/" + newName : path.substring(0, last + 1) + newName;
			VirtualStorageUnit dst = VirtualStorageUnit.create(new File(unit.getBase()), newPath);
			if (unit.getPhysicAbsoluteFile().renameTo(dst.getPhysicAbsoluteFile()))
				return dst;
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}

		return null;
	}

	
	@Override
	public boolean copy(VirtualStorageUnit src, VirtualStorageUnit dst) throws RemoteException {
		try {
			if (isStore(src)) {
				final Path srcPath = src.getPhysicAbsolutePath();
				final Path dstPath = dst.getPhysicAbsolutePath();
				Files.walkFileTree(src.getPhysicAbsolutePath(), new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						Path newdir = dstPath.resolve(srcPath.relativize(dir));
						Files.copy(dir, newdir, StandardCopyOption.REPLACE_EXISTING);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.copy(file, dstPath.resolve(srcPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
		                return FileVisitResult.CONTINUE;
					}

				});
				
				return true;
			}
			else {
				Path path = Files.copy(src.getPhysicAbsolutePath(), dst.getPhysicAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
				return path != null;
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return false;
	}


	@Override
	public boolean move(VirtualStorageUnit src, VirtualStorageUnit dst) throws RemoteException {
		try {
			if (isStore(src)) {
				final Path srcPath = src.getPhysicAbsolutePath();
				final Path dstPath = dst.getPhysicAbsolutePath();
				Files.walkFileTree(src.getPhysicAbsolutePath(), new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
						Path newdir = dstPath.resolve(srcPath.relativize(dir));
						Files.copy(dir, newdir, StandardCopyOption.REPLACE_EXISTING);
						return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
						Files.move(file, dstPath.resolve(srcPath.relativize(file)), StandardCopyOption.REPLACE_EXISTING);
		                return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						if (exc == null) {
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						}
						else {
							throw exc;
						}
					}
					
				});
				
				return true;
			}
			else {
				Path path = Files.move(src.getPhysicAbsolutePath(), dst.getPhysicAbsolutePath(), StandardCopyOption.REPLACE_EXISTING);
				return path != null;
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return false;
	}


	@Override
	public boolean delete(VirtualStorageUnit unit) throws RemoteException {
		try {
			if (isStore(unit)) {
				Files.walkFileTree(unit.getPhysicAbsolutePath(), new SimpleFileVisitor<Path>() {

					@Override
					public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
		                Files.delete(file);
		                return FileVisitResult.CONTINUE;
					}

					@Override
					public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
						if (exc == null) {
							Files.delete(dir);
							return FileVisitResult.CONTINUE;
						}
						else {
							throw exc;
						}
					}
					
				});
				
				return true;
			}
			else {
				Files.delete(unit.getPhysicAbsolutePath());
				return true;
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return false;
	}


}
