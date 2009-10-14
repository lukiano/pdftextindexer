package org.lucho.client;

import com.extjs.gxt.charts.client.model.Text;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
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
	private Button uploadButton;

	public IndexerTreePanel(final SearchRemoteServiceAsync searchService) {
		this.searchService = searchService;
		// main panel
		this.setSize(400, 600);
		treePanelItems();
		
	}

	private void treePanelItems() {
		// file tree
		fileTree = new MyTreePanel<Node>(new TreeStore<Node>());
		fileTree.setDisplayProperty("text");
		Icons icons = GWT.create(Icons.class);
		fileTree.getStyle().setLeafIcon(icons.document());
		fileTree.addListener(Events.DoubleClick, new Listener<TreePanelEvent<Node>>() {

			public void handleEvent(TreePanelEvent<Node> tpe) {
				Node selectedNode = (Node) tpe.getNode().getModel();
				if (selectedNode == null) {
					MessageBox.alert(Constants.TITLE, "No document has been selected", null);
				} else if (!selectedNode.hasChildren()) {
					Window.open(selectedNode.getPath(), "_blank",
					"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
				}
			}
		});
		
		// load tree for first time
		fillModel(fileTree.getStore());
		
		ContentPanel filePanel = new ContentPanel(new FitLayout());
		filePanel.setScrollMode(Scroll.AUTOY);
		filePanel.add(fileTree);
		filePanel.setHeading("Traverse folders and documents");
		filePanel.setWidth("100%");
		formPanel.setHeight(400);

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
				MessageBox.alert(Constants.TITLE, be.getResultHtml(), null);
			}

		});
		
		directoryHidden = new HiddenField<Text>();
		directoryHidden.setName(Constants.HIDDEN_FIELD);
		
		formPanel.setWidth("100%");
		formPanel.setHeight(100);
		
		// Create a FileUpload widget.
		final FileUploadField upload = new FileUploadField();
		upload.setName(Constants.UPLOAD_FIELD);
		upload.setFieldLabel("Document");
		formPanel.add(directoryHidden);
		formPanel.add(upload);
		formPanel.setHeading("Upload new document");
		
		//upload button
		uploadButton = new Button("Upload to chosen folder");
		uploadButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			public void handleEvent(ButtonEvent be) {
				if (upload.getValue() == null || upload.getValue().length() == 0) {
					MessageBox.alert(Constants.TITLE, "Nothing to upload.", null);
					return;
				}
				Node selectedNode =  fileTree.getSelectionModel().getSelectedItem();
				if (selectedNode == null) {
					MessageBox.alert(Constants.TITLE, "No folder selected", null);
					return;
				}
				if (!selectedNode.hasChildren()) {
					MessageBox.alert(Constants.TITLE, "Selected item is not a folder. Please choose a folder to upload the document.", null);
					return;
				}
				directoryHidden.setValue(new Text(selectedNode.getPath()));
				formPanel.submit();
			}
		});
		formPanel.addButton(uploadButton);
		formPanel.setButtonAlign(HorizontalAlignment.CENTER);

		this.add(filePanel);
		this.add(formPanel);
	}

	private void fillModel(final TreeStore<Node> model) {
		
		AsyncCallback<Node> asyncCallback = new AsyncCallback<Node>() {

			public void onSuccess(Node result) {
				model.add(result, true);
				uploadButton.setEnabled(true);
			}

			public void onFailure(Throwable caught) {
				MessageBox box = MessageBox.alert(Constants.TITLE, "Unable to load tree. Failure message is "
						+ caught.getMessage(), null);
				box.setIcon(MessageBox.ERROR);
				box.show();
				uploadButton.setEnabled(true);
			}

		};
		searchService.listFiles(asyncCallback);
	}

}
