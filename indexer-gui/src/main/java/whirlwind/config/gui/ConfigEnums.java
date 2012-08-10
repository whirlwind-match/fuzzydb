package whirlwind.config.gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Map.Entry;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import org.fuzzydb.attrs.enums.EnumDefinition;

import com.wwm.indexer.config.XmlLoader;
import com.wwm.util.gui.table.TableModel;
import com.wwm.util.gui.table.TableSorter;

public class ConfigEnums extends JPanel {

	private static final long serialVersionUID = 1L;
	private JScrollPane enumsScrollPane = null;
	private JTable enumsjTable = null;
	private TableModel defaultTableModel = null;
	private XmlLoader loader;
	
	private class TableData {
		String name;
		EnumDefinition def;
		public TableData(String name, EnumDefinition def) {
			this.name = name;
			this.def = def;
		}

		Object[] getTableData() {
			Object[] res = 	{this, def.getValues().toString()};
			return res;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}
	
	/**
	 * This is the default constructor
	 * @param loader 
	 */
	public ConfigEnums(XmlLoader loader) {
		super();
		this.loader = loader;
		initialize();
		getDefaultTableModel().setColumnCount(2);
		getDefaultTableModel().configureColumn(0, "Name", 100, 200, 300);
		getDefaultTableModel().configureColumn(1, "Values", null, null, null);
		refreshTable();
	}

	private void onEdit(TableData enumdata) {
		ConfigEnumsDialog edit = new ConfigEnumsDialog(enumdata.name, enumdata.def);
		edit.setModal(true);
		edit.setVisible(true);
	}
	
	void refreshTable() {
		getDefaultTableModel().setRowCount(0);		
		for (Entry<String, EnumDefinition> entry: loader.getEnumDefs().entrySet()) {
			getDefaultTableModel().addRow(new TableData(entry.getKey(), entry.getValue()).getTableData());	
		}
	}
	
	
	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.gridx = 0;
		this.setSize(541, 416);
		this.setLayout(new GridBagLayout());
		this.add(getEnumsScrollPane(), gridBagConstraints);
	}

	/**
	 * This method initializes enumsScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getEnumsScrollPane() {
		if (enumsScrollPane == null) {
			enumsScrollPane = new JScrollPane();
			enumsScrollPane.setViewportView(getEnumsjTable());
		}
		return enumsScrollPane;
	}

	/**
	 * This method initializes enumsjTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getEnumsjTable() {
		if (enumsjTable == null) {
			enumsjTable = new JTable();
			TableSorter sorter = new TableSorter(getDefaultTableModel());
			sorter.setTableHeader(enumsjTable.getTableHeader());
			enumsjTable.setModel(sorter);

			enumsjTable.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
                    if (e.getClickCount() == 2) {
                        TableData enumdata = (TableData) getEnumsjTable().getValueAt(getEnumsjTable().getSelectedRow(), 0);
                    	onEdit(enumdata);
                    }
				}
			});
		}
		return enumsjTable;
	}

	private TableModel getDefaultTableModel() {
		if (defaultTableModel == null) {
			defaultTableModel = new TableModel(getEnumsjTable());
		}
		return defaultTableModel;
	}
}  //  @jve:decl-index=0:visual-constraint="10,10"
