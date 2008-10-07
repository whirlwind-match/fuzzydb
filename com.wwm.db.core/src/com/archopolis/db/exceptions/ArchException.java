package com.archopolis.db.exceptions;


/**
 * Our base Exception for catching DB-specific exceptions.
 * Implmented constructors allow derived classes to access those
 * methods from Java's Exception.
 * @author nu
 *
 */
@SuppressWarnings("serial")
public class ArchException extends Exception {

    /**
     * TODO Not sure if we want this one implemented.
     */
    public ArchException(){
        super();
    }

    public ArchException(Throwable cause) {
        super(cause);
    }
    
    public ArchException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchException(String message) {
        super(message);
    }
}
