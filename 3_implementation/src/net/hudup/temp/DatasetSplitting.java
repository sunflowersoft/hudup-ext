/**
 * 
 */
package net.hudup.temp;

import java.io.PrintWriter;
import java.util.List;
import java.util.Random;

import net.hudup.core.Util;
import net.hudup.core.data.AttributeList;
import net.hudup.core.data.DataConfig;
import net.hudup.core.data.Fetcher;
import net.hudup.core.data.Pair;
import net.hudup.core.data.Profile;
import net.hudup.core.data.ProviderAssoc;
import net.hudup.core.logistic.UriAdapter;
import net.hudup.core.logistic.xURI;
import net.hudup.core.logistic.ui.ProgressEvent;
import net.hudup.core.logistic.ui.ProgressListener;
import net.hudup.data.ProviderAssocFactory;

/**
 * Utility class for splitting dataset into training dataset and testing dataset.
 * 
 * @author Loc Nguyen
 * @version 10.0
 */
public final class DatasetSplitting {

	
	/**
	 * Splitting dataset into training dataset and testing dataset.
	 * @param datasetUri URI of original dataset.
	 * @param store storage (directory) for split training dataset and testing dataset.
	 * @param testRatio ratio of splitting.
	 * @param registeredListener listener to monitor splitting progress.
	 * @throws Exception if any error raises.
	 */
	public static void split(
			xURI datasetUri, 
			xURI store, 
			double testRatio, 
			ProgressListener registeredListener) throws Exception {

		int numFold = (int)(1.0 / testRatio);
		if (numFold == 0)
			return;
		
		List<List<Integer>> foldList = Util.newList();
		for (int i = 0; i < numFold; i++) {
			List<Integer> list = Util.newList();
			foldList.add(list);
		}
		
		UriAdapter adapter = new UriAdapter(datasetUri);
		List<StringBuffer> content = adapter.readLines(datasetUri);
		adapter.close();
		if (content.size() == 0)
			return;
		
		// Removing CSV header
		String firstRecord = content.get(0).toString();
		AttributeList attributes = new AttributeList();
		try {
			attributes.parseText(firstRecord);
		}
		catch (Throwable e) {
			e.printStackTrace();
			attributes.clear();
		}
		if (attributes.size() > 0 && !attributes.get(0).getName().isEmpty()) {
			content.remove(0);
		}
		else
			firstRecord = null;
		
		List<Pair> lineNumber = Util.newList();
		for (int i = 0; i < content.size(); i++)
			lineNumber.add(new Pair(i, 0));
		
		Random rnd = new Random();
		while (true) {
			
			for (int i = 0; i < numFold; i++) {
				int n = lineNumber.size();
				if (n > 0) {
					int k = rnd.nextInt(n);
					Pair pair = lineNumber.get(k);
					lineNumber.remove(k);
					
					List<Integer> list = foldList.get(i); 
					list.add(pair.key());
				}
			}
			
			if (lineNumber.size() == 0)
				break;
		}
		

		adapter = new UriAdapter(datasetUri);
		store = (store == null ? 
				adapter.getStoreOf(datasetUri) : store);
		
		String lastName = datasetUri.getLastName();
		int dot = lastName.lastIndexOf(".");
		if (dot != -1)
			lastName = lastName.substring(0, dot);
		
		PrintWriter[] foldWriter = new PrintWriter[numFold * 2];
		for (int i = 0; i < numFold * 2; i += 2) {
			xURI base = store.concat(lastName + (i/2+1) + ".base");
			xURI test = store.concat(lastName + (i/2+1) + ".test");

			foldWriter[i] = new PrintWriter(adapter.getWriter(base, false));
			foldWriter[i + 1] = new PrintWriter(adapter.getWriter(test, false));
			
			if (firstRecord != null) {
				foldWriter[i].println(firstRecord);
				foldWriter[i + 1].println(firstRecord);
			}
		}

		int progressTotal = content.size();
		int progressStep = 0;
		for (int i = 0; i < content.size(); i++) {
			StringBuffer line = content.get(i);
			
			for (int j = 0; j < numFold; j++) {
				List<Integer> list = foldList.get(j);
				if (list.contains(i))
					foldWriter[2*j + 1].println(line); // test
				else
					foldWriter[2*j].println(line); // base
				
			}
			if (registeredListener != null)
				registeredListener.receiveProgress(
					new ProgressEvent(registeredListener, progressTotal, ++progressStep, "Written: " + line));
		}
		
	
		for (PrintWriter writer :  foldWriter) {
			writer.flush();
			writer.close();
		}
	
		adapter.close();
	}
	
	
	/**
	 * Splitting dataset into training dataset and testing dataset.
	 * @param datasetUri URI of original dataset.
	 * @param testRatio ratio of splitting.
	 * @param registeredListener listener to monitor splitting progress.
	 * @throws Exception if any error raises.
	 */
	public static void split(xURI datasetUri, double testRatio, ProgressListener registeredListener) throws Exception {
		split(datasetUri, null, testRatio, registeredListener);
	}
	
	
	/**
	 * Making sparse the unit (table) specified by the source configuration and source unit.
	 * The resulted sparse unit is stored in the storage of source unit.
	 * @param srcConfig specified source configuration.
	 * @param srcUnit specified source unit.
	 * @param sparseIndices index list of columns to be made sparse
	 * @param sparseRatio ratio of making sparse.
	 * @param registeredListener listener to monitor making sparse progress.
	 * @throws Exception if any error raises.
	 */
	public static void makeSparse(
			DataConfig srcConfig,
			String srcUnit,
			int[] sparseIndices,
			double sparseRatio, 
			ProgressListener registeredListener) throws Exception {
		int n = sparseIndices.length; 
		if (n <= 0)
			return;
		
		ProviderAssoc assoc = ProviderAssocFactory.create(srcConfig);
		Fetcher<Profile> fetcher = assoc.getProfiles(srcUnit, null);
		int m = fetcher.getMetadata().getSize();
		fetcher.close();
		
		List<Integer> indices = Util.newList();
		int N = m*n;
		for (int i = 0; i < N; i++) {
			indices.add(i);
		}
		
		int K = Math.min((int)(sparseRatio * N), N);
		Random rnd = new Random();
		List<int[]> removedRowColumnList = Util.newList();
		for (int i = 0; i < K; i++) {
			int j = rnd.nextInt(indices.size());
			indices.remove(j);
			
			int row = j / n;
			int column = j % n;
			removedRowColumnList.add(new int[] {row, column});
		}
		
		String dstUnit = srcUnit + "_missing";
		AttributeList attributes = assoc.getAttributes(srcUnit);
		assoc.createUnit(dstUnit, attributes);
		fetcher = assoc.getProfiles(srcUnit, null);
		int i = 0;
		while (fetcher.next()) {
			Profile profile = fetcher.pick();
			if (profile == null)
				continue;
			
			Profile newProfile = (Profile)profile.clone();
			for (int j = 0; j < attributes.size(); j++) {
				for (int k = 0; k < K; k++) {
					int[] rowColumn = removedRowColumnList.get(k); 
					int row = rowColumn[0];
					int column = sparseIndices[rowColumn[1]];
					
					if (i == row && j == column)
						newProfile.setMissing(j);
				}
			}
			
			assoc.insertProfile(dstUnit, newProfile);
			i++;
		}
		fetcher.close();
		
		assoc.close();
	}
	
	
}
