package org.lucho.server.lucene;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;

public interface IndexFiles {

	void index(final FileObject docsDir) throws IOException;
	
	void clearIndex() throws IOException;
	
}