package com.springsource.insight.plugin.whirlwind;

import com.springsource.insight.intercept.operation.BasicOperation;
import com.springsource.insight.intercept.operation.OperationType;
import com.springsource.insight.intercept.operation.SourceCodeLocation;

/**
 *  This is a straight bean that the FTL gets to see as the model attribute 'operation'.
 *  It just nees the relevant getters for whatever we want to expose. 
 */
public class WhirlwindOperation extends BasicOperation {

	public WhirlwindOperation(SourceCodeLocation scl) {
		super(scl);
	}

	@Override
	public String getLabel() {
		return getSourceCodeLocation().getClassName() + ":" + getSourceCodeLocation().getMethodName();
	}

	@Override
	public OperationType getType() {
		return OperationType.valueOf("READ");
	}
	
	

}
