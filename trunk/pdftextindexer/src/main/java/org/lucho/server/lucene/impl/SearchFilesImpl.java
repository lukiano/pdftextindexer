package org.lucho.server.lucene.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.vectorhighlight.FastVectorHighlighter;
import org.apache.lucene.search.vectorhighlight.FieldQuery;
import org.apache.lucene.util.Version;
import org.lucho.server.FileResolver;
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

	@Inject
	private FileResolver fileResolver;

	public List<FileObject> search(final String text) throws 
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

	private List<FileObject> doSearch(final IndexSearcher searcher, Query query)
			throws IOException {
		TopDocs topDocs = searcher.search(query, 10);
		List<FileObject> files = new ArrayList<FileObject>(topDocs.totalHits);
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			Document document = searcher.doc(scoreDoc.doc);
			FileObject file = fileResolver.getFile(document.get(ServerConstants.PATH_FIELD));
			if (file.exists()) { // maybe the index is outdated
				files.add(file);
			}
		}
		return files;
	}

	public String highlight(final String queryString, final String path) throws IOException {
		Analyzer analyzer = analyzerFactory.getAnalyzer();
		QueryParser parser = new QueryParser(Version.LUCENE_CURRENT, ServerConstants.CONTENTS_FIELD, analyzer);
		Query query;
		try {
			query = parser.parse(queryString);
		} catch (ParseException e1) {
			return "";
		}
		TopDocs topDocs = luceneFactory.getSearcher().search(new TermQuery(new Term(ServerConstants.PATH_FIELD, path)), 1);
		if (topDocs.scoreDocs.length == 0) {
			return "";
		}
		FastVectorHighlighter highlighter = new FastVectorHighlighter();
		FieldQuery fieldQuery = highlighter.getFieldQuery(query);
		return highlighter.getBestFragment(fieldQuery, luceneFactory.getSearcher().getIndexReader(), topDocs.scoreDocs[0].doc, ServerConstants.CONTENTS_FIELD, 128);
	}

	public String suggest(final String queryString) throws IOException {
		String[] suggestions = luceneFactory.getSpellChecker().suggestSimilar(queryString, 1);
		if (suggestions.length == 0) {
			return null;
		}
		return suggestions[0];
	}

}
