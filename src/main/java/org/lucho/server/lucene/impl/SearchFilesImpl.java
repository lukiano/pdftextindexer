package org.lucho.server.lucene.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
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

	public List<File> search(final String text) throws 
			IOException {

		IndexSearcher searcher = luceneFactory.getSearcher();
		try {
			Analyzer analyzer = analyzerFactory.getAnalyzer();
			QueryParser parser = new QueryParser(Constants.CONTENTS_FIELD, analyzer);
			Query query = parser.parse(text);
			return doSearch(searcher, query);
		} catch (ParseException e) {
			// return no matches if parse error
			return Collections.emptyList();
		} finally {
			searcher.close();
		}
	}

	private List<File> doSearch(final IndexSearcher searcher, Query query)
			throws IOException {
		TopDocs topDocs = searcher.search(query, 10);
		List<File> files = new ArrayList<File>(topDocs.totalHits);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document = searcher.doc(scoreDoc.doc);
			File file = new File(document.get(Constants.PATH_FIELD));
			if (file.exists()) { // maybe the index is outdated
				files.add(file);
			}
		}
		return files;
	}

}
