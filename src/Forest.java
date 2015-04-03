import java.util.*;

public class Forest {
	private List<ClusteringTree> list = null;
	private int numberOfTrees;
	
	public Forest () {
		this.list = new ArrayList<ClusteringTree>();
		this.numberOfTrees = 0;
	}
	
	public void addTree(ClusteringTree t) {
		list.add(t);
		this.numberOfTrees++;
	}
	
	public void removeTree(ClusteringTree t) {
		list.remove(t);
		this.numberOfTrees--;
	}
	
	public ClusteringTree getTree(int index) {
		return list.get(index);
	}
	
	public int getNumberOfTrees() {
		return this.numberOfTrees;
	}
	
	public int [] RFPredict (double [] X, int numOfLabels) {
		int [] p = new int [numOfLabels];
		for(int j=0;j<numOfLabels;j++)
			p[j] = 0;
		for(int i=0;i<this.numberOfTrees;i++) {
			ClusteringTree t = this.list.get(i);
			int[] Y = t.predictLabels(X);
			for(int j=0;j<numOfLabels;j++)
				p[i]+=Y[i];
		}
		for(int j=0;j<numOfLabels;j++)
			p[j] = p[j] / this.numberOfTrees;
		return p;
	}
}
