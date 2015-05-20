package part.two;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DiseaseHierarchy {
	private HashMap<String,DiseaseNode> treeMap;
	private String fileName;
	private DiseaseNode root;

	//
	public DiseaseHierarchy(String fileName) throws Exception {
		this.treeMap = new HashMap<String,DiseaseNode>();
		this.fileName = fileName;
		this.root = createDiseaseHierarchy();
	}
	
	private DiseaseNode createDiseaseHierarchy() throws Exception {
		String [][] rawDisease = ReadFromFile.readFromExcelFile(new File(this.fileName));
		for (int i=0; i < rawDisease.length; i++) {
			String nodeID = rawDisease[i][0].replaceAll("DOID:", "");
			if (!treeMap.containsKey(nodeID)) {
				DiseaseNode newNode = new DiseaseNode(nodeID, rawDisease[i][1]);
				treeMap.put(nodeID, newNode);
				paternityTest(newNode, rawDisease[i][4]);
			}
			else {
				DiseaseNode existingNode = treeMap.get(nodeID);
				
				existingNode.setName(rawDisease[i][1]);
				paternityTest(existingNode, rawDisease[i][4]);
			}
		}
		//sanity check
		if (!treeMap.containsKey("4")) {
			System.out.println("Disease root was not recorder!?!?!?");
		}
		if (!(treeMap.get("4").getParents().isEmpty())) {
			System.out.println("root has parents");
			Set <DiseaseNode> parents = treeMap.get("4").getParents();
			for (DiseaseNode n : parents)
				System.out.println(n.getID()+" " + n.getName());
		}
		System.out.println();
		//test();
		return treeMap.get("4");
	}

	private void test() {
		System.out.println("TEST");
		DiseaseNode n = treeMap.get("14566");
		Set <DiseaseNode> dads = n.getParents();
		System.out.println("size of dads is: "+dads.size());
		for(DiseaseNode k : dads)
			System.out.println(k.getID()+" "+k.getName());
		
		System.out.println("END OF TEST");
	}

	private void paternityTest(DiseaseNode son, String rawParentIDs) {
		String [] parentIDs = rawParentIDs.split(",");
		for(String id : parentIDs) {
			id = id.replaceAll("DOID:", "");
			if (treeMap.containsKey(id)) {
				son.setParent(treeMap.get(id));
			}
			else {
				DiseaseNode newParent = new DiseaseNode(id);
				son.setParent(newParent);
				this.treeMap.put(id, newParent);
			}
		}
	}
	
	public DiseaseNode getRoot() {
		return this.root;
	}
	public HashMap<String,DiseaseNode> getTreeMap() {
		return this.treeMap;
	}
}
