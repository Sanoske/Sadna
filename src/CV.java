import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

/* the cross validation library*/
public class CV {
	
	private static final int precision = 0, recall = 1,error = 2, FPR = 3;
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
		numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] != numberOfTest) {
				ans[numOfRows] = X[i].clone();
				numOfRows++;
			}
		return ans;
	}
	/* given the partition, extract the train labels */
	public static int [][] getYTrain(int [][] Y ,int [] partition, int numberOfTest) {
		int numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] != numberOfTest)
				numOfRows++;
		
		int [][] ans = new int [numOfRows][Y[0].length];
		numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] != numberOfTest) {
				ans[numOfRows] = Y[i].clone();
				numOfRows++;
			}
		return ans;
	}
	/* given the partition, extract the test data */
	public static double [][] getXTest(double [][] X ,int [] partition, int numberOfTest) {
		int numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] == numberOfTest)
				numOfRows++;
		
		double [][] ans = new double [numOfRows][X[0].length];
		numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] == numberOfTest) {
				ans[numOfRows] = X[i].clone();
				numOfRows++;
			}
		return ans;
	}
	/* given the partition, extract the train labels */
	public static int [][] getYTest(int [][] Y ,int [] partition, int numberOfTest) {
		int numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] == numberOfTest)
				numOfRows++;
		
		int [][] ans = new int [numOfRows][Y[0].length];
		numOfRows = 0;
		for(int i=0;i<partition.length;i++)
			if(partition[i] == numberOfTest) {
				ans[numOfRows] = Y[i].clone();
				numOfRows++;
			}
		return ans;
	}
	/* get cross validation prediction matrix*/ 
	public static double [][] CVPredict(double [][] X,int [][] Y, int folds, int ntree, double lambda,int mtry, int sigma0,int n0) {
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
	/* get the CV precision, recall , error and FPR*/
	public static double [] SimplePerformanceScores(int [] Y , double [] predict, double threshold) {
		int [] YGag = new int [predict.length];
		for(int i=0;i<predict.length;i++)
			YGag[i] = (predict[i] > threshold ? 1 : 0);
		int TP=0,FP=0, FN = 0, TN=0;
		for(int i=0;i<predict.length;i++) {
			if(Y[i] == 1 && YGag[i] == 1)
				TP++;
			if(Y[i] == 0 && YGag[i] == 1)
				FP++;
			if(Y[i] == 1 && YGag[i] == 0)
				FN++;
			if(Y[i] == 0 && YGag[i] == 0)
				TN++;
		}
		double [] ans = new double [4];
		ans[precision] = TP/(TP+FP);
		ans[recall] = TP/(TP+FN);
		ans [error] = (FN+FP)/Y.length;
		ans [FPR] = FP/(FP+TN);
		return ans;
	}
	
	public static double AUCcurve(int [] Y, double [] predict, boolean roc) {
		double [] thrs = predict.clone();
		Arrays.sort(thrs);
		double[] plotX = new double[thrs.length];
		double[] plotY = new double[thrs.length];
		double[] sps = new double[4];
		for(int i=0;i<thrs.length;i++) {
			sps = SimplePerformanceScores(Y,predict,thrs[i]);
			if (roc) {
				plotX[i] = sps[recall];
				plotY[i] = sps[FPR];
			}
			else {
				plotX[i] = sps[precision];
				plotY[i] = sps[recall];
			}
		}
		UnivariateInterpolator interpolator = new SplineInterpolator();
		UnivariateFunction function = interpolator.interpolate(plotX, plotY); //interpolating the plot to a function using cubic spline
		SimpsonIntegrator integrator = new SimpsonIntegrator();
		
		double auc = integrator.integrate(10, function, getMinMax(plotX,"min"), getMinMax(plotX,"max"));
		return auc;
	}
	//a method to get a min/max value of an array
	private static double getMinMax(double[] plotX, String minmax) {
		double value = plotX[0];
		for(int i=1;i<plotX.length;i++) {
			if (minmax.equals("min") && plotX[i]<value) {
				value = plotX[i];
			}
			if (minmax.equals("max") && plotX[i]>value) {
				value = plotX[i];
			}
		}
		return value;
	}
}
