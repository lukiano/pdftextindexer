package org.lucho.server.lucene;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface SearchFiles {

	List<File> search(final String text) throws IOException;

}