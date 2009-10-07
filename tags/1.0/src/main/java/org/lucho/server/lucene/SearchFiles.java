package org.lucho.server.lucene;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

public interface SearchFiles {

	File[] search(final String text, final File indexDir) throws CorruptIndexException,
	IOException, ParseException;

}