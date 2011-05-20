package whirlwind.demo.gui.searchselectors;

import javax.swing.JComboBox;
import javax.swing.JComponent;

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.model.attributes.EnumAttribute;


public class EnumSearchData extends SearchData {
	
	private EnumDefinition enumdef;
	private JComboBox combo;
	
	
	public EnumSearchData(String name, EnumDefinition enumdef) {
		super(name);
		this.enumdef = enumdef;
	}
	
	
	@Override
	public EnumAttribute getValue() {
		JDataItem i = (JDataItem) combo.getSelectedItem();
		return new EnumAttribute(getName(), enumdef.getName(), (String)i.data);
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
			
			for (String name : enumdef.getValues()) {
				combo.addItem(new JDataItem(name + " :: " + name, name));
			}
		}
		return combo;
	}

}