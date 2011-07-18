/******************************************************************************
 * Copyright (c) 2005-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.db.internal.whirlwind;

import com.wwm.db.Ref;

/**
 * Interface that is exposed on objects where we need them to know their own
 * RefImpl, (say for passing to other objects they create to tell them how
 * to refer back).
 * Implementation should have a transient RefImpl (as there's no need to store it),
 * and be retrieved by RefAwareHelper.getRefAwareObject( RefImpl ref);
 * @author Neale
 *
 */
public interface RefAware<T> extends Immutable { // NOTE: Immutable is just for testing (ask Neale if he can remember why)

	public void setRef( Ref<T> ref);
	
	public Ref<T> getRef();
}
