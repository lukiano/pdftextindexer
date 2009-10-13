package org.lucho.client;

import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IndexerSearchPanel extends VerticalPanel {

	// services
	private SearchRemoteServiceAsync searchService;

	// panel items
	private TextField<String> inputBox;
	private ListField<Text> listResults;

	// panel buttons
	private Button cleanButton;
	private Button reindexButton;

	public IndexerSearchPanel(final SearchRemoteServiceAsync searchService) {
		this.searchService = searchService;
		this.setTitle("Search by text");
		// panel description label
		this.setTableHeight("100%");
		this.setTableWidth("100%");
		this.setSize("400px", "600px");
		// main panel
		this.add(rightPanelItems());
		this.add(rightPanelButtons());
	}

	private Component rightPanelButtons() {
		// clear results button
		cleanButton = new Button("Clear results");
		cleanButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			public void handleEvent(ButtonEvent be) {
				listResults.getStore().removeAll();
				inputBox.setValue("");
			}
		});

		// reindex button
		reindexButton = new Button("Rebuild index");
		reindexButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			
			private MessageBox waitBox = MessageBox.wait(Constants.TITLE, "Rebuilding index", "working...");

			public void handleEvent(ButtonEvent be) {
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						waitBox.close();
						inputBox.enable();
					}

					public void onFailure(Throwable caught) {
						waitBox.close();
						MessageBox.alert(Constants.TITLE, "Couldn't rebuild index!", null).show();
						inputBox.enable();
					}
				};
				inputBox.disable();
				waitBox.show();
				searchService.reindex(callback);
			}
		});

		// first button dock panel
		HorizontalPanel buttonPanel = new HorizontalPanel();
		buttonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		buttonPanel.setWidth("100%");
		buttonPanel.setTableWidth("100%");
		buttonPanel.add(cleanButton);
		buttonPanel.add(reindexButton);
		return buttonPanel;
	}

	private Component rightPanelItems() {
		// input search text box
		inputBox = new TextField<String>();
		inputBox.setEmptyText("Type query here...");
		inputBox.setWidth("100%");
		inputBox.setFieldLabel("Text search");
		inputBox.addKeyListener(new KeyListener() {

			public void componentKeyPress(ComponentEvent event) {
				int code = event.getKeyCode();
				if (code == KeyCodes.KEY_ENTER) {
					searchByText();
				}
			}

		});

		// results list box
		listResults = new ListField<Text>();
		ListStore<Text> store = new ListStore<Text>();
		listResults.setStore(store);
		listResults.setWidth("100%");
		listResults.addListener(Events.DoubleClick,
				new Listener<ButtonEvent>() {
					public void handleEvent(ButtonEvent ignored) {
						Text path = listResults.getValue();
						if (path == null) {
							MessageBox.alert(Constants.TITLE, "Please select a document from the results list.", null).show();
						} else {
							Window.open(path.getText(), "_blank",
											"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
						}
					}
				});

		VerticalPanel panel = new VerticalPanel();
		panel.setTableHeight("100%");
		panel.setTableWidth("100%");
		panel.setWidth("100%");
		panel.add(inputBox);
		panel.add(listResults);
		return panel;
	}

	protected void searchByText() {
		final MessageBox waitBox = MessageBox.wait(Constants.TITLE, "Please wait", "searching...");
		AsyncCallback<Node[]> callback = new AsyncCallback<Node[]>() {

			// This method will be called if the service call fails
			public void onFailure(Throwable caught) {
				waitBox.close();
				// Show a message informing the user why the call failed
				MessageBox box = MessageBox.alert(Constants.TITLE, "Unable to perform search. Failure message is "
						+ caught.getMessage(), null);
				box.setIcon(MessageBox.ERROR);
				box.show();
			}

			// This method will be called if the service call succeeds
			public void onSuccess(Node[] results) {
				waitBox.close();
				// Get the service call result and cast it to the
				// desired type and display it
				listResults.getStore().removeAll();
				for (Node node : results) {
					Text data = new Text(node.getText());
					listResults.getStore().add(data);
				}
				if (results.length == 0) {
					MessageBox.info(Constants.TITLE, "No matches found!", null).show();
				}
			}
		};
		searchService.searchByText(inputBox.getValue().toString(), callback);
		waitBox.show();
	}

}
