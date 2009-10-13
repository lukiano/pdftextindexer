package org.lucho.client;

import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IndexerTreePanel extends VerticalPanel {
	
	// services
	private SearchRemoteServiceAsync searchService;

	// panel items
	private TreeLoader<Node> loader;
	private TreePanel<Node> fileTree;
	private FormPanel formPanel;
	private HiddenField<Text> directoryHidden;
	
	// panel buttons
	private Button reloadButton;
	private Button uploadButton;

	public IndexerTreePanel(final SearchRemoteServiceAsync searchService) {
		this.searchService = searchService;
		// main panel
		this.setTableHeight("100%");
		this.setTableWidth("100%");
		this.setSize("400px", "600px");
		this.add(leftPanelItems());
		this.add(leftPanelButtons());
		this.setSize("400px", "600px");
//		this.setCellHorizontalAlignment(botonPanel,
//				HasHorizontalAlignment.ALIGN_CENTER);
		this.setTitle("Traverse files");
	}

	private Component leftPanelButtons() {
		// reload tree button
		reloadButton = new Button("Reload tree");
		reloadButton.setEnabled(false);
		reloadButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {

			public void handleEvent(ButtonEvent be) {
				reloadButton.setEnabled(false);
				uploadButton.setEnabled(false);
				fileTree.getStore().removeAll();
				fillModel(fileTree.getStore());
			}
		});
		
		//upload button
		uploadButton = new Button("Upload");
		
		uploadButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			public void handleEvent(ButtonEvent be) {
				Node selectedNode =  fileTree.getSelectionModel().getSelectedItem();
				if (selectedNode == null) {
					Window.alert("No folder selected");
					return;
				}
				if (!selectedNode.hasChildren()) {
					Window.alert("Selected item is not a folder. Please choose a folder to upload the document.");
					return;
				}
				directoryHidden.setValue(new Text(selectedNode.getPath()));
				formPanel.submit();
			}
		});
		
		// button dock panel
		HorizontalPanel botonPanel = new HorizontalPanel();
		botonPanel.setHorizontalAlign(HorizontalAlignment.CENTER);
		botonPanel.setWidth("100%");
		botonPanel.setTableWidth("100%");
		botonPanel.add(uploadButton);
		botonPanel.add(reloadButton);
		return botonPanel;
	}

	private Component leftPanelItems() {
		// file tree
		
		fileTree = new TreePanel<Node>(new TreeStore<Node>());
		fileTree.setDisplayProperty("text");
		Icons icons = GWT.create(Icons.class);
		fileTree.getStyle().setLeafIcon(icons.document());
		fileTree.addListener(Events.DoubleClick, new Listener<ButtonEvent>() {

			public void handleEvent(ButtonEvent ignored) {
				Node selectedNode =  fileTree.getSelectionModel().getSelectedItem();
				if (selectedNode == null) {
					MessageBox.alert(Constants.TITLE, "No document has been selected", null);
				} else if (selectedNode.hasChildren()) {
					MessageBox.alert(Constants.TITLE, "Selected item is a folder. Please choose a document to download.", null).show();
				} else {
					Window.open(selectedNode.getPath(), "_blank",
					"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
				}
			}
		});
		
		// load tree for first time
		fillModel(fileTree.getStore());
		
		// form panel
		formPanel = new FormPanel();
		formPanel.setAction(GWT.getModuleBaseURL() + "/uploadHandler");
		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		formPanel.setEncoding(Encoding.MULTIPART);
		formPanel.setMethod(Method.POST);
		// Add an event handler to the form.
		formPanel.addListener(Events.Submit, new Listener<FormEvent>() {
			
			public void handleEvent(FormEvent be) {
				// When the form submission is successfully completed, this
				// event is
				// fired. Assuming the service returned a response of type
				// text/html,
				// we can get the result text here (see the FormPanel
				// documentation for
				// further explanation).
				MessageBox.alert(Constants.TITLE, be.getResultHtml(), null).show();
			}

		});
		
		directoryHidden = new HiddenField<Text>();
		directoryHidden.setName(Constants.HIDDEN_FIELD);
		
		formPanel.setWidth("100%");
		
		// Create a FileUpload widget.
		FileUploadField upload = new FileUploadField();
		upload.setAllowBlank(false);
		upload.setName(Constants.UPLOAD_FIELD);
		upload.setWidth("100%");
		upload.setFieldLabel("Upload new document");

		formPanel.add(directoryHidden);
		formPanel.add(upload);

		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("100%");
		panel.setTableHeight("100%");
		panel.setTableWidth("100%");
		panel.add(fileTree);
		panel.setScrollMode(Scroll.AUTOY);
		panel.add(formPanel);
		return panel;
	}

	private void fillModel(final TreeStore<Node> model) {
		
		AsyncCallback<Node> asyncCallback = new AsyncCallback<Node>() {

			public void onSuccess(Node result) {
				model.add(result, true);
				reloadButton.setEnabled(true);
				uploadButton.setEnabled(true);
			}

			public void onFailure(Throwable caught) {
				MessageBox box = MessageBox.alert(Constants.TITLE, "Unable to load tree. Failure message is "
						+ caught.getMessage(), null);
				box.setIcon(MessageBox.ERROR);
				box.show();
				reloadButton.setEnabled(true);
				uploadButton.setEnabled(true);
			}

		};
		searchService.listFiles(asyncCallback);
	}

}
