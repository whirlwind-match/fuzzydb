package com.wwm.indexer.demo;

import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;
import java.util.Map.Entry;

import com.wwm.context.JVMAppListener;
import com.wwm.indexer.IndexerFactory;
import com.wwm.indexer.Record;
import com.wwm.indexer.demo.internal.WhirlwindCommon;
import com.wwm.indexer.internal.RecordImpl;
import com.wwm.indexer.internal.random.RandomGenerator;
import com.wwm.model.attributes.Attribute;
import com.wwm.util.NanoTimer;

public class WhirlwindRandomiser {

    public static interface IProgress {
        void complete(int complete, int total, long elapsedms,
                long totalelapsedms);
    }

    private static final int NUMOBJECTSPERCREATE = 1000;

    private final WhirlwindCommon wCommon;

    Date now = new Date();

    public WhirlwindRandomiser(WhirlwindCommon WCommon) {
        this.wCommon = WCommon;
    }

    public void createRandomEntries(int numEntries, IProgress progress)
    throws Exception {
        NanoTimer total = new NanoTimer();

        int numberToCreate = NUMOBJECTSPERCREATE;
        for (int i = 0; i < numEntries; i += NUMOBJECTSPERCREATE) {

            if (numEntries < (i + NUMOBJECTSPERCREATE)) {
                numberToCreate = numEntries % NUMOBJECTSPERCREATE;
            }

            ArrayList<Record> entries = new ArrayList<Record>();
            for (int j = 0; j < numberToCreate; j++) {
                RecordImpl rec = new RecordImpl("Rec:" + (i*NUMOBJECTSPERCREATE)+j);
                rec.setTitle("Random Data");
                rec.setAttributes(generateProfileAttributes());
                entries.add(rec);
            }

            wCommon.getIndexer().addRecords(entries);

            if (progress != null) {
                progress.complete(i + NUMOBJECTSPERCREATE, numEntries, total
                        .getLapMillis(), (long) total.getMillis());
            }
        }
    }

    private TreeMap<String, Attribute<?>> generateAttributes() throws Exception {
        TreeMap<String, Attribute<?>> attributes = new TreeMap<String, Attribute<?>>();

        for (Entry<String, RandomGenerator> entry : wCommon.getXmlLoader().getDemoCfg().getRandomGenerators().entrySet()) {
            Attribute<?> attr = entry.getValue().next(entry.getKey());
            attributes.put(entry.getKey(), attr);
        }
        return attributes;
    }

    public TreeMap<String, Attribute<?>> generateProfileAttributes() throws Exception {
        TreeMap<String, Attribute<?>> attributes = generateAttributes();
        //		Randomiser r = wCommon.getCfg().getRandomiser();
        //		if (r != null)
        //			r.processIndexItem(attributes);
        return attributes;
    }

    public TreeMap<String, Attribute<?>> generateSearchAttributes() throws Exception {
        TreeMap<String, Attribute<?>> attributes = generateAttributes();
        //		Randomiser r = wCommon.getCfg().getRandomiser();
        //		if (r != null)
        //			r.processSearchItem(attributes);
        return attributes;
    }

    public static void main(String[] args) throws Exception {
        System.out.println(WhirlwindRandomiser.class.getSimpleName() + " Start");

        if (args.length != 3) {
            System.out.println(WhirlwindRandomiser.class.getSimpleName() + " requires two command line argument: <democfg.xml> <storeUrl> <numberOfProfiles>");
            System.exit(1);
        }

        String xmlPath = args[0];
        String storeUrl = args[1];

        JVMAppListener.getInstance().setSingleSession();
        JVMAppListener.getInstance().preRequest();
        IndexerFactory.setCurrentStoreUrl(storeUrl);
        // Use store name as username as we use username as the store
        // in web service.
//        String storeName = WWMDBProtocolHander.getAsURL(storeUrl).getPath();
//        AtomFactory.setCredentials(storeName, "dummy");


        WhirlwindRandomiser randomiser = new WhirlwindRandomiser(new WhirlwindCommon(xmlPath));
        try {
            IProgress p = new IProgress() {
                public void complete(int complete, int total, long elapsedms, long totalelapsedms) {
                    System.out.println(elapsedms + " ms) " + complete + " of " + total + " Created in " + totalelapsedms + " ms" );
                }
            };

            randomiser.createRandomEntries(Integer.valueOf(args[2]), p);
        } catch (NumberFormatException e) {
            System.out.println("The second argument (" + args[2] + ") must be an integer.");
            System.exit(1);
        }

        System.out.println(WhirlwindRandomiser.class.getSimpleName() + " Complete!");
    }

}
