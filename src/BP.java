import java.util.Arrays;


public class BP {
	private double [][] x1;
	private double [][] x2;
	private double [][] y1;
	private double [][] y2;
	private double f;
	private double gain;
	
	public BP(double [][] x1,double [][] x2,double [][] y1,double [][] y2,double f,double gain) {
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
		creatMat(x1);
	}
	public void setX2(double [][] x2) {
		creatMat(x2);
	}
	public double [][] getY1() {
		return this.y1;
	}
	public double [][] getY2() {
		return this.y2;
	}
	public void setY1(double [][] y1) {
		creatMat(y1);
	}
	public void setY2(double [][] y2) {
		creatMat(y2);
	}
	public double getF() {
		return this.f;
	}
	public void setF(double f) {
		this.f = f;
	}
	public double getGain() {
		return this.gain;
	}
	public void setGain(double f) {
		this.f = f;
	}
	private double [][] creatMat(double [][] orig) {
		double [][] rtrn = new  double[orig.length][];
		for(int i=0;i<orig.length;i++) {
			rtrn[i] = Arrays.copyOf(orig[i], orig[i].length);
		}
		return rtrn;
	}
}
