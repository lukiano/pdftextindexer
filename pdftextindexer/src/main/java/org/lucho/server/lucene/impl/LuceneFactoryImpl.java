package org.lucho.server.lucene.impl;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.NIOFSDirectory;
import org.lucho.client.Constants;
import org.lucho.server.lucene.AnalyzerFactory;
import org.lucho.server.lucene.LuceneFactory;

import com.google.inject.Inject;

public class LuceneFactoryImpl implements LuceneFactory {

	private final IndexWriter indexWriter;
	
	private final IndexReader indexReader;
	
	private final IndexSearcher indexSearcher;

	@Inject
	public LuceneFactoryImpl(final AnalyzerFactory analyzerFactory) throws IOException {
		Directory directory = new NIOFSDirectory(new File(
				Constants.INDEX_DIR));
		indexWriter = new IndexWriter(directory, analyzerFactory
				.getAnalyzer(), MaxFieldLength.UNLIMITED);
		indexReader = indexWriter.getReader();
		indexSearcher = new IndexSearcher(indexReader);
	}

	public IndexSearcher getSearcher() {
		return indexSearcher;
	}

	public IndexWriter getWriter() {
		return indexWriter;
	}

}
