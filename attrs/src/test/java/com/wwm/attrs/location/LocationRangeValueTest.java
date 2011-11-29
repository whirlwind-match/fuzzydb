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
package com.wwm.attrs.location;



import org.junit.Assert;
import org.junit.Test;

import com.wwm.attrs.dimensions.DimensionsRangeConstraint;
import com.wwm.attrs.location.EcefVector;
import com.wwm.model.dimensions.Point3D;

public class LocationRangeValueTest {

    private int locId = 1; 
	
    DimensionsRangeConstraint left = new DimensionsRangeConstraint( locId,
            new Point3D( -1.0001f, -1.0001f, -1.0001f ),
			new Point3D( 1.0001f, 1.0001f, 0f ) );
    
    DimensionsRangeConstraint right = new DimensionsRangeConstraint( locId,
            new Point3D( -1.0001f, -1.0001f, 0f ),
			new Point3D( 1.0001f, 1.0001f, 1.0001f ) );
    
    EcefVector leftvec = new EcefVector(locId, -0.5f,-0.5f,-0.5f);
    EcefVector rightvec = new EcefVector(locId, 0.5f,0.5f,0.5f);
    EcefVector boundryvec = new EcefVector(locId, 0f,0f,0f);
    
    
	@Test
    public void testContainLeft() {
        Assert.assertTrue( left.consistent( leftvec ) );
        Assert.assertFalse( left.consistent( rightvec ) );
    }

	@Test
    public void testContainRight() {
        Assert.assertTrue( right.consistent( rightvec ) );
        Assert.assertFalse( right.consistent( leftvec ) );
    }
    
	@Test
    public void testSplitPoint() {
    	// The split point should go into one or the other container - but not both
    	Assert.assertTrue(right.consistent( boundryvec ) ^ left.consistent( boundryvec ));
    }

	
}
