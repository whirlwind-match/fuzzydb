package com.wwm.db.internal.server;

import org.fuzzydb.core.LogFactory;
import org.slf4j.Logger;


public abstract aspect AbstractLoggingAspect {

	private Logger log = LogFactory.getLogger(getClass());
	
	abstract pointcut logPointcut();
	
	before() : logPointcut() {
		if (log.isTraceEnabled()){
			log.trace("Entering: {}", thisJoinPointStaticPart.toShortString() );
		}
	}
	
	after() returning() : logPointcut() {
		if (log.isTraceEnabled()){
			log.trace("Exiting: {}", thisJoinPointStaticPart.toShortString() );
		}
	}
	
	after() throwing(Exception e) : logPointcut() {
		if (log.isTraceEnabled()){
			log.trace("Exiting: {} - threw {}", thisJoinPointStaticPart.toShortString(), e.getClass().getName() );
		}
	}
}
