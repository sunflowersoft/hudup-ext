package net.hudup.core.logistic;

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
import java.util.concurrent.ArrayBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.BadLocationException;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class represents a console that attaches text area to input stream and output stream. Available at
 * <a href="https://stackoverflow.com/questions/28512381/how-can-i-turn-a-text-area-into-an-input-stream-in-java">https://stackoverflow.com/questions/28512381/how-can-i-turn-a-text-area-into-an-input-stream-in-java</a>
 * 
 * @author William Matrix Peckham
 * @version 1.0
 */
public abstract class ConsoleImpl extends JTextPane implements Console {
	
	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Internal output stream.
	 */
	protected DocOutputStream out = null;
	
	
	/**
	 * Internal print stream.
	 */
	protected PrintStream pout = null;
	
	
	/**
	 * Internal input stream.
	 */
	protected DocInputStream in = null;
	
	
	/**
	 * Internal document.
	 */
	protected StyledDocument doc = null;
	
	
	
	/**
	 * Default constructor.
	 */
	public ConsoleImpl() {
		super();
		doc = this.getStyledDocument();
		out = new DocOutputStream(doc,this);
		pout = new PrintStream(out);
		in = new DocInputStream();
		addKeyListener(in);
	}
	
	
	@Override
	public boolean stop(Object... params) throws Exception {
		return false;
	}


	/**
	 * Get input.
	 * @return internal input.
	 */
	public InputStream getIn() {
		return in;
	}
	
	
	/**
	 * Getting out put.
	 * @return print stream.
	 */
	public PrintStream getOut() {
		return pout;
	}
	
	
	/**
	 * Setting foreground color.
	 * @param fg foreground color.
	 */
	public void setFGColor(Color fg) {
		StyleConstants.setForeground(out.cur, fg);
	}
	
	
	/**
	 * Setting background color.
	 * @param bg background color.
	 */
	public void setBGColor(Color bg) {
		StyleConstants.setBackground(out.cur, bg);
	}
	
	
	/**
	 * This class represents document output stream.
	 * @author William Matrix Peckham
	 * @version 1.0
	 */
	private static class DocOutputStream extends OutputStream {
		
		/**
		 * Internal document.
		 */
		StyledDocument doc = null;
		
		/**
		 * Internal attribute set.
		 */
		MutableAttributeSet cur = null;
		
		/**
		 * Text panel.
		 */
		JTextPane pane = null;
		
		/**
		 * Constructor with document and text pane.
		 * @param doc specified document.
		 * @param pane specified text pane.
		 */
		public DocOutputStream(StyledDocument doc, JTextPane pane) {
			this.doc = doc;
			this.pane = pane;
			cur = new SimpleAttributeSet();
		}
		
		@Override
		public void write(int b) throws IOException {
			try {
				doc.insertString(doc.getLength(), (char)b + "", cur);
				pane.setCaretPosition(doc.getLength());
			}
			catch (BadLocationException e) {
				Logger.getLogger(Console.class.getName()).log(Level.SEVERE, null, e);
			}
		}
		
	}
	
	
	/**
	 * This class represents document input stream.
	 * @author William Matrix Peckham, MatrixPeckham
	 * @version 1.0
	 */
	private static class DocInputStream extends InputStream implements KeyListener {
		
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
			public boolean start(Object... params) throws Exception {
				JOptionPane.showMessageDialog(dlgConsole, "Start", "Start", JOptionPane.INFORMATION_MESSAGE);
				return true;
			}
			
		};
		dlgConsole.add(new JScrollPane(console), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		dlgConsole.add(footer, BorderLayout.SOUTH);

		JButton start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					console.start();
				} catch (Exception ex) {ex.printStackTrace();}
			}
			
		});
		footer.add(start);

		JButton close = new JButton("Close");
		close.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
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