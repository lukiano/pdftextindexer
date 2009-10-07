package org.lucho.client;


import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

public class Main implements EntryPoint {

	// services
	private SearchRemoteServiceAsync searchService;
	
	// panels
	private DockPanel  mainPanel;
	private VerticalPanel leftPanel;
	private VerticalPanel rightPanel;
	
	// left panel items
	private Tree fileTree;
	private FormPanel formPanel;
	private Hidden directoryHidden;
	
	// left panel buttons
	private Button downloadFromTreeButton;
	private Button reloadButton;
	private Button uploadButton;
	
	// right panel items
	private TextBox inputBox;
	private ListBox listResults;
	
	// right panel buttons
	private Button cleanButton;
	private Button reindexButton;
	private Button downloadButton;

	public void onModuleLoad() {
		// get service
		searchService = (SearchRemoteServiceAsync) GWT
				.create(SearchRemoteService.class);
		((ServiceDefTarget) searchService).setServiceEntryPoint(GWT
				.getModuleBaseURL()
				+

				"/searchRemoteService");

		this.leftPanel();
		this.rightPanel();
		
		// add nice borders
		DecoratorPanel leftDecoratorPanel = new DecoratorPanel();
		leftDecoratorPanel.setWidget(leftPanel);
		DecoratorPanel rightDecoratorPanel = new DecoratorPanel();
		rightDecoratorPanel.setWidget(rightPanel);

		// main panel
		mainPanel = new DockPanel();
		mainPanel.setTitle("Text Indexer");
		mainPanel.add(leftDecoratorPanel, DockPanel.WEST);
		mainPanel.add(rightDecoratorPanel, DockPanel.EAST);
		mainPanel.setSize("600px", "400px");
		RootPanel.get().add(mainPanel);
	}

	private void leftPanel() {
		Panel scrollPanel = leftPanelItems();
		Panel botonPanel = leftPanelButtons();

		// main panel
		leftPanel = new VerticalPanel();
		leftPanel.setSize("400px", "300px");
		leftPanel.add(scrollPanel);
		leftPanel.add(botonPanel);
		leftPanel.setCellHorizontalAlignment(botonPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		leftPanel.setTitle("Traverse files");

	}

	private Panel leftPanelButtons() {
		// download button
		downloadFromTreeButton = new Button("Download file");
//		downloadFromTreeButton.setWidth("75%");
		downloadFromTreeButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				TreeItem selectedItem = fileTree.getSelectedItem();
				if (selectedItem == null) {
					Window.alert("No file selected");
					return;
				}
				Node selectedNode = (Node) selectedItem.getUserObject();
				if (selectedNode.hasChildren()) {
					Window.alert("Selected item is a folder. Please choose a file to download.");
					return;
				}
				Window.open(selectedNode.getPath(), "_blank",
								"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
			}
		});

		// reload tree button
		reloadButton = new Button("Reload tree");
//		reloadButton.setWidth("75%");
		reloadButton.setEnabled(false);
		reloadButton.addClickHandler(new ClickHandler() {

			public void onClick(ClickEvent event) {
				reloadButton.setEnabled(false);
				uploadButton.setEnabled(false);
				downloadFromTreeButton.setEnabled(false);
				fileTree.clear();
				fileTree.addItem(getModel());
			}
		});
		
		//upload button
		uploadButton = new Button("Upload", new ClickHandler() {
			public void onClick(ClickEvent event) {
				TreeItem selectedItem = fileTree.getSelectedItem();
				if (selectedItem == null) {
					Window.alert("No folder selected");
					return;
				}
				Node selectedNode = (Node) selectedItem.getUserObject();
				if (!selectedNode.hasChildren()) {
					Window.alert("Selected item is not a folder. Please choose a folder to upload file.");
					return;
				}
				directoryHidden.setValue(selectedNode.getPath());
				formPanel.submit();
			}
		});
		
		// button dock panel
		HorizontalPanel botonPanel1 = new HorizontalPanel();
		botonPanel1.setHorizontalAlignment(HasHorizontalAlignment.ALIGN_CENTER);
		botonPanel1.setWidth("100%");
		botonPanel1.add(downloadFromTreeButton);
		botonPanel1.add(uploadButton);
		botonPanel1.add(reloadButton);
		return botonPanel1;
	}

	private Panel leftPanelItems() {
		// file tree
		fileTree = new Tree();
		
		// load tree for first time
		fileTree.addItem(getModel());
		
		// scroll panel wraps tree
		ScrollPanel scrollPanel = new ScrollPanel(fileTree);
		scrollPanel.setWidth("100%");
		scrollPanel.setSize("400px", "200px");
		
		// form panel
		formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL() + "/uploadHandler");
		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		formPanel.setEncoding(FormPanel.ENCODING_MULTIPART);
		formPanel.setMethod(FormPanel.METHOD_POST);
		// Add an event handler to the form.
		formPanel.addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed, this
				// event is
				// fired. Assuming the service returned a response of type
				// text/html,
				// we can get the result text here (see the FormPanel
				// documentation for
				// further explanation).
				Window.alert(event.getResults());
				reloadButton.click();
			}

		});
		
		directoryHidden = new Hidden(Constants.HIDDEN_FIELD);
		
		VerticalPanel insideFormPanel = new VerticalPanel();
		insideFormPanel.setWidth("100%");
		formPanel.setWidget(insideFormPanel);
		formPanel.setWidth("100%");
		
		// Create a FileUpload widget.
		FileUpload upload = new FileUpload();
		upload.setName(Constants.UPLOAD_FIELD);
		upload.setWidth("100%");

		Label label = new Label("Upload new file");
		label.setWidth("100%");
		label.setHorizontalAlignment(Label.ALIGN_LEFT);

		insideFormPanel.add(label);
		insideFormPanel.add(directoryHidden);
		insideFormPanel.add(upload);

		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("100%");
		panel.add(scrollPanel);
		panel.add(formPanel);
		return panel;
	}

	private void rightPanel() {
		// panel description label
		Panel upperRightPanel = rightPanelItems();
		Panel buttonPanel = rightPanelButtons();

		//main panel
		rightPanel = new VerticalPanel();
		rightPanel.add(upperRightPanel);
		rightPanel.add(buttonPanel);
		rightPanel.setSize("400px", "300px");
		rightPanel.setTitle("Search by text");
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

	private TreeItem getModel() {
		final TreeItem root = new TreeItem("/");
		AsyncCallback<Node> asyncCallback = new AsyncCallback<Node>() {

			public void onSuccess(Node result) {
				if (result.hasChildren()) {
					addNode(root, result);
				}
				reloadButton.setEnabled(true);
				uploadButton.setEnabled(true);
				downloadFromTreeButton.setEnabled(true);
			}

			public void onFailure(Throwable caught) {
				Window.alert("Unable to load tree. Failure message is "
						+ caught.getMessage());
				reloadButton.setEnabled(true);
				uploadButton.setEnabled(true);
				downloadFromTreeButton.setEnabled(true);
			}

			private void addNode(TreeItem treeItem, Node node) {
				for (Node childNode : node.getChildren()) {
					TreeItem newItem = treeItem.addItem(childNode.getText());
					newItem.setUserObject(childNode);
					if (childNode.hasChildren() && childNode.getChildren() != null
							&& childNode.getChildren().length > 0) {
						addNode(newItem, childNode);
					}
				}
			}

		};
		searchService.listFiles(asyncCallback);
		return root;
	}

}
