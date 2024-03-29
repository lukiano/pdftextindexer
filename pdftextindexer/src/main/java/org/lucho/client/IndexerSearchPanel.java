package org.lucho.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.KeyListener;
import com.extjs.gxt.ui.client.event.ListViewEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IndexerSearchPanel extends LayoutContainer {
	
	private final int height;

	// services
	private SearchRemoteServiceAsync searchService;
	
	// panel items
	private TextField<String> inputBox;
	private TextField<String> suggestBox;
	private TextField<String> highlightBox;
	private ListView<Node> listResults;

	// panel buttons
	private Button cleanButton;
	private Button reindexButton;

	public IndexerSearchPanel(final SearchRemoteServiceAsync searchService, final int width, final int height) {
		this.searchService = searchService;
		this.height = height;
		this.setBorders(false);
		this.setLayout(new VBoxLayout());
		this.add(searchPanelItems());
		this.setSize(width, height);
	}

	private void searchPanelButtons(final ContentPanel contentPanel) {
		// clear results button
		cleanButton = new Button("Clear results");
		cleanButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			public void handleEvent(final ButtonEvent be) {
				listResults.getStore().removeAll();
				inputBox.clear();
			}
		});

		// reindex button
		reindexButton = new Button("Rebuild index");
		reindexButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			public void handleEvent(final ButtonEvent be) {
				final MessageBox waitBox = MessageBox.wait(Constants.TITLE, "Rebuilding index", "working...");
				AsyncCallback<Void> callback = new AsyncCallback<Void>() {
					public void onSuccess(final Void result) {
						waitBox.close();
						inputBox.enable();
					}

					public void onFailure(final Throwable caught) {
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
			public void componentKeyUp(final ComponentEvent event) {
				int code = event.getKeyCode();
				if (code == KeyCodes.KEY_ENTER) {
					searchByText();
				} else {
					suggestATerm();
				}
			}
		});
		
		//suggestion box
		suggestBox = new TextField<String>();
		suggestBox.setWidth("100%");
		suggestBox.setReadOnly(true);

		//highlight box
		highlightBox = new TextField<String>();
		highlightBox.setWidth("100%");
		highlightBox.setReadOnly(true);

		// results list box
		listResults = new ListView<Node>();
		ListStore<Node> store = new ListStore<Node>();
		listResults.setStore(store);
		listResults.setDisplayProperty("text");
		listResults.setWidth("100%");
		listResults.setHeight("100%");
		listResults.addListener(Events.Select,
				new Listener<ListViewEvent<Node> >() {
					public void handleEvent(final ListViewEvent<Node> lve) {
						Node selectedNode = lve.getModel();
						if (selectedNode == null) {
							MessageBox.alert(Constants.TITLE, "Please select a document from the results list.", null);
						} else {
							highlight(selectedNode);
						}
					}
				});
		listResults.addListener(Events.DoubleClick,
				new Listener<ListViewEvent<Node> >() {
					public void handleEvent(final ListViewEvent<Node> lve) {
						Node selectedNode = lve.getModel();
						if (selectedNode == null) {
							MessageBox.alert(Constants.TITLE, "Please select a document from the results list.", null);
						} else {
							Window.open(selectedNode.getPath(), "_blank",
							"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
						}
					}
				});

		ContentPanel panel = new ContentPanel();
		panel.setHeading("Search documents");
		panel.add(inputBox);
		panel.add(suggestBox);	
		panel.add(listResults);
		panel.add(highlightBox);
		panel.setWidth("100%");
		panel.setHeight(height - 120);
		searchPanelButtons(panel);
		return panel;
	}
	
	protected final void highlightAndDownload(final Node node) {
		MessageBox.confirm("Download?", "highlighted", new Listener<MessageBoxEvent>() {

			public void handleEvent(final MessageBoxEvent be) {
				if (Dialog.YES.equals(be.getValue())) {
					Window.open(node.getPath(), "_blank",
					"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
				}
			}
		});
	}

	protected final void searchByText() {
		final MessageBox waitBox = MessageBox.wait(Constants.TITLE, "Please wait", "searching...");
		AsyncCallback<Node[]> callback = new AsyncCallback<Node[]>() {

			// This method will be called if the service call fails
			public void onFailure(final Throwable caught) {
				waitBox.close();
				// Show a message informing the user why the call failed
				MessageBox box = MessageBox.alert(Constants.TITLE, "Unable to perform search.", null);
				box.setIcon(MessageBox.ERROR);
				box.show();
			}

			// This method will be called if the service call succeeds
			public void onSuccess(final Node[] results) {
				waitBox.close();
				// Get the service call result and cast it to the
				// desired type and display it
				ListStore<Node> store = listResults.getStore(); 
				store.removeAll();
				for (Node node : results) {
					store.add(node);
				}
				if (results.length == 0) {
					MessageBox.info(Constants.TITLE, "No matches found!", null);
				}
				store.commitChanges();
			}
		};
		String query = inputBox.getValue();
		
		searchService.searchByText(query, callback);
	}
	
	private void suggestATerm() {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			
			public void onSuccess(final String suggestion) {
				suggestBox.setValue("Suggestion: " + suggestion);
			}
			
			public void onFailure(final Throwable ignored) {
			}
		};
		String query = inputBox.getValue();
		if (query.length() > 3) {
			searchService.suggest(query, callback);
		} else {
			suggestBox.clear();
		}
	}

	private void highlight(final Node node) {
		AsyncCallback<String> callback = new AsyncCallback<String>() {
			
			public void onSuccess(final String highlight) {
				highlightBox.setValue(highlight);
			}
			
			public void onFailure(final Throwable ignored) {
			}
		};
		String query = inputBox.getValue();
		searchService.highlight(node, query, callback);
	}

}
