package org.lucho.server.lucene.impl;

import java.io.IOException;

import org.apache.commons.vfs.FileObject;
import org.lucho.server.lucene.IndexFiles;

import com.google.inject.Inject;

public class IndexFilesDecorator implements IndexFiles {
	
	@Inject
	private IndexFiles delegate;

	public void clearIndex() throws IOException {
		this.delegate.clearIndex();
	}

	public void index(final FileObject docsDir) throws IOException {
		this.delegate.index(docsDir);
	}

}
