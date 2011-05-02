package com.wwm.io.core;

/**
 * Implemented typically by a MessageSource which may need to ask for class definitions (i.e. a remoting one vs one
 * that shares the same classloader).

 * @author Neale Upstone
 */
public interface ClassDefinitionRepositoryAware {

	void setCli(ClassLoaderInterface cli);

}
