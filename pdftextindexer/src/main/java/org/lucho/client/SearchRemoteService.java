package org.lucho.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SearchRemoteService extends RemoteService {
	
	public Node[] searchByText(String text);
	
	public List<Node> listRootNodes();
	
	public List<Node> listChildren(Node parent);
	
	public void reindex();
	
	public String highlight(Node node, String queryString);
	
	public String suggest(String queryString);
	
}
