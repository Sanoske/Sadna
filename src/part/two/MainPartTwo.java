package part.two;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class MainPartTwo {
	
	public static void main (String [] args) throws Exception {
			String s = "Cosmic_slice3_3.txt";
			File file = new File(s);
			String [][] cosmic =ReadFromFile.readFromFile(file);
			Global.initVars(cosmic);
			DiseaseHierarchy tree = new DiseaseHierarchy("fixed_disease_ontology_data.xlsx");
			Global.initTier(tree.getRoot());
			double [][] X = CreateXMatrix.createTheMatrix(cosmic);
			
			String [][] extractedPatient = ReadFromFile.readExportPatient(new File("extracted_export_patient_info_filtered.txt"));
			//int [][] Y = CreateYMatrix.createTheMatrix(extractedPatient,tree.getRoot(),tree.getTreeMap());
			int [][] Y = CreateYMatrix.createTheMatrix(extractedPatient, tree.getRoot());
			System.out.println(yIsNull(Y));
			
			printMatrixToFile(X,new File ("X matrix.txt"),Global.geneToColumns,"The X Matrix");
			printMatrixToFile(Y,new File("Y matrix.txt"),Global.labelToColumns, "The Y Matrix",tree.getRoot());
			
			System.out.println("DONE PRINT MATRIX");
	}

	private static boolean yIsNull(int[][] x) {
		for(int i=0; i<x.length; i++)
			for(int j=0; j<x[0].length; j++)
				if(x[i][j] != 0)
					return false;
		return true;
	}

	public static void printMatrixToFile(double [][] x, File file1, Map<String, Integer> map, String headline) throws Exception {
		FileOutputStream fos = new FileOutputStream(file1);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		fos.getChannel().truncate(0);
		bw.write(headline);
		bw.newLine();
		bw.newLine();
		bw.write("samples");
		bw.newLine();
		String s="";
		for(int i=0; i<x.length; i++) {
			s=Global.samples[i]+"\t";
			for(int j=0; j<x[i].length; j++)
				s+= (int)x[Global.sampleToRows.get(Global.samples[i])][j] + "\t";
			bw.write(s);
			bw.newLine();
			s="";
		}
		
		/*for(int k=0; k<map.size(); k++) {
			s = "gene "+Global.genes[k]+" is column number "+map.get(Global.genes[k]);
			bw.write(s);
			bw.newLine();
			s="";
		}*/
	}
	public static void printMatrixToFile(int [][] x, File file1, Map<String, Integer> map, String headline, DiseaseNode root) throws Exception {
		FileOutputStream fos = new FileOutputStream(file1);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write(headline);
		bw.newLine();
		bw.newLine();
		bw.write("samples");
		bw.newLine();
		String s="";
		for(int i=0; i<x.length; i++) {
			s=Global.samples[i]+"  ";
			for(int j=0; j<x[i].length; j++)
				s+= x[Global.sampleToRows.get(Global.samples[i])][j] + "  ";
			bw.write(s);
			bw.newLine();
			s="";
		}
		
		/*Queue<DiseaseNode> queue  = new LinkedList<DiseaseNode>();
		queue.add(root);
        while(!queue.isEmpty()){
            DiseaseNode node = queue.poll();
            s = "doid:"+node.getID()+" is column number "+Global.labelToColumns.get(node.getID());
            bw.write(s);
			bw.newLine();
			s="";
            queue.addAll(node.getChildren());
        }*/
	}
}
