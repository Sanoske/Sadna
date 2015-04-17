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
		int test = 43;
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
		
		ClusteringTree tree = AlgorithmUtils.RPCT(features, labels,(int)Math.floor(Math.sqrt(features.length)) , 0, 5);
		System.out.println("built tree");
		Forest f = new Forest();
		AlgorithmUtils.BootstrapRF(features, labels, 10, 0.75,(int)Math.floor(Math.sqrt(features.length))
				, 0, 5, f);
		System.out.println("built forest");
		
		double [][] cv = CV.CVPredict(features, labels, 10, 50, 0.5,(int)Math.floor(Math.sqrt(features.length)) , 0, 5);
		System.out.println("FINISH");
		
		
		/*f.addTree(tree);
		double [] predicted_labels = f.RFPredict(features[test], 6);
		for(int k=0; k<6; k++) {
			System.out.print(predicted_labels[k] +" ");
		}
		System.out.println(" ");
		for(int l=0; l<6; l++) {
			System.out.print(labels[test][l] +" ");
		}
		System.out.println();
		double [] scores = CV.SimplePerformanceScores(labels[test], predicted_labels, 0.5);
		System.out.println("precision = " +scores[0]);
		System.out.println("recall = " +scores[1]);
		System.out.println("error = " +scores[2]);
		System.out.println("FPR = " +scores[3]);*/
	}
}
