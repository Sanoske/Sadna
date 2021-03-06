package part.one;
import java.util.Arrays;


public class Node {
	private Node leftSon;
	private Node rightSon;
	private int [][] labels;
	private double threshold;
	private int featureNumber;
	
	public Node(int featureNumber,double threshold,int [][] labels) {
		if(labels == null)
			this.labels = null;
		else
			this.labels = creatMat(labels);
		this.threshold = threshold;
		this.featureNumber = featureNumber;
		this.leftSon = null;
		this.rightSon = null;
	}
	
	public Node getLeftSon() {
		return this.leftSon;
	}
	public Node getRightSon() {
		return this.rightSon;
	}
	public void setLeftSon(Node son) {
		this.leftSon = son;
	}
	public void setRightSon(Node son) {
		this.rightSon = son;
	}
	public double getThreshold() {
		return this.threshold;
	}
	public void setTrheshold(double t) {
		this.threshold = t;
	}
	public int getFeatrueNumber() {
		return this.featureNumber;
	}
	public void setFeatureNumber(int f) {
		this.featureNumber = f;
	}
	public int [][] getLabels() {
		return creatMat(labels);
	}
	public void setLables(int [][] l) {
		//this.labels = creatMat(l);
		this.labels = l;
	}
	public boolean isLeaf() {
		if( this.leftSon == null && this.rightSon==null)
			return true;
		return false;
	}
	// clone orig matrix
	private int [][] creatMat(int [][] orig) {
		int [][] rtrn = new  int[orig.length][];
		for(int i=0;i<orig.length;i++) {
			rtrn[i] = orig[i].clone();
		}
		return rtrn;
	}
}
