package part.two;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Global {
	public static XSSFSheet mySheet;
	public static XSSFWorkbook myWorkBook;
	public static Map <String,Integer> geneToColumns;
	public static Map <String,Integer> sampleToRows;
	public static String [] samples;
	public static String [] genes;
	public static Map <String,Integer> labelToColumns;
	
	//the input is array string of genes names. each gene appears only once
		private static Map<String,Integer> mapGeneToColumns(String [] genes) {
			Map <String,Integer> map = new HashMap<String,Integer>();
			for(int i=0; i<genes.length; i++)
				map.put(genes[i], i);
			return map;
		}
		
		//the input is array string of samples ids. each id appears only once
		private static Map<String,Integer> mapSamplesToRows(String [] samples) {
			Map <String,Integer> map = new HashMap<String,Integer>();
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
		private static Map<String, Integer> mapLabelToColumns(DiseaseNode root) {
			Queue<DiseaseNode> level  = new LinkedList<DiseaseNode>();
			Map <String,Integer> a = new HashMap<String, Integer>();
			Set <String> s = new HashSet<String>();
	        level.add(root);
	        int count = 0;
	        while(!level.isEmpty()){
	        	DiseaseNode d = level.poll();
	        	s.add(d.getID());
	        	level.addAll(d.getChildren());
	        }
	        for(String str : s) {
	        	a.put(str,count);
	        	count++;
	        }
	        return a;
		}
		
		public static void initVars(String [][] cosmic) throws Exception {
			
			FileInputStream fis = new FileInputStream(new File("fixed_disease_ontology_data.xlsx"));
			
			// Finds the workbook instance for XLSX file
	        myWorkBook = new XSSFWorkbook (fis);
	        
	     // Return first sheet from the XLSX workbook
	        mySheet = myWorkBook.getSheetAt(0);
			samples = samplesArray(cosmic);
			genes = geneArray(cosmic);
			geneToColumns = mapGeneToColumns(genes);
			sampleToRows = mapSamplesToRows(samples);
			fis.close();
		}
		
		public static void initTier(DiseaseNode root) {
			labelToColumns = mapLabelToColumns(root);
		}
}
