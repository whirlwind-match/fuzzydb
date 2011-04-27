package com.wwm.io.core.impl;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

import com.wwm.db.core.LogFactory;
import com.wwm.io.core.ClassLoaderInterface;

public class DummyCli implements ClassLoaderInterface {

	private static final long serialVersionUID = 1L;

	static private Logger log = LogFactory.getLogger(DummyCli.class);

	public Class<?> getClass(int storeId, String className) throws ClassNotFoundException {
		//log.info("DummyCli getClass() loading: " + className );


		ClassLoader cl = getClass().getClassLoader();
		//		ClassLoader cl = ClassLoader.getSystemClassLoader();

		// see http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6446627 JDK5 code was: return systemClassLoader.loadClass(className);
		Class<?> c = Class.forName(className, false, cl);
		if (c == null){
			return null; // just put this in so I can have a breakpoint
		}
		return c;
	}

	/**
	 * Resolve the class and return it's bytes for returning to requesting entity.
	 */
	public byte[] getClassBytecode(int storeId, String className) {
		log.info("DummyCli getClassBytecode() loading: " + className );
		
		ClassLoader cl = getClass().getClassLoader();
		Class<?> c;
		try {
			c = Class.forName(className, false, cl);
		} catch (ClassNotFoundException e) {
			return null; // or re-throw
		}
	
		String resourceName = "/" + className.replace('.', '/')+ ".class";
		
		InputStream stream = cl.getResourceAsStream(resourceName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			stream.close();
		} catch (IOException e) {
			// ignore
		}
		return null;
		
//		throw new UnsupportedOperationException();
	}

	public void addClass(int storeId, String className, byte[] bytecode) {
		//log.info("DummyCli addClass() storing: " + className );

	}

	public void waitForClass(int storeId, String className) {

	}

}
