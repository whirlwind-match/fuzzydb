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

public class LinearScoreMapper implements ScoreMapper, Serializable {

    private static final long serialVersionUID = -3409637404300553323L;

    /**
     * Implement the simple case of providing 1.0 down to 0.0 with 0.0 for all 
     * beyond range.
     */
    public float getScore(float scoreFactor) {
        return  (scoreFactor >= 0f) ? scoreFactor : 0f;
    }

}
