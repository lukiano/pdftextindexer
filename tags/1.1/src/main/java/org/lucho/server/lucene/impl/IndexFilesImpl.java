package org.lucho.server.lucene.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.tika.parser.ParsingReader;
import org.lucho.client.Constants;
import org.lucho.server.lucene.IndexFiles;
import org.lucho.server.lucene.LuceneFactory;

import com.google.inject.Inject;

/** Index all text files under a directory. */
public class IndexFilesImpl implements IndexFiles {
	
	private static final Logger log = Logger.getLogger(IndexFiles.class);
	
	@Inject
	private FileFilter fileFilter;

	@Inject
	private LuceneFactory luceneFactory;

	public void clearIndex() throws IOException {
		IndexWriter writer = luceneFactory.getWriter();
		try {
			writer.deleteAll();
			writer.commit();
		} catch (AlreadyClosedException ace) {
			luceneFactory.open();
		}
	}

	public void index(final File docsDir) throws IOException {
		IndexWriter writer = luceneFactory.getWriter();
		try {
			indexDocs(writer, docsDir);
			writer.optimize();
			writer.commit();
		} catch (AlreadyClosedException ace) {
			luceneFactory.open();
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
			throws IOException {
		ParsingReader parsingReader = new ParsingReader(file);
		Document document = new Document();
		document.add(new Field(Constants.PATH_FIELD, file.getPath(), Store.YES, Index.NO));
		document.add(new Field(Constants.CONTENTS_FIELD, parsingReader, TermVector.WITH_POSITIONS_OFFSETS));
		try {
			writer.addDocument(document);
		} catch (IOException e) {
			log.warn("Unable to index file " + file.getPath(), e);
		} finally {
			parsingReader.close();
		}
		writer.commit();
	}

	private void indexFolder(final IndexWriter writer, final File file)
			throws IOException {
		File[] files = file.listFiles(fileFilter);
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				indexDocs(writer, files[i]);
				writer.commit();
			}
		}
	}

}