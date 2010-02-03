package org.lucho.server.lucene;

import java.io.Closeable;
import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spell.SpellChecker;

public interface LuceneFactory extends Closeable {
	
	void reopen() throws IOException;
	
	IndexWriter getWriter();

	IndexSearcher getSearcher();
	
	void updateSpellIndex() throws IOException;
	
	SpellChecker getSpellChecker();
	
}
