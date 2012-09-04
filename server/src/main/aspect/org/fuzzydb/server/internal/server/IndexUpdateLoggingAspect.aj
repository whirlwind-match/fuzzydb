package org.fuzzydb.server.internal.server;

import org.fuzzydb.server.internal.index.Index;
import org.fuzzydb.server.internal.server.Indexes;

public aspect IndexUpdateLoggingAspect extends AbstractLoggingAspect {

	pointcut logPointcut() : call(* Index.*(..)) || execution(* Indexes.*(..));

}
