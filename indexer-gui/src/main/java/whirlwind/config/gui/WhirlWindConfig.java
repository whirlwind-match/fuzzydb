package whirlwind.config.gui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.wwm.attrs.WhirlwindConfiguration;
import com.wwm.attrs.userobjects.StandaloneWWIndexData;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.config.XmlLoader;

public class WhirlWindConfig extends JFrame {

    private static final long serialVersionUID = 1L;

    private JPanel jContentPane = null;

    private JTabbedPane configTabsTabbedPane = null;

    private XmlLoader loader;

    // FIXME : Read from DB
    private WhirlwindConfiguration conf = new WhirlwindConfiguration(StandaloneWWIndexData.class);

    public static void main(String[] args) {


        WhirlWindConfig app = new WhirlWindConfig(args[0], args[1]);
        app.setVisible(true);
    }

    /**
     * This is the default constructor
     */
    public WhirlWindConfig(String username, String xmlPath) {
        super();
        initialize();

        String url = "wwmdb:/" + username;
        IndexerFactory.setCurrentStoreUrl(url);

        loader = new XmlLoader(xmlPath, conf);

        getConfigTabsTabbedPane().addTab("Enums", null, new ConfigEnums(loader), null);
    }

    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        this.setSize(786, 364);
        this.setContentPane(getJContentPane());
        this.setTitle("JFrame");
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new JPanel();
            jContentPane.setLayout(new BorderLayout());
            jContentPane.add(getConfigTabsTabbedPane(), BorderLayout.CENTER);
        }
        return jContentPane;
    }

    /**
     * This method initializes configTabsTabbedPane
     * 
     * @return javax.swing.JTabbedPane
     */
    private JTabbedPane getConfigTabsTabbedPane() {
        if (configTabsTabbedPane == null) {
            configTabsTabbedPane = new JTabbedPane();
        }
        return configTabsTabbedPane;
    }

}  //  @jve:decl-index=0:visual-constraint="10,10"
