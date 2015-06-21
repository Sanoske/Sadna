package part.three;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import mulan.classifier.lazy.BRkNN;
import mulan.classifier.lazy.MLkNN;
import mulan.data.InvalidDataFormatException;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.*;
import mulan.evaluation.measure.MacroAUC;
import mulan.evaluation.measure.Measure;
import mulan.examples.CrossValidationExperiment;
import part.*;
import part.one.CV;
import part.two.DiseaseHierarchy;
import part.two.Global;
import weka.classifiers.Evaluation;
import weka.core.Instance;
import weka.core.Instances;
public class test_cv {

	private static void makeARFF(String fileName, double[][] x, int[][] y, int numOfLabels) throws IOException{
		File f=new File(fileName);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		Scanner scan = new Scanner(new File("emotions.csv"));
		String currLine = scan.nextLine();
		String [] row_temp = currLine.split("\t");
		String [] row = new String [row_temp.length-6];
		for(int k=0; k<row.length; k++)
			row[k] = row_temp[k];
		fos.getChannel().truncate(0);
		bw.write("@relation workshopARFF");
		bw.newLine();
		bw.newLine();
		for(String str : row) {
			bw.write("@attribute "+str+" numeric");
			bw.newLine();
		}
		for(int i=0; i<numOfLabels; i++) {
			bw.write("@attribute "+"label"+i+" {0,1}");
			bw.newLine();
		}
		
		bw.write("@data");
		bw.newLine();
		bw.newLine();
		String s="";
		for(int i=0;i<x.length;i++){	// i is the row
			for(int j=0;j<x[i].length;j++){
				s+=(x[i][j]);
				s+=",";
			}
			for(int j=0;j<y[i].length;j++){
				s+=(int)(y[i][j]);
				if(j<y[i].length-1){
					s+=",";
				}else{
					bw.write(s);
					bw.newLine();
					s="";
				}
			}
		}
		bw.close();
		fos.close();
	}
	
	private static void makeXML (String filename, double[][] x, int[][] y, int numOfLabels) throws IOException {
		File f=new File(filename);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		bw.newLine();
		bw.write("<labels xmlns=\"http://mulan.sourceforge.net/labels\">");
		bw.newLine();
		
		for(int i=0; i<numOfLabels; i++) {
			bw.write("<label name=\"label"+i+"\">" + "</label>");
			bw.newLine();
		}
		bw.write("</labels>");
		bw.close();
		fos.close();
	}
	
	// extract specific colums out of the matrix
	public static int [] extractcolumn(int [][] a, int column) {
		int [] ans = new int [a.length];
		for(int i=0; i<a.length; i++)
			ans[i] = a[i][column];
		return ans;
	}
	// extract specific colums out of the matrix
	public static double [] extractcolumn(double [][] a, int column) {
		double [] ans = new double [a.length];
		for(int i=0; i<a.length; i++)
			ans[i] = a[i][column];
		return ans;
	}
		
	public static void main(String[] args) {
		try {
			int num_of_labels = 6;
			double[][] features_and_labels = part.one.Main.readCSV(new File("emotions.csv"));
			double [][] features = new double [features_and_labels.length][features_and_labels[0].length-num_of_labels];
			int [][] labels = new int [features_and_labels.length][num_of_labels]; 
			for(int i=0;i<features_and_labels.length;i++)
				for(int j=0;j<features_and_labels[i].length - num_of_labels;j++)
					features[i][j] = features_and_labels[i][j];
			
			for(int i=0;i<features_and_labels.length;i++)
				for(int j=0;j<num_of_labels;j++)
					labels[i][j] = (int)features_and_labels[i][j + features_and_labels[i].length - num_of_labels];
			makeARFF("data.arff", features, labels,num_of_labels);
			System.out.println("arff done");
			makeXML("relations.xml", features, labels, num_of_labels);
			System.out.println("xml done");
			CV_kNN k=new CV_kNN(new BRkNN());
			double [][] predict = k.runCV(features, labels, CV.CVPartition(10, features.length),3);
			System.out.println("AUC matrix");
	           double [][] aucMatrix = new double [2][predict[0].length]; // each row is algorithm, each column is label
	           
	           for(int j=0; j<predict[0].length; j++) {
	        	   aucMatrix[0][j] = CV.AUCcurve(extractcolumn(labels, j), extractcolumn(predict, j), false);
	        	   aucMatrix[1][j] = CV.AUCcurve(extractcolumn(labels, j), extractcolumn(predict, j), false);
	           }
	           writeToFile(aucMatrix);
		}
		catch (InvalidDataFormatException ex) {
            Logger.getLogger(CrossValidationExperiment.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(CrossValidationExperiment.class.getName()).log(Level.SEVERE, null, ex);
        }
		System.out.println("DONE");
	}
	
	private static void writeToFile(double[][] aucMatrix) throws IOException {
		File f=new File("testEmotions.txt");
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		fos.getChannel().truncate(0);
		
		for( int i=0; i<aucMatrix.length; i++) {
			for(int j=0; j<aucMatrix[0].length; j++) {
				bw.write(Double.toString(aucMatrix[i][j])+"\t");
			}
			bw.newLine();
		}
		
		bw.close();
		fos.close();
	}

}
