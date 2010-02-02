package org.lucho.client;

import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.HBoxLayout;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;

public class Main implements EntryPoint {

	public void onModuleLoad() {
		// get service
		SearchRemoteServiceAsync searchService = (SearchRemoteServiceAsync) GWT
				.create(SearchRemoteService.class);
		((ServiceDefTarget) searchService).setServiceEntryPoint(GWT
				.getModuleBaseURL()
				+ "searchRemoteService");

		// main panel
		Window w = new Window();
		setParameters(w);
		int width = 800;
		int height = 600;
		w.add(new IndexerTreePanel(searchService, width/2, height));
		w.add(new IndexerSearchPanel(searchService, width/2, height));
		w.setSize(width, height);
		w.show();
	}

	private void setParameters(final Window window) {
		window.setHeading(Constants.TITLE);
		window.setModal(false);
		window.setClosable(false);
		window.setLayout(new HBoxLayout());
		window.setMaximizable(false);
		window.setMinimizable(false);
		window.setResizable(false);
	}

}
