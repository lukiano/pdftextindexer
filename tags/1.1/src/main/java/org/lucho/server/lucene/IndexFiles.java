package org.lucho.server.lucene;

import java.io.File;
import java.io.IOException;

public interface IndexFiles {

	void index(final File docsDir) throws IOException;
	
	void clearIndex() throws IOException;
	
}