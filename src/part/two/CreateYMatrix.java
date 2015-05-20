package part.two;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
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
	private static  HashMap <String,String []> tier1Map;
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
	
	public static int [][] createTheMatrix(String [][] extractedPatient, DiseaseNode root, HashMap<String,DiseaseNode> mapID) {
		inittier1Map();
		HashMap <String,String> memoizationMap = new HashMap <String,String>();
		int [][] y = new int[Global.samples.length][];
		for (int i = 0; i<extractedPatient.length;i++) {
			DiseaseNode startNode = root;
			for (int j = 1; j<extractedPatient[i].length;j++) {
				DiseaseNode bingoNode = findMatchBFS(extractedPatient[i][j],startNode,mapID,i,j);
				if (!bingoNode.getID().equals("-1")) {
					startNode = bingoNode;
				}
			}
			markMatrix(y,startNode,extractedPatient[i][0]);
		}
		return y;
	}
	
	private static void markMatrix(int[][] y, DiseaseNode startNode, String line) {
		Queue<DiseaseNode> queue  = new LinkedList<DiseaseNode>();
		queue.add(startNode);
        while(!queue.isEmpty()){
            DiseaseNode node = queue.poll();
            if (!node.getID().equals("NA")) {
            	y[Global.sampleToRows.get((line))][Global.labelToColumns.get((node.getID()))] = 1;
            }
            queue.addAll(node.getParents());
        }    
	}
	private static DiseaseNode findMatchBFS (String disease, DiseaseNode searchRoot, HashMap<String,DiseaseNode> mapID, int line, int column) {
		String matchID = "-1";
		Queue<DiseaseNode> queue  = new LinkedList<DiseaseNode>();
		queue.add(searchRoot);
		DiseaseNode resultNode = new DiseaseNode("-1");
        while(!queue.isEmpty() || !resultNode.getID().equals("-1")){
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
			System.out.println("Search for match failed, please be kind and help us find a match for description: " + disease + "in line: " +line+ "column: " +column);
			Scanner in = new Scanner(System.in);
			String input = in.nextLine();
			in.close();
			if (input.equals("-1")) {
				return resultNode;
			}
			else {
				memoizationMap.put(disease, input);
				resultNode = mapID.get(input);
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
		double totalCount = 0;
		for (int i = 0;i<diseaseNameArr.length;i++) {
			int count = Integer.MAX_VALUE;
			for (int j = 0;j<compareNameArr.length;j++) {
				int tempCount= distanceLevenshtein(diseaseNameArr[i],compareNameArr[j]);
				if (tempCount < count) {
					count = tempCount;
				}
			}
			totalCount+=count;
		}
		totalCount = (totalCount/diseaseNameArr.length);
		if (totalCount > 0.6) {
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
