package org.fuzzydb.attrs.layout;


import org.fuzzydb.attrs.bool.BooleanScorer;
import org.fuzzydb.attrs.bool.BooleanValue;
import org.fuzzydb.attrs.internal.IConstraintMap;
import org.fuzzydb.attrs.internal.ScoreConfiguration;
import org.fuzzydb.attrs.layout.LayoutAttrMap;
import org.fuzzydb.attrs.layout.LayoutConstraintMap;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeConstraint;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


import static org.junit.Assert.*;


/**
 * Test for getting new attribute map stuff done.
 * @author Neale
 */
public class LayoutBooleanTest extends BaseAttributeTest {

	protected int genderId;
	protected int wantGenderId;
	protected BooleanValue bTrue;
	protected BooleanValue bFalse; 

	protected BooleanValue bWantTrue;
	protected BooleanValue bWantFalse; 

	@Before
	public void setUp() throws Exception {
		// reset attrDefMgr
		genderId = mgr.getAttrId("Gender", BooleanValue.class);
		wantGenderId = mgr.getAttrId("wantGender", BooleanValue.class);
		bTrue = new BooleanValue(genderId, true);
		bFalse = new BooleanValue(genderId, false);
		bWantTrue = new BooleanValue(wantGenderId, true);
		bWantFalse = new BooleanValue(wantGenderId, false);
	}	

	protected void addScorers(ScoreConfiguration scorers) {
		scorers.add( new BooleanScorer(genderId, genderId) );   // FIXME: Replace with LayoutBooleanScorer
	}
	
	@Test 
	public void testBoolean() throws Exception {
		IAttributeMap<IAttribute> map = new LayoutAttrMap<IAttribute>();
		map.put(genderId, bTrue);
		
        // Read back to check it works
		IAttribute attr = map.findAttr(genderId);
		assertEquals(1, map.size() );

		assertTrue(bTrue.equals(attr)); // might not be the case as IAttribute could become a pointer into the map
		assertFalse(bTrue == attr);
	}

	@Ignore("See FIXME below")
	@Test 
	public void testTwoBoolean() throws Exception {
		IAttributeMap<IAttribute> map = new LayoutAttrMap<IAttribute>();
		map.put(genderId, bTrue);
		map.put(wantGenderId, bWantFalse);
		
        // Read back to check it works
		assertEquals(2, map.size() ); // FIXME: We expect this to fail, as layoutmap.BooleanCodec is not finished 

		{
			IAttribute attr = map.findAttr(genderId);
			assertTrue(bTrue.equals(attr));
			assertFalse(bTrue == attr);
		}

		{
			IAttribute attr = map.findAttr(wantGenderId);
			assertTrue(bWantFalse.equals(attr));
			assertFalse(bWantFalse == attr);
		}
	}

	
    @Test 
    public void testBooleanConstraintTrue() throws Exception {
        
        IConstraintMap constraints = new LayoutConstraintMap();
        IAttributeConstraint bc = bTrue.createAnnotation();
        constraints.put(genderId, bc);

        // Read back to check it works
        IAttributeConstraint constraint = constraints.findAttr(genderId);
        assertTrue(bc.equals(constraint));
        assertFalse(bc == constraint);
        assertFalse( constraint.isIncludesNotSpecified() );
        assertTrue( constraint.consistent(bTrue));
        assertFalse( constraint.consistent(bFalse));
    }

    @Test 
    public void testBooleanConstraintBoth() throws Exception {
        IConstraintMap constraints = new LayoutConstraintMap();
        // Create True annotation
        IAttributeConstraint bc = bTrue.createAnnotation();
        constraints.put(genderId, bc);
        
        // Expand with False annotation
        constraints.expand(bFalse, genderId);
        
        // Read back to check it works
        IAttributeConstraint constraint = constraints.findAttr(genderId);
        assertFalse( constraint.isIncludesNotSpecified() );
        assertTrue( constraint.consistent(bTrue));
        assertTrue( constraint.consistent(bFalse));
    }

    @Test 
    public void testBooleanConstraintTrueAndNulls() throws Exception {
        IConstraintMap constraints = new LayoutConstraintMap();
        IAttributeConstraint bc = bTrue.createAnnotation();
        bc.expand(null);
        constraints.put(genderId, bc);

        // Read back to check it works
        IAttributeConstraint constraint = constraints.findAttr(genderId);
        assertTrue( constraint.isIncludesNotSpecified() );
        assertTrue( constraint.consistent(bTrue));
        assertFalse( constraint.consistent(bFalse));
    }
}
