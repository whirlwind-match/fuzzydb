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

import java.util.Hashtable;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import com.wwm.db.core.LogFactory;


public class Activator implements BundleActivator {

	private static final String PATH = "/fuzz";
	private HttpServlet servlet = new FuzzAbderaServlet();
	private ServiceTracker httpServiceTracker;
	
//	private ServiceRegistration reg;

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {

		// This should work but didn't!
//		HttpServlet servlet = new FuzzAbderaServlet();
//		Hashtable<String, Object> props = new Hashtable<String,Object>();
//		props.put("alias",  PATH); // desired http alias
//		reg = context.registerService(HttpServlet.class.getName(), servlet, props); // THIS SHOULD WORK AND DOESN'T UNDER EQUINOX

		httpServiceTracker = new ServiceTracker(context, HttpService.class.getName(), null) {
			@Override
			public Object addingService(ServiceReference reference) {
				HttpService svc = (HttpService) context.getService(reference);
				Hashtable<String, Object> props = new Hashtable<String,Object>();
				try {
					svc.registerServlet(PATH, servlet, props, null);
					System.out.println("Registered servlet at: " + PATH);
				} catch (Exception e) {
					LogFactory.getLogger(Activator.class).log(Level.SEVERE, "Unhandled exception starting bundle: " 
							+ context.getBundle().getSymbolicName(), e );
					throw new Error("Unhandled exception:", e);
				}
				return svc;
			}
			
			@Override
			public void removedService(ServiceReference reference, Object service) {
				HttpService svc = (HttpService) context.getService(reference);
				svc.unregister(PATH);
				super.removedService(reference, service);
			}
		};
		httpServiceTracker.open(); 
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
//		reg.unregister();
		httpServiceTracker.close();
	}

}
