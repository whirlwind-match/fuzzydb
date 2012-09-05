package org.fuzzydb.tests.functional;

import org.junit.BeforeClass;

public class TCPBasedCRUDTest extends CRUDTest {

	@BeforeClass
	static public void setNonEmbedded() {
		useEmbeddedDatabase = false;
	}
}
