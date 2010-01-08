package org.lucho.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.lucho.client.Node;
import org.lucho.client.SearchRemoteService;
import org.lucho.server.lucene.IndexFiles;
import org.lucho.server.lucene.SearchFiles;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.inject.Inject;

@RemoteServiceRelativePath("searchRemoteService")
public class SearchRemoteServiceImpl extends HttpServlet implements
		SearchRemoteService {

	@Inject
	private ServletContext servletContext;

	@Inject
	private SearchFiles searchFiles;

	@Inject
	private FileFilter searchFilter;

	@Inject
	private IndexFiles indexFiles;

	/**
	 * Auto generated serial id
	 */
	private static final long serialVersionUID = 1420839684628425128L;

	public Node[] searchByText(String text) {
		List<File> results;
		try {
			results = searchFiles.search(text);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		Node[] nodes = new Node[results.size()];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = fileToNode(results.get(i));
		}
		return nodes;
	}

	public Node listFiles() {
		File rootFile = new File(ServerConstants.FILES_DIR);
		Node rootNode = Node.create("root");
		rootNode.hasChildren(true);
		addFile(rootNode, rootFile);
		return rootNode;
	}

	private File getFile(String path) {
		return new File(this.getServletContext().getRealPath(path));
	}

	private void addFile(Node node, File file) {
		File[] children = file.listFiles(searchFilter);
		if (children != null) {
			for (File child : children) {
				Node newNode = fileToNode(child);
				node.add(newNode);
				if (child.isDirectory()) {
					addFile(newNode, child);
				}
			}
		}
	}

	private Node fileToNode(File child) {
		Node newNode = Node.create(child.getName());
		int prefixLength = this.getServletContext().getRealPath(".").length();
		String relativePath = child.getPath().substring(prefixLength - 1);
		relativePath = relativePath.replaceAll("\\\\", "/");
		newNode.setPath(relativePath);
		newNode.hasChildren(child.isDirectory());
		return newNode;
	}

	public void reindex() {
		try {
			indexFiles.clearIndex();
			indexFiles.index(new File(ServerConstants.FILES_DIR));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public ServletContext getServletContext() {
		return servletContext;
	}

	public String highlight(Node node, String queryString) {
		try {
			return searchFiles.highlight(queryString, this.getFile(node
					.getPath()
					+ ExtensionFilter.EXTENSION));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String suggest(String queryString) {
		try {
			return searchFiles.suggest(queryString);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

}
