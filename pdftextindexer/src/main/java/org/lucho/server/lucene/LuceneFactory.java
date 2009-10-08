package org.lucho.server.lucene;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

public interface LuceneFactory {

	IndexWriter getWriter();

	IndexSearcher getSearcher();
}
