package com.springsource.insight.plugin.whirlwind;



import org.aspectj.lang.JoinPoint;

import com.springsource.insight.collection.AbstractOperationCollectionAspect;


/**
 * Collect data on a number of interesting operations.
 * @author Neale Upstone
 */
public aspect CollectionAspect extends AbstractOperationCollectionAspect {

	// In parent aspect this triggers the call of Operation.enter() on before advice
	// and Operation.exitNormal() afterReturning.
	public pointcut collectionPoint() : 
		execution(public * com.wwm.db.DataOperations.count(..)) || 
		execution(public * com.wwm.db.DataOperations.retrieve*(..)) || 
		execution(public * com.wwm.db.DataOperations.query*(..));
	
	@Override
	protected WhirlwindOperation createOperation(JoinPoint jp) {
		return createWhirlwindOperation(jp);
	}
	
	
	
	private WhirlwindOperation createWhirlwindOperation(JoinPoint jp) {
		WhirlwindOperation op = new WhirlwindOperation(getSourceCodeLocation(jp));
		op.setArgs(jp.getArgs());
		return op;
	}

//	// TODO: I think this can be an around advice. 
//	Object around(Transaction target) : collectionPoint() && this(target)  
//	{
//		WhirlwindOperation op = createOperation(thisJoinPoint);
//		getCollector().enter(op);
//		Object result = proceed(target);
//		getCollector().exitNormal();
//		return result;
//	}

}
