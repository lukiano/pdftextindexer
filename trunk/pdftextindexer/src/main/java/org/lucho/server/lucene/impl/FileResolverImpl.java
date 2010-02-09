package org.lucho.server.lucene.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileSystemOptions;
import org.apache.commons.vfs.FileType;
import org.apache.commons.vfs.UserAuthenticator;
import org.apache.commons.vfs.VFS;
import org.apache.commons.vfs.auth.StaticUserAuthenticator;
import org.apache.commons.vfs.impl.DefaultFileSystemConfigBuilder;
import org.lucho.client.Node;
import org.lucho.server.FileResolver;

import com.google.inject.Inject;

public class FileResolverImpl implements FileResolver {
	
	@Inject
	private Properties properties;
	
	public FileObject getBaseFolder() throws FileSystemException {
		return VFS.getManager().resolveFile(properties.getProperty("url"), getOperations());
	}

	private FileSystemOptions getOperations() throws FileSystemException {
		FileSystemOptions fileSystemOptions = new FileSystemOptions();
		UserAuthenticator userAuthenticator = new StaticUserAuthenticator("", properties.getProperty("username"), properties.getProperty("password"));
		DefaultFileSystemConfigBuilder.getInstance().setUserAuthenticator(fileSystemOptions, userAuthenticator);
		return fileSystemOptions;
	}

	public FileObject getFile(final String url) throws FileSystemException {
		return VFS.getManager().resolveFile(url, getOperations());
	}

	public FileObject getFile(final FileObject baseFile, final String name) throws FileSystemException {
		return VFS.getManager().resolveFile(baseFile, name);
	}

	public List<Node> getChildren(final FileObject file) throws FileSystemException {
		FileObject[] children = file.getChildren();
		List<Node> returnNodes = new ArrayList<Node>();
		for (FileObject child : children) {
			returnNodes.add(fileToNode(child));
		}
		return returnNodes;
	}

	public boolean isFolder(FileObject child) throws FileSystemException {
		return child.getType().equals(FileType.FOLDER);
	}

	public Node fileToNode(final FileObject child) throws FileSystemException {
		Node newNode = Node.create(child.getName().getBaseName());
		newNode.setPath(child.getURL().toString());
		newNode.hasChildren(isFolder(child));
		return newNode;
	}

	public FileObject nodeToFile(final Node node) throws FileSystemException {
		return VFS.getManager().resolveFile(node.getPath(), getOperations());
	}

}
