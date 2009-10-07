package org.lucho.server.lucene;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.BitSet;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.lucho.client.Constants;

public class SearchFiles {

	public File[] search(final String text, final File indexDir) throws CorruptIndexException,
			IOException, ParseException {

		IndexReader reader = IndexReader.open(indexDir);
		try {
			Searcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new SnowballAnalyzer("English",
					StopAnalyzer.ENGLISH_STOP_WORDS);

			QueryParser parser = new QueryParser(Constants.CONTENTS_FIELD, analyzer);
			Query query = parser.parse(text);
			return doStreamingSearch(searcher, query);
		} finally {
			reader.close();
		}
	}

	/**
	 * This method uses a custom HitCollector implementation which simply prints
	 * out the docId and score of every matching document.
	 * 
	 * This simulates the streaming search use case, where all hits are supposed
	 * to be processed, regardless of their relevance.
	 */
	private File[] doStreamingSearch(final Searcher searcher, Query query)
			throws IOException {
		final BitSet bits = new BitSet(searcher.maxDoc());
		HitCollector streamingHitCollector = new HitCollector() {
			public void collect(int doc, float score) {
				bits.set(doc);
			}

		};
		searcher.search(query, streamingHitCollector);
		
		File[] files = new File[bits.cardinality()];
		int j = 0;
		for (int i = 0; i < bits.length(); i++) {
			if (bits.get(i)) {
				Document document = searcher.doc(i);
				files[j] = new File(document.get(Constants.PATH_FIELD));
				j++;
			}
		}
		return files;
	}

	// /**
	// * This demonstrates a typical paging search scenario, where the search
	// engine presents
	// * pages of size n to the user. The user can then go to the next page if
	// interested in
	// * the next hits.
	// *
	// * When the query is executed for the first time, then only enough results
	// are collected
	// * to fill 5 result pages. If the user wants to page beyond this limit,
	// then the query
	// * is executed another time and all hits are collected.
	// *
	// */
	// public static void doPagingSearch(BufferedReader in, Searcher searcher,
	// Query query,
	// int hitsPerPage, boolean raw, boolean interactive) throws IOException {
	// 
	// // Collect enough docs to show 5 pages
	// TopDocCollector collector = new TopDocCollector(5 * hitsPerPage);
	// searcher.search(query, collector);
	// ScoreDoc[] hits = collector.topDocs().scoreDocs;
	//    
	// int numTotalHits = collector.getTotalHits();
	// System.out.println(numTotalHits + " total matching documents");
	//
	// int start = 0;
	// int end = Math.min(numTotalHits, hitsPerPage);
	//        
	// while (true) {
	// if (end > hits.length) {
	// System.out.println("Only results 1 - " + hits.length +" of " +
	// numTotalHits + " total matching documents collected.");
	// System.out.println("Collect more (y/n) ?");
	// String line = in.readLine();
	// if (line.length() == 0 || line.charAt(0) == 'n') {
	// break;
	// }
	//
	// collector = new TopDocCollector(numTotalHits);
	// searcher.search(query, collector);
	// hits = collector.topDocs().scoreDocs;
	// }
	//      
	// end = Math.min(hits.length, start + hitsPerPage);
	//      
	// for (int i = start; i < end; i++) {
	// if (raw) { // output raw format
	// System.out.println("doc="+hits[i].doc+" score="+hits[i].score);
	// continue;
	// }
	//
	// Document doc = searcher.doc(hits[i].doc);
	// String path = doc.get("path");
	// if (path != null) {
	// System.out.println((i+1) + ". " + path);
	// String title = doc.get("title");
	// if (title != null) {
	// System.out.println("   Title: " + doc.get("title"));
	// }
	// } else {
	// System.out.println((i+1) + ". " + "No path for this document");
	// }
	//                  
	// }
	//
	// if (!interactive) {
	// break;
	// }
	//
	// if (numTotalHits >= end) {
	// boolean quit = false;
	// while (true) {
	// System.out.print("Press ");
	// if (start - hitsPerPage >= 0) {
	// System.out.print("(p)revious page, ");
	// }
	// if (start + hitsPerPage < numTotalHits) {
	// System.out.print("(n)ext page, ");
	// }
	// System.out.println("(q)uit or enter number to jump to a page.");
	//          
	// String line = in.readLine();
	// if (line.length() == 0 || line.charAt(0)=='q') {
	// quit = true;
	// break;
	// }
	// if (line.charAt(0) == 'p') {
	// start = Math.max(0, start - hitsPerPage);
	// break;
	// } else if (line.charAt(0) == 'n') {
	// if (start + hitsPerPage < numTotalHits) {
	// start+=hitsPerPage;
	// }
	// break;
	// } else {
	// int page = Integer.parseInt(line);
	// if ((page - 1) * hitsPerPage < numTotalHits) {
	// start = (page - 1) * hitsPerPage;
	// break;
	// } else {
	// System.out.println("No such page");
	// }
	// }
	// }
	// if (quit) break;
	// end = Math.min(numTotalHits, start + hitsPerPage);
	// }
	//      
	// }
	//
	// }
}
