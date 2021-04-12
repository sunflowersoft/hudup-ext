/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import net.hudup.core.Constants;
import net.hudup.core.logistic.DirWatcher;

/**
 * This class represents server watcher.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public abstract class Watcher extends DirWatcher {

	
	/**
	 * Timer period in second.
	 */
	public static int PERIOD = 1;
	
	
	/**
	 * Default constructor.
	 */
	public Watcher() {
		super(PERIOD*1000, false);
		setPriority(Priority.min);
	}
	
	
	@Override
	public synchronized boolean start() {
		boolean ret = super.start();
		if (ret) register(Paths.get(Constants.LIB_DIRECTORY));
		return ret;
	}

	
	@Override
	protected boolean onCreate(Path entry) {
		if (Files.isDirectory(entry))
			return onLoadLib(entry);
		else
			return false;
	}

	
	@Override
	protected boolean onDelete(Path entry) {
		return false;
	}

	
	@Override
	protected boolean onModify(Path entry) {
		return onLoadLib(entry);
	}

	
	/**
	 * Event-driven method for loading library.
	 * @param libPath library path.
	 * @return true if loading is successful.
	 */
	protected abstract boolean onLoadLib(Path libPath);
	
	
}
