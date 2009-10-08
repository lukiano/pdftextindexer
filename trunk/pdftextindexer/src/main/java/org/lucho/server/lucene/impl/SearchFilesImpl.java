package org.lucho.server.lucene.impl;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TopDocs;
import org.lucho.client.Constants;
import org.lucho.server.lucene.AnalyzerFactory;
import org.lucho.server.lucene.LuceneFactory;
import org.lucho.server.lucene.SearchFiles;

import com.google.inject.Inject;

public class SearchFilesImpl implements SearchFiles {
	
	@Inject
	private AnalyzerFactory analyzerFactory;
	
	@Inject
	private LuceneFactory luceneFactory;

	public File[] search(final String text) throws 
			IOException {

		IndexSearcher searcher = luceneFactory.getSearcher();
		try {
			Analyzer analyzer = analyzerFactory.getAnalyzer();
			QueryParser parser = new QueryParser(Constants.CONTENTS_FIELD, analyzer);
			Query query = parser.parse(text);
			return doSearch(searcher, query);
		} catch (ParseException e) {
			// return no matches if parse error
			return new File[0];
		} finally {
			searcher.close();
		}
	}

	private File[] doSearch(final Searcher searcher, Query query)
			throws IOException {
		TopDocs topDocs = searcher.search(query, 10);
		File[] files = new File[topDocs.scoreDocs.length];
		for (int i = 0; i < files.length; i++) {
			Document document = searcher.doc(topDocs.scoreDocs[i].doc);
			files[i] = new File(document.get(Constants.PATH_FIELD));
		}
		return files;
	}

}
