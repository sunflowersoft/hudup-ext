package net.hudup.core.logistic.console;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.JTextComponent;

import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.console.ConsoleEvent.Type;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class represents a console that attaches text area to input stream and output stream. Available at
 * <a href="https://stackoverflow.com/questions/28512381/how-can-i-turn-a-text-area-into-an-input-stream-in-java">https://stackoverflow.com/questions/28512381/how-can-i-turn-a-text-area-into-an-input-stream-in-java</a>
 * 
 * @author William Matrix Peckham
 * @version 1.0
 */
public abstract class ConsoleImpl implements Console {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Text area.
	 */
	protected JTextComponent txtArea = null;
	
	
	/**
	 * Internal input stream.
	 */
	protected DocInputStream in = null;
	
	
	/**
	 * Internal output stream.
	 */
	protected DocOutputStream out = null;
	
	
	/**
	 * Holding a list of event listeners.
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
     * Internal runner.
     */
	protected AbstractRunner runner = null;

	
	/**
     * Exporting flag.
     */
	protected boolean exported = false;

	
	/**
	 * Default constructor.
	 */
	public ConsoleImpl() {
		super();
		txtArea = new JTextPane();
		txtArea.setEditable(Console.DEFAULT_TEXT_EDITABLE);
		txtArea.setBackground(new Color(0, 0, 0));
		txtArea.setForeground(new Color(255, 255, 255));
		txtArea.setCaretColor(new Color(255, 255, 255));
		
		out = new DocOutputStream(txtArea);
		in = new DocInputStream();
		txtArea.addKeyListener(in);
		
		runner = new AbstractRunner() {
			
			@Override
			protected void task() {
				getThisConsole().send("Started");
				getThisConsole().consoleTask();
				
				thread = null;
				
				if (paused) {
					paused = false;
					notifyAll();
				}
				
				getThisConsole().send("Stopped");
			}
			
			@Override
			protected void clear() {

			}

		};
	}
	
	
	/**
	 * The task is executed one time.
	 */
	protected abstract void consoleTask();
	

	@Override
	public boolean startConsole(Object... params) throws RemoteException {
		txtArea.setEditable(Console.DEFAULT_TEXT_EDITABLE);
		boolean started = runner.start();
		return started;
	}


	@Override
	public boolean stopConsole(Object... params) throws RemoteException {
		boolean stopped = runner.forceStop();
		if (stopped) send("Forcedly stopped");
		return stopped;
	}


	@Override
	public boolean isConsoleStarted() throws RemoteException {
		return runner.isStarted();
	}


	@Override
	public String getContent() throws RemoteException {
		return txtArea.getText();
	}


	@Override
	public void addListener(ConsoleListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.add(ConsoleListener.class, listener);
		}
	}


	@Override
	public void removeListener(ConsoleListener listener) throws RemoteException {
		synchronized (listenerList) {
			listenerList.remove(ConsoleListener.class, listener);
		}
	}


    /**
     * Return array of listeners for this console.
     * @return array of listeners for this console.
     */
    protected ConsoleListener[] getListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(ConsoleListener.class);
		}
    }

    
    /**
     * Firing (issuing) an event from this console to all listeners. 
     * @param evt event from this console.
     */
    protected void fireEvent(ConsoleEvent evt) {
		ConsoleListener[] listeners = getListeners();
		for (ConsoleListener listener : listeners) {
	    	try {
				listener.receiveMessage(evt);
			}
			catch (Exception e) {LogUtil.trace(e);}
		}
    }

    
    /**
	 * Entering information.
	 * @param prompt prompt message.
	 * @return entered information.
	 */
	protected String enter(String prompt) {
		if (txtArea == null) return null;
		boolean editable = txtArea.isEditable();
		txtArea.setEditable(true);
		
		if(prompt != null) new PrintStream(out).print(prompt);
		String info = null;
		try {
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(in);
			info = scanner.nextLine().trim();
		} catch (Throwable e) {}
		
		txtArea.setEditable(editable);
		return info;
	}
	
	
	/**
	 * Sending message.
	 * @param message specified message.
	 */
	protected void send(String message) {
		new PrintStream(out).println(message);
	}

	
	/**
	 * Getting component.
	 * @return component.
	 */
	public JTextComponent getComponent() {
		return txtArea;
	}
	
	
	/**
	 * Getting output stream.
	 * @return output stream.
	 */
	public DocOutputStream getOut() {
		return out;
	}
	
	
	/**
	 * Getting input stream.
	 * @return input stream.
	 */
	public DocInputStream getIn() {
		return in;
	}
	
	
	/**
	 * Getting this console.
	 * @return this console.
	 */
	private ConsoleImpl getThisConsole() {
		return this;
	}
	
	
	/**
	 * This class represents document output stream.
	 * @author William Matrix Peckham
	 * @version 1.0
	 */
	protected class DocOutputStream extends OutputStream {
		
		/**
		 * Text area.
		 */
		JTextComponent txtArea = null;
		
		/**
		 * Constructor with document and text pane.
		 * @param doc specified document.
		 * @param pane specified text pane.
		 */
		public DocOutputStream(JTextComponent txtArea) {
			this.txtArea = txtArea;
		}
		
		@Override
		public void write(int b) throws IOException {
			try {
				Document doc = txtArea.getDocument();
				doc.insertString(doc.getLength(), (char)b + "", null);
				txtArea.setCaretPosition(doc.getLength());
			}
			catch (BadLocationException e) {
				Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, e);
			}
		}

		@Override
		public void write(byte[] b, int off, int len) throws IOException {
			super.write(b, off, len);
			fireEvent(new ConsoleEvent(getThisConsole(), Type.inform, new String(b, off, len)));
		}
		
	}
	
	
	/**
	 * This class represents document input stream.
	 * @author William Matrix Peckham, MatrixPeckham
	 * @version 1.0
	 */
	protected class DocInputStream extends InputStream implements KeyListener {
		
		/**
		 * Internal blocking queue.
		 */
		ArrayBlockingQueue<Integer> queue = null;
		
		/**
		 * Default constructor.
		 */
		public DocInputStream() {
			queue = new ArrayBlockingQueue<Integer>(1024);
		}

		@Override
		public int read() throws IOException {
			Integer i = null;
			try {
				i = queue.take();
			}
			catch (InterruptedException e) {
				Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, e);
			}
			if(i != null) return i;
			return -1;
		}
		
		@Override
		public int read(byte[] b, int off, int len) throws IOException {
			if (b == null) {
				throw new NullPointerException();
			}
			else if (off < 0 || len < 0 || len > b.length - off) {
				throw new IndexOutOfBoundsException();
			}
			else if (len == 0) {
				return 0;
			}
			
			int c = read();
			if (c == -1) {
				return -1;
			}
			b[off] = (byte)c;

			int i = 1;
			try {
				for (; i < len && available() > 0 ; i++) {
					c = read();
					if (c == -1) {
						break;
					}
					b[off + i] = (byte)c;
				}
			} catch (IOException e) {}   
			return i;
		}
		
		@Override
		public int available() {
			return queue.size();
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
			int c = e.getKeyChar();
			try {
				queue.put(c);
			} catch (InterruptedException ex) {
				Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		@Override
		public void keyPressed(KeyEvent e) {
			
		}
		
		@Override
		public void keyReleased(KeyEvent e) {
			
		}
	
	}
	
	
	@Override
	public synchronized boolean export(int serverPort) throws RemoteException {
		if (exported) return false;
		try {
			return (exported = (UnicastRemoteObject.exportObject(this, serverPort) != null));
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		return false;
	}


	@Override
	public synchronized boolean unexport() throws RemoteException {
		if (exported) {
			try {
	        	return !(exported = !UnicastRemoteObject.unexportObject(this, true));
			}
			catch (Throwable e) {LogUtil.trace(e);}
			return false;
		}
		else
			return false;
	}

	
	@Override
	public void close() throws Exception {
		try {
			stopConsole();
		} catch (Throwable e) {LogUtil.trace(e);}
		
		try {
			unexport();
		} catch (Throwable e) {LogUtil.trace(e);}
	}


	/**
	 * Showing console dialog.
	 * @param comp parent component.
	 */
	public static void showDlg(Component comp) {
		JDialog dlgConsole = new JDialog(UIUtil.getDialogForComponent(comp), true);
		dlgConsole.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlgConsole.setSize(400, 400);
		dlgConsole.setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		dlgConsole.setLayout(new BorderLayout());
		
		ConsoleImpl console = new ConsoleImpl() {
			
			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void consoleTask() {
				boolean bool = false;
				try {
					bool = Boolean.parseBoolean(enter("Enter boolean value true|false (false is default):").trim());
				} catch (Throwable e) {}
				send(bool ? "True\n" : "False\n");
			}

			@Override
			public String getName() throws RemoteException {
				return "sample";
			}
			
		};
		dlgConsole.add(new JScrollPane(console.getComponent()), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		dlgConsole.add(footer, BorderLayout.SOUTH);

		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					console.startConsole();
				} catch (RemoteException ex) {LogUtil.trace(ex);}
			}
			
		});
		footer.add(start);

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					console.close();
				} catch (Exception ex) {LogUtil.trace(ex);}
				dlgConsole.dispose();
			}
		});
		footer.add(close);

		dlgConsole.setVisible(true);
	}


	/**
	 * Main method.
	 * @param args arguments.
	 */
	public static void main(String[] args) {
		showDlg(null);
	}
	
	
}