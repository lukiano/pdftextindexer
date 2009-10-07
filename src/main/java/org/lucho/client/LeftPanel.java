package org.lucho.client;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Hidden;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;

public class LeftPanel extends VerticalPanel {
	
	// services
	private SearchRemoteServiceAsync searchService;

	// panel items
	private Tree fileTree;
	private FormPanel formPanel;
	private Hidden directoryHidden;
	
	// panel buttons
	private Button downloadFromTreeButton;
	private Button reloadButton;
	private Button uploadButton;

	public LeftPanel(final SearchRemoteServiceAsync searchService) {
		this.searchService = searchService;
		Panel scrollPanel = leftPanelItems();
		Panel botonPanel = leftPanelButtons();

		// main panel
		this.setSize("400px", "300px");
		this.add(scrollPanel);
		this.add(botonPanel);
		this.setCellHorizontalAlignment(botonPanel,
				HasHorizontalAlignment.ALIGN_CENTER);
		this.setTitle("Traverse files");
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
