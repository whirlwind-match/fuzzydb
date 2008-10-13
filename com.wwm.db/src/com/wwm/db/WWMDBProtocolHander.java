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
package com.wwm.db;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import com.wwm.db.core.Settings;

/**
 * Protocol handler to fit with
 * @author Administrator
 *
 */
public class WWMDBProtocolHander extends URLStreamHandler {

    static private WWMDBProtocolHander instance = new WWMDBProtocolHander();

    static public WWMDBProtocolHander getInstance() {
        return instance;
    }

    private WWMDBProtocolHander() {
        // should use getInstance()
    }

    @Override
    protected URLConnection openConnection(URL url) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected int getDefaultPort() {
        return Settings.getInstance().getPrimaryServerPort();
    }

    static public URL getAsURL(String strUrl) throws MalformedURLException {
        return new URL(null, strUrl, getInstance());
    }
}
