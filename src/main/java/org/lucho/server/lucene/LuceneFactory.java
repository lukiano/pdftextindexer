package org.lucho.server.lucene;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

public interface LuceneFactory {
	
	void open();
	
	void close();

	IndexWriter getWriter();

	IndexSearcher getSearcher();
}
