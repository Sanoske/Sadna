package part.three;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import part.*;
import part.two.CreateXMatrix;
import part.two.CreateYMatrix;
import part.two.DiseaseHierarchy;
import part.two.Global;
import part.two.ReadFromFile;
public class MainPartThree {

	public static void main(String[] args) throws Exception {
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
		
		//part.two.MainPartTwo.printMatrixToFile(X,new File ("X matrix.txt"),Global.geneToColumns,"The X Matrix");
		//part.two.MainPartTwo.printMatrixToFile(Y,new File("Y matrix.txt"),Global.labelToColumns, "The Y Matrix",tree.getRoot());
		
		System.out.println("DONE BUILD MATRIX");
		
		String [][] extractedPatientFull = part.two.ReadFromFile.readExportPatientFull(new File("extracted_export_patient_info_filtered.txt"));
		Set <String> temp = new HashSet<String>();
		Map <String,Integer> studyToFold = new HashMap<String, Integer>();
		for(int i=0; i< extractedPatientFull.length; i++)
			temp.add(extractedPatientFull[i][10]);
		int count = 0;
		for(String str : temp) {
			studyToFold.put(str, count);
			count++;
		}
		int [] partition = new int [extractedPatientFull.length];
		for(int j=0; j<partition.length; j++) {
			Set <String> set = part.two.Global.sampleToRows.keySet();
			for(String str : set) {
				if (str.equals(extractedPatientFull[j][0])) {
					partition[j] = studyToFold.get(extractedPatientFull[j][10]);
					break;
				}
			}
		}
		
		part.one.CV.CVPredict(X, Y, 2, 5, 0.5, (int)Math.floor(Math.sqrt(extractedPatientFull.length)), 1000000000, 600000000);
		System.out.println("DONE");
	}
}
