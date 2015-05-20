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
	private static final HashMap <String,String []> tire1Map = {()};
	private static HashMap <String,String> memoizationMap;
	private static final String [] commonAdditions = {"cancer","disorder","disease","syndrome"};
	
	public static String [][] createTheMatrix(String [][] extractedPatient, DiseaseNode root, HashMap<String,DiseaseNode> mapID) {
		HashMap <String,String> memoizationMap = new HashMap <String,String>();
		int [][] x = new int[][];
		for (int i = 0; i<extractedPatient.length;i++) {
			DiseaseNode startNode = root;
			for (int j = 1; j<extractedPatient[i].length;j++) {
				DiseaseNode bingoNode = findMatchBFS(extractedPatient[i][j],startNode,mapID,i,j);
				if (!bingoNode.getID().equals("-1")) {
					startNode = bingoNode;
				}
			}
			markMatrix(x,startNode,extractedPatient[i][0]);
		}
	}
	private static void markMatrix(int[][] x, DiseaseNode startNode, String line) {
		Queue<DiseaseNode> queue  = new LinkedList<DiseaseNode>();
		queue.add(startNode);
        while(!queue.isEmpty()){
            DiseaseNode node = queue.poll();
            if (!node.getID().equals("NA")) {
            	x[Global.sampleToRows(line)][Global.lableToColumns(node.getID())] = 1;
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
		if (tire1Map.containsKey(node.getID())) {
			for (String addString : tire1Map.get(node.getID())) {
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
