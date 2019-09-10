package net.hudup.data.ui;


/**
 * This class is Java table to show rating value with YRoller (Yellow Roller) algorithm.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
@Deprecated
public class YRollerDatasetTable extends RatingValueTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public YRollerDatasetTable() {
		super();
		// TODO Auto-generated constructor stub
	}


	@Override
	public RatingValueTM newTableModel() {
		// TODO Auto-generated method stub
		return new YRollerDatasetTM();
	}

}



/**
 * This class is table model of {@link YRollerDatasetTable} for YRoller (Yellow Roller) algorithm.
 * @author Loc Nguyen
 * @version 10.0
 */
class YRollerDatasetTM extends RatingValueTM {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public YRollerDatasetTM() {
		super();
		// TODO Auto-generated constructor stub
	}

	
}
