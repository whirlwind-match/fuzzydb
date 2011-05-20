package whirlwind.demo.gui.searchtabs;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;

import com.wwm.indexer.SearchResult;
import com.wwm.indexer.demo.internal.WhirlwindCommon;
import com.wwm.indexer.demo.internal.WhirlwindQuery;
import com.wwm.indexer.demo.internal.WhirlwindQuery.QueryConfig;
import com.wwm.util.gui.table.TableModel;
import com.wwm.util.gui.table.TableSorter;

public class TablePane extends SearchTab {

    private static final long serialVersionUID = 1L;
    private JScrollPane resultsScrollPane = null;
    private JTable resultsTable = null;
    private TableModel defaultTableModel = null;
    private JTableHeader jTableHeader = null;
    //	private static DetailedResultPopup popup = null;
    //	private static ScorerResultPopup scorerPopup = null;

    // This is not called when loaded via XML
    public TablePane() {
        super();
        initialize();
    }

    @Override
    public void whirlwind_initialize(String name, WhirlwindCommon WCommon, WhirlwindQuery query) {
        super.whirlwind_initialize(name, WCommon, query);
        initialize();

        // MUST SET THE COL COUNT FIRST!
        getDefaultTableModel().setColumnCount(WCommon.getXmlLoader().getDemoCfg().getRandomAttributes().size() + 3);
        int index = 0;
        getDefaultTableModel().configureColumn(index++, "#", 25, 25, 25);
        getDefaultTableModel().configureColumn(index++, "Score", 25, 50, null);
        getDefaultTableModel().configureColumn(index++, "RecId", 60, 80, null);
        for (String attrname : WCommon.getXmlLoader().getDemoCfg().getRandomAttributes()) {
            getDefaultTableModel().configureColumn(index++, attrname, 25, 100, null);
        }
    }

    private class SearchResultWrapper {
        public int index;
        public SearchResult result;

        public SearchResultWrapper(SearchResult result, int index) {
            super();
            this.result = result;
            this.index = index;
        }

        @Override
        public String toString() {
            return String.valueOf(index);
        }
    }

    @Override
    protected void onSetSearchCfg(QueryConfig searchCfg) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onReset() {
        while(getDefaultTableModel().getRowCount() !=0) {
            getDefaultTableModel().removeRow(0);
        }
    }

    @Override
    protected void onSetResult(SearchResult result) {

        try {
            ArrayList<Object> values = new ArrayList<Object>();

            values.add(new SearchResultWrapper(result, getDefaultTableModel().getRowCount()+1));
            values.add(result.getScore());
            values.add(result.getPrivateId());

            for (String name : WCommon.getXmlLoader().getDemoCfg().getRandomAttributes()) {
                Object res = result.getAttributes().get(name);
                if (res.getClass().isArray()) {
                    StringBuilder sb = new StringBuilder();
                    for (Object obj : (Object[]) res) {
                        sb.append(obj.toString());
                        sb.append(", ");
                    }
                    res = sb;
                }

                values.add(res.toString());
            }

            getDefaultTableModel().addRow(values.toArray());
        } catch (Exception e) { e.printStackTrace(); } // FIXME: Document this exception
    }

    @Override
    public void onPageStart() {
        getResults().clear();
        while(getDefaultTableModel().getRowCount() !=0) {
            getDefaultTableModel().removeRow(0);
        }
    }

    @Override
    public void onPageComplete() {
    }


    void showDetailedSearchResult(SearchResult result) throws Exception {
        //		if (popup == null) {
        //		popup = new DetailedResultPopup(WCommon);
        //		}
        //		popup.updateResults(getSearchCfg(), result, query.scoreResult(getSearchCfg(), result));
        //		popup.setVisible(true);
    }

    protected void showScorerResults(SearchResult result) throws Exception {
        //		if (scorerPopup == null) {
        //		scorerPopup = new ScorerResultPopup(WCommon);
        //		}
        //		scorerPopup.updateResults(getSearchCfg(), result, query);
        //		scorerPopup.setVisible(true);
    }


    @Override
    public void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.gridx = 0;
        this.setSize(600, 600);
        this.setLayout(new GridBagLayout());
        this.add(getResultsScrollPane(), gridBagConstraints);
    }


    /**
     * This method initializes resultsScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getResultsScrollPane() {
        if (resultsScrollPane == null) {
            resultsScrollPane = new JScrollPane();
            resultsScrollPane.setViewportView(getResultsTable());
        }
        return resultsScrollPane;
    }


    /**
     * This method initializes resultsTable
     * 
     * @return javax.swing.JTable
     */
    private JTable getResultsTable() {
        if (resultsTable == null) {
            resultsTable = new JTable();
            resultsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            TableSorter sorter = new TableSorter(getDefaultTableModel());
            sorter.setTableHeader(getJTableHeader());
            resultsTable.setModel(sorter);
            resultsTable.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1 ) {
                        SearchResultWrapper wrapper = (SearchResultWrapper)getResultsTable().getValueAt(getResultsTable().getSelectedRow(),0);
                        try {
                            showDetailedSearchResult(wrapper.result);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    } else if (e.getButton() == MouseEvent.BUTTON3){
                        // FIXME: WARNING: Get indexOutOfBounds Exception if right-click
                        // after search, without having double-clicked
                        SearchResultWrapper wrapper = (SearchResultWrapper)getResultsTable().getValueAt(getResultsTable().getSelectedRow(),0);
                        try {
                            showScorerResults(wrapper.result);
                        } catch (Exception e1) {
                            e1.printStackTrace();
                        }
                    }

                }
            });
        }
        return resultsTable;
    }


    /**
     * This method initializes defaultTableModel
     * 
     * @return adminTool.utils.TableModel
     */
    private TableModel getDefaultTableModel() {
        if (defaultTableModel == null) {
            defaultTableModel = new TableModel(getResultsTable());
        }
        return defaultTableModel;
    }


    /**
     * This method initializes jTableHeader
     * 
     * @return javax.swing.table.JTableHeader
     */
    private JTableHeader getJTableHeader() {
        if (jTableHeader == null) {
            jTableHeader = getResultsTable().getTableHeader();
        }
        return jTableHeader;
    }

}