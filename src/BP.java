import java.util.Arrays;


public class BP {
	private double [][] x1;
	private double [][] x2;
	private int [][] y1;
	private int [][] y2;
	private int f;
	private double gain;
	private double threshold;
	
	//build a return object for bestPartition
	public BP(double [][] x,int [][] y,int[] x1,int[] x2,int f,double gain,double threshold) {
	this.x1 = creatMat(x,x1);
	this.x2 = creatMat(x,x2);
	this.y1 = creatMat(y,x1);
	this.y2 = creatMat(y,x2);
	this.f = f;
	this.gain = gain;
	this.threshold = threshold;
	}
	
	//overloading builders
	public BP() {
		this(null,null,null,null,0,0,0);
	}
	
	public double [][] getX1() {
		return this.x1;
	}
	public double [][] getX2() {
		return this.x2;
	}
	public void setX1(double [][] x1) {
		this.x1=setMat(x1);
	}
	public void setX2(double [][] x2) {
		this.x2=setMat(x2);
	}
	public int [][] getY1() {
		return this.y1;
	}
	public int [][] getY2() {
		return this.y2;
	}
	public void setY1(int [][] y1) {
		this.y1=setMat(y1);
	}
	public void setY2(int [][] y2) {
		this.y2=setMat(y2);
	}
	public int getF() {
		return this.f;
	}
	public void setF(int f) {
		this.f = f;
	}
	public double getGain() {
		return this.gain;
	}
	public void setGain(double gain) {
		this.gain = gain;
	}
	public double getThreshold() {
		return this.threshold;
	}
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	private int [][] creatMat(int [][] orig,int[] index) {
		int [][] rtrn = new int[index.length][];
		for(int i=0;i<index.length;i++) {
			rtrn[i] = Arrays.copyOf(orig[index[i]], orig[index[i]].length);
		}
		return rtrn;
	}
	
	private double [][] creatMat(double [][] orig,int[] index) {
		double [][] rtrn = new double[index.length][];
		for(int i=0;i<index.length;i++) {
			rtrn[i] = Arrays.copyOf(orig[index[i]], orig[index[i]].length);
		}
		return rtrn;
	}
	
	private int [][] setMat(int [][] orig) {
		int [][] rtrn = new int[orig.length][];
		for(int i=0;i<orig.length;i++) {
			rtrn[i] = Arrays.copyOf(orig[i], orig[i].length);
		}
		return rtrn;
	}
	
	private double [][] setMat(double [][] orig) {
		double [][] rtrn = new double[orig.length][];
		for(int i=0;i<orig.length;i++) {
			rtrn[i] = Arrays.copyOf(orig[i], orig[i].length);
		}
		return rtrn;
	}
}
