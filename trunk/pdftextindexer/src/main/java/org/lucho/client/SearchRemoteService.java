package org.lucho.client;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SearchRemoteService extends RemoteService {
	
	public Node[] searchByText(String text);
	
	public Node listFiles();
	
	public void reindex();
	
}
