/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.server.ext;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import net.hudup.core.Util;
import net.hudup.core.client.ConnectInfo;
import net.hudup.core.client.Service;
import net.hudup.core.data.Attribute;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ui.ProfileTable;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.EvaluatorAbstract;
import net.hudup.core.evaluate.EvaluatorEvent;
import net.hudup.core.evaluate.EvaluatorListener;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.NextUpdate;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This class is advanced control panel for evaluator.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@NextUpdate
public class EvaluatorCP2 extends JFrame implements Remote {

	
	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal service.
	 */
	protected Service service = null;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = null;

	
	/**
	 * Exported stub (EvaluatorListener).
	 */
	protected Remote exportedStub = null;

	
	/**
	 * Table of evaluators.
	 */
	protected EvaluatorTable tblEvaluator = null;
	
	
    /**
     * Refreshing button.
     */
    protected JButton btnRefresh = null;

    
    /**
     * Closing button.
     */
    protected JButton btnClose = null;

    
    /**
	 * Constructor with specified service.
	 * @param service specified service.
	 */
	public EvaluatorCP2(Service service) {
		this(service, null);
	}

	
	/**
	 * Constructor with specified service, account, password, and bound URI.
	 * @param service specified service.
	 * @param connectInfo connection information.
	 */
	public EvaluatorCP2(Service service, ConnectInfo connectInfo) {
		super("Evaluator control panel list");
		this.service = service;
		this.connectInfo = connectInfo != null ? connectInfo : (connectInfo = new ConnectInfo());
		
		if (connectInfo.bindUri != null) { //Evaluator is remote
			this.exportedStub = NetUtil.RegistryRemote.export(this, connectInfo.bindUri.getPort());
			if (this.exportedStub != null)
				LogUtil.info("Evaluator control panel exported at port " + connectInfo.bindUri.getPort());
			else
				LogUtil.info("Evaluator control panel failed to exported");
		}

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(null);
        Image image = UIUtil.getImage("evaluator-32x32.png");
        if (image != null) setIconImage(image);
		
        addWindowListener(new WindowAdapter() {
			
			@Override
			public void windowClosed(WindowEvent e) {
				super.windowClosed(e);
			}
			
		});
        
        setLayout(new BorderLayout());
        JPanel header = new JPanel(new BorderLayout());
        add(header, BorderLayout.NORTH);
        
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);
		
		tblEvaluator = new EvaluatorTable(service, connectInfo);
		body.add(new JScrollPane(tblEvaluator), BorderLayout.CENTER);

		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		btnRefresh = new JButton("Refresh");
		btnRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
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
		
		refresh();
	}

	
	/**
	 * Refreshing evaluators.
	 */
	protected synchronized void refresh() {
		tblEvaluator.update();
	}

		
	@Override
	public void dispose() {
		tblEvaluator.clear();
		
		if (exportedStub != null) {
			boolean ret = NetUtil.RegistryRemote.unexport(this);
			if (ret)
				LogUtil.info("Evaluator control panel unexported successfully");
			else
				LogUtil.info("Evaluator control panel unexported failedly");
			exportedStub = null;
		}

		super.dispose();
	}

	
}



/**
 * Table to show evaluators.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
class EvaluatorTable extends ProfileTable implements EvaluatorListener {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Internal service.
	 */
	protected Service service = null;
	
	
	/**
	 * Connection information.
	 */
	protected ConnectInfo connectInfo = null;


	/**
	 * Constructor with specified service and connection information.
	 * @param service specified service.
	 * @param connectInfo specified connection information.
	 */
	public EvaluatorTable(Service service, ConnectInfo connectInfo) {
		this.service = service;
		this.connectInfo = connectInfo;
	}
	
	
	/**
	 * Clearing evaluator table.
	 */
	public synchronized void clear() {
		List<Profile> data = getData();
		for (Profile profile : data) {
			try {
				Evaluator evaluator = ((EvaluatorCP.EvaluatorItem) profile.getValue(0)).evaluator;
				if (!EvaluatorAbstract.isPullModeRequired(evaluator) && !connectInfo.pullMode)
					evaluator.removeEvaluatorListener(this);
			} catch (Exception e) {LogUtil.trace(e);}
		}
		
		super.update(Util.newList());
	}
	
	
	/**
	 * Updating evaluator table.
	 */
	public synchronized void update() {
		clear();
		
		List<Profile> newData = Util.newList();
		List<Evaluator> evaluators = ExtendedService.getEvaluators(service, connectInfo);
		if (evaluators.size() == 0) return;
		
		for (Evaluator evaluator : evaluators) {
			Profile profile = emptyProfile();
			profile.setValue(0, new EvaluatorCP.EvaluatorItem(evaluator));
			profile.setValue(1, getStatusText(evaluator));
			newData.add(profile);
		}
		
		super.update(newData);
		
		for (Evaluator evaluator : evaluators) {
			if (!EvaluatorAbstract.isPullModeRequired(evaluator) && !connectInfo.pullMode) {
				try {
					evaluator.addEvaluatorListener(this);
				} catch (Exception e) {LogUtil.trace(e);}
			}
		}
	}
	
	
	@Override
	public synchronized void receivedEvaluator(EvaluatorEvent evt) throws RemoteException {
		int n = getRowCount();
		if (n == 0) return;
		for (int i = 0; i < n; i++) {
			try {
				Evaluator evaluator = ((EvaluatorCP.EvaluatorItem)getValueAt(i, 0)).evaluator;
				if (evaluator == evt.getEvaluator()) {
					setValueAt(getStatusText(evaluator), i, 1);
					return;
				}
			} catch (Exception e) {LogUtil.trace(e);}
		}
	}

	
	@Override
	public boolean classPathContains(String className) throws RemoteException {
    	try {
    		Util.getPluginManager().loadClass(className, false);
    		return true;
    	} catch (Exception e) {}
    	
		return false;
	}

	
	@Override
	public boolean ping() throws RemoteException {
		return true;
	}


	/**
	 * Getting status text of specified evaluator.
	 * @param evaluator specified evaluator.
	 * @return status text of specified evaluator.
	 */
	public static String getStatusText(Evaluator evaluator) {
		try {
			if (!evaluator.remoteIsStarted())
				return "stopped";
			else if (evaluator.remoteIsRunning())
				return "running...";
			else
				return "paused";
		}
		catch (Exception e) {LogUtil.trace(e);}

		return "unknown";
	}
	
	
	/**
	 * Creating empty profile.
	 * @return empty profile.
	 */
	private Profile emptyProfile() {
		AttributeList attList = AttributeList.create(new Attribute[] {new Attribute("Evaluator", Type.object), new Attribute("Status", Type.string)});
		return Profile.createProfile(attList, null);
	}
	
	
}
