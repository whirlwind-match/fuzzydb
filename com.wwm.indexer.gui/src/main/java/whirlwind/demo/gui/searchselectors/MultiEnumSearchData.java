package whirlwind.demo.gui.searchselectors;

import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;

import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.wwm.attrs.enums.EnumDefinition;
import com.wwm.model.attributes.MultiEnumAttribute;

public class MultiEnumSearchData extends PopupSearchData {
	
	private EnumDefinition enumdef;
	private JDialog dialog;
	private ArrayList<JCheckBox> boxs = new ArrayList<JCheckBox>();
	
	
	public MultiEnumSearchData(String name, EnumDefinition enumdef) {
		super(name);
		this.enumdef = enumdef;
	}
	
	
	@Override
	public MultiEnumAttribute getValue() {
		ArrayList<String> values = new ArrayList<String>();
		int i = 0;
		for (JCheckBox box : boxs) {
			if (box.isSelected()) {
				values.add(enumdef.getValues().get(i));
			}
			i++;
		}
		String[] res = new String[values.size()];
		return new MultiEnumAttribute(getName(), enumdef.getName(), values.toArray(res) );
	}
	
	
	@Override
    public void show() {
		if (dialog == null) {
			dialog = new JDialog();
			dialog.setLayout(new GridBagLayout());
			dialog.setTitle(getName());
			JPanel panel = new JPanel();
			panel.setLayout(new FlowLayout());

			for (String name : enumdef.getValues()) {
				JPanel p = new JPanel();

				JCheckBox box = new JCheckBox();
				boxs.add(box);
				p.add(box);

				JLabel label = new JLabel();
				label.setText(name);
				p.add(label);

				panel.add(p);
			}

			dialog.setPreferredSize(new java.awt.Dimension(460,300));
			dialog.setSize(new java.awt.Dimension(460,300));
			
			dialog.setContentPane(panel);
		}
		dialog.setVisible(true);
	}
}