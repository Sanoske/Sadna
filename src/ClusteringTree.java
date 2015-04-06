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
	
	public int [][] predictLabels(double [] sample) {
		Node n = this.root;
		while(!n.isLeaf()) {
			double threshold = n.getThreshold();
			int feature = n.getFeatrueNumber();
			if( sample[feature] < threshold)
				n = n.getLeftSon();
			else
				n = n.getRightSon();
		}
		return n.getLabels();
	}
}
