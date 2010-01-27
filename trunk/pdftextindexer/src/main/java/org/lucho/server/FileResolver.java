package org.lucho.server;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;

public interface FileResolver {
	
	FileObject getBaseFolder() throws FileSystemException;
	
	FileObject getFile(String url) throws FileSystemException;

	FileObject getFile(final FileObject baseFile, final String name) throws FileSystemException;
}
