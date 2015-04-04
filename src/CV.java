import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;


public class CV {
	
	/* make an equal partition of the data into numOfFolds groups*/
	public static int [] CVPartition (int numOfFolds, int numOfSamples) {
		int [] partition = new int [numOfSamples];
		Random rnd = new Random();
		List<Integer> S = new ArrayList<Integer>();
		int count;
		for(int i=0;i<numOfFolds;i++) {
			count = 0;
			while(count<numOfSamples/numOfFolds) {
				S.add(i); 
				count++;
			}
		}
		while(S.size() < numOfSamples) {
			S.add(rnd.nextInt(numOfFolds));
		}
		Collections.shuffle(S, rnd);
		for(int i=0;i<numOfSamples;i++)
			partition[i] = S.get(i);
		return partition;
	}
	/* given the partition, extract the train data */
	public double [][] getXTrain(double [][] X ,int [] partition, int numberOfTest) {
		int numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] != numberOfTest)
				numOfRows++;
		
		double [][] ans = new double [numOfRows][X[0].length];
		for(int i=0;i<partition.length;i++)
			if(partition[i] != numberOfTest)
				ans[i] = X[i].clone();
		return ans;
	}
	/* given the partition, extract the train labels */
	public int [][] getYTrain(int [][] Y ,int [] partition, int numberOfTest) {
		int numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] != numberOfTest)
				numOfRows++;
		
		int [][] ans = new int [numOfRows][Y[0].length];
		for(int i=0;i<partition.length;i++)
			if(partition[i] != numberOfTest)
				ans[i] = Y[i].clone();
		return ans;
	}
	/* given the partition, extract the test data */
	public double [][] getXTest(double [][] X ,int [] partition, int numberOfTest) {
		int numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] == numberOfTest)
				numOfRows++;
		
		double [][] ans = new double [numOfRows][X[0].length];
		for(int i=0;i<partition.length;i++)
			if(partition[i] == numberOfTest)
				ans[i] = X[i].clone();
		return ans;
	}
	/* given the partition, extract the train labels */
	public int [][] getYTest(int [][] Y ,int [] partition, int numberOfTest) {
		int numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] == numberOfTest)
				numOfRows++;
		
		int [][] ans = new int [numOfRows][Y[0].length];
		for(int i=0;i<partition.length;i++)
			if(partition[i] == numberOfTest)
				ans[i] = Y[i].clone();
		return ans;
	}
	
	public void getCVError(double [][] X,int [][] Y, int folds, int ntree, int lambda,int mtry, int sigma0,int n0) {
		int [] partition = CVPartition(folds, X.length);
		double [] predict;
		for(int i=0;i<folds;i++) {
			Forest f = new Forest(); 
			AlgorithmUtils.BootstrapRF(getXTrain(X,partition,i), getYTrain(Y,partition,i), ntree, lambda, mtry, sigma0, n0, f);
			for(int j=0;j<getXTest(X,partition,i).length;j++) {
				predict = f.RFPredict(getXTest(X,partition,i)[j], Y[0].length);
				
			}
		}
	}
	
}
