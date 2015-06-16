package part.three;


import java.util.HashSet;
import java.util.Set;

import mulan.classifier.MultiLabelOutput;
import mulan.data.MultiLabelInstances;
import weka.core.Instance;
import weka.core.Instances;

public abstract class CVMulan {
//	public static String TEMP_ARFF_TRAIN = "temp_train.arff";
//	public static String TEMP_ARFF_TEST = "temp_test.arff";
	public abstract void train(MultiLabelInstances m) throws Exception;
	public abstract MultiLabelOutput predict(Instance Ins) throws Exception;
	public Instances toTrain;
	public Instances toTest;
	private Instances emptyDataSet;				// TODO
	private int [][] Y;
	
	// implemented
	public void runCV(double[][] x, int[][] y, int[] partition) throws Exception{
		int numOfFolds = 1+max(partition);
		int f;
		
		Set<Integer> testPos;
		Instances allData = (new MultiLabelInstances("data.arff", "relations.xml")).getDataSet();
		emptyDataSet = new Instances(allData);
		emptyDataSet.delete();
		Y = new int[x.length][x[0].length];
		
		
		// All partitions folds

		for(f=0; f<numOfFolds;f++){
			System.out.println("fold "+f+"/"+numOfFolds);
			testPos=leaveStudyOutPartition(allData, f, partition);
			train(new MultiLabelInstances(toTrain,"relations.xml"));			
			for(int j : testPos){
				MultiLabelOutput output =predict(toTrain.get(j));		// TODO
				boolean [] predicted_labels_bools = output.getBipartition();
				int [] predicted_labels = new int[predicted_labels_bools.length];
				for(int k=0; k<predicted_labels.length; k++)
					predicted_labels[k] = ((predicted_labels_bools[k]) == true ? 1 : 0);
				Y[j] = predicted_labels;
			}
		}
		
	}
	private Set<Integer> leaveStudyOutPartition(Instances allData, int study, int[] study_list){
		
		
		Instance currIns;
		Set<Integer> testPos=new HashSet<Integer>(); 
		int dataSize=allData.numInstances();
		toTrain=new Instances(emptyDataSet);
		toTest=new Instances(emptyDataSet);
		
		for(int i=0; i<dataSize;i++){
			currIns=allData.get(i);
			if(study_list[i]==study){
				toTest.add(currIns);
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
