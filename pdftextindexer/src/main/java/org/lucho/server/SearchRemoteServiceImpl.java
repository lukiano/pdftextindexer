package org.lucho.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.commons.vfs.FileObject;
import org.apache.commons.vfs.FileSystemException;
import org.apache.commons.vfs.FileType;
import org.lucho.client.Node;
import org.lucho.client.SearchRemoteService;
import org.lucho.server.lucene.IndexFiles;
import org.lucho.server.lucene.SearchFiles;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.inject.Inject;
import com.google.inject.name.Named;

@RemoteServiceRelativePath("searchRemoteService")
public class SearchRemoteServiceImpl extends HttpServlet implements
		SearchRemoteService {

	@Inject
	private ServletContext servletContext;

	@Inject
	private SearchFiles searchFiles;

	@Inject
	@Named("async")
	private IndexFiles indexFiles;
	
	@Inject
	private FileResolver fileResolver;
	
	/**
	 * Auto generated serial id
	 */
	private static final long serialVersionUID = 1420839684628425128L;

	public Node[] searchByText(final String text) {
		try {
			List<FileObject> results = searchFiles.search(text);
			Node[] nodes = new Node[results.size()];
			for (int i = 0; i < nodes.length; i++) {
				nodes[i] = fileToNode(results.get(i));
			}
			return nodes;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Node> listChildren(Node parent) {
		try {
			FileObject parentFile = fileResolver.getFile(parent.getPath());
			return getChildren(parentFile);
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	public List<Node> listRootNodes() {
		try {
			FileObject rootFile = fileResolver.getBaseFolder();
			return getChildren(rootFile);
		} catch (FileSystemException e) {
			throw new RuntimeException(e);
		}
	}

	private List<Node> getChildren(final FileObject file) throws FileSystemException {
		FileObject[] children = file.getChildren();
		List<Node> returnNodes = new ArrayList<Node>();
		for (FileObject child : children) {
			returnNodes.add(fileToNode(child));
		}
		return returnNodes;
	}

	private boolean isFolder(FileObject child) throws FileSystemException {
		return child.getType().equals(FileType.FOLDER);
	}

	private Node fileToNode(final FileObject child) throws FileSystemException {
		Node newNode = Node.create(child.getName().getBaseName());
		newNode.setPath(child.getURL().toString());
		newNode.hasChildren(isFolder(child));
		return newNode;
	}

	public void reindex() {
		try {
			indexFiles.clearIndex();
			indexFiles.index(fileResolver.getBaseFolder());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	public String highlight(final Node node, final String queryString) {
		try {
			return searchFiles.highlight(queryString, node.getPath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String suggest(final String queryString) {
		try {
			return searchFiles.suggest(queryString);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
