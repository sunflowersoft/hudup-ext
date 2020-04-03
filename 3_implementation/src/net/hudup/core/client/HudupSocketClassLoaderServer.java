/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: http://www.locnguyen.net/st/products/hudup
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.client;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import net.hudup.core.Constants;
import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NextUpdate;

/**
 * This class implements server network class loader.
 * This class is deprecated because it does not work properly. Hudup keeps it for late improvement.
 * 
 * @author Lorenzo, Donato, Loc Nguyen
 * @version 1.0
 * @see https://www.drdobbs.com/jvm/a-java-2-network-class-loader/184404484
 *
 */
@NextUpdate
public class HudupSocketClassLoaderServer extends AbstractRunner {

	
	/**
	 * This is server socket.
	 */
	protected ServerSocket serverSocket = null;

	
	/**
	 * Server port.
	 */
	protected int serverPort = Constants.DEFAULT_NETWORK_CLASS_LOADER_PORT;
	
	
	/**
	 * Constructor with server port.
	 * 
	 */
	public HudupSocketClassLoaderServer(int serverPort) {
		this.serverPort = serverPort;
	}

	
	@Override
	protected void task() {
		if (serverSocket == null || paused)
			return;
		
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			if (paused) {
				socket.close();
				socket = null;
			}
				
		}
		catch (Throwable e) {
			socket = null;
		}
		if (socket == null) return;

		synchronized (this) {
			DataOutputStream out = null;
			BufferedReader in = null;
			try {
				socket.setSoTimeout(Constants.DEFAULT_SERVER_TIMEOUT);

				out = new DataOutputStream(socket.getOutputStream());
				in = new BufferedReader(
						new InputStreamReader(socket.getInputStream()));
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Network class loader server failed to connect to client, caused by " + e.getMessage());
			}

			while (socket != null && !socket.isClosed()) {
				String requestText = null;
				try {
					requestText = in.readLine(); //Wait here.
					if (requestText == null)
						break;
					else {
						byte[] bytes = new byte[] {};
						try {
							bytes = ClassProcessor.getByteCode0(requestText);
						}
						catch (Exception e) {LogUtil.trace(e);}
						
						out.write(bytes);
					}
					
					break; //Work-around solution.
				} 
				catch (Throwable e) {
					LogUtil.trace(e);
					LogUtil.error("Error by writing data to client.");
					try {
						socket.close();
					} 
					catch (Throwable ex) {}
					socket = null;
				}
			}
		
			try {
				if (in != null) in.close();
			} 
			catch (Throwable e) {LogUtil.trace(e);}

			try {
				if (out != null) out.close();
			} 
			catch (Throwable e) {LogUtil.trace(e);}
			
			try {
				if (socket != null && !socket.isClosed())
					socket.close();
				socket = null;
			} 
			catch (Throwable e) {LogUtil.trace(e);}
			
		} //End synchronize
	}

	
	@Override
	public synchronized boolean start() {
		// TODO Auto-generated method stub
		if (isStarted()) return false;
		
		setupServerSocket();
		
		boolean started = super.start();
		LogUtil.info("Network class loader server is serving at port " + serverPort);
		return started;
	}
	
	
	@Override
	public synchronized boolean pause() {
		// TODO Auto-generated method stub
		if (!isRunning()) return false;
		
		boolean paused = super.pause();
		LogUtil.info("Network class loader server paused");
		return paused;
	}


	@Override
	public synchronized boolean resume() {
		// TODO Auto-generated method stub
		if (!isPaused()) return false;
		
		boolean resumed = super.resume();
		LogUtil.info("Network class loader server resumed");
		return resumed;
	}


	@Override
	public synchronized boolean stop() {
		// TODO Auto-generated method stub
		if (!isStarted()) return false;
		
		destroyServerSocket();
		super.stop();
		
		LogUtil.info("Network class loader server stopped");
		
		return true;
	}

	
	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		destroyServerSocket();
	}

	
	/**
	 * Setting up (initializing) the internal socket {@link #serverSocket}.
	 */
	private void setupServerSocket() {
		if (serverSocket == null || serverSocket.isClosed()) {
			try {
				serverPort = NetUtil.getPort(serverPort, Constants.TRY_RANDOM_PORT);
				serverSocket = new ServerSocket(serverPort);
				serverSocket.setSoTimeout(Constants.DEFAULT_SERVER_TIMEOUT);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Network class loader server failed to create server socket, caused by " + e.getMessage());
				destroyServerSocket();
			}
		}
		
	}

	
	/**
	 * Getting server port.
	 * @return server port.
	 */
	public int getServerPort() {
		return serverPort;
	}
	
	
	/**
	 * Destroying the internal socket {@link #serverSocket}.
	 */
	private void destroyServerSocket() {
		if (serverSocket != null && !serverSocket.isClosed()) {
			try {
				serverSocket.close();
			} 
			catch (Throwable e) {
				LogUtil.trace(e);
				LogUtil.error("Socket server failed to close server socket, caused by " + e.getMessage());
			}
		}
		serverSocket = null;
	}


	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			stop();
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}


}


