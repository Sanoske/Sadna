package part.three;


import java.io.*;
import java.util.Set;

import part.two.*;
public class arffGenerator {
	
	public static void makeARFF(String fileName, double[][] x, int[][] y) throws IOException{
		File f=new File(fileName);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		fos.getChannel().truncate(0);
		bw.write("@relation workshopARFF");
		bw.newLine();
		bw.newLine();
		Set <String> labels = Global.labelToColumns.keySet();
		Set <String> doids = Global.geneToColumns.keySet();
		for(String doid : doids){
			bw.write("@attribute "+doid+" numeric");
			bw.newLine();
		}
		for(String str : labels){
			bw.write("@attribute DOID"+str+" {0,1}");
			bw.newLine();
		}
		bw.write("@data");
		bw.newLine();
		String s="";
		for(int i=0;i<x.length;i++){	// i is the row
			for(int j=0;j<x[i].length;j++){
				s+=(int)(x[i][j]);
				s+=",";
			}
			for(int j=0;j<y[i].length;j++){
				s+=(int)(y[i][j]);
				if(j<y[i].length-1){
					s+=",";
				}else{
					bw.write(s);
					bw.newLine();
					s="";
				}
			}
		}
		bw.close();
		fos.close();
	}
}
