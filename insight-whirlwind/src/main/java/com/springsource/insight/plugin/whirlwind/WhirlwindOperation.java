package com.springsource.insight.plugin.whirlwind;

import java.util.ArrayList;

import com.springsource.insight.intercept.operation.BasicOperation;
import com.springsource.insight.intercept.operation.OperationType;
import com.springsource.insight.intercept.operation.SourceCodeLocation;

/**
 *  This is a straight bean that the FTL gets to see as the model attribute 'operation'.
 *  It just nees the relevant getters for whatever we want to expose. 
 *  NOTE: setters are also important as EL for JSP page that renders AJAX bits expects attributes if a setter exists
 */
public class WhirlwindOperation extends BasicOperation {

	private ArrayList<String> parameters;

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
	
	public String getParameters() {
		return parameters.toString(); // Workaround.  Insight JSP function doesn't like list...
	}

	void setArgs(Object[] args) {
		if (args == null) return;
		
		this.parameters = new ArrayList<String>(args.length);
		for (Object arg : args) {
			this.parameters.add(arg == null ? "null" : arg.toString());
		}
	}
	

}
