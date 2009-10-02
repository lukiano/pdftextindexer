package org.lucho.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormHandler;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormSubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormSubmitEvent;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Main implements EntryPoint {

	public void onModuleLoad() {
		ThemeHelper.getInstance().setThemeName("ascetic");

		DockPanel mainPanel = new DockPanel();
		
		mainPanel.setTitle("Text Indexer");
		mainPanel.add(this.leftPanel(), DockPanel.WEST);
		mainPanel.add(this.rightPanel(), DockPanel.EAST);
		mainPanel.setSize("600px", "400px");
		
		//addUploadForm(window);
		RootPanel.get().add(mainPanel);
	}
	
	private Panel leftPanel() {
	    Tree fileTree = new Tree();
	    fileTree.addItem(getModel());
	    VerticalPanel panel = new VerticalPanel();
	    panel.add(fileTree);
	    panel.setSize("300px", "400px");
	    return panel;
	}

	private Panel searchPanel() {
		Label label = new Label("Text search");
		label.setWidth("100%");
		label.setHorizontalAlignment(Label.ALIGN_CENTER);
		TextBox inputBox = new TextBox();
		inputBox.setText("Type something here...");
		inputBox.setWidth("100%");
		ListBox listResults = new ListBox();
		listResults.setWidth("100%");
		Button botonBuscar = new Button("search");
		botonBuscar.setWidth("75%");
		Button botonLimpiar = new Button("clear results");
		botonLimpiar.setWidth("75%");
	    VerticalPanel panel = new VerticalPanel();
	    panel.add(label);
	    panel.add(inputBox);
	    panel.add(listResults);
	    DockPanel botonPanel = new DockPanel();
	    botonPanel.add(botonBuscar, DockPanel.WEST);
	    botonPanel.add(botonLimpiar, DockPanel.EAST);
	    botonPanel.setCellHorizontalAlignment(botonBuscar, HasHorizontalAlignment.ALIGN_CENTER);
	    botonPanel.setCellHorizontalAlignment(botonLimpiar, HasHorizontalAlignment.ALIGN_CENTER);
	    botonPanel.setWidth("100%");
	    panel.add(botonPanel);
	    panel.setSize("300px", "200px");
	    return panel;
	}

	private Panel rightPanel() {
		VerticalPanel panel = new VerticalPanel();
	    panel.add(this.searchPanel());
	    panel.add(this.uploadPanel());
	    panel.setSize("300px", "400px");
	    return panel;
	}

	private Panel uploadPanel() {
//		Button botonUpload = new Button("upload");
		VerticalPanel panel = new VerticalPanel();
		this.addUploadForm(panel);
		panel.setSize("300px", "200px");
	    return panel;
	}

	private TreeItem getModel() {
		TreeItem root = new TreeItem("root");
		TreeItem fold = new TreeItem("varios");
		root.addItem(new TreeItem("pdf1"));
		root.addItem(new TreeItem("pdf2"));
		fold.addItem(new TreeItem("pdf3"));
		fold.addItem(new TreeItem("pdf4"));
		root.addItem(fold);
		return root;
	}

	private void addUploadForm(Panel outerPanel) {
		final FormPanel form = new FormPanel();
		form.setAction(GWT.getModuleBaseURL() + "/uploadHandler");

		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);

		VerticalPanel panel = new VerticalPanel();
		panel.setWidth("100%");
		form.setWidget(panel);
		// Create a FileUpload widget.
		FileUpload upload = new FileUpload();
		upload.setName("uploadFormElement");
		upload.setWidth("100%");

		panel.add(upload);

		Button submitButton = new Button("Submit", new ClickHandler() {
			
		      public void onClick(ClickEvent event) {
		    	  form.submit();
			      }
			    });


		// Add a 'submit' button.
		panel.add(submitButton);

		// Add an event handler to the form.
		form.addFormHandler(new FormHandler() {

			public void onSubmitComplete(FormSubmitCompleteEvent event) {
				// When the form submission is successfully completed, this
				// event is
				// fired. Assuming the service returned a response of type
				// text/html,
				// we can get the result text here (see the FormPanel
				// documentation for
				// further explanation).
				com.google.gwt.user.client.Window.alert(event.getResults());
			}

			public void onSubmit(FormSubmitEvent event) {
				// TODO Auto-generated method stub

			}
		});

		outerPanel.add(form);
	}

}
