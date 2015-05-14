package part.two;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class readFromFile {
	
	public static String [][] readFromExcelFile(File file) throws Exception {
		
        FileInputStream fis = new FileInputStream(file);

        // Finds the workbook instance for XLSX file
        XSSFWorkbook myWorkBook = new XSSFWorkbook (fis);
       
        // Return first sheet from the XLSX workbook
        XSSFSheet mySheet = myWorkBook.getSheetAt(0);
       
        // Get iterator to all the rows in current sheet
        Iterator<Row> rowIterator = mySheet.iterator();
        rowIterator.next();
        List <List<String>> l = new LinkedList<List<String>>();
        // Traversing over each row of XLSX file
        while (rowIterator.hasNext()) {
            Row row = rowIterator.next();
            // For each row, iterate through each columns
            Iterator<Cell> cellIterator = row.cellIterator();
            List<String> l_row = new LinkedList<String>();
            while (cellIterator.hasNext()) {
                Cell cell = cellIterator.next();
                l_row.add(cell.getStringCellValue());
            }
            l.add(l_row);
        }
        String [][] ans = new String [l.size()][];
        for(int i=0; i<ans.length; i++) {
        	List <String> l_ans = l.get(i);
        	ans[i] = new String [l_ans.size()];
        	for(int j=0; j<ans[i].length; j++)
        		ans[i][j] = l_ans.get(j);
        }
        return ans;
	}
	// read a  "txt" file and put it in String matrix. the words must be seperated with TAB
		public static String[][] readFromFile(File file) throws Exception 
		{
			Scanner scan = new Scanner(file);
			ArrayList<String[]> answer = new ArrayList<String[]>();
			String currLine = scan.nextLine();
			while(scan.hasNextLine())
			{
				currLine = scan.nextLine();
				String [] a = currLine.split("\t");
				answer.add(a);
			}
			scan.close();
			int size2 = answer.size();
			String [][] answer1 = new  String[size2][];
			for(int i=0;i<size2;i++) {
				answer1[i] = new String[answer.get(i).length];
				for(int j=0;j<answer.get(i).length;j++)
					answer1[i][j] = answer.get(i)[j];
			}
			return answer1;
		}
		
		/*public static void main (String [] args) throws Exception {
			String s = "disease_ontology_data.xlsx";
			File file = new File(s);
			String [][] features_and_labels =readFromExcelFile(file);
			System.out.println("DONE");
		}*/
}
