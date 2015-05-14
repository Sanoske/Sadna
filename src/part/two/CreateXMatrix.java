package part.two;

import java.util.*;

public class CreateXMatrix {
	//the input is array string of genes names. each gene appears only once
	private static Map<String,Integer> mapGeneToColumns(String [] genes) {
		Map <String,Integer> map = new TreeMap<String,Integer>();
		for(int i=0; i<genes.length; i++)
			map.put(genes[i], i);
		return map;
	}
	
	//the input is array string of samples ids. each id appears only once
	private static Map<String,Integer> mapSamplesToRows(String [] samples) {
		Map <String,Integer> map = new TreeMap<String,Integer>();
		for(int i=0; i<samples.length; i++)
			map.put(samples[i], i);
		return map;
	}
	
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
	public static double [][] createTheMatrix(String [][] cosmic) {
		String [] samples = samplesArray(cosmic);
		String [] genes = geneArray(cosmic);
		Map geneToColumns = mapGeneToColumns(genes);
		Map sampleToRow = mapSamplesToRows(samples);
		double [][] X = new double [samples.length][genes.length];
		initMatrix(X);
		for( String sam : samples) {
			Set<Integer> rows = findRowsInCosmic(sam,cosmic);
			for( int row: rows) {
				for(int j=0; j<genes.length; j++) {
					if(genes[j].equals(cosmic[row][0]) && !cosmic[row][7].toLowerCase().contains("silent"))
						X[(Integer) sampleToRow.get(sam)][(Integer) geneToColumns.get(genes[j])] = 1;
				}
			}
		}
		return X;
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
