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
package com.wwm.util;

public interface ScoreMapper {

    /**
     * get the mapped score (between 0 and 1 inclusive) for the given scoreFactor
     * @param scoreFactor float <= 1.0 (negative values allowed)
     * Negative values indicate outside of preferred range.
     * @return
     */
    float getScore(float scoreFactor);

}
