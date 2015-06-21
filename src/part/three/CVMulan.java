package part.three;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashSet;
import java.util.Set;

import part.two.Global;
import mulan.classifier.MultiLabelOutput;
import mulan.data.MultiLabelInstances;
import mulan.evaluation.measure.MacroAUC;
import weka.core.Instance;
import weka.core.Instances;

public abstract class CVMulan {
//	public static String TEMP_ARFF_TRAIN = "temp_train.arff";
//	public static String TEMP_ARFF_TEST = "temp_test.arff";
	public abstract void train(MultiLabelInstances m) throws Exception;
	public abstract MultiLabelOutput predict(Instance Ins) throws Exception;
	public Instances toTrain;
	public Instances toTest;
	private Instances emptyDataSet;
	private double [][] Y;
	
	// implemented
	public double [][] runCV(double[][] x, int[][] y, int[] partition,int Algorithm) throws Exception{
		int numOfFolds = 1+max(partition);
		int f;
		
		Instances allData = (new MultiLabelInstances("data.arff", "relations.xml")).getDataSet();
		emptyDataSet = new Instances(allData);
		emptyDataSet.delete();
		toTest = new Instances(allData);
		Y = new double[x.length][x[0].length];
		
		// All partitions folds

		for(f=0; f<numOfFolds;f++){
			System.out.println("fold "+(f+1)+"/"+(numOfFolds));
			Set<Integer> testPos=leaveStudyOutPartition(allData, f, partition);
			train(new MultiLabelInstances(toTrain,"relations.xml"));			
			for(int j: testPos){
				MultiLabelOutput output =predict(toTest.get(j));
				if(!output.hasConfidences())
					System.out.println("no confidence");
				Y[j] = output.getConfidences();
			}
		}
		return Y;
	}
		
	
	private double[] score_arr(double[] predicted_labels, double[] confidences) {
		double [] arr = new double [predicted_labels.length];
		for(int i=0;i<predicted_labels.length;i++){
			if( predicted_labels[i] == 1)
				arr[i] = confidences[i];
			else
				arr[i] = confidences[i];
		}
		return arr;
	}
	private Set<Integer> leaveStudyOutPartition(Instances allData, int study, int[] study_list){
		
		
		Instance currIns;
		Set<Integer> testPos=new HashSet<Integer>(); 
		int dataSize=allData.numInstances();
		toTrain=new Instances(emptyDataSet);
		
		for(int i=0; i<dataSize;i++){
			currIns=allData.get(i);
			if(study_list[i]==study){
				testPos.add(i);
			}else{
				toTrain.add(currIns);
			}
		
		}
		return testPos;
	}
	
	
	private static int max(int[] arr) {
		int m=arr[0];
		for(int i=0; i<arr.length;i++){
			m=Math.max(m, arr[i]);
		}
		return m;
	}


}
