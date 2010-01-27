package org.lucho.server.lucene;

import java.io.IOException;
import java.util.List;

import org.apache.commons.vfs.FileObject;

public interface SearchFiles {

	List<FileObject> search(final String text) throws IOException;

	String highlight(String queryString, final String url) throws IOException;
	
	String suggest(String queryString) throws IOException;
	
}