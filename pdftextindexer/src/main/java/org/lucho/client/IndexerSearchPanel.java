package org.lucho.client;

import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.VerticalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
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
	private ListView<Text> listResults;

	// panel buttons
	private Button cleanButton;
	private Button reindexButton;

	public IndexerSearchPanel(final SearchRemoteServiceAsync searchService) {
		this.searchService = searchService;
		this.setTableHeight("100%");
		this.setTableWidth("100%");
		this.setSize(400, 600);
		this.add(searchPanelItems());
	}

	private void searchPanelButtons(final ContentPanel contentPanel) {
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
			public void handleEvent(ButtonEvent be) {
				final MessageBox waitBox = MessageBox.wait(Constants.TITLE, "Rebuilding index", "working...");
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					public void onSuccess(Void result) {
						waitBox.close();
						inputBox.enable();
					}

					public void onFailure(Throwable caught) {
						waitBox.close();
						MessageBox.alert(Constants.TITLE, "Couldn't rebuild index!", null);
						inputBox.enable();
					}
				};
				inputBox.disable();
				waitBox.show();
				searchService.reindex(callback);
			}
		});

		contentPanel.addButton(cleanButton);
		contentPanel.addButton(reindexButton);
		contentPanel.setButtonAlign(HorizontalAlignment.CENTER);
	}

	private Component searchPanelItems() {
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
		listResults = new ListView<Text>();
		ListStore<Text> store = new ListStore<Text>();
		listResults.setStore(store);
		listResults.setWidth("100%");
		listResults.setHeight(400);
		listResults.addListener(Events.DoubleClick,
				new Listener<ListViewEvent<Text> >() {
					public void handleEvent(ListViewEvent<Text> lve) {
						Text path = lve.getModel();
						if (path == null) {
							MessageBox.alert(Constants.TITLE, "Please select a document from the results list.", null);
						} else {
							Window.open(path.getText(), "_blank",
											"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
						}
					}
				});

		ContentPanel panel = new ContentPanel();
		panel.setWidth(400-10);
		panel.setHeight("100%");
		panel.setHeading("Search documents");
		panel.add(inputBox);
		panel.add(listResults);
		searchPanelButtons(panel);
		return panel;
	}

	protected final void searchByText() {
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
					MessageBox.info(Constants.TITLE, "No matches found!", null);
				}
			}
		};
		searchService.searchByText(inputBox.getValue().toString(), callback);
	}

}
