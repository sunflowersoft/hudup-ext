/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.temp;

import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Set;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Predicate;

import net.hudup.core.Util;
import net.hudup.core.alg.cf.NeighborCF;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.xURI;

/**
 * This is temporal testing class.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class Test {

	
//	/**
//	 * Default constructor.
//	 */
//	public Test() {
//		// TODO Auto-generated constructor stub
//	}
//
//	
	/**
	 * Getting a list of instances from reflections and referred class.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class.
	 * @param reflections specified reflections. 
	 * @return list of instances from specified package and referred class.
	 */
	static <T> List<T> getInstances(Class<T> referredClass, Reflections reflections) {
		Set<Class<? extends T>> apClasses = reflections.getSubTypesOf(referredClass);
		List<T> instances = Util.newList();
		for (Class<? extends T> apClass : apClasses) {
			if (apClass == null) continue;

			if (!referredClass.isAssignableFrom(apClass))
				continue;
			
			if (apClass.isInterface() || apClass.isMemberClass() || apClass.isAnonymousClass())
				continue;
			
			int modifiers = apClass.getModifiers();
			if ( (modifiers & Modifier.ABSTRACT) != 0 || (modifiers & Modifier.PUBLIC) == 0)
				continue;
			
			if (apClass.getAnnotation(BaseClass.class) != null || 
					apClass.getAnnotation(Deprecated.class) != null) {
				continue;
			}

			try {
				T instance = Util.newInstance(apClass);
				instances.add(instance);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		
		return instances;
	}

	
	/**
	 * Getting a list of instances from JAR list and referred class.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class.
	 * @param jarUriList list of JAR URI (s).
	 * @return list of instances from specified package and referred class.
	 */
	static <T> List<T> getInstances(Class<T> referredClass, xURI...jarUriList) {
		List<URL> formalJarUrlList = Util.newList(jarUriList.length);
		for (xURI jarUri : jarUriList) {
			try {
				URL formalJarUrl = jarUri.toURL(); //new URL("jar", "", jarUri.toURL() + "!/");
				formalJarUrlList.add(formalJarUrl);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
		
		URLClassLoader classLoader = new URLClassLoader(
				formalJarUrlList.toArray(new URL[] {})/*, Test.class.getClassLoader()*/);
		try {
			//URL c = classLoader.findResource("net/hudup/em/AbstractEM.class");
			//Class<?> c = Class.forName("org.apache.commons.math3.Field", true, classLoader);
			//System.out.println(c.toString());
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		Reflections2 reflections = new Reflections2("net", classLoader);
		
		List<T> instances = getInstances(referredClass, reflections);
		try {
			classLoader.close();
		} catch (Throwable e) {e.printStackTrace();}
		return instances;
	}


	/**
	 * Main method.
	 * @param args argument.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		getInstances(NeighborCF.class, xURI.create("/E:/sim.jar"));
//		System.out.println(System.getProperty("java.rmi.server.useCodebaseOnly"));
	}

	
}


class Reflections2 extends Reflections {

	public Reflections2(String prefix, ClassLoader...classLoaders) {
		super(new ConfigurationBuilder() {
			{
				Predicate<String> filter = new FilterBuilder.Include(FilterBuilder.prefix(prefix));
				setUrls(ClasspathHelper.forPackage(prefix, classLoaders));
				filterInputsBy(filter);
				
				setScanners(
					new TypeAnnotationsScanner().filterResultsBy(filter),
					new SubTypesScanner().filterResultsBy(filter));
            }
		});
	}
	
}
