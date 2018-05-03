package net.hudup.temp;

import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;

/**
 * This class represents a parameter as a vector, which is an extension of {@link Parameter} class.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RealVectorParameter extends Parameter {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public RealVectorParameter() {
		// TODO Auto-generated constructor stub
	}

	
	/**
	 * Constructor with size and initial value.
	 * @param size specified size of this vector parameter.
	 * @param initialValue specified initial value
	 */
	public RealVectorParameter(int size, double initialValue) {
		// TODO Auto-generated constructor stub
		AttributeList attRef = new AttributeList();
		for (int i = 0; i < size; i++) {
			Attribute att = new Attribute("" + i, Type.real);
			attRef.add(att);
		}
		setAttRef(attRef);
		
		//Initialize parameter entries as zeroes.
		for (int i = 0; i < size; i++) {
			setValue(i, initialValue);
		}
	}


}
