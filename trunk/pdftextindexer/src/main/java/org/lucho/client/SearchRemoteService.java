package org.lucho.client;

import java.io.IOException;

import com.google.gwt.user.client.rpc.RemoteService;

public interface SearchRemoteService extends RemoteService {
	
	public Node[] searchByText(String text) throws IOException;
	
	public Node listFiles();
	
	public void reindex() throws IOException;
	
}
