import java.util.*;

public class Forest {
	private List<ClusteringTree> list = null;
	
	public Forest () {
		list = new ArrayList<ClusteringTree>();
	}
	
	public void addTree(ClusteringTree t) {
		list.add(t);
	}
	
	public void removeTree(ClusteringTree t) {
		list.remove(t);
	}
	
	public ClusteringTree getTree(int index) {
		return list.get(index);
	}
}
