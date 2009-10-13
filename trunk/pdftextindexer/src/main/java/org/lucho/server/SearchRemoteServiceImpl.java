package org.lucho.server;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.lucho.client.Constants;
import org.lucho.client.Node;
import org.lucho.client.SearchRemoteService;
import org.lucho.server.lucene.IndexFiles;
import org.lucho.server.lucene.SearchFiles;

import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.google.inject.Inject;

@RemoteServiceRelativePath("searchRemoteService")
public class SearchRemoteServiceImpl extends HttpServlet implements SearchRemoteService {
	
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

	public Node[] searchByText(String text) throws IOException {
		File[] results = searchFiles.search(text);
		Node[] nodes = new Node[results.length];
		for (int i = 0; i < nodes.length; i++) {
			File file = results[i];
			nodes[i] = fileToNode(file);
		}
		return nodes;
	}

	public Node listFiles() {
		File rootFile = this.getFile(Constants.FILES_DIR);
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

	public void reindex() throws IOException {
		indexFiles.index(this.getFile(Constants.FILES_DIR));
	}

	public ServletContext getServletContext() {
		return servletContext;
	}


}
