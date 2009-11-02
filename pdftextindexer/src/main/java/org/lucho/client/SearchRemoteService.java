package org.lucho.client;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SearchRemoteService extends RemoteService {
	
	public Node[] searchByText(String text);
	
	public Node listFiles();
	
	public void reindex();
	
	public String highlight(Node node, String queryString);
	
	public String suggest(String queryString);
	
}
