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
package com.wwm.db.internal;

import org.junit.Test;

import com.wwm.db.userobjects.MutableString;


public class MetaMapTest {

	/**
	 * Test to mimic adding, finding, and re-adding (i.e. create & commit(), getRef(), update & commit()
	 * @throws InterruptedException 
	 */
	@Test //(timeout=10000)
	public void testModifiedObject() throws InterruptedException{
		MetaMap map = new MetaMap();
		{
			MutableString str = new MutableString("Initial String");
			RefImpl<MutableString> refImpl = new RefImpl<MutableString>(1,2,3);
			MetaObject<MutableString> mo = new MetaObject<MutableString>(refImpl, 1, str);
			map.add(mo);
			assert map.size() == 1;
			
			{
				MetaObject found = map.find(str);
				assert found != null; // what do we expect? .getObject() == .getObject() and ref.equals
				assert found.getRef().equals(refImpl);
			}
			
			str.value = "Modified String";
			MetaObject<MutableString> mo2 = new MetaObject<MutableString>(refImpl, 2, str); // same object but modified value
			map.add(mo2);
	
			// Correct behaviour is that we should have replaced a key
			assert map.size() == 1; // map hasn't changed size
			
			{
				MetaObject found = map.find(str);
				assert found != null; // what do we expect? .getObject() == .getObject() and ref.equals
				assert found.getRef().equals(refImpl);
				assert found.getVersion() == 2;
			}
			
			// test weak ref causes removal of key
			assert map.size() == 1; // still 1
			str = null; mo = null; mo2 = null;
		}
		
//		Object o;
//		// try to force GC
//		for (int i = 0; i < 10000; i++){
//			o = new int[1000];
//		}
//		System.gc();
//		synchronized (map) {
//			// Wait for size to drop to zero
//			while (map.size() != 0) {
//				map.wait(100);
//				map.find(new Object()); // to cause RefQueue.poll/remove() to be called (via private flush() function)
//			}
//		}
//		// str is no longer in scope
//		System.gc(); // Mmm.. like this is guaranteed!
//		synchronized(map){ // need memory barrier cos update to map contents is happening elsewhere...
//			assert map.size() == 0;
//		}
//		
	}
}
