package org.lucho.server.lucene.impl;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.InvalidTokenOffsetsException;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.util.Version;
import org.lucho.server.ServerConstants;
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
			QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, ServerConstants.CONTENTS_FIELD, analyzer);
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
			File file = new File(document.get(ServerConstants.PATH_FIELD));
			if (file.exists()) { // maybe the index is outdated
				files.add(file);
			}
		}
		return files;
	}

	public String highlight(String queryString, File file) throws IOException {
		Analyzer analyzer = analyzerFactory.getAnalyzer();
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, ServerConstants.CONTENTS_FIELD, analyzer);
		Query query;
		try {
			query = parser.parse(queryString);
		} catch (ParseException e1) {
			return "";
		}
//		FastVectorHighlighter highlighter = new FastVectorHighlighter();
//		FieldQuery fieldQuery = highlighter.getFieldQuery(query);
//		return highlighter.getBestFragment(fieldQuery, luceneFactory.getSearcher().getIndexReader(), docId, ServerConstants.CONTENTS_FIELD, 128);
		
		FileReader fileReader = new FileReader(file);
		try {
			Highlighter highlighter = new Highlighter(new QueryScorer(query, luceneFactory.getSearcher().getIndexReader(), ServerConstants.CONTENTS_FIELD));
			TokenStream tokenStream = analyzer.tokenStream(ServerConstants.CONTENTS_FIELD, fileReader);
			return highlighter.getBestFragment(tokenStream, "");
		} catch (InvalidTokenOffsetsException e) {
			return "";
		} finally {
			fileReader.close();
		}
	}

	public String suggest(String queryString) throws IOException {
		String[] suggestions = luceneFactory.getSpellChecker().suggestSimilar(queryString, 1);
		if (suggestions.length == 0) {
			return null;
		}
		return suggestions[0];
	}

}
