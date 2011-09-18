package com.wwm.attrs.layout;

import org.junit.Before;


import com.wwm.attrs.internal.AttrDefinitionMgr;
import com.wwm.attrs.layout.LayoutAttrMap;
import com.wwm.db.core.Settings;


/**
 * @author Adeline Darling
 *
 * Base class for attribute tests. Provides subclass for creating instance
 * of AttrDefinitionMgr and protected mgr field for storing it. Also default
 * setUp method which creates instance of AttrDefinitionMgr.
 */
public abstract class BaseAttributeTest {
	
	protected AttrDefinitionMgr mgr;

	/**
	 * Create subclass so we can get at an instance
	 */
	@SuppressWarnings("serial")
	static class SimpleAttrDefinitionMgr extends AttrDefinitionMgr {

		static AttrDefinitionMgr createInstance(){
			return new SimpleAttrDefinitionMgr();
		}
	}

	@Before
	public void setUpBaseAttrTest() throws Exception {
		// reset attrDefMgr
		Settings.getInstance().setAttributeMapClassName(LayoutAttrMap.class.getName());
		mgr = SimpleAttrDefinitionMgr.createInstance();

		Settings.getInstance().setConfigDAOClassName(StaticMapDao.class.getName());
	}

}
