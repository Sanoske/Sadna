import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;


public class Main {
	// extract specific colums out of the matrix
	public static int [] extractcolumn(int [][] a, int column) {
		int [] ans = new int [a.length];
		for(int i=0; i<a.length; i++)
			ans[i] = a[i][column];
		return ans;
	}
	// extract specific colums out of the matrix
	public static double [] extractcolumn(double [][] a, int column) {
		double [] ans = new double [a.length];
		for(int i=0; i<a.length; i++)
			ans[i] = a[i][column];
		return ans;
	}
	// build random forest
	public static Forest RF_PCT(double [][] x, int [][] y, int ntree, int mtry,
								int sigma0, int n0, double lambda) {
		Forest f = new Forest ();
		AlgorithmUtils.BootstrapRF(x, y, ntree, lambda, mtry, sigma0, n0, f);
		return f;
	}
	// given model f, matrix data x 
	// and number of labels for each sample, returns the predictions for x 
	// (double values, also known as probability predictions)
	public static double [][] RF_PCT_predict (Forest f, double [][] x, int numOfLabels) {
		double [][] y = new double [x.length][];
		for( int i=0; i<x.length; i++)
			y[i] = f.RFPredict(x[i], numOfLabels).clone();
		return y;
	}
	// read a CSV file and put it in matrix
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
		System.out.println("START");
		JFrame f = new JFrame();
		long start_global = System.nanoTime();
		int [] ntree_array = {1,10,25,50,100};
		int [] mtry_array = {5,10,20,40};
		double   precision_recall ,roc;
		double [][] plotX = new double [ntree_array.length][6]; 
		double [][] plotY_roc = new double [ntree_array.length][6];
		double [][] plotY_precision = new double [ntree_array.length][6];
		double [][] plotY_error = new double [ntree_array.length][6];
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
		int count = 0;
		for(int ntree : ntree_array) {
			System.out.println(ntree+ " trees");
			double [][] cv = CV.CVPredict(features, labels, 10, ntree, 0.5,(int)Math.floor(Math.sqrt(features.length)) , 0, 5);
			double [][] predict = new double [cv.length][];
			int [][] label = new int [cv.length][];
			for(int i=0; i<cv.length; i++) {
				predict[i] = cv[i].clone();
				label[i] = labels[i].clone();
			}
			for(int j=0; j<6; j++) {
				int [] labelToFunc = extractcolumn(label,j);
				double [] predictToFunc = extractcolumn(predict,j);
				precision_recall = CV.AUCcurve(labelToFunc ,predictToFunc, false);
				roc = CV.AUCcurve(labelToFunc ,predictToFunc, true);
				System.out.println("precision is: "+precision_recall+" and roc is: "+roc+"for label "+j);
				plotX[count][j] = ntree;
				plotY_roc[count][j] = roc;
				plotY_precision[count][j] = precision_recall;
				plotY_error[count][j] = CV.SimplePerformanceScores(extractcolumn(label,j), extractcolumn(predict,j), 0.5)[2];
			}
			count++;
		}
		for(int j=0; j<6; j++) {
				paintToFile(f,extractcolumn(plotX,j),extractcolumn(plotY_precision,j),"Precision-Recall curve. label "+j);
				paintToFile(f,extractcolumn(plotX,j),extractcolumn(plotY_roc,j),"roc AUC curve. label "+j);
				paintToFile(f,extractcolumn(plotX,j),extractcolumn(plotY_error,j),"error rate. label "+j);
		}
		System.out.println();
		int ntree = 100;
		double error;
		Forest forest = new Forest();
		int [] count_featrues;
		count_featrues = AlgorithmUtils.BootstrapRF(features, labels, ntree, 0.5, (int)Math.floor(Math.sqrt(features.length)), 0, 5, forest);
		rankFeatures(count_featrues);
		System.out.println();
		for(int mtry : mtry_array) {
			long start = System.nanoTime();
			double [][] cv = CV.CVPredict(features, labels, 10, ntree, 0.5,mtry , 0, 5);
			double [][] predict = new double [cv.length][];
			int [][] label = new int [cv.length][];
			for(int i=0; i<cv.length; i++) {
				predict[i] = cv[i].clone();
				label[i] = labels[i].clone();
			}
			System.out.println();
			error = 0;
			for(int j=0; j<6; j++) { 
				error += CV.SimplePerformanceScores(extractcolumn(label,j), extractcolumn(predict,j), 0.5)[2];
			}
			error = error/6;
			System.out.println("the avg error in mtry = "+mtry+" is "+error);
			long end = System.nanoTime();
			double time = (end-start)/(double)Math.pow(10, 9);
			time = time / (double)60;
			System.out.println("elapsed time for mtry = "+mtry+" is: "+time+" minutes");
		}
		System.out.println();
		long end_global = System.nanoTime();
		double time_global = (end_global-start_global)/(double)Math.pow(10, 9);
		time_global = time_global / (double)60;
		System.out.println("elapsed total time: "+time_global+" minutes");
		
		System.out.println("DONE");
	}
	//paint the graphs into JPG file
	private static void paintToFile(JFrame f, double[] plotX, double[] plotY,String s) {
    	int width = 400, height = 400;
	    f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    f.getContentPane().add(new GraphingData(plotX,plotY,width,height,s));
	    f.setSize(width,height);
	    f.setLocation(200,200);
	    f.setVisible(true);		
	}
	//rank the features by times they are used
	private static void rankFeatures(int[] count_featrues) {
		int maxIndex;
		for(int i=0; i<count_featrues.length; i++) {
			maxIndex = getMax(count_featrues);
			System.out.println("feature number " +maxIndex+" is ranked "+(i+1)+ " and used "+count_featrues[maxIndex]+" times.");
			count_featrues[maxIndex] = Integer.MIN_VALUE;
		}
		
	}
	// get the index of the maximum number
	private static int getMax(int[] count_featrues) {
		int max = count_featrues[0];
		int index = 0;
		for(int i=1; i<count_featrues.length; i++) {
			if(max < count_featrues[i]) {
				max = count_featrues[i];
				index = i;
			}
		}
		return index;
	}
}
