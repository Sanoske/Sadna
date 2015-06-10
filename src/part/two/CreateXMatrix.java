package part.two;

import java.util.*;

public class CreateXMatrix {
	
	// make the gene array
	private static String[] geneArray(String [][] cosmic) {
		Set <String> s = new HashSet<String>();
		for(int i=0; i<cosmic.length; i++)
			s.add(cosmic[i][0]);
		String [] ans = new String [s.size()];
		int count = 0;
		for(String str : s) {
			ans[count] = str;
			count++;
		}
		return ans;
	}
	
	// make the samples array
	private static String[] samplesArray(String [][] cosmic) {
		Set <String> s = new HashSet<String>();
		for(int i=0; i<cosmic.length; i++)
			s.add(cosmic[i][2]);
		String [] ans = new String [s.size()];
		int count = 0;
		for(String str : s) {
			ans[count] = str;
			count++;
		}
		return ans;
	}
		
	//create the X matrix. the input is the COSMIC file as a matrix
	public static double [][] createTheMatrix(String [][] cosmic) throws Exception {
		String [] samples = samplesArray(cosmic);
		String [] genes = geneArray(cosmic);
		double [][] X = new double [samples.length][genes.length];
		Map<String,Integer> realGeneToColumns = new HashMap<String, Integer>();
		initMatrix(X);
		int realPosition = 0;
		for( String sam : samples) {
			Set<Integer> rows = findRowsInCosmic(sam,cosmic);
			for( int row: rows) {
				for(int j=0; j<genes.length; j++) {
					if(genes[j].equals(cosmic[row][0]) && !(cosmic[row][7].toLowerCase().contains("silent"))) {
						X[(Integer) Global.sampleToRows.get(sam)][(Integer) Global.geneToColumns.get(genes[j])] = 1;
						if(!(realGeneToColumns.containsKey(genes[j]))) {
							realGeneToColumns.put(genes[j], realPosition);
							realPosition++;
						}
					}
				}
			}
		}
		double [][] realX = new double [samples.length][realGeneToColumns.size()];
		Set <String> keys = realGeneToColumns.keySet();
		for( int j=0; j<realX.length; j++) {
			for(String key: keys) {
				realX[j][realGeneToColumns.get(key)] = X[j][Global.geneToColumns.get(key)];
			}
		}
		if(Global.geneToColumns.size() == realGeneToColumns.size()) {
			System.out.println("NOT GOOD");
			throw new Exception();
		}
		System.out.println("X matrix before change: "+Global.geneToColumns.size());
		Global.geneToColumns = realGeneToColumns;
		System.out.println("X matrix after change: "+Global.geneToColumns.size());
		return realX;
	}

	private static void initMatrix(double[][] x) {
		for(int i=0; i<x.length; i++)
			for(int j=0; j<x[0].length; j++)
				x[i][j] = 0;
		
	}

	private static Set<Integer> findRowsInCosmic(String sam, String[][] cosmic) {
		Set <Integer> ans = new HashSet<Integer>();
		for(int i=0; i<cosmic.length; i++) {
			if( cosmic[i][2].equals(sam))
				ans.add(i);
		}
		return ans;
	}
}
