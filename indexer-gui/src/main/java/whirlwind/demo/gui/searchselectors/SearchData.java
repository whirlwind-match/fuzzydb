package whirlwind.demo.gui.searchselectors;

import java.awt.FlowLayout;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.fuzzydb.dto.attributes.Attribute;

import com.wwm.indexer.exceptions.AttributeException;

public abstract class SearchData {
    private JPanel panel;
    private JCheckBox check;
    private final String name;

    public SearchData(String name) {
        this.name = name;
    }

    public abstract JComponent getComponent();
    public abstract Attribute<?> getValue() throws AttributeException;
    public abstract int getHeight();

    public String getName() {
        return name;
    }

    public JCheckBox getCheck() {
        if (check == null) {
            check = new JCheckBox();
            check.setPreferredSize(new java.awt.Dimension(20,20));
        }
        return check;
    }

    public JPanel getPanel() {
        if (panel == null) {
            panel = new JPanel();
            panel.setLayout(new FlowLayout());
            panel.add(getCheck());
            panel.add(getComponent());

            panel.setPreferredSize(new java.awt.Dimension(210,getHeight()));
            panel.setSize(new java.awt.Dimension(210,getHeight()));
        }
        return panel;
    }

}