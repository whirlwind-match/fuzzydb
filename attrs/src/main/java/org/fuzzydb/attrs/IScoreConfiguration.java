/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package org.fuzzydb.attrs;

import java.util.Collection;

import org.fuzzydb.attrs.Score.Direction;
import org.fuzzydb.attrs.internal.IConstraintMap;


import com.wwm.db.whirlwind.SearchSpec.SearchMode;
import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeMap;

/**
 * This is the guts of our scoring.  An implementation could be as we have done historically with individual scorers, 
 * or it could be a heavily optimised inline implementation that is generated for certain frequent searches.
 * 
 * We could, for example generate code, or we could create a score configuration that is very clever with
 * the boolean and enums it's expecting, and also knows that it is expecting a LayoutAttrMap of a given layout, and can
 * therefore score very quickly on fixed offsets in floats[] and ints[] of LAM.
 */
public interface IScoreConfiguration {

	public Collection<Scorer> getScorers();
	
	public void add(Scorer scorer);

	
	// Used by ItemAttributeContainer and SearchAttributeContainer (DB1 only)
	public void scoreAllItemToItems(Score score, Direction d,
			IAttributeMap<IAttribute> attrs, IAttributeMap<IAttribute> c);


	
	public Score scoreAllItemToItem(IAttributeMap<IAttribute> searchAttrs, IAttributeMap<IAttribute> itemAttrs,
			SearchMode searchMode);

	/**
	 * Score search->constraints and constraints->search
	 * @param currentScore
	 * @param constraints
	 * @param mode
	 * @param searchAttrs
	 */
	public void scoreSearchToNodeBothWays(Score currentScore, IConstraintMap constraints, SearchMode mode, IAttributeMap<IAttribute> searchAttrs);


	
}