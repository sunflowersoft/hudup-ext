/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

/**
 * This class implements socket class loader.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class RMIClassLoader extends ClassLoader {

	
	/**
	 * Class processor.
	 */
	protected ClassProcessor cp = null;
	
	
	/**
	 * Constructor with parent class loader and class processor.
	 * @param cp class processor.s
	 */
	public RMIClassLoader(ClassProcessor cp) {
		super();
		this.cp = cp;
	}

	
	/**
	 * Constructor with parent class loader and class processor.
	 * @param parent parent class loader.
	 * @param cp class processor.s
	 */
	public RMIClassLoader(ClassLoader parent, ClassProcessor cp) {
		super(parent);
		this.cp = cp;
	}

	
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Class<?> cls = null;
		try {
	    	byte[] codeBytes = cp.getByteCode(name);
			cls = defineClass(name, codeBytes, 0, codeBytes.length);
		}
		catch (Exception e) {LogUtil.trace(e);}
		
		if (cls != null)
			return cls;
		else
			throw new ClassNotFoundException("Class " + name + " not found");
    }


}
