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
package com.wwm.db.internal;

import com.wwm.db.query.Result;
import com.wwm.model.attributes.Score;

public class ResultImpl<E extends Object> implements Result<E> {

	private E item;
	private Score score;

	public ResultImpl(E item, Score score) {
		super();
		this.item = item;
		this.score = score;
	}

	public E getItem() {
		return item;
	}

	public Score getScore() {
		return score;
	}
}
