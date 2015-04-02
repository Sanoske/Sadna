import java.util.*;

public class ClusteringTree {
	private Node root;
	
	public ClusteringTree(Node root1) {
		this.root = root1;
	}
	
	public Node getRoot() {
		return this.root;
	}
	public void setRoot(Node n) {
		this.root = n;
	}
	
	public int [] predictLabels(double [] sample) {
		Node n = this.root;
		while(!n.isLeaf()) {
			int threshold = n.getThreshold();
			int feature = n.getFeatrueNumber();
			if( sample[feature] < threshold)
				n = n.getLeftSon();
			else
				n = n.getRightSon();
		}
		return n.getLabels();
	}
	
	public void RPCT (double [][] X , double [][] Y ,int mtry, int n0, int sigma0 ) {
		Node np = new Node(-1, -1, null);
		if (nrow(X) < n0 || var(Y) < sigma0) {
			int [] labels = majority(Y);
			np.setLables(labels);
			return np;
		}
		double [][] I = concerate(X,Y);
		int [] fs = pickRandomFeatures(X[0].length,mtry);
		BP best = bestPartition(X,Y,fs);
		
	}
	
	private int[] majority(double[][] y) {
		int one=0,zero=0;
		int [] ans = new int [y[0].length];
		for(int i=0;i<y[0].length;i++) {
			for(int j=0;j<y.length;j++) {
				if( y[j][i] == 1)
					one++;
				else
					zero++;
			}
			ans[i] = (one>zero) ? one : zero;
			zero = 0;
			one = 0;
		}
		return ans;
	}

	private int[] pickRandomFeatures(int length, int mtry) {
		Random rnd = new Random();
		int [] ans = new int [mtry];
		List<Integer> S = new ArrayList<Integer>(length);
		for (int i = 0; i < length; i++) {
	        S.add(i + 1);
	    }
		Collections.shuffle(S, rnd);
		for(int i=0;i<mtry;i++)
			ans[i] = S.get(i);
		return ans;
	}

	private double[][] concerate(double[][] x, double[][] y) {
		double [][] ans = new double [x.length][x[0].length+y[0].length];
		for(int i=0;i<x.length;i++)
			for(int j=0;j<x[0].length;j++)
				ans[i][j] = x[i][j];
		for(int i=0;i<x.length;i++)
			for(int j=0;j<y[0].length;j++)
				ans[i][j+x[0].length] = y[i][j];
		return ans;
	}

	private double distanceSquare(double [] x1, double [] x2) {
		double result = 0;
		for(int i=0;i<x1.length;i++)
			result+=(x1[i]-x2[i])*(x1[i]-x2[i]);
		return result;
	}
	private double [] YGag(double [][] y) {
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
	private double var(double[][] y) {
		double ans = 0;
		double [] yGag = YGag(y);
		for(int i=0;i<y.length;i++)
			ans+=distanceSquare(y[i], yGag);
		ans = ans / y.length;
		return ans;
	}

	private int nrow(double[][] x) {
		return x.length;
	}
	
	public Forest BootstrapRF (double [][] x, double [][] y, int lambda , int mtry, int sigma0,int n0) {
		
	}
}
