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

public class SearchFilesImpl implements SearchFiles {

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

}
