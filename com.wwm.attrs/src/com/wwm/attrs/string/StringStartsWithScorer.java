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
package com.wwm.attrs.string;

import java.util.TreeSet;

public class StringStartsWithScorer extends StringBaseScorer { 
    
    private static final long serialVersionUID = 6358946451731357494L;
    
    public StringStartsWithScorer(int attrId, int otherAttrId) {
        super( attrId, otherAttrId );
    }
    @Override
    protected float calcScore(StringValue thisAttr, StringValue otherAttr) {
        if (otherAttr.getValue().startsWith(thisAttr.getValue())) {
            return maxScore;
        }
        return minScore;
    }
    
    @Override
    protected float calcScore(StringValue thisAttr, StringMultiValue otherAttr) {
        for (String val: otherAttr.getValue()) {
            if (val.startsWith(thisAttr.getValue())) {
                return maxScore;
            }
        }
        return minScore;
    }
    
    @Override
    protected float calcScore(StringConstraint bc, StringValue attr) {
        if (!attr.isDelimited()) {
            return maxScore;
        }
        
        String start = attr.getValue().split(String.valueOf(attr.getDelimiter()), 2)[0];
        TreeSet<String> prefs = bc.getValues();
        if ( prefs.size() == 0) {
            return maxScore;
        }

        for (String pref: prefs) {
            if (start.startsWith(pref)) {
                return maxScore;
            }
        }
        return minScore;
    }    
    
    @Override
    protected float calcScore(StringValue attr, StringConstraint bc) {
        if (!bc.isDelimited()) {
            return maxScore;
        }

        TreeSet<String> prefs = bc.getValues();
        if ( prefs.size() == 0) {
            return maxScore;
        }

        String attrStart = attr.getValue().split(String.valueOf(bc.getDelimiter()), 2)[0];
        for (String pref: prefs) {
            if (pref.startsWith(attrStart)) {
                return maxScore;
            }
        }
        return minScore;
    }    
    
    @Override
    protected float calcScore(StringConstraint bc, StringMultiValue attr) {
        if (!attr.isDelimited()) {
            return maxScore;
        }
        
        for (String attrVal: attr.getValue()) {
            String start = attrVal.split(String.valueOf(attr.getDelimiter()), 2)[0];
            TreeSet<String> prefs = bc.getValues();
            if ( prefs.size() == 0) {
                return maxScore;
            }
    
            for (String pref: prefs) {
                if (start.startsWith(pref)) {
                    return maxScore;
                }
            }
        }
        return minScore;
    }    
}
