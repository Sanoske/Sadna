import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;


public class Main {
	
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
		int test = 161;
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
		
		Forest f = new Forest();
		AlgorithmUtils.BootstrapRF(features, labels, 2, 0.5,(int)Math.floor(Math.sqrt(features.length)) , 0, 5, f);
		double [][] cv = CV.CVPredict(features, labels, 10, 50, 0.5,(int)Math.floor(Math.sqrt(features.length)) , 0, 5);
		System.out.println("FINISH CV");
		System.out.println();
		double [] predict0 = new double [cv.length];
		int [] label0 = new int [cv.length];
		for(int i=0; i<cv.length; i++) {
			predict0[i] = cv[i][0];
			label0[i] = labels[i][0];
		}
		double [] ans = CV.SimplePerformanceScores(label0, predict0, 0.5);
		System.out.println("precision = "+ans[0]);
		System.out.println("recall = "+ans[1]);
		System.out.println("error = "+ans[2]);
		System.out.println("FPR = "+ans[3]);
		
		//double precision_recall = CV.AUCcurve(label0, predict0, false);
		//System.out.println(precision_recall);
	}
}
