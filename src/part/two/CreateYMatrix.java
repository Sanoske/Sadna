package part.two;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class CreateYMatrix {
	
	private static Row findRow(String cellContent) {
	    for (Row row : Global.mySheet) {
	        Cell cell = row.getCell(0);
	        if (cell.getRichStringCellValue().getString().trim().equals(cellContent))
	        	return row;
	       /* cell = row.getCell(2,Row.CREATE_NULL_AS_BLANK);
	        if (cell.getRichStringCellValue().getString().trim().contains(cellContent))
	        	return row;
	        cell = row.getCell(3,Row.CREATE_NULL_AS_BLANK);
	        if (cell.getRichStringCellValue().getString().trim().contains(cellContent))
	        	return row;*/
	    }
	    return null;
	}
	
	private static void getAncestors (String doid, String [][] disease_ontology,Set <String> ancestors) throws Exception {
		        
        Row row = findRow(doid);
        if( !(row.getCell(4).getRichStringCellValue().getString().equals("NA"))) {
        	Cell cell = row.getCell(4);
        	String [] dads = cell.getRichStringCellValue().getString().split(",");
        	for( String doidDad : dads) {
        		ancestors.add(doidDad);
        		getAncestors(doidDad, disease_ontology,ancestors);
        	}
        }
	}
	
	public static String [] getAncestors (String doid, String [][] disease_ontology) throws Exception {
	    
		Set<String> ancestors = new HashSet<String>();
		getAncestors(doid, disease_ontology,ancestors);
		String [] ans = new String [ancestors.size()];
		int count = 0;
		for(String str : ancestors) {
			ans[count] = str;
			count++;
		}
		return ans;
	}
}
