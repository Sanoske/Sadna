
public class Node {
	private Node leftSon;
	private Node rightSon;
	private int [] labels;
	private int threshold;
	private int featureNumber;
	
	public Node(int featureNumber,int threshold,int [] labels) {
		this.labels = new int [labels.length];
		for (int i=0;i<this.labels.length;i++)
			this.labels[i] = labels[i];
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
	public int getThreshold() {
		return this.threshold;
	}
	public void setTrheshold(int t) {
		this.threshold = t;
	}
	public int getFeatrueNumber() {
		return this.featureNumber;
	}
	public void setFeatureNumber(int f) {
		this.featureNumber = f;
	}
	public int [] getLabels() {
		int [] ans = new int [this.labels.length];
		for(int i=0;i<this.labels.length;i++)
			ans[i]=this.labels[i];
		return ans;
	}
	public void setLables(int [] l) {
		for(int i=0;i<l.length;i++)
			this.labels[i]=l[i];
	}
	public boolean isLeaf() {
		if( this.leftSon == null && this.rightSon==null)
			return true;
		return false;
	}
}
