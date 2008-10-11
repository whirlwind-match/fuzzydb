/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db.core;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.management.ManagementFactory;
import java.net.InetSocketAddress;
import java.util.Properties;

import javax.management.MBeanServer;
import javax.management.ObjectName;


/**
 *
 */
public class Settings implements SettingsMBean {
	public enum ScorerVersion {
		v1, v2, compact
	}

	private static Settings instance = null; 
	
    private Properties props = new Properties();

	// DbClient Configs
	private int primaryServerPort = 27601;		// e.g. Authoritative 	(used by DbClient)
	private int secondaryServerPort = 27601;	// e.g. read-only		(used by DbClient)
	private String primaryServer = "127.0.0.1";
	private String secondaryServer = null;
	private int defaultTargetNumResults = 250;
	private float defaultScoreThreshold = 0.0f;
	private String defaultStore = null; // no longer a default. Must set in .properties or from app "SearchDemo";
   
    // Postcode Data Configs
    private String postcodeRootWin = "c:\\apps\\wwm-postcode";
    private String postcodeRootLinux = "/lmdb/lmpostcode";

    // IPLookup Data Configs
    private String iplookupRootWin = "c:\\apps\\wwm-iplookup";
    private String iplookupRootLinux = "/lmdb/lmiplookup";

	// DbServer Configs
    private boolean dbCleanOnStart = false; // NOTE: true deletes database dirs EVERY time
	private String dbRootWin = "c:\\apps\\wwm-db";
	private String dbRootLinux = "/lmdb/db";
	private String reposDir = "repos";
	private String txDir = "tx";
	private String logDir = "log";
	private int listenPort = 27601;
	private int shutdownPort = 26001;
	private int leafCriticalMass = 20; // Has been 5, 50, 250 !

	private int transactionInactivityTimeoutSecs = 60;
	private int transactionTimeToLiveSecs = 3600;
	private int queryInactivityTimeoutSecs = 60;
	private int queryTimeToLiveSecs = 3600;
	private int searchTimeToLiveSecs = 600;
	private int searchInactivityTimeoutSecs = 3600;

	/** Time client waits for server to respond */
	private int commandTimeoutSecs = 300;
	
	
	private boolean isSlave = false;
	private InetSocketAddress parentNode = InetSocketAddress.createUnresolved("127.0.0.1", 27601);
	
	// Shared configs
	private boolean compressLogs = false;
	private boolean xmlLogs = false;
	
	private String attributeMapClassName = "likemynds.db.indextree.internal.AttributeMap";
    private String constraintMapClassName = "com.archopolis.db.whirlwind.internal.ConstraintMap";
    private String configDAOClassName = "com.archopolis.db.dao.internal.Db1ClientDAO";
    private String statsDAOClassName = "com.archopolis.db.dao.StatsStoreDao"; 
    
	private ScorerVersion scorerVersion = ScorerVersion.v2; // v1 no longer supported

	
	/**
	 * Read configuration 
	 * NOTE: Recently made private (30Aug08) cos seems like not really needed as public
	 * @throws IOException - if file not found
	 */
    private void readConfiguration() {

        // Postcode bits
        postcodeRootWin = getProp( "PostcodeRootWin", postcodeRootWin );
        postcodeRootLinux = getProp( "PostcodeRootLinux", postcodeRootLinux );
        
        // IpLookup bits
        iplookupRootWin = getProp( "IpLookupRootWin", iplookupRootWin );
        iplookupRootLinux = getProp( "IpLookupRootLinux", iplookupRootLinux );
        
		// DbClient bits
		primaryServerPort = getPropAsInt( "PrimaryServerPort", primaryServerPort );
		secondaryServerPort = getPropAsInt( "SecondaryServerPort", secondaryServerPort );
		primaryServer = getProp( "PrimaryServer", primaryServer );
		secondaryServer = getProp( "SecondaryServer", secondaryServer );
		defaultTargetNumResults = getPropAsInt( "DefaultTargetNumResults", defaultTargetNumResults );
		defaultScoreThreshold = getPropAsFloat( "DefaultScoreThreshold", defaultScoreThreshold );
        defaultStore = getProp( "DefaultStore", defaultStore );

		// DbServer bits
		dbCleanOnStart = getPropAsBool("DbCleanOnStart", dbCleanOnStart);
		dbRootWin = getProp( "DbRootWin", dbRootWin );
		dbRootLinux = getProp( "DbRootLinux", dbRootLinux );
		reposDir = getProp( "ReposDir", reposDir );
		txDir = getProp( "TxDir", txDir );
		logDir = getProp( "LogDir", logDir );
		listenPort = getPropAsInt( "ListenPort", listenPort );
		shutdownPort = getPropAsInt( "ShutdownPort", shutdownPort);
		leafCriticalMass = getPropAsInt( "LeafCriticalMass", leafCriticalMass );
		isSlave = getPropAsBool("Slave", isSlave);
		parentNode = getPropAsSockAddr("ParentNode", parentNode);
		compressLogs = getPropAsBool( "CompressLogs", compressLogs);
		xmlLogs = getPropAsBool( "XmlLogs", xmlLogs);
		
		transactionInactivityTimeoutSecs = getPropAsInt( "TransactionInactivityTimeoutSecs", transactionInactivityTimeoutSecs );
		transactionTimeToLiveSecs = getPropAsInt( "TransactionTimeToLiveSecs", transactionTimeToLiveSecs );
		queryInactivityTimeoutSecs = getPropAsInt( "QueryInactivityTimeoutSecs", queryInactivityTimeoutSecs );
		queryTimeToLiveSecs = getPropAsInt( "QueryTimeToLiveSecs", queryTimeToLiveSecs );
		searchInactivityTimeoutSecs = getPropAsInt( "SearchInactivityTimeoutSecs", searchInactivityTimeoutSecs );
		searchTimeToLiveSecs = getPropAsInt( "SearchTimeToLiveSecs", searchTimeToLiveSecs );
		
		attributeMapClassName = getProp("AttributeMapClassName", attributeMapClassName);
        constraintMapClassName = getProp("ConstraintMapClassName", constraintMapClassName);
        configDAOClassName = getProp("ConfigDAOClassName", configDAOClassName); 
        statsDAOClassName = getProp("StatsDAOClassName", statsDAOClassName); 

        scorerVersion = ScorerVersion.valueOf(getProp("ScorerVersion", "v2"));
    }

	
	/**
	 * Private constructor to force use of singleton
	 */
	private Settings() {
		try {
			initialise();
		} catch (IOException e) {
			System.out.println( "INFO No Config file found: Using defaults");
//			System.err.println( "Error: " + e.getMessage() );
//			throw new Error( e ); // Can't cope with errors here
		}
	}

	/**
	 * get the singleton instance
	 */
	synchronized public static Settings getInstance() {
        if (instance == null) {
            instance = new Settings();
            
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer(); 
            
            ObjectName name;
			try {
				name = new ObjectName("com.wwm.db.core:type=Settings");
				mbs.registerMBean(instance, name); 
			} catch (Throwable e) {
				//	e.printStackTrace();
				System.out.println( "Exception '" + e.getMessage() + "' registering MBean.  Continuing without JMX support" );
			}
        }
		return instance;
	}	
	
	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#isWindows()
	 */
	public boolean isWindows() {
		String osName = System.getProperty("os.name");
		return osName.startsWith("Windows");
	}
	
	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getPrimaryServer()
	 */
	public String getPrimaryServer() {
		return primaryServer;
	}

	public void setPrimaryServer(String primaryServer) {
		this.primaryServer = primaryServer;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getPrimaryServerPort()
	 */
	public int getPrimaryServerPort() {
		return primaryServerPort;
	}

	public void setPrimaryServerPort(int primaryServerPort) {
		this.primaryServerPort = primaryServerPort;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getSecondaryServer()
	 */
	public String getSecondaryServer() {
		return secondaryServer;
	}

	public void setSecondaryServer(String secondaryServer) {
		this.secondaryServer = secondaryServer;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getSecondaryServerPort()
	 */
	public int getSecondaryServerPort() {
		return secondaryServerPort;
	}

	public void setSecondaryServerPort(int secondaryServerPort) {
		this.secondaryServerPort = secondaryServerPort;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getCompressLogs()
	 */
	public boolean getCompressLogs() {
		return compressLogs;
	}
	
	public void setCompresssLogs(boolean compress) {
		compressLogs = compress;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getXmlLogs()
	 */
	public boolean getXmlLogs() {
		return xmlLogs;
	}
	
	public void setXmlLogs(boolean xml) {
		xmlLogs = xml;
	}
	
	/**
	 * Use true FOR UNIT TEST CONFIG ONLY.
	 * @return true if server should delete database on each start.
	 */
	public boolean getDbCleanOnStart() {
		return dbCleanOnStart;
	}
	
	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getDbRoot()
	 */
	public String getDbRoot() {
		if (isWindows()) {
			return dbRootWin;
		} else {
			return dbRootLinux;
		}
	}
	
	public void setDbRoot(String dbRoot) {
		if (isWindows()) {
			this.dbRootWin = dbRoot;
		} else {
			this.dbRootLinux = dbRoot;
		}
	}

    /* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getPostcodeRoot()
	 */
    public String getPostcodeRoot() {
        if (isWindows()) {
            return postcodeRootWin;
        } else {
            return postcodeRootLinux;
        }
    }
    
    public void setPostcodeRoot(String dbRoot) {
        if (isWindows()) {
            this.postcodeRootWin = dbRoot;
        } else {
            this.postcodeRootLinux = dbRoot;
        }
    }
    
    /* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getIpLookupRoot()
	 */
    public String getIpLookupRoot() {
        if (isWindows()) {
            return iplookupRootWin;
        } else {
            return iplookupRootLinux;
        }
    }
    
    public void setIpLookupRoot(String dbRoot) {
        if (isWindows()) {
            this.iplookupRootWin = dbRoot;
        } else {
            this.iplookupRootLinux = dbRoot;
        }
    }
    
	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getListenPort()
	 */
	public int getListenPort() {
		return listenPort;
	}
	
	public void setListenPort(int listenPort) {
		this.listenPort = listenPort;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getLogDir()
	 */
	public String getLogDir() {
		return getDbRoot() + File.separator + logDir;
	}
	

	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}
	

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getReposDir()
	 */
	public String getReposDir() {
		return getDbRoot() + File.separator + reposDir;
	}
	

	public void setReposDir(String reposDir) {
		this.reposDir = reposDir;
	}
	

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getTxDir()
	 */
	public String getTxDir() {
		return getDbRoot() + File.separator + txDir;
	}
	

	public void setTxDir(String txDir) {
		this.txDir = txDir;
	}
	
	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getLeafCriticalMass()
	 */
	public int getLeafCriticalMass() {
		return leafCriticalMass;
	}

	
	
	/**
	 * Initialise from config file specified by system property likemynds.config.file
	 * and if not found, look in java.home/lib for likemynds.properties
	 */
	private void initialise() throws IOException {
		String filename = System.getProperty("likemynds.config.file");
		if (filename == null) {
		    filename = System.getProperty("java.home");
		    if (filename == null) {
			throw new Error("Can't find java.home ??");
		    }
		    File f = new File(filename, "lib");
		    f = new File(f, "likemynds.properties");
		    filename = f.getCanonicalPath();
		}
        
        InputStream in;
		try { 
            in = new FileInputStream(filename); // this might throw exception upwards
            System.out.println( "INFO Opened config in file: " + filename );
        }
        catch (IOException e) {
            // failed to open file .. try something else
            in = null;
        }
        
        // If can't find from above, try from resources:  
        // ("database.properties" will look for likemynds/db/database.properties in that JAR)
        // "/database.properties" will look for database.properties in the root of the JAR).
        if (in == null) {
            in = Settings.class.getResourceAsStream( "/database.properties");
            if (in != null) System.out.println( "Loaded database.properties from classloader that loaded Settings");
        }

        // try the loader for this thread (good way of getting at servlet loader maybe.. copied from log4j.
        // see http://www.docjar.com/html/api/org/apache/log4j/helpers/Loader.java.html
        if (in == null) {
            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            in = loader.getResourceAsStream( "/database.properties");
            if (in != null) System.out.println( "Loaded database.properties from ThreadContext classloader");
        }

		BufferedInputStream bin = new BufferedInputStream(in);
		try {
			// Load the properties (if we have some)
			props.load(bin);

		} finally {
			readConfiguration(); // merges defaults with System.getProperties()
		    if (in != null) {
		    	in.close();
		    }
		}
	}
	
	
	private int getPropAsInt( String key, int defaultVal ) {
		String prop = getPropMergeEnv(key);
		
		if (prop == null) return defaultVal;
		
		try {
			return Integer.valueOf( prop );
		}
		catch ( NumberFormatException e ) {
			System.err.println( "Error in properties file: an integer is required for the key: " 
					+ key + ", found: " + prop );
			return defaultVal;
		}
	}

	private InetSocketAddress getPropAsSockAddr(String key, InetSocketAddress defaultVal) {
		String prop = getPropMergeEnv(key);
		
		if (prop == null) return defaultVal;
		
		try {
			int sep = prop.indexOf(':');
			if (sep < 1) {
				System.err.println( "Error in properties file: format 'address:port' is required for the key: " 
						+ key + ", found: " + prop );
				return defaultVal;
			}
			String address = prop.substring(0, sep);
			String port = prop.substring(sep+1);
			InetSocketAddress addr = new InetSocketAddress(address, Integer.valueOf( port ));
			return addr;
		}
		catch ( NumberFormatException e ) {
			System.err.println( "Error in properties file: a port number is required for the key: " 
					+ key + ", found: " + prop );
			return defaultVal;
		}
	}
	
	private boolean getPropAsBool( String key, boolean defaultVal ) {
		String prop = getPropMergeEnv(key);
		
		if (prop == null) return defaultVal;
		
		try {
			return Boolean.valueOf( prop );
		}
		catch ( NumberFormatException e ) {
			System.err.println( "Error in properties file: a boolean is required for the key: " 
					+ key + ", found: " + prop );
			return defaultVal;
		}
	}
	
	private float getPropAsFloat( String key, float defaultVal ) {
		String prop = getPropMergeEnv(key);
		
		if (prop == null) return defaultVal;
		
		try {
			return Float.valueOf( prop );
		}
		catch ( NumberFormatException e ) {
			System.err.println( "Error in properties file: an number is required for the key: " 
					+ key + ", found: " + prop );
			return defaultVal;
		}
	}

	private String getProp( String key, String defaultVal ) {
		String prop = getPropMergeEnv(key);
		
		if (prop == null) return defaultVal;
		
		return prop;
	}


	/**
	 * Gets the given key from the properties file, unless the property has been defined in the 
	 * Java runtime properties using -Dkey=value
	 * @param key
	 * @return value
	 */
	private String getPropMergeEnv(String key) {
		String value = System.getProperty(key);
		if (value != null) {
			return value;
		}
		return props.getProperty( key );
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getDefaultTargetNumResults()
	 */
	public int getDefaultTargetNumResults() {
		return defaultTargetNumResults;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getDefaultScoreThreshold()
	 */
	public float getDefaultScoreThreshold() {
		return defaultScoreThreshold;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getQueryInactivityTimeoutSecs()
	 */
	public int getQueryInactivityTimeoutSecs() {
		return queryInactivityTimeoutSecs;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getQueryTimeToLiveSecs()
	 */
	public int getQueryTimeToLiveSecs() {
		return queryTimeToLiveSecs;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getSearchInactivityTimeoutSecs()
	 */
	public int getSearchInactivityTimeoutSecs() {
		return searchInactivityTimeoutSecs;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getSearchTimeToLiveSecs()
	 */
	public int getSearchTimeToLiveSecs() {
		return searchTimeToLiveSecs;
	}
	
	public void setTransactionTimeToLiveSecs(int transactionTimeToLiveSecs) {
		this.transactionTimeToLiveSecs = transactionTimeToLiveSecs;
	}
	

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getTransactionInactivityTimeoutSecs()
	 */
	public int getTransactionInactivityTimeoutSecs() {
		return transactionInactivityTimeoutSecs;
	}

	public void setTransactionInactivityTimeoutSecs(int transactionInactivityTimeoutSecs) {
		this.transactionInactivityTimeoutSecs = transactionInactivityTimeoutSecs;
	}
	
	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getTransactionTimeToLiveSecs()
	 */
	public int getTransactionTimeToLiveSecs() {
		return transactionTimeToLiveSecs;
	}

    /* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getDefaultStore()
	 */
    public String getDefaultStore() {
        return defaultStore;
    }

    public void setDefaultStore(String storeName) {
        defaultStore = storeName;
    }

    
	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#isSlave()
	 */
	public boolean isSlave() {
		return isSlave;
	}

	/* (non-Javadoc)
	 * @see com.wwm.db.core.SettingsMBean#getParentNode()
	 */
	public InetSocketAddress getParentNode() {
		return parentNode;
	}


	public int getShutdownPort() {
		return shutdownPort;
	}

	public String getAttributeMapClassName() {
		return attributeMapClassName;
	}
	
	public void setAttributeMapClassName(String attributeMapClassName) {
		this.attributeMapClassName = attributeMapClassName;
	}


    public String getConstraintMapClassName() {
		return constraintMapClassName;
	}
    
    public ScorerVersion getScorerVersion(){
    	return scorerVersion;
    }
    
//    /**
//     * Provide access to a data access object that abstracts away simple operations for accessing configuration
//     * @return
//     */
//    @SuppressWarnings("unchecked")
//   	static public Class<SimpleDAO> getStatsDAOClass() {
//		try {
//			return (Class<SimpleDAO>) Class.forName(Settings.getInstance().getStatsDAOClassName());
//		} catch (ClassNotFoundException e) {
//			throw new Error(e); // Fatal error if we can't find it.
//		}
//    }

    public void setConfigDAOClassName(String configDAOClassName) {
		this.configDAOClassName = configDAOClassName;
	}

    public String getConfigDAOClassName() {
    	return configDAOClassName;
    }

    public String getStatsDAOClassName() {
		return statsDAOClassName;
	}
    
	public String getTmpDir() {
		return System.getProperty("java.io.tmpdir");
	}


	public int getCommandTimeoutSecs() {
		return commandTimeoutSecs;
	}
	
	public void setCommandTimeoutSecs(int commandTimeout) {
		this.commandTimeoutSecs = commandTimeout;
	}


}
