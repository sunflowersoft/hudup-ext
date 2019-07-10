package net.hudup.core.evaluate;

import java.awt.Component;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.table.DefaultTableModel;

import flanagan.plot.PlotGraph;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCell;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.hudup.core.Constants;
import net.hudup.core.RegisterTable;
import net.hudup.core.Util;
import net.hudup.core.alg.Alg;
import net.hudup.core.data.PropList;
import net.hudup.core.logistic.MathUtil;
import net.hudup.core.logistic.SystemUtil;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;


/**
 * Utility class for processing metrics, for example, exporting metrics into text file, excel file.
 * 
 * @author Loc Nguyen
 * @version 10.0
 *
 */
public class MetricsUtil {
	
	
	/**
	 * List of metrics.
	 */
	protected Metrics metrics = null;
	
	
	/**
	 * Algorithm table.
	 */
	protected RegisterTable algTable = new RegisterTable();
	
	
	/**
	 * Constructor with metrics and algorithm table.
	 * 
	 * @param metrics specified metrics.
	 * @param algTable specified algorithm table.
	 */
	public MetricsUtil(Metrics metrics, RegisterTable algTable) {
		this.metrics = metrics;
		this.algTable = algTable == null ? new RegisterTable() : algTable;
	}
	
	
	/**
	 * Parsing metrics into a map of map of metrics with specified dataset. The keys of outermost map is algorithm names.
	 * The key of inner map is metric names.
	 * @param datasetId specified dataset identifier.
	 * @return map of map of metric values. The keys of outermost map is algorithm names. The key of inner map is metric names.
	 */
	private Map<String, Map<String, MetricValue>> parseMetrics(int datasetId) {
		
		Map<String, Map<String, MetricValue>> values = Util.newMap();
		
		List<String> algNameList = this.metrics.getAlgNameList();
		for (String algName : algNameList) {
			Metrics metrics = (datasetId < 0) ? 
				 this.metrics.mean(algName) : this.metrics.gets(algName, datasetId);
				
			Map<String, MetricValue> algValues = values.get(algName);
			if (algValues == null) {
				algValues = Util.newMap();
				values.put(algName, algValues);
			}
			
			for (int i = 0; i < metrics.size(); i++) {
				Metric metric = metrics.get(i);
				
				if (!metric.isValid())
					continue;
				
				MetricValue metricValue = metric.getAccumValue();
				if (metricValue != null)
					algValues.put(metric.getName(), metricValue);
			} // end for i
			
		} // end for algorithm name
		
		
		return values;
	}
	
	
	/**
	 * Creating plot graph to show metrics.
	 * @param metricName plot graph to show metrics.
	 * @return {@link PlotGraph} to show metrics.
	 */
	public PlotGraph createPlotGraph(String metricName) {
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		if (algNameList.size() == 0 || datasetIdList.size() == 0)
			return null;
		
		int curves = datasetIdList.size();
		int points = algNameList.size();
		int n = Math.max(points, 3);
		double[][] data = PlotGraph.data(curves, n);
		
		for (int i = 0; i < curves; i ++) {
			int datasetId = datasetIdList.get(i);
			
			int curveIdx = 2*i;
			for (int j = 0; j < points; j++) {
				Metric metric = this.metrics.get(metricName, algNameList.get(j), datasetId);
				data[curveIdx][j] = j;
				
				double value = 0;
				if (metric != null && metric.isValid() && metric.getAccumValue().isUsed()) {
					double metricValue = MetricValue.extractRealValue(metric.getAccumValue());
					if (Util.isUsed(metricValue))
						value = metricValue;
					else {
						String info = "Metric \"" + metricName + "\" on algorithm \"" + 
								algNameList.get(j) + "\" and dataset \"" + datasetId + "\" is not real number";
						System.out.println(info);
					}
				}
				else {
					String info = "There is no metric \"" + metricName + "\" on algorithm \"" + 
								algNameList.get(j) + "\" and dataset \"" + datasetId + "\"";
					System.out.println(info);
				}
				data[curveIdx + 1][j] = value;
			}
			
		}
		
		
		// Fixing cubic interpolation
		if (points < n)  {
			for (int i = 0; i < curves; i ++) {
				
				int curveIdx = 2*i;
				for (int j = points; j < n; j++) {
					data[curveIdx][j] = j;
					data[curveIdx + 1][j] = data[curveIdx + 1][points - 1];
				}
			}
			
		}
		
		
		PlotGraph graph = new PlotGraph(data);
		graph.setXaxisLegend("Algorithm");
		graph.setYaxisLegend(metricName);
		
		return graph;
	}

	
	/**
	 * Creating metrics evaluation by dataset.
	 * @param datasetId specified dataset identifier.
	 * @return {@link JTable} as metrics evaluation by dataset.
	 */
	public JTable createDatasetTable(int datasetId) {
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		Map<String, Map<String, MetricValue>> values = parseMetrics(datasetId);
		
		List<String> metricNameList = this.metrics.getMetricNameList();
		Collections.sort(metricNameList);
		
		Vector<Vector<Object>> data = Util.newVector();
		for (String metricName : metricNameList) {
			Vector<Object> row = Util.newVector();
			row.add(metricName);
			
			for (String algName : algNameList) {
				Map<String, MetricValue> algValues = values.get(algName);
				if (algValues.containsKey(metricName))
					row.add(algValues.get(metricName));
				else
					row.add("");
			}
			data.add(row);
		}
		
		Vector<String> columns = Util.newVector();
		columns.add("");
		columns.addAll(algNameList);
		
		DefaultTableModel generalModel = new DefaultTableModel(data, columns) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			
			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		
		
		JTable table = new JTable(generalModel);
		return table;
		
	}
	
	
	/**
	 * Create metrics evaluation by all datasets.
	 * @return {@link JTable} as metrics evaluation by all datasets.
	 */
	public JTable createDatasetTable() {
		return createDatasetTable(-1);
	}
	
	
	/**
	 * Create metrics evaluation by metric name.
	 * @param metricName specified metric name.
	 * @return {@link JTable} metrics evaluation by metric name.
	 */
	public JTable createMetricTable(String metricName) {
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int datasetId : datasetIdList) {
			Vector<Object> row = Util.newVector();
			row.add("Dataset \"" + datasetId + "\"");
			
			for (String algName : algNameList) {
				Metric metric = this.metrics.get(metricName, algName, datasetId);
				if (metric == null || !metric.isValid())
					row.add("");
				else {
					MetricValue metricValue = metric.getAccumValue();
					if (metricValue != null)
						row.add(metricValue);
					else
						row.add("");
				}
				
			}
			data.add(row);
		}
		
		Vector<String> columns = Util.newVector();
		columns.add("");
		columns.addAll(algNameList);
		
		DefaultTableModel generalModel = new DefaultTableModel(data, columns) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		
		
		JTable table = new JTable(generalModel);
		return table;
	}

	
	/**
	 * Create table for showing algorithms descriptions.
	 * @return {@link JTable} for showing algorithms descriptions.
	 */
	public JTable createAlgDescsTable() {
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int datasetId : datasetIdList) {
			Vector<Object> row = Util.newVector();
			row.add("Dataset \"" + datasetId + "\"");
			
			for (String algName : algNameList) {
				String algDesc = metrics.getAlgDesc(algName, datasetId);
				algDesc = algDesc == null ? "" : algDesc; 
				row.add(algDesc);
				
			}
			data.add(row);
		}
		
		Vector<String> columns = Util.newVector();
		columns.add("");
		columns.addAll(algNameList);
		
		DefaultTableModel generalModel = new DefaultTableModel(data, columns) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		
		
		JTable table = new JTable(generalModel);
		return table;
	}

	
	/**
	 * Create text area for showing algorithms descriptions.
	 * @return {@link JTable} for showing algorithms descriptions.
	 */
	public JTextArea createAlgDescsTextArea() {
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);

		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < algNameList.size(); i++) {
			String algName = algNameList.get(i);
			if (i > 0)
				buffer.append("\n\n");
			buffer.append(algName);
			
			for (int j = 0; j < datasetIdList.size(); j++) {
				int datasetId = datasetIdList.get(j);
				String algDesc = metrics.getAlgDesc(algName, datasetId);
				algDesc = algDesc == null ? "" : algDesc;
				buffer.append("\n  Dataset \"" + datasetId + "\": " + algDesc);
			}
		}
		
		JTextArea txtAlgDescs = new JTextArea(buffer.toString());
		txtAlgDescs.setEditable(false);
		
		return txtAlgDescs;
	}
	
	
	/**
	 * Create metrics evaluation by parameters.
	 * @return parameters {@link JTable} metrics evaluation by parameters.
	 */
	public JTable createAlgParamsTable() {
		List<String> algNameList = this.metrics.getAlgNameList();
		
		Map<String, Map<Integer, String>> map = Util.newMap();
		int maxParameters = 0;
		for (String algName : algNameList) {
			
			Alg alg = algTable.query(algName);
			if (alg == null) continue;
			
			Map<Integer, String> pmap = null;
			if (map.containsKey(algName))
				pmap = map.get(algName);
			else {
				pmap = Util.newMap();
				map.put(algName, pmap);
			}
				
			List<String> paramNames = Util.newList();
			paramNames.addAll(alg.getConfig().keySet());
			int countParameters = 0;
			for (String paramName : paramNames) {
				Serializable param = alg.getConfig().get(paramName);
				if (param == null)
					continue;
				
				String paramText = paramName + "=";
				if (param instanceof Boolean)
					paramText += param.toString();
				else if (param instanceof java.lang.Number)
					paramText += MathUtil.format( ((java.lang.Number)param).doubleValue() );
				else if (param instanceof Date) {
					SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
					paramText += df.format( (Date)param );
				}
				else
					paramText += param.toString();
				
				pmap.put(countParameters, paramText);
				countParameters ++;
			}
			
			maxParameters = Math.max(maxParameters, countParameters);
		}
		
		Vector<String> columns = Util.newVector();
		columns.addAll(map.keySet());
		Collections.sort(columns);
		
		Vector<Vector<Object>> data = Util.newVector();
		for (int i = 0; i < maxParameters; i++) {
			Vector<Object> row = Util.newVector();
			for (String algName : columns) {
				Map<Integer, String> pmap = map.get(algName);
				if (pmap.containsKey(i))
					row.add(pmap.get(i));
				else
					row.add("");
			}
			
			data.add(row);
		}
		
		
		DefaultTableModel parameterModel = new DefaultTableModel(data, columns) {

			/**
			 * Serial version UID for serializable class. 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			public boolean isCellEditable(int row, int column) {
				// TODO Auto-generated method stub
				return false;
			}
			
		};
		
		
		JTable table = new JTable(parameterModel);
		return table;
	}
	
	
	/**
	 * Create an excel cell at specified row and column for the specified metric value at specified.
	 * @param metricValue specified metric value.
	 * @param row specified row.
	 * @param column specified column.
	 * @param cellFormat specified cell format.
	 * @return excel cell at specified row and column for the specified metric value at specified.
	 */
	private WritableCell createMetricValueCell(MetricValue metricValue, int row, int column, WritableCellFormat cellFormat) {
		if (metricValue == null || !metricValue.isUsed())
			return null;
		
		Object value = metricValue.value(); 
		if (value instanceof java.lang.Number)
			return new Number(column, row,
					MathUtil.round(((java.lang.Number)value).doubleValue()),
					cellFormat);
		else
			return new Label(column, row, metricValue.toString(), cellFormat);
	}
	
	
	/**
	 * Create excel rows of metrics evaluation by dataset.
	 * @param sheet excel sheet writer.
	 * @param datasetId dataset identifier.
	 * @param row starting row index.
	 * @param col starting column index
	 * @return number of excel rows to be created
	 * @throws Exception if any error raises.
	 */
	private int createDatasetExcel(
			WritableSheet sheet, 
			int datasetId, 
			int row, 
			int col) throws Exception {
		int rows = 0;
		
		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		int newcol = col + 1;
		for (String algName : algNameList) {
			Label lblAlg = new Label(newcol, row, algName, boldCellFormat12);
			sheet.addCell(lblAlg);
			
			newcol++;
		}
		row++;
		rows ++;
		
		Map<String, Map<String, MetricValue>> values = parseMetrics(datasetId);
		List<String> metricNameList = this.metrics.getMetricNameList();
		Collections.sort(metricNameList);
		
		//Create metric cells for given dataset.
		for (String metricName : metricNameList) {
			Label lblMetricName = new Label(col, row, metricName, cellFormat12);
			sheet.addCell(lblMetricName);
			
			newcol = col + 1;
			for (String algName : algNameList) {
				Map<String, MetricValue> algValues = values.get(algName);
				MetricValue metricValue = null;
				if (algValues.containsKey(metricName))
					metricValue = algValues.get(metricName);
				
				WritableCell cell = createMetricValueCell(metricValue, row, newcol, cellFormat12);
				if (cell != null)
					sheet.addCell(cell);
				
				newcol++;
			}
			
			row++;
			rows ++;
		}
		
		return rows;
	}
	
	
	/**
	 * Create excel rows of metrics evaluation by dataset.
	 * @param sheet excel sheet writer.
	 * @param row staring row index.
	 * @param col starting column index.
	 * @return rows to be saved
	 * @throws Exception if any error raises.
	 */
	private int createDatasetExcel(
			WritableSheet sheet, 
			int row, 
			int col) throws Exception {
		
		return createDatasetExcel(sheet, -1, row, col);
	}

	
	/**
	 * Create excel rows of metrics evaluation by metric name.
	 * @param sheet excel sheet writer.
	 * @param metricName metric name.
	 * @param row starting row index.
	 * @param col starting column index.
	 * @return rows to be saved
	 * @throws Exception if any error raises.
	 */
	private int createMetricExcel(
			WritableSheet sheet, 
			String metricName, 
			int row, 
			int col) throws Exception {
		int rows = 0;
		
		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		int newcol = col + 1;
		for (String algName : algNameList) {
			Label lblAlg = new Label(newcol, row, algName, boldCellFormat12);
			sheet.addCell(lblAlg);
			
			newcol++;
		}
		row++;
		rows++;
		
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		for (int datasetId : datasetIdList) {
			Label lblDataset = new Label(
					col, 
					row, 
					"Dataset \"" + datasetId + "\"", 
					cellFormat12);
			
			sheet.addCell(lblDataset);
			
			newcol = col + 1;
			
			for (String algName : algNameList) {
				Metric metric = this.metrics.get(metricName, algName, datasetId);
				MetricValue metricValue = null;
				if (metric != null && metric.isValid())
					metricValue = metric.getAccumValue();
				
				WritableCell cell = createMetricValueCell(metricValue, row, newcol, cellFormat12);
				if (cell != null)
					sheet.addCell(cell);

				newcol++;
			}
			row ++;
			rows ++;
		}
		
		return rows;
	}

	
	/**
	 * Create excel rows of algorithm parameters.
	 * @param sheet excel sheet writer.
	 * @param metricName metric name.
	 * @param row starting row index.
	 * @param col starting column index.
	 * @return rows to be saved
	 * @throws Exception if any error raises.
	 */
	private int createAlgParamsExcel(
			WritableSheet sheet, 
			int row, 
			int col) throws Exception {
		
		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);

		int maxRow = 0;
		for (int c = 0; c < algNameList.size(); c++) {
			String algName = algNameList.get(c);
			Alg alg = algTable.query(algName);
			if (alg == null) continue;
			
			int r = 0;
			Label lblAlg = new Label(c + 1, row + r, algName, boldCellFormat12);
			sheet.addCell(lblAlg);
			
			List<String> paramNames = Util.newList();
			paramNames.addAll(alg.getConfig().keySet());
			int countRow = 0;
			for (String paramName : paramNames) {
				Serializable param = alg.getConfig().get(paramName);
				if (param == null)
					continue;
				
				String paramText = paramName + "=";
				if (param instanceof Boolean)
					paramText += param.toString();
				else if (param instanceof java.lang.Number)
					paramText += MathUtil.format( ((java.lang.Number)param).doubleValue() );
				else if (param instanceof Date) {
					SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
					paramText += df.format( (Date)param );
				}
				else
					paramText += param.toString();
				
				r ++;
				countRow ++;
				Label paramCell = new Label(c + 1, row + r, paramText, cellFormat12);
				sheet.addCell(paramCell);
			}
			maxRow = Math.max(maxRow, countRow);
		}
		
		return maxRow + 1;
	}
	
	
	/**
	 * Create excel rows of algorithm descriptions.
	 * @param sheet excel sheet writer.
	 * @param metricName metric name.
	 * @param row starting row index.
	 * @param col starting column index.
	 * @return rows to be saved
	 * @throws Exception if any error raises.
	 */
	private int createAlgDescsExcel(
			WritableSheet sheet, 
			int row, 
			int col) throws Exception {
		int rows = 0;
		
		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		int newcol = col + 1;
		for (String algName : algNameList) {
			Label lblAlg = new Label(newcol, row, algName, boldCellFormat12);
			sheet.addCell(lblAlg);
			
			newcol++;
		}
		row++;
		rows++;
		
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		for (int datasetId : datasetIdList) {
			Label lblDataset = new Label(
					col, 
					row, 
					"Dataset \"" + datasetId + "\"", 
					cellFormat12);
			
			sheet.addCell(lblDataset);
			
			newcol = col + 1;
			
			for (String algName : algNameList) {
				String algDesc = metrics.getAlgDesc(algName, datasetId);
				algDesc = algDesc == null ? "" : algDesc; 
				
				Label cell = new Label(newcol, row, algDesc, cellFormat12);
				sheet.addCell(cell);

				newcol++;
			}
			row ++;
			rows ++;
		}
		
		return rows;
	}

	
	/**
	 * Create excel rows of notes.
	 * @param sheet excel sheet writer.
	 * @param metricName metric name.
	 * @param row starting row index.
	 * @param col starting column index.
	 * @return rows to be saved
	 * @throws Exception if any error raises.
	 */
	private int createNoteExcel(
			WritableSheet sheet, 
			int row, 
			int col) throws Exception {
		
		WritableFont font12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.NO_BOLD, false);
		WritableCellFormat cellFormat12 = new WritableCellFormat(font12);

		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		int rows = 0;
		for (int datasetId : datasetIdList) {
			xURI datasetUri = metrics.getDatasetUri(datasetId);
			
			if (datasetUri != null) {
				Label lbl = new Label(0, row, 
						"Dataset \"" + datasetId + "\" has path \"" + datasetUri + "\"", 
						cellFormat12);
				sheet.addCell(lbl);
				
				row ++;
				rows ++;
			}
		}
		
		PropList sysProps = SystemUtil.getSystemProperties();
		List<String> keys = Util.newList();
		keys.addAll(sysProps.keySet());
		for (int i = 0; i < keys.size(); i++) {
			row ++;
			rows ++;
			
			String key = keys.get(i).toString();
			Label lbl = new Label(0, row, 
					key + ": " + sysProps.getAsString(key), 
					cellFormat12);
			sheet.addCell(lbl);
		}
		
		return rows;
	}
	
	
	/**
	 * Create excel file for metrics evaluation.
	 * @param uri specified URI of excel file.
	 * @throws Exception if any error raises.
	 */
	public void createExcel(xURI uri) throws Exception {
		UriAdapter adapter = new UriAdapter(uri);
		OutputStream os = adapter.getOutputStream(uri, false);
		WritableWorkbook workbook = Workbook.createWorkbook(os);
		
		WritableSheet sheet = workbook.createSheet("Results", 0);
	
		WritableFont boldFont14 = new WritableFont(WritableFont.TIMES, 14,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat14 = new WritableCellFormat(boldFont14);

		WritableFont boldFont12 = new WritableFont(WritableFont.TIMES, 12,
				WritableFont.BOLD, false);
		WritableCellFormat boldCellFormat12 = new WritableCellFormat(boldFont12);

		int row = 0;
		
		
		//General evaluation
		Label lblGeneral = new Label(0, row, "General evaluation", boldCellFormat14);
		sheet.addCell(lblGeneral);
		row ++;
		int count = createDatasetExcel(sheet, row, 0);
		row += count + 2;
		
		
		//Dataset evaluation
		Label lblDsDetails = new Label(0, row, "Datasets evaluation", boldCellFormat14);
		sheet.addCell(lblDsDetails);
		row ++;
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		int maxCount = 0;
		int col = 0;
		for (int datasetId : datasetIdList) {
			Label lblDataset = new Label(col, row, "Dataset \"" + datasetId + "\"", boldCellFormat12);
			sheet.addCell(lblDataset);
			
			count = createDatasetExcel(sheet, datasetId, row + 1, col);
			col += algNameList.size() + 3;
			
			if (maxCount < count)
				maxCount = count;
		}
		maxCount++;
		row += maxCount + 2;
		
		
		// Evaluation on each metric
		List<String> metricNameList = this.metrics.getMetricNameList();
		Collections.sort(metricNameList);
		for (String metricName : metricNameList) {
			Label lblMetricName = new Label(0, row, metricName + " evaluation", boldCellFormat14);
			sheet.addCell(lblMetricName);
			row ++;
			row += createMetricExcel(sheet, metricName, row, 0) + 2;
		}

		
		// Algorithm parameters
		Label lblParameters = new Label(0, row, "Algorithm parameters", boldCellFormat14);
		sheet.addCell(lblParameters);
		row ++;
		row += createAlgParamsExcel(sheet, row, col) + 2;
		
		
		// Algorithm descriptions
		Label lblDescs = new Label(0, row, "Algorithm descriptions", boldCellFormat14);
		sheet.addCell(lblDescs);
		row ++;
		row += createAlgDescsExcel(sheet, row, 0) + 2;
		
		
		// Note
		Label lblNote = new Label(0, row, "Note", boldCellFormat12);
		sheet.addCell(lblNote);
		row ++;
		row += createNoteExcel(sheet, row, 0) + 2;
		
		
		workbook.write();
		workbook.close();
		try {
			os.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		adapter.close();
	}

	
	/**
	 * Create plain text for metrics evaluation.
	 * @return plain text of {@link Metrics} evaluation.
	 */
	public String createPlainText() {
		StringBuffer buffer = new StringBuffer();
		

		buffer.append("General evaluation");
		List<String> algNameList = this.metrics.getAlgNameList();
		Collections.sort(algNameList);
		Map<String, Map<String, MetricValue>> values = parseMetrics(-1);
		List<String> metricNameList = this.metrics.getMetricNameList();
		Collections.sort(metricNameList);
		
		for (String algName : algNameList) {
			
			buffer.append("\n\n  " + algName);
			for (int j = 0; j < metricNameList.size(); j++) {
				String metricName =  metricNameList.get(j);
				
				Map<String, MetricValue> algValues = values.get(algName);
				MetricValue metricValue = null;
				if (algValues.containsKey(metricName))
					metricValue = algValues.get(metricName);
				if (metricValue == null || !metricValue.isUsed())
					continue;
				
				buffer.append("\n");
				buffer.append("    " + metricName + " = " + MetricValue.valueToText(metricValue));
			}
		}
		
		
		buffer.append("\n\n\nDataset evaluation");
		List<Integer> datasetIdList = this.metrics.getDatasetIdList();
		Collections.sort(datasetIdList);
		for (int datasetId : datasetIdList) {
			buffer.append("\n\n  Dataset \"" + datasetId + "\"");
			
			for (int j = 0; j < algNameList.size(); j++) {
				String algName = algNameList.get(j);
				
				buffer.append("\n    " + algName);
				Metrics metrics = this.metrics.gets(algName, datasetId);
				for (int k = 0; k < metrics.size(); k++) {
					MetricValue metricValue = null;
					Metric metric = metrics.get(k);
					if (metric.isValid() && metric.getAccumValue() != null)
						metricValue = metric.getAccumValue();
					if (metricValue == null || !metricValue.isUsed())
						continue;
					
					buffer.append("\n      " + metric.getName() + " = " + MetricValue.valueToText(metricValue));
				}
				
			}
		}

		
		for (String metricName : metricNameList) {
			buffer.append("\n\n\n" + metricName + " evaluation");
			
			for (String algName : algNameList) {
				buffer.append("\n\n  " + algName);
				
				for (int datasetId : datasetIdList) {
					Metric metric = this.metrics.get(metricName, algName, datasetId);
					if (metric == null)
						buffer.append("\n    Dataset \"" + datasetId + "\" : NaN");
					else {
						MetricValue metricValue = null;
						if (metric.isValid() && metric.getAccumValue() != null)
							metricValue = metric.getAccumValue();
						if (metricValue == null || !metricValue.isUsed())
							continue;
						
						buffer.append("\n    Dataset \"" + datasetId + "\" got " + MetricValue.valueToText(metricValue));
					}
				}
				
			} // algorithm name iteration
			
		} // metric name iteration
		
		
		// Algorithm parameters
		buffer.append("\n\n\nAlgorithm parameters");
		for (String algName : algNameList) {
			buffer.append("\n\n  " + algName);
			Alg alg = algTable.query(algName);
			if (alg == null) continue;
			
			List<String> paramNames = Util.newList();
			paramNames.addAll(alg.getConfig().keySet());
			for (String paramName : paramNames) {
				Serializable param = alg.getConfig().get(paramName);
				if (param == null)
					continue;
				
				String paramText = paramName + "=";
				if (param instanceof Boolean)
					paramText += param.toString();
				else if (param instanceof java.lang.Number)
					paramText += MathUtil.format( ((java.lang.Number)param).doubleValue() );
				else if (param instanceof Date) {
					SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
					paramText += df.format( (Date)param );
				}
				else
					paramText += param.toString();
				
				buffer.append("\n    " + paramText);
			}
		}
		
		
		// Algorithm descriptions
		buffer.append("\n\n\nAlgorithm descriptions");
		for (String algName : algNameList) {
			buffer.append("\n\n  " + algName);
			for (int datasetId : datasetIdList) {
				String algDesc = metrics.getAlgDesc(algName, datasetId);
				algDesc = algDesc == null ? "" : algDesc;
				buffer.append("\n    Dataset \"" + datasetId + "\" got " + algDesc);
			}
			
		} // algorithm name iteration

		
		buffer.append("\n\n\nNote");
		for (int datasetId : datasetIdList) {
			xURI datasetUri = this.metrics.getDatasetUri(datasetId);
			if (datasetUri != null) {
				buffer.append("\n  Dataset \"" + datasetId + "\" has path \"" + datasetUri + "\"");
			}
		}
		buffer.append("\n\n");
		PropList sysProps = SystemUtil.getSystemProperties();
		List<String> keys = Util.newList();
		keys.addAll(sysProps.keySet());
		for (int i = 0; i < keys.size(); i++) {
			if (i > 0)
				buffer.append("\n");
			String key = keys.get(i);
			buffer.append("  " + key + ": " + sysProps.getAsString(key));
		}
		
		return buffer.toString();
	}

	
	/**
	 * Exporting (saving) metrics evaluation to excel file or plain text.
	 * @param comp parent component to show dialog.
	 */
	public void export(Component comp) {
		UriAdapter adapter = new UriAdapter();
        xURI uri = adapter.chooseDefaultUri(comp, false, null);
        adapter.close();
        
        if (uri == null) {
			JOptionPane.showMessageDialog(
					comp, 
					"URI not save", 
					"URI not save", 
					JOptionPane.WARNING_MESSAGE);
			return;
        }
        
        adapter = new UriAdapter(uri);
        boolean existed = adapter.exists(uri);
        adapter.close();
        if (existed) {
        	int ret = JOptionPane.showConfirmDialog(
        			comp, 
        			"URI exist. Do you want to override it?", 
        			"URI exist", 
        			JOptionPane.YES_NO_OPTION, 
        			JOptionPane.QUESTION_MESSAGE);
        	if (ret == JOptionPane.NO_OPTION)
        		return;
        }
		
        try {
            String ext = uri.getLastNameExtension();
	        if(ext != null && ext.toLowerCase().equals("xls")) {
	        	createExcel(uri);
	        }
	        else {
	            adapter = new UriAdapter(uri);
        		Writer writer = adapter.getWriter(uri, false);
        		
		        String text = createPlainText();
		        
		        writer.write(text);
		        writer.flush();
		        writer.close();
		        adapter.close();
	        }
        	
        	JOptionPane.showMessageDialog(comp, 
        			"URI saved successfully", 
        			"URI saved successfully", JOptionPane.INFORMATION_MESSAGE);
        }
		catch(Exception e) {
			e.printStackTrace();
		}
        
        
	}
	
	
}