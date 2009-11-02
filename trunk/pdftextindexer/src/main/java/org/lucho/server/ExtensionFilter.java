/**
 * 
 */
package org.lucho.server;

import java.io.File;
import java.io.FileFilter;

public class ExtensionFilter implements FileFilter {
	
	public static final String EXTENSION = ".metadata";

	public boolean accept(File file) {
		return file.isDirectory() || !file.getName().endsWith(EXTENSION);
	}
	  
  }