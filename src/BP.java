import java.util.Arrays;


public class BP {
	private double [][] x1;
	private double [][] x2;
	private int [][] y1;
	private int [][] y2;
	private int f;
	private double gain;
	
	public BP(double [][] x1,double [][] x2,int [][] y1,int [][] y2,int f,double gain) {
	this.x1 = creatMat(x1);
	this.x2 = creatMat(x2);
	this.y1 = creatMat(y1);
	this.y2 = creatMat(y2);
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
		this.x1=creatMat(x1);
	}
	public void setX2(double [][] x2) {
		this.x2=creatMat(x2);
	}
	public int [][] getY1() {
		return this.y1;
	}
	public int [][] getY2() {
		return this.y2;
	}
	public void setY1(int [][] y1) {
		this.y1=creatMat(y1);
	}
	public void setY2(int [][] y2) {
		this.y2=creatMat(y2);
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
	
	private int [][] creatMat(int [][] orig) {
		int [][] rtrn = new  int[orig.length][];
		for(int i=0;i<orig.length;i++) {
			rtrn[i] = Arrays.copyOf(orig[i], orig[i].length);
		}
		return rtrn;
	}
	
	private double [][] creatMat(double [][] orig) {
		double [][] rtrn = new  double[orig.length][];
		for(int i=0;i<orig.length;i++) {
			rtrn[i] = Arrays.copyOf(orig[i], orig[i].length);
		}
		return rtrn;
	}
}
