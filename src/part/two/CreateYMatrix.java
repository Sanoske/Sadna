package part.two;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Scanner;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateYMatrix {

	private static HashMap <String,String []> tier1Map;
	private static HashMap <String,String> memoizationMap;
	private static final String [] commonAdditions = {"cancer","disorder","disease","syndrome"};
	
	private static void inittier1Map() {
		  tier1Map = new HashMap<String, String[]>();
		  String [] arr1 = {"genetic_disorder","physical_disorder"};
		  String [] arr2 = {"neoplasm"};
		  String [] arr3 = {"system_disease"};
		  String [] arr4 = {"disorder"};
		  String [] arr5 = {"syndrome"};
		  String [] arr6 = {"infectious_disease"};
		  String [] arr7 = {"metabolic_disorder","metabolic_disease"};
		  tier1Map.put("0060035", arr1);
		  tier1Map.put("14566", arr2);
		  tier1Map.put("7",arr3);
		  tier1Map.put("150",arr4);
		  tier1Map.put("225",arr5);
		  tier1Map.put("0050117",arr6);
		  tier1Map.put("0014667",arr7);
		 }
	
	public static int [][] createTheMatrix(String [][] extractedPatient, DiseaseNode root) throws Exception {
		FileInputStream fis = new FileInputStream(new File("extracted_export_ordered2.xlsx"));
		
		// Finds the workbook instance for XLSX file
		XSSFSheet mySheet1;
		XSSFWorkbook myWorkBook1 = new XSSFWorkbook (fis);
        
     // Return first sheet from the XLSX workbook
        mySheet1 = myWorkBook1.getSheetAt(0);
		Iterator<Row> rowIterator = mySheet1.iterator();
		rowIterator.next();
		Iterator<Row> rowIterator1 = mySheet1.iterator();
		rowIterator1.next();
		Set <String> keys = new HashSet <String>();
		Map <String,Integer> realMap = new HashMap<String, Integer>();
		while (rowIterator1.hasNext()) {
            Row row = rowIterator1.next();
            Cell cell_id = row.getCell(0);
            Cell cell_doid = row.getCell(6);
            String [] doid_array = {cell_doid.getRichStringCellValue().getString()};
            if(cell_doid.getRichStringCellValue().getString().contains(","))
            	doid_array = cell_doid.getRichStringCellValue().getString().split(",");
            
            //System.out.println(cell_doid.getRichStringCellValue().getString());
            
            for(String doid: doid_array) {
	            DiseaseNode n = findNode(doid, root);
	            if(n == null) {
	            	System.out.println("we have a null node");
	            	System.out.println(cell_doid.getRichStringCellValue().getString());
	            	throw new Exception();
	            }
	            putDads(n, keys);
            }
		}
		int count = 0;
		for(String key : keys) {
			realMap.put(key, count);
			count++;
		}
		System.out.println("Y matrix before change: "+Global.labelToColumns.size());
		Global.labelToColumns = realMap;
		System.out.println("Y matrix after change: "+Global.labelToColumns.size());
		int [][] y = new int[Global.samples.length][Global.labelToColumns.size()];
		for(int i=0; i<y.length; i++)
			for(int j=0; j<y[0].length; j++)
				y[i][j] = 0;
		
		
		while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            Cell cell_id = row.getCell(0);
            Cell cell_doid = row.getCell(6);
            String [] doid_array = {cell_doid.getRichStringCellValue().getString()};
            if(cell_doid.getRichStringCellValue().getString().contains(","))
            	doid_array = cell_doid.getRichStringCellValue().getString().split(",");
            
            //System.out.println(cell_doid.getRichStringCellValue().getString());
            
            for(String doid: doid_array) {
	            DiseaseNode n = findNode(doid, root);
	            if(n == null) {
	            	System.out.println("we have a null node");
	            	System.out.println(cell_doid.getRichStringCellValue().getString());
	            	throw new Exception();
	            }
	            markMatrix(y, n, Integer.toString((int)cell_id.getNumericCellValue()));
            }
		}
		return y;
	}
	private static DiseaseNode findNode (String id,DiseaseNode root) {
		id = id.replaceAll("doid:", "");
		id = id.replaceAll("DOID:", "");
		Queue<DiseaseNode> queue  = new LinkedList<DiseaseNode>();
		queue.add(root);
        while(!queue.isEmpty()){
            DiseaseNode node = queue.poll();
            if (node.getID().equals(id)) {
            	return node;
            }
            queue.addAll(node.getChildren());
        }
        return null;
	}
	private static void markMatrix(int[][] y, DiseaseNode startNode, String line) {
		Queue<DiseaseNode> queue  = new LinkedList<DiseaseNode>();
		queue.add(startNode);
        while(!queue.isEmpty()){
            DiseaseNode node = queue.poll();
            if (!(node.getID().equals("NA"))) {
            	int i = Global.sampleToRows.get(line);
            	int j = Global.labelToColumns.get(node.getID());
            	y[i][j] = 1;
            }
            queue.addAll(node.getParents());
        }    
	}
	
	private static void putDads(DiseaseNode startNode, Set <String> keys) {
		Queue<DiseaseNode> queue  = new LinkedList<DiseaseNode>();
		queue.add(startNode);
        while(!queue.isEmpty()){
            DiseaseNode node = queue.poll();
            keys.add(node.getID());
            queue.addAll(node.getParents());
        }
	}
	private static DiseaseNode findMatchBFS (String disease, DiseaseNode searchRoot, HashMap<String,DiseaseNode> mapID, int line, int column) {
		String matchID = "-1";
		Queue<DiseaseNode> queue  = new LinkedList<DiseaseNode>();
		queue.add(searchRoot);
		DiseaseNode resultNode = new DiseaseNode("-1");
        while(!queue.isEmpty() && !resultNode.getID().equals("-1")){
            DiseaseNode node = queue.poll();
            matchID = compareDescription(disease,node);
            if (!matchID.equals("-1")) {
                resultNode = mapID.get(matchID);
            }
            queue.addAll(node.getChildren());
        }
		if (!resultNode.getID().equals("-1")) {
			return resultNode;
		}
		else {
			System.out.println("Search for match failed, please be kind and help us find a match for description: " + disease + " in line: " +line+ " column: " +column);
			Scanner in = new Scanner(System.in);
			String input = in.next();
			//in.close();
			if (input.equals("-1")) {
				return resultNode;
			}
			else {
				resultNode = mapID.get(input);
				memoizationMap.put(disease, resultNode.getName());
				return resultNode;
			}
		}
	}

	private static String compareDescription(String disease, DiseaseNode node) {
		String originalName = disease.replace("_", " ").toLowerCase();
		String compareName = node.getName().toLowerCase();
		//compare with manual learned mapping
		if (memoizationMap.containsKey(disease)) {
			return memoizationMap.get(disease);
		}
		//naive compare
		if (originalName.equals(compareName)) {
			return node.getID();
		}
		//compare with suffix from tier1
		if (tier1Map.containsKey(node.getID())) {
			for (String addString : tier1Map.get(node.getID())) {
				String altrName = disease + "_" + addString;
				if (compareWithLevenshtein(altrName,compareName)) {
					return node.getID();
				}
			}
		}
		//no addition Levenshtein distance
		if (compareWithLevenshtein(disease,compareName)) {
			return node.getID();
		}
		//compare with suffix from commonAdditions
		for (String addString : commonAdditions) {
			String altrName = disease + "_" + addString;
			if (compareWithLevenshtein(altrName,compareName)) {
				return node.getID();
			}
		}
		return "-1";
	}
	private static boolean compareWithLevenshtein (String  disease, String compareName) {
		String [] diseaseNameArr = disease.split("_");
		String [] compareNameArr = compareName.split(" ");
		String [] smallCompare;
		String [] largeCompare;
		if (diseaseNameArr.length < compareNameArr.length) {
			smallCompare = diseaseNameArr;
			largeCompare = compareNameArr;
		}
		else {
			largeCompare = diseaseNameArr;
			smallCompare = compareNameArr;
		}
		double totalCount = 0;
		for (int i = 0;i<largeCompare.length;i++) {
			int count = Integer.MAX_VALUE;
			for (int j = 0;j<smallCompare.length;j++) {
				int tempCount= distanceLevenshtein(largeCompare[i],smallCompare[j]);
				if (tempCount < count) {
					count = tempCount;
				}
			}
			totalCount+=count;
		}
			totalCount = (totalCount/smallCompare.length);
		if (totalCount < 6) {
			return true;
		}
		else {
			return false;
		}
	}
	private static int distanceLevenshtein (String a, String b) {
		a = a.toLowerCase();
        b = b.toLowerCase();
		// i == 0
        int [] costs = new int [b.length() + 1];
        for (int j = 0; j < costs.length; j++)
            costs[j] = j;
        for (int i = 1; i <= a.length(); i++) {
            // j == 0; nw = lev(i - 1, j)
            costs[0] = i;
            int nw = i - 1;
            for (int j = 1; j <= b.length(); j++) {
                int cj = Math.min(1 + Math.min(costs[j], costs[j - 1]), a.charAt(i - 1) == b.charAt(j - 1) ? nw : nw + 1);
                nw = costs[j];
                costs[j] = cj;
            }
        }
        return costs[b.length()];
    }
}
