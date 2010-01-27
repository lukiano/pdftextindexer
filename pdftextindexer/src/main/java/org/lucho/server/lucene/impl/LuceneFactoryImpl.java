package org.lucho.server.lucene.impl;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.spell.Dictionary;
import org.apache.lucene.search.spell.LuceneDictionary;
import org.apache.lucene.search.spell.SpellChecker;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lucho.server.ServerConstants;
import org.lucho.server.lucene.AnalyzerFactory;
import org.lucho.server.lucene.LuceneFactory;

import com.google.inject.Inject;

public class LuceneFactoryImpl implements LuceneFactory {

	private IndexWriter indexWriter;

	private IndexReader indexReader;

	private IndexSearcher indexSearcher;

	private AnalyzerFactory analyzerFactory;

	private SpellChecker spellChecker;

	@Inject
	public LuceneFactoryImpl(final AnalyzerFactory analyzerFactory) throws IOException {
		this.analyzerFactory = analyzerFactory;
		this.open();
	}

	public IndexSearcher getSearcher() {
		return indexSearcher;
	}

	public IndexWriter getWriter() {
		return indexWriter;
	}

	public void open() throws IOException {
		Directory directory = FSDirectory.open(getIndexDirectory());
		indexWriter = new IndexWriter(directory, analyzerFactory
				.getAnalyzer(), MaxFieldLength.UNLIMITED);
		indexReader = indexWriter.getReader();
		indexSearcher = new IndexSearcher(indexReader);

		Directory suggestDirectory = FSDirectory
				.open(getSuggestDirectory());
		this.spellChecker = new SpellChecker(suggestDirectory);
	}

	private File getSuggestDirectory() throws IOException {
		File file = new File(ServerConstants.SUGGEST_INDEX_DIR);
		if (!file.exists() && !file.mkdirs()) {
			throw new IOException("Cannot make suggest directory");
		}
		return file;
	}

	private File getIndexDirectory() throws IOException {
		File file = new File(ServerConstants.INDEX_DIR);
		if (!file.exists() && !file.mkdirs()) {
			throw new IOException("Cannot make index directory");
		}
		return file;
	}

	public void updateSpellIndex() throws IOException {
		indexReader = indexReader.reopen();
		Dictionary dictionary = new LuceneDictionary(indexReader,
				ServerConstants.CONTENTS_FIELD);
		spellChecker.indexDictionary(dictionary);
	}

	public SpellChecker getSpellChecker() {
		return spellChecker;
	}

	public void close() throws IOException {
		indexSearcher.close();
		indexReader.close();
		indexWriter.close();
	}

}
