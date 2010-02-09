package org.lucho.server.lucene;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.lucho.client.Node;

public interface IndexFiles {

	void index(final Node node) throws IOException;
	
	void index(final FileObject file) throws IOException;
	
	void clearIndex() throws IOException;
	
}