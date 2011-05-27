package com.wwm.util;

import java.io.Closeable;
import java.io.IOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

/**
 * Processor for doing an operation with a number of resources.
 * 
 * Wraps IOException in RuntimeException to save on boilerplate for obvious errors
 * 
 * @author Neale Upstone
 */
abstract public class ResourcePatternProcessor {
	
	/**
	 * 
	 * @param resource - the resource to process
	 * @return if you use resource.getInputStream() and don't close it, then return it here for closing 
	 * by this class. Otherwise return null;
	 * @throws IOException
	 */
	abstract protected Closeable process(Resource resource) throws IOException;

	public final void runWithResources(String resourcesUrl) {

        Resource[] resources = null;
        try {
			resources = new PathMatchingResourcePatternResolver().getResources(resourcesUrl);
		} catch (IOException e) {
			// shouldn't happen
			throw new RuntimeException(e);
		}

        for (Resource resource: resources) {
        	Closeable toClose = null;
        	try {
				toClose = process(resource);
			} catch (IOException e) {
				// shouldn't happen
				throw new RuntimeException(e);
			}
			finally {
				if (toClose != null) {
					try {
						toClose.close();
					} catch (IOException e) {
						// Ignore any error closing, we can't do much about it
						// TODO: Review: Perhaps log this or re-throw
					}
				}
			}
        }
	}
}
