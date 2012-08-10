package org.fuzzydb.attrs.layout;

import java.util.Map;
import java.util.TreeMap;

import org.fuzzydb.core.dao.SimpleDAO;


/**
 * A simple Dao that pretends to store things, but actually sticks one instance per class in
 * a map against the class name.  The stored data does not persist.
 */
public class StaticMapDao implements SimpleDAO {

	static final Map<String, Object> instances = new TreeMap<String, Object>();
	
	public void begin() {
		// Do nothing
	}

	public void commit() {
		// Do nothing we commit on create!
	}

	public Object create(Object object, Object key) {
		String mapKey = getKey(object.getClass(), key);
		instances.put(mapKey, object);
		return mapKey;
	}

	@SuppressWarnings("unchecked")
	public <T> T retrieve(Class<T> clazz, Object key) {
		String mapKey = getKey(clazz, key);
		return (T)instances.get(mapKey);
	}

	public void update(Object object, Object ref) {
		instances.put( (String)ref, object);
	}

	// Create a unique key that obeys SimpleDao interface
	private String getKey(Class<?> clazz, Object key) {
		return clazz.getName() + key;
	}
	
}
