package part.three;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import part.*;
import part.two.CreateXMatrix;
import part.two.CreateYMatrix;
import part.two.DiseaseHierarchy;
import part.two.Global;
import part.two.ReadFromFile;
import mulan.classifier.lazy.MLkNN;
import mulan.classifier.meta.RAkEL;
import mulan.classifier.transformation.LabelPowerset;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import mulan.examples.CrossValidationExperiment;
import weka.classifiers.trees.J48;
import weka.core.Utils;
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
		
		//System.out.println("step 2");
		//part.two.MainPartTwo.printMatrixToFile(X,new File ("X matrix.txt"),Global.geneToColumns,"The X Matrix");
		//part.two.MainPartTwo.printMatrixToFile(Y,new File("Y matrix.txt"),Global.labelToColumns, "The Y Matrix",tree.getRoot());
		
		System.out.println("DONE BUILD MATRIX");
		
		String [][] extractedPatientFull = part.two.ReadFromFile.readExportPatientFull(new File("extracted_export_patient_info_filtered.txt"));
		Set <String> temp = new HashSet<String>();
		Map <String,Integer> studyToFold = new HashMap<String, Integer>();
		for(int i=0; i< extractedPatientFull.length; i++) {
			if(extractedPatientFull[i][10].contains("pmid"))
				temp.add(extractedPatientFull[i][10]);
		}
		int count = 0;
		for(String str : temp) {
			studyToFold.put(str, count);
			count++;
		}
		int [] partition = new int [X.length];
		initArray(partition);
		Set <String> set = part.two.Global.sampleToRows.keySet();
		for(int j=0; j<partition.length; j++) {
			for(String str : set) {
				if (str.equals(extractedPatientFull[j][0])) {
					if(extractedPatientFull[j][10].contains("pmid"))
						partition[j] = studyToFold.get(extractedPatientFull[j][10]);
					break;
				}
			}
		}
		System.out.println("partition is ready");
		//part.one.CV.CVPredict(X, Y, partition, 1, 0.5, (int)Math.floor(Math.sqrt(extractedPatientFull.length)), 100000000, 10000000);
		arffGenerator.makeARFF("data.arff", X, Y);
		System.out.println("arff is done");
		xmlGenerator.makeXMlNoRelations("relations.xml", X, Y, tree);
		System.out.println("xml is done");
		
		try {

            System.out.println("Loading the dataset...");
            MultiLabelInstances dataset = new MultiLabelInstances("data.arff", "relations.xml");

            RAkEL learner1 = new RAkEL(new LabelPowerset(new J48()));
            MLkNN learner2 = new MLkNN();

            Evaluator eval = new Evaluator();
            MultipleEvaluation results;

            int numFolds = 10;
            //results = eval.crossValidate(learner1, dataset, numFolds);
           // System.out.println(results);
            results = eval.crossValidate(learner2, dataset, numFolds);
            System.out.println(results);
        } catch (InvalidDataFormatException ex) {
            Logger.getLogger(CrossValidationExperiment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrossValidationExperiment.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		System.out.println("DONE");
	}

	private static void initArray(int[] partition) {
		for(int i=0; i<partition.length;i++)
			partition[i] = 0;
	}
}
