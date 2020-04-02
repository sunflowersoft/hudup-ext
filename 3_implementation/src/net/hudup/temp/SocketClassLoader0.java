package net.hudup.temp;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.PrintWriter;
import java.net.Socket;

import net.hudup.core.Constants;
import net.hudup.core.logistic.LogUtil;

/**
 * This class implements client network class loader.
 * This class is deprecated because it does not work properly. Hudup keeps it for late improvement.
 * 
 * @author Lorenzo, Donato
 * @version 1.0
 * @see https://www.drdobbs.com/jvm/a-java-2-network-class-loader/184404484
 *
 */
@Deprecated
public class SocketClassLoader0 extends ClassLoader {
	
	
	/**
	 * Server host.
	 */
    protected String host = null;
    
    
    /**
     * Server port.
     */
    protected int port = Constants.DEFAULT_NETWORK_CLASS_LOADER_PORT;
    
    
    /**
     * Socket.
     */
    protected Socket socket = null;
    
    
	/**
	 * Writer for writing data to network socket.
	 */
	protected PrintWriter out = null;
	
	
	/**
	 * Input stream for reading data from network socket.
	 */
	protected InputStream in = null;
    
    
    /**
     * Default constructor.
     */
    public SocketClassLoader0() {
        this("localhost", Constants.DEFAULT_NETWORK_CLASS_LOADER_PORT);
    }
    
    
    /**
     * Constructor with server host and server port.
     * @param host server host.
     * @param port server port.
     */
    public SocketClassLoader0(String host, int port) {
        super();
        this.host = host;
        this.port = port;
    }
    
    
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
    	if (!isConnected()) connect();
    	
    	try {
        	return loadClassFromServer(name);
    	}
    	catch (IOException e) {close(); throw new ClassNotFoundException(e.getMessage());}
    }
    
    
    /**
     * Loading class bytes from server.
     * @param className class name.
     * @return class from server.
     * @throws ClassNotFoundException if class is not found.
     */
    private Class<?> loadClassFromServer(String className)
        throws ClassNotFoundException, IOException {
    	
    	out.println(className);
    	
    	ObjectInputStream objectIn = new ObjectInputStream(in);
    	return (Class<?>)objectIn.readObject();
    }
    
    
	/**
     * Connect to server.
	 * @return whether connect successfully.
     */
    private boolean connect() {
		try {
			close();
			
			socket = new Socket(host, port);
			out = new PrintWriter(socket.getOutputStream(), true);
			in = socket.getInputStream();
			return true;
		}
		catch (Throwable e) {
			LogUtil.trace(e);
			close();
			LogUtil.error("Network class loader client connects to server fail caused by error " + e.getMessage());
		}
		
		return false;
    }
    
    
	/**
	 * Testing whether the client is connected.
	 * @return whether the client is connected.
	 */
	public boolean isConnected() {
		return socket != null && out != null && in != null;
	}

	
	/**
     * Closing client.
     */
	public void close() {
		try {
			if (in != null) {
				in.close();
				in = null;
			}
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			if (out != null) {
				out.close();
				out = null;
			}
		}
		catch (Throwable e) {LogUtil.trace(e);}

		try {
			if (socket != null) {
				socket.close();
				socket = null;
			}
		}
		catch (Throwable e) {LogUtil.trace(e);}
	}

	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		try {
			close();
		}
		catch (Exception e) {LogUtil.trace(e);}
	}


}
