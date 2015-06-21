package part.three;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import part.*;
import part.one.AlgorithmUtils;
import part.one.CV;
import part.one.ClusteringTree;
import part.one.Forest;
import part.one.GraphingData;
import part.two.CreateXMatrix;
import part.two.CreateYMatrix;
import part.two.DiseaseHierarchy;
import part.two.Global;
import part.two.ReadFromFile;
import mulan.classifier.lazy.BRkNN;
import mulan.classifier.lazy.MLkNN;
import mulan.classifier.meta.RAkEL;
import mulan.classifier.meta.thresholding.MLPTO;
import mulan.classifier.neural.BPMLL;
import mulan.classifier.neural.MMPLearner;
import mulan.classifier.transformation.AdaBoostMH;
import mulan.classifier.transformation.LabelPowerset;
import mulan.classifier.transformation.PPT;
import mulan.data.ConverterLibSVM;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.Evaluator;
import mulan.evaluation.MultipleEvaluation;
import mulan.examples.CrossValidationExperiment;
import weka.classifiers.trees.J48;
import weka.core.Utils;
public class MainPartThree {
	
// extract specific colums out of the matrix
	private static int [] extractcolumn(int [][] a, int column) {
		int [] ans = new int [a.length];
		for(int i=0; i<a.length; i++)
			ans[i] = a[i][column];
		return ans;
	}
	// extract specific colums out of the matrix
	private static double [] extractcolumn(double [][] a, int column) {
		double [] ans = new double [a.length];
		for(int i=0; i<a.length; i++)
			ans[i] = a[i][column];
		return ans;
	}
	public static void main(String[] args) throws Exception {
		String s = "Cosmic_slice3_3.txt";
		File file = new File(s);
		
		String [][] cosmic =ReadFromFile.readFromFile(file);
		Global.initVars(cosmic);
		DiseaseHierarchy tree = new DiseaseHierarchy("fixed_disease_ontology_data.xlsx");
		Global.initTier(tree.getRoot());
		double [][] X = CreateXMatrix.createTheMatrix(cosmic);
		
		String [][] extractedPatient = ReadFromFile.readExportPatient(new File("extracted_export_patient_info_filtered.txt"));

		int [][] Y = CreateYMatrix.createTheMatrix(extractedPatient, tree.getRoot());
		
		//System.out.println("print matrices");
		//part.two.MainPartTwo.printMatrixToFile(X,new File ("X matrix.txt"),Global.geneToColumns,"The X Matrix");
		//part.two.MainPartTwo.printMatrixToFile(Y,new File("Y matrix.txt"),Global.labelToColumns, "The Y Matrix",tree.getRoot());
		
		System.out.println("DONE BUILD MATRIX");
		
		String [][] extractedPatientFull = part.two.ReadFromFile.readExportPatientFull(new File("extracted_export_patient_info_filtered.txt"));
		Set <String> temp = new HashSet<String>();
		Map <String,Integer> studyToFold = new HashMap<String, Integer>();
		for(int i=0; i< extractedPatientFull.length; i++) {
			if(extractedPatientFull[i][10].contains("pmid")) {
				temp.add(extractedPatientFull[i][10]);
			}
		}
		int count = 0;
		for(String str1 : temp) {
			studyToFold.put(str1, (int)count/12);
			count++;
		}
		
		int [] partition = new int [X.length];
		initArray(partition);
		for(int j=0; j<partition.length; j++) {
			Set <String> set = part.two.Global.sampleToRows.keySet();
			for(String str : set) {
				if(Global.sampleToRows.get(str) == j) {
					for(int k=0; k<extractedPatientFull.length; k++) {
						if(extractedPatientFull[k][0].equals(str) && extractedPatientFull[k][10].contains("pmid")) {
							partition[j] = studyToFold.get(extractedPatientFull[k][10]);
							break;
						}
					}
					break;
				}
			}
		}
		System.out.println("partition is ready");
		System.out.println("num of folds is: "+max(partition));
		int [] genes_counter = new int [Global.geneToColumns.size()];
		initArray(genes_counter);
		double [][] randomForestPrediction = part.one.CV.CVPredict(X, Y, partition, 10, 0.8, 100, 0, 70,genes_counter);
		System.out.println("Done random forest CV");
		int maxIndex = getMax(genes_counter);
		Set<String> geneSet = Global.geneToColumns.keySet();
		for( String g : geneSet) {
			if(Global.geneToColumns.get(g) == maxIndex)
				System.out.println("gene "+g+ " is most used and its used "+genes_counter[maxIndex]+" times");
		}
		
		arffGenerator.makeARFF("data.arff", X, Y);
		System.out.println("arff is done");
		xmlGenerator.makeXMlNoRelations("relations.xml", X, Y, tree);
		System.out.println("xml is done");
		
		try {

            System.out.println("Loading the dataset...");
            MultiLabelInstances dataset = new MultiLabelInstances("data.arff", "relations.xml");

            PPT learner1 = new PPT();
            BRkNN learner2 = new BRkNN();
            
            CV_PPT cvPPT = new CV_PPT(learner1);
            System.out.println("PPT CV");
           //double [][] perceptronPrediction =  cvPPT.runCV(X, Y, partition,2);
           
            System.out.println("KNN CV");
            CV_kNN cvknn = new CV_kNN(learner2);
            //double [][] knnPrediction = cvknn.runCV(X, Y, partition,1);
           
           System.out.println("AUC matrix");
           
           double [][] aucMatrix = new double [3][randomForestPrediction[0].length]; // each row is algorithm, each column is label
           for(int j=0; j<randomForestPrediction[0].length; j++) {
        	   aucMatrix[0][j] = CV.AUCcurve(extractcolumn(Y, j), extractcolumn(randomForestPrediction, j), true); // random forest
        	   if(aucMatrix[0][j]>0.85){
        		   draw_ROC("GOOD DOID_"+Global.ColumnsToLabel.get(j)+"_curveRF", extractcolumn(Y, j), extractcolumn(randomForestPrediction,j));
        	   }
        	   if(aucMatrix[0][j]<0.6){
        		   draw_ROC("BAD DOID_"+Global.ColumnsToLabel.get(j)+"_curveRF", extractcolumn(Y, j), extractcolumn(randomForestPrediction,j));
        	   }
        	   //aucMatrix[1][j] = CV.AUCcurve(extractcolumn(Y, j), extractcolumn(knnPrediction, j), true); // KNN
        	   //aucMatrix[2][j] = CV.AUCcurve(extractcolumn(Y, j), extractcolumn(perceptronPrediction, j), true); // PPT
           }
           
           writeToFile(aucMatrix,"aucMatrix.txt");
           
           System.out.println("AUPR matrix");
           double [][] auprMatrix = new double [3][randomForestPrediction[0].length]; // each row is algorithm, each column is label
          
           for(int j=0; j<randomForestPrediction[0].length; j++) {
        	   auprMatrix[0][j] = CV.AUCcurve(extractcolumn(Y, j), extractcolumn(randomForestPrediction, j), false); // random forest
        	   //auprMatrix[1][j] = CV.AUCcurve(extractcolumn(Y, j), extractcolumn(knnPrediction, j), false); // KNN
        	  // auprMatrix[2][j] = CV.AUCcurve(extractcolumn(Y, j), extractcolumn(perceptronPrediction, j), false); // PPT
           }
           
           writeToFile(auprMatrix,"auprMatrix.txt");
           
           
        } catch (InvalidDataFormatException ex) {
            Logger.getLogger(CrossValidationExperiment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrossValidationExperiment.class.getName()).log(Level.SEVERE, null, ex);
        }
		
		/*Forest forest = new Forest();
		int [] genes_counter1 = new int [Global.geneToColumns.size()];
		initArray(genes_counter1);
		System.out.println("build forest");
		genes_counter1 = AlgorithmUtils.BootstrapRF(X, Y, 50, 0.8, 70, 0, 40, forest);
		System.out.println("done build forest");
		Set<String> geneSet1 = Global.geneToColumns.keySet();
		for(int i=0; i<10; i++) {
			int maxIndex = getMax(genes_counter1);
			for( String g : geneSet1) {
				if(Global.geneToColumns.get(g) == maxIndex)
					System.out.println(g);
			}
			genes_counter1[maxIndex] = Integer.MIN_VALUE;
		}*/
		System.out.println("DONE");
	}

	private static void draw_ROC(String name, int[] correct, double[] confidences) {
		double [][] XandY = CV.makeArraysForGraphsAndAUC(correct, confidences, true);
		double [] plotX = XandY[0];
		double [] plotY = XandY[1];
		for(int i=0; i<plotX.length; i++) {
			plotX[i] = Double.parseDouble(new DecimalFormat("##.###").format(plotX[i]));
			plotY[i] = Double.parseDouble(new DecimalFormat("##.###").format(plotY[i]));
		}
		GraphingData graph1 = new GraphingData(plotX, plotY, 400, 400, name);
	}
	private static void writeToFile(double[][] aucMatrix, String filename) throws IOException {
		File f=new File(filename);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		fos.getChannel().truncate(0);
		Set <String> keys1 = Global.labelToColumns.keySet();
		String [] keys = new String[keys1.size()];
		int countPos = 0;
		for(String key: keys1) {
			keys[countPos] = key;
			countPos++;
		}
		for(int y=0; y<keys.length; y++)
			bw.write(keys[y]+"\t");
		bw.newLine();
		for( int i=0; i<aucMatrix.length; i++) {
			for(int j=0; j<keys.length; j++) {
				bw.write(Double.toString(aucMatrix[i][Global.labelToColumns.get(keys[j])])+"\t");
			}
			bw.newLine();
		}
		
		bw.close();
		fos.close();
	}
	private static void initArray(int[] partition) {
		for(int i=0; i<partition.length;i++)
			partition[i] = 0;
	}
	
	//rank the features by times they are used
	private static void rankFeatures(int[] count_featrues) {
		int maxIndex;
		for(int i=0; i<count_featrues.length; i++) {
			maxIndex = getMax(count_featrues);
			count_featrues[maxIndex] = Integer.MIN_VALUE;
		}
	}
	// get the index of the maximum number
	private static int getMax(int[] count_featrues) {
		int max = count_featrues[0];
		int index = 0;
		for(int i=1; i<count_featrues.length; i++) {
			if(max < count_featrues[i]) {
				max = count_featrues[i];
				index = i;
			}
		}
		return index;
	}
	
	private static int max(int[] arr) {
		int m=arr[0];
		for(int i=0; i<arr.length;i++){
			m=Math.max(m, arr[i]);
		}
		return m;
	}
}
