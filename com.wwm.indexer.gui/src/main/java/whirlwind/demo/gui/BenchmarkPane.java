package whirlwind.demo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.table.JTableHeader;

import whirlwind.demo.actions.InsertBenchmark;
import whirlwind.demo.actions.QueryBenchmark;

import com.wwm.indexer.demo.WhirlwindBenchmark;
import com.wwm.indexer.demo.WhirlwindRandomiser;
import com.wwm.indexer.demo.internal.WhirlwindCommon;
import com.wwm.indexer.demo.internal.WhirlwindQuery.QueryConfig;
import com.wwm.util.gui.table.TableModel;
import com.wwm.util.gui.table.TableSorter;

public class BenchmarkPane extends JPanel {

    private static final long serialVersionUID = 1L;
    private JPanel benchmarkCfgPanel = null;
    private JButton benchmarkButton = null;
    private JTextField numQueryThreadsTextField = null;
    private JTextField numInsertThreadsTextField = null;
    private JTextField numDeleteThreadsTextField = null;
    private JLabel jLabelNumQueryThreads = null;
    private JLabel jLabelNumRandSpecs = null;
    private JTextField numSpecsTextField = null;
    private JLabel jLabelNumActions = null;
    private JTextField numQueriesTextField = null;
    private JLabel jLabelPagesPerQuery = null;
    private JTextField numPagesPerQueryTextField = null;
    private JLabel jLabelResultsPerPage = null;
    private JTextField ResultsPerPageTextField = null;
    private JButton stopBenchmarkButton = null;
    private JProgressBar benchmarkProgressBar = null;
    private JScrollPane benchmarkScrollPane = null;
    private JTable benchmarkTable = null;
    private TableModel defaultTableModel = null;
    private JTableHeader jTableHeader = null;
    private JComboBox scorerCfgComboBox = null;
    private JLabel scoreThresholdLabel = null;
    private JSlider scoreThresholdSlider = null;
    private JLabel jLabel = null;
    private JTextArea maxResultsTextArea = null;
    private String[] columnToolTips;


    // -----------------------------------------------------
    // My Attributes
    // -----------------------------------------------------
    private WhirlwindCommon wCommon;
    private WhirlwindRandomiser randomiser;

    private ArrayList<WhirlwindBenchmark> currentBenchmarks;  //  @jve:decl-index=0:
    // -----------------------------------------------------

    private int numberOfBenchMarks;
    private JLabel jLabelNumInsertThreads = null;
    private JLabel jLabelNumDeleteThreads = null;

    /**
     * This is the default constructor
     */
    public BenchmarkPane(WhirlwindCommon WCommon, WhirlwindRandomiser randomiser) {
        super();
        this.wCommon = WCommon;
        this.randomiser = randomiser;
        initialize();

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
            getScorerCfgComboBox().addItem(new JDataItem(name + " Scorer Config", name));
        }
        getScorerCfgComboBox().setSelectedIndex(select);


        // MUST SET THE COL COUNT FIRST!
        getDefaultTableModel().setColumnCount(7);
        int index = 0;
        getDefaultTableModel().configureColumn(index++, "#Thrd", 25, 70, 70);
        getDefaultTableModel().configureColumn(index++, "Thrd type", 25, 70, 70);
        getDefaultTableModel().configureColumn(index++, "Actions", 25, 70, 70);
        getDefaultTableModel().configureColumn(index++, "Min Action ms", 25, 150, 150);
        getDefaultTableModel().configureColumn(index++, "Max Action ms", 25, 150, 150);
        getDefaultTableModel().configureColumn(index++, "Avg ms Action", 25, 150, 150);
        getDefaultTableModel().configureColumn(index++, "Avg ms Next Action", 25, 150, 150);

        setColumnToolTips();
    }


    // -----------------------------------------------------
    // My functions
    // -----------------------------------------------------
    int getIntegerValue(JTextField textField, int defaultVal) {
        try {
            String textval = textField.getText();
            if (textval.length() != 0) {
                return Integer.valueOf(textval);
            }
        } catch (NumberFormatException e) {
            textField.setText(String.valueOf(defaultVal));
        }
        return defaultVal;
    }

    private Object getValue(JComboBox box) {
        JDataItem i = (JDataItem) box.getSelectedItem();
        return i.data;
    }

    void doBenchMark() {

        int numberOfQueryThreads = getIntegerValue(getNumQueryThreadsTextField(), 1);
        int numberOfInsertThreads = getIntegerValue(getNumInsertThreadsTextField(), 1);
        int numberOfDeleteThreads = getIntegerValue(getNumDeleteThreadsTextField(), 1);
        int numberOfSpecs = getIntegerValue(getNumSpecsTextField(), 500);
        int numberOfQueries = getIntegerValue(getNumActionsTextField(), 500);
        int numberOfPagesPerQuery = getIntegerValue(getNumPagesPerQueryTextField(), 1);
        int numberOfResultsPerPage = getIntegerValue(getResultsPerPageTextField(), 10);

        while(getDefaultTableModel().getRowCount() !=0) {
            getDefaultTableModel().removeRow(0);
        }
        currentBenchmarks = new ArrayList<WhirlwindBenchmark>();

        // ------------------------------------------------------
        // Num Threads
        // ------------------------------------------------------
        for (int i = 0; i < numberOfQueryThreads; i++) {
            addQueryBenchmark(numberOfSpecs, numberOfResultsPerPage, numberOfQueries, numberOfPagesPerQuery);
        }

        for (int i = 0; i < numberOfInsertThreads; i++) {
            addInsertBenchmark(numberOfQueries);
        }

        for (int i = 0; i < numberOfDeleteThreads; i++) {
            addDeleteBenchmark();
        }

        // ------------------------------------------------------

        getBenchmarkButton().setEnabled(false);
        getStopBenchmarkButton().setEnabled(true);

        // ------------------------------------------------------
        // Start All the Threads
        // ------------------------------------------------------
        for (WhirlwindBenchmark bench: currentBenchmarks) {
            bench.runBenchmark();
        }

        // ------------------------------------------------------

        // ------------------------------------------------------
        // kick off a thread to monitor the Benchmarks and report
        // When all done
        // ------------------------------------------------------
        Thread benchMonitor = new Thread() {
            @Override
            public void run() {

                getBenchmarkProgressBar().setValue(0);

                boolean running = true;
                while (running) {
                    try { Thread.sleep(1500); } catch (InterruptedException e) { e.printStackTrace(); } // FIXME: Document this exception

                    float maxActions = 0;
                    float currentQueries = 0;

                    running = false;
                    for (WhirlwindBenchmark bench: currentBenchmarks) {
                        maxActions += bench.getNumberOfActions();
                        currentQueries += bench.getNumberOfActionsSoFar();

                        if (bench.isRunning()) {
                            running = true;
                        }
                    }


                    getBenchmarkProgressBar().setValue((int)((currentQueries/maxActions) *100));
                }

                summariseBenchmarks();

                getBenchmarkButton().setEnabled(true);
                getStopBenchmarkButton().setEnabled(false);
            }

            private void summariseBenchmarks() {

                int numberOfFirstPages = 0;
                float totalFirstPageTimeMs = 0;

                int numberOfNextPages = 0;
                float totalNextPageTimeMs = 0;

                float minTimeMs = Float.MAX_VALUE;
                float maxTimeMs = Float.MIN_VALUE;

                int i = 0;
                for (WhirlwindBenchmark bench: currentBenchmarks) {
                    i++;

                    int numSecondBench = 0;
                    float timeSecondBench = 0f;
                    float aveSecond = 0f;
                    if (bench instanceof QueryBenchmark){
                        QueryBenchmark queryBench = (QueryBenchmark) bench;
                        numSecondBench = queryBench.getNumberOfNextPages();
                        timeSecondBench = queryBench.getTotalNextPageTimeMs();
                        aveSecond = queryBench.getTotalNextPageTimeMs() / queryBench.getNumberOfNextPages();
                    }

                    Object[] tableRow = {bench.getId(i),
                            bench.getThreadType(),
                            bench.getNumberOfActionsSoFar() + numSecondBench,
                            bench.getMinTimeMs(),
                            bench.getMaxTimeMs(),
                            (bench.getTotalActionTimeMs() / bench.getNumberOfActionsSoFar()),
                            aveSecond
                    };
                    getDefaultTableModel().addRow(tableRow);

                    totalFirstPageTimeMs += bench.getTotalActionTimeMs();
                    numberOfFirstPages += bench.getNumberOfActionsSoFar();

                    totalNextPageTimeMs += timeSecondBench;
                    numberOfNextPages += numSecondBench;

                    if (bench.getMinTimeMs() < minTimeMs) {
                        minTimeMs = bench.getMinTimeMs();
                    }
                    if (bench.getMaxTimeMs() > maxTimeMs) {
                        maxTimeMs = bench.getMaxTimeMs();
                    }
                }

                Object[] tablerow = {"Total",
                        "All",
                        (numberOfFirstPages + numberOfNextPages),
                        minTimeMs,
                        maxTimeMs,
                        (totalFirstPageTimeMs / numberOfFirstPages),
                        (totalNextPageTimeMs / numberOfNextPages)};
                getDefaultTableModel().addRow(tablerow);
            }

            //            private void monitorInserts() {
            //
            //                int numberOfInserts = 0;
            //                float totalInsertTimeMs = 0;
            //
            //                float minTimeMs = Float.MAX_VALUE;
            //                float maxTimeMs = Float.MIN_VALUE;
            //
            //                int i = 0;
            //                for (InsertBenchmark bench: currentInsertBenchMarks) {
            //                    i++;
            //
            //                    Object[] tablerow = {i,
            //                            bench.getNumberOfInserts(),
            //                            bench.getMinTimeMs(),
            //                            bench.getMaxTimeMs(),
            //                            (bench.getTotalActionTimeMs() / bench.getNumberOfInserts()),
            //                            "-",
            //                    };
            //                    getDefaultTableModel().addRow(tablerow);
            //
            //                    totalInsertTimeMs += bench.getTotalActionTimeMs();
            //                    numberOfInserts += bench.getNumberOfInserts();
            //
            //                    if (bench.getMinTimeMs() < minTimeMs) minTimeMs = bench.getMinTimeMs();
            //                    if (bench.getMaxTimeMs() > maxTimeMs) maxTimeMs = bench.getMaxTimeMs();
            //                }
            //
            //                Object[] tablerow = {-1,
            //                        numberOfInserts,
            //                        minTimeMs,
            //                        maxTimeMs,
            //                        (totalInsertTimeMs / numberOfInserts)};
            //                getDefaultTableModel().addRow(tablerow);
            //            }
            //
            //            private void monitorDeletes() {
            //
            //            }

        };

        benchMonitor.start();
        // ------------------------------------------------------
    }

    void addQueryBenchmark(int numberOfSpecs, int numberOfResultsPerPage, int numberOfQueries, int numberOfPagesPerQuery) {
        //      ------------------------------------------------------
        // Number Of Query Specs
        // ------------------------------------------------------
        ArrayList<QueryConfig> specs = new ArrayList<QueryConfig>();

        for (int j = 0; j < numberOfSpecs; j++) {
            QueryConfig cfg = new QueryConfig();
            cfg.scorerConfig = (String) getValue(getScorerCfgComboBox());

            cfg.pageSize = numberOfResultsPerPage;
            cfg.scoreThreshold = (getScoreThresholdSlider().getValue()) / 100.0f;

            // Random Query Spec
            try {
                cfg.setAttributes(randomiser.generateSearchAttributes());
            } catch (Exception e) {
                //              benchmarkTextArea.setText(e.toString());
                return;
            }
            specs.add(cfg);
        }
        // ------------------------------------------------------

        currentBenchmarks.add(new QueryBenchmark(wCommon, numberOfQueries, numberOfPagesPerQuery, specs));
        numberOfBenchMarks++;
    }

    void addInsertBenchmark(int numberOfInserts) {
        currentBenchmarks.add( new InsertBenchmark(wCommon, numberOfInserts));
    }

    void addDeleteBenchmark() {

    }

    void stopBenchMark() {
        getStopBenchmarkButton().setEnabled(false);
        if (numberOfBenchMarks == 0) {
            return;
        }
        for (WhirlwindBenchmark bench: currentBenchmarks) {
            bench.stopBenchMark();
        }
    }
    // -----------------------------------------------------



    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(674, 403);
        this.setLayout(new BorderLayout());
        this.add(getBenchmarkCfgPanel(), java.awt.BorderLayout.WEST);
        this.add(getBenchmarkScrollPane(), java.awt.BorderLayout.CENTER);
    }

    /**
     * This method initializes benchmarkCfgPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getBenchmarkCfgPanel() {
        if (benchmarkCfgPanel == null) {
            jLabelNumDeleteThreads = new JLabel();
            jLabelNumDeleteThreads.setPreferredSize(new Dimension(100, 18));
            jLabelNumDeleteThreads.setHorizontalTextPosition(SwingConstants.CENTER);
            jLabelNumDeleteThreads.setText("#Delete Threads");
            jLabelNumDeleteThreads.setHorizontalAlignment(SwingConstants.CENTER);
            jLabelNumInsertThreads = new JLabel();
            jLabelNumInsertThreads.setPreferredSize(new Dimension(100, 18));
            jLabelNumInsertThreads.setHorizontalTextPosition(SwingConstants.CENTER);
            jLabelNumInsertThreads.setText("#Insert Threads");
            jLabelNumInsertThreads.setHorizontalAlignment(SwingConstants.CENTER);
            jLabel = new JLabel();
            jLabel.setPreferredSize(new Dimension(90, 16));
            jLabel.setText("Max Results ");
            scoreThresholdLabel = new JLabel();
            scoreThresholdLabel.setPreferredSize(new Dimension(200, 16));
            scoreThresholdLabel.setText("Score Threshold : 1");

            jLabelResultsPerPage = new JLabel();
            jLabelResultsPerPage.setPreferredSize(new Dimension(100, 18));
            jLabelResultsPerPage.setHorizontalAlignment(SwingConstants.CENTER);
            jLabelResultsPerPage.setHorizontalTextPosition(SwingConstants.CENTER);
            jLabelResultsPerPage.setText("Results Per Page");
            jLabelPagesPerQuery = new JLabel();
            jLabelPagesPerQuery.setPreferredSize(new Dimension(100, 18));
            jLabelPagesPerQuery.setHorizontalAlignment(SwingConstants.CENTER);
            jLabelPagesPerQuery.setHorizontalTextPosition(SwingConstants.CENTER);
            jLabelPagesPerQuery.setText("Pages Per Query");
            jLabelNumActions = new JLabel();
            jLabelNumActions.setPreferredSize(new Dimension(100, 18));
            jLabelNumActions.setHorizontalAlignment(SwingConstants.CENTER);
            jLabelNumActions.setHorizontalTextPosition(SwingConstants.CENTER);
            jLabelNumActions.setText("# Actions");
            jLabelNumRandSpecs = new JLabel();
            jLabelNumRandSpecs.setPreferredSize(new Dimension(100, 18));
            jLabelNumRandSpecs.setHorizontalAlignment(SwingConstants.CENTER);
            jLabelNumRandSpecs.setHorizontalTextPosition(SwingConstants.CENTER);
            jLabelNumRandSpecs.setText("# Random Specs");
            jLabelNumQueryThreads = new JLabel();
            jLabelNumQueryThreads.setPreferredSize(new Dimension(100, 18));
            jLabelNumQueryThreads.setHorizontalAlignment(SwingConstants.CENTER);
            jLabelNumQueryThreads.setHorizontalTextPosition(SwingConstants.CENTER);
            jLabelNumQueryThreads.setText("#Query Threads");
            benchmarkCfgPanel = new JPanel();
            benchmarkCfgPanel.setLayout(new FlowLayout());
            benchmarkCfgPanel.setPreferredSize(new Dimension(200, 100));

            benchmarkCfgPanel.add(jLabelNumActions, null);
            benchmarkCfgPanel.add(getNumActionsTextField(), null);
            benchmarkCfgPanel.add(jLabelNumInsertThreads, null);
            benchmarkCfgPanel.add(getNumInsertThreadsTextField(), null);
            benchmarkCfgPanel.add(jLabelNumDeleteThreads, null);
            benchmarkCfgPanel.add(getNumDeleteThreadsTextField(), null);
            benchmarkCfgPanel.add(jLabelNumQueryThreads, null);
            benchmarkCfgPanel.add(getNumQueryThreadsTextField(), null);
            benchmarkCfgPanel.add(jLabelNumRandSpecs, null);
            benchmarkCfgPanel.add(getNumSpecsTextField(), null);
            benchmarkCfgPanel.add(jLabelPagesPerQuery, null);
            benchmarkCfgPanel.add(getNumPagesPerQueryTextField(), null);
            benchmarkCfgPanel.add(jLabelResultsPerPage, null);
            benchmarkCfgPanel.add(getResultsPerPageTextField(), null);
            benchmarkCfgPanel.add(getScorerCfgComboBox(), null);
            benchmarkCfgPanel.add(scoreThresholdLabel, null);
            benchmarkCfgPanel.add(getScoreThresholdSlider(), null);
            benchmarkCfgPanel.add(jLabel, null);
            benchmarkCfgPanel.add(getMaxResultsTextArea(), null);
            benchmarkCfgPanel.add(getBenchmarkButton(), null);
            benchmarkCfgPanel.add(getStopBenchmarkButton(), null);
            benchmarkCfgPanel.add(getBenchmarkProgressBar(), null);

        }
        return benchmarkCfgPanel;
    }

    /**
     * This method initializes benchmarkButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getBenchmarkButton() {
        if (benchmarkButton == null) {
            benchmarkButton = new JButton();
            benchmarkButton.setPreferredSize(new Dimension(200, 18));
            benchmarkButton.setText("Run Benchmark");
            benchmarkButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    doBenchMark();
                }
            });
        }
        return benchmarkButton;
    }

    /**
     * This method initializes numQueryThreadsThreadsTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNumQueryThreadsTextField() {
        if (numQueryThreadsTextField == null) {
            numQueryThreadsTextField = new JTextField();
            numQueryThreadsTextField.setPreferredSize(new Dimension(90, 18));
            numQueryThreadsTextField.setText("1");
        }
        return numQueryThreadsTextField;
    }

    /**
     * This method initializes numInsertThreadsThreadsTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNumInsertThreadsTextField() {
        if (numInsertThreadsTextField == null) {
            numInsertThreadsTextField = new JTextField();
            numInsertThreadsTextField.setPreferredSize(new Dimension(90, 18));
            numInsertThreadsTextField.setText("0");
        }
        return numInsertThreadsTextField;
    }

    /**
     * This method initializes numInsertThreadsThreadsTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNumDeleteThreadsTextField() {
        if (numDeleteThreadsTextField == null) {
            numDeleteThreadsTextField = new JTextField();
            numDeleteThreadsTextField.setPreferredSize(new Dimension(90, 18));
            numDeleteThreadsTextField.setText("0");
        }
        return numDeleteThreadsTextField;
    }


    /**
     * This method initializes numSpecsTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNumSpecsTextField() {
        if (numSpecsTextField == null) {
            numSpecsTextField = new JTextField();
            numSpecsTextField.setPreferredSize(new Dimension(90, 18));
            numSpecsTextField.setText("500");
        }
        return numSpecsTextField;
    }

    /**
     * This method initializes numQueriesTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNumActionsTextField() {
        if (numQueriesTextField == null) {
            numQueriesTextField = new JTextField();
            numQueriesTextField.setPreferredSize(new Dimension(90, 18));
            numQueriesTextField.setText("500");
        }
        return numQueriesTextField;
    }

    /**
     * This method initializes numPagesPerQueryTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getNumPagesPerQueryTextField() {
        if (numPagesPerQueryTextField == null) {
            numPagesPerQueryTextField = new JTextField();
            numPagesPerQueryTextField.setPreferredSize(new Dimension(90, 18));
            numPagesPerQueryTextField.setText("1");
        }
        return numPagesPerQueryTextField;
    }

    /**
     * This method initializes ResultsPerPageTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getResultsPerPageTextField() {
        if (ResultsPerPageTextField == null) {
            ResultsPerPageTextField = new JTextField();
            ResultsPerPageTextField.setPreferredSize(new Dimension(90, 18));
            ResultsPerPageTextField.setText("10");
        }
        return ResultsPerPageTextField;
    }

    /**
     * This method initializes stopBenchmarkButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopBenchmarkButton() {
        if (stopBenchmarkButton == null) {
            stopBenchmarkButton = new JButton();
            stopBenchmarkButton.setEnabled(false);
            stopBenchmarkButton.setPreferredSize(new Dimension(200, 18));
            stopBenchmarkButton.setText("Stop Benchmark");
            stopBenchmarkButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    stopBenchMark();
                }
            });
        }
        return stopBenchmarkButton;
    }

    /**
     * This method initializes benchmarkProgressBar
     * 
     * @return javax.swing.JProgressBar
     */
    private JProgressBar getBenchmarkProgressBar() {
        if (benchmarkProgressBar == null) {
            benchmarkProgressBar = new JProgressBar();
        }
        return benchmarkProgressBar;
    }

    /**
     * This method initializes benchmarkScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getBenchmarkScrollPane() {
        if (benchmarkScrollPane == null) {
            benchmarkScrollPane = new JScrollPane();
            benchmarkScrollPane.setViewportView(getBenchmarkTable());
        }
        return benchmarkScrollPane;
    }

    /**
     * This method initializes benchmarkTable
     * 
     * @return javax.swing.JTable
     */
    private JTable getBenchmarkTable() {
        if (benchmarkTable == null) {

            benchmarkTable = new JTable() {
                private static final long serialVersionUID = 1L;

                @Override
                protected JTableHeader createDefaultTableHeader() {

                    return new JTableHeader(columnModel) {
                        private static final long serialVersionUID = 1L;

                        @Override
                        public String getToolTipText(java.awt.event.MouseEvent e) {
                            int x = e.getX();
                            int index = columnModel.getColumnIndexAtX(x);
                            int realIndex =
                                columnModel.getColumn(index).getModelIndex();
                            return columnToolTips[realIndex];
                        }
                    };
                }

            };
            benchmarkTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
            benchmarkTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            TableSorter sorter = new TableSorter(getDefaultTableModel());
            sorter.setTableHeader(getJTableHeader());
            benchmarkTable.setModel(sorter);
        }
        return benchmarkTable;
    }

    /**
     * This method initializes defaultTableModel
     * 
     * @return adminTool.utils.TableModel
     */
    private TableModel getDefaultTableModel() {
        if (defaultTableModel == null) {
            defaultTableModel = new TableModel(getBenchmarkTable());
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
            jTableHeader = getBenchmarkTable().getTableHeader();
        }
        return jTableHeader;
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
     * This method sets the colum tool tip text
     */

    private void setColumnToolTips() {

        columnToolTips = new String[] {
                "The ID of the thread.",
                "The type of thread. e.g. a query thread.",
                "Number of actions performed for the thread in question.",
                "Minumum action time for the thread.",
                "Maximum action time for the thread.",
                "Average action time for the thread.",
                "<html>"+"Average time for a subsequent action if one performed."+"<br>"
                +"ie. if a query thread is producing more than one page per query"+"</html>"
        };
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"
