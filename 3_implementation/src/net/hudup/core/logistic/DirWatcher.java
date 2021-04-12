/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import static java.nio.file.LinkOption.NOFOLLOW_LINKS;
import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.hudup.core.Util;

/**
 * This class implements directory watching service. Please read the tutorial Watching a Directory for Changes available at <a href="https://docs.oracle.com/javase/tutorial/essential/io/notification.html">https://docs.oracle.com/javase/tutorial/essential/io/notification.html</a>
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class DirWatcher extends Timer2 {
	
	
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
	 * Constructor with period.
	 * @param period period time in miliseconds.
	 * @param recursive recursive mode.
	 */
    public DirWatcher(long period, boolean recursive) {
    	super(period, period);
    	this.recursive = recursive;
    }

    
	@Override
	public synchronized boolean start() {
    	if (isStarted()) return false;
    	
    	clear();
    	
    	try {
			watcher = FileSystems.getDefault().newWatchService();
		}
    	catch (Exception e) {LogUtil.trace(e);}

    	if (watcher != null)
    		return super.start();
    	else
    		return false;
	}


	
    @Override
	protected void task() {
    	synchronized (watcher) {
			task0();
		}
	}

    
    /**
     * Task definition.
     */
    private void task0() {
    	WatchKey key = null;
    	try {
    		key = watcher.poll(period, TimeUnit.MILLISECONDS);
    	}
    	catch (Throwable e) {}
    	
    	if (key == null) return;
    	Path dir = keys.get(key);
        if (dir == null) {
            System.err.println("WatchKey not recognized!!");
            return;
        }
        
        for (WatchEvent<?> event: key.pollEvents()) {
        	WatchEvent.Kind<?> kind = event.kind();
            if (kind == OVERFLOW) continue;

            @SuppressWarnings("unchecked")
            Path subPath = ((WatchEvent<Path>) (event)).context();
            Path entry = dir.resolve(subPath);

            System.out.format("%s: %s\n", event.kind().name(), entry);

            if (kind == ENTRY_CREATE) {
            	if (recursive && Files.isDirectory(entry, NOFOLLOW_LINKS))
            		registerAll0(entry);
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
     * Register to watch specified directory.
     * @param watchedDir specified directory.
     * @return true if registering is successful.
     */
    public boolean register(Path watchedDir) {
    	if (watcher == null || watchedDir == null) return false;
    	
    	synchronized (watcher) {
        	if (recursive)
        		return registerAll0(watchedDir);
        	else
        		return register0(watchedDir);
		}
    }
    
    
    /**
     * Registering watched directory and all its sub-directories.
     * @param dir watched directory.
     * @return true if registering is successful.
     */
    private boolean registerAll0(Path startDir) {
    	try {
    		System.out.format("Scanning %s ...\n", startDir);
			Files.walkFileTree(startDir, new SimpleFileVisitor<Path>() {
				 
				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					register0(dir);
					return FileVisitResult.CONTINUE;
				}
			     
			});
			System.out.println("Done.");
			return true;
    	}
    	catch (Exception e) {
    		LogUtil.trace(e);
    	}
    	
    	return false;
    }
    
    
    /**
     * Registering watched directory.
     * @param dir watched directory.
     * @return true if registering is successful.
     */
    private boolean register0(Path dir) {
    	try {
			WatchKey key = dir.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
			Path prev = keys.get(key);
			if (prev == null)
				System.out.format("Register: %s\n", dir);
			else {
				if (!dir.equals(prev)) System.out.format("Update: %s -> %s\n", prev, dir);
			}
			
			keys.put(key, dir);
			return true;
    	}
    	catch (Exception e) {
    		LogUtil.trace(e);
    	}
    	
    	return false;
    }
    
    
	@Override
	protected void clear() {
    	if (watcher != null) {
    		try {watcher.close();}
    		catch (Throwable e) {LogUtil.trace(e);}
    		watcher = null;
    	}
    	
    	keys.clear();
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
    
    
}
