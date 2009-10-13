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
				+ "/searchRemoteService");

		// main panel
		Window w = new Window();
		w.setHeading(Constants.TITLE);
		w.setModal(true);
		w.setSize(800, 600);
		w.setLayout(new HBoxLayout());
//		w.setMaximizable(true);
		w.setToolTip("The ExtGWT product page...");
		w.add(new IndexerSearchPanel(searchService));
		w.add(new IndexerTreePanel(searchService));
		w.show();
	}

}
