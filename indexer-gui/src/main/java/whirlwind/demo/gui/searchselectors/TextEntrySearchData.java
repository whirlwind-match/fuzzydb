package whirlwind.demo.gui.searchselectors;
import java.awt.FlowLayout;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public abstract class TextEntrySearchData extends SearchData {
	
	private JPanel panel;
	private JLabel label;
	private JTextField text;
	
	public TextEntrySearchData(String name) {
		super(name);
	}
	
	public abstract String getLabelText();
	
	@Override
	public int getHeight() {
		return 50;
	}

	@Override
    public JComponent getComponent() {
		if (panel == null) {
			panel = new JPanel();
			panel.setLayout(new FlowLayout());
			
			label = new JLabel();
			label.setText(getLabelText());
			label.setPreferredSize(new java.awt.Dimension(170,18));
			label.setSize(new java.awt.Dimension(170,18));
			panel.add(label);
			
			text = new JTextField();
			text.setPreferredSize(new java.awt.Dimension(170,18));
			text.setSize(new java.awt.Dimension(170,18));
			panel.add(text);
		}
		panel.setPreferredSize(new java.awt.Dimension(170,46));
		panel.setSize(new java.awt.Dimension(170,46));
		return panel;
	}

	public JTextField getText() {
		return text;
	}	
}