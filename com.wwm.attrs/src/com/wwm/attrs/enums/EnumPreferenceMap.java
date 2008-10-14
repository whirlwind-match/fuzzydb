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
package com.wwm.attrs.enums;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Map of scores for have/want combinations (e.g. want=non-smoker, have=smoker, giving up, score = 0.2)
 * 
 * User preference is along the row 
 *	Values are down columns
 *
 * @author ac
 */
public class EnumPreferenceMap implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Short prefEnumDefId;
	private Short otherEnumDefId;
    
	ArrayList< ArrayList<Float> > map = new ArrayList< ArrayList<Float>>();	// list of rows
	
	public EnumPreferenceMap() {
	}
	
	/**
	 * Constructor for building a db2 map out of a db1 one
	 * A bodge to help with transition
	 * @param map
	 */
	public EnumPreferenceMap(int prefId, int otherId, ArrayList<ArrayList<Float>> map) {
		prefEnumDefId = (short)prefId;
		otherEnumDefId = (short)otherId;
		this.map = map;
	}

	public void add(EnumExclusiveValue prefVal, EnumExclusiveValue otherVal, float score) {
		assert( prefVal != null );
		if (prefEnumDefId == null) {
			prefEnumDefId = prefVal.getEnumDefId();
		}
		if (otherEnumDefId == null && otherVal != null) {
			otherEnumDefId = otherVal.getEnumDefId();
		}
		
		// Ensure we're not adding mixed enums to the map.
		assert( prefVal == null || (short)prefEnumDefId == prefVal.getEnumDefId() );
		assert( otherVal == null || (short)otherEnumDefId == otherVal.getEnumDefId() );
		
		short pref = prefVal.getEnumIndex();

        // To support null all values are added starting at 1 
        // and null will be in index 0  
        short val = 0; 
        if (otherVal != null) {
            val = otherVal.getEnumIndex();
            val++;
        }
        
	    // Insert rows until map is big enough
        while (map.size() <= pref) addRow(); 

	    ArrayList<Float> row = map.get(pref);
		while (row.size() <= val) row.add(null);
		row.set(val, score); // This was probably it (in combo iwth having have and want wrong way round in Scorer
	}
	
	private void addRow() {
		map.add(map.size(), new ArrayList<Float>());
	}
	
	
//  private static Logger log = LogFactory.getLogger(EnumPreferenceMap.class);

	/**
	 * Get the score for the supplied have against the supplied want
	 * @param prefVal - The "want" attribute
     * @param otherVal - The "have" attribute 
	 * @return
	 */
	public float score(EnumExclusiveValue prefVal, EnumExclusiveValue otherVal) {
        assert(prefVal != null);
	    short pref = prefVal.getEnumIndex();
	    
        // To support null all values are added starting at 1 
        // and null will be in index 0  
        short val = 0; 
        if (otherVal != null) {
            val = otherVal.getEnumIndex();
            val++;
        }
        
	    if (map.size() <= pref) return 0.0f;	// not in map

	    ArrayList<Float> row = map.get(pref);
		if (row.size() <= val) return 0.0f;	// not in map

		Float score = row.get(val);
        
		if (score == null) return 0.0f;	// not in map

		return score.floatValue();
	}
	
	/**
	 * Get the score for the supplied have against the supplied want
	 * @param prefVal - The "want" attribute
     * @param otherVal - The "have" attribute (use -1 for 'null') 
	 * @return
	 */
	public float score(short pref, short val) {
        assert(pref != -1);
	    assert(val >= -1); // -1 indicates 'null' which is at index 0
	    
        // To support null all values are added starting at 1 
        // and null will be in index 0  
        val++;
        
	    if (map.size() <= pref) return 0.0f;	// not in map

	    ArrayList<Float> row = map.get(pref);
		if (row.size() <= val) return 0.0f;	// not in map

		Float score = row.get(val);
        
		if (score == null) return 0.0f;	// not in map

		return score.floatValue();
	}

}
