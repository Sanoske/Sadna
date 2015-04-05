import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import org.apache.commons.math3.analysis.*;

public class AlgorithmUtils {
	
	private static int[] pickRandomNumbers(int range, int numOfNumbers) {
		Random rnd = new Random();
		int [] ans = new int [numOfNumbers];
		List<Integer> S = new ArrayList<Integer>();
		for (int i = 0; i < range; i++) {
	        S.add(i + 1);
	    }
		Collections.shuffle(S, rnd);
		for(int i=0;i<numOfNumbers;i++)
			ans[i] = S.get(i);
		return ans;
	}

	private static double[][] concerate(double[][] x, int[][] y) {
		double [][] ans = new double [x.length][x[0].length+y[0].length];
		for(int i=0;i<x.length;i++)
			for(int j=0;j<x[0].length;j++)
				ans[i][j] = x[i][j];
		for(int i=0;i<x.length;i++)
			for(int j=0;j<y[0].length;j++)
				ans[i][j+x[0].length] = y[i][j];
		return ans;
	}

	private static double distanceSquare(int [] x1, int [] x2) {
		double result = 0;
		for(int i=0;i<x1.length;i++)
			result+=(x1[i]-x2[i])*(x1[i]-x2[i]);
		return result;
	}
	private static int [] YGag(int [][] y) {
		int [] ans = new int [y[0].length];
		for(int j=0;j<ans.length;j++) {
			ans[j] = 0;
			for(int i=0;i<y.length;i++) {
				ans[j]+= y[i][j];
			}
			ans[j] = ans[j] / y.length;
		}
		return ans;
	}
	private static double var(int[][] y) {
		double ans = 0;
		int [] yGag = YGag(y);
		for(int i=0;i<y.length;i++)
			ans+=distanceSquare(y[i], yGag);
		ans = ans / y.length;
		return ans;
	}

	private static int nrow(double[][] x) {
		return x.length;
	}
	
	private static Node RPCTNode (double [][] X , int [][] Y ,int mtry, int sigma0, int n0 ) {
		Node np = new Node(-1, -1, null);
		if (nrow(X) < n0 || var(Y) < sigma0) {
			int [][] labels = Y.clone();
			np.setLables(labels);
			return np;
		}
		//double [][] I = concerate(X,Y);
		int [] fs = pickRandomNumbers(X[0].length,mtry);
		BP best = bestPartition(X,Y,fs);
		Node nc = RPCTNode(best.getX1(),best.getY1(),mtry,sigma0,n0);
		Node nd = RPCTNode(best.getX2(),best.getY2(),mtry,sigma0,n0);
		np.setLeftSon(nc);
		np.setRightSon(nd);
		np.setFeatureNumber(best.getF());
		np.setTrheshold(best.getThreshold());
		return np;
	}
	
	private static BP bestPartition(double[][] x, int [][] y, int[] fs) {
		double threshold;
		double h;
		Object[] ps;
		BP best = new BP();
		for (int f : fs) {
			threshold = thresholdAVG(f,x);
			ps = binaryPartitions(f,x,threshold);
			h = 1 ;//calc h somehow...
			if (h>best.getGain()) {
				best = new BP(x,y,)
			}
		}
		return null;
	}
	
	private static Object[] binaryPartitions(int f, double[][] x, double threshold) {
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
		int[] leftTarr = new int[leftTlist.size()];
		leftTarr = leftTlist.toArray();
		int[] rightTarr = new int[rightTlist.size()];
		rightTarr = rightTlist.toArray(rightTarr);
		return new Object[]{leftTarr, rightTarr};
	}

	private static double thresholdAVG(int f, double[][] x) {
		double sum = 0.00;
		for (int i=0;i<x.length;i++) {
			sum+=x[i][f];
		}
		return (sum/x.length);
	}

	public static ClusteringTree RPCT (double [][] X , int [][] Y ,int mtry, int sigma0, int n0 ) {
		Node root = RPCTNode(X, Y, mtry, sigma0, n0);
		ClusteringTree tree = new ClusteringTree(root);
		return tree;
	}
	
	/* creating RandomForest. the Function returns how many times each feature is used in the forest (array of int).
	 * the last input is an empty forest that the function fills with trees. */
	public static int [] BootstrapRF (double [][] X, int [][] Y, int ntree,int lambda,int mtry,int sigma0,int n0, Forest forest) {
		int n = nrow(X) * lambda;
		for(int i=0;i<ntree;i++) {
			int [] s = pickRandomNumbers(X.length, n);
			double [][] X_temp = new double [s.length][X[0].length];
			int [][] Y_temp = new int [s.length][Y[0].length];
			for(int j=0;j<s.length;j++) {
				X_temp[j] = X[j].clone();
				Y_temp[j] = Y[j].clone();
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
