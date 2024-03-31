/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.logistic.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.io.Serializable;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingWorker;

import net.hudup.core.logistic.AbstractRunner;
import net.hudup.core.logistic.I18nUtil;
import net.hudup.core.logistic.LogUtil;

/**
 * This class shows a waiting dialog.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
public class WaitDialog implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Parent component of waiting dialog.
	 */
	protected Component comp = null;
	
	
	/**
	 * Waiting dialog.
	 */
	protected JDialog waitDlg = null;
	
	
	/**
	 * Default constructor.
	 * @param comp parent component.
	 */
	public WaitDialog(Component comp) {
		this.comp = comp;
	}

	
	/**
	 * Starting method.
	 * @return this waiting dialog.
	 */
	public synchronized WaitDialog start() {
		if (waitDlg != null) return this;
		
		AbstractRunner runner = new AbstractRunner() {
			
			@Override
			protected void task() {
				waitDlg = createDialog(comp);
				waitDlg.setVisible(true);
				waitDlg = null;
				thread = null;
			}
			
			@Override
			protected void clear() {
				
			}
			
		};
		
		runner.start();
		
		return this;
	}
	
	
	/**
	 * Stopping method.
	 * @return this waiting dialog.
	 */
	public synchronized WaitDialog stop() {
		if (waitDlg != null)
			waitDlg.dispose(); 
		waitDlg = null;
		
		return this;
	}
	
	
	/**
	 * Creating dialog for waiting.
	 * @param comp parent component.
	 * @return dialog for waiting.
	 */
	public static JDialog createDialog(Component comp) {
		JDialog waitDlg = new JDialog(UIUtil.getDialogForComponent(comp), "Please wait...", true);
		waitDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		waitDlg.setSize(200, 100);
		waitDlg.setLocationRelativeTo(null);
		waitDlg.setLayout(new BorderLayout());
		waitDlg.add(new JLabel(I18nUtil.message("please_wait") + "..."), BorderLayout.NORTH);
		
		waitDlg.add(new JLabel(UIUtil.getImageIcon("wait-64x64.gif", I18nUtil.message("please_wait"))), BorderLayout.CENTER);

		waitDlg.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		return waitDlg;
	}
	
	
	/**
	 * This is interface for doing some task.
	 * @author Loc Nguyen
	 * @version 1.0
	 */
	public static interface Task<T> {
		
		/**
		 * Doing some task.
		 * @param params array of parameters
		 * @return some result. Type {@link Void} represents void.
		 */
		T doSomeTask(Object...params);
		
	}

	
	/**
	 * Doing some task.
	 * @param <T> type of result. Type {@link Void} represents void.
	 * @param task specified task.
	 * @param comp parent component.
	 * @param params array of parameters.
	 * @return result.
	 */
	public static <T> T doTask(Task<T> task, Component comp, Object...params) {
		JDialog dlgWait = WaitDialog.createDialog(comp);
		dlgWait.setUndecorated(true);
		
		SwingWorker<T, T> worker = new SwingWorker<T, T>() {
			
			@Override
			protected T doInBackground() throws Exception {
				return task.doSomeTask(params);
			}
			
			@Override
			protected void done() {
				super.done();
				dlgWait.dispose();
			}
		};
		worker.execute();
		
		dlgWait.setVisible(true);
		try {
			return worker.get();
		} catch (Throwable e) {LogUtil.trace(e);}
		return null;
	}
	
	
	/**
	 * Doing some task.
	 * @param <T> type of result. Type {@link Void} represents void.
	 * @param task specified task.
	 * @param params array of parameters.
	 * @return result.
	 */
	public static <T> T doTask(Task<T> task, Object...params) {
		return doTask(task, null, params);
	}
	
	
}
