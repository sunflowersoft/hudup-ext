package net.hudup.core;

import java.io.Serializable;

import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgList;
import net.hudup.core.data.ExternalQuery;
import net.hudup.core.data.ctx.CTSManager;
import net.hudup.core.evaluate.Metric;
import net.hudup.core.parser.DatasetParser;

/**
 * This class represents wrapper of system plug-in storage.
 * 
 * @author Loc Nguyen
 * @version 1.0
 *
 */
public final class PluginStorageWrapper implements Serializable {

	
	/**
	 * Default serial version UID.
	 */
	private static final long serialVersionUID = 1L;

	
	/**
	 * Normal algorithm register. Note, this is public and static variable.
	 */
	protected RegisterTable normalAlgReg = new RegisterTable();

	
	/**
	 * {@link DatasetParser} register. Note, this is public and static variable.
	 */
	protected RegisterTable parserReg = new RegisterTable();

	
	/**
	 * {@link Metric} register. Note, this is public and static variable.
	 */
	protected RegisterTable metricReg = new RegisterTable();

	
	/**
	 * {@link ExternalQuery} register. Note, this is public and static variable.
	 */
	protected RegisterTable externalQueryReg = new RegisterTable();

	
	/**
	 * {@link CTSManager} register. Note, this is public and static variable.
	 */
	protected RegisterTable ctsmReg = new RegisterTable();

	
	/**
	 * List of next-update algorithms. Next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * Currently, next-update algorithms are not used. Note, this is public and static variable.
	 */
	protected AlgList nextUpdateList = new AlgList();
	
	
	/**
	 * Default constructor.
	 */
	public PluginStorageWrapper() {
		// TODO Auto-generated constructor stub
		assignFromSystem();
	}

	
	/**
	 * Assigning from system plug-in storage: this = PluginStorage.
	 */
	private void assignFromSystem() {
		this.normalAlgReg = PluginStorage.getNormalAlgReg();
		this.parserReg = PluginStorage.getParserReg();
		this.metricReg = PluginStorage.getMetricReg();
		this.externalQueryReg = PluginStorage.getExternalQueryReg();
		this.ctsmReg = PluginStorage.getCTSManagerReg();
		this.nextUpdateList = PluginStorage.getNextUpdateList();
	}
	
	
	/**
	 * Assigning to system plug-in storage: this = PluginStorage. Using this method carefully.
	 */
	public void assignToSystem() {
		PluginStorage.releaseAllRegisteredAlgs();
		
		PluginStorage.normalAlgReg = this.normalAlgReg;
		PluginStorage.parserReg = this.parserReg;
		PluginStorage.metricReg = this.metricReg;
		PluginStorage.externalQueryReg = this.externalQueryReg;
		PluginStorage.ctsmReg = this.ctsmReg;
		PluginStorage.nextUpdateList = this.nextUpdateList;
	}

	
	/**
	 * This static method gets normal algorithm register.
	 * @return register table of {@link Alg}.
	 */
	public RegisterTable getNormalAlgReg() {
		return normalAlgReg;
	}
	
	
	/**
	 * This static method gets {@link DatasetParser} register.
	 * @return register table of {@link DatasetParser}.
	 */
	public RegisterTable getParserReg() {
		return parserReg;
	}

	
	/**
	 * This static method gets {@link Metric} register.
	 * @return {@link Metric} register.
	 */
	public RegisterTable getMetricReg() {
		return metricReg;
	}

	
	/**
	 * This static method gets {@link ExternalQuery} register.
	 * @return {@link ExternalQuery} register.
	 */
	public RegisterTable getExternalQueryReg() {
		return externalQueryReg;
	}

	
	/**
	 * This static method gets {@link CTSManager} register.
	 * @return {@link CTSManager} register.
	 */
	public RegisterTable getCTSManagerReg() {
		return ctsmReg;
	}

	
	/**
	 * This static method gets list of next-update algorithms.
	 * Note, next-update algorithm is the one which needs to be updated in next version of Hudup framework because it is not perfect, for example.
	 * Currently, next-update algorithms are not used.
	 * @return list of next-update algorithms.
	 */
	public AlgList getNextUpdateList() {
		return nextUpdateList;
	}


}
