
public class BP {
	private double [][] x1;
	private double [][] x2;
	private double [][] y1;
	private double [][] y2;
	private double f;
	private double gain;
	
	public BP(double [][] x1,double [][] x2,double [][] y1,double [][] y2,double f,double gain) {
	this.x1 = x1;
	this.x2 = x2;
	this.y1 = y1;
	this.y2 = y2;
	this.f = f;
	this.gain=gain;
	}
	
	public double [][] getX1() {
		return this.x1;
	}
	public double [][] getX2() {
		return this.x2;
	}
	public void setX1(double [][] x1) {
		setMat(x1);
	}
	public void setX2(double [][] x2) {
		setMat(x2);
	}
	public double [][] getY1() {
		return this.y1;
	}
	public Node getX2() {
		return this.rightSon;
	}
	public void setX1(Node son) {
		this.leftSon = son;
	}
	public void setX2(Node son) {
		this.rightSon = son;
	}
	public int getF() {
		return this.threshold;
	}
	public void setF(int t) {
		this.threshold = t;
	}
	public int getGain() {
		return this.featureNumber;
	}
	public void setGain(int f) {
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
