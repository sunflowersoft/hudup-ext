/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.temp;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.event.EventListenerList;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.reflections.Reflections;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.util.FilterBuilder;

import com.google.common.base.Predicate;

import net.hudup.core.PluginChangedEvent;
import net.hudup.core.PluginChangedListener;
import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.RegisterTableList;
import net.hudup.core.RegisterTableList.RegisterTableItem;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.alg.AlgRemoteWrapper;
import net.hudup.core.alg.ui.AlgConfigDlg;
import net.hudup.core.alg.ui.AlgListBox;
import net.hudup.core.client.ClientUtil;
import net.hudup.core.client.Service;
import net.hudup.core.client.SocketConnection;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.DataDriver;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.DataDriverList;
import net.hudup.core.data.Exportable;
import net.hudup.core.data.HiddenText;
import net.hudup.core.data.ui.DatasetConfigurator;
import net.hudup.core.evaluate.Evaluator;
import net.hudup.core.evaluate.MetaMetric;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.evaluate.MetricValue;
import net.hudup.core.evaluate.MetricWrapper;
import net.hudup.core.evaluate.Metrics;
import net.hudup.core.evaluate.MetricsUtil;
import net.hudup.core.logistic.BaseClass;
import net.hudup.core.logistic.ClipboardUtil;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.NetUtil;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.DescriptionDlg;
import net.hudup.core.logistic.ui.TagTextField;
import net.hudup.core.logistic.ui.UIUtil;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.RmiServerIndicator;
import net.hudup.core.parser.SnapshotParserImpl;
import net.hudup.core.parser.SocketServerIndicator;
import net.hudup.listener.RemoteInfo;
import net.hudup.listener.RemoteInfoList;

/**
 * This is temporal testing class.
 * 
 * @author Loc Nguyen
 * @version 12.0
 *
 */
@Deprecated
public class Test {

	
	/**
	 * Main method.
	 * @param args argument.
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//getInstances(NeighborCF.class, xURI.create("/E:/sim.jar"));
		//System.out.println(System.getProperty("java.rmi.server.useCodebaseOnly"));
//		Util.getPluginManager().fire();
//		AlgDesc2List list = new AlgDesc2List();
//		list.addAll2(PluginStorage.getNormalAlgReg().getAlgList());
		//AlgDesc2Table.showDlg(null, list);
//		System.out.println(new Date(1));
//		UIManager.LookAndFeelInfo[] looks = UIManager.getInstalledLookAndFeels();
//		for (UIManager.LookAndFeelInfo look : looks) {
//			System.out.println(look.getClassName());
//		}
//		
//		System.out.println(System.getProperties().getProperty("java.rmi.server.codebase"));
		
//		HudupSocketClassLoader nlc = new HudupSocketClassLoader();
//		Class<?> cls = nlc.loadClass("net.hudup.SimFirer");
//		System.out.println(cls);
		
		System.out.println(NetUtil.testPort(-1));
	}

	
//	/**
//	 * Default constructor.
//	 */
//	public Test() {
//		// TODO Auto-generated constructor stub
//	}
//
//	
	/**
	 * Getting a list of instances from reflections and referred class.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class.
	 * @param reflections specified reflections. 
	 * @return list of instances from specified package and referred class.
	 */
	static <T> List<T> getInstances(Class<T> referredClass, Reflections reflections) {
		Set<Class<? extends T>> apClasses = reflections.getSubTypesOf(referredClass);
		List<T> instances = Util.newList();
		for (Class<? extends T> apClass : apClasses) {
			if (apClass == null) continue;

			if (!referredClass.isAssignableFrom(apClass))
				continue;
			
			if (apClass.isInterface() || apClass.isMemberClass() || apClass.isAnonymousClass())
				continue;
			
			int modifiers = apClass.getModifiers();
			if ( (modifiers & Modifier.ABSTRACT) != 0 || (modifiers & Modifier.PUBLIC) == 0)
				continue;
			
			if (apClass.getAnnotation(BaseClass.class) != null || 
					apClass.getAnnotation(Deprecated.class) != null) {
				continue;
			}

			try {
				T instance = Util.newInstance(apClass);
				instances.add(instance);
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
			
		}
		
		return instances;
	}

	
	/**
	 * Getting a list of instances from JAR list and referred class.
	 * @param <T> type of returned instances.
	 * @param referredClass referred class.
	 * @param jarUriList list of JAR URI (s).
	 * @return list of instances from specified package and referred class.
	 */
	static <T> List<T> getInstances(Class<T> referredClass, xURI...jarUriList) {
		List<URL> formalJarUrlList = Util.newList(jarUriList.length);
		for (xURI jarUri : jarUriList) {
			try {
				URL formalJarUrl = jarUri.toURL(); //new URL("jar", "", jarUri.toURL() + "!/");
				formalJarUrlList.add(formalJarUrl);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		URLClassLoader classLoader = new URLClassLoader(
				formalJarUrlList.toArray(new URL[] {})/*, Test.class.getClassLoader()*/);
		try {
			//URL c = classLoader.findResource("net/hudup/em/AbstractEM.class");
			//Class<?> c = Class.forName("org.apache.commons.math3.Field", true, classLoader);
			//System.out.println(c.toString());
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		Reflections2 reflections = new Reflections2("net", classLoader);
		
		List<T> instances = getInstances(referredClass, reflections);
		try {
			classLoader.close();
		} catch (Throwable e) {LogUtil.trace(e);}
		return instances;
	}


}

/**
 * This class is extended reflections.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
@Deprecated
class Reflections2 extends Reflections {

	
	/**
	 * Constructor with prefix and class loaders.
	 * @param prefix specified prefix.
	 * @param classLoaders class loaders.
	 */
	public Reflections2(String prefix, ClassLoader...classLoaders) {
		super(new ConfigurationBuilder() {
			{
				Predicate<String> filter = new FilterBuilder.Include(FilterBuilder.prefix(prefix));
				setUrls(ClasspathHelper.forPackage(prefix, classLoaders));
				filterInputsBy(filter);
				
				setScanners(
					new TypeAnnotationsScanner().filterResultsBy(filter),
					new SubTypesScanner().filterResultsBy(filter));
            }
		});
	}
	
	
}


/**
 * {@link PluginStorage} manages many {@link RegisterTable} (s) and each {@link RegisterTable} stores algorithms having the same type.
 * For example, a register table manages recommendation algorithms (recommenders) whereas another manages metrics for evaluating recommenders.
 * This {@link PluginStorageManifest2} which is the graphic user interface (GUI) allows users to manage {@link PluginStorage}.
 * Every time {@link PluginStorage} was changed, an event {@link PluginChangedEvent} is issued and dispatched to a listener {@link PluginChangedListener}.
 * Later on, {@link PluginChangedListener} can do some tasks in its method {@link PluginChangedListener#pluginChanged(PluginChangedEvent)}.
 * Please pay attention that such {@link PluginChangedListener} must be registered with {@link PluginStorageManifest2} before to receive {@link PluginChangedEvent}.
 * <br> 
 * As a convention, this class is called {@code plug-in storage manifest}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class PluginStorageManifest2 extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Internal list of registered {@link PluginChangedListener} (s).
	 */
    protected EventListenerList listenerList = new EventListenerList();

    
    /**
     * Exported port.
     */
    protected int port = 0;

    
    /**
	 * Default constructor.
	 */
	public PluginStorageManifest2() {
		super(new RegisterTM2());
		update();
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if(SwingUtilities.isRightMouseButton(e) ) {
					JPopupMenu contextMenu = createContextMenu();
					if (contextMenu != null)
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
				}
				else if (e.getClickCount() >= 2) {
					int selectedColumn = getSelectedColumn();
					if (selectedColumn != 4 && selectedColumn != 5 && selectedColumn != 6)
						showConfig();
				}
			}
			
		});
	}
	
	
	/**
	 * Default constructor with specified exported port.
	 * @param port specified exported port.
	 */
	public PluginStorageManifest2(int port) {
		this();
		this.port = port;
	}
	
	
	/**
	 * Create context menu.
	 * @return context menu.
	 */
	private JPopupMenu createContextMenu() {
		JPopupMenu contextMenu = new JPopupMenu();
		
		int selectedRow = getSelectedRow();
		Alg alg = selectedRow < 0 ? null : (Alg) getModel().getValueAt(selectedRow, 3);
		DataConfig config = alg == null ? null : alg.getConfig(); 
		if (config != null) {
			JMenuItem miConfig = UIUtil.makeMenuItem( (String)null, "Configuration", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						showConfig();
					}
				});
			contextMenu.add(miConfig);
			
			contextMenu.addSeparator();
		}
		
		JMenuItem miRegisterAllAlgs = UIUtil.makeMenuItem( (String)null, "Register all algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(true, 4);
				}
			});
		contextMenu.add(miRegisterAllAlgs);
		
		JMenuItem miUnregisterAllAlgs = UIUtil.makeMenuItem( (String)null, "Unregister all algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(false, 4);
				}
			});
		contextMenu.add(miUnregisterAllAlgs);

		contextMenu.addSeparator();
		
		JMenuItem miExportAllAlgs = UIUtil.makeMenuItem( (String)null, "Export all algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(true, 5);
				}
			});
		contextMenu.add(miExportAllAlgs);
		
		JMenuItem miUnexportAllAlgs = UIUtil.makeMenuItem( (String)null, "Unexport all algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(false, 5);
				}
			});
		contextMenu.add(miUnexportAllAlgs);

		contextMenu.addSeparator();
		
		JMenuItem miRemoveAllAlgs = UIUtil.makeMenuItem( (String)null, "Remove all algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(true, 6);
				}
			});
		contextMenu.add(miRemoveAllAlgs);
		
		JMenuItem miUnremoveAllAlgs = UIUtil.makeMenuItem( (String)null, "Unremove all algorithms", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					selectAllNormalAlgs(false, 6);
				}
			});
		contextMenu.add(miUnremoveAllAlgs);
		
		
		return contextMenu;
	}
	
	
	/**
	 * Showing configuration dialog of selected algorithm. 
	 */
	private void showConfig() {
		int selectedRow = getSelectedRow();
		Alg alg = selectedRow < 0 ? null : (Alg) getModel().getValueAt(selectedRow, 3);
		DataConfig config = alg == null ? null : alg.getConfig(); 
		
		if (config == null) {
			JOptionPane.showMessageDialog(
					UIUtil.getFrameForComponent(this), 
					"Apply plug-in storage successfully. Algorithms are registered/unregistered/removed/unremoved", 
					"Apply successfully", 
					JOptionPane.INFORMATION_MESSAGE);
		}
		else {
			AlgConfigDlg dlgConfig = new AlgConfigDlg(UIUtil.getFrameForComponent(getThisManifest()), alg);
			dlgConfig.getPropPane().setToolbarVisible(false);
			dlgConfig.getPropPane().setControlVisible(false);
			dlgConfig.getPropPane().setEnabled(false);
			dlgConfig.setVisible(true);
		}
	}
	
	
	/**
	 * Getting this manifest.
	 * @return this manifest.
	 */
	private PluginStorageManifest2 getThisManifest() {
		return this;
	}
	
	
	/**
	 * Updating {@link PluginStorageManifest2} according to {@link RegisterTM2}.
	 */
	private void update() {
		getRegisterTM().update();
		
		if (getColumnModel().getColumnCount() > 3) {
			getColumnModel().getColumn(3).setMaxWidth(0);
			getColumnModel().getColumn(3).setMinWidth(0);
			getColumnModel().getColumn(3).setPreferredWidth(0);
		}
	}

	
	/**
	 * This public method is called from outside in order to update {@link PluginStorageManifest2} according to {@link RegisterTM2}.
	 * It really calls method {@link #update()}.
	 * @return true if {@link PluginStorageManifest2} is updated successfully from outside.
	 */
	public boolean apply() {
		boolean idle = isListenersIdle();
		if (!idle) return false;

		
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		int n = getRowCount();
		List<Alg> unexportedAlgList = Util.newList();
		boolean changed = false;
		for (int i = 0; i < n; i++) {
			Alg alg = (Alg) getValueAt(i, 3);
			RegisterTable table = PluginStorage.lookupTable(alg.getClass());
			if (table == null) continue;

			boolean registered = (Boolean)getValueAt(i, 4);
			boolean exported = (Boolean)getValueAt(i, 5);
			boolean removed = (Boolean)getValueAt(i, 6);
			
			if (removed) {
				if (!table.contains(alg.getName()))
					nextUpdateList.remove(alg);
				else
					table.unregister(alg.getName());
				
				unexportedAlgList.add(alg);
				changed = true;
			}
			else {
				if (registered) {
					if (!table.contains(alg.getName())) {
						table.register(alg);
						nextUpdateList.remove(alg);
						changed = true;
					}
				}
				else if(table.contains(alg.getName())) {
					table.unregister(alg.getName());
					nextUpdateList.add(alg);
					changed = true;
				}
				
				if (alg instanceof Exportable) {
					try {
						boolean algExported = ((Exportable)alg).getExportedStub() != null;
						if (exported) {
							if (!algExported) {
								String algTypeName = getValueAt(i, 0).toString();
								if (algTypeName.equals(PluginStorage.NORMAL_ALG)) { //Only export normal algorithms.
									((Exportable)alg).export(port);
									changed = true;
								}
							}
						}
						else {
							if (algExported) {
								unexportedAlgList.add(alg);
								changed = true;
							}
						}
					} 
					catch (Throwable e) {
						LogUtil.trace(e);
						changed = true;
					}
				}
			}
		}
		
		if (changed)
			firePluginChangedEvent(new PluginChangedEvent(this));
		
		for (Alg alg : unexportedAlgList) {
			if (alg instanceof Exportable) {
				try {
					((Exportable)alg).unexport(); //Finalize method will call unsetup method if unsetup method exists in this algorithm.
				} catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		
		update();

		return true;
	}
	

	/**
	 * Selecting or unselecting all rows according the specified input parameter {@code selected}.
	 * @param selected if {@code true} then all rows are selected. Otherwise, all rows are unselected.
	 * @param column column to be selected or not selected.
	 */
	protected void selectAll(boolean selected, int column) {
		if (column != 4 && column != 5 && column != 6)
			return;
		
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			setValueAt(selected, i, column);
		}
	}

	
	/**
	 * Registering or unregistering all normal algorithms.
	 * @param selected if {@code true} then all normal algorithms are selected. Otherwise, all normal algorithms are unselected. 
	 * @param column column to be selected or not selected.
	 */
	protected void selectAllNormalAlgs(boolean selected, int column) {
		if (column != 4 && column != 5 && column != 6)
			return;

		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			String algTypeName = getValueAt(i, 0).toString();
			if (algTypeName.equals(PluginStorage.NORMAL_ALG))
				setValueAt(selected, i, column);
		}
	}

	
	/**
	 * Getting the model of this {@link PluginStorageManifest2}.
	 * @return register table model {@link RegisterTM2}
	 */
	public RegisterTM2 getRegisterTM() {
		return (RegisterTM2)getModel();
	}
	
	
	/**
	 * Adding the specified listener to the end of list of listeners, which means that such listener is registered.
	 * 
	 * @param listener specified {@link PluginChangedListener} that is registered.
	 * 
	 */
	public void addPluginChangedListener(PluginChangedListener listener) {
		synchronized (listenerList) {
			listenerList.add(PluginChangedListener.class, listener);
		}
    }

    
	/**
	 * Remove the specified listener from the list of listener
	 * 
	 * @param listener {@link PluginChangedListener} that is unregistered.
	 */
    public void removePluginChangedListener(PluginChangedListener listener) {
		synchronized (listenerList) {
			listenerList.remove(PluginChangedListener.class, listener);
		}
    }
	
    
    /**
     * Return an array of registered {@link PluginChangedListener} (s).
     * 
     * @return array of registered {@link PluginChangedListener} (s).
     * 
     */
    protected PluginChangedListener[] getPluginChangedListeners() {
		synchronized (listenerList) {
			return listenerList.getListeners(PluginChangedListener.class);
		}
    }

    
    /**
     * Dispatching {@link PluginChangedEvent} event to registered {@link PluginChangedListener} (s) after {@link PluginStorageManifest2} was changed.
     * @param evt {@link PluginChangedEvent} event is issued to registered {@link PluginChangedListener} (s) after {@link PluginStorageManifest2} was changed.
     */
    protected void firePluginChangedEvent(PluginChangedEvent evt) {
		PluginChangedListener[] listeners = getPluginChangedListeners();
		
		for (PluginChangedListener listener : listeners) {
			try {
				listener.pluginChanged(evt);
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
	
    }

    
    /**
     * Testing whether a registered {@link PluginChangedListener} is idle.
     * @return whether a registered {@link PluginChangedListener} is idle. Note, there can be many registered {@link PluginChangedListener} (s). 
     */
    protected boolean isListenersIdle() {
		PluginChangedListener[] listeners = getPluginChangedListeners();
		
		for (PluginChangedListener listener : listeners) {
			try {
				if (!listener.isIdle())
					return false;
			}
			catch (Throwable e) {
				LogUtil.trace(e);
			}
		}
		
		return true;
	
    }

    
	/**
	 * Testing whether manifest table is modified.
	 * @return whether manifest table is modified.
	 */
	public boolean isModified() {
		return getRegisterTM().isModified();
	}

	
	/**
	 * Panel for plug-in storage manifest.
	 * @author Loc Nguyen
	 * @version 12.0
	 */
	public static class PluginStorageManifestPanel extends JPanel {

		/**
		 * Default serial version UID.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * Plug-in storage manifest.
		 */
		protected PluginStorageManifest2 tblRegister = null;
		
		/**
		 * Constructor with plug-in changed listener.
		 * @param listener plug-in changed listener.
		 */
		public PluginStorageManifestPanel(PluginChangedListener listener) {
			setLayout(new BorderLayout());
			JPanel body = new JPanel(new BorderLayout());
			add(body, BorderLayout.CENTER);
			
			int port = 0;
			try {
				port = listener != null ? listener.getPort() : 0;
			} catch (Exception e) {port = 0; LogUtil.trace(e);}
			tblRegister = new PluginStorageManifest2(port < 0 ? 0 : port);
			if (listener != null)
				tblRegister.addPluginChangedListener(listener);
			body.add(new JScrollPane(tblRegister), BorderLayout.CENTER);
			
			JPanel footer = new JPanel(new BorderLayout());
			add(footer, BorderLayout.SOUTH);

			JPanel toolbar1 = new JPanel(new FlowLayout(FlowLayout.RIGHT));
			footer.add(toolbar1, BorderLayout.NORTH);
			
			JPanel toolbar1Grp1 = new JPanel(new BorderLayout());
			toolbar1Grp1.setBorder(BorderFactory.createEtchedBorder());
			toolbar1.add(toolbar1Grp1);

			toolbar1Grp1.add(new JLabel("Register/Unregister"), BorderLayout.NORTH);
			JPanel toolbar1Grp1Buttons = new JPanel();
			toolbar1Grp1.add(toolbar1Grp1Buttons, BorderLayout.SOUTH);
			
			JButton registerAll = UIUtil.makeIconButton(
				"selectall-16x16.png", 
				"register_all", "Register all", "Register all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(true, 4);
					}
				});
			registerAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp1Buttons.add(registerAll);

			JButton unregisterAll = UIUtil.makeIconButton(
				"unselectall-16x16.png", 
				"unregister_all", "Unregister all", "Unregister all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(false, 4);
					}
				});
			unregisterAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp1Buttons.add(unregisterAll);

			JPanel toolbar1Grp2 = new JPanel(new BorderLayout());
			toolbar1Grp2.setBorder(BorderFactory.createEtchedBorder());
			toolbar1.add(toolbar1Grp2);

			toolbar1Grp2.add(new JLabel("Export/Unexport"), BorderLayout.NORTH);
			JPanel toolbar1Grp2Buttons = new JPanel();
			toolbar1Grp2.add(toolbar1Grp2Buttons, BorderLayout.SOUTH);

			JButton exportAll = UIUtil.makeIconButton(
				"selectall-16x16.png", 
				"export_all", "Export all", "Export all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(true, 5);
					}
				});
			exportAll.setMargin(new Insets(0, 0 , 0, 0));
			exportAll.setToolTipText("Only export normal algorithms");
			toolbar1Grp2Buttons.add(exportAll);

			JButton unexportAll = UIUtil.makeIconButton(
				"unselectall-16x16.png", 
				"unexport_all", "Unexport all", "Unexport all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(false, 5);
					}
				});
			unexportAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp2Buttons.add(unexportAll);

			JPanel toolbar1Grp3 = new JPanel(new BorderLayout());
			toolbar1Grp3.setBorder(BorderFactory.createEtchedBorder());
			toolbar1.add(toolbar1Grp3);

			toolbar1Grp3.add(new JLabel("Remove/Unremove"), BorderLayout.NORTH);
			JPanel toolbar1Grp3Buttons = new JPanel();
			toolbar1Grp3.add(toolbar1Grp3Buttons, BorderLayout.SOUTH);

			JButton removeAll = UIUtil.makeIconButton(
				"selectall-16x16.png", 
				"remove_all", "Remove all", "Remove all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(true, 6);
					}
				});
			removeAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp3Buttons.add(removeAll);

			JButton unremoveAll = UIUtil.makeIconButton(
				"unselectall-16x16.png", 
				"unremove_all", "Unremove all", "Unremove all", 
				new ActionListener() {
					
					@Override
					public void actionPerformed(ActionEvent e) {
						// TODO Auto-generated method stub
						tblRegister.selectAll(false, 6);
					}
				});
			unremoveAll.setMargin(new Insets(0, 0 , 0, 0));
			toolbar1Grp3Buttons.add(unremoveAll);

			
			JPanel toolbar2 = new JPanel(new BorderLayout());
			footer.add(toolbar2, BorderLayout.SOUTH);
			
			JPanel toolbar2Grp1 = new JPanel();
			toolbar2.add(toolbar2Grp1, BorderLayout.WEST);

			//Reload plugin storage from built packages.
			JButton reloadAlg = new JButton("Reload");
			reloadAlg.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					tblRegister.firePluginChangedEvent(new PluginChangedEvent(tblRegister)); //Force to unsetting up algorithms.
					Util.getPluginManager().discover();
					tblRegister.update();
					tblRegister.firePluginChangedEvent(new PluginChangedEvent(tblRegister));
				}
			});
			reloadAlg.setToolTipText("Reload plugin storage from built packages");
			toolbar2Grp1.add(reloadAlg);

			//Only import normal algorithms
			JButton importAlg = new JButton("Import");
			importAlg.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					if (tblRegister.isModified()) {
						int confirm = JOptionPane.showConfirmDialog(
								tblRegister, 
								"System properties are modified. Do you want to apply them?", 
								"System properties are modified", 
								JOptionPane.YES_NO_OPTION,
								JOptionPane.QUESTION_MESSAGE);
						
						if (confirm == JOptionPane.YES_OPTION)
							apply();
					}
					
					//new ImportAlgDlag(tblRegister).setVisible(true);
					tblRegister.update();
				}
			});
			importAlg.setToolTipText("Only import normal algorithms");
			toolbar2Grp1.add(importAlg);
			
			JPanel toolbar2Grp2 = new JPanel();
			toolbar2.add(toolbar2Grp2, BorderLayout.CENTER);
			
			JButton apply = new JButton("Apply");
			apply.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					apply();
				}
			});
			toolbar2Grp2.add(apply);

			JButton reset = new JButton("Reset");
			reset.addActionListener(new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					// TODO Auto-generated method stub
					tblRegister.update();
				}
			});
			toolbar2Grp2.add(reset);

		}
		
		/**
		 * Testing whether the plug-in storage manifest is modified.
		 * @return whether the plug-in storage manifest is modified.
		 */
		public boolean isModified() {
			return tblRegister.isModified();
		}
		
		/**
		 * Applying changes to the plug-in storage manifest.
		 * @return true if applying is successful.
		 */
		public boolean apply() {
			boolean ret = tblRegister.apply();
			if (ret) {
				JOptionPane.showMessageDialog(
						UIUtil.getFrameForComponent(tblRegister), 
						"Apply plug-in storage successfully.\nAlgorithms were registered/unregistered exported/unexported removed/unremoved.", 
						"Apply successfully", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			else {
				JOptionPane.showMessageDialog(
						UIUtil.getFrameForComponent(tblRegister), 
						"Apply plug-in storage failed", 
						"Apply failed", 
						JOptionPane.INFORMATION_MESSAGE);
			}
			return ret;
		}
		
	}
	
	
	/**
	 * Showing a dialog containing {@link PluginStorageManifest2}.
	 * @param comp parent component.
	 * @param listener {@link PluginChangedListener} to receive {@link PluginChangedEvent} if {@link PluginStorageManifest2} is changed. 
	 * @param modal whether or not the dialog is modal. The modal dialog will block user inputs. Please see {@link JDialog} for more details. 
	 */
	public static void showDlg(Component comp, PluginChangedListener listener, boolean modal) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Plugin storage", modal);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		dlg.add(new PluginStorageManifestPanel(listener), BorderLayout.CENTER);
		
		dlg.setVisible(true);
	}
	
	
}



/**
 * This is table model of {@link PluginStorageManifest2} because {@link PluginStorageManifest2} is itself a table. 
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class RegisterTM2 extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Whether or not this model was modified.
	 */
	protected boolean modified = false;

	
	/**
	 * Default constructor.
	 */
	public RegisterTM2() {
		super();
		update();
	}
	
	
	/**
	 * Updating this model.
	 */
	public void update() {
		Vector<Vector<Object>> data = Util.newVector();
		
		updateReg(data);
		
		updateNextUpdateList(data);
		
		setDataVector(data, toColumns());
		
		modified = false;
	}
	
	
	/**
	 * Updating this model with a matrix of objects.
	 * @param data specified data as a matrix of objects.
	 */
	private void updateReg(Vector<Vector<Object>> data) {
		RegisterTableList list = PluginStorage.getRegisterTableList();
		for (int i = 0; i < list.size(); i++) {
			RegisterTableItem item = list.get(i);
			updateEachReg(data, item.getRegisterTable());
			
		}
		
	}
	
	
	/**
	 * Creating data from the specified register table for this model.
	 * @param data output parameter that is filled from the specified register table.
	 * @param regTable specified register table.
	 */
	private void updateEachReg(Vector<Vector<Object>> data, RegisterTable regTable) {
		
		List<Alg> algList = regTable.getAlgList();
		for (Alg alg : algList) {
			Vector<Object> row = Util.newVector();
			
			row.add(PluginStorage.lookupTableName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
			row.add(true);
			
			boolean exported = false;
			if (alg instanceof Exportable) {
				try {
					exported = ((Exportable)alg).getExportedStub() != null;
				} catch (Throwable e) {
					LogUtil.trace(e);
					exported = false;
				}
			}
			row.add(exported);

			row.add(false);

			data.add(row);
		}
		
	}
	
	
	/**
	 * Creating data from next-update algorithms for this model.
	 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * List of next-update algorithms is managed by {@link PluginStorage}.
	 * 
	 * @param data output parameter that is filled from next-update algorithms.
	 */
	private void updateNextUpdateList(Vector<Vector<Object>> data) {
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (int i = 0; i < nextUpdateList.size(); i++) {
			Alg alg = nextUpdateList.get(i);
			Vector<Object> row = Util.newVector();
			
			row.add(PluginStorage.lookupTableName(alg.getClass()));
			row.add(alg.getName());
			row.add(alg.getClass().toString());
			row.add(alg);
			row.add(false);
			
			boolean exported = false;
			if (alg instanceof Exportable) {
				try {
					exported = ((Exportable)alg).getExportedStub() != null;
				} catch (Throwable e) {
					LogUtil.trace(e);
					exported = false;
				}
			}
			row.add(exported);

			row.add(false);

			data.add(row);
			
		}
	}
	
	
	/**
	 * Creating a string vector for columns of this model.
	 * @return a string vector for columns of this model, specified by {@link Vector} of string.
	 */
	protected Vector<String> toColumns() {
		Vector<String> columns = Util.newVector();
		columns.add("Type");
		columns.add("Name");
		columns.add("Java class");
		columns.add("Object");
		columns.add("Registered");
		columns.add("Exported");
		columns.add("Removed");
		
		return columns;
	}


	/**
	 * Testing whether this model is modified.
	 * @return whether model is modified.
	 */
	public boolean isModified() {
		return modified;
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 4 || columnIndex == 5 || columnIndex == 6)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		if (column == 4 || column == 5 || column == 6)
			return true;
		else
			return false;
	}
	
	
	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, row, column);
		
		modified = true;
	}
	
	
}



/**
 * This graphic user interface (GUI) component as a table shows a list of data drivers ({@link DataDriverList}).
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class DataDriverListTable2 extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public DataDriverListTable2() {
		super(new DataDriverListTM2());
	}
	
	
	/**
	 * Updating this table by specified {@link DataDriverList}.
	 * @param dataDriverList specified {@link DataDriverList}.
	 */
	public void update(DataDriverList dataDriverList) {
		getDataDriverListTM().update(dataDriverList);
	}
	
	
	/**
	 * Getting data model of this table represented by {@link DataDriverListTM2}.
	 * @return data model of this table.
	 */
	public DataDriverListTM2 getDataDriverListTM() {
		return (DataDriverListTM2)getModel();
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		DataDriverListTM2 model = getDataDriverListTM();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		if(renderer == null) return super.getCellRenderer(row, column);
		
		return renderer;
	}

	
	/**
	 * This static method creates a GUI component {@link JPanel} that contains a {@link DataDriverListTable2}.
	 * Such {@link DataDriverListTable2} contains the specified list of data drivers.
	 * @param dataDriverList specified data driver list.
	 * @return {@link JPanel} that contains {@link DataDriverListTable2}.
	 */
	public static JPanel createPane(DataDriverList dataDriverList) {
		
		JPanel result = new JPanel(new BorderLayout());
		
		JPanel body = new JPanel(new BorderLayout());
		result.add(body, BorderLayout.CENTER);
		
		final DataDriverListTable2 tblDataDriverList = new DataDriverListTable2();
		tblDataDriverList.update(dataDriverList);
		body.add(new JScrollPane(tblDataDriverList), BorderLayout.CENTER);
		
		return result;
	}
	
	
	/**
	 * This static method shows a GUI component as dialog that contains a {@link DataDriverListTable2}.
	 * Such {@link DataDriverListTable2} contains the specified list of data drivers.
	 * @param comp parent component.
	 * @param dataDriverList specified data driver list.
	 * @param modal if {@code true}, the dialog blocks user inputs.
	 */
	public static void showDlg(Component comp, DataDriverList dataDriverList, boolean modal) {
		JDialog dlg = new JDialog(UIUtil.getFrameForComponent(comp), "Register table", modal);
		dlg.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		dlg.setSize(600, 400);
		dlg.setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		dlg.setLayout(new BorderLayout());
		dlg.add(createPane(dataDriverList), BorderLayout.CENTER);
		
		dlg.setVisible(true);
	}


}


/**
 * This class is the data model of {@link DataDriverListTable2}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class DataDriverListTM2 extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public DataDriverListTM2() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Updating this table by specified {@link DataDriverList}.
	 * @param dataDriverList specified {@link DataDriverList}.
	 */
	public void update(DataDriverList dataDriverList) {
		Vector<Vector<Object>> data = Util.newVector();
		
		for (int i = 0; i < dataDriverList.size(); i++) {
			DataDriver dataDriver = dataDriverList.get(i);
			
			Vector<Object> row = Util.newVector();
			row.add(dataDriver.getName());
			row.add(dataDriver.getInnerClass());
			row.add(dataDriver.isFlatServer());
			row.add(dataDriver.isDbServer());
			row.add(dataDriver.isHudupServer());
			
			data.add(row);
		}
		
		setDataVector(data, toColumns());
	}
	
	
	/**
	 * Creating header names (column names) of the {@link DataDriverListTable2}.
	 * @return {@link Vector} of header names. 
	 */
	protected Vector<String> toColumns() {
		Vector<String> columns = Util.newVector();
		columns.add("Name");
		columns.add("Underlying driver class");
		columns.add("Flat");
		columns.add("Database");
		columns.add("Recommender");
		
		return columns;
	}

	
	/**
	 * Getting the class of value at specified row and columns.
	 * @param row specified row.
	 * @param column specified column.
	 * @return class of value at specified row and columns.
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		
		return value.getClass();
	}

	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}



/**
 * Table for showing metric list.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class MetricsOptionTable2 extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public MetricsOptionTable2() {
		super(new MetricsOptionTM2());
	}


	/**
	 * Updating table by default selected metrics.
	 * 
	 * @param selectedMetricList default selected metrics.
	 */
	public void update(List<Metric> selectedMetricList) {
		getMetricsOptionTM().update(selectedMetricList);

		if (getColumnCount() > 4) {
			TableColumn tc = getColumn(getColumnName(4)); 
			tc.setMinWidth(0);
			tc.setMaxWidth(0);
		}
	}

	
	/**
	 * Getting selected metrics list.
	 * @return selected metrics list.
	 */
	public List<Metric> getSelectedMetricList() {
		return getMetricsOptionTM().getSelectedMetricList();
	}

	
	/**
	 * Getting model for this table as {@link MetricsOptionTM2}.
	 * @return model for this table as {@link MetricsOptionTM2}.
	 */
	public MetricsOptionTM2 getMetricsOptionTM() {
		return (MetricsOptionTM2)getModel();
	}


	@Override
	public void setValueAt(Object aValue, int row, int column) {
		// TODO Auto-generated method stub
		super.setValueAt(aValue, row, column);
		
        if (column != 3)
        	return;
        
        boolean selected = (Boolean)aValue;
    	Metric metric = (Metric) getValueAt(row, 4);
    	if (metric instanceof MetricWrapper)
    		return;
    	
    	if (selected && (metric instanceof MetaMetric)) {
    		selectRelatedMetrics( (MetaMetric)metric );
    	}
    	else if (!selected)
    		unselectRelatedMetrics(metric);
		
	}


	/**
	 * Selecting / deselecting all metrics according to flag.
	 * @param selected specified flag to select / deselect all metrics.
	 */
	public void selectAll(boolean selected) {
		int rows = getRowCount();
		for (int row = 0; row < rows; row++) {
			boolean aValue = (Boolean)getValueAt(row, 3);
			if (aValue != selected)
				setValueAt(selected, row, 3);
		}
	}
	
	
	/**
	 * A meta-metric depends on other metrics and so this method selects such other metrics that relate to the specified meta-metric.
	 * 
	 * @param metaMetric specified meta-metric.
	 */
	private void selectRelatedMetrics(MetaMetric metaMetric) {
		int rows = getRowCount();
		for (int row = 0; row < rows; row++) {
			Metric otherMetric = (Metric) getValueAt(row, 4);
			
			if (!otherMetric.getName().equals(metaMetric.getName()) && 
					!(otherMetric instanceof MetricWrapper) &&
					metaMetric.referTo(otherMetric.getName()))
				setValueAt(true, row, 3);
			
		}
	}
	
	
	/**
	 * A meta-metric depends on other metrics and so this method deselects meta-metrics that relate to the specified metric.
	 * @param metric specified metric.
	 */
	private void unselectRelatedMetrics(Metric metric) {
		
		int rows = getRowCount();
		for (int row = 0; row < rows; row++) {
			Metric otherMetric = (Metric) getValueAt(row, 4);
			if (!(otherMetric instanceof MetaMetric))
				continue;
			
			MetaMetric metaMetric = (MetaMetric)otherMetric;
			if (!metaMetric.getName().equals(metric.getName()) && 
					metaMetric.referTo(metric.getName()))
				setValueAt(false, row, 3);
			
		}
	}
	
	
}


/**
 * Model of the metrics table.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class MetricsOptionTM2 extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public MetricsOptionTM2() {
		
	}

	
	/**
	 * Update this model by specified selected metrics list.
	 * @param selectedMetricList specified selected metrics list.
	 */
	public void update(List<Metric> selectedMetricList) {
		Vector<Vector<Object>> data = Util.newVector();
		
		List<Alg> metricList = PluginStorage.getMetricReg().getAlgList(); 
		for (int i = 0; i < metricList.size(); i++) {
			try {
				Metric metric = (Metric) metricList.get(i);
				
				Vector<Object> row = Util.newVector();
				row.add(i + 1);
				row.add(metric.getName());
				row.add(metric.getTypeName());
				
				boolean found = false;
				for (Metric selectedMetric : selectedMetricList) {
					if (selectedMetric.getName().equals(metric.getName())) {
						found = true;
						break;
					}
				}
				if (found)
					row.add(true);
				else
					row.add(false);
				
				row.add(metric);
				
				data.add(row);
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		
		setDataVector(data, createColumns());
	}
	
	
	/**
	 * Getting selected metrics list.
	 * @return selected metrics list.
	 */
	public List<Metric> getSelectedMetricList() {
		List<Metric> selectedList = Util.newList();
		
		int rows = getRowCount();
		for (int i = 0; i < rows; i++) {
			boolean selected = (Boolean) getValueAt(i, 3);
			
			if (selected)
				selectedList.add((Metric)getValueAt(i, 4));
		}
		
		return selectedList;
	}
	
	
	/**
	 * Creating columns.
	 * @return vector of column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Metric name");
		columns.add("Metric type");
		columns.add("Select");
		columns.add("Metric");
		
		return columns;
	}

	
	@Override
	public Class<?> getColumnClass(int columnIndex) {
		// TODO Auto-generated method stub
		if (columnIndex == 3)
			return Boolean.class;
		else
			return super.getColumnClass(columnIndex);
	}


	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return column == 3;
	}
	
	
}



/**
 * Table to show metrics evaluation.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class MetricsTable2 extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;
	
	
	/**
	 * Default constructor.
	 */
	public MetricsTable2() {
		this(null, null);
	}

	
	/**
	 * Constructor with algorithm table.
	 * @param algTable specified algorithm table.
	 */
	public MetricsTable2(RegisterTable algTable) {
		this(algTable, null);
	}

	
	/**
	 * Constructor with algorithm table and referred evaluator.
	 * @param algTable specified algorithm table.
	 * @param referredEvaluator referred evaluator.
	 */
	public MetricsTable2(final RegisterTable algTable, final Evaluator referredEvaluator) {
		// TODO Auto-generated constructor stub
		super (new MetricsTM2());
		
		this.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				if (SwingUtilities.isRightMouseButton(e)) {
					final Metrics metrics = getMetricsTM().getMetrics();
					if (metrics == null || metrics.size() == 0)
						return;
					
					JPopupMenu contextMenu = new JPopupMenu();
					
					JMenuItem miCopyToClipboard = UIUtil.makeMenuItem((String)null, "Copy to clipboard", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								try {
									ClipboardUtil.util.setText(metrics.translate());
								} catch (Exception ex) {ex.printStackTrace();}
							}
						});
					contextMenu.add(miCopyToClipboard);
					
					JMenuItem miSave = UIUtil.makeMenuItem((String)null, "Save", 
						new ActionListener() {
							
							@Override
							public void actionPerformed(ActionEvent e) {
								MetricsUtil util = new MetricsUtil(metrics, algTable, referredEvaluator);
								util.export(getThis());
							}
						});
					contextMenu.add(miSave);
					
					JMenuItem miAlgDesc = createAlgDescMenuItem();
					if (miAlgDesc != null) {
						contextMenu.addSeparator();
						contextMenu.add(miAlgDesc);
					}

					if(contextMenu != null) 
						contextMenu.show((Component)e.getSource(), e.getX(), e.getY());
					
				}
			}
			
		});
	}

	
	/**
	 * Creating menu item of algorithm description.
	 * @return menu item of algorithm description.
	 */
	private JMenuItem createAlgDescMenuItem() {
		int selectedRow = getSelectedRow();
		if (selectedRow == -1)
			return null;
		Object algName = getValueAt(selectedRow, 1);
		if (algName == null)
			return null;
		Object datasetId = getValueAt(selectedRow, 2);
		if (datasetId == null)
			return null;
		
		try {
			algName = algName.toString();
			datasetId = Integer.parseInt(datasetId.toString());
		}
		catch (Exception e) {
			LogUtil.trace(e);
			return null;
		}
		
		String algDesc = getMetricsTM().getMetrics().getAlgDesc((String)algName, (Integer)datasetId);
		if (algDesc == null || algDesc.isEmpty())
			return null;
		
		String description = "Algorithm \"" + algName + "\":\n    " + algDesc + "\n\n" + "Testing dataset pair: " + datasetId;
		JMenuItem miAlgDesc = UIUtil.makeMenuItem((String)null, "Algorithm description", 
			new ActionListener() {
				
				@Override
				public void actionPerformed(ActionEvent e) {
					new DescriptionDlg(getThis(), "Algorithm description", description).setVisible(true);;
				}
			});
		
		return miAlgDesc;
	}
	
	
	/**
	 * Update table by specified metrics.
	 * @param metrics specified metrics.
	 */
	public synchronized void update(Metrics metrics) {
		
		getMetricsTM().update(metrics);
		
		if (getColumnCount() > 0)
			getColumnModel().getColumn(0).setMaxWidth(50);
	}

	
	/**
	 * Clearing table.
	 */
	public synchronized void clear() {
		getMetricsTM().clear();
	}
	
	
	/**
	 * Getting metrics table model.
	 * @return {@link MetricsTM2}
	 */
	private MetricsTM2 getMetricsTM() {
		return (MetricsTM2) getModel();
	}
	
	
	/**
	 * Getting this metrics table.
	 * @return this metrics table.
	 */
	private MetricsTable2 getThis() {
		return this;
	}
	
	
}



/**
 * This class is Metrics table model.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class MetricsTM2 extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * List of metrics.
	 */
	protected Metrics metrics = null;
	

	/**
	 * Default constructor.
	 */
	public MetricsTM2() {
		super();
		// TODO Auto-generated constructor stub
	}


	/**
	 * Update this model by metrics.
	 * @param metrics specified metrics.
	 */
	public void update(Metrics metrics) {
		if (metrics == null) {
			clear();
			return;
		}
		
		this.metrics = metrics;
		
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		int no = 0;
		for (int i = 0; i < this.metrics.size(); i++) {
			try {
				MetricWrapper wrapper = this.metrics.get(i);
				if (!wrapper.isValid())
					continue;
				
				Metric metric = wrapper.getMetric();
				
				Vector<Object> row = Util.newVector();
				
				row.add(++no);
				row.add(wrapper.getAlgName());
				row.add(wrapper.getDatasetId());
				row.add(metric.getName());
				row.add(metric.getTypeName());
				
				MetricValue metricValue = wrapper.getAccumValue();
				if (metricValue != null)
					row.add(metricValue);
				else
					row.add("");
					
				data.add(row);
			}
			catch (Throwable e) {LogUtil.trace(e);}
		}
		
		
		try {
			setDataVector(data, columns);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Clearing content of this model.
	 */
	public void clear() {
		Vector<String> columns = createColumns();
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		
		this.metrics = null;
		
		try {
			setDataVector(data, columns);
		}
		catch (Throwable e) {
			LogUtil.trace(e);
		}
	}
	
	
	/**
	 * Creating column names.
	 * @return vector of column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Algorithm name");
		columns.add("Dataset Id");
		columns.add("Metric name");
		columns.add("Metric type");
		columns.add("Value");
		
		return columns;
	}
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


	/**
	 * Getting metrics.
	 * @return {@link Metrics}.
	 */
	public Metrics getMetrics() {
		return metrics;
	}
	
	
}



/**
 * This graphic user interface (GUI) is the table for showing remote information {@link RemoteInfo}.
 * It allows users to modify such remote information.
 * This table contains a built-in list of remote information specified by {@link RemoteInfoList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class RemoteInfoTable2 extends JTable {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor.
	 */
	public RemoteInfoTable2() {
		super(new RemoteInfoTM2());
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		this.setDefaultRenderer(HiddenText.class, new HiddenTextCellRenderer());
	}
	
	
	/**
	 * Getting the model of this table.
	 * @return {@link RemoteInfoTM2} of this table.
	 */
	public RemoteInfoTM2 getRemoteInfoTM() {
		return (RemoteInfoTM2)this.getModel();
		
	}
	
	
	/**
	 * Update this table by the specified list of remote information.
	 * @param rInfoList specified {@link RemoteInfoList}.
	 */
	public void update(RemoteInfoList rInfoList) {
		getRemoteInfoTM().update(rInfoList);
	}
	
	
	/**
	 * Getting the remote information at currently selected row.
	 * @return {@link RemoteInfo} at currently selected row.
	 */
	public RemoteInfo getSelectedRemoteInfo() {
		int row = getSelectedRow();
		if (row == -1)
			return null;
		
		return getRemoteInfoTM().getRemoteInfo(row);
	}


	/**
	 * Selecting the row showing the remote information having specified host and port.
	 * @param host specified host.
	 * @param port specified port.
	 */
	public void selectRemoteInfo(String host, int port) {
		int n = getRowCount();
		int selected = -1;
		for (int i = 0; i < n; i++) {
			String h = (String)getValueAt(i, 1);
			int p = (Integer)getValueAt(i, 2);
			
			if (h.compareToIgnoreCase(host) == 0 && p == port) {
				selected = i;
				break;
			}
		}
		
		if (selected != -1)
			this.getSelectionModel().addSelectionInterval(selected, selected);
	}
	
	
	@Override
	public TableCellRenderer getCellRenderer(int row, int column) {
		RemoteInfoTM2 model = getRemoteInfoTM();
		TableCellRenderer renderer = getDefaultRenderer(
				model.getColumnClass(row, column));
		if(renderer == null) 
			return super.getCellRenderer(row, column);
		
		return renderer;
	}
	
	
	/**
	 * The cell renderer for showing a value at each cell of {@link RemoteInfoTable2}.
	 * 
	 * @author Loc Nguyen
	 * @version 10.0
	 *
	 */
	private class HiddenTextCellRenderer extends DefaultTableCellRenderer.UIResource {

		
		/**
		 * Serial version UID for serializable class. 
		 */
		private static final long serialVersionUID = 1L;

		
		/**
		 * Default constructor.
		 */
		public HiddenTextCellRenderer() {
			super();
			// TODO Auto-generated constructor stub
		}
		
		
		@Override
        public void setValue(Object value) {
            if (value == null) {
                setText("");
            }
            else if (value instanceof HiddenText) {
                setText( ((HiddenText)value).getMask() );
            }
            else {
            	HiddenText hidden = new HiddenText(value.toString());
            	setText(hidden.getMask());
            }
        }
		
	}
	
	
}


/**
 * This is model of the table ({@link RemoteInfoTable2}) for showing remote information.
 * Note, {@link RemoteInfoTable2} allows users to modify such remote information.
 * This model contains the list of remote information specified by {@link RemoteInfoList}.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
@Deprecated
class RemoteInfoTM2 extends DefaultTableModel {

	
	/**
	 * Serial version UID for serializable class. 
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Default constructor.
	 */
	public RemoteInfoTM2() {
		super();
	}
	
	
	/**
	 * Creating names of columns of tables.
	 * @return vector of column names.
	 */
	private Vector<String> createColumns() {
		Vector<String> columns = Util.newVector();
		
		columns.add("No");
		columns.add("Host");
		columns.add("Port");
		columns.add("Account");
		columns.add("Password");
		
		return columns;
	}
	
	
	/**
	 * Update this model from the specified list of remote information.
	 * @param rInfoList specified {@link RemoteInfoList}.
	 */
	public void update(RemoteInfoList rInfoList) {
		Vector<String> columns = createColumns();
		
		Vector<Vector<Object>> data = new Vector<Vector<Object>>();
		for (int i = 0; i < rInfoList.size(); i++) {
			RemoteInfo rInfo = rInfoList.get(i);
			
			Vector<Object> row = Util.newVector();
			
			row.add(i + 1);
			row.add(rInfo.host);
			row.add(rInfo.port);
			row.add(rInfo.account);
			row.add(rInfo.password);
				
			data.add(row);
		}
		
		setDataVector(data, columns);
	}
	
	
	/**
	 * Getting the remote information at specified row.
	 * @param row specified row.
	 * @return {@link RemoteInfo} at specified row.
	 */
	public RemoteInfo getRemoteInfo(int row) {
		String host = (String) getValueAt(row, 1);
		int port = (Integer) getValueAt(row, 2);
		String account = (String) getValueAt(row, 3);
		HiddenText password = (HiddenText) getValueAt(row, 4);
		return new RemoteInfo(host, port, account, password);
		
	}
	
	
	/**
	 * Getting the remote information having specified host and port.
	 * @param host specified host.
	 * @param port specified port.
	 * @return {@link RemoteInfo} having specified host and port.
	 */
	public RemoteInfo getRemoteInfo(String host, int port) {
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = getRemoteInfo(i);
			if (rInfo.host.compareToIgnoreCase(host) == 0 && rInfo.port == port)
				return rInfo;
		}
		
		return null;
	}
	
	
	/**
	 * Getting the list of remote information of this model.
	 * @return {@link RemoteInfoList} of this model.
	 */
	public RemoteInfoList getRemoteInfoList() {
		RemoteInfoList rInfoList = new RemoteInfoList();
		int n = getRowCount();
		for (int i = 0; i < n; i++) {
			RemoteInfo rInfo = getRemoteInfo(i);
			rInfoList.add(rInfo);
		}
		
		return rInfoList;
	}
	
	
	
	/**
	 * Getting the class of value at specified row and column.
	 * @param row specified row.
	 * @param column specified column.
	 * @return class of value at specified row and column.
	 */
	public Class<?> getColumnClass(int row, int column) {
		Object value = getValueAt(row, column);
		
		return value.getClass();
	}
	
	
	
	@Override
	public boolean isCellEditable(int row, int column) {
		// TODO Auto-generated method stub
		return false;
	}


}



/**
 * This is GUI allowing users to import/register dynamically algorithms from jar files.
 * @author Loc Nguyen
 * @version 12.0
 */
@Deprecated
class JarImportAlgDlag extends JDialog {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Constructor with parent component.
	 * @param comp parent component.
	 */
	public JarImportAlgDlag(Component comp) {
		super(UIUtil.getFrameForComponent(comp), "Import algorithms from jar file", true);
		
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		
		JPanel body = new JPanel(new BorderLayout());
		add(body, BorderLayout.CENTER);

		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				onOk();
			}
		});
		
		JButton cancel = new JButton("Cancel");
		footer.add(cancel);
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});

		
		setVisible(true);
	}
	
	
	/**
	 * Event-driven method response to OK button command.
	 */
	protected void onOk() {
		dispose();
	}
	
	
}



/**
 * This is GUI allowing users to import/register dynamically and remotely algorithms from Hudup server/service.
 * @author Loc Nguyen
 * @version 12.0
 */
@Deprecated
class ImportAlgDlag extends JDialog {
	
	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Browsing button.
	 */
	protected JButton btnBrowse = null;
	
	
	/**
	 * Browsing text field.
	 */
	protected TagTextField txtBrowse = null;
	
	
	/**
	 * Connection button.
	 */
	protected JButton btnConnect = null;

	
	/**
	 * The left algorithm list box assists users to select algorithms.
	 */
	protected AlgListBox leftList = null;

	
	/**
	 * The right algorithm list box contains chosen algorithms.
	 */
	protected AlgListBox rightList = null;

	
	/**
	 * Result as list of chosen algorithms.
	 */
	protected AlgList result = new AlgList();

	
	/**
	 * If {@code true}, users press OK button to close this dialog.
	 */
	private boolean ok = false;

	
	/**
	 * Constructor with parent component.
	 * @param comp parent component.
	 */
	public ImportAlgDlag(Component comp) {
		super(UIUtil.getFrameForComponent(comp), "Import remotely algorithms from Hudup server/service", true);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		setSize(600, 400);
		setLocationRelativeTo(UIUtil.getFrameForComponent(comp));
		JPanel pane = null;
		
		setLayout(new BorderLayout());
		
		JPanel header = new JPanel(new BorderLayout());
		add(header, BorderLayout.NORTH);
		
		pane = new JPanel(new BorderLayout());
		header.add(pane, BorderLayout.NORTH);
		btnBrowse = new JButton("Browse");
		btnBrowse.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				browse();
			}
		});
		pane.add(btnBrowse, BorderLayout.WEST);
		txtBrowse = new TagTextField();
		txtBrowse.setEditable(false);
		pane.add(txtBrowse, BorderLayout.CENTER);
		
		pane = new JPanel();
		header.add(pane, BorderLayout.SOUTH);
		btnConnect = new JButton("Load");
		btnConnect.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					connect();
				} catch (Throwable ex) {ex.printStackTrace();}
			}
		});
		pane.add(btnConnect);

		
		JPanel body = new JPanel(new GridLayout(1, 0));
		add(body, BorderLayout.CENTER);
		
		JPanel left = new JPanel(new BorderLayout());
		body.add(left);
		
		left.add(new JLabel("Available algorithm list"), BorderLayout.NORTH);
		leftList = new AlgListBox(true);
		//leftList.update(remainList);
		leftList.setEnableDoubleClick(false);
		left.add(new JScrollPane(leftList), BorderLayout.CENTER);

		JPanel buttons = new JPanel();
		buttons.setLayout(new GridLayout(0, 1));
		left.add(buttons, BorderLayout.EAST);
		
		JButton leftToRight = new JButton("> ");
		leftToRight.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				leftToRight();
			}
		});
		pane = new JPanel();
		pane.add(leftToRight);
		buttons.add(pane);
		
		JButton leftToRightAll = new JButton(">>");
		leftToRightAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				leftToRightAll();
			}
		});
		pane = new JPanel();
		pane.add(leftToRightAll);
		buttons.add(pane);
		
		JButton rightToLeft = new JButton("< ");
		rightToLeft.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				rightToLeft();
			}
		});
		pane = new JPanel();
		pane.add(rightToLeft);
		buttons.add(pane);
		
		JButton rightToLeftAll = new JButton("<<");
		rightToLeftAll.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				rightToLeftAll();
			}
		});
		pane = new JPanel();
		pane.add(rightToLeftAll);
		buttons.add(pane);

		JPanel right = new JPanel(new BorderLayout());
		body.add(right);
		
		right.add(new JLabel("Selected algorithm list"), BorderLayout.NORTH);
		
		rightList = new AlgListBox(true);
		rightList.setEnableDoubleClick(false);
		//rightList.update(selectedList);
		right.add(new JScrollPane(rightList), BorderLayout.CENTER);
		
		
		JPanel footer = new JPanel();
		add(footer, BorderLayout.SOUTH);

		JButton ok = new JButton("OK");
		footer.add(ok);
		ok.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				ok();
			}
		});
		
		JButton cancel = new JButton("Cancel");
		footer.add(cancel);
		cancel.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				dispose();
			}
		});

	}

	
	/**
	 * Event-driven method for browsing button to browse place to store algorithms.
	 */
	protected void browse() {
		List<Alg> parserList = Util.newList();
		RmiServerIndicator rmiIndicator = new RmiServerIndicator();
		parserList.add(rmiIndicator);
		SocketServerIndicator socketIndicator = new SocketServerIndicator();
		parserList.add(socketIndicator);
		SnapshotParserImpl snapshotParser = new SnapshotParserImpl(); 
		parserList.add(snapshotParser);
		
		DataDriverList dataDriverList = new DataDriverList();
		dataDriverList.add(new DataDriver(DataType.file));
		dataDriverList.add(new DataDriver(DataType.hudup_rmi));
		dataDriverList.add(new DataDriver(DataType.hudup_socket));
		
		DataConfig defaultConfig = (DataConfig)txtBrowse.getTag();
		if (defaultConfig == null) {
			defaultConfig = new DataConfig();
			defaultConfig.setParser(rmiIndicator);
		}
		DatasetConfigurator configurator = new DatasetConfigurator(this, parserList, dataDriverList, defaultConfig);
		DataConfig config = configurator.getResultedConfig();
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Configuration was not established", 
				"Configuration was not established", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		txtBrowse.setText(config.getStoreUri().toString(), config);
	}
	
	
	/**
	 * Event-driven method for connecting button to connect place to store algorithms.
	 * @throws RemoteException if any error raises.
	 */
	protected void connect() {
		DataConfig config = (DataConfig)txtBrowse.getTag();
		if (config == null) {
			JOptionPane.showMessageDialog(
				this, 
				"Configuration was not established", 
				"Configuration was not established", 
				JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		DatasetParser parser = config.getParser();
		List<Alg> availableAlgList = Util.newList();
		if (parser instanceof RmiServerIndicator)
			loadClassesFromRmiServer(config, availableAlgList);
		else if (parser instanceof SocketServerIndicator)
			loadClassesFromSocketServer(config, availableAlgList);
		else
			loadClassesFromStore(config.getStoreUri(), availableAlgList);
		
		if (availableAlgList.size() == 0) {
			JOptionPane.showMessageDialog(
					this, 
					"Empty algorithm list", 
					"Empty algorithm list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		leftList.update(availableAlgList);
		rightList.clear();
	}
	
	
	/**
	 * Loading classes from RMI server specified by configuration.
	 * @param config server configuration.
	 * @param outAlgList list of algorithms as output.
	 */
	private void loadClassesFromRmiServer(DataConfig config, List<Alg> outAlgList) {
		xURI storeUri = config.getStoreUri();
		if (storeUri == null) return;
		Service service = ClientUtil.getRemoteService(storeUri.getHost(), storeUri.getPort(), config.getStoreAccount(), config.getStorePassword().getText());
		if (service == null) return;
		
		String[] algNames = new String[0];
		try {
			algNames = service.getAlgNames();
		}
		catch (Throwable e) {
			LogUtil.error("Retrieving remote algorithm names error by: " + e.getMessage());
		}
		if (algNames == null) algNames = new String[0];
		
		RegisterTable normalReg = PluginStorage.getNormalAlgReg();
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (String algName : algNames) {
			if (normalReg.contains(algName)) continue;
			
			Alg alg = null;
			try {
				alg = service.getAlg(algName);
			}
			catch (Throwable e) {
				LogUtil.error("Retrieving remote algorithm error by: " + e.getMessage());
				alg = null;
			}
			if (alg == null) continue;
			
			int idx = nextUpdateList.indexOf(algName);
			if (idx < 0)
				outAlgList.add(alg);
			else {
				Alg nextUpdateAlg = nextUpdateList.get(idx);
				if (PluginStorage.lookupTableName(nextUpdateAlg.getClass()) != PluginStorage.lookupTableName(alg.getClass()))
					outAlgList.add(alg);
				else if (alg instanceof AlgRemoteWrapper) {
					((AlgRemoteWrapper)alg).setExclusive(true);
					try {
						((AlgRemoteWrapper)alg).unexport();
					} catch (Throwable e) {LogUtil.trace(e);}
				}
			}
		}
	}
	
	
	/**
	 * Loading classes from socket server specified by configuration.
	 * @param config server configuration.
	 * @param outAlgList list of algorithms as output.
	 */
	private void loadClassesFromSocketServer(DataConfig config, List<Alg> outAlgList) {
		xURI storeUri = config.getStoreUri();
		if (storeUri == null) return;
		
		SocketConnection service = null;
		String[] algNames = new String[0];
		try {
			service = ClientUtil.getSocketConnection(storeUri.getHost(), storeUri.getPort(),config.getStoreAccount(), config.getStorePassword().getText());
			algNames = service.getAlgNames();
			service.close(); service = null;
		}
		catch (Throwable e) {
			LogUtil.error("Retrieving remote algorithm names error by: " + e.getMessage());
		}
		if (algNames == null) algNames = new String[0];
		
		RegisterTable normalReg = PluginStorage.getNormalAlgReg();
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (String algName : algNames) {
			if (normalReg.contains(algName)) continue;
			
			Alg alg = null;
			try {
				service = ClientUtil.getSocketConnection(storeUri.getHost(), storeUri.getPort(),config.getStoreAccount(), config.getStorePassword().getText());
				alg = service.getAlg(algName);
				service.close(); service = null;
			}
			catch (Throwable e) {
				LogUtil.error("Retrieving remote algorithm error by: " + e.getMessage());
				alg = null;
			}
			if (alg == null) continue;
			
			int idx = nextUpdateList.indexOf(algName);
			if (idx < 0)
				outAlgList.add(alg);
			else {
				Alg nextUpdateAlg = nextUpdateList.get(idx);
				if (PluginStorage.lookupTableName(nextUpdateAlg.getClass()) != PluginStorage.lookupTableName(alg.getClass()))
					outAlgList.add(alg);
				else if (alg instanceof AlgRemoteWrapper) {
					((AlgRemoteWrapper)alg).setExclusive(true);
					try {
						((AlgRemoteWrapper)alg).unexport();
					} catch (Throwable e) {LogUtil.trace(e);}
				}
			}
		}
		
		if (service != null) service.close();
	}

	
	/**
	 * Loading classes from store.
	 * @param storeUri store URI.
	 * @param outAlgList list of algorithms as output.
	 */
	private void loadClassesFromStore(xURI storeUri, List<Alg> outAlgList) {
		if (storeUri == null) return;

		List<Alg> algList = Util.getPluginManager().loadInstances(storeUri, Alg.class);
		RegisterTable normalReg = PluginStorage.getNormalAlgReg();
		AlgList nextUpdateList = PluginStorage.getNextUpdateList();
		for (Alg alg : algList) {
			if (normalReg.contains(alg.getName())) continue;
			
			int idx = nextUpdateList.indexOf(alg.getName());
			if (idx < 0)
				outAlgList.add(alg);
			else {
				Alg nextUpdateAlg = nextUpdateList.get(idx);
				if (PluginStorage.lookupTableName(nextUpdateAlg.getClass()) != PluginStorage.lookupTableName(alg.getClass()))
					outAlgList.add(alg);
			}
		}
		
	}
	
	
	/**
	 * Transferring selected algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 */
	protected void leftToRight() {
		List<Alg> list = leftList.removeSelectedList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithm not selected or empty list", 
					"Algorithm not selected or empty list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		rightList.addAll(list);
		
	}
	

	/**
	 * Transferring all algorithms from the left {@link AlgListBox} to the right {@link AlgListBox}.
	 */
	protected void leftToRightAll() {
		List<Alg> list = leftList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		
		rightList.addAll(list);
		leftList.clear();
	}

	
	/**
	 * Transferring selected algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 */
	protected void rightToLeft() {
		List<Alg> list = rightList.removeSelectedList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"Algorithm not selected or empty list", 
					"Algorithm not selected or empty list", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}
		leftList.addAll(list);
	}
	

	/**
	 * Transferring all algorithms from the right {@link AlgListBox} to the left {@link AlgListBox}.
	 */
	protected void rightToLeftAll() {
		List<Alg> list = rightList.getAlgList();
		if (list.isEmpty()) {
			JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.WARNING_MESSAGE);
			return;
		}

		leftList.addAll(list);
		rightList.clear();
	}

	
	/**
	 * Event-driven method response to OK button command.
	 */
	protected void ok() {
		List<Alg> selectedAlgList = this.rightList.getAlgList();
		if (selectedAlgList.size() == 0) {
			if (leftList.getAlgList().size() > 0) {
				JOptionPane.showMessageDialog(
					this, 
					"List empty", 
					"List empty", 
					JOptionPane.ERROR_MESSAGE);
			}
			
			dispose();
			return;
		}
		
		for (Alg selectedAlg : selectedAlgList) {
			if (selectedAlg instanceof AlgRemoteWrapper)
				((AlgRemoteWrapper)selectedAlg).setExclusive(true);
			
			if (selectedAlg != null)
				PluginStorage.getNextUpdateList().add(selectedAlg);
		}
		this.result = PluginStorage.getNextUpdateList();
		this.rightList.clear();
		
		List<Alg> remainAlgs = this.leftList.getAlgList();
		for (Alg remainAlg : remainAlgs) {
			if (remainAlg instanceof AlgRemoteWrapper) {
				((AlgRemoteWrapper)remainAlg).setExclusive(true);
				try {
					((AlgRemoteWrapper)remainAlg).unexport();
				} catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		this.leftList.clear();
		
		ok = true;
		dispose();
	}
	
	
	/**
	 * Getting the result as list of chosen algorithms.
	 * @return result as list of chosen algorithms.
	 */
	public AlgList getResult() {
		return result;
	}
	
	
	/**
	 * Checking whether or not the OK button is pressed.
	 * @return whether or not the OK button is pressed.
	 */
	public boolean isOK() {
		return ok;
	}


	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		List<Alg> algList = this.leftList.getAlgList();
		algList.addAll(this.rightList.getAlgList());
		for (Alg alg : algList) {
			if (alg instanceof AlgRemoteWrapper) {
				((AlgRemoteWrapper)alg).setExclusive(true);
				try {
					((AlgRemoteWrapper)alg).unexport();
				} catch (Throwable e) {LogUtil.trace(e);}
			}
		}
		
		this.leftList.clear();
		this.rightList.clear();
		
		super.dispose();
	}

	
}



