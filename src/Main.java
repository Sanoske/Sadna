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
		int test = 9;
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
		int [] partition = CV.CVPartition(10, features.length);
		int [] count = new int [10];
		for(int i=0; i<partition.length; i++)
			count[partition[i]]++;
		System.out.print("Partition: ");
		for(int i=0; i<10; i++)
			System.out.print(i+": "+count[i]+ "  ");
		System.out.println();
		double [][] test5 = CV.getXTest(features, partition, 5);
		double [][] train5 = CV.getXTrain(features, partition, 5);
		for(int k=0; k<6; k++) {
			System.out.print(cv[test][k] +" ");
		}
		System.out.println(" ");
		for(int l=0; l<6; l++) {
			System.out.print(labels[test][l] +" ");
		}
		System.out.println();
		System.out.println("REAL FINISH");
		
		/*double [][] x_temp = new double[10][features[0].length];
		int [][] y_temp = new int[10][labels.length];
		for(int i=0; i<10; i++) {
			x_temp[i] = features[i].clone();
			y_temp[i] = labels[i].clone();
		}
		System.out.println("Not sorted");
		for(int i=0; i<10; i++) {
			for(int j=0; j<x_temp[0].length;j++)
				System.out.print(x_temp[i][j]+ " ");
			System.out.println();
		}
		System.out.println(); System.out.println();
		for(int i=0; i<10; i++) {
			for(int j=0; j<y_temp[0].length;j++)
				System.out.print(y_temp[i][j]+ " ");
			System.out.println();
		}
		System.out.println(); System.out.println();
		System.out.println("Sorted");
		x_temp = AlgorithmUtils.sortWithRespectToFeature(x_temp, 0, y_temp);
		for(int i=0; i<10; i++) {
			for(int j=0; j<x_temp[0].length;j++)
				System.out.print(x_temp[i][j]+ " ");
			System.out.println();
		}
		System.out.println(); System.out.println();
		for(int i=0; i<10; i++) {
			for(int j=0; j<y_temp[0].length;j++)
				System.out.print(y_temp[i][j]+ " ");
			System.out.println();
		}
		
		x_temp = AlgorithmUtils.extractXMatrix(features_and_labels, features[0].length);
		y_temp = AlgorithmUtils.extractYMatrix(features_and_labels, features[0].length, features[0].length+6);*/
	}
}
