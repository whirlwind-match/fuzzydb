/******************************************************************************
 * Copyright (c) 2004-2008 Whirlwind Match Limited. All rights reserved.
 *
 * This is open source software; you can use, redistribute and/or modify
 * it under the terms of the Open Software Licence v 3.0 as published by the 
 * Open Source Initiative.
 *
 * You should have received a copy of the Open Software Licence along with this
 * application. if not, contact the Open Source Initiative (www.opensource.org)
 *****************************************************************************/
package com.wwm.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StringUtils {

	public static String stripSpaces(String code) {
		// exit without creating objects if no spaces found
		if (code.indexOf(' ') < 0) {
			return code;
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < code.length(); i++) {
			char c = code.charAt(i);
			if (c != ' ') {
				sb.append(c);
			}
		}
		return sb.toString();
	}

	public static String subStringBefore(String str, String token) {
		int i = str.indexOf(token);
		if (i == -1) {
			return null;
		}
		return str.substring(0, i);
	}

    static public String readToString(InputStreamReader r) {
        BufferedReader br = new BufferedReader( r );
        StringBuilder str = new StringBuilder();
        String s;
        try {
            while( (s = br.readLine()) != null ){
                str.append(s);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    
        return str.toString();
    }

}
