package org.lucho.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.RootPanel;

public class Main implements EntryPoint {

	public void onModuleLoad() {
		// get service
		SearchRemoteServiceAsync searchService = (SearchRemoteServiceAsync) GWT
				.create(SearchRemoteService.class);
		((ServiceDefTarget) searchService).setServiceEntryPoint(GWT
				.getModuleBaseURL() + "/searchRemoteService");

		// add nice borders
		DecoratorPanel leftDecoratorPanel = new DecoratorPanel();
		leftDecoratorPanel.setWidget(new LeftPanel(searchService));
		DecoratorPanel rightDecoratorPanel = new DecoratorPanel();
		rightDecoratorPanel.setWidget(new RightPanel(searchService));

		// main panel
		DockPanel mainPanel = new DockPanel();
		mainPanel.setTitle("Text Indexer");
		mainPanel.add(leftDecoratorPanel, DockPanel.WEST);
		mainPanel.add(rightDecoratorPanel, DockPanel.EAST);
		mainPanel.setSize("600px", "400px");
		RootPanel.get().add(mainPanel);
	}


}
