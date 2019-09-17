/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.temp;

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
//	/**
//	 * Getting a list of instances from reflections and referred class.
//	 * @param <T> type of returned instances.
//	 * @param referredClass referred class.
//	 * @param reflections specified reflections. 
//	 * @return list of instances from specified package and referred class.
//	 */
//	static <T> List<T> getInstances(Class<T> referredClass, Reflections reflections) {
//		Set<Class<? extends T>> apClasses = reflections.getSubTypesOf(referredClass);
//		List<T> instances = Util.newList();
//		for (Class<? extends T> apClass : apClasses) {
//			if (!referredClass.isAssignableFrom(apClass))
//				continue;
//			
//			if (apClass.isInterface() || apClass.isMemberClass() || apClass.isAnonymousClass())
//				continue;
//			
//			int modifiers = apClass.getModifiers();
//			if ( (modifiers & Modifier.ABSTRACT) != 0 || (modifiers & Modifier.PUBLIC) == 0)
//				continue;
//			
//			if (apClass.getAnnotation(BaseClass.class) != null || 
//					apClass.getAnnotation(Deprecated.class) != null) {
//				continue;
//			}
//
//			try {
//				T instance = Util.newInstance(apClass);
//				instances.add(instance);
//			}
//			catch (Exception e) {
//				e.printStackTrace();
//			}
//			
//		}
//		
//		return instances;
//	}
//
//	
//	/**
//	 * Getting a list of instances from JAR list and referred class.
//	 * @param <T> type of returned instances.
//	 * @param referredClass referred class.
//	 * @param jarUriList list of JAR URI (s).
//	 * @return list of instances from specified package and referred class.
//	 */
//	@SuppressWarnings("unused")
//	@Deprecated
//	static <T> List<T> getInstances(Class<T> referredClass, xURI...jarUriList) {
//		List<URL> formalJarUrlList = Util.newList(jarUriList.length);
//		for (xURI jarUri : jarUriList) {
//			try {
//				URL formalJarUrl = new URL("jar", "", jarUri.toURL() + "!/");
//				formalJarUrlList.add(formalJarUrl);
//			}
//			catch (Throwable e) {
//				e.printStackTrace();
//			}
//		}
//		
//		URLClassLoader classLoader = new URLClassLoader(
//				formalJarUrlList.toArray(new URL[] {}));
//		try {
//			URL c = classLoader.findResource("net.hudup.em.AbstractEM");
//		}
//		catch (Exception e) {
//			e.printStackTrace();
//		}
//		ConfigurationBuilder cfgBuilder = new ConfigurationBuilder().addUrls(classLoader.getResource("/"));
//		Reflections reflections = new Reflections(cfgBuilder);
//		
//		List<T> instances = getInstances(referredClass, reflections);
//		try {
//			classLoader.close();
//		} catch (Throwable e) {e.printStackTrace();}
//		return instances;
//	}


	/**
	 * Main method.
	 * @param args argument.
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//getInstances(Alg.class, xURI.create("/E:/em-v1.jar"));
		System.out.println(System.getProperty("java.rmi.server.useCodebaseOnly"));
	}

	
}
