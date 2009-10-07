package org.lucho.server;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;
import org.lucho.client.Constants;
import org.lucho.client.Node;
import org.lucho.client.SearchRemoteService;
import org.lucho.server.lucene.IndexFiles;
import org.lucho.server.lucene.SearchFiles;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class SearchRemoteServiceImpl extends RemoteServiceServlet implements SearchRemoteService {
	
	private SearchFiles searchFiles = new SearchFiles();
	
	private IndexFiles indexFiles = new IndexFiles();

	/**
	 * Auto generated serial id
	 */
	private static final long serialVersionUID = 1420839684628425128L;

	public Node[] searchByText(String text) {
		File[] results;
		try {
			results = searchFiles.search(text, this.getFile(Constants.INDEX_DIR));
		} catch (CorruptIndexException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
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
		File[] children = file.listFiles(new PDFFilter());
		if (children != null) {
			int length = children.length;
			node.setChildren(new Node[length]);
			for (int i = 0; i < length; i++) {
				File child = children[i];
				Node newNode = fileToNode(child);
				node.getChildren()[i] = newNode;
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
			indexFiles.index(this.getFile(Constants.FILES_DIR), this.getFile(Constants.INDEX_DIR));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}


}
