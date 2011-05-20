package whirlwind.demo.gui.searchselectors;

import javax.swing.JComponent;

import whirlwind.demo.gui.util.FloatTextField;

import com.wwm.model.attributes.FloatAttribute;

public class FloatSearchData extends SearchData {
	
	private FloatTextField textField;
	
	
	public FloatSearchData(String name) {
		super(name);
	}
	
	
	@Override
	public FloatAttribute getValue() {
        return new FloatAttribute(getName(), textField.getFloat() );
	}
	
	
	@Override
	public int getHeight() {
		return 23;
	}
	
	
	@Override
    public JComponent getComponent() {
		if (textField == null) {
		    textField = new FloatTextField(Float.MIN_VALUE, Float.MAX_VALUE);
			textField.setPreferredSize(new java.awt.Dimension(170,18));
			textField.setSize(new java.awt.Dimension(170,18));
			textField.setText("0.0");
		}
		return textField;
	}
}