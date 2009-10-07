package org.lucho.server.lucene;

import java.io.File;
import java.io.IOException;

public interface IndexFiles {

	void index(final File docsDir, final File indexDir) throws IOException;
	
}