package net.hudup.core.data.ui;

import java.awt.Component;
import java.util.EventListener;
import java.util.EventObject;

import net.hudup.core.data.ProviderAssoc;


/**
 * This class represents a table for showing data in a unit.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public interface UnitTable {

	
	/**
	 * Updating the unit.
	 * @param providerAssoc specified provider associator.
	 * @param unit specified unit.
	 */
	void update(ProviderAssoc providerAssoc, String unit);
	
	
	/**
	 * Clear table.
	 */
	void clear();
	
	
	/**
	 * Refresh table.
	 */
	void refresh();
	
	
	/**
	 * Getting selected row.
	 * @return selected row.
	 */
	int getSelectedRow();
	
	
	/**
	 * Getting value at specified row and column.
	 * @param row specified row.
	 * @param column specified column.
	 * @return value at specified row and column.
	 */
	Object getValueAt(int row, int column);
	
	
	/**
	 * Getting the graphic user interface (GUI) associated with this table.
	 * @return {@link Component} associated with this table.
	 */
	Component getComponent();
	
	
	/**
	 * Add the selection changed listener to the end of listener list.
	 * @param listener specified selection changed listener.
	 */
	void addSelectionChangedListener(SelectionChangedListener listener);

    
	/**
	 * Remove the selection changed listener from the listener list.
	 * @param listener specified selection changed listener.
	 */
    void removeSelectionChangedListener(SelectionChangedListener listener);


	/**
	 * This interface is used to response to a selection changed event.
	 * @author Loc Nguyen
	 * @version 11.0
	 *
	 */
	interface SelectionChangedListener extends EventListener {
		
		/**
		 * The method responses to a data changed event.
		 * @param evt specified data changed event.
		 */
		void respond(SelectionChangedEvent evt);
	}

	
    /**
	 * This class represents an event when selection is changed.
	 * @author Loc Nguyen
	 * @version 11.0
	 *
	 */
	class SelectionChangedEvent extends EventObject {

		/**
		 * Serial UID.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Constructor with specified which is often unit table.
		 * @param source specified source.
		 */
		public SelectionChangedEvent(Object source) {
			super(source);
			// TODO Auto-generated constructor stub
		}

	}
	
	
}



