import java.util.ArrayList;

public class TreeObject {
	private String display;
	private ArrayList<TreeObject> children = new ArrayList<>();
	
	public TreeObject() {}
	
	public TreeObject(String display) {
		this.display = display;
	}
	
	public ArrayList<TreeObject> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<TreeObject> children) {
		this.children = children;
	}
	public String getDisplay() {
		return display;
	}
	public void setDisplay(String display) {
		this.display = display;
	}
}
