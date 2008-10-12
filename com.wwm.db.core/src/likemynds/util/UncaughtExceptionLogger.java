/**
 * 
 */
package likemynds.util;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.Thread.UncaughtExceptionHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.archopolis.internal.util.LogFactory;

/**
 * Default handler to tell Thread what to do with uncaught exceptions in threads.
 * Without installing an uncaught exception handler, the threads would
 * just die silently.
 * To use, place the following code as a static initialiser in main
 * class for application (e.g. server.Database):<p>
 * <code>
 * static { UncaughtExceptionLogger.initialise(); }
 * </code>
 * @author Neale Upstone
 *
 */
public class UncaughtExceptionLogger implements UncaughtExceptionHandler {

	static private Logger log = LogFactory.getLogger(UncaughtExceptionLogger.class);
	
	static private UncaughtExceptionLogger instance = null;
	
	/**
	 * Install a singleton instance of UncaughtExceptionLogger as the 
	 * default uncaught exception handler.
	 */
	static public synchronized void initialise() {
		if (instance == null) {
			instance = new UncaughtExceptionLogger();
			Thread.setDefaultUncaughtExceptionHandler( instance );
		}
	}
	
	/**
	 * Log exceptions
	 */
	public void uncaughtException(Thread t, Throwable e) {
		ByteArrayOutputStream s = new ByteArrayOutputStream();
		e.printStackTrace( new PrintStream( s ) );
		log.log( Level.SEVERE, "Fatal Exception in Thread: " + t.getName(), e );
		log.log( Level.SEVERE, "Details - Exception = : " + s.toString() );
		
	}

}
