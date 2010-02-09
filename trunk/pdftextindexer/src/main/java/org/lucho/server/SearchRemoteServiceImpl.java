package org.lucho.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;

import org.apache.commons.vfs.FileObject;
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

	public Node[] searchByText(final String text) throws IOException {
		List<FileObject> results = searchFiles.search(text);
		Node[] nodes = new Node[results.size()];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = fileResolver.fileToNode(results.get(i));
		}
		return nodes;
	}
	
	public List<Node> listChildren(Node parent) throws IOException {
		FileObject parentFile = fileResolver.getFile(parent.getPath());
		return fileResolver.getChildren(parentFile);
	}

	public List<Node> listRootNodes() throws IOException {
		FileObject rootFile = fileResolver.getBaseFolder();
		return fileResolver.getChildren(rootFile);
	}

	public void reindex() throws IOException {
		indexFiles.clearIndex();
		indexFiles.index(fileResolver.getBaseFolder());
	}
	
	public ServletContext getServletContext() {
		return servletContext;
	}

	public String highlight(final Node node, final String queryString) throws IOException {
		return searchFiles.highlight(queryString, node.getPath());
	}

	public String suggest(final String queryString) throws IOException {
		return searchFiles.suggest(queryString);
	}

}
