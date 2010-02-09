package org.lucho.client;

import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.data.TreeLoader;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.Store;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;
import com.extjs.gxt.ui.client.widget.form.StoreFilterField;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.VBoxLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class IndexerTreePanel extends LayoutContainer {

	private final int width;
	private final int height;

	// services
	private SearchRemoteServiceAsync searchService;

	// panel items
	private TreePanel<Node> fileTree;
	private FormPanel formPanel;
	private HiddenField<String> directoryHidden;
	private Button uploadButton;

	public IndexerTreePanel(final SearchRemoteServiceAsync searchService,
			final int width, final int height) {
		this.searchService = searchService;
		this.setBorders(false);
		this.setLayout(new VBoxLayout());
		// this.setTableHeight("100%");
		// this.setTableWidth("100%");
		this.width = width;
		this.height = height;
		treePanelItems();
		this.setSize(width, height);
	}

	private void treePanelItems() {
		// data proxy
		RpcProxy<List<Node>> proxy = new RpcProxy<List<Node>>() {
			@Override
			protected void load(final Object node,
					final AsyncCallback<List<Node>> callback) {
				searchService.listChildren((Node) node, callback);
			}
		};
		// file tree
		TreeLoader<Node> loader = new BaseTreeLoader<Node>(proxy) {
			@Override
			public boolean hasChildren(final Node parent) {
				return parent.hasChildren();
			}
		};

		TreeStore<Node> store = new TreeStore<Node>(loader);
		fileTree = new MyTreePanel<Node>(store);
		fileTree.setDisplayProperty("text");
		fileTree.setWidth("100%");
		Icons icons = GWT.create(Icons.class);
		fileTree.getStyle().setLeafIcon(icons.document());
		fileTree.addListener(Events.DoubleClick,
				new Listener<TreePanelEvent<Node>>() {

					public void handleEvent(final TreePanelEvent<Node> tpe) {
						Node selectedNode = (Node) (tpe.getNode()==null?null:tpe.getNode().getModel());
						if (selectedNode == null) {
							MessageBox.alert(Constants.TITLE,
									"No document has been selected", null);
						} else if (!selectedNode.hasChildren()) {
							Window
									.open(selectedNode.getPath(), "_blank",
											"menubar=no,location=yes,resizable=no,scrollbars=no,status=no");
						}
					}
				});

		StoreFilterField<Node> nodeFilter = new StoreFilterField<Node>() {

			@Override
			protected boolean doSelect(final Store<Node> store, final Node parent,
					final Node record, final String property, final String filter) {
				// only match leaf nodes
				if (record.hasChildren()) {
					return false;
				}
				String name = record.getText();
				name = name.toLowerCase();
				if (name.startsWith(filter.toLowerCase())) {
					return true;
				}
				return false;
			}

		};
		nodeFilter.setWidth("100%");
		nodeFilter.bind(store);

		ContentPanel filePanel = new ContentPanel(new FitLayout());
		filePanel.setScrollMode(Scroll.AUTOY);
		filePanel.add(fileTree);
		filePanel.setHeading("Traverse folders and documents");
		filePanel.setWidth("100%");
		filePanel.setHeight(height - 200);

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
				fileTree.getStore().removeAll();
				fillRootNodes(fileTree.getStore());
				MessageBox.alert(Constants.TITLE, be.getResultHtml(), null);
			}
		});

		directoryHidden = new HiddenField<String>();
		directoryHidden.setName(Constants.HIDDEN_FIELD);

		formPanel.setWidth("100%");
		// formPanel.setHeight("25%");

		// Create a FileUpload widget.
		final FileUploadField upload = new FileUploadField();
		upload.setName(Constants.UPLOAD_FIELD);
		upload.setFieldLabel("Document");
		formPanel.add(directoryHidden);
		formPanel.add(upload);
		formPanel.setHeading("Upload new document");

		// upload button
		uploadButton = new Button("Upload to chosen folder");
		uploadButton.addListener(Events.OnClick, new Listener<ButtonEvent>() {
			public void handleEvent(ButtonEvent be) {
				if (upload.getValue() == null
						|| upload.getValue().length() == 0) {
					MessageBox.alert(Constants.TITLE,
							"There is nothing to upload.", null);
					return;
				}
				Node selectedNode = fileTree.getSelectionModel()
						.getSelectedItem();
				if (selectedNode == null) {
					MessageBox.alert(Constants.TITLE,
							"No folder has been selected", null);
					return;
				}
				if (selectedNode.isLeaf()) {
					MessageBox
							.alert(
									Constants.TITLE,
									"The selected item is not a folder. Please choose a folder to upload the document.",
									null);
					return;
				}
				directoryHidden.setValue(selectedNode.getPath());
				formPanel.submit();
			}
		});
		formPanel.addButton(uploadButton);
		formPanel.setButtonAlign(HorizontalAlignment.CENTER);

		this.add(nodeFilter);
		this.add(filePanel);
		this.add(formPanel);

		// load tree for first time
		fillRootNodes(fileTree.getStore());
	}

	private void fillRootNodes(final TreeStore<Node> model) {
		AsyncCallback<List<Node>> asyncCallback = new AsyncCallback<List<Node>>() {
			public void onSuccess(final List<Node> result) {
				model.add(result, false);
				uploadButton.setEnabled(true);
			}

			public void onFailure(Throwable caught) {
				MessageBox box = MessageBox.alert(Constants.TITLE,
						"Unable to load tree. (" + caught.getMessage() + ")",
						null);
				box.setIcon(MessageBox.ERROR);
				box.show();
				uploadButton.setEnabled(true);
			}
		};
		searchService.listRootNodes(asyncCallback);
	}

//	private void fillChidrenNodes(final TreeStore<Node> model, final Node parent) {
//		AsyncCallback<List<Node>> asyncCallback = new AsyncCallback<List<Node>>() {
//			public void onSuccess(final List<Node> result) {
//				parent.removeAll();
//				model.add(parent, result, true);
//				for (Node resultNode : result) {
//					parent.add(resultNode);
//				}
//			}
//
//			public void onFailure(Throwable caught) {
//				MessageBox box = MessageBox.alert(Constants.TITLE,
//						"Unable to load tree. (" + caught.getMessage() + ")",
//						null);
//				box.setIcon(MessageBox.ERROR);
//				box.show();
//			}
//		};
//		searchService.listChildren(parent, asyncCallback);
//	}

}
