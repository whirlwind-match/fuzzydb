package com.springsource.insight.plugin.whirlwind;

import likemynds.db.client.Transaction;

import org.aspectj.lang.JoinPoint;

import com.springsource.insight.collection.AbstractOperationCollectionAspect;

public aspect CollectionAspect extends AbstractOperationCollectionAspect {

	// In parent aspect this triggers the call of Operation.enter() on before advice
	// and Operation.exitNormal() afterReturning.
	public pointcut collectionPoint() : 
		execution(public * Transaction.count(..)) || 
		execution(public * Transaction.retrieve*(..)) || 
		execution(public * Transaction.query*(..));
	
	@Override
	protected WhirlwindOperation createOperation(JoinPoint jp) {
		return createWhirlwindOperation(jp);
	}
	
	
	private WhirlwindOperation createWhirlwindOperation(JoinPoint jp) {
		WhirlwindOperation op = new WhirlwindOperation(getSourceCodeLocation(jp));
		// TODO fill out the detail??
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
