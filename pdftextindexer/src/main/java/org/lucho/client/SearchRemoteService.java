package org.lucho.client;

import java.io.IOException;
import java.util.List;

import org.apache.commons.vfs.FileSystemException;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SearchRemoteService extends RemoteService {
	
	public Node[] searchByText(String text) throws IOException;
	
	public List<Node> listRootNodes() throws IOException;
	
	public List<Node> listChildren(Node parent) throws IOException;
	
	public void reindex() throws IOException;
	
	public String highlight(Node node, String queryString) throws IOException;
	
	public String suggest(String queryString) throws IOException;
	
}
