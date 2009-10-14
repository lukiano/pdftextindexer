package org.lucho.server.lucene.impl;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
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
	
	private static final Logger log = Logger.getLogger(LuceneFactory.class);

	private IndexWriter indexWriter;
	
	private IndexReader indexReader;
	
	private IndexSearcher indexSearcher;
	
	private AnalyzerFactory analyzerFactory;

	@Inject
	public LuceneFactoryImpl(final AnalyzerFactory analyzerFactory) {
		this.analyzerFactory = analyzerFactory;
		this.open();
	}

	public IndexSearcher getSearcher() {
		return indexSearcher;
	}

	public IndexWriter getWriter() {
		return indexWriter;
	}

	public void open() {
		Directory directory;
		try {
			directory = new NIOFSDirectory(new File(
					Constants.INDEX_DIR));
			indexWriter = new IndexWriter(directory, analyzerFactory
					.getAnalyzer(), MaxFieldLength.UNLIMITED);
			indexReader = indexWriter.getReader();
			indexSearcher = new IndexSearcher(indexReader);
		} catch (IOException e) {
			log.error("Unable to open Lucene");
		}
	}

	public void close() {
		try {
			indexSearcher.close();
			indexReader.close();
			indexWriter.close();
		} catch (IOException e) {
			log.error("Error while closing Lucene.", e);
		}
	}

}
