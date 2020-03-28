/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data.ctx;

import java.io.Serializable;

import net.hudup.core.Constants;
import net.hudup.core.Util;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.LogUtil;

/**
 * This class is default implementation of context value.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class ContextValueImpl implements ContextValue {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal value.
	 */
	private Serializable value = null;
			
	
	/**
	 * Internal quantized value.
	 */
	private double quantizedValue = Constants.UNUSED;
	
	
	/**
	 * Constructor with value and quantized value.
	 * @param value specified value.
	 * @param quantizedValue quantized value.
	 */
	private ContextValueImpl(Serializable value, double quantizedValue) {
		this.value = value;
		this.quantizedValue = quantizedValue;
	}
	
	
	@Override
	public Serializable getValue() {
		// TODO Auto-generated method stub
		return value;
	}


	@Override
	public boolean isQuantized() {
		// TODO Auto-generated method stub
		return Util.isUsed(quantizedValue);
	}


	@Override
	public double getQuantizedValue() {
		// TODO Auto-generated method stub
		return quantizedValue;
	}


	@Override
	public Object clone() {
		// TODO Auto-generated method stub
		return new ContextValueImpl( (Serializable) Util.clone(value), quantizedValue);
	}


	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof ContextValue))
			return false;
		
		ContextValue that = (ContextValue)obj;
		if (this.isQuantized()) {
			if (that.isQuantized())
				return this.getValue().equals(that.getValue()) && 
						this.getQuantizedValue() == that.getQuantizedValue();
			else
				return false;
		}
		else {
			if (that.isQuantized())
				return false;
			else
				return this.getValue().equals(that.getValue());
		}
		
	}
	
	
	/**
	 * Create context value from attribute and specified value.
	 * @param attribute specified attribute.
	 * @param value specified value.
	 * @return context value from attribute and specified value.
	 */
	public static ContextValue create(Attribute attribute, Serializable value) {
		
		Serializable newValue = null;
		try {
			newValue = (Serializable) Profile.createValue(attribute, value);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
			newValue = null;
		}
		if (newValue == null)
			return null;
		
		final double quantizedValue = attribute.isShownAsNumber() ? 
				Profile.getValueAsReal(attribute, newValue) : Constants.UNUSED;

				
		return new ContextValueImpl(newValue, quantizedValue);
		
	}
	
	
	/**
	 * Create context value.
	 * @param type specified type.
	 * @param value specified value.
	 * @return {@link ContextValue} created.
	 */
	public static ContextValue create(Type type, Serializable value) {
		Attribute attribute = new Attribute(DataConfig.CTX_VALUE_FIELD, type);
		return create(attribute, value);
	}
	
	
	/**
	 * Create context value.
	 * @param value specified value.
	 * @return {@link ContextValue} created.
	 */
	public static ContextValue create(Serializable value) {
		if (value == null)
			return null;
		
		Type type = Attribute.fromObject(value);
		return create(type, value);
	}
	
	
}
