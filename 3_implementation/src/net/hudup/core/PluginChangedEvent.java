/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core;

import java.util.EventObject;
import java.util.List;

import net.hudup.core.alg.Alg;
import net.hudup.core.logistic.ui.PluginStorageManifest;

/**
 * {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For example, a register table manages recommendation algorithms (recommenders) whereas another manages metrics for evaluating recommenders.
 * {@link PluginStorageManifest} which is the graphic user interface (GUI) allows users to manage {@link PluginStorage}.
 * Every time {@link PluginStorage} was changed, this event {@link PluginChangedEvent} is issued and dispatched to a listener {@link PluginChangedListener}.
 * Later on, {@link PluginChangedListener} can do some tasks in its method {@link PluginChangedListener#pluginChanged(PluginChangedEvent)}.
 * Please pay attention that such {@link PluginChangedListener} must be registered with {@link PluginStorageManifest} before to receive {@link PluginChangedEvent}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class PluginChangedEvent extends EventObject {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


//	/**
//	 * Type of plug-in changed event.
//	 * @author Loc Nguyen
//	 * @version 10.0
//	 */
//	public static enum Type {
//		
//		/**
//		 * Plug-in storage changing.
//		 */
//		doing,
//		
//		/**
//		 * Plug-in storage changed.
//		 */
//		done
//	}

	
//	/**
//	 * Type of setting up event. 
//	 */
//	protected Type type = Type.doing;

	
	/**
	 * Constructor with specified source and type.
	 * @param source the source to issue this event. As usual, it is {@link PluginStorageManifest} which is the graphic user interface (GUI) allows users to manage {@link PluginStorage}.
	 * Note, {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
	 * For example, a register table manages recommendation algorithms (recommenders) whereas another manages metrics for evaluating recommenders.
	 */
	public PluginChangedEvent(Object source) {
		super(source);
	}

	
//	/**
//	 * Getting type.
//	 * @return type.
//	 */
//	public Type getType() {
//		return type;
//	}
	
	
}



/**
 * This class represents extended plug-in changed event.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@Deprecated
class PluginChangedEvent2 extends PluginChangedEvent {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of removed algorithm.
	 */
	protected List<Alg> removedAlgList = Util.newList();
	
	
	/**
	 * Constructor with specified source.
	 * @param source specified source.
	 */
	public PluginChangedEvent2(Object source) {
		super(source);
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with specified source and list of removed algorithm.
	 * @param source specified source.
	 * @param removedAlgList list of removed algorithm.
	 */
	public PluginChangedEvent2(Object source, List<Alg> removedAlgList) {
		super(source);
		// TODO Auto-generated constructor stub
		
		if (removedAlgList != null)
			this.removedAlgList = removedAlgList;
	}

	
	/**
	 * Getting list of removed algorithm.
	 * @return list of removed algorithm.
	 */
	public List<Alg> getRemovedAlgList() {
		return removedAlgList;
	}
	
	
}
