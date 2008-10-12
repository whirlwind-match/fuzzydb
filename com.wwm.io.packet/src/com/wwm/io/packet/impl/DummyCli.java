package com.wwm.io.packet.impl;

import java.util.logging.Logger;

import com.archopolis.internal.util.LogFactory;
import com.wwm.io.packet.ClassLoaderInterface;

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

	public byte[] getClassBytecode(int storeId, String className) {
		log.info("DummyCli getClassBytecode() loading: " + className );
		throw new UnsupportedOperationException();
	}

	public void addClass(int storeId, String className, byte[] bytecode) {
		//log.info("DummyCli addClass() storing: " + className );

	}

	public void waitForClass(int storeId, String className) {

	}

}
