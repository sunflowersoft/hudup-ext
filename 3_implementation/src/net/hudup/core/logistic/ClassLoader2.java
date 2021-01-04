/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * This is an extension of URL class loader with support of finding classes.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ClassLoader2 extends URLClassLoader {

	
	/**
	 * Internal URL class loader.
	 */
	protected URLClassLoader cl = null;
	
	
	/**
	 * Constructor with other URL class loader.
	 * @param cl other URL class loader.
	 */
	public ClassLoader2(URLClassLoader cl) {
		super(new URL[0]);
		this.cl = cl;
	}


	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		if (cl == null) return null;
		
	    Method method = null;
		try {
		    method = URLClassLoader.class.getDeclaredMethod("findClass", String.class);
		    method.setAccessible(true);
		}
		catch (Exception e) {
			method = null;
		}
		if (method == null) return null;
		
		try {
		    return (Class<?>)method.invoke(cl, name);
		}
		catch (Exception e) {}
		
		return null;
	}

	
	/**
	 * Loading class by name.
	 * @param cl class loader.
	 * @param name specified.
	 * @return class given name.
	 */
	public static Class<?> findClass(ClassLoader cl, String name) {
		if (cl == null || name == null) return null;
		
		if (cl instanceof URLClassLoader) {
			ClassLoader2 cl2 = new ClassLoader2((URLClassLoader)cl);
			Class<?> cls = null;
			try {
				cls = cl2.findClass(name);
			}
			catch (Exception e) {
			}
			finally {
				try {
					cl2.close();
				}
				catch (Exception e) {}
			}
			if (cls != null) return cls;
		}
		
		try {
			Class<?> cls = cl.loadClass(name);
			if (cls != null) return cls;
		} catch (Exception e) {}
		
		try {
			Class<?> cls = Class.forName(name, true, cl);
			if (cls != null) return cls;
		} catch (Exception e) {}
		
		try {
			Class<?> cls = Class.forName(name);
			if (cls != null) return cls;
		} catch (Exception e) {}

		return null;
	}

	
}
