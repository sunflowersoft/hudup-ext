/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.rmi.Naming;
import java.rmi.NoSuchObjectException;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.Enumeration;
import java.util.Random;

/**
 * This final class provides utility static methods for network programming. Some methods in this class are available on internet.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class NetUtil {
	
	
	/**
	 * Testing whether specified port is valid.
	 * @param port specified port.
	 * @return whether specified port is valid.
	 */
	public static boolean testPort(int port) {
		ServerSocket ss = null; 
		DatagramSocket ds = null;
	    
		try { 
			ss = new ServerSocket(port); 
			ss.setReuseAddress(true); 
			ds = new DatagramSocket(port); 
			ds.setReuseAddress(true);
			
			return true; 
	    }
		catch (Throwable e) {
			
	    } 
		finally { 
			if (ds != null)
	            ds.close(); 
	 
			if (ss != null) { 
				try { 
					ss.close(); 
				} 
				catch (Throwable e) { 
	            } 
	        } 
	    } 
	 
		return false;
	}
	
	
	/**
	 * Testing whether specified port is valid. If such port is valid, the method returns it.
	 * If the port is not valid but the input parameter {@code tryRandom} is {@code true}, the method tries to test random port so as to get a valid port.
	 * @param port specified port
	 * @param tryRandom if {@code true}, the method will try to test random port so as to get a valid port after determining that the specified port is invalid.
	 * @return valid port which can be used.
	 */
	public static int getPort(int port, boolean tryRandom) {
		if (testPort(port))
			return port;
		
		if (tryRandom) {
			Random rnd = new Random();
			int i = 0;
			while (i < 48128) {
				port = rnd.nextInt(48128) + 1024;
				if (testPort(port))
					return port;
				i++;
			}
		}
		return -1;
	}
	
	
	/**
	 * Getting the internet address of current terminator.
	 * @return internet address as {@link InetAddress} of current terminator.
	 */
	public static InetAddress getLocalInetAddress() {
		Enumeration<NetworkInterface> nis = null;
		
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } 
        catch (Exception e) {
            return null;
        }

        while (nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                        && addr.getHostAddress().indexOf(":") == -1 ) {
                    return addr;
                }
            }
        }
        
        try {
            return InetAddress.getLocalHost();
        }
        catch (Exception e) {
            return null;
        }
	}
	
	
	/**
	 * This class represents internet address and hardware address.
	 * @author Loc Nguyen
	 * @version 13
	 */
	public static class InetHardware {
		
		/**
		 * Network interface.
		 */
		public NetworkInterface ni = null;
		
		/**
		 * Internet address.
		 */
		public InetAddress inetAddr = null;
		
		/**
		 * Getting host address.
		 * @return host address.
		 */
		public String getHostAddress() {
			if (inetAddr == null)
				return null;
			else
				return inetAddr.getHostAddress();
		}
		
		/**
		 * Getting MAC address. Refer to <a href="https://stackoverflow.com/questions/6164167/get-mac-address-on-local-machine-with-java">https://stackoverflow.com/questions/6164167/get-mac-address-on-local-machine-with-java</a>
		 * @return MAC address.
		 */
		public String getMACAddress() {
			if (ni == null) return null;
			
			try {
				byte[] mac = ni.getHardwareAddress();
				if (mac == null) return null;
				
				StringBuilder txtMac = new StringBuilder();
	            for (int i = 0; i < mac.length; i++) {
	            	txtMac.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));;
	            }
	            
	            return txtMac.toString();
			}
			catch (Exception e) {
				LogUtil.trace(e);
				return null;
			}
		}
	}
	
	
	/**
	 * Getting internet and hardware addresses.
	 * @return internet and hardware addresses.
	 */
	public static InetHardware getInetHardware() {
		Enumeration<NetworkInterface> nis = null;
		
        try {
            nis = NetworkInterface.getNetworkInterfaces();
        } 
        catch (Exception e) {
            return null;
        }

        while (nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            Enumeration<InetAddress> addrs = ni.getInetAddresses();
            while (addrs.hasMoreElements()) {
                InetAddress addr = addrs.nextElement();
                if (!addr.isLoopbackAddress() && addr.isSiteLocalAddress()
                        && addr.getHostAddress().indexOf(":") == -1 ) {
                	
                	InetHardware ih = new InetHardware();
                	ih.ni = ni;
                	ih.inetAddr = addr;
                    return ih;
                }
            }
        }
        
        return null;
	}
	
	
	/**
	 * Class contains registry and remote object.
	 * @author Loc Nguyen
	 * @version 1.0
	 *
	 */
	public static class RegistryRemote {
		
		/**
		 * Registry. It should be serialized.
		 */
		protected Registry registry = null;
		
		/**
		 * Stub object. It must be serialized.
		 */
		protected Remote stub = null;
		
		/**
		 * Creation flag to indicate whether the registry {@link #registry} is created or retrieve (get).
		 */
		protected boolean registryCreated = true;
		
		/**
		 * Constructor with specified registry and remote object.
		 * @param registry specified registry.
		 * @param stub specified remote object.
		 */
		private RegistryRemote(Registry registry, Remote stub) {
			this.registry = registry;
			this.stub = stub;
		}
		
		/**
		 * Getting registry.
		 * @return registry.
		 */
		public Registry getRegistry() {
			return registry;
		}
		
		/**
		 * Getting stub.
		 * @return stub.
		 */
		public Remote getStub() {
			return stub;
		}
		
		/**
		 * Checking whether the registry {@link #registry} is created or retrieve (get).
		 * @return the registry {@link #registry} is created or retrieve (get).
		 */
		public boolean isRegistryCreated() {
			return registryCreated;
		}
		
		/**
		 * Export remote object.
		 * @param remote remote object.
		 * @param port registered port.
		 * @return successfully stub object. Return null if failed.
		 */
		public static Remote export(Remote remote, int port) {
			if (remote == null) return null;
			
			Remote stub = null;
			try {
				stub = UnicastRemoteObject.exportObject(remote, port);
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtil.trace(e);
				try {
					if (stub != null)
						UnicastRemoteObject.unexportObject(remote, true);
				}
				catch (Exception e2) {
					e2.printStackTrace();
				}
				
				stub = null;
			}
			
			return stub;
		}
		
		
		/**
		 * Export remote object.
		 * @param remote remote object.
		 * @param bindUri bind URI.
		 * @return successfully stub object. Return null if failed.
		 */
		public static Remote export(Remote remote, xURI bindUri) {
			if (bindUri == null)
				return null;
			else
				return export(remote, bindUri.getPort());
		}
		
		/**
		 * Unexport remote object.
		 * @param remote remote object.
		 * @return true if unexporting is successfully.
		 */
		public static boolean unexport(Remote remote) {
			if (remote == null) return false;
			
			try {
	        	UnicastRemoteObject.unexportObject(remote, true);
	        	return true;
			}
			catch (NoSuchObjectException e) {
				//LogUtil.trace(e);
				return true;
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
			
			return false;
		}
		
		
		/**
		 * Register and export remote object. This method is deprecated because
		 * method LocateRegistry.getRegistry(port) always returns non-null reference of a registry even though such registry is not created yet.
		 * @param remote remote object.
		 * @param port registered port.
		 * @return registry and stub.
		 */
		@Deprecated
		@SuppressWarnings("unused")
		private static RegistryRemote registerExport0(Remote remote, int port) {
			if (remote == null) return null;

			Registry registry = null;
			Remote stub = null;
			boolean registryCreated = true;
			try {
				registry = LocateRegistry.getRegistry(port);
				if (registry != null)
					registryCreated = false;
				else {
					registry = LocateRegistry.createRegistry(port);
					registryCreated = true;
				}
				if (registry == null) return null;
				
				stub = UnicastRemoteObject.exportObject(remote, port);
				if (stub == null) {
					if (registryCreated)
						UnicastRemoteObject.unexportObject(registry, true);
					return null;
				}
				else {
					RegistryRemote registryRemote = new RegistryRemote(registry, stub);
					registryRemote.registryCreated = registryCreated;
					return registryRemote;
				}
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				
				try {
		        	if (stub != null)
		        		UnicastRemoteObject.unexportObject(remote, true);
				}
				catch (Throwable e1) {
					e1.printStackTrace();
				}
				
				try {
		        	if (registry != null)
		        		UnicastRemoteObject.unexportObject(registry, true);
				}
				catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
			
			return null;
		}

		/**
		 * Register and export remote object.
		 * @param remote remote object.
		 * @param port registered port.
		 * @return registry and stub.
		 */
		public static RegistryRemote registerExport(Remote remote, int port) {
			if (remote == null) return null;

			Registry registry = null;
			Remote stub = null;
			boolean registryCreated = true;
			try {
				registry = LocateRegistry.createRegistry(port);
				if (registry == null) return null;
				
				stub = UnicastRemoteObject.exportObject(remote, port);
				if (stub == null) {
					if (registryCreated)
						UnicastRemoteObject.unexportObject(registry, true);
					return null;
				}
				else {
					RegistryRemote registryRemote = new RegistryRemote(registry, stub);
					registryRemote.registryCreated = registryCreated;
					return registryRemote;
				}
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				
				try {
		        	if (stub != null)
		        		UnicastRemoteObject.unexportObject(remote, true);
				}
				catch (Throwable e1) {
					e1.printStackTrace();
				}
				
				try {
		        	if (registry != null)
		        		UnicastRemoteObject.unexportObject(registry, true);
				}
				catch (Throwable e1) {
					e1.printStackTrace();
				}
			}
			
			return null;
		}
		
		/**
		 * Register and export remote object.
		 * @param remote remote object.
		 * @param bindUri bind URI.
		 * @return registry and stub.
		 */
		public static RegistryRemote registerExport(Remote remote, xURI bindUri) {
			if (bindUri == null)
				return null;
			else
				return registerExport(remote, bindUri.getPort());
		}
		
		/**
		 * Unregister registry and unexport remote object. 
		 * @param registry registry.
		 * @param remote remote object.
		 * @return true if successfully unregistering and unexporting.
		 */
		public static boolean unregisterUnexport(Registry registry, Remote remote) {
			boolean result = true;
			try {
	        	if (remote != null)
	        		result = result && UnicastRemoteObject.unexportObject(remote, true);
			}
			catch (NoSuchObjectException e) {
				LogUtil.trace(e);
				result = result && true;
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				result = result && false;
			}
			
			try {
	        	if (registry != null)
	        		result = result && UnicastRemoteObject.unexportObject(registry, true);
			}
			catch (NoSuchObjectException e) {
				LogUtil.trace(e);
				result = result && true;
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				result = result && false;
			}
			
			return result;
		}
		
		/**
		 * Register, export, and naming remote object.
		 * @param remote remote object.
		 * @param port registered port.
		 * @param bindUriText bind URI text.
		 * @return registry and stub.
		 */
		public static RegistryRemote registerExportNaming(Remote remote, int port, String bindUriText) {
			if (bindUriText == null) return null;
			
			RegistryRemote result = registerExport(remote, port);
			if (result == null) return null;
			
			try {
				Naming.rebind(bindUriText, remote);
				return result;
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				LogUtil.trace(e);
			}
			
			return null;
		}

		/**
		 * Register, export, and naming remote object.
		 * @param remote remote object.
		 * @param bindUri bind URI.
		 * @return registry and stub.
		 */
		public static RegistryRemote registerExportNaming(Remote remote, xURI bindUri) {
			if (bindUri == null)
				return null;
			else
				return registerExportNaming(remote, bindUri.getPort(), bindUri.toString());
		}
		
		/**
		 * Unregister registry, unexport and unnaming remote object. 
		 * @param registry registry.
		 * @param remote remote object.
		 * @param bindUriText bind URI text.
		 * @return true if successfully unregistering and unexporting.
		 */
		public static boolean unregisterUnexportUnnaming(Registry registry, Remote remote, String bindUriText) {
			boolean result = true;
			
			if (bindUriText != null) {
				try {
					Naming.unbind(bindUriText);
					result = result && true;
				}
				catch (NotBoundException e) {
					LogUtil.trace(e);
					result = result && true;
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					LogUtil.trace(e);
					result = result && false;
				}
			}
			
			return result && unregisterUnexport(registry, remote);
		}
		
		/**
		 * Unregister registry, unexport and unnaming remote object. 
		 * @param registry registry.
		 * @param remote remote object.
		 * @param bindUri bind URI.
		 * @return true if successfully unregistering and unexporting.
		 */
		public static boolean unregisterUnexportUnnaming(Registry registry, Remote remote, xURI bindUri) {
			boolean result = true;
			
			if (bindUri != null) {
				try {
					Naming.unbind(bindUri.toString());
					result = result && true;
				}
				catch (NotBoundException e) {
					LogUtil.trace(e);
					result = result && true;
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					LogUtil.trace(e);
					result = result && false;
				}
			}
			
			return result && unregisterUnexport(registry, remote);
		}
		
		/**
		 * Getting registry at specified port.
		 * @param port specified port.
		 * @param createIfNotExist when this flag is true, registry will be created if it does not exist.
		 * @return registry at specified port.
		 */
		public static Registry getRegistry(int port, boolean createIfNotExist) {
			Registry registry = null;
			try {
				registry = LocateRegistry.getRegistry(port);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				registry = null;
			}
			
			if (registry != null)
				return registry;
			else if (createIfNotExist) {
				try {
					registry = LocateRegistry.createRegistry(port);
				}
				catch (Throwable e) {
					LogUtil.trace(e);
					registry = null;
				}
				return registry;
			}
			else
				return null;
		}
		
	}
	
	
	/**
	 * Main method.
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		System.out.println(getInetHardware().getMACAddress());
	}
	
	
}
