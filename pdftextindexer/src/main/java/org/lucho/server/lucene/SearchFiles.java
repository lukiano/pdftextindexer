package org.lucho.server.lucene;

import java.io.File;
import java.io.IOException;

public interface SearchFiles {

	File[] search(final String text) throws IOException;

}