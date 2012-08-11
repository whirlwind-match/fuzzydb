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
package org.fuzzydb.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**Uber CSV line cracking parser that handles empty fields, quoted fields, quoted fields containing escaped quotes and commas.
 * This class should probably implement Iterable.
 * @author ac
 *
 */
public class CsvTokeniser {

	private final static Pattern pCSVmain = Pattern.compile(

			"\\G(?:^|,)\n"+
			"(?:\n"+
			"# Either a double-quoted field...\n"+
			"\" # field's opening quote\n"+
			"( (?> [^\"]*+ ) (?> \"\" [^\"]*+ )*+ )\n"+
			"\" # field's closing quote\n"+
			"# ... or ...\n"+
			"|\n"+
			"# ... some non-quote/non-comma text ...\n"+
			"( [^\",]*+ )\n"+
			")\n",
			Pattern.COMMENTS);
/*
			"\\G(?:^|,)"+
			"(?:" + // # Either a double-quoted field...\r\n"+
				"\"" + // # field's opening quote\r\n"+
				"( (?> [^\"]* ) (?> \"\" [^\"]* )* )"+   // "asdf""sdfsf""asdfasf"
				// "([^\",]*)" +
			"\"" + // # field's closing quote\r\n"+
			// "# ... or ...\r\n"+
			"|"+
			// "# ... some non-quote/non-comma text ...\r\n"+
			"( [^\",]* )"+
			")",
			Pattern.COMMENTS);
*/	
	private final static Pattern pCSVquote = Pattern.compile("\"\"");
	
	//		 Now create Matcher objects, with dummy text, that we'll use later.
	private final Matcher mCSVmain = pCSVmain.matcher("");
	private final Matcher mCSVquote = pCSVquote.matcher("");

	private int missingStarts = 0;
	
	public CsvTokeniser(String line) {
		while (line.startsWith(",")) {
			missingStarts++;
			line = line.substring(1);
		}
		mCSVmain.reset(line); // Tie the target text to the mCSVmain object
	}
			
	/**Get the current token and advance to the next field
	 * @return the next field, empty string if field is empty, null if end of line
	 */
	public String next() {
		if (missingStarts > 0) {
			missingStarts--;
			return "";
		}
		if ( mCSVmain.find() )
		{
			String field; // We'll fill this in with $1 or $2 . . .
			//System.out.println("(1) " + mCSVmain.group(1) );
			String second = mCSVmain.group(2);
			//System.out.println("(2) " + second );
			if ( second != null )
				return second;
			else {
	//		 If $1, must replace paired double-quotes with one double quote
				mCSVquote.reset(mCSVmain.group(1));
				field = mCSVquote.replaceAll("\"");
			}
	//		 We can now work with field . . .
			return field;
		}
		return null;
	}
}

/*
public class CsvTokeniser {

	private static final Pattern csvPattern = Pattern.compile("\"([^\"]+?)\",?|([^,]+),?|,");
	//private static final Pattern csvPattern = Pattern.compile("\"([,]*[^\"]*?\\\")\",");
	private static final Pattern csvPattern = Pattern.compile(",(?=(?:[^\"]*\"[^\"]*\")*(?![^\"]*\"))");
	
	private final boolean stripQuotes;
	private final Matcher matcher;
	
	public CsvTokeniser(String line, boolean stripQuotes) {
		this.stripQuotes = stripQuotes;
		matcher = csvPattern.matcher(line);
	}
	
	public String next() {
		if (!matcher.find()) return null;
		String result = matcher.group();
		if (result == null) return null;
		if (result.endsWith(",")) {
			result = result.substring(0, result.length() - 1);
		}
		if (stripQuotes && result.startsWith("\"")) {
			result = result.substring(1, result.length() - 1);
		}
		return result;
	}
}
*/
