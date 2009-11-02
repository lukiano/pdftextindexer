package org.lucho.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface SearchRemoteServiceAsync {

	void searchByText(String text, AsyncCallback<Node[]> callback);

	void listFiles(AsyncCallback<Node> callback);

	void reindex(AsyncCallback<Void> callback);

	void highlight(Node node, String queryString, AsyncCallback<String> callback);

	void suggest(String queryString, AsyncCallback<String> callback);

}
