package org.fuzzydb.io.core;

import java.io.IOException;

public interface ClassDefinitionSource {

	void requestClassData(int storeId, String className) throws IOException;

}