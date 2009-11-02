package org.lucho.server.lucene;

import java.io.IOException;

import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spell.SpellChecker;

public interface LuceneFactory {
	
	void open();
	
	void close();

	IndexWriter getWriter();

	IndexSearcher getSearcher();
	
	void updateSpellIndex() throws IOException;
	
	public SpellChecker getSpellChecker();
	
}
