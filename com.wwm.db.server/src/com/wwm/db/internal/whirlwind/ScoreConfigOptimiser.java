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

import com.wwm.attrs.IScoreConfiguration;
import com.wwm.db.whirlwind.SearchSpec;



/**
 * Class that is capable of simplifying a search so that less repeated work is done when doing the actual search 
 * @author Neale
 *
 */
public class ScoreConfigOptimiser {

	/**
	 * Eliminate unneeded scorers, based on what is in searchSpec, and also create scorers where we can
	 * turn a two way into a one way scorer based on the constant value in searchSpec.
	 */ 
	public static IScoreConfiguration getMergedScorers(SearchSpec searchSpec, IScoreConfiguration config) {
		// does nothing for now
		return config;
		// Algo:
		// - for each scorer, we want to know if that scorer has any role to play.
		// - If a scorer is ForwardsOnly, and the trigger attribute does not exist, then we can eliminate it
		// - If it scores both ways, and the attribute doesn't exist in the search, then we can use the reverse only variant
		// - If it is a Boolean, we can use a fixedBooleanScorer hard-coded for a search for true or for false - thus saving
		// the need to refer to the search attrs
		// A similar strategy to the boolean can be done for a float and float range preference... in fact even without
		// the step of eliminating any unused scorers, we can merge all the search attributes into a merged ScoreConfiguration
		// which uses only scorers that refer to the attribute in the database, not those in the search.
		// The attributes in that scorer can be given a "final" keyword too, which would allow the compiler to 
		// assume that the value never changes.
	}

}
