package part.two;

import java.io.File;

public class MainPartTwo {
	
	public static void main (String [] args) throws Exception {
		String s = "Cosmic_slice3_3.txt";
		File file = new File(s);
		String [][] cosmic =ReadFromFile.readFromFile(file);
		Global.initVars(cosmic);
		double [][] X = CreateXMatrix.createTheMatrix(cosmic);
		DiseaseHierarchy tree = new DiseaseHierarchy("fixed_disease_ontology_data.xlsx");
		BTreePrinter.printNode(tree.getRoot(),4);
	}
}
