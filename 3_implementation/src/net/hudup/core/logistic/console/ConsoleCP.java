/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.console;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import net.hudup.core.Util;
import net.hudup.core.app.App;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.Connector;
import net.hudup.core.client.PowerServer;
import net.hudup.core.client.Server;
import net.hudup.core.logistic.Counter;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.ui.StartDlg;
import net.hudup.core.logistic.ui.TextArea;
import net.hudup.core.logistic.ui.TooltipTextArea;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class provides a remote control panel for console.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public class ConsoleCP extends JDialog implements ConsoleListener {


	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal console.
	 */
	protected Console console = null;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = null;

	
	/**
	 * Text area.
	 */
	protected JTextComponent txtArea = null;
	
	
	/**
	 * Starting button.
	 */
	protected JButton btnStart = null;
	
	
	/**
	 * Stopping button.
	 */
	protected JButton btnStop = null;
	
	
	/**
	 * Clearing text button.
	 */
	protected JButton btnClearText = null;

	
	/**
	 * Refreshing button.
	 */
	protected JButton btnRefresh = null;

	
	/**
	 * Closing button.
	 */
	protected JButton btnClose = null;

	
	/**
	 * Internal time counter.
	 */
	protected Timer timer = null;

	
	/**
	 * Constructor with console, connection information, and parent component.
	 * @param console specified console.
	 * @param connectInfo connection information.
	 * @param comp parent component.
	 */
	public ConsoleCP(Console console, ConnectInfo connectInfo, Component comp) {
		super(UIUtil.getDialogForComponent(comp), "Console control panel", true);
		this.connectInfo = (connectInfo != null ? connectInfo : new ConnectInfo());
		this.console = console;
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getDialogForComponent(comp));
		setLayout(new BorderLayout());
		
		initUI();
		
		bindServer();
		
		updateControls();

		ConnectInfo thisConnectInfo = this.connectInfo;
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowOpened(WindowEvent e) {
				super.windowOpened(e);
				
				if (timer != null || thisConnectInfo.bindUri == null || !thisConnectInfo.pullMode)
					return;
				
				timer = new Timer();
				long milisec = thisConnectInfo.accessPeriod < Counter.PERIOD*1000 ? Counter.PERIOD*1000 : thisConnectInfo.accessPeriod;
				timer.schedule(
					new TimerTask() {
					
						@Override
						public void run() {
							updateControls();
						}
					}, 
					milisec, 
					milisec);
			}
			
		});
	}

	
	/**
	 * Constructor with console and connection information.
	 * @param console specified console.
	 * @param comp parent component.
	 */
	public ConsoleCP(Console console, Component comp) {
		this(console, null, comp);
	}
	
	
	/**
	 * Constructor with console.
	 * @param console specified console.
	 */
	public ConsoleCP(Console console) {
		this(console, null, null);
	}

	
	/**
	 * Initialize user interface.
	 */
	protected void initUI() {
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		if ((connectInfo.bindUri == null) && (console instanceof ConsoleImpl))
			txtArea = ((ConsoleImpl)console).getComponent();
		else {
			txtArea = new JTextPane();
			txtArea.setEditable(Console.DEFAULT_TEXT_EDITABLE);
		}
		body.add(new JScrollPane(txtArea), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);
		
		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					console.startConsole();
				} catch (Throwable ex) {LogUtil.trace(ex);}
			}
			
		});
		footer.add(btnStart);
		
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					console.stopConsole();
				} catch (Throwable ex) {LogUtil.trace(ex);}
			}
			
		});
		footer.add(btnStop);

		btnClearText = new JButton("Clear text");
		btnClearText.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				txtArea.setText("");
			}
			
		});
		footer.add(btnClearText);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				updateControls();
			}
			
		});
		footer.add(btnRefresh);

		btnClose = new JButton("Close");
		btnClose.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
			
		});
		footer.add(btnClose);
	}

	
	/**
	 * Binding remote server.
	 */
	protected void bindServer() {
		if (connectInfo.bindUri == null) {
			try {
				console.addListener(this);
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		else if (!connectInfo.pullMode) {
			try {
				UnicastRemoteObject.exportObject(this, connectInfo.bindUri.getPort());
				console.addListener(this);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
				try {
		        	UnicastRemoteObject.unexportObject(this, true);
				}
				catch (Throwable e1) {LogUtil.trace(e1);}
			}
		}
		
		btnStart.setVisible((connectInfo.bindUri == null) && (console instanceof ConsoleImpl));
		btnRefresh.setVisible(connectInfo.bindUri != null);
	}


	/**
	 * Update controls.
	 */
	protected void updateControls() {
		if (console == null) return;
		
		if ((connectInfo.bindUri != null) || !(console instanceof ConsoleImpl)) {
			try {
				txtArea.setText(console.getContent());
				txtArea.setCaretPosition(txtArea.getDocument().getLength());
			} catch (Throwable e) {LogUtil.trace(e);}
		}

		boolean started = false;
		try {
			started = console.isConsoleStarted();
		} catch (Throwable e) {LogUtil.trace(e);}
		btnStart.setEnabled(!started);
		btnStop.setEnabled(started);
	}


	@Override
	public void receiveMessage(ConsoleEvent evt) throws RemoteException {
		updateControls();
	}


	@Override
	public void dispose() {
		try {
			if (console != null && (connectInfo.bindUri == null || !connectInfo.pullMode))
				console.removeListener(this);
		}
		catch (Throwable e) {LogUtil.trace(e);}
		console = null;
		
		try {
			if (connectInfo.bindUri != null && !connectInfo.pullMode)
				UnicastRemoteObject.unexportObject(this, true);
		}
		catch (Throwable e) {LogUtil.trace(e);}
		
		if (timer != null) timer.cancel();
		timer = null;

		super.dispose();
	}


	/**
	 * This class represents a named console.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	private static class NamedConsole {
		
		/**
		 * Console.
		 */
		public Console console = null;
		
		/**
		 * Name of console.
		 */
		public String name = null;
		
		/**
		 * Constructor with specified console.
		 * @param console specified console.
		 */
		public NamedConsole(Console console) {
			this.console = console;
			try {
				this.name = console.getName();
			} catch (Throwable e) {LogUtil.trace(e);}
			this.name = this.name != null ? this.name : "noname";
		}

		@Override
		public String toString() {
			return name != null ? name : super.toString();
		}
		
	}
	
	
	/**
	 * Connect method.
	 */
	public static void connect() {
		Connector connector = Connector.connect();
		Server server = connector.getServer();
		ConnectInfo connectInfo = connector.getConnectInfo();
		if (server == null) {
			JOptionPane.showMessageDialog(
				null, "Fail to retrieve server", "Fail to retrieve server", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (connectInfo.bindUri != null && !connectInfo.pullMode && Connector.isPullModeRequired(server)) {
			JOptionPane.showMessageDialog(null,
				"Can't retrieve server because PULL MODE is not set\n" +
				"whereas the remote server requires PULL MODE.\n" +
				"You have to check PULL MODE in connection dialog.",
				"Retrieval to server failed", JOptionPane.ERROR_MESSAGE);
			return;
		}
		if (!(server instanceof PowerServer)) {
			JOptionPane.showMessageDialog(
				null, "Not power server", "Not power server", JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		List<App> apps = Util.newList();
		try {
			apps = ((PowerServer)server).getExtraService().getApps();
		} catch (Throwable e) {LogUtil.trace(e);}
		if (apps.size() == 0) {
			JOptionPane.showMessageDialog(
				null, "Applications empty", "Applications empty", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		List<NamedConsole> consoles = Util.newList();
		for (App app : apps) {
			try {
				Remote ro = app.getRemoteObject();
				if ((ro != null) && (ro instanceof Console)) consoles.add(new NamedConsole((Console)ro));
			} catch (Throwable e) {LogUtil.trace(e);}
		}
		if (consoles.size() == 0) {
			JOptionPane.showMessageDialog(
				null, "Consoles empty", "Consoles empty", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		
		Collections.sort(consoles, new Comparator<NamedConsole>() {

			@Override
			public int compare(NamedConsole o1, NamedConsole o2) {
				return o1.name.compareTo(o2.name);
			}
			
		});

		final StartDlg dlgStarter = new StartDlg((JFrame)null, "Consoles") {
			
			/**
			 * Serial version UID for serializable class.
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void start() {
				Console console = ((NamedConsole) getItemControl().getSelectedItem()).console;
				dispose();
				new ConsoleCP(console, connectInfo, null).setVisible(true);
			}
			
			@Override
			protected JComboBox<?> createItemControl() {
				return new JComboBox<NamedConsole>(consoles.toArray(new NamedConsole[0]));
			}
			
			@Override
			protected TextArea createHelp() {
				TooltipTextArea tooltip = new TooltipTextArea();
				tooltip.setEditable(false);
				return tooltip;
			}
		};

		dlgStarter.setSize(600, 300);
		dlgStarter.setLocationRelativeTo(null);
        dlgStarter.setVisible(true);
	}


	/**
	 * Main method.
	 * @param args specified arguments.
	 */
	public static void main(String[] args) {
		connect();
	}
	
	
}
