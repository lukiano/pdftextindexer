package org.lucho.server.lucene.impl;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileType;
import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.document.Field.TermVector;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.AlreadyClosedException;
import org.apache.tika.parser.ParsingReader;
import org.lucho.server.ServerConstants;
import org.lucho.server.lucene.IndexFiles;
import org.lucho.server.lucene.LuceneFactory;

import com.google.inject.Inject;

/** Index all text files under a directory. */
public class IndexFilesImpl implements IndexFiles {

	private static final Logger log = Logger.getLogger(IndexFiles.class);

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

	public void index(final FileObject docsDir) throws IOException {
		IndexWriter writer = luceneFactory.getWriter();
		try {
			indexDocs(writer, docsDir);
			writer.optimize();
			writer.commit();
		} catch (AlreadyClosedException ace) {
			luceneFactory.open();
		}
		luceneFactory.updateSpellIndex();
	}

	private void indexDocs(final IndexWriter writer, final FileObject file)
			throws IOException {
		// do not try to index files that cannot be read
		if (file.isReadable()) {
			if (file.getType().equals(FileType.FOLDER)) {
				indexFolder(writer, file);
			} else {
				indexFile(writer, file);
			}
		}
	}

	private void indexFile(final IndexWriter writer, final FileObject file)
			throws IOException {
		log.info("Indexing file " + file.getName());
		Document document = new Document();
		document.add(new Field(ServerConstants.PATH_FIELD, file.getURL().toString(), Store.YES,
				Index.NO));
		ParsingReader parsingReader = new ParsingReader(file.getContent().getInputStream());
		document.add(new Field(ServerConstants.CONTENTS_FIELD, parsingReader,
				TermVector.WITH_POSITIONS_OFFSETS));
		try {
			writer.addDocument(document);
		} catch (IOException e) {
			e.printStackTrace();
			log.warn("Unable to index file " + file.getName(), e);
		} finally {
			parsingReader.close();
		}
		writer.commit();
	}

	private void indexFolder(final IndexWriter writer, final FileObject folder)
			throws IOException {
		for (FileObject child : folder.getChildren()) {
			indexDocs(writer, child);
			writer.commit();
		}
	}

}
