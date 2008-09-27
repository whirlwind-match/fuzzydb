/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.util;

import java.util.logging.Formatter;
import java.util.logging.LogRecord;

/**
 * @author Neale
 */
public class LogFormatter extends Formatter {

    static private long startMillis;

    public LogFormatter() {
        super();
    }

    
    /* (non-Javadoc)
     * @see java.util.logging.Formatter#format(java.util.logging.LogRecord)
     */
    @Override
	public String format(LogRecord record) {
        
        final String newline = System.getProperty("line.separator");
        
        StringBuilder buf = new StringBuilder();
        
        appendTime( buf, record );
        buf.append( ' ' ).append( record.getLevel().toString() );
        buf.append( ' ' ).append( record.getMessage() ).append( newline );
        
        return buf.toString();
    }
    
    /**
     * Add a timestamp to buf, which is the time since our first log record.
     * First record should show [0.000 secs]
     * @param buf
     * @param record
     */
    private void appendTime( StringBuilder buf, LogRecord record ) {
    	
    	long millisNow = record.getMillis();

    	// If startMillis not set, then thread-safely set it. 
    	if ( startMillis == 0) {
    		synchronized( LogFormatter.class ) {
    	    	if ( startMillis == 0) {
    	    		startMillis = millisNow;
    	    	}
    		}
    	}
    	
    	long millis = millisNow - startMillis; 
    	long secs = millis / 1000;
    	long afterDot = millis - (secs * 1000L);
    	buf.append( secs ).append( '.' );
    	
    	if ( afterDot < 10 ) {
    		buf.append("00");
    	} 
    	else if (afterDot < 100) {
    		buf.append('0');
    	}
    	
    	buf.append( afterDot );
    }

}
