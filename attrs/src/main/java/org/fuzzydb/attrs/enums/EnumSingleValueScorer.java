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
package org.fuzzydb.attrs.enums;



import org.fuzzydb.attrs.Score;
import org.fuzzydb.attrs.Scorer;
import org.fuzzydb.attrs.Score.Direction;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.springframework.util.Assert;

import com.wwm.db.whirlwind.internal.IAttribute;
import com.wwm.db.whirlwind.internal.IAttributeConstraint;
import com.wwm.db.whirlwind.internal.IAttributeMap;
import com.wwm.util.BitSet64;



public class EnumSingleValueScorer extends Scorer {

	private static final long serialVersionUID = 4763946886241506862L;

	private EnumExclusiveValue matchValue;

    /** Default ctor for serialization libraries */
    @SuppressWarnings("unused")
    private EnumSingleValueScorer() {
        this(1, null);
    }

	public EnumSingleValueScorer(int scoreAttrId, EnumExclusiveValue value) {
		super(scoreAttrId);
		this.matchValue = value;
	}


	
   @Override
    public void scoreItemToItem(Score score, Score.Direction d, IAttributeMap<IAttribute> c, IAttributeMap<IAttribute> scoreAttrs) {
	   
	   // attr is ignored, as we have the attribute in matchValue
	   // otherAttr may be EnumExclusiveValue or EnumMultipleValue
        EnumValue otherAttr = (EnumValue) c.findAttr(scorerAttrId);

        float result = calcScore(otherAttr);
        
        score.add(this, result, d);
    }

   
   @Override
	public void scoreSearchToNode(Score score, Direction d, IConstraintMap c, IAttributeMap<? extends IAttribute> searchAttrs) {

	   IAttributeConstraint na = c.findAttr(scorerAttrId);

       // If there is no Node Data then we only score null 
       if (na == null) {
           score.addNull(this, d);
           return;
       }

       float result = 0f;
       
       // If there are nulls underneath this 
       // node include a null score
       if (na.isIncludesNotSpecified() ) {
           if (isScoreNull()) {
               result = getScoreOnNull();
           }
       }        
           
       // Return the maximum possible score, which might be scoreOnNull score if nulls exist.
       result = Math.max(result, calcScore(na) );
       score.add(this, result, d);
       return;
	}

   
   @Override
	public void scoreNodeToSearch(Score score, Direction d, IAttributeMap<IAttributeConstraint> c, IAttributeMap<IAttribute> searchAttrs) {
		// do nothing. Gives 100% score for now
	   // FIXME: perhaps this aint optimal!
	   return;
	}

   

   protected float calcScore(EnumValue otherAttr) {
       if (otherAttr instanceof EnumExclusiveValue) {
           return calcScore(matchValue, (EnumExclusiveValue)otherAttr);
       } else if (otherAttr instanceof EnumMultipleValue) {
           return calcScore((EnumMultipleValue)otherAttr, matchValue );
       }
       return minScore;
   }

   
   protected float calcScore(IAttributeConstraint otherBc) {
       if (otherBc instanceof EnumExclusiveConstraint ) {
           return calcScore((EnumExclusiveConstraint)otherBc, matchValue);
       } else if (otherBc instanceof EnumMultipleConstraint) {
           return calcScore((EnumMultipleConstraint)otherBc, matchValue);
       }
       assert(false);
       return minScore;
   }    

   
   // Score Excl -> Excl
   protected float calcScore(EnumExclusiveValue thisAttr, EnumExclusiveValue otherAttr) {
       return (thisAttr.getEnumIndex() == otherAttr.getEnumIndex())? maxScore : minScore;
   }
   
   
   // Score Multi -> Excl
   protected float calcScore(EnumMultipleValue mAttr, EnumExclusiveValue attr) {
       return (mAttr.contains(attr.getEnumIndex()))? maxScore : minScore;
   }


   // Score ExclNode Against Excl
   protected float calcScore(EnumExclusiveConstraint bc, EnumExclusiveValue attr) {
//       Collection<EnumExclusiveValue> prefs = bc.getValues();
//       for (EnumExclusiveValue pref : prefs) {
//           if (pref.getEnumIndex() == attr.getEnumIndex()) {
//               return maxScore;
//           }
//       }
	   if (bc.consistent(attr)){ // a rather quicker version than the above!
		   return maxScore;
	   }
	   return minScore;
   }    


   // Score MultiNode Against Excl
   protected float calcScore(EnumMultipleConstraint bc, EnumExclusiveValue attr) {
   	BitSet64 bits = bc.getBitSet();
    if (bits.get(attr.getEnumIndex())){
    	return maxScore;
    }
    return minScore;
// was
//    Collection<EnumMultipleValue> prefs = bc.getValues();
//       for (EnumMultipleValue pref : prefs) {
//           if (pref.contains(attr.getEnumIndex())) {
//               return maxScore;
//           }
//       }
//       return minScore;
    }

    @Override
    protected void assertValidInternal() {
    	Assert.state(matchValue != null, "matchValue must be defined");
    }
}
