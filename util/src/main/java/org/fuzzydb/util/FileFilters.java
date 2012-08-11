/**
 * 
 */
package org.fuzzydb.util;

import java.io.File;
import java.io.FileFilter;

public class FileFilters {
	
	private static final FileFilter isFile = new IsFile();
	private static final FileFilter isDir = new IsDir();
	
	public static class IsFile implements FileFilter {
		public boolean accept(File file) { return file.isFile();}
	}

	public static class IsDir implements FileFilter {
		public boolean accept(File file) { return file.isFile();}
	}

	public static FileFilter isDir(){
		return isDir;
	}
	
	public static FileFilter isFile() {
		return isFile;
	}
}