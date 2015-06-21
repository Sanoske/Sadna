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
		for(int i=0; i<doids.size(); i++) {
			for(String doid : doids){
				if(Global.geneToColumns.get(doid) == i) {
					bw.write("@attribute "+doid+" numeric");
					bw.newLine();
				}
			}
		}
		for(int j=0; j<labels.size(); j++) {
			for(String str : labels){
				if(Global.labelToColumns.get(str) == j) {
					bw.write("@attribute DOID"+str+" {0,1}");
					bw.newLine();
				}
			}
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
