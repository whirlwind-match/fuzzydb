package whirlwind.demo.gui.searchselectors;

import javax.swing.JButton;
import javax.swing.JComponent;

public abstract class PopupSearchData extends SearchData {
	
	private JButton button;
	
	public PopupSearchData(String name) {
		super(name);
	}
	
	@Override
	public int getHeight() {
		return 25;
	}

	@Override
    public JComponent getComponent() {
		if (button == null) {
			button = new JButton();
			button.setPreferredSize(new java.awt.Dimension(170,18));
			button.setText(getName());
			button.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					show();
				}
			});
		}
		return button;
	}	

	public abstract void show();
}