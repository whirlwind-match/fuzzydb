package org.fuzzydb.util.context;

import java.util.Map;
import java.util.TreeMap;


/**
 * Bean that contains all application data that we want to be able to share between
 * different sessions of a given application instance.
 * 
 * The ApplicationContextManager is responsible for allowing the application to
 * get access to the ApplicationContext instance (irrespective of how that instance
 * gets created and managed - it might be a Spring bean, or stored in the servlet context
 * of a servlet, or just created for a JUnit test).
 * 
 * Interface is designed to be like javax.servlet.Session where possible.
 * 
 * @author Neale Upstone
 */
public class SimpleApplicationContext implements ApplicationContext {

    static private SimpleApplicationContext instance = new SimpleApplicationContext();

    /**
     * Use with caution.  It is better to store an SAC in a container supplied appContext.
     * @return singleton instance.
     */
    static public SimpleApplicationContext getInstance(){
        return instance;
    }

    /**
     * Map of objects for this app context.
     */
    private Map<String, Object> objects;

    /**
     * Construct one
     */
    public SimpleApplicationContext() {
        objects = new TreeMap<String, Object>();
    }


    /**
     * Get named object
     * @param name
     * @return object, or null
     */
    public Object get(String name) {
        return objects.get( name );
    }



    /**
     * Set object for a given name
     * @param name
     * @param objInstance
     */
    public void set(String name, Object objInstance) {
        objects.put( name, objInstance );
    }

    /**
     * Invalidates the current Application Context Data
     */
    public void invalidate() {
        objects.clear();
    }
}
