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
package com.wwm.abdera.util.server;

import org.apache.abdera.protocol.server.CollectionAdapter;
import org.apache.abdera.protocol.server.CollectionInfo;
import org.apache.abdera.protocol.server.Filter;
import org.apache.abdera.protocol.server.FilterChain;
import org.apache.abdera.protocol.server.RequestContext;
import org.apache.abdera.protocol.server.ResponseContext;
import org.apache.abdera.protocol.server.TargetType;
import org.apache.abdera.protocol.server.context.RequestContextWrapper;
import org.apache.abdera.protocol.server.impl.AbstractWorkspaceProvider;
import org.apache.abdera.protocol.server.impl.RegexTargetResolver;
import org.apache.abdera.protocol.server.impl.SimpleWorkspaceInfo;
import org.apache.abdera.protocol.server.impl.TemplateTargetBuilder;


/**
 * A generic workspace provider that just provides the bare minimum of access to a collection adapter
 * 
 * Thoughts: The CollectionAdapter should probably be something that is retrieved as an OSGi Service to match
 * a given feed Url pattern...
 */
public class BaseProviderImpl extends AbstractWorkspaceProvider {

    //	protected static Logger log = LogFactory.getLogger(BaseProviderImpl.class);

	private final CollectionAdapter ca;
	
	/**
	 * NOTE: This is protected to ensure we remember to make the concrete version's default ctor provide it.
	 * @param ca
	 */
    protected BaseProviderImpl(CollectionAdapter ca) {
        super();
        this.ca = ca;
        RegexTargetResolver tr = new RegexTargetResolver();
        tr.setPattern("/fuzz/feed(\\?[^#]*)?", TargetType.TYPE_COLLECTION);
        tr.setPattern("/fuzz/feed/([^/#?]+)(\\?[^#]*)?", TargetType.TYPE_ENTRY);
        tr.setPattern("/fuzz(\\?[^#]*)?", TargetType.TYPE_SERVICE);
        
        targetResolver = tr;
        
        // The target builder is used to construct url's for the various targets
        setTargetBuilder(
            new TemplateTargetBuilder()
              .setTemplate(TargetType.TYPE_SERVICE, "{target_base}/atom")
              .setTemplate(TargetType.TYPE_COLLECTION, "{target_base}/atom/{collection}{-opt|?|q,c,s,p,l,i,o}{-join|&|q,c,s,p,l,i,o}")
              .setTemplate(TargetType.TYPE_CATEGORIES, "{target_base}/atom/{collection};categories")
              .setTemplate(TargetType.TYPE_ENTRY, "{target_base}/atom/{collection}/{entry}")
          );
        
        // Add a Workspace descriptor so the provider can generate an atompub service document
        SimpleWorkspaceInfo workspace = new SimpleWorkspaceInfo();
        workspace.setTitle("A Simple Workspace");
        CollectionInfo collection = new DumbCollectionInfo();
        workspace.addCollection(collection);
        addWorkspace(workspace);
        
        
        // Add one of more Filters to be invoked prior to invoking the Provider
//        addFilter(new SimpleFilter());
	}
    
    /** Example filter */
    public class SimpleFilter 
    implements Filter {
      public ResponseContext filter(
        RequestContext request, 
        FilterChain chain) {
          RequestContextWrapper rcw = new RequestContextWrapper(request);
          rcw.setAttribute("offset", 10);
          rcw.setAttribute("count", 10);
          return chain.next(rcw);
      }    

    }


    public CollectionAdapter getCollectionAdapter(RequestContext request) {
    	return ca;
    }
	
}