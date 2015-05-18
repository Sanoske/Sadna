package part.two;

import java.util.HashSet;

public class DiseaseNode {
	private final int PARENT_CAPACITY = 6; //no more than 3 parents in the DB (4 to be on the safe side) ==> 4/0.75  5.3
	
	private String id;
	private String name;
	private HashSet<DiseaseNode> parents;
	private HashSet<DiseaseNode> children;

	//creates a new node - first entry in DB as node
	public DiseaseNode(String id, String name) {
		this.id = id;
		this.name = name;
		this.parents = new HashSet<DiseaseNode>(PARENT_CAPACITY);
		this.children = new HashSet<DiseaseNode>();
	}
	//constructor overload - if first entry in DB is on parent field
	public DiseaseNode(String id) {
		this(id,null);
	}
	
	public void setID(String id) {
		this.id = id;
	}
	public void setName(String name) {
		this.name = name;
	}
	//sets parent and son relationship
	public void setParent(DiseaseNode parent) {
		this.parents.add(parent);
		parent.setChild(this);
	}
	public String getID() {
		return this.id;
	}
	public String getName() {
		return this.name;
	}
	//returns the set of parents - according to our DB not more than 3 (constant)
	public HashSet<DiseaseNode> getParents() {
		return this.parents;
	}
	public boolean isUnassigned() {
		return (this.name == null);
	}
	private void setChild(DiseaseNode child) {
		this.children.add(child);
	}
	public HashSet<DiseaseNode> getChildren() {
		return this.children;
	}
}
