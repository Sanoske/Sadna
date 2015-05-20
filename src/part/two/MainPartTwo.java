package part.two;

import java.io.File;

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
		int [][] Y = CreateYMatrix.createTheMatrix(extractedPatient,tree.getRoot(),tree.getTreeMap());
	}
}
