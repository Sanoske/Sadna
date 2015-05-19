package part.two;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BTreePrinter {

    public static  void printNode(DiseaseNode root,int depth) {
    	Queue<DiseaseNode> level  = new LinkedList<DiseaseNode>();
        level.add(root);
        int count = 1;
        DiseaseNode sep = new DiseaseNode("-1");
        level.add(sep);
        System.out.println("level 1");
        while(!level.isEmpty() && count<=depth){
            DiseaseNode node = level.poll();
            if(node.getID().equals("-1")) {
            	count++;
            	System.out.println("level "+count);
            	level.add(sep);
            	continue;
            }
            System.out.print(node.getID() + " and sons are: ");
            for (DiseaseNode n : node.getChildren())
            	System.out.print(n.getID()+" ");
            System.out.println();
            level.addAll(node.getChildren());
        }
    	
    }
    
    public static int treeDepth(DiseaseNode root) {
    	if(root.getChildren().isEmpty())
    		return 1;
    	int max = 0;
    	for(DiseaseNode n : root.getChildren()) {
    		int temp = treeDepth(n);
    		if(temp > max)
    			max = temp;
    	}
    	return max+1;
    }
}
