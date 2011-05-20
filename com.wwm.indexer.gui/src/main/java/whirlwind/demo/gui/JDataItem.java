package whirlwind.demo.gui;

public class JDataItem {
	public String display;
	public Object data;
	
	public JDataItem(String display, Object data) {
		this.display = display;
		this.data = data;
	}
	
	@Override
	public String toString() {
		return display;
	}
}
