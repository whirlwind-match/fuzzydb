package org.fuzzydb.attrs.layout;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.fuzzydb.attrs.dimensions.Value3D;
import org.fuzzydb.attrs.layout.LayoutAttrMap;
import org.fuzzydb.attrs.location.EcefVector;
import org.fuzzydb.core.whirlwind.internal.IAttribute;
import org.fuzzydb.core.whirlwind.internal.IAttributeMap;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


@Ignore("Fails due to LayoutMapConfig.getInstance() not being reset when attrDefMgr is in BaseAttributeTest")
public class EcefVectorTest extends BaseAttributeTest {

	protected int locationId;
	protected EcefVector ecef;
	protected EcefVector ecefUp;
	protected EcefVector ecefDown;
	
	@Before
	public void setUp() throws Exception {
		// reset attrDefMgr
		locationId = mgr.getAttrId("Location", EcefVector.class);
		ecef = new EcefVector(locationId, 1.0f, 0.5f, 0.5f);
		ecefUp = new EcefVector(locationId, 1.0f, 0.6f, 0.6f);
		ecefDown = new EcefVector(locationId, 1.0f, 0.4f, 0.4f);
	}
	
	/**
	 * Put EcefVector instance into a LayoutAttrMap and retrieve it.
	 * Test it has same values but is different object.
	 */
	@Test public void testEcefVector() throws Exception {

		IAttributeMap<IAttribute> map = new LayoutAttrMap<IAttribute>();
		map.put(locationId, ecef);
		
		IAttribute attr = map.findAttr(locationId);
	
		Value3D v2 = (Value3D)attr;
		assertTrue(ecef.equals(v2));
		assertFalse(ecef == v2);
	}
	
	/**
	 * Expand EcefVector up, put into a LayoutAttrMap and retrieve it.
	 * Test it can no longer be expanded up by the same EcefVector it was
	 * expanded with before map insertion. Test it can be expanded up
	 * by new EcefVector with higher values.
	 */
	@Test public void testEcefVectorExpandUp() throws Exception {
		
		IAttributeMap<IAttribute> map = new LayoutAttrMap<IAttribute>();
		ecef.expandUp(ecefUp);
		map.put(locationId, ecef);
		
		IAttribute attr = map.findAttr(locationId);
		EcefVector v = (EcefVector)attr;
		
		assertTrue(ecef.equals(v));
		assertFalse(ecef == v);
		
		assertFalse(v.canExpandUp(ecefUp));
		
		EcefVector ecefOut = new EcefVector(locationId, 1.0f, 0.7f, 0.7f);
		assertTrue(v.canExpandUp(ecefOut));
	}

	/**
	 * Expand EcefVector down, put into a LayoutAttrMap and retrieve it.
	 * Test it can no longer be expanded down by the same EcefVector it was
	 * expanded with before map insertion. Test it can be expanded down
	 * by new EcefVector with lower values.
	 */
	@Test public void testEcefVectorExpandDown() throws Exception {
		
		IAttributeMap<IAttribute> map = new LayoutAttrMap<IAttribute>();
		ecef.expandDown(ecefDown);
		map.put(locationId, ecef);
		
		IAttribute attr = map.findAttr(locationId);
		EcefVector v = (EcefVector)attr;
		
		assertTrue(ecef.equals(v));
		assertFalse(ecef == v);
		
		assertFalse(v.canExpandDown(ecefDown));
		
		EcefVector ecefOut = new EcefVector(locationId, 1.0f, 0.3f, 0.3f);
		assertTrue(v.canExpandDown(ecefOut));
	}
}
