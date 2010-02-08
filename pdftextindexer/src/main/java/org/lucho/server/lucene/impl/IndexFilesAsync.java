package org.lucho.server.lucene.impl;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.commons.vfs.FileObject;

import com.google.inject.Inject;

public class IndexFilesAsync extends IndexFilesDecorator {

	@Inject
	private ExecutorService executorService;

	public void clearIndex() throws IOException {
		executeAndWait(new Callable<Void>() {
			public Void call() throws IOException {
				IndexFilesAsync.super.clearIndex();
				return null;
			}
		});
	}
	
	public void index(final FileObject docsDir) throws IOException {
		executeAndWait(new Callable<Void>() {
			public Void call() throws IOException {
				IndexFilesAsync.super.index(docsDir);
				return null;
			}
		});
	}
	
	private void executeAndWait(final Callable<Void> callable) throws IOException {
		Future<Void> future = executorService.submit(callable);
		try {
			future.get();
		} catch (InterruptedException ignored) {
		} catch (ExecutionException e) {
			throw (IOException) e.getCause();
		}
	}

}
