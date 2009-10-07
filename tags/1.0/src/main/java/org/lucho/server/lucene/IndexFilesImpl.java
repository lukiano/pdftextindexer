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
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.StopAnalyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.pdfbox.searchengine.lucene.LucenePDFDocument;

import com.google.inject.Inject;

/** Index all text files under a directory. */
public class IndexFilesImpl implements IndexFiles {
	
	private static Logger log = Logger.getLogger(IndexFiles.class);
	
	@Inject
	private FileFilter fileFilter;

	public void index(final File docsDir, final File indexDir) throws IOException {
		Analyzer analyzer = new SnowballAnalyzer("English",
				StopAnalyzer.ENGLISH_STOP_WORDS);
		IndexWriter writer = new IndexWriter(indexDir, analyzer,
				true, IndexWriter.MaxFieldLength.LIMITED);
		try {
			indexDocs(writer, docsDir);
			writer.optimize();
		} finally {
			writer.close();
		}
	}

	private void indexDocs(final IndexWriter writer, final File file) throws IOException {
		// do not try to index files that cannot be read
		if (file.canRead()) {
			if (file.isDirectory()) {
				indexFolder(writer, file);
			} else {
				indexFile(writer, file);
			}
		}
	}

	private void indexFile(final IndexWriter writer, final File file)
			throws FileNotFoundException, CorruptIndexException, IOException {
		Document document;
		try {
			document = LucenePDFDocument.getDocument(file);
		}
		// at least on windows, some temporary files raise this
		// exception with an "access denied" message
		// checking if the file can be read doesn't help
		catch (FileNotFoundException fnfe) {
			log.info("File not found " + file, fnfe);
			return;
		} catch (IOException ioe) {
			log.error("Couldn't add " + file, ioe);
			document = FileDocument.Document(file, false);
		}
		writer.addDocument(document);
	}

	private void indexFolder(final IndexWriter writer, final File file)
			throws IOException, CorruptIndexException {
		File[] files = file.listFiles(fileFilter);
		// an IO error could occur
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				indexDocs(writer, files[i]);
				writer.commit();
			}
		}
	}

}
