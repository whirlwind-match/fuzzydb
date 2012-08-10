package com.wwm.db.tests.functional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import junit.framework.Assert;

import org.fuzzydb.client.Ref;
import org.fuzzydb.client.Transaction;
import org.fuzzydb.client.exceptions.UnknownObjectException;
import org.junit.Test;

import com.wwm.db.BaseDatabaseTest;



public class NamespacesTest extends BaseDatabaseTest {

    
    // sufficiently unique namespace versions, such that we don't care if 
	// we've reloaded an existing database for this test.
	private static final String NAMESPACE1 = "NS1_" + System.currentTimeMillis();
	private static final String NAMESPACE2 = "NS2_" + System.currentTimeMillis();


	@Test public void testCreate2ObjectsNamespaced() {
		Ref<String> ref1 = null;
		Ref<String> ref2 = null;
		
		{
			Transaction t = store.getAuthStore().begin();

			String s1 = new String("Hello World 1");
			String s2 = new String("Hello World 2");

			boolean thrown = false;
			try {
				t.getVersion(s1);
			} catch (UnknownObjectException e) {
				thrown = true;
			}
			Assert.assertTrue(thrown);
			
			t.setNamespace("ns1");
			ref1 = t.create(s1);
			
			Assert.assertEquals(ref1, t.getRef(s1));
			Assert.assertEquals(0, t.getVersion(s1));
			
			t.setNamespace("ns2");
			ref2 = t.create(s2);
			Assert.assertEquals(ref2, t.getRef(s2));
			Assert.assertEquals(0, t.getVersion(s2));

			t.commit();
			
			Assert.assertEquals(1, t.getVersion(s1));
			Assert.assertEquals(1, t.getVersion(s2));
		}
		
		{
			Transaction t = store.begin();
			String s = t.retrieve(ref1);
			Assert.assertEquals("Hello World 1", s);
			Assert.assertEquals(1, t.getVersion(s));
			s = t.retrieve(ref2);
			Assert.assertEquals("Hello World 2", s);
			Assert.assertEquals(1, t.getVersion(s));
		}
		
		// Test multi retrieve
		{
			Collection<Ref<String>> refs = new ArrayList<Ref<String>>();
			refs.add(ref1);
			refs.add(ref2);

			Transaction t = store.begin();
			Map<Ref<String>, String> result = t.retrieve(refs);
			
			Assert.assertTrue(result.containsKey(ref1));
			Assert.assertTrue(result.containsKey(ref2));
			Assert.assertEquals("Hello World 1", result.get(ref1));
			Assert.assertEquals("Hello World 2", result.get(ref2));
		}
	}


    @Test public void testListNamespaces() {

    	
    	Transaction t = store.getAuthStore().begin();
    	String[] defaultNamespaces = t.listNamespaces();
    	int numDefaultsNSes = defaultNamespaces.length;

    	t.setNamespace(NAMESPACE1);
        t.create(new String("Hello World"));
        t.commit();

        t = store.getAuthStore().begin();
        t.setNamespace(NAMESPACE2);
        t.create(new String("Hello World"));
        t.commit();
        
        t = store.getAuthStore().begin();
        String[] namespaces = t.listNamespaces();
        Assert.assertEquals(2, namespaces.length - numDefaultsNSes);
        
        HashSet<String> set = new HashSet<String>();
        set.addAll(Arrays.asList(namespaces));
        
        Assert.assertTrue(set.contains(NAMESPACE1));
        Assert.assertTrue(set.contains(NAMESPACE2));
    }

}
