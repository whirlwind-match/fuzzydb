package whirlwind.demo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.attrs.enums.EnumMultipleValue;
import org.fuzzydb.attrs.enums.EnumValue;
import org.fuzzydb.attrs.internal.EnumAttributeSpec;
import org.fuzzydb.attrs.simple.FloatValue;
import org.fuzzydb.dto.attributes.Attribute;
import org.fuzzydb.dto.attributes.LocationAttribute;
import org.fuzzydb.util.NanoTimer;

import whirlwind.demo.gui.searchselectors.BooleanSearchData;
import whirlwind.demo.gui.searchselectors.EnumSearchData;
import whirlwind.demo.gui.searchselectors.FloatSearchData;
import whirlwind.demo.gui.searchselectors.MultiEnumSearchData;
import whirlwind.demo.gui.searchselectors.SearchData;
import whirlwind.demo.gui.searchselectors.StringSearchData;
import whirlwind.demo.gui.searchtabs.SearchTab;
import whirlwind.demo.gui.searchtabs.SearchTabXmlConfig;

import com.wwm.indexer.SearchResult;
import com.wwm.indexer.config.XmlLoader;
import com.wwm.indexer.demo.internal.WhirlwindCommon;
import com.wwm.indexer.demo.internal.WhirlwindQuery;
import com.wwm.indexer.demo.internal.WhirlwindQuery.QueryConfig;
import com.wwm.indexer.exceptions.AttributeException;


public class SearchPane extends JPanel {

    private static final long serialVersionUID = 1L;
    private JPanel searchButtonPanel = null;
    private JButton searchButton = null;
    private JButton nextPageButton = null;
    private JLabel pageTimeLabel = null;
    private JPanel searchPanel = null;
    private JComboBox resultsPerPageComboBox = null;
    private JComboBox scorerCfgComboBox = null;
    private JLabel scoreThresholdLabel = null;
    private JSlider scoreThresholdSlider = null;
    private JLabel jLabel = null;
    private JTextArea maxResultsTextArea = null;

    private JTabbedPane jTabbedPane = null;


    // -----------------------------------------------------
    // My Attributes
    // -----------------------------------------------------
    private final WhirlwindCommon WCommon;
    private WhirlwindQuery query;

    private QueryConfig currentSearchCfg;  //  @jve:decl-index=0:

    private final ArrayList<SearchData> customSearch = new ArrayList<SearchData>();  //  @jve:decl-index=0:
    private int currentCount = 0;

    private final ArrayList<SearchTab> tabs = new ArrayList<SearchTab>();  //  @jve:decl-index=0:

    /**
     * This is the default constructor
     */
    public SearchPane(WhirlwindCommon WCommon) {
        super();
        this.WCommon = WCommon;
        initialize();
        initializeControls();

        // --------------------------------------------
        // Configured tabs from the config xml
        // --------------------------------------------
        for (SearchTabXmlConfig tabcfg : WCommon.getXmlLoader().getDemoCfg().getSearchTabs()) {
            SearchTab tab = WCommon.getXmlLoader().getDemoCfg().getSearchTab(tabcfg.getTabClass());
            tabs.add(tab);
            tab.whirlwind_initialize(tabcfg.getTabName(), WCommon, query);
            getJTabbedPane().addTab(tab.getTabName(), null, tab, null);
        }
        // --------------------------------------------
    }

    // -----------------------------------------------------
    // My functions
    // -----------------------------------------------------
    void initializeControls() {
        try {
            getResultsPerPageComboBox().addItem(new JDataItem("10 Results Per Page", 10));
            getResultsPerPageComboBox().addItem(new JDataItem("50 Results Per Page", 50));
            getResultsPerPageComboBox().addItem(new JDataItem("100 Results Per Page", 100));
            getResultsPerPageComboBox().addItem(new JDataItem("1000 Results Per Page", 1000));

            int i = 0;
            int select = 0;
            SortedSet<String> sortedCfgs = new TreeSet<String>(WCommon.getXmlLoader().getScorerCfgs());
            for (String name: sortedCfgs) {
                if (name.equals("default")) {
                    continue;
                }
                if (name.equals(WCommon.getXmlLoader().getDemoCfg().getPrimaryScorerCfg())) {
                    select = i;
                }
                i++;
                getScorerCfgComboBox().addItem(new JDataItem(name, name));
            }
            getScorerCfgComboBox().setSelectedIndex(select);
            addCustomSearch();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void addCustomSearch() throws Exception {

        for (SearchData sd: customSearch) {
            getSearchPanel().remove(sd.getPanel());
        }
        customSearch.clear();

        XmlLoader loader = WCommon.getXmlLoader();

        for (String name : loader.getDemoCfg().getRandomAttributes() ) {
            SearchData data = null;
            Object o = loader.getAttributes().get(name + ".xml");

            if (o instanceof Class) {
                Class<?> clazz = (Class<?>) o;
                if (clazz.equals(FloatValue.class)) {
                    data = new FloatSearchData(name);
                } else if (clazz.equals(BooleanValue.class)) {
                    data = new BooleanSearchData(name);
                } else if (clazz.equals(LocationAttribute.class)) {
                    data = new StringSearchData(name); // Keep as postcode
                }
            } else if (o instanceof EnumAttributeSpec) {
                EnumAttributeSpec spec = (EnumAttributeSpec) o;
                if (spec.clazz.equals(EnumValue.class)) {
                    data = new EnumSearchData(name, loader.getEnumDefs().get(spec.enumdef + ".xml"));
                } else if (spec.clazz.equals(EnumMultipleValue.class)) {
                    data = new MultiEnumSearchData(name, loader.getEnumDefs().get(spec.enumdef + ".xml"));
                }
            }
            assert data != null;
            customSearch.add(data);
            getSearchPanel().add(data.getPanel(), null);
        }
    }

    private Object getValue(JComboBox box) {
        JDataItem i = (JDataItem) box.getSelectedItem();
        return i.data;
    }

    /**
     * Get the query represented by search options
     * @return
     * @throws AttributeException
     */
    public QueryConfig getQueryConfig() throws AttributeException {
        QueryConfig currentCfg;
        currentCfg = new QueryConfig();
        currentCfg.scorerConfig = (String) getValue(getScorerCfgComboBox());
        currentCfg.pageSize = (Integer) getValue(getResultsPerPageComboBox());

        currentCfg.scoreThreshold = (getScoreThresholdSlider().getValue()) / 100.0f;

        String maxResults = getMaxResultsTextArea().getText();
        if (maxResults.length() != 0) {
            Integer results = Integer.valueOf(maxResults);
            if (results > 0) {
                currentCfg.maxResults = results;
            }
        }

        for (SearchData data : customSearch) {
            if (data.getCheck().isSelected()) {
            	Attribute<?> obj = data.getValue();
                if (obj != null) {
                    currentCfg.getAttributes().put(data.getName(), obj);
                }
            }
        }

        return currentCfg;
    }

    private void doSearch() throws Exception {
        currentSearchCfg = getQueryConfig();
        query = new WhirlwindQuery(WCommon);

        NanoTimer t = new NanoTimer();
        ArrayList<SearchResult> currentResults = query.doSearch(currentSearchCfg);
        pageTimeLabel.setText("Page Time : " + t.getMillis() + " ms");
        showPage(currentResults);
    }

    void showNextPage() throws Exception {
        NanoTimer t = new NanoTimer();
        ArrayList<SearchResult> currentResults = query.nextpage();
        pageTimeLabel.setText("Page Time : " + t.getMillis() + " ms");
        showPage(currentResults);
    }


    private void showPage(ArrayList<SearchResult> currentResults) throws Exception {
        for (SearchTab tab : tabs) {
            tab.pageStart();
        }

        for (SearchTab tab : tabs) {
            currentCount++;
            tab.addResults(currentResults);
        }

        for (SearchTab tab : tabs) {
            tab.pageComplete();
        }

        //		getNextPageButton().setEnabled(currentResults.hasNext());
        getNextPageButton().setEnabled(true);
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(608, 387);

        this.setLayout(new BorderLayout());
        this.add(getSearchButtonPanel(), BorderLayout.SOUTH);
        this.add(getSearchPanel(), BorderLayout.WEST);
        this.add(getJTabbedPane(), BorderLayout.CENTER);

    }

    /**
     * This method initializes searchButtonPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchButtonPanel() {
        if (searchButtonPanel == null) {
            searchButtonPanel = new JPanel();
            searchButtonPanel.setLayout(null);
            searchButtonPanel.setPreferredSize(new Dimension(200, 20));
            searchButtonPanel.add(getSearchButton(), null);
            searchButtonPanel.add(getNextPageButton(), null);

            pageTimeLabel = new JLabel();
            pageTimeLabel.setPreferredSize(new Dimension(150, 20));
            pageTimeLabel.setName("pageTimeLabel");
            pageTimeLabel.setLocation(new Point(313, 1));
            pageTimeLabel.setSize(new Dimension(260, 20));
            pageTimeLabel.setText("");

            searchButtonPanel.add(pageTimeLabel, null);
            searchButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        doSearch();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        return searchButtonPanel;
    }

    /**
     * This method initializes searchButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getSearchButton() {
        if (searchButton == null) {
            searchButton = new JButton();
            searchButton.setFont(new Font("Dialog", Font.BOLD, 12));
            searchButton.setRolloverEnabled(true);
            searchButton.setText("Search");
            searchButton.setName("searchButton");
            searchButton.setLocation(new Point(0, 0));
            searchButton.setSize(new Dimension(200, 20));
            searchButton.setPreferredSize(new Dimension(200, 20));
        }
        return searchButton;
    }

    /**
     * This method initializes nextPageButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getNextPageButton() {
        if (nextPageButton == null) {
            nextPageButton = new JButton();
            nextPageButton.setEnabled(false);
            nextPageButton.setPreferredSize(new Dimension(100, 20));
            nextPageButton.setName("nextPageButton");
            nextPageButton.setLocation(new Point(201, 1));
            nextPageButton.setSize(new Dimension(100, 20));
            nextPageButton.setText("Next Page");
            nextPageButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    try {
                        showNextPage();
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            });
        }
        return nextPageButton;
    }

    /**
     * This method initializes searchPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getSearchPanel() {
        if (searchPanel == null) {
            jLabel = new JLabel();
            jLabel.setPreferredSize(new Dimension(90, 16));
            jLabel.setText("Max Results ");
            scoreThresholdLabel = new JLabel();
            scoreThresholdLabel.setPreferredSize(new Dimension(200, 16));
            scoreThresholdLabel.setText("Score Threshold : 1");
            searchPanel = new JPanel();
            searchPanel.setLayout(new FlowLayout());
            searchPanel.setPreferredSize(new Dimension(200, 100));
            searchPanel.add(getResultsPerPageComboBox(), null);
            searchPanel.add(getScorerCfgComboBox(), null);
            searchPanel.add(scoreThresholdLabel, null);
            searchPanel.add(getScoreThresholdSlider(), null);
            searchPanel.add(jLabel, null);
            searchPanel.add(getMaxResultsTextArea(), null);
        }
        return searchPanel;
    }

    /**
     * This method initializes resultsPerPageComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getResultsPerPageComboBox() {
        if (resultsPerPageComboBox == null) {
            resultsPerPageComboBox = new JComboBox();
            resultsPerPageComboBox.setPreferredSize(new Dimension(200, 18));
        }
        return resultsPerPageComboBox;
    }

    /**
     * This method initializes scorerCfgComboBox
     * 
     * @return javax.swing.JComboBox
     */
    private JComboBox getScorerCfgComboBox() {
        if (scorerCfgComboBox == null) {
            scorerCfgComboBox = new JComboBox();
            scorerCfgComboBox.setPreferredSize(new Dimension(200, 18));
        }
        return scorerCfgComboBox;
    }

    /**
     * This method initializes scoreThresholdSlider
     * 
     * @return javax.swing.JSlider
     */
    private JSlider getScoreThresholdSlider() {
        if (scoreThresholdSlider == null) {
            scoreThresholdSlider = new JSlider();
            scoreThresholdSlider.setValue(1);
            scoreThresholdSlider.addChangeListener(new javax.swing.event.ChangeListener() {
                public void stateChanged(javax.swing.event.ChangeEvent e) {
                    scoreThresholdLabel.setText("Score Threshold : " + scoreThresholdSlider.getValue());
                }
            });
        }
        return scoreThresholdSlider;
    }

    /**
     * This method initializes maxResultsTextArea
     * 
     * @return javax.swing.JTextArea
     */
    private JTextArea getMaxResultsTextArea() {
        if (maxResultsTextArea == null) {
            maxResultsTextArea = new JTextArea();
            maxResultsTextArea.setPreferredSize(new Dimension(100, 16));
            maxResultsTextArea.setText("0");
        }
        return maxResultsTextArea;
    }


    /**
     * This method initializes jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();

        }
        return jTabbedPane;
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
