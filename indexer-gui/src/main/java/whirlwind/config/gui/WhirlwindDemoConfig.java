package whirlwind.config.gui;

import java.util.ArrayList;
import java.util.TreeMap;

import org.fuzzydb.client.Client;
import org.fuzzydb.dto.attributes.RandomGenerator;

import whirlwind.demo.gui.searchtabs.SearchTab;
import whirlwind.demo.gui.searchtabs.SearchTabXmlConfig;

import com.wwm.indexer.demo.internal.Randomiser;

public class WhirlwindDemoConfig {

    private ArrayList<String> randomAttributes;

    private String primaryScorerCfg;

    private String randomiser;
    private Randomiser randInst;
    private TreeMap<String, RandomGenerator<?>> randomGenerators;
    private ArrayList<SearchTabXmlConfig> searchTabs;
    //	private TreeMap<String, BaseFormatter> formatters;

    public ArrayList<String> getRandomAttributes() {
        return randomAttributes;
    }

    public Randomiser getRandomiser() {
        if (randInst != null) {
            return randInst;
        }
        if (randomiser == null || randomiser.equals("")) {
            return null;
        }

        Class<?> clazz;
        Object o;
        try {
            clazz = ClassLoader.getSystemClassLoader().loadClass(randomiser);
            o = clazz.newInstance();
        } catch (Exception e) {
            throw new Error(e);
        }
        randInst = (Randomiser) o;
        return randInst;
    }

    public SearchTab getSearchTab(String className) {
        if (className == null || className.equals("")) {
            return null;
        }
        try {
            Class<?> clazz = getClass().getClassLoader().loadClass(className);
            Object o = clazz.newInstance();
            return (SearchTab) o;
        } catch (Exception e) {
            throw new Error(e);
        }
    }

    public void setClient(Client client) {
        randInst = getRandomiser();
        if (randInst != null) {
            randInst.setClient(client);
            randInst.setWhirlwindDemoConfig(this);
        }
    }

    public ArrayList<SearchTabXmlConfig> getSearchTabs() {
        return searchTabs;
    }

    public String getPrimaryScorerCfg() {
        return primaryScorerCfg;
    }

    //	public TreeMap<String, BaseFormatter> getFormatters() {
    //		return formatters;
    //	}

    public TreeMap<String, RandomGenerator<?>> getRandomGenerators() {
        return randomGenerators;
    }
}
