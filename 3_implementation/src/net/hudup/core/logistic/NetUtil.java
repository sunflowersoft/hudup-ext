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
 * This final class provides utility static methods for network programming.
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
	 * Class contains registry and remote object.
	 * @author Loc Nguyen
	 * @version 1.0
	 *
	 */
	public static class RegistryRemote {
		
		/**
		 * Registry.
		 */
		protected Registry registry = null;
		
		/**
		 * Stub object.
		 */
		protected Remote stub = null;
		
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
				e.printStackTrace();
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
				e.printStackTrace();
				return true;
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
			
			return false;
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
			try {
//				try {
//					registry = LocateRegistry.getRegistry(port);
//				}
//				catch (Throwable e) {
//					registry = null;
//				}
				//Fixing bug: always to create new registry. Date: 2019.08.06 by Loc Nguyen.
				if (registry == null)
					registry = LocateRegistry.createRegistry(port);
				
				stub = UnicastRemoteObject.exportObject(remote, port);
				
				if (registry == null || stub == null)
					return null;
				else
					return new RegistryRemote(registry, stub);
			}
			catch (Throwable e) {
				e.printStackTrace();
				
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
				e.printStackTrace();
				result = result && true;
			}
			catch (Throwable e) {
				e.printStackTrace();
				result = result && false;
			}
			
			try {
	        	if (registry != null)
	        		result = result && UnicastRemoteObject.unexportObject(registry, true);
			}
			catch (NoSuchObjectException e) {
				e.printStackTrace();
				result = result && true;
			}
			catch (Throwable e) {
				e.printStackTrace();
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
				e.printStackTrace();
			}
			
			return null;
		}

		/**
		 * Register, export, and naming remote object.
		 * @param remote remote object.
		 * @param port registered port.
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
					e.printStackTrace();
					result = result && true;
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
					e.printStackTrace();
					result = result && true;
				}
				catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					result = result && false;
				}
			}
			
			return result && unregisterUnexport(registry, remote);
		}
		
	}
	
	

	
}
