package whirlwind.demo.gui.searchselectors;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import org.fuzzydb.dto.attributes.BooleanAttribute;


public class BooleanSearchData extends SearchData {
	
	private JComboBox combo;
	
	
	public BooleanSearchData(String name) {
		super(name);
	}
	
	
	@Override
	public BooleanAttribute getValue() {
		JDataItem i = (JDataItem) combo.getSelectedItem();
		return new BooleanAttribute(getName(), (Boolean)i.data);
	}
	
	
	@Override
	public int getHeight() {
		return 23;
	}
	
	
	@Override
    public JComponent getComponent() {
		if (combo == null) {
			combo = new JComboBox();
			combo.setPreferredSize(new java.awt.Dimension(170,18));
			
			combo.addItem(new JDataItem(getName() + " :: " + true, Boolean.TRUE));
			combo.addItem(new JDataItem(getName() + " :: " + false, Boolean.FALSE));
		}
		return combo;
	}
}