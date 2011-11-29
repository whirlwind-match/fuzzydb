package com.wwm.db.internal.server;

import com.wwm.db.internal.index.Index;
import com.wwm.db.internal.server.Indexes;

public aspect IndexUpdateLoggingAspect extends AbstractLoggingAspect {

	pointcut logPointcut() : call(* Index.*(..)) || execution(* Indexes.*(..));

}
