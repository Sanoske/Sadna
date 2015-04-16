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
		double [][] ans = new double [x.length][x[0].length+y[0].length];
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
					double [][] x_small = extractXMatrix(ps.get(0),x[0].length);
					int [][] y_small = extractYMatrix(ps.get(0),x[0].length+1, ps.get(0)[0].length);
					double [][] x_big = extractXMatrix(ps.get(1), x[0].length);
					int [][] y_big = extractYMatrix(ps.get(1), x[0].length+1, ps.get(1)[0].length);
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
	private static int[][] extractYMatrix(double[][] ds, int start, int end) {
		int [][] ans = new int [ds.length][end-start+1];
		for(int i=0; i<ds.length; i++)
			for(int j=start; j<end; j++)
				ans[i][j] = (int) ds[i][j];
		return ans;
	}

	private static double[][] extractXMatrix(double[][] ds, int length) {
		double [][] ans = new double [ds.length][length];
		for(int i=0; i<ds.length; i++)
			for( int j=0; j<length; j++)
				ans[i][j] = ds[i][j];
		return ans;
	}

	// sort the matrix x with respect to feature number f
	private static double [][] sortWithRespectToFeature(double[][] x, int f, int [][] y) {
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
		double [][] i_small = concatenate(small,y_small);
		double [][] i_big = concatenate(big, y_big);
		partition.put(0, i_small);
		partition.put(1, i_big);
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
		Integer [] oldArray = (Integer[]) s.toArray();
		int [] newArray = new int[oldArray.length];
		for(int i=0; i<oldArray.length; i++)
			newArray[i] = (int) oldArray[i];
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
		if(root == null)
			return;
		fcounts[root.getFeatrueNumber()]++;
		countFeaturesInTree(fcounts, root.getLeftSon());
		countFeaturesInTree(fcounts, root.getRightSon());
	}

	public static double[][] readCSV(File file) throws Exception 
	{
		Scanner scan = new Scanner(file);
		ArrayList<double[]> answer = new ArrayList<double[]>();
		String currLine = scan.nextLine();
		int size1 = currLine.split("\t").length;
		while(scan.hasNextLine())
		{
			currLine = scan.nextLine();
			String [] a = currLine.split("\t");
			double [] a1 = new double [a.length];
			for(int i=0;i<a.length;i++)
				a1[i] = Double.parseDouble(a[i]);
			answer.add(a1);
		}
		scan.close();
		int size2 = answer.size();
		double [][] answer1 = new  double[size2][size1];
		for(int i=0;i<size2;i++)
			for(int j=0;j<size1;j++)
				answer1[i][j] = answer.get(i)[j];
		return answer1;
	}
	
	public static void main(String[] args) throws Exception {
		File file = new File("emotions.csv");
		double [][] features_and_labels =readCSV(file);
		double [][] features = new double [features_and_labels.length][features_and_labels[0].length-6];
		int [][] labels = new int [features_and_labels.length][6]; 
		for(int i=0;i<features_and_labels.length;i++)
			for(int j=0;j<features_and_labels[i].length - 6;j++)
				features[i][j] = features_and_labels[i][j];
		
		for(int i=0;i<features_and_labels.length;i++)
			for(int j=0;j<6;j++)
				labels[i][j] = (int)features_and_labels[i][j + features_and_labels[i].length - 6];
	}
}
