package org.lucho.server.lucene.impl;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
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
	
	private SpellChecker spellChecker;

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
		try {
			Directory directory = FSDirectory.open(new File(
					Constants.INDEX_DIR));
			indexWriter = new IndexWriter(directory, analyzerFactory
					.getAnalyzer(), MaxFieldLength.UNLIMITED);
			indexReader = indexWriter.getReader();
			indexSearcher = new IndexSearcher(indexReader);
			
			Directory suggestDirectory = FSDirectory.open(new File(Constants.SUGGEST_INDEX_DIR));
			this.spellChecker = new SpellChecker(suggestDirectory);
		} catch (IOException e) {
			log.error("Unable to open Lucene");
		}
	}
	
	public void updateSpellIndex() throws IOException {
		indexReader = indexReader.reopen();
		Dictionary dictionary = new LuceneDictionary(indexReader, Constants.CONTENTS_FIELD);
		spellChecker.indexDictionary(dictionary);
	}
	
	public SpellChecker getSpellChecker() {
		return spellChecker;
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
