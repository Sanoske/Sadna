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
		Map<String,Integer> realrealGeneToColumns = new HashMap<String, Integer>();
		realPosition = 0;
		int [] countgenes = new int [realGeneToColumns.size()];
		
		Set <String> keys = realGeneToColumns.keySet();
		for( String gene: keys) {
			int pos = realGeneToColumns.get(gene);
			for(int i=0; i<X.length; i++) {
				if(X[i][pos] == 1)
					countgenes[pos]++;
			}
		}
		
		for(String gene: keys) {
			int pos = realGeneToColumns.get(gene);
			if(countgenes[pos] > 0) {
				realrealGeneToColumns.put(gene, realPosition);
				realPosition++;
			}
		}
		double [][] realX1 = new double [samples.length][realrealGeneToColumns.size()];
		keys = realrealGeneToColumns.keySet();
		for( int j=0; j<realX1.length; j++) {
			for(String key: keys) {
				realX1[j][realrealGeneToColumns.get(key)] = X[j][realGeneToColumns.get(key)];
			}
		}
		if(Global.geneToColumns.size() == realrealGeneToColumns.size()) {
			System.out.println("NOT GOOD");
			throw new Exception();
		}
		
		/*Map<String,Integer> realsamplesToRows = new HashMap<String, Integer>();
		keys = Global.sampleToRows.keySet();
		boolean hasOnes = false;
		realPosition = 0;
		for(int i=0; i<realX1.length; i++) {
			for(int j=0; j<realX1[0].length; j++) {
				if(realX1[i][j] == 1)
					hasOnes = true;
			}
			if(hasOnes) {
				for(String row : keys) {
					if(Global.sampleToRows.get(row) == i) {
						realsamplesToRows.put(row, realPosition);
						realPosition++;
						break;
					}
				}
			}
			hasOnes = false;
		}
		
		double [][] realX = new double [realsamplesToRows.size()][realrealGeneToColumns.size()];
		Set <String > rows = realsamplesToRows.keySet();
		Set <String> columns = realrealGeneToColumns.keySet();
		for(String row: rows) {
			for(String column: columns) {
				realX[realsamplesToRows.get(row)][realrealGeneToColumns.get(column)] = realX1[Global.sampleToRows.get(row)][realrealGeneToColumns.get(column)];
			}
		}
		
		System.out.println("X matrix before change rows: "+Global.geneToColumns.size());
		Global.sampleToRows = realsamplesToRows;
		System.out.println("X matrix after change rows: "+Global.geneToColumns.size());
		
		keys = Global.sampleToRows.keySet();
		String [] newSamples = new String [Global.sampleToRows.size()];
		int count = 0;
		for(String sam: keys) {
			newSamples[count] = sam;
			count++;
		}
		Global.samples = newSamples;*/
		
		System.out.println("X matrix before change columns: "+Global.geneToColumns.size());
		Global.geneToColumns = realrealGeneToColumns;
		System.out.println("X matrix after change columns: "+Global.geneToColumns.size());
		return realX1;
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
