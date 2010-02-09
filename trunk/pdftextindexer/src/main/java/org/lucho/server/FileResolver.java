package org.lucho.server;

import java.util.List;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.lucho.client.Node;

public interface FileResolver {
	
	FileObject getBaseFolder() throws FileSystemException;
	
	FileObject getFile(String url) throws FileSystemException;

	FileObject getFile(final FileObject baseFile, final String name) throws FileSystemException;
	
	List<Node> getChildren(final FileObject file) throws FileSystemException;
	
	boolean isFolder(FileObject child) throws FileSystemException;
	
	Node fileToNode(final FileObject child) throws FileSystemException;
	
	FileObject nodeToFile(final Node node) throws FileSystemException;
}
