/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import static java.nio.file.StandardWatchEventKinds.*;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import static java.nio.file.LinkOption.*;

import net.hudup.core.Util;

/**
 * This class implements directory watching service. Please read the tutorial Watching a Directory for Changes available at <a href="https://docs.oracle.com/javase/tutorial/essential/io/notification.html">https://docs.oracle.com/javase/tutorial/essential/io/notification.html</a>
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class DirWatcher extends AbstractRunner {
	
	
	/**
	 * Watching service.
	 */
    protected WatchService watcher = null;
    
    
    /**
     * Watched keys. Each key corresponds to a watched directory.
     */
    protected Map<WatchKey, Path> keys = Util.newMap();
    
    
    /**
     * Recursive mode.
     */
    protected boolean recursive = false;
    
    
    /**
     * Default constructor.
     */
    public DirWatcher() {

    }
    
    
    /**
     * Start watching the specified directory.
     * @param watchedDir specified directory.
     * @param recursive recursive mode.
     * @return true if watching is successful.
     */
    public synchronized boolean start(Path watchedDir, boolean recursive) {
    	if (isStarted()) return false;
    	
    	clear();
    	
    	try {
			watcher = FileSystems.getDefault().newWatchService();
		}
    	catch (Exception e) {LogUtil.trace(e);}

    	if (recursive)
    		registerAll(watchedDir);
    	else
    		register(watchedDir);
    	this.recursive = recursive;
    	
    	return super.start();
    }
    
    
	@Override
	public synchronized boolean start() {
		return start(Paths.get("."), false);
	}


    @Override
	protected void task() {
    	WatchKey key = watcher.poll();
    	if (key == null) return;
    	Path dir = keys.get(key);
        if (dir == null) {
            System.err.println("WatchKey not recognized!!");
            return;
        }
        
        try {
            Thread.sleep(1);
		} catch (Throwable e) {LogUtil.trace(e);}
        
        for (WatchEvent<?> event: key.pollEvents()) {
        	WatchEvent.Kind<?> kind = event.kind();
            if (kind == OVERFLOW) continue;

            @SuppressWarnings("unchecked")
            Path subPath = ((WatchEvent<Path>) (event)).context();
            Path entry = dir.resolve(subPath);

            System.out.format("%s: %s\n", event.kind().name(), entry);

            if (kind == ENTRY_CREATE) {
            	if (recursive && Files.isDirectory(entry, NOFOLLOW_LINKS))
            		registerAll(entry);
            	else {
            		onCreate(entry);
            	}
            }
            else if (kind == ENTRY_DELETE) {
        		onDelete(entry);
            }
            else if (kind == ENTRY_MODIFY) {
        		onModify(entry);
            }
        }

        //Reset key
        boolean valid = key.reset();
        if (!valid) keys.remove(key);
	}

    
    /**
     * Event-driven method for entry creation.
     * @param entry event-driven entry.
     * @return true if process is success.
     */
    protected abstract boolean onCreate(Path entry);
    
    
    /**
     * Event-driven method for entry deletion.
     * @param entry event-driven entry.
     * @return true if process is success.
     */
    protected abstract boolean onDelete(Path entry);
    
    
    /**
     * Event-driven method for entry modification.
     * @param entry event-driven entry.
     * @return true if process is success.
     */
    protected abstract boolean onModify(Path entry);
    
    
    /**
     * Registering watched directory and all its sub-directories.
     * @param dir watched directory.
     */
    private void registerAll(Path startDir) {
    	try {
    		System.out.format("Scanning %s ...\n", startDir);
			Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
				 
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					register(dir);
					return FileVisitResult.CONTINUE;
				}
			     
			});
			System.out.println("Done.");
    	}
    	catch (Exception e) {
    		LogUtil.trace(e);
    	}
    }
    
    
    /**
     * Registering watched directory.
     * @param dir watched directory.
     */
    private void register(Path dir) {
    	try {
			WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			Path prev = keys.get(key);
			if (prev == null)
				System.out.format("Register: %s\n", dir);
			else {
				if (!dir.equals(prev)) System.out.format("Update: %s -> %s\n", prev, dir);
			}
			
			keys.put(key, dir);
    	}
    	catch (Exception e) {
    		LogUtil.trace(e);
    	}
    }
    
    
	@Override
	protected void clear() {
    	if (watcher != null) {
    		try {watcher.close();}
    		catch (IOException e) {LogUtil.trace(e);}
    		watcher = null;
    	}
		recursive = false;
	}


}
