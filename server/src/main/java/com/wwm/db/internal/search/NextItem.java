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
package com.wwm.db.internal.search;

import com.wwm.attrs.Score;
import com.wwm.db.internal.MetaObject;
import com.wwm.db.marker.IWhirlwindItem;


/**
 * Tuple of unreported result index item and its priority
 */
public class NextItem extends Priority {
	private Score score;
	private Object leaf; // Keep track of leaf node to allow to track splits (store as object because can't see Leaf here
	private MetaObject<? extends IWhirlwindItem> item;
	
	/**
	 * @return Returns the item.
	 */
	public MetaObject<? extends IWhirlwindItem> getItem() {
		return item;
	}

	/**
	 * @param score
	 * @param sequence
	 * @param node
	 */
	public NextItem(Score score, int sequence, MetaObject<? extends IWhirlwindItem> item, Object leaf) {
		super(sequence);
		this.score = score;
		this.item = item;
		this.leaf = leaf;
	}
	
	@Override
	public Score getScore() {
		return score;
	}

	public Object getLeaf() {
		return leaf;
	}
}
