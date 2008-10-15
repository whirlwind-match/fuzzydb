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
package com.wwm.postcode;


import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * Activator to get hold of other services we need.
 *
 */
public class Activator implements BundleActivator {

	private ServiceTracker svcTracker;
	
	public void start(BundleContext context) throws Exception {
		
		// create a tracker and track the service, and get callback when this service is added
		svcTracker = new ServiceTracker(context, PostcodeService.class.getName(), null){
			@Override
			public Object addingService(ServiceReference reference) {
				
				PostcodeService svc = (PostcodeService) context.getService(reference);
				PostcodeConvertor.setService(svc);
				System.out.println("Postcode service added:" + svc.getClass().getName() );
				
				return super.addingService(reference);
			}
			
			@Override
			public void removedService(ServiceReference reference, Object service) {
				super.removedService(reference, service);
				
//				PostcodeService svc = (PostcodeService) context.getService(reference); 
			}
		};
		svcTracker.open();

		
	}

	public void stop(BundleContext context) throws Exception {
		svcTracker.close();
		svcTracker = null;
	}

}
