import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/* the cross validation library*/
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
	public static double [][] getXTrain(double [][] X ,int [] partition, int numberOfTest) {
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
	public static int [][] getYTrain(int [][] Y ,int [] partition, int numberOfTest) {
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
	public static double [][] getXTest(double [][] X ,int [] partition, int numberOfTest) {
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
	public static int [][] getYTest(int [][] Y ,int [] partition, int numberOfTest) {
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
	/* get cross validation prediction matrix*/ 
	public static double [][] CVPredict(double [][] X,int [][] Y, int folds, int ntree, int lambda,int mtry, int sigma0,int n0) {
		int [] partition = CVPartition(folds, X.length);
		int pos;
		double [][] predict = new double [X.length][Y[0].length];
		for(int i=0;i<folds;i++) {
			Forest f = new Forest(); 
			AlgorithmUtils.BootstrapRF(getXTrain(X,partition,i), getYTrain(Y,partition,i), ntree, lambda, mtry, sigma0, n0, f);
			pos = 0;
			for(int j=0;j<getXTest(X,partition,i).length;j++) {
				while(pos < partition.length) {
					if(partition[pos] == i) {
						predict[pos] = f.RFPredict(getXTest(X,partition,i)[j], Y[0].length).clone();
						break;
					}
					pos++;
				}
			}
		}
		return predict;
	}
	/* get the CV precision*/
	public static double getPrecision(int [][] Y , double [][] predict, double threshold) {
		int [][] YGag = new int [predict.length][predict[0].length];
		for(int i=0;i<predict.length;i++)
			for(int j=0;j<predict[0].length;j++)
				YGag[i][j] = (predict[i][j] > threshold ? 1 : 0);
		int TP=0,FP=0;
		for(int i=0;i<predict.length;i++) {
			for(int j=0;j<predict[0].length;j++) {
				if(Y[i][j] == 1 && YGag[i][j] == 1)
					TP++;
				if(Y[i][j] == 0 && YGag[i][j] == 1)
					FP++;
			}
		}
		return TP/(TP+FP);
	}
	/* get the CV recall*/
	public static double getRecall(int [][] Y , double [][] predict, double threshold) {
		int [][] YGag = new int [predict.length][predict[0].length];
		for(int i=0;i<predict.length;i++)
			for(int j=0;j<predict[0].length;j++)
				YGag[i][j] = (predict[i][j] > threshold ? 1 : 0);
		int TP=0,FN=0;
		for(int i=0;i<predict.length;i++) {
			for(int j=0;j<predict[0].length;j++) {
				if(Y[i][j] == 1 && YGag[i][j] == 1)
					TP++;
				if(Y[i][j] == 1 && YGag[i][j] == 0)
					FN++;
			}
		}
		return TP/(TP+FN);
	}
	/* get the CV error*/
	public static double getError(int [][] Y , double [][] predict, double threshold) {
		int [][] YGag = new int [predict.length][predict[0].length];
		for(int i=0;i<predict.length;i++)
			for(int j=0;j<predict[0].length;j++)
				YGag[i][j] = (predict[i][j] > threshold ? 1 : 0);
		int FN=0,FP=0;
		for(int i=0;i<predict.length;i++) {
			for(int j=0;j<predict[0].length;j++) {
				if(Y[i][j] == 1 && YGag[i][j] == 0)
					FN++;
				if(Y[i][j] == 0 && YGag[i][j] == 1)
					FP++;
			}
		}
		return (FN+FP);  //NOT CORRECT RESULT
	}
	/* get the CV FPR*/
	public static double getFPR(int [][] Y , double [][] predict, double threshold) {
		int [][] YGag = new int [predict.length][predict[0].length];
		for(int i=0;i<predict.length;i++)
			for(int j=0;j<predict[0].length;j++)
				YGag[i][j] = (predict[i][j] > threshold ? 1 : 0);
		int TN=0,FP=0;
		for(int i=0;i<predict.length;i++) {
			for(int j=0;j<predict[0].length;j++) {
				if(Y[i][j] == 0 && YGag[i][j] == 0)
					TN++;
				if(Y[i][j] == 0 && YGag[i][j] == 1)
					FP++;
			}
		}
		return FP/(FP+TN);
	}
	
	
}
