package whirlwind.demo.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Point;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.border.SoftBevelBorder;

import com.wwm.context.JVMAppListener;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.demo.WhirlwindRandomiser;
import com.wwm.indexer.demo.WhirlwindRandomiser.IProgress;
import com.wwm.indexer.demo.internal.WhirlwindCommon;
import com.wwm.indexer.exceptions.IndexerException;


public class WhirlWindDemo extends JFrame {

    private static final long serialVersionUID = -4592298544825215112L;

    private final WhirlwindRandomiser randomiser;

    private JPanel jContentPane = null;
    private JPanel jPanel = null;
    private JLabel freeLabel = null;
    private JLabel maxLabel = null;
    private JLabel totalLabel = null;
    private JLabel usedLabel = null;
    private JLabel entrysLabel = null;
    private JPanel jPanel1 = null;
    private JButton randomButton = null;
    private JTextField randomTextField = null;
    private JProgressBar randomProgressBar = null;
    private JLabel totalmsLabel = null;
    private JLabel splitmsLabel = null;
    private JTabbedPane jTabbedPane = null;

    public WhirlWindDemo(WhirlwindCommon wCommon) throws Exception {
        super();
        this.randomiser = new WhirlwindRandomiser(wCommon);

        initialize();

        // Ugly workaround to allow the VE Editor to work
        getJTabbedPane().addTab("Query", null, new SearchPane(wCommon), null);
        getJTabbedPane().addTab("Benchmark", null, new BenchmarkPane(wCommon, randomiser), null);
        //		getJTabbedPane().addTab("Scorer Config", null, new ScorerConfigPane(WCommon), null);
        updateStats(false);
    }


    private void randomData() {
        getRandomButton().setEnabled(false);
        getRandomTextField().setEnabled(false);

        Thread randomT = new Thread() {
            @Override
            public void run() {
                try {
                    IProgress progress = new IProgress() {
                        public void complete(int complete, int total, long elapsedms, long totalelapsedms) {
                            getRandomProgressBar().setValue((int)(((float)complete / (float)total) * 100));
                            splitmsLabel.setText("Split ms: " + elapsedms);
                            totalmsLabel.setText("Total ms: " + totalelapsedms);
                        }
                    };
                    randomiser.createRandomEntries(Integer.valueOf(randomTextField.getText()), progress);
                } catch (Throwable e) {
                    e.printStackTrace();
                } // FIXME: Document this exception
                finally {
                    getRandomButton().setEnabled(true);
                    getRandomTextField().setEnabled(true);
                    updateStats(false);
                }
            }
        };
        randomT.setDaemon(true);
        randomT.start();
    }



    /**
     * This method initialises this
     * 
     * @return void
     */
    private void initialize() {
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new java.awt.Dimension(830,450));
        this.setSize(931, 522);
        this.setContentPane(getJContentPane());
        this.setTitle("WhirlwindDemo");
    }

    /**
     * This method initialises jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getJTabbedPane(), BorderLayout.CENTER);
            jContentPane.add(getJPanel(), BorderLayout.SOUTH);
            jContentPane.add(getJPanel1(), BorderLayout.NORTH);
        }
        return jContentPane;
    }

    /**
     * This method initialises jPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel() {
        if (jPanel == null) {
            entrysLabel = new JLabel();
            entrysLabel.setBounds(new java.awt.Rectangle(5,0,148,20));
            entrysLabel.setText("Entries: ");
            entrysLabel.setPreferredSize(new Dimension(148, 20));
            usedLabel = new JLabel();
            usedLabel.setBounds(new java.awt.Rectangle(155,0,105,20));
            usedLabel.setText("Used: ");
            usedLabel.setPreferredSize(new Dimension(148, 20));
            totalLabel = new JLabel();
            totalLabel.setBounds(new java.awt.Rectangle(490,0,106,20));
            totalLabel.setText("Total: ");
            totalLabel.setPreferredSize(new Dimension(148, 20));
            maxLabel = new JLabel();
            maxLabel.setBounds(new java.awt.Rectangle(380,0,106,21));
            maxLabel.setText("Max: ");
            maxLabel.setPreferredSize(new Dimension(148, 20));
            freeLabel = new JLabel();
            freeLabel.setBounds(new java.awt.Rectangle(265,0,106,20));
            freeLabel.setText("Free: ");
            freeLabel.setPreferredSize(new Dimension(148, 20));
            jPanel = new JPanel();
            jPanel.setLayout(null);
            jPanel.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
            jPanel.setPreferredSize(new java.awt.Dimension(1,20));
            jPanel.add(freeLabel, null);
            jPanel.add(maxLabel, null);
            jPanel.add(totalLabel, null);
            jPanel.add(usedLabel, null);
            jPanel.add(entrysLabel, null);
        }
        return jPanel;
    }


    /**
     * This method initialises jPanel1
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJPanel1() {
        if (jPanel1 == null) {
            splitmsLabel = new JLabel();
            splitmsLabel.setBounds(new java.awt.Rectangle(450,0,136,20));
            splitmsLabel.setText("Split ms:");
            splitmsLabel.setPreferredSize(new Dimension(136, 20));
            totalmsLabel = new JLabel();
            totalmsLabel.setText("Total ms:");
            totalmsLabel.setSize(new java.awt.Dimension(136,20));
            totalmsLabel.setLocation(new java.awt.Point(600,0));
            totalmsLabel.setPreferredSize(new java.awt.Dimension(136,20));
            jPanel1 = new JPanel();
            jPanel1.setLayout(null);
            jPanel1.setPreferredSize(new Dimension(1, 20));
            jPanel1.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
            jPanel1.add(getRandomButton(), null);
            jPanel1.add(getRandomTextField(), null);
            jPanel1.add(getRandomProgressBar(), null);
            jPanel1.add(totalmsLabel, null);
            jPanel1.add(splitmsLabel, null);
        }
        return jPanel1;
    }


    /**
     * This method initialises jButton
     * 
     * @return javax.swing.JButton
     */
    private JButton getRandomButton() {
        if (randomButton == null) {
            randomButton = new JButton();
            randomButton.setPreferredSize(new Dimension(130, 16));
            randomButton.setSize(new Dimension(130, 16));
            randomButton.setLocation(new Point(0, 2));
            randomButton.setText("Random Entries");
            randomButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    randomData();
                }
            });
        }
        return randomButton;
    }


    /**
     * This method initialises randomTextField
     * 
     * @return javax.swing.JTextField
     */
    private JTextField getRandomTextField() {
        if (randomTextField == null) {
            randomTextField = new JTextField();
            randomTextField.setPreferredSize(new java.awt.Dimension(110,18));
            randomTextField.setLocation(new java.awt.Point(130,0));
            randomTextField.setText("1000");
            randomTextField.setSize(new java.awt.Dimension(110,18));
            randomTextField.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    randomData();
                }
            });
        }
        return randomTextField;
    }


    /**
     * This method initialises randomProgressBar
     * 
     * @return javax.swing.JProgressBar
     */
    private JProgressBar getRandomProgressBar() {
        if (randomProgressBar == null) {
            randomProgressBar = new JProgressBar();
            randomProgressBar.setPreferredSize(new java.awt.Dimension(200,18));
            randomProgressBar.setSize(new java.awt.Dimension(200,18));
            randomProgressBar.setStringPainted(true);
            randomProgressBar.setForeground(new java.awt.Color(247,16,201));
            randomProgressBar.setLocation(new java.awt.Point(240,0));
        }
        return randomProgressBar;
    }

    /**
     * This method initialises jTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getJTabbedPane() {
        if (jTabbedPane == null) {
            jTabbedPane = new JTabbedPane();
            jTabbedPane.setPreferredSize(new Dimension(300, 300));
            //			jTabbedPane.addTab("Query", null, new SearchPane(WCommon, query), null);
            //			jTabbedPane.addTab("Benchmark", null, new BenchmarkPane(WCommon, randomiser, query), null);
        }
        return jTabbedPane;
    }


    
	private void updateStats(boolean gc) {
		long entries;
		try {
			entries = IndexerFactory.getIndexer().getCount();
			entrysLabel.setText("Entries: " + entries );
		} catch (IndexerException e) {
			e.printStackTrace();
		}

//    	ServerStats stats = IndexerFactory.getCurrentStore().getStats(gc);
//        if (stats == null) {
//            return;
//        }
//        freeLabel.setText("Free: " + (stats.getFree()/(1024*1024)) + "MB");
//        maxLabel.setText("Max: " + (stats.getMax()/(1024*1024)) + "MB");
//        totalLabel.setText("Total: " + (stats.getTotal()/(1024*1024)) + "MB");
//        usedLabel.setText("Used: " + (stats.getUsed()/(1024*1024)) + "MB");
	}

    
    
    private static WhirlWindDemo application;

    /**
     * Launches this application
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        if( args.length < 2 ) {
            System.out.println("Usage : " +WhirlWindDemo.class.getSimpleName() + " : <path to xml> <wwmdb:[//host[:port]]/storeName>");
            System.exit(1);
        }

        String xmlPath = args[0];
        String storeUrl = args[1];

        JVMAppListener.getInstance().setSingleSession();
        JVMAppListener.getInstance().preRequest();
        IndexerFactory.setCurrentStoreUrl(storeUrl);
        // Use store name as username as we use username as the store
        // in web service.
//        String userName = WWMDBProtocolHander.getAsURL(storeUrl).getPath().substring(1);
//        AtomFactory.setCredentials(userName, "dummy");


        application = new WhirlWindDemo(new WhirlwindCommon(xmlPath));
        application.setVisible(true);
    }
}  //  @jve:decl-index=0:visual-constraint="10,10"