import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;


public class AlgorithmUtils {
	// pick numOfNumbers random numbers from 0 to range-1 with no repetitions (needed in RPCTNode)
	private static int[] pickRandomNumbers(int range, int numOfNumbers) {
		Random rnd = new Random();
		int [] ans = new int [numOfNumbers];
		List<Integer> S = new ArrayList<Integer>();
		for (int i = 0; i < range; i++) {
	        S.add(i);
	    }
		Collections.shuffle(S, rnd);
		for(int i=0;i<numOfNumbers;i++)
			ans[i] = S.get(i);
		return ans;
	}

	private static double[][] concatenate(double[][] x, int[][] y) {
		int len = x[0].length+y[0].length;
		double [][] ans = new double [x.length][len];
		for(int i=0;i<x.length;i++)
			for(int j=0;j<x[0].length;j++)
				ans[i][j] = x[i][j];
		for(int i=0;i<x.length;i++)
			for(int j=0;j<y[0].length;j++)
				ans[i][j+x[0].length] = y[i][j];
		return ans;
	}
	
	// compute the  Euclidian distance between 2 vectors
	private static double distanceSquare(int [] x1, double [] x2) {
		double result = 0;
		for(int i=0;i<x1.length;i++)
			result+=Math.pow((x1[i]-x2[i]),2);
		return result;
	}
	// compute the  Euclidian distance between 2 vectors
		private static double distanceSquare(double [] x1, double [] x2) {
			double result = 0;
			for(int i=0;i<x1.length;i++)
				result+=Math.pow((x1[i]-x2[i]),2);
			return result;
		}
	//  compute the mean over the columns of  matrix y
	private static double [] YGag(int [][] y) {
		double [] ans = new double [y[0].length];
		for(int j=0;j<ans.length;j++) {
			ans[j] = 0;
			for(int i=0;i<y.length;i++) {
				ans[j]+= y[i][j];
			}
			ans[j] = ans[j] / y.length;
		}
		return ans;
	}
//  compute the mean over the columns of  matrix y
	private static double [] YGag(double [][] y) {
		double [] ans = new double [y[0].length];
		for(int j=0;j<ans.length;j++) {
			ans[j] = 0;
			for(int i=0;i<y.length;i++) {
				ans[j]+= y[i][j];
			}
			ans[j] = ans[j] / y.length;
		}
		return ans;
	}
	// compute the variance of matrix y
	private static double var(int[][] y) {
		double ans = 0;
		double [] yGag = YGag(y);
		for(int i=0;i<y.length;i++)
			ans+=distanceSquare(y[i], yGag);
		ans = ans / y.length;
		return ans;
	}
	// compute the variance of matrix y
		private static double var(double[][] y) {
			double ans = 0;
			double [] yGag = YGag(y);
			for(int i=0;i<y.length;i++)
				ans+=distanceSquare(y[i], yGag);
			ans = ans / y.length;
			return ans;
		}
	// number of rows in matrix x
	private static int nrow(double[][] x) {
		return x.length;
	}
	// build RPCT 
	private static Node RPCTNode (double [][] X , int [][] Y ,int mtry, int sigma0, int n0 ) {
		Node np = new Node(-1, -1, null);
		if (nrow(X) < n0 || var(Y) < sigma0) {
			int [][] labels = new int [Y.length][];
			for( int k=0;k<Y.length;k++)
				labels[k] = Y[k].clone();
			np.setLables(labels);
			return np;
		}
		int [] fs = pickRandomNumbers(X[0].length,mtry);
		BP best = bestPartition(X,Y,fs);
		Node nc = RPCTNode(best.getX1(),best.getY1(),mtry,sigma0,n0);
		np.setLeftSon(nc);
		Node nd = RPCTNode(best.getX2(),best.getY2(),mtry,sigma0,n0);
		np.setRightSon(nd);
		np.setFeatureNumber(best.getF());
		np.setTrheshold(best.getThreshold());
		return np;
	}
	// given set of features,finds the featrue with the best partition and computes the partition
	private static BP bestPartition(double[][] x_start, int [][] y_start, int[] fs) {
		double threshold;
		double h;
		HashMap<Integer, double [][]> ps;
		BP best = new BP();
		int numOfSamples = (x_start.length > 50 ? 50 : x_start.length);
		for(int i=0; i<3; i++) {
			int [] samples = pickRandomNumbers(x_start.length, numOfSamples);
			double [][] x = new double [numOfSamples][x_start[0].length];
			int [][] y = new int [numOfSamples][y_start[0].length];
			//now we work with only 50 samples
			for( int j=0; j<numOfSamples; j++) {
				x[j] = x_start[samples[j]].clone();
				y[j] = y_start[samples[j]].clone();
			}
			
			for (int f : fs) {
				x = sortWithRespectToFeature(x,f,y);
				for( int k=0; k< x.length - 1; k++) {
					threshold = (x[k][f]+x[k+1][f])/2;
					ps = binaryPartitions(f,x,y,threshold);
					if(ps.get(0) == null || ps.get(1) == null)
						continue;
					double [][] x_small = extractXMatrix(ps.get(0),x[0].length);
					int [][] y_small = extractYMatrix(ps.get(0),x[0].length, ps.get(0)[0].length);
					double [][] x_big = extractXMatrix(ps.get(1), x[0].length);
					int [][] y_big = extractYMatrix(ps.get(1), x[0].length, ps.get(1)[0].length);
					h = y_small.length*var(y_small) + y_big.length*var(y_big);
					h = h/ x.length;
					if (h<best.getGain()) {
						best.setX1(x_small);
						best.setX2(x_big);
						best.setY1(y_small);
						best.setY2(y_big);
						best.setF(f);
						best.setGain(h);
						best.setThreshold(threshold);
					}
				}
			}
		}
		return best;
	}
	public static int[][] extractYMatrix(double[][] ds, int start, int end) {
		int [][] ans = new int [ds.length][end-start+1];
		for(int i=0; i<ds.length; i++)
			for(int j=start; j<end; j++)
				ans[i][j-start] = (int) ds[i][j];
		return ans;
	}

	public static double[][] extractXMatrix(double[][] ds, int length) {
		double [][] ans = new double [ds.length][length];
		for(int i=0; i<ds.length; i++)
			for( int j=0; j<length; j++)
				ans[i][j] = ds[i][j];
		return ans;
	}

	// sort the matrix x with respect to feature number f
	public static double [][] sortWithRespectToFeature(double[][] x, int f, int [][] y) {
		double [][] result = new double [x.length][x[0].length];
		int [][] y_result = new int [y.length][y[0].length];
		int min;
		for( int i=0; i < x.length; i++) {
			min = getMin(x,f);
			result[i] = x[min].clone();
			y_result[i] = y[min].clone();
			x[min][f] = Double.MAX_VALUE;
		}
		for(int j=0; j<y.length; j++)
			y[j] = y_result[j].clone();
		return result;
	}
	// returns the index of the minimum value in column number f
	private static int getMin(double[][] x, int f) {
		double min = x[0][f];
		int minIndex = 0;
		for( int i=1; i < x.length ; i++)
			if( x[i][f] < min) {
				min = x[i][f];
				minIndex = i;
			}
		return minIndex;
	}

	//Binary split X into 2 matrices, and returns them
	private static HashMap<Integer, double [][]> binaryPartitions(int f, double[][] x,int [][] y, double threshold) {
		HashMap<Integer, double [][]> partition = new HashMap<Integer, double[][]>();
		List<Integer> leftTlist = new ArrayList<Integer>();
		List<Integer> rightTlist = new ArrayList<Integer>();
		for(int i=0;i<x.length;i++) {
			if (x[i][f]<threshold) {
				leftTlist.add(i);
			}
			else {
				rightTlist.add(i);
			}
		}
		double [][] small = new double [leftTlist.size()][x[0].length];
		int [][] y_small = new int [leftTlist.size()][y[0].length];
		int count = 0;
		for( int j : leftTlist) {
			small[count] = x[j].clone();
			y_small[count] = y[j].clone();
			count++;
		}
		double [][] big = new double [rightTlist.size()][x[0].length];
		int [][] y_big = new int [rightTlist.size()][y[0].length];
		count = 0;
		for( int j : rightTlist) {
			big[count] = x[j].clone();
			y_big[count] = y[j].clone();
			count++;
		}
		if( leftTlist.size() != 0) {
			double [][] i_small = concatenate(small,y_small);
			partition.put(0, i_small);
		}
		if (rightTlist.size() != 0) {
			double [][] i_big = concatenate(big, y_big);
			partition.put(1, i_big);
		}
		return partition;
	}
	// creates the root of the clustering tree and assigns it to a tree
	public static ClusteringTree RPCT (double [][] X , int [][] Y ,int mtry, int sigma0, int n0 ) {
		Node root = RPCTNode(X, Y, mtry, sigma0, n0);
		ClusteringTree tree = new ClusteringTree(root);
		return tree;
	}
	
	/* creating RandomForest. the Function returns how many times each feature is used in the forest (array of int).
	 * the last input is an empty forest that the function fills with trees. */
	public static int [] BootstrapRF (double [][] X, int [][] Y, int ntree,double lambda,int mtry,int sigma0,int n0, Forest forest) {
		int n = (int) (nrow(X) * lambda);
		for(int i=0;i<ntree;i++) {
			int [] s = pickRandomNumbersWithReplacement(X.length, n);
			double [][] X_temp = new double [s.length][X[0].length];
			int [][] Y_temp = new int [s.length][Y[0].length];
			for(int j=0;j<s.length;j++) {
				X_temp[j] = X[s[j]].clone();
				Y_temp[j] = Y[s[j]].clone();
			}
			ClusteringTree T = RPCT(X_temp,Y_temp,mtry,sigma0,n0);
			forest.addTree(T);
		}
		int [] fcounts = new int [X[0].length];
		for(int i=0;i<fcounts.length;i++)
			fcounts[i]=0;
		countFeaturesInForest(fcounts,forest);
		return fcounts;
	}
	/* sample number from 0 to length-1 n times with replacment*/
	private static int[] pickRandomNumbersWithReplacement(int length, int n) {
		HashSet<Integer> s = new HashSet<Integer>();
		Random r = new Random();
		for(int j=0; j<n; j++)
			s.add(r.nextInt(length));
		int [] newArray = new int[s.size()];
		int count = 0;
		for( int i : s) {
			newArray[count] = (int) i;
			count++;
		}
		return newArray;
	}

	/* count the number of times each feature is used in the forest (array of int). */
	private static void countFeaturesInForest(int[] fcounts, Forest forest) {
		int size =forest.getNumberOfTrees();
		for(int i=0;i<size;i++) {
			ClusteringTree t = forest.getTree(i);
			countFeaturesInTree(fcounts,t.getRoot());
		}
	}
	/* count the number of times each feature is used in a tree (array of int). */
	private static void countFeaturesInTree(int[] fcounts, Node root) {
		if(root.isLeaf())
			return;
		fcounts[root.getFeatrueNumber()]++;
		countFeaturesInTree(fcounts, root.getLeftSon());
		countFeaturesInTree(fcounts, root.getRightSon());
	}
}
