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

import org.fuzzydb.attrs.Score;



/** Container for a score-sequence tuple. The sequence number is used to give priority
 * to newer items in the event of equal score haves. This produces a best-first search
 * which degrades to depth-first search given lack of score discrimination.
 */
public abstract class Priority implements Comparable<Priority> {

    /**
     * @return Returns the score.
     */
    public abstract Score getScore();
    /**
     * @param score
     * @param sequence
     */
    public Priority(int sequence) {
        super();
        this.sequence = sequence;
    }
    private int sequence;

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Priority rhs) {

        // First, order by score
        int compare = getScore().compareTo( rhs.getScore() );
        if (compare != 0) {
            return compare;
        }

        // scores are same, higher (more recent) sequence numbers win out
        if (this.sequence > rhs.sequence) {
            return 1;
        }
        if (this.sequence < rhs.sequence) {
            return -1;
        }

        return 0; // shouldn't happen because sequence numbers are unique
    }
}
