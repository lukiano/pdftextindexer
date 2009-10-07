package org.lucho.client;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class RightPanel extends VerticalPanel {

	// services
	private SearchRemoteServiceAsync searchService;

	// panel items
	private TextBox inputBox;
	private ListBox listResults;
	
	// panel buttons
	private Button cleanButton;
	private Button reindexButton;
	private Button downloadButton;

	public RightPanel(final SearchRemoteServiceAsync searchService) {
		this.searchService = searchService;
		
		// panel description label
		Panel upperRightPanel = rightPanelItems();
		Panel buttonPanel = rightPanelButtons();

		//main panel
		this.add(upperRightPanel);
		this.add(buttonPanel);
		this.setSize("400px", "300px");
		this.setTitle("Search by text");
	}

	private Panel rightPanelButtons() {
		// clear results button
		cleanButton = new Button("Clear results");
//		cleanButton.setWidth("50%");
		cleanButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				listResults.clear();
				inputBox.setText("");
			}
		});

		// download button
		downloadButton = new Button("Download file");
//		downloadButton.setWidth("50%");
		downloadButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String path = listResults.getValue(listResults
						.getSelectedIndex());
				Window
						.open(path, "_blank",
								"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
			}
		});

		// reindex button
		reindexButton = new Button("Rebuild index");
//		reindexButton.setWidth("50%");
		reindexButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						inputBox.setEnabled(true);
					}

					public void onFailure(Throwable caught) {
						Window.alert("Unable to rebuild index");
						inputBox.setEnabled(true);
					}
				};
				inputBox.setEnabled(false);
				searchService.reindex(callback);
			}
		});

		// first button dock panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buttonPanel.setWidth("100%");
		buttonPanel.add(downloadButton);
		buttonPanel.add(cleanButton);
		buttonPanel.add(reindexButton);
		return buttonPanel;
	}

	private Panel rightPanelItems() {
		Label label = new Label("Text search");
		label.setWidth("100%");
		label.setHorizontalAlignment(Label.ALIGN_CENTER);

		// input search text box
		inputBox = new TextBox();
		inputBox.setText("Type something here...");
		inputBox.setWidth("100%");
		inputBox.addKeyPressHandler(new KeyPressHandler() {
			
			public void onKeyPress(KeyPressEvent event) {
				if (event.getCharCode() == KeyCodes.KEY_ENTER) {
					searchByText();
				}
			}

		});

		// results list box
		listResults = new ListBox();
		listResults.setVisibleItemCount(10);
		listResults.setWidth("100%");
		
		VerticalPanel upperRightPanel = new VerticalPanel();
		upperRightPanel.setWidth("100%");
		upperRightPanel.add(label);
		upperRightPanel.add(inputBox);
		upperRightPanel.add(listResults);
		return upperRightPanel;
	}

	protected void searchByText() {
		AsyncCallback<Node[]> callback = new AsyncCallback<Node[]>() {

			// This method will be called if the service call fails
			public void onFailure(Throwable caught) {
				// Show a message informing the user why the call failed
				Window.alert("Unable to search. Failure message is "
						+ caught.getMessage());
			}

			// This method will be called if the service call succeeds
			public void onSuccess(Node[] results) {

				// Get the service call result and cast it to the
				// desired type and display it
				listResults.clear();
				for (Node node : results) {
					listResults.addItem(node.getText(), node.getPath());
				}
				if (results.length == 0) {
					Window.alert("No matches found!");
				}
			}
		};
		searchService.searchByText(inputBox.getText(), callback);
	}

}
