/**
 * HUDUP: A FRAMEWORK OF E-COMMERCIAL RECOMMENDATION ALGORITHMS
 * (C) Copyright by Loc Nguyen's Academic Network
 * Project homepage: hudup.locnguyen.net
 * Email: ng_phloc@yahoo.com
 * Phone: +84-975250362
 */
package net.hudup.core.data;

import java.awt.Component;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.swing.JOptionPane;

import net.hudup.core.PluginStorage;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.alg.AlgDesc2;
import net.hudup.core.alg.AlgDesc2.MethodType;
import net.hudup.core.alg.SocketAlg;
import net.hudup.core.data.Attribute.Type;
import net.hudup.core.data.DataDriver.DataType;
import net.hudup.core.data.ui.DatasetConfigurator;
import net.hudup.core.logistic.LogUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.parser.DatasetParser;
import net.hudup.core.parser.DbServerIndicator;
import net.hudup.core.parser.FlatServerIndicator;
import net.hudup.core.parser.Indicator;
import net.hudup.core.parser.KBaseIndicator;
import net.hudup.core.parser.RmiServerIndicator;
import net.hudup.core.parser.ScannerParser;
import net.hudup.core.parser.SnapshotParser;
import net.hudup.core.parser.SnapshotParserImpl;
import net.hudup.core.parser.SocketServerIndicator;
import net.hudup.core.parser.TextParserUtil;

/**
 * This is the second utility class for processing dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class DatasetUtil2 {
	
	
	/**
	 * Creating default data configuration from specified algorithm.
	 * @param defaultAlg specified algorithm.
	 * @return {@link DataConfig} from specified algorithm.
	 */
	public static DataConfig createDefaultConfig(Alg defaultAlg) {
		if (defaultAlg == null)
			return null;
		
		DatasetParser parser = null;
		MethodType defaultAlgType = AlgDesc2.methodTypeOf(defaultAlg);
		if (defaultAlgType == MethodType.modelbased)
			parser = (DatasetParser) PluginStorage.getParserReg().query(new KBaseIndicator().getName());
		else if (defaultAlgType == MethodType.service) {
			if (defaultAlg instanceof SocketAlg)
				parser = (DatasetParser) PluginStorage.getParserReg().query(new SocketServerIndicator().getName());
			else
				parser = (DatasetParser) PluginStorage.getParserReg().query(new RmiServerIndicator().getName());
		}
		else
			parser = (DatasetParser) PluginStorage.getParserReg().query(new SnapshotParserImpl().getName());
		
		if (parser == null)
			return null;
		
		DataDriver dataDriver = null;
		if (parser instanceof KBaseIndicator)
			dataDriver = new DataDriver(DataType.file);
		else if (parser instanceof SocketServerIndicator)
			dataDriver = new DataDriver(DataType.hudup_socket);
		else if (parser instanceof RmiServerIndicator)
			dataDriver = new DataDriver(DataType.hudup_rmi);
		else
			dataDriver = new DataDriver(DataType.file);

		DataConfig config = (DataConfig) defaultAlg.getConfig().clone();
		config.setParser(parser.getName());
		config.setDataDriverName(dataDriver.getName());
		return config;
	}

	
	/**
	 * Opening choosing dialog for constructing a data configuration from specified list of algorithms and data driver list.
	 * @param comp parent component.
	 * @param parserList specified list of algorithms.
	 * @param dataDriverList specified data driver list.
	 * @param defaultConfig default configuration.
	 * @return {@link DataConfig}.
	 */
	public static DataConfig chooseConfig(
			Component comp, 
			List<Alg> parserList,
			DataDriverList dataDriverList,
			DataConfig defaultConfig) {
		
		DatasetConfigurator configurator = new DatasetConfigurator(
				comp, 
				parserList, 
				dataDriverList,
				defaultConfig);
		
		return configurator.getResultedConfig();
	}

	
	/**
	 * Opening choosing dialog for constructing a data configuration from initial configuration.
	 * @param comp parent component.
	 * @param initConfig initial configuration.
	 * @return {@link DataConfig} from initial configuration.
	 */
	public static DataConfig chooseConfig(Component comp, DataConfig initConfig) {
		DataConfig defaultConfig = initConfig == null ? new DataConfig() : (DataConfig) initConfig.clone();
		
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		
		try {
			String dataDriverName = defaultConfig.getDataDriverName();
			if (dataDriverName == null) {
				xURI store = defaultConfig.getStoreUri();
				if (store != null) {
					DataDriver dataDriver = DataDriver.create(store);
					if (dataDriver != null)
						dataDriverName = dataDriver.getName(); 
				}
			}
			
			DataDriver dataDriver = null;
			if (dataDriverName != null) {
				defaultConfig.setDataDriverName(dataDriverName);
				dataDriver = DataDriver.createByName(dataDriverName);
			}
			
			DatasetParser parser = defaultConfig.getParser();
			if (parser == null && dataDriver != null) {
				if (dataDriver.isFlatServer())
					defaultConfig.setParser(new FlatServerIndicator());
				else if (dataDriver.isDbServer())
					defaultConfig.setParser(new DbServerIndicator());
				else if (dataDriver.isHudupServer())
					defaultConfig.setParser(new RmiServerIndicator());
			}
			
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return chooseConfig(
				comp, parserList, DataDriverList.get(), defaultConfig);
	}

	
	/**
	 * Opening choosing dialog for constructing a data configuration of training dataset from default configuration and algorithm.
	 * @param comp parent component.
	 * @param defaultConfig default configuration.
	 * @param alg specified algorithm.
	 * @return {@link DataConfig} of training dataset from default configuration and algorithm.
	 */
	public static DataConfig chooseTrainingConfig(Component comp, DataConfig defaultConfig, Alg alg) {
		if (alg == null)
			return DatasetUtil2.chooseConfig(comp, defaultConfig);
		
		MethodType algType = AlgDesc2.methodTypeOf(alg);
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		List<Alg> newParserList = Util.newList();
		for (Alg parser : parserList) {
			if (algType == MethodType.modelbased) {
				if (parser instanceof KBaseIndicator || 
						parser instanceof SnapshotParser ||
						parser instanceof ScannerParser)
					newParserList.add(parser);
			}
			else if (algType == MethodType.composite) {
				newParserList.add(parser);
			}
			else if (algType == MethodType.service) {
				if (parser instanceof Indicator && !(parser instanceof KBaseIndicator))
					newParserList.add(parser);
			}
			else
				newParserList.add(parser);
		}
		
		return chooseConfig(comp, newParserList, DataDriverList.get(), defaultConfig);
	}

	
	/**
	 * Opening choosing dialog for constructing a data configuration of testing dataset from default configuration.
	 * @param comp parent component.
	 * @param defaultConfig default configuration.
	 * @return {@link DataConfig} of testing dataset from default configuration.
	 */
	public static DataConfig chooseTestingConfig(Component comp, DataConfig defaultConfig) {
		
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		List<Alg> newParserList = Util.newList();
		for (Alg parser : parserList) {
			if (parser instanceof SnapshotParser || parser instanceof ScannerParser)
				newParserList.add(parser);
		}
		
		return chooseConfig(comp, newParserList, DataDriverList.get(), defaultConfig);
	}

	
	/**
	 * Opening choosing dialog for constructing a data configuration of whole dataset from default configuration.
	 * @param comp parent component.
	 * @param defaultConfig default configuration.
	 * @return {@link DataConfig} of whole dataset from default configuration.
	 */
	public static DataConfig chooseWholeConfig(Component comp, DataConfig defaultConfig) {
		
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		List<Alg> newParserList = Util.newList();
		for (Alg parser : parserList) {
			if (parser instanceof SnapshotParser || parser instanceof ScannerParser)
				newParserList.add(parser);
		}
		
		return chooseConfig(comp, newParserList, DataDriverList.get(), defaultConfig);
	}

	
	/**
	 * Opening choosing dialog for constructing a data configuration of server from initial configuration.
	 * @param comp parent component.
	 * @param initConfig initial configuration.
	 * @return {@link DataConfig} of server from initial configuration.
	 */
	public static DataConfig chooseServerConfig(Component comp, DataConfig initConfig) {
		DataConfig defaultConfig = initConfig == null ? new DataConfig() : (DataConfig) initConfig.clone();
		
		List<Alg> parserList = ((RegisterTable) PluginStorage.getParserReg().clone()).getAlgList();
		List<Alg> newParserList = Util.newList();
		for (Alg parser : parserList) {
			if (parser instanceof Indicator)
				newParserList.add(parser);
		}
		
		try {
			String dataDriverName = null;
			xURI store = defaultConfig.getStoreUri();
			if (store != null) {
				DataDriver dataDriver = DataDriver.create(store);
				if (dataDriver != null)
					dataDriverName = dataDriver.getName(); 
			}
			
			if (dataDriverName == null)
				return chooseConfig(
						comp, newParserList, DataDriverList.get(), defaultConfig);
				
			defaultConfig.setDataDriverName(dataDriverName);
			DataDriver dataDriver = DataDriver.createByName(dataDriverName);
			
			if (dataDriver.isFlatServer())
				defaultConfig.setParser(new FlatServerIndicator());
			else if (dataDriver.isDbServer())
				defaultConfig.setParser(new DbServerIndicator());
			else if (dataDriver.isHudupServer())
				defaultConfig.setParser(new RmiServerIndicator());
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		return chooseConfig(
				comp, newParserList, DataDriverList.get(), defaultConfig);
	}

	
	/**
	 * Loading properties of configuration of file system (flat system).
	 * @param config configuration of file system (flat system).
	 * @return {@link Properties}.
	 */
	public static Properties loadFlatConfig(DataConfig config) {
		Properties props = new Properties();
		
		UriAdapter adapter = new UriAdapter(config);
		try {
			xURI configUri = config.getStoreUri().concat(config.getConfigUnit());
			
			Reader reader = adapter.getReader(configUri);
			
			props.load(reader);
		} 
		catch (Throwable e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		adapter.close();
		
		return props;
	}
	
	
	/**
	 * Validating the specified training dataset conforms with list of algorithms.
	 * @param comp parent component.
	 * @param training training dataset.
	 * @param algList specified list of algorithms.
	 * @return whether training dataset conforms with algorithms.
	 */
	public static boolean validateTrainingset(Component comp, Dataset training, Alg[] algList) {
		if (training == null)
			return false;
		if (algList == null || algList.length == 0)
			return true;
		
		for (Alg alg : algList) {
			if (alg == null)
				return false;
		}
		
		if (training instanceof Pointer) {
			
			if (training instanceof KBasePointer) {
				boolean flag = false;
				
				for (Alg alg : algList) {
					MethodType algType = AlgDesc2.methodTypeOf(alg);
					if (algType == MethodType.modelbased || algType == MethodType.composite) {
						flag = true;
						break;
					}
				}
				
				if (!flag) {
					JOptionPane.showMessageDialog(
							comp, 
							"Training dataset is KBasePointer but there is not model based recommender nor composite recomender", 
							"Invalid training dataset", 
							JOptionPane.ERROR_MESSAGE);
					
					return false;
				}
			}
			else {
				boolean flag = false;
				
				for (Alg alg : algList) {
					MethodType algType = AlgDesc2.methodTypeOf(alg);
					if (algType == MethodType.service || algType == MethodType.composite) {
						flag = true;
						break;
					}
				}
				
				if (!flag) {
					JOptionPane.showMessageDialog(
							comp, 
							"Training dataset is Pointer but there is not service recommender nor composite recommender", 
							"Invalid training dataset", 
							JOptionPane.ERROR_MESSAGE);
					
					return false;
				}
				
			} // if (training instance of KBasePointer)
			
			
		} // if (training instance of Pointer)
		else {
			
			boolean flag = false;
			
			for (Alg alg : algList) {
				MethodType algType = AlgDesc2.methodTypeOf(alg);
				if (algType != MethodType.service) {
					flag = true;
					break;
				}
			}
			
			if (!flag) {
				JOptionPane.showMessageDialog(
						comp, 
						"Training dataset is normal dataset but there are all service recommenders", 
						"Invalid training dataset", 
						JOptionPane.ERROR_MESSAGE);
				
				return false;
			}

		}
		
		return true;
		
	}

	
	/**
	 * Loading nominal attributes from reader.
	 * @param reader specified reader.
	 * @param filterUnit filtered unit which is unit to be read.
	 * @return map of nominal {@link Attribute}.
	 */
	public static Map<String, Attribute> loadNominalAttributes(Reader reader, String filterUnit) {
		Map<String, List<ObjectPair<Nominal>>> map = Util.newMap();
		
		BufferedReader buffer = new BufferedReader(reader);
		String line = null;
		try {
			while ( (line = buffer.readLine()) != null ) {
				List<String> parts = TextParserUtil.split(line, TextParserUtil.NOSPACE_DEFAULT_SEP, null);
				if (parts.size() < 4)
					continue;
				
				String unit = parts.get(0);
				if (!unit.equals(filterUnit))
					continue;
				
				try {
					String att = parts.get(1);
					int nominalIndex = Integer.parseInt(parts.get(2));
					String nominalValue = parts.get(3);
					int parentIndex = -1;
					if (parts.size() > 4)
						parentIndex = Integer.parseInt(parts.get(4));
					
					List<ObjectPair<Nominal>> list = map.get(att);
					if (list == null) {
						list = Util.newList();
						map.put(att, list);
					}
					
					Nominal nominal = new Nominal(nominalValue, nominalIndex, parentIndex);
					ObjectPair<Nominal> pair = new ObjectPair<Nominal>(nominal, nominalIndex);
					list.add(pair);
				}
				catch (Exception e) {
					LogUtil.trace(e);
				}
			}
		}
		catch (Exception e) {
			LogUtil.trace(e);
		}
		
		Set<String> attNames = map.keySet();
		Map<String, Attribute> attributeMap = Util.newMap();
		for (String attName : attNames) {
			List<ObjectPair<Nominal>> list = map.get(attName);
			ObjectPair.sort(list, false);
			
			List<Nominal> nominals = ObjectPair.getKeyList(list);
			Attribute att = new Attribute(attName, new NominalList(nominals));
			attributeMap.put(attName, att);
		}
		
		
		return attributeMap;
	}
	
	
	/**
	 * Loading nominal attributes from specified URI.
	 * @param adapter specified URI.
	 * @param uri URI adapter.
	 * @param filterUnit filtered unit which is unit to be read.
	 * @return map of {@link Attribute} from specified URI.
	 */
	public static Map<String, Attribute> loadNominalAttributes(UriAdapter adapter, xURI uri, String filterUnit) {
		Reader reader = adapter.getReader(uri);
		
		if (reader == null)
			return Util.newMap();
		
		Map<String, Attribute> attributeMap = loadNominalAttributes(reader, filterUnit);
		
		try {
			reader.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
		return attributeMap;
	}
	
	
	/**
	 * Saving nominal attributes by writer.
	 * @param nominalAttributes collection of nominal attributes.
	 * @param writer specified writer.
	 * @param filterUnit filtered unit.
	 */
	public static void saveNominalAttributes(
			Collection<Attribute> nominalAttributes, 
			Writer writer,
			String filterUnit) {
		
		BufferedWriter buffer = new BufferedWriter(writer);
		for (Attribute att : nominalAttributes) {
			if (att.getType() != Type.nominal)
				continue;
			
			try {
				String attName = att.getName();
				NominalList nominals = att.getNominalList();
				
				for (int i = 0; i < nominals.size(); i++) {
					Nominal nominal = nominals.get(i);
					String line = 
							filterUnit + ", " + 
							attName + ", " + 
							nominal.getIndex() + ", " + 
							nominal.getValue() + ", " + 
							nominals.get(i) + "\n";
					writer.write(line);
				}
			}
			catch (Exception e) {
				LogUtil.trace(e);
			}
		}
		
		try {
			buffer.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
	}

	
	/**
	 * Saving nominal attributes to resource specified by URI.
	 * @param nominalAttributes collection of nominal attributes.
	 * @param adapter URI adapter.
	 * @param uri URI pointing to resource.
	 * @param filterUnit filtered unit.
	 */
	public static void saveNominalAttributes(
			Collection<Attribute> nominalAttributes,
			UriAdapter adapter,
			xURI uri,
			String filterUnit) {
		
		Writer writer = adapter.getWriter(uri, false);
		if (writer == null)
			return;
		saveNominalAttributes(nominalAttributes, writer, filterUnit);
		
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.trace(e);
		}
		
	}
	
	
	/**
	 * Converting real rating values to integer rating values based on zero.
	 * @param realValues real rating values.
	 * @param minRating minimum rating value.
	 * @return zero-based integer array.
	 */
	public static int[] zeroBasedRatingValueOf(double[] realValues, double minRating) {
		int[] inner = new int[realValues.length];
		for (int i = 0; i < realValues.length; i++) {
			inner[i] = zeroBasedRatingValueOf(realValues[i], minRating);
		}
		
		return inner;
	}

	
	/**
	 * Converting real rating value to integer rating value based on zero.
	 * @param realValue real rating value.
	 * @param minRating minimum rating value.
	 * @return zero-based value
	 */
	public static int zeroBasedRatingValueOf(double realValue, double minRating) {
		return (int) (realValue - minRating);
	}

	
	/**
	 * Converting zero-based value to real value with minimum rating value.
	 * @param zeroBasedValue zero-based value.
	 * @param minRating minimum rating value.
	 * @return real rating value.
	 */
	public static double realRatingValueOf(int zeroBasedValue, double minRating) {
		return zeroBasedValue + minRating;
	}

	
}
