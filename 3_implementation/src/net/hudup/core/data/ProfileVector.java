/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

/**
 * This class models a profile as vector.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ProfileVector extends Profile {
	

	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor
	 */
	public ProfileVector() {

	}

	
	/**
	 * Constructor with specified attribute list.
	 * @param attRef specified attribute list as {@link AttributeList}.
	 */
	public ProfileVector(AttributeList attRef) {
		super(attRef);
	}

	
	@Override
	public Object clone() {
		ProfileVector profile = new ProfileVector();
		profile.attRef = this.attRef;
		
		profile.attValues.clear();
		profile.attValues.addAll(this.attValues);
		
		return profile;
	}

	
	/**
	 * Adding this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from adding this vector and specified vector.
	 */
	public ProfileVector add(ProfileVector that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		for (int i = 0; i < n; i++) {
			double value = this.getValueAsReal(i) + that.getValueAsReal(i);
			this.setValue(i, value);
		}
		
		return this;
	}


	/**
	 * Subtracting this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from subtracting this vector and specified vector.
	 */
	public ProfileVector subtract(ProfileVector that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		for (int i = 0; i < n; i++) {
			double value = this.getValueAsReal(i) - that.getValueAsReal(i);
			this.setValue(i, value);
		}
		
		return this;
	}


	/**
	 * Multiplying this vector by specified number.
	 * @param alpha specified number.
	 * @return resulted vector from multiplying this vector by specified number. 
	 */
	public ProfileVector multiply(double alpha) {
		int n = this.getAttCount();
		for (int i = 0; i < n; i++) {
			double value = alpha * this.getValueAsReal(i);
			this.setValue(i, value);
		}
		
		return this;
	}


	/**
	 * Wise-multiplying this vector and specified vector.
	 * @param that specified vector.
	 * @return resulted vector from wise-multiplying this vector and specified vector.
	 */
	public ProfileVector multiplyWise(ProfileVector that) {
		int n = Math.min(this.getAttCount(), that.getAttCount());
		for (int i = 0; i < n; i++) {
			double value = this.getValueAsReal(i) * that.getValueAsReal(i);
			this.setValue(i, value);
		}
		
		return this;
	}


	/**
	 * Creating vector from specified attribute list and object.
	 * @param attList specified attribute list
	 * @param object specified object.
	 * @return vector from specified attribute list and object.
	 */
	public static ProfileVector createVector(AttributeList attList, Object object) {
		Profile profile = Profile.createProfile(attList, object);
		if (profile == null) return null;
		
		ProfileVector vector = new ProfileVector();
		vector.attRef = profile.attRef;
		vector.attValues = profile.attValues;
		
		return vector;
	}
	
	
	/**
	 * Creating vector list with real fields.
	 * By default, all variables are real numbers.
	 * @param maxVarNumber maximum number of variables.
	 * @param initialValue initial value.
	 * @return vector with real fields.
	 */
	public static ProfileVector createVector(int maxVarNumber, double initialValue) {
		AttributeList attRef = AttributeList.defaultRealAttributeList(maxVarNumber);
		ProfileVector vector = new ProfileVector(attRef);
		
		for (int i = 0; i < attRef.size(); i++) {
			vector.setValue(i, initialValue);
		}
		
		return vector;
	}
	
	
}
