package org.lucho.client;

import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.widgets.Button;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class RightPanel extends VLayout {

	// services
	private SearchRemoteServiceAsync searchService;

	// panel items
	private TextItem inputBox;
	private ListGrid listResults;
	
	// panel buttons
	private Button cleanButton;
	private Button reindexButton;
	private Button downloadButton;

	public RightPanel(final SearchRemoteServiceAsync searchService) {
		this.searchService = searchService;
		// panel description label
		Canvas upperRightPanel = rightPanelItems();
		Canvas buttonPanel = rightPanelButtons();

		//main panel
		this.addChild(upperRightPanel);
		this.addChild(buttonPanel);
		this.setSize("400px", "300px");
		this.setTitle("Search by text");
	}

	private Canvas rightPanelButtons() {
		// clear results button
		cleanButton = new Button("Clear results");
		cleanButton.addClickHandler(new ClickHandler() {
			
			public void onClick(ClickEvent event) {
				listResults.clear();
				inputBox.setValue("");
			}
		});

		// download button
		downloadButton = new Button("Download file");
		downloadButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				String path = listResults.getSelectedRecord().getSingleCellValue();
				Window
						.open(path, "_blank",
								"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
			}
		});

		// reindex button
		reindexButton = new Button("Rebuild index");
		reindexButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						inputBox.enable();
					}

					public void onFailure(Throwable caught) {
						Window.alert("Unable to rebuild index");
						inputBox.enable();
					}
				};
				inputBox.disable();
				searchService.reindex(callback);
			}
		});

		// first button dock panel
		HLayout buttonPanel = new HLayout();
//		buttonPanel.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		buttonPanel.setWidth("100%");
		buttonPanel.addChild(downloadButton);
		buttonPanel.addChild(cleanButton);
		buttonPanel.addChild(reindexButton);
		return buttonPanel;
	}

	private Canvas rightPanelItems() {
		Label label = new Label("Text search");
		label.setWidth("100%");
		label.setAlign(Alignment.CENTER);

		// input search text box
		
		inputBox = new TextItem();
		inputBox.setValue("Type something here...");
		inputBox.setWidth("100%");
		inputBox.addKeyPressHandler(new KeyPressHandler() {
			
			public void onKeyPress(KeyPressEvent event) {
				Integer code = event.getCharacterValue(); 
				if (code != null && code.intValue() == KeyCodes.KEY_ENTER) {
					searchByText();
				}
			}

		});

		// results list box
		listResults = new ListGrid();
//		listResults.setsetVisibleItemCount(10);
		listResults.setWidth("100%");
		
		VLayout upperRightPanel = new VLayout();
		upperRightPanel.setWidth("100%");
		upperRightPanel.addChild(label);
//		upperRightPanel.addChild(inputBox);
		upperRightPanel.addChild(listResults);
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
					ListGridRecord record = new ListGridRecord();
					record.setSingleCellValue(node.getText());
					listResults.addData(record);
				}
				if (results.length == 0) {
					Window.alert("No matches found!");
				}
			}
		};
		searchService.searchByText(inputBox.getValue().toString(), callback);
	}

}
