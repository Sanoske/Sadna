package part.one;
import java.lang.management.PlatformManagedObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.integration.SimpsonIntegrator;
import org.apache.commons.math3.analysis.integration.TrapezoidIntegrator;
import org.apache.commons.math3.analysis.interpolation.LinearInterpolator;
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
			int len = getXTest(X,partition,i).length;
			for(int j=0;j<len;j++) {
				while(pos < partition.length) {
					if(partition[pos] == i) {
						predict[pos] = f.RFPredict(getXTest(X,partition,i)[j], Y[0].length).clone();
						pos++;
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
			YGag[i] = (predict[i] >= threshold ? 1 : 0);
		double TP=0,FP=0, FN = 0, TN=0;
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
	// compute the AUCcurve integral
	@SuppressWarnings("static-access")
	public static double AUCcurve(int [] Y, double [] predict, boolean roc) {
		double [] thrs = predict.clone();
		Arrays.sort(thrs);
		List <Double> plotX_temp = new LinkedList<Double>();
		List <Double> plotY_temp = new LinkedList<Double>();
		double[] sps = new double[4];
		Set <Double> s = new HashSet<Double>();
		for(double k : thrs)
			s.add(k);
		
		for(double i : s) {
			sps = SimplePerformanceScores(Y,predict,i);
			if (roc) {
				if(!contain(sps[FPR],plotX_temp)) {
						plotX_temp.add(sps[FPR]);
						plotY_temp.add(sps[recall]);
					}
			}
			else {
				if(!contain(sps[recall],plotX_temp)) {
					plotX_temp.add(sps[recall]);
					plotY_temp.add(sps[precision]);
				}
			}
		}
		
		double [] plotX = new double [plotX_temp.size()];
		double [] plotY = new double [plotX_temp.size()];
		for(int j=0; j<plotX.length; j++) {
			plotX[j] = plotX_temp.get(j);
			plotY[j] = plotY_temp.get(j);
		}
		sortAccordingToX(plotX,plotY);
		UnivariateInterpolator interpolator = new LinearInterpolator();
		UnivariateFunction function = interpolator.interpolate(plotX, plotY); //interpolating the plot to a function using cubic spline
		SimpsonIntegrator integrator = new SimpsonIntegrator();
		double auc = integrator.integrate(Integer.MAX_VALUE, function, plotX[0], plotX[plotX.length-1]);
		return auc;
	}
	// sort x coordinate, and match the y coordinate according to it
	private static void sortAccordingToX(double[] plotX, double[] plotY) {
		double temp;
		for(int i=0; i<plotX.length-1; i++) {
			for(int j=i+1; j<plotX.length; j++){
				if(plotX[i] > plotX[j]) {
					temp = plotX[i];
					plotX[i] = plotX[j];
					plotX[j] = temp;
					temp = plotY[i];
					plotY[i] = plotY[j];
					plotY[j] = temp;
				}
			}
		}
	}
	// check if d is in plotX
	private static boolean contain(double d, List <Double> plotX) {
		for( double num : plotX)
			if(num == d)
				return true;
		return false;
	}
}
