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
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexWriter;
import org.apache.pdfbox.searchengine.lucene.LucenePDFDocument;
import org.lucho.server.PDFFilter;

/** Index all text files under a directory. */
public class IndexFiles {

	public void index(final File docsDir, final File indexDir) throws IOException {
		Analyzer analyzer = new SnowballAnalyzer("English",
				StopAnalyzer.ENGLISH_STOP_WORDS);
		IndexWriter writer = new IndexWriter(indexDir, analyzer,
				true, IndexWriter.MaxFieldLength.LIMITED);
		indexDocs(writer, docsDir);
		writer.optimize();
		writer.close();
	}

	private void indexDocs(final IndexWriter writer, final File file) throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				File[] files = file.listFiles(new PDFFilter());
				// an IO error could occur
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						indexDocs(writer, files[i]);
						writer.commit();
					}
				}
			} else {
				Document document;
				try {
					document = LucenePDFDocument.getDocument(file);
				}
				// at least on windows, some temporary files raise this
				// exception with an "access denied" message
				// checking if the file can be read doesn't help
				catch (FileNotFoundException fnfe) {
					return;
				} catch (IOException ioe) {
					System.err.println("Couldn't add " + file
							+ " because of a " + ioe.getClass()
							+ " with message " + ioe.getMessage());
					document = FileDocument.Document(file, false);
					return;
				}
				writer.addDocument(document);
			}
		}
	}

}
