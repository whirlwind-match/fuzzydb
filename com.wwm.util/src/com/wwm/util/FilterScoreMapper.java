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

import java.io.Serializable;

public class FilterScoreMapper implements ScoreMapper, Serializable {

    private static final long serialVersionUID = 7323343915839805208L;

    /**
     * Implement the simple case of providing 1.0 for in range and 0.0 for all 
     * beyond range.
     */
    public float getScore(float scoreFactor) {
        return  (scoreFactor >= 0f) ? 1.0f : 0f;
    }

}
