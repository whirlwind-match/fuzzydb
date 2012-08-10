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
package com.wwm.atom.impl;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import org.slf4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.abdera.protocol.server.ServiceManager;
import org.apache.abdera.protocol.server.servlet.AbderaServlet;
import org.apache.commons.codec.binary.Base64;
import org.fuzzydb.core.LogFactory;

import com.wwm.context.ApplicationContext;
import com.wwm.context.SimpleApplicationContext;
import com.wwm.context.servlet.WebContainerAppListener;
import com.wwm.indexer.IndexerFactory;


public class FuzzAbderaServlet extends AbderaServlet {

    static final Logger log = LogFactory.getLogger(FuzzAbderaServlet.class);

    private static final long serialVersionUID = 1L;

    public FuzzAbderaServlet() {
    	int i = 42;   	i++;
	}
    
    @Override
    protected Map<String, String> getProperties(ServletConfig config) {
    	Map<String, String> map = super.getProperties(config);
    	map.put(ServiceManager.PROVIDER, ProviderImpl.class.getName());
    	return map;
    }
    
//    @Override
//    protected Provider createProvider() {
//    	ProviderImpl p = new ProviderImpl();
//    	p.init( getAbdera(), getProperties(getServletConfig()));
//		return p;
//    }


    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // SIMULATE BASICS OF WHAT SPRING DOES
        try {
            request.getSession(); // forces session to be created - must happen before preRequest below.

            ApplicationContext applicationContext = SimpleApplicationContext.getInstance(); // simple but better to map on to container
            WebContainerAppListener.getInstance().preRequest( applicationContext, request );

            //    		request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, ctx);
            //			interceptor.preHandle(request, response, null);

            // Set up SiteDataMgr to know what site/host we are on, so that login will work for correct set of accounts
            siteDataMgrInterceptor(request);

            // FIXME: Create interceptor to deal with Auth header
            // authInterceptor.preHandle( request, response, null );
            if ( !authorize(request, response) ) {
                return;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        super.service(request, response);
    }



    private boolean authorize(HttpServletRequest request,
            HttpServletResponse response) throws IOException,
            UnsupportedEncodingException {
        //		SecurityMgr securityMgr = ServiceFactory.getSecurityService();
        //		try {
        //			if (securityMgr.isLoggedIn()) {
        //				return true;
        //			}

        String auth = request.getHeader("Authorization");
        if ( auth == null) {
            // if no auth supplied, expect some
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setHeader("WWW-Authenticate", "Basic realm=\"Private Access\""	);
            return false;
        }
        assert( auth.startsWith("Basic "));
        auth = auth.substring( "Basic ".length() );
        Base64 b64 = new Base64();
        byte[] decodeBuffer = b64.decode(auth.getBytes());
        //        byte[] decodeBuffer = new BASE64Decoder().decodeBuffer(auth);
        String decoded = new String( decodeBuffer, "utf-8" );
        int colonIndex = decoded.indexOf(':');
        String username = decoded.substring(0, colonIndex);
        //			String password = decoded.substring(colonIndex + 1);
        //			securityMgr.login(username, password);
        //			log.info("Successfully logged in as:" + username);


        // Leave host and port as default, and set store as username
        String url = "wwmdb:/" + username;
        IndexerFactory.setCurrentStoreUrl(url);

        return true;
        //		} catch (ServiceUserIdOrPasswordInvalidException e) {
        //			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        //			response.setHeader("WWW-Authenticate", "Basic realm=\"Private Access\""	);
        //		} catch (BaseServiceException e){
        //			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        //			e.printStackTrace();
        //		}
        //		return false;
    }


    /**
     * This ought to be quite simply a mapping of URL/account to a store
     * Initially just use the Username to select a store
     * 
     * @param request
     * @throws ServiceInternalErrorException
     */
    private void siteDataMgrInterceptor(HttpServletRequest request) {
        // -----------------------------------------------------------------
        // Very simply, we set the site and theme based on current request
        // Theme and Site config services then cache relevant data.
        // -----------------------------------------------------------------
        //		String host = UrlUtils.getDomainName(request);
        //		host = UrlUtils.stripHostname(host);
        //
        //		try {
        //			if (!SiteDataMgr.isSite(host)) {
        //				host = ApplicationContextManager.getInstance().getAppConfig().getDefaultSite();
        //			}
        //		} catch (Throwable e) {
        //			// FIXME: If database is down it gets nasty, so set site to what we got from URL, and finish
        //			SiteDataMgr.setSiteName(host);
        ////		    	return true;
        //		}
        //
        //		SiteDataMgr.setSiteName(host);
    }
}
