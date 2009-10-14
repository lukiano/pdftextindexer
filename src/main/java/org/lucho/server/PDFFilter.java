/**
 * 
 */
package org.lucho.server;

import java.io.File;
import java.io.FileFilter;

public class PDFFilter implements FileFilter {

	public boolean accept(File file) {
		return file.isDirectory() || file.getName().endsWith(".pdf");
	}
	  
  }