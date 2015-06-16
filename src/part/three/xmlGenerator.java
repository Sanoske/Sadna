package part.three;

import java.io.*;
import java.util.HashSet;
import java.util.Set;

import part.two.DiseaseHierarchy;
import part.two.Global;

public class xmlGenerator {
	public static void makeXML(String filename, double[][] x, int[][] y, DiseaseHierarchy root) throws IOException{
		File f=new File(filename);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		fos.getChannel().truncate(0);
		bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		bw.newLine();
		bw.write("<labels xmlns=\"http://mulan.sourceforge.net/labels\">");
		labelTreeXML(bw, root.getRoot());
		bw.write("</labels>");
		bw.close();
		fos.close();
	}
	private static void labelTreeXML(BufferedWriter bw, part.two.DiseaseNode node) throws IOException{
		Set <part.two.DiseaseNode> children = node.getChildren();
		bw.write("<label name=\"DOID"+node.getID()+"\">");
		bw.newLine();
		for(part.two.DiseaseNode myNode: children){
			labelTreeXML(bw, myNode);
			bw.newLine();
		}
		
		bw.write("</label>");
	}
	
	public static void makeXMlNoRelations(String filename, double[][] x, int[][] y, DiseaseHierarchy root) throws IOException {
		//Set <part.two.DiseaseNode> s = new HashSet <part.two.DiseaseNode>();
		//part.two.DiseaseNode node = root.getRoot();
		//blabla(node,s);
		Set <String> labels = Global.labelToColumns.keySet();
		File f=new File(filename);
		FileOutputStream fos = new FileOutputStream(f);
		BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
		bw.write("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		bw.newLine();
		bw.write("<labels xmlns=\"http://mulan.sourceforge.net/labels\">");
		bw.newLine();
		for( String n : labels) {
			bw.write("<label name=\"DOID"+n+"\">" + "</label>");
			bw.newLine();
		}
		bw.write("</labels>");
		bw.close();
		fos.close();
	}
	
	private static void blabla(part.two.DiseaseNode node, Set <part.two.DiseaseNode> s) {
		if(node == null)
			return;
		s.add(node);
		for( part.two.DiseaseNode n : node.getChildren())
			blabla(n,s);
	}
}
