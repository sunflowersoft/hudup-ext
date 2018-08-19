package net.hudup.core.alg;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.event.EventListenerList;

import net.hudup.core.Util;
import net.hudup.core.alg.SetupAlgEvent.Type;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.Dataset;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Profile;
import net.hudup.core.logistic.ui.UIUtil;

/**
 * This is the most abstract class for testing algorithm.
 * 
 * @author Loc Nguyen
 * @version 1.0
 */
public abstract class AbstractTestingAlg extends AbstractAlg implements TestingAlg {


	/**
	 * Serial version UID for serializable class.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Data sample for testing algorithm.
	 */
	protected Dataset dataset = null;
	
	
	/**
	 * Data sample for testing algorithm.
	 */
	protected Fetcher<Profile> sample = null;
	
	
	/**
	 * Holding a list of listeners.
	 * 
	 */
    protected EventListenerList listenerList = new EventListenerList();
    

    /**
     * Default constructor.
     */
    public AbstractTestingAlg() {
		super();
		// TODO Auto-generated constructor stub
	}


	@SuppressWarnings("unchecked")
	@Override
	public synchronized void setup(Dataset dataset, Object...info) throws Exception {
		// TODO Auto-generated method stub
		unsetup();
		this.dataset = dataset;
		if (info != null && info.length > 0 && (info[0] instanceof Fetcher<?>))
			this.sample = (Fetcher<Profile>)info[0];
		else
			this.sample = dataset.fetchSample();
		
		learn();
		
		SetupAlgEvent evt = new SetupAlgEvent(
				this,
				Type.done,
				this,
				dataset,
				"Learned models: " + this.getDescription());
		fireSetupEvent(evt);
	}

	
	@Override
	public void setup(Fetcher<Profile> sample, Object...info) throws Exception {
		// TODO Auto-generated method stub
		List<Object> additionalInfo = Util.newList();
		additionalInfo.add(sample);
		additionalInfo.addAll(Arrays.asList(info));

		setup((Dataset)null, additionalInfo.toArray());
	}


	@Override
	public synchronized void unsetup() {
		try {
			if (dataset != null && sample != null)
				sample.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		finally {
			sample = null;
		}
	
		if (dataset != null && dataset.isExclusive())
			dataset.clear();
		dataset = null;
	}

	
	@Override
	public synchronized void manifest() {
		// TODO Auto-generated method stub
		JDialog manifestDlg = new JDialog(UIUtil.getFrameForComponent(null), "Manifest", true);
		manifestDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		manifestDlg.setLocationRelativeTo(null);
		manifestDlg.setSize(400, 200);
		manifestDlg.setLayout(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		manifestDlg.add(body, BorderLayout.CENTER);
		
		JTextArea txtDesc = new JTextArea(getDescription());
		txtDesc.setEditable(false);
		txtDesc.setLineWrap(true);
		txtDesc.setWrapStyleWord(true);
		body.add(new JScrollPane(txtDesc), BorderLayout.CENTER);
		
		JPanel footer = new JPanel();
		manifestDlg.add(footer, BorderLayout.SOUTH);
		
		JButton btnOK = new JButton("OK");
		btnOK.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				manifestDlg.dispose();
			}
		});
		footer.add(btnOK);
		
		manifestDlg.setVisible(true);
	}

	
	@Override
	public void addSetupListener(SetupAlgListener listener) {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			listenerList.add(SetupAlgListener.class, listener);
		}
	}


	@Override
	public void removeSetupListener(SetupAlgListener listener) {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			listenerList.remove(SetupAlgListener.class, listener);
		}
	}


	/**
	 * Getting an array of listeners for this EM.
	 * @return array of listeners for this EM.
	 */
	protected SetupAlgListener[] getSetupListeners() {
		// TODO Auto-generated method stub
		synchronized (listenerList) {
			return listenerList.getListeners(SetupAlgListener.class);
		}
	}


    /**
     * Firing (issuing) an event from this EM to all listeners. 
     * @param evt event from this EM.
     */
	protected void fireSetupEvent(SetupAlgEvent evt) {
		// TODO Auto-generated method stub
		SetupAlgListener[] listeners = getSetupListeners();
		for (SetupAlgListener listener : listeners) {
			try {
				listener.receivedSetup(evt);
			}
			catch (Throwable e) {
				e.printStackTrace();
			}
		}
	}


	/**
	 * Getting attribute list of sample.
	 * @param sample specified sample.
	 * @return attribute list of sample.
	 */
	public static AttributeList getSampleAttributeList(Fetcher<Profile> sample) {
		AttributeList attList = null;
		try {
			synchronized (sample) {
				while (sample.next()) {
					Profile profile = sample.pick();
					if (profile == null)
						continue;
					
					attList = profile.getAttRef();
					if (attList != null)
						break;
				}
				sample.reset();
			}
		}
		catch (Throwable e) {
			e.printStackTrace();
			attList = null;
		}
		
		return attList;
	}
	
	
	@Override
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
		
		try {
			unsetup();
		}
		catch (Throwable e) {
			e.printStackTrace();
		}
	}


}
