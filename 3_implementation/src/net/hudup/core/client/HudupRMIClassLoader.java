/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import net.hudup.core.logistic.LogUtil;

/**
 * This class implements socket class loader.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class HudupRMIClassLoader extends ClassLoader {

	
	/**
	 * Class processor.
	 */
	protected ClassProcessor cp = null;
	
	
	/**
	 * Constructor with parent class loader and class processor.
	 * @param cp class processor.s
	 */
	public HudupRMIClassLoader(ClassProcessor cp) {
		super();
		this.cp = cp;
	}

	
	/**
	 * Constructor with parent class loader and class processor.
	 * @param parent parent class loader.
	 * @param cp class processor.s
	 */
	public HudupRMIClassLoader(ClassLoader parent, ClassProcessor cp) {
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
		catch (ClassFormatError e) {
			LogUtil.trace(e);
			LogUtil.error("Error format of class " + name + " due to " + e.getMessage());
		}
		catch (Exception e) {}
		
		if (cls != null)
			return cls;
		else
			throw new ClassNotFoundException("Class " + name + " not found");
    }


}
